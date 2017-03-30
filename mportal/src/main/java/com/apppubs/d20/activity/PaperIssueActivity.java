package com.apppubs.d20.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.apppubs.d20.bean.PaperCatalog;
import com.apppubs.d20.bean.PaperIssue;
import com.apppubs.d20.widget.TitleBar;
import com.apppubs.d20.R;
import com.apppubs.d20.model.BussinessCallbackCommon;
import com.apppubs.d20.fragment.PaperInfoListFragment;
import com.apppubs.d20.fragment.PaperIssuePreviewFragment;

/**
 * 某一期的activity
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年2月6日 by zhangwen create
 * 
 */
public class PaperIssueActivity extends BaseActivity {

	public static final String EXTRA_NAME_ISSUE_ID = "issue_id";
	private String mIssueId;
	private PaperIssue mPaperIssue;
	private LinearLayout mProgressLl;
	private int mCurMode;// 当前显示模式 0==列表模式 1==预览模式
	private Map<Integer,Fragment> mFragmentMap = new HashMap<Integer, Fragment>();
	private Fragment mCuFragment;
	private Future<?> mGetPaperFuture;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.act_paper_issue);
		init();
		fill();
	}

	private void fill() {
		mGetPaperFuture = mPaperBussiness.getPaperIssueInfo(mIssueId, new BussinessCallbackCommon<PaperIssue>() {

			@Override
			public void onException(int excepCode) {
				Toast.makeText(PaperIssueActivity.this, "读取报纸信息错误", Toast.LENGTH_SHORT).show();
				mProgressLl.setVisibility(View.GONE);
			}

			@Override
			public void onDone(PaperIssue obj) {
				mPaperIssue = obj;
				displayIssue(mCurMode);
				mProgressLl.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
		mGetPaperFuture.cancel(true);
	}
	private void init() {
		mProgressLl = (LinearLayout) findViewById(R.id.paper_issue_progress);
		mIssueId = getIntent().getStringExtra(EXTRA_NAME_ISSUE_ID);
		mTitleBar.setRightBtnImageResourceId(R.drawable.paper_info_preview);
		mTitleBar.setRightBtnClickListener(this);
		

	}

	public List<PaperCatalog> getCatalogList() {

		return mPaperIssue.getCatalogList();
	}

	/**
	 * 显示模式切换 0==列表模式 1==预览模式
	 * 
	 * @param mode
	 */
	private void displayIssue(int mode) {
		
		Fragment frg = mFragmentMap.get(mode);
		if(frg==null){
			if (mode == 0) {
				frg = new PaperInfoListFragment();
			} else {
				frg = new PaperIssuePreviewFragment();
			}
			mFragmentMap.put(mode, frg);
		}
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if(!fragments.contains(frg)){
			transaction.add(R.id.paper_issue_frg,frg);
		}
		
		if(mCuFragment!=null){
			transaction.hide(mCuFragment);
			transaction.show(frg);
		}
		mCuFragment = frg;
		transaction.commitAllowingStateLoss();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == TitleBar.ID_RIGHT_BTN&&mPaperIssue!=null) {
			mCurMode = mCurMode ^ 1;
			if(mCurMode==1){
				mTitleBar.setRightBtnImageResourceId(R.drawable.paper_info_list);
			}else{
				mTitleBar.setRightBtnImageResourceId(R.drawable.paper_info_preview);
			}
			displayIssue(mCurMode);
		}
	}

}
