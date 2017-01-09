package com.mportal.client.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mportal.client.R;
import com.mportal.client.util.LogM;
import com.mportal.client.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 顶部标题栏
 * 
 * @author zhangwen
 * 
 */
public class TitleBar extends RelativeLayout {

	public final static int ID_RIGHT_BTN = R.id.titlebar_right_btn;
	public final static int ID_LEFT_BTN = R.id.titlebar_left_btn;

	private Context mContext;
	private String mLeftText;
	private float mLeftTextSize;
	private Drawable mLeftBackground;
	private Drawable mLeftImage;
	private int mLeftTextColor;

	private String mRightText;
	private Drawable mRightBackground;
	private Drawable mRightImage;
	private float rightTextSize;
	private ColorStateList mRightTextColor;

	private float mTitleTextSize;
	private int mTitleTextColor;
	private String mTitle;
	private Drawable mConterBackground;

	private Button mLeftBtn;
	private TextView mTitleTv;
	private Button mRightBtn;
	private Button mRight2ndBtn;

	/**
	 * 当前使用的view
	 */
	private View mCurLeftView;
	private View mCurRightView;
	private View mCurLeft2ndView;// 左边第二个按钮
	private View mCurRight2ndView;// 从右边数第二个按钮
	private View mCurTitleView;

	/**
	 * 下划线
	 */
	private View underLineView;
	
	private OnClickListener mLeftClickListener;
	private OnClickListener mRightClickListener;
	private OnClickListener mLeft2ndClickListener;
	private OnClickListener mRight2ndClickListener;
	private OnClickListener mTitleClickListener;

	private LayoutParams mLeftLp, mLeft2ndLp, mRight2ndLp, mRightLp, mTitleLp;

	private int mImageBtnWidth;
	
	public TitleBar(Context context){
		this(context,null);
	}
	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
		mLeftTextColor = ta.getColor(R.styleable.TitleBar_leftTextColor, -1);
		mLeftTextSize = ta.getDimension(R.styleable.TitleBar_leftTextSize,Utils.dip2px(context,15));
		mLeftBackground = ta.getDrawable(R.styleable.TitleBar_leftBackground);
		mLeftImage = ta.getDrawable(R.styleable.TitleBar_leftImgSrc);
		mLeftText = ta.getString(R.styleable.TitleBar_leftText);
		mRightTextColor = ta.getColorStateList(R.styleable.TitleBar_rightTextColor);
		mRightBackground = ta.getDrawable(R.styleable.TitleBar_rightBackground);
		mRightImage = ta.getDrawable(R.styleable.TitleBar_rightImgSrc);
		mConterBackground = ta.getDrawable(R.styleable.TitleBar_conterBackground);
		mRightText = ta.getString(R.styleable.TitleBar_rightText);
		rightTextSize = ta.getDimension(R.styleable.TitleBar_rightTextSize, 25);
		mTitle = ta.getString(R.styleable.TitleBar_mTitle);
		mTitleTextColor = ta.getColor(R.styleable.TitleBar_titleColor, -1);
		mTitleTextSize = ta.getDimension(R.styleable.TitleBar_titleSize, 25);
		ta.recycle();

		mImageBtnWidth = Utils.dip2px(mContext, 56);
		
		mLeftBtn = new Button(context);
		mTitleTv = new TextView(context);
		mRightBtn = new Button(context, null, R.style.ButtonWhiteText);
		mRight2ndBtn = new Button(context, null, R.style.ButtonWhiteText);

