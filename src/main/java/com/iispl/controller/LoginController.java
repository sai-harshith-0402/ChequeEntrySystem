package com.iispl.controller;

import com.iispl.components.DynamicButton;
import com.iispl.components.ErrorLabel;
import com.iispl.service.ChequeService;
import com.iispl.service.ChequeServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

public class LoginController extends SelectorComposer<Component> {

    private final ChequeService chequeService = new ChequeServiceImpl();

    @Wire("#usernameBox")
    private Textbox usernameBox;

    @Wire("#passwordBox")
    private Textbox passwordBox;

    @Wire("#loginBtn")
    private DynamicButton loginBtn;

    @Wire("#loginError")
    private ErrorLabel loginError;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        loginBtn.setLabel("Login");
        loginBtn.setOnVerify(e -> {
            String username = usernameBox.getValue().trim();
            String password = passwordBox.getValue().trim();

            if (username.isEmpty() || password.isEmpty()) {
                loginError.setMessage("Please fill in all fields.");
                return;
            }

            if (chequeService.validateLogin(username, password)) {
                Executions.sendRedirect("chequeverification.zul");
            } else {
                loginError.setMessage("Invalid username or password.");
            }
        });
    }
}