package com.apppubs.d20.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.d20.bean.ServiceNo;
import com.apppubs.d20.widget.CircularImage;
import com.apppubs.d20.R;
import com.apppubs.d20.activity.ContainerActivity;
import com.apppubs.d20.model.BussinessCallbackCommon;

/**
 * 添加服务号 Copyright (c) heaven Inc. Original Author: zhangwen ChangeLog:
 * 2015年4月1日 by zhangwen create
 */
public class ServiceNoSubscribeFragment extends BaseFragment {

	private ListView mLv;
	private LinearLayout progressBsr;
	private String usetAttention;
	private SharedPreferences attentionServiceMsg;
	private List<ServiceNo> mServiceNoList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mRootView = mInflater.inflate(R.layout.frg_serviceno_subscribe, null);
		mLv = (ListView) mRootView.findViewById(R.id.serviceno_lv);
		// mLv = new ListView(mContext);
		// mRootView = mLv;
		progressBsr = (LinearLayout) mRootView.findViewById(R.id.serviceno_progress_ll);

		// mServiceNoList = SugarRecord.find(ServiceNo.class,
		// "allow_subscribe = ?", ServiceNo.ALLOW_SUBSCRIBE_YES+"");
		return mRootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		attentionServiceMsg = getActivity().getSharedPreferences(ServiceNoListOfMineFragment.USERATTECTIONSP,
				Context.MODE_PRIVATE);
		usetAttention = attentionServiceMsg.getString(ServiceNoListOfMineFragment.USERATTECTIONSPS, "");

		// MyAdapter adapter = new MyAdapter(mServiceNoList);
		// mLv.setAdapter(adapter);
		// mLv.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// Bundle b = new Bundle();
		// b.putString(ServiceNoInfoFragment.ARGS_STRING_SERVICE_NO_ID,
		// mServiceNoList.get(position).getId());
		// ContainerActivity.startActivity(mContext,
		// ServiceNoInfoFragment.class, b, "服务号");
		// }
		// });

		mMsgBussiness.getSubcribableServiceNoList(new BussinessCallbackCommon<List<ServiceNo>>() {

			@Override
			public void onException(int excepCode) {
			}

			@Override
			public void onDone(final List<ServiceNo> obj) {
				progressBsr.setVisibility(View.GONE);
				setVisibilityOfViewByResId(mRootView, R.id.serviceno_empty_tv, obj.isEmpty() ? View.VISIBLE : View.GONE);

				MyAdapter adapter = new MyAdapter(obj);
				mLv.setAdapter(adapter);
				mLv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Bundle b = new Bundle();
						b.putString(ServiceNoInfoFragment.ARGS_STRING_SERVICE_NO_ID, obj.get(position).getId());
						ContainerActivity.startActivity(mContext, ServiceNoInfoFragment.class, b, "服务号");
					}
				});
			}

		});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

	}

	private class MyAdapter extends BaseAdapter {
		List<ServiceNo> services = new ArrayList<ServiceNo>();

		public MyAdapter(List<ServiceNo> services) {
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
				convertView = getActivity().getLayoutInflater().inflate(R.layout.item_serviceno_xlv, null);
				holder = new ViewHolder();
				holder.des = (TextView) convertView.findViewById(R.id.item_service_des);
				holder.name = (TextView) convertView.findViewById(R.id.item_service_name);
				holder.flag = (ImageView) convertView.findViewById(R.id.item_service_flag);
				holder.pic = (CircularImage) convertView.findViewById(R.id.item_service_pic);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ServiceNo service = services.get(position);
			holder.des.setText(service.getDesc());
			holder.name.setText(service.getName());
			String picurl = service.getPicURL();
			if (!picurl.equals("") && picurl != null) {
				mImageLoader.displayImage(picurl, holder.pic);
			}
			if (usetAttention.indexOf(service.getId()) >= 0) {
				System.out.println("返回的标志。。。" + usetAttention + "...." + service.getId());
				holder.flag.setVisibility(View.VISIBLE);
			} else {
				holder.flag.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			private TextView des;
			private TextView name;
			private CircularImage pic;
			private ImageView flag;
		}

	}

}
