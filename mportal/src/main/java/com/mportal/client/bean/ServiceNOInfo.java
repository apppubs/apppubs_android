package com.mportal.client.bean;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class ServiceNOInfo extends SugarRecord {
	
	public static final int TYPE_NORMAL = 1;//图文
	public static final int TYPE_LINK = 2;//链接
	public static final int TYPE_NONE_CONTENT = 3;//没有正文
	
	/**
	 * serviceinfo_id 消息ID 
	 * serviceinfo_titl 消息标题
	 * serviceinfo_picurl 消息图片
	 * serviceinfo_summary 消息摘要 
	 * serviceinfo_content 消息内容 
	 * serviceinfo_thedate消息时间
	 */
	@SerializedName("serviceinfo_id")
	private String id;
	@SerializedName("serviceinfo_title")
	private String title;
	@SerializedName("serviceinfo_picurl")
	private String picurl;
	@SerializedName("serviceinfo_summary")
	private String summary;
	@SerializedName("serviceinfo_content")
	private String content;
	@SerializedName("serviceinfo_thedate")
	private Date dotime;
	@SerializedName("serviceinfo_linkurl")
	public String link;
	
	@SerializedName("serviceinfo_flag")
	private int type;//获取详细信息时需要回传此字段
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



	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Date getCreateDate() {
		return dotime;
	}

	public void setCreateDate(Date createDate) {
		this.dotime = createDate;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ServiceInfo [id=" + id + ", title=" + title + ", picurl=" + picurl + ", summary=" + summary + ", content="
				+ content + ", dotime=" + dotime + "]";
	}

	
   
}
