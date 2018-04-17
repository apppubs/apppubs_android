package com.apppubs.bean;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class NewsAudioInfo {

	@SerializedName("infoid")
	private String infoId;
	@SerializedName("title")
	private String title;
	@SerializedName("picurl")
	private String picUrl;
	@SerializedName("audiourl")
	private String audioUrl;
	@SerializedName("description")
	private String description;
	@SerializedName("pubtime")
	private Date pubTime;
	
	public String getInfoId() {
		return infoId;
	}
	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
	public String getAudioUrl() {
		return audioUrl;
	}
	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
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
