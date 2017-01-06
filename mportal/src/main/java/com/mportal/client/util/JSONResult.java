package com.mportal.client.util;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 用于从服务器返回的数据中提取信息
 *
 */
public class JSONResult<T> {
	
	public static final int RESULT_CODE_SUCCESS = 1;
	public static final int RESULT_CODE_FAIL = -1;
	
	
	public int resultCode;
	public String reason;
	public String result;
	public Date responseTime;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static JSONResult compile(String jsonStr){
		JSONResult jsonResult = new JSONResult();
		JSONObject jo = null;
		try {
			jo = new JSONObject(jsonStr);
			jsonResult.resultCode = jo.getInt("resultcode");
			jsonResult.reason = jo.getString("resultreason");
			jsonResult.result = jo.getString("resultinfo");
			
			if(jo.has("responsetime")&&!TextUtils.isEmpty(jo.getString("responsetime"))){
				try {
					jsonResult.responseTime = sdf.parse(jo.getString("responsetime"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else{
				jsonResult.responseTime = new Date();
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	public HashMap<String,String> getResultMap() throws JSONException{
		JSONObject jsonObject = new JSONObject(result);
		Iterator<String> iterator = jsonObject.keys();
		HashMap<String,String> map = new HashMap<String, String>();
		while(iterator.hasNext()){
			String key = iterator.next();
			map.put(key, jsonObject.getString(key));
		}
		return map;
	}

	public T getResultObject(Class<T> clazz){
		return GsonUtils.getGson().fromJson(result,clazz);
	}

}
