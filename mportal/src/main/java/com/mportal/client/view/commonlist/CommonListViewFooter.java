package com.mportal.client.view.commonlist;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mportal.client.R;

public class CommonListViewFooter extends LinearLayout {
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;
	public final static int STATE_ALL_LOADED = 3;
	public final static int STATE_FAIL = 4;
	private int curState;
	private Context mContext;

	private View mContentView;
	private ProgressBar mProgressBar;
	private TextView mHintView;

	public CommonListViewFooter(Context context) {
		super(context);
		initView(context);
	}

	public CommonListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	public void reset(){
		curState = STATE_NORMAL;
		mHintView.setText("上拉加载更多");
		mProgressBar.setVisibility(View.INVISIBLE);
	}
	public void setState(int state) {
		if(curState==STATE_ALL_LOADED){
			return;
		}
		curState = state;
		mProgressBar.setVisibility(View.INVISIBLE);
		if (state == STATE_READY) {
			mHintView.setText("松开加载...");
		} else if (state == STATE_LOADING) {
			mProgressBar.setVisibility(View.VISIBLE);
			mHintView.setText("加载中...");
		} else if(state== STATE_NORMAL){
			mHintView.setText("上拉加载更多");
		}else{
			mHintView.setText("没有更多");
		}
	}
	public int getState(){
		return curState;
	}
	public void setBottomMargin(int height) {
		if (height < 0)
			return;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.setMargins(0, 0, 0, height);
		mContentView.setLayoutParams(lp);
	}

	public int getBottomMargin() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		return lp.bottomMargin;
	}

	/**
	 * normal status
	 */
	public void normal() {
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}

	/**
	 * loading status
	 */
	public void loading() {
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * hide footer when disable pull load more
	 */
	public void hide() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.height = 0;
		mContentView.setLayoutParams(lp);
	}

	/**
	 * show footer
	 */
	public void show() {
		
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView
				.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
	}

	private void initView(Context context) {
		mContext = context;
		RelativeLayout moreView = (RelativeLayout) LayoutInflater.from(mContext)
				.inflate(R.layout.commonlistview_footer, null);
		addView(moreView,new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		mContentView = moreView.findViewById(R.id.commonlv_footer_content);
		mProgressBar = (ProgressBar) moreView.findViewById(R.id.commonlv_footer_progressbar);
		mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#6b6b6b"),
                android.graphics.PorterDuff.Mode.MULTIPLY);
//		mProgressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#1874CD"),
//				android.graphics.PorterDuff.Mode.MULTIPLY);
		mHintView = (TextView) moreView
				.findViewById(R.id.commonlv_footer_hint_textview);
	}

}
