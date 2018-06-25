package adoctorr.application.refactoring;

import adoctorr.application.ASTUtilities;
import adoctorr.application.bean.ProposalMethodBean;
import adoctorr.application.bean.SmellMethodBean;
import beans.MethodBean;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.io.File;
import java.io.IOException;

public class BulkDataTransferOnSlowNetworkProposer {

    public BulkDataTransferOnSlowNetworkProposer() {

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

            //L'aggiunta non tiene conto degli errori di compilazione. E' tutto a carico del pogrammatore!!
            //IMPORTANTE: Dichiarazioni del metodo non valide comportano uno skip nel recupero dei MethodDeclaration, infatti
            //quel metodo viene considerato inseistente.
            // E' lecito avere errori NEL corpo, ma non nella dichiarazione!!!!!!

            ProposalMethodBean proposalMethodBean = new ProposalMethodBean();
            proposalMethodBean.setSmellMethodBean(smellMethodBean);
            proposalMethodBean.setProposedMethodDeclaration(proposedMethodDeclaration);

            return proposalMethodBean;
        }
    }
}
