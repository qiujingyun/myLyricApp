package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class MusicViewContent implements Serializable{

	private int id;
	private String name;
	private int parentId;
	private String parentName;

	private int getId() {
		
		return id;
	}

    public String getName() {
		
		return name;
	}
    public int getParentId() {
    	return parentId;
    }
    
    public String getParentName() {
    	return parentName;
    }
    
    public String toString() {
    	
    	return "{\"id\":" + id +","+ "\"name\":" + "\""+name +"\""+ "," + "\"parentId\":"+parentId+","+ "\"parentName\":"+"\""+parentName+"\""+"}";
    }
	
}
