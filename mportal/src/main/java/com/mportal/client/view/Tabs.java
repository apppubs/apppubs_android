package com.mportal.client.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mportal.client.R;
import com.mportal.client.util.LogM;

public class Tabs extends FrameLayout{

	private Context mContext;
	private int mDisplayWidth;
	private LinearLayout mContainer;
	private View mCursorV;//底部滚动条
	private int mItemWidth;
	
	private int mCurPos;
	private int mTextColor;
	private float mTextSize;
	private int mSelectedTextColor;
	private TextView mCurrentTabTv;
	
	private OnItemClickListener mOnItemClickListener;
	public interface OnItemClickListener{
		void onItemClick(int pos);
	}
	@SuppressWarnings("deprecation")
	public Tabs(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.Tabs);
		mTextColor = ta.getColor(R.styleable.Tabs_textColor, Color.BLACK);
		mSelectedTextColor = ta.getColor(R.styleable.Tabs_selectedTextColor, Color.BLACK);
		mTextSize = ta.getDimension(R.styleable.Tabs_textSize, 24f);
		ta.recycle();
		
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		mDisplayWidth = display.getWidth();
		mContainer = new LinearLayout(context);
		LayoutParams containerLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(mContainer,containerLp);
		mCursorV = new View(context);
		mCursorV.setBackgroundColor(mSelectedTextColor);
	}
	
	public void setTabs(String[] tabNames){
		int size = tabNames.length;
		mItemWidth = mDisplayWidth/size;
		LayoutParams cursorLp = new LayoutParams(mItemWidth, 3);
		cursorLp.gravity = Gravity.BOTTOM;
		addView(mCursorV,cursorLp);
		for(int i=-1;++i<size;){
			TextView tv = new TextView(mContext);
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mItemWidth, LayoutParams.MATCH_PARENT);
			tv.setGravity(Gravity.CENTER);
			int padding=(int)getResources().getDimension(R.dimen.text_padding);
			
			tv.setPadding(padding,padding,padding,padding);
			tv.setText(tabNames[i]);
			tv.setTag(i);
			tv.setTextColor(mTextColor);
			if(i==0) tv.setTextColor(mSelectedTextColor);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize);
			mContainer.addView(tv,lp);
			tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setCurrentTab((Integer) v.getTag());
					if(mOnItemClickListener!=null){
						mOnItemClickListener.onItemClick(mCurPos);
					}
				}
			});
		}
	}
	
	public void setCurrentTab(int currentTab) {
		
		if(currentTab==mCurPos) return;
		
		
		LogM.log(getClass(), "setCurrentTab:"+currentTab);
		if(mContainer.getChildCount()==0) return;
		//首先清理上次的点击的样式
		if(mCurrentTabTv!=null){
			mCurrentTabTv.setTextColor(mTextColor);
			mCurrentTabTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize);
		}
		this.mCurPos = currentTab;
		
		mCurrentTabTv = (TextView) mContainer.getChildAt(currentTab);
		mCurrentTabTv.setTextColor(mSelectedTextColor);
		
		LayoutParams lp = (FrameLayout.LayoutParams) mCursorV.getLayoutParams();
		lp.leftMargin = (int) (currentTab*mItemWidth);
		mCursorV.setLayoutParams(lp);
		
	}


	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}
	
	
//	public void onPageScrolled(int position,float offset){
//		
//		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mCursorV.getLayoutParams();
//		lp.leftMargin = (int) ((position+offset)*mItemWidth);
//		mCursorV.setLayoutParams(lp);
//		
//		TextView next = this.getItem(position+1);
//		TextView cur = this.getItem(position);
//		
//		if(cur!=null){
//			cur.setTextSize(TypedValue.COMPLEX_UNIT_PX,mSelectedTextSize-offset*mDTextSize);
//			
//			int temp = Color.rgb((int)(mSr-offset*(mSr-mR)), (int)(mSg-offset*(mSg-mG)), (int)(mSb-offset*(mSb-mB)));
//			cur.setTextColor(temp);
//			
//			
//		}
//			
//		if(next!=null){
//			
//			int temp = Color.rgb((int)(mR+offset*(mSr-mR)), (int)(mG+offset*(mSg-mG)), (int)(mB+offset*(mSb-mB)));
//			next.setTextColor(temp);
//			
//			next.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize+offset*mDTextSize);
//		}
//	}

}
