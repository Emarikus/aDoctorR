package adoctorr.presentation.dialog;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.*;

public class SuccessDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonAnalyze;
    private JButton buttonQuit;

    private Project project;

    private SuccessDialog(Project project) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonAnalyze);
        setTitle("aDoctor-R - Success");

        this.project = project;

        buttonAnalyze.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onStartAnalysis();
            }
        });

        buttonQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onQuit();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onQuit();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onQuit();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void show(Project project) {
        SuccessDialog successDialog = new SuccessDialog(project);

        successDialog.pack();
        successDialog.setVisible(true);
    }

    private void onStartAnalysis() {
        dispose();
        AnalysisDialog.show(project);
    }

    private void onQuit() {
        dispose();
    }
}
