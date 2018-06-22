package adoctorr.application.analysis;

import adoctorr.application.smell.SmellMethodBean;
import beans.ClassBean;
import beans.MethodBean;
import beans.PackageBean;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import parser.CodeParser;
import process.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RigidAlarmManagerAnalyzer extends SmellAnalyzer {
    /**
     * @param packageList
     * @param sourceFileMap
     * @return
     * @throws IOException
     */
    public ArrayList<SmellMethodBean> analyze(ArrayList<PackageBean> packageList, HashMap<String, File> sourceFileMap) throws IOException {
        ArrayList<SmellMethodBean> smellList = new ArrayList<>();

        for (PackageBean packageBean : packageList) {
            for (ClassBean classBean : packageBean.getClasses()) {
                String className = classBean.getName();
                String packageName = packageBean.getName();
                String classFullName = packageName + "." + className;
                File sourceFile = sourceFileMap.get(classFullName);

                CodeParser codeParser = new CodeParser();
                String javaFileContent = FileUtilities.readFile(sourceFile.getAbsolutePath());
                CompilationUnit compilationUnit = codeParser.createParser(javaFileContent);
                for (MethodBean methodBean : classBean.getMethods()) {
                    MethodDeclaration methodDeclaration = getNodeFromBean(methodBean, compilationUnit);

                    // Warning: Source code with accents might give problems in the methodDeclaration fetch
                    if (methodDeclaration != null) {
                        // TODO: Analizza il methodDeclaration e il methodBean per cercare lo smell, quando ne trova uno
                        //TODO: costruisce lo SmellMethodBean e lo aggiunge alla smellList
                    }
                }
            }
        }
        return smellList;
    }
}
