package com.apppubs.bean.webapp;

import java.io.Serializable;

/**
 * Created by zhangwen on 2018/1/9.
 */

public class UserPickerDTO implements Serializable {

	public static final int SELECT_MODE_MULTI = 1;
	public static final int SELECT_MODE_SINGLE = 0;

	private int mSelectMode;
	private String mDeptsURL;
	private String mUsersURL;
	private String mSearchURL;
	private String mRootDeptId;
	private int maxSelectedNum = -1;

	public int getmSelectMode() {
		return mSelectMode;
	}

	public void setmSelectMode(int mSelectMode) {
		this.mSelectMode = mSelectMode;
	}

	public String getDeptsURL() {
		return mDeptsURL;
	}

	public void setmDeptsURL(String mDeptsURL) {
		this.mDeptsURL = mDeptsURL;
	}

	public String getUsersURL() {
		return mUsersURL;
	}

	public void setmUsersURL(String mUsersURL) {
		this.mUsersURL = mUsersURL;
	}

	public String getSearchURL() {
		return mSearchURL;
	}

	public void setmSearchURL(String mSearchURL) {
		this.mSearchURL = mSearchURL;
	}

	public String getRootDeptId() {
		return mRootDeptId;
	}

	public void setRootDeptId(String mRootDeptId) {
		this.mRootDeptId = mRootDeptId;
	}

	public int getMaxSelectedNum() {
		return maxSelectedNum;
	}

	public void setMaxSelectedNum(int maxSelectedNum) {
		this.maxSelectedNum = maxSelectedNum;
	}
}
