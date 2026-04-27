package com.iispl.components;

import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

public class DynamicTable extends HtmlMacroComponent {
	@Wire
	private Columns mc_columns;
	
	@Wire
	private Rows mc_rows;
	
	private List<String> headers;
	private List<Map<String,String>> data;
	
	public DynamicTable() {
		compose();
	}
	
	@Override
	public void afterCompose() {
		super.afterCompose();
		renderTable();
	}
	
	private void renderTable() {
		if(mc_columns == null || mc_rows == null) return;
		
		mc_columns.getChildren().clear();
		mc_rows.getChildren().clear();
		
		if(headers != null) {
			for(String header : headers) {
				Column col = new Column(header);
				mc_columns.appendChild(col);
			}
		}
		
		if(data != null && headers != null) {
			for(Map<String,String> rowData : data){
				Row row = new Row();
				for(String header : headers) {
					String value = rowData.get(header);
					row.appendChild(new Label(value != null ? value : ""));
				}
				
				mc_rows.appendChild(row);
			}
		}
	}
}
