package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class CollectResponseResult  implements Serializable{
	private List<WasuContentId> contentIds;

	public List<WasuContentId> getContents() {
		return contentIds;
	}

}
