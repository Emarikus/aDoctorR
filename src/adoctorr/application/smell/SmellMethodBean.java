package adoctorr.application.smell;

import beans.MethodBean;

public class SmellMethodBean {
    public static final int DURABLE_WAKELOCK = 1;
    public static final int DATA_TRANSMISSION_WITHOUT_COMPRESSION = 2;
    public static final int PROHIBITED_DATA_TRANSFER = 3;
    public static final int BULK_DATA_TRANSFR_ON_SLOW_NETWORK = 4;
    public static final int EARLY_RESOURCE_BINDING = 5;
    public static final int RIGID_ALARM_MANAGER = 6;

    private MethodBean methodBean;
    private int smellType;
    private boolean resolved;

    public SmellMethodBean() {
    }

    public static String getSmellName(int smellType) {
        switch (smellType) {
            case DURABLE_WAKELOCK: return "Durable Wakelock";
            case DATA_TRANSMISSION_WITHOUT_COMPRESSION: return "Data Transmission Without Compression";
            case PROHIBITED_DATA_TRANSFER: return "Prohibited Data Transfer";
            case BULK_DATA_TRANSFR_ON_SLOW_NETWORK: return "Bulk Data Transfer On Slow Network";
            case EARLY_RESOURCE_BINDING: return "Early Resource Binding";
            case RIGID_ALARM_MANAGER: return "Rigid Alarm Manager";
            default: return null;
        }
    }

    public MethodBean getMethodBean() {
        return methodBean;
    }

    public void setMethodBean(MethodBean methodBean) {
        this.methodBean = methodBean;
    }

    public int getSmellType() {
        return smellType;
    }

    public void setSmellType(int smellType) {
        this.smellType = smellType;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
}