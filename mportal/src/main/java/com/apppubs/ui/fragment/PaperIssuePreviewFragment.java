package com.apppubs.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.apppubs.ui.activity.PaperIssueActivity;
import com.apppubs.bean.PaperCatalog;
import com.apppubs.util.LogM;
import com.apppubs.ui.widget.DragScheduleBar;
import com.apppubs.d20.R;

/**
 *  报纸的版面预览视图
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年2月6日 by zhangwen create
 *
 */
public class PaperIssuePreviewFragment extends BaseFragment implements OnPageChangeListener{
	
	private ViewPager mVp;
	private PaperFragmentAdapter mAdapter;
	private PaperIssueActivity mHostActivity;
	private List<PaperCatalog> mCatalogList;
	private DragScheduleBar mDragScheduleBar;
	private LinearLayout mPreviewLl;
	private ImageView mPreviewIv;
	private TextView mPreviewTv;
	private TextView mCurPageTv;
	private TextView mTotalPageTv;
	private int mCurPage;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		LogM.log(this.getClass(), "onCreateView");
		View rootView = inflater.inflate(R.layout.frg_paper_issue_preview, null);
		mVp = (ViewPager) rootView.findViewById(R.id.paper_issue_vp);
		mDragScheduleBar = (DragScheduleBar) rootView.findViewById(R.id.paper_issue_dsb);
		mPreviewLl = (LinearLayout) rootView.findViewById(R.id.paper_issue_drag_preview_ll);
		mPreviewIv = (ImageView) rootView.findViewById(R.id.paper_issue_preview_iv);
		mPreviewTv = (TextView) rootView.findViewById(R.id.paper_issue_preview_tv);
		mCurPageTv = (TextView) rootView.findViewById(R.id.paper_issue_preview_curpage_tv);
		mTotalPageTv = (TextView) rootView.findViewById(R.id.paper_issue_preview_totalpage_tv);
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		LogM.log(this.getClass(), "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		mHostActivity = (PaperIssueActivity) getActivity();
		mCatalogList = mHostActivity.getCatalogList();
		mDragScheduleBar.setCount(mCatalogList.size());
		mTotalPageTv.setText("/"+mCatalogList.size());

		mDragScheduleBar.setOnPageSelectListener(new DragScheduleBar.OnGragedListener() {
			
			@Override
			public void onGraged(int pos,int marginLeft) {
				if(mCurPage!=pos){
					LogM.log(this.getClass(), "当前page："+pos);
					String pic = mCatalogList.get(pos).getPic();
					pic = pic.replace(".jpg", "_M.jpg");
					mImageLoader.displayImage(pic, mPreviewIv);
					mPreviewTv.setText(mCatalogList.get(pos).getName());
					mCurPageTv.setText((pos+1)+"");
					mCurPage = pos;
				}
				
				LayoutParams lp = (LayoutParams) mPreviewLl.getLayoutParams();
				lp.leftMargin = marginLeft;
				mPreviewLl.setLayoutParams(lp);
			}

			@Override
			public void onDisplayChanged(int pos,boolean show) {
				mPreviewLl.setVisibility(show?View.VISIBLE:View.GONE);
				
				if(!show){
					mVp.setCurrentItem(pos,false);
					
				}
			}
		});
		mAdapter = new PaperFragmentAdapter(getChildFragmentManager());
		List<PaperCatalogFragment> list = new ArrayList<PaperCatalogFragment>();
		for(PaperCatalog pc:mCatalogList){
			PaperCatalogFragment pcf = new PaperCatalogFragment();
			Bundle args = new Bundle();
			args.putString(PaperCatalogFragment.ARG_NAME_CATALOG_ID, pc.getId());
			pcf.setArguments(args);
			list.add(pcf);
		}
		mAdapter.setData(list);
		mVp.setAdapter(mAdapter);
		mVp.setOnPageChangeListener(this);
		mTitleBar.setTitle(mCatalogList.get(mCurPage).getName());
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			mTitleBar.setTitle(mCatalogList.get(mCurPage).getName());
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		LogM.log(this.getClass(), "onResume");
	}
	public class PaperFragmentAdapter extends FragmentPagerAdapter{
		private List<PaperCatalogFragment> mList ;
		public PaperFragmentAdapter(FragmentManager fm) {
			super(fm);
		}
		

		@Override
		public Fragment getItem(int pos) {
			return mList.get(pos);
		}

		@Override
		public int getCount() {
			return mList.size();
		}
		@Override
		public int getItemPosition(Object object) {
			return  POSITION_NONE;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
			Log.v("PaperFragmentAdapter","fragment adapter destory item :"+position);
		}
		public void setData(List<PaperCatalogFragment> list){
			this.mList = list;
		}
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int pos) {
		mDragScheduleBar.setCurIndex(pos);
		mHostActivity.getTitleBar().setTitle(mCatalogList.get(pos).getName());
	}
	
}
