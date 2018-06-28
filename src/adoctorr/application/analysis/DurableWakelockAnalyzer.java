package adoctorr.application.analysis;

import adoctorr.application.ast.ASTUtilities;
import adoctorr.application.bean.DurableWakelockSmellMethodBean;
import adoctorr.application.bean.SmellMethodBean;
import beans.MethodBean;
import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DurableWakelockAnalyzer {

    // Warning: Source code with method-level compile error and accents might give problems in the methodDeclaration fetch
    public DurableWakelockSmellMethodBean analyzeMethod(MethodBean methodBean, MethodDeclaration methodDeclaration, File sourceFile) {
        if (methodBean == null) {
            return null;
        } else if (methodDeclaration == null) {
            return null;
        } else if (sourceFile == null) {
            return null;
        } else {
            Block acquireBlock = null;
            Statement acquireStatement = null;

            ArrayList<Block> methodBlockList = ASTUtilities.getBlocksInMethod(methodDeclaration);
            int k = 0;
            boolean smellFound = false;
            while (!smellFound && k < methodBlockList.size()) {
                Block block = methodBlockList.get(k);
                List<Statement> statementList = block.statements();

                int i = 0;
                while (i < statementList.size()) {
                    Statement statement = statementList.get(i);
                    String callerName = ASTUtilities.getCallerName(statement, "acquire");
                    if (callerName != null) {

                        boolean releaseFound = false;
                        int j = i + 1;
                        while (!releaseFound && j < statementList.size()) {
                            Statement statement2 = statementList.get(j);
                            String callerName2 = ASTUtilities.getCallerName(statement2, "release");
                            if (callerName2 != null && callerName.equals(callerName2)) {
                                releaseFound = true;
                            }
                            j++;
                        }
                        if (!releaseFound) {
                            smellFound = true;
                            acquireBlock = block;
                            acquireStatement = statement;
                        }
                    }
                    i++;
                }
                k++;
            }
            if (smellFound) {
                DurableWakelockSmellMethodBean smellMethodBean = new DurableWakelockSmellMethodBean();
                smellMethodBean.setMethodBean(methodBean);
                smellMethodBean.setResolved(false);
                smellMethodBean.setSourceFile(sourceFile);
                smellMethodBean.setSmellType(SmellMethodBean.DURABLE_WAKELOCK);
                smellMethodBean.setAcquireBlock(acquireBlock);
                smellMethodBean.setAcquireStatement(acquireStatement);
                return smellMethodBean;
            } else {
                return null;
            }
        }
    }

    /*
    public DurableWakelockSmellMethodBean analyzeMethod(MethodBean methodBean, MethodDeclaration methodDeclaration, File sourceFile) {
        if (methodBean == null) {
            return null;
        } else if (methodDeclaration == null) {
            return null;
        } else if (sourceFile == null) {
            return null;
        } else {
            String wakelockName = "";
            String methodContent = methodDeclaration.toString();
            // Regex to get all the acquire()
            Pattern acquireRegex = Pattern.compile("(.*)acquire(\\s*)\\(\\)", Pattern.MULTILINE);
            Matcher acquireMatcher = acquireRegex.matcher(methodContent);

            boolean smellFound = false;
            while (!smellFound && acquireMatcher.find()) {
                String matchingString = acquireMatcher.group();
                wakelockName = matchingString.substring(0, matchingString.indexOf(".")).replaceAll("\\s+", "");

                // Look for the release of the same wakelock
                Pattern releaseRegex = Pattern.compile(wakelockName + "\\.release(\\s*)\\(\\)", Pattern.MULTILINE);
                Matcher releaseMatcher = releaseRegex.matcher(methodContent);
                // If the corresponding release is not found
                if (!releaseMatcher.find()) {
                    smellFound = true;
                } else {
                    // there are some corresponding release in the method
                    int acquireStart = acquireMatcher.start();
                    // repeat for every corresponding release: checking if there is no one located after the acquire
                    boolean foundAfter = false;
                    do {
                        int releaseStart = releaseMatcher.start();
                        if (releaseStart > acquireStart) {
                            foundAfter = true;
                        }
                    }
                    while (!foundAfter && releaseMatcher.find());
                    smellFound = !foundAfter;
                }
            }
            if (smellFound) {
                String acquireString = wakelockName + ".acquire()";
                MethodInvocation acquireMethodInvocation = ASTUtilities.getMethodInvocationInMethod(methodDeclaration, acquireString);

                DurableWakelockSmellMethodBean smellMethodBean = new DurableWakelockSmellMethodBean();
                smellMethodBean.setMethodBean(methodBean);
                smellMethodBean.setResolved(false);
                smellMethodBean.setSourceFile(sourceFile);
                smellMethodBean.setSmellType(SmellMethodBean.DURABLE_WAKELOCK);
                smellMethodBean.setAcquireMethodInvocation(acquireMethodInvocation);
                return smellMethodBean;
            } else {
                return null;
            }
        }
    }
    */
}
