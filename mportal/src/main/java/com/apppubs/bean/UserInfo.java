package com.apppubs.bean;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年3月10日 by zhangwen create
 * 2015-04-15 增加menuPower字段 by zhangwen
 */
public class UserInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	//result":"2","username":"zhangw","email":"zhangw@hingesoft.com.cn","cnname":"张稳","userid":"1429733645294","inoutset":"2","password":"","mobile":"18686030360"}

	private String id;
	private String userId;
	private String username;
	private String token;
	private String rongToken;
	private String trueName;
	private String nickName;
	private String type;
	private String icon;
	private String password;
	private String email;
	private String mobile;
	private String mobile2;
	private String officeNO;
	private String workTEL;
	private String sex;
	private String initials;//拼音首字母

	private Date lastUsedTime;//上次使用时间，用来判断常用

	private String menuPower;//菜单权限字符串
	private String addressBookPower;//通讯录组织权限
	private int sortId;

	private String avatarUrl;//20170113
	private String orgCode;

	private String appCodeVersion;


	public UserInfo() {
	}

	public UserInfo(String userid, String username, String cnname, String password, String user_email,
                    String user_phone) {
		this.userId = userid;
		this.username = username;
		this.trueName = cnname;
		this.password = password;
		this.email = user_email;
		this.mobile = user_phone;
		this.orgCode = "";
	}
	public UserInfo(String userid, String username, String cnname, String password, String user_email,
                    String user_phone, String menuPower) {
		this(userid,  username,  cnname,  password,  user_email,
				 user_phone);
		this.menuPower = menuPower;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRongToken() {
		return rongToken;
	}

	public void setRongToken(String rongToken) {
		this.rongToken = rongToken;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}

	public String getOfficeNO() {
		return officeNO;
	}

	public void setOfficeNO(String officeNO) {
		this.officeNO = officeNO;
	}

	public String getWorkTEL() {
		return workTEL;
	}

	public void setWorkTEL(String workTEL) {
		this.workTEL = workTEL;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	
	public Date getLastUsedTime() {
		return lastUsedTime;
	}

	public void setLastUsedTime(Date lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

	public String getMenuPower() {
		return menuPower;
	}

	public void setMenuPower(String menuPower) {
		this.menuPower = menuPower;
	}

	public String getAddressBookPower() {
		return addressBookPower;
	}

	public void setAddressBookPower(String addressBookPower) {
		this.addressBookPower = addressBookPower;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	@Override
	public String toString() {
		return "TUser [id=" + id + ", userId=" + userId + ", username=" + username + ", trueName=" + trueName
				+ ", nickName=" + nickName + ", loginResult= type=" + type + ", icon=" + icon
				+ ", password=" + password + ", email=" + email + ", mobile=" + mobile + ", mobile2=" + mobile2
				+ ", officeNO=" + officeNO + ", workTEL=" + workTEL + ", sex=" + sex + ", initials=" + initials + "]";
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getAppCodeVersion() {
		return appCodeVersion;
	}

	public void setAppCodeVersion(String appCodeVersion) {
		this.appCodeVersion = appCodeVersion;
	}
}
