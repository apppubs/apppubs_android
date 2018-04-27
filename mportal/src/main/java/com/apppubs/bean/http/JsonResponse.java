package com.apppubs.bean.http;

/**
 * Created by siger on 2018/4/17.
 */

public class JsonResponse<T extends IJsonResult> {
    private Integer code;
    private String msg;
    private T result;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
