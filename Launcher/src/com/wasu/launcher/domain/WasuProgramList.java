package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class WasuProgramList implements Serializable{
	private String assetId;
	private String calibratedEndTime;
	private String code;
	private String compere;
	private String description;
	private String director;
	private String duration;
	private String endTime;
	private String extendInfoFlag;
	private String fid;
	private String honoredGuest;
	private List<WasuImage> images;
	private String keyword;
	private String name;
	private String orderNumber;
	private String protagonist;
	private String relationContent;
	private String setNumber;
	private String startTime;
	private String type;
	private String watchingFocus;
	public String getAssetId() {
		return assetId;
	}
	public String getCalibratedEndTime() {
		return calibratedEndTime;
	}
	public String getCode() {
		return code;
	}
	public String getCompere() {
		return compere;
	}
	public String getDescription() {
		return description;
	}
	public String getDirector() {
		return director;
	}
	public String getDuration() {
		return duration;
	}
	public String getEndTime() {
		return endTime;
	}
	public String getExtendInfoFlag() {
		return extendInfoFlag;
	}
	public String getFid() {
		return fid;
	}
	public String getHonoredGuest() {
		return honoredGuest;
	}
	public List<WasuImage> getImages() {
		return images;
	}
	public String getKeyword() {
		return keyword;
	}
	public String getName() {
		return name;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public String getProtagonist() {
		return protagonist;
	}
	public String getRelationContent() {
		return relationContent;
	}
	public String getSetNumber() {
		return setNumber;
	}
	public String getStartTime() {
		return startTime;
	}
	public String getType() {
		return type;
	}
	public String getWatchingFocus() {
		return watchingFocus;
	}
	
	
}
