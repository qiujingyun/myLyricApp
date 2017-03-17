package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class WasuContentId implements Serializable{
	private String contentId;
	private String contentType;
	private String folderCode;
	private String folderName;
	private List<WasuImageFile> images;
	private String name;
	private String playExitTime;
	private String programId;
	private String programType;
	private String recordTime;
	private String segmentId;
	private String sortIndex;
	public String getContentId() {
		return contentId;
	}
	public String getContentType() {
		return contentType;
	}
	public String getFolderCode() {
		return folderCode;
	}
	public String getFolderName() {
		return folderName;
	}
	public List<WasuImageFile> getImages() {
		return images;
	}
	public String getName() {
		return name;
	}
	public String getPlayExitTime() {
		return playExitTime;
	}
	public String getProgramId() {
		return programId;
	}
	public String getProgramType() {
		return programType;
	}
	public String getRecordTime() {
		return recordTime;
	}
	public String getSegmentId() {
		return segmentId;
	}
	public String getSortIndex() {
		return sortIndex;
	}
	
	
  	
}
