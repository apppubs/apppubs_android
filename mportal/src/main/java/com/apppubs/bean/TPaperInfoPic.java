package com.apppubs.bean;

import com.orm.SugarRecord;

/**
 * 
 * @author zhangwen 2014-10-30
 *
 */
public class TPaperInfoPic extends SugarRecord {//新闻里面的图片
	
	private String id;
	private String infoId;
	private String url;
	private String issueId;
	private String appId;
	private String catalogId;
	
	
	/*public String getId() {
		return id;
	}*/
/*	public void setId(String id) {
		this.id = id;
	}*/
	public String getInfoId() {
		return infoId;
	}
	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIssueId() {
		return issueId;
	}
	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}
	@Override
	public String getId() {
		
		return null;
	}
	@Override
	public void setId(String id) {
	}
	
	
	
}
