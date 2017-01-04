package com.mportal.client.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.mportal.client.R;
/**
 * 处理天气的实用类
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年3月2日 by zhangwen create
 *
 */
public class WeatherUtils {
	
	
	public static String getContent(String url) {
		HttpClient hc = new DefaultHttpClient();
		HttpGet hg = new HttpGet(url);
		try {
			HttpResponse hr = hc.execute(hg);
			String content = EntityUtils.toString(hr.getEntity(), "UTF-8");
			return content;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}



	public static int solvedWeather(String str) {
		String info = str;
		if (info.equals("暴雪") || info.startsWith("暴雪")) {
			return R.drawable.biz_pc_plugin_weather_baoxue;
		} else if (info.equals("暴雨") || info.startsWith("暴雨")) {
			return R.drawable.biz_pc_plugin_weather_baoyu;
		} else if (info.equals("大雪") || info.startsWith("大雪")) {
			return R.drawable.biz_pc_plugin_weather_daxue;
		} else if (info.equals("大雨") || info.startsWith("大雨")) {
			return R.drawable.biz_pc_plugin_weather_dayu;
		} else if (info.equals("多云") || info.startsWith("多云")) {
			return R.drawable.biz_pc_plugin_weather_duoyun;
		} else if (info.equals("浮尘") || info.startsWith("浮尘")) {
			return R.drawable.biz_pc_plugin_weather_fuchen;
		} else if (info.equals("雷阵雨") || info.startsWith("雷阵雨")) {
			return R.drawable.biz_pc_plugin_weather_leizhenyu;
		} else if (info.equals("雷阵雨冰雹") || info.startsWith("雷阵雨冰雹")) {
			return R.drawable.biz_pc_plugin_weather_leizhenyubingbao;
		} else if (info.equals("晴") || info.startsWith("晴")) {
			return R.drawable.biz_pc_plugin_weather_qing;
		} else if (info.equals("沙尘暴") || info.startsWith("沙尘暴")) {
			return R.drawable.biz_pc_plugin_weather_shachenbao;
		} else if (info.equals("雾") || info.startsWith("雾")) {
			return R.drawable.biz_pc_plugin_weather_wu;
		} else if (info.equals("小雪") || info.startsWith("小雪")) {
			return R.drawable.biz_pc_plugin_weather_xiaoxue;
		} else if (info.equals("小雨") || info.startsWith("小雨")) {
			return R.drawable.biz_pc_plugin_weather_xiaoyu;
		} else if (info.equals("扬沙") || info.startsWith("扬沙")) {
			return R.drawable.biz_pc_plugin_weather_yangsha;
		} else if (info.equals("阴") || info.startsWith("阴")) {
			return R.drawable.biz_pc_plugin_weather_yin;
		} else if (info.equals("雨夹雪") || info.startsWith("雨夹雪")) {
			return R.drawable.biz_pc_plugin_weather_yujiaxue;
		} else if (info.equals("阵雪") || info.startsWith("阵雪")) {
			return R.drawable.biz_pc_plugin_weather_zhenxue;
		} else if (info.equals("阵雨") || info.startsWith("阵雨")) {
			return R.drawable.biz_pc_plugin_weather_zhenyu;
		} else if (info.equals("中雪") || info.startsWith("中雪")) {
			return R.drawable.biz_pc_plugin_weather_zhongxue;
		} else if (info.equals("中雨") || info.startsWith("中雨")) {
			return R.drawable.biz_pc_plugin_weather_zhongyu;
		} else {
			return R.drawable.biz_pc_plugin_weather_zhongyu;
		}
	}

	public static int solvedAnimWeather(String info) {

		if (info.equals("暴雪") || info.startsWith("暴雪")) {
			return R.drawable.biz_plugin_weather_baoxue;
		} else if (info.equals("暴雨") || info.startsWith("暴雨")) {
			return R.drawable.biz_plugin_weather_baoyu;
		} else if (info.equals("大雪") || info.startsWith("大雪")) {
			return R.drawable.biz_plugin_weather_daxue;
		} else if (info.equals("大雨") || info.startsWith("大雨")) {
			return R.drawable.biz_plugin_weather_dayu;
		} else if (info.equals("多云") || info.startsWith("多云")) {
			return R.drawable.biz_plugin_weather_duoyun;
		} else if (info.equals("浮尘") || info.startsWith("浮尘")) {
			return R.drawable.biz_plugin_weather_fuchen;
		} else if (info.equals("雷阵雨") || info.startsWith("雷阵雨")) {
			return R.drawable.biz_plugin_weather_leizhenyu;
		} else if (info.equals("雷阵雨冰雹") || info.startsWith("雷阵雨冰雹")) {
			return R.drawable.biz_plugin_weather_leizhenyubingbao;
		} else if (info.equals("晴") || info.startsWith("晴")) {
			return R.drawable.biz_plugin_weather_qing;
		} else if (info.equals("沙尘暴") || info.startsWith("沙尘暴")) {
			return R.drawable.biz_plugin_weather_shachenbao;
		} else if (info.equals("雾") || info.startsWith("雾")) {
			return R.drawable.biz_plugin_weather_wu;
		} else if (info.equals("霾") || info.startsWith("霾")) {
			return R.drawable.biz_plugin_weather_wu;
		} else if (info.equals("小雪") || info.startsWith("小雪")) {
			return R.drawable.biz_plugin_weather_xiaoxue;
		} else if (info.equals("小雨") || info.startsWith("小雨")) {
			return R.drawable.biz_plugin_weather_xiaoyu;
		} else if (info.equals("扬沙") || info.startsWith("扬沙")) {
			return R.drawable.biz_plugin_weather_yangsha;
		} else if (info.equals("阴") || info.startsWith("阴")) {
			return R.drawable.biz_plugin_weather_yin;
		} else if (info.equals("雨夹雪") || info.startsWith("雨夹雪")) {
			return R.drawable.biz_plugin_weather_yujiaxue;
		} else if (info.equals("阵雪") || info.startsWith("阵雪")) {
			return R.drawable.biz_plugin_weather_zhenxue;
		} else if (info.equals("阵雨") || info.startsWith("阵雨")) {
			return R.drawable.biz_plugin_weather_zhenyu;
		} else if (info.equals("中雪") || info.startsWith("中雪")) {
			return R.drawable.biz_plugin_weather_zhongxue;
		} else if (info.equals("中雨") || info.startsWith("中雨")) {
			return R.drawable.biz_plugin_weather_zhongyu;
		} else {
			return R.drawable.biz_plugin_weather_qing;
		}
	}
}
