package com.mportal.client.fragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.activity.ContainerActivity;
import com.mportal.client.business.BussinessCallbackCommon;
import com.mportal.client.util.LogM;
import com.mportal.client.widget.AlertDialog;
import com.mportal.client.widget.AlertDialog.OnOkClickListener;
import com.mportal.client.widget.ConfirmDialog;
import com.mportal.client.widget.ConfirmDialog.ConfirmListener;

/**
 * 文件预览界面，用于办公应用的附件预览，聊天的附件预览，我的文件的文件预览等。
 * 
 * @author hezheng
 * 
 */
public class FilePreviewFragment extends BaseFragment{

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

	private String mFileUrl;
	private int mFileType;
	private String mFileLocalPath;// 本地地址
	private int mTextCharSet;
	private String mFileName;// 文件名，如果没有传入的值，则从url中找出

	private TextView mFileNameTv;
	private ImageView mTypeIv;
	private TextView mDownloadProgressTv;
	private Button mPreViewBtn;

	private AsyncTask<String, Integer, String> mDownloadTask;
	
	private boolean isEnableQQShare;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Bundle args = getArguments();
		mFileUrl = args.getString(ARGS_STRING_URL);
		isEnableQQShare = args.getBoolean(ARGS_BOOLEAN_SHARE_2_QQ);
		if(!TextUtils.isEmpty(mFileUrl)){
			mFileUrl = args.getString(ARGS_STRING_URL).replaceAll("\r|\n", "");
		}
		mFileLocalPath = args.getString(ARGS_STRING_FILE_LOCAL_PATH);
		mFileType = args.getInt(ARGS_INTEGER_FILE_TYPE);
		mTextCharSet = args.getInt(ARGS_TEXT_CHARSET);
		mFileName = args.getString(ARGS_FILE_NAME);
		if (TextUtils.isEmpty(mFileName)&&!TextUtils.isEmpty(mFileUrl)) {
			String strArr[] = mFileUrl.split("/");
			mFileName = strArr[strArr.length - 1];
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		mRootView = inflater.inflate(R.layout.frg_file_preview, container, false);
		mFileNameTv = (TextView) mRootView.findViewById(R.id.file_preview_filename);
		mDownloadProgressTv = (TextView) mRootView.findViewById(R.id.file_preview_progress_tv);
		mPreViewBtn = (Button) mRootView.findViewById(R.id.file_preview_pre_btn);
		mTypeIv = (ImageView) mRootView.findViewById(R.id.file_preview_iv);

		mDownloadTask = new DownloadAsyncTask();
		if (!TextUtils.isEmpty(mFileLocalPath)) {
			mPreViewBtn.setVisibility(View.VISIBLE);
		}else{
			mDownloadTask.execute(mFileUrl);
		}
		mFileNameTv.setText(mFileName);

		mPreViewBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mFileType==FILE_TYPE_TXT){
					displayTXTByPath(mFileLocalPath);
				}else{
					previewFile(mFileLocalPath);
				}
			}
		});

		// 默认如果未指定类型则从url中获取类型
		if (mFileType == FILE_TYPE_UNKNOW) {
			mFileType = parsetFileTypeFromUrl(TextUtils.isEmpty(mFileUrl)?mFileLocalPath:mFileUrl);
		}

		replacePicByFileType(mFileType);

		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(isEnableQQShare){
			mTitleBar.addRightBtnWithImageResourceIdAndClickListener(R.drawable.qq, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					share(mRootView);
				}
			});
		}
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
		case FILE_TYPE_TXT:
			mTypeIv.setImageResource(R.drawable.file_preview_txt);
			break;
		default:
			mTypeIv.setImageResource(R.drawable.file_preview_unknow);
		}

	}

	private int parsetFileTypeFromUrl(String url) {

		if (url.endsWith(".txt")||url.endsWith(".log")) {
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
		    ComponentName component = new ComponentName("com.tencent.mobileqq","com.tencent.mobileqq.activity.JumpActivity");
		    share.setComponent(component);
		    File file = new File(mFileLocalPath);
		    System.out.println("file " + file.exists());
		    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		    share.setType("*/*");
		    startActivity(Intent.createChooser(share, "发送"));
	}
	private class DownloadAsyncTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			int count;
			String fileName = null;
			InputStream input = null;
			OutputStream output = null;
			try {
				if (TextUtils.isEmpty(params[0])) {
					LogM.log(this.getClass(), "下载地址为空");
					return null;
				}
				LogM.log(this.getClass(), "下载地址" + params[0]);
				String dnurl = params[0];

				int lastSplash = dnurl.lastIndexOf("/");
				if (lastSplash > 0) {
					fileName = dnurl.substring(lastSplash + 1, dnurl.length()).toLowerCase();
					fileName.trim();
					fileName = fileName.replaceAll("\r|\n", "");
				}

				URL url = new URL(params[0]);
				URLConnection connection = url.openConnection();
				connection.connect();

				int lenghtOfFile = connection.getContentLength();

				input = new BufferedInputStream(url.openStream());
				File desFile = new File(mContext.getExternalFilesDir(""), fileName);

				output = new FileOutputStream(desFile);

				byte data[] = new byte[1024];

				long total = 0;

				float preProgress = 0;
				while ((count = input.read(data)) != -1) {
					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
					total += count;
					float curProgress = (total * 100) / lenghtOfFile;
					if (curProgress - preProgress >= 1) {
						preProgress = curProgress;
						publishProgress((int) preProgress);
					}

					output.write(data, 0, count);

				}

			} catch (Exception e) {

				Log.e("FilePreviewFragement", e.getMessage().toString());
				return null;

			} finally {
				try {
					output.close();
					input.close();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

			}
			return fileName;

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mDownloadProgressTv.setText(values[0] + "%");
		}

		@Override
		protected void onPostExecute(String filename) {

			if (filename == null) {
				return;
			}
			mFileLocalPath = filename;
			mPreViewBtn.setVisibility(View.VISIBLE);
			mPreViewBtn.performClick();
		}

	}

	// 预览文件
	private void previewFile(String filename) {

		File sourceFile = new File(mContext.getExternalFilesDir(null), filename);

		if (mFileType == FILE_TYPE_TXT) {
			displayTXT(filename);
		} else if (mFileType == FILE_TYPE_PDF) {
			displayPdf(sourceFile);
		} else if (mFileType == FILE_TYPE_DOC) {
			displayDoc(sourceFile);
		} else if (mFileType == FILE_TYPE_EXCEL) {
			displayExcel(sourceFile);
		} else if (mFileType == FILE_TYPE_PIC) {
			displayPic(filename);
		} else {
			Toast.makeText(mContext, "系统不支持此文件预览", Toast.LENGTH_LONG).show();
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
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            
//			Uri uri = Uri.parse(sourceFile.getAbsolutePath());
//			Intent intent = new Intent(mHostActivity,MuPDFActivity.class);
//			intent.putExtra(MuPDFActivity.EXTRA_BOOLEAN_EDITABLE, true);
//			intent.setAction(Intent.ACTION_VIEW);
//			intent.setData(uri);
//			mContext.startActivity(intent);
//			mHostActivity.startActivity(intent);
//			String url = String.format(URLs.URL_APP_CONFIG, Constants.APP_CONFIG_PARAM_PDF_EDITABLE);
//			mRequestQueue.add(new StringRequest(url, new Listener<String>() {
//
//				@Override
//				public void onResponse(String response) {
//					JSONResult jr = JSONResult.compile(response);
//					if(jr.resultCode==JSONResult.RESULT_CODE_SUCCESS){
//						int editable = 0;
//						try {
//							editable = Integer.parseInt(jr.getResultMap().get(Constants.APP_CONFIG_PARAM_PDF_EDITABLE));
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//						Uri uri = Uri.parse(sourceFile.getAbsolutePath());
//						Intent intent = new Intent(mHostActivity,MuPDFActivity.class);
//						intent.putExtra(MuPDFActivity.EXTRA_BOOLEAN_EDITABLE, editable==0?false:true);
//						intent.setAction(Intent.ACTION_VIEW);
//						intent.setData(uri);
//						mContext.startActivity(intent);
//						mHostActivity.startActivity(intent);
//
//					}
//				}
//			}, new ErrorListener() {
//
//				@Override
//				public void onErrorResponse(VolleyError arg0) {
//					Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
//				}
//			}));
			
		} catch (Exception e) {
			e.printStackTrace();
			showInstallAppDialog("请安装PDF阅读器！");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		try {
//			String desPath = FileUtils.getAppExternalFilesStorageFile().getAbsolutePath()+"/document/"+new Date().getTime()+".pdf";
//			String src = getIntent().getData().toString();
//			FileUtils.copy(src,desPath );
//			LocalFile localFile = new LocalFile();
//			localFile.setName(src.substring(src.lastIndexOf("/")+1));
//			localFile.setPath(desPath);
//			localFile.setSize(new File(desPath).length());
//			localFile.setType(LocalFile.TYPE_DOCUMENT);
//			localFile.setSaveTime(new Date());
//			localFile.setSourcePath(src);
//			SugarRecord.save(localFile);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	// 将下载的文本显示
	private void displayTXT(String filename) {
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			File source = new File(mContext.getExternalFilesDir(""), filename);
			isr = new InputStreamReader(new FileInputStream(source), mTextCharSet == 1 ? "GBK" : "UTF-8");
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
	
	// 将下载的文本显示
	private void displayTXTByPath(String filePath) {
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			File source = new File(filePath);
			isr = new InputStreamReader(new FileInputStream(source), mTextCharSet == 1 ? "GBK" : "UTF-8");
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
	private void displayPic(String fileName) {
		File source = new File(mContext.getExternalFilesDir(""), fileName);

		setVisibilityOfViewByResId(mRootView, R.id.file_preview_info_con_ll, View.GONE);
		setVisibilityOfViewByResId(mRootView, R.id.file_preview_pre_btn, View.GONE);
		setVisibilityOfViewByResId(mRootView, R.id.file_preview_pic_div, View.VISIBLE);
		fillImageView(R.id.file_preview_pic_div, "file://" + source.getAbsolutePath());
	}

	//显示提示框之前首先获取服务端参数，如果已经配置好下载地址则允许用户点击下载按钮否则给出提示框即可
	private void showInstallAppDialog(final String message) {
		
		mSystemBussiness.aSyncAppConfig(mContext, new BussinessCallbackCommon<Object>() {

			@Override
			public void onException(int excepCode) {
				showAlertDialog(message);
			}

			@Override
			public void onDone(Object obj) {
				 if(TextUtils.isEmpty(MportalApplication.app.getDocumentReaderPageUrl())){
					 showAlertDialog(message);
				 }else{
					 showSelectiveDialog(message);
				 }
			}
		});

	}

	private void showAlertDialog(String message) {
		new AlertDialog(mContext, new OnOkClickListener() {

			@Override
			public void onclick() {

			}
		}, "提示信息", message, "确定").show();
	}
	
	private void showSelectiveDialog(String message){
		new ConfirmDialog(mContext, new ConfirmListener() {
			
			@Override
			public void onOkClick() {
				skip2DownloadPage();
			}
			
			@Override
			public void onCancelClick() {
				
			}
		}, message, "是否下载推荐文档阅读器？", "取消", "前往下载").show();
	}
	
	private void skip2DownloadPage(){
		mSystemBussiness.aSyncAppConfig(mContext, new BussinessCallbackCommon<Object>() {
			
			@Override
			public void onException(int excepCode) {
				Toast.makeText(mContext, "网络错误", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onDone(Object obj) {
				Bundle extra = new Bundle();
				extra.putString(WebAppFragment.ARGUMENT_STRING_URL, MportalApplication.app.getDocumentReaderPageUrl());
				ContainerActivity.startActivity(mContext, WebAppFragment.class,extra);
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mDownloadTask.cancel(true);
		LogM.log(getClass(), "onDestroy()");
	}
	
	public static boolean isAbleToRead(String url){
		
		if(url.endsWith(".pdf") || url.endsWith(".txt") || url.endsWith(".doc") || url.endsWith(".docx")||url.endsWith(".xls")||url.endsWith(".xlsx")||url.endsWith(".ppt")||url.endsWith(".pptx")){
			return true;
		}else{
			return false;
		}
	}

}
