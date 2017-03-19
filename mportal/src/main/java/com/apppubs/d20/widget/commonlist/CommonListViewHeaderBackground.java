package com.apppubs.d20.widget.commonlist;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apppubs.d20.widget.CircularImage;
import com.apppubs.d20.widget.ProgressPie;
import com.apppubs.d20.R;


public class CommonListViewHeaderBackground extends LinearLayout {
	private RelativeLayout mContainer;
	private TextView mHintTextView;
	private ProgressPie mProgressPie;
	private CircularImage mIcon;
	private int mState = STATE_NORMAL;

	
	private Animation mRotateAnim;
	private long mStartRTime;//开始旋转时间
	private long mEndRTime;//结束旋转时间

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;
	public final static int STATE_DONE = 3;//加载完成
	public final static int STATE_FAIL = 4;//刷新失败

	public CommonListViewHeaderBackground(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CommonListViewHeaderBackground(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_HORIZONTAL);
		mContainer = (RelativeLayout) LayoutInflater.from(context).inflate(
				R.layout.commonlistview_header,null);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.commonlistview_header_h));
		addView(mContainer,lp);

		mHintTextView = (TextView) findViewById(R.id.clv_header_hint_textview);

		mRotateAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_icon);
		
		mProgressPie = (ProgressPie) findViewById(R.id.clv_header_pp);
		mIcon = (CircularImage) findViewById(R.id.clv_header_icon);
	}
	public void setProgressPieProgress(float progress){
		mProgressPie.setProgress(progress);
	}
	public void setState(int state) {
		if (state == mState)
			return;

		if (state == STATE_REFRESHING) { // 显示进度
			mIcon.setVisibility(View.VISIBLE);
		} 
		switch (state) {
		case STATE_NORMAL:
			mHintTextView.setText("下拉刷新");
			mProgressPie.setVisibility(View.VISIBLE);
			mIcon.setVisibility(View.GONE);
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				mIcon.setVisibility(View.VISIBLE);
				mProgressPie.setVisibility(View.GONE);
				mHintTextView.setText("释放刷新");
			}
			break;
		case STATE_REFRESHING:
			mHintTextView.setText("正在刷新");
			mIcon.startAnimation(mRotateAnim);
			mStartRTime = SystemClock.uptimeMillis();
			break;
		case STATE_DONE:
			mHintTextView.setText("刷新成功");
			mEndRTime = SystemClock.uptimeMillis();
			RotateAnimation ra = new RotateAnimation(((mEndRTime-mStartRTime)/1000f%1)*360, 360, Animation.RELATIVE_TO_SELF,
					0.5f,Animation.RELATIVE_TO_SELF,0.5f);
			ra.setDuration(CommonListView.SCROLL_DURATION/2);
			mIcon.clearAnimation();
			mIcon.startAnimation(ra);
			
			break;
		default:
		}

		mState = state;
	}
}