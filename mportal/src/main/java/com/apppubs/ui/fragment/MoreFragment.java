package com.apppubs.ui.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.apppubs.bean.TMenuItem;
import com.apppubs.d20.R;
import com.apppubs.util.Utils;

/**
 * 底部菜单样式的更多界面
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年1月16日 by zhangwen create
 * 
 */
public class MoreFragment extends HomeFragment implements OnClickListener{

	private List<TMenuItem> mSquareMenuList;
	private LinearLayout mContainerLl;
	private int mDividerColor;

	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.frg_more, null);
		mContainerLl = (LinearLayout) mRootView.findViewById(R.id.more_ll);
		mDividerColor = getResources().getColor(R.color.common_divider);
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mSquareMenuList = mHostActivity.getSecondaryMenuList();
		addDivider();
		int size = mSquareMenuList.size();
		for(int i=-1;++i<size;){
			TMenuItem mi = mSquareMenuList.get(i);
			View v = LayoutInflater.from(mHostActivity).inflate(R.layout.item_more_lv, null);
			TextView tv = (TextView) v.findViewById(R.id.item_more_tv);
			ImageView iv = (ImageView) v.findViewById(R.id.item_more_iv);
			View line = v.findViewById(R.id.line);
			
			
			tv.setText(mi.getName());
			// 填充数据
			if (mi.getIconpic() != null) {
				iv.setVisibility(View.VISIBLE);
				mImageLoader.displayImage(mi.getIconpic(), iv);
			} else {
				iv.setVisibility(View.INVISIBLE);
			}
			if(i==size-1){
				line.setVisibility(View.GONE);
			}
			v.setTag(mi);
			v.setOnClickListener(this);
			mContainerLl.addView(v);
		}
		addDivider();
		View view1 = new View(mHostActivity);
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Utils.dip2px(mHostActivity, 20));
		mContainerLl.addView(view1, lp1);
		addDivider();
		View v = LayoutInflater.from(mHostActivity).inflate(R.layout.item_more_lv, null);
		TextView tv = (TextView) v.findViewById(R.id.item_more_tv);
		ImageView iv = (ImageView) v.findViewById(R.id.item_more_iv);
		View lineV = v.findViewById(R.id.line);
		
		tv.setText("设置");
		iv.setImageResource(R.drawable.left_menu_setup_normal);
		lineV.setVisibility(View.GONE);
		v.setOnClickListener(this);
		mContainerLl.addView(v);
		addDivider();
		
	}


	@Override
	public void onClick(View v) {
		TMenuItem mi = null;
		if((mi=(TMenuItem) v.getTag())==null){
			mViewController.startSettingView(mHostActivity,mi.getId());
		}else{
			mViewController.executeInHomeActivity(mi,mHostActivity);
		}
	}
	
	private void addDivider() {
		View lineV = new View(mContext);
		LayoutParams lineLp = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		lineV.setBackgroundColor(mDividerColor);
		mContainerLl.addView(lineV, lineLp);
	}
}
