package com.apppubs.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apppubs.AppContext;
import com.apppubs.AppManager;
import com.apppubs.MportalApplication;
import com.apppubs.d20.R;
import com.apppubs.bean.AppConfig;
import com.apppubs.bean.Settings;
import com.apppubs.bean.UserInfo;
import com.apppubs.constant.Actions;
import com.apppubs.model.message.MsgBussiness;
import com.apppubs.model.message.UserBussiness;
import com.apppubs.model.APResultCallback;
import com.apppubs.model.NewsBussiness;
import com.apppubs.model.PaperBussiness;
import com.apppubs.model.SystemBussiness;
import com.apppubs.model.VersionInfo;
import com.apppubs.ui.start.StartUpActivity;
import com.apppubs.util.JSONResult;
import com.apppubs.util.LogM;
import com.apppubs.util.Utils;
import com.apppubs.ui.widget.AlertDialog;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.ui.widget.TitleBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class BaseActivity extends FragmentActivity implements OnClickListener {

	public static final int FILECHOOSER_RESULTCODE = 1;
	public static final String EXTRA_STRING_TITLE = "extra_title";
	public static final String EXTRA_BOOLEAN_NEED_TITLEBAR = "need_titlebar";

	protected Context mContext;
	/**
	 * 是否需要titlebar
	 */
	private boolean isNeedTitleBar = true;
	private boolean isNeedBack = true;
	protected TitleBar mTitleBar;
	protected ImageLoader mImageLoader;
	protected MportalApplication mApp;
	protected AppContext mAppContext;

	protected NewsBussiness mNewsBussiness;
	protected SystemBussiness mSystemBussiness;
	protected PaperBussiness mPaperBussiness;
	protected UserBussiness mUserBussiness;
	protected MsgBussiness mMsgBussiness;

	protected int curTheme;
	protected int mThemeColor;
	private boolean mShouldInterceptBackClicked;
	private BroadcastReceiver mBr;

	public static int mDefaultColor;// 字体默认颜色

	protected RequestQueue mRequestQueue;
	private static int mActiveActivityNum;// 处于活跃状态的activity

	private ValueCallback<Uri> mUploadMessage;

	public void setUploadMessage(ValueCallback<Uri> uploadMessage) {
		mUploadMessage = uploadMessage;
	}

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		LogM.log(this.getClass(), " BaseActivity onCreate");


		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		};

		mHandler.sendEmptyMessage(3);

		mContext = this;
		mAppContext = AppContext.getInstance(mContext);
		isNeedTitleBar = getIntent().getBooleanExtra(EXTRA_BOOLEAN_NEED_TITLEBAR, isNeedTitleBar);

		int theme = mAppContext.getSettings().getTheme();
		switch (theme) {
		case Settings.THEME_BLUE:
			setTheme(R.style.AppThemeBlue);
			break;
		case Settings.THEME_INDIGO:
			setTheme(R.style.AppThemeIndigo);
			break;
		case Settings.THEME_RED:
			setTheme(R.style.AppThemeRed);
			break;
		case Settings.THEME_BROWN:
			setTheme(R.style.AppThemeBrown);
			break;
		default:
			setTheme(R.style.AppThemeBlue);
		}

		// app配色
		if (theme < 4) {

			TypedArray array = getTheme().obtainStyledAttributes(new int[] { R.attr.appDefaultColor });
			mThemeColor = array.getColor(0, 0x000000);
			array.recycle();

		} else {
			mThemeColor = Color.parseColor(mAppContext.getApp().getCustomThemeColor());
		}

		// // 横竖屏 当方向标记为2,3时，平板为横屏，为3,4时手机为横屏
		int orientationFlag = Utils.getIntegerMetaData(this, "DISPLAY_ORIENTATION");
		if (!Utils.isPad(this) && orientationFlag >= 3
				|| (Utils.isPad(this) && (orientationFlag == 2 || orientationFlag == 3))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		}

		System.out.println(Utils.getIntegerMetaData(this, "DISPLAY_ORIENTATION"));

		TypedArray ta = obtainStyledAttributes(new int[] { R.attr.appDefaultColor });
		mDefaultColor = ta.getColor(0, 0xffffff);
		ta.recycle();
		getDefaultImageLoaderOption();
		mImageLoader = ImageLoader.getInstance();
		mApp = (MportalApplication) getApplication();

		mNewsBussiness = NewsBussiness.getInstance(mContext);
		mSystemBussiness = SystemBussiness.getInstance(this);
		mPaperBussiness = PaperBussiness.getInstance();
		mUserBussiness = UserBussiness.getInstance(this);
		mMsgBussiness = MsgBussiness.getInstance(this);

		mBr = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				LogM.log(this.getClass(), "onReceive finish");
				finish();
			}
		};
		registerReceiver(mBr, new IntentFilter(Actions.CLOSE_ALL_ACTIVITY));

		mRequestQueue = Volley.newRequestQueue(this);

	}

	public DisplayImageOptions getDefaultImageLoaderOption() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.white)
				.showImageOnFail(R.drawable.white)
				.showImageForEmptyUri(R.drawable.white)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888)
