package com.apppubs.d20.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PictureInfoViewPager extends ViewPager{

	private DragImageView mCurrentDragImageView;
	
	private float lastX,lastY;
	public PictureInfoViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
//		float curX = event.getRawX();
//		if(mCurrentDragImageView.isScaled()&&(!mCurrentDragImageView.isRightEdgeVisiable()&&!mCurrentDragImageView.isLeftEdgeVisiable())){
//			return false;
//		}
//		lastX = curX;
		return super.onInterceptTouchEvent(event);
	}
	
	public void setCurrentDragImageView(DragImageView currentView){
		mCurrentDragImageView = currentView;
	}

}
