package com.wasu.launcher.domain;

import java.io.Serializable;

public class WasuExtend implements Serializable{
	private String viewpoints;
	private String typeLargeItem;
	private String broadcastChannel;
	private String source;
	
	public String getTypeLargeItem() {
		return typeLargeItem;
	}

	public String getViewpoints() {
		return viewpoints;
	}

	public String getBroadcastChannel() {
		return broadcastChannel;
	}

	public String getSource() {
		return source;
	}
	
	
}
