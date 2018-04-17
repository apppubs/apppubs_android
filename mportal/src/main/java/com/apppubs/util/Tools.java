package com.apppubs.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.apppubs.bean.SearchInfo;
import com.apppubs.bean.Weather;
import com.apppubs.constant.URLs;
import com.apppubs.bean.WeiboInfo;

public class Tools {
	private Context context;

	public Tools(Context context) {
		this.context = context;
	}


	/**
	 * 提交报料报料 http://www.sxxynews.com:8080/wmh360/epaper/json/ readernews.jsp?
	 * &userid=123&title=123&name=1233&
	 * content=123&contract=123&picurl=123&appcode=D01
	 * 
	 * @return
	 */
	public int submmitBaoliao(String userid, String title, String name, String content, String contract, String picurl,
			String appcode) {
		String requestUrl = String.format(URLs.URL_BAOLIAO,URLs.baseURL);
		Map<String, Object> requestParamsMap = new HashMap<String, Object>();
		requestParamsMap.put("userid", userid);
		requestParamsMap.put("title", title);
		requestParamsMap.put("name", name);
		requestParamsMap.put("content", content);
		requestParamsMap.put("contract", contract);
		requestParamsMap.put("picurl", picurl);
		requestParamsMap.put("appcode", appcode);
		requestParamsMap.put("clientkey","bb7c1386d85044ba7a7ae53f3362d634");
		String resurt = "";
		int back = 0;
		try {
			resurt = WebUtils.requestWithPost(requestUrl, requestParamsMap);
			JSONObject jo = new JSONObject(resurt);
			back = Integer.parseInt(jo.getString("result"));
			System.out.println("打印报料提交结果"+back);
			return back;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return back;
	}

	/**
	 * 提交评论 *http://202.85.221.113/wmh360/json/updatecomment.jsp?
	 * infoid=1226239645973833&imei=imeitest&content=contenttest&
	 * clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */

	public String submmitCommment(String infoid, String ip, String content, String clientkey,String userId,String trueName) {
		String requestUrl = URLs.URL_COMMENT_SUMMIT;
		Map<String, Object> requestParamsMap = new HashMap<String, Object>();
		requestParamsMap.put("infoid", infoid);
		requestParamsMap.put("ip", ip);
		requestParamsMap.put("content", content);
		requestParamsMap.put("clientkey", clientkey);
		requestParamsMap.put("userid", userId);
		requestParamsMap.put("imei", "imeitest");
		requestParamsMap.put("cnname", trueName);
		String result =  WebUtils.requestWithPost(requestUrl, requestParamsMap);
		String ok = "";
		try {
			JSONObject json = new JSONObject(result);
			ok = json.getString("result").trim();
		} catch (Exception e) {
			ok = "";
			e.printStackTrace();
		}
		return ok;

	}


	/**
	 * 获得微博列表
	 * http://124.205.71.106:8080/wmh360/json/getweiboinfo.jsp?appcode=D01
	 * &imei=imeitest
	 */
	public List<WeiboInfo> getWeiboinfos(String imei) {
		List<WeiboInfo> infos = new ArrayList<WeiboInfo>();
		String url = URLs.URL_WEIBO + "&imei=" + imei;
		try {
			String data = WebUtils.requestWithGet(url);
			try {
				JSONObject jo = new JSONObject(data);
				JSONArray array = jo.getJSONArray("weibo");
				for (int i = 0; i < array.length(); i++) {
					JSONObject jo1 = array.getJSONObject(i);
					WeiboInfo info = new WeiboInfo(jo1.getString("appname"), jo1.getString("appurl"));
					infos.add(info);
				}
				return infos;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return infos;
	}

	/**
	 * 搜索
	 * http://124.205.71.106:8080/wmh360/json/getsearchlist.jsp?webappcode=A09
	 * &keyword=1 &pno=1&pernum=10&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public List<SearchInfo> getSearchInfos(String webappcode, String keyword, int pno, int pernum, String clientkey) {
		String requestUrl = String.format(URLs.URL_SEARCH,URLs.baseURL);
		List<SearchInfo> infos = new ArrayList<SearchInfo>();
		Map<String, Object> requestParamsMap = new HashMap<String, Object>();
		requestParamsMap.put("webappcode", webappcode);
		requestParamsMap.put("keyword", keyword);
		requestParamsMap.put("pno", pno + "");
		requestParamsMap.put("pernum", pernum + "");
		requestParamsMap.put("clientkey", URLs.CLIENTKEY);
		String resurt = "";
		resurt = WebUtils.requestWithPost(requestUrl, requestParamsMap);
		System.out.println("查看搜索是否带有频道编码。。。。" + resurt);
		JSONObject jo;
		try {
			jo = new JSONObject(resurt);
			JSONArray array;
			array = jo.getJSONArray("info");
			for (int i = 0; i < array.length(); i++) {
				JSONObject jo1;
				jo1 = array.getJSONObject(i);
				SearchInfo info;
				info = new SearchInfo(jo1.getString("infoid"), jo1.getString("url"), jo1.getString("topic"),
						jo1.getString("pubtime"), jo1.getString("comment"), jo1.getString("summary"),
						jo1.getString("channelcode"));
				infos.add(info);
			}
			return infos;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return infos;

	}


	public static ArrayList<Weather> getWeatherList(Context context, String str) {
		ArrayList<Weather> list = new ArrayList<Weather>();
		try {
			// String
			// url=ProtocolInfo.PROTOCOL_HOST+"wmh360/json/getweather.jsp?cityname="+str;

			String url = "http://api.map.baidu.com/telematics/v3/weather?location=" + str
					+ "&output=json&ak=rnm8udmHdWaHFWZTO2tuTiG8";
			String content = WeatherUtils.getContent(url);
			if (content.indexOf("results") > 0) {
				JSONObject jo = new JSONObject(content);
				JSONArray array = jo.getJSONArray("results");
				for (int j = 0; j < array.length(); j++) {
					JSONObject jo1 = array.getJSONObject(j);
					String cityName = jo1.getString("currentCity");
					JSONArray array2 = jo1.getJSONArray("weather_data");
					for (int i = 0; i < array2.length(); i++) {
						Weather info = new Weather();
						JSONObject jo2 = array2.getJSONObject(i);
						info.setCityName(cityName);
						info.setData(jo2.getString("date"));
						info.setWeather(jo2.getString("weather"));
						info.setWind(jo2.getString("wind"));
						info.setTemp(jo2.getString("temperature"));
						list.add(info);
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
