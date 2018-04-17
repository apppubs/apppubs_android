package com.apppubs.ui.start;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.apppubs.util.Utils;

/**
 * Created by zhangwen on 2017/9/4.
 */

public class ProgressSkipView extends View implements View.OnClickListener{


	private int mArcStrokeWidth ;

	private Paint mPaint;
	private Paint mArcPaint;
	private RectF mCir;
	private RectF mArcRec;

	private Paint mTextPaint;
	private float mTextSize;
	private float mProgress;

	private boolean isCanceled;

	private SkipListener mListener;

	public ProgressSkipView(Context context) {
		super(context);
	}

	public ProgressSkipView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setColor(Color.parseColor("#80666666"));
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);

		mCir = new RectF();
		mArcRec = new RectF();
		mArcPaint = new Paint();
		mArcPaint.setColor(Color.RED);
		mArcPaint.setAntiAlias(true);
		mArcStrokeWidth = Utils.dip2px(context,3);
		mArcPaint.setStrokeWidth(mArcStrokeWidth);
		mArcPaint.setStyle(Paint.Style.STROKE);

		mTextPaint = new Paint();
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setTextAlign(Paint.Align.CENTER);

		setOnClickListener(this);
	}

	public ProgressSkipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(mCir.width()/2, mCir.height()/2, mCir.width()/2-2, mPaint);
		canvas.drawArc(mArcRec,-90,360*mProgress,false,mArcPaint);

		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
		float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

		canvas.drawText("跳过",mCir.width()/2, mCir.height()/2-top/2-bottom/2,mTextPaint);
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
		mArcRec.set(padLeft+mArcStrokeWidth, padRight+mArcStrokeWidth, ww + padLeft-mArcStrokeWidth, hh + padTop-mArcStrokeWidth);
		// Figure out how big we can make the pie.
		float diameter = Math.min(ww, hh);

		mTextSize = (diameter-mArcStrokeWidth*2-25)/2;
		mTextPaint.setTextSize(mTextSize);
	}

	public void startProgress(final long millis, SkipListener listener){
		mListener = listener;
		final CountDownTimer timer = new CountDownTimer(millis,50) {
			@Override
			public void onTick(long millisUntilFinished) {
				mProgress= (millis-millisUntilFinished)/((float)millis);
				invalidate();
			}

			@Override
			public void onFinish() {
				if (isCanceled){
					return;
				}
				mProgress = 1;
				invalidate();
				if (mListener!=null){
					mListener.onComplete();
				}
			}
		};
		timer.start();
	}

	@Override
	public void onClick(View v) {
		isCanceled = true;
		if (mListener!=null){
			mListener.onClick();
		}
	}

	public interface SkipListener{
		void onClick();
		void onComplete();
	}



}
