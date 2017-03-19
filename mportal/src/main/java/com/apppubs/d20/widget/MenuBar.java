package com.apppubs.d20.widget;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apppubs.d20.R;
import com.apppubs.d20.bean.MenuItem;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 主界面底部的菜单
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年1月15日 by zhangwen create
 *
 */
public class MenuBar extends LinearLayout implements OnClickListener{

	private Context mContext;
	private int mMenuNum;
	private Map<String,View> mMenuViewMap;
	private OnItemClickListener mOnItemClickListener;
	public interface OnItemClickListener{
		void onItemClick(int position);
	}
	
	
	public MenuBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mMenuViewMap = new HashMap<String, View>();
	}
	
	public void addMenuItem(MenuItem item){
		
		FrameLayout itemFrameLayout = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.item_menu_bottom, null);
		itemFrameLayout.setTag(mMenuNum);
		TextView tv = (TextView) itemFrameLayout.findViewById(R.id.menu_bottom_tv);
		ImageView iv = (ImageView) itemFrameLayout.findViewById(R.id.menu_buttom_iv);
		itemFrameLayout.setOnClickListener(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
		lp.weight = 1;
		tv.setText(item.getName());
		iv.setColorFilter( getResources().getColor(R.color.menubar_default), Mode.SRC_ATOP);

		ImageLoader.getInstance().displayImage(item.getIconpic(), iv);
		addView(itemFrameLayout,lp);
		mMenuViewMap.put(item.getId(), itemFrameLayout);
		mMenuNum++;
	}
	
	public void setUnreadNumForMenu(String menuId,int num){
		FrameLayout fl = (FrameLayout) mMenuViewMap.get(menuId);
		TextView unreadTv = (TextView) fl.findViewById(R.id.menu_buttom_iv_unread_tv);
		if(num<100){
			unreadTv.setText(num+"");
		}else{
			unreadTv.setText("99+");
		}
		if(num<1){
			unreadTv.setVisibility(View.GONE);
		}else{
			unreadTv.setVisibility(View.VISIBLE);
		}
	}
	
	public void setOnItemClickListener(OnItemClickListener listener){
		this.mOnItemClickListener = listener;
	}

	@Override
	public void onClick(View v) {
		if(mOnItemClickListener!=null){
			mOnItemClickListener.onItemClick(Integer.parseInt(v.getTag().toString()));
		}
	}
	
	
	
	
	
}
