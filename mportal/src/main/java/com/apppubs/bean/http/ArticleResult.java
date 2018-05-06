package com.apppubs.bean.http;

import java.util.Date;

public class ArticleResult implements IJsonResult {

    private String infoId;
    private String topic;
    private Date pubTime;
    private String content;
    private String picURL;
    private int commentNum;
    private String fontName;
    private int fontSize;
    private String channelName;
    private int commentFlag;
    private int collectFlag;
    private int shareflag;
    private String URL;
    private String contentURL;
    private String contentType;

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public String getInfoId() {
        return infoId;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setPubTime(Date pubTime) {
        this.pubTime = pubTime;
    }

    public Date getPubTime() {
        return pubTime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setCommentFlag(int commentFlag) {
        this.commentFlag = commentFlag;
    }

    public int getCommentFlag() {
        return commentFlag;
    }

    public void setCollectFlag(int collectFlag) {
        this.collectFlag = collectFlag;
    }

    public int getCollectFlag() {
        return collectFlag;
    }

    public void setShareflag(int shareflag) {
        this.shareflag = shareflag;
    }

    public int getShareflag() {
        return shareflag;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getURL() {
        return URL;
    }

    public void setContentURL(String contentURL) {
        this.contentURL = contentURL;
    }

    public String getContentURL() {
        return contentURL;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

}