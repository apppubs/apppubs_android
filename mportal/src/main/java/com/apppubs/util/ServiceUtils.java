package com.apppubs.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {
	// 是否有服务在运行
		public static boolean isServiceRunning(Context mContext, String className) {

			boolean isRunning = false;
			ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);

			if (!(serviceList.size() > 0)) {
				return false;
			}

			for (int i = 0; i < serviceList.size(); i++) {
				if (serviceList.get(i).service.getClassName().equals(className) == true) {
					isRunning = true;
					break;
				}
			}
			return isRunning;
		}

		private boolean isMyServiceRunning(Context mContext) {
			ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if ("DownloadAppService".equals(service.service.getClassName())) {
					return true;
				}
			}
			return false;
		}
}
