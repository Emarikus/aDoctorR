package adoctorr.application.refactoring;

import adoctorr.application.ASTUtilities;
import adoctorr.application.bean.ProposalMethodBean;
import adoctorr.application.bean.SmellMethodBean;
import beans.MethodBean;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import parser.CodeParser;
import process.FileUtilities;

import java.io.File;
import java.io.IOException;

public class DurableWakelockProposer {

    public DurableWakelockProposer() {

    }

    public ProposalMethodBean computeProposal(SmellMethodBean smellMethodBean) throws IOException {
        if (smellMethodBean == null) {
            System.out.println("Errore precondizione");
            return null;
        } else {
            File sourceFile = smellMethodBean.getSourceFile();
            MethodBean methodBean = smellMethodBean.getMethodBean();

            CompilationUnit compilationUnit = ASTUtilities.getCompilationUnit(sourceFile);
            MethodDeclaration proposedMethodDeclaration = ASTUtilities.getNodeFromBean(methodBean, compilationUnit);

            //TODO (super altissima): Computa proposta lavorando con il targetAST, lo smellMethodBean e la proposedMethodDeclaration
            AST targetAST = compilationUnit.getAST();
            //TODO: Get the first wakelock and create a release() MethodInvocation for what wakelock and add it at the end of the method

            ProposalMethodBean proposalMethodBean = new ProposalMethodBean();
            proposalMethodBean.setSmellMethodBean(smellMethodBean);
            proposalMethodBean.setProposedMethodDeclaration(proposedMethodDeclaration);

            return proposalMethodBean;
        }
    }
}
