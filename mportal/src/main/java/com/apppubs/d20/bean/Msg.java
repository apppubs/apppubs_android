package com.apppubs.d20.bean;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * 消息实体
 * 为了区别android系统的Message 对象 起名为Msg
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年3月17日 by zhangwen create
 *
 */
public class Msg extends SugarRecord implements Serializable{

	public static final int TYPE_CONTENT_TEXT = 1;
	public static final int TYPE_CONTENT_IMAGE = 2;
	public static final int TYPE_CONTENT_SOUND = 3;
	public static final int TYPE_CONTENT_VIDEO = 4;
	
	//0 系统服务号;1 CMS服务号;2 第三方服务号;3 报纸服务号；4 聊天---最新启用
	public static final int TYPE_SYSTEM = 0;
	public static final int TYPE_CMS = 1;
	public static final int TYPE_THIRD_PARTY = 2;
	public static final int TYPE_PAPER = 3;
	public static final int TYPE_CHAT = 4;
	
	
	
	@SerializedName("ID")
	private String id;
	private int type;
	@SerializedName("sender")
	private String senderId;
	@SerializedName("receiver")
	private String receiverUsername;
	@SerializedName("sender_nm")
	private String senderName;
	@SerializedName("chatTime")
	private Date sendTime;
	
	private String title;
	
	@SerializedName("content")
	private String content;
	/**
	 * 消息内容类别
	 *  1 文字,2 图片,3 音频, 4视频 无附件则为空
	 */
	@SerializedName("contentType")
	private int contentType = 1;

	@SerializedName("filename")
	private String fileURL;
	@SerializedName("filesize")
	private Integer fileSize;//文件大小
	@SerializedName("length")
	private Integer timeLength;//时长
	private String voiceLocation;
	private String picLocation;
	private String url;
	private String info;
	
	
	public String getPicLocation() {
		return picLocation;
	}

	public void setPicLocation(String picLocation) {
		this.picLocation = picLocation;
	}


	@Override
	public String getId() {
		
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderUsername) {
		this.senderId = senderUsername;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getReceiverUsername() {
		return receiverUsername;
	}

	public void setReceiverUsername(String receiverUsername) {
		this.receiverUsername = receiverUsername;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setContent(String content) {
		this.content = content;
	}


	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getLength() {
		return timeLength;
	}

	public void setLength(int length) {
		this.timeLength = length;
	}

	public String getVoiceLocation() {
		return voiceLocation;
	}

	public void setVoiceLocation(String voiceLocation) {
		this.voiceLocation = voiceLocation;
	}

	@Override
	public String toString() {
		return "Msg [id=" + id + ", senderUsername=" + senderId + ", receiverUsername=" + receiverUsername
				+ ", sendTime=" + sendTime + ", contentType=" + contentType + ", content=" + content + "]";
	}

	public String getFileURL() {
		return fileURL;
	}

	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	

}
