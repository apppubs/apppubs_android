package com.apppubs.d20.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

public class LocationUtils {
	private static String cityname = "";
	private static AMapLocation maMapLocation;
	private static LocationManagerProxy maMapLocManager;
	private static Handler mhandler;

	public LocationUtils(Handler handler, LocationManagerProxy aMapLocManager, AMapLocation aMapLocation) {
		this.maMapLocation = aMapLocation;
		this.maMapLocManager = aMapLocManager;
		this.mhandler = handler;
	}

	@SuppressWarnings("deprecation")
	public static String getlocatioccityname() {
		maMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, new AMapLocationListener() {

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location arg0) {
				// TODO Auto-generated method stub

			}

			/**
			 * 混合定位回调函数
			 */
			@Override
			public void onLocationChanged(AMapLocation location) {
				// TODO Auto-generated method stub
				if (location != null) {
					maMapLocation = location;// 判断超时机制
					Double geoLat = location.getLatitude();
					Double geoLng = location.getLongitude();
					String cityCode = "";
					String desc = "";
					Bundle locBundle = location.getExtras();
					if (locBundle != null) {
						cityCode = locBundle.getString("citycode");
						desc = locBundle.getString("desc");
					}
					String str = ("定位成功:(" + geoLng + "," + geoLat + ")" + "\n精    度    :" + location.getAccuracy() + "米"
							+ "\n定位方式:" + location.getProvider() + "\n定位时间:" + convertToTime(location.getTime()) + "\n城市编码:"
							+ cityCode + "\n位置描述:" + desc + "\n省:" + location.getProvince() + "\n市:" + location.getCity()
							+ "\n区(县):" + location.getDistrict() + "\n区域编码:" + location.getAdCode());
					cityname = location.getCity();
				}
			}
		});
		mhandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (maMapLocation == null) {

					stopLocation();// 销毁掉定位
				}
			}
		}, 12000);

		return cityname;
	}

	/**
	 * 销毁定位
	 */
	public static void stopLocation() {
		if (maMapLocManager != null) {
			maMapLocManager.removeUpdates(new AMapLocationListener() {

				@Override
				public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderEnabled(String arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderDisabled(String arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLocationChanged(Location arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLocationChanged(AMapLocation arg0) {
					// TODO Auto-generated method stub

				}
			});
			maMapLocManager.destory();
		}
		maMapLocManager = null;
	}

	public static void show(Context context, String info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	public static void show(Context context, int info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	public static String convertToTime(long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);
		return df.format(date);
	}

}