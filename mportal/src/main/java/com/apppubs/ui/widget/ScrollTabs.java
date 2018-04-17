package com.apppubs.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apppubs.util.LogM;
import com.apppubs.d20.R;
import com.apppubs.util.Utils;

/**
 * 可滚动的栏目条
 * 
 * @author Administrator
 * 
 */
public class ScrollTabs extends RelativeLayout {

	private static final int LEFT_MARGIN_DP = 5;
	private Context mContext;
	private float mHeight;
	private LinearLayout ll;
	private LayoutParams llLp;
	private ImageView cursorIv;
	private int mCurPos;
	private TextView currentTabTv;
	private OnItemClickListener mOnItemClickListener;
	private OnColunmBtnClickListener mOnColumnBtnClickListener;
	private String[] tabsArr;
	private HorizontalScrollView mHsv;
	private TextView mColumnBar;// 切换栏目标题
	private View mCursorV;
	private View mSelColV;// 选择栏目按钮
	private PopupWindow mPw;// 栏目选择框

	private int mTextColor = Color.BLACK;
	private int mSelectedTextColor = Color.BLACK;
	private float mTextSize = -1;
	private float mSelectedTextSize = -1;
	private float mItemWidth;

	private float mDTextSize = 0;

	int mR = Color.red(mTextColor);
	int mG = Color.green(mTextColor);
	int mB = Color.blue(mTextColor);
	int mSr = Color.red(mSelectedTextColor);
	int mSg = Color.green(mSelectedTextColor);
	int mSb = Color.blue(mSelectedTextColor);

	private int screenW;
	private int screenH;
	private DisplayMetrics mDisplayMetircs;
	private float scale;
	private boolean isSelectMode;// 是否为选择模式
	private boolean mIsAllowConfig;// 是否允许配置栏目
	private boolean mHaveDownArrow;//是否有向下的箭头
	private LayoutParams hsvLp = null;
	private FrameLayout selBtnCon = null;
	private ImageView mDownArrow;

	public interface OnItemClickListener {
		void onclick(int pos);
	}

	public interface OnColunmBtnClickListener {
		void onClick(boolean isOpen);
	}

	public ScrollTabs(Context context){
		this(context,null);
	}
	
	public ScrollTabs(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mDisplayMetircs = context.getResources().getDisplayMetrics();
		scale = mDisplayMetircs.density;
		screenW = mDisplayMetircs.widthPixels;
		screenH = mDisplayMetircs.heightPixels;

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScrollTabs);
		int textColor = ta.getColor(R.styleable.ScrollTabs_textColor, Color.BLACK);
		int selectedTextColor = ta.getColor(R.styleable.ScrollTabs_selectedTextColor, Color.BLACK);
		float textSize = ta.getDimension(R.styleable.ScrollTabs_textSize, 24f);
		float selectedTextSize = ta.getDimension(R.styleable.ScrollTabs_selectedTextSize, 30f);
		mItemWidth = ta.getDimension(R.styleable.ScrollTabs_tabMinWidth, 50f);
		ta.recycle();

		mHsv = new HorizontalScrollView(context);// 水平滚动视图，内包含cursor和tabs
		mHsv.setHorizontalScrollBarEnabled(false);
		FrameLayout scrollCon = new FrameLayout(context);// 滚动tab容器，包含一个linearlayout（tabs）和一个cursorview

		ll = new LinearLayout(context);
		ll.setBackgroundColor(Color.TRANSPARENT);

		mCursorV = new View(context);
		
		FrameLayout.LayoutParams cursorVLp = new FrameLayout.LayoutParams(40, Utils.dip2px(mContext, 2));
		cursorVLp.leftMargin = Utils.dip2px(mContext, LEFT_MARGIN_DP);
		cursorVLp.gravity = Gravity.BOTTOM;
		scrollCon.addView(mCursorV, cursorVLp);
		scrollCon.addView(ll);

		llLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		mHsv.addView(scrollCon, llLp);

