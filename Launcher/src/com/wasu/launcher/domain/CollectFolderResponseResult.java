package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class CollectFolderResponseResult  implements Serializable{
	private List<WasuFolder> folders;

	public List<WasuFolder> getFolders() {
		return folders;
	}

}
