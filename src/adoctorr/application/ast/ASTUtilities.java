package adoctorr.application.ast;

import beans.MethodBean;
import org.eclipse.jdt.core.dom.*;
import parser.CodeParser;
import parser.InstanceVariableVisitor;
import parser.MethodInvocationsVisitor;
import parser.MethodVisitor;
import process.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        while (!found && i < methodDeclarationList.size()) {
            if (methodDeclarationList.get(i).toString().equals(methodBeanContent)) {
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

    public static MethodDeclaration getMethodDeclarationFromName(String methodName, CompilationUnit compilationUnit) {
        TypeDeclaration typeDeclaration = (TypeDeclaration) compilationUnit.types().get(0);
        ArrayList<MethodDeclaration> methodDeclarationList = new ArrayList<>();
        // Fetch all MethodDeclarations of the class with an AST visitor of aDoctor
        typeDeclaration.accept(new MethodVisitor(methodDeclarationList));

        // Fetch the correct MethodDeclaration through a comparison with the content of the MethodBean parameter
        int i = 0;
        boolean found = false;
        while (!found && i < methodDeclarationList.size()) {
            if (methodDeclarationList.get(i).getName().toString().equals(methodName)) {
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
        // Fetch all Blocks
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

    public static FieldDeclaration getFieldDeclarationInClass(String variableName, CompilationUnit compilationUnit) {
        TypeDeclaration typeDeclaration = (TypeDeclaration) compilationUnit.types().get(0);
        // Fetch all FieldDeclaration of the method an AST visitor of aDoctor;
        ArrayList<FieldDeclaration> instanceVariableList = new ArrayList<>();
        typeDeclaration.accept(new FieldDeclarationVisitor(instanceVariableList));
        int i = 0;
        int instanceVariableListSize = instanceVariableList.size();
        boolean found = false;
        while (!found && i < instanceVariableListSize) {
            List fragments = instanceVariableList.get(i).fragments();
            if (fragments != null && fragments.size() > 0) {
                VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
                if (fragment.getName().toString().equals(variableName)) {
                    found = true;
                } else {
                    i++;
                }
            } else {
                i++;
            }
        }
        if (found) {
            return instanceVariableList.get(i);
        } else {
            return null;
        }
    }

    public static ExpressionStatement getExpressionStatementInMethod(String statementContent, MethodDeclaration methodDeclaration) {
        // Fetch all ExpressionStatments
        ArrayList<ExpressionStatement> statementList = new ArrayList<>();
        methodDeclaration.accept(new ExpressionStatementVisitor(statementList));
        int i = 0;
        int statementListSize = statementList.size();
        while (!statementList.get(i).toString().equals(statementContent) && i < statementListSize) {
            i++;
        }
        if (i < statementListSize) {
            return statementList.get(i);
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

