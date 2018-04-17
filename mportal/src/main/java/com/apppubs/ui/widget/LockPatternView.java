package com.apppubs.ui.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.apppubs.d20.R;
import com.apppubs.util.Utils;

/**
 * 滑动解锁view
 * 
 * @author
 * 
 */
public class LockPatternView extends View {

	private final int POINT_SIZE_MAX = 8;
	private final int POINT_SIZE_MIN = 4;
	private boolean isInitialized;
	private boolean isSelectedMode;//是否正在选择
	private boolean isFinished;//是否完成选择
	private boolean isNeedLine;
	private int mWidth, mHeight, mPointBitmapRadius;
	private float mOffsetX, mOffsetY,mMotionX,mMotionY;
	private Point[][] mPoints;
	private Bitmap mPointBitmapNormal, mPointBitmapPress, mPointBitmapError, mLineNormal, mLineError;
	private Paint mPaint;
	private List<Point> mSelectedPointsList;
	private Matrix mLineMatrix;
	private Point mLastedPoint;
	private OnFinishListener mOnFinishListener;
	
	public interface OnFinishListener{
		public static final int RESULT_SUCCESS = 1;
		public static final int RESULT_FAIL = 0;
		public void onFinish(int result);
	}
	
	public LockPatternView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPoints = new Point[3][3];
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStrokeWidth(Utils.dip2px(context, 5));
		mPaint.setColor(Color.parseColor("#F0F0F0"));
		mSelectedPointsList = new ArrayList<Point>();
		mLineMatrix = new Matrix();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		if (!isInitialized) {
			isInitialized = true;
			initPoints();
		}
		
		drawPoints(canvas);
		
		for(Point p:mSelectedPointsList){
			
			if(mLastedPoint!=null){
				drawLines(canvas, mLastedPoint, p);
			}
			
			mLastedPoint = p;
		}
		if(mLastedPoint!=null){
			
		}
		
