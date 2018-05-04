package com.apppubs.bean;

/**
 * Created by zhangwen on 2017/10/20.
 */

public class VersionInfo {
	private boolean needUpdate = false;
	private boolean needForceUpdate = false;
	private String version;
	private String updateDescribe;
	private String updateUrl;
	private boolean needAlert = false;

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

	public boolean isNeedForceUpdate() {
		return needForceUpdate;
	}

	public void setNeedForceUpdate(boolean needForceUpdate) {
		this.needForceUpdate = needForceUpdate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUpdateDescribe() {
		return updateDescribe;
	}

	public void setUpdateDescribe(String updateDescribe) {
		this.updateDescribe = updateDescribe;
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	public boolean isNeedAlert() {
		return needAlert;
	}

	public void setNeedAlert(boolean needAlert) {
		this.needAlert = needAlert;
	}
}
