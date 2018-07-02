package adoctorr.application.refactoring;

import adoctorr.application.bean.proposal.DurableWakelockProposalMethodBean;
import adoctorr.application.bean.proposal.EarlyResourceBindingProposalMethodBean;
import adoctorr.application.bean.proposal.ProposalMethodBean;
import adoctorr.application.bean.smell.SmellMethodBean;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import org.eclipse.jface.text.BadLocationException;

import java.io.IOException;

public class Refactorer {

    public Refactorer() {

    }

    // Applica il refactor e sovrascrive il file
    public boolean applyRefactoring(ProposalMethodBean proposalMethodBean) throws IOException, BadLocationException {
        if (proposalMethodBean == null) {
            System.out.println("Errore precondizione");
            return false;
        } else {
            boolean result = false;
            int smellType = proposalMethodBean.getSmellMethodBean().getSmellType();
            switch (smellType) {
                case SmellMethodBean.DURABLE_WAKELOCK: {
                    DurableWakelockProposalMethodBean durableWakelockProposalMethodBean = (DurableWakelockProposalMethodBean) proposalMethodBean;
                    DurableWakelockRefactorer durableWakelockRefactorer = new DurableWakelockRefactorer();
                    result = durableWakelockRefactorer.applyRefactor(durableWakelockProposalMethodBean);
                    break;
                }
                case SmellMethodBean.EARLY_RESOURCE_BINDING: {
                    EarlyResourceBindingProposalMethodBean earlyResourceBindingProposalMethodBean = (EarlyResourceBindingProposalMethodBean) proposalMethodBean;
                    EarlyResourceBindingRefactorer earlyResourceBindingRefactorer = new EarlyResourceBindingRefactorer();
                    result = earlyResourceBindingRefactorer.applyRefactor(earlyResourceBindingProposalMethodBean);
                    break;
                }
                default:
                    break;
            }
            return result;
        }
    }
}
