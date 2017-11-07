package com.apppubs.d20.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.apppubs.d20.bean.PaperInfo;
import com.apppubs.d20.R;
import com.apppubs.d20.bean.Collection;
import com.apppubs.d20.model.CollectionBussiness;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.util.ShareTools;
import com.apppubs.d20.widget.ImageButton;
import com.orm.SugarRecord;

public class PaperInfoActivity extends BaseActivity implements OnClickListener {
	
	public static final String EXTRA_STRING_ID = "id";
	
	private ImageButton mBack;
	private ImageButton mSaveIM;
	private ImageButton mShare;
	private LinearLayout progress;
	private WebView mWebView;
	private String mInfoId;
	private String mTitle;
	private ShareTools mShareTools;
	private PaperInfo mPaperInfo;
	private boolean isCollected;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mInfoId = getIntent().getStringExtra(EXTRA_STRING_ID);
		mTitle = getIntent().getStringExtra(EXTRA_STRING_TITLE);
		setContentView(R.layout.act_paperinfo);
		init();
		mShareTools = new ShareTools(PaperInfoActivity.this);
		if(!TextUtils.isEmpty(mTitle)){
			mTitleBar.setTitle(mTitle);
		}
		
		setTitle("正文");
		
		initCollectionStatus();
	}


	private void initCollectionStatus() {
		isCollected = null!=SugarRecord.findByProperty(Collection.class,"info_id", mInfoId)?true:false;
		mTitleBar.setRightBtnImageResourceId(isCollected?R.drawable.menubar_favorite_h:R.drawable.menubar_favorite);
		mTitleBar.setRightBtnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggleCollection();
			}

			private void toggleCollection() {
				PaperInfo pi = SugarRecord.findById(PaperInfo.class, mInfoId);
				CollectionBussiness.toggleCollect(Collection.TYPE_PAPER, PaperInfoActivity.this, isCollected, mInfoId, pi.getTitle(), null);
				isCollected = !isCollected;
				mTitleBar.setRightBtnImageResourceId(isCollected?R.drawable.menubar_favorite_h:R.drawable.menubar_favorite);
				Toast.makeText(PaperInfoActivity.this, isCollected?"已收藏":"取消收藏", Toast.LENGTH_SHORT).show();
			}
		});
	}

	
	private void init() {
		progress = (LinearLayout) findViewById(R.id.paperinfo_progressBar);
		mWebView = (WebView) findViewById(R.id.paperinfo_wv);

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setAppCacheEnabled(false);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		webSettings.setJavaScriptEnabled(true);

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progress.setVisibility(View.GONE);
			}

		});
		mWebView.loadUrl(String.format(URLs.URL_PAPER_INFO, URLs.baseURL) + "?id=" + mInfoId);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	
}
