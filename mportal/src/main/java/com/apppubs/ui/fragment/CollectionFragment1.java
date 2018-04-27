package com.apppubs.ui.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apppubs.bean.TCollection;
import com.apppubs.bean.TNewsInfo;
import com.apppubs.ui.activity.NewsVideoInfoActivity;
import com.apppubs.util.LogM;
import com.apppubs.d20.R;
import com.apppubs.ui.activity.CollectionActivity;
import com.apppubs.ui.activity.NewsInfoActivity;
import com.apppubs.ui.activity.NewsPictureInfoActivity;
import com.apppubs.ui.activity.PaperInfoActivity;
import com.apppubs.ui.fragment.CollectionFragment1.CollectionAdapter.ViewHoder;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.ui.widget.ConfirmDialog.ConfirmListener;
import com.apppubs.ui.widget.commonlist.CommonListView;
import com.orm.SugarRecord;

public class CollectionFragment1 extends BaseFragment {

	protected static final int TYPE_NEWS = 0;
	protected static final int TYPE_PAPER = 1;
	protected static final int TYPE_PIC = 2;
	private List<ImageView> ivs = new ArrayList<ImageView>();
	protected int mType;// 收藏类型
	protected CollectionAdapter adapter;
	protected List<TCollection> mList;
	private boolean isDelete;
	private CommonListView mXlv;
	private LinearLayout mEmptyLl;
	private BroadcastReceiver br;
	private ViewHoder viewhoder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frg_collection, null);
		mXlv = (CommonListView) v.findViewById(R.id.collection_xlv);
		mEmptyLl = (LinearLayout) v.findViewById(R.id.collection_nullshow_ll);

		init();
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (br != null) {
			getActivity().unregisterReceiver(br);
		}
	}

	private void init() {

		mXlv.setPullRefreshEnable(false);
		mXlv.setPullLoadEnable(false);

		mXlv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posion, long arg3) {
				if (isDelete) {
					doDeletd(posion - 1);
				} else {
					Class cls = null;
					if (mList.get(posion - 1).getType() == TCollection.TYPE_NORMAL) {
						cls = NewsInfoActivity.class;
					} else if (mList.get(posion - 1).getType() == TCollection.TYPE_PIC) {
						cls = NewsPictureInfoActivity.class;
					} else if (mList.get(posion - 1).getType() == TCollection.TYPE_PAPER) {
						cls = PaperInfoActivity.class;
					} else {
						cls = NewsVideoInfoActivity.class;
					}
					Intent i = new Intent(getActivity(), cls);
					i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_ID, mList.get(posion - 1).getInfoId());
					i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_CHANNELCODE, "");
					startActivity(i);
				}
			}

		});
	}

	@Override
	public void onResume() {

		super.onResume();
		adapter = new CollectionAdapter();
		if (adapter.isEmpty()) {
			mEmptyLl.setVisibility(View.VISIBLE);
		} else {
			mXlv.setAdapter(adapter);
		}

	}

	public class CollectionAdapter extends BaseAdapter {
		private List<ImageView> ivs = new ArrayList<ImageView>();

		public CollectionAdapter() {
			// mList = SugarRecord.find(TCollection.class, "TYPE = ?", mType+"");
			mList = SugarRecord.find(TCollection.class, "TYPE = ?", new String[] { mType + "" }, null, null, null);
			LogM.log(this.getClass(), "CollectionAdapter mList.size():" + mList.size());
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int pos, View convertView, ViewGroup arg2) {
			viewhoder = null;
			if (convertView == null) {
				// 初始化HoderView
				viewhoder = new ViewHoder();
				LayoutInflater inflater = (LayoutInflater) mHostActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.save_xlvitem, null);
				LinearLayout lay = (LinearLayout) convertView.findViewById(R.id.saveitem_lay);
				viewhoder.name = (TextView) convertView.findViewById(R.id.saveitem_name);
				viewhoder.desc = (TextView) convertView.findViewById(R.id.saveitem_desp);
				viewhoder.delsete = (ImageView) convertView.findViewById(R.id.saveitem_delsete);
				ivs.add(viewhoder.delsete);
				// initBrowase();//操作广播是否显示删除按钮
				br = new BroadcastReceiver() {

					@Override
					public void onReceive(Context arg0, Intent intent) {
						// TODO Auto-generated method stub
						if (intent.getAction().equals(CollectionActivity.COLLECTIONDELECTACTION)) {
							isDelete = intent.getBooleanExtra(CollectionActivity.COLLECTIONDELECT, false);
							if (isDelete) {

								for (int i = 0; i < ivs.size(); i++) {
									ivs.get(i).setVisibility(View.VISIBLE);
								}
								// viewhoder.delsete.setVisibility(View.VISIBLE);
							} else {
								for (int i = 0; i < ivs.size(); i++) {
									ivs.get(i).setVisibility(View.GONE);
								}
								// viewhoder.delsete.setVisibility(View.GONE);
							}
						}
					}
				};
				IntentFilter filter = new IntentFilter();
				filter.addAction(CollectionActivity.COLLECTIONDELECTACTION);
				getActivity().registerReceiver(br, filter);
				viewhoder.delsete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						doDeletd(pos);
					}
				});
				convertView.setTag(viewhoder);
			} else {
				viewhoder = (ViewHoder) convertView.getTag();
			}
			// 填充数据
			TCollection curCollection = mList.get(pos);
			viewhoder.name.setText(curCollection.getTitle());

			if (curCollection.getContentAbs() == null || curCollection.getContentAbs().equals("")) {
				viewhoder.desc.setVisibility(View.GONE);
			} else {
				viewhoder.desc.setVisibility(View.VISIBLE);
				String des = mList.get(pos).getContentAbs();
				int len = des.length();
				String str = len > 28 ? (des.subSequence(0, 28) + "···") : des;
				viewhoder.desc.setText(str);
			}
			return convertView;
		}

		class ViewHoder {
			private TextView name, desc;
			private ImageView delsete;
		}
	}

	public void doDeletd(final int i) {
		new ConfirmDialog(getActivity(), new ConfirmListener() {

			@Override
			public void onOkClick() {
				TNewsInfo mNewsInfo = new TNewsInfo();
				mNewsInfo.setIsCollected(0);
				SugarRecord.updateById(TNewsInfo.class, mList.get(i).getInfoId(), "IS_COLLECTED", TNewsInfo.UNCOLLECTED + "");
				TCollection c = new TCollection();
				c.setAddTime(new Date());
				c.setInfoId(mList.get(i).getInfoId());
				c.setTitle(mList.get(i).getTitle());
				c.setContentAbs(mList.get(i).getContentAbs());
				c.setType(mType);
				c.save();
				SugarRecord.deleteAll(TCollection.class, "INFO_ID=?", mList.get(i).getInfoId());
				mList.remove(i);
				adapter.notifyDataSetChanged();
				if (mList.size() == 0) {
					mEmptyLl.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onCancelClick() {

			}
		}, "确定删除吗？", "取消", "确定").show();
	}

	public void setType(int type) {
		mType = type;
	}

}
