package com.wasu.launcher.domain;

import java.io.Serializable;

public class WasuItem implements Serializable{
	private String assettype;
	private String contentid;
	private int playcount;
	private String showurl;
	private String playurl;
	private String picurl;
	private String hdplayurl;
	private String name;
	private String programname;
	private String begintime;
	private String endtime;
	private String columncode;

	public String getColumncode() {
		return columncode;
	}
	public int getPlaycount() {
		return playcount;
	}
	public String getContentid() {
		return contentid;
	}
	public String getHdplayurl() {
		return hdplayurl;
	}
	public String getName() {
		return name;
	}
	public String getProgramname() {
		return programname;
	}
	public String getEndtime() {
		return endtime;
	}
	public String getShowurl() {
		return showurl;
	}
	public String getBegintime() {
		return begintime;
	}
	public String getAssettype() {
		return assettype;
	}
	public String getPlayurl() {
		return playurl;
	}
	public String getPicurl() {
		return picurl;
	}
	
	
}
