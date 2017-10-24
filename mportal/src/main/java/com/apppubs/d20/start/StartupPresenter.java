package com.apppubs.d20.start;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.bean.App;
import com.apppubs.d20.model.APResultCallback;
import com.apppubs.d20.model.SystemBussiness;
import com.apppubs.d20.model.VersionInfo;
import com.apppubs.d20.service.DownloadAppService;
import com.apppubs.d20.util.Utils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangwen on 2017/9/5.
 */

public class StartupPresenter {

	private final long SKIP_MILLIS = 5*1000;
	private SystemBussiness mSystemBussiness;
	private IStartUpView mView;
	private Context mContext;
	private boolean isStartCanceled;

	public StartupPresenter(Context context, IStartUpView view) {
		mContext = context;
		mSystemBussiness = SystemBussiness.getInstance(context);
		mView = view;
	}

	public void onStart() {

		String startUpPic = AppContext.getInstance(mContext).getApp().getStartUpPic();
		if (!TextUtils.isEmpty(startUpPic)){
			showBgPic(startUpPic);
		}
		if (isNeedWelcome()){
			mView.showWelcomeFragment();
		}else{
			init();
		}
	}

	public void init(){
		mSystemBussiness.initSystem(new APResultCallback<App>() {
			@Override
			public void onDone(App obj) {
				showBgPic(obj.getStartUpPic());
				checkUpdate();
			}

			@Override
			public void onException(int excepCode) {
				mView.showInitFailDialog();
			}
		});
	}

	private void showBgPic(String url){
		mView.showBgImage(url);
	}

	private boolean isNeedWelcome() {

		//版本升级,并且有欢迎图
		if(Utils.getVersionCode(mContext)>AppContext.getInstance(mContext).getApp().getPreWorkingVersion()){

			int welcomePicNum = 0;
			try {
				welcomePicNum = mContext.getAssets().list("welcome").length;
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (welcomePicNum > 0) {
				return true;
			}
		}
		return false;
	}

	private void checkUpdate() {
		mSystemBussiness.checkUpdate(mContext, new SystemBussiness.CheckUpdateListener() {

			@Override
			public void onDone(VersionInfo vi) {
				if (vi.isNeedUpdate()&&vi.isNeedAlert()) {
					String title = String.format("检查到有新版 %s", TextUtils.isEmpty(vi.getVersion()) ? "" : "V" + vi.getVersion());
					mView.showUpdateDialog(title, vi.getUpdateDescribe(), vi.getUpdateUrl(), vi.isNeedForceUpdate());
				}else{
					boolean enableSkip = Utils.getBooleanMetaValue(mContext,"ENABLE_SPLASH_SKIP");
					if (enableSkip){
						mView.showSkipBtn(SKIP_MILLIS);
					}else{
						preSkip2Home();
					}
				}
			}
		});
	}

	public void startDownloadApp(String updateUrl) {
		Intent it = new Intent(mContext, DownloadAppService.class);
		it.putExtra(DownloadAppService.SERVICRINTENTURL, updateUrl);
		it.putExtra(DownloadAppService.SERVACESHARENAME, 0);
		mContext.startService(it);
	}

	public void preSkip2Home(){
		Timer skipTimer = new Timer();
		skipTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isStartCanceled){
					return;
				}
				mView.skip2Home();
			}
		},2*1000);
	}

	public void cancelSkip2Home(){
		isStartCanceled = true;
	}


}
