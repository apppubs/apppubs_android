package com.apppubs.bean;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * 
 * @author zhangwen 2014-10-30 the issue(qi) entity
 *  一期的内容
 */
@Table(name="paper_issue")
public class TPaperIssue extends SugarRecord {

	@SerializedName("id")
	private String id;
	@SerializedName("qiname")
	private String name;// QINAME
	private String paperCode;
	@SerializedName("fengmian")
	private String cover;// FENGMIAN,
	private transient List<TPaperCatalog> catalogList;// the catalogs of this
														// Issue
	private int isDownload;
	private String firstCatalogPic;// 首版图片（本地地址，不是网络地址）

	private Date downloadedTime;
	private int size;

	public TPaperIssue() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPaperCode() {
		return paperCode;
	}

	public void setPaperCode(String paperCode) {
		this.paperCode = paperCode;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public List<TPaperCatalog> getCatalogList() {
		return catalogList;
	}

	public void setCatalogList(List<TPaperCatalog> catalogList) {
		this.catalogList = catalogList;
	}

	public int getIsDownload() {
		return isDownload;
	}

	public void setIsDownload(int isDownload) {
		this.isDownload = isDownload;
	}

	public String getFirstCatalogPic() {
		return firstCatalogPic;
	}

	public void setFirstCatalogPic(String firstCatalogPic) {
		this.firstCatalogPic = firstCatalogPic;
	}

	public Date getDownloadedTime() {
		return downloadedTime;
	}

	public void setDownloadedTime(Date downloadedTime) {
		this.downloadedTime = downloadedTime;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
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
