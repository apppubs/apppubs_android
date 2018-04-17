package com.apppubs.ui.fragment;
/**
 * 默认的fragment构造工厂
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年1月25日 by zhangwen create
 *
 */
public class CommonFragmentFactory {
	
	public static final int TYPE_WEIBO = 1;//微博fragment
	
	

	public static BaseFragment getFragment(int type){
		switch (type) {
		case TYPE_WEIBO:
			return new WeiBoFragment();
		
		}
		return null;
	}
	
	
}
