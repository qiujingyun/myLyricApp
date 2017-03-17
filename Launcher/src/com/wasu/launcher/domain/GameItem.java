package com.wasu.launcher.domain;

public class GameItem {
	int ID;
	String Name;
	String Image;
	int Type;
	String PackageName;
	String StartActivity;
	String Argument;
	String DownloadUrl;
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getImage() {
		return Image;
	}
	public void setImage(String image) {
		Image = image;
	}
	public int getType() {
		return Type;
	}
	public void setType(int type) {
		Type = type;
	}
	public String getPackageName() {
		return PackageName;
	}
	public void setPackageName(String packageName) {
		PackageName = packageName;
	}
	public String getStartActivity() {
		return StartActivity;
	}
	public void setStartActivity(String startActivity) {
		StartActivity = startActivity;
	}
	public String getArgument() {
		return Argument;
	}
	public void setArgument(String argument) {
		Argument = argument;
	}
	public String getDownloadUrl() {
		return DownloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		DownloadUrl = downloadUrl;
	}
	
	
}
