package com.apppubs.d20.webapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.widget.PopupWindow;

import com.alipay.sdk.app.PayTask;
import com.amap.api.location.AMapLocation;
import com.apppubs.d20.AppContext;
import com.apppubs.d20.AppManager;
import com.apppubs.d20.MportalApplication;
import com.apppubs.d20.R;
import com.apppubs.d20.activity.ContainerActivity;
import com.apppubs.d20.activity.HomeBaseActivity;
import com.apppubs.d20.activity.ViewCourier;
import com.apppubs.d20.bean.MenuItem;
import com.apppubs.d20.fragment.BaseFragment;
import com.apppubs.d20.myfile.FilePreviewFragment;
import com.apppubs.d20.util.Base64;
import com.apppubs.d20.util.BitmapUtils;
import com.apppubs.d20.util.LocationManager;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.SystemUtils;
import com.apppubs.d20.widget.ProgressHUD;
import com.apppubs.d20.widget.ProgressWebView;
import com.apppubs.d20.widget.ProgressWebView.ProgressWebViewListener;
import com.apppubs.d20.widget.TitleBar;
import com.apppubs.jsbridge.BridgeHandler;
import com.apppubs.jsbridge.BridgeWebView;
import com.apppubs.jsbridge.CallBackFunction;
import com.apppubs.jsbridge.DefaultHandler;
import com.apppubs.multi_image_selector.MultiImageSelectorActivity;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class WebAppFragment extends BaseFragment implements OnClickListener, IWebAppView {

	public static final String ARGUMENT_INT_MENUBARTYPE = "menu_bar_type";
	public static final String ARGUMENT_STRING_URL = "url";
	public static final String ARGUMENT_STRING_MORE_MENUS = "more_menus";

	public static final int REQUEST_CODE_PICTURES = 100;

	private final String JS_MENU_ITEM_REFRESH = "menu_item_refresh";

	private static final int SDK_PAY_FLAG = 1;

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

	private WebAppPresenter mPresenter;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_PAY_FLAG: {
					JSONObject json = new JSONObject((Map<String, String>) msg.obj);
					mTmpHandelCallbackFunction.onCallBack(json.toString());
					break;
				}

				default:
					break;
			}
		}

		;
	};

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
		mUrl = AppContext.getInstance(mContext).convertUrl(mUrl);
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
		initPresenter();
		mPresenter.onCreateView();
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// 如果没有标题，则标题为“载入中。。。”
		if (mTitleBar != null && TextUtils.isEmpty(mTitleBar.getTitle())) {
			mTitleBar.setTitle(mOnloadingText);
		}

		if (!(mHostActivity instanceof HomeBaseActivity) && mTitleBar != null) {
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

	private void initPresenter() {
		mPresenter  = new WebAppPresenter(getContext(),this);
	}

	@SuppressLint({"NewApi", "SetJavaScriptEnabled"})
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
				if (mTitleBar != null && !TextUtils.isEmpty(title)) {
					mTitleBar.setTitle(title);
				}

			}

			@Override
			public void onFinished() {
				if (mTitleBar != null && mTitleBar.getTitle().equals(mOnloadingText)) {
					mTitleBar.setTitle("");
				}
			}
		});


		mWebView.registerHandler("getUserInfo", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				LogM.log(WebAppFragment.class, "getUserInfo");

				function.onCallBack(AppContext.getInstance(mContext).getCurrentUser().getUserId());
			}
		});

		mWebView.registerHandler("hideMenuItems", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				System.out.println("hideMenuItems" + data);
				if (!TextUtils.isEmpty(data) && !TextUtils.isEmpty(mMoreMenusStr) && data.contains(JS_MENU_ITEM_REFRESH)) {
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
				ViewCourier.getInstance(mHostActivity).execute(mHostActivity, data);
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
				ViewCourier.getInstance(mHostActivity).execute(mHostActivity, "apppubs://qrcode");
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
						AppManager.getInstant(mContext).showChangeDialog(mContext, strArr[0], strArr[1]);
						changeActivityTitleView(mTitleBar);
					}
				});
			}
		});

		//支付宝支付
		mWebView.registerHandler("alipay", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				mTmpHandelCallbackFunction = function;
				final String[] strArr = data.split(",");
				LogM.log(this.getClass(), "支付宝进行宝支付");
				try {
					JSONObject jo = new JSONObject(data);
					awakenAlipay(jo.getString("orderstr"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mWebView.post(new Runnable() {

					@Override
					public void run() {

					}
				});
			}
		});

		//微信支付
		mWebView.registerHandler("wxpay", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				mTmpHandelCallbackFunction = function;
				LogM.log(this.getClass(), "微信支付");
				try {
					awakeWxpay(data);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				mWebView.post(new Runnable() {

					@Override
					public void run() {

					}
				});
			}
		});

		mWebView.registerHandler("amap", new BridgeHandler() {
			@Override
			public void handler(String data, final CallBackFunction function) {
				System.out.println("amap点击");
				LocationManager manager = LocationManager.getInstance(mContext);
				manager.setListener(new LocationManager.LocationListener() {
					@Override
					public void onLocationChanded(AMapLocation location) {
						String data = "{\"latitude\":" + location.getLatitude() + ",\"longtitude\":" + location.getLongitude() + "}";
						System.out.println(data);
						function.onCallBack(data);
					}
				});
				manager.requestLocation();
			}
		});

		//分享
		mWebView.registerHandler("share", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {

				try {
					JSONArray arr = new JSONArray(data);

					ShareParams sp = new ShareParams();
					sp.setCustomFlag(new String[]{getString(R.string.app_name)});

					ShareSDK.initSDK(mContext);
					String type = arr.getString(0);


					if ("wechat".equals(type)) {
						if (arr.length() > 1) {
							String msg = arr.getString(1);
							sp.setText(msg);
						}
						Platform p = ShareSDK.getPlatform(Wechat.NAME);
						p.share(sp);
					} else if ("wechat_timeline".equals(type)) {
						if (arr.length() > 1) {
							String msg = arr.getString(1);
							sp.setText(msg);
						}
						Platform p = ShareSDK.getPlatform(WechatMoments.NAME);
						Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
						sp.setImageData(bmp);
						p.share(sp);
					} else if ("qq".equals(type)) {
						if (arr.length() > 1) {
							String msg = arr.getString(1);
							sp.setText(msg);
						}
						if (arr.length() > 2) {
							sp.setTitleUrl(arr.getString(2));
							sp.setUrl(arr.getString(2));
						}
						sp.setShareType(Platform.SHARE_TEXT);
						Platform p = ShareSDK.getPlatform(QQ.NAME);
						p.share(sp);
					} else if ("sms".equals(type)) {
						if (arr.length() > 1) {
							String msg = arr.getString(1);
							sp.setText(msg);
						}
						Platform p = ShareSDK.getPlatform(ShortMessage.NAME);
						p.share(sp);
					}


				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void awakenAlipay(final String orderStr) {

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(getActivity());
				Map<String, String> result = alipay.payV2(orderStr, true);
				Log.i("msp", result.toString());

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	private void awakeWxpay(String content) throws JSONException {
		JSONObject json = new JSONObject(content);
		if (null != json && !json.has("retcode")) {
			PayReq req = new PayReq();
			//req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
			req.appId = json.getString("appid");
			req.partnerId = json.getString("partnerid");
			req.prepayId = json.getString("prepayid");
			req.nonceStr = json.getString("noncestr");
			req.timeStamp = json.getString("timestamp");
			req.packageValue = json.getString("package");
			req.sign = json.getString("sign");
			req.extData = "app data"; // optional
			// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
			IWXAPI api = WXAPIFactory.createWXAPI(mContext, req.appId);
			api.sendReq(req);
		} else {
			Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
		}
	}

	@Override
	public void changeActivityTitleView(TitleBar titleBar) {
		super.changeActivityTitleView(titleBar);
		if (titleBar != null) {
			titleBar.reset();
			titleBar.setTitle(titleBar.getTitle() + "");
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

				String downloadUrl = url;
				CookieManager cookieManager = CookieManager.getInstance();
				String cookieStr = cookieManager.getCookie(url);
				if (!TextUtils.isEmpty(cookieStr)) {
					if (url.contains("?")) {
						downloadUrl += "&" + cookieStr;
					} else {
						downloadUrl += "?" + cookieStr;
					}
				}
				Bundle args = new Bundle();


				String fileName = fetchFileName(contentDisposition, url);
				if (!TextUtils.isEmpty(fileName)) {
					args.putString(FilePreviewFragment.ARGS_FILE_NAME, fileName);
				}
				args.putString(FilePreviewFragment.ARGS_STRING_URL, url);
				args.putString(ContainerActivity.EXTRA_STRING_TITLE, "文件预览");
				args.putString(FilePreviewFragment.ARGS_STRING_MIME_TYPE, mimetype);
				ContainerActivity.startActivity(getContext(), FilePreviewFragment.class, args);
			}

			private String fetchFileName(String contentDisposition, String url) {

				if (!TextUtils.isEmpty(contentDisposition)) {
					Pattern pattern = Pattern.compile("filename=\"(.*?)\"", Pattern.DOTALL);
					Matcher matcher = pattern.matcher(contentDisposition);
					while (matcher.find()) {
						String fileName = null;
						if (!TextUtils.isEmpty(fileName = matcher.group(1))) {
							try {
								fileName = URLDecoder.decode(fileName, "utf-8");
								return fileName;
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					try {
						URL urlO = new URL(url);
						if (!TextUtils.isEmpty(urlO.getFile())) {
							return urlO.getFile().substring(urlO.getFile().lastIndexOf("/") + 1);
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}

				return null;
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


		if (requestCode == REQUEST_CODE_PICTURES && resultCode == Activity.RESULT_OK) {
			mProgressHUD = ProgressHUD.show(mContext, null, true, false, null);

			List<String> selectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (String p : selectPath) {
				Bitmap bitmap = BitmapUtils.convertToBitmap(p, 800, 800);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				String base64 = Base64.encode(byteArray);
				if (sb.length() > 1) {
					sb.append(",\"" + base64 + "\"");
				} else {
					sb.append("\"" + base64 + "\"");
				}
				bitmap = null;
				try {
					if (stream != null) {
						stream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			sb.append("]");
			mTmpHandelCallbackFunction.onCallBack(sb.toString());
			mProgressHUD.hide();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mWebView.destroy();
		mWebView.cancelNetworkError();
	}

	//IWebAppView
	@Override
	public BridgeWebView getBridgeWebView() {
		return mWebView;
	}
}
