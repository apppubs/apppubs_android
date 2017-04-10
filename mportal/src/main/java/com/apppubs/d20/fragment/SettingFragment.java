package com.apppubs.d20.fragment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.apppubs.d20.AppManager;
import com.apppubs.d20.activity.FeedbackActivity;
import com.apppubs.d20.activity.StartUpActivity;
import com.apppubs.d20.model.SystemBussiness;
import com.apppubs.d20.widget.ConfirmDialog;
import com.apppubs.d20.MportalApplication;
import com.apppubs.d20.R;
import com.apppubs.d20.activity.AboutUsActivity;
import com.apppubs.d20.activity.ContainerActivity;
import com.apppubs.d20.activity.CustomWebAppUrlProtocolAndIpActivity;
import com.apppubs.d20.activity.LogsListActivity;
import com.apppubs.d20.activity.ThemeSwitchActivity;
import com.apppubs.d20.activity.WebAppActivity;
import com.apppubs.d20.bean.MenuItem;
import com.apppubs.d20.bean.Settings;
import com.apppubs.d20.model.BussinessCallbackCommon;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.service.DownloadAppService;
import com.apppubs.d20.util.FileUtils;
import com.apppubs.d20.util.Utils;
import com.orm.SugarRecord;

public class SettingFragment extends TitleMenuFragment implements OnClickListener{

	private TextView mCacheTv;
	private ToggleButton mPushTb, mNetworkTb;
	private boolean isSwitch, isSwitchTheme;
	private TextView currentVersionTv;
	private ImageView newVresion;
	private boolean isHaveNewVersion;
	private int mClickNum = 0;
	