//				.denyNetworkDownload(MportalApplication.systemState.getNetworkState() !=
//						ConnectivityManager.TYPE_WIFI && !mAppContext.getSettings()
//						.isAllowDowPicUse2G())
				.build();
		return options;
	}

	public RequestQueue getRequestQueue() {
		return mRequestQueue;
	}

	public Request<String> addStringRequest(String url,Listener<String> listener,ErrorListener errorListener){
		StringRequest request = new StringRequest(url, listener,errorListener);
		return mRequestQueue.add(request);
	}

	public String getUrlCache(String url){
		Cache.Entry entry = mRequestQueue.getCache().get(url);
		if (entry!=null){
			return new String(entry.data);
		}
		return null;
	}

	@Override
	public void setContentView(int layoutResID) {

		if (isNeedTitleBar) {
			super.setContentView(R.layout.act_base);
			ViewGroup vg = (ViewGroup) findViewById(R.id.title_ll);
			getLayoutInflater().inflate(layoutResID, vg);
			mTitleBar = (TitleBar) findViewById(R.id.base_tb);
			mTitleBar.setBackgroundColor(mThemeColor);
			if (isNeedBack) {
				mTitleBar.setLeftBtnClickListener(this);
				mTitleBar.setLeftImageResource(R.drawable.top_back_btn);
				String title = getIntent().getStringExtra(EXTRA_STRING_TITLE);
				if (!TextUtils.isEmpty(title)) {
					setTitle(title);
				}
			}
		} else {
			super.setContentView(layoutResID);
		}

	}

	@Override
	public void setContentView(View view) {

		if (isNeedTitleBar) {
			super.setContentView(R.layout.act_base);
			ViewGroup vg = (ViewGroup) findViewById(R.id.title_ll);
			vg.addView(view);
			mTitleBar = (TitleBar) findViewById(R.id.base_tb);
			mTitleBar.setBackgroundColor(mThemeColor);
			if (isNeedBack) {
				mTitleBar.setLeftBtnClickListener(this);
				mTitleBar.setLeftImageResource(R.drawable.top_back_btn);
				String title = getIntent().getStringExtra(EXTRA_STRING_TITLE);
				if (!TextUtils.isEmpty(title)) {
					setTitle(title);
				}
			}
		} else {
			super.setContentView(view);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		EventBus.getDefault().register(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mActiveActivityNum++;

		if (mActiveActivityNum == 1) {
			onAppActive();
		}
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(MessageEvent event){

	}

	protected void onAppActive() {
		if (!(this instanceof FirstLoginActity)||(this instanceof StartUpActivity)){
			mUserBussiness.updateUserInfo(this, new APResultCallback<UserInfo>() {
				@Override
				public void onDone(UserInfo obj) {
					if (obj==null||TextUtils.isEmpty(obj.getUserId())){
						Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
						sendBroadcast(closeI);
						startActivity(FirstLoginActity.class);
					}
				}

				@Override
				public void onException(int excepCode) {

				}
			});

//			this.preCheckUpdate();

		}

	}

	private void preCheckUpdate(){
		mSystemBussiness.aSyncAppConfig(this, new APResultCallback<AppConfig>() {
			@Override
			public void onDone(AppConfig obj) {
				System.out.print("同步appconfig成功");
				//避免在startupactivit中进行重复检测
				if (!(BaseActivity.this instanceof StartUpActivity)){
					checkUpdate();
				}
			}

			@Override
			public void onException(int excepCode) {
				System.out.print("同步appconfig失败");
			}
		});
	}
	protected void checkUpdate() {
		mSystemBussiness.checkUpdate(BaseActivity.this,new SystemBussiness.CheckUpdateListener(){

			@Override
			public void onDone(final VersionInfo vi) {
				if (vi.isNeedUpdate()&&vi.isNeedAlert()){
					String title = String.format("检查到有新版 %s", TextUtils.isEmpty(vi.getVersion())?"":"V"+vi.getVersion());
					if(vi.isNeedForceUpdate()){
						AlertDialog ad = new AlertDialog(BaseActivity.this, new AlertDialog.OnOkClickListener() {

							@Override
							public void onclick() {
								AppManager.getInstant(mContext).downloadApp(vi.getUpdateUrl());
//								Intent it = new Intent(BaseActivity.this, DownloadAppService.class);
//								it.putExtra(DownloadAppService.SERVICRINTENTURL, vi.getUpdateUrl());
//								it.putExtra(DownloadAppService.SERVACESHARENAME, 0);
//								startService(it);
								Toast.makeText(BaseActivity.this, "正在下载中，请稍候", Toast.LENGTH_SHORT).show();
							}
						}, title, vi.getUpdateDescribe(),"更新");
						ad.show();
						ad.setCancelable(false);
						ad.setCanceledOnTouchOutside(false);
					}else{
						ConfirmDialog dialog = new ConfirmDialog(BaseActivity.this, new ConfirmDialog.ConfirmListener() {

							@Override
							public void onCancelClick() {
							}

							@Override
							public void onOkClick() {
								AppManager.getInstant(mContext).downloadApp(vi.getUpdateUrl());
//								Intent it = new Intent(BaseActivity.this, DownloadAppService.class);
//								it.putExtra(DownloadAppService.SERVICRINTENTURL, vi.getUpdateUrl());
//								it.putExtra(DownloadAppService.SERVACESHARENAME, 0);
//								startService(it);
//								mUserBussiness.logout(BaseActivity.this);
							}
						}, title , vi.getUpdateDescribe(), "下次", "更新");
						dialog.show();
						dialog.setCanceledOnTouchOutside(false);
					}
				}else{
				}
			}
		});
	}


	@Override
	protected void onStop() {
		super.onStop();

		EventBus.getDefault().unregister(this);

		mActiveActivityNum--;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.titlebar_left_btn:
			if (!shouldInterceptBackClick()) {
				finish();
			}
			break;
		default:
			break;
		}
	}

	public boolean shouldInterceptBackClick() {
		return mShouldInterceptBackClicked;
	}

	public void setShouldInterceptBackClick(boolean shouldIntercept) {
		this.mShouldInterceptBackClicked = shouldIntercept;
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBr);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
	}

	public TitleBar getTitleBar() {
		return mTitleBar;
	}

	/**
	 * 设置标题文字
	 *
	 * @param title
	 */
	protected void setTitle(String title) {
		if (mTitleBar != null)
			mTitleBar.setTitle(title);
	}

	/**
	 * 隐藏titlebar
	 */
	public void hideTitleBar() {
		if (mTitleBar != null)
			mTitleBar.setVisibility(View.GONE);
	}

	public int getThemeColor() {
		return mThemeColor;
	}

	/**
	 * 设置此activity是否需要titlebar 必须在setconview之前进行设置
	 *
	 * @param need
	 */
	protected void setNeedTitleBar(boolean need) {
		isNeedTitleBar = need;
		if (mTitleBar != null) {
			mTitleBar.setVisibility(need ? View.VISIBLE : View.GONE);
		}
	}

	public void setNeedBack(boolean isNeedBack) {
		this.isNeedBack = isNeedBack;
	}

	protected void setVisibilityOfViewByResId(int resId, int visibility) {

		View v = findViewById(resId);
		v.setVisibility(visibility);
	}

	protected void setVisibilityOfViewByResId(View view, int resId, int visibility) {
		View v = view.findViewById(resId);
		v.setVisibility(visibility);

	}

	protected void startActivity(Class<?> clazz) {
		startActivity(clazz, null);
	}

	protected void startActivity(Class<?> clazz, Bundle extras) {
		Intent i = new Intent(this, clazz);
		if (extras != null) {
			i.putExtras(extras);
		}
		startActivity(i);
	}

	/**
	 * 填充某View下的某TextView
	 *
	 * @param resId
	 *            TextView 的id
	 * @param content
	 *            TextView 文本内容
	 */
	protected void fillTextView(int resId, String content) {
		TextView tv = (TextView) findViewById(resId);
		tv.setText(content);

	}

	protected void addRequest(String url, final RequestListener listener, final int requestCode) {
		mRequestQueue.add(new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				JSONResult jr = JSONResult.compile(response);
				listener.onResponse(jr, requestCode);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				listener.onException(JSONResult.RESULT_CODE_FAIL, requestCode);
			}
		}));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		LogM.log(this.getClass(), "onActivityResult");
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}

		super.onActivityResult(requestCode, resultCode, intent);
	}

}
