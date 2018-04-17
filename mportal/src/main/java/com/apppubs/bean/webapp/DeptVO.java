package com.apppubs.bean.webapp;

/**
 * Created by zhangwen on 2018/1/9.
 */

public class DeptVO {

	private String id;
	private String name;
	private boolean isLeaf;

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

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean leaf) {
		isLeaf = leaf;
	}
}
