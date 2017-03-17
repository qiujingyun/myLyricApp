package com.wasu.launcher.entity;

public class MyListItem {
	private String name;
	private String pcode;
	private String bosscode;
	private String orderBy;
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getBosscode() {
		return bosscode;
	}
	public void setBosscode(String bosscode) {
		this.bosscode = bosscode;
	}
	public String getName(){
		return name;
	}
	public String getPcode(){
		return pcode;
	}
	public void setName(String name){
		this.name=name;
	}
	public void setPcode(String pcode){
		this.pcode=pcode;
	}
}
