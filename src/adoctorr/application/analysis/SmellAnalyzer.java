package adoctorr.application.analysis;

import beans.MethodBean;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import parser.MethodVisitor;

import java.util.ArrayList;

abstract class SmellAnalyzer {

    /**
     * Fetch the related MethodDeclarationNode from the CompilationUnit given the MethodBean
     *
     * @param methodBean
     * @param compilationUnit
     * @return
     */
    MethodDeclaration getNodeFromBean(MethodBean methodBean, CompilationUnit compilationUnit) {
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
}
