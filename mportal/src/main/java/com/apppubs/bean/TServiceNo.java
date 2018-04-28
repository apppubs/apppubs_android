package com.apppubs.bean;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * 服务号
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年4月1日 by zhangwen create
 * 2015-05-22 增加type类型
 *
 */
@Table(name="service_no")
public class TServiceNo extends SugarRecord{

	public static final int TYPE_SYSTEM = 0;
	public static final int TYPE_THIRD_PARTY = 1;
	
	public static final int ALLOW_SUBSCRIBE_YES = 0;
	
	@SerializedName("service_id")
	private String id;
	
	@SerializedName("service_name")
	private String name;
	@SerializedName("service_picurl")
	private String picURL;
	@SerializedName("service_desc")
	private String desc;
	@SerializedName("service_thedate")
	private Date createDate;
	@SerializedName("service_order")
	private int sortId;
	@SerializedName("service_flag")
	private int type;//服务号类型 ：0 系统级 1第三方
	@SerializedName("receive_flag")
	private int receiverType; //0.所有人1.给订阅人
	private transient boolean isAllowSubscribe;//是否允许订阅 只有type==0&&receiverType==0时才为true
	@Override
	public String getId() {
		
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicURL() {
		return picURL;
	}

	public void setPicURL(String picURL) {
		this.picURL = picURL;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getReceiverType() {
		return receiverType;
	}

	public void setReceiverType(int receiverType) {
		this.receiverType = receiverType;
	}

	public boolean isAllowSubscribe() {
		if(type==0&&receiverType==0){
			isAllowSubscribe = true;
		}else{
			isAllowSubscribe = false;
		}
		return isAllowSubscribe;
	}

	

	@Override
	public String toString() {
		return "TServiceNo [id=" + id + ", name=" + name + ", picURL=" + picURL + ", desc=" + desc + ", createDate="
				+ createDate + ", sortId=" + sortId + "]";
	}


	
}
