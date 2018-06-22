package adoctorr.application.analysis;

import adoctorr.application.smell.SmellMethodBean;
import beans.ClassBean;
import beans.MethodBean;
import beans.PackageBean;
import com.intellij.openapi.project.Project;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import parser.CodeParser;
import process.FileUtilities;
import process.FolderToJavaProjectConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Analyzer {

    public Analyzer() {

    }

    /**
     * Builds an ArrayList of PackageBean, a legacy structure from aDoctor.
     * This will be used during the building of the list of code smells.
     *
     * @param project
     * @return
     */
    public ArrayList<PackageBean> buildPackageList(Project project) throws IOException {
        ArrayList<PackageBean> packageList = null;
        // Precondition check
        if (project != null) {
            String projectBasePath = project.getBasePath();
            if (projectBasePath != null) {
                // PackageList to be filled
                File projectDirectory = new File(project.getBasePath());
                // Calling the aDoctor legacy method to build this list
                packageList = FolderToJavaProjectConverter.convert(projectDirectory.getAbsolutePath());
                // belongingClass was not set in aDoctor API: this is just a fix
                for (PackageBean packageBean : packageList) {
                    for (ClassBean classBean : packageBean.getClasses()) {
                        for (MethodBean methodBean : classBean.getMethods()) {
                            methodBean.setBelongingClass(classBean);
                        }
                    }
                }
            }
        }
        return packageList;
    }

    /**
     * Obtains all project Java files
     *
     * @param project
     * @return
     */
    public ArrayList<File> getAllJavaFiles(Project project) {
        ArrayList<File> javaFilesList = null;
        // Precondition check
        if (project != null) {
            String projectBasePath = project.getBasePath();
            if (projectBasePath != null) {
                File projectDirectory = new File(project.getBasePath());
                // Invokes the recursive function to get all .java files
                javaFilesList = getJavaFilesInDirectory(projectDirectory);
            }
        }
        return javaFilesList;
    }

    /**
     * Builds a HashMap that given a class FQN it is possible to get the related java File.
     *
     * @param javaFilesList
     * @return
     */
    public HashMap<String, File> buildSourceFileMap(ArrayList<File> javaFilesList) throws IOException {
        HashMap<String, File> sourceFileMap = null;
        // Precondition check
        if (javaFilesList != null && javaFilesList.size() > 0) {
            sourceFileMap = new HashMap<>();
            for (File javaFile : javaFilesList) {
                // Creates the CompilationUnit of every Java file in order to get its FQN easily. This is done through CodeParser of aDoctor
                CodeParser codeParser = new CodeParser();
                String javaFileContent = FileUtilities.readFile(javaFile.getAbsolutePath());
                CompilationUnit compilationUnit = codeParser.createParser(javaFileContent);

                // Builds the FQN String
                String packageName = compilationUnit.getPackage().getName().getFullyQualifiedName();
                TypeDeclaration typeDeclaration = (TypeDeclaration) compilationUnit.types().get(0);
                String className = typeDeclaration.getName().toString();
                String classFullName = packageName + "." + className;

                sourceFileMap.put(classFullName, javaFile);
            }
        }
        return sourceFileMap;
    }

    /**
     * Builds an ArrayList with all code smell found in the whole project
     *
     * @param packageList
     * @param sourceFileMap
     * @return
     */
    public ArrayList<SmellMethodBean> analyze(ArrayList<PackageBean> packageList, HashMap<String, File> sourceFileMap) throws IOException {
        ArrayList<SmellMethodBean> smellMethodList = new ArrayList<>();

        ArrayList<SmellMethodBean> durableWakelockList = null;
        ArrayList<SmellMethodBean> dataTransmissionWithoutComrpessionList = null;
        ArrayList<SmellMethodBean> prohibitedDataTransferList = null;
        ArrayList<SmellMethodBean> bulkDataTransferOnSlowNetworkList = null;
        ArrayList<SmellMethodBean> earlyResourceBindingList = null;
        ArrayList<SmellMethodBean> rigidAlarmManagerList = null;

        DurableWakelockAnalyzer durableWakelockAnalyzer = new DurableWakelockAnalyzer();
        DataTransmissionWithoutCompressionAnalyzer dataTransmissionWithoutCompressionAnalyzer = new DataTransmissionWithoutCompressionAnalyzer();
        ProhibitedDataTransferAnalyzer prohibitedDataTransferAnalyzer = new ProhibitedDataTransferAnalyzer();
        BulkDataTransferOnSlowNetworkAnalyzer bulkDataTransferOnSlowNetworkAnalyzer = new BulkDataTransferOnSlowNetworkAnalyzer();
        EarlyResourceBindingAnalyzer earlyResourceBindingAnalyzer = new EarlyResourceBindingAnalyzer();
        RigidAlarmManagerAnalyzer rigidAlarmManagerAnalyzer = new RigidAlarmManagerAnalyzer();

        durableWakelockList = durableWakelockAnalyzer.analyze(packageList, sourceFileMap);
        dataTransmissionWithoutComrpessionList = dataTransmissionWithoutCompressionAnalyzer.analyze(packageList, sourceFileMap);
        prohibitedDataTransferList = prohibitedDataTransferAnalyzer.analyze(packageList, sourceFileMap);
        bulkDataTransferOnSlowNetworkList = bulkDataTransferOnSlowNetworkAnalyzer.analyze(packageList, sourceFileMap);
        earlyResourceBindingList = earlyResourceBindingAnalyzer.analyze(packageList, sourceFileMap);
        rigidAlarmManagerList = rigidAlarmManagerAnalyzer.analyze(packageList, sourceFileMap);

        if (durableWakelockList != null) {
            smellMethodList.addAll(durableWakelockList);
        }
        if (dataTransmissionWithoutComrpessionList != null) {
            smellMethodList.addAll(dataTransmissionWithoutComrpessionList);
        }
        if (prohibitedDataTransferList != null) {
            smellMethodList.addAll(prohibitedDataTransferList);
        }
        if (bulkDataTransferOnSlowNetworkList != null) {
            smellMethodList.addAll(bulkDataTransferOnSlowNetworkList);
        }
        if (earlyResourceBindingList != null) {
            smellMethodList.addAll(earlyResourceBindingList);
        }
        if (rigidAlarmManagerList != null) {
            smellMethodList.addAll(rigidAlarmManagerList);
        }
        return smellMethodList;
    }

    /**
     * Get all Java files in the directory and then recusively does the same in all subdirectories
     *
     * @param directory
     * @return
     */
    private ArrayList<File> getJavaFilesInDirectory(File directory) {
        ArrayList<File> javaFilesList = new ArrayList<>();
        // File list of the current directory
        File[] fList = directory.listFiles();

        // If the directory is non empty
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    if (file.getName().contains(".java")) {
                        javaFilesList.add(file);
                    }
                } else if (file.isDirectory()) {
                    File subDirectory = new File(file.getAbsolutePath());
                    javaFilesList.addAll(getJavaFilesInDirectory(subDirectory));
                }
            }
        }
        return javaFilesList;
    }
}