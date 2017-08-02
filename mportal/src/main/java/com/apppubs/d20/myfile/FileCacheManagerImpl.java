package com.apppubs.d20.myfile;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.exception.ESUnavailableException;
import com.apppubs.d20.util.FileUtils;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.WebUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangwen on 2017/7/13.
 */

public class FileCacheManagerImpl implements FileCacheManager {

	private final int MAX_THREAD_NUM = 3;

	private Map<String,CacheTask> mTaskMap;
	private ExecutorService mExecutorService;
	private Handler mHandler;
	private Context mContext;

	private static final int TIME_OUT_CONNECT_MILLISECOND = 5 * 1000;
	private static final int TIME_OUT_READ_MILLISECOND = 15 * 1000;
	private static final String CHARSET = "utf-8";

	private static FileCacheManagerImpl sManager;

	private interface CacheTaskInterface{
		Runnable getTaskRunnable();
		Runnable getOnDoneRunnable(final String filePath);
		Runnable getOnProgressRunnable(final float progress, final long bytesExceptedRead);
		Runnable getOnExceptionRunnable(final FileCacheErrorCode e);
		void cancel();
	}

	private FileCacheManagerImpl(Context context) {
		mTaskMap = new HashMap<String,CacheTask>();
		mExecutorService = Executors.newFixedThreadPool(MAX_THREAD_NUM);
		mHandler = new Handler(Looper.getMainLooper());
		mContext = context;
	}

	public static FileCacheManagerImpl getInstance(Context context){
		if (sManager==null){
			synchronized (FileCacheManagerImpl.class){
				if (sManager==null){
					sManager = new FileCacheManagerImpl(context);
				}
			}
		}
		return sManager;
	}

	@Override
	public void cacheFile(String fileUrl,CacheListener listener){
		if (TextUtils.isEmpty(fileUrl)){
			return;
		}
		cancelCacheFile(fileUrl);
		CacheTask task = new CacheTask(fileUrl,mHandler,listener);
		mTaskMap.put(fileUrl,task);
		mExecutorService.execute(task.getTaskRunnable());
	}

	@Override
	public void cancelCacheFile(String fileUrl){
		if (TextUtils.isEmpty(fileUrl)){
			return;
		}
		CacheTask t = mTaskMap.get(fileUrl);
		if (t!=null){
			t.cancel();
		}
	}

	private class CacheTask implements CacheTaskInterface{
		private String mFileUrl;
		private Handler mHandler;
		private CacheListener mListener;
		private boolean mIsCancel;
		CacheTask(String fileUrl,Handler handler,CacheListener listener){
			mHandler = handler;
			mFileUrl = fileUrl;
			mListener = listener;
		}

