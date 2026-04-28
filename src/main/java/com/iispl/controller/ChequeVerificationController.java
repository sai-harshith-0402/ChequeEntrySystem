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
import org.zkoss.zk.ui.Session;
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

    /**
     * Root cause fix:
     * The cheque number is captured HERE the moment the user clicks "Verify"
     * and stored as an instance field. onEnterCheque() reads THIS field —
     * never re-reads getInputValue() — so manual-entry cheques always get
     * saved with the correct cheque number regardless of what the input
     * shows when "Enter Cheque" is clicked.
     */
    private String verifiedChequeNo = "";

    @Wire("#chequeVerifyBox")
    private VerificationBox chequeVerifyBox;

    @Wire("#searchError")
    private ErrorLabel searchError;

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
                chequeVerifyBox.resetButton();
                return;
            }

            // Capture now — onEnterCheque() uses this, not getInputValue()
            verifiedChequeNo = chequeNo;
            searchError.clear();
            enterError.clear();

            String now = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            ChequeDetails found = chequeService.findByChequeNumber(chequeNo);
            if (found != null) {
                // Existing cheque — auto-fill and lock all fields
                txAmountBox.setValue(String.format("%.2f", found.getAmount()));
                txAmountBox.setReadonly(true);
                txAccountBox.setValue(found.getAccountNumber());
                txAccountBox.setReadonly(true);
                txReceiverBox.setValue(found.getReceiverName());
                txReceiverBox.setReadonly(true);
                txMicrBox.setValue(found.getMicrCode());
                txMicrBox.setReadonly(true);
                searchError.setMessage("Cheque found — details auto-filled.");
            } else {
                // New cheque — clear and open all fields for manual entry
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
        // Use the stored instance field — NOT getInputValue()
        if (verifiedChequeNo.isEmpty()) {
            enterError.setMessage("Please verify a cheque number first.");
            return;
        }

        String amount   = txAmountBox.getValue().trim();
        String accNo    = txAccountBox.getValue().trim();
        String date     = txDateBox.getValue().trim();
        String receiver = txReceiverBox.getValue().trim();
        String micr     = txMicrBox.getValue().trim();

        // Presence check
        if (amount.isEmpty() || accNo.isEmpty() || receiver.isEmpty() || micr.isEmpty()) {
            enterError.setMessage("All fields are required.");
            return;
        }

        // Amount: digits with optional up to 2 decimal places
        if (!amount.matches("\\d+(\\.\\d{1,2})?")) {
            enterError.setMessage("Amount must be a valid number (e.g. 15000 or 15000.50).");
            return;
        }

        double amt = Double.parseDouble(amount);
        if (amt <= 0) {
            enterError.setMessage("Amount must be greater than zero.");
            return;
        }

        // Account number: digits only, 8-20 characters
        if (!accNo.matches("\\d{8,20}")) {
            enterError.setMessage("Account number must contain digits only (8–20 digits).");
            return;
        }

        // Receiver name: letters and spaces only
        if (!receiver.matches("[a-zA-Z ]+")) {
            enterError.setMessage("Receiver name must contain letters only.");
            return;
        }

        // MICR code: exactly 9 digits
        if (!micr.matches("\\d{9}")) {
            enterError.setMessage("MICR code must be exactly 9 digits.");
            return;
        }

        enterError.clear();

        ChequeDetails cd = new ChequeDetails(verifiedChequeNo, amt, accNo, date, receiver, micr);

        Session session  = Executions.getCurrent().getDesktop().getSession();
        String sessionId = (String) session.getAttribute("sessionId");

        String error = batchService.addToBatch(cd, sessionId);
        if (error != null) {
            enterError.setMessage(error);
            return;
        }

        Executions.sendRedirect("batchprocessing.zul");
    }
}