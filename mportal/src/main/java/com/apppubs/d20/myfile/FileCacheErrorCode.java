package com.apppubs.d20.myfile;

/**
 * Created by zhangwen on 2017/7/23.
 */

public enum FileCacheErrorCode {
	UNKNOWN(0,"未知错误"),PARAMETER_ERROR(1,"参数错误"),DOWNLOAD_CANCELED(2,"下载取消"),FILE_NOT_EXIST(3,"文件不存在"), MALFORMEDA_URL(4,"错误的文件地址"),IO_EXCEPTION(5,"IO错误");

	private int code;
	private String msg;

	private FileCacheErrorCode(int code, java.lang.String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getValue() {
		return code;
	}

	public java.lang.String getMessage() {
		return msg;
	}

	public static FileCacheErrorCode valueOf(int code) {
		for (FileCacheErrorCode item : FileCacheErrorCode.values()) {
			if (item.code==code) {
				return item;
			}
		}
		return null;
	}
}
