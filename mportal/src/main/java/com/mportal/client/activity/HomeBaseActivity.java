package com.mportal.client.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.bean.App;
import com.mportal.client.bean.MenuItem;
import com.mportal.client.bean.User;
import com.mportal.client.bean.Weather;
import com.mportal.client.constant.Actions;
import com.mportal.client.fragment.BaseFragment;
import com.mportal.client.service.DownloadAppService;
import com.mportal.client.util.FileUtils;
import com.mportal.client.util.LogM;
import com.mportal.client.util.SharedPreferenceUtils;
import com.mportal.client.util.SystemUtils;
import com.mportal.client.util.Tools;
import com.mportal.client.util.Utils;
import com.orm.SugarRecord;

public abstract class HomeBaseActivity extends BaseActivity {

	public static String MPORTAL_PREFERENCE_NAME = "mportal_preference";
	public static String MPORTAL_PREFERENCE_APP_RUNNING_KEY = "is_app_running";
	
	/**
	 * 应用主目录
	 */
	protected List<MenuItem> mPrimaryMenuList;
	/**
	 * 应用次目录
	 */
	protected static ArrayList<Weather> mWeathers;
	protected List<MenuItem> mSecondaryMenuList;
	protected ViewCourier mViewCourier;// 视图控制器
	protected MportalApplication mApp;

	private BroadcastReceiver mLogoutBR;

	protected void onCreate(android.os.Bundle arg0) {
		super.onCreate(arg0);
		overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
		LogM.log(this.getClass(), " HomeActivity onCreate");
		if (MportalApplication.user.getMenuPower() != null) {
			String menuPower = MportalApplication.user.getMenuPower();
			String[] menus = menuPower.split(",");
			String sqlParam = "";
			for(int i=-1;++i<menus.length;){
				if(i!=0){
					sqlParam+=",";
				}
				sqlParam += "'"+menus[i]+"'";
			}
			mPrimaryMenuList = SugarRecord.find(MenuItem.class, "LOCATION=? and (PROTECTED_FLAG = 0 or ID in ("+sqlParam+"))",
					new String[] { MenuItem.MENU_LOCATION_PRIMARY + "" }, null,
					"SORT_ID", null);
		} else {

			mPrimaryMenuList = SugarRecord.find(MenuItem.class, "LOCATION=? and PROTECTED_FLAG = 0",
					new String[] { MenuItem.MENU_LOCATION_PRIMARY + "" }, null, "SORT_ID", null);
		}
		mSecondaryMenuList = SugarRecord.find(MenuItem.class, "LOCATION=?",
				new String[] { MenuItem.MENU_LOCATION_SECONDARY + "" }, null, "SORT_ID", null);
		mApp = (MportalApplication) this.getApplication();
		mViewCourier = ViewCourier.getInstance(this);
		
		mSystemBussiness.makeStartUpRequest();
		initWeather();
		requestWeather();

		initBroadcastReceiver();
		
		SharedPreferenceUtils.getInstance(this).putBoolean(MPORTAL_PREFERENCE_NAME, MPORTAL_PREFERENCE_APP_RUNNING_KEY, true);
	};

	/**
	 * 初始化天气信息
	 */
	private void initWeather() {

		Object o = FileUtils.readObj(this, "weathers.cfg");
		if (o == null) {
			mWeathers = new ArrayList<Weather>();
		} else {
			mWeathers = (ArrayList<Weather>) o;
		}

	}

