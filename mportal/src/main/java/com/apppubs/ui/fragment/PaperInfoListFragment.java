package com.apppubs.ui.fragment;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.bean.TPaperInfo;
import com.apppubs.ui.activity.PaperInfoActivity;
import com.apppubs.ui.activity.PaperIssueActivity;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.bean.TPaperCatalog;
import com.apppubs.util.LogM;
import com.apppubs.MportalApplication;
import com.apppubs.d20.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 报纸的版面预览视图
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年2月6日 by zhangwen create
 * 
 */
public class PaperInfoListFragment extends BaseFragment implements OnScrollListener {

	private DisplayImageOptions mImageLoaderOptions;
	private PaperIssueActivity mHostActivity;
	private List<TPaperCatalog> mCatalogList;
	private ListView lv;
	private TextView mTextflag;

	private String mTitle;

	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.frg_paperissuelist, null);
		mTextflag = (TextView) v.findViewById(R.id.frg_paperissuelist_flag);
		lv = (ListView) v.findViewById(R.id.frg_paperissurelist_lv);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		mHostActivity = (PaperIssueActivity) getActivity();
		Drawable drawable = Drawable.createFromPath(mHostActivity.getFilesDir().getAbsolutePath() + "/stance.png");
		mImageLoaderOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(drawable)
				.showImageForEmptyUri(drawable)
				.showImageOnFail(drawable)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.denyNetworkDownload(
						MportalApplication.systemState.getNetworkState() != ConnectivityManager.TYPE_WIFI
								&& !mAppContext.getSettings().isAllowDowPicUse2G()).build();
		mCatalogList = mHostActivity.getCatalogList();

		lv.setAdapter(new PaperIssueListAdapter());
		lv.setOnScrollListener(this);
		mTitle = mTitleBar.getTitle();
	}

	@Override
	public void onResume() {
		super.onResume();
		LogM.log(this.getClass(), "onResume");
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			mTitleBar.setTitle(mTitle);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		/**
		 * we want to load the next chunk of data before the user reach the end
		 * of the list. 在列表打到末尾之前，我们要加载下一个数据块 firstVisibleItem
		 * 表示在当前屏幕显示的第一个listItem在整个listView里面的位置（下标从0开始）
		 * visibleItemCount表示在现时屏幕可以见到的ListItem(部分显示的ListItem也算)总数
		 * totalItemCount表示ListView的ListItem总数
		 * listView.getLastVisiblePosition()表示在现时屏幕最后一个ListItem
		 * (最后ListItem要完全显示出来才算)在整个ListView的位置（下标从0开始）
		 */

		mTextflag.setText(mCatalogList.get(firstVisibleItem).getName());
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}

	public class PaperIssueListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCatalogList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return "";
		}

		@Override
		public long getItemId(int arg0) {
			return mCatalogList.size();
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup arg2) {
			convertView = mInflater.inflate(R.layout.item_frg_paperisurelist_list, null);
			TextView mBanbiaoti = (TextView) convertView.findViewById(R.id.frg_papreissurelist_list_ban);
			ListView mSecondLv = (ListView) convertView.findViewById(R.id.frg_papreissurelist_list_lv);
			if (pos == 0) {
				mBanbiaoti.setVisibility(View.GONE);
			} else {
				mBanbiaoti.setVisibility(View.VISIBLE);
			}
			mBanbiaoti.setText(mCatalogList.get(pos).getName());
			mSecondLv.setAdapter(new CommonAdapter<TPaperInfo>(mHostActivity, mCatalogList.get(pos).getInfoList(),
					R.layout.item_frg_paperissuelist) {

				@Override
				protected void fillValues(ViewHolder holder, TPaperInfo bean, int position) {
					TextView titleTv = holder.getView(R.id.item_frg_paperissuelist_name);
					ImageView iv = holder.getView(R.id.item_frg_paperissuelist_img_iv);
					if (!TextUtils.isEmpty(bean.getPic1())) {
						iv.setVisibility(View.VISIBLE);
						mImageLoader.displayImage(bean.getPic1(), iv, mImageLoaderOptions);
					} else {
						iv.setVisibility(View.GONE);
					}
					titleTv.setText(bean.getTitle());
				}
			});
			mSecondLv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					TPaperInfo info = (TPaperInfo) parent.getAdapter().getItem(position);
					Intent intent = new Intent(getActivity(), PaperInfoActivity.class);
					intent.putExtra(PaperInfoActivity.EXTRA_STRING_ID, info.getId());
					getActivity().startActivity(intent);
				}

			});
			return convertView;
		}

	}

}
