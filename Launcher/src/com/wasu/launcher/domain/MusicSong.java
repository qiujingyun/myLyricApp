package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class MusicSong implements Serializable{
	private int showType;
	private String showImage;
	private MusicToView toview;

	public int getShowType() {
		
		return showType;
	}
	public String getShowImage() {
		return showImage;
	}
	public MusicToView getToview() {
		return toview;
	}

	
}
