package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class WasuFolder implements Serializable{
	private String desc;
	private String folderCode;
	private String folderName;
	private String folderUrl;
	private List<WasuImage> images;
	private int level;
	private int newFlag;
	private String paraentFolderCode;
	private String recordTime;
	private String siteCode;
	private int sortIndex;
	
	public String getDesc() {
		return desc;
	}
	public String getFolderCode() {
		return folderCode;
	}
	public String getFolderName() {
		return folderName;
	}
	public String getFolderUrl() {
		return folderUrl;
	}
	public List<WasuImage> getImages() {
		return images;
	}
	public int getLevel() {
		return level;
	}
	public int getNewFlag() {
		return newFlag;
	}
	public String getParaentFolderCode() {
		return paraentFolderCode;
	}
	public String getSiteCode() {
		return siteCode;
	}
	public int getSortIndex() {
		return sortIndex;
	}
	public String getRecordTime() {
		return recordTime;
	}
	
	
}
