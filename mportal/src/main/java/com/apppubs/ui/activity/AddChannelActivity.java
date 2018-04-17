package com.apppubs.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.apppubs.ui.fragment.ChannelFragment;
import com.apppubs.util.LogM;
import com.apppubs.d20.R;
import com.apppubs.bean.NewsChannel;
import com.apppubs.ui.fragment.ChannelFragmentFactory;
import com.apppubs.ui.widget.commonlist.CommonListView;
import com.orm.SugarRecord;
/**
 * 增加 频道
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年2月2日 by zhangwen create
 *
 */
public class AddChannelActivity extends BaseActivity {
	
	public static final String EXTRA_STRING_NAME_CHANNELTYPE = "channel_type";
	
	private CommonListView mLv;
	private List<NewsChannel> mList;
	private String mChannelTypeId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_add_channel_b);
		mLv = (CommonListView) findViewById(R.id.add_channel_ll);
		mChannelTypeId = getIntent().getStringExtra(EXTRA_STRING_NAME_CHANNELTYPE);
		mList = SugarRecord.find(NewsChannel.class,  "TYPE_ID=?", new String[]{mChannelTypeId+""}, null, null, null);
		LogM.log(this.getClass(), "onCreate: mList size:"+mList.size());
		mLv.setAdapter(new MyAdapter());
		mLv.setPullLoadEnable(false);
		mLv.setPullRefreshEnable(false);
        mLv.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			/*	Intent i = new Intent(AddChannelActivity.this,ChannelActivity.class);
				i.putExtra(ChannelActivity.ARGUMENT_SERIALIZABLE_NAME_CHANNEL, (NewsChannel)parent.getAdapter().getItem(position));
				startActivity(i);*/
				NewsChannel nc = (NewsChannel)parent.getAdapter().getItem(position);
				ChannelFragment cfrg = ChannelFragmentFactory.getChannelFragment(nc.getShowType());
				Bundle args = new Bundle();
				args.putString(ChannelFragment.ARG_KEY, nc.getCode());;
				ContainerActivity.startActivity(AddChannelActivity.this, cfrg.getClass(),args,nc.getName());
			}
		});
        
        setTitle("添加频道");
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if(convertView==null){
				holder = new Holder();
				convertView = LayoutInflater.from(AddChannelActivity.this).inflate(R.layout.item_channel_add_b, null);
				holder.tv = (TextView) convertView.findViewById(R.id.channel_add_tv);
				holder.iv = (ImageView) convertView.findViewById(R.id.channel_add_iv);
				holder.tb = (ToggleButton) convertView.findViewById(R.id.channel_add_tgb);
				convertView.setTag(holder);
			}
			else{
				holder = (Holder) convertView.getTag();
			}
			NewsChannel nc = mList.get(position);
			holder.tv.setText(nc.getName());
			mImageLoader.displayImage(nc.getPic(), holder.iv);
			if(nc.getDisplayOrder()!=0){
				holder.tb.setChecked(true);
			}else{
				holder.tb.setChecked(false);
			}
			holder.tb.setTag(position);
			holder.tb.setOnCheckedChangeListener(onCheckedChangeListener);
			return convertView;
		}
		
		private class Holder{
			TextView tv;
			ImageView iv;
			ToggleButton tb;
		}
		
		
	}
	
	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			int pos = Integer.parseInt(buttonView.getTag().toString());
			LogM.log(this.getClass(), "onCheckedChanged :"+isChecked+" pos:"+pos);
			NewsChannel nc =  mList.get(pos);
			
			if(isChecked){
				mNewsBussiness.addChannel(mChannelTypeId, nc.getCode());
				nc.setDisplayOrder(1);
			}else{
				mNewsBussiness.removeChannel(mChannelTypeId, nc.getCode());
				nc.setDisplayOrder(0);
			}
		}
	};
	
}
