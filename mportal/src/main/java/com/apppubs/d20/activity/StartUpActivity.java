package com.apppubs.d20.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.bean.UserInfo;
import com.apppubs.d20.constant.Constants;
import com.apppubs.d20.model.APResultCallback;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.R;
import com.apppubs.d20.asytask.AsyTaskCallback;
import com.apppubs.d20.asytask.AsyTaskExecutor;
import com.apppubs.d20.bean.App;
import com.apppubs.d20.model.SystemBussiness;
import com.apppubs.d20.fragment.WelcomeFragment;
import com.apppubs.d20.service.DownloadAppService;
import com.apppubs.d20.util.Utils;
import com.apppubs.d20.widget.AlertDialog;
import com.apppubs.d20.widget.AlertDialog.OnOkClickListener;
import com.apppubs.d20.widget.ConfirmDialog;
import com.apppubs.d20.widget.ConfirmDialog.ConfirmListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import cn.jpush.android.api.JPushInterface;

/**
 * app入口
 * 
 * 1.当前的app有欢迎图且欢迎图尚未展示则进行展示否则跳过
 * 2.初始化组建
 * 3.显示启动背景
 * 4.初始化系统
 * 6.系统初始化完成之后如果是利用用户名密码登录且是自动登录时，做异步操作，1.载入图片，2.验证登录，3.版本检测
 * 7.各项操作均完成之后判断是否是第一次启动，如果是则显示欢迎页面
 * 8.启动验证各项操作完成情况并进入主界面或者登录界面
 * 
 * 
 */
public class StartUpActivity extends BaseActivity implements AsyTaskCallback{

	private final int VERIFY_USER_TASK_TAG = 2;
	
	private final String SERVER_MIN_SUPPORT_APP_CODE_VERSION = "min_android_code_version";
	
	private final int MESSAGE_EXCEPTION = 2;
	private final int MESSAGE_REFRESH_IV = 3;

	private DisplayImageOptions mImageLoaderOptions;
	private ImageView mStartupIv;
	private ProgressBar mPb;
	private boolean isSystemInitCompleted;// 刷新菜单是否完成
	private boolean isImageLoadComplete;// 图片是否加载完成
	private boolean isVerfyUserCompleted;// 自动登录时验证密码完成
	private boolean isVerfyUserSuccess;// 用户验证是否成功
	private boolean isVersionChecked;//版本是否检测完成
	private boolean isGuidePlayed;// 引导图是否播放
	private String curStartUpPicURL;
	private Future<?> mFuture;

