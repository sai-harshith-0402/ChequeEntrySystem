package com.iispl.controller;

import com.iispl.components.DynamicButton;
import com.iispl.components.DynamicTable;
import com.iispl.model.Batch;
import com.iispl.service.BatchService;
import com.iispl.service.BatchServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BatchProcessingController extends SelectorComposer<Component> {

    private final BatchService batchService = new BatchServiceImpl();

    @Wire("#batchTable")
    private DynamicTable batchTable;

    @Wire("#totalChequeLabel")
    private Label totalChequeLabel;

    @Wire("#totalBatchLabel")
    private Label totalBatchLabel;

    @Wire("#addMoreBtn")
    private DynamicButton addMoreBtn;

    @Wire("#generateBtn")
    private DynamicButton generateBtn;

    private List<Batch> batches;

    // Column headers — must match keys used in toBatchTableRows()
    private static final List<String> HEADERS = Arrays.asList(
        "Batch ID", "Cheque No.", "Amount (₹)", "Account No.", "Date", "Receiver Name", "MICR Code"
    );

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        batches = batchService.getAllBatches();
        int total = batches.size();
        int batchCount = (int) Math.ceil(total / 5.0);

        totalChequeLabel.setValue(String.valueOf(total));
        totalBatchLabel.setValue(String.valueOf(Math.max(batchCount, 0)));

        List<Map<String, String>> rows = batchService.toBatchTableRows(batches, true);
        batchTable.setHeaders(HEADERS);
        batchTable.setData(rows);

        addMoreBtn.setLabel("+ Add More Cheques");
        addMoreBtn.setOnVerify(e -> Executions.sendRedirect("chequeverification.zul"));

        generateBtn.setLabel("Generate Batch & Send to NPCI");
        generateBtn.setOnVerify(e -> {
            if (batches == null || batches.isEmpty()) {
                Messagebox.show("No cheques available. Please enter cheques first.");
                return;
            }
            String xml = batchService.generateCxfXml(batches);
            Executions.getCurrent().getDesktop().getSession().setAttribute("cxfXml", xml);
            Executions.sendRedirect("NPCIValidation.zul");
        });
    }
}