package com.mportal.client.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.mportal.client.R;

/**
 * 点击自动更换颜色的imageview,可以当作button使用
 * 可以设置按钮的默认颜色和按压颜色（图片的显示颜色）
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年1月26日 by zhangwen create
 * 2015年1月26日 by zhangwen 增加按钮默认颜色
 * 
 *
 */
public class ImageButton extends ImageView{

	private static final int BUTTON_BOUNCE_DELAY = 50;
	/**
	 * 高亮颜色
	 */
	private int mHighlightColor;
	private int mDefaultColor = Color.TRANSPARENT;
	public ImageButton(Context context) {
		super(context);
		
		init();
	}

	public ImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes( attrs,R.styleable.ImageButton);
		mHighlightColor = ta.getColor(R.styleable.ImageButton_highlightColor, Color.WHITE);
		mDefaultColor = ta.getColor(R.styleable.ImageButton_defaultColor, mDefaultColor);
		ta.recycle();
		
		this.setColorFilter(mDefaultColor,Mode.SRC_ATOP);
		
		init();
	}
	
	private void init(){
/*		
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int actionMasked  = event.getActionMasked();
				switch (actionMasked) {
				case MotionEvent.ACTION_DOWN:
					ImageButton.this.setColorFilter(mHighlightColor, Mode.SRC_ATOP);
					break;
				case MotionEvent.ACTION_UP:
					ImageButton.this.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							ImageButton.this.setColorFilter(mDefaultColor,Mode.SRC_ATOP);
						}
					}, BUTTON_BOUNCE_DELAY);
					
					break;
				default:
					break;
				}
				return false;
			}
		});*/
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean superResult = super.onTouchEvent(event);
		int actionMasked  = event.getActionMasked();
		switch (actionMasked) {
		case MotionEvent.ACTION_DOWN:
			ImageButton.this.setColorFilter(mHighlightColor, Mode.SRC_ATOP);
			break;
		case MotionEvent.ACTION_UP:
			ImageButton.this.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					ImageButton.this.setColorFilter(mDefaultColor,Mode.SRC_ATOP);
				}
			}, BUTTON_BOUNCE_DELAY);
			
			break;
		case MotionEvent.ACTION_CANCEL:
			ImageButton.this.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					ImageButton.this.setColorFilter(mDefaultColor,Mode.SRC_ATOP);
				}
			}, BUTTON_BOUNCE_DELAY);
			break;
		default:
			break;
		}
		return true;
	}
	

	public void setHightLightColor(int hightLightColor){
		mHighlightColor = hightLightColor;
	}
	
	public void setDefaultColor(int defaultColor){
		mDefaultColor = defaultColor;
		this.setColorFilter(mDefaultColor,Mode.SRC_ATOP);
	}
	
	
	
	
}
