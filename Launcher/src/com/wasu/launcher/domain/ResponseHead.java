package com.wasu.launcher.domain;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseHead implements Serializable{
	private String resultcode;
	private String resultdesc;
	
	public String getResultcode() {
		return resultcode;
	}
	public String getResultdesc() {
		return resultdesc;
	}
	
	
}
