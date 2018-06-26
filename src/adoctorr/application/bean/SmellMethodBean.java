package adoctorr.application.bean;

import beans.MethodBean;

import java.io.File;

public class SmellMethodBean {
    public static final int DURABLE_WAKELOCK = 1;
    public static final int DATA_TRANSMISSION_WITHOUT_COMPRESSION = 2;
    public static final int PROHIBITED_DATA_TRANSFER = 3;
    public static final int BULK_DATA_TRANSFER_ON_SLOW_NETWORK = 4;
    public static final int EARLY_RESOURCE_BINDING = 5;

    private MethodBean methodBean;
    private int smellType;
    private boolean resolved;
    private File sourceFile;

    public SmellMethodBean() {
    }

    public static String getSmellName(int smellType) {
        switch (smellType) {
            case DURABLE_WAKELOCK: return "Durable Wakelock";
            case DATA_TRANSMISSION_WITHOUT_COMPRESSION: return "Data Transmission Without Compression";
            case PROHIBITED_DATA_TRANSFER: return "Prohibited Data Transfer";
            case BULK_DATA_TRANSFER_ON_SLOW_NETWORK: return "Bulk Data Transfer On Slow Network";
            case EARLY_RESOURCE_BINDING: return "Early Resource Binding";
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

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }
}