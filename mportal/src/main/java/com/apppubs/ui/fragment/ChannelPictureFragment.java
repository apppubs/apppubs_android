package com.apppubs.ui.fragment;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apppubs.bean.TNewsInfo;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.ui.activity.NewsPictureInfoActivity;
import com.apppubs.constant.URLs;
import com.apppubs.util.LogM;
import com.apppubs.ui.widget.commonlist.CommonListView;
import com.apppubs.ui.widget.commonlist.CommonListViewListener;
import com.apppubs.d20.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ChannelPictureFragment extends ChannelFragment {
	private CommonListView mLv;
	private LinearLayout mProgress;
	
	private DisplayImageOptions mImageLoaderOptions;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frg_pic_list, null);
		
		initComponent(view);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
	private void initComponent(View view){
		mProgress = (LinearLayout)view.findViewById(R.id.frg_pic_progress_ll);
		mLv=(CommonListView) view.findViewById(R.id.pic_lv);
		mLv.setPullRefreshEnable(true);
		mLv.setPullLoadEnable(true);
		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TNewsInfo ni = (TNewsInfo) parent.getAdapter().getItem(position);
				Intent intent = new Intent(mHostActivity,NewsPictureInfoActivity.class);
				intent.putExtra(NewsPictureInfoActivity.EXTRA_STRING_NAME_ID, ni.getId());
				startActivity(intent);
			}
		});
		
		mLv.setCommonListViewListener(new CommonListViewListener() {
			
			@Override
			public void onRefresh() {
				refresh();
			}
			
			@Override
			public void onLoadMore() {
				load();
			}
		});
	}
	private void init() {
		
		Drawable drawable = Drawable.createFromPath(mHostActivity.getFilesDir().getAbsolutePath()+"/stance_pic.png");
		mImageLoaderOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(drawable)
		.showImageForEmptyUri(drawable)
		.showImageOnFail(drawable)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

		mCurPage = 1;
		load();
	}

	private void load(){
		
		mNewsBiz.getNewsInfoPage(TNewsInfo.NEWS_TYPE_PICTURE,mChannelCode, mCurPage, URLs.PAGE_PIC_SIZE, new IAPCallback<List<TNewsInfo>>() {
			
			@Override
			public void onException(APError excepCode) {
				mLv.stopRefresh();
				mLv.stopLoadMore();
			}
			
			@Override
			public void onDone(List<TNewsInfo> obj) {
				
				mProgress.setVisibility(View.GONE);
				
				LogM.log(this.getClass(), "返回成功 size:"+obj.size()+"mCurPage:"+mCurPage);
				if(mCurPage==0){
					mNewsInfoList = obj;
					mAdapter = new PicGridViewAdapter();
					mLv.setAdapter(mAdapter);
					mLv.stopRefresh();
					mCurPage = 2;
				}else if(mCurPage==1){
					mNewsInfoList = obj;
					mAdapter = new PicGridViewAdapter();
					mLv.setAdapter(mAdapter);
					
					mCurPage ++;
					
					if(mNewsInfoList.size()==0){
						refresh();
					}
					
				}else {
					mNewsInfoList.addAll(obj);
					mAdapter.notifyDataSetChanged();
					mLv.stopLoadMore();
					mCurPage ++;
				}
				if(obj.size()!=URLs.PAGE_PIC_SIZE){
					mLv.haveLoadAll();
				}
			}
		});
		
	}
	
	
	private class PicGridViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mNewsInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return mNewsInfoList.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {

			ViewHolder holder = null;
			if (convertView == null) {
				// 初始化HoderView
				holder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) mHostActivity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_pic_gv, null);
				holder.iv = (ImageView) convertView
						.findViewById(R.id.pic_iv);
				holder.tv = (TextView) convertView.findViewById(R.id.pic_tv);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			TNewsInfo ni = mNewsInfoList.get(position);
			// 获得屏幕的宽高,屏幕宽度PX
			mImageLoader.displayImage(ni.getPicURL(), holder.iv,mImageLoaderOptions);
			holder.tv.setText(ni.getTitle());
			LogM.log(this.getClass(), "评论数："+ni.getCommentNum());
			return convertView;
		}

		class ViewHolder {
			private TextView tv;
			private ImageView iv;
		}
	}


	@Override
	public void refresh() {
		mCurPage = 0;
		load();
	}
	
}
