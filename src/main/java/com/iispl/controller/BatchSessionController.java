package com.iispl.controller;

import com.iispl.components.DynamicButton;
import com.iispl.components.DynamicTable;
import com.iispl.service.BatchService;
import com.iispl.service.BatchServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BatchSessionController extends SelectorComposer<Component> {

    private final BatchService batchService = new BatchServiceImpl();

    @Wire("#statusTable")
    private DynamicTable statusTable;

    @Wire("#backBtn")
    private DynamicButton backBtn;

    private static final List<String> HEADERS = Arrays.asList(
        "Batch ID", "Status", "Rejection Reason", "Processed At"
    );

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        List<Map<String, String>> rows = batchService.findAllBatchStatus();
        statusTable.setHeaders(HEADERS);
        statusTable.setData(rows);

        backBtn.setLabel("← Back");
        backBtn.setOnVerify(e -> Executions.sendRedirect("validphase.zul"));
    }
}