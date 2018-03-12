package com.apppubs.d20.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.AppManager;
import com.apppubs.d20.MportalApplication;
import com.apppubs.d20.R;
import com.apppubs.d20.bean.App;
import com.apppubs.d20.bean.MenuItem;
import com.apppubs.d20.bean.UserInfo;
import com.apppubs.d20.bean.Weather;
import com.apppubs.d20.constant.Actions;
import com.apppubs.d20.fragment.BaseFragment;
import com.apppubs.d20.message.model.UserBasicInfo;
import com.apppubs.d20.model.APResultCallback;
import com.apppubs.d20.service.DownloadAppService;
import com.apppubs.d20.util.FileUtils;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.SharedPreferenceUtils;
import com.apppubs.d20.util.SystemUtils;
import com.apppubs.d20.util.Tools;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

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
		//检测是否需要登录而且是否登录
		if(mAppContext.getApp().getLoginFlag()!=App.LOGIN_INAPP&&TextUtils.isEmpty(mAppContext.getCurrentUser().getUserId())){
			//跳转到登录界面
			startActivity(FirstLoginActity.class);
			finish();
		}

		// 此时如果客户端是不需要登陆的则需要注册设备账号
		if (mAppContext.getApp().getLoginFlag() == App.LOGIN_INAPP) {
			mUserBussiness.registerDevice(null);
		}

		LogM.log(this.getClass(), " HomeActivity onCreate");
		if (AppContext.getInstance(mContext).getCurrentUser().getMenuPower() != null) {
			String menuPower = AppContext.getInstance(mContext).getCurrentUser().getMenuPower();
			String[] menus = menuPower.split(",");
			String sqlParam = "";
			for (int i = -1; ++i < menus.length; ) {
				if (i != 0) {
					sqlParam += ",";
				}
				sqlParam += "'" + menus[i] + "'";
			}
			mPrimaryMenuList = SugarRecord.find(MenuItem.class, "LOCATION=? and (PROTECTED_FLAG = 0 or ID in (" + sqlParam + "))",
					new String[]{MenuItem.MENU_LOCATION_PRIMARY + ""}, null,
					"SORT_ID", null);
		} else {

			mPrimaryMenuList = SugarRecord.find(MenuItem.class, "LOCATION=? and PROTECTED_FLAG = 0",
					new String[]{MenuItem.MENU_LOCATION_PRIMARY + ""}, null, "SORT_ID", null);
		}
		mSecondaryMenuList = SugarRecord.find(MenuItem.class, "LOCATION=?",
				new String[]{MenuItem.MENU_LOCATION_SECONDARY + ""}, null, "SORT_ID", null);
		mApp = (MportalApplication) this.getApplication();
		mViewCourier = ViewCourier.getInstance(this);

		mSystemBussiness.makeStartUpRequest();
		if (mAppContext.getAppConfig().getChatFlag().equals("1")) {
			mMsgBussiness.loginRC();
		}

		initBroadcastReceiver();

		SharedPreferenceUtils.getInstance(this).putBoolean(MPORTAL_PREFERENCE_NAME, MPORTAL_PREFERENCE_APP_RUNNING_KEY, true);



		mUserBussiness.updateUserInfo(this, new APResultCallback<UserInfo>() {
			@Override
			public void onDone(final UserInfo obj) {

				RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

					@Override
					public io.rong.imlib.model.UserInfo getUserInfo(String userId) {
						UserBasicInfo ubi = mUserBussiness.getCachedUserBasicInfo(userId);
						io.rong.imlib.model.UserInfo ui = new io.rong.imlib.model.UserInfo(ubi.getUserId(),ubi.getTrueName(),Uri.parse(ubi.getAtatarUrl()));
						return ui;//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
					}

				}, true);
				RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
					@Override
					public boolean onReceived(Message message, int i) {
						return false;
					}
				});
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
	 */
	public static void refreshWether(Context context, ArrayList<Weather> weathers) {
		mWeathers = weathers;
		FileUtils.writeObj(context, weathers, "weathers.cfg");
	}

	protected abstract void changeContent(BaseFragment fragment);

	protected abstract void setUnreadNumForMenu(String menuId, int num);

	@Override
	protected void onResume() {
		super.onResume();

		String paddingUrl = mAppContext.getApp().getPaddingUrlOnHomeActivityStartUp();
		if (!TextUtils.isEmpty(paddingUrl)) {
			mAppContext.getApp().setPaddingUrlOnHomeActivityStartUp(null);
			if (paddingUrl.startsWith("apppubs://message")){
				for (MenuItem mi:mPrimaryMenuList){
					if (mi.getUrl().startsWith("apppubs://message")){
						mViewCourier.executeInHomeActivity(mi,HomeBaseActivity.this);

						break;
					}
				}
			}else{
				ViewCourier.getInstance(mContext).execute(mContext, paddingUrl);
			}
		}

		mAppContext.showCompelMessageIfHave();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
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
		Intent closeService = new Intent("com.apppubs.d20.stopdownload");
		sendBroadcast(closeService);
		stopService(new Intent(HomeBaseActivity.this, DownloadAppService.class));

		// 记录此次运行的版本
//
//		App app = mAppContext.getApp();
//		app.setPreWorkingVersion(Utils.getVersionCode(mContext));
//		mAppContext.setApp(app);
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

		Intent intent = null;
		AppContext appContext = AppContext.getInstance(fromActivy);
		if (appContext.getApp().getLoginFlag()!=App.LOGIN_INAPP){
			UserInfo currentUser = AppContext.getInstance(fromActivy).getCurrentUser();

			int layout = AppContext.getInstance(fromActivy).getApp().getLayoutLocalScheme();

			if (isNeedLogin(appContext)){
				intent = new Intent(fromActivy,FirstLoginActity.class);
			}else{
				if (layout == App.STYLE_SLIDE_MENU) {
					intent = new Intent(fromActivy, HomeSlideMenuActivity.class);
				} else {
					intent = new Intent(fromActivy, HomeBottomMenuActivity.class);
				}
			}
		}else{
			int layout = AppContext.getInstance(fromActivy).getApp().getLayoutLocalScheme();
			if (layout == App.STYLE_SLIDE_MENU) {
				intent = new Intent(fromActivy, HomeSlideMenuActivity.class);
			} else {
				intent = new Intent(fromActivy, HomeBottomMenuActivity.class);
			}
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		fromActivy.startActivity(intent);

	}

	private static boolean isNeedLogin(AppContext context) {
		return context.getCurrentUser()==null|| TextUtils.isEmpty(context.getCurrentUser().getUserId());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mViewCourier.destory();
		SharedPreferenceUtils.getInstance(this).putBoolean(MPORTAL_PREFERENCE_NAME, MPORTAL_PREFERENCE_APP_RUNNING_KEY, false);
		unregisterReceiver(mLogoutBR);
		AppManager.getInstant(this).destory();

		//如果是用户名密码登录，没有自动登录时候清空用户信息
		if(mAppContext.getApp().getLoginFlag()==App.LOGIN_ONSTART_USE_USERNAME_PASSWORD&&!mAppContext.getSettings().isAllowAutoLogin()){
			mAppContext.clearCurrentUser();
		}
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
				RongIM.getInstance().disconnect();
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
		AppContext.getInstance(mContext).setCurrentUser(new UserInfo());
		if (mAppContext.getApp().getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME_PASSWORD || mAppContext.getApp().getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME_PASSWORD_ORGCODE) {
			Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
			sendBroadcast(closeI);
			Intent intent = new Intent(this, FirstLoginActity.class);
			startActivity(intent);

		} else if (mAppContext.getApp().getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME) {
			Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
			sendBroadcast(closeI);
			Intent intent = new Intent(this, FirstLoginActity.class);
			startActivity(intent);

		} else if (mAppContext.getApp().getLoginFlag() == App.LOGIN_ONSTART_USE_PHONE_NUMBER) {
			Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
			sendBroadcast(closeI);
			Intent intent = new Intent(this, FirstLoginActity.class);
			startActivity(intent);
		} else {
		}
	}

	public void selectMessageFragment() {
		for (int i = -1; ++i < mPrimaryMenuList.size(); ) {
			MenuItem mi = mPrimaryMenuList.get(i);
			if (mi.getUrl().contains("apppubs://message")) {
				selectTab(i);
			}
		}
	}

	protected abstract void selectTab(int index);

}
