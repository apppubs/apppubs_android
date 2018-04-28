package com.apppubs.bean;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

@Table(name="title_menu")
public class TTitleMenu extends SugarRecord{

	public static final int TYPE_LEFT = 0;
	public static final int TYPE_RIGHT = 1;
	
	@SerializedName("menuid")
	private String menuId;
	@SerializedName("menutype")
	private int type;
	@SerializedName("sortnum")
	private int orderNum;
	@SerializedName("supermenuid")
	private String superMenuId;
	
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}
	public String getSuperMenuId() {
		return superMenuId;
	}
	public void setSuperMenuId(String superMenuId) {
		this.superMenuId = superMenuId;
	}
	@Override
	public String getId() {
		return null;
	}
	@Override
	public void setId(String id) {
		
	}
	
	
}
