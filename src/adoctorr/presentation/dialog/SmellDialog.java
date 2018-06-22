package adoctorr.presentation.dialog;

import adoctorr.application.smell.SmellMethodBean;
import beans.PackageBean;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SmellDialog extends JDialog {
    private JPanel contentPane;

    private JScrollPane smellListPane;

    private JPanel smellDetailsPane;
    private JPanel descriptionPane;
    private JPanel codePane;
    private JScrollPane actualCodePane;
    private JScrollPane proposedCodePane;

    private JList listSmell;

    private JLabel labelSmellName;

    private JTextArea areaActualCode;
    private JTextArea areaProposedCode;

    private Project project;
    private ArrayList<SmellMethodBean> smellMethodList;
    private ArrayList<PackageBean> projectPackageList;
    private HashMap<String, File> sourceFileMap;


    private SmellDialog(Project project, ArrayList<SmellMethodBean> smellMethodList, ArrayList<PackageBean> projectPackageList, HashMap<String, File> sourceFileMap) {
        setContentPane(contentPane);
        setModal(true);

        this.project = project;
        this.smellMethodList = smellMethodList;
        this.projectPackageList = projectPackageList;
        this.sourceFileMap = sourceFileMap;

        //TODO: Initialize JList con smellMethodList e vede come viene

        //TODO: Listener del click di un elemento della JList con:
        //TODO: aggiornamento delle varie label, aggiornamtno actualCode, calcolo proposta e aggiormanento proposedCode

        //TODO: Selezione di default già del primo elemento della JList

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });
    }

    public static void show(Project project, ArrayList<SmellMethodBean> smellMethodList, ArrayList<PackageBean> projectPackageList, HashMap<String, File> sourceFileMap) {
        SmellDialog dialog = new SmellDialog(project, smellMethodList, projectPackageList, sourceFileMap);

        dialog.pack();
        dialog.setVisible(true);
    }

    private void onExit() {
        // add your code here if necessary
        dispose();
    }
}
