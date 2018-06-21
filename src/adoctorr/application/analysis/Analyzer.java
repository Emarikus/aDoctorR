package adoctorr.application.analysis;

import adoctorr.application.smell.SmellMethodBean;
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
    public ArrayList<PackageBean> buildPackageList(Project project) {
        ArrayList<PackageBean> packageList = null;
        // Precondition check
        if (project != null) {
            String projectBasePath = project.getBasePath();
            if (projectBasePath != null) {
                // PackageList to be filled
                File projectDirectory = new File(project.getBasePath());
                try {
                    // Calling the aDoctor legacy method to build this list
                    packageList = FolderToJavaProjectConverter.convert(projectDirectory.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return packageList;
    }

    /**
     * Obtains all project Java files
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
     * @param javaFilesList
     * @return
     */
    public HashMap<String, File> buildSourceFileMap(ArrayList<File> javaFilesList) {
        HashMap<String, File> sourceFileMap = null;
        // Precondition check
        if (javaFilesList != null && javaFilesList.size() > 0) {
            sourceFileMap = new HashMap<>();
            try {
                for (File javaFile : javaFilesList) {
                    // Creates the CompilationUnit of every Java file in order to get its FQN easily. This is done through CodeParser of aDoctor
                    CodeParser codeParser = new CodeParser();
                    CompilationUnit compilationUnit = codeParser.createParser(FileUtilities.readFile(javaFile.getAbsolutePath()));

                    // Builds the FQN String
                    String packageName = compilationUnit.getPackage().getName().getFullyQualifiedName();
                    TypeDeclaration typeDeclaration = (TypeDeclaration) compilationUnit.types().get(0);
                    String className = typeDeclaration.getName().toString();
                    String classFullName = packageName + "." + className;

                    sourceFileMap.put(classFullName, javaFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
                sourceFileMap = null;
            }
        }
        return sourceFileMap;
    }

    /**
     * Builds an ArrayList with all code smell found in the whole project
     * @param packageList
     * @param sourceFileMap
     * @return
     */
    public ArrayList<SmellMethodBean> analyze(ArrayList<PackageBean> packageList, HashMap<String, File> sourceFileMap) {
        ArrayList<SmellMethodBean>  smellMethodList = null;

        //TODO 0: Chiama tutti i vari analyzer per cercare gli smell e popolare la lista

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
    /*

                            //4) Analisi Bean alla ricerca degli Smell
                            System.out.println("Ricerca dei Code Smell...");

                            MemberIgnoringMethodResolver mimResolver = new MemberIgnoringMethodResolver();
                            DataTransmissionWithoutCompressionResolver dataResolver = new DataTransmissionWithoutCompressionResolver();
                            DurableWakeLockResolver wakeLockResolver = new DurableWakeLockResolver();
                            //TODO: Istanziare gli altri Resolver

                            ArrayList<MethodBean> mimList = mimResolver.analyze(packageList, sourceFileMap);
                            ArrayList<MethodBean> bulkList = new ArrayList<>(); //TODO: DEBUG
                            ArrayList<MethodBean> dataList = dataResolver.analyze(packageList, sourceFileMap);
                            ArrayList<MethodBean> wakeLockList = wakeLockResolver.analyze(packageList, sourceFileMap);
                            ArrayList<MethodBean> earlyList = new ArrayList<>(); //TODO: DEBUG
                            ArrayList<MethodBean> prohibitedList = new ArrayList<>(); //TODO: DEBUG
                            ArrayList<MethodBean> rigidList = new ArrayList<>(); //TODO: DEBUG
                            smellMatrix.add(mimList);
                            smellMatrix.add(bulkList);
                            smellMatrix.add(dataList);
                            smellMatrix.add(wakeLockList);
                            smellMatrix.add(earlyList);
                            smellMatrix.add(prohibitedList);
                            smellMatrix.add(rigidList);

                            System.out.println("Ricerca dei Code Smell terminata con successo");
                    */