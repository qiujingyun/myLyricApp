package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class MovieResponseResult implements Serializable{
	private String actors;
	private String assetId;
	private String contentId;
	private String contentType;
	private String desc;
	private String director;
	private String duber;
	private String duration;
	private String encodingprofile;
	private WasuExtend extend;
	private int hdFlag;
	private List<WasuImage> images;
	private int isPlay;
	private String name;
	private int newFlag;
	private String originalCountry;
	private String producedYear;
	private int programType;
	private String startTime;
	private List<WasuVideo> videos;
	public String getActors() {
		return actors;
	}
	public String getAssetId() {
		return assetId;
	}
	public String getContentId() {
		return contentId;
	}
	public String getContentType() {
		return contentType;
	}
	public String getDesc() {
		return desc;
	}
	public String getDirector() {
		return director;
	}
	public String getDuber() {
		return duber;
	}
	public String getDuration() {
		return duration;
	}
	public String getEncodingprofile() {
		return encodingprofile;
	}
	public WasuExtend getExtend() {
		return extend;
	}
	public int getHdFlag() {
		return hdFlag;
	}
	public List<WasuImage> getImages() {
		return images;
	}
	public int getIsPlay() {
		return isPlay;
	}
	public String getName() {
		return name;
	}
	public int getNewFlag() {
		return newFlag;
	}
	public String getOriginalCountry() {
		return originalCountry;
	}
	public String getProducedYear() {
		return producedYear;
	}
	public int getProgramType() {
		return programType;
	}
	public String getStartTime() {
		return startTime;
	}
	public List<WasuVideo> getVideos() {
		return videos;
	}
	@Override
	public String toString() {
		return "MovieResponseResult [actors=" + actors + ", assetId=" + assetId
				+ ", contentId=" + contentId + ", contentType=" + contentType
				+ ", desc=" + desc + ", director=" + director + ", duber="
				+ duber + ", duration=" + duration + ", encodingprofile="
				+ encodingprofile + ", extend=" + extend + ", hdFlag=" + hdFlag
				+ ", images=" + images + ", isPlay=" + isPlay + ", name="
				+ name + ", newFlag=" + newFlag + ", originalCountry="
				+ originalCountry + ", producedYear=" + producedYear
				+ ", programType=" + programType + ", startTime=" + startTime
				+ ", videos=" + videos + "]";
	}
	
}
