package com.apppubs.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

import com.apppubs.MportalApplication;
import com.apppubs.constant.Constants;

/**
 * 系统日志
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年1月4日 by zhangwen create
 * 
 */
public class LogM {

	
	public static void log(Class<?> clazz, String msg) {
		Log.v("LogM V"+clazz.getSimpleName(), msg);
		// 写入本地文件
		if (Constants.IS_DEBUG) {
			formatMsg(clazz, msg);
		}
	}

	private static void formatMsg(Class<?> clazz, String msg) {
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",Locale.CHINA);
		insert2File(sdf.format(date)+"-"+String.format("%-12s", clazz.getSimpleName())+"\n"+msg+"\n\n");
	}

	/**
	 * 
	 * @param clazz
	 * @param msg
	 * @param level
	 *            使用Log类中的level
	 */
	public static void log(Class<?> clazz, String msg, int level) {
		switch (level) {
		case Log.VERBOSE:
			Log.v(clazz.getSimpleName(), msg);
			break;
		case Log.DEBUG:
			Log.d(clazz.getSimpleName(), msg);
			break;
		case Log.INFO:
			Log.i(clazz.getSimpleName(), msg);
			break;
		case Log.WARN:
			Log.w(clazz.getSimpleName(), msg);
			break;
		case Log.ERROR:
			Log.e(clazz.getSimpleName(), msg);
			break;
		default:
			Log.v(clazz.getSimpleName(), msg);
		}

		// 写入本地文件
		if (Constants.IS_DEBUG) {
			formatMsg(clazz, msg);

		}
	}
	
	public static void log(Class<?> clazz,Throwable throwable){
		StringWriter  writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		throwable.printStackTrace(pw);
		log(clazz, writer.toString(),Log.ERROR);
	}
	private static void insert2File(String msg) {
		Context context = MportalApplication.getContext();
		File logs = context.getExternalFilesDir("logs");
		if (!logs.exists()) {
			logs.mkdirs();
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		String fileName = sdf.format(date) + ".log";
		FileReader fr = null;
		StringBuilder sb = new StringBuilder();
		sb.append(msg);

		File outFile = new File(logs, fileName);
		FileWriter fw = null;
		try {
			fw = new FileWriter(outFile, true);
			fw.write(msg);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fr!=null){
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
