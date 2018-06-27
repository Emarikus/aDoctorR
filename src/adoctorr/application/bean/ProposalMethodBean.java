package adoctorr.application.bean;


import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.ArrayList;

public class ProposalMethodBean {
    private SmellMethodBean smellMethodBean;
    private MethodDeclaration proposedMethodDeclaration;
    private ArrayList<String> proposedCodeToHighlightList;

    public ProposalMethodBean() {
    }

    public ArrayList<String> getProposedCodeToHighlightList() {
        return proposedCodeToHighlightList;
    }

    public void setProposedCodeToHighlightList(ArrayList<String> proposedCodeToHighlightList) {
        this.proposedCodeToHighlightList = proposedCodeToHighlightList;
    }

    public SmellMethodBean getSmellMethodBean() {
        return smellMethodBean;
    }

    public void setSmellMethodBean(SmellMethodBean smellMethodBean) {
        this.smellMethodBean = smellMethodBean;
    }

    public MethodDeclaration getProposedMethodDeclaration() {
        return proposedMethodDeclaration;
    }

    public void setProposedMethodDeclaration(MethodDeclaration proposedMethodDeclaration) {
        this.proposedMethodDeclaration = proposedMethodDeclaration;
    }
}