	private void initBroadcastReceiver() {
		mLogoutBR = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				logout();
			}
		};
		registerReceiver(mLogoutBR, new IntentFilter(Actions.ACTION_LOGOUT));
	}

	/**
	 * 天气信息(系统静态变量)
	 * 
	 * @param user
	 */
	public static void refreshWether(Context context, ArrayList<Weather> weathers) {
		mWeathers = weathers;
		FileUtils.writeObj(context, weathers, "weathers.cfg");
	}

	public void requestWeather() {
		// 能否联网
		boolean bo = SystemUtils.canConnectNet(getApplication());
		if (bo) {
			new Thread() {
				@Override
				public void run() {
					String cityName = mWeathers != null && mWeathers.size() != 0 ? mWeathers.get(0).getCityName()
							: "北京";
					mWeathers = Tools.getWeatherList(HomeBaseActivity.this, cityName);
					Intent intent = new Intent();
					intent.setAction(Actions.REFRESH_WEATHER);
					sendBroadcast(intent);
				}
			}.start();

		} else {
			Toast.makeText(getApplication(), getResources().getString(R.string.network_faile), Toast.LENGTH_LONG)
					.show();
		}
	}

	protected abstract void changeContent(BaseFragment fragment);

	protected abstract void setUnreadNumForMenu(String menuId,int num);
	
	@Override
	protected void onResume() {
		super.onResume();
	}


	public ViewCourier getViewController() {
		return mViewCourier;
	}

	@Override
	public void onClick(View v) {

	}

	public List<MenuItem> getPrimaryMenuList() {
		return mPrimaryMenuList;
	}

	public List<MenuItem> getSecondaryMenuList() {
		return mSecondaryMenuList;
	}

	@Override
	public void finish() {
		LogM.log(this.getClass(), "关闭应用");
		super.finish();
		// 发广播关闭下载服务
		Intent closeService = new Intent("com.mportal.client.stopdownload");
		sendBroadcast(closeService);
		stopService(new Intent(HomeBaseActivity.this, DownloadAppService.class));

		unregisterReceiver(mLogoutBR);

		// 记录此次运行的版本
		MportalApplication.app.setPreWorkingVersion(Utils.getVersionCode(HomeBaseActivity.this));
		MportalApplication.commitApp(this);
	}

	// protected void exit() {
	//
	// new ConfirmDialog(this, new ConfirmListener() {
	//
	// @Override
	// public void onOkClick() {
	//
	// overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
	// Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
	// sendBroadcast(closeI);
	//
	// }
	//
	// @Override
	// public void onCancelClick() {
	//
	// }
	// }, "确定退出？", "取消", "退出").show();
	// }

	public static void startHomeActivity(Context fromActivy) {

		LogM.log(Class.class, "startHomeActivity-->启动主界面");
		int layout = MportalApplication.app.getLayoutLocalScheme();
		Intent stopAllActivity = new Intent(Actions.CLOSE_ALL_ACTIVITY);
		fromActivy.sendBroadcast(stopAllActivity);
		Intent intent = null;
		if (layout == App.STYLE_SLIDE_MENU) {
			intent = new Intent(fromActivy, HomeSlideMenuActivity.class);
		} else {
			intent = new Intent(fromActivy, HomeBottomMenuActivity.class);
		}
		fromActivy.startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mViewCourier.destory();
		SharedPreferenceUtils.getInstance(this).putBoolean(MPORTAL_PREFERENCE_NAME, MPORTAL_PREFERENCE_APP_RUNNING_KEY, false);
	}

	private long lastClickTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
			long curClickTime = System.currentTimeMillis();
			if (curClickTime - lastClickTime < 1000) {
				HomeBaseActivity.super.finish();
				overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
				Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
				sendBroadcast(closeI);
			} else {
				lastClickTime = curClickTime;
				Toast.makeText(HomeBaseActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
			}
			// exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public List<Weather> getWeatherList() {
		return mWeathers;
	}

	public void logout() {
		User user = new User();
		MportalApplication.saveAndRefreshUser(mApp, user);
		if (MportalApplication.app.getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME_PASSWORD||MportalApplication.app.getLoginFlag()==App.LOGIN_ONSTART_USE_USERNAME_PASSWORD_ORGCODE) {
			Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
			sendBroadcast(closeI);
			Intent intent = new Intent(this, FirstLoginActity.class);
			startActivity(intent);

		} else if(MportalApplication.app.getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME){
			Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
			sendBroadcast(closeI);
			Intent intent = new Intent(this, FirstLoginActity.class);
			startActivity(intent);

		}else if(MportalApplication.app.getLoginFlag() == App.LOGIN_ONSTART_USE_PHONE_NUMBER){
			Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
			sendBroadcast(closeI);
			Intent intent = new Intent(this, FirstLoginActity.class);
			startActivity(intent);
		}else {
		}

	}
	
	

}
