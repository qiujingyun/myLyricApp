package com.wasu.launcher.domain;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class RecommendCoaRequestBody extends JSONObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sitecode;
	private String xcode;
	private String xhdsupport;
	private String vsitecode;
	private String contentid;
	private String contenttype;
	private String playtime;
	private String programname;
	private int number;
	private String foldercode;
	
	public RecommendCoaRequestBody(String contenttype, String contentid, String playtime, String programname, int number,
			String foldercode, String sitecode, String vsitecode,
			String xcode) {
		super();
		this.sitecode = sitecode;
		this.vsitecode = vsitecode;
		this.xcode = xcode;
		this.contentid =contentid;
		this.contenttype = contenttype;
		this.playtime = playtime;
		this.programname = programname;
		this.number = number;
		this.foldercode = foldercode;
		try {
			put("contentid", contentid);
			put("contenttype", contenttype);
			
			if (playtime != null) {
			    put("playtime", playtime);
			}
			if (programname != null) {
			    put("programname", programname);
			}
			if (number != -1) {
			    put("number", number);
			}
			if (foldercode != null) {
				put("foldercode", foldercode);
			}
			put("sitecode", this.sitecode);
			put("vsitecode",this.vsitecode);
			put("xcode", this.xcode);
			put("xhdsupport", getXhdsupport());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getXhdsupport() {
		if(xhdsupport == null)
			return "SD";
		return xhdsupport;
	}
	
	public void setXhdsupport(String xhdsupport) {
		this.xhdsupport = xhdsupport;
		try {
			put("xhdsupport", this.xhdsupport);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toJsonString() {
		JSONObject jsonObject = new JSONObject();
		try{
			put("contentid", contentid);
			put("contenttype", contenttype);
			
			if (playtime != null) {
			    put("playtime", playtime);
			}
			if (programname != null) {
			    put("programname", programname);
			}
			if (number != -1) {
			    put("number", number);
			}
			if (foldercode != null) {
				put("foldercode", foldercode);
			}
			jsonObject.put("sitecode", this.sitecode);
			jsonObject.put("vsitecode", this.vsitecode);
			jsonObject.put("xcode", this.xcode);
			jsonObject.put("xhdsupport", this.xhdsupport);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return jsonObject.toString();
		
	}
	
}
