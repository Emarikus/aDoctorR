package adoctorr.presentation.dialog;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AbortDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonAnalyze;
    private JButton buttonQuit;

    private Project project;

    private AbortDialog(Project project) {
        setContentPane(contentPane);
        setModal(true);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - getWidth()) / 3;
        int y = (screenSize.height - getHeight()) / 3;
        setLocation(x, y);
        getRootPane().setDefaultButton(buttonAnalyze);
        setTitle("aDoctor-R - Aborted");

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
        AbortDialog abortDialog = new AbortDialog(project);

        abortDialog.pack();
        abortDialog.setVisible(true);
    }

    private void onStartAnalysis() {
        dispose();
        AnalysisDialog.show(project);
    }

    private void onQuit() {
        dispose();
    }
}