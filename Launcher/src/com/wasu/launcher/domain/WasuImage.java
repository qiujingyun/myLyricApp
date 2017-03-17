package com.wasu.launcher.domain;

import java.io.Serializable;

public class WasuImage implements Serializable{
	private String hdflag;
	private String height;
	private String type;
	private String url;
	private String width;
	
	public String getHdflag() {
		return hdflag;
	}
	public String getHeight() {
		return height;
	}
	public String getType() {
		return type;
	}
	public String getUrl() {
		return url;
	}
	public String getWidth() {
		return width;
	}
	@Override
	public String toString() {
		return "WasuImage [height=" + height + ", type=" + type + ", url="
				+ url + ", width=" + width + "]";
	}
	
}
