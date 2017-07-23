package com.apppubs.d20.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.apppubs.d20.R;

/**
 * Created by zhangwen on 2017/7/23.
 * 拥有默认titlebar的fragment
 */

public class TitleBarFragment extends BaseFragment {

	private int mContentResId;
	public TitleBarFragment(int resId){
		super();
		mContentResId = resId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mRootView = inflater.inflate(R.layout.frg_base,container);
		return mRootView;
	}


}
