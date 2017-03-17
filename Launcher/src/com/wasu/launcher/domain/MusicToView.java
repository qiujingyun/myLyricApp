package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class MusicToView implements Serializable{

	private String type;
	private MusicViewContent content;

	public String getType() {
		
		return type;
	}

   public MusicViewContent getMusicViewContent() {
		
		return content;
	}
	
}
