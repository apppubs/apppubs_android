package com.mportal.client.fragment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mportal.client.R;
import com.mportal.client.activity.NewsInfoActivity;
import com.mportal.client.activity.NewsPictureInfoActivity;
import com.mportal.client.activity.NewsVideoInfoActivity;
import com.mportal.client.activity.PaperInfoActivity;
import com.mportal.client.adapter.CommonAdapter;
import com.mportal.client.adapter.ViewHolder;
import com.mportal.client.bean.Collection;
import com.mportal.client.widget.ConfirmDialog;
import com.mportal.client.widget.ConfirmDialog.ConfirmListener;
import com.mportal.client.widget.TitleBar;
import com.mportal.client.widget.commonlist.CommonListView;
import com.orm.SugarRecord;

public class CollectionFragment extends BaseFragment implements OnItemClickListener {

	protected List<Collection> mList;
	private boolean isEditMode;
	private MyAdapter mAdapter;
	private CommonListView mXlv;
	private LinearLayout mEmptyLl;
	private SimpleDateFormat mSimpleDateFormat;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
		mRootView = inflater.inflate(R.layout.frg_collection, null);
		initView();
		return mRootView;
	}

	private void initView() {
		mXlv = (CommonListView) mRootView.findViewById(R.id.collection_xlv);
		mEmptyLl = (LinearLayout) mRootView.findViewById(R.id.collection_nullshow_ll);
		mXlv.setPullRefreshEnable(false);
		mXlv.setPullLoadEnable(false);
		mXlv.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int posion, long arg3) {
		if (isEditMode) {
			doDeletd(posion - 1);
		} else {
			Intent i = null;
			Class<?> cls = null;
			if (mList.get(posion - 1).getType() == Collection.TYPE_NORMAL) {
				cls = NewsInfoActivity.class;
				i = new Intent(getActivity(), cls);
				String[] infoIdAndChannelCode = mList.get(posion - 1).getInfoId().split(",");
				i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_ID, infoIdAndChannelCode[0]);
				i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_CHANNELCODE, infoIdAndChannelCode[1]);
			} else if (mList.get(posion - 1).getType() == Collection.TYPE_PIC) {
				cls = NewsPictureInfoActivity.class;
				i = new Intent(getActivity(), cls);
				i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_ID, mList.get(posion - 1).getInfoId());
			} else if (mList.get(posion - 1).getType() == Collection.TYPE_VEDIO) {
				cls = NewsVideoInfoActivity.class;
				i = new Intent(getActivity(), cls);
				String[] infoIdAndChannelCode = mList.get(posion - 1).getInfoId().split(",");
				i.putExtra(NewsVideoInfoActivity.EXTRA_STRING_NAME_ID, infoIdAndChannelCode[0]);
				i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_CHANNELCODE, infoIdAndChannelCode[1]);
			}else if (mList.get(posion - 1).getType() == Collection.TYPE_PAPER) {
				cls = PaperInfoActivity.class;
				i = new Intent(getActivity(), cls);
				i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_ID, mList.get(posion - 1).getInfoId());
			} else {
				cls = NewsVideoInfoActivity.class;
				i = new Intent(getActivity(), cls);
				i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_ID, mList.get(posion - 1).getInfoId());
			}

			startActivity(i);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}

	@Override
	public void changeActivityTitleView(TitleBar titleBar) {
		super.changeActivityTitleView(titleBar);
		mTitleBar.setRightText("编辑");
		mTitleBar.setRightBtnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isEditMode = !isEditMode;
				mTitleBar.setRightText(isEditMode ? "完成" : "编辑");
				mAdapter.notifyDataSetChanged();
			}
		});
	}
	@Override
	public void onResume() {
		super.onResume();
		mList = SugarRecord.find(Collection.class, null, null, null, "ADD_TIME desc", null);
		mAdapter = new MyAdapter(mContext, mList, R.layout.item_collection);
		mXlv.setAdapter(mAdapter);
		mEmptyLl.setVisibility(mList.isEmpty() ? View.VISIBLE : View.GONE);
	}

	private class MyAdapter extends CommonAdapter<Collection> {

		public MyAdapter(Context context, List<Collection> datas, int resId) {
			super(context, datas, resId);
		}

		@Override
		protected void fillValues(ViewHolder holder, Collection bean, final int position) {

			TextView titleTv = holder.getView(R.id.collection_item_title_tv);
			TextView typeAndTimeTv = holder.getView(R.id.collection_item_type_time_tv);

			titleTv.setText(bean.getTitle());

			String timeStr = mSimpleDateFormat.format(bean.getAddTime());
			String typeStr = getTypeString(bean.getType());
			typeAndTimeTv.setText(typeStr + " - " + timeStr);

			ImageView deleteIv = holder.getView(R.id.collection_item_delete_iv);
			deleteIv.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
			if (isEditMode) {

				deleteIv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						onItemClick(null, null, position + 1, 0);
					}
				});
			}

		}

		private String getTypeString(int type) {
			String typeStr;
			switch (type) {
			case Collection.TYPE_NORMAL:
				typeStr = "资讯";
				break;
			case Collection.TYPE_PAPER:
				typeStr = "报纸";
				break;
			case Collection.TYPE_PIC:
				typeStr = "图片";
				break;
			case Collection.TYPE_URL:
				typeStr = "链接";
				break;
			case Collection.TYPE_VEDIO:
				typeStr = "视频";
				break;
			default:
				typeStr = "";
				break;
			}
			return typeStr;
		}

	}

	public void doDeletd(final int i) {
		new ConfirmDialog(getActivity(), new ConfirmListener() {

			@Override
			public void onOkClick() {
				SugarRecord.deleteAll(Collection.class, "INFO_ID=?", mList.get(i).getInfoId());
				mList.remove(i);
				mAdapter.notifyDataSetChanged();
				if (mList.size() == 0) {
					mEmptyLl.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onCancelClick() {

			}
		}, "确定删除吗？", "取消", "确定").show();
	}

}
