package com.apppubs.asytask;

import java.lang.ref.WeakReference;

import android.os.Handler;

public class AsyTaskItem {

	private AsyTaskCallback mCallback;
	private Integer mTag;
	private Handler mHandler;
	private String[] mParams;
	private Object mParam;
	private WeakReference<AsyTaskExecutor> mExecuteReference;
	public AsyTaskItem(Handler handler,String[] params) {
		mHandler = handler;
		mParams = params;
	}
	

	public void setTaskTag(Integer tag) {
		this.mTag = tag;
	}
	
	public Integer getTag() {
		return this.mTag;
	}

	public void setExecutor(AsyTaskExecutor executor){
		mExecuteReference = new WeakReference<AsyTaskExecutor>(executor);
	}
	public void setCallback(AsyTaskCallback callback) {
		this.mCallback = callback;
	}

	public Runnable getTaskRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				
				Object result = null;
				try {
					result = mCallback.onExecute(mTag,mParams);
					
					mHandler.post(getOnDoneRunnalbe(result));
				} catch (Exception e) {
					e.printStackTrace();
					mHandler.post(getOnFailRunnable(e));
				}
				
				AsyTaskExecutor executor = mExecuteReference.get();
				if(executor!=null){
					executor.removeTaskItem(AsyTaskItem.this);
				}
			}
		};
	}

	private Runnable getOnDoneRunnalbe(final Object result) {
		return new Runnable() {

			@Override
			public void run() {
				mCallback.onTaskSuccess(mTag, result);
			}

		};
	}
	
	private Runnable getOnFailRunnable(final Exception e){
		return new Runnable() {

			@Override
			public void run() {
				mCallback.onTaskFail(mTag,e);
			}

		};
	}

}
