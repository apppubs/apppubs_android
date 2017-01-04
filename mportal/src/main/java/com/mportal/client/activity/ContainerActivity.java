package com.mportal.client.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.mportal.client.R;
import com.mportal.client.fragment.BaseFragment;

/**
 * 充当容器的activity
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年1月25日 by zhangwen create
 * 
 */
public class ContainerActivity<T> extends BaseActivity {

	public static final String EXTRA_FRAGMENT_CLASS_NAME = "class_name";
	public static final String EXTRA_STRING_TITLE = "title";
	public static final String EXTRA_BOOLEAN_IS_FULLSCREEN = "is_full_screen";

	private BaseFragment mFrg;
	private boolean mIsFullScreen;

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		Bundle bundle = getIntent().getExtras();
		String className = bundle.getString(EXTRA_FRAGMENT_CLASS_NAME);
		String title = bundle.getString(EXTRA_STRING_TITLE);
		mIsFullScreen = bundle.getBoolean(EXTRA_BOOLEAN_IS_FULLSCREEN);
		setNeedTitleBar(!mIsFullScreen);
		setContentView(R.layout.act_container);

		if (title != null)
			setTitle(title);

		try {
			Class<?> fragmentC = Class.forName(className);
			mFrg = (BaseFragment) fragmentC.newInstance();
			mFrg.setArguments(bundle);

		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		}
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container_fg, mFrg);
		transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
		transaction.commit();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mFrg.onKeyDown(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	
	
	
	
	/**
	 * 打开一个装有某fragment的activity
	 * 
	 * @param context
	 * @param frgClass
	 */
	
	public static void startActivity(Context context, Class<? extends BaseFragment> frgClass,boolean isFullScreen) {
		Bundle args = new Bundle();
		args.putBoolean(EXTRA_BOOLEAN_IS_FULLSCREEN, isFullScreen);
		startActivity(context, frgClass, args, null);
	}
	public static void startActivity(Context context, Class<? extends BaseFragment> frgClass) {
		startActivity(context, frgClass, null, null);
	}

	public static void startActivity(Context context, Class<? extends BaseFragment> frgClass, Bundle extras) {
		startActivity(context, frgClass, extras, null);
	}

	/**
	 * 打开容器activity
	 * 
	 * @param context
	 * @param frgClass
	 * @param extras
	 * @param title
	 */
	public static void startActivity(Context context, Class<? extends BaseFragment> frgClass, Bundle extras,
			String title) {
		Intent i = new Intent(context, ContainerActivity.class);
		i.putExtra(EXTRA_FRAGMENT_CLASS_NAME, frgClass.getName());

		if (extras != null)
			i.putExtras(extras);

		if (title != null) {
			i.putExtra(EXTRA_STRING_TITLE, title);
		}

		
		context.startActivity(i);
	}
	

	/**
	 * 打开容易activity，期望得到返回值
	 * @param context
	 * @param frgClass
	 * @param extras
	 * @param title
	 */
	public static void startActivityForResult(Activity context, Class<? extends BaseFragment> frgClass, Bundle extras,
			String title,int requestCode) {
		Intent i = new Intent(context, ContainerActivity.class);
		i.putExtra(EXTRA_FRAGMENT_CLASS_NAME, frgClass.getName());

		if (extras != null)
			i.putExtras(extras);

		if (title != null) {
			i.putExtra(EXTRA_STRING_TITLE, title);
		}

		context.startActivityForResult(i, requestCode);
	}

}
