package com.apppubs.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.apppubs.ui.widget.PdfViewWithHotArea;

public class PaperPageFragment extends BaseFragment{

	
	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FrameLayout fl = new FrameLayout(mContext);
		PdfViewWithHotArea pdfView = new PdfViewWithHotArea(mContext);
		fl.addView(fl);

		mRootView = fl;

		return mRootView;
	}
}
