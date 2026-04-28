package com.iispl.controller;

import com.iispl.components.DynamicButton;
import com.iispl.components.InfoCard;
import com.iispl.service.BatchService;
import com.iispl.service.BatchServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

public class ValidPhaseController extends SelectorComposer<Component> {

    private final BatchService batchService = new BatchServiceImpl();

    @Wire("#batchInfoCard")
    private InfoCard batchInfoCard;

    @Wire("#rejectionReasonDiv")
    private Div rejectionReasonDiv;

    @Wire("#rejectionReasonLabel")
    private Label rejectionReasonLabel;

    @Wire("#cxfLabel")
    private Label cxfLabel;

    @Wire("#rrfLabel")
    private Label rrfLabel;

    @Wire("#bpxfDiv")
    private Div bpxfDiv;

    @Wire("#bpxfLabel")
    private Label bpxfLabel;

    @Wire("#viewBatchesBtn")
    private DynamicButton viewBatchesBtn;

    @Wire("#newSessionBtn")
    private DynamicButton newSessionBtn;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        Session session = Executions.getCurrent().getDesktop().getSession();
        String batchId  = (String) session.getAttribute("batchId");
        String status   = (String) session.getAttribute("status");
        String reason   = (String) session.getAttribute("rejectionReason");
        String cxfXml   = (String) session.getAttribute("cxfXml");
        String rrfXml   = (String) session.getAttribute("rrfXml");
        String bpxfXml  = (String) session.getAttribute("bpxfXml");

        batchInfoCard.setBatchId(batchId != null ? batchId : "—");
        batchInfoCard.setStatus(status   != null ? status  : "—");

        boolean isValid = "VALID".equalsIgnoreCase(status);

        if (!isValid && reason != null && !reason.isEmpty()) {
            rejectionReasonLabel.setValue(reason);
            rejectionReasonDiv.setVisible(true);
        } else {
            rejectionReasonDiv.setVisible(false);
        }

        cxfLabel.setValue(cxfXml  != null ? "CXF.xml  — Generated ✓" : "CXF.xml  — N/A");
        rrfLabel.setValue(rrfXml  != null ? "RRF.xml  — Generated ✓" : "RRF.xml  — N/A");
        bpxfDiv.setVisible(!isValid);
        if (!isValid) bpxfLabel.setValue(bpxfXml != null ? "BPXF.xml — Generated ✓" : "BPXF.xml — N/A");

        viewBatchesBtn.setLabel("View All Batches");
        viewBatchesBtn.setOnVerify(e -> Executions.sendRedirect("batchprocessing.zul"));

        newSessionBtn.setLabel("New Session");
        newSessionBtn.setOnVerify(e -> {
            // ── Bug 3 fix: delete the batch_session row so the next
            //    login starts a fresh batch number sequence ─────────────────────
            String sessionId = (String) session.getAttribute("sessionId");
            batchService.clearSession(sessionId);

            // Clear all session attributes
            session.removeAttribute("sessionId");
            session.removeAttribute("batchId");
            session.removeAttribute("status");
            session.removeAttribute("rejectionReason");
            session.removeAttribute("cxfXml");
            session.removeAttribute("rrfXml");
            session.removeAttribute("bpxfXml");

            Executions.sendRedirect("login.zul");
        });
    }
}