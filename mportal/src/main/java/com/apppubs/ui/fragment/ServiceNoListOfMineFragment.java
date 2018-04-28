package com.apppubs.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.bean.TServiceNo;
import com.apppubs.AppContext;
import com.apppubs.constant.APError;
import com.apppubs.d20.R;
import com.apppubs.model.IAPCallback;
import com.apppubs.ui.activity.ContainerActivity;
import com.apppubs.ui.widget.CircularImage;
import com.apppubs.ui.widget.TitleBar;

/**
 * 我的服务号列表
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年4月3日 by zhangwen create
 * 
 */
public class ServiceNoListOfMineFragment extends BaseFragment {

	private ListView mLv;
	private LinearLayout progressBsr;
	private TextView mEmptyTv;
	public static final String USERATTECTIONSP = "userattectionlist";
	public static final String USERATTECTIONSPS = "userattectionliststring";
	private SharedPreferences attentionServiceMsg;


	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = mInflater.inflate(R.layout.frg_serviceno_of_mine, null);
		mLv = (ListView) mRootView.findViewById(R.id.serviceno_lv);
		progressBsr = (LinearLayout) mRootView.findViewById(R.id.serviceno_progress_ll);
		mEmptyTv = (TextView) mRootView.findViewById(R.id.serviceno_empty_tv);
		return mRootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		attentionServiceMsg = getActivity().getSharedPreferences(USERATTECTIONSP, Context.MODE_PRIVATE);
		mMsgBussiness.getUserServiceNoList(AppContext.getInstance(mContext).getCurrentUser().getUsername(),
				new IAPCallback<List<TServiceNo>>() {

					public void onException(APError excepCode) {

					}

					@Override
					public void onDone(final List<TServiceNo> obj) {
						// 获得给用户订阅的号；
						progressBsr.setVisibility(View.GONE);
						if(obj.isEmpty()){
							mEmptyTv.setVisibility(View.VISIBLE);
						}else{
							mEmptyTv.setVisibility(View.GONE);
						}
						fillListView(obj);
					}

					private void fillListView(final List<TServiceNo> obj) {
						MyAdapter adapter = new MyAdapter(obj);
						mLv.setAdapter(adapter);
						mLv.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								Bundle b = new Bundle();
								b.putString(ServiceNoInfoFragment.ARGS_STRING_SERVICE_NO_ID, obj.get(position).getId());
								ContainerActivity.startActivity(mContext, ServiceNoInfoListFragement.class, b,
										"服务号历史消息");
							}
						});

						Editor et = attentionServiceMsg.edit();
						String userattention = "";
						if (obj.size() > 0) {
							for (int i = 0; i < obj.size(); i++) {
								userattention = userattention + obj.get(i).getId() + ",";
							}
						}
						et.putString(USERATTECTIONSPS, userattention);
						et.commit();
					}
				});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
	}

	private class MyAdapter extends BaseAdapter {
		List<TServiceNo> services = new ArrayList<TServiceNo>();

		public MyAdapter(List<TServiceNo> services) {
			this.services = services;
		}

		@Override
		public int getCount() {
			return services.size();
		}

		@Override
		public Object getItem(int position) {
			return services.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.item_myattentionservice_xlv, null);
				holder = new ViewHolder();
				holder.des = (TextView) convertView.findViewById(R.id.item_myservice_des);
				holder.name = (TextView) convertView.findViewById(R.id.item_myservice_name);
				holder.pic = (CircularImage) convertView.findViewById(R.id.item_myservice_pic);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TServiceNo service = services.get(position);
			holder.des.setText(service.getDesc());
			holder.name.setText(service.getName());
			String picurl = service.getPicURL();
			if (!picurl.equals("") && picurl != null) {
				mImageLoader.displayImage(picurl, holder.pic);
			}
			return convertView;
		}

		private class ViewHolder {
			private TextView des;
			private TextView name;
			private CircularImage pic;
		}

	}

	@Override
	public void changeActivityTitleView(TitleBar titleBar) {

		super.changeActivityTitleView(titleBar);
		titleBar.setRightText("添加");
		titleBar.setRightBtnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ContainerActivity.startActivity(mContext, ServiceNoSubscribeFragment.class, null, "服务号");
			}
		});

	}

}
