package com.wasu.launcher.domain;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class RankListRequestHead extends JSONObject implements Serializable{
	private String servicecode;
	private String clienttype;
	private String clientid;
	private String clientver;
	private String devicetype;
	private String clientos;
	private String command;
	public RankListRequestHead(String servicecode, String clienttype, String clientid, String command) {
		super();
		this.servicecode = servicecode;
		this.clienttype = clienttype;
		this.clientid = clientid;
		this.command = command;
		try {
			put("clienttype", this.clienttype);
			put("servicecode", this.servicecode);
			put("clientid", this.clientid);
			
			if (command != null) {
			    put("command", this.command);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getServicecode() {
		return servicecode;
	}
	public void setServicecode(String servicecode) {
		this.servicecode = servicecode;
		try {
			put("servicecode", this.servicecode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getClienttype() {
		return clienttype;
	}
	public void setClienttype(String clienttype) {
		this.clienttype = clienttype;
		try {
			put("clienttype", this.clienttype);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
		try {
			put("clientid", this.clientid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getClientver() {
		return clientver;
	}
	public void setClientver(String clientver) {
		this.clientver = clientver;
		try {
			put("clientver", this.clientver);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getDevicetype() {
		return devicetype;
	}
	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
		try {
			put("devicetype", this.devicetype);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getClientos() {
		return clientos;
	}
	public void setClientos(String clientos) {
		this.clientos = clientos;
		try {
			put("clientos", this.clientos);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getCommand() {
		
		return command;
	}
	
	public void setCommand(String command) {
		
		this.command = command;
	}

	public String toJsonString() {
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("servicecode", this.servicecode);
			jsonObject.put("clienttype", this.clienttype);
			jsonObject.put("clientid", this.clientid);
			jsonObject.put("clientver", this.clientver);
			jsonObject.put("devicetype", this.devicetype);
			jsonObject.put("clientos", this.clientos);
			if (this.command != null) {
			    jsonObject.put("command", this.command);
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return jsonObject.toString();
		
	}
	
	
	
	
}
