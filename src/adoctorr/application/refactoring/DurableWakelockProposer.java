package adoctorr.application.refactoring;

import adoctorr.application.ASTUtilities;
import adoctorr.application.bean.DurableWakelockSmellMethodBean;
import adoctorr.application.bean.ProposalMethodBean;
import beans.MethodBean;
import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DurableWakelockProposer {

    public DurableWakelockProposer() {

    }

    public ProposalMethodBean computeProposal(DurableWakelockSmellMethodBean smellMethodBean) throws IOException {
        if (smellMethodBean == null) {
            System.out.println("Errore precondizione");
            return null;
        } else {
            File sourceFile = smellMethodBean.getSourceFile();
            MethodBean methodBean = smellMethodBean.getMethodBean();

            CompilationUnit compilationUnit = ASTUtilities.getCompilationUnit(sourceFile);
            MethodDeclaration methodDeclaration = ASTUtilities.getNodeFromBean(methodBean, compilationUnit);
            if (methodDeclaration == null) {
                return null;
            } else {
                MethodInvocation acquireMethodInvocation = smellMethodBean.getAcquireMethodInvocation();
                if (acquireMethodInvocation == null) {
                    return null;
                } else {
                    AST targetAST = compilationUnit.getAST();

                    Expression acquireExpression = acquireMethodInvocation.getExpression();
                    SimpleName releaseSimpleName = targetAST.newSimpleName("release");

                    MethodInvocation releaseMethodInvocation = targetAST.newMethodInvocation();
                    releaseMethodInvocation.setExpression((Expression) ASTNode.copySubtree(targetAST, acquireExpression));
                    releaseMethodInvocation.setName(releaseSimpleName);
                    // Wrap the MethodInvocation in an ExpressionStatement
                    ExpressionStatement releaseExpressionStatement = targetAST.newExpressionStatement(releaseMethodInvocation);

                    //TODO 3: Migliorare il fatto del punto di inserimento a seconda dello scope del wakelock
                    List<Statement> statementList = methodDeclaration.getBody().statements();
                    statementList.add(releaseExpressionStatement);

                    ArrayList<String> proposedCodeToHighlightList = new ArrayList<>();
                    proposedCodeToHighlightList.add(releaseMethodInvocation.toString() + ";");

                    ProposalMethodBean proposalMethodBean = new ProposalMethodBean();
                    proposalMethodBean.setSmellMethodBean(smellMethodBean);
                    proposalMethodBean.setProposedMethodDeclaration(methodDeclaration);
                    proposalMethodBean.setProposedCodeToHighlightList(proposedCodeToHighlightList);
                    return proposalMethodBean;
                }
            }

        }
    }
}
