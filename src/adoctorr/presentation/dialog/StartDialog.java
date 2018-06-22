package adoctorr.presentation.dialog;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.*;

public class StartDialog extends JDialog {
    private JPanel contentPane;
    private JPanel descriptionPane;
    private JPanel analysisPane;
    private JPanel toolPane;

    private JButton buttonStart;
    private JButton buttonExit;
    private JButton buttonAbout;

    private JLabel labelWelcome;
    private JLabel labelDescription;
    private JLabel labelSave;

    private Project project;

    /**
     * Default constructor and initializator of the dialog
     * @param project
     */
    private StartDialog(Project project) {
        // Leave them as they are
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonStart);    //Pressing Enter means clicking buttonStart

        this.project = project;

        // Assign all various listeners
        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onStartAnalysis();
            }
        });

        buttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        });

        // call onExit() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        /*
        // call onExit() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        */
    }

    /**
     * First and only method from the outside to be called in order to show this dialog
     * @param project
     */
    public static void show(Project project) {
        StartDialog startDialog = new StartDialog(project);

        // Leave them as they are
        startDialog.pack();
        startDialog.setVisible(true);
    }

    /**
     * Start the analysis, after saving all project files
     * Called when Start Analysis button is clicked
     */
    private void onStartAnalysis() {
        dispose();

        // Save all files in the current project
        FileDocumentManager.getInstance().saveAllDocuments();

        // Starts the analysis by showing the AnalysisDialog
        AnalysisDialog.show(project);
    }

    /**
     * Exit from the plugin
     * Called when Exit button is clicked
     */
    private void onExit() {
        dispose();
    }
}