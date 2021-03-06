package com.apppubs.model.message;

import android.text.TextUtils;

/**
 * Created by zhangwen on 2017/8/11.
 */

public class FilePickerModel {
	private String filePath;
	private String fileUrl;
	private long size;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean equals(FilePickerModel model){
		if ((!TextUtils.isEmpty(model.getFilePath())&&model.getFilePath().equals(filePath))||(!TextUtils.isEmpty(model.getFileUrl())&&model.getFileUrl().equals(fileUrl))){
			return true;
		}
		return false;
	}
}
