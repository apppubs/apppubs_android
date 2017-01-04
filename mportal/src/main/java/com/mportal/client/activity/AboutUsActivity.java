package com.mportal.client.activity;
/**
 * 在branche中修改
 * 我在开发版本中正在开发
 */
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.mportal.client.R;
import com.mportal.client.view.MyWebChromeClient;
/**
 * 
 */
public class AboutUsActivity extends BaseActivity{
	private WebView mWeb;
	private LinearLayout progress;
	private String mUrl;
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i=getIntent();
		mUrl = i.getStringExtra(WebAppActivity.EXTRA_NAME_URL);
		setContentView(R.layout.act_mianze);
		init();
		
	}
	
	private void init() {
		setTitle("关于我们");
		progress=(LinearLayout) findViewById(R.id.mianze_progressBar);
		mWeb=(WebView) findViewById(R.id.mianze_webview);
		WebSettings webSettings = mWeb.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setAppCacheEnabled(false);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		// 设置WebView属性，能够执行Javascript脚本
		webSettings.setJavaScriptEnabled(true);
		//?clientid=D58&what=reginfo  //
	//	mWeb.loadUrl(URLs.URL_MIANZE+"clientid="+URLs.APP_CODE_DEFAULT+"&what=reginfo");
		mWeb.loadUrl(mUrl);
		//mWeb.loadUrl("http://202.85.221.113/wmh360/json/getappwebinfo.jsp?clientid=D58&what=reginfo"); 
		mWeb.setWebChromeClient(new MyWebChromeClient());
		mWeb.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progress.setVisibility(View.GONE);
			}
			@Override
			public void onLoadResource(WebView view, String url) {
				
				super.onLoadResource(view, url);
				System.out.println("加载资源 url"+url);
			}
			
			@SuppressLint("NewApi")
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view,
					String url) {
				System.out.println("拦截请求 url"+url);
				
				return null;
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				System.out.println("shouldOverrideUrlLoading url"+url);
				return super.shouldOverrideUrlLoading(view, url);
			}

		});
		
	}
	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

		default:
			break;
		}
	}
}
