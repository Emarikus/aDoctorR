package adoctorr.application.refactoring;

import adoctorr.application.bean.proposal.EarlyResourceBindingProposalMethodBean;
import org.eclipse.jface.text.BadLocationException;

import java.io.IOException;

public class EarlyResourceBindingRefactorer {

    EarlyResourceBindingRefactorer() {

    }

    public boolean applyRefactor(EarlyResourceBindingProposalMethodBean proposalMethodBean) throws BadLocationException, IOException {
        if (proposalMethodBean == null) {
            return false;
        } else {
            //TODO: Implementare

            return true;
        }
    }
}
