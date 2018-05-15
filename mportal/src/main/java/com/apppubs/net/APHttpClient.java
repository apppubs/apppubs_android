package com.apppubs.net;

import android.os.Environment;

import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.util.LogM;
import com.apppubs.util.Utils;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
                if (e instanceof SocketTimeoutException) {
                    listener.onResponse(null, new APError(APErrorCode.NETWORK_ERROR,
                            "网络请求超时！请检查网络是否畅通！"));
                } else {
                    listener.onResponse(null, new APError(APErrorCode.NETWORK_ERROR,
                            "网络异常！请检查网络是否畅通！"));
                }
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
    public void asyncMultiPOST(String url, Map<String, String> headers, Map<String, Object>
            params, final IRequestListener listener) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (headers != null) {
            for (String key : headers.keySet()) {
                String val = headers.get(key);
                builder.addFormDataPart(key, val);
            }
        }
        if (!Utils.isEmpty(params)) {
            //追加参数
            for (String key : params.keySet()) {
                Object object = params.get(key);
                if (object instanceof File) {
                    File file = ((File) object);
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
                } else {
                    builder.addFormDataPart(key, object.toString());
                }
            }
        }

        //创建RequestBody
        RequestBody body = builder.build();
        //创建Request
        final Request request = new Request.Builder().url(url).post(body).build();
        //单独设置参数 比如读取超时时间
        final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onResponse(null, new APError(APErrorCode.GENERAL_ERROR, "上传失败！"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    listener.onResponse(string, null);
                } else {
                    listener.onResponse(null, new APError(APErrorCode.GENERAL_ERROR, "上传失败！"));
                }
            }
        });
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
