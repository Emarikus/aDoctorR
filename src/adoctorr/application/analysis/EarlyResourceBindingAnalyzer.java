package adoctorr.application.analysis;

import adoctorr.application.ASTUtilities;
import adoctorr.application.bean.DataTransmissionWithoutCompressionSmellMethodBean;
import adoctorr.application.bean.EarlyResourceBindingSmellMethodBean;
import adoctorr.application.bean.SmellMethodBean;
import beans.ClassBean;
import beans.MethodBean;
import beans.PackageBean;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import parser.CodeParser;
import process.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EarlyResourceBindingAnalyzer {

    //TODO: fare bene
    // Warning: Source code with method-level compile error and accents might give problems in the methodDeclaration fetch
    public EarlyResourceBindingSmellMethodBean analyzeMethod(MethodBean methodBean, MethodDeclaration methodDeclaration, File sourceFile) {
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
                MethodInvocation acquireMethodInvocation = ASTUtilities.getNodeFromInvocationName(methodDeclaration, acquireString);

                EarlyResourceBindingSmellMethodBean smellMethodBean = new EarlyResourceBindingSmellMethodBean();
                smellMethodBean.setMethodBean(methodBean);
                smellMethodBean.setResolved(false);
                smellMethodBean.setSourceFile(sourceFile);
                smellMethodBean.setSmellType(SmellMethodBean.EARLY_RESOURCE_BINDING);
                return smellMethodBean;
            } else {
                return null;
            }
        }
    }
}
