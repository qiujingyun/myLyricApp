package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class ContentItem implements Serializable{
	private String contentId;
	private List<WasuImage> images;
	private int index;
	private int isPlay;
	private String startTime;
	private List<WasuVideo> videos;
	public String getContentId() {
		return contentId;
	}
	public List<WasuImage> getImages() {
		return images;
	}
	public int getIndex() {
		return index;
	}
	public int getIsPlay() {
		return isPlay;
	}
	public String getStartTime() {
		return startTime;
	}
	public List<WasuVideo> getVideos() {
		return videos;
	}
	
}
