package adoctorr.application.refactoring;

import adoctorr.application.ASTUtilities;
import adoctorr.application.bean.ProposalMethodBean;
import adoctorr.application.bean.SmellMethodBean;
import beans.MethodBean;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import process.FileUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class Refactorer {

    public Refactorer() {

    }

    //Applica il refactor e sovrascrive il file
    public boolean applyRefactoring(ProposalMethodBean proposalMethodBean) throws BadLocationException, IOException {
        if (proposalMethodBean == null) {
            System.out.println("Errore precondizione");
            return false;
        } else {
            MethodDeclaration proposedMethodDeclaration = proposalMethodBean.getProposedMethodDeclaration();
            SmellMethodBean smellMethodBean = proposalMethodBean.getSmellMethodBean();
            File sourceFile = smellMethodBean.getSourceFile();
            MethodBean methodBean = smellMethodBean.getMethodBean();

            // Builds the Document object
            String targetSource = FileUtilities.readFile(sourceFile.getAbsolutePath());
            Document document = new Document(targetSource);
            //TODO: Debug
            System.out.println("---Documento Attuale---");
            System.out.println(document.get());

            CompilationUnit compilationUnit = ASTUtilities.getCompilationUnit(sourceFile);

            // MethodDeclaration to be replaced
            MethodDeclaration targetMethodDeclaration = ASTUtilities.getNodeFromBean(methodBean, compilationUnit);
            if(targetMethodDeclaration == null){
                return false;
            } else {
                // Refactoring phase
                AST targetAST = compilationUnit.getAST();
                ASTRewrite rewriter = ASTRewrite.create(targetAST);
                rewriter.replace(targetMethodDeclaration, proposedMethodDeclaration, null);
                TextEdit edits = rewriter.rewriteAST(document, null);
                edits.apply(document, TextEdit.UPDATE_REGIONS);
                String documentContent = document.get();
                //TODO: Debug
                System.out.println("---Documento Rifattorizzato---");
                System.out.println(documentContent);
                //TODO: Le edits non presentano gli \n. Come si risolve?

                // Overwrite directly the file
                PrintWriter pw = new PrintWriter(new FileOutputStream(sourceFile, false));
                pw.println(documentContent);
                pw.close();

                return true;
            }
        }
    }
}
