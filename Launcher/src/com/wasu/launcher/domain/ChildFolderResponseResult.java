package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class ChildFolderResponseResult implements Serializable{
	private List<WasuFolder> folders;
	private int pageIndex;
	private int totalItems;
	private int totalPages;
	public List<WasuFolder> getFolders() {
		return folders;
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
	
	
}
