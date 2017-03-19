package com.apppubs.d20.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class NewsViewPager extends ViewPager {
	private final String TAG = this.getClass().getSimpleName();
	public NewsViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v(TAG, "onTouchEvent" + event.getAction());
		return super.onTouchEvent(event);
//		return true;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
	}

	// ontcuch时事件冲突
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		Log.v(TAG, "onTouchEvent" + event.getAction());
		return super.onInterceptTouchEvent(event);
	}
	
}
