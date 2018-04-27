package com.apppubs.bean;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * 用户和部门关联表
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年3月10日 by zhangwen create
 *
 */
public class TUserDeptLink extends SugarRecord{

	@SerializedName("userid")
	private String userId;
	@SerializedName("deptid")
	private String deptId;
	@SerializedName("orderno")
	private Integer sortId;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDepId() {
		return deptId;
	}


	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public Integer getSortId() {
		return sortId;
	}

	public void setSortId(Integer sortId) {
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
