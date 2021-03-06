package adoctorr.application.bean.proposal;


import org.eclipse.jdt.core.dom.MethodDeclaration;

public class EarlyResourceBindingProposalMethodBean extends ProposalMethodBean {

    private MethodDeclaration proposedOnCreate;
    private MethodDeclaration actualOnResume;
    private MethodDeclaration proposedOnResume;

    public EarlyResourceBindingProposalMethodBean() {
    }

    public MethodDeclaration getActualOnResume() {
        return actualOnResume;
    }

    public void setActualOnResume(MethodDeclaration actualOnResume) {
        this.actualOnResume = actualOnResume;
    }

    public MethodDeclaration getProposedOnCreate() {
        return proposedOnCreate;
    }

    public void setProposedOnCreate(MethodDeclaration proposedOnCreate) {
        this.proposedOnCreate = proposedOnCreate;
    }

    public MethodDeclaration getProposedOnResume() {
        return proposedOnResume;
    }

    public void setProposedOnResume(MethodDeclaration proposedOnResume) {
        this.proposedOnResume = proposedOnResume;
    }
}
