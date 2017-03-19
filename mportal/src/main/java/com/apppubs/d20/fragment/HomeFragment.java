package com.apppubs.d20.fragment;

import android.os.Bundle;
import android.view.View;

import com.apppubs.d20.activity.HomeBaseActivity;
import com.apppubs.d20.activity.ViewCourier;


/**
 * 
 * @author zhangwen 2014-12-22
 * 可置于主界面的Fragment
 *
 */
public class HomeFragment extends TitleMenuFragment{
	
	protected View mRootView;
	protected HomeBaseActivity mHostActivity;
	protected ViewCourier mViewController;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		mHostActivity  = (HomeBaseActivity) getActivity();
		mViewController = mHostActivity.getViewController();
		
	}
	
}
