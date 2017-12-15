package com.apppubs.d20.util;


import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by zhangwen on 2017/12/14.
 */

public class LocationManager {

	private static LocationManager sManager;

	//声明AMapLocationClient类对象
	public AMapLocationClient mLocationClient = null;
	//声明定位回调监听器
	public AMapLocationListener mLocationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation aMapLocation) {
			System.out.println("定位变化:"+aMapLocation);
		}
	};

	private LocationManager(Context cotntext) {
		//初始化定位
		mLocationClient =new AMapLocationClient(cotntext);
		//设置定位回调监听
		mLocationClient.setLocationListener(mLocationListener);
	}

	public LocationManager getInstance(Context context) {

		if (sManager == null) {
			synchronized (LocationManager.class) {
				if (sManager == null) {
					sManager = new LocationManager(context);
				}
			}
		}
		return sManager;
	}


}
