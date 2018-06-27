package adoctorr.presentation.dialog;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.*;

public class AboutDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonBack;

    private Project project;

    private AboutDialog(Project project) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonBack);

        this.project = project;

        buttonBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onBack();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onBack();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onBack();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void show(Project project) {
        AboutDialog aboutDialog = new AboutDialog(project);

        aboutDialog.pack();
        aboutDialog.setVisible(true);
    }

    private void onBack() {
        // add your code here if necessary
        dispose();
        StartDialog.show(project);
    }
}
