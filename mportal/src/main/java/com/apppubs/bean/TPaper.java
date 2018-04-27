package com.apppubs.bean;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * 
 * @author zhangwen 2014-10-30
 * 
 */
public class TPaper extends SugarRecord{

	private String id;
	private String parentId;
	@SerializedName("name")
	private String name;
	@SerializedName("code")
	private String paperCode;
	@SerializedName("sortid")
	private int sortId;
	private boolean isDefault; // IFDEFAULT
	private Date updateTime;
	@SerializedName("qiname")
	private String newestIssueName;//最新期
	private Date insertTime;
	private String appCodeCMS;
	private String baseURL;
	private int sysType;
	@SerializedName("fengmian")
	private String cover;
	@SerializedName("describe")
	private String describtion;
	@SerializedName("hotAreaBaseWidth")
	private int hotAreaBaseWidth;//热区基准宽度
	@SerializedName("hotAreaBaseHeight")
	private int hotAreaBaseHeight;


	public TPaper() {
	}

	public TPaper(String parentId, String paperName, String paperCode,
				  int sortId, boolean isDefault, Date updateTime, Date insertTime,
				  String appCodeCMS, String baseURL, int sysType, String cover,
				  String describtion) {
		super();
		this.parentId = parentId;
		this.name = paperName;
		this.paperCode = paperCode;
		this.sortId = sortId;
		this.isDefault = isDefault;
		this.updateTime = updateTime;
		this.insertTime = insertTime;
		this.appCodeCMS = appCodeCMS;
		this.baseURL = baseURL;
		this.sysType = sysType;
		this.cover = cover;
		this.describtion = describtion;
	}
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String paperName) {
		this.name = paperName;
	}

	public String getPaperCode() {
		return paperCode;
	}

	public void setPaperCode(String paperCode) {
		this.paperCode = paperCode;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public String getAppCodeCMS() {
		return appCodeCMS;
	}

	public void setAppCodeCMS(String appCodeCMS) {
		this.appCodeCMS = appCodeCMS;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public int getSysType() {
		return sysType;
	}

	public void setSysType(int sysType) {
		this.sysType = sysType;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getDescribtion() {
		return describtion;
	}

	public void setDescribtion(String describtion) {
		this.describtion = describtion;
	}

	@Override
	public String getId() {
		
		return null;
	}

	@Override
	public void setId(String id) {
	}

	public String getNewestIssueName() {
		return newestIssueName;
	}

	public void setNewestIssueName(String newestIssueName) {
		this.newestIssueName = newestIssueName;
	}

	public int getHotAreaBaseWidth() {
		return hotAreaBaseWidth;
	}

	public void setHotAreaBaseWidth(int hotAreaBaseWidth) {
		this.hotAreaBaseWidth = hotAreaBaseWidth;
	}

	public int getHotAreaBaseHeight() {
		return hotAreaBaseHeight;
	}

	public void setHotAreaBaseHeight(int hotAreaBaseHeight) {
		this.hotAreaBaseHeight = hotAreaBaseHeight;
	}
	

}
