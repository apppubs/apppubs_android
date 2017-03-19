package com.apppubs.d20.widget;

/**
 * 提供选择子view的流动布局
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class CheckableFlowLayout extends ViewGroup implements OnClickListener {

	private static final String TAG = "FlowLayout";

	private Context mContext;
	private float mDensity;
	private float mScale;// 当前屏幕宽度和380dp的比值
	private boolean isHorizontallCenter;
	private OnItemClickListener mOnItemClickListener;
	private int mTagSize;
	private int mMaxSelectedNum = 0;// 最大可选择数量
	private Map<Integer, TextView> mSelectedTagMap;
	private GradientDrawable mTagBgGd;// 标签普通背景
	private GradientDrawable mTagHighlightBgGd;// 高亮

	public interface OnItemClickListener {
		/**
		 * 标签选择回调
		 * 
		 * @param pos
		 *            被点击的标签的索引
		 * @param tag
		 *            标签文字
		 * @param isSelect
		 *            是否是点击动作 true：选择 false：取消选择
		 */
		void onItemClick(int pos, String tag, boolean isSelect);

		/*
		 * 超出最大可选数量
		 */
		void onExceedMaxSelectedNum();
	}

	public CheckableFlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
//		TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.CheckableFlowLayout, 0, 0);
//		isHorizontallCenter = t.getBoolean(R.styleable.CheckableFlowLayout_is_horizontal_center, false);
//		t.recycle();
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		mDensity = dm.density;
		mScale = dm.widthPixels / dm.density / 375;

		System.out.println("当前设备宽度dp:" + dm.widthPixels / dm.density + "比例：" + mScale);
		mSelectedTagMap = new HashMap<Integer, TextView>();

		mTagBgGd = new GradientDrawable();// 创建drawable
		mTagBgGd.setColor(Color.parseColor("#FFFFFF"));
		mTagBgGd.setCornerRadius((float) (mDensity * 15 * mScale));
		mTagBgGd.setStroke(1, Color.parseColor("#E5E5E5"));

		mTagHighlightBgGd = new GradientDrawable();
		mTagHighlightBgGd.setColor(Color.parseColor("#F55E53"));
		mTagHighlightBgGd.setCornerRadius((float) (mDensity * 15 * mScale));
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new MarginLayoutParams(p);
	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	/**
	 * 负责设置子控件的测量模式和大小 根据所有子控件设置自己的宽和高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获得它的父容器为它设置的测量模式和大小
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		// Log.e(TAG, sizeWidth + "," + sizeHeight);

		// 如果是warp_content情况下，记录宽和高
		int width = 0;
		int height = 0;
		/**
		 * 记录每一行的宽度，width不断取最大宽度
		 */
		int lineWidth = 0;
		/**
		 * 每一行的高度，累加至height
		 */
		int lineHeight = 0;

		int cCount = getChildCount();

		// 遍历每个子元素
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			// 测量每一个child的宽和高
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			// 得到child的lp
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			// 当前子空间实际占据的宽度
			int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
			// 当前子空间实际占据的高度
			int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
			/**
			 * 如果加入当前child，则超出最大宽度，则的到目前最大宽度给width，类加height 然后开启新行
			 */
			if (lineWidth + childWidth > sizeWidth) {
				width = Math.max(lineWidth, childWidth);// 取最大的
				lineWidth = childWidth; // 重新开启新行，开始记录
				// 叠加当前高度，
				height += lineHeight;
				// 开启记录下一行的高度
				lineHeight = childHeight;
			} else
			// 否则累加值lineWidth,取最大高度
			{
				lineWidth += childWidth;
				lineHeight = Math.max(lineHeight, childHeight);
			}
			// 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
			if (i == cCount - 1) {
				width = Math.max(width, lineWidth);
				height += lineHeight;
			}

		}
		height += getPaddingBottom() + getPaddingTop();
		setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width,
				(modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);

	}

	/**
	 * 存储所有的View，按行记录
	 */
	private List<List<View>> mAllViews = new ArrayList<List<View>>();
	/**
	 * 记录每一行的最大高度
	 */
	private List<Integer> mLineHeight = new ArrayList<Integer>();
	// 横向居中时每行x坐标的偏移量
	private List<Float> mLineOffsetXList = new ArrayList<Float>();

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		mLineOffsetXList.clear();
		mAllViews.clear();
		mLineHeight.clear();

		int width = getWidth();

		int lineWidth = 0;
		int lineHeight = 0;
		// 存储每一行所有的childView
		List<View> lineViews = new ArrayList<View>();
		int cCount = getChildCount();

		// 遍历所有的孩子
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			// 如果已经需要换行
			if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {

				// 记录这一行的x偏移量
				if (isHorizontallCenter) {
					mLineOffsetXList.add((width - lineWidth) / 2.0f);
				}
				// 记录这一行所有的View以及最大高度
				mLineHeight.add(lineHeight);
				// 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
				mAllViews.add(lineViews);
				lineWidth = 0;// 重置行宽
				lineViews = new ArrayList<View>();
			}
			/**
			 * 如果不需要换行，则累加
			 */
			lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
			lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
			lineViews.add(child);
		}
		// 记录最后一行
		mLineHeight.add(lineHeight);
		mAllViews.add(lineViews);
		if (isHorizontallCenter) {
			mLineOffsetXList.add((width - lineWidth) / 2.0f);
		}

		int left = 0;
		int top = 0;
		// 得到总行数
		int lineNums = mAllViews.size();
		for (int i = 0; i < lineNums; i++) {
			// 每一行的所有的views
			lineViews = mAllViews.get(i);
			// 当前行的最大高度
			lineHeight = mLineHeight.get(i);

			// Log.e(TAG, "第" + i + "行 ：" + lineViews.size() + " , " +
			// lineViews);
			// Log.e(TAG, "第" + i + "行， ：" + lineHeight);

			float offsetX = 0;
			if (isHorizontallCenter) {
				offsetX = mLineOffsetXList.get(i);
			}
			// 遍历当前行所有的View
			for (int j = 0; j < lineViews.size(); j++) {
				View child = lineViews.get(j);
				if (child.getVisibility() == View.GONE) {
					continue;
				}
				MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

				// 计算childView的left,top,right,bottom
				int lc = (int) (left + lp.leftMargin + offsetX);
				int tc = top + lp.topMargin;
				int rc = lc + child.getMeasuredWidth();
				int bc = tc + child.getMeasuredHeight();

				// Log.e(TAG, child + " , l = " + lc + " , t = " + t + " , r ="
				// + rc + " , b = " + bc);

				child.layout(lc, tc, rc, bc);

				left += child.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;
			}
			left = 0;
			top += lineHeight;
		}

	}

	/**
	 * 增加标签
	 * 
	 * @param tag 标签名称
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void addTag(String tag) {

		TextView tv = new TextView(getContext());
		tv.setTag(mTagSize);
		tv.setText(tag);
		tv.setOnClickListener(this);
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mScale * 13);
		tv.setTextColor(Color.parseColor("#555555"));
		Paint paint = new Paint();
		paint.setTextSize(mScale * 13 * mDensity);
		float textWidth = paint.measureText(tag);
		int width = (int) (textWidth + 28 * mDensity * mScale + 0.5);

		MarginLayoutParams lp = new MarginLayoutParams(width, (int) (25 * mDensity * mScale + 0.5));
		int dp10 = (int) (mDensity * 10 * mScale);
		int dp15 = (int) (mDensity * 15 * mScale);
		lp.leftMargin = dp10;
		lp.rightMargin = dp10;
		lp.topMargin = dp15;

		mTagBgGd = new GradientDrawable();// 创建drawable
		mTagBgGd.setColor(Color.parseColor("#FFFFFF"));
		mTagBgGd.setCornerRadius((float) (mDensity * 15 * mScale));
		mTagBgGd.setStroke(1, Color.parseColor("#E5E5E5"));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			tv.setBackground(mTagBgGd);
		} else {
			tv.setBackgroundDrawable(mTagBgGd);
		}

		mTagSize++;

		addView(tv, lp);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	public void setMaxSelectedNum(int maxNum) {
		mMaxSelectedNum = maxNum;
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		if (mOnItemClickListener != null) {
			int pos = (Integer) v.getTag();
			TextView tv = (TextView) getChildAt(pos);
			boolean isSel = false;
			if (mSelectedTagMap.containsKey(pos)) {
				isSel = false;
				mSelectedTagMap.remove(pos);
				tv.setTextColor(Color.parseColor("#555555"));

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					tv.setBackground(mTagBgGd);
				} else {
					tv.setBackgroundDrawable(mTagBgGd);
				}
			} else {
				if (mSelectedTagMap.keySet().size() == mMaxSelectedNum && mOnItemClickListener != null) {
					mOnItemClickListener.onExceedMaxSelectedNum();
				} else {
					mSelectedTagMap.put(pos, tv);
					isSel = true;
				}

			}
			if (mOnItemClickListener!=null) {
				mOnItemClickListener.onItemClick(pos, tv.getText().toString(), isSel);
			}
		}
		if(mMaxSelectedNum>0){
			refreshTagsBackground();
		}
	}

	/**
	 * 移除所有标签
	 */
	public void clearAllTags() {
		removeAllViews();
		mTagSize = 0;
		mSelectedTagMap.clear();
	}

	@SuppressLint("NewApi")
	private void refreshTagsBackground() {

		for (int index : mSelectedTagMap.keySet()) {
			TextView tv = mSelectedTagMap.get(index);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				tv.setBackground(mTagHighlightBgGd);
			} else {
				tv.setBackgroundDrawable(mTagHighlightBgGd);
			}
			tv.setTextColor(Color.WHITE);

		}
	}

	/**
	 * 获取当前选中的所有标签
	 * 
	 * @return
	 */
	public List<String> getSelectedTags() {
		List<String> tags = new ArrayList<String>();
		for (int pos : mSelectedTagMap.keySet()) {
			tags.add(mSelectedTagMap.get(pos).getText().toString());
		}
		return tags;
	}

}
