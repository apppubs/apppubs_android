package com.mportal.client.activity;

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

import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.bean.StartUpPic;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 查看启动图
 * 
 * @author sunyu
 * 
 */
public class SplashActivity extends BaseActivity{
//	private ViewPager viewparge;
    private ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setNeedTitleBar(false);
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
		setContentView(R.layout.act_splash);
		iv=(ImageView) findViewById(R.id.splash_iv);
		
		mImageLoader.displayImage(mAppContext.getApp().getStartUpPic(), iv);
//		viewparge = (ViewPager) findViewById(R.id.spladh_viewparge);
//		viewparge.setAdapter(new SplashPagerAdapter(getApplication()));
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

class SplashPagerAdapter extends PagerAdapter {
	List<ImageView> viewLists = new ArrayList<ImageView>();
	private Context context;

	public SplashPagerAdapter(Context contex) {
		super();
		context = contex;
		viewLists = getViewLists();

	}

	private List<ImageView> getViewLists() {
		// TODO Auto-generated method stub
		List<ImageView> viewLists = new ArrayList<ImageView>();
		StartUpPic pic = StartUpPic.find(StartUpPic.class, null, null).get(0);
		ImageView iv = new ImageView(context);
		iv.setScaleType(ScaleType.CENTER_CROP);
		ImageLoader.getInstance().displayImage(pic.getPicURL(), iv);
		viewLists.add(iv);
		return viewLists;
	}

	@Override
	public int getCount() {
		return viewLists.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	// 销毁Item
	@Override
	public void destroyItem(ViewGroup view, int position, Object object) {
		// TODO Auto-generated method stub
		super.destroyItem(view, position, object);
		((ViewPager) view).removeView(viewLists.get(position));
	}

	// 实例化Item
	@Override
	public Object instantiateItem(View view, int position) {
		// TODO Auto-generated method stub
		((ViewPager) view).addView(viewLists.get(position), 0);
		return viewLists.get(position);
	}

}
