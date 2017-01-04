package com.mportal.client.asytask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;

public class AsyTaskExecutor {

	private final int MAX_THREAD_NUM = 3;

	private static AsyTaskExecutor sAsyTaskExecutor;
	
	private List<AsyTaskItem> mTaskList;
	private ExecutorService mExecutorService;
	private Handler mHandler;

	private AsyTaskExecutor() {
		mTaskList = new ArrayList<AsyTaskItem>();
		mExecutorService = Executors.newFixedThreadPool(MAX_THREAD_NUM);
		mHandler = new Handler(Looper.getMainLooper());
	}

	public static AsyTaskExecutor getInstance() {
		if (sAsyTaskExecutor==null) {
			synchronized (AsyTaskExecutor.class) {
				if (sAsyTaskExecutor == null) {
					sAsyTaskExecutor = new AsyTaskExecutor();
				}
			}
		}
		return sAsyTaskExecutor;
	}

	public void startTask(Integer taskTag, AsyTaskCallback callback, String[] params) {
		AsyTaskItem item = new AsyTaskItem(mHandler, params);
		item.setTaskTag(taskTag);
		item.setCallback(callback);
		item.setExecutor(this);
		mTaskList.add(item);
		mExecutorService.execute(item.getTaskRunnable());
	}
	

	public  void stopTask(Integer taskTag) {
		for (AsyTaskItem task : mTaskList) {
			if (task.getTag() == taskTag) {
				mTaskList.remove(task);
			}

		}
	}

	void removeTaskItem(AsyTaskItem item) {
		mTaskList.remove(item);
	}

}
