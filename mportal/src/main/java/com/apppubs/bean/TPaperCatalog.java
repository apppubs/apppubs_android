package com.apppubs.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * 
 * @author zhangwen 2014-10-30 mulu
 */
@Table(name="paper_catalog")
public class TPaperCatalog extends SugarRecord {// 报纸版面目录
	@SerializedName("id")
	private String id;
	@SerializedName("name")
	private String name;// 目录的名字
	@SerializedName("mulupic")
	private String pic;
	@SerializedName("mulupdf")
	private String pdf;
	private String issueId;
	private String appId;
	@SerializedName("order")
	private int sortId;
	private transient List<TPaperInfo> infoList;
	/** 缓存图片地址 */
	private String picPath;
	/** 缓存pdf */
	private String pdfPath;
	/** 是否为横版 */
	private boolean isHorizontalVersion;

	public TPaperCatalog() {
	}

	public TPaperCatalog(String id, String name, String pic, int sortId, String issueId, String pdf, boolean isHorizontalVersion) {
		this.id = id;
		this.name = name;
		this.pic = pic;
		this.sortId = sortId;
		this.issueId = issueId;
		this.pdf = pdf;
		this.isHorizontalVersion = isHorizontalVersion;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
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

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	public List<TPaperInfo> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<TPaperInfo> infoList) {
		this.infoList = infoList;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public boolean isHorizontalVersion() {
		return isHorizontalVersion;
	}

	public void setHorizontalVersion(boolean isHorizontalVersion) {
		this.isHorizontalVersion = isHorizontalVersion;
	}

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}

	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}


	@Override
	public String getId() {
		
		return id;
	}


	@Override
	public void setId(String id) {
		this.id = id;
	}

}
