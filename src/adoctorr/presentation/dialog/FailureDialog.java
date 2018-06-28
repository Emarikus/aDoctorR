package adoctorr.presentation.dialog;

import adoctorr.application.bean.smell.SmellMethodBean;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FailureDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonBack;
    private JButton buttonQuit;

    private Project project;
    private ArrayList<SmellMethodBean> smellMethodList;

    private FailureDialog(Project project, ArrayList<SmellMethodBean> smellMethodList) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonBack);
        setTitle("aDoctor-R - Success");

        this.project = project;
        this.smellMethodList = smellMethodList;

        buttonBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onBack();
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

    public static void show(Project project, ArrayList<SmellMethodBean> smellMethodList) {
        FailureDialog failureDialog = new FailureDialog(project, smellMethodList);

        failureDialog.pack();
        failureDialog.setVisible(true);
    }

    private void onBack() {
        dispose();
        SmellDialog.show(project, smellMethodList);
    }

    private void onQuit() {
        dispose();
    }
}
