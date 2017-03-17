package com.wasu.launcher.domain;

import java.io.Serializable;

public class WasuVideo implements Serializable{
    private String bitrate;
	private String codec;
	private String encodingprofile;
	private String hdFlag;
	private String price;
	private String serviceCode;
	private String subContentId;
	public String getBitrate() {
		return bitrate;
	}
	public String getCodec() {
		return codec;
	}
	public String getEncodingprofile() {
		return encodingprofile;
	}
	public String getHdFlag() {
		return hdFlag;
	}
	public String getPrice() {
		return price;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public String getSubContentId() {
		return subContentId;
	}
	@Override
	public String toString() {
		return "WasuVideo [bitrate=" + bitrate + ", codec=" + codec
				+ ", encodingprofile=" + encodingprofile + ", hdFlag=" + hdFlag
				+ ", price=" + price + ", serviceCode=" + serviceCode
				+ ", subContentId=" + subContentId + "]";
	}
	
	
}
