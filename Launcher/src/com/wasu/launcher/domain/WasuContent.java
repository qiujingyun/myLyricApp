package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class WasuContent implements Serializable{
	private String placeCode;
	private String type;
	private List<WasuInfo> infoList;
	public String getPlaceCode() {
		return placeCode;
	}
	
	public int getType() {
		if(type == null){
			return Integer.MAX_VALUE;
		}
		return Integer.parseInt(type);
	}

	public List<WasuInfo> getInfoList() {
		return infoList;
	}
	
}
