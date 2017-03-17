package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

import com.wasu.vod.BaseActivity;
import com.wasu.vod.utils.Constant;
import com.wasu.vod.utils.Utils;


public class CustomContent implements Serializable{
	private String contentId;
	private String contentType;
	private String programType;
	private String name;
	private String title;
	private String url;
	private String desc;
	private String createTime;
	private String recordTime;
	private String duration;
	private int hdFlag;
	private int updateItem;
	private int totalItem;
	
	private Object extra;
	

	public CustomContent(Contents content){
		if(content == null)
			return;
		
		contentId = content.getContentId();
		contentType = content.getContentType();
		programType = content.getProgramType(); 
		name = content.getName();
		url = getImageUrl(content.getImages());		
		createTime = content.getCreateTime();
		hdFlag = content.getHdFlag();
		extra = content.getSegment();
	}

	public CustomContent(WasuItem content) {
		// TODO Auto-generated constructor stub
		if(content == null)
			return;
		
		contentId = content.getContentid();
		contentType = content.getAssettype();
		name = content.getName();
		url = content.getPicurl();
		createTime = content.getBegintime();
	}

	public CustomContent(WasuFolder content) {
		// TODO Auto-generated constructor stub
		if(content == null)
			return;
		
		contentId = content.getFolderCode();
		contentType = null;
		recordTime = content.getRecordTime();
		desc = content.getDesc();
		name = content.getFolderName();
		url = getImageUrl(content.getImages());		
	}

	public CustomContent(WasuContentId content) {
		// TODO Auto-generated constructor stub
		if(content == null)
			return;
		
		contentId = content.getContentId();
		contentType = content.getContentType();
		programType = content.getProgramType();
		recordTime = content.getRecordTime();
		name = content.getName();
		url = getImageFileUrl(content.getImages());
	}

	public CustomContent(WasuInfo content) {
		// TODO Auto-generated constructor stub
		if(content == null)
			return;
		contentId = content.getFodderid();
		contentType = content.getFoddertype();
		programType = content.getFoddertype();
		name = content.getText();
		if(content.getImg() != null){
			url = content.getImg().split(";")[0];
		}
	}

	public CustomContent(MovieResponseResult content) {
		// TODO Auto-generated constructor stub
		if(content == null)
			return;
		
		contentId = content.getContentId();
		contentType = content.getContentType();
		programType = content.getProgramType()+"";
		desc = content.getDesc();
		name = content.getName();
		createTime = content.getStartTime();
		duration = content.getDuration();
		url = getImageUrl(content.getImages());
		hdFlag = content.getHdFlag();
	}

	public CustomContent(SeriesResponseResult content) {
		// TODO Auto-generated constructor stub
		if(content == null)
			return;
		
		contentId = content.getContentId();
		contentType = content.getContentType();
		programType = content.getProgramType()+"";
		desc = content.getDesc();
		name = content.getName();
		duration = content.getDuration();
		url = getImageUrl(content.getImages());
		hdFlag = content.getHdFlag();
	}
	
	public CustomContent(PlaybackResponseResult content) {
		// TODO Auto-generated constructor stub
		if(content == null)
			return;
		
		contentId = content.getContentId();
		contentType = content.getContentType();
		programType = content.getProgramType()+"";
		desc = content.getDesc();
		name = content.getName();
		duration = content.getDuration();
		url = getImageUrl(content.getImages());
		hdFlag = content.getHdFlag();
	
	}
	
	public CustomContent(WasuContents content) {
		// TODO Auto-generated constructor stub
		if(content == null)
			return;
		
		contentId = content.getContentId();
		contentType = content.getContentType();
		programType = content.getProgramType();
		name = content.getName();
		desc = content.getDescription();
		url = getImageFileUrl(content.getImageFiles());
		if(content.getTypeLargeItem().isEmpty()){
			if(Utils.isTypeMovie(contentType)){
				title = "电影";
			}else if(Utils.isTypeSeries(contentType)){
				title = "电视剧";
			}else if(Constant.TYPE_COLUMN.contains(content.getContentType())){
				title = "栏目";
			}else if(Constant.TYPE_NEWS.contains(content.getContentType())){
				title = "资讯";
			}
		}else{
			title = content.getTypeLargeItem();
		}
	}

	private String getImageFileUrl(List<WasuImageFile> images) {

		if(images == null)
			return null;

		String url = null;
		
		if(images.size() == 1){
			url = images.get(0).getUrl();
		}else{
			for(WasuImageFile image:images){
				String[] types = Constant.IMAGE_TYPE_POSTER.split("\\|");
				for(String type:types){
					if(type.equals(image.getType())){
						url = image.getUrl();
						break;
					}
				}
				if(url != null){
					break;
				}
			}
		}
		if(url == null && images.size() > 0 )
			url = images.get(0).getUrl();
		
		return url == null?null:url.split(",")[0];
	}

	public CustomContent(WasuProgramList program) {
		// TODO Auto-generated constructor stub
		if(program == null)
			return;
		contentId = program.getAssetId();
		desc = program.getDescription();
		name = program.getName();
		url = getImageUrl(program.getImages());
		createTime = program.getStartTime();
		duration = program.getEndTime();
		
	}
	
	public CustomContent(WasuContentIds content) {
		// TODO Auto-generated constructor stub
		if(content == null)
			return;
		if("content".equals(content.getObjType())){
			contentId = content.getContent().getContentId();
			contentType = content.getContent().getContentType();
			programType = content.getContent().getProgramType();
			recordTime = content.getRecordTime();
			name = content.getContent().getName();
			url = getImageFileUrl(content.getContent().getImages());
		}else if("folder".equals(content.getObjType())){
			contentId = content.getFolder().getFolderCode();
			contentType = null;
			recordTime = content.getRecordTime();
			desc = content.getFolder().getDesc();
			name = content.getFolder().getFolderName();
			url = getImageUrl(content.getFolder().getImages());
		}
	}
	
	private String getImageUrl(List<WasuImage> images){

		if(images == null)
			return null;

		String url = null;
		
		if(images.size() == 1){
			url = images.get(0).getUrl();
		}else{
			for(WasuImage image:images){
				String[] types = Constant.IMAGE_TYPE_POSTER.split("\\|");
				for(String type:types){
					if(type.equals(image.getType())){
						url = image.getUrl();
						break;
					}
				}
				if(url != null){
					break;
				}
			}
		}
		if(url == null && images.size() > 0 )
			url = images.get(0).getUrl();
		
		return url == null?null:url.split(",")[0];
	}
	
	public String getContentId() {
		return contentId;
	}

	public String getContentType() {
		return contentType;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getCreateTime() {
		return createTime;
	}

	public String getDuration() {
		return duration;
	}

	public int getHdFlag() {
		return hdFlag;
	}

	public int getUpdateItem() {
		return updateItem;
	}

	public int getTotalItem() {
		return totalItem;
	}
	

	public void setUpdateItem(int updateItem) {
		this.updateItem = updateItem;
	}

	public void setTotalItem(int totalItem) {
		this.totalItem = totalItem;
	}

	public String getDesc() {
		return desc;
	}

	public String getTitle() {
		return title;
	}

	public String getProgramType() {
		return programType;
	}

	
	public String getRecordTime() {
		return recordTime;
	}

	public Object getExtra() {
		return extra;
	}

	public void setExtra(Object extra) {
		this.extra = extra;
	}
}