	private static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
		};
	};

	/**
	 * 循环执行的线程，每1秒检测执行一次
	 */
	private Runnable enterHomeRun = new Runnable() {
		@Override
		public void run() {
			if ((mSystemBussiness.isInitialized()&&mAppContext.getApp().getInitTimes() < 2)||(mSystemBussiness.isInitialized()&&isVersionChecked)) {

				int welcomePicNum = 0;
				try {
					welcomePicNum = getAssets().list("welcome").length;
				} catch (IOException e) {
					e.printStackTrace();
				}

				// 有欢迎图而且系统初始化次数为0
				if (welcomePicNum > 0 && mAppContext.getApp().getInitTimes() < 2&&!mAppContext.getApp().isWelcomePlayed()) {

					isGuidePlayed = true;
					startWelcome();
					App app = mAppContext.getApp();
					app.setWelcomePlayed(true);
					mAppContext.setApp(app);
					mAppContext.serializeApp();
				} else {
					HomeBaseActivity.startHomeActivity(StartUpActivity.this);
					finish();
				}

			} else {
				mHandler.postDelayed(enterHomeRun, 1000);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
		setNeedTitleBar(false);
		setContentView(R.layout.act_start);
		initComponents();
		startMainLoop();

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	public void onEvent(MessageEvent event) {
		super.onEvent(event);
		if (event.tag== Constants.MESSAGE_EVENT_INIT_APP){
			if(mAppContext.getApp().getInitTimes()>0){
				onInitDone();
			}else {
				showErrorDialog();
			}
		}
	}

	private void startWelcome() {

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		Fragment welcome = new WelcomeFragment();
		transaction.add(R.id.startup_frg_con, welcome);
		transaction.commit();
		setVisibilityOfViewByResId(R.id.startup_iv, View.GONE);

	}

	// 进入主流程
	private void startMainLoop() {
		displayBackgroundPic();
		checkUpdate();
		mHandler.postDelayed(enterHomeRun, 1000);
	}

	protected void checkUpdate(){
		mSystemBussiness.checkUpdate(StartUpActivity.this, new SystemBussiness.CheckUpdateListener() {

			@Override
			public void onDone(boolean needUpdate, boolean needForceUpdate, String version, String updateDescribe, final String updateUrl) {
				if (needUpdate) {
					String title = String.format("检查到有新版 %s", TextUtils.isEmpty(version) ? "" : "V" + version);
					if (needForceUpdate) {
						AlertDialog ad = new AlertDialog(StartUpActivity.this, new OnOkClickListener() {

							@Override
							public void onclick() {
								Intent it = new Intent(StartUpActivity.this, DownloadAppService.class);
								it.putExtra(DownloadAppService.SERVICRINTENTURL, updateUrl);
								it.putExtra(DownloadAppService.SERVACESHARENAME, 0);
								startService(it);
								Toast.makeText(StartUpActivity.this, "正在下载中，请稍候", Toast.LENGTH_SHORT).show();
							}
						}, title, updateDescribe, "更新");
						ad.show();
						ad.setCancelable(false);
						ad.setCanceledOnTouchOutside(false);
					} else {
						ConfirmDialog dialog = new ConfirmDialog(StartUpActivity.this, new ConfirmListener() {

							@Override
							public void onCancelClick() {
								isVersionChecked = true;
							}

							@Override
							public void onOkClick() {
								isVersionChecked = true;
								Intent it = new Intent(StartUpActivity.this, DownloadAppService.class);
								it.putExtra(DownloadAppService.SERVICRINTENTURL, updateUrl);
								it.putExtra(DownloadAppService.SERVACESHARENAME, 0);
								startService(it);
								// bindService(it, conn, Context.BIND_AUTO_CREATE);
								mUserBussiness.logout(StartUpActivity.this);
							}
						}, title, updateDescribe, "下次", "更新");
						dialog.show();
						dialog.setCanceledOnTouchOutside(false);
					}
				} else {
					isVersionChecked = true;
				}
			}
		});
	}


	private void initComponents() {
		
		mPb = (ProgressBar) findViewById(R.id.startup_pb);
		mPb.getIndeterminateDrawable().setColorFilter(Color.parseColor("#1874CD"),
				android.graphics.PorterDuff.Mode.MULTIPLY);
		
	}
	
	private void displayBackgroundPic(){
		mImageLoaderOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.transparent)
				.showImageForEmptyUri(R.drawable.transparent).showImageOnFail(R.drawable.transparent)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
				.build();

		mStartupIv = (ImageView) findViewById(R.id.startup_iv);
		curStartUpPicURL = mAppContext.getApp().getStartUpPic();
		mImageLoader.displayImage(curStartUpPicURL,mStartupIv,mImageLoaderOptions);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mHandler.removeCallbacks(enterHomeRun);
		if (mFuture != null) {
			mFuture.cancel(true);
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initSystem() {
		mFuture = mSystemBussiness.initSystem(new APResultCallback<Object>() {

			@Override
			public void onException(int excepCode) {
				if(mAppContext.getApp().getInitTimes()>0){
					onInitDone();
				}else if (!StartUpActivity.this.isFinishing()){
					showErrorDialog();
				}
			}

			@Override
			public void onDone(Object obj) {

				onInitDone();

			}

		});
	}

	private void onInitDone() {
		startUpPush();
		isSystemInitCompleted = true;

		if (curStartUpPicURL == null || !curStartUpPicURL.equals(mAppContext.getApp().getStartUpPic())) {
			LogM.log(this.getClass(), "init 初始化：当前启动图：" + curStartUpPicURL);
			mImageLoader.displayImage(mAppContext.getApp().getStartUpPic(), mStartupIv, mImageLoaderOptions,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
							isImageLoadComplete = true;
						}

						@Override
						public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
							mPb.setVisibility(View.GONE);
							isImageLoadComplete = true;
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							isImageLoadComplete = true;
						}
					});
		} else {
			isImageLoadComplete = true;
			mPb.setVisibility(View.GONE);
		}

		// 用户名密码或者用户名密码和组织嘛登录方式下需要登录而且已经登录过而且是自动登录的情况下验证密码,
		UserInfo curUser = AppContext.getInstance(mContext).getCurrentUser();
		if ((mAppContext.getApp().getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME_PASSWORD) && curUser != null
				&& !TextUtils.isEmpty(curUser.getUserId())
				&& mAppContext.getSettings().isAllowAutoLogin()) {

			String deviceid = mAppContext.getApp().getPushVendorType()==App.PUSH_VENDOR_TYPE_BAIDU?mAppContext.getApp().getBaiduPushUserId():JPushInterface.getRegistrationID(this);// 百度硬件设备号
			String systemVresion = Utils.getAndroidSDKVersion();// 操作系统号
			String currentVersionCode = Utils.getVersionName(StartUpActivity.this);// app版本号
			String token = mAppContext.getApp().getPushVendorType() == App.PUSH_VENDOR_TYPE_BAIDU ? mAppContext.getApp()
					.getBaiduPushUserId() : JPushInterface.getRegistrationID(this);// 百度硬件设备号
			String[] params = new String[]{curUser.getUsername(), curUser.getPassword(), deviceid,token, Build.MODEL,
					systemVresion, currentVersionCode,"true"};

			AsyTaskExecutor.getInstance().startTask(VERIFY_USER_TASK_TAG, StartUpActivity.this, params);
		} else {

			isVerfyUserCompleted = true;
			isVerfyUserSuccess = true;
		}
	}

	private void showErrorDialog() {
		Dialog confirmDialog = new ConfirmDialog(this, new ConfirmListener() {

			@Override
			public void onOkClick() {
				initSystem();
			}

			@Override
			public void onCancelClick() {
				StartUpActivity.this.finish();
			}
		}, "网络异常", "请检查网络是否通畅，然后重试!", "关闭", "重试");
		confirmDialog.setCancelable(false);
		confirmDialog.setCanceledOnTouchOutside(false);
		confirmDialog.show();

	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, R.anim.zoom_fade_out);
	}

	/**
	 * 启动推送
	 */
	private void startUpPush() {
		
		switch (mAppContext.getApp().getPushVendorType()) {
		case App.PUSH_VENDOR_TYPE_BAIDU:
			List<String> tagList = new ArrayList<String>();
			tagList.add(mAppContext.getApp().getCode());
			LogM.log(this.getClass(), "启动百度推送，百度推送已经弃用在20000的版本中已经被移除：" + mAppContext.getApp().getBaiduPushApiKey());
			break;
		case App.PUSH_VENDOR_TYPE_JPUSH:
			LogM.log(this.getClass(), "启动极光推送，百度推送已经弃用在20000的版本中已经被移除：" );
			JPushInterface.setDebugMode(true);
			JPushInterface.init(this);
			String resigtrationId = JPushInterface.getRegistrationID(this);
			if(!TextUtils.isEmpty(resigtrationId)){
				App app = mAppContext.getApp();
				app.setJpushRegistrationID(resigtrationId);
				mAppContext.setApp(app);
				mAppContext.serializeApp();
			}
			LogM.log(this.getClass(), "jpush注册id" + resigtrationId);
			break;
		default:
			break;
		}

	}

	@Override
	public Object onExecute(Integer tag, String[] params) {
	 if(tag==VERIFY_USER_TASK_TAG){
			
//			String[] params = new String[]{curUser.getUsername(), curUser.getPassword(), deviceid, Build.MODEL,
//					systemVresion, currentVersionCode,"true"};
			Integer result = mUserBussiness.login(params[0], params[1], params[2], params[3], params[4], params[5], params[6],true);
			
			return result;
		}
		return null;
		
	}

	@Override
	public void onTaskSuccess(Integer tag, Object obj) {
		
	 if(tag==VERIFY_USER_TASK_TAG){
			if (Integer.parseInt(obj.toString()) == 2) {
				isVerfyUserSuccess = true;
			} else {
				isVerfyUserSuccess = false;
			}
			isVerfyUserCompleted = true;
		}
	}

	@Override
	public void onTaskFail(Integer tag,Exception e) {

	}

}
