package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecommendRequestBody extends JSONObject implements Serializable{
	private List<String> codes;
	private String sitecode;
	private String regioncode;
	private String xcode;
	private String xhdsupport;
	private String vsitecode;
	public RecommendRequestBody(List<String> codes, String sitecode, String vsitecode,String regioncode,
			String xcode) {
		super();
		this.codes = codes;
		this.sitecode = sitecode;
		this.regioncode = regioncode;
		this.vsitecode = vsitecode;
		this.xcode = xcode;
		try {
			JSONArray jsonArray = new JSONArray();
			for(String code:codes){
				jsonArray.put(code);
			}
			put("codes", jsonArray);
			put("sitecode", this.sitecode);
			put("regioncode", this.regioncode);
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
			JSONArray jsonArray = new JSONArray();
			for(String code:codes){
				jsonArray.put(code);
			}
			put("codes", jsonArray);
			jsonObject.put("sitecode", this.sitecode);
			jsonObject.put("vsitecode", this.vsitecode);
			jsonObject.put("regioncode", this.regioncode);
			jsonObject.put("xcode", this.xcode);
			jsonObject.put("xhdsupport", this.xhdsupport);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return jsonObject.toString();
		
	}
	
}
