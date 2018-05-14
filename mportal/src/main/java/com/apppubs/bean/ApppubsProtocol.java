package com.apppubs.bean;

import android.text.TextUtils;

import com.apppubs.util.StringUtils;

import java.util.Map;

public class ApppubsProtocol {

    private String mUri;
    private String mTitle;
    private String mType;
    private Map<String, String> mQuerys;

    public ApppubsProtocol(String uri) {
        if (TextUtils.isEmpty(uri)) {
            throw new IllegalArgumentException("协议不可为空！");
        }
        if (!uri.startsWith("apppubs://")) {
            throw new IllegalArgumentException("协议不合法！");
        }
        mUri = uri;
        mTitle = StringUtils.getQueryParameter(uri, "title");
        String[] params = StringUtils.getPathParams(uri);
        mType = params[0];
        mQuerys = StringUtils.getQueryParameters(uri);
    }

    public String getUri() {
        return mUri;
    }

    public String getTitle() {
        return mTitle;
    }


    public String getType() {
        return mType;
    }

    public Map<String, String> getQuerys() {
        return mQuerys;
    }

    public static boolean isApppubsProtocol(String url) {
        return url.startsWith("apppubs://");
    }
}
