package com.apppubs.model;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.alibaba.fastjson.JSONObject;
import com.apppubs.AppContext;
import com.apppubs.AppManager;
import com.apppubs.bean.http.IJsonResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.constant.Constants;
import com.apppubs.net.APHttpClient;
import com.apppubs.net.APNetException;
import com.apppubs.net.IHttpClient;
import com.apppubs.net.IRequestListener;
import com.apppubs.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 提供基础的线程相关功能
 * Copyright (c) heaven Inc.
 * <p>
 * Original Author: zhangwen
 * <p>
 * ChangeLog:
 * 2015年1月13日 by zhangwen create
 */
public abstract class BaseBiz {

    private static final String LOG_TAG = "BaseBussiness";

    protected AppContext mAppContext;
    protected Context mContext;

    private IHttpClient mHttpClient;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "BaseBussiness #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);
    //线程池类
    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    public static final ExecutorService SERIAL_EXECUTOR = Executors.newCachedThreadPool();
    public static final InternalHandler sHandler = new InternalHandler();
    public static volatile ExecutorService sDefaultExecutor = SERIAL_EXECUTOR;

    public BaseBiz(Context context) {
        mContext = context;
        mAppContext = AppContext.getInstance(context);
        mHttpClient = new APHttpClient();
    }

    protected static class InternalHandler extends Handler {
        //创建handler绑定主线程looper
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
        @Override
        public void handleMessage(Message msg) {
        }
    }


    protected class OnExceptionRun<T> implements Runnable {
        private IAPCallback<T> mCallback;

        public OnExceptionRun(IAPCallback<T> callback) {
            mCallback = callback;
        }

        public void run() {
            mCallback.onException(new APError(APErrorCode.GENERAL_ERROR, "系统异常！"));
        }
    }

    protected class OnDoneRun<T> implements Runnable {
        private IAPCallback<T> mCallback;
        private T mResult;

        public OnDoneRun(IAPCallback<T> callback, T obj) {
            mCallback = callback;
            mResult = obj;
        }

        @Override
        public void run() {
            mCallback.onDone(mResult);
        }

    }

    public Future<?> post(Runnable runnable) {
        return sDefaultExecutor.submit(runnable);
    }

    protected <T> void onDone(IAPCallback<T> callback, T obj) {
        sHandler.post(new OnDoneRun<T>(callback, obj));
    }

    protected <T> void onException(IAPCallback<T> callback) {
        sHandler.post(new OnExceptionRun<T>(callback));
    }

    public Map<String, String> getCommonHeader() {

        String screenDimenFlag = getScreenStr();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("version", mAppContext.getVersionName());
        headers.put("versionCode", mAppContext.getVersionCode() + "");
        headers.put("os", Build.VERSION.RELEASE);
        headers.put("from", "android");
        headers.put("screen", screenDimenFlag);
        headers.put("model", Build.DEVICE);
        headers.put("channel", "default");
        headers.put("net", "unknown");
        headers.put("deviceId", AppManager.getInstance(mContext).getMachineId());
        headers.put("nonce", "");
        headers.put("timestamp", "");
        headers.put("sign", "");
        headers.put("appId", mAppContext.getLocalAppId());
        headers.put("orgCode", mAppContext.getApp().getOrgCode());
        headers.put("apiVersion", "1.0.0");

        return headers;
    }

    @NonNull
    private String getScreenStr() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels + "*" + dm.heightPixels;
    }

    protected <T extends IJsonResult> void asyncPOST(String apiName, Map<String, String>
            params, final Class<T> clazz, final IRQListener listener) {
        asyncPOST(apiName, params, false, clazz, listener);
    }

    protected <T extends IJsonResult> void asyncPOST(String apiName, Map<String, String>
            params, boolean needUser, final Class<T> clazz, final IRQListener listener) {
        String entryURL = getEntryURL();
        params = getTrueParams(apiName, params);
        if (needUser) {
            params.putAll(getUserParams());
        }
        mHttpClient.asyncPOST(entryURL, getCommonHeader(), params, new IRequestListener() {

            @Override
            public void onResponse(String json, APError e) {
                if (e != null) {
                    listener.onResponse(null, e);
                    return;
                }
                try {
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
                }catch (Exception ex){
                    ex.printStackTrace();
                    listener.onResponse(null, new APError(APErrorCode.JSON_PARSE_ERROR, "json格式错误！"));
                }
            }
        });
    }

    @NonNull
    private Map<String, String> getTrueParams(String apiName, Map<String, String> params) {
        if (null == params) {
            params = new HashMap<String, String>();
        }
        params.put("apiName", apiName);
        return params;
    }

    protected void asyncPOST(String apiName, Map<String, String> params, @NonNull final
    IRQStringListener listener) {
        asyncPOST(apiName, params, false, listener);
    }

    protected void asyncPOST(String apiName, Map<String, String> params, boolean needUser,
                             @NonNull final IRQStringListener listener) {
        String entryURL = getEntryURL();
        params = getTrueParams(apiName, params);
        if (needUser) {
            params.putAll(getUserParams());
        }
        mHttpClient.asyncPOST(entryURL, getCommonHeader(), params, new IRequestListener() {

            @Override
            public void onResponse(String json, APError e) {
                if (e != null) {
                    listener.onResponse(null, e);
                    return;
                }
                JSONObject jo = JSONObject.parseObject(json);
                Integer code = jo.getInteger("code");
                String msg = jo.getString("msg");
                if (code == APErrorCode.SUCCESS.getCode()) {
                    String resultStr = jo.getString("result");
                    listener.onResponse(resultStr, null);
                } else {
                    APError err = new APError(code, msg);
                    listener.onResponse(null, err);
                }
            }
        });
    }

    private String getEntryURL() {
        return mAppContext.getLocalBaseURL() + Constants.API_ENTRY;
    }

    protected <T extends IJsonResult> T syncPOST(String apiName, @NonNull Map<String, String>
            params, Class<T> clazz) throws APNetException {
        return syncPOST(apiName, params, false, clazz);
    }

    protected <T extends IJsonResult> T syncPOST(String apiName, @NonNull Map<String, String>
            params, boolean needUser, Class<T> clazz) throws APNetException {
        String entryURL = getEntryURL();
        params = getTrueParams(apiName, params);
        if (needUser) {
            params.putAll(getUserParams());
        }
        String json = mHttpClient.syncPOST(entryURL, getCommonHeader(), params);
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

    public interface IRQListener<T extends IJsonResult> {
        void onResponse(T jr, APError error);
    }

    public interface IRQStringListener {
        void onResponse(String result, APError error);
    }

    private Map<String, String> getUserParams() {
        if (Utils.isEmpty(mAppContext.getCurrentUser()) || Utils.isEmpty(mAppContext
                .getCurrentUser().getUserId())) {
            return new HashMap<>();
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", mAppContext.getCurrentUser().getUsername());
        params.put("token", mAppContext.getCurrentUser().getToken());
        return params;
    }
}
