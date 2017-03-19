package com.apppubs.d20.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.apppubs.d20.util.LogM;
import com.apppubs.d20.R;

/**
  * 有高宽比的布局
  * 
  * Copyright (c) heaven Inc.
  *
  * Original Author: zhangwen
  *
  * ChangeLog:
  * 2015年1月25日 by zhangwen create
  *
  */
public class RatioLayout extends RelativeLayout {
	
	private float mRatio = 1f;//默认是正方形
    public RatioLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.RatioLayout);
        mRatio = ta.getFloat(R.styleable.RatioLayout_ratio,mRatio);
		ta.recycle();
    }
 
    public RatioLayout(Context context) {
        super(context);
    }
 
    public RatioLayout(Context context ,float heightWidthRatio){
    	super(context);
    	mRatio = heightWidthRatio;
    }
    
    @SuppressWarnings("unused")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	LogM.log(this.getClass(), "onMeasure ");
        // For simple implementation, or internal size is always 0.
        // We depend on the container to specify the layout size of
        // our view. We can't really know what it is since we will be
        // adding and removing different arbitrary views and do not
        // want the layout to change as this happens.
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
 
        // Children are just made to fill our space.
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        //高度和宽度一样
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int)(childWidthSize*mRatio),MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    
}

 