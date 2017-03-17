package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class SearchFilterResponseResult implements Serializable{
	private List<WasuContents> contents;
	private String correctionKeyword;
	private String pageIndex;
	private String totalItems;
	private String totalPages;
	
	public List<WasuContents> getContents() {
		return contents;
	}
	public String getCorrectionKeyword() {
		return correctionKeyword;
	}
	public String getPageIndex() {
		return pageIndex;
	}
	public String getTotalItems() {
		return totalItems;
	}
	public String getTotalPages() {
		return totalPages;
	}
	
	
}
