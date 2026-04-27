package com.iispl.components;


import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class VerificationBox extends HtmlMacroComponent {

    @Wire private Label mc_title;
    @Wire private Label mc_label;
    @Wire private Textbox mc_input;
    @Wire private Button mc_btn;

    private String title;
    private String fieldLabel;
    private String placeholder;

    private EventListener<Event> onVerify;

    public VerificationBox() {
        compose();
    }

    @Override
    public void afterCompose() {
        super.afterCompose();

        if (title != null) mc_title.setValue(title);
        if (fieldLabel != null) mc_label.setValue(fieldLabel);
        if (placeholder != null) mc_input.setPlaceholder(placeholder);

        mc_btn.addEventListener(Events.ON_CLICK, e -> handleVerify(e));
    }

    private void handleVerify(Event event) {
        mc_btn.setDisabled(true);
        mc_btn.setLabel("Verifying...");

        if (onVerify != null) {
            try {
                onVerify.onEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 🔹 getters
    public String getInputValue() {
        return mc_input.getValue();
    }

    // 🔹 setters
    public void setTitle(String title) {
        this.title = title;
        if (mc_title != null) mc_title.setValue(title);
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
        if (mc_label != null) mc_label.setValue(fieldLabel);
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        if (mc_input != null) mc_input.setPlaceholder(placeholder);
    }

    public void setOnVerify(EventListener<Event> listener) {
        this.onVerify = listener;
    }

    public void resetButton() {
        mc_btn.setDisabled(false);
        mc_btn.setLabel("Verify");
    }
}