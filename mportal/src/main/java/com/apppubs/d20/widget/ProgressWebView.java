package com.apppubs.d20.widget;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.apppubs.d20.fragment.WebAppFragment;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.R;
import com.apppubs.d20.SkipActivity;
import com.apppubs.d20.activity.BaseActivity;
import com.apppubs.d20.activity.ContainerActivity;
import com.apppubs.d20.activity.HomeBaseActivity;
import com.apppubs.d20.activity.ViewCourier;
import com.apppubs.d20.constant.Constants;
import com.apppubs.d20.fragment.FilePreviewFragment;
import com.apppubs.d20.util.HttpRequestParser;
import com.apppubs.d20.util.WebUtils;
import com.apppubs.d20.widget.AlertDialog.OnOkClickListener;
import com.apppubs.jsbridge.BridgeUtil;
import com.apppubs.jsbridge.BridgeWebView;
import com.apppubs.jsbridge.Message;

@SuppressWarnings("deprecation")
public class ProgressWebView extends BridgeWebView {

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	
	
	private Context mContext;
	private BaseActivity mHostActivity;
	private ProgressBar progressbar;
	private ProgressWebViewListener mListener;
	private ProgressDialog mProgressDialog;
	private AsyncTask mCurrentTask;
	private CountDownTimer mCounterDownTimer;
	

	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
		Drawable drawable = null;
		/*
		 * int theme = mAppContext.getSettings().getTheme(); switch
		 * (theme) { case Settings.THEME_BLUE:
		 * 
		 * drawable = context.getResources().getDrawable(
		 * R.drawable.progress_blue); break; case Settings.THEME_RED: drawable =
		 * context.getResources().getDrawable( R.drawable.progress_red); break;
		 * case Settings.THEME_INDIGO: drawable =
		 * context.getResources().getDrawable( R.drawable.progress_indigo);
		 * break; case Settings.THEME_BROWN: drawable =
		 * context.getResources().getDrawable( R.drawable.progress_brown);
		 * break; }
		 */
		// 全部用绿色
		drawable = context.getResources().getDrawable(R.drawable.progress_green);
		progressbar.setProgressDrawable(drawable);
		int height = context.getResources().getDimensionPixelSize(R.dimen.progress_webview_progress_height);
		progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height, 0, 0));
		addView(progressbar);
		setWebChromeClient(new WebChromeClient());
		setWebViewClient(new MyWebViewClient());

		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("正在下载文件..");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				mCurrentTask.cancel(true);
			}
		});
		// mProgressDialog.show();

		if (mCounterDownTimer==null){
			mCounterDownTimer = new CountDownTimer(5*1000,1000) {
				@Override
				public void onTick(long millisUntilFinished) {
				}

				@Override
				public void onFinish() {
					Toast.makeText(mContext,"请检查网络状态(或者VPN是否连接)",Toast.LENGTH_LONG).show();
				}
			};
		}
		mCounterDownTimer.start();
	}

	public void cancelNetworkError(){
		mCounterDownTimer.cancel();
	}

	public void setHostActivity(BaseActivity activity){
		this.mHostActivity = activity;
	}
	
	public class WebChromeClient extends android.webkit.WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				progressbar.setVisibility(GONE);
			} else {
				if (progressbar.getVisibility() == GONE)
					progressbar.setVisibility(VISIBLE);
				progressbar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			if (mListener != null) {
				mListener.onReceiveTitle(title);
			}
		}

		// 处理Alert事件
		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

			Dialog dialog = new AlertDialog(view.getContext(), new OnOkClickListener() {

				@Override
				public void onclick() {
					result.confirm();
				}
			}, "提示信息", message, "确定");
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

			return true;

		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {

			new ConfirmDialog(view.getContext(), new ConfirmDialog.ConfirmListener() {

				@Override
				public void onOkClick() {
					result.confirm();
				}

				@Override
				public void onCancelClick() {
					result.cancel();
				}
			}, message, "取消", "确定").show();
			return true;
		}
		
	      //Eclipse will swear at you if you try to put @Override here  
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {  

        	mHostActivity.setUploadMessage(uploadMsg);
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
            i.addCategory(Intent.CATEGORY_OPENABLE);  
            i.setType("image/*");  
            mHostActivity.startActivityForResult(Intent.createChooser(i,"File Chooser"), HomeBaseActivity.FILECHOOSER_RESULTCODE);  

           }

        // For Android 3.0+
           public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
           mHostActivity.setUploadMessage(uploadMsg);
           Intent i = new Intent(Intent.ACTION_GET_CONTENT);
           i.addCategory(Intent.CATEGORY_OPENABLE);
           i.setType("*/*");
           mHostActivity.startActivityForResult(
           Intent.createChooser(i, "File Browser"),
           BaseActivity.FILECHOOSER_RESULTCODE);
           }

        //For Android 4.1
           public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
               mHostActivity.setUploadMessage(uploadMsg);
               Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
               i.addCategory(Intent.CATEGORY_OPENABLE);  
               i.setType("image/*");  
               mHostActivity.startActivityForResult( Intent.createChooser( i, "File Chooser" ), BaseActivity.FILECHOOSER_RESULTCODE );

           }


	}

//	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
//		return false;
//
//	};

	public class MyWebViewClient extends WebViewClient {
		@SuppressLint("NewApi") 
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
			LogM.log(ProgressWebView.class, "当前webview访问url:"+url);
			
			WebResourceResponse response = null;
			if(url!=null&&url.contains("www.apppubs.com/fonts")){
				String file = url.substring(url.indexOf("www.apppubs.com"), url.length()).replaceAll("www.apppubs.com/", "");
				try {
					InputStream is = view.getContext().getAssets().open(file);
					response = new WebResourceResponse("text/html", "utf-8", is);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}  
			}else{
				response = super.shouldInterceptRequest(view, url);
			}
			return response;
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			// super.onReceivedError(view, errorCode, description, failingUrl);
			view.stopLoading();
			// view.clearView();
			LogM.log(this.getClass(), "progressWebView-->出现异常错误errorCode" + errorCode);
			// if(errorCode/100%4==0){
			// LogM.log(this.getClass(), "progressWebView-->出现4xx异常错误");
			// loadUrl("file:///android_asset/web/error.html");
			// }
			// if(errorCode/100%5==0){
			// LogM.log(this.getClass(), "progressWebView-->出现5xx异常错误");
			// loadUrl("file:///android_asset/web/500.html");
			// } error.html
			// loadUrl("file:///android_asset/web/error.html");
			// Message msg=handler.obtainMessage();//发送通知，加入线程
			// msg.what=1;//通知加载自定义404页面
			// handler.sendMessage(msg);//通知发送！
		}
		
		boolean isLoaded = false;
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			if (mCounterDownTimer!=null){
				mCounterDownTimer.cancel();
			}

			isLoaded = true;
			if (mListener != null) {
				mListener.onFinished();
			}
			
	        if (ProgressWebView.this.getStartupMessage() != null) {
	            for (Message m : ProgressWebView.this.getStartupMessage()) {
	            	ProgressWebView.this.dispatchMessage(m);
	            }
	            ProgressWebView.this.setStartupMessage(null);
	        }
		}

		

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			LogM.log(this.getClass(), "shouldOverrideUrlLoading-->url" + url);
			mCounterDownTimer.start();
	        try {
	            url = URLDecoder.decode(url, "UTF-8");
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }

			if (url.indexOf("getaffixclick") > -1) {

				// 国投附件处理
				String[] arr = url.split(":");
				if (arr.length > 1 && arr[1].equals("getaffixclick")) {
					//获取当前协议和主机地址
					Pattern pattern = Pattern.compile("http://[^/]+");
					Matcher matcher = pattern.matcher(view.getUrl());
					matcher.find();
					
					final String affixname = matcher.group() + arr[2];
					new AsyncTask<String, Integer, String>() {

						@Override
						protected String doInBackground(String... arg0) {
							String pdfurl = null;
							try {
								pdfurl = WebUtils.requestWithGet(affixname);
							} catch (IOException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							return pdfurl;
						}

						@Override
						protected void onPostExecute(String result) {
							// mCurrentTask = new
							// DownloadFileAsync().execute(result);
							openFileWithUrl(result);
						}

					}.execute("");

				}
				return true;

			} else if (url.indexOf("getaffix") > -1) {
				System.out.println("oa----url-----" + url);
				// showDialog("url--1--"+url);
				HttpRequestParser.Request request = HttpRequestParser.parse(url);

				String fileid = request.getParameter("fileid");
				String filename = request.getParameter("filename");
				String tablename = request.getParameter("tablename");
				String fieldname = request.getParameter("fieldname");
				// userid = request.getParameter("userid");
				// workid = request.getParameter("workid");
				String httpserver = request.getBaseRequestURL();
				// System.out.println("httpserver----"+httpserver);
				// System.out.println("workid----"+workid);
				// System.out.println("userid----"+userid);
				// System.out.println("fileid----"+fileid);
				// System.out.println("filename----"+filename);
				// 开始生成pdf文件，请求远程的地址得到返回值
				final String genpdfurl = httpserver + "/emobile/oamobile/showaffix.jsp?fileid=" + fileid + "&filename="
						+ filename + "&tablename=" + tablename + "&fieldname=" + fieldname;
				// System.out.println("genpdfurl----"+genpdfurl);

				new AsyncTask<String, Integer, String>() {

					@Override
					protected String doInBackground(String... arg0) {
						String pdfurl = null;
						try {
							pdfurl = WebUtils.requestWithGet(genpdfurl);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						return pdfurl;
					}

					@Override
					protected void onPostExecute(String result) {
						// new DownloadFileAsync().execute(result);
						openFileWithUrl(result);
					}

				}.execute("");

				// System.out.println("pdfurl----"+pdfurl);
				// downloadurl = pdfurl;
				// startDownload(downloadurl);

				// return super.shouldOverrideUrlLoading(view, url);

				return true;

			} else if (url.indexOf("getgd") > -1) { // 是gd格式的附件,国华gd格式
				HttpRequestParser.Request request = HttpRequestParser.parse(url);

				String fileid = request.getParameter("fileid");
				String filename = request.getParameter("filename");
				String httpserver = request.getBaseRequestURL();
				// 开始生成pdf文件，请求远程的地址得到返回值
				final String gentxturl = httpserver + "/emobile/oamobile/gdtotxt.jsp?fileid=" + fileid + "&filename="
						+ filename;

				new AsyncTask<String, Integer, String>() {

					@Override
					protected String doInBackground(String... arg0) {
						String pdfurl = null;
						try {
							pdfurl = WebUtils.requestWithGet(gentxturl);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						return pdfurl;
					}

					@Override
					protected void onPostExecute(String result) {
						System.out.println("生成的密码" + result);
						if (result.indexOf(".txt") < 0) {

							showDialog("书生格式转换失败,请确认书生转换程序正常启用!");
						} else {

							openFileWithUrl(result);
						}
					}

				}.execute("");

				return true;

			} else if (FilePreviewFragment.isAbleToRead(url)) {
				openFileWithUrl(url);
				mCounterDownTimer.cancel();
				return true;
			} else if (url.contains("target=_blank")) {
				
				Bundle args = new Bundle();
				args.putString(WebAppFragment.ARGUMENT_STRING_URL, url);
				if(url.contains("titlebarflag=0")){
					args.putBoolean(BaseActivity.EXTRA_BOOLEAN_NEED_TITLEBAR, false);
				}
				ContainerActivity.startActivity(mContext, WebAppFragment.class, args);
				mCounterDownTimer.cancel();
				return true;
			}else if(url.startsWith(Constants.CUSTOM_SCHEMA_APPPUBS_NEWS+"://")){
				Intent skipIntent = new Intent(mContext,SkipActivity.class);
				skipIntent.setData(Uri.parse(url));
				mContext.startActivity(skipIntent);
				return true;
			} else if(url.startsWith(Constants.CUSTOM_SCHEMA_APPPUBS+"://")){
				ViewCourier.execute(mHostActivity, url);
				return true;
			}else if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
	            handlerReturnData(url);
	            return true;
	        } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
	        	flushMessageQueue();
	            return true;
	        } else if (mListener != null) {
				mListener.onURLClicked(url);
			}

			if(!url.startsWith("http://")&&!url.startsWith("https")){
				try {
					final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					mContext.startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
			return false;
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();// 接受证书
			// handleMessage(Message msg); 其他处理
		}

	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
		lp.x = l;
		lp.y = t;
		progressbar.setLayoutParams(lp);
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public void setListener(ProgressWebViewListener listener) {
		mListener = listener;

	}

	public interface ProgressWebViewListener {
		public void onURLClicked(String url);

		public void onReceiveTitle(String title);

		public void onFinished();
	}

	private void openFileWithUrl(String url) {
		url = url.replaceAll("\r|\n", "");

		Bundle args = new Bundle();
		args.putString(FilePreviewFragment.ARGS_STRING_URL, url);
//		LocalFile localFile = SugarRecord.findByProperty(LocalFile.class, "source_path", url);
//		if(localFile!=null){
//			args.putString(FilePreviewFragment.ARGS_STRING_FILE_LOCAL_PATH, localFile.getSourcePath());
//		}
		// args.putInt(FilePreviewFragment.ARGS_INTEGER_FILE_TYPE,
		// FilePreviewFragment.FILE_TYPE_TXT);
		if (url.endsWith(".txt")) {
			args.putInt(FilePreviewFragment.ARGS_TEXT_CHARSET, FilePreviewFragment.TEXT_CHARSET_CBK);
		}
		ContainerActivity.startActivity(mContext, FilePreviewFragment.class, args, "文件预览");
	}

	private void showDialog(String message) {
		new AlertDialog(mContext, new OnOkClickListener() {

			@Override
			public void onclick() {

			}
		}, "提示信息", message, "确定").show();

	}
}
