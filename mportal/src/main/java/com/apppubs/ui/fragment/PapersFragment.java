package com.apppubs.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.apppubs.model.PaperBiz;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.bean.TPaper;
import com.apppubs.util.LogM;
import com.apppubs.ui.widget.SegmentedGroup;
import com.apppubs.ui.widget.TitleBar;
import com.apppubs.d20.R;

public class PapersFragment extends TitleMenuFragment implements OnPageChangeListener {
	// 报纸列表
	private List<TPaper> mPaperList;
	// fragment列表
	private List<PaperIssueListFragment> mPaperFragmentList;
	private PaperBiz mPaperBiz;
	private SegmentedGroup mSg;
	
	private Fragment mCurFrg;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mRootView = new FrameLayout(mContext);
		mRootView.setId(R.id.fragment_container);
		init();
		return mRootView;
	}


	private void init() {
		mPaperBiz = PaperBiz.getInstance(getContext());
		mPaperList = mPaperBiz.getPaperList();
		mPaperFragmentList = new ArrayList<PaperIssueListFragment>();
		for (TPaper p : mPaperList) {
			LogM.log(this.getClass(), "将：" + p.getPaperCode() + "增加fragment");
			// mScrollTabs.addTab(p.getName());
			PaperIssueListFragment paperFrg = new PaperIssueListFragment();
			Bundle args = new Bundle();
			args.putString(PaperIssueListFragment.ARG_PAPERCODE, p.getPaperCode());
			paperFrg.setArguments(args);
			mPaperFragmentList.add(paperFrg);
		}
		 changeContent(mPaperFragmentList.get(0));
	}

	@Override
	public void changeActivityTitleView(TitleBar titleBar) {
		super.changeActivityTitleView(titleBar);
		mPaperBiz = PaperBiz.getInstance(getContext());
		mPaperList = mPaperBiz.getPaperList();
		int size = mPaperList.size();// 报纸的份数
		if (size == 1) {
			titleBar.setTitle(mPaperList.get(0).getName());
		} else if (size == 2 || size == 3) {
			View titleView = LayoutInflater.from(titleBar.getContext()).inflate(R.layout.paper_segment_btn, null);
			mSg = (SegmentedGroup) titleView.findViewById(R.id.segmented_paper);
			LayoutInflater inflater = (LayoutInflater) titleBar.getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			for (int i = 0; i < size; i++) {
				RadioButton rdbtn = (RadioButton) inflater.inflate(R.layout.radiobutton, null);
				RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				rdbtn.setPadding(5, 0, 5, 0);
				rdbtn.setLayoutParams(lp);
				rdbtn.setId(i + 10);
				rdbtn.setText(mPaperList.get(i).getName());
				if (i == 0) {
					rdbtn.setChecked(true);
				}
				mSg.addView(rdbtn);
			}
			mSg.setTintColor(Color.WHITE, BaseActivity.mDefaultColor);
			mSg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup arg0, int checkedId) {
					Fragment fg = null;
					switch (checkedId) {
					case 10:
						fg = mPaperFragmentList.get(0);

						break;
					case 11:
						fg = mPaperFragmentList.get(1);

						break;
					case 12:
						if (mPaperFragmentList.size() > 2) {
							fg = mPaperFragmentList.get(0);
						}
						break;
					}
					
					changeContent(fg);
				}
			});
			titleBar.setTitleView(titleView);
			
		}

	}


	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int position, float offset, int offsetPixel) {
		// mScrollTabs.onPageScrolled(position, offset);
	}

	@Override
	public void onPageSelected(int pos) {
	}

	
	
	protected void changeContent(Fragment fragment) {
		
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
		if (fragments == null || !fragments.contains(fragment)) {
			transaction.add(R.id.fragment_container, fragment);
		}
		if (mCurFrg != null){
			transaction.hide(mCurFrg);
		}
		transaction.show(fragment);
		mCurFrg = fragment;
		transaction.commit();
	}

}
