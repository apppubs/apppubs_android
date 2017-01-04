package com.mportal.client.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * 处理网络请求的实用类
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年3月2日 by zhangwen create
 * 
 */
public class WebUtils {

	
	public static final String EXTRA_NAME_FLOAT_PROGRESS = "progress_num";
	// Json Get请求
	private static final String TAG = WebUtils.class.getSimpleName();
	private static final int TIME_OUT_CONNECT_MILLISECOND = 5 * 1000;
	private static final int TIME_OUT_READ_MILLISECOND = 10 * 1000;
	private static final String CHARSET = "utf-8";

	public static Gson gson;
	
	public static long localAndServiceTimeInterval;
	static {

		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

			@Override
			public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
					throws JsonParseException {
				try {
					String jsonStr = json.getAsString();
					if (jsonStr == null || jsonStr.equals("")) {
						return null;
					} else {
						return df.parse(jsonStr);
					}
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
			}
		});

		gb.registerTypeAdapter(Integer.class, new JsonDeserializer<Integer>() {

			@Override
			public Integer deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
					throws JsonParseException {
				String jsonStr = json.getAsString();
				if (jsonStr == null || jsonStr.equals("")) {
					return 0;
				} else {
					return Integer.parseInt(jsonStr);
				}
			}
		});
		gson = gb.create();
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String requestWithGet(String url) throws IOException, InterruptedException {
		return requestWithGet(url, null, false, null);
	}
	
	/**
	 * get请求
	 * 
	 * @param url
	 * @param requestId 请求id
	 * @param needProgressBroadcast 是否需要
	 * @return action 广播action
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String requestWithGet(String url,Context context,boolean needProgressBroadcast,String action) throws IOException, InterruptedException {
		if(TextUtils.isEmpty(url)){
			return null;
		}
		Log.v(TAG, "请求：" + url);
		StringBuilder sb = new StringBuilder(521);
		HttpURLConnection conn = null;
		BufferedReader br = null;
		try {
			URL urlO = new URL(url);
			conn = (HttpURLConnection) urlO.openConnection();
			conn.setRequestMethod("GET");
			// 告知服务器端解码
			conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			// hp.setHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8")
			conn.setReadTimeout(TIME_OUT_READ_MILLISECOND);
			conn.setConnectTimeout(TIME_OUT_CONNECT_MILLISECOND);
			localAndServiceTimeInterval = conn.getDate()!=0?conn.getDate():localAndServiceTimeInterval;
			long pre = System.currentTimeMillis();
			
			if (conn.getResponseCode() / 100 != 2){
				throw new IOException("Network failure , please check network!");
			}
			int lenghtOfFile = conn.getContentLength();
			
			
			Intent broadcastIntent = new Intent(action);
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()), 10 * 1024);
			char[] buffer = new char[512];
			long total = 0;
			float preProgress = 0;
			int len = 0;
			while ((len = br.read(buffer)) != -1) {
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException();
				}
				sb.append(buffer, 0, len);
				
				if(needProgressBroadcast){
					total += len;
					float curProgress = total / (float)lenghtOfFile;
					if (curProgress - preProgress >= 0.005) {
						preProgress = curProgress;
						broadcastIntent.putExtra(EXTRA_NAME_FLOAT_PROGRESS, preProgress);
						context.sendBroadcast(broadcastIntent);
						Log.v(TAG,"当前进度："+curProgress+"当前action:"+action);
					}
				}
	
			}
			Log.v(TAG, "请求完成：" + url + "用时：" + (System.currentTimeMillis() - pre) + "ms  "+sb.toString());
		} catch (IOException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		} finally {
			if (br != null){
				br.close();
			}
			conn.disconnect();
		}
		return sb.toString();
	}

	// HttpConnection发送post请求
	public static String requestWithPost(String requestUrl, Map<String, Object> requestParamsMap) {
		Log.v(TAG, "发起post请求：" + requestUrl);
		HttpClient hc = new DefaultHttpClient();
		HttpPost hp = new HttpPost(requestUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String key : requestParamsMap.keySet()) {
			params.add(new BasicNameValuePair(key, (String) requestParamsMap.get(key)));
		}
		/*
		 * // 编码方式为HTTP.UTF_8) hp.setEntity(new UrlEncodedFormEntity(parameters,
		 * HTTP.UTF_8)); // 告知服务器端解码 hp.setHeader("Content-Type",
		 * "application/x-www-form-urlencoded; charset=utf-8"); HttpResponse hr
		 * = hc.execute(hp); String data = EntityUtils.toString(hr.getEntity(),
		 * "utf-8");
		 */
		try {
			hp.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			hp.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			HttpResponse hr = null;
			hr = hc.execute(hp);
			String result = "";
			result = EntityUtils.toString(hr.getEntity(), "utf-8");
			Log.v(TAG, "请求：" + requestUrl+"完成");
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";


	}

