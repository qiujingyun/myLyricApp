package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class RankListResponseBody implements Serializable{
	private List<WasuItem> items;

	public List<WasuItem> getItems() {
		return items;
	}
	

	
}
