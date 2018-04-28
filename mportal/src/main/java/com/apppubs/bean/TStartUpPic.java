package com.apppubs.bean;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * 闪屏图
 * @author zhangwen 2014-12-30
 *
 */

@Table(name="start_up_pic")
public class TStartUpPic extends SugarRecord{

	
	@SerializedName("picurl")
	private String picURL;
	@SerializedName("updatetime")
	private Date updateTime;
	@SerializedName("sortid")
	private int sortId;
	
	public String getPicURL() {
		return picURL;
	}
	public void setPicURL(String picURL) {
		this.picURL = picURL;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public int getSortId() {
		return sortId;
	}
	public void setSortId(int sortId) {
		this.sortId = sortId;
	}
	@Override
	public String getId() {
		
		return null;
	}
	@Override
	public void setId(String id) {
	}
	
	
}
