package adoctorr.presentation.dialog;

import adoctorr.application.analysis.Analyzer;
import adoctorr.application.bean.smell.SmellMethodBean;
import beans.PackageBean;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AnalysisDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonAbort;

    private AnalysisThread analysisThread;

    private Project project;
    private ArrayList<SmellMethodBean> smellMethodList;

    private volatile boolean runThread;
    private volatile boolean analysisStarted;
    private volatile boolean analysisTerminated;

    /**
     * Default constructor and initializator of the dialog
     *
     * @param project
     */
    private AnalysisDialog(Project project) {
        // Leave them as they are
        setContentPane(contentPane);
        setModal(true);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - getWidth()) / 3;
        int y = (screenSize.height - getHeight()) / 5;
        setLocation(x, y);
        setTitle("aDoctor-R - Analysis");

        this.project = project;
        smellMethodList = null;
        analysisStarted = false;
        analysisTerminated = false;

        buttonAbort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAbort();
            }
        });

        // call onAbort() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onAbort();
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
        analysisDialog.runThread = true;
        analysisDialog.analysisThread.start();

        // setVisibile(true) is blocking, that's why we use a Thread to start the real analysis
        analysisDialog.pack();
        analysisDialog.setVisible(true);

        // Invoked at the end of the analysis thread or when the dialog is closed because a dispose() is executed
        analysisDialog.checkResults();
    }

    private void onAbort() {
        // sets this flag to false, in order to stop the analysis thread, as soon as it can
        runThread = false;

        System.out.println("Thread abortito");

        // Disposing the analysis window unlocks UI thread blocked at the preceding setVisible(true)
        dispose();
    }

    private void checkResults() {
        // Disposing the analysis window unlocks UI thread blocked at the preceding setVisible(true)
        dispose();

        if (analysisStarted && analysisTerminated) {
            if (smellMethodList != null) {
                // If there is at least one smell, show the SmellDialog
                SmellDialog.show(project, smellMethodList);
            } else {
                // There are no smell
                NoSmellDialog.show(project);
            }
        } else {
            // Aborted
            AbortDialog.show(project);
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
            System.out.println("Thread avviato");

            analysisDialog.analysisStarted = true;
            startAnalysis();
            analysisDialog.analysisTerminated = true;

            // Disposing the analysis window unlocks UI thread blocked at the preceding setVisible(true)
            analysisDialog.dispose();
        }

        void startAnalysis() {
            Analyzer analyzer = new Analyzer();
            ArrayList<PackageBean> projectPackageList;
            try {
                // runThread flag is periodically checked to see if the analysis can go on
                if (analysisDialog.runThread) {
                    // Very very slow!
                    projectPackageList = analyzer.buildPackageList(project);
                    // Post condition check
                    if (projectPackageList != null && analysisDialog.runThread) {
                        System.out.println("projectPackageList costruita");
                        ArrayList<File> javaFilesList = analyzer.getAllJavaFiles(project);
                        // Postcondition check
                        if (javaFilesList != null && analysisDialog.runThread) {
                            System.out.println("javaFilesList costruita");
                            HashMap<String, File> sourceFileMap;
                            try {
                                sourceFileMap = analyzer.buildSourceFileMap(javaFilesList);
                                // Postcondition check
                                if (sourceFileMap != null && analysisDialog.runThread) {
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
                                        analysisDialog.smellMethodList = smellMethodList;
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
                }
            } catch (IOException e1) {
                //TODO Error handle 1: errore nella costruzione della packageList. E' comunque vuota
                e1.printStackTrace();
            }
        }
    }
}
