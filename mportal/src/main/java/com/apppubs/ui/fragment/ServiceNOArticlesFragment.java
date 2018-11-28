package com.apppubs.ui.fragment;

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

import com.apppubs.bean.TServiceNOInfo;
import com.apppubs.bean.http.ServiceNOInfoPageResult;
import com.apppubs.constant.URLs;
import com.apppubs.d20.R;
import com.apppubs.model.MsgController;
import com.apppubs.presenter.ServiceNOInfoListPresenter;
import com.apppubs.ui.IServiceNoInfoPageView;
import com.apppubs.ui.activity.ContainerActivity;
import com.apppubs.ui.activity.ServiceNOInfoActivity;
import com.apppubs.ui.webapp.WebAppFragment;
import com.apppubs.ui.widget.commonlist.CommonListView;
import com.apppubs.ui.widget.commonlist.CommonListViewListener;
import com.apppubs.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ServiceNOArticlesFragment extends TitleBarFragment implements IServiceNoInfoPageView{
	
	public static final int REUQEST_CODE_SERVICE_NO = 1;

	public static final String ARGS_STRING_SERVICE_NO_ID = "service_no_id";
	
	/**
	 * 服务历史推送
	 */
	private CommonListView mLv;
	private HistoryAdapter adapter;
	private List<ServiceNOInfoPageResult.Items> mDatas;
	private String mServiceNoId;

	private ServiceNOInfoListPresenter mPresenter;

	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.frg_service_history, null);
		Bundle args = getArguments();
		mServiceNoId = args.getString(ServiceNOArticlesFragment.ARGS_STRING_SERVICE_NO_ID);
		init();

		MsgController.getInstance(mContext).cancelNotificationBySenderId(mServiceNoId);
		return mRootView;
	}

	private void init() {
		mLv = (CommonListView) mRootView.findViewById(R.id.service_history_xlv);
		mLv.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		mLv.setPullRefreshEnable(true);
		mLv.setPullLoadEnable(true);
		mLv.setCommonListViewListener(new CommonListViewListener() {

			@Override
			public void onRefresh() {
				mPresenter.onRefresh();
			}

			@Override
			public void onLoadMore() {
				mPresenter.onLoadMore();
			}
		});

		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posion, long arg3) {
			}
		});

		mPresenter = new ServiceNOInfoListPresenter(mContext, this, mServiceNoId);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		mTitleBar.setRightBtnImageResourceId(R.drawable.new_service);
		mTitleBar.setRightBtnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {// 跳到服务号详情界面
				Intent intent = new Intent(mHostActivity,ServiceNOInfoActivity.class);
				intent.putExtra(ServiceNOInfoActivity.EXTRA_STRING_TITLE, getResources().getString(R.string.service_no));
				intent.putExtra(ServiceNoInfoFragment.ARGS_STRING_SERVICE_NO_ID, mServiceNoId);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});
		mLv.refresh();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REUQEST_CODE_SERVICE_NO&&resultCode==Activity.RESULT_OK){
			mHostActivity.finish();
		}
	}

	@Override
	public void setDatas(List<ServiceNOInfoPageResult.Items> datas) {
		mDatas = datas;

		if (adapter == null){
			adapter = new HistoryAdapter();
			mLv.setAdapter(adapter);
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void hideRefreshAndLoadMore() {
		mLv.stopLoadMore();
		mLv.stopRefresh();
	}

	@Override
	public void haveLoadAll() {
		mLv.haveLoadAll();
	}

	public class HistoryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public ServiceNOInfoPageResult.Items getItem(int arg0) {
			return mDatas.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if (convertView == null) {
				// 初始化HoderView
				holder = new ViewHolder();
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
				holder = (ViewHolder) convertView.getTag();
			}
			// 填充数据
			final ServiceNOInfoPageResult.Items history = mDatas.get(position);
			holder.name.setText(history.getTitle());
			holder.desc.setText(history.getSummary());
			String picurl = history.getPicURL();
			if (!TextUtils.isEmpty(picurl)) {
				holder.imageContainer.setVisibility(View.VISIBLE);
				mImageLoader.displayImage(picurl, holder.pic);
			} else {
				holder.imageContainer.setVisibility(View.GONE);
			}
			String dotime = StringUtils.getCommmentDate1(history.getCreateTime(), new Date());
			holder.comment.setText(dotime);
			holder.sendTimeTv.setText(sdf.format(history.getCreateTime()));
			if(TextUtils.isEmpty(history.getLinkURL())){
				holder.detailLl.setVisibility(View.GONE);
			}else{
				holder.detailLl.setVisibility(View.VISIBLE);
			}
			
			holder.serviceifo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					// 需要根据不同类型进行不通动作
					String url = history.getLinkURL();;
					if (!TextUtils.isEmpty(url)) {
						Bundle extras = new Bundle();
						extras.putString(WebAppFragment.ARGUMENT_STRING_URL, url);
						ContainerActivity.startContainerActivity(mHostActivity, WebAppFragment.class, extras, mHostActivity
								.getTitleBar().getTitle());
					}

				}
			});
			return convertView;
		}

		class ViewHolder {
			private TextView name, desc, comment;
			private ImageView pic;
			private RelativeLayout serviceifo, imageContainer;
			private TextView sendTimeTv;
			private LinearLayout detailLl;
		}

	}
}
