package adoctorr.application.analysis;

import adoctorr.application.ast.ASTUtilities;
import adoctorr.application.bean.ProhibitedDataTransferSmellMethodBean;
import adoctorr.application.bean.SmellMethodBean;
import beans.MethodBean;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProhibitedDataTransferAnalyzer {

    //TODO: fare bene
    // Warning: Source code with method-level compile error and accents might give problems in the methodDeclaration fetch
    public ProhibitedDataTransferSmellMethodBean analyzeMethod(MethodBean methodBean, MethodDeclaration methodDeclaration, File sourceFile) {
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
                MethodInvocation acquireMethodInvocation = ASTUtilities.getMethodInvocationInMethod(acquireString, methodDeclaration);

                ProhibitedDataTransferSmellMethodBean smellMethodBean = new ProhibitedDataTransferSmellMethodBean();
                smellMethodBean.setMethodBean(methodBean);
                smellMethodBean.setResolved(false);
                smellMethodBean.setSourceFile(sourceFile);
                smellMethodBean.setSmellType(SmellMethodBean.PROHIBITED_DATA_TRANSFER);
                return smellMethodBean;
            } else {
                return null;
            }
        }
    }
}
