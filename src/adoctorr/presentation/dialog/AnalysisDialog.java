package adoctorr.presentation.dialog;

import adoctorr.application.analysis.Analyzer;
import adoctorr.application.bean.SmellMethodBean;
import beans.PackageBean;
import com.intellij.ide.SaveAndSyncHandlerImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AnalysisDialog extends JDialog {
    private JPanel contentPane;

    private AnalysisThread analysisThread;

    private Project project;
    private ArrayList<SmellMethodBean> smellMethodList;
    private ArrayList<PackageBean> projectPackageList;
    private HashMap<String, File> sourceFileMap;

    /**
     * Default constructor and initializator of the dialog
     *
     * @param project
     */
    private AnalysisDialog(Project project) {
        // Leave them as they are
        setContentPane(contentPane);
        setModal(true);
        setTitle("aDoctor-R - Analysis");

        this.project = project;
        projectPackageList = null;
        sourceFileMap = null;
        smellMethodList = null;

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onQuit();
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

        // Save all files in the current project before starting the analysis
        FileDocumentManager.getInstance().saveAllDocuments();
        project.save();

        // Thread that manage the real analysis
        analysisDialog.analysisThread = new AnalysisThread(project, analysisDialog);
        analysisDialog.analysisThread.start();

        // setVisibile(true) is blocking, that's why we use a Thread to start the real analysis
        analysisDialog.pack();
        analysisDialog.setVisible(true);

        // Invoked at the end of the analysis thread
        analysisDialog.checkResults();
    }

    private void onQuit() {
        dispose();
    }

    private void checkResults() {
        dispose();
        if (smellMethodList != null) {
            // If there is at least one smell, show the SmellDialog
            SmellDialog.show(project, smellMethodList);
        } else {
            // There are no smell
            NoSmellDialog.show(project);
        }
    }

    private static class AnalysisThread extends Thread {
        private Project project;
        private AnalysisDialog analysisDialog;

        AnalysisThread(Project project, AnalysisDialog analysisDialog) {
            this.project = project;
            this.analysisDialog = analysisDialog;
        }

        public void run() {
            startAnalysis();
        }

        void startAnalysis() {
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
                                    // Postocondition check
                                    if (smellMethodList != null && smellMethodList.size() > 0) {
                                        System.out.println("smellMethodList costruita");
                                    } else {
                                        smellMethodList = null;
                                        //TODO Error handle 4: smellMethodList ritornata vuota
                                    }

                                    // Set all the results to the analysisDialog in a callback-like style
                                    analysisDialog.projectPackageList = projectPackageList;
                                    analysisDialog.sourceFileMap = sourceFileMap;
                                    analysisDialog.smellMethodList = smellMethodList;   // The most important

                                    // Hides the analysis window, unlocking the thread blocked at the preceding setVisible(true)
                                    analysisDialog.setVisible(false);
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
