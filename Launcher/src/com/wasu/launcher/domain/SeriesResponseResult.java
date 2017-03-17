package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class SeriesResponseResult implements Serializable{
	private String actors;
	private String assetId;
	private String contentId;
	private List<ContentItem> contentItems;
	private String contentType;
	private String desc;
	private String director;
	private String duber;
	private String duration;
	private String encodingprofile;
	private WasuExtend extend;
	private int hdFlag;
	private List<WasuImage> images;
	private int items;
	private String name;
	private int newFlag;
	private String originalCountry;
	private String pageIndex;
	private String producedYear;
	private String programType;
	private String totalItems;
	private String totalPages;
	private int updateItem;
	public String getActors() {
		return actors;
	}
	public String getAssetId() {
		return assetId;
	}
	public String getContentId() {
		return contentId;
	}
	public List<ContentItem> getContentItems() {
		return contentItems;
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
	public int getItems() {
		return items;
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
	public String getPageIndex() {
		return pageIndex;
	}
	public String getProducedYear() {
		return producedYear;
	}
	public String getProgramType() {
		return programType;
	}
	public int getTotalItems() {
		try {
			return Integer.parseInt(totalItems);	
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	public String getTotalPages() {
		return totalPages;
	}
	public int getUpdateItem() {
		return updateItem;
	}
	@Override
	public String toString() {
		return "SeriesResponseResult [actors=" + actors + ", assetId="
				+ assetId + ", contentId=" + contentId + ", contentItems="
				+ contentItems + ", contentType=" + contentType + ", desc="
				+ desc + ", director=" + director + ", duber=" + duber
				+ ", duration=" + duration + ", encodingprofile="
				+ encodingprofile + ", extend=" + extend + ", hdFlag=" + hdFlag
				+ ", images=" + images + ", items=" + items + ", name=" + name
				+ ", newFlag=" + newFlag + ", originalCountry="
				+ originalCountry + ", pageIndex=" + pageIndex
				+ ", producedYear=" + producedYear + ", programType="
				+ programType + ", totalItems=" + totalItems + ", totalPages="
				+ totalPages + ", updateItem=" + updateItem + "]";
	}
	
}
