package com.apppubs.bean;

import java.util.Date;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * 消息记录(列表)
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年3月18日 by zhangwen create
 * 
 */
@Table(name="msg_record")
public class TMsgRecord extends SugarRecord {

	public static final int TYPE_CHAT = 1;
	public static final int TYPE_SERVICE = 2;

	private String id;
	private Date updateTime;
	private String icon;
	private String title;
	private String subTitle;
	private String sourceUsernameOrId;
	private int type;// 记录类型 同msg类型
	private int unreadNum;// 未读信息数

	@Override
	public String getId() {

		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getSourceUsernameOrId() {
		return sourceUsernameOrId;
	}

	public void setSourceUsernameOrId(String sourceUsernameOrId) {
		this.sourceUsernameOrId = sourceUsernameOrId;
	}

	public int getUnreadNum() {
		return unreadNum;
	}

	public void setUnreadNum(int unreadNum) {
		this.unreadNum = unreadNum;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
