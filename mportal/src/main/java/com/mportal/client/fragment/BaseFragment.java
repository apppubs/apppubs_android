package com.mportal.client.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.mportal.client.activity.BaseActivity;
import com.mportal.client.business.MsgBussiness;
import com.mportal.client.business.SystemBussiness;
import com.mportal.client.business.UserBussiness;
import com.mportal.client.view.ConfirmDialog;
import com.mportal.client.view.TitleBar;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BaseFragment extends Fragment implements  KeyEvent.Callback{
	
	protected static int mDefaultColor;
	/**
	 * 根元素
	 */
	protected View mRootView;
	/**
	 * 布局渲染工具
	 */
	protected LayoutInflater mInflater;
	protected BaseActivity mHostActivity;
	protected Context mContext;
	protected ImageLoader mImageLoader;
	protected TitleBar mTitleBar;
	
	protected UserBussiness mUserBussiness;
	protected MsgBussiness mMsgBussiness;
	protected SystemBussiness mSystemBussiness;
	protected RequestQueue mRequestQueue;

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mHostActivity = (BaseActivity) activity;
		mSystemBussiness = SystemBussiness.getInstance(activity);
		mMsgBussiness = MsgBussiness.getInstance();
		mUserBussiness = UserBussiness.getInstance(activity);
		mContext = activity;
		mRequestQueue = mHostActivity.getRequestQueue();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		mDefaultColor = BaseActivity.mDefaultColor;
		return null;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		mImageLoader = mHostActivity.getImageLoader();
		mTitleBar = mHostActivity.getTitleBar();
		changeActivityTitleView(mTitleBar);
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			changeActivityTitleView(mTitleBar);
		}
	}
	/**
	 * 修改TitleView属性
	 * 当activity被创建后调用此方法可修改activity的titlebar
	 * 当fragment重现被展示时调用此方法刷新父activity的titlebar
	 */
	public void changeActivityTitleView(TitleBar titleBar){
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		return false;
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		return false;
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
		
		return false;
	}
	/**
	 * 填充某View下的某TextView
	 * @param rootView TextView的父view
	 * @param resId TextView 的id
	 * @param content TextView 文本内容
	 */
	protected void fillTextView(View rootView,int resId,String content){
		TextView tv = (TextView) rootView.findViewById(resId);
		tv.setText(content);
		
	}
	
	protected void fillImageView(View rootView,int resId,String uri) {
		ImageView iv = (ImageView) rootView.findViewById(resId);
		mImageLoader.displayImage(uri, iv);
	}
	
	protected void fillTextView(int resId,String content){
		fillTextView(mRootView, resId, content);
	}
	
	protected void fillImageView(int resId,String uri){
		fillImageView(mRootView, resId, uri);
		
	}
	
	protected void setVisibilityOfViewByResId(View rootView,int resId,int visibility){
		
		View v = rootView.findViewById(resId);
		v.setVisibility(visibility);
	}
	
	protected void registerOnClickListener(int resid,OnClickListener listener) {
		View view = mRootView.findViewById(resid);
		if(view!=null){
			view.setOnClickListener(listener);
		}
	}
	
	protected void startActivity(Class<?> clazz){
		Intent intent = new Intent(mHostActivity,clazz);
		startActivity(intent);
	}
	
}
