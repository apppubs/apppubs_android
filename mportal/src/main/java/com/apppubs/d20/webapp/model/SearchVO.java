package com.apppubs.d20.webapp.model;

/**
 * Created by zhangwen on 2018/1/9.
 */

public class SearchVO {

	private String id;
	private String name;
	private String deptName;
	private boolean isSelected;


	public UserVO toUserVO() {
		UserVO userVO = new UserVO();
		userVO.setId(getId());
		userVO.setName(getName());
		userVO.setSelected(isSelected());
		return userVO;
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

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}
}
