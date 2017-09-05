package com.apppubs.d20.start;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.apppubs.d20.R;
import com.apppubs.d20.activity.BaseActivity;
import com.apppubs.d20.activity.HomeBaseActivity;
import com.apppubs.d20.fragment.WelcomeFragment;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.widget.AlertDialog;
import com.apppubs.d20.widget.AlertDialog.OnOkClickListener;
import com.apppubs.d20.widget.ConfirmDialog;
import com.apppubs.d20.widget.ConfirmDialog.ConfirmListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

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
public class StartUpActivity extends BaseActivity implements IStartUpView {

	private final String TAG_FRAGMENT_WELCOME = "TAG_FRAGMENT_WELCOME";
	
	private DisplayImageOptions mImageLoaderOptions;
	private ImageView mStartupIv;
	private StartupPresenter mPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setNeedTitleBar(false);
		setContentView(R.layout.act_start);
		mPresenter = new StartupPresenter(this,this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPresenter.onStart();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		hideWelcomeFragment();
		mPresenter.init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, R.anim.zoom_fade_out);
		mPresenter.cancelSkip2Home();
	}

	@Override
	public void showWelcomeFragment() {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		Fragment welcome = new WelcomeFragment();
		transaction.add(R.id.startup_frg_con,welcome,TAG_FRAGMENT_WELCOME);
		transaction.commitAllowingStateLoss();
	}

	@Override
	public void hideWelcomeFragment() {
		FragmentManager manager = getSupportFragmentManager();
		Fragment fragment = manager.findFragmentByTag(TAG_FRAGMENT_WELCOME);
		if (fragment!=null){
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.remove(fragment);
			transaction.commitAllowingStateLoss();
		}
	}

	@Override
	public void showBgImage(String url) {
		mImageLoaderOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.transparent)
				.showImageForEmptyUri(R.drawable.transparent).showImageOnFail(R.drawable.transparent)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
				.build();

		mStartupIv = (ImageView) findViewById(R.id.startup_iv);
		mImageLoader.displayImage(url,mStartupIv,mImageLoaderOptions);
	}

	@Override
	public void showSkipBtn(long millis) {
		ProgressSkipView skipView = (ProgressSkipView) findViewById(R.id.act_start_skip_view);
		skipView.setVisibility(View.VISIBLE);
		skipView.startProgress(millis, new ProgressSkipView.SkipListener() {
			@Override
			public void onClick() {
				skip2Home();
			}

			@Override
			public void onComplete() {
				skip2Home();
			}
		});
	}

	@Override
	public void skip2Home() {
		HomeBaseActivity.startHomeActivity(StartUpActivity.this);
		finish();
	}

	@Override
	public void showUpdateDialog(String title, String message, final String updateUrl, boolean needForceUpdate) {
		if (needForceUpdate) {
			AlertDialog ad = new AlertDialog(StartUpActivity.this, new OnOkClickListener() {

				@Override
				public void onclick() {
					mPresenter.startDownloadApp(updateUrl);
					Toast.makeText(mContext, "正在下载中，请稍候", Toast.LENGTH_SHORT).show();
				}
			}, title, message, "更新");
			ad.show();
			ad.setCancelable(false);
			ad.setCanceledOnTouchOutside(false);
		} else {
			ConfirmDialog dialog = new ConfirmDialog(StartUpActivity.this, new ConfirmListener() {

				@Override
				public void onCancelClick() {
					skip2Home();
				}

				@Override
				public void onOkClick() {
					mPresenter.startDownloadApp(updateUrl);
					Toast.makeText(mContext, "正在下载中，请稍候", Toast.LENGTH_SHORT).show();
				}
			}, title, message, "下次", "更新");
			dialog.show();
			dialog.setCanceledOnTouchOutside(false);
		}

	}

	@Override
	public void showInitFailDialog() {
		Dialog confirmDialog = new ConfirmDialog(this, new ConfirmListener() {

			@Override
			public void onOkClick() {
				mPresenter.init();
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
}
