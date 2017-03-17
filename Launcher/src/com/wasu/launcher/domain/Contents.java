package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class Contents implements Serializable{
	private long assetId;
	private String contentId;
	private String contentType;
	private String contentUrl;
	private String createTime;
	private String encodingprofile;
	private WasuExtend extend;
	private int hdFlag;
	private List<WasuImage> images;
	private String name;
	private int newFlag;
	private String programType;
	private WasuSegment segment;
	private int sortIndex;
	public long getAssetId() {
		return assetId;
	}
	public String getContentId() {
		return contentId;
	}
	public String getContentType() {
		return contentType;
	}
	public String getContentUrl() {
		return contentUrl;
	}
	public String getCreateTime() {
		return createTime;
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
	public String getName() {
		return name;
	}
	public int getNewFlag() {
		return newFlag;
	}
	public String getProgramType() {
		return programType;
	}
	public WasuSegment getSegment() {
		return segment;
	}
	public int getSortIndex() {
		return sortIndex;
	}
	@Override
	public String toString() {
		return "Contents [assetId=" + assetId + ", contentId=" + contentId
				+ ", contentType=" + contentType + ", contentUrl=" + contentUrl
				+ ", createTime=" + createTime + ", encodingprofile="
				+ encodingprofile + ", extend=" + extend + ", hdFlag=" + hdFlag
				+ ", images=" + images + ", name=" + name + ", newFlag="
				+ newFlag + ", programType=" + programType + ", segment="
				+ segment + ", sortIndex=" + sortIndex + "]";
	}
  	
}
