package adoctorr.presentation.dialog;

import adoctorr.application.bean.ProposalMethodBean;
import adoctorr.application.bean.SmellMethodBean;
import adoctorr.application.refactoring.Proposer;
import com.intellij.openapi.project.Project;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

public class SmellDialog extends JDialog {
    private JPanel contentPane;

    private JList<String> listSmell;

    private JLabel labelSmellName;

    private JTextArea areaActualCode;
    private JTextArea areaProposedCode;
    private JLabel labelMethodName;
    private JButton buttonApply;
    private JButton buttonQuit;

    private Project project;
    private ArrayList<SmellMethodBean> smellMethodList;
    private ProposalMethodBean proposalMethodBean;

    private ArrayList<SmellMethodBean> unresolvedSmellMethodList;

    private SmellDialog(Project project, ArrayList<SmellMethodBean> smellMethodList) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonApply);
        setTitle("aDoctor-R - Smells' list");

        this.project = project;
        this.smellMethodList = smellMethodList;
        proposalMethodBean = null;

        unresolvedSmellMethodList = new ArrayList<>();
        for (SmellMethodBean smellMethodBean : smellMethodList) {
            if (!smellMethodBean.isResolved()) {
                unresolvedSmellMethodList.add(smellMethodBean);
            }
        }

        //TODO (priorità bassa): Migliorare l'estetica, sebbene funzioni
        DefaultListModel<String> listSmellModel = (DefaultListModel<String>) listSmell.getModel();
        for (SmellMethodBean smellMethodBean : unresolvedSmellMethodList) {
            String methodName = smellMethodBean.getMethodBean().getName();
            int smellType = smellMethodBean.getSmellType();
            String smellName = SmellMethodBean.getSmellName(smellType);

            listSmellModel.addElement(methodName + " - " + smellName + "()");
        }

        listSmell.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //This if statement prevents multiple fires
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

        // Select the first smell of the list
        listSmell.setSelectedIndex(0);
    }

    public static void show(Project project, ArrayList<SmellMethodBean> smellMethodList) {
        SmellDialog dialog = new SmellDialog(project, smellMethodList);

        dialog.pack();
        dialog.setVisible(true);
    }

    private void onUpdateSmellMethodDetails() {
        int selectedIndex = listSmell.getSelectedIndex();
        SmellMethodBean smellMethodBean = unresolvedSmellMethodList.get(selectedIndex);

        // Compute the proposal of the selected smell
        Proposer proposer = new Proposer(project);
        try {
            proposalMethodBean = proposer.computeProposal(smellMethodBean);
            String className = smellMethodBean.getMethodBean().getBelongingClass().getName();
            String packageName = smellMethodBean.getMethodBean().getBelongingClass().getBelongingPackage();
            String methodFullName = packageName + "." + className + "." + smellMethodBean.getMethodBean().getName();
            MethodDeclaration proposedMethodDeclaration = proposalMethodBean.getProposedMethodDeclaration();

            labelSmellName.setText(SmellMethodBean.getSmellName(smellMethodBean.getSmellType()));
            labelMethodName.setText(methodFullName);

            String actualCode = smellMethodBean.getMethodBean().getTextContent();
            String proposedCode = proposedMethodDeclaration.toString();
            areaActualCode.setText(actualCode);
            areaActualCode.setCaretPosition(0);
            areaProposedCode.setText(proposedCode);
            areaProposedCode.setCaretPosition(0);

            Highlighter highlighter = areaProposedCode.getHighlighter();
            highlighter.removeAllHighlights();
            ArrayList<String> proposedCodeToHighlightList = proposalMethodBean.getProposedCodeToHighlightList();
            if (proposedCodeToHighlightList != null && proposedCodeToHighlightList.size() > 0) {
                for (String proposedCodeToHighlight : proposedCodeToHighlightList) {
                    int highlightIndex = proposedCode.indexOf(proposedCodeToHighlight);
                    highlighter.addHighlight(highlightIndex, highlightIndex + proposedCodeToHighlight.length(), DefaultHighlighter.DefaultPainter);
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (BadLocationException e2) {
            // When the index of the string to highlight is wrong
            e2.printStackTrace();
        }
    }

    private void onApplyRefactoring() {
        dispose();
        RefactoringDialog.show(proposalMethodBean, project, smellMethodList);
    }

    private void onQuit() {
        dispose();
    }
}
