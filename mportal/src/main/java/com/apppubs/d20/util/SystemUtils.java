package com.apppubs.d20.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.d20.R;

public class SystemUtils {
	private static long lastClickTime;

	// 手机能否联网那个
	public static boolean canConnectNet(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo infos[] = cm.getAllNetworkInfo();
		int flag = 0;
		for (int i = 0; i < infos.length; i++) {
			if (infos[i].getState() == NetworkInfo.State.CONNECTED) {
				flag++;
			}
		}
		boolean result = flag != 0;
		if (!result)
			showToast(context, "Network Errors , Please Check Your Neetwork !");
		return result;
	}

	// 获取WiFi的IP地址
	public static String getWIFiAddress(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		String ip = "";
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		ip = intToIp(ipAddress);
		return ip;
	}

	private static String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}

	// 获取GPRS手机的IP
	public static String getLocalIpAddress() {
		String ip = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ip = inetAddress.getHostAddress().toString();
						return ip;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return ip;
	}

	// 弹toast
	private static Toast curToast;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi") public static void showToast(Context context, String info) {
		if (curToast != null)
			curToast.cancel();
		Toast toast = new Toast(context);
		curToast = toast;
		toast.setGravity(Gravity.BOTTOM, 0, 20);
		TextView tv = new TextView(context);
		tv.setTextSize(14);
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		tv.setTextColor(context.getResources().getColor(android.R.color.white));
		tv.setLayoutParams(lp1);
		tv.setPadding(25, 10, 25, 10);
		if (Build.VERSION.SDK_INT >= 16) {
			// 使用api11 新加 api
			tv.setBackground(context.getResources().getDrawable(R.drawable.toastbg));
		}else{
			tv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.toastbg));
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
		}, 500);
	}



	public static String md5(String key) {
		try {
			char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			byte[] buf = key.getBytes();
			md.update(buf, 0, buf.length);
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder(32);
			for (byte b : bytes) {
				sb.append(hex[((b >> 4) & 0xF)]).append(hex[((b >> 0) & 0xF)]);
			}
			key = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return key;
	}


	// 用户未等待响应一直点击操作
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}



}
