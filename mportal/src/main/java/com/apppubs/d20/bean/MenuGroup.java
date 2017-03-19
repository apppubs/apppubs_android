package com.apppubs.d20.bean;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * 菜单分组
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年3月5日 by zhangwen create
 *
 */
public class MenuGroup extends SugarRecord{
	
	public static final int STYLE_LIST = 0;
	public static final int STYLE_GRID = 1;
	public static final int STYLE_BIG_GRID_WITH_SLIDEPIC = 2;//大方块
	
	public static final int DIVIDER_TRUE = 1;
	public static final int DIVIDER_FALSE = 0;
	
	@SerializedName("id")
	private String id;
	private String superId;
	@SerializedName("menuids")
	private String menuIds;
	@SerializedName("sortid")
	private int sortId;
	@SerializedName("style")
	private int style;
	@SerializedName("margintop")
	private int marginTop;
	@SerializedName("marginbottom")
	private int marginBottom;
	
	@SerializedName("dividertop")
	private int dividerTopFlag;//菜单组顶部分割线
	@SerializedName("dividerbottom")
	private int dividerBottomFlag;//菜单组底部分割线
	@SerializedName("dividerinternal")
	private int dividerInternalFlag;//内部菜单之间的分割线
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getSuperId() {
		return superId;
	}

	public void setSuperId(String superId) {
		this.superId = superId;
	}

	public String getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(String menuIds) {
		this.menuIds = menuIds;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public int getMarginTop() {
		return marginTop;
	}

	public void setMarginTop(int marginTop) {
		this.marginTop = marginTop;
	}

	public int getMarginBottom() {
		return marginBottom;
	}

	public void setMarginBottom(int marginBottom) {
		this.marginBottom = marginBottom;
	}

	public int getDividerTopFlag() {
		return dividerTopFlag;
	}

	public void setDividerTopFlag(int dividerTopFlag) {
		this.dividerTopFlag = dividerTopFlag;
	}

	public int getDividerBottomFlag() {
		return dividerBottomFlag;
	}

	public void setDividerBottomFlag(int dividerBottomFlag) {
		this.dividerBottomFlag = dividerBottomFlag;
	}

	public int getDividerInternalFlag() {
		return dividerInternalFlag;
	}

	public void setDividerInternalFlag(int dividerInternalFlag) {
		this.dividerInternalFlag = dividerInternalFlag;
	}
	
	
	
	

}
