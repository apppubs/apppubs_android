package com.apppubs.d20;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.apppubs.d20.activity.MessageEvent;
import com.apppubs.d20.activity.UserInfoActivity;
import com.apppubs.d20.model.BussinessCallbackCommon;
import com.apppubs.d20.model.SystemBussiness;
import com.apppubs.d20.constant.Constants;
import com.apppubs.d20.util.MathUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orm.SugarContext;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class MportalApplication extends MultiDexApplication {

	public static final String MSG_DELETED_CHAT_GROUP_MAP = "deleted_chat_group_map";
	private static final String CONFIG_DIRECTORY = "static_objs";

	/**
	 * 系统设置
	 */
//	public static Settings systemSettings;
	/**
	 * 系统状态
	 */
	public static SystemState systemState;


	// /** 获取可見区域高度 **/
	public static int windowWidth;
	public static int windowHeight;
	
	private static Context sContext;

	@Override
	public void onCreate() {

		super.onCreate();

		String processName = getProcessName(this, android.os.Process.myPid());
		if (processName != null) {
			boolean defaultProcess = processName.equals(Constants.REAL_PACKAGE_NAME);
			if (defaultProcess) {
				initAppForMainProcess();
			} else if (processName.contains(":ipc")) {
			}
		}
	}

	private void initAppForMainProcess() {
		initDefaultExceptionHandler();
		// 初始化设置
		initSystemState();
		initImageLoader();

		// 初始化SugarORM
		SugarContext.init(this);

		//初始化融云
		RongIM.init(this);
		RongIM.setConversationBehaviorListener(new MyConversationBehaviorListener());
		RongIM.getInstance().addUnReadMessageCountChangedObserver(new IUnReadMessageObserver() {
			@Override
			public void onCountChanged(int i) {
				AppContext.getInstance(getContext()).getApp().setmMessageUnreadNum(i);
			}
		}, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.PRIVATE);

		WindowManager manager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		windowWidth = dm.widthPixels;
		windowHeight = dm.heightPixels;
		sContext = getApplicationContext();


		SystemBussiness.getInstance(this).initSystem(new BussinessCallbackCommon<Object>() {
			@Override
			public void onDone(Object obj) {
				MessageEvent me = new MessageEvent();
				me.tag = Constants.MESSAGE_EVENT_INIT_APP;
				EventBus.getDefault().post(me);
			}

			@Override
			public void onException(int excepCode) {
				MessageEvent me = new MessageEvent();
				me.tag = Constants.MESSAGE_EVENT_INIT_APP;
				EventBus.getDefault().post(me);
			}
		});
	}


	private class MyConversationBehaviorListener implements RongIM.ConversationBehaviorListener{
		@Override
		public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
			UserInfoActivity.startActivity(context,userInfo.getUserId());
			return true;
		}

		@Override
		public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
			return false;
		}

		@Override
		public boolean onMessageClick(Context context, View view, Message message) {
			return false;
		}

		@Override
		public boolean onMessageLinkClick(Context context, String s) {
			return false;
		}

		@Override
		public boolean onMessageLongClick(Context context, View view, Message message) {
			return false;
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		// 终止sugerORM
		SugarContext.terminate();
	}

	/**
	 * 系统状态
	 * 
	 * Copyright (c) heaven Inc.
	 * 
	 * Original Author: zhangwen
	 * 
	 * ChangeLog: 2015年2月26日 by zhangwen create
	 * 
	 */
	public class SystemState {

		/**
		 * 网络状态
		 */
		private int networkState;

		public int getNetworkState() {
			return networkState;
		}

		public void setNetworkState(int networkState) {
			this.networkState = networkState;
		}

	}

	public static void refreshSystemState(SystemState st) {
		MportalApplication.systemState = st;
	}

	private void initImageLoader() {

		File stancePicFile = new File(getFilesDir().getAbsolutePath(), "stance_pic.png");
		Drawable draw = null;
		if (stancePicFile.exists()) {
			draw = Drawable.createFromPath(getFilesDir().getAbsolutePath() + "stance_pic.png");
		} else {
			draw = getResources().getDrawable(R.drawable.stance_gray);
		}
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(draw)
				.showImageForEmptyUri(draw).showImageOnFail(draw).cacheInMemory(true).cacheOnDisk(true)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
		// .memoryCacheExtraOptions(480, 800) // default = device screen
		// dimensions
		// .diskCacheExtraOptions(480, 800, null)
		// .taskExecutor(...)
		// .taskExecutorForCachedImages(...)
				.threadPoolSize(3) // default
				.threadPriority(Thread.NORM_PRIORITY - 2) // default
				// .tasksProcessingOrder(QueueProcessingType.FIFO) // default
				.denyCacheImageMultipleSizesInMemory()
				// .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
				// .memoryCacheSize(5 * 1024 * 1024)
				// .memoryCacheSizePercentage(13) // default
				// .diskCache(new UnlimitedDiskCache(cacheDir)) // default
				// .diskCacheSize(500 * 1024 * 1024)
				// .diskCacheFileCount(1000)
				// .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
				// // defaul
				// .diskCacheFileNameGenerator(new EReaderFileNameGenerator())
				// // defaul
				// .imageDownloader(new BaseImageDownloader(context)) // default
				// .imageDecoder(new BaseImageDecoder()) // default
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				// // default
				.defaultDisplayImageOptions(options) // default
				// .writeDebugLogs()
				.build();

		ImageLoader.getInstance().init(config);
	}

	/**
	 * 初始化系统状态
	 */
	private void initSystemState() {

		// 初始化网络状态
		MportalApplication.systemState = new SystemState();

		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null)
			MportalApplication.systemState.setNetworkState(activeNetInfo.getType());
	}

	/**
	 * 机器唯一标识
	 */
	public static String getMachineId() {
		return MathUtils.MD5("sdk=" + Build.VERSION.SDK_INT + "|" + "model=" + Build.MODEL + "|" + Build.SERIAL + "|"
				+ Build.DEVICE);

	}
