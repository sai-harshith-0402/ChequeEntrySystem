package com.iispl.controller;

import com.iispl.components.ErrorLabel;
import com.iispl.components.VerificationBox;
import com.iispl.model.ChequeDetails;
import com.iispl.service.BatchService;
import com.iispl.service.BatchServiceImpl;
import com.iispl.service.ChequeService;
import com.iispl.service.ChequeServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChequeVerificationController extends SelectorComposer<Component> {

    private final ChequeService chequeService = new ChequeServiceImpl();
    private final BatchService  batchService  = new BatchServiceImpl();

    // ── Search section ───────────────────────────────────────────
    @Wire("#chequeVerifyBox")
    private VerificationBox chequeVerifyBox;

    @Wire("#searchError")
    private ErrorLabel searchError;

    // ── Details section (hidden until verified) ──────────────────
    @Wire("#detailsSection")
    private Div detailsSection;

    @Wire("#txAmountBox")
    private Textbox txAmountBox;

    @Wire("#txAccountBox")
    private Textbox txAccountBox;

    @Wire("#txDateBox")
    private Textbox txDateBox;

    @Wire("#txReceiverBox")
    private Textbox txReceiverBox;

    @Wire("#txMicrBox")
    private Textbox txMicrBox;

    @Wire("#enterError")
    private ErrorLabel enterError;

    @Wire("#enterChequeBtn")
    private Button enterChequeBtn;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        detailsSection.setVisible(false);

        chequeVerifyBox.setTitle("Cheque Entry and Verification");
        chequeVerifyBox.setFieldLabel("Cheque Number");
        chequeVerifyBox.setPlaceholder("Enter cheque number");

        chequeVerifyBox.setOnVerify(e -> {
            String chequeNo = chequeVerifyBox.getInputValue().trim();
            if (chequeNo.isEmpty()) {
                searchError.setMessage("Please enter a cheque number.");
                return;
            }
            searchError.setMessage("");

            String now = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            ChequeDetails found = chequeService.findByChequeNumber(chequeNo);
            if (found != null) {
                // auto-fill, lock fields
                txAmountBox.setValue(String.format("%.2f", found.getAmount()));
                txAmountBox.setReadonly(true);
                txAccountBox.setValue(found.getAccountNumber());
                txAccountBox.setReadonly(true);
                txReceiverBox.setValue(found.getReceiverName());
                txReceiverBox.setReadonly(true);
                txMicrBox.setValue(found.getMicrCode());
                txMicrBox.setReadonly(true);
                searchError.setMessage("✓ Cheque found — details auto-filled.");
            } else {
                // new cheque — editable
                txAmountBox.setValue("");   txAmountBox.setReadonly(false);
                txAccountBox.setValue("");  txAccountBox.setReadonly(false);
                txReceiverBox.setValue(""); txReceiverBox.setReadonly(false);
                txMicrBox.setValue("");     txMicrBox.setReadonly(false);
                searchError.setMessage("New cheque — enter details manually.");
            }
            txDateBox.setValue(now);
            txDateBox.setReadonly(true);
            detailsSection.setVisible(true);
            chequeVerifyBox.resetButton();
        });
    }

    @Listen("onClick = #enterChequeBtn")
    public void onEnterCheque() {
        String chequeNo = chequeVerifyBox.getInputValue().trim();
        String amount   = txAmountBox.getValue().trim();
        String accNo    = txAccountBox.getValue().trim();
        String date     = txDateBox.getValue().trim();
        String receiver = txReceiverBox.getValue().trim();
        String micr     = txMicrBox.getValue().trim();

        if (chequeNo.isEmpty() || amount.isEmpty() || accNo.isEmpty()
                || receiver.isEmpty() || micr.isEmpty()) {
            enterError.setMessage("All fields are required.");
            return;
        }

        double amt;
        try {
            amt = Double.parseDouble(amount);
        } catch (NumberFormatException ex) {
            enterError.setMessage("Amount must be a valid number.");
            return;
        }

        ChequeDetails cd = new ChequeDetails(chequeNo, amt, accNo, date, receiver, micr);
        batchService.addToBatch(cd);
        Executions.sendRedirect("batchprocessing.zul");
    }
}