package adoctorr.application.analysis;

import adoctorr.application.ASTUtilities;
import adoctorr.application.bean.SmellMethodBean;
import beans.ClassBean;
import beans.MethodBean;
import beans.PackageBean;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurableWakelockAnalyzer {

    /**
     * @param packageList
     * @param sourceFileMap
     * @return
     * @throws IOException
     */
    // Warning: Source code with method-level compile error and accents might give problems in the methodDeclaration fetch
    public ArrayList<SmellMethodBean> analyze(ArrayList<PackageBean> packageList, HashMap<String, File> sourceFileMap) throws IOException {
        ArrayList<SmellMethodBean> smellList = new ArrayList<>();
        for (PackageBean packageBean : packageList) {
            for (ClassBean classBean : packageBean.getClasses()) {
                String className = classBean.getName();
                String packageName = packageBean.getName();
                String classFullName = packageName + "." + className;
                File sourceFile = sourceFileMap.get(classFullName);

                CompilationUnit compilationUnit = ASTUtilities.getCompilationUnit(sourceFile);
                for (MethodBean methodBean : classBean.getMethods()) {
                    MethodDeclaration methodDeclaration = ASTUtilities.getNodeFromBean(methodBean, compilationUnit);
                    if (analyzeMethod(methodDeclaration)) {
                        //TODO: Costruire un DurableWakelockSmellMethodBean: da costruire.
                        SmellMethodBean smellMethodBean = new SmellMethodBean();
                        smellMethodBean.setMethodBean(methodBean);
                        smellMethodBean.setResolved(false);
                        smellMethodBean.setSourceFile(sourceFile);
                        smellMethodBean.setSmellType(SmellMethodBean.DURABLE_WAKELOCK);
                        smellList.add(smellMethodBean);
                    }
                }
            }
        }
        return smellList;
    }

    private boolean analyzeMethod(MethodDeclaration methodDeclaration) {
        if (methodDeclaration == null) {
            return false;
        } else {
            String wakelockName = "";
            // Works, but it could be improved through MethodInvocation instead of String regex
            String methodContent = methodDeclaration.toString();
            // Regex to get all the wl.acquire()
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
            return smellFound;
        }
    }
}