//	/**
//	 * 提交并刷新内存中的设置信息
//	 *
//	 * @param settings
//	 * @param context
//	 */
//	public static void commitAndRefreshSystemSettings(Settings settings, Context context) {
//
//		Log.v("MportalApplication", "commitAndRefreshSystemSettings");
//		MportalApplication.systemSettings = settings;
//
//		File file = new File(context.getDir(CONFIG_DIRECTORY, Context.MODE_PRIVATE), "settings.cfg");
//		ObjectOutputStream oos = null;
//		try {
//			oos = new ObjectOutputStream(new FileOutputStream(file));
//			oos.writeObject(settings);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (oos != null)
//					oos.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	/**
	 * 持久化对象
	 *
	 * @param obj
	 * @param fileName
	 */

	public static void writeObj(Context context, Object obj, String fileName) {

		File dir = context.getDir(CONFIG_DIRECTORY, Context.MODE_PRIVATE);
		if (!dir.exists()){
			dir.mkdirs();
		}
		File file = new File(dir, fileName);

		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(obj);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null){
					oos.flush();
					oos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读取持久化对象
	 *
	 * @param fileName
	 * @return
	 */
	public static  Object readObj(Context context,String fileName) {
		ObjectInputStream ois = null;
		Object result = null;
		try {
			File file = new File(context.getDir(CONFIG_DIRECTORY, Context.MODE_PRIVATE), fileName);
			if (!file.exists())
				return null;
			ois = new ObjectInputStream(new FileInputStream(file));

			result = ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null)
					ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;

	}

	ArrayList<Activity> list = new ArrayList<Activity>();

	public void initDefaultExceptionHandler() {
		// 设置该CrashHandler为程序的默认处理器
//		UnCeHandler catchExcep = new UnsetDefaultUncaughtExceptionHandlerCeHandler(this);
//		Thread.(catchExcep);
	}

	/**
	 * Activity关闭时，删除Activity列表中的Activity对象
	 */
	public void removeActivity(Activity a) {
		list.remove(a);
	}

	/**
	 * 向Activity列表中添加Activity对象
	 */
	public void addActivity(Activity a) {
		list.add(a);
	}

	/**
	 * 关闭Activity列表中的所有Activity
	 */
	public void finishActivity() {
		for (Activity activity : list) {
			if (null != activity) {
				activity.finish();
			}
		}
		// 杀死该应用进程
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public static Context getContext(){
		return sContext;
	}

	/**
	 * @return null may be returned if the specified process not found
	 */
	public static String getProcessName(Context cxt, int pid) {
		ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
		if (runningApps == null) {
			return null;
		}
		for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
			if (procInfo.pid == pid) {
				return procInfo.processName;
			}
		}
		return null;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

}
