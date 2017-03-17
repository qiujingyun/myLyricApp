package com.wasu.launcher.domain;

import java.io.Serializable;

public class PlayUrlResponseResult implements Serializable{
	private String continuePlayUrl;
	private String isPlay;
	private String playUrl;
	private String result;
	public String getContinuePlayUrl() {
		return continuePlayUrl;
	}
	public String getIsPlay() {
		return isPlay;
	}
	public String getPlayUrl() {
		return playUrl;
	}
	public String getResult() {
		return result;
	}
	
	
}
