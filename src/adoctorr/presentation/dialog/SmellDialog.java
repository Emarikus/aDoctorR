package adoctorr.presentation.dialog;

import adoctorr.application.bean.ProposalMethodBean;
import adoctorr.application.bean.SmellMethodBean;
import adoctorr.application.refactoring.Proposer;
import adoctorr.application.refactoring.Refactorer;
import beans.PackageBean;
import com.intellij.openapi.project.Project;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.text.BadLocationException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
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

    private JList<String> listSmell;

    private JLabel labelSmellName;

    private JTextArea areaActualCode;
    private JTextArea areaProposedCode;
    private JLabel labelMethodName;
    private JPanel applyPane;
    private JButton buttonApply;

    private Project project;
    private ArrayList<SmellMethodBean> smellMethodList;
    private ArrayList<PackageBean> projectPackageList;
    private HashMap<String, File> sourceFileMap;
    private ProposalMethodBean proposalMethodBean;


    private SmellDialog(Project project, ArrayList<SmellMethodBean> smellMethodList, ArrayList<PackageBean> projectPackageList, HashMap<String, File> sourceFileMap) {
        setContentPane(contentPane);
        setModal(true);

        this.project = project;
        this.smellMethodList = smellMethodList;
        this.projectPackageList = projectPackageList;
        this.sourceFileMap = sourceFileMap;
        proposalMethodBean = null;

        //TODO (priorità bassa): Migliorare l'estetica, sebbene funzioni
        DefaultListModel<String> listSmellModel = (DefaultListModel<String>) listSmell.getModel();
        for (SmellMethodBean smellMethodBean : smellMethodList) {
            String methodName = smellMethodBean.getMethodBean().getName();
            int smellType = smellMethodBean.getSmellType();
            String smellName = SmellMethodBean.getSmellName(smellType);

            listSmellModel.addElement(smellName + " - (" + methodName + "())");
        }

        listSmell.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //This line prevents multiple fires
                if (!e.getValueIsAdjusting()) {
                    onUpdateSmellMethodDetails();
                }
            }
        });

        buttonApply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onApplyRefactoring();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        // Select the first smell of the list
        listSmell.setSelectedIndex(0);
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

    private void onUpdateSmellMethodDetails() {
        int selectedIndex = listSmell.getSelectedIndex();
        SmellMethodBean smellMethodBean = smellMethodList.get(selectedIndex);

        Proposer proposer = new Proposer(project);
        try {
            proposalMethodBean = proposer.computeProposal(smellMethodBean);
            MethodDeclaration proposedMethodDeclaration = proposalMethodBean.getProposedMethodDeclaration();

            labelSmellName.setText(SmellMethodBean.getSmellName(smellMethodBean.getSmellType()));
            String className = smellMethodBean.getMethodBean().getBelongingClass().getName();
            String packageName = smellMethodBean.getMethodBean().getBelongingClass().getBelongingPackage();
            String methodFullName = packageName + "." + className + "." + smellMethodBean.getMethodBean().getName();
            labelMethodName.setText(methodFullName);
            areaActualCode.setText(smellMethodBean.getMethodBean().getTextContent());
            areaProposedCode.setText(proposedMethodDeclaration.toString());
            areaActualCode.setCaretPosition(0);
            areaProposedCode.setCaretPosition(0);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void onApplyRefactoring() {
        Refactorer refactorer = new Refactorer();
        boolean result = false;
        try {
            result = refactorer.applyRefactoring(proposalMethodBean);
            if (result) {
                //TODO: Mostrare avviso di avvenuto refactoring e settare a true il valore resolved del corrispettivo SmellMethodBean
                    //TODO: Aggionrare la lista degli smell rimuovendo quello risolto
            } else {
                //TODO: Mostrare un avviso di failure
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
