package com.apppubs.d20.util;


import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by zhangwen on 2017/12/14.
 */

public class LocationManager {

	public interface LocationListener {
		void onLocationChanded(AMapLocation location);
	}

	private static LocationManager sManager;

	private LocationListener mListener;

	//声明AMapLocationClient类对象
	public AMapLocationClient mLocationClient = null;

	private LocationManager(Context cotntext) {
		//初始化定位
		mLocationClient = new AMapLocationClient(cotntext);
		mLocationClient.setLocationListener(new AMapLocationListener() {
			@Override
			public void onLocationChanged(AMapLocation aMapLocation) {
				if (mListener!=null){
					mListener.onLocationChanded(aMapLocation);
				}
			}
		});
	}

	public static LocationManager getInstance(Context context) {

		if (sManager == null) {
			synchronized (LocationManager.class) {
				if (sManager == null) {
					sManager = new LocationManager(context);
				}
			}
		}
		return sManager;
	}

	public void requestLocation() {
		//声明AMapLocationClientOption对象
		AMapLocationClientOption mLocationOption = null;
		//初始化AMapLocationClientOption对象
		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		//设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
		//获取一次定位结果：
		//该方法默认为false。
		mLocationOption.setOnceLocation(true);

		//获取最近3s内精度最高的一次定位结果：
		//设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
		mLocationOption.setOnceLocationLatest(true);
		//单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
		mLocationOption.setHttpTimeOut(80000);
		//关闭缓存机制
		mLocationOption.setLocationCacheEnable(false);
		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		//启动定位
		mLocationClient.startLocation();
	}

	public void setListener(LocationListener listener){
		mListener = listener;
	}

}
