package adoctorr.application.refactoring;

import adoctorr.application.ast.ASTUtilities;
import adoctorr.application.bean.DurableWakelockSmellMethodBean;
import adoctorr.application.bean.ProposalMethodBean;
import beans.MethodBean;
import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DurableWakelockProposer {

    DurableWakelockProposer() {

    }

    public ProposalMethodBean computeProposal(DurableWakelockSmellMethodBean smellMethodBean) throws IOException {
        if (smellMethodBean == null) {
            System.out.println("Errore precondizione");
            return null;
        } else {
            File sourceFile = smellMethodBean.getSourceFile();
            MethodBean methodBean = smellMethodBean.getMethodBean();

            CompilationUnit compilationUnit = ASTUtilities.getCompilationUnit(sourceFile);
            MethodDeclaration methodDeclaration = ASTUtilities.getMethodDeclarationFromBean(methodBean, compilationUnit);
            if (methodDeclaration == null) {
                return null;
            } else {
                Statement acquireStatement = smellMethodBean.getAcquireStatement();
                if (acquireStatement == null) {
                    return null;
                } else {
                    AST targetAST = compilationUnit.getAST();
                    MethodInvocation releaseMethodInvocation = targetAST.newMethodInvocation();

                    // This is done in order to het the wakelock identifier
                    ExpressionStatement acquireExpressionStatement = (ExpressionStatement) acquireStatement;
                    Expression acquireExpression = acquireExpressionStatement.getExpression();
                    MethodInvocation acquireMethodInvocation = (MethodInvocation) acquireExpression;
                    releaseMethodInvocation.setExpression((Expression) ASTNode.copySubtree(targetAST, acquireMethodInvocation.getExpression()));

                    SimpleName releaseSimpleName = targetAST.newSimpleName("release");
                    releaseMethodInvocation.setName(releaseSimpleName);

                    // Wrap the MethodInvocation in an ExpressionStatement
                    ExpressionStatement releaseExpressionStatement = targetAST.newExpressionStatement(releaseMethodInvocation);

                    // If the scope is the method, then add it to the end of the method
                    Block acquireBlock = ASTUtilities.getBlockInMethod(smellMethodBean.getAcquireBlock().toString(), methodDeclaration);
                    List<Statement> statementList = acquireBlock.statements();
                    statementList.add(releaseExpressionStatement);

                    ArrayList<String> proposedCodeToHighlightList = new ArrayList<>();
                    proposedCodeToHighlightList.add(releaseExpressionStatement.toString());

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
