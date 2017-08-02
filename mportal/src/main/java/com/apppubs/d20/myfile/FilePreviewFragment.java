package com.apppubs.d20.myfile;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.R;
import com.apppubs.d20.activity.ContainerActivity;
import com.apppubs.d20.bean.AppConfig;
import com.apppubs.d20.fragment.BaseFragment;
import com.apppubs.d20.fragment.WebAppFragment;
import com.apppubs.d20.model.BussinessCallbackCommon;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.widget.AlertDialog;
import com.apppubs.d20.widget.ConfirmDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 文件预览界面，用于办公应用的附件预览，聊天的附件预览，我的文件的文件预览等。
 *
 * @author hezheng
 */
public class FilePreviewFragment extends BaseFragment {

	public static final int REQUEST_CODE_SAVE_PDF = 1;

	public static final String ARGS_STRING_URL = "url";// 文件的网络地址!!
	public static final String ARGS_STRING_FILE_LOCAL_PATH = "local_path";//本地的地址，如果有了本地的地址则不会从网络下载
	public static final String ARGS_INTEGER_FILE_TYPE = "type";// 文件类型
	public static final String ARGS_TEXT_CHARSET = "charset";
	public static final String ARGS_FILE_NAME = "file_name";// 文件名
	public static final String ARGS_BOOLEAN_SHARE_2_QQ = "share_2_qq";

	public static final int TEXT_CHARSET_UTF8 = 0;// 文本编码
	public static final int TEXT_CHARSET_CBK = 1;// 文本编码
	public static final int FILE_TYPE_UNKNOW = 0;// 自动类型，根据文件后缀来区分文件
	public static final int FILE_TYPE_TXT = 1;// 纯文本
	public static final int FILE_TYPE_PDF = 2;// pdf
	public static final int FILE_TYPE_DOC = 3;// word类型
	public static final int FILE_TYPE_EXCEL = 4;// excel类型
	public static final int FILE_TYPE_PPT = 5;// ppt
	public static final int FILE_TYPE_PIC = 6;// 图片类型

	private final int PERMISSION_STORAGE_REQUEST_CODE = 1;

	private String mFileUrl;
	private int mFileType;
	private String mFileLocalPath;// 本地地址
	private int mTextCharSet;
	private String mFileName;// 文件名，如果没有传入的值，则从url中找出

	private TextView mFileNameTv;
	private ImageView mTypeIv;
	private TextView mDownloadProgressTv;
	private Button mPreViewBtn;
	private FileCacheManager mFileCacheManager;

