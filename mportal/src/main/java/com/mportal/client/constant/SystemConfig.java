package com.mportal.client.constant;

import com.mportal.client.MportalApplication;


/**
 * 有关系统的常量
 *
 */
public class SystemConfig {
	/**
	 * 系统外部存储根文件夹
	 */
	public static final String APP_FOLDER_NAME = "apppubs";
	public static String APP_FILE_NAME = "mportal.apk";
	
	/**
	 * 是否打印日志
	 */
	public static boolean CONFIG_UTIL_PRINT_LOG = true;
	
	/**
	 * 各种url中的占位符
	 */
	public static final String PLACEHOLDER_USERNAME = "$username";//用户名 
	public static final String PLACEHOLDER_USERID = "$userid";//用户ID；
	public static final String PLACEHOLDER_APPID = "$appid";//appid==appcode
	public static final String PLACEHOLDER_COPER_CODE = "$corpcode";
	
	
	
	/**
	 * 转换服务器传来的url
	 * @param url
	 * @return
	 */
	public static String convertUrl(String url){
		if(url.contains(SystemConfig.PLACEHOLDER_USERNAME)){
			url = url.replace(SystemConfig.PLACEHOLDER_USERNAME, MportalApplication.user.getUsername());
			
		}
		if(url.contains(SystemConfig.PLACEHOLDER_USERID)){
			url = url.replace(SystemConfig.PLACEHOLDER_USERID, MportalApplication.user.getUserId());
			
		}
		if(url.contains(SystemConfig.PLACEHOLDER_APPID)){
			url = url.replace(SystemConfig.PLACEHOLDER_APPID, MportalApplication.app.getCode());
		}
		if(url.contains(SystemConfig.PLACEHOLDER_COPER_CODE)){
			url = url.replace(SystemConfig.PLACEHOLDER_COPER_CODE,  MportalApplication.user.getOrgCode());
		}
		return url;
	}
	
}
