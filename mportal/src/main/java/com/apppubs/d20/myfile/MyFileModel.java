package com.apppubs.d20.myfile;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by zhangwen on 2017/7/17.
 */

public class MyFileModel {
	@SerializedName("id")
	private String fileId;
	@SerializedName("filename")
	private String name;
	@SerializedName("filesize")
	private long bytes;
	@SerializedName("addtime")
	private Date addTime;
	@SerializedName("filetype")
	private String typeStr;
	@SerializedName("filepath")
	private String fileUrl;
	private String fileLocalPath;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getBytes() {
		return bytes;
	}

	public void setBytes(long bytes) {
		this.bytes = bytes;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public String getTypeStr() {
		return typeStr;
	}

	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFileLocalPath() {
		return fileLocalPath;
	}

	public void setFileLocalPath(String fileLocalPath) {
		this.fileLocalPath = fileLocalPath;
	}
}
