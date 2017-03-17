package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class WasuScheduleList implements Serializable{
	private String assetId;
	private String date;
	private String name;
	private List<WasuProgramList> programList;
	public String getAssetId() {
		return assetId;
	}
	public String getDate() {
		return date;
	}
	public String getName() {
		return name;
	}
	public List<WasuProgramList> getProgramList() {
		return programList;
	}

	
}
