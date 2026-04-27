package com.iispl.components;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

public class ErrorLabel extends HtmlMacroComponent {

    @Wire
    private Label mc_errorLabel;

    private String message;

    public ErrorLabel() {
        compose();
    }

    @Override
    public void afterCompose() {
        super.afterCompose();
        applyMessage();
    }

    // ── Public API ───────────────────────────────────────────────

    public void setMessage(String message) {
        this.message = message;
        applyMessage();
    }

    public String getMessage() {
        return message;
    }

    // ── Internal ─────────────────────────────────────────────────

    private void applyMessage() {
        if (mc_errorLabel == null) return;
        boolean hasMsg = message != null && !message.isEmpty();
        mc_errorLabel.setValue(hasMsg ? message : "");
        mc_errorLabel.setVisible(hasMsg);
    }
}