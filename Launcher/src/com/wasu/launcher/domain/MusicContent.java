package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class MusicContent implements Serializable{
	private int showType;
	private String showImage;
	private MusicToView toview;
	private List<MusicSong> songList;
	private List<MusicSong> ugcList;

	public int getShowType() {
		
		return showType;
	}
	public String getShowImage() {
		return showImage;
	}
	public MusicToView getToview() {
		return toview;
	}
	public List<MusicSong> getSongList() {
		return songList;
	}
	public List<MusicSong> getUgcList() {
		return ugcList;
	}

	
}
