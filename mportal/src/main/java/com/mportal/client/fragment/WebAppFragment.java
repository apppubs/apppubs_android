package com.mportal.client.fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.widget.PopupWindow;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;

import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.activity.HomeBaseActivity;
import com.mportal.client.activity.ViewCourier;
import com.mportal.client.bean.MenuItem;
import com.mportal.client.constant.SystemConfig;
import com.mportal.client.util.Base64;
import com.mportal.client.util.BitmapUtils;
import com.mportal.client.util.LogM;
import com.mportal.client.util.SystemUtils;
import com.mportal.client.view.ProgressHUD;
import com.mportal.client.view.ProgressWebView;
import com.mportal.client.view.ProgressWebView.ProgressWebViewListener;
import com.mportal.client.view.TitleBar;
import com.mportal.jsbridge.BridgeHandler;
import com.mportal.jsbridge.CallBackFunction;
import com.mportal.jsbridge.DefaultHandler;
import com.mportal.multi_image_selector.MultiImageSelectorActivity;

public class WebAppFragment extends BaseFragment implements OnClickListener {

	public static final String ARGUMENT_INT_MENUBARTYPE = "menu_bar_type";
	public static final String ARGUMENT_STRING_URL = "url";
	public static final String ARGUMENT_STRING_MORE_MENUS = "more_menus";
	
	public static final int REQUEST_CODE_PICTURES = 100;
	
	private final String JS_MENU_ITEM_REFRESH = "menu_item_refresh";
	
	private String mUrl;
	private String mMoreMenusStr;

	private ProgressWebView mWebView;
	private View mRootView;
	private WebSettings mSettings;

	private PopupWindow mPopWin;
	private ProgressHUD mProgressHUD;

	boolean isCloseButtonAdded;
	private CallBackFunction mTmpHandelCallbackFunction;

