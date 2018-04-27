package com.apppubs.model;

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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.alibaba.fastjson.JSONObject;
import com.apppubs.AppContext;
import com.apppubs.MportalApplication;
import com.apppubs.bean.http.IJsonResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.net.APHttpClient;
import com.apppubs.net.APNetException;
import com.apppubs.net.IHttpClient;
import com.apppubs.net.IRequestListener;
import com.apppubs.util.Utils;

/**
 * 
 * 提供基础的线程相关功能
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年1月13日 by zhangwen create
 *
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

    public static final ExecutorService SERIAL_EXECUTOR =  Executors.newCachedThreadPool();
    public static final InternalHandler sHandler = new InternalHandler();
    public static volatile ExecutorService sDefaultExecutor = SERIAL_EXECUTOR;

    public BaseBiz(Context context) {
        mContext = context;
        mAppContext = AppContext.getInstance(context);
        mHttpClient = new APHttpClient();
    }


    public enum Status {
        /**
         * Indicates that the task has not been executed yet.
         */
        PENDING,
        /**
         * Indicates that the task is running.
         */
        RUNNING,
        /**
         * Indicates that {@link AsyncTask#onPostExecute} has finished.
         */
        FINISHED,
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
		private APResultCallback<T> mCallback;
		public  OnExceptionRun(APResultCallback<T> callback){
			mCallback = callback;
		}
		public void run() {
			mCallback.onException(0);
		}
	}
	
	protected class OnDoneRun<T> implements Runnable{
		private APResultCallback<T> mCallback;
		private T mResult;
		public OnDoneRun(APResultCallback<T> callback, T obj){
			mCallback = callback;
			mResult = obj;
		}
		@Override
		public void run() {
			mCallback.onDone(mResult);
		}
		
	}

    public Future<?> post(Runnable runnable){
        return sDefaultExecutor.submit(runnable);
    }

    protected <T> void onDone(APResultCallback<T> callback, T obj){
        sHandler.post(new OnDoneRun<T>(callback,obj));
    }

    protected <T> void onException(APResultCallback<T> callback){
        sHandler.post(new OnExceptionRun<T>(callback));
    }

    private Map<String, String> getCommonHeader() {
        String orientationFlag = Utils.isScreenHorizontal(mContext) ? "1" : "0";
        String deviceFlag = Utils.isPad(mContext) ? "4" : "3";
        String screenDimenFlag = null;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenDimenFlag = dm.widthPixels + "X" + dm.heightPixels;
        return null;
    }

    public <T extends IJsonResult> void asyncPOST(final String url, final Map<String, String>
            params, final Class<T>
                                                          clazz, final IRQListener listener) {
        mHttpClient.asyncPOST(url, params, new IRequestListener() {

            @Override
            public void onResponse(String json, APError e) {
                if (e == null) {
                    listener.onResponse(null, e);
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

    public <T extends IJsonResult> T syncPOST(String url, Map<String, String> params,
                                              Class<T> clazz) throws APNetException {
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

interface IRQListener<T extends IJsonResult> {
    void onResponse(T jr, APError error);
}