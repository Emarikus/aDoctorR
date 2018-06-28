package adoctorr.application.ast;

import beans.MethodBean;
import org.eclipse.jdt.core.dom.*;
import parser.CodeParser;
import parser.MethodInvocationsVisitor;
import parser.MethodVisitor;
import process.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ASTUtilities {

    public static CompilationUnit getCompilationUnit(File sourceFile) throws IOException {
        CodeParser codeParser = new CodeParser();
        String javaFileContent = FileUtilities.readFile(sourceFile.getAbsolutePath());
        return codeParser.createParser(javaFileContent);
    }

    public static MethodDeclaration getMethodDeclarationFromBean(MethodBean methodBean, CompilationUnit compilationUnit) {
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

    public static ArrayList<Block> getBlocksInMethod(MethodDeclaration methodDeclaration) {
        if (methodDeclaration == null) {
            return null;
        } else {
            ArrayList<Block> blockList = new ArrayList<>();
            methodDeclaration.accept(new BlockVisitor(blockList));
            return blockList;
        }
    }

    public static ArrayList<MethodInvocation> getMethodInvocationsInMethod(MethodDeclaration methodDeclaration) {
        if (methodDeclaration == null) {
            return null;
        } else {
            MethodInvocationsVisitor methodInvocationsVisitor = new MethodInvocationsVisitor();
            methodDeclaration.accept(methodInvocationsVisitor);
            return (ArrayList<MethodInvocation>) methodInvocationsVisitor.getMethods();
        }
    }

    public static Block getBlockInMethod(String blockContent, MethodDeclaration methodDeclaration) {
        // Fetch all Blocks of the method an AST visitor of aDoctor;
        ArrayList<Block> blockList = new ArrayList<>();
        methodDeclaration.accept(new BlockVisitor(blockList));
        int i = 0;
        int blockListSize = blockList.size();
        while (!blockList.get(i).toString().equals(blockContent) && i < blockListSize) {
            i++;
        }
        if (i < blockListSize) {
            return blockList.get(i);
        } else {
            return null;
        }
    }

    public static MethodInvocation getMethodInvocationInMethod(String methodInvocationName, MethodDeclaration methodDeclaration) {
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

    public static String getCallerName(Statement statement, String methodName) {
        if (statement == null || methodName == null) {
            return null;
        } else {
            if (statement instanceof ExpressionStatement) {
                ExpressionStatement expressionStatement = (ExpressionStatement) statement;
                Expression expression = expressionStatement.getExpression();
                if (expression instanceof MethodInvocation) {
                    MethodInvocation methodInvocation = (MethodInvocation) expression;
                    // If there is an explicit caller
                    if (methodInvocation.getExpression() != null) {
                        if (methodInvocation.getName().toString().equals(methodName)) {
                            return methodInvocation.getExpression().toString();
                        }
                    }
                }
            }
            return null;
        }
    }
}