		mLastedPoint = null;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		isNeedLine = false;
		mMotionX = event.getX();
		mMotionY = event.getY();
		Point point = null;
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				point = checkTouchedPoint(mMotionX, mMotionY);
				if(point!=null){
					isSelectedMode = true;
				}
				isFinished = false;
				break;
			case MotionEvent.ACTION_UP:
				isSelectedMode = false;
				isFinished = true;
				break;
			case MotionEvent.ACTION_MOVE:
				if(isSelectedMode){
					point = checkTouchedPoint(mMotionX, mMotionY);
					if(point!=null){
						isNeedLine = true;
					}
				}
				break;
		}
		
		//重复选中判断
		if(isSelectedMode&&!isFinished&&point!=null){
			if(mSelectedPointsList.contains(point)){
				isNeedLine = true;
			}else{
				point.state = Point.STATE_PRESS;
				mSelectedPointsList.add(point);
			}
		}
		if(isFinished){
			if(mSelectedPointsList.size()<1){
				mSelectedPointsList.clear();
			}else if(mSelectedPointsList.size()<=POINT_SIZE_MAX&&mSelectedPointsList.size()>=POINT_SIZE_MIN){
				
			}
			resetPoint();
			if(mOnFinishListener!=null){
				int resultNum = 0;
				int size = mSelectedPointsList.size();
				for(int i=-1;++i<size;){
					resultNum += Math.pow(10, i)*mSelectedPointsList.get(i).sortId;
				}
				mOnFinishListener.onFinish(resultNum==2589?OnFinishListener.RESULT_SUCCESS:OnFinishListener.RESULT_FAIL);
			}
		}
		postInvalidate();
		return true;
	}
	
	private void resetPoint(){
		mSelectedPointsList.clear();
		for (int i = -1; ++i < mPoints.length;) {
			for (int j = -1; ++j < mPoints[i].length;) {
				mPoints[i][j].state = Point.STATE_NORMAL;
			}

		}
	}
	private void makeSelectedPointError(){
		
	}
	private void initPoints() {

		mWidth = getWidth();
		mHeight = getHeight();
		if (mHeight > mWidth) {// 竖屏
			mOffsetY = (mHeight - mWidth) / 2;
			mHeight = mWidth;
		} else {// 横屏
			mOffsetX = (mWidth - mHeight) / 2;
			mWidth = mHeight;
		}

		mPointBitmapNormal = BitmapFactory.decodeResource(getResources(), R.drawable.point_normal);
		mPointBitmapPress = BitmapFactory.decodeResource(getResources(), R.drawable.point_press);
		mPointBitmapError = BitmapFactory.decodeResource(getResources(), R.drawable.point_error);
		mPointBitmapRadius = mPointBitmapNormal.getHeight() / 2;

		mPoints[0][0] = new Point(mOffsetX + mWidth / 4, mOffsetY + mWidth / 4,1);
		mPoints[0][1] = new Point(mOffsetX + mWidth / 2, mOffsetY + mWidth / 4,2);
		mPoints[0][2] = new Point(mOffsetX + mWidth - mWidth / 4, mOffsetY + mWidth / 4,3);

		mPoints[1][0] = new Point(mOffsetX + mWidth / 4, mOffsetY + mWidth / 2,4);
		mPoints[1][1] = new Point(mOffsetX + mWidth / 2, mOffsetY + mWidth / 2,5);
		mPoints[1][2] = new Point(mOffsetX + mWidth - mWidth / 4, mOffsetY + mWidth / 2,6);

		mPoints[2][0] = new Point(mOffsetX + mWidth / 4, mOffsetY + mWidth - mWidth / 4,7);
		mPoints[2][1] = new Point(mOffsetX + mWidth / 2, mOffsetY + mWidth - mWidth / 4,8);
		mPoints[2][2] = new Point(mOffsetX + mWidth - mWidth / 4, mOffsetY + mWidth - mWidth / 4,9);
	}

	private void drawPoints(Canvas canvas) {
		for (int i = -1; ++i < mPoints.length;) {
			for (int j = -1; ++j < mPoints[i].length;) {
				Point point = mPoints[i][j];
				if (point.state == Point.STATE_NORMAL) {
					canvas.drawBitmap(mPointBitmapNormal, point.x - mPointBitmapRadius, point.y - mPointBitmapRadius,
							mPaint);
				} else if (point.state == Point.STATE_PRESS) {
					canvas.drawBitmap(mPointBitmapPress, point.x - mPointBitmapRadius, point.y - mPointBitmapRadius,
							mPaint);
				} else {
					canvas.drawBitmap(mPointBitmapError, point.x - mPointBitmapRadius, point.y - mPointBitmapRadius,
							mPaint);
				}
			}

		}

	}
	
	
	private void drawLines(Canvas canvas,Point startP,Point endP){
		float distance = Point.distanceOf2Points(startP, endP);
		canvas.drawLine(startP.x, startP.y, endP.x, endP.y, mPaint);
		
		
	}
	//检测当前触摸点的坐标是否在九宫格中的九个点之一的范围
	private Point checkTouchedPoint(float touchX,float touchY){
		for(int i=-1;++i<mPoints.length;){
			for(int j=-1;++j<mPoints[i].length;){
				Point p = mPoints[i][j];
				if(Point.isTouchPoint(touchX, touchY, p.x, p.y, mPointBitmapRadius)){
					
					return mPoints[i][j];
				}
				
			}
		}
		return null;
	}


	private static class Point {

		public static final int STATE_NORMAL = 0;
		public static final int STATE_PRESS = 1;
		public static final int STATE_ERROR = 2;
		public float x, y;
		public int state;
		public int sortId;//排序数，从1开始

		public Point(float x, float y,int sortId) {
			this.x = x;
			this.y = y;
			state = 0;
			sortId = sortId;
		}
		/**
		 * 判断是否触摸到某点
		 * @param x
		 * @param y
		 * @param pointCenterx
		 * @param pointCenterY
		 * @param radius
		 * @return
		 */
		public static boolean isTouchPoint(float x,float y,float pointCenterx,float pointCenterY,float radius){
			System.out.println(Math.sqrt((x-pointCenterx)*(x-pointCenterx)+(y-pointCenterY)*(y-pointCenterY)));
			System.out.println(Math.sqrt(Math.pow((x-pointCenterx),2)+Math.pow(y-pointCenterY,2)));
			return Math.sqrt(Math.pow((x-pointCenterx),2)+Math.pow(y-pointCenterY,2))<radius;
		}
		
		public static float distanceOf2Points(Point pointStart,Point pointEnd){
			return (float) Math.sqrt(Math.pow((pointStart.x-pointEnd.x),2)+Math.pow((pointStart.y-pointEnd.y), 2));
		}
		
	}


	public void setOnFinishListener(OnFinishListener onFinishListener) {
		this.mOnFinishListener = onFinishListener;
	}
	
	

}
