package adoctorr.application;

import beans.MethodBean;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import parser.CodeParser;
import parser.MethodInvocationsVisitor;
import parser.MethodVisitor;
import process.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ASTUtilities {

    public static MethodDeclaration getNodeFromBean(MethodBean methodBean, CompilationUnit compilationUnit) {
        TypeDeclaration typeDeclaration = (TypeDeclaration) compilationUnit.types().get(0);
        ArrayList<MethodDeclaration> methodDeclarationList = new ArrayList<>();
        // Fetch all MethodDeclarations of the class with an AST visitor of aDoctor
        typeDeclaration.accept(new MethodVisitor(methodDeclarationList));

        // Fetch the correct MethodDeclaration through a comparison with the content of the MethodBean parameter
        String methodBeanContent = methodBean.getTextContent();
        int i = 0;
        boolean found = false;
        while (i < methodDeclarationList.size() && !found) {
            if (methodBeanContent.equals(methodDeclarationList.get(i).toString())) {
                found = true;
            } else {
                i++;
            }
        }
        if (found) {
            return methodDeclarationList.get(i);
        } else {
            return null;
        }
    }

    public static MethodInvocation getNodeFromInvocationName(MethodDeclaration methodDeclaration, String methodInvocationName) {
        // Fetch all MethodInvocation of the method an AST visitor of aDoctor;
        MethodInvocationsVisitor methodInvocationsVisitor = new MethodInvocationsVisitor();
        methodDeclaration.accept(methodInvocationsVisitor);
        ArrayList<MethodInvocation> methodInvocationList = (ArrayList<MethodInvocation>) methodInvocationsVisitor.getMethods();
        int i = 0;
        int methodInvocationListSize = methodInvocationList.size();
        while (!methodInvocationList.get(i).toString().equals(methodInvocationName) && i < methodInvocationListSize) {
            i++;
        }
        if (i < methodInvocationListSize) {
            return methodInvocationList.get(i);
        } else {
            return null;
        }
    }

    public static CompilationUnit getCompilationUnit(File sourceFile) throws IOException {
        CodeParser codeParser = new CodeParser();
        String javaFileContent = FileUtilities.readFile(sourceFile.getAbsolutePath());
        return codeParser.createParser(javaFileContent);
    }
}
