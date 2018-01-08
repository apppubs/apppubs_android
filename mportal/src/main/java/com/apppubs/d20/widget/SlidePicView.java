package com.apppubs.d20.widget;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apppubs.d20.util.LogM;
import com.apppubs.d20.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 滑动图片
 *
 * @author zhangwen 2014-12-10
 */
public class SlidePicView extends RelativeLayout implements OnPageChangeListener {

	public static final int STYLE_NORMAL = 0;//默认的样式，指示器在图片下方在图片上堆叠
	public static final int STYLE_UNDER_PIC = 1;//指示器在图片下方且和图片不重叠
	public static final int STYLE_PAGE_CONTROL_ONLY = 2;


	private final static float PIC_RATIO = 0.56f;
	private Context mContext;
	private List<SlidePicItem> mList;
	private int mSize;
	private ViewPager mViewPager;
	private LayoutParams mViewLp;

	/**
	 * 图片标题大小 dp
	 */
	private float mTitleTextSize;
	private TextView mTitleTv;
	private int mTitleTextColor;
	private LayoutParams mTitleTvLp;
	private Indicator mIndicator;
	private LayoutParams mIndicatorLp;

	private int mPicHeight;

	private Runnable mPagingRunnable;//翻页任务，用于定时翻页
	private int mCurPos;
	private int mMaxPage;
	private float mScale;
	private OnItemClickListener mOnItemClickListener;
	private DisplayImageOptions mImageLoaderOptions;
	private int mStyle;//布局风格
	private float mPicRatio = 0.56f;

	public interface OnItemClickListener {
		void onClick(int pos, SlidePicItem item);
	}

	public SlidePicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViewPager(context);
	}

	public SlidePicView(Context context) {

		this(context, STYLE_NORMAL, PIC_RATIO);

	}

	public SlidePicView(Context context, int style) {
		this(context, style, PIC_RATIO);
	}

	public SlidePicView(Context context, int style, float picRatio) {
		super(context);

		mContext = context;
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		mScale = dm.density;

		mStyle = style;
		mPicRatio = picRatio;

		if (mStyle == STYLE_NORMAL) {
			mTitleTextColor = Color.WHITE;
		} else {
			mTitleTextColor = Color.BLACK;
		}

		mTitleTextSize = (float) mScale * 15;

		mPicHeight = (int) (dm.widthPixels * mPicRatio);

		initViewPager(context);


	}

	private void initViewPager(Context context) {


		int height = (mStyle == STYLE_NORMAL || mStyle == STYLE_PAGE_CONTROL_ONLY) ? mPicHeight : (int) (mPicHeight + (30 + 0.5) * mScale);
		this.setLayoutParams(new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, height));

		mViewPager = new ViewPager(context);
		mViewLp = new LayoutParams(LayoutParams.MATCH_PARENT, mPicHeight);
		addView(mViewPager, mViewLp);
		if (mStyle == STYLE_NORMAL || mStyle == STYLE_UNDER_PIC) {
			mTitleTv = new TextView(context);
//		mTitleTv.setFilters(new InputFilter[] { new InputFilter.LengthFilter(17) });
//		mTitleTv.setShadowLayer(1, 3, 3, Color.BLACK);
			if (mStyle == STYLE_NORMAL) {
				mTitleTv.setBackgroundResource(R.drawable.slide_pic_gradient_title_bg);
			}
			mTitleTv.setGravity(Gravity.CENTER_VERTICAL);
			mTitleTv.setPadding((int) (mScale * 10), 0, (int) (mScale * 80), 0);
			mTitleTv.setTextColor(mTitleTextColor);
			mTitleTv.setText("");
			mTitleTv.setSingleLine();
			mTitleTv.setEllipsize(TextUtils.TruncateAt.END);
			mTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);


			mTitleTvLp = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (mTitleTextSize * 2));
			mTitleTvLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			addView(mTitleTv, mTitleTvLp);
		}

		try {
			Field mScroller = null;
			mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			FixedViewPagerScroller scroller = new FixedViewPagerScroller(
					context);
			mScroller.set(mViewPager, scroller);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Drawable drawable = Drawable.createFromPath(context.getFilesDir().getAbsolutePath() + "/stance_pic.png");
		mImageLoaderOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(drawable)
				.showImageForEmptyUri(drawable)
				.showImageOnFail(drawable)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();

	}

	public void setTitleTextSize(float titleTextSize) {
		mTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
	}

	public void setData(List<SlidePicItem> list) {
		mList = new ArrayList<SlidePicView.SlidePicItem>();
//		mList.add(list.get(list.size()-1));
		mList.addAll(list);
//		mList.add(list.get(0));

		mSize = list.size();

//		mCurPos = 999;

		mPagingRunnable = new Runnable() {

			@Override
			public void run() {
				if (mSize == 1)
					return;
				mViewPager.setCurrentItem(mCurPos == mList.size() - 1 ? 0
						: mCurPos + 1);
				mViewPager.postDelayed(mPagingRunnable, 4500);
			}
		};

		if (mIndicator != null) {
			removeView(mIndicator);
		}
		int indicatorStyle = mStyle == STYLE_NORMAL ? Indicator.STYLE_LIGHT : Indicator.STYLE_DARK;
		mIndicator = new Indicator(mContext, list.size(), indicatorStyle);
		mIndicator.setPadding(0, 0, (int) (10 * mScale), 0);
		mIndicator.setCurItem(0);
		mIndicator.setGravity(Gravity.CENTER);
		mIndicatorLp = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) mTitleTextSize * 2);
		mIndicatorLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		mIndicatorLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		addView(mIndicator, mIndicatorLp);

		List<ImageView> viewList = new ArrayList<ImageView>();
		for (int i = -1; ++i < mList.size(); ) {
			ImageView iv = new ImageView(mContext);
			if (mStyle == STYLE_PAGE_CONTROL_ONLY) {
				iv.setScaleType(ScaleType.FIT_XY);
			} else {
				iv.setScaleType(ScaleType.CENTER_CROP);
			}
			viewList.add(iv);
		}
		PagerAdapter adapter = new MyPagerAdapter(viewList);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(SlidePicView.this);
