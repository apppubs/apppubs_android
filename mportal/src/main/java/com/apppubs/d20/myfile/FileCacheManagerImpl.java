package com.apppubs.d20.myfile;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.apppubs.d20.exception.ESUnavailableException;
import com.apppubs.d20.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

	private static FileCacheManagerImpl sManager;

	private FileCacheManagerImpl() {
		mTaskMap = new HashMap<String,CacheTask>();
		mExecutorService = Executors.newFixedThreadPool(MAX_THREAD_NUM);
		mHandler = new Handler(Looper.getMainLooper());
	}

	public static FileCacheManagerImpl getInstance(){
		if (sManager==null){
			synchronized (FileCacheManagerImpl.class){
				if (sManager==null){
					sManager = new FileCacheManagerImpl();
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

	private class CacheTask{
		private String mFileUrl;
		private Handler mHandler;
		private CacheListener mListener;
		private boolean mIsCancel;
		CacheTask(String fileUrl,Handler handler,CacheListener listener){
			mHandler = handler;
			mFileUrl = fileUrl;
			mListener = listener;
		}

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

		private Runnable getOnDoneRunnable(final String filePath){
			return new Runnable() {
				@Override
				public void run() {
					mListener.onDone(filePath);
				}
			};
		}

		private Runnable getOnProgressRunnable(final float progress, final long bytesExceptedRead){
			return new Runnable() {
				@Override
				public void run() {
					mListener.onProgress(progress,bytesExceptedRead);
				}
			};
		}

		private Runnable getOnExceptionRunnable(final FileCacheErrorCode e){
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
		File cacheDir = new File(FileUtils.getAppExternalFilesStorageFile(),"caches");
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
