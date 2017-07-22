package com.apppubs.d20.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.apppubs.d20.exception.ESUnavailableException;
import com.apppubs.d20.model.BussinessCallbackCommon;
import com.apppubs.d20.constant.Constants;

/**
 * 
 * @author zhangwen
 * 
 */

public class FileUtils {
	public static final String LOCAL_CAMERA_DIR_PATH = "/sdcard/mportal/camera/";
	private static final String TAG = FileUtils.class.getSimpleName();
	private static FileUtils mInstance = null;

	private FileUtils() {

	}

	/**
	 * 向应用文件夹写入图片信息
	 * 
	 * @param context
	 * @param srcPath
	 * @throws IOException 
	 */
	public static String  putImage(Context context, String srcPath, String fileName) throws IOException {
		File desFile = new File(context.getExternalFilesDir("portal"),fileName);
		copy(srcPath,desFile.getAbsolutePath());
		return desFile.getAbsolutePath();
	}
	
	

	private static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static void copy(String src, String des) throws IOException {
		File srcFile = new File(src);
		if (!srcFile.exists()) {
			throw new FileNotFoundException();
		}
		File file = new File(des);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}

		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
		try {
			byte[] buffer = new byte[1024];
			int size = 0;
			while ((size = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, size);
			}
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
		Log.v("FileUtils", "成功拷贝"+src+"到："+des);
	}

	// 以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * 持久化对象
	 * 
	 * @param context
	 * @param obj
	 * @param fileName
	 */
	public static void writeObj(Context context, Object obj, String fileName) {

		if(obj==null||fileName==null){
			return;
		}
		File file = new File(context.getFilesDir(), fileName);

		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(obj);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读取持久化对象
	 * 
	 * @param fileName
	 * @return
	 */
	public static Object readObj(Context context, String fileName) {
		ObjectInputStream ois = null;

		try {
			File file = new File(context.getFilesDir(), fileName);
			if (!file.exists())
				return null;
			ois = new ObjectInputStream(new FileInputStream(file));
			return ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null)
					ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	public static FileUtils getInstance() {
		if (null == mInstance) {
			mInstance = new FileUtils();
		}
		return mInstance;
	}

	public static File getExternalStorageFile() throws IOException {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory();
		} else {
			throw new ESUnavailableException("The sd card is unusable！！");
		}

	}

	public static File getAppExternalStorageFile() throws ESUnavailableException {

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return new File(Environment.getExternalStorageDirectory(), Constants.APP_FOLDER_NAME);
		} else {
			throw new ESUnavailableException("Thes sd card is unusable！！");
		}

	}
	public static File getAppExternalFilesStorageFile() throws ESUnavailableException{
		File file = getAppExternalStorageFile();
		return new File(file.getAbsoluteFile(),"files");
	}

	public static File getPaperStorageFile() throws ESUnavailableException {

		File file = getAppExternalStorageFile();
		File newFile = new File(file, Constants.APP_FOLDER_NAME);
		return newFile;

	}

