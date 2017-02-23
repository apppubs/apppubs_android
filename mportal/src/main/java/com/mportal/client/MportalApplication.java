package com.mportal.client;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mportal.client.activity.StartUpActivity;
import com.mportal.client.bean.App;
import com.mportal.client.bean.Settings;
import com.mportal.client.bean.User;
import com.mportal.client.constant.URLs;
import com.mportal.client.util.LogM;
import com.mportal.client.util.MathUtils;
import com.mportal.client.util.Utils;
import com.mportal.client.widget.ConfirmDialog;
import com.mportal.client.widget.ConfirmDialog.ConfirmListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orm.SugarContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import io.rong.imkit.RongIM;

public class MportalApplication extends Application {

	public static final String MSG_DELETED_CHAT_GROUP_MAP = "deleted_chat_group_map";
	private static final String CONFIG_DIRECTORY = "static_objs";

	/**
	 * 系统设置
	 */
	public static Settings systemSettings;
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
		initDefaultExceptionHandler();
		// 初始化设置
		initSystemState();
		initImageLoader();

		// 初始化SugarORM
		SugarContext.init(this);


		//初始化融云
		RongIM.init(this);

		WindowManager manager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		windowWidth = dm.widthPixels;
		windowHeight = dm.heightPixels;
		sContext = getApplicationContext();
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
	/**
	 * 提交并刷新内存中的设置信息
	 *
	 * @param settings
	 * @param context
	 */
	public static void commitAndRefreshSystemSettings(Settings settings, Context context) {

		Log.v("MportalApplication", "commitAndRefreshSystemSettings");
		MportalApplication.systemSettings = settings;

		File file = new File(context.getDir(CONFIG_DIRECTORY, Context.MODE_PRIVATE), "settings.cfg");
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(settings);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 持久化对象
	 *
	 * @param obj
	 * @param fileName
	 */

	public static void writeObj(Context context, Object obj, String fileName) {

		File file = new File(context.getDir(CONFIG_DIRECTORY, Context.MODE_PRIVATE), fileName);

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
				if (oos != null)
					oos.close();
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

		try {
			File file = new File(context.getDir(CONFIG_DIRECTORY, Context.MODE_PRIVATE), fileName);
			if (!file.exists())
				return null;
			ois = new ObjectInputStream(new FileInputStream(file));
			return ois.readObject();
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

		return null;

	}

	ArrayList<Activity> list = new ArrayList<Activity>();

	public void initDefaultExceptionHandler() {
		// 设置该CrashHandler为程序的默认处理器
//		UnCeHandler catchExcep = new UnCeHandler(this);
//		Thread.setDefaultUncaughtExceptionHandler(catchExcep);
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
	
	public void showChangeDialog(final Context context, final String  ip, final String code) {
		new ConfirmDialog(context, new ConfirmListener() {

			@Override
			public void onOkClick() {
				final Settings s = AppContext.getInstance(MportalApplication.this).getSettings();

				LogM.log(this.getClass(), ip + ":" + code);
				String verifyURL = ip+URLs.URL_APP_BASIC_INFO+"&appcode="+code;
				StringRequest request = new StringRequest(verifyURL, new Listener<String>() {

					@Override
					public void onResponse(String str) {
						if(!TextUtils.isEmpty(str)&&!str.equals("null")){
							s.setBaseURL(ip);
							s.setAppCode(code);
							MportalApplication.this.commitAndRefreshSystemSettings(s,context);
							User user = new User();
							AppContext.getInstance(MportalApplication.this).setCurrentUser(user);
							App app = AppContext.getInstance(context).getApp();
							app.setStartupTimes(0);
							AppContext.getInstance(context).setApp(app);
							AppManager.getInstant(context).restart();

						}else{
							Toast.makeText(MportalApplication.this, "ip地址错误或者客户号不存在",Toast.LENGTH_LONG).show();

						}
					}

				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(MportalApplication.this, "ip地址错误或者客户号不存在",Toast.LENGTH_LONG).show();
					}

				});
				Volley.newRequestQueue(MportalApplication.this).add(request);

			}

			@Override
			public void onCancelClick() {
			}
		}, "确定修改？修改会抹除当前数据然后自动启动", "取消", "确定").show();
	}

}
