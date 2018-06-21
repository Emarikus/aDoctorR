package adoctorr.presentation.dialog;

import adoctorr.application.analysis.Analyzer;
import adoctorr.application.smell.SmellMethodBean;
import beans.PackageBean;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.io.File;
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

        //TODO 1: Call Analyzer methods sending it the project object. Takes the various results and then it calls the SmellDialog
        Analyzer analyzer = new Analyzer();
        ArrayList<PackageBean> projectPackageList = analyzer.buildPackageList(project);
        // Postcondition check
        if (projectPackageList == null) {
            //TODO 2: Handle error
        } else {
            ArrayList<File> javaFilesList = analyzer.getAllJavaFiles(project);
            // Postcondition check
            if (javaFilesList == null) {
                //TODO 3: Handle error
            } else {
                HashMap<String, File> sourceFileMap = analyzer.buildSourceFileMap(javaFilesList);
                // Postcondition check
                if (sourceFileMap == null) {
                    //TODO 4: Handle error
                } else {
                    ArrayList<SmellMethodBean> smellMethodList = analyzer.analyze(projectPackageList, sourceFileMap);
                    if (sourceFileMap == null) {
                        //TODO 5: Handle error
                    } else {
                        //TODO 0: Show next dialog sending it the smellMethodList
                    }
                }
            }
        }
    }
}
