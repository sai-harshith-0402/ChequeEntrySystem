package com.iispl.controller;

import com.iispl.components.DynamicButton;
import com.iispl.components.ErrorLabel;
import com.iispl.service.BatchService;
import com.iispl.service.BatchServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

public class RejectPhaseController extends SelectorComposer<Component> {

    private final BatchService batchService = new BatchServiceImpl();

    @Wire("#batchIdDisplay")
    private Label batchIdDisplay;

    @Wire("#reasonListbox")
    private Listbox reasonListbox;

    @Wire("#rejectError")
    private ErrorLabel rejectError;

    @Wire("#submitRejectBtn")
    private DynamicButton submitRejectBtn;

    @Wire("#cancelBtn")
    private DynamicButton cancelBtn;

    private static final String[] REASONS = {
        "Amount mismatch (words vs figures)",
        "Outdated / post-dated cheque",
        "Incorrect or invalid date",
        "Account does not exist",
        "Signature mismatch",
        "Invalid image / hash mismatch",
        "Insufficient funds",
        "Cheque reported lost or stolen"
    };

    private String batchId;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        Session session = Executions.getCurrent().getDesktop().getSession();
        batchId = (String) session.getAttribute("batchId");
        batchIdDisplay.setValue(batchId != null ? batchId : "—");

        for (String reason : REASONS) {
            reasonListbox.appendChild(new Listitem(reason));
        }

        submitRejectBtn.setLabel("Submit Rejection");
        submitRejectBtn.setOnVerify(e -> {
            Listitem selected = reasonListbox.getSelectedItem();
            if (selected == null) {
                rejectError.setMessage("Please select a rejection reason.");
                return;
            }
            String reason  = selected.getLabel();
            String bpxfXml = batchService.generateBpxfXml(batchId, reason);
            batchService.saveBatchStatus(batchId, "REJECTED", reason);

            session.setAttribute("status", "REJECTED");
            session.setAttribute("rejectionReason", reason);
            session.setAttribute("bpxfXml", bpxfXml);
            Executions.sendRedirect("validphase.zul");
        });

        cancelBtn.setLabel("Cancel");
        cancelBtn.setOnVerify(e -> Executions.sendRedirect("NPCIValidation.zul"));
    }
}