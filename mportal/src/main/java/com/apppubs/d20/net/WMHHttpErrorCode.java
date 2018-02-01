package com.apppubs.d20.net;

/**
 * Created by zhangwen on 2017/7/23.
 */

public enum WMHHttpErrorCode {
	UNKNOWN(4000,"未知错误"),PARAMETER_ERROR(4001,"参数错误"),DOWNLOAD_CANCELED(4002,"取消请求"),FILE_NOT_EXIST(3,"请求地址不存在"), MALFORMEDA_URL(4004,"错误的请求地址"),IO_EXCEPTION(4005,"IO异常"),JSON_PARSE_ERROR(6,"JSON解析错误");

	private int code;
	private String msg;

	private WMHHttpErrorCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getValue() {
		return code;
	}

	public String getMessage() {
		return msg;
	}

	public static WMHHttpErrorCode valueOf(int code) {
		for (WMHHttpErrorCode item : WMHHttpErrorCode.values()) {
			if (item.code==code) {
				return item;
			}
		}
		return null;
	}
}
