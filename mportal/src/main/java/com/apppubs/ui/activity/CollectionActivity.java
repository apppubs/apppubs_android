package com.apppubs.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apppubs.ui.fragment.CollectionFragment1;
import com.apppubs.d20.R;
import com.apppubs.ui.adapter.MyFragementAdapter;
import com.apppubs.bean.Collection;

/**
 * 收藏
 */
public class CollectionActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {

	public static String COLLECTIONDELECTACTION = "com.apppubs.d20.activity.delete";
	public static String COLLECTIONDELECT = "isdelete";

	private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
	private MyFragementAdapter mFragmentAdapter;
	private ViewPager mPageVp;
	private HorizontalScrollView hsv;
	/** * Tab显示内容TextView */
	/** * Tab的那个引导线 */
	private View mTabLineIv;
	/** * Fragment */
	private CollectionFragment1 newsFg, picFg, paperFg, videoFg;
	/** * ViewPager的当前选中页 */
	private int currentIndex;
	/** * 屏幕的宽度 */
	private int screenWidth;
	private List<TextView> tvs = new ArrayList<TextView>();
	private LinearLayout hsv_lay;
	private int mSize;// tab个数
	private boolean isDelete;//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_collection);
		findById();
		init();
		initTabLineWidth();
	}

	private void findById() {
		mTabLineIv = (View) this.findViewById(R.id.collection_line);
		mPageVp = (ViewPager) this.findViewById(R.id.collection_viewparge);
		hsv_lay = (LinearLayout) findViewById(R.id.collect_hsv_lay);
		hsv = (HorizontalScrollView) findViewById(R.id.collect_hsv);
		// String[] strs = { "资讯", "报纸", "图片", "视频" };
		String[] strs = { "资讯", "图片" };
		mSize = strs.length;
		getHomeScollTvs(strs);// 水平进度条的头部填充
	}

	private void init() {

		setTitle("收藏");
		mTitleBar.setRightText("编辑");
//		mTitleBar.setRightTextColor(getResources().getColorStateList(R.drawable.sel_com_text));
		mTitleBar.setRightTextSize(getResources().getDimensionPixelSize(R.dimen.title_text_size));
		mTitleBar.setRightBtnClickListener(this);
		newsFg = new CollectionFragment1();
		picFg = new CollectionFragment1();
		// paperFg = new CollectionFragment();
		// videoFg = new CollectionFragment();

		newsFg.setType(Collection.TYPE_NORMAL);
		picFg.setType(Collection.TYPE_PIC);
		// paperFg.setType(Collection.TYPE_PAPER);
		// videoFg.setType(Collection.TYPE_VEDIO);

		mFragmentList.add(newsFg);
		mFragmentList.add(picFg);
		// mFragmentList.add(paperFg);
		// mFragmentList.add(videoFg);

		mFragmentAdapter = new MyFragementAdapter(getSupportFragmentManager(), (ArrayList<Fragment>) mFragmentList);
		mPageVp.setAdapter(mFragmentAdapter);
		mPageVp.setCurrentItem(0);
		mPageVp.setOnPageChangeListener(this);
	}

	// 上方水平进度条的填充
	public void getHomeScollTvs(String[] names) {

		for (int i = 0; i < names.length; i++) {
			TextView tv = new TextView(CollectionActivity.this);
			// tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
			// LayoutParams.WRAP_CONTENT));
			LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay()
					.getWidth() / names.length, LinearLayout.LayoutParams.WRAP_CONTENT);
			tv.setGravity(Gravity.CENTER);
			int padding = (int) getResources().getDimension(R.dimen.text_padding);
			tv.setPadding(padding, padding, padding, padding);

			if (i == 0) {
				int color = obtainStyledAttributes(new int[] { R.attr.appDefaultTextColor }).getColor(0, Color.BLACK);
				tv.setTextColor(color);
			}
			tv.setText(names[i]);
			lp1.weight = 1;
			// tv.setTypeface(face, Typeface.BOLD);
			tv.setLayoutParams(lp1);
			tv.setId(tvs.size() + 1);// 设置id
			tv.setOnClickListener(this);
			tvs.add(tv);
			hsv_lay.addView(tv);
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTabLineIv.getLayoutParams();
					for (int i = 0; i < tvs.size(); i++) {
						if (v.getId() == i + 1) {
							mPageVp.setCurrentItem(i);
							tvs.get(i).setTextColor(mDefaultColor);
							int move = tvs.get(i).getWidth() * (tvs.size() - (tvs.size() - i)); // 点击移动距离
							hsv.smoothScrollBy(move, 0); // 点击图标移动
							if (i == 1) {
								lp.leftMargin = (int) (1.0 / mSize * (screenWidth * 1.0 / mSize)) + currentIndex
										* (screenWidth / tvs.size());
								mTabLineIv.setLayoutParams(lp);
							}

						} else {
							tvs.get(i).setTextColor(getResources().getColor(R.color.common_text_gray));
						}
					}
					//mTabLineIv.setLayoutParams(lp);
				}
			});
		}
	}

	/**
	 * * 设置滑动条的宽度为屏幕的1/3(根据Tab的个数而定)
	 * */
	private void initTabLineWidth() {
		DisplayMetrics dpMetrics = new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
		screenWidth = dpMetrics.widthPixels;
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTabLineIv.getLayoutParams();
		lp.width = screenWidth / mSize;
		mTabLineIv.setLayoutParams(lp);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		switch (v.getId()) {
		case R.id.titlebar_right_btn:
			isDelete = mTitleBar.getRightText().endsWith("编辑");
			if (isDelete) {// 开始为true
				Intent intent = new Intent();
				intent.putExtra(COLLECTIONDELECT, isDelete);
				intent.setAction(COLLECTIONDELECTACTION);
				sendBroadcast(intent);
				isDelete = !isDelete;
				mTitleBar.setRightText("完成");

			} else {
				Intent intent = new Intent();
				intent.putExtra(COLLECTIONDELECT, isDelete);
				intent.setAction(COLLECTIONDELECTACTION);
				sendBroadcast(intent);
				isDelete = !isDelete;
				mTitleBar.setRightText("编辑");
			}
			break;
		default:

			break;
		}
		
	}

	/** * 重置颜色 */
	private void resetTextView() {
		for (int i = 0; i < tvs.size(); i++) {
			tvs.get(i).setTextColor(getResources().getColor(R.color.common_text_gray));
		}
	}

	/** * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。 */
	@Override
	public void onPageScrollStateChanged(int state) {
	}

	/**
	 * * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比 * offsetPixels:当前页面偏移的像素位置
	 */
	@Override
	public void onPageScrolled(int position, float offset, int offsetPixels) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTabLineIv.getLayoutParams();
		/**
		 * * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来 * 设置mTabLineIv的左边距
		 * 滑动场景： * 记3个页面, * 从左到右分别为0,1,2
		 * 
		 * * 0->1; 1->2; 2->1; 1->0
		 * */
		if (currentIndex == position)// 0->1
		{
			lp.leftMargin = (int) (offset * (screenWidth * 1.0 / mSize) + currentIndex * (screenWidth / mSize));
		} else if (currentIndex > position) // 1->0
		{
			lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / mSize) + currentIndex * (screenWidth / mSize));
		}
		mTabLineIv.setLayoutParams(lp);
	}

	@Override
	public void onPageSelected(int position) {

		resetTextView();
		currentIndex = position;
		if (!isDelete) {// 是完成时
			Intent intent = new Intent();
			intent.putExtra(COLLECTIONDELECT, isDelete);
			intent.setAction(COLLECTIONDELECTACTION);
			sendBroadcast(intent);
			mTitleBar.setRightText("编辑");
		}
		if (tvs.size() != 0) {
			for (int i = 0; i < tvs.size(); i++) {
				if (i == position) {
					tvs.get(i).setTextColor(mDefaultColor);
				} else {
					tvs.get(i).setTextColor(getResources().getColor(R.color.common_text_gray));
				}
			}
			int move = tvs.get(position).getWidth() * (tvs.size() - (tvs.size() - position)); // 点击移动距离
			hsv.smoothScrollBy(move, 0); // 点击图标移动
		}
	}
}