//		mViewPager.setCurrentItem(1,false);
		if (mSize > 0 && mTitleTv != null) {
			mTitleTv.setText(mList.get(0).title);
		}
	}


	public class MyPagerAdapter extends PagerAdapter implements OnClickListener {

		// 界面列表
		private List<ImageView> views;

		public MyPagerAdapter(List<ImageView> views) {
			this.views = views;
		}

		// 销毁arg1位置的界面
		@Override
		public void destroyItem(View view, int position, Object arg2) {
//            ((ViewPager) view).removeView(views.get(getPosition(position)));
			LogM.log(this.getClass(), "销毁第：" + position + "页");
		}

		// 获得当前界面数(返回一个比较大的数模拟无限连续滑动)
		@Override
		public int getCount() {
			return views.size();
		}

		// 初始化arg1位置的界面
		@Override
		public Object instantiateItem(View view, int position) {
			LogM.log(this.getClass(), "新建第：" + position + "页");
			int pos = getPosition(position);
			ImageView iv = views.get(position);

			iv.setId(position);
			iv.setOnClickListener(this);
			try {
				((ViewPager) view).addView(iv, position);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ImageLoader.getInstance().displayImage(mList.get(position).picURL, iv, mImageLoaderOptions);
			return iv;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
								   Object object) {
			LogM.log(this.getClass(), "setPrimaryItem pposition:" + position);
			super.setPrimaryItem(container, 2, null);

		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		@Override
		public void onClick(View v) {
			if (mOnItemClickListener != null) {
				mOnItemClickListener.onClick(v.getId(), mList.get(v.getId()));
			}
		}
	}


	@Override
	public void onPageScrollStateChanged(int state) {
//		if(state==0){
//			if(mViewPager.getCurrentItem()==mList.size()-1){
//				mViewPager.setCurrentItem(1,false);
//			}else if(mViewPager.getCurrentItem()==0){
//				mViewPager.setCurrentItem(mList.size()-2,false);
//			}
//		}

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {


//		if ( position < 1) { //首位之前，跳转到末尾（N）  
//            position = mList.size(); //注意这里是mList，而不是mViews  
//            mViewPager.setCurrentItem(position, false);  
//        } else if ( position > mList.size()) { //末位之后，跳转到首位（1）  
//            mViewPager.setCurrentItem(1, false); //false:不显示跳转过程的动画  
//            position = 1;  
//        }  

//		int pos = getPosition(position);
//		mCurPos = pos;
		if (mTitleTv != null) {
			mTitleTv.setText(mList.get(position).title);
		}
//		if(position>mList.size()-2){
//			position = mList.size()-3;
//		}else if(position<2){
//			position = 0;
//		}else{
//			position -= 1;
//		}

		mIndicator.setCurItem(position);


	}

	public OnItemClickListener getmOnItemClickListener() {
		return mOnItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	private int getPosition(int position) {
		return position % mSize;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {


		return super.onInterceptTouchEvent(ev);


	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.v("SlidePicView", "onInterceptTouchEvent" + ev.getAction());
		return super.dispatchTouchEvent(ev);
	}


	/**
	 * 滑动图片的包装
	 * <p>
	 * Copyright (c) heaven Inc.
	 * <p>
	 * Original Author: zhangwen
	 * <p>
	 * ChangeLog:
	 * 2015年1月16日 by zhangwen create
	 */
	public static class SlidePicItem {
		public String title;
		public String picURL;
		public String infoId;
		public String channelCode;
		public String linkType;
		public String linkValue;
	}

}
