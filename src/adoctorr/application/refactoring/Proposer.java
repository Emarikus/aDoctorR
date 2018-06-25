package adoctorr.application.refactoring;

import adoctorr.application.bean.ProposalMethodBean;
import adoctorr.application.bean.SmellMethodBean;
import com.intellij.openapi.project.Project;

import java.io.IOException;

public class Proposer {
    private Project project;

    public Proposer(Project project) {
        this.project = project;
    }

    public ProposalMethodBean computeProposal(SmellMethodBean smellMethodBean) throws IOException {
        if (smellMethodBean != null) {
            int smellType = smellMethodBean.getSmellType();
            ProposalMethodBean proposedMethodBean = null;
            switch (smellType) {
                case SmellMethodBean.DURABLE_WAKELOCK: {
                    DurableWakelockProposer durableWakelockProposer = new DurableWakelockProposer();
                    proposedMethodBean = durableWakelockProposer.computeProposal(smellMethodBean);
                    break;
                }
                case SmellMethodBean.DATA_TRANSMISSION_WITHOUT_COMPRESSION: {
                    DataTransmissionWithoutCompressionProposer dataTransmissionWithoutCompressionProposer = new DataTransmissionWithoutCompressionProposer();
                    proposedMethodBean = dataTransmissionWithoutCompressionProposer.computeProposal(smellMethodBean);
                    break;
                }
                case SmellMethodBean.PROHIBITED_DATA_TRANSFER: {
                    ProhibitedDataTransferProposer prohibitedDataTransferProposer = new ProhibitedDataTransferProposer();
                    proposedMethodBean = prohibitedDataTransferProposer.computeProposal(smellMethodBean);
                    break;
                }
                case SmellMethodBean.BULK_DATA_TRANSFR_ON_SLOW_NETWORK: {
                    BulkDataTransferOnSlowNetworkProposer bulkDataTransferOnSlowNetworkProposer = new BulkDataTransferOnSlowNetworkProposer();
                    proposedMethodBean = bulkDataTransferOnSlowNetworkProposer.computeProposal(smellMethodBean);
                    break;
                }
                case SmellMethodBean.EARLY_RESOURCE_BINDING: {
                    EarlyResourceBindingProposer earlyResourceBindingProposer = new EarlyResourceBindingProposer();
                    proposedMethodBean = earlyResourceBindingProposer.computeProposal(smellMethodBean);
                    break;
                }
                case SmellMethodBean.RIGID_ALARM_MANAGER: {
                    RigidAlarmManagerProposer rigidAlarmManagerProposer = new RigidAlarmManagerProposer();
                    proposedMethodBean = rigidAlarmManagerProposer.computeProposal(smellMethodBean);
                    break;
                }
                default:
                    return null;
            }
            return proposedMethodBean;
        } else {
            return null;
        }
    }
}