	// HttpURLConnection发送post请求
	public static int getSummit(String requestUrl, Map<String, Object> requestParamsMap) {
		StringBuffer params = new StringBuffer();
		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;
		HttpURLConnection httpURLConnection = null;
		StringBuffer responseResult = new StringBuffer();
		// 组织请求参数
		Iterator it = requestParamsMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry element = (Map.Entry) it.next();
			params.append(element.getKey());
			params.append("=");
			params.append(element.getValue());
			params.append("&");
		}
		if (params.length() > 0) {
			params.deleteCharAt(params.length() - 1);
		}

		try {
			URL realUrl = new URL(requestUrl);
			// 打开和URL之间的连接
			httpURLConnection = (HttpURLConnection) realUrl.openConnection();
			httpURLConnection.setRequestProperty("Content-Type", "plain/text; charset=UTF-8");

			// 设置通用的请求属性
			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Content-Length", String.valueOf(params.length()));
			// 发送POST请求必须设置如下两行
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			printWriter = new PrintWriter(httpURLConnection.getOutputStream());
			// 发送请求参数
			printWriter.write(params.toString());
			// flush输出流的缓冲
			printWriter.flush();
			// 根据ResponseCode判断连接是否成功
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode / 100 != 2) {
				Log.v(TAG, " Error===" + responseCode);
			} else {
				Log.v(TAG, "Post Success!");
			}
			// 定义BufferedReader输入流来读取URL的ResponseData
			bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				responseResult.append("/n").append(line);
			}
		} catch (Exception e) {
			Log.v(TAG, "send post request error!" + e);
		} finally {
			httpURLConnection.disconnect();
			try {
				if (printWriter != null) {
					printWriter.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
		System.out.println("Integer.parseInt(responseResult.toString().trim())            .." + responseResult.toString().trim());
		return Integer.parseInt(responseResult.toString().trim());
	}

	public static String reqestWithPost(String urlString, String params) {
		URL url;
		StringBuffer sb = new StringBuffer();
		try {
			url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "plain/text; charset=UTF-8");

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(1000 * 5);
			conn.getOutputStream().write(params.getBytes("utf8"));
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
			byte[] buffer = new byte[1024];

			InputStream in = conn.getInputStream();
			int httpCode = conn.getResponseCode();
			System.out.println(in.available());
			while (in.read(buffer, 0, 1024) != -1) {
				sb.append(new String(buffer));
			}
			System.out.println("sb:" + sb.toString());
			in.close();
			System.out.println(httpCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 请求网络并返回实体对象
	 * 
	 * @param url
	 * @param entityClass
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static <T> T request(String url, Class<T> entityClass) throws IOException, InterruptedException ,JsonParseException {
		try {
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			String data = requestWithGet(url);
			Log.v("WebUtils", "请求url:" + url + " 返回数据：" + data);
			return gson.fromJson(data, entityClass);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw e;
		}catch (JsonParseException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 请求网络并返回实体对象， 此实体对象为json中key=“name”的值
	 * 
	 * @param url
	 * @param entityClass
	 * @param name
	 *            实体对象的key
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	public static <T> T request(String url, Class<T> entityClass, String name) throws IOException, InterruptedException,
			JSONException {
		try {

			String data = requestWithGet(url);
			JSONObject jo = new JSONObject(data);
			String resultJoStr = null;
			try{
				JSONArray ja = jo.getJSONArray(name);
				resultJoStr = ja.getString(0);
			}catch(JSONException e){
				resultJoStr = jo.getString(name);
			}
			
			return gson.fromJson(resultJoStr, entityClass);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw e;
		} catch (JSONException e) {
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * Gson 获得对应的对象（History）List
	 * 
	 * @param url
	 * @param entityClass
	 * @param name
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static <T> List<T> requestList(String url, Class<T> entityClass, String name) throws JSONException, IOException,
			InterruptedException {

		return requestList(url, entityClass, name,null);
	}
	public static <T> List<T> requestList(String url, Class<T> entityClass, String name,String password) throws JSONException, IOException,
	InterruptedException {
		
		
		return requestList(url, entityClass, name, password, null, false, null);
		
	}
	public static <T> List<T> requestList(String url, Class<T> entityClass, String name,String password,Context context,boolean needProgress,String action) throws JSONException, IOException,
	InterruptedException {
		
		JSONObject jo;
		
		try {
			String data = requestWithGet(url,context,needProgress,action);
			if(password!=null){
				
				String  content = "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";
				byte[] temp = Base64.decode(data,Base64.DEFAULT);
				byte[] res = decrypt(temp, "bb7c1386d85044ba7a7ae53f3362d656");
				Log.v("Webutils", "解码结果："+new String(res));
				data = new String(res);
				
			}
			jo = new JSONObject(data);
			
			
			List<T> list = new ArrayList<T>();
			if (jo.getString(name)==null||jo.getString(name).equals("")) {
				return null;
			}
			JSONArray ja = jo.getJSONArray(name);
			for (int i = -1; ++i < ja.length();) {
//				System.out.println(ja.getString(i));
				list.add(gson.fromJson(ja.getString(i), entityClass));
			}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	/**
	 * 请求一个数据类型的json返回响应list
	 * 
	 * @param url
	 * @param entityClass
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	public static <T> List<T> requestList(String url, Class<T> entityClass) throws IOException, InterruptedException,
			JSONException {
		String data = requestWithGet(url);
		JSONArray ja = new JSONArray(data);
		int size = ja.length();
		List<T> list = new ArrayList<T>();
		for (int i = -1; ++i < size;) {
			list.add(gson.fromJson(ja.getString(i), entityClass));
		}
		return list;
	}

	/**
	 * 获取装有map的list
	 * @param url
	 * @param name
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	public static List<Map<String,Object>> requestMapList(String url,String name) throws IOException, InterruptedException, JSONException{
				
		String data = requestWithGet(url);
		JSONObject jo = new JSONObject(data);
		JSONArray ja = jo.getJSONArray(name);
		int size = ja.length();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>(size);
		for (int i = -1; ++i < size;) {
			JSONObject tempJO =  ja.getJSONObject(i);
			Map<String,Object> map = new HashMap<String, Object>();
			Iterator<String> names = tempJO.keys();
			
			while(names.hasNext()){
				String tempName = names.next();
				map.put(tempName, tempJO.get(tempName));
			}
			list.add(map);
		}
		return list;
	}
	
	/**
	 * 请求一个数据返回单个结果
	 * 
	 * @param url
	 * @param entityClass
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	public static <T> String requestAloneRequest(String url) throws IOException, InterruptedException, JSONException {
		String data = requestWithGet(url);
		JSONObject jo = new JSONObject(data);
		String resurt = jo.getString("result");
		return resurt;
	}

	/**
	 * 上传文件到服务器
	 * 
	 * @param file需要上传的文件
	 * @param uploadName
	 *            name属性
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 */
	public static String uploadFile(File file, String uploadName, String requestURL) {
		int res = 0;
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		try {
			URL url = new URL(requestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT_READ_MILLISECOND);
			conn.setConnectTimeout(TIME_OUT_CONNECT_MILLISECOND);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

			if (file != null) {
				/**
				 * 当文件不为空时执行上传
				 */
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名
				 */

				sb.append("Content-Disposition: form-data; name=\"" + uploadName + "\"; filename=\"" + file.getName() + "\""
						+ LINE_END);
				sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				res = conn.getResponseCode();
				LogM.log(WebUtils.class, "response code:" + res);
				if (res == 200) {
					LogM.log(WebUtils.class, "request success");
					InputStreamReader isr = new InputStreamReader(conn.getInputStream());
					StringBuffer sb1 = new StringBuffer();
					char[] buffer = new char[1024];
					int size = 0;
					while ((size = isr.read(buffer)) != -1) {
						sb1.append(buffer, 0, size);
					}
					result = sb1.toString();
					LogM.log(WebUtils.class, "result : " + result);
				} else {
					LogM.log(WebUtils.class, "request error");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	// 把一个url的网络图片保存在本地
	public static String writePicUrlSD(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			String filepath = Environment.getExternalStorageDirectory() + "/downloadpic/";
			File file = new File(filepath + url.substring(url.length() - 16, url.length()));
			if (!file.exists()) {

				file.createNewFile();

			}
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 80, out)) {
				out.flush();
				out.close();
				is.close();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}
	
	
	
	public static void saveRemoteFile(String url,File destFile) throws IOException, InterruptedException{
		
		Log.v(TAG, "下载地址：：" + url);
		HttpURLConnection conn = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		try {
			URL urlO = new URL(url);
			conn = (HttpURLConnection) urlO.openConnection();
			conn.setRequestMethod("GET");
			// 告知服务器端解码
			conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			// hp.setHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8")
			conn.setReadTimeout(TIME_OUT_READ_MILLISECOND);
			conn.setConnectTimeout(TIME_OUT_CONNECT_MILLISECOND);

			long pre = System.currentTimeMillis();

			if (conn.getResponseCode() / 100 != 2)
				throw new IOException("Network failure , Please Check networK!");

			bis = new BufferedInputStream (conn.getInputStream());
			if (!destFile.getParentFile().exists()) {
				destFile.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(destFile);
			
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = bis.read(buffer)) != -1) {
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException();
				}
				fos.write(buffer,0,len);
			}

			Log.v(TAG, "请求完成：" + url + "用时：" + (System.currentTimeMillis() - pre) + "ms");
		} catch (IOException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		} finally {
			if (bis != null)
				bis.close();
			if(fos!=null)
				fos.close();
			conn.disconnect();
		}
		
	}
	
	/**
	 * 加密
	 * @param content
	 * @param password
	 * @return
	 */
	public static byte[] encrypt(String content, String password) {
		try {
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	        random.setSeed(password.getBytes());
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128,  random);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化 
			byte[] result = cipher.doFinal(byteContent);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}  
	
	/**
	 * 解密
	 * @param content
	 * @param password
	 * @return
	 */
	/**
	 * 解密
	 * @param content
	 * @param password
	 * @return
	 */
	protected static byte[] decrypt (byte[] encrypted,String password) {
	    byte[] clearText = null;
	    try {
	        byte[] keyData = password.getBytes();
	        SecretKey ks = new SecretKeySpec(keyData, "AES");
	        Cipher c = Cipher.getInstance("AES");
	        c.init(Cipher.DECRYPT_MODE, ks);
	        clearText = c.doFinal(encrypted);
	        return clearText;
	    } catch (Exception e) {
	        return null;
	    }
	} 
	
	/**
	 * 将二进制转换成16进制
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	} 
	
	/**
	 * 将16进制转换为二进制 
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length()/2];
		for (int i = 0;i< hexStr.length()/2; i++) {
			int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
			int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}   
}