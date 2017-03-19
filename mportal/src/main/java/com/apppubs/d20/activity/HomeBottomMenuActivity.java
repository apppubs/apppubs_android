package com.apppubs.d20.activity;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.d20.bean.MenuItem;
import com.apppubs.d20.constant.Actions;
import com.apppubs.d20.fragment.BaseFragment;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.widget.MenuBar;
import com.apppubs.d20.R;

/**
 * 区别于左右菜单的主界面此主界面为底部菜单类似zaker
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年1月15日 by zhangwen create
 *
 */
public class HomeBottomMenuActivity extends HomeBaseActivity {
	
	private MenuBar mMenuBar;
	/**
	 * 底部菜单数量最大值
	 */
	private final int MAX_MENU_NUM = 5;
//	private MenuItem[] miArr;
	private int mCurPos;
	private int mIntentCurPos;
	private int mMenuBarBtnDefaultColor;
	private BroadcastReceiver mLogoutBr;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		LogM.log(this.getClass(), " HomeBottomMenuActivity onCreate");
		super.onCreate(arg0);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		setContentView(R.layout.act_home_bottommenu);
		initComponent();
		
		initMenu();
		mLogoutBr = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				onLogout();
			}

			private void onLogout() {
				selectMenu(0);
			}
		};
		registerReceiver(mLogoutBr, new IntentFilter(Actions.ACTION_LOGOUT));
	}
	@Override
	protected void onResume() {
		
		super.onResume();
		
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		
		LogM.log(this.getClass(), "onSaveInstanceState-->调用");
	}
	
	
	private void initComponent(){
		mMenuBarBtnDefaultColor = getResources().getColor(R.color.menubar_default);
		mMenuBar = (MenuBar) findViewById(R.id.home_bottom_menubar);
		int size = mPrimaryMenuList.size();
		
		if(size==1){
			mMenuBar.setVisibility(View.GONE);
//			miArr = new MenuItem[1];
//			miArr[0] = mPrimaryMenuList.get(0);
		} else {

//			int loopTimes = size > MAX_MENU_NUM ? MAX_MENU_NUM : size;
//			miArr = new MenuItem[loopTimes];
//			for (int i = -1; ++i < loopTimes;) {
//				miArr[i] = mPrimaryMenuList.get(i);
//				LogM.log(this.getClass(), "增加菜单：" + miArr[i].getName());
//			}

		}
		
	}
	
	private void initMenu(){
		int size = mPrimaryMenuList.size();
		for(int i=-1;++i<size;){
			mMenuBar.addMenuItem(mPrimaryMenuList.get(i));
		}
		mMenuBar.setOnItemClickListener(new MenuBar.OnItemClickListener() {
			@Override
			public void onItemClick(int position) {
				selectMenu(position);
			}
		});
		if(mPrimaryMenuList!=null&&mPrimaryMenuList.size()>0){//判断是否有菜单如果没有提示用户配置菜单
			selectMenu(mCurPos);
		}else{
			Toast.makeText(this, "请配置菜单", Toast.LENGTH_LONG).show();
		}
	
	}

	/**
	 * 选择某个菜单
	 * @param position
	 */
	private void selectMenu(int position){
		mIntentCurPos = position;
		MenuItem mi = mPrimaryMenuList.get(position);
		if(ViewCourier.openLoginViewIfNeeded(mi.getUrl(), this)){
			return ;
		}
//		MenuItem mi = miArr[position];
		mViewCourier.executeInHomeActivity(mi,this);
		
		ImageView ivC = (ImageView) mMenuBar.getChildAt(mCurPos).findViewById(R.id.menu_buttom_iv);
		ivC.setColorFilter(mMenuBarBtnDefaultColor,Mode.SRC_ATOP);
		TextView tvC = (TextView) mMenuBar.getChildAt(mCurPos).findViewById(R.id.menu_bottom_tv);
		tvC.setTextColor(Color.parseColor("#8c8c8c"));
		
		ImageView iv = (ImageView) mMenuBar.getChildAt(position).findViewById(R.id.menu_buttom_iv);
		iv.setColorFilter( mThemeColor, Mode.SRC_ATOP);
		TextView tv = (TextView) mMenuBar.getChildAt(position).findViewById(R.id.menu_bottom_tv);
		tv.setTextColor(mThemeColor);
		
		mCurPos = position;
	}
	
	private Fragment mCurFrg;
	@Override
	protected void changeContent(BaseFragment fragment) {

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if (fragments==null||!fragments.contains(fragment)) {
			transaction.add(R.id.home_bottom_container_fgm, fragment);
		}
		if (mCurFrg != null){
			transaction.hide(mCurFrg);
		}
		transaction.show(fragment);
		mCurFrg = fragment;
		transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
		transaction.commitAllowingStateLoss();
		mTitleBar.clearLeftAndRight();
	}
	
	
	@Override
	protected void setUnreadNumForMenu(String menuId, int num) {
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mViewCourier.destory();
		if(mLogoutBr!=null){
			unregisterReceiver(mLogoutBr);
		}
		LogM.log(this.getClass(), "销毁HomeBottomMenuActivity");
	}

	@Override
	protected void selectTab(int index) {
		selectMenu(index);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(requestCode==LoginActivity.REQUEST_CODE&&resultCode==RESULT_OK){
			selectMenu(mIntentCurPos);
		}
	}
	
	
}
