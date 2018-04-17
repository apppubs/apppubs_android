package com.apppubs.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.webkit.WebView;

public class EpaperWebView extends WebView {
	
	public static String INTENT_ACTION_SWIP = ".SWIP";//广播action名称
	public static String INTENT_ACTION_SWIP_TYPE = "type";//滑动类型字段
	public static String INTENT_ACTION_SWIP_TYPE_UP = "up";//向上滑动
	public static String INTENT_ACTION_SWIP_TYPE_DOWN = "down";//向下滑动
	
	private static final String TAG = null;
	private Context mContext;
	private OnStopListener mOnStopListener;
	private boolean mIsRendering;
	/**渲染结束时间，如果触摸时间发生的时间在此之前则忽略*/
	private long mRenderDoneTime = -1;
	private long mRenderStartTime = -1;
	private Runnable mR;
	public EpaperWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	 private long lastScrollUpdate = -1;

	private class ScrollStateHandler implements Runnable {

		@Override
		public void run() {
			if ((SystemClock.uptimeMillis() - lastScrollUpdate) > 100) {
				if(mOnStopListener==null) return;
				lastScrollUpdate = -1;
				mOnStopListener.onRender(getScrollX(), getScrollY(), 0, 0);
			} else {
				postDelayed(this, 100);
			}
		}
	}	
	
	
	boolean shouldShowIv = true;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		long eventTime = event.getEventTime();
		//如果此触摸时间是在渲染时发送则直接忽略
		if(eventTime<mRenderDoneTime&&eventTime>mRenderStartTime) return true;
		super.onTouchEvent(event);
//		gesturedetector.onTouchEvent(event);
		if(mOnStopListener!=null&&event.getAction()==MotionEvent.ACTION_DOWN){
			mOnStopListener.onPrepare();
			shouldShowIv = true;
		}else if(mOnStopListener!=null&&event.getAction()==MotionEvent.ACTION_MOVE){
			shouldShowIv = false;
		}else if(shouldShowIv&&mOnStopListener!=null&&event.getAction()==MotionEvent.ACTION_UP){
			mOnStopListener.onCancel();
			this.removeCallbacks(mR);
		}
		
		return true;
	}
	
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		

			
		if(this.getUrl()!=null&&this.getUrl().equals("about:black")) return;
		if (lastScrollUpdate == -1) {
			shouldShowIv = false;
			mRenderStartTime = SystemClock.uptimeMillis();
			mR = new ScrollStateHandler();
            postDelayed(mR, 100);
        }

        lastScrollUpdate = SystemClock.uptimeMillis();
	}
	public void setOnStopListener(OnStopListener onStopListener) {

		this.mOnStopListener = onStopListener;
	}


	/**滚动停止或者缩放停止监听*/
	public interface OnStopListener{
		void onRender(int x,int y,int width,int height);
		void onPrepare();
		void onCancel();
	}
	// Private class for gestures
	private class SwipeGestureDetector extends SimpleOnGestureListener {
		// Swipe properties, you can change it to make the swipe
		// longer or shorter and speed
		private static final int SWIPE_MIN_DISTANCE = 80;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			Log.v(TAG, "手势");
			float diffy = e1.getY() - e2.getY();
			if (diffy > SWIPE_MIN_DISTANCE) {
				Intent intent = new Intent(mContext.getPackageName()+INTENT_ACTION_SWIP);
				intent.putExtra(INTENT_ACTION_SWIP_TYPE, INTENT_ACTION_SWIP_TYPE_UP);
				mContext.sendBroadcast(intent);
				Log.v(TAG,"向上手势");
			} else if (-diffy > SWIPE_MIN_DISTANCE) {
				Intent intent = new Intent(mContext.getPackageName()+INTENT_ACTION_SWIP);
				intent.putExtra(INTENT_ACTION_SWIP_TYPE, INTENT_ACTION_SWIP_TYPE_DOWN);
				mContext.sendBroadcast(intent);
				Log.v(TAG,"向下手势");
			} else {
				return false;
			}
			return false;
		}
		
	}
	
	public void setIsRendering(boolean isRender){
		this.mIsRendering = isRender;
	}
	
	public boolean isRendering(){
		return mIsRendering;
	}


	public long getRenderDoneTime() {
		return mRenderDoneTime;
	}


	public void setRenderDoneTime(long renderDoneTime) {
		this.mRenderDoneTime = renderDoneTime;
	}
	

}
