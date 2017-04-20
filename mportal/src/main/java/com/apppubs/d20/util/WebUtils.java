package com.apppubs.d20.util;

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

	
	// Json Get请求
	private static final String TAG = WebUtils.class.getSimpleName();
	private static final int TIME_OUT_CONNECT_MILLISECOND = 5 * 1000;
	private static final int TIME_OUT_READ_MILLISECOND = 15 * 1000;
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
			conn.setReadTimeout(TIME_OUT_READ_MILLISECOND);
			conn.setConnectTimeout(TIME_OUT_CONNECT_MILLISECOND);
			localAndServiceTimeInterval = conn.getDate()!=0?conn.getDate():localAndServiceTimeInterval;
			long pre = System.currentTimeMillis();

			if (conn.getResponseCode() / 100 != 2){
				throw new IOException("Network failure , please check network!");
			}


			int lenghtOfFile = conn.getContentLength();

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
			}

			Log.v(TAG, "请求完成：" + url + "用时：" + (System.currentTimeMillis() - pre) + "ms  "+sb.toString());
		} catch (IOException e) {
			throw e;
		}  finally {
			if (br != null){
				br.close();
			}
			if (conn!=null){
				conn.disconnect();
			}
		}
		return sb.toString();
	}
	
	/**
	 * get请求
	 * 
	 * @param url
	 * @param listener 下载进度
	 * @return action 广播action
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void requestWithGet(String url,DownloadLisener listener) throws IOException, InterruptedException {
		if(TextUtils.isEmpty(url)){
			return ;
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
			conn.setReadTimeout(TIME_OUT_READ_MILLISECOND);
			conn.setConnectTimeout(TIME_OUT_CONNECT_MILLISECOND);
			localAndServiceTimeInterval = conn.getDate()!=0?conn.getDate():localAndServiceTimeInterval;
			long pre = System.currentTimeMillis();
			
			if (conn.getResponseCode() / 100 != 2){
				throw new IOException("Network failure , please check network!");
			}
			int lenghtOfFile = conn.getContentLength();
			
			
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
				
				if(listener!=null){
					total += len;
					float curProgress = total / (float)lenghtOfFile;
					if (curProgress - preProgress >= 0.005) {
						preProgress = curProgress;
						listener.onUpdate(curProgress);
					}else if(total==lenghtOfFile){
						listener.onUpdate(1.0f);
					}

				}
	
			}
			if (listener!=null){
				listener.onSuccess(sb.toString());
			}
			Log.v(TAG, "请求完成：" + url + "用时：" + (System.currentTimeMillis() - pre) + "ms  "+sb.toString());
		} catch (IOException|InterruptedException e) {
			e.printStackTrace();
			if (listener!=null){
				listener.onExceptioin(e);
			}
		} finally {
			if (br != null){
				br.close();
			}
            if (conn!=null){
                conn.disconnect();
            }
		}
	}

	public interface DownloadLisener {
		void onUpdate(double progress);
		void onSuccess(String response);
		void onExceptioin(Exception e);
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
		System.out.println("Integer.parseInt(responseResult.toString().trim()) .." + responseResult.toString().trim());
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
		
		
		return requestList(url, entityClass, name, password, null, null, null);
		
	}
	public static <T> List<T> requestList(String url, Class<T> entityClass, String name, String password, Context context, DownloadLisener listener, String action) throws JSONException, IOException,
	InterruptedException {
		
		JSONObject jo;
		
		try {
			String data = requestWithGet(url);
			if(password!=null){
				
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
	 * @param file 需要上传的文件
	 * @param uploadName
	 *            name属性
	 * @param requestURL
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

	public static String uploadFile(byte[] bytes, String uploadName, String requestURL,Map<String,String> params) {
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

			if (bytes != null) {
				/**
				 * 当文件不为空时执行上传
				 */
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				sb.append(PREFIX + BOUNDARY).append(LINE_END);

				for (String key:params.keySet()){
					String value = params.get(key);
					sb.append("Content-Disposition: form-data; name=\""+key+"\"")
							.append(LINE_END);
					sb.append("Content-Type: text/plain; charset=" + CHARSET).append(
							LINE_END);
					sb.append(LINE_END);
					sb.append(value);
					sb.append(LINE_END);

					sb.append(PREFIX + BOUNDARY).append(LINE_END);
				}

//				sb.append("Content-Disposition: form-data; name=\"appcode\"")
//						.append(LINE_END);
//				sb.append("Content-Type: text/plain; charset=" + CHARSET).append(
//						LINE_END);
//				sb.append(LINE_END);
//				sb.append("U1477213539026").append(LINE_END);
//
//				sb.append(PREFIX + BOUNDARY).append(LINE_END);

				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名
				 */

				sb.append("Content-Disposition: form-data; name=\"" + uploadName + "\"; filename=\"" + uploadName + "\""
						+ LINE_END);
				sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				dos.write(bytes);
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