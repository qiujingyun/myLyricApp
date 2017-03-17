package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecommendResponseResult implements Serializable{
	private int count;
	private List<WasuContent> contents;
	public int getCount() {
		return count;
	}
	public List<WasuContent> getContents() {
		return contents;
	}
	
}
