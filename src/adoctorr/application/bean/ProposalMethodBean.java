package adoctorr.application.bean;


import org.eclipse.jdt.core.dom.MethodDeclaration;

public class ProposalMethodBean {
    private SmellMethodBean smellMethodBean;
    private MethodDeclaration proposedMethodDeclaration;

    public ProposalMethodBean() {
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
