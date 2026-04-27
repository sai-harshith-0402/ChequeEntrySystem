package com.iispl.components;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

public class InfoCard extends HtmlMacroComponent{
	@Wire 
	private Label mc_batchId;
	
	@Wire
	private Label mc_status;
	
	private String batchId;
	private String status;
	
	public InfoCard() {
		compose();
	}
	
	@Override
	public void afterCompose() {
		super.afterCompose();
		
		if(batchId != null) {
			mc_batchId.setValue(batchId);
		}
		
		if(status != null) {
			mc_status.setValue(status);
			applyStatusStyle(status);
		}
	}
	
	public void setBatchId(String batchId){
		this.batchId = batchId;
		if(mc_batchId != null) {
			mc_batchId.setValue(batchId);
		}
	}
	
	public void setStatus(String status) {
		this.status = status;
		if(mc_status != null) {
			mc_status.setValue(status);
			applyStatusStyle(status);
		}
	}
	
	private void applyStatusStyle(String status) {
		if("VALID".equalsIgnoreCase(status)) {
			mc_status.setSclass("label-status valid");
		}else {
			mc_status.setSclass("label-status invalid");
		}
	}
}
