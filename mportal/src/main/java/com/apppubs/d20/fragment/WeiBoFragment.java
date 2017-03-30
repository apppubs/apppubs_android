package com.apppubs.d20.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.apppubs.d20.bean.WeiboInfo;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.SystemUtils;
import com.apppubs.d20.widget.ProgressWebView;
import com.apppubs.d20.widget.Tabs;
import com.apppubs.d20.R;
import com.apppubs.d20.model.BussinessCallbackCommon;
import com.apppubs.d20.model.SystemBussiness;

public class WeiBoFragment extends BaseFragment implements OnPageChangeListener ,OnClickListener{

	
	private View mRootView;
	private ProgressBar mprogress;
	private ViewPager viewparge;
	private WeiboPargeAdapter adapter;
	private List<WeiboInfo> minfos;
	private WebView mCurWebView;
	private ImageView mMenuBarBack, mMenuBarForward, mMenuBarRefresh, mMenuBarBrowser,mOneBack;

	private List<ProgressWebView> mWebViewLists;
	private int mCurPos;
	private SystemBussiness mSystemBussiness;
	private Tabs mSt;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.frg_weibo, null);
		
		return mRootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		init();
		fill();
	}

	private void init() {
		mSystemBussiness = SystemBussiness.getInstance(mHostActivity);
		mprogress = (ProgressBar) mRootView.findViewById(R.id.weibo_progress);
		viewparge = (ViewPager) mRootView.findViewById(R.id.weibo_viewparger);
		
		viewparge.setOnPageChangeListener(this);// 页面变化时的监听器
		
		//下边webview的工具栏
		mOneBack=(ImageView) mRootView.findViewById(R.id.bottom_menubar_one_back);
		mMenuBarBack = (ImageView) mRootView.findViewById(R.id.menubar_back);
		mMenuBarForward = (ImageView) mRootView.findViewById(R.id.menubar_forword);
		mMenuBarRefresh = (ImageView) mRootView.findViewById(R.id.menubar_refresh);
		mMenuBarBrowser = (ImageView) mRootView.findViewById(R.id.menubar_browser);
		mOneBack.setOnClickListener(this);
		mMenuBarBack.setOnClickListener(this);
		mMenuBarForward.setOnClickListener(this);
		mMenuBarRefresh.setOnClickListener(this);
		mMenuBarBrowser.setOnClickListener(this);
		mSt = (Tabs) mRootView.findViewById(R.id.weibo_st);
	}
	private void fill(){
		mSystemBussiness.getWeiBoInfo(new BussinessCallbackCommon<List<WeiboInfo>>() {
			
			@Override
			public void onException(int excepCode) {
			}
			
			@Override
			public void onDone(List<WeiboInfo> obj) {
				mprogress.setVisibility(View.GONE);
				if(obj.size()<1)
					return;
				LogM.log(this.getClass(), "微博fragment");
				minfos = obj;
				adapter = new WeiboPargeAdapter();
				viewparge.setAdapter(adapter);
				if(mWebViewLists!=null&&mWebViewLists.size()>0)
					mCurWebView = mWebViewLists.get(0);
				
				String[] names = new String[minfos.size()];
				
				for(int i=-1;++i<names.length;){
					names[i] = minfos.get(i).getName();
				}
				mSt.setTabs(names);
				mSt.setOnItemClickListener(new Tabs.OnItemClickListener() {
					
					@Override
					public void onItemClick(int pos) {
						viewparge.setCurrentItem(pos);
					}
				});
			}
		});
		
	}
	
	private WebView getCurWebView(){
		return adapter.backProgressWebView().get(mCurPos);
	}
	
	@Override
	public void onClick(View v) {
		if(mCurWebView==null) return;
		switch (v.getId()) {
		case R.id.menubar_back:
			if (mCurWebView.canGoBack()){
				mCurWebView.goBack();
			}else{
				SystemUtils.showToast(mHostActivity, "已经是第 一页");
			}
			break;
		case R.id.bottom_menubar_one_back:
			if (mCurWebView.canGoBack()){
				mCurWebView.goBack();
			}else{
				SystemUtils.showToast(mHostActivity, "已经是第 一页");
			}
			break;
		case R.id.menubar_forword:
			if (mCurWebView.canGoForward()) {
				mCurWebView.goForward();
			}else{
				SystemUtils.showToast(mHostActivity, "已经是最后一页");
			}
			break;
		case R.id.menubar_refresh:
			mCurWebView.reload();
			break;
		case R.id.menubar_browser:
			// 判断联网请求数据
			Uri uri = Uri.parse(mCurWebView.getUrl());
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(it);
			break;
		}

	}
	


	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int position, float offset, int arg01) {
	}

	@Override
	public void onPageSelected(int position) {
		
		mCurWebView = mWebViewLists.get(position);
		mSt.setCurrentTab(position);
	}

	
	
	
	
	
	public class WeiboPargeAdapter extends PagerAdapter {
		

		public WeiboPargeAdapter() {
			mWebViewLists = new ArrayList<ProgressWebView>();
			for (int i = 0; i < minfos.size(); i++) {
				AttributeSet attrs = null;
				ProgressWebView webview = new ProgressWebView(mHostActivity, attrs);
				// 设置WebView属性，能够执行Javascript脚本
				webview.setDownloadListener(new DownloadListener() {
					@Override
					public void onDownloadStart(String url, String userAgent,
							String contentDisposition, String mimetype,
							long contentLength) {
						if (url != null && url.startsWith("http://"))
							mHostActivity.startActivity(new Intent(
									Intent.ACTION_VIEW, Uri.parse(url)));
					}
				});
				webview.getSettings().setJavaScriptEnabled(true);
				webview.setWebViewClient(new WebViewClient());
				WebSettings webSettings = webview.getSettings();
				webSettings.setSupportZoom(false);
				LogM.log(this.getClass(), "加载链接");
				webview.loadUrl(minfos.get(i).getUrl());
				mWebViewLists.add(webview);
				}
		}


		@Override
		public int getCount() {
			return minfos.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		// 销毁Item
		@Override
		public void destroyItem(View view, int position, Object object) {
			((ViewPager) view).removeView(mWebViewLists.get(position));
		}

		// 实例化Item
		@Override
		public Object instantiateItem(View view, int position) {
			((ViewPager) view).addView(mWebViewLists.get(position), 0);
			return mWebViewLists.get(position);
		}

		public List<ProgressWebView> backProgressWebView() {
			return mWebViewLists;
		}
	}

}
