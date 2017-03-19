package com.apppubs.d20.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 图片滚动指示器用户显示当前图片是第几张
 *
 */
/**
 * 图片滚动指示器用户显示当前图片是第几张
 *
 */
public class Indicator extends LinearLayout{

	public static final int STYLE_LIGHT = 0;
	public static final int STYLE_DARK = 1;
	
	private Context mContext;
	private int mMaxNum;// 总数
	private int mCurPos;
	/**普通圆点半径，高亮圆点半径*/
	private float mRadius,mRadiusH;
	/**点之间距离*/
	private int padding;
	/**高亮点的颜色*/
	private int mHighLightC = 0xFFFFFFFF;
	private int mDimColor = 0x40FFFFFF;
	

	public Indicator(Context context,int num,int style) {
		super(context);
		mContext = context;
		mMaxNum = num;
		float scale = context.getResources().getDisplayMetrics().density;
		mRadius = scale*3;
		mRadiusH  = (float) (scale*3);
		padding = 10;
		if(style==STYLE_DARK){
			mHighLightC = Color.BLACK;
			mDimColor = 0x40000000;
		}else if(style==STYLE_LIGHT){
			mHighLightC = 0xFFFFFFFF;
			mDimColor = 0x40FFFFFF;
		}

		this.setOrientation(LinearLayout.HORIZONTAL);
		drawDots(context);
		
	}
	private void drawDots(Context context) {
		for(int i=-1;++i<mMaxNum;){
			Dot dot = new Dot(context);
			LayoutParams lp = new LayoutParams((int) (padding+mRadiusH*2), (int) (mRadiusH*2));
			lp.gravity = Gravity.CENTER;
			addView(dot,lp);
		}
	}
	public Indicator(Context context, AttributeSet attrs) {
		this(context,3,STYLE_LIGHT);
		
	}
	
	public void setCurItem(int pos){
		if(pos+1>mMaxNum){
			return;
		}
		if(pos<0){
			return;
		}
		((Dot)this.getChildAt(mCurPos)).dim();
		((Dot)this.getChildAt(pos)).highLight();
		mCurPos = pos;
	}

	public void setNum(int num){
		mMaxNum = num;
		removeAllViews();
		drawDots(mContext);
	}
 
	private class Dot extends View{
		private Paint mP;
		private boolean isHilight;
		public Dot(Context context) {
			super(context);
			mP = new Paint();
			mP.setColor(mDimColor);
			mP.setAntiAlias(true);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {

			if(isHilight){
				canvas.drawCircle(mRadiusH, mRadiusH, mRadiusH, mP);
			}else{
				canvas.drawCircle(mRadiusH, mRadiusH, mRadius, mP);
			}
		}
		
		/**高亮*/
		public void highLight(){
			isHilight = true;
			mP.setColor(mHighLightC);
			this.invalidate();
		}
		/**变为普通*/
		public void dim(){
			mP.setColor(mDimColor);
			this.invalidate();
			isHilight = false;
		}
		

		
	}

}



