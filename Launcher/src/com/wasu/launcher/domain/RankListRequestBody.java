package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class RankListRequestBody extends JSONObject implements Serializable{
	private String command;
	private String sitecode;
	private String vsitecode;
	private String regioncode;
	private String assettype;
	private String toptype;
	private int detailindex;
	private String foldercode;
	private int number;
	private String area;
	private String groupname;
	private String programtype;
	private String typelargeitem;
	private String typeseconditem;
	private String pictype;
	private String picformat;
	private int picwidth;
	private int picheight;
	private String piccomtype;
	private String xcode;
	private String xhdsupport;
	
	public RankListRequestBody(String command, String sitecode, String vsitecode,
			String regioncode, String assettype, String toptype, String xcode) {
		super();
		this.command = command;
		this.sitecode = sitecode;
		this.vsitecode = vsitecode;
		this.regioncode = regioncode;
		this.assettype = assettype;
		this.toptype = toptype;
		this.xcode = xcode;
		try {
			put("command", this.command);
			put("sitecode", this.sitecode);
			put("vsitecode", this.vsitecode);
			put("regioncode", this.regioncode);
			put("assettype", this.assettype);
			put("toptype", this.toptype);
			put("xcode", this.xcode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public String getCommand() {
		return command;
	}



	public void setCommand(String command) {
		this.command = command;
		try {
			put("command", this.command);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getSitecode() {
		return sitecode;
	}



	public void setSitecode(String sitecode) {
		this.sitecode = sitecode;
		try {
			put("sitecode", this.sitecode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getVsitecode() {
		return vsitecode;
	}



	public void setVsitecode(String vsitecode) {
		this.vsitecode = vsitecode;
		try {
			put("vsitecode", this.vsitecode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getRegioncode() {
		return regioncode;
	}



	public void setRegioncode(String regioncode) {
		this.regioncode = regioncode;
		try {
			put("regioncode", this.regioncode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getAssettype() {
		return assettype;
	}



	public void setAssettype(String assettype) {
		this.assettype = assettype;
		try {
			put("assettype", this.assettype);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getToptype() {
		return toptype;
	}



	public void setToptype(String toptype) {
		this.toptype = toptype;
		try {
			put("toptype", this.toptype);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public int getDetailindex() {
		return detailindex;
	}



	public void setDetailindex(int detailindex) {
		this.detailindex = detailindex;
		try {
			put("detailindex", this.detailindex);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getFoldercode() {
		return foldercode;
	}



	public void setFoldercode(String foldercode) {
		this.foldercode = foldercode;
		try {
			put("foldercode", this.foldercode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public int getNumber() {
		return number;
	}



	public void setNumber(int number) {
		this.number = number;
		try {
			put("number", this.number);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getArea() {
		return area;
	}



	public void setArea(String area) {
		this.area = area;
		try {
			put("area", this.area);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getGroupname() {
		return groupname;
	}



	public void setGroupname(String groupname) {
		this.groupname = groupname;
		try {
			put("groupname", this.groupname);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getProgramtype() {
		return programtype;
	}



	public void setProgramtype(String programtype) {
		this.programtype = programtype;
		try {
			put("programtype", this.programtype);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getTypelargeitem() {
		return typelargeitem;
	}



	public void setTypelargeitem(String typelargeitem) {
		this.typelargeitem = typelargeitem;
		try {
			put("typelargeitem", this.typelargeitem);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getTypeseconditem() {
		return typeseconditem;
	}



	public void setTypeseconditem(String typeseconditem) {
		this.typeseconditem = typeseconditem;
		try {
			put("typeseconditem", this.typeseconditem);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getPictype() {
		return pictype;
	}



	public void setPictype(String pictype) {
		this.pictype = pictype;
		try {
			put("pictype", this.pictype);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getPicformat() {
		return picformat;
	}



	public void setPicformat(String picformat) {
		this.picformat = picformat;
		try {
			put("picformat", this.picformat);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public int getPicwidth() {
		return picwidth;
	}



	public void setPicwidth(int picwidth) {
		this.picwidth = picwidth;
		try {
			put("picwidth", this.picwidth);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public int getPicheight() {
		return picheight;
	}



	public void setPicheight(int picheight) {
		this.picheight = picheight;
		try {
			put("picheight", this.picheight);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getPiccomtype() {
		return piccomtype;
	}



	public void setPiccomtype(String piccomtype) {
		this.piccomtype = piccomtype;
		try {
			put("piccomtype", this.piccomtype);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getXcode() {
		return xcode;
	}



	public void setXcode(String xcode) {
		this.xcode = xcode;
		try {
			put("xcode", this.xcode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getXhdsupport() {
		return xhdsupport;
	}



	public void setXhdsupport(String xhdsupport) {
		this.xhdsupport = xhdsupport;
		try {
			put("xhdsupport", this.xhdsupport);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String toJsonString() {
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("command", this.command);
			jsonObject.put("sitecode", this.sitecode);
			jsonObject.put("vsitecode", this.vsitecode);
			jsonObject.put("regioncode", this.regioncode);
			jsonObject.put("assettype", this.assettype);
			jsonObject.put("toptype", this.toptype);
			jsonObject.put("xcode", this.xcode);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return jsonObject.toString();
		
	}
	
}
