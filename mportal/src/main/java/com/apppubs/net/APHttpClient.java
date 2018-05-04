package com.apppubs.net;

import android.os.Environment;

import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.util.LogM;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by siger on 2018/4/18.
 */

public class APHttpClient implements IHttpClient {

    private OkHttpClient mOkHttpClient;
    private final long cacheSize = 1024 * 1024 * 20;// 缓存文件最大限制大小20M
    private Cache cache;
    private static String cacheDirectory = Environment.getExternalStorageDirectory() +
            "/okttpcaches";

    public APHttpClient() {

        cache = new Cache(new File(cacheDirectory), cacheSize);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(6, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(false);
        builder.cache(cache);
        mOkHttpClient = builder.build();
    }

    @Override
    public void asyncPOST(String url, Map<String, String> params, final IRequestListener listener) {

        asyncPOST(url, null, params, listener);
    }

    @Override
    public void asyncPOST(String url, Map<String, String> headers, Map<String, String> params,
                          final IRequestListener listener) {
        LogM.log(APHttpClient.class, "onRequest url:" + url + ",headers: " + headers + " params:"
                + params);
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (String key : params.keySet()) {
                String val = params.get(key);
                builder.add(key, val);
            }
        }
        FormBody formBody = builder.build();


        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(formBody);

        if (headers != null) {
            for (String key : headers.keySet()) {
                String val = headers.get(key);
                requestBuilder.addHeader(key, val);
            }
        }

        Request request = requestBuilder.build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onResponse(null, new APError(APErrorCode.NETWORK_ERROR,
                        "网络异常！请检查网络是否畅通！"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                LogM.log(APHttpClient.class, "onResponse：" + responseStr);
                listener.onResponse(responseStr, null);
            }
        });

    }

    @Override
    public void asyncPOST(String url, Map<String, String> params, File file, IRequestListener
            listener) {

    }

    @Override
    public void asyncPOST(String url, Map<String, String> headers, File file, Map<String, String>
            params, IRequestListener listener) {

    }

    @Override
    public String syncPOST(String url, Map<String, String> params) throws APNetException {
        return syncPOST(url, null, params);
    }

    @Override
    public String syncPOST(String url, Map<String, String> headers, Map<String, String> params)
            throws APNetException {
        LogM.log(APHttpClient.class, "onRequest url:" + url + ",headers: " + headers + " params:"
                + params);
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (String key : params.keySet()) {
                String val = params.get(key);
                builder.add(key, val);
            }
        }
        FormBody formBody = builder.build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(formBody);

        if (headers != null) {
            for (String key : headers.keySet()) {
                String val = headers.get(key);
                requestBuilder.addHeader(key, val);
            }
        }

        Request request = requestBuilder.build();

        Call call = mOkHttpClient.newCall(request);
        try {
            String responseStr = call.execute().body().string();
            LogM.log(APHttpClient.class, "onResponse：url:" + url + " response: " + responseStr);
            return responseStr;
        } catch (IOException e) {
            e.printStackTrace();
            throw new APNetException(new APError(APErrorCode.NETWORK_ERROR, "网络异常！请检查网络是否畅通！"));
        }

    }

    @Override
    public String syncPOST(String url, Map<String, String> params, File file) throws
            APNetException {
        return null;
    }

    @Override
    public String syncPOST(String url, Map<String, String> headers, File file, Map<String,
            String> params) throws APNetException {
        return null;
    }
}
