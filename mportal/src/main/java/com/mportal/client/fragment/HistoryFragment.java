package com.mportal.client.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.activity.NewsInfoActivity;
import com.mportal.client.bean.ServiceNOInfo;
import com.mportal.client.business.BussinessCallbackCommon;
import com.mportal.client.util.StringUtils;
import com.mportal.client.util.SystemUtils;
import com.mportal.client.widget.ProgressHUD;
import com.mportal.client.widget.commonlist.CommonListView;
import com.mportal.client.widget.commonlist.CommonListViewListener;

public class HistoryFragment extends BaseFragment {
	/**
	 * 历史推送
	 */
	private CommonListView mLv;
	private HistoryAdapter adapter;
	private List<ServiceNOInfo> mInfos;
	private TextView mEmptyText;
	private Date mStandardDateTime;
	private SimpleDateFormat mSimpleDateFormat;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		mRootView = inflater.inflate(R.layout.frg_history, null);
		init();
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getPage(0);
		ProgressHUD.show(mHostActivity, null, true, false, null);
	}

	private void getPage(final int i) {
		
		if (SystemUtils.canConnectNet(mContext)) {

			mSystemBussiness.getStandardDataTime(new BussinessCallbackCommon<Date>() {

				@Override
				public void onException(int excepCode) {

				}

				@Override
				public void onDone(Date obj) {
					mStandardDateTime = obj;

					mMsgBussiness.getAloneServiceList(mAppContext.getApp().getDefaultServiceNoId(),
							new BussinessCallbackCommon<List<ServiceNOInfo>>() {

								@Override
								public void onDone(List<ServiceNOInfo> obj) {

									mInfos = obj;
									adapter = new HistoryAdapter();
									mLv.setAdapter(adapter);
									mLv.stopRefresh();
									
									ProgressHUD.dismissProgressHUDInThisContext(mHostActivity);
								}

								@Override
								public void onException(int excepCode) {
									ProgressHUD.dismissProgressHUDInThisContext(mHostActivity);
								}
							});
				}
			});
		} else {
			Toast.makeText(mContext, getResources().getString(R.string.network_faile), Toast.LENGTH_LONG).show();
		}
	}

	private void init() {
		mLv = (CommonListView) mRootView.findViewById(R.id.history_xlv);
		mLv.setPullRefreshEnable(true);
		mLv.setPullLoadEnable(false);

		mLv.setCommonListViewListener(new CommonListViewListener() {

			@Override
			public void onRefresh() {
				getPage(0);
			}

			@Override
			public void onLoadMore() {
			}
		});

		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posion, long arg3) {
				
				Intent intent = new Intent(mContext, NewsInfoActivity.class);
				String[] arr = mInfos.get(posion-1).getId().split("\\|");
				Bundle extras = new Bundle();
				extras.putString(NewsInfoActivity.EXTRA_STRING_NAME_ID, arr[0]);
				extras.putString(NewsInfoActivity.EXTRA_STRING_NAME_CHANNELCODE, arr[1]);
				intent.putExtras(extras);
				startActivity(intent);
			}
		});
		
		mEmptyText = (TextView) mRootView.findViewById(R.id.history_empty_tv);

	}

	public class HistoryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mInfos.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mInfos.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHoder holder = null;
			if (convertView == null) {
				// 初始化HoderView
				holder = new ViewHoder();
				LayoutInflater inflater = LayoutInflater.from(mContext);
				convertView = inflater.inflate(R.layout.item_history_lv, null);

				holder.name = (TextView) convertView.findViewById(R.id.searchitem_topic);
				holder.comment = (TextView) convertView.findViewById(R.id.searchitem_time);
				holder.pic = (ImageView) convertView.findViewById(R.id.item_history_imagview);
				holder.timeTv = (TextView) convertView.findViewById(R.id.history_item_time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHoder) convertView.getTag();
			}
			// 填充数据
			final ServiceNOInfo info = mInfos.get(position);
			holder.name.setText(info.getTitle());
			String dotime = StringUtils.getCommmentDate1(info.getCreateDate(), mStandardDateTime);
			holder.comment.setText(dotime);
			holder.timeTv.setText(mSimpleDateFormat.format(info.getCreateDate()));
			return convertView;
		}

		class ViewHoder {
			private TextView name, desc, comment,timeTv;
			private ImageView pic;
		}

	}
}
