package com.apppubs.d20.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ProgressPie extends View{

	private Paint mPaint;
	private Paint mArcPaint;
	private RectF mCir;
	private RectF mArcRec;
	
	private float mProgress;
	public ProgressPie(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setColor(Color.GRAY);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.STROKE);
		
		mCir = new RectF();
		mArcRec = new RectF();
		mArcPaint = new Paint();
		mArcPaint.setColor(Color.GRAY);
		mArcPaint.setAntiAlias(true);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(mCir.width()/2, mCir.height()/2, mCir.width()/2-2, mPaint);
		canvas.drawArc(mArcRec,-90,360*mProgress,true,mArcPaint);
		
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		Log.v("ProgressPie", "onFinishInflate");
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.v("ProgressPie ","onMeasure widthMeasureSpec:"+widthMeasureSpec+" heightMeasureSpec:"+ heightMeasureSpec);
		
		setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		Log.v("MyView ","dispatchTouchEvent"+event.getAction());
		return true;
	}
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,

			int bottom) {
		Log.v("ProgressPie", "onLayout changed:"+changed+" left:"+left+" top:"+top+" right:"+right+" buttom:"+bottom);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		Log.v("ProgressPie", "onSizeChanged width:"+ w+"height:"+ h+"old width:"+oldw+"old height:"+oldh);
		
		int padLeft =  getPaddingLeft();
		int padRight = getPaddingRight();
		int padTop = getPaddingTop();
		int padBottom = getPaddingBottom();
		// Account for padding
		float xpad = (float) (padLeft + padRight);
		float ypad = (float) (padTop + padBottom);

		float ww = (float) w - xpad;
		float hh = (float) h - ypad;
		mCir.set(padLeft, padRight, ww + padLeft, hh + padTop);
		mArcRec.set(padLeft+1, padRight+1, ww + padLeft-1, hh + padTop-1);
		// Figure out how big we can make the pie.
		float diameter = Math.min(ww, hh);
	}
	
	/**
	 * 设置比例  progress=0~1
	 * @param progress 比例
	 */
	public void setProgress(float progress){
		mProgress = progress;
		invalidate();
	}

}
