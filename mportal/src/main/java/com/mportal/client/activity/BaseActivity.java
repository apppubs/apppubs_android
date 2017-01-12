package com.mportal.client.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.bean.App;
import com.mportal.client.bean.Settings;
import com.mportal.client.bean.User;
import com.mportal.client.business.MsgBussiness;
import com.mportal.client.business.NewsBussiness;
import com.mportal.client.business.PaperBussiness;
import com.mportal.client.business.SystemBussiness;
import com.mportal.client.message.model.UserBussiness;
import com.mportal.client.constant.Actions;
import com.mportal.client.constant.URLs;
import com.mportal.client.net.RequestListener;
import com.mportal.client.util.JSONResult;
import com.mportal.client.util.LogM;
import com.mportal.client.util.Utils;
import com.mportal.client.widget.TitleBar;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class BaseActivity extends FragmentActivity implements OnClickListener {

	public static final int FILECHOOSER_RESULTCODE = 1;
	public static final String EXTRA_STRING_TITLE = "extra_title";
	public static final String EXTRA_BOOLEAN_NEED_TITLEBAR = "need_titlebar";
	/**
	 * 是否需要titlebar
	 */
	private boolean isNeedTitleBar = true;
	private boolean isNeedBack = true;
	protected TitleBar mTitleBar;
	protected ImageLoader mImageLoader;
	protected MportalApplication mApp;

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

	@Override
	protected void onCreate(Bundle arg0) {
		LogM.log(this.getClass(), " BaseActivity onCreate");
		super.onCreate(arg0);
		isNeedTitleBar = getIntent().getBooleanExtra(EXTRA_BOOLEAN_NEED_TITLEBAR, isNeedTitleBar);
		int theme = MportalApplication.systemSettings.getTheme();
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
			mThemeColor = Color.parseColor(MportalApplication.app.getCustomThemeColor());
		}

		// // 横竖屏 当方向标记为2,3时，平板为横屏，为3,4时手机为横屏
		int orientationFlag = Utils.getMetaDataInteger(this, "DISPLAY_ORIENTATION");
		if (!Utils.isPad(this) && orientationFlag >= 3
				|| (Utils.isPad(this) && (orientationFlag == 2 || orientationFlag == 3))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		}

		System.out.println(Utils.getMetaDataInteger(this, "DISPLAY_ORIENTATION"));

		TypedArray ta = obtainStyledAttributes(new int[] { R.attr.appDefaultColor });
		mDefaultColor = ta.getColor(0, 0xffffff);
		ta.recycle();

		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		mImageLoader = ImageLoader.getInstance();
		mApp = (MportalApplication) getApplication();
		mNewsBussiness = NewsBussiness.getInstance();
		mSystemBussiness = SystemBussiness.getInstance(this);
		mPaperBussiness = PaperBussiness.getInstance();
		mUserBussiness = UserBussiness.getInstance(this);
		mMsgBussiness = MsgBussiness.getInstance();

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
	protected void onResume() {
		super.onResume();

		mActiveActivityNum++;
		
		if (mActiveActivityNum == 1) {
			onAppActive();
		}
	}

	protected void onAppActive() {
		// 如果是用户名登陆则启动是验证
		if (MportalApplication.app.getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME) {
			confirmDeviceBindStateWhenLoginWithUsername();
		}
		
		AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>(){
			@Override
			protected String doInBackground(String... params) {
				try {
					mSystemBussiness.syncAppConfig();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		asyncTask.execute("");
	}

	/*
	 * 
	 * 检测当前客户端绑定状态
	 */
	private void confirmDeviceBindStateWhenLoginWithUsername() {

		String osVersion = Utils.getAndroidSDKVersion();// 操作系统号
		String currentVersionName = Utils.getVersionName(this);// app版本号
		int appCode = Utils.getVersionCode(this);
		String url = null;
		try {
			// /wmh360/json/login/usersmslogin.jsp?username=%s&deviceid=%s&token=%s&os=%s&dev=%s&app=%s&fr=4&appcode="+appCode;
			url = String.format(URLs.URL_LOGIN_WITH_USERNAME, MportalApplication.user.getUsername(),
					mSystemBussiness.getMachineId(), MportalApplication.app.getPushToken(), osVersion,
					URLEncoder.encode(Build.MODEL, "utf-8"), currentVersionName,appCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		LogM.log(this.getClass(), "请求url:" + url);
		mRequestQueue.add(new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject jo = new JSONObject(response);
					int result = jo.getInt("result");
					User user = new User(jo.getString("userid"), jo.getString("username"), jo.getString("cnname"), "",
							jo.getString("email"), jo.getString("mobile"), jo.getString("menupower"));

					if (result != 2) {
						Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
						sendBroadcast(closeI);
						startActivity(FirstLoginActity.class);
					} else {
						MportalApplication.saveAndRefreshUser(BaseActivity.this, user);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
			}
		}));
	}

	@Override
	protected void onStop() {
		super.onStop();
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
	protected void hideTitleBar() {
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
		super.onActivityResult(requestCode, resultCode, intent);
		LogM.log(this.getClass(), "onActivityResult");
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}

	}

}