	private boolean isEnableQQShare;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Bundle args = getArguments();
		mFileUrl = args.getString(ARGS_STRING_URL);
		isEnableQQShare = args.getBoolean(ARGS_BOOLEAN_SHARE_2_QQ);
		if (!TextUtils.isEmpty(mFileUrl)) {
			mFileUrl = args.getString(ARGS_STRING_URL).replaceAll("\r|\n", "");
		}
		mFileLocalPath = args.getString(ARGS_STRING_FILE_LOCAL_PATH);
		mFileType = getFileType(args);
		mTextCharSet = args.getInt(ARGS_TEXT_CHARSET);
		mFileName = args.getString(ARGS_FILE_NAME);
		if (TextUtils.isEmpty(mFileName) && !TextUtils.isEmpty(mFileUrl)) {
			mFileName = getFileName(mFileUrl);
		}
		mFileCacheManager = AppContext.getInstance(mContext).getCacheManager();
	}

	private String getFileName(String fileUrl) {
		if (fileUrl == null) {
			return null;
		}
		String strArr[] = fileUrl.split("/");
		return strArr[strArr.length - 1];
	}

	private int getFileType(Bundle args) {
		int type = args.getInt(ARGS_INTEGER_FILE_TYPE);
		// 默认如果未指定类型则从url中获取类型
		if (type == FILE_TYPE_UNKNOW) {
			type = parseFileTypeFromUrl(TextUtils.isEmpty(mFileUrl) ? mFileLocalPath : mFileUrl);
		}
		return type;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		initView(inflater, container);
		mPreViewBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				previewFile(mFileLocalPath);
			}
		});

		return mRootView;
	}

	private void initView(LayoutInflater inflater, ViewGroup container) {
		mRootView = inflater.inflate(R.layout.frg_file_preview, container, false);
		mFileNameTv = (TextView) mRootView.findViewById(R.id.file_preview_filename);
		mDownloadProgressTv = (TextView) mRootView.findViewById(R.id.file_preview_progress_tv);
		mPreViewBtn = (Button) mRootView.findViewById(R.id.file_preview_pre_btn);
		mTypeIv = (ImageView) mRootView.findViewById(R.id.file_preview_iv);

		replacePicByFileType(mFileType);
	}

	private void onFileIsReady(String fileUrl) {
		if (fileUrl == null) {
			return;
		}
		mDownloadProgressTv.setVisibility(View.GONE);
		mFileLocalPath = fileUrl;
		mPreViewBtn.setVisibility(View.VISIBLE);
		previewFile(mFileLocalPath);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (isEnableQQShare) {
			mTitleBar.addRightBtnWithImageResourceIdAndClickListener(R.drawable.qq, new OnClickListener() {

				@Override
				public void onClick(View v) {
					share(mRootView);
				}
			});
		}

		if (!TextUtils.isEmpty(mFileLocalPath)) {
			mPreViewBtn.setVisibility(View.VISIBLE);
		} else {
			if (requestStoragePermissionsIfNeed()){
				loadFile();
			}
		}
		mFileNameTv.setText(mFileName);
	}

	private boolean requestStoragePermissionsIfNeed() {
		int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
		final String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
		if (permission != PackageManager.PERMISSION_GRANTED) {
			FilePreviewFragment.this.requestPermissions(PERMISSIONS_STORAGE,PERMISSION_STORAGE_REQUEST_CODE);
			return false;
		}else{
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode==PERMISSION_STORAGE_REQUEST_CODE){
			if (isAllPermissionGranted(grantResults)){
				loadFile();
			}else{
				Toast.makeText(getContext(),"请在设置中允许访问存储",Toast.LENGTH_LONG).show();
			}
		}
	}

	private boolean isAllPermissionGranted(@NonNull int[] grantResults) {
		for (int permission : grantResults){
			if (permission!= PackageManager.PERMISSION_GRANTED){
				return false;
			}
		}
		return true;
	}

	private void loadFile() {
		File cacheFile = mFileCacheManager.fetchCache(mFileUrl);
		if (cacheFile != null) {
			onFileIsReady(cacheFile.getAbsolutePath());
		} else {
			downloadFile();
		}
	}

	private void downloadFile() {
		mFileCacheManager.cacheFile(mFileUrl, new CacheListener() {
			@Override
			public void onException(FileCacheErrorCode e) {
				if (!e.equals(FileCacheErrorCode.DOWNLOAD_CANCELED)) {
					Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onDone(String fileUrl) {

				onFileIsReady(fileUrl);
			}

			@Override
			public void onProgress(float progress, long totalBytesExpectedToRead) {
				mDownloadProgressTv.setText((int) (progress * 100) + "%");
			}
		});
	}

	private void replacePicByFileType(int fileType) {

		switch (fileType) {

			case FILE_TYPE_PDF:
				mTypeIv.setImageResource(R.drawable.file_preview_pdf);
				break;
			case FILE_TYPE_DOC:
				mTypeIv.setImageResource(R.drawable.file_preview_word);
				break;
			case FILE_TYPE_EXCEL:
				mTypeIv.setImageResource(R.drawable.file_preview_excel);
				break;
			case FILE_TYPE_PPT:
				mTypeIv.setImageResource(R.drawable.file_preview_ppt);
				break;
			case FILE_TYPE_TXT:
				mTypeIv.setImageResource(R.drawable.file_preview_txt);
				break;
			default:
				mTypeIv.setImageResource(R.drawable.file_preview_unknow);
		}

	}

	private int parseFileTypeFromUrl(String url) {

		if (url.endsWith(".txt") || url.endsWith(".log")) {
			return FILE_TYPE_TXT;
		} else if (url.endsWith(".pdf")) {
			return FILE_TYPE_PDF;
		} else if (url.endsWith(".doc") || url.endsWith(".docx")) {
			return FILE_TYPE_DOC;
		} else if (url.endsWith(".xls") || url.endsWith(".xlsx")) {
			return FILE_TYPE_EXCEL;
		} else if (url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".jpeg")) {
			return FILE_TYPE_PIC;
		} else if (url.endsWith(".ppt") || url.endsWith(".pptx")) {
			return FILE_TYPE_PPT;
		} else {
			return FILE_TYPE_UNKNOW;
		}

	}

	public void share(View view) {
		Intent share = new Intent(Intent.ACTION_SEND);
		ComponentName component = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
		share.setComponent(component);
		File file = new File(mFileLocalPath);
		System.out.println("file " + file.exists());
		share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		share.setType("*/*");
		startActivity(Intent.createChooser(share, "发送"));
	}

	// 预览文件
	private void previewFile(String path) {

		File sourceFile = new File(path);

		if (mFileType == FILE_TYPE_TXT) {
			displayTXT(sourceFile);
		} else if (mFileType == FILE_TYPE_PDF) {
			displayPdf(sourceFile);
		} else if (mFileType == FILE_TYPE_DOC) {
			displayDoc(sourceFile);
		} else if (mFileType == FILE_TYPE_EXCEL) {
			displayExcel(sourceFile);
		} else if (mFileType == FILE_TYPE_PPT) {
			displayPpt(sourceFile);
		} else if (mFileType == FILE_TYPE_PIC) {
			displayPic(sourceFile);
		} else {
			Toast.makeText(mContext, "系统不支持此文件预览", Toast.LENGTH_LONG).show();
		}
	}

	private void displayPpt(File sourceFile) {

		try {
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			Uri uri = Uri.fromFile(sourceFile);
			intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
			mContext.startActivity(intent);
		} catch (Exception e) {
			showInstallAppDialog("请安装PPT阅读器！");
		}
	}

	/**
	 * 显示doc文档
	 *
	 * @param sourceFile
	 */
	private void displayDoc(File sourceFile) {
		try {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(sourceFile), "application/msword");
			startActivity(intent);
		} catch (Exception e) {
			showInstallAppDialog("请安装Word阅读器！");
		}
	}

	private void displayExcel(File sourceFile) {
		try {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(sourceFile), "application/vnd.ms-excel");
			startActivity(intent);
		} catch (Exception e) {
			showInstallAppDialog("请安装Excel阅读器！");
		}
	}

	// 显示pdf
	private void displayPdf(final File sourceFile) {
		try {

			Uri path = Uri.fromFile(sourceFile);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(path, "application/pdf");
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);

		} catch (Exception e) {
			e.printStackTrace();
			showInstallAppDialog("请安装PDF阅读器！");
		}
	}

	// 将下载的文本显示
	private void displayTXT(File file) {
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			isr = new InputStreamReader(new FileInputStream(file), mTextCharSet == 1 ? "GBK" : "UTF-8");
			br = new BufferedReader(isr);

			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp + "\n");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				isr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		setVisibilityOfViewByResId(mRootView, R.id.file_preview_info_con_ll, View.GONE);
		setVisibilityOfViewByResId(mRootView, R.id.file_preview_pre_btn, View.GONE);
		setVisibilityOfViewByResId(mRootView, R.id.file_preview_container_sv, View.VISIBLE);
		fillTextView(R.id.file_preview_txt_tv, sb.toString());
	}

	// 显示图片
	private void displayPic(File file) {

		setVisibilityOfViewByResId(mRootView, R.id.file_preview_info_con_ll, View.GONE);
		setVisibilityOfViewByResId(mRootView, R.id.file_preview_pre_btn, View.GONE);
		setVisibilityOfViewByResId(mRootView, R.id.file_preview_pic_div, View.VISIBLE);
		fillImageView(R.id.file_preview_pic_div, "file://" + file.getAbsolutePath());
	}

	//显示提示框之前首先获取服务端参数，如果已经配置好下载地址则允许用户点击下载按钮否则给出提示框即可
	private void showInstallAppDialog(final String message) {

		mSystemBussiness.aSyncAppConfig(mContext, new BussinessCallbackCommon<AppConfig>() {

			@Override
			public void onException(int excepCode) {
				showAlertDialog(message);
			}

			@Override
			public void onDone(AppConfig obj) {
				if (TextUtils.isEmpty(mAppContext.getApp().getDocumentReaderPageUrl())) {
					showAlertDialog(message);
				} else {
					showSelectiveDialog(message);
				}
			}
		});

	}

	private void showAlertDialog(String message) {
		new AlertDialog(mContext, new AlertDialog.OnOkClickListener() {

			@Override
			public void onclick() {

			}
		}, "提示信息", message, "确定").show();
	}

	private void showSelectiveDialog(String message) {
		new ConfirmDialog(mContext, new ConfirmDialog.ConfirmListener() {

			@Override
			public void onOkClick() {
				skip2DownloadPage();
			}

			@Override
			public void onCancelClick() {

			}
		}, message, "是否下载推荐文档阅读器？", "取消", "前往下载").show();
	}

	private void skip2DownloadPage() {
		mSystemBussiness.aSyncAppConfig(mContext, new BussinessCallbackCommon<AppConfig>() {

			@Override
			public void onException(int excepCode) {
				Toast.makeText(mContext, "网络错误", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onDone(AppConfig obj) {
				Bundle extra = new Bundle();
				extra.putString(WebAppFragment.ARGUMENT_STRING_URL, mAppContext.getApp().getDocumentReaderPageUrl());
				ContainerActivity.startActivity(mContext, WebAppFragment.class, extra);
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogM.log(getClass(), "onDestroy()");
		mFileCacheManager.cancelCacheFile(mFileUrl);
	}

	public static boolean isAbleToRead(String url) {

		if (url.endsWith(".pdf") || url.endsWith(".txt") || url.endsWith(".doc") || url.endsWith(".docx") || url.endsWith(".xls") || url.endsWith(".xlsx") || url.endsWith(".ppt") || url.endsWith(".pptx")) {
			return true;
		} else {
			return false;
		}
	}

}