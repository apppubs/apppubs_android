package com.mportal.client.bean;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * 用户部门
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年3月10日 by zhangwen create
 *
 */
public class Department extends SugarRecord{

	
	private String id;
	@SerializedName("deptid")
	private String deptId;
	@SerializedName("deptname")
	private String name;
	@SerializedName("deptlevel")
	private String level;
	@SerializedName("deptcode")
	private String code;
	@SerializedName("parentid")
	private String superId;
	@SerializedName("orderno")
	private int sortId;
	
	@SerializedName("totalnum")
	private int totalNum;
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}


	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSuperId() {
		return superId;
	}

	public void setSuperId(String superId) {
		this.superId = superId;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	
	

}
