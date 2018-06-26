package adoctorr.application.bean;

import org.eclipse.jdt.core.dom.MethodInvocation;

public class DurableWakelockSmellMethodBean extends SmellMethodBean {

    private MethodInvocation acquireMethodInvocation;

    public DurableWakelockSmellMethodBean() {
        super();
    }

    public MethodInvocation getAcquireMethodInvocation() {
        return acquireMethodInvocation;
    }

    public void setAcquireMethodInvocation(MethodInvocation acquireMethodInvocation) {
        this.acquireMethodInvocation = acquireMethodInvocation;
    }
}
