package com.apppubs.ui.news;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.apppubs.bean.TNewsChannel;
import com.apppubs.model.NewsBiz;
import com.apppubs.ui.activity.AddChannelActivity;
import com.apppubs.ui.activity.ContainerActivity;
import com.apppubs.ui.activity.SortChannelBActivity;
import com.apppubs.bean.THeadPic;
import com.apppubs.util.LogM;
import com.apppubs.ui.widget.SlidePicView;
import com.apppubs.d20.R;
import com.apppubs.ui.widget.HeaderGridView;
import com.orm.SugarRecord;
/**
 * 方格类型的频道列表
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年2月5日 by zhangwen create
 *
 */
public class ChannelsSquareFragment extends ChannelsFragment implements OnClickListener{
	
	private static final int REQUEST_CODE_SORT = 1;
	private static final int REQUEST_CODE_ADD = 2;
	
	public static  final String  ARGS_CHANNEL_TYPE_ID = "channel_type_id";
	
	private HeaderGridView mChannelsGv;
	private SlidePicView mSlidePicView;
	private MyAdapter mAdapter;
	private List<THeadPic> mPicList;//推广图
	private PopupWindow mEditPopupWindow;
	private View mEditLl,mDelLl;
	private NewsBiz mNewsBiz;
	
	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.frg_channels_bottom, null);

		init(inflater);
		return mRootView;
	}

	private void init(LayoutInflater inflater){
		
		mChannelsGv = (HeaderGridView) mRootView.findViewById(R.id.channels_hgl);
		mPicList = SugarRecord.find(THeadPic.class, "CHANNEL_TYPE_ID = ?" ,new String[]{mChannelTypeId+""},null,null,null);
		mNewsBiz = NewsBiz.getInstance(mContext);
		
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		fill();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private View curLongClickView;
	private int curLongClickedPos;
	private void fill(){
		if(mPicList!=null&&mPicList.size()!=0){
			
			mSlidePicView = new SlidePicView(mHostActivity);
			mSlidePicView.setBackgroundColor(0xffffffff);
			final List<SlidePicView.SlidePicItem> list = new ArrayList<SlidePicView.SlidePicItem>();
			for(THeadPic hp:mPicList){
				SlidePicView.SlidePicItem sp = new SlidePicView.SlidePicItem();
				sp.picURL = hp.getPicURL();
				sp.title = hp.getTopic();
				sp.infoId = String.valueOf(hp.getInfoid());
				list.add(sp);
			}
			mSlidePicView.setData(list);
			mChannelsGv.addHeaderView(mSlidePicView);
			mSlidePicView.setOnItemClickListener(new SlidePicView.OnItemClickListener() {
				
				@Override
				public void onClick(int pos,SlidePicView.SlidePicItem item) {
					Intent i = new Intent(mHostActivity,NewsInfoActivity.class);
						
					i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_ID,list.get(pos).infoId);
					i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_CHANNELCODE,  list.get(pos).channelCode);
					mHostActivity.startActivity(i);
				}
			});
			
		}
		
		
		mAdapter = new MyAdapter();
		mChannelsGv.setAdapter(mAdapter);
		mChannelsGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TNewsChannel nc = (TNewsChannel)mChannelsGv.getAdapter().getItem(position);
				if(nc==null){
					Intent i = new Intent(mHostActivity,AddChannelActivity.class);
					i.putExtra(AddChannelActivity.EXTRA_STRING_NAME_CHANNELTYPE, mChannelTypeId+"");
					startActivityForResult(i, REQUEST_CODE_ADD);
				}else{
					
					ChannelFragment cfrg = ChannelFragmentFactory.getChannelFragment(nc.getShowType());
					Bundle args = new Bundle();
					args.putString(ChannelFragment.ARG_KEY, nc.getCode());;
					ContainerActivity.startContainerActivity(mHostActivity, cfrg.getClass(),args,nc.getName());
					
				}
			}
		});
		
		if(mAllowConfig){
			
			mChannelsGv.setOnItemLongClickListener(new OnItemLongClickListener() {
				
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view,
						int position, long id) {
					int pos = 0;
					if((pos=(int) mChannelsGv.getAdapter().getItemId(position))==mChannelSelectedList.size()){
						return true;
					}
					showModifyChannelPw();
					curLongClickView = view.findViewById(R.id.channel_gv_layer_v);
					curLongClickView.setBackgroundColor(Color.parseColor("#40DBDBDB"));
					curLongClickedPos = pos;
					
					return true;
				}
			});
		}
	
		
	}

	@Override
	public void setSelectedChannels(List<TNewsChannel> channels) {

	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(mAllowConfig){
				
				return mChannelSelectedList.size()+1;
			}
			return mChannelSelectedList.size();
		}

		@Override
		public Object getItem(int position) {
			if(position>=mChannelSelectedList.size()){
				return null;
			}
			return mChannelSelectedList.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView =  LayoutInflater.from(mHostActivity).inflate(R.layout.item_channels_b, null);
			
			if(position%3==2){
				convertView.setPadding(0, 0, 0, 1);
			}else{
				convertView.setPadding(0, 0, 1, 1);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.channels_gv_tv);
			ImageView iv = (ImageView) convertView.findViewById(R.id.channels_gv_iv);
			if(position==mChannelSelectedList.size()){
				iv.setImageResource(R.drawable.plus);
				tv.setText("添加");
				return convertView;
			}
			TNewsChannel nc = mChannelSelectedList.get(position);
			tv.setText(nc.getName());
			LogM.log(this.getClass(), "加载频道:"+nc.getPic());
			mImageLoader.displayImage(nc.getPic(), iv);
			return convertView;
		}
		
	}
	
	/**
	 * 弹出修改某栏目的底部菜单
	 */
	public void showModifyChannelPw(){
		if(mEditPopupWindow==null){
			
			float height = mHostActivity.getResources().getDimension(R.dimen.channels_bm_pop_height);
			View channelChangePop = LayoutInflater.from(mHostActivity).inflate(R.layout.pop_channels_edit, null);
			mEditLl = channelChangePop.findViewById(R.id.channels_edit_order_ll);
			mDelLl = channelChangePop.findViewById(R.id.channels_edit_del_ll);
			mEditLl.setOnClickListener(this);
			mDelLl.setOnClickListener(this);
			mEditPopupWindow = new PopupWindow(channelChangePop,
					ViewGroup.LayoutParams.MATCH_PARENT, (int)height);
			mEditPopupWindow.setFocusable(true);
			mEditPopupWindow.setOutsideTouchable(true);
			mEditPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mEditPopupWindow.setAnimationStyle(R.style.popwin_channel_anim_style);
			mEditPopupWindow.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss() {
					curLongClickView.setBackgroundColor(Color.TRANSPARENT);
				}
			});
		}
		
		mEditPopupWindow.showAtLocation(mChannelsGv, Gravity.BOTTOM, 0, 0);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.channels_edit_order_ll:
			Intent i = new Intent(mHostActivity,SortChannelBActivity.class);
			i.putExtra(SortChannelBActivity.EXTRA_STRING_NAME_CHANNELTYPE, mChannelTypeId+"");
			startActivityForResult(i, REQUEST_CODE_SORT);
			break;
		case R.id.channels_edit_del_ll:
			delChannel();
			break;

		default:
			break;
		}
	}

	private void delChannel() {
		TNewsChannel nc = mChannelSelectedList.get(curLongClickedPos);
		mChannelSelectedList.remove(curLongClickedPos);
		mAdapter.notifyDataSetChanged();
		mEditPopupWindow.dismiss();
		mNewsBiz.removeChannel(mChannelTypeId+"", nc.getCode());
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogM.log(this.getClass(), "返回值");
		//如果顺序被更改则刷新频道,隐藏popupwindow
		if(requestCode==REQUEST_CODE_SORT&&resultCode==Activity.RESULT_OK&&data.getBooleanExtra(SortChannelBActivity.EXTRA_BOOLEAN_NAME, false)){
			refreshSelectedList();
			mAdapter.notifyDataSetChanged();
			mEditPopupWindow.dismiss();
		}
		
	}
	
}
