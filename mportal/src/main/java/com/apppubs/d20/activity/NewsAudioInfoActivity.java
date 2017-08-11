package com.apppubs.d20.activity;

import java.util.concurrent.Future;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.d20.bean.Collection;
import com.apppubs.d20.bean.Comment;
import com.apppubs.d20.bean.NewsInfo;
import com.apppubs.d20.model.APResultCallback;
import com.apppubs.d20.model.CollectionBussiness;
import com.apppubs.d20.model.NewsBussiness;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.ShareTools;
import com.apppubs.d20.util.Utils;
import com.apppubs.d20.widget.MyWebChromeClient;
import com.apppubs.d20.R;
import com.apppubs.d20.bean.Settings;
import com.orm.SugarRecord;

/**
 * 新闻详情页
 */
public class NewsAudioInfoActivity extends BaseActivity {

	public static final String EXTRA_STRING_NAME_ID = "id";
	public static final String EXTRA_STRING_NAME_CHANNELCODE = "channel_code";
	private WebView mWebView;
	// private ImageView back, mSaveImagview, share;
	// private View mCommontTv;
	private LinearLayout progress;
	private NewsInfo mNewsInfo;
	private String mInfoId;
	private String mChannelCode;
	private Comment mCommment;// 评论数，赞，踩
	private NewsBussiness mNewsBussiness;
	private Future<?> mFuture;
	private TextView mCommentTv;
	private PopupWindow mMenuPW;
	private boolean isCollected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		mInfoId = i.getStringExtra(EXTRA_STRING_NAME_ID);
		mChannelCode = i.getStringExtra(EXTRA_STRING_NAME_CHANNELCODE);
		
		setContentView(R.layout.act_newsinfo);
		init();
		mNewsInfo = SugarRecord.findById(NewsInfo.class, mInfoId);
		if (mNewsInfo == null) {
			mNewsInfo = new NewsInfo();
			mNewsInfo.setId(mInfoId);
			mNewsInfo.setChannelCode(mChannelCode);
		}else{
			mChannelCode = mNewsInfo.getChannelCode();
		}
		mNewsBussiness = NewsBussiness.getInstance(mContext);
		mFuture = mNewsBussiness.getNewsInfo(mNewsInfo.getId(), mNewsInfo.getChannelCode(),
				new APResultCallback<NewsInfo>() {

					@Override
					public void onException(int excepCode) {
						Log.v("newsInfoActivity", "getNewsInfo出现异常");
						Toast.makeText(NewsAudioInfoActivity.this, "获取正文出错", Toast.LENGTH_SHORT).show();
						progress.setVisibility(View.GONE);
					}

					@Override
					public void onDone(NewsInfo obj) {

						mNewsInfo = obj;
						Log.v("newsInfoActivity", "getNewsInfo完成" + obj.getContent());
						mWebView.loadDataWithBaseURL("", obj.getContent(), "text/html", "utf-8", null);
						mWebView.loadUrl("http://www.baidu.com");
						
						if (obj.getShareFlag() == 0) {
							// share.setVisibility(View.GONE);
						}
						System.out.println("打印新闻正文的链接..................." + obj.getContent());
						progress.setVisibility(View.GONE);

						// 数据完整后初始化菜单
						initMenu();
					}
				});

	}

	private void init() {

		setTitle("正文");
		mWebView = (WebView) findViewById(R.id.newsinfo_wv);
		progress = (LinearLayout) findViewById(R.id.newsinfo_progressBar);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setAppCacheEnabled(false);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		// 设置WebView属性，能够执行Javascript脚本
		webSettings.setJavaScriptEnabled(false);
		int textSize = mAppContext.getSettings().getTextSize();
		switch (textSize) {
		case Settings.TEXTSIZE_BIG:
			webSettings.setTextSize(TextSize.LARGER);
			break;
		case Settings.TEXTSIZE_MEDIUM:
			webSettings.setTextSize(TextSize.NORMAL);
			break;
		case Settings.TEXTSIZE_SMALL:
			webSettings.setTextSize(TextSize.SMALLER);
			break;
		default:
			break;
		}
		mWebView.setWebChromeClient(new MyWebChromeClient());
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progress.setVisibility(View.GONE);
			}

			@Override
			public void onLoadResource(WebView view, String url) {

				super.onLoadResource(view, url);
				System.out.println("加载资源 url" + url);

			}

			@SuppressLint("NewApi")
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
				System.out.println("拦截请求 url" + url);

				return null;
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				System.out.println("shouldOverrideUrlLoading url" + url);

