package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class WasuContents implements Serializable{
	private String actors;
	private String category;
	private String contentId;
	private String contentType;
	private String description;
	private String director;
	private String hot;
	private List<WasuImageFile> imageFiles;
	private String items;
	private String keyword;
	private String name;
	private String programType;
	private String region;
	private String typeLargeItem;
	private String typeSecondItem;
	private String year;
	public String getActors() {
		return actors;
	}
	public String getCategory() {
		return category;
	}
	public String getContentId() {
		return contentId;
	}
	public String getContentType() {
		return contentType;
	}
	public String getDescription() {
		return description;
	}
	public String getDirector() {
		return director;
	}
	public String getHot() {
		return hot;
	}
	public List<WasuImageFile> getImageFiles() {
		return imageFiles;
	}
	public String getItems() {
		return items;
	}
	public String getKeyword() {
		return keyword;
	}
	public String getName() {
		return name;
	}
	public String getProgramType() {
		return programType;
	}
	public String getRegion() {
		return region;
	}
	public String getTypeLargeItem() {
		return typeLargeItem;
	}
	public String getTypeSecondItem() {
		return typeSecondItem;
	}
	public String getYear() {
		return year;
	}
	
	
}
