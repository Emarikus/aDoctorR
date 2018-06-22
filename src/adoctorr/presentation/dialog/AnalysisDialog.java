package adoctorr.presentation.dialog;

import adoctorr.application.analysis.Analyzer;
import adoctorr.application.smell.SmellMethodBean;
import beans.PackageBean;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AnalysisDialog extends JDialog {
    private JPanel contentPane;
    private JPanel infoPane;

    private Project project;

    /**
     * Default constructor and initializator of the dialog
     *
     * @param project
     */
    private AnalysisDialog(Project project) {
        // Leave them as they are
        setContentPane(contentPane);
        setModal(true);

        this.project = project;
    }

    /**
     * First and only method from the outside to be called in order to show this dialog
     *
     * @param project
     */
    public static void show(Project project) {
        AnalysisDialog analysisDialog = new AnalysisDialog(project);

        // Leave them as they are
        analysisDialog.pack();
        analysisDialog.setVisible(true);

        Analyzer analyzer = new Analyzer();
        ArrayList<PackageBean> projectPackageList;
        try {
            projectPackageList = analyzer.buildPackageList(project);
            // Post condition check
            if (projectPackageList != null) {
                ArrayList<File> javaFilesList = analyzer.getAllJavaFiles(project);
                // Postcondition check
                if (javaFilesList != null) {
                    HashMap<String, File> sourceFileMap;
                    try {
                        sourceFileMap = analyzer.buildSourceFileMap(javaFilesList);
                        // Postcondition check
                        if (sourceFileMap != null) {
                            ArrayList<SmellMethodBean> smellMethodList = null;
                            try {
                                smellMethodList = analyzer.analyze(projectPackageList, sourceFileMap);
                                // Postocondition check
                                if (smellMethodList != null) {
                                    SmellDialog.show(project, smellMethodList, projectPackageList, sourceFileMap);
                                } else {
                                    //TODO Error handle 4: smellMethodList ritornata vuota
                                }
                            } catch (IOException e3) {
                                //TODO Error handle 4: errore nella costruzione della smellMethodList. E' comunque vuota
                                e3.printStackTrace();
                            }
                        } else {
                            //TODO Error handle 3: sourceFileMap ritornata vuota
                        }
                    } catch (IOException e2) {
                        //TODO Error handle 3: errore nella costruzione della sourceFileMap. E' comunque vuota
                        e2.printStackTrace();
                    }
                } else {
                    //TODO Error handle 2: javaFileList ritornata vuota
                }
            } else {
                //TODO Error handle 1: packageList ritornata vuota
            }
        } catch (IOException e1) {
            //TODO Error handle 1: errore nella costruzione della packageList. E' comunque vuota
            e1.printStackTrace();
        }
    }
}