//				// 如果此链接是pdf，doc，txt，等等附件则跳转到附件预览界面
//				if (FilePreviewFragment.isAbleToRead(url)) {
//					Bundle args = new Bundle();
//					args.putString(FilePreviewFragment.ARGS_STRING_URL, url);
//					LocalFile localFile = SugarRecord.findByProperty(LocalFile.class, "source_path", url);
//					if(localFile!=null){
//						args.putString(FilePreviewFragment.ARGS_STRING_FILE_LOCAL_PATH, localFile.getSourcePath());
//					}
//					ContainerActivity.startActivity(NewsAudioInfoActivity.this, FilePreviewFragment.class, args, "文件预览");
//					return true;
//				} else {
//					Bundle bundle = new Bundle();
//					bundle.putString(WebAppFragment.ARGUMENT_STRING_URL, url);
//					ContainerActivity.startActivity(NewsAudioInfoActivity.this, WebAppFragment.class, bundle, "详情");
//					return true;
//				}
				return super.shouldOverrideUrlLoading(view, url);

			}

		});

	}

	private void initMenu() {

		this.mTitleBar.setRightBtnImageResourceId(R.drawable.title_more);
		this.mTitleBar.setRightBtnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View menuPop = LayoutInflater.from(NewsAudioInfoActivity.this).inflate(R.layout.pop_news_info_menu, null);
				mMenuPW = new PopupWindow(menuPop, ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				mMenuPW.setFocusable(true);
				mMenuPW.setOutsideTouchable(true);
				mMenuPW.setBackgroundDrawable(new BitmapDrawable());
				mMenuPW.showAsDropDown(mTitleBar.getRightView());

				// 是否可收藏
				if (mNewsInfo.getShareFlag() == 0) {
					setVisibilityOfViewByResId(menuPop, R.id.pop_news_info_share, View.GONE);
				}

				if (mNewsInfo.getCollectFlag() == 0) {
					setVisibilityOfViewByResId(menuPop, R.id.pop_news_info_collect, View.GONE);
				}else{
					isCollected = null!=SugarRecord.findByProperty(Collection.class,"info_id", mInfoId+","+mChannelCode)?true:false;
					if (isCollected) {
						ImageView iv = (ImageView) menuPop.findViewById(R.id.pop_news_info_collect_ib);
						iv.setImageResource(R.drawable.menubar_favorite_h);
					}
					
				}

			}
		});

		// 评论数量按钮
		if (mNewsInfo.getCommentFlag() == 1) {
			mCommentTv = new TextView(this);
			mCommentTv.setText(mCommment == null ? 0 + "" : mCommment.getCommentnum() + "");
			mCommentTv.setGravity(Gravity.CENTER);
			mCommentTv.setTextColor(Color.parseColor("#FFFFFF"));
			mCommentTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
			mCommentTv.setBackgroundResource(R.drawable.menubar_comment);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Utils.dip2px(this, 30),
					Utils.dip2px(this, 24));
			lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			mCommentTv.setLayoutParams(lp);
			RelativeLayout con = new RelativeLayout(this);
			con.addView(mCommentTv, lp);
			this.mTitleBar.setRight2ndView(con);

			this.mTitleBar.setRight2ndBtnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(NewsAudioInfoActivity.this, CommentActivity.class);
					intent.putExtra(EXTRA_STRING_NAME_ID, mInfoId);
					startActivity(intent);
					overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
				}
			});
		}

	}

	@Override
	protected void onResume() {
		LogM.log(this.getClass(), "onResume");
		super.onResume();
		initState();
		// 评论数
		refreshCommet();
		if(mWebView!=null){
			mWebView.onResume();
		}
	}

	private void initState() {

		if (mNewsInfo.getIsCollected() == NewsInfo.COLLECTED) {
			// mSaveImagview.setImageResource(R.drawable.menubar_favorite_h);
		}
	}

	private int tempTextSize = 1;

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.pop_news_info_collect:
			String title = mNewsInfo.getTitle();
			String summy = mNewsInfo.getSummary();
			ImageView iv = (ImageView) mMenuPW.getContentView().findViewById(R.id.pop_news_info_collect_ib);
			CollectionBussiness.toggleCollect(Collection.TYPE_NORMAL, this, isCollected, mInfoId+","+mChannelCode, title, summy);
			isCollected = !isCollected;
			Toast.makeText(this, isCollected?"已收藏":"取消收藏", Toast.LENGTH_SHORT).show();
			iv.setImageResource(isCollected?R.drawable.menubar_favorite_h:R.drawable.menubar_favorite);
			break;
		case R.id.pop_news_info_share:
			new ShareTools(getApplication()).showShare(mNewsInfo.getTitle(), mNewsInfo.getUrl(), "");
			break;
		case R.id.pop_news_info_textsize:

			new AlertDialog.Builder(this)  
			.setTitle("字体大小")  
			.setSingleChoiceItems(new String[] {"大号","中号","小号"}, tempTextSize,   
			  new DialogInterface.OnClickListener() {  
			                              
			     public void onClick(DialogInterface dialog, int which) {  
			        dialog.dismiss();  
					WebSettings webSettings = mWebView.getSettings();
					if (which == 0) {
						webSettings.setTextSize(TextSize.LARGER);
						tempTextSize = 0;
					} else if (which == 1) {
						webSettings.setTextSize(TextSize.NORMAL);
						tempTextSize = 1;
					} else {
						webSettings.setTextSize(TextSize.SMALLER);
						tempTextSize = 2;
					}
			     }  
			  }  
			)  
			.setNegativeButton("取消", null)  
			.show(); 
