package com.wasu.launcher.domain;

import java.io.Serializable;

public class WasuSegment implements Serializable{
    private String beginTime;
	private String endTime;
	private String imageUrl;
	private String name;
	private String segmentId;
	private String type;
	public String getBeginTime() {
		return beginTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getName() {
		return name;
	}
	public String getSegmentId() {
		return segmentId;
	}
	public String getType() {
		return type;
	}
	@Override
	public String toString() {
		return "WasuSegment [beginTime=" + beginTime + ", endTime=" + endTime
				+ ", imageUrl=" + imageUrl + ", name=" + name + ", segmentId="
				+ segmentId + ", type=" + type + "]";
	}
	
	
}
