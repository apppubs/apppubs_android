package com.mportal.client.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.mportal.client.R;
import com.mportal.client.util.LogM;
import com.mportal.client.util.Utils;

public class DragScheduleBar extends RelativeLayout{
	
	private View mDragDot;
	private View mScheduleLine;
	private int mWidth;
	private int mDotWidth;
	private int mCount;
	private int mCurPos;
	private float mPageScheduleWidth;
	private OnGragedListener mOnPageSelectListener;
	public interface OnGragedListener{
		void onGraged(int pos,int marginLeftPixel);
		void onDisplayChanged(int pos,boolean show);
	}
	public DragScheduleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}

	private void init(Context context) {
		mDragDot = new View(context);
		mDragDot.setBackgroundResource(R.drawable.dot);
		
		mScheduleLine = new View(context);
		mScheduleLine.setBackgroundResource(R.drawable.page_schedule_bg);
		
		RelativeLayout.LayoutParams scheduleLineLp = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dip2px(context, 3));
		scheduleLineLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		mScheduleLine.setLayoutParams(scheduleLineLp);
		addView(mScheduleLine);
		
		mDotWidth = Utils.dip2px(context, 16);
		RelativeLayout.LayoutParams dotLp = new LayoutParams(mDotWidth,mDotWidth);
		dotLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		mDragDot.setLayoutParams(dotLp);
		
		addView(mDragDot);
//		this.setOnTouchListener(new OnTouchListener() {
//			float lastX = 0;
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				
//				LogM.log(this.getClass(), "当前动作："+event.getActionMasked()+"x:"+event.getX());
//				int action = event.getActionMasked();
//				switch (action) {
//				case MotionEvent.ACTION_DOWN:
//					lastX = event.getRawX();
//					float x = event.getX();
//					moveDotTo(x);
//					if(mOnPageSelectListener!=null&&mCount!=0){
//						mOnPageSelectListener.onDisplayChanged(mCurPos,true);
//					}
//					break;
//				case MotionEvent.ACTION_MOVE:
//					float curX = event.getRawX();
//					moveDotBy(curX - lastX );
//					lastX = curX;
//					break;
//				case MotionEvent.ACTION_UP:
//					if(mOnPageSelectListener!=null&&mCount!=0){
//						mOnPageSelectListener.onDisplayChanged(mCurPos,false);
//					}
//					break;
//				default:
//					break;
//				}
//				return true;
//			}
//		});
		
		
	}
	float lastX = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		
		LogM.log(this.getClass(), "当前动作："+event.getActionMasked()+"x:"+event.getX());
		int action = event.getActionMasked();
		float x = event.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			lastX = event.getRawX();
			moveDotTo(x);
			if(mOnPageSelectListener!=null&&mCount!=0){
				mOnPageSelectListener.onDisplayChanged(mCurPos,true);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float curX = event.getRawX();
//			moveDotBy(curX - lastX );
			moveDotTo(x);
			lastX = curX;
			break;
		case MotionEvent.ACTION_UP:
			if(mOnPageSelectListener!=null&&mCount!=0){
				mOnPageSelectListener.onDisplayChanged(mCurPos,false);
			}
			break;
		default:
			break;
		}
		return true;
	
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		LogM.log(this.getClass(), "onMeasure");
		mPageScheduleWidth = mWidth/mCount;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = getMeasuredWidth();
	}
	
	private void moveDotBy(float deltaX){
		LayoutParams lp = (LayoutParams) mDragDot.getLayoutParams();
		int newLeftMargin = (int) (lp.leftMargin + deltaX);
		LogM.log(this.getClass(), "宽度："+getMeasuredWidth());
		if(newLeftMargin>=0){
			if(newLeftMargin+mDotWidth>mWidth){//避免圆点被压缩
				lp.leftMargin = mWidth-mDotWidth;
				if(mOnPageSelectListener!=null&&mCount!=0){
					mCurPos = mCount-1;
					mOnPageSelectListener.onGraged(mCurPos,lp.leftMargin);
				}
			}else{
				lp.leftMargin = newLeftMargin;
				if(mOnPageSelectListener!=null&&mCount!=0){
					mCurPos = (int) (newLeftMargin/mPageScheduleWidth);
					mOnPageSelectListener.onGraged(mCurPos,newLeftMargin);
				}
			}
			mDragDot.setLayoutParams(lp);
			
		}
	}
	
	private void moveDotTo(float x){
		
		LayoutParams lp = (LayoutParams) mDragDot.getLayoutParams();
		moveDotBy(x-lp.leftMargin);
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int count) {
		LogM.log(this.getClass(), "setCount");
		this.mCount = count;
	}

	public void setCurIndex(int index){
		if(index>mCount){
			throw new IndexOutOfBoundsException("必须小于 count："+mCount+"当前 ："+index);
		}
		if(mCurPos==index){
			return;
		}else if(index==mCount-1){
			moveDotTo(mWidth);
		}else{
			moveDotTo(index*mPageScheduleWidth);
		}
	}

	public void setOnPageSelectListener(OnGragedListener onPageSelectListener) {
		this.mOnPageSelectListener = onPageSelectListener;
	}
	
	
	

}
