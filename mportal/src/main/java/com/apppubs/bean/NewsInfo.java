package com.apppubs.bean;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class NewsInfo extends SugarRecord implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String NEWS_TYPE_NORAML = "HTML";
	public static final String NEWS_TYPE_VOTE = "VOTE";
	public static final String NEWS_TYPE_SPECIALS = "SPE";
	public static final String NEWS_TYPE_VIDEO = "MOV";
	public static final String NEWS_TYPE_PICTURE = "PIC";
	public static final String NEWS_TYPE_URL = "SURL";
	public static final String NEWS_TYPE_AUDIO = "AUD";
	public static final String NEWS_TYPE_FILE = "FILE";
	
	public static final int COLLECTED = 1;
	public static final int UNCOLLECTED = 0;
	/**
	 * 非推广信息
	 */
	public static final int POPULATION_TYPE_NONE = 0;
	/**
	 * 频道推广
	 */
	public static final int POPULATION_TYPE_CHANNEL = 1;
	/**
	 * 顶层推广
	 */
	public static final int POPULATION_TYPE_TOP = 2;
	
	@SerializedName("infoid")
	private String id; 
	private String channelCode;//频道code
	@SerializedName("topic")
	private String title;//标题
	@SerializedName("pubtime")
	private Date pubTime;//发布时间
	@SerializedName("summary")
	private String summary;//摘要
	
//	private int type;//信息类别 普通信息，投票，专题，视频，图片
	@SerializedName("contenttype")
	private String type;// 普通类型 ：HTML ,SURL
	@SerializedName("contenturl")
	private String contentUrl;
	private String url;
	@SerializedName("picurl")
	private String picURL;
	/**
	 * 大图url，本条信息可能在列表页显示较大的图,如推广信息
	 */
	private String picBigURL;
	private String content;
	@SerializedName("comment")
	private int commentNum;
	private int isCollected;
	private String videoURL;
	//标识是否为重点强调的信息 (推广)0==默认信息不推广1==频道推广 2==主界面推广（在zaker风格下可用到）
	private int populationType;
	
	private int size;//词条信息存储大小用于统计缓存大小
	
	@SerializedName("fontsize")
	private int fontSizeFlag;
	@SerializedName("fontname")
	private int fontFamilyFlag;
	@SerializedName("collectflag")
	private int collectFlag;
	@SerializedName("shareflag")
	private int shareFlag;
	@SerializedName("commentflag")
	private int commentFlag;
	@SerializedName("tag")
	private String tag;
	
	public String getTitle() {
		return title;
	}

	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getPubTime() {
		return pubTime;
	}
	public void setPubTime(Date pubTime) {
		this.pubTime = pubTime;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getIsCollected() {
		return isCollected;
	}
	public void setIsCollected(int isCollected) {
		this.isCollected = isCollected;
	}
	@Override
	public String getId() {
		return id;
	}
	@Override
	public void setId(String id) {
		this.id = id;
	}

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommontNum(int commontNum) {
		this.commentNum = commontNum;
	}

	public String getVideoURL() {
		return videoURL;
	}

	public void setVideoURL(String videoURL) {
		this.videoURL = videoURL;
	}

	public String getPicURL() {
		return picURL;
	}

	public void setPicURL(String picURL) {
		this.picURL = picURL;
	}

	public int getPopulationType() {
		return populationType;
	}

	public void setPopulationType(int populationType) {
		this.populationType = populationType;
	}

	public String getPicBigURL() {
		return picBigURL;
	}

	public void setPicBigURL(String picBigURL) {
		this.picBigURL = picBigURL;

	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getFontSizeFlag() {
		return fontSizeFlag;
	}

	public void setFontSizeFlag(int fontSizeFlag) {
		this.fontSizeFlag = fontSizeFlag;
	}

	public int getFontFamilyFlag() {
		return fontFamilyFlag;
	}

	public void setFontFamilyFlag(int fontFamilyFlag) {
		this.fontFamilyFlag = fontFamilyFlag;
	}


	public int getCollectFlag() {
		return collectFlag;
	}

	public void setCollectFlag(int collectFlag) {
		this.collectFlag = collectFlag;
	}

	public int getShareFlag() {
		return shareFlag;
	}

	public void setShareFlag(int shareFlag) {
		this.shareFlag = shareFlag;
	}

	public int getCommentFlag() {
		return commentFlag;
	}

	public void setCommentFlag(int commentFlag) {
		this.commentFlag = commentFlag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	
}
