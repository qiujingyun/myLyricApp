package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class WasuContentIds implements Serializable{
	private String objType;
	private int sortIndex;
	private String recordTime;
	private Folder folder;
	private Content content;
	
	public String getObjType() {
		return objType;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public String getRecordTime() {
		return recordTime;
	}

	public Folder getFolder() {
		return folder;
	}

	public Content getContent() {
		return content;
	}
	
	class Content implements Serializable{
		private String contentId;
		private String contentType;
		private String folderCode;
		private String folderName;
		private List<WasuImageFile> images;
		private String name;
		private String playExitTime;
		private String programId;
		private String programType;
		private String segmentId;
		
		public String getContentId() {
			return contentId;
		}
		public String getContentType() {
			return contentType;
		}
		public String getFolderCode() {
			return folderCode;
		}
		public String getFolderName() {
			return folderName;
		}
		public List<WasuImageFile> getImages() {
			return images;
		}
		public String getName() {
			return name;
		}
		public String getPlayExitTime() {
			return playExitTime;
		}
		public String getProgramId() {
			return programId;
		}
		public String getProgramType() {
			return programType;
		}
		public String getSegmentId() {
			return segmentId;
		}

	}

	class Folder implements Serializable{
		private String desc;
		private String folderCode;
		private String folderName;
		private String folderUrl;
		private List<WasuImage> images;
		private int level;
		private int newFlag;
		private String parentFolderCode;
		private String siteCode;
		public String getDesc() {
			return desc;
		}
		public String getFolderCode() {
			return folderCode;
		}
		public String getFolderName() {
			return folderName;
		}
		public String getFolderUrl() {
			return folderUrl;
		}
		public List<WasuImage> getImages() {
			return images;
		}
		public int getLevel() {
			return level;
		}
		public int getNewFlag() {
			return newFlag;
		}
		public String getParentFolderCode() {
			return parentFolderCode;
		}
		public String getSiteCode() {
			return siteCode;
		}
	}		
}
