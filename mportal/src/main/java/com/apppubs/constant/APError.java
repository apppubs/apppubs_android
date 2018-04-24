package com.apppubs.constant;

/**
 * Created by siger on 2018/4/18.
 */

public final class APError {

    private APErrorCode code;
    private String msg;

    public APError(APErrorCode code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public APError(int code, String msg) {
        this(APErrorCode.valueOf(code), msg);
    }

    public APErrorCode getCode() {
        return code;
    }

    public void setCode(APErrorCode code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
