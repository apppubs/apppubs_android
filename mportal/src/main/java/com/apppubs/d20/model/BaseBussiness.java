package com.apppubs.d20.model;

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

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.apppubs.d20.MportalApplication;

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
public abstract class BaseBussiness {

    private static final String LOG_TAG = "BaseBussiness";

    
    private MportalApplication mApp;
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
    
    public BaseBussiness(){
    }
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);
    //线程池类
    public static final Executor THREAD_POOL_EXECUTOR
    = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    public static final ExecutorService SERIAL_EXECUTOR =  Executors.newCachedThreadPool();
    public static final InternalHandler sHandler = new InternalHandler();
    public static volatile ExecutorService sDefaultExecutor = SERIAL_EXECUTOR;

    
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
   
}
