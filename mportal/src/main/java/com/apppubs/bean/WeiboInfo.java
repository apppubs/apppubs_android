package com.apppubs.bean;

import com.google.gson.annotations.SerializedName;

public class WeiboInfo {
	@SerializedName("id")
	private String id;
	@SerializedName("appname")
	private String name;
	@SerializedName("appurl")
	private String url;
	@SerializedName("apptype")
	private String type;
	@SerializedName("sortid")
	private String sortid;

/*	public WeiboInfo(String weibo_id, String weibo_name, String weibo_rul,
			String weibo_type, String weibo_sortid) {
		super();
		this.weibo_id = weibo_id;
		this.weibo_name = weibo_name;
		this.weibo_rul = weibo_rul;
		this.weibo_type = weibo_type;
		this.weibo_sortid = weibo_sortid;
	}*/

	public WeiboInfo(String weibo_name, String weibo_rul) {
		super();
		this.name = weibo_name;
		this.url = weibo_rul;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSortid() {
		return sortid;
	}

	public void setSortid(String sortid) {
		this.sortid = sortid;
	}


}