	private String mOnloadingText = "载入中 ···";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		LogM.log(this.getClass(), "WebAppFragment-->onAttach");
		Bundle args = getArguments();
		mUrl = args.getString(ARGUMENT_STRING_URL);
		mMoreMenusStr = args.getString(ARGUMENT_STRING_MORE_MENUS);
		if (TextUtils.isEmpty(mMoreMenusStr)) {
			mMoreMenusStr = "0";
		}
		mUrl = SystemConfig.convertUrl(mUrl);
		mHostActivity.setShouldInterceptBackClick(true);
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.frg_webapp, null);

		initComponent(mRootView);
		initStates();

		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// 如果没有标题，则标题为“载入中。。。”
		if (mTitleBar!=null&&TextUtils.isEmpty(mTitleBar.getTitle())) {
			mTitleBar.setTitle(mOnloadingText);
		}

		if (!(mHostActivity instanceof HomeBaseActivity)&&mTitleBar!=null) {
			mTitleBar.setLeftBtnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!webviewGoBack()) {
						mHostActivity.finish();
					}
				}
			});
		}
		
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onResume() {
		super.onResume();

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK && webviewGoBack()) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	private void initComponent(View v) {

		mWebView = (ProgressWebView) mRootView.findViewById(R.id.webapp_wb);
		mWebView.setInitialScale(1);
		mWebView.setHostActivity(mHostActivity);
		
		mSettings = mWebView.getSettings();
		mSettings.setJavaScriptEnabled(true);
		mSettings.setAppCacheEnabled(true);
		mSettings.setBuiltInZoomControls(true);
		mSettings.setUseWideViewPort(true);
		mSettings.setSupportZoom(true);
		mSettings.setDomStorageEnabled(true);

		if (android.os.Build.VERSION.SDK_INT >= 11) {

			mSettings.setDisplayZoomControls(false);

		}
		mWebView.setDefaultHandler(new DefaultHandler());
		mWebView.setListener(new ProgressWebViewListener() {

			@Override
			public void onURLClicked(String url) {

			}

			@Override
			public void onReceiveTitle(String title) {
				// 之前如果没有设置过标题则将网页的title当做标题
				if (mTitleBar!=null&&(TextUtils.isEmpty(mTitleBar.getTitle()) || mTitleBar.getTitle().equals(mOnloadingText))) {
					mTitleBar.setTitle(title);
				}

			}

			@Override
			public void onFinished() {
				if (mTitleBar!=null&&mTitleBar.getTitle().equals(mOnloadingText)) {
					mTitleBar.setTitle("");
				}
			}
		});
		

		mWebView.registerHandler("getUserInfo", new BridgeHandler() {
	        @Override
	        public void handler(String data, CallBackFunction function) {
	        	LogM.log(WebAppFragment.class, "getUserInfo");
	            function.onCallBack(MportalApplication.user.getUserId());
	        }
	    });
		
		mWebView.registerHandler("hideMenuItems", new BridgeHandler() {
	        @Override
	        public void handler(String data, CallBackFunction function) {
	        	System.out.println("hideMenuItems"+data);
				if(!TextUtils.isEmpty(data)&&!TextUtils.isEmpty(mMoreMenusStr)&&data.contains(JS_MENU_ITEM_REFRESH)){
					mMoreMenusStr = mMoreMenusStr.replaceAll("0", "").replaceAll(",0", "").replaceAll("0", "");
				}
				mWebView.post(new Runnable() {
					
					@Override
					public void run() {
						changeActivityTitleView(mTitleBar);
					}
				});
	        }
	    });
		mWebView.registerHandler("closeWindow", new BridgeHandler() {
	        @Override
	        public void handler(String data, CallBackFunction function) {
	        	
	        	mHostActivity.finish();
	            function.onCallBack("请求结束");
	        }
	    });
		
		mWebView.registerHandler("openWindow", new BridgeHandler() {
	        @Override
	        public void handler(String data, CallBackFunction function) {
	        	ViewCourier.execute(mHostActivity, data);
	            function.onCallBack("请求结束");
	        }
	    });
		//选择图片
		mWebView.registerHandler("chooseImage", new BridgeHandler() {
	        

			@Override
	        public void handler(String data, CallBackFunction function) {
	        	mTmpHandelCallbackFunction = function;
	        	Intent intent = new Intent(mHostActivity, MultiImageSelectorActivity.class);
	        	startActivityForResult(intent, REQUEST_CODE_PICTURES);
	        }
	    });
		//获取设备id
		mWebView.registerHandler("getDeviceId", new BridgeHandler() {

			@Override
	        public void handler(String data, CallBackFunction function) {
				function.onCallBack(MportalApplication.getMachineId());
	        }

	    });
		//扫描二维码
		mWebView.registerHandler("scanQRCode", new BridgeHandler() {
			
			@Override
			public void handler(String data, CallBackFunction function) {
				ViewCourier.execute(mHostActivity, "apppubs://qrcode");
			}
			
		});
		//切换app
		mWebView.registerHandler("changeApp", new BridgeHandler() {
	        @Override
	        public void handler(String data, CallBackFunction function) {
	        	final String[] strArr = data.split(",");
	        	
				mWebView.post(new Runnable() {
					
					@Override
					public void run() {
						MportalApplication app = (MportalApplication) mHostActivity.getApplication();
						app.showChangeDialog(mContext, strArr[0], strArr[1]);
						changeActivityTitleView(mTitleBar);
					}
				});
	        }
	    });
		
		//分享
		mWebView.registerHandler("share", new BridgeHandler() {
	        @Override
	        public void handler(String data, CallBackFunction function) {
	        	ShareSDK.initSDK(mContext);
	        	ShareParams sp = new ShareParams();
	        	sp.setText("测试分享的文本");
//	        	sp.setImagePath(“/mnt/sdcard/测试分享的图片.jpg”);

	        	Platform qq = ShareSDK.getPlatform(Wechat.NAME);
//	        	qq.setPlatformActionListener(null); // 设置分享事件回调
	        	// 执行图文分享
	        	qq.share(sp);
	        }
	    });
	}

	@Override
	public void changeActivityTitleView(TitleBar titleBar) {
		super.changeActivityTitleView(titleBar);
		if(titleBar!=null){
			titleBar.reset();
			titleBar.setTitle(titleBar.getTitle()+"");
			if (mHostActivity instanceof HomeBaseActivity) {
				isCloseButtonAdded = true;// 避免出现关闭
//			titleBar.addLeftBtnWithImageResourceIdAndClickListener(R.drawable.top_back_btn, new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					webviewGoBack();
//				}
//			});
			}
			
			
			if (mMoreMenusStr != null && !mMoreMenusStr.equals("") && mMoreMenusStr.split(",").length > 1) {
				titleBar.addRightBtnWithImageResourceIdAndClickListener(R.drawable.title_more, new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						openMenu();
					}
				});
			} else if (mMoreMenusStr != null && !mMoreMenusStr.equals("") && mMoreMenusStr.split(",").length == 1) {
				if (mMoreMenusStr.equals(MenuItem.WEB_APP_MENU_REFRESH + "")) {
					titleBar.addRightBtnWithImageResourceIdAndClickListener(R.drawable.titlebar_refresh, new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							refresh();
						}
					});
				} else if (mMoreMenusStr.equals(MenuItem.WEB_APP_MENU_SHARE + "")) {
					titleBar.addRightBtnWithTextAndClickListener("分享", new OnClickListener() {
						
						@Override
						public void onClick(View v) {
						}
					});
				} else if (mMoreMenusStr.equals(MenuItem.WEB_APP_MENU_OPEN_WITH_BROWSER + "")) {
					titleBar.addRightBtnWithTextAndClickListener("浏览器打开", new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							openInBrowser();
						}
					});
				}
			}
		}
	}

	private void openMenu() {

		View menuPop = LayoutInflater.from(mHostActivity).inflate(R.layout.pop_web_menu, null);

		mPopWin = new PopupWindow(menuPop, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopWin.setFocusable(true);
		mPopWin.setOutsideTouchable(true);
		mPopWin.setBackgroundDrawable(new BitmapDrawable());
		mPopWin.showAsDropDown(mTitleBar.getRightView());

		String[] arr = mMoreMenusStr.split(",");
		for (String s : arr) {

			int menuIntValue = Integer.parseInt(s);
			switch (menuIntValue) {
			case MenuItem.WEB_APP_MENU_REFRESH:
				setVisibilityOfViewByResId(menuPop, R.id.pop_ref_ll, View.VISIBLE);
				break;
			case MenuItem.WEB_APP_MENU_OPEN_WITH_BROWSER:
				setVisibilityOfViewByResId(menuPop, R.id.pop_browser_ll, View.VISIBLE);
				break;
			case MenuItem.WEB_APP_MENU_SHARE:
				setVisibilityOfViewByResId(menuPop, R.id.pop_share_ll, View.VISIBLE);
				break;
			default:
				break;
			}
		}
		View refV = menuPop.findViewById(R.id.pop_ref_ll);
		View brsV = menuPop.findViewById(R.id.pop_browser_ll);
		refV.setOnClickListener(this);
		brsV.setOnClickListener(this);
	}

	private void initStates() {

		mWebView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
					long contentLength) {
				if (url != null && url.startsWith("http://"))
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			}
		});

		// 判断联网请求数据
		if (SystemUtils.canConnectNet(getActivity())) {
			mWebView.loadUrl(mUrl);
		} else {
			SystemUtils.showToast(getActivity(), "联网失败，请检查您的网络");
		}
	}

	public boolean webviewGoBack() {

		String curUrl = mWebView.getUrl();
		LogM.log(this.getClass(), curUrl + "<->" + mUrl);

		boolean result = false;
		String previousUrl = null;
		try {
			previousUrl = extractPreviousFromUrl(curUrl);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (previousUrl != null && previousUrl.equals("main")) {
			result = false;
		} else if (previousUrl != null) {
			result = true;
			mWebView.loadUrl(previousUrl);
		} else if (mWebView.canGoBack()) {
			if (!isCloseButtonAdded) {
				addClose();
			}
			mWebView.goBack();
			result = true;
		}

		return result;
	}

	private String extractPreviousFromUrl(String url) throws UnsupportedEncodingException {

		Pattern pattern = Pattern.compile("previousurl=([^&?]*)");
		Matcher m = pattern.matcher(url);
		while (m.find()) {
			return URLDecoder.decode(m.group(1), "utf-8");
		}
		return null;
	}

	private void addClose() {
		// 当主界面跳转时判断此button是否需要，如果需要才显示，判断需要的方式则为此button是否被加载过
		// 根据此fragment嵌入的activity不同，显示不同的样式
		mTitleBar.addLeftBtnWithImageResourceIdAndClickListener(R.drawable.close_circle, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mHostActivity.finish();

			}
		});
	}

	public void refresh() {
		if (SystemUtils.canConnectNet(mHostActivity)) {
			mWebView.reload();
		} else {
			SystemUtils.showToast(getActivity(), "联网失败，请检查您的网络");
		}
	}

	public void openInBrowser() {
		// 判断联网请求数据
		if (SystemUtils.canConnectNet(mHostActivity)) {
			Uri uri = Uri.parse(mWebView.getUrl());
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(it);
		} else {
			SystemUtils.showToast(getActivity(), "联网失败，请检查您的网络");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.pop_ref_ll:
			this.refresh();
			mPopWin.dismiss();
			break;
		case R.id.pop_browser_ll:
			this.openInBrowser();
			mPopWin.dismiss();
			break;
		default:
			break;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onPause() {
		super.onPause();
	}

	@SuppressLint("NewApi")
	@Override
	public void onStop() {
		super.onStop();
//		mWebView.stopLoading();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogM.log(this.getClass(), "onActivityResult");
		
		
		if(requestCode==REQUEST_CODE_PICTURES&&resultCode==Activity.RESULT_OK){
			mProgressHUD = ProgressHUD.show(mContext,null, true, false, null);
			
			List<String> selectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (String p : selectPath) {
            	Bitmap bitmap = BitmapUtils.convertToBitmap(p, 800, 800);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String base64 = Base64.encode(byteArray);
                if(sb.length()>1){
                	sb.append(",\""+base64+"\"");
                }else{
                	sb.append("\""+base64+"\"");
                }
                bitmap = null;
                try {
                	if(stream!=null){
                		stream.close();
                	}
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            sb.append("]");
			mTmpHandelCallbackFunction.onCallBack( sb.toString());
			mProgressHUD.hide();
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		mWebView.destroy();
	}

}
