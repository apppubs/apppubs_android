package com.mportal.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mportal.client.widget.PdfViewWithHotArea;

public class PaperPageFragment extends BaseFragment{

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		FrameLayout fl = new FrameLayout(mContext);
		PdfViewWithHotArea pdfView = new PdfViewWithHotArea(mContext);
		fl.addView(fl);
		
		mRootView = fl;
		
		return mRootView;
	}
}
