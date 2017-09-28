package com.apppubs.d20.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.d20.bean.NewsInfo;
import com.apppubs.d20.R;
import com.apppubs.d20.bean.Collection;
import com.orm.SugarRecord;

public class Utils {

	public static int parseColor(String colorStr){
		try {
			return Color.parseColor(colorStr);
		}catch (IllegalArgumentException e){
			e.printStackTrace();
		}
		return 0x000000;
	}

	private static Toast curToast;

	/**
	 *
	 * @param context
	 * @param info
	 * @param millisecond
	 *            显示的毫秒数
	 */
	@SuppressLint("NewApi")
	public static void showToast(Context context, String info, int millisecond) {
		if (curToast != null)
			curToast.cancel();
		Toast toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_LONG);
		curToast = toast;
		toast.setGravity(Gravity.CENTER, 0, 0);
		TextView tv = new TextView(context);
		tv.setTextSize(17);
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		tv.setTextColor(context.getResources().getColor(android.R.color.white));
		tv.setLayoutParams(lp1);
		tv.setPadding(28, 28, 28, 28);
		if (Build.VERSION.SDK_INT >= 16) {
			// 使用api11 新加 api
			tv.setBackground(context.getResources().getDrawable(R.drawable.toastbg));
		}

		tv.setGravity(Gravity.CENTER);
		tv.setText(info);
		toast.setView(tv);
		toast.show();

		// 减短时间
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				curToast.cancel();
			}
		}, millisecond);
	}

	/**
	 * 由dp转换为px
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 由px转换为dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获得当前版本号
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getVersionCode(Context mContext) {
		PackageInfo pInfo = null;
		try {
			pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return pInfo.versionCode;
	}

	/**
	 * 获取操作系统版本
	 * 
	 * @return android.os.Build.VERSION.RELEASE
	 */
	public static String getAndroidSDKVersion() {
		String version = "";
		version = android.os.Build.VERSION.RELEASE;
		return version;

	}


	/**
	 * 获得当前版本名字
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getVersionName(Context mContext) {
		PackageInfo pInfo = null;
		try {
			pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return pInfo.versionName;
	}

	/**
	 * 关闭软键盘
	 */
	public static void colseInput(Activity context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
	}

	public static final String RESPONSE_METHOD = "method";
	public static final String RESPONSE_CONTENT = "content";
	public static final String RESPONSE_ERRCODE = "errcode";
	protected static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
	public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
	public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
	public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
	protected static final String EXTRA_ACCESS_TOKEN = "access_token";
	public static final String EXTRA_MESSAGE = "message";

	public static String logStringCache = "";

	// 获取ApiKey
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return apiKey;
	}

	public static boolean getBooleanMetaValue(Context context,String metaKey){
		boolean value = false;

		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			value = ai.metaData.getBoolean(metaKey);
		} catch (Exception e) {
			Log.d("mportal", "Couldn't find config value: " + metaKey);
		}

		return value;
	}

	public static Integer getIntegerMetaData(Context context, String name) {
		Integer value = null;

		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			value = ai.metaData.getInt(name);
		} catch (Exception e) {
			Log.d("mportal", "Couldn't find config value: " + name);
		}

		return value;
	}

	// 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false
	public static boolean hasBind(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String flag = sp.getString("bind_flag", "");
		if ("ok".equalsIgnoreCase(flag)) {
			return true;
		}
		return false;
	}

	public static void setBind(Context context, boolean flag) {
		String flagStr = "not";
		if (flag) {
			flagStr = "ok";
		}
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("bind_flag", flagStr);
		editor.commit();
	}

	public static List<String> getTagsList(String originalText) {
		if (originalText == null || originalText.equals("")) {
			return null;
		}
		List<String> tags = new ArrayList<String>();
		int indexOfComma = originalText.indexOf(',');
		String tag;
		while (indexOfComma != -1) {
			tag = originalText.substring(0, indexOfComma);
			tags.add(tag);

			originalText = originalText.substring(indexOfComma + 1);
			indexOfComma = originalText.indexOf(',');
		}

		tags.add(originalText);
		return tags;
	}

	public static String getLogText(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString("log_text", "");
	}

	public static void setLogText(Context context, String text) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString("log_text", text);
		editor.commit();
	}

	public static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
		final Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
		replaceFont(staticTypefaceFieldName, regular);
	}

	private static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
		try {
			final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
			staticField.setAccessible(true);
			staticField.set(null, newTypeface);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	// 收藏操作
	public static void toggleCollect(int type, Context context, boolean isCollected, String mInfoId, String title, String summy,
			ImageView mSaveImagview) {
		/**
		 * // * 收藏开关 //
		 */
		if (isCollected) {
			Log.v("NEWSINFO", "收藏");
			Utils.showToast(context, "收藏成功", 1000);
			SugarRecord.updateById(NewsInfo.class, mInfoId, "IS_COLLECTED", NewsInfo.COLLECTED + "");
			Collection c = new Collection();
			c.setAddTime(new Date());
			c.setInfoId(mInfoId);
			c.setTitle(title);
			c.setContentAbs(summy);
			c.setType(type);
			c.save();
			mSaveImagview.setImageResource(R.drawable.menubar_favorite_h);
		} else {
			Utils.showToast(context, "取消收藏", 1000);
			SugarRecord.updateById(NewsInfo.class, mInfoId, "IS_COLLECTED", NewsInfo.UNCOLLECTED + "");
			Collection c = new Collection();
			c.setAddTime(new Date());
			c.setInfoId(mInfoId);
			c.setTitle(title);
			c.setContentAbs(summy);
			c.setType(type);
			c.save();
			mSaveImagview.setImageResource(R.drawable.menubar_favorite);
			SugarRecord.deleteAll(Collection.class, "INFO_ID=?", mInfoId);
		}
	}
	
	/**
	 * 设备是否是pad
	 * @param context
	 * @return
	 */
	public static boolean isPad(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		//某些手机如酷派，xdip和ydip会为160（理论应该不是160）
//		double screenInchSize = Math.sqrt(Math.pow(dm.widthPixels/dm.xdpi, 2)+Math.pow(dm.heightPixels/dm.ydpi,2));
		double screenInchSize = Math.sqrt(Math.pow(dm.widthPixels, 2)+Math.pow(dm.heightPixels,2))/(160*dm.density);
		if(screenInchSize>6.0){
			return true;
		}
		return false;
	}
	/**
	 * 屏幕是否为横向
	 * @return
	 */
	public static boolean isScreenHorizontal(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		if(dm.widthPixels>dm.heightPixels){
			return true;
		}
		return false;
	}
	
}