		mLeftBtn.setBackgroundDrawable(mLeftBackground);
		mLeftBtn.setTextColor(mLeftTextColor);
		mLeftBtn.setId(R.id.titlebar_left_btn);
		mLeftBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX,mLeftTextSize);
		mLeftBtn.setText(mLeftText);
		mLeftBtn.setGravity(Gravity.CENTER);

		mLeftBtn.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.title_padding_left), 0, mContext
				.getResources().getDimensionPixelSize(R.dimen.title_padding_right), 0);

		mRightBtn.setId(ID_RIGHT_BTN);
		mRightBtn.setText(mRightText);
		mRightBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
		mRight2ndBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
		mRightBtn.setBackgroundDrawable(mRightBackground);
		mRightBtn.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.title_padding_left), 0, mContext
				.getResources().getDimensionPixelSize(R.dimen.title_padding_right), 0);

		if (mRightTextColor == null) {
			mRightTextColor = getResources().getColorStateList(R.drawable.button_text_color);
		}
		mRightBtn.setTextColor(mRightTextColor);
		mRight2ndBtn.setTextColor(mRightTextColor);
		mRightBtn.setIncludeFontPadding(false);
		mRightBtn.setGravity(Gravity.CENTER);
		mRight2ndBtn.setIncludeFontPadding(false);
		mRight2ndBtn.setGravity(Gravity.CENTER);

		mTitleTv.setId(R.id.titlebar_title_tv);
		mTitleTv.setBackgroundDrawable(mConterBackground);
		mTitleTv.setText(mTitle);
		mTitleTv.setTextColor(mTitleTextColor);
		mTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);
		mTitleTv.setGravity(Gravity.CENTER);
		mTitleTv.setSingleLine(true);
		mLeftLp = new LayoutParams(mImageBtnWidth, LayoutParams.MATCH_PARENT);
		mLeftLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);

		addView(mLeftBtn, mLeftLp);
		mCurLeftView = mLeftBtn;

		mLeft2ndLp = new LayoutParams(Utils.dip2px(context, 40), LayoutParams.MATCH_PARENT);
		mRight2ndLp = new LayoutParams(mImageBtnWidth, LayoutParams.MATCH_PARENT);

		mRightLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		mRightLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
		if(mRightText!=null||mRightBackground!=null){
			addView(mRightBtn, mRightLp);
			mCurRightView = mRightBtn;
		}

		mTitleLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mTitleLp.leftMargin = mImageBtnWidth;
		mTitleLp.rightMargin = 30;
		mTitleLp.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
		addView(mTitleTv, mTitleLp);
		mCurTitleView = mTitleTv;

		if (mLeftImage != null) {
			setLeftImageDrawable(mLeftImage);
		}
		if (mRightImage != null) {
			setRightImageDrawable(mRightImage);
		}

	}

	/**
	 * 设置左边的view
	 * 
	 * @param view
	 */
	public void setLeftView(View view) {
		removeView(mCurLeftView);
		mCurLeftView = view;
		mCurLeftView.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.title_padding_left), 0, mContext
				.getResources().getDimensionPixelSize(R.dimen.title_padding_right), 0);
		addView(mCurLeftView, mLeftLp);
		mCurLeftView.setOnClickListener(mLeftClickListener);
	}

	public void setLeftBtnWithText(String text) {
		removeView(mCurLeftView);
		mLeftBtn.setText(text);
		mCurLeftView = mLeftBtn;
		mLeftLp.width =  LayoutParams.WRAP_CONTENT;
		addView(mCurLeftView, mLeftLp);
	}

	public void setLeftImageResource(int resId) {
		setLeftImageDrawable(getResources().getDrawable(resId));
	}

	public void setLeftImageDrawable(Drawable drawable) {
		ImageButton ib = new ImageButton(mContext);
		ib.setImageDrawable(drawable);
		ib.setHightLightColor(getResources().getColor(R.color.button_white_hight_color));
		ib.setPadding(getResources().getDimensionPixelSize(R.dimen.title_padding_left), 0, mContext.getResources()
				.getDimensionPixelSize(R.dimen.title_padding_right), 0);
		ib.setId(ID_LEFT_BTN);
		removeView(mCurLeftView);
		mCurLeftView = ib;
		mCurLeftView.setOnClickListener(mLeftClickListener);
		addView(ib, mLeftLp);
	}

	public void setLeftBtnWithImageUrl(String iamgeUrl) {
		
		ImageButton ib = new ImageButton(mContext);
		ib.setHightLightColor(getResources().getColor(R.color.button_white_hight_color));
		int padding = getResources().getDimensionPixelSize(R.dimen.title_padding_left);
		ib.setPadding(padding, padding, padding, padding);
		ib.setId(ID_LEFT_BTN);
		ib.setScaleType(ScaleType.CENTER_CROP);
		removeView(mCurLeftView);
		mCurLeftView = ib;
		ImageLoader.getInstance().displayImage(iamgeUrl, ib);
		if(mLeftClickListener!=null){
			mCurLeftView.setOnClickListener(mLeftClickListener);
		}
		addView(ib, mLeftLp);
	}

	public void setRightBtnImageResourceId(int resId) {
		setRightImageDrawable(getResources().getDrawable(resId));
	}

	public void setRightImageWithUrl(String url) {

		removeView(mCurRightView);
		ImageButton ib = new ImageButton(mContext);
		ib.setScaleType(ScaleType.CENTER_CROP);
		ib.setHightLightColor(getResources().getColor(R.color.button_white_hight_color));
		int padding = getResources().getDimensionPixelSize(R.dimen.title_padding_left);
		ib.setPadding(padding, padding, padding, padding);
		ib.setId(ID_RIGHT_BTN);
		mCurRightView = ib;
		mCurRightView.setOnClickListener(mRightClickListener);
		mRightLp.width = mImageBtnWidth;
		ImageLoader.getInstance().displayImage(url, ib);
		addView(ib, mRightLp);
	}

	private void setRightImageDrawable(Drawable drawable) {

		removeView(mCurRightView);

		ImageButton ib = new ImageButton(mContext);
		ib.setScaleType(ScaleType.CENTER_INSIDE);
		ib.setImageDrawable(drawable);
		ib.setHightLightColor(getResources().getColor(R.color.button_white_hight_color));
		ib.setDefaultColor(Color.WHITE);
		int padding = getResources().getDimensionPixelSize(R.dimen.title_padding_left);
		ib.setPadding(padding, 0, padding, 0);
		ib.setId(ID_RIGHT_BTN);
		mCurRightView = ib;
		mCurRightView.setOnClickListener(mRightClickListener);
		mRightLp.width = mImageBtnWidth;
		addView(ib, mRightLp);
	}

	public void setRightBtnWithText(String text) {
		removeView(mCurRightView);
		mRightBtn.setText(text);
		mCurRightView = mRightBtn;
		mRightLp.width =  LayoutParams.WRAP_CONTENT;
		addView(mCurRightView, mRightLp);
	}

	public void setRight2ndBtnWithText(String text) {
		removeView(mCurRight2ndView);
		mRight2ndBtn.setText(text);
		mCurRight2ndView = mRight2ndBtn;
		mRight2ndLp.addRule(RelativeLayout.LEFT_OF, mCurRightView.getId());
		addView(mCurRight2ndView, mRight2ndLp);
	}

	public void setLeft2ndBtnWithImageResourceId(int resId) {
		ImageButton ib = new ImageButton(mContext);
		ib.setImageResource(resId);
		ib.setDefaultColor(Color.WHITE);
		ib.setScaleType(ScaleType.CENTER_INSIDE);
		ib.setHightLightColor(getResources().getColor(R.color.button_white_hight_color));
		ib.setPadding(0, getResources().getDimensionPixelSize(R.dimen.title_padding_left), mContext.getResources()
				.getDimensionPixelSize(R.dimen.title_padding_right),
				getResources().getDimensionPixelSize(R.dimen.title_padding_right));
		removeView(mCurLeft2ndView);
		mCurLeft2ndView = ib;
		if (mLeft2ndClickListener != null)
			mCurLeft2ndView.setOnClickListener(mLeft2ndClickListener);
		mLeft2ndLp.addRule(RelativeLayout.RIGHT_OF, mCurLeftView.getId());
		addView(ib, mLeft2ndLp);
	}
	
	public void setLeft2ndBtnWithImageUrl(String imageUrl){
		ImageButton ib = new ImageButton(mContext);
		ib.setHightLightColor(getResources().getColor(R.color.button_white_hight_color));
		ib.setPadding(getResources().getDimensionPixelSize(R.dimen.title_padding_left), 0, mContext.getResources()
				.getDimensionPixelSize(R.dimen.title_padding_right),
				0);
		ImageLoader.getInstance().displayImage(imageUrl,ib);
		removeView(mCurLeft2ndView);
		mCurLeft2ndView = ib;
		if (mLeft2ndClickListener != null){
			mCurLeft2ndView.setOnClickListener(mLeft2ndClickListener);
		}
		mLeft2ndLp.width = mImageBtnWidth;
		mLeft2ndLp.addRule(RelativeLayout.RIGHT_OF, mCurLeftView.getId());
		addView(ib, mLeft2ndLp);
	}

	public void setRight2ndBtnWithUrl(String imageUrl) {

		ImageButton ib = new ImageButton(mContext);
		ib.setHightLightColor(getResources().getColor(R.color.button_white_hight_color));
		ib.setPadding( getResources().getDimensionPixelSize(R.dimen.title_padding_left), 0, getResources()
				.getDimensionPixelSize(R.dimen.title_padding_right),0);
		ib.setScaleType(ScaleType.CENTER_INSIDE);
		removeView(mCurRight2ndView);
		mCurRight2ndView = ib;

		ImageLoader.getInstance().displayImage(imageUrl, ib);

		if (mRight2ndClickListener != null) {
			mCurRight2ndView.setOnClickListener(mLeft2ndClickListener);
		}
		mRight2ndLp.addRule(RelativeLayout.LEFT_OF, mCurRightView.getId());
		addView(ib, mRight2ndLp);

	}

	public void setRight2ndBtnWithResourceId(int resId) {
		ImageButton ib = new ImageButton(mContext);
		ib.setImageResource(resId);
		ib.setDefaultColor(Color.WHITE);
		ib.setScaleType(ScaleType.CENTER_INSIDE);
		ib.setHightLightColor(getResources().getColor(R.color.button_white_hight_color));
		ib.setPadding(getResources().getDimensionPixelSize(R.dimen.title_padding_left), 0, getResources()
				.getDimensionPixelSize(R.dimen.title_padding_right),0);
		removeView(mCurRight2ndView);
		mCurRight2ndView = ib;
		if (mRight2ndClickListener != null) {
			mCurRight2ndView.setOnClickListener(mLeft2ndClickListener);
		}
		mRight2ndLp.addRule(RelativeLayout.LEFT_OF, mCurRightView.getId());
		addView(ib, mRight2ndLp);
	}

	/**
	 * 设置右边的view
	 * 
	 * @param view
	 */
	public void setRightView(View view) {

		removeView(mCurRightView);
		mCurRightView = view;
		addView(mCurRightView, mRightLp);
	}

	public void setRight2ndView(View right2ndView) {

		removeView(mCurRight2ndView);
		mCurRight2ndView = right2ndView;
		mRight2ndLp.addRule(RelativeLayout.LEFT_OF, mCurRightView.getId());
		addView(mCurRight2ndView, mRight2ndLp);

	}

	public void setLeftBtnClickListener(OnClickListener listener) {

		if (mCurLeftView != null)
			mCurLeftView.setOnClickListener(listener);

		mLeftClickListener = listener;
		LogM.log(this.getClass(), "setLeftClickListener");
	}

	public void setRightBtnClickListener(OnClickListener listener) {
		if(mCurRightView!=null){
			mCurRightView.setOnClickListener(listener);
		}
		mRightClickListener = listener;
	}

	public void setOnTitleClickListener(OnClickListener listener) {
		mTitleClickListener = listener;
		mCurTitleView.setOnClickListener(listener);
	}

	public void clearLeftAndRight() {
		LogM.log(this.getClass(), "清除菜单左右");
		removeView(mCurLeftView);
		removeView(mCurRightView);
		removeView(mCurLeft2ndView);
		removeView(mCurLeft2ndView);

	}

	public void setTitle(String str) {
		removeView(mCurTitleView);
		mCurTitleView = mTitleTv;
		addView(mCurTitleView, mTitleLp);
		mTitle = str;
		mTitleTv.setText(str);
	}

	public String getTitle() {
		return mTitle;
	}
	
	public void setTitleTextColor(int color){
		mTitleTextColor = color;
		mTitleTv.setTextColor(color);
	}
	
	public void setTitleTextSize(int unit,float size){
		mTitleTextSize = size;
		mTitleTv.setTextSize(unit, size);
	}
	
	public void setTitleView(View view) {
		removeView(mCurTitleView);
		mCurTitleView = view;
		if (mTitleClickListener != null) {
			mCurTitleView.setOnClickListener(mTitleClickListener);
		}
		addView(mCurTitleView, mTitleLp);
	}

	public void setRightText(String str) {

		if (mCurRightView != mRightBtn) {
			removeView(mCurRightView);
			mRightLp.width = LayoutParams.WRAP_CONTENT;
			addView(mRightBtn, mRightLp);
			mCurRightView = mRightBtn;
		}
		mRightText = str;
		mRightBtn.setText(mRightText);

	}

	public void setRightTextSize(int textSizePx) {
		rightTextSize = textSizePx;
		mRightBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
	}

	public void setRightTextColor(ColorStateList color) {
		mRightTextColor = color;
		mRightBtn.setTextColor(mRightTextColor);
	}

	public String getRightText() {
		return mRightText;
	}

	public View getRightView() {
		return mCurRightView;
	}

	public void setLeftBtnWithImageResourceId(int resId) {

		ImageButton ib = new ImageButton(mContext);
		ib.setImageResource(resId);
		ib.setDefaultColor(Color.WHITE);

		ib.setHightLightColor(getResources().getColor(R.color.button_white_hight_color));
		ib.setPadding(getResources().getDimensionPixelSize(R.dimen.title_padding_left), getResources()
				.getDimensionPixelSize(R.dimen.title_padding_left), 0,
				getResources().getDimensionPixelSize(R.dimen.title_padding_right));
		removeView(mCurLeftView);
		mCurLeftView = ib;
		if (mLeft2ndClickListener != null) {
			mCurLeftView.setOnClickListener(mLeft2ndClickListener);
		}

		addView(ib, mLeftLp);

	}

	public void setLeft2ndClickListener(OnClickListener left2ndClickListener) {
		this.mLeft2ndClickListener = left2ndClickListener;
		if (mCurLeft2ndView != null) {
			mCurLeft2ndView.setOnClickListener(left2ndClickListener);
		}
	}

	public void setRight2ndBtnClickListener(OnClickListener listener) {
		this.mRight2ndClickListener = listener;
		if (mCurRight2ndView != null) {
			mCurRight2ndView.setOnClickListener(listener);
		}
	}

	/**
	 * 在左边增加按钮如果左边没有则加到第一个否则加到第二个最多两个
	 * 
	 * @param resId
	 *            图片的资源id
	 * @param listener 此按钮的点击事件
	 */
	public void addLeftBtnWithImageResourceIdAndClickListener(int resId, OnClickListener listener) {
		// 如果第一个子view是当前的第一个btn的话说明已经有一个左边btn在titlebar了则需要将新的加到第二个上，否则加到第一个
		if (mCurLeftView.getParent() != null) {
			setLeft2ndBtnWithImageResourceId(resId);
			setLeft2ndClickListener(listener);
		} else {
			setLeftBtnWithImageResourceId(resId);
			setLeftBtnClickListener(listener);
		}
	}

	public void addLeftBtnWithImageUrlAndClickListener(String imageUrl, OnClickListener listener) {

		addLeftBtnWithImageUrlAndClickListener(imageUrl,null,listener);
	}
	
	public void addLeftBtnWithImageUrlAndClickListener(String imageUrl,Object tag,OnClickListener listener){
		if (mCurLeftView.getParent() != null) {
			setLeftBtnWithImageUrl(imageUrl);
			setLeftBtnClickListener(listener);
			if(tag!=null){
				mCurLeftView.setTag(tag);
			}
		} else {
			setLeft2ndBtnWithImageUrl(imageUrl);
			setLeft2ndClickListener(listener);
			if(tag!=null){
				mCurLeft2ndView.setTag(tag);
			}
		}
	}

	/**
	 * 类似addLeftBtnWithImageResourceIdAndClickListener
	 * 
	 * @param resId
	 * @param listener
	 */
	public void addRightBtnWithImageResourceIdAndClickListener(int resId, OnClickListener listener) {

		if (mCurRightView == null || mCurRightView.getParent() == null) {
			setRightBtnImageResourceId(resId);
			setRightBtnClickListener(listener);
		} else {
			setRight2ndBtnWithResourceId(resId);
			setRight2ndBtnClickListener(listener);

		}
	}

	public void addRightBtnWithImageUrlAndClickListener(String imageUrl, OnClickListener listener) {

		addRightBtnWithImageUrlAndClickListener(imageUrl, -1, listener);
	}

	public void addRightBtnWithImageUrlAndClickListener(String imageUrl, Object tag, OnClickListener listener) {

		if (mCurRightView == null || mCurRightView.getParent() == null) {
			setRightImageWithUrl(imageUrl);
			setRightBtnClickListener(listener);
			if (tag != null) {
				mCurRightView.setTag(tag);
			}
		} else {
			setRight2ndBtnWithUrl(imageUrl);
			setRight2ndBtnClickListener(listener);
			if (tag != null) {
				mCurRight2ndView.setTag(tag);
			}
		}
	}

	public void addRightBtnWithTextAndClickListener(String text, OnClickListener listener) {
		if (mCurRightView==null||mCurRightView.getParent() == null) {
			setRightBtnWithText(text);
			setRightBtnClickListener(listener);
		} else {
			setRight2ndBtnWithText(text);
			setRight2ndBtnClickListener(listener);
		}
	}

	/**
	 * 移除后加入的在左边的button(addLeftButton的反操作)
	 */
	public void removeLeftBtn() {

		if (mCurLeft2ndView != null && mCurLeft2ndView.getParent() != null) {
			removeView(mCurLeft2ndView);
		} else if (mCurLeftView != null && mCurLeftView.getParent() != null) {
			removeView(mCurLeftView);
		}
	}

	/**
	 * 清除左边第二个按钮
	 */
	public void removeLeft2ndView() {
		removeView(mCurLeft2ndView);
	}

	public void removeRight2ndView() {
		removeView(mCurRight2ndView);
	}
	public void removeRightView(){
		removeView(mCurRightView);
	}
	
	public void reset(){
		removeView(mCurLeft2ndView);
		removeView(mCurRight2ndView);
		removeView(mCurRightView);
		removeView(mCurTitleView);
		removeView(mCurLeft2ndView);
	}
	
	public void setUnderlineColor(int color){
		if(underLineView!=null){
			removeView(underLineView);
		}
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		underLineView = new View(mContext);
		underLineView.setBackgroundColor(color);
		addView(underLineView,lp);
	}
}
