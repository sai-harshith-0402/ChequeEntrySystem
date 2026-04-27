package com.iispl.controller;

import com.iispl.components.DynamicButton;
import com.iispl.components.DynamicTable;
import com.iispl.components.ErrorLabel;
import com.iispl.components.VerificationBox;
import com.iispl.model.Batch;
import com.iispl.service.BatchService;
import com.iispl.service.BatchServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NPCIValidationController extends SelectorComposer<Component> {

    private final BatchService batchService = new BatchServiceImpl();

    @Wire("#batchVerifyBox")
    private VerificationBox batchVerifyBox;

    @Wire("#searchError")
    private ErrorLabel searchError;

    @Wire("#batchDetailSection")
    private Div batchDetailSection;

    @Wire("#npciTable")
    private DynamicTable npciTable;

    @Wire("#validBtn")
    private DynamicButton validBtn;

    @Wire("#rejectBtn")
    private DynamicButton rejectBtn;

    private static final List<String> HEADERS = Arrays.asList(
        "Cheque No.", "Amount (₹)", "Account No.", "Date", "Receiver Name", "MICR Code"
    );

    private String currentBatchId;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        batchDetailSection.setVisible(false);

        batchVerifyBox.setTitle("NPCI Validation");
        batchVerifyBox.setFieldLabel("Batch ID");
        batchVerifyBox.setPlaceholder("e.g. BATCH001");

        batchVerifyBox.setOnVerify(e -> {
            String batchId = batchVerifyBox.getInputValue().trim();
            if (batchId.isEmpty()) {
                searchError.setMessage("Please enter a Batch ID.");
                batchVerifyBox.resetButton();
                return;
            }

            List<Batch> found = batchService.getBatchById(batchId);
            if (found == null || found.isEmpty()) {
                searchError.setMessage("Batch ID not found in CXF.XML.");
                batchDetailSection.setVisible(false);
                batchVerifyBox.resetButton();
                return;
            }

            currentBatchId = batchId;
            searchError.setMessage("");

            List<Map<String, String>> rows = batchService.toBatchTableRows(found, false);
            npciTable.setHeaders(HEADERS);
            npciTable.setData(rows);
            batchDetailSection.setVisible(true);
            batchVerifyBox.resetButton();
        });

        validBtn.setLabel("Mark as Valid");
        validBtn.setOnVerify(e -> processDecision("VALID", null));

        rejectBtn.setLabel("Reject Batch");
        rejectBtn.setOnVerify(e -> {
            // pass batchId to rejection page via session
            Session session = Executions.getCurrent().getDesktop().getSession();
            session.setAttribute("batchId", currentBatchId);
            Executions.sendRedirect("rejectionphase.zul");
        });
    }

    private void processDecision(String status, String reason) {
        Session session = Executions.getCurrent().getDesktop().getSession();
        String rrfXml = batchService.generateRrfXml(currentBatchId, status);
        batchService.saveBatchStatus(currentBatchId, status, reason);
        session.setAttribute("batchId", currentBatchId);
        session.setAttribute("status", status);
        session.setAttribute("rrfXml", rrfXml);
        Executions.sendRedirect("validphase.zul");
    }
}