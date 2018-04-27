package com.apppubs.ui.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apppubs.AppContext;
import com.apppubs.bean.ServiceNOInfo;
import com.apppubs.constant.APError;
import com.apppubs.model.MsgController;
import com.apppubs.d20.R;
import com.apppubs.ui.activity.ContainerActivity;
import com.apppubs.model.APCallback;
import com.apppubs.constant.URLs;
import com.apppubs.util.StringUtils;
import com.apppubs.util.SystemUtils;
import com.apppubs.ui.webapp.WebAppFragment;
import com.apppubs.ui.widget.commonlist.CommonListView;
import com.apppubs.ui.widget.commonlist.CommonListViewListener;

public class ServiceNoInfoListFragement extends BaseFragment {
	
	public static final int REUQEST_CODE_SERVICE_NO = 1;
	
	/**
	 * 服务历史推送
	 */
	private LinearLayout mprogress;
	private CommonListView mLv;
	private HistoryAdapter adapter;
	private List<ServiceNOInfo> mInfos;
	private int mCurPage = 1;
	private LinearLayout mEmptyLl;
	private TextView mEmptyText;
	private Date mStandardDateTime;
	private String mServiceNoId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mRootView = inflater.inflate(R.layout.frg_service_history, null);
		Bundle args = getArguments();
		mServiceNoId = args.getString(ServiceNoInfoFragment.ARGS_STRING_SERVICE_NO_ID);
		init();
		mEmptyLl = (LinearLayout) mRootView.findViewById(R.id.collection_nullshow_ll);
		mEmptyText = (TextView) mRootView.findViewById(R.id.null_text);
		mEmptyText.setText("没有服务推送消息");

