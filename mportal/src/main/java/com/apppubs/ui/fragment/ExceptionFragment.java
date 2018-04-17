package com.apppubs.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ExceptionFragment extends BaseFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView tv = new TextView(container.getContext());
		tv.setText("应用类型不支持或者应用配置错误");
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
		tv.setTextColor(Color.RED);
		tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
		tv.setGravity(Gravity.CENTER);
		return tv;
	}
}
