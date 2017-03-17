package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class FolderResponseResult implements Serializable{
	private List<Contents> contents;
	private int pageIndex;
	private int totalItems;
	private int totalPages;
	public List<Contents> getContents() {
		return contents;
	}
	public int getPageIndex() {
		return pageIndex;
	}
	public int getTotalItems() {
		return totalItems;
	}
	public int getTotalPages() {
		return totalPages;
	}
	@Override
	public String toString() {
		return "ResponseResult [contents=" + contents + ", pageIndex="
				+ pageIndex + ", totalItems=" + totalItems + ", totalPages="
				+ totalPages + "]";
	}	
	
}