		@Override
		public Runnable getTaskRunnable(){
			return new Runnable(){
				@Override
				public void run() {

					HttpURLConnection conn = null;
					BufferedInputStream bis = null;
					BufferedOutputStream bos = null;
					Log.v(this.getClass().getName(),"下载地址"+mFileUrl);
					File desTempFile = null;
					try {
						URL urlO = new URL(encodeFileName(mFileUrl));
						conn = (HttpURLConnection) urlO.openConnection();
						conn.setRequestMethod("GET");
						conn.setReadTimeout(1000 * 10);
						conn.setConnectTimeout(1000 * 5);
						long totalLen = conn.getContentLength();

						Log.v("FileCacheManager","响应码："+conn.getResponseCode()+"");
						if (conn.getResponseCode() / 100 == 2) {
							long pre = System.currentTimeMillis();
							desTempFile = getTempDesFile(mFileUrl);
							bis = new BufferedInputStream(conn.getInputStream(), 32 * 1024);
							bos = new BufferedOutputStream(new FileOutputStream(desTempFile), 64 * 1024);
							InputStream is = conn.getInputStream();
							byte[] buffer = new byte[1024 * 10];

							int len = 0;
							int curLen = 0;// 当前下载长度
							float preProgress = 0;// 上一个进度位置
							while ((len = is.read(buffer)) != -1) {
								if (mIsCancel){
									throw new InterruptedException("下载取消");
								}
								bos.write(buffer, 0, len);
								curLen += len;
								float curProgress = curLen / (float) totalLen;// 当前进度位置
								System.out.print("当前进度"+curProgress);
								if (curProgress - preProgress > 0.01) {// 当下载量超过1%时进行通知
									mHandler.post(getOnProgressRunnable(curProgress,totalLen));
									preProgress = curProgress;
								}

								// 如果当前线程中断下载立即停止
								if (Thread.currentThread().isInterrupted()) {
									Log.v(this.getClass().getName(), "跳出循环结束下载并如果需要的话删除未下载万文件");
									if (desTempFile.exists()){
										desTempFile.delete();
									}
									throw new InterruptedException("下载中断");
								}
							}
							//下载完成
							File desFile = getDesFile(mFileUrl);
							desTempFile.renameTo(desFile);
							FileCacheDataHelper.getInstance().put(mFileUrl,desFile.getAbsolutePath());
							mHandler.post(getOnProgressRunnable(1,totalLen));
							mHandler.post(getOnDoneRunnable(desFile.getAbsolutePath()));
							Log.v(this.getClass().getName(), "下载 "+mFileUrl+" 用时：" + (System.currentTimeMillis() - pre) + "ms");
						}else{
							mHandler.post(getOnExceptionRunnable(FileCacheErrorCode.FILE_NOT_EXIST));
						}

					} catch(InterruptedException e){
						mHandler.post(getOnExceptionRunnable(FileCacheErrorCode.DOWNLOAD_CANCELED));
					} catch (MalformedURLException e){
						mHandler.post(getOnExceptionRunnable(FileCacheErrorCode.MALFORMEDA_URL));
					}catch (IOException e){
						e.printStackTrace();
						mHandler.post(getOnExceptionRunnable(FileCacheErrorCode.IO_EXCEPTION));
					}finally {
						if (conn != null)
							conn.disconnect();
						try {
							if (bis != null)
								bis.close();
							if (bos != null)
								bos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (desTempFile!=null&&desTempFile.exists()){
							desTempFile.delete();
						}
					}
				}
			};
		}

		private String encodeFileName(String fileUrl) {
			String[] arr = fileUrl.split("/");
			String fileName = arr[arr.length-1];
			String encodedFileName = null;
			try {
				encodedFileName = URLEncoder.encode(fileName,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return fileUrl.replace(fileName,encodedFileName);
		}

		public Runnable getOnDoneRunnable(final String filePath){
			return new Runnable() {
				@Override
				public void run() {
					mListener.onDone(filePath);
				}
			};
		}

		public Runnable getOnProgressRunnable(final float progress, final long bytesExceptedRead){
			return new Runnable() {
				@Override
				public void run() {
					mListener.onProgress(progress,bytesExceptedRead);
				}
			};
		}

		public Runnable getOnExceptionRunnable(final FileCacheErrorCode e){
			return new Runnable() {
				@Override
				public void run() {
					mListener.onException(e);
				}
			};
		}

		public void cancel(){
			mIsCancel = true;
		}

	}

	private class UploadTask implements CacheTaskInterface{

		private File mFile;
		private CacheListener mListener;
		private Handler mHandler;
		UploadTask (File file,CacheListener listener,Handler handler){
			mFile = file;
			mListener = listener;
			mHandler = handler;
		}
		@Override
		public Runnable getTaskRunnable() {
			return new Runnable() {

				@Override
				public void run() {
					int res = 0;
					String result = null;
					String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
					String PREFIX = "--", LINE_END = "\r\n";
					String CONTENT_TYPE = "multipart/form-data"; // 内容类型

					try {
						URL url = new URL(URLs.URL_UPLOAD);
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

						if (mFile != null) {
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

							sb.append("Content-Disposition: form-data; name=file; filename=\"" + mFile.getName() + "\""
									+ LINE_END);
							sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
							sb.append(LINE_END);
							dos.write(sb.toString().getBytes());
							InputStream is = new FileInputStream(mFile);
							byte[] bytes = new byte[1024];
							int len = 0;
							long totalBytes = mFile.length();
							long uploadedBytes = 0;
							while ((len = is.read(bytes)) != -1) {
								dos.write(bytes, 0, len);
								uploadedBytes+= len;
								mHandler.post(getOnProgressRunnable(uploadedBytes/totalBytes,totalBytes));
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
								JSONResult jr = JSONResult.compile(result);
								String str = (String) jr.getResultMap().get("photourl");
								mHandler.post(getOnDoneRunnable(str));

							} else {
								LogM.log(WebUtils.class, "request error");
							}
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		}

		@Override
		public Runnable getOnDoneRunnable(final String filePath) {
			return new Runnable() {
				@Override
				public void run() {
					mListener.onDone(filePath);
				}
			};
		}

		@Override
		public Runnable getOnProgressRunnable(final float progress, final long bytesExceptedRead) {
			return new Runnable() {
				@Override
				public void run() {
					mListener.onProgress(progress,bytesExceptedRead);
				}
			};
		}

		@Override
		public Runnable getOnExceptionRunnable(final FileCacheErrorCode e) {
			return new Runnable() {
				@Override
				public void run() {
					mListener.onException(e);
				}
			};
		}

		@Override
		public void cancel() {

		}
	}

	@NonNull
	private File getDesFile(String fileUrl) throws ESUnavailableException {
		File cacheDir = getCacheDirFile();
		File desFile = new File(cacheDir,getFileName(fileUrl));
		while (desFile.exists()){
			desFile = getNextSameNameFile(desFile);
		}
		return desFile;
	}

	private String getFileNameWithoutExt(File file){
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex>-1){
			return fileName.substring(0,dotIndex);
		}
		return fileName;
	}

	private File getNextSameNameFile(File file){
		String nameWithoutExt = getFileNameWithoutExt(file);
		Pattern pattern = Pattern.compile("\\(\\d\\)");
		Matcher matcher = pattern.matcher(nameWithoutExt);
		String indexStr = null;
		while (matcher.find()){
			indexStr = matcher.group();
		}
		if (indexStr!=null){
			int index = Integer.parseInt(indexStr.substring(1,indexStr.length()-1));
			String newName = nameWithoutExt.replaceAll("\\("+index+"\\)","")+"("+ ++index+")"+getSuffixOfFile(file);
			return new File(file.getParentFile(),newName);
		}
		return new File(file.getParentFile(),nameWithoutExt+"(1)"+getSuffixOfFile(file));
	}


	private String getSuffixOfFile(File file){
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex>-1){
			return fileName.substring(dotIndex);
		}
		return "";
	}

	private File getTempDesFile (String fileUrl) throws ESUnavailableException {
		File tempCacheDir = getCacheDirFile();
		String fileName = UUID.randomUUID().toString().replaceAll("-","")+".temp";
		return new File(tempCacheDir,fileName);
	}

	private String getFileName(String fileUrl) {
		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
		return fileName;
	}

	private File getCacheDirFile() throws ESUnavailableException {
		File cacheDir = new File(FileUtils.getAppFilesDirectory(mContext),"caches");
		if (!cacheDir.exists()){
			cacheDir.mkdirs();
		}
		return cacheDir;
	}

	@Override
	public File fetchCache(String fileUrl){
		String cachedPath = FileCacheDataHelper.getInstance().getPath(fileUrl);
		if (cachedPath!=null&&new File(cachedPath).exists()){
			return new File(cachedPath);
		}
		return null;
	}

	@Override
	public boolean removeCache(String fileUrl){
		String cachedPath = FileCacheDataHelper.getInstance().pop(fileUrl);
		if (cachedPath!=null&&new File(cachedPath).exists()){
			return new File(cachedPath).delete();
		}
		return false;
	}


	@Override
	public void uploadFile(File file, CacheListener listener) {

		if (file==null){
			return;
		}
		UploadTask uploadTask = new UploadTask(file,listener,mHandler);

		mExecutorService.execute(uploadTask.getTaskRunnable());

	}
}

class FileCacheDataHelper {
	private Map<String,String> mData = new HashMap<String,String>();
	private File mDataFile ;
	private static FileCacheDataHelper sHelper;

	private FileCacheDataHelper(){
		try {
			mDataFile = new File(FileUtils.getAppExternalFilesStorageFile(),"files.data");
			if (mDataFile.exists()){
				restoreFromDisk();
			}
		} catch (ESUnavailableException e) {
			e.printStackTrace();
		}
	}

	static FileCacheDataHelper getInstance(){
		if (sHelper==null){
			synchronized (FileCacheDataHelper.class){
				if (sHelper==null){
					sHelper = new FileCacheDataHelper();
				}
			}
		}
		return sHelper;
	}

	void put(String url,String filePath){
		mData.put(url,filePath);
		writeData2Disk();
	}

	String pop(String url){
		return mData.remove(url);
	}
	String getPath(String url){
		return mData.get(url);
	}

	private void writeData2Disk(){
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(mDataFile));
			oos.writeObject(mData);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (oos!=null){
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void restoreFromDisk(){
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(mDataFile));
			mData = (Map<String, String>) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ois!=null){
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
