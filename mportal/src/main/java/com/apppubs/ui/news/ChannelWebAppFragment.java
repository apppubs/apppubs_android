package com.apppubs.ui.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.apppubs.AppContext;
import com.apppubs.ui.home.HomeBaseActivity;
import com.apppubs.util.LogM;
import com.apppubs.d20.R;
import com.apppubs.util.SystemUtils;

public class ChannelWebAppFragment extends ChannelFragment implements OnClickListener {
	
	public static final String ARGUMENT_INT_MENUBARTYPE = "menu_bar_type";
	
	
	public static final int MENUBAR_STYLE_ALL = 0;
	public static final int MENUBAR_STYLE_BACK = 1;
	public static final int MENUBAR_STYLE_NONE = 2;
	
	private WebView mWebView;
	private View mRootView;
//	private FrameLayout menuBarAll;
//	private LinearLayout  menuBarBack;
	private WebSettings mSettings;
	
//	private View mBackV,mForwardV,mRefresh,mBrowser;
	
	private String mUrl;
	private int mMenuBarType;

	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.frg_webapp, null);

		initComponent(mRootView);
		initStates();

		return mRootView;
	}

	private void initComponent(View v) {
		Bundle args = getArguments();
		mMenuBarType = args.getInt(ChannelWebAppFragment.ARGUMENT_INT_MENUBARTYPE, 0);
		mUrl = mChannel.getLinkURL();
		mWebView = (WebView) mRootView.findViewById(R.id.webapp_wb);
//		menuBarAll = (FrameLayout) v.findViewById(R.id.bottom_menubar_four);
//		menuBarBack = (LinearLayout) v.findViewById(R.id.bottom_menubar_one);
		
	
		
	}

	private void initStates() {
		switch (mMenuBarType) {
		case MENUBAR_STYLE_ALL:
//			menuBarAll.setVisibility(View.VISIBLE);
//			menuBarBack.setVisibility(View.GONE);
//			mBackV = menuBarAll.findViewById(R.id.menubar_back);
//			mForwardV = menuBarAll.findViewById(R.id.menubar_forword);
//			mRefresh = menuBarAll.findViewById(R.id.menubar_refresh);
//			mBrowser = menuBarAll.findViewById(R.id.menubar_browser);
//			mBackV.setOnClickListener(this);
//			mForwardV.setOnClickListener(this);
//			mRefresh.setOnClickListener(this);
//			mBrowser.setOnClickListener(this);
			break;
		case MENUBAR_STYLE_BACK:
//			menuBarAll.setVisibility(View.GONE);
//			menuBarBack.setVisibility(View.VISIBLE);
//			mBackV = menuBarAll.findViewById(R.id.menubar_back);
//			mBackV.setOnClickListener(this);
			break;
		case MENUBAR_STYLE_NONE:
//			menuBarAll.setVisibility(View.GONE);
//			menuBarBack.setVisibility(View.GONE);
			break;
		}
		
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mWebView.loadUrl(url);
				return true;
			}
		});
		mWebView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				if (url != null && url.startsWith("http://"))
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			}
		});
		mSettings = mWebView.getSettings();
		mSettings.setJavaScriptEnabled(true);
		
		// 判断联网请求数据
		if (SystemUtils.canConnectNet(getActivity())) {
			mWebView.loadUrl(mUrl);
		} else {
			SystemUtils.showToast(getActivity(), "联网失败，请检查您的网络");
		}
	}

	@Override
	public void onClick(View v) {
		LogM.log(this.getClass(), "onClick");
		switch (v.getId()) {
		case R.id.menubar_back:
			if (mWebView.canGoBack()){
				mWebView.goBack();
			}else{
				if(!(mHostActivity instanceof HomeBaseActivity)){
					mHostActivity.finish();
				}
			}
			break;
		case R.id.menubar_forword:
			if (mWebView.canGoForward()) {
				mWebView.goForward();
			}else{
				SystemUtils.showToast(mHostActivity, "已经是最后一页");
			}
			break;
		case R.id.menubar_refresh:
			// 判断联网请求数据
			if (SystemUtils.canConnectNet(mHostActivity)) {
				mWebView.loadUrl(mUrl);
			} else {
				SystemUtils.showToast(getActivity(), "联网失败，请检查您的网络");
			}
			break;
		case R.id.menubar_browser:
			// 判断联网请求数据
			if (SystemUtils.canConnectNet(mHostActivity)) {
				Uri uri = Uri.parse(mUrl);
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			} else {
				SystemUtils.showToast(getActivity(), "联网失败，请检查您的网络");
			}
			break;
		}
	}

	@Override
	public void refresh() {
	}

}
