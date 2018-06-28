package adoctorr.application.proposal;

import adoctorr.application.ast.ASTUtilities;
import adoctorr.application.bean.proposal.EarlyResourceBindingProposalMethodBean;
import adoctorr.application.bean.smell.EarlyResourceBindingSmellMethodBean;
import beans.MethodBean;
import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EarlyResourceBindingProposer {

    EarlyResourceBindingProposer() {

    }

    public EarlyResourceBindingProposalMethodBean computeProposal(EarlyResourceBindingSmellMethodBean smellMethodBean) throws IOException {
        if (smellMethodBean == null) {
            System.out.println("Errore precondizione");
            return null;
        } else {
            File sourceFile = smellMethodBean.getSourceFile();
            MethodBean methodBean = smellMethodBean.getMethodBean();

            CompilationUnit compilationUnit = ASTUtilities.getCompilationUnit(sourceFile);
            MethodDeclaration onCreateMethodDeclaration = ASTUtilities.getMethodDeclarationFromBean(methodBean, compilationUnit);
            if (onCreateMethodDeclaration == null) {
                return null;
            } else {
                //TODO: Implementare
                ///////////////////////

                Statement requestStatement = smellMethodBean.getRequestStatement();
                // Precondition check
                if (requestStatement == null) {
                    return null;
                } else {
                    // All preconditions passed
                    AST targetAST = compilationUnit.getAST();

                    ArrayList<String> actualCodeToHighlightList = new ArrayList<>();
                    actualCodeToHighlightList.add(requestStatement.toString());

                    ArrayList<String> proposedCodeToHighlightList = new ArrayList<>();

                    // Only for public|protected void onResume()
                    boolean foundOnResume = false;
                    MethodDeclaration onResumeMethodDeclaration = ASTUtilities.getMethodDeclarationFromName("onResume", compilationUnit);
                    if (onResumeMethodDeclaration != null) {
                        Type returnType = onResumeMethodDeclaration.getReturnType2();
                        if (returnType != null && returnType.toString().equals("void")) {
                            boolean found = false;
                            int i = 0;
                            List modifierList = onResumeMethodDeclaration.modifiers();
                            int n = modifierList.size();
                            while (!found && i < n) {
                                IExtendedModifier modifier = (IExtendedModifier) modifierList.get(i);
                                if (modifier.toString().equals("public") || modifier.toString().equals("protected")) {
                                    List parameters = onResumeMethodDeclaration.parameters();
                                    if (parameters == null || parameters.size() == 0) {
                                        found = true;
                                    }
                                }
                                i++;
                            }
                            foundOnResume = found;
                        }
                    }

                    // Create the new statement for onResume
                    ExpressionStatement requestExpressionStatementTEMP = (ExpressionStatement) requestStatement;
                    ExpressionStatement requestExpressionStatement = ASTUtilities.getExpressionStatementInMethod(requestExpressionStatementTEMP.toString(), onCreateMethodDeclaration);
                    Expression requestExpression = requestExpressionStatement.getExpression();

                    // Remove from onCreate
                    //TODO: Perchè non rimuove?
                    Block requestBlock = ASTUtilities.getBlockInMethod(smellMethodBean.getRequestBlock().toString(), onCreateMethodDeclaration);
                    List<Statement> statementList = requestBlock.statements();
                    statementList.remove(requestStatement);

                    if (!foundOnResume) {
                        SimpleName onResumeIdentifier = targetAST.newSimpleName("onResume");

                        Modifier onResumePublicModifier = targetAST.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);

                        Block onResumeBody = targetAST.newBlock();

                        onResumeMethodDeclaration = targetAST.newMethodDeclaration();
                        onResumeMethodDeclaration.setName(onResumeIdentifier);
                        onResumeMethodDeclaration.modifiers().add(onResumePublicModifier);
                        onResumeMethodDeclaration.setBody(onResumeBody);
                    }

                    // Add at the bottom of the onResume
                    Statement newRequestStatement = targetAST.newExpressionStatement((Expression) ASTNode.copySubtree(targetAST, requestExpression));
                    List<Statement> onResumeStatementList = onResumeMethodDeclaration.getBody().statements();
                    onResumeStatementList.add(newRequestStatement);

                    if (!foundOnResume) {
                        String onResumeMethodDeclarationString = onResumeMethodDeclaration.toString();
                        proposedCodeToHighlightList.add(onResumeMethodDeclarationString);
                    } else {
                        proposedCodeToHighlightList.add(newRequestStatement.toString());
                    }

                    EarlyResourceBindingProposalMethodBean proposalMethodBean = new EarlyResourceBindingProposalMethodBean();
                    proposalMethodBean.setSmellMethodBean(smellMethodBean);
                    proposalMethodBean.setProposedOnCreate(onCreateMethodDeclaration);
                    proposalMethodBean.setProposedOnResume(onResumeMethodDeclaration);
                    proposalMethodBean.setActualCodeToHighlightList(actualCodeToHighlightList);
                    proposalMethodBean.setProposedCodeToHighlightList(proposedCodeToHighlightList);
                    return proposalMethodBean;
                }
            }
        }
    }
}
