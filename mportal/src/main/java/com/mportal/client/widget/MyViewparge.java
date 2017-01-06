package com.mportal.client.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewparge extends ViewPager {

	public MyViewparge(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyViewparge(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		getParent().requestDisallowInterceptTouchEvent(true);// 这句话的作用
																// 告诉父view，我的单击事件我自行处理，不要阻碍我。
		return super.dispatchTouchEvent(ev);
	}
}
