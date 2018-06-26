package adoctorr.application.refactoring;

import adoctorr.application.bean.*;
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
            ProposalMethodBean proposedMethodBean;
            switch (smellType) {
                case SmellMethodBean.DURABLE_WAKELOCK: {
                    DurableWakelockProposer durableWakelockProposer = new DurableWakelockProposer();
                    DurableWakelockSmellMethodBean durableWakelockSmellMethodBean = (DurableWakelockSmellMethodBean) smellMethodBean;
                    proposedMethodBean = durableWakelockProposer.computeProposal(durableWakelockSmellMethodBean);
                    break;
                }
                case SmellMethodBean.DATA_TRANSMISSION_WITHOUT_COMPRESSION: {
                    DataTransmissionWithoutCompressionProposer dataTransmissionWithoutCompressionProposer = new DataTransmissionWithoutCompressionProposer();
                    DataTransmissionWithoutCompressionSmellMethodBean dataTransmissionWithoutCompressionSmellMethodBean = (DataTransmissionWithoutCompressionSmellMethodBean) smellMethodBean;
                    proposedMethodBean = dataTransmissionWithoutCompressionProposer.computeProposal(dataTransmissionWithoutCompressionSmellMethodBean);
                    break;
                }
                case SmellMethodBean.PROHIBITED_DATA_TRANSFER: {
                    ProhibitedDataTransferProposer prohibitedDataTransferProposer = new ProhibitedDataTransferProposer();
                    ProhibitedDataTransferSmellMethodBean prohibitedDataTransferSmellMethodBean = (ProhibitedDataTransferSmellMethodBean) smellMethodBean;
                    proposedMethodBean = prohibitedDataTransferProposer.computeProposal(prohibitedDataTransferSmellMethodBean);
                    break;
                }
                case SmellMethodBean.BULK_DATA_TRANSFER_ON_SLOW_NETWORK: {
                    BulkDataTransferOnSlowNetworkProposer bulkDataTransferOnSlowNetworkProposer = new BulkDataTransferOnSlowNetworkProposer();
                    BulkDataTransferOnSlowNetworkSmellMethodBean bulkDataTransferOnSlowNetworkSmellMethodBean = (BulkDataTransferOnSlowNetworkSmellMethodBean) smellMethodBean;
                    proposedMethodBean = bulkDataTransferOnSlowNetworkProposer.computeProposal(bulkDataTransferOnSlowNetworkSmellMethodBean);
                    break;
                }
                case SmellMethodBean.EARLY_RESOURCE_BINDING: {
                    EarlyResourceBindingProposer earlyResourceBindingProposer = new EarlyResourceBindingProposer();
                    EarlyResourceBindingSmellMethodBean earlyResourceBindingSmellMethodBean = (EarlyResourceBindingSmellMethodBean) smellMethodBean;
                    proposedMethodBean = earlyResourceBindingProposer.computeProposal(earlyResourceBindingSmellMethodBean);
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