	// 拍照图片的本地存储
	public String getCameraTempFilePath() {
		File dir = new File(LOCAL_CAMERA_DIR_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return LOCAL_CAMERA_DIR_PATH;
	}

	/**
	 * 格式化byte类型的文件大小
	 * 
	 * @param fileS
	 * @return
	 */
	public static String formetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("0.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获得文件或者文件夹大小
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	public static long getFileSize(File file) throws FileNotFoundException {

		long size = 0;
		if (file == null || !file.exists()) {
			throw new FileNotFoundException("文件" + file + "不存在");
		}
		if (file.isFile()) {
			return file.length();
		}
		File flist[] = file.listFiles();
		for (int i = 0; i < flist.length; i++) {
			size += getFileSize(flist[i]);
		}
		return size;
	}

	/**
	 * 获取某一期的缓存文件夹File对象
	 * 
	 * @return
	 * @throws ESUnavailableException
	 */
	public static File getIssueDirectory(String paperCode, String issueName) throws ESUnavailableException {
		File ereader = getAppExternalStorageFile();
		return new File(ereader, paperCode + "/" + issueName);
	}

	/**
	 * 级联递归删除,如果传入的file为目标则包括此目录
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public static void deleteRec(File file) throws FileNotFoundException {

		Log.v(TAG, "当前文件：" + file.getAbsolutePath());
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		if (file.isFile() || file.list().length == 0) {
			file.delete();
		} else if (file.isDirectory()) {
			File[] fileArr = file.listFiles();
			for (File f : fileArr) {
				delete(f);
			}
		}
		// 包括自己
		file.delete();

	}

	public static void delete(File file) throws FileNotFoundException {
		// 为了避免EBUSY异常,删除前先重命名
		File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
		file.renameTo(to);
		deleteRec(to);
	}

	public static void delete(String path) throws FileNotFoundException{
		delete(new File(path));
	}
	
	public interface OnUnZipListener {
		void onUnZip(String file) throws IOException, JSONException;
	}

	/**
	 * 递归解zip包操作,平铺展开
	 * 
	 * @param zipFile
	 * @param outputFolder
	 * @throws JSONException
	 * @throws IOException
	 */
	public static void unZip(String zipFile, String outputFolder, OnUnZipListener onUnZipListener) throws JSONException,
			IOException {

		byte[] buffer = new byte[1024];

		// create output directory is not exists
		File folder = new File(outputFolder);
		if (!folder.exists()) {
			folder.mkdir();
		}
		try {
			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry ze = null;

			while ((ze = zis.getNextEntry()) != null) {

				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				System.out.println("file unzip : " + newFile.getAbsoluteFile());

				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				// new File(newFile.getParent()).mkdirs();
				File des = new File(newFile.getParentFile().getParentFile(), newFile.getName());
				FileOutputStream fos = new FileOutputStream(des);

				FileChannel fc = fos.getChannel(); // 获取FileChannel对象
				FileLock fl = fc.tryLock(); // or fc.lock();

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				if (null != fl)
					fl.release(); // 释放文件锁 注意：释放锁要在文件写操作之前，否则会出异常

				ze = null;
				fos.flush();
				fos.close();
				onUnZipListener.onUnZip(des.getAbsolutePath());
			}
			zis.closeEntry();
			zis.close();
			new File(zipFile).delete();
			Log.v("FileUtils", "解压完成");
		} catch (JSONException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {

		}

	}

	public static void asyDownload(final String url, final String desPath, final BussinessCallbackCommon<String> callback) {
		new Thread("异步下载") {
			@Override
			public void run() {
				super.run();
				Log.v(TAG, "开始异步下载" + "下载url：" + url + "下载目的地：" + desPath);
				HttpURLConnection conn = null;
				BufferedInputStream bis = null;
				BufferedOutputStream bos = null;
				try {
					URL urlO = new URL(url);
					conn = (HttpURLConnection) urlO.openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(1000 * 10);
					conn.setConnectTimeout(1000 * 5);
					int tolLen = conn.getContentLength();
					if (conn.getResponseCode() / 100 == 2) {

						long pre = System.currentTimeMillis();
						bis = new BufferedInputStream(conn.getInputStream(), 64 * 1024);
						File desFile = new File(desPath);
						if (!desFile.getParentFile().exists())
							desFile.mkdirs();
						bos = new BufferedOutputStream(new FileOutputStream(desFile), 64 * 1024);
						InputStream is = conn.getInputStream();
						byte[] buffer = new byte[1024 * 10];

						int len = 0;
						while ((len = is.read(buffer, 0, 1024 * 10)) != -1) {
							bos.write(buffer, 0, len);
						}
						Log.v(TAG, "下载用时：" + (System.currentTimeMillis() - pre) + "ms" + "下载大小：" + tolLen + "bytes");
						callback.onDone(desFile.getAbsolutePath());
					}
					callback.onException(BussinessCallbackCommon.EXCEPTION_COMMON);

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
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
				}

			}
		}.start();

	}

	public static void download(String url, String desPath) throws InterruptedException {
		downloadWithProgress(url, desPath, false, false, null);

	}

	/**
	 * 有进度条的下载
	 * 
	 * @param url
	 * @param desPath
	 * @param needProgress 是否需呀有进度
	 *            ,如果需要的话需要有onprogressListener
	 * @param onProgress
	 * @param needDelete
	 *            下载中断是否需要删除文件
	 * @return 下载大小
	 * @throws InterruptedException
	 */
	public static int downloadWithProgress(String url, String desPath, boolean needProgress, boolean needDelete,
			OnProgressListener onProgress) throws InterruptedException {
		HttpURLConnection conn = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		int totalLen = 0;
		System.out.println("当前下载url" + url);
		try {
			URL urlO = new URL(url);
			conn = (HttpURLConnection) urlO.openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(1000 * 10);
			conn.setConnectTimeout(1000 * 5);
			totalLen = conn.getContentLength();
			if (conn.getResponseCode() / 100 == 2) {
				long pre = System.currentTimeMillis();
				File desFile = new File(desPath);

				bis = new BufferedInputStream(conn.getInputStream(), 32 * 1024);
				if (!desFile.getParentFile().exists())
					desFile.getParentFile().mkdirs();
				bos = new BufferedOutputStream(new FileOutputStream(desFile), 64 * 1024);
				InputStream is = conn.getInputStream();
				byte[] buffer = new byte[1024 * 10];

				int len = 0;
				int curLen = 0;// 当前下载长度
				float preProgress = 0;// 上一个进度位置
				while ((len = is.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
					curLen += len;
					if (needProgress) {

						float curProgress = curLen / (float) totalLen;// 当前进度位置
						if (curProgress - preProgress > 0.01) {// 当下载量超过1%时进行通知
							onProgress.onProgress(curProgress, totalLen, curLen);
							preProgress = curProgress;
						}
					}

					// 如果当前线程中断下载立即停止
					if (Thread.currentThread().isInterrupted()) {
						Log.v(TAG, "跳出循环结束下载并如果需要的话删除未下载万文件");
						if (desFile.exists() && needDelete)
							desFile.delete();
						throw new InterruptedException("下载中断");
					}
				}
				Log.v(TAG, "同步下载 "+url+" 用时：" + (System.currentTimeMillis() - pre) + "ms");
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
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
		}
		return totalLen;
	}

	public interface OnProgressListener {
		void onProgress(float progress, int total, int cur);
	}

	public static String readFile(File file) throws IOException {

		if (file.exists() && file.isFile()) {
			StringBuilder sb = new StringBuilder(1024 * 5);
			FileReader fr = new FileReader(file);
			char[] buffer = new char[1024];
			int len = 0;
			while ((len = fr.read(buffer)) != -1) {
				sb.append(buffer, 0, len);
			}

			fr.close();
			return sb.toString();
		}
		throw new IOException();
	}

	

}