	private MportalApplication mApp;
	private RequestQueue mRequestQueue;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 super.onCreateView(inflater, container, savedInstanceState);
		 mRootView = inflater.inflate(R.layout.frg_setting, null);
//			//左右滑动的布局下从下打开，其他情况用默认打开方式
//			if(mAppContext.getApp().getLayoutLocalScheme()==App.STYLE_SLIDE_MENU){
//				mTitleBar.setLeftImageResource(R.drawable.close);
//			}
			init();
			initState1();
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mApp = (MportalApplication) mHostActivity.getApplication();
	}
	private void initState1() {
		mSystemBussiness.checkUpdate(mHostActivity, new SystemBussiness.CheckUpdateListener() {
			@Override
			public void onDone(boolean needUpdate, boolean needForceUpdate, String version, String updateDescribe, String updateUrl) {
				isHaveNewVersion = needUpdate;
				if (isHaveNewVersion) {
					newVresion.setVisibility(View.VISIBLE);
				} else {
					newVresion.setVisibility(View.GONE);
				}
			}
		});

        currentVersionTv=(TextView) mRootView.findViewById(R.id.settting_update_version);
        
        PackageInfo pInfo = null;
		try {
			pInfo = mHostActivity.getPackageManager().getPackageInfo(mHostActivity.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		currentVersionTv.setText("V" + pInfo.versionName+" ("+Utils.getVersionCode(mContext)+")");
		
	}

	private void init() {
		newVresion = (ImageView) mRootView.findViewById(R.id.settting_update_newcursion);
		mPushTb = (ToggleButton) mRootView.findViewById(R.id.setting_push_tb);
		mNetworkTb = (ToggleButton) mRootView.findViewById(R.id.settings_network_tb);
		mCacheTv = (TextView) mRootView.findViewById(R.id.settings_cache_tv);
//		mTextSizeTv = (TextView) findViewById(R.id.settings_textsize_tv);
        
		registerOnClickListener(R.id.setting_push_tb, this);
		registerOnClickListener(R.id.settings_network_tb, this);
		registerOnClickListener(R.id.settings_cache, this);
		registerOnClickListener(R.id.settings_feed, this);
		registerOnClickListener(R.id.settings_about, this);
		registerOnClickListener(R.id.settings_welcome,this);
		registerOnClickListener(R.id.settings_update, this);
		registerOnClickListener(R.id.settings_switch, this);
		registerOnClickListener(R.id.settings_switch_color, this);
		registerOnClickListener(R.id.settings_switch_app, this);
		registerOnClickListener(R.id.setting_custom_webapp_url_ip_rl, this);
		registerOnClickListener(R.id.settings_logs_rl, this);
		mRequestQueue = Volley.newRequestQueue(mHostActivity);
		
		//根据assets文件夹下的welcome文件夹来判断是否显示欢迎图
		int welcomePicNum = 0;
		try {
			welcomePicNum = mHostActivity.getAssets().list("welcome").length;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(welcomePicNum>0){
			setVisibilityOfViewByResId(mRootView,R.id.settings_welcome, View.VISIBLE);
			setVisibilityOfViewByResId(mRootView,R.id.settings_welcome_line, View.VISIBLE);
		}
		
	}

	
	/**
	 * 恢复设置状态
	 */
	private void initState() {
		Settings settings = mAppContext.getSettings();
		mPushTb.setChecked(settings.isNeedPushNotification());
		mNetworkTb.setChecked(settings.isAllowDowPicUse2G());

		refreshCacheSize();
		if(mAppContext.getSettings().isDevMode()){
			showOrHideDevItems(true);
		}else{
			showOrHideDevItems(false);
		}
		
		
		mClickNum = 0;
		initTitleClickListener();
		
		List<MenuItem> list = SugarRecord.find(MenuItem.class, "is_allow_custom_ip = ?", MenuItem.YES+"");
		if(list.size()>0){
			setVisibilityOfViewByResId(mRootView, R.id.setting_segment3, View.VISIBLE);
		}
	}

	private void initTitleClickListener() {
		mTitleBar.setOnTitleClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mClickNum++;
				System.out.println("标题点击"+mClickNum+"次");
				Settings settings = mAppContext.getSettings();
				if(mClickNum==10&&!mAppContext.getSettings().isDevMode()){
					settings.setDevMode(true);
					mAppContext.setSettings(settings);
					Toast.makeText(mHostActivity, "切换应用功能已经打开", Toast.LENGTH_SHORT).show();
					showOrHideDevItems(true);
					mClickNum = 0;
				}else if(mClickNum==10&&mAppContext.getSettings().isDevMode()){
					mAppContext.getSettings().setDevMode(false);
					mAppContext.setSettings(settings);
					Toast.makeText(mHostActivity, "切换应用功能已经关闭", Toast.LENGTH_SHORT).show();
					showOrHideDevItems(false);
					mClickNum = 0;
					deleteLogFiles();
				}
			}

		});
	}
	
	/**
	 *  删除所有日志文件
	 */
	private void deleteLogFiles() {
		File[] files = mContext.getExternalFilesDir("logs").listFiles();
		if(files!=null&&files.length>0){
			for(int i=-1;++i<files.length;){
				files[i].delete();
			}
		}
	}
	
	private void showOrHideDevItems(boolean isNeedShow){
		if(isNeedShow){
			setVisibilityOfViewByResId(mRootView, R.id.setting_hidden_ll, View.VISIBLE);
		}else{
			setVisibilityOfViewByResId(mRootView, R.id.setting_hidden_ll, View.GONE);
		}
	}
	private void refreshCacheSize() {
		mCacheTv.setText(FileUtils.FormetFileSize(mSystemBussiness.getCacheSize()));
	}

	@Override
	public void onResume() {
		super.onResume();
		initState();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(hidden){
			//界面不能显示时禁用titlebar标题上的点击事件，否则如果settingfragment在主界面上是会导致其他界面下点击标题依然触发彩蛋
			mTitleBar.setOnTitleClickListener(null);
		}else{
			initTitleClickListener();
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();

	}
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.themeSwitch_blue_rl:
//			startActivity(new Intent(mApp, ChangTextSizeActivity.class));
			break;
		case R.id.settings_cache:
			if (mCacheTv.getText().equals("0.00B")) {
				Toast.makeText(mHostActivity, "没有要清理的缓存", Toast.LENGTH_SHORT).show();
				;
			} else {
				new ConfirmDialog(mHostActivity, new ConfirmDialog.ConfirmListener() {

					@Override
					public void onOkClick() {
						mSystemBussiness.clearCache(new BussinessCallbackCommon<Boolean>() {

							@Override
							public void onException(int excepCode) {
							}

							@Override
							public void onDone(Boolean obj) {
								refreshCacheSize();
							}
						});
					}

					@Override
					public void onCancelClick() {

					}
				}, "确定清理？", "取消", "确定").show();
			}
			break;
		// case R.id.settings_recommend://应用推荐
		// intent = new Intent(this, WebAppActivity.class);
		// intent.putExtra(WebAppActivity.EXTRA_NAME_TITLE, "应用推荐");
		// intent.putExtra(WebAppActivity.EXTRA_NAME_URL,
		// "http://www.baidu.com/");
		// this.startActivity(intent);
		// overridePendingTransition(R.anim.slide_in_from_right,
		// R.anim.slide_out_to_left);
		// break;
		case R.id.settings_feed:// 意见反馈
			startActivity(new Intent(mHostActivity, FeedbackActivity.class));
			break;
	
		case R.id.settings_about:// 关于我们
			intent = new Intent(mHostActivity, AboutUsActivity.class);
			intent.putExtra(WebAppActivity.EXTRA_NAME_TITLE, "关于我们");
			intent.putExtra(WebAppActivity.EXTRA_NAME_URL, URLs.URL_ABOUT);
			this.startActivity(intent);
			break;
		case R.id.setting_push_tb:// 是否接受消息点击
			if (mPushTb.isChecked()) {
				mAppContext.getSettings().setNeedPush(true);
			} else {
				mAppContext.getSettings().setNeedPush(false);
			}
//			m.commitSystemSettings(mAppContext.getSettings());
			break;
		case R.id.settings_network_tb:// 网络
			Settings settings = mAppContext.getSettings();
			if (mNetworkTb.isChecked()) {
				settings.setAllowDowPicUse2G(true);
			} else {
				settings.setAllowDowPicUse2G(false);
			}
			mAppContext.setSettings(settings);
			break;
		case R.id.settings_update:
			checkVersion();
			break;
		case R.id.settings_switch:// 切换样式
			new ConfirmDialog(mHostActivity, new ConfirmDialog.ConfirmListener() {

				@Override
				public void onOkClick() {
					isSwitchTheme = true;
					AppManager.getInstant(mContext).switchLayout();
				}

				@Override
				public void onCancelClick() {
				}
			}, "切换样式会自动重启应用，确定切换？", "取消", "确定").show();
			break;
		case R.id.settings_switch_color:
			intent = new Intent(mHostActivity, ThemeSwitchActivity.class);
			startActivity(intent);
			break;
		case R.id.settings_switch_app:
			DisplayMetrics dm = getResources().getDisplayMetrics();
			int width = dm.widthPixels;
			int height = dm.heightPixels;
			View popV = LayoutInflater.from(mHostActivity).inflate(R.layout.pop_switch_app, null);
			Button subBtn = (Button) popV.findViewById(R.id.switch_submit_btn);
			final EditText ipEt = (EditText) popV.findViewById(R.id.switch_ip_et);
			final EditText codeEt = (EditText) popV.findViewById(R.id.switch_code_et);
			ipEt.setText(mAppContext.getSettings().getBaseURL());
			codeEt.setText(mAppContext.getSettings().getAppCode());
			subBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					AppManager.getInstant(mContext).showChangeDialog(mContext, ipEt.getText().toString(), codeEt.getText().toString());
				}
			});

			TextView resetTV = (TextView) popV.findViewById(R.id.switch_reset_btn);
			resetTV.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String baseUrl = Utils.getMetaValue(mContext, "BASE_URL");
					String appCode = Utils.getMetaValue(mContext, "APPCODE");
					ipEt.setText(baseUrl);
					codeEt.setText(appCode);
				}
			});
			PopupWindow pw = new PopupWindow(popV);
			pw = new PopupWindow(popV, width, height);
			pw.setFocusable(true);
			pw.setOutsideTouchable(true);
			pw.setBackgroundDrawable(new BitmapDrawable());
			pw.showAtLocation(mTitleBar, Gravity.CENTER, 0, 0);
			break;
		case R.id.settings_welcome:
			ContainerActivity.startActivity(mHostActivity, WelcomeFragment.class,true);
			break;
		case R.id.setting_custom_webapp_url_ip_rl:
			startActivity(CustomWebAppUrlProtocolAndIpActivity.class);
			break;
		case R.id.settings_logs_rl:
			startActivity(LogsListActivity.class);
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		if (isSwitch || isSwitchTheme) {
			Intent mStartActivity = new Intent(mHostActivity, StartUpActivity.class);
			mStartActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			int mPendingIntentId = 123456;

			PendingIntent mPendingIntent = PendingIntent.getActivity(mHostActivity, mPendingIntentId, mStartActivity,
					PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager mgr = (AlarmManager) mHostActivity.getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
			if (isSwitch)
				android.os.Process.killProcess(android.os.Process.myPid());

		}
	}

	private void checkVersion() {

		mSystemBussiness.checkUpdate(mHostActivity, new SystemBussiness.CheckUpdateListener() {
			@Override
			public void onDone(boolean needUpdate, boolean needForceUpdate, String version, String updateDescribe, final String updateUrl) {

				if (needUpdate) {
					String title = String.format("检查到有新版 %s", TextUtils.isEmpty(version)?"":"V"+version);
					newVresion.setVisibility(View.VISIBLE);
					ConfirmDialog dialog = new ConfirmDialog(mHostActivity, new ConfirmDialog.ConfirmListener() {

						@Override
						public void onCancelClick() {
						}

						@Override
						public void onOkClick() {
							Intent it = new Intent(mHostActivity, DownloadAppService.class);
							it.putExtra(DownloadAppService.SERVICRINTENTURL, updateUrl);
							it.putExtra(DownloadAppService.SERVACESHARENAME, 0);
							mHostActivity.startService(it);
							// bindService(it, conn, Context.BIND_AUTO_CREATE);
							mUserBussiness.logout(mHostActivity);
						}
					}, title , updateDescribe, "下次", "更新");
					dialog.show();
					dialog.setCanceledOnTouchOutside(false);
				} else {
					newVresion.setVisibility(View.GONE);
					Toast.makeText(mHostActivity, "当前已是最新版本", Toast.LENGTH_SHORT).show();
				}
			}
		});


	}


	
	
	
	
}