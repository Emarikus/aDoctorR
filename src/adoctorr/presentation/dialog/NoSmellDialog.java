package adoctorr.presentation.dialog;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.*;

public class NoSmellDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonQuit;

    private NoSmellDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonQuit);
        setTitle("aDoctor-R - Congratulations");

        buttonQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onQuit();
            }
        });

        // call onQuit() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onQuit();
            }
        });

        // call onQuit() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onQuit();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onQuit() {
        dispose();
    }

    public static void show(Project project) {
        NoSmellDialog noSmellDialog = new NoSmellDialog();

        noSmellDialog.pack();
        noSmellDialog.setVisible(true);
    }
}
