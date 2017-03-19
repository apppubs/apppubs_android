package com.apppubs.d20.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.apppubs.d20.widget.MyWebChromeClient;
import com.apppubs.d20.R;

public class TemperatureCatFragment extends HomeFragment {
	private WebView mwebview;
	private LinearLayout progress;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left_webdu, null);
		mwebview=(WebView) view.findViewById(R.id.left_wendu);
		progress=(LinearLayout) view.findViewById(R.id.left_wendu_progress);
	    WebSettings webSettings = mwebview.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setAppCacheEnabled(false);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		// 设置WebView属性，能够执行Javascript脚本
		webSettings.setJavaScriptEnabled(true);
		mwebview.setWebChromeClient(new MyWebChromeClient());
		mwebview.setWebViewClient(new WebViewClient(){
			
			 @Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progress.setVisibility(View.GONE);
			}
		
		});
		return view;
	}
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
