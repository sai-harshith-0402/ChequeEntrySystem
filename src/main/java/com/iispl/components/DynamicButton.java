package com.iispl.components;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;

public class DynamicButton extends HtmlMacroComponent{
	@Wire
	private Button mc_btn;
	
	private String label = "Verify";
	private EventListener<Event> onVerify;
	public DynamicButton() {
		compose();
	}
	
	@Override
	public void afterCompose() {
		super.afterCompose();
		mc_btn.setLabel(label);
		mc_btn.setSclass("verify-btn");
		mc_btn.addEventListener(Events.ON_CLICK,e -> {
			handleClick(e);
		});
	}
	
	private void handleClick(org.zkoss.zk.ui.event.Event event) {
		mc_btn.setDisabled(true);
		mc_btn.setLabel("Verfying...");
		
		try {
			if(onVerify != null) {
				onVerify.onEvent(event);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			mc_btn.setDisabled(false);
			mc_btn.setLabel(label);
		}
	}
	
	  public void setLabel(String label) {
	        this.label = label;
	        if (mc_btn != null) mc_btn.setLabel(label);
	    }

	    public void setOnVerify(EventListener<Event> listener) {
	        this.onVerify = listener;
	    }

	    public void setDisabled(boolean disabled) {
	        if (mc_btn != null) mc_btn.setDisabled(disabled);
	    }
	
}