//			WebSettings webSettings = mWebView.getSettings();
//			if (tempTextSize == 0) {
//				webSettings.setTextSize(TextSize.LARGER);
//				tempTextSize = 1;
//			} else if (tempTextSize == 1) {
//				webSettings.setTextSize(TextSize.SMALLER);
//				tempTextSize = -1;
//			} else {
//				webSettings.setTextSize(TextSize.NORMAL);
//				tempTextSize = 0;
//			}

			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {

		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);

		// 由于推送情况下需要直接打开此activity，如果主Activity打开的话则直接不做处理，否则打开主activity

	}


	@Override
	protected void onPause() {
		super.onPause();
		if(mWebView!=null){
			mWebView.onPause();
		}
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		mFuture.cancel(true);
		if(mWebView!=null){
			mWebView.destroy();
		}
		super.onDestroy();
	}

	public void refreshCommet() {
		mSystemBussiness.getCommentSizeZanCai(mInfoId, new APResultCallback<Comment>() {
			@Override
			public void onException(int excepCode) {
				mCommment = null;
			}

			@Override
			public void onDone(Comment obj) {
				mCommment = obj;
				if (mCommentTv != null) {
					mCommentTv.setText(mCommment.getCommentnum());
				}

				// 更新数据库中的评论数
				SugarRecord.updateById(NewsInfo.class, mInfoId, "COMMENT_NUM", mCommment.getCommentnum());
			}
		});
	}
}
