package com.apppubs.ui.news;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.BaseAdapter;

import com.apppubs.bean.TNewsChannel;
import com.apppubs.bean.TNewsInfo;
import com.apppubs.model.NewsBiz;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.util.LogM;
import com.orm.SugarRecord;
/**
 * 频道fragment，展示频道信息列表
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年1月28日 by zhangwen create
 *
 */
public abstract class ChannelFragment extends BaseFragment {
	
	public static final String ARG_KEY = "channel_code"; 
	protected String mChannelCode;
	protected int mCurPage = 1;
	protected TNewsChannel mChannel;
	protected NewsBiz mNewsBiz;
	protected List<TNewsInfo> mNewsInfoList;
	protected BaseAdapter mAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle args = getArguments();
		if(args!=null){
			
			mChannelCode = args.getString(ARG_KEY);
			mChannel = SugarRecord.findByProperty(TNewsChannel.class, "CODE", mChannelCode);
		}
		super.onCreate(savedInstanceState);
		mNewsBiz = NewsBiz.getInstance(mContext);
	}
	
	public abstract void refresh();
	
	
	/**
	 * 根据newsinfo的类型不同打开不同的正文activity
	 * @param infoId
	 */
	protected void startInfoActivity(TNewsInfo newsInfo){
		String type = newsInfo.getType();
		if(TextUtils.isEmpty(type)){
			LogM.log(this.getClass(), "newsinfo 的类型是空！！！");
			return ;
		}
		if(type.equals(TNewsInfo.NEWS_TYPE_NORAML)||type.equals(TNewsInfo.NEWS_TYPE_VIDEO)||type.equals(TNewsInfo.NEWS_TYPE_AUDIO)||type.equals(TNewsInfo.NEWS_TYPE_PICTURE)){
			NewsInfoBaseActivity.startInfoActivity(mContext, newsInfo.getType(), mChannelCode,newsInfo.getId());
		}else if(type.equals(TNewsInfo.NEWS_TYPE_SPECIALS)||type.equals(TNewsInfo.NEWS_TYPE_URL)||type.equals(TNewsInfo.NEWS_TYPE_FILE)){
			NewsInfoBaseActivity.startInfoActivity(mContext, newsInfo.getType(), newsInfo.getUrl());
		}
	}
	
	protected void startNewsInfoActivity(String infoId,String channelCode) {
		

		Intent i = new Intent(mHostActivity,NewsInfoActivity.class);
		i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_ID,infoId);
		i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_CHANNELCODE,  channelCode);
		mHostActivity.startActivity(i);
	}
	
}
