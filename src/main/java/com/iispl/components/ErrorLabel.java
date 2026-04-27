package com.iispl.components;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

public class ErrorLabel extends HtmlMacroComponent{
	@Wire
	private Label mc_errorLabel;
	
	private String message;
	
	public ErrorLabel() {
		compose();
	}
	
	@Override
	public void afterCompose() {
		super.afterCompose();
		mc_errorLabel.setValue(message);
		mc_errorLabel.setVisible(message != null && !message.isEmpty());
		
	}
}
