package com.apppubs.model;

import com.alibaba.fastjson.JSONObject;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.net.APHttpClient;
import com.apppubs.net.APNetException;
import com.apppubs.net.IHttpClient;
import com.apppubs.net.IRequestListener;

import java.util.Map;

/**
 * Created by siger on 2018/4/19.
 */

public class BaseBiz {
    IHttpClient mHttpClient;

    public BaseBiz() {
        mHttpClient = new APHttpClient();
    }

    public <T> void asyncPOST(final String url, final Map<String, String> params, final Class<T>
            clazz, final IRQListener<T> listener) {
        mHttpClient.asyncPOST(url, params, new IRequestListener() {

            @Override
            public void onResponse(String json, APError e) {
                if (e == null) {
                    listener.onResponse(null,e);
                    return;
                }
                JSONObject jo = JSONObject.parseObject(json);
                Integer code = jo.getInteger("code");
                String msg = jo.getString("msg");
                if (code == APErrorCode.SUCCESS.getCode()) {
                    String resultStr = jo.getString("result");
                    T r = JSONObject.parseObject(resultStr, clazz);
                    listener.onResponse(r, null);
                } else {
                    APError err = new APError(code, msg);
                    listener.onResponse(null, err);
                }
            }
        });
    }

    public <T> T syncPOST(final String url, final Map<String, String> params, final Class<T>
            clazz) throws APNetException {
        String json = mHttpClient.syncPOST(url, params);
        JSONObject jo = JSONObject.parseObject(json);
        Integer code = jo.getInteger("code");
        String msg = jo.getString("msg");
        if (code == APErrorCode.SUCCESS.getCode()) {
            String resultStr = jo.getString("result");
            return JSONObject.parseObject(resultStr, clazz);
        } else {
            APError error = new APError(code, msg);
            throw new APNetException(error);
        }
    }
}

interface IRQListener<T> {
    void onResponse(T jr, APError error);
}