		if (mIsAllowConfig) {

			hsvLp = new LayoutParams((int) (screenW - scale * 40), LayoutParams.MATCH_PARENT);
		} else {
			hsvLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
		this.addView(mHsv, hsvLp);

		// 栏目选择模式
		mColumnBar = new TextView(context);
		mColumnBar.setText("切换栏目 长按拖动可更改顺序");
		mColumnBar.setGravity(Gravity.CENTER_VERTICAL);
		mColumnBar.setPadding(20, 0, 0, 0);
		mColumnBar.setTextColor(Color.parseColor("#1B1B1B"));
		mColumnBar.setBackgroundColor(Color.parseColor("#E9ffffff"));
		mColumnBar.setVisibility(View.GONE);
		LayoutParams columnBarLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addView(mColumnBar, columnBarLp);

		mSelColV = new View(context);
		mSelColV.setBackgroundResource(R.drawable.down);
		selBtnCon = new FrameLayout(context);
		selBtnCon.setBackgroundResource(R.drawable.col_btn_bg);
		FrameLayout.LayoutParams selBtnLp = new FrameLayout.LayoutParams((int) (scale * 40), (int) (scale * 40));
		selBtnLp.gravity = Gravity.RIGHT;
		selBtnLp.rightMargin = (int) (scale * 10);
		selBtnCon.addView(mSelColV, selBtnLp);
		LayoutParams selColLp = new LayoutParams((int) (80 * scale), LayoutParams.MATCH_PARENT);
		selColLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		selColLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		selBtnCon.setLayoutParams(selColLp);
		if (mIsAllowConfig) {
			this.addView(selBtnCon);
		}

		mSelColV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openOrCloseSelectMode(!isSelectMode);
			}
		});
		
		mDownArrow = new ImageView(mContext);
		LayoutParams downArrayLp = new LayoutParams((int) (40 * scale), LayoutParams.MATCH_PARENT);
		downArrayLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		downArrayLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		mDownArrow.setLayoutParams(downArrayLp);
		mDownArrow.setImageResource(R.drawable.down);
		mDownArrow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mOnColumnBtnClickListener != null) {
					mOnColumnBtnClickListener.onClick(true);
				}				
			}
		});
		setTextColor(textColor);
		setSelectedTextColor(selectedTextColor);
		setTextSize((int)textSize);
		setSelectedSize((int)selectedTextSize);

	}

	public void openOrCloseSelectMode(boolean isOpen) {
		final Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_180);
		final Animation anim1 = AnimationUtils.loadAnimation(mContext, R.anim.rotate_180_1);
		if (isOpen) {
			isSelectMode = true;
			mColumnBar.setVisibility(View.VISIBLE);
			mSelColV.startAnimation(anim);
		} else {
			isSelectMode = false;
			mColumnBar.setVisibility(View.GONE);
			mSelColV.startAnimation(anim1);
		}

		if (mOnColumnBtnClickListener != null) {
			mOnColumnBtnClickListener.onClick(isOpen);
		}

	}

	public void setTextColor(int color){
		mTextColor = color;
		mR = Color.red(mTextColor);
		mG = Color.green(mTextColor);
		mB = Color.blue(mTextColor);
	}
	
	public void setSelectedTextColor(int color){
		mSelectedTextColor = color;
		mSr = Color.red(mSelectedTextColor);
		mSg = Color.green(mSelectedTextColor);
		mSb = Color.blue(mSelectedTextColor);
		mCursorV.setBackgroundColor(mSelectedTextColor);
	}
	
	public void setTextSize(int size){
		mTextSize = size;
		calculateDSize();
	}

	public void setSelectedSize(int size){
		mSelectedTextSize = size;
		calculateDSize();
	}
	
	private void calculateDSize() {
		if(mTextSize!=-1&&mSelectedTextSize!=-1){
			mDTextSize = mSelectedTextSize - mTextSize;
		}
	}
	
	public boolean isSelectMode() {
		return isSelectMode;
	}

	public void setSelectMode(boolean isSelectMode) {
		this.isSelectMode = isSelectMode;
	}

	public void setIsAllowConfiguration(boolean isAllow) {
		if (mIsAllowConfig == isAllow) {
			return;
		}
		mIsAllowConfig = isAllow;
		if (mIsAllowConfig) {

			hsvLp = new LayoutParams((int) (screenW - scale * 40), LayoutParams.MATCH_PARENT);
			mHsv.setLayoutParams(hsvLp);
			this.addView(selBtnCon);
		} else {
			hsvLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			this.removeView(selBtnCon);
		}
	}
	
	public void setHaveDownArrow(boolean haveDownArrow) {
		if(mHaveDownArrow==haveDownArrow){
			return;
		}
		this.mHaveDownArrow = haveDownArrow;
		
		if (haveDownArrow) {

			hsvLp = new LayoutParams((int) (screenW - scale * 40), LayoutParams.MATCH_PARENT);
			mHsv.setLayoutParams(hsvLp);
			this.addView(mDownArrow);
		} else {
			hsvLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			this.removeView(mDownArrow);
		}
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	public void setOnColumnBtnClickListener(OnColunmBtnClickListener onColumnBtnClickListener) {
		this.mOnColumnBtnClickListener = onColumnBtnClickListener;
	}

	public String[] getTabsArr() {
		return tabsArr;
	}

	public ImageView getCursorIv() {
		return this.cursorIv;
	}

	public LinearLayout getTabsLinearLayout() {
		return this.ll;
	}

	public int getCurrentTab() {
		return mCurPos;
	}

	public TextView getCurrentTabTv() {
		return currentTabTv;
	}

	public interface OnScrollListener {
		void scroll(int l, int t, int oldl, int oldt);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	/**
	 * 选定某一个元素 更改样式
	 * 
	 * @param currentTab
	 *            当前元素
	 */
	public void setCurrentTab(int currentTab) {

		LogM.log(getClass(), "setCurrentTab:" + currentTab);

		int count = ll.getChildCount();
		if (count == 0)
			return;
		// 首先清理上次的点击的样式
		for (int i = -1; ++i < count;) {
			TextView cur = (TextView) ll.getChildAt(i);
			cur.setTextColor(mTextColor);
			cur.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);

		}
		this.mCurPos = currentTab;
		currentTabTv = (TextView) ll.getChildAt(currentTab);
		currentTabTv.setTextColor(mSelectedTextColor);
		currentTabTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectedTextSize);
		scrollIfExceed();

	}

	int firstTabWidth = 0;
	private boolean isFirstTab = true;
	public void addTab(String name) {
		
		TextView tv = new TextView(this.mContext);
		tv.setText(name);
		int padding = Utils.dip2px(mContext, 15);
		//宽度为：大字体的宽度*字数+左右边距
		android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams((int) (padding*2+mSelectedTextSize * name.length()+5),
				LayoutParams.MATCH_PARENT);
		
		tv.setPadding(padding, 0, padding, 0);
		
		if (isFirstTab) {
			firstTabWidth = lp.width;
			lp.leftMargin = Utils.dip2px(mContext, LEFT_MARGIN_DP);
			System.out.println("加载第一个tab。。。。。。。。。。"+lp.width);
			ViewGroup.LayoutParams cursorLp = mCursorV.getLayoutParams();
			cursorLp.width = firstTabWidth;
			mCursorV.setLayoutParams(cursorLp);
		} else {

		}
		// tv.setMinimumWidth((int)mItemWidth);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(mTextColor);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int index = ll.indexOfChild(v);
				setCurrentTab(index);
				if (mOnItemClickListener != null)
					mOnItemClickListener.onclick(index);
			}
		});
		ll.addView(tv, lp);
		isFirstTab = false;
	}

	public void removeAll() {
		ll.removeAllViews();
	}

	public TextView getItem(int index) {
		return (TextView) ll.getChildAt(index);
	}

	private void scrollIfExceed() {
		if (mCurPos > 0 && mCurPos < ll.getChildCount() - 1) {

			TextView nextTab = (TextView) ll.getChildAt(mCurPos + 1);
			TextView preTab = (TextView) ll.getChildAt(mCurPos - 1);

			int temp = 0;
			if ((temp = currentTabTv.getRight() + nextTab.getWidth() - this.getWidth() - mHsv.getScrollX()) > 0
					|| (temp = currentTabTv.getLeft() - preTab.getWidth() - mHsv.getScrollX()) < 0) {
				mHsv.smoothScrollBy(temp, 0);
			}
		} else if (mCurPos == 0) {// 如果当前tab是第一个则直接滚动到最前
			mHsv.smoothScrollTo(0, 0);
		} else if (mCurPos == ll.getChildCount() - 1) {// 如果当前tab是最后一个则直接滚动到最后
			mHsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
		}

	}

	public void onPageScrolled(int position, float offset) {

		TextView cur = this.getItem(position);
		TextView next = this.getItem(position + 1);

		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mCursorV.getLayoutParams();

		if (cur != null) {

			if (cur.getWidth() != 0) {
				lp.leftMargin = (int) (cur.getLeft() + offset * cur.getWidth());
			}
			cur.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectedTextSize - offset * mDTextSize);

			int temp = Color.rgb((int) (mSr - offset * (mSr - mR)), (int) (mSg - offset * (mSg - mG)), (int) (mSb - offset
					* (mSb - mB)));
			cur.setTextColor(temp);

		}

		if (next != null) {
			if (cur.getWidth() == 0) {
				lp.width = firstTabWidth;
			} else {
				lp.width = (int) (cur.getWidth() + offset * (next.getWidth() - cur.getWidth()));
			}

			LogM.log(this.getClass(), "onPageScrolled lp.width:" + lp.width + "cur.getWidth():" + cur.getWidth() + "offset:"
					+ offset);
			int temp = Color.rgb((int) (mR + offset * (mSr - mR)), (int) (mG + offset * (mSg - mG)), (int) (mB + offset
					* (mSb - mB)));
			next.setTextColor(temp);

			next.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize + offset * mDTextSize);
		}

		mCursorV.setLayoutParams(lp);
	}

}
