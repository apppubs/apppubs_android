package com.apppubs.ui.fragment;

import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.apppubs.ui.start.StartUpActivity;
import com.apppubs.presenter.StartupPresenter;
import com.apppubs.util.SharedPreferenceUtils;
import com.apppubs.util.Utils;
import com.apppubs.d20.R;

public class WelcomeFragment extends BaseFragment implements OnPageChangeListener {

	private ViewPager mViewPager;
	private String[] fileNames;// 欢迎图的文件名数组
	private int mCurPos;

	private View mRootView;

	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.activity_main_vm, null);
		try {
			fileNames = mContext.getAssets().list("welcome");
		} catch (IOException e) {
			e.printStackTrace();
		}

		mViewPager = (ViewPager) mRootView.findViewById(R.id.viewpager);
		mViewPager.setAdapter(new MyPagerAdapter());
		mViewPager.setOnPageChangeListener(this);

		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
	}

	public class MyPagerAdapter extends PagerAdapter {

		public MyPagerAdapter() {
		}

		// 销毁arg1位置的界面
		@Override
		public void destroyItem(View viewPager, int pos, Object view) {
			((ViewPager) viewPager).removeView((View) view);

		}

		// 获得当前界面数
		@Override
		public int getCount() {
			return fileNames.length;
		}

		// 初始化arg1位置的界面
		@Override
		public Object instantiateItem(View viewPager, final int position) {
			View v = null;

			ImageView iv = new ImageView(mHostActivity);
			InputStream ims;
			try {
				ims = mHostActivity.getAssets().open("welcome/" + fileNames[position]);
				Drawable d = Drawable.createFromStream(ims, null);
				iv.setImageDrawable(d);
				iv.setScaleType(ScaleType.FIT_XY);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (position == fileNames.length - 1) {
				RelativeLayout fl = new RelativeLayout(mContext);

				fl.addView(iv, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

				if (mHostActivity instanceof StartUpActivity) {

					iv.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							//每一次完成时记录此版本已经完成了欢迎图的显示；新版app不会受到影响
							SharedPreferenceUtils.getInstance(getContext()).putString( StartupPresenter.SHARED_PREFERENCE_NAME_WELCOME_LOAD_HISTORY, Utils.getVersionCode(getContext())+"","f");
							Intent i = new Intent(mContext,StartUpActivity.class);
							startActivity(i);
						}
					});
				} else {
					iv.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mHostActivity.finish();
						}
					});
				}

				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						Utils.dip2px(mContext, 40));
				lp.leftMargin = Utils.dip2px(mContext, 40);
				lp.rightMargin = Utils.dip2px(mContext, 40);
				lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				lp.bottomMargin = Utils.dip2px(mContext, 40);

				((ViewPager) viewPager).addView(fl);
				v = fl;
			} else {
				((ViewPager) viewPager).addView(iv);
				v = iv;
			}

			return v;
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		Log.d("liuhai", "state:" + state);

	}

	/**
	 * position :当前页面，及你点击滑动的页面 positionOffset:当前页面偏移的百分比
	 * positionOffsetPixels:当前页面偏移的像素位置
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int currentPosition) {
		mCurPos = currentPosition;
	}
}
