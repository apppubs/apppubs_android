package com.apppubs.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.apppubs.d20.R;
import com.apppubs.bean.TStartUpPic;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 查看启动图
 * 
 * @author sunyu
 * 
 */
public class SplashActivity extends BaseActivity{
    private ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setNeedTitleBar(false);
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
		setContentView(R.layout.act_splash);
		iv=(ImageView) findViewById(R.id.splash_iv);
		Glide.with(this).load(mAppContext.getApp().getStartUpPic()).into(iv);
	}
     /**
      * 滑动关闭界面
      */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float oldx = 0;
		float newx = 0;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Log.v("Himi", "ACTION_DOWN");
			oldx = event.getX();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			newx = event.getX();
			Log.v("Himi", "ACTION_UP");
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			Log.v("Himi", "ACTION_MOVE");
		}
		if (newx - oldx >= 10) {
			finish();
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
	}
	
	@Override
		public void onClick(View v) {
			
			if(v.getId()==R.id.splash_back){
				finish();
				return;
			}
		
			super.onClick(v);
			
		}

}