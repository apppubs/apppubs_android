package com.apppubs.bean;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * 头条推广图图
 *
 */
@Table(name="head_pic")
public class THeadPic extends SugarRecord{
	
	@SerializedName("infoid")
	private String infoid;
	private String channelCode;
	private String channelTypeId;
	private String topic;
	@SerializedName("picurl")
	private String picURL;
	private String appId;
	@SerializedName("sortid")
	private int sortId;
	@SerializedName("comment")
	private int commentNum;
	
	
	public String getInfoid() {
		return infoid;
	}
	public void setInfoid(String infoid) {
		this.infoid = infoid;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getPicURL() {
		return picURL;
	}
	public void setPicURL(String picURL) {
		this.picURL = picURL;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public int getSortId() {
		return sortId;
	}
	public void setSortId(int sortId) {
		this.sortId = sortId;
	}
	public int getCommentNum() {
		return commentNum;
	}
	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}
	public String getChannelTypeId() {
		return channelTypeId;
	}
	public void setChannelTypeId(String channelTypeId) {
		this.channelTypeId = channelTypeId;
	}
	@Override
	public String getId() {
		
		return null;
	}
	@Override
	public void setId(String id) {
	}
	
	
}
