package com.mportal.client.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceUtils {

	private  Context mContext;
	private static SharedPreferenceUtils sPrefreenceUtils;
	
	private SharedPreferenceUtils(Context context){
		mContext = context;
	}
	
	public static SharedPreferenceUtils getInstance(Context context) {
		if(sPrefreenceUtils==null){
			sPrefreenceUtils = new SharedPreferenceUtils(context);
		}
		return sPrefreenceUtils;
	}
	
	private SharedPreferences getSharedPreference(String name) {
		SharedPreferences preference  = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
		return preference;
	}
	
	public void putBoolean(String name,String key,boolean value){
		SharedPreferences preference = getSharedPreference(name);
		Editor editor = preference.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getBoolean(String name,String key,boolean defaultValue){
		SharedPreferences sp = getSharedPreference(name);
		boolean result = sp.getBoolean(key, defaultValue);
		return result;
	}
	
	public void putString(String name,String key,String value){
		SharedPreferences sp = getSharedPreference(name);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public String getString(String name,String key,String defaltValue){
		SharedPreferences sp = getSharedPreference(name);
		return sp.getString(key, defaltValue);
	}
	
	public void putInt(String name,String key,int value){
		SharedPreferences preference = getSharedPreference(name);
		Editor editor = preference.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public int getInt(String name,String key, int defValue){
		SharedPreferences sp = getSharedPreference(name);
		return sp.getInt(key, defValue);
	}
	
	public void putFloat(String name,String key,float value){
		SharedPreferences preference = getSharedPreference(name);
		Editor editor = preference.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
	
	public float getFloat(String name,String key, int defValue){
		SharedPreferences sp = getSharedPreference(name);
		return sp.getFloat(key, defValue);
	}
	public void putLong(String name,String key,long value){
		SharedPreferences preference = getSharedPreference(name);
		Editor editor = preference.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public long getLong(String name,String key, long defValue){
		SharedPreferences sp = getSharedPreference(name);
		return sp.getLong(key, defValue);
	}
	
	

	
	
	
}
