package com.apppubs.d20.bean;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
/**
 * 新闻图片
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年1月20日 by zhangwen create
 *
 */
public class NewsPictureInfo{
	
	
	private String id;
	@SerializedName("title")
	private String title;
	@SerializedName("width")
	private String width;
	@SerializedName("height")
	private String height;
	@SerializedName("src")
	private String url;// 网络连接
	@SerializedName("infoid")
	private String infoId;// info ID
	@SerializedName("channelcode")
	private String channelCode;
	@SerializedName("contenttype")
	private String contentType;
	@SerializedName("description")
	private String description;
	private String commnet;
	@SerializedName("pubtime")
	private Date pubTime;//发布时间
	public String getCommontNum() {
		return commnet;
	}


	public void setCommontNum(String commontNum) {
		this.commnet = commontNum;
	}



	public NewsPictureInfo() {
	}
	public NewsPictureInfo(String title, String width, String height,
			String url, String infoId) {
		this.title = title;
		this.width = width;
		this.height = height;
		this.url = url;
		this.infoId = infoId;
	}






	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getWidth() {
		return width;
	}


	public void setWidth(String width) {
		this.width = width;
	}


	public String getHeight() {
		return height;
	}


	public void setHeight(String height) {
		this.height = height;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getInfoId() {
		return infoId;
	}


	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}


	public String getChannelCode() {
		return channelCode;
	}


	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}


	public String getContentType() {
		return contentType;
	}


	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Date getPubTime() {
		return pubTime;
	}


	public void setPubTime(Date pubTime) {
		this.pubTime = pubTime;
	}


	
	

}
