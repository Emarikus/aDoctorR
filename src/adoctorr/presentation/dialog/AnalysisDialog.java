package adoctorr.presentation.dialog;

import adoctorr.application.analysis.Analyzer;
import adoctorr.application.bean.SmellMethodBean;
import beans.PackageBean;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });
    }

    /**
     * First and only method from the outside to be called in order to show this dialog
     *
     * @param project
     */
    public static void show(Project project) {
        AnalysisDialog analysisDialog = new AnalysisDialog(project);

        // Thread that manage the real analysis
        AnalysisThread thread = new AnalysisThread(project, analysisDialog);
        thread.start();

        // Leave them as they are
        analysisDialog.pack();
        // setVisibile(true) is blocking, that's why we use a Thread to start the real analysis
        analysisDialog.setVisible(true);
    }

    private void onExit() {
        // TODO: Mettere un are you sure to abort? Se si, stoppare tutta l'esecuzione
        dispose();
    }

    private static class AnalysisThread extends Thread {
        private Project project;
        private AnalysisDialog analysisDialog;

        AnalysisThread(Project project, AnalysisDialog analysisDialog) {
            this.project = project;
            this.analysisDialog = analysisDialog;
        }

        public void run() {
            System.out.println("Sono nel Thread e inizio le mie cose. Ci vorrà un po'...");
            Analyzer analyzer = new Analyzer();
            ArrayList<PackageBean> projectPackageList;
            try {
                // Very very slow!
                projectPackageList = analyzer.buildPackageList(project);
                // Post condition check
                if (projectPackageList != null) {
                    System.out.println("projectPackageList costruita");
                    ArrayList<File> javaFilesList = analyzer.getAllJavaFiles(project);
                    // Postcondition check
                    if (javaFilesList != null) {
                        System.out.println("javaFilesList costruita");
                        HashMap<String, File> sourceFileMap;
                        try {
                            sourceFileMap = analyzer.buildSourceFileMap(javaFilesList);
                            // Postcondition check
                            if (sourceFileMap != null) {
                                System.out.println("sourceFileMap costruita");
                                ArrayList<SmellMethodBean> smellMethodList = null;
                                try {
                                    smellMethodList = analyzer.analyze(projectPackageList, sourceFileMap);
                                    // Hides the analysis window, unlocking the thread blocked at the preceding setVisible(true)
                                    analysisDialog.setVisible(false);
                                    // Postocondition check
                                    if (smellMethodList != null && smellMethodList.size()>0) {
                                        System.out.println("smellMethodList costruita");
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
}
