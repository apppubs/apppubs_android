package com.mportal.client.bean;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * 
 * @author zhangwen 2014-10-30
 *	info
 *	这一套注释（Gson）在“获取一条信息”时用到
 *
 */
public class PaperInfo extends SugarRecord implements Serializable{
	
	@SerializedName("infoid")
	private String id;
	@SerializedName("name")
	private String title;
	@SerializedName("title2")
	private String subTitle;//二级标题
	@SerializedName("content")
	private String content;
	private String contentAbs;//内容摘要手动生成
	@SerializedName("summary")
	private String summary;//也是摘要后台提供
	@SerializedName("author")
	private String author;
	@SerializedName("authormail")
	private String authorMail;
	@SerializedName("qiid")
	private String issueId;
	@SerializedName("qiname")
	private String issueName;
	@SerializedName("papercode")
	private String paperCode;
	@SerializedName("shorturl")
	private String shortURL;
	

	//其他的不会被序列化和反序列化
	private transient Date pubTime;//发布时间
	private transient Date addTime;//后台插入时间
	@SerializedName("posw")
	private int posW;
	@SerializedName("posh")
	private int posH;
	@SerializedName("posx")
	private int posX;
	@SerializedName("posy")
	private int posY;
	private String catalogId;
	@SerializedName("pic1")
	private String pic1;
	@SerializedName("pic2")
	private String pic2;
	private transient String appId;
	
	private transient String md5URL;
	
	
	public PaperInfo() {
		
	}
	
	
	public PaperInfo(String id, String title, int posW, int posH, int posX,
			int posY, String catalogId) {
		this.id = id;
		this.title = title;
		this.posW = posW;
		this.posH = posH;
		this.posX = posX;
		this.posY = posY;
		this.catalogId = catalogId;
	}
	
	public PaperInfo(String id, String title, int posW, int posH, int posX,
			int posY, String catalogId,String issueId) {
		this(id,title,posW,posH,posX,posY,catalogId);
		this.issueId = issueId;
	}


	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContentAbs() {
		return contentAbs;
	}


	public void setContentAbs(String contentAbs) {
		this.contentAbs = contentAbs;
	}


	public Date getPubTime() {
		return pubTime;
	}
	public void setPubTime(Date pubTime) {
		this.pubTime = pubTime;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public int getPosW() {
		return posW;
	}
	public void setPosW(int posW) {
		this.posW = posW;
	}
	public int getPosH() {
		return posH;
	}
	public void setPosH(int posH) {
		this.posH = posH;
	}
	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX) {
		this.posX = posX;
	}
	public int getPosY() {
		return posY;
	}
	public void setPosY(int posY) {
		this.posY = posY;
	}


	public String getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}
	public String getPic1() {
		return pic1;
	}
	public void setPic1(String pic1) {
		this.pic1 = pic1;
	}
	public String getPic2() {
		return pic2;
	}
	public void setPic2(String pic2) {
		this.pic2 = pic2;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	public String getIssueId() {
		return issueId;
	}


	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}


	public String getMd5URL() {
		return md5URL;
	}
	public void setMd5URL(String md5url) {
		md5URL = md5url;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}


	/**
	 * @return the issueName
	 */
	public String getIssueName() {
		return issueName;
	}


	/**
	 * @param issueName the issueName to set
	 */
	public void setIssueName(String issueName) {
		this.issueName = issueName;
	}


	/**
	 * @return the paperCode
	 */
	public String getPaperCode() {
		return paperCode;
	}


	/**
	 * @param paperCode the paperCode to set
	 */
	public void setPaperCode(String paperCode) {
		this.paperCode = paperCode;
	}


	public String getShortURL() {
		return shortURL;
	}


	public void setShortURL(String shortURL) {
		this.shortURL = shortURL;
	}


	@Override
	public String getId() {
		
		return id;
	}


	@Override
	public void setId(String id) {
		this.id = id;
	}


	

	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public String getAuthorMail() {
		return authorMail;
	}


	public void setAuthorMail(String authorMail) {
		this.authorMail = authorMail;
	}
	

	public void clear() {
		this.id = null;
		this.title = null;
		this.subTitle = null;
		this.content = null;
		this.contentAbs = null;
		this.summary = null;
		this.author = null;
		this.authorMail = null;
		this.pubTime = null;
		this.addTime = null;
		this.catalogId = null;
		this.pic1 = null;
		this.pic2 = null;
		this.appId = null;
		this.issueId = null;
		this.md5URL = null;
		this.shortURL = null;
	}


	@Override
	public String toString() {
		return "PaperInfo [id=" + id + ", title=" + title + ", subTitle="
				+ subTitle + ", content=" + content + ", contentAbs="
				+ contentAbs + ", summary=" + summary + ", author=" + author
				+ ", authorMail=" + authorMail + ", issId=" + issueId
				+ ", issueName=" + issueName + ", paperCode=" + paperCode
				+ ", shortURL=" + shortURL + ", posW=" + posW + ", posH="
				+ posH + ", posX=" + posX + ", posY=" + posY + ", catalogId="
				+ catalogId + "]";
	}

}
