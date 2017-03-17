package com.wasu.launcher.domain;

import java.io.Serializable;

public class WasuInfo implements Serializable{
	private String url;
	private String text;
	private String img;
	private String foddertype;
	private String fodderid;
	private String jumptype;
	private String jumpurl;
	private String duration;
	
	public String getUrl() {
		return url;
	}
	public String getText() {
		return text;
	}
	public String getImg() {
		return img;
	}
	public String getFoddertype() {
		return foddertype;
	}
	public String getFodderid() {
		return fodderid;
	}
	public String getJumptype() {
		return jumptype;
	}
	public String getJumpurl() {
		return jumpurl;
	}
	public String getDuration() {
		return duration;
	}
	
}
