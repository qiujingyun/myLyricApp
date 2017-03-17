package com.wasu.launcher.domain;

import java.io.Serializable;

public class WasuPlayUrl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3837625620965337945L;

	private String bitrate;
	private String codec;
	private String continuePlayUrl;
	private String encodingprofile;
	private String hdFlag;
	private int isPlay;
	private String playUrl;
	private String result;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getBitrate() {
		return bitrate;
	}

	public String getCodec() {
		return codec;
	}

	public String getContinuePlayUrl() {
		return continuePlayUrl;
	}

	public String getEncodingprofile() {
		return encodingprofile;
	}

	public String getHdFlag() {
		return hdFlag;
	}

	public int getIsPlay() {
		return isPlay;
	}

	public String getPlayUrl() {
		return playUrl;
	}

	public String getResult() {
		return result;
	}

	public void setBitrate(String bitrate) {
		this.bitrate = bitrate;
	}

	public void setCodec(String codec) {
		this.codec = codec;
	}

	public void setContinuePlayUrl(String continuePlayUrl) {
		this.continuePlayUrl = continuePlayUrl;
	}

	public void setEncodingprofile(String encodingprofile) {
		this.encodingprofile = encodingprofile;
	}

	public void setHdFlag(String hdFlag) {
		this.hdFlag = hdFlag;
	}

	public void setIsPlay(int isPlay) {
		this.isPlay = isPlay;
	}

	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
