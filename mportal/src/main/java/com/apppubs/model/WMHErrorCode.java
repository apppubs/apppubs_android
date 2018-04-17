package com.apppubs.model;

/**
 * Created by zhangwen on 2017/10/24.
 */

public enum WMHErrorCode {
	UNKNOWN(0,"未知错误"),PARAMETER_ERROR(1,"参数错误"),DOWNLOAD_CANCELED(2,"下载取消"),FILE_NOT_EXIST(3,"文件不存在"), MALFORMEDA_URL(4,"错误的文件地址"),IO_EXCEPTION(5,"IO错误"),JSON_PARSE_ERROR(6,"JSON解析错误");

	private int code;
	private String msg;

	private WMHErrorCode(int code, java.lang.String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getValue() {
		return code;
	}

	public java.lang.String getMessage() {
		return msg;
	}

	public static WMHErrorCode valueOf(int code) {
		for (WMHErrorCode item : WMHErrorCode.values()) {
			if (item.code==code) {
				return item;
			}
		}
		return null;
	}
}