		clearUnreadNum();
		MsgController.getInstance(mContext).cancelNotificationBySenderId(mServiceNoId);
		return mRootView;
	}

	private void clearUnreadNum() {
		String url = String.format(URLs.URL_CLEAR_UNREAD_NUM_FOR_SERVICE_NO_AND_CHAT,URLs.baseURL,URLs.appCode, mServiceNoId, AppContext.getInstance(mContext).getCurrentUser().getUsername());
		mRequestQueue.add(new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				
			}
		}));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		mTitleBar.setRightBtnImageResourceId(R.drawable.new_service);
		mTitleBar.setRightBtnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {// 跳到服务号详情界面
				Bundle b = new Bundle();
				b.putString(ServiceNoInfoFragment.ARGS_STRING_SERVICE_NO_ID, mServiceNoId);
//				ContainerActivity.startActivity(mContext, ServiceNoInfoFragment.class, b, "服务号");
				Intent intent = new Intent(mHostActivity,ContainerActivity.class);
				intent.putExtra(ContainerActivity.EXTRA_FRAGMENT_CLASS_NAME, ServiceNoInfoFragment.class.getName());
				intent.putExtra(ContainerActivity.EXTRA_STRING_TITLE, "服务号");
				intent.putExtra(ServiceNoInfoFragment.ARGS_STRING_SERVICE_NO_ID, mServiceNoId);
				startActivityForResult(intent, REUQEST_CODE_SERVICE_NO);
			}
		});
		getPage(0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REUQEST_CODE_SERVICE_NO&&resultCode==Activity.RESULT_OK){
			mHostActivity.finish();
		}
	}
	private void getPage(final int i) {
		if (SystemUtils.canConnectNet(mContext)) {
			mSystemBiz.getStandardDataTime(new APCallback<Date>() {

				@Override
				public void onException(APError excepCode) {

				}

				@Override
				public void onDone(Date obj) {
					mStandardDateTime = obj;
					loadData(i);
				}
			});
		} else {
			Toast.makeText(mContext, getResources().getString(R.string.network_faile), Toast.LENGTH_LONG).show();
		}
	}

	private void init() {
		mprogress = (LinearLayout) mRootView.findViewById(R.id.service_progress_ll);
		mLv = (CommonListView) mRootView.findViewById(R.id.service_history_xlv);
		mLv.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		mLv.setPullRefreshEnable(false);
		mLv.setPullLoadEnable(false);
		mLv.setCommonListViewListener(new CommonListViewListener() {

			@Override
			public void onRefresh() {
				mCurPage = 1;
				getPage(1);
			}

			@Override
			public void onLoadMore() {
				getPage(0);
			}
		});

		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posion, long arg3) {
			}
		});
	}

	private void loadData(final int i) {
		mMsgBussiness.getAloneServiceList(mServiceNoId, new APCallback<List<ServiceNOInfo>>() {

			@Override
			public void onException(APError excepCode) {
			}

			@Override
			public void onDone(List<ServiceNOInfo> obj) {
				mprogress.setVisibility(View.GONE);
				adapter = new HistoryAdapter();
				if (mCurPage == 1) {
					mInfos = obj;
					if (mInfos.size() == 0) {
						mEmptyLl.setVisibility(View.VISIBLE);
					} else {
						if (i == 1) {
							mLv.stopRefresh();
						}
						mLv.setAdapter(adapter);
					}
				} else {// lodemore
					if (obj.size() == 0) {
						Toast.makeText(mContext, "没有更多了", Toast.LENGTH_SHORT).show();
					}
					mInfos.addAll(obj);
					adapter.notifyDataSetChanged();
					mLv.stopLoadMore();
				}
				mCurPage++;
			}
		});
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

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHoder holder = null;
			if (convertView == null) {
				// 初始化HoderView
				holder = new ViewHoder();
				LayoutInflater inflater = LayoutInflater.from(mContext);
				convertView = inflater.inflate(R.layout.item_aloneservice_xlv, null);
				holder.name = (TextView) convertView.findViewById(R.id.aloneservice_history_topic);
				holder.desc = (TextView) convertView.findViewById(R.id.aloneservice_history_desp);
				holder.comment = (TextView) convertView.findViewById(R.id.aloneservice_history_time);
				holder.pic = (ImageView) convertView.findViewById(R.id.aloneservice_history_imagview);
				holder.serviceifo = (RelativeLayout) convertView.findViewById(R.id.aloneservice_history_info);
				holder.imageContainer = (RelativeLayout) convertView.findViewById(R.id.aloneservice_history_img_rl);
				holder.sendTimeTv = (TextView) convertView.findViewById(R.id.aloneservice_history_fabiaoshijian);
				holder.detailLl = (LinearLayout) convertView.findViewById(R.id.service_no_info_item_detail_ll);
				convertView.setTag(holder);
			} else {
				holder = (ViewHoder) convertView.getTag();
			}
			// 填充数据
			final ServiceNOInfo history = mInfos.get(position);
			holder.name.setText(history.getTitle());
			holder.desc.setText(history.getSummary());
			String picurl = history.getPicurl();
			if (!TextUtils.isEmpty(picurl)) {
				holder.imageContainer.setVisibility(View.VISIBLE);
				mImageLoader.displayImage(picurl, holder.pic);
			} else {
				holder.imageContainer.setVisibility(View.GONE);
			}
			String dotime = StringUtils.getCommmentDate1(history.getCreateDate(), mStandardDateTime);
			holder.comment.setText(dotime);
			holder.sendTimeTv.setText(sdf.format(history.getCreateDate()));
			if(history.getType()==ServiceNOInfo.TYPE_NONE_CONTENT){
				holder.detailLl.setVisibility(View.GONE);
			}else{
				holder.detailLl.setVisibility(View.VISIBLE);
			}
			
			holder.serviceifo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					// 需要根据不同类型进行不通动作
					String url = null;
					if (history.getType() == ServiceNOInfo.TYPE_NORMAL) {
						url = String.format(URLs.URL_SERVICEINFO,URLs.baseURL,URLs.appCode) + "&serviceinfo_id=" + history.getId() + "&service_id="
								+ mServiceNoId;
					} else if (history.getType() == ServiceNOInfo.TYPE_LINK) {
						url = history.getLink();
					}
					if (!TextUtils.isEmpty(url)) {
						Bundle extras = new Bundle();
						extras.putString(WebAppFragment.ARGUMENT_STRING_URL, url);
						ContainerActivity.startActivity(mHostActivity, WebAppFragment.class, extras, mHostActivity
								.getTitleBar().getTitle());
					}

				}
			});
			return convertView;
		}

		class ViewHoder {
			private TextView name, desc, comment;
			private ImageView pic;
			private RelativeLayout serviceifo, imageContainer;
			private TextView sendTimeTv;
			private LinearLayout detailLl;
		}

		public List<ServiceNOInfo> backHistoryInfo() {
			return mInfos;
		}
	}
}
