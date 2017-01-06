package com.mportal.client.widget;

import android.content.Context;
import android.widget.Scroller;

public class FixedViewPagerScroller extends Scroller {

	private int mScrollDuration = 800;// 滑动速度

	public FixedViewPagerScroller(Context context) {
		super(context);
	}
	
	public FixedViewPagerScroller(Context context,int millisecond){
		super(context);
		mScrollDuration = millisecond;
	}


	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, mScrollDuration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		super.startScroll(startX, startY, dx, dy, mScrollDuration);
	}

}
