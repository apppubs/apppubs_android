package com.apppubs.ui.fragment;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.apppubs.ui.activity.NewsVideoInfoActivity;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.asytask.AsyTaskExecutor;
import com.apppubs.util.WebUtils;
import com.apppubs.ui.widget.commonlist.CommonListView;
import com.apppubs.ui.widget.commonlist.CommonListViewListener;
import com.apppubs.d20.R;
import com.apppubs.asytask.AsyTaskCallback;
import com.apppubs.bean.NewsVideoInfo;
import com.apppubs.constant.URLs;

public class ChannelVideoFragment extends ChannelFragment implements AsyTaskCallback {
	
	private final int REQUEST_TAG_VIDEO_LIST = 1;
	
	private CommonListView mLv;
	private VideoAdapter mAdapter;
	public static String VEDIOINTENTINFOS = "vedioinfo";
	private List<NewsVideoInfo> mVideos;

	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frg_video, null);
		init(view);

		return view;
	}

	private void init(View view) {
		mLv = (CommonListView) view.findViewById(R.id.left_vedio_xlv);
		mLv.setPullRefreshEnable(true);
		mLv.setPullLoadEnable(true);
		mLv.setOnItemClickListener(new OnItemClickListener() {
       
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent(getActivity(), NewsVideoInfoActivity.class);
				NewsVideoInfo video = mAdapter.getItem(position-1);
				intent.putExtra(NewsVideoInfoActivity.EXTRA_STRING_NAME_ID, video.getInfoId());
				intent.putExtra(NewsVideoInfoActivity.EXTRA_STRING_NAME_CHANNELCODE, mChannelCode);
				getActivity().startActivity(intent);
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
		refresh();
	}
	private void load() {
		AsyTaskExecutor.getInstance().startTask(REQUEST_TAG_VIDEO_LIST, this, new String[]{mCurPage+++""});
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	
	private class VideoAdapter extends CommonAdapter<NewsVideoInfo> {


		public VideoAdapter(Context context, List<NewsVideoInfo> datas, int resId) {
			super(context, datas, resId);
		}

		@Override
		protected void fillValues(ViewHolder holder, NewsVideoInfo bean, int position) {
			TextView titleTV = holder.getView(R.id.item_frg_video_title);
			titleTV.setText(bean.getTitle());
//			
			ImageView iv = holder.getView(R.id.item_frg_video_pic);
			mImageLoader.displayImage(bean.getPicUrl(), iv);
			
		}
		
		
	}


	@Override
	public void refresh() {
		mCurPage = 0;
		load();
	}

	@Override
	public Object onExecute(Integer tag, String[] params) {
		Object result = null;
		if (tag==REQUEST_TAG_VIDEO_LIST) {
			String url = String.format(URLs.URL_VIDEO_LIST, URLs.baseURL,mChannel.getCode(),mCurPage);
				try {
					result = WebUtils.requestList(url, NewsVideoInfo.class,"info");
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		return result;
	}

	@Override
	public void onTaskSuccess(Integer tag, Object obj) {
		if (tag==REQUEST_TAG_VIDEO_LIST) {
			mVideos = (List<NewsVideoInfo>) obj;
			if (mAdapter==null) {
				mAdapter = new VideoAdapter(mHostActivity, mVideos, R.layout.item_frg_video_xlv);
				
			}
			if (mCurPage==1) {
				mLv.setAdapter(mAdapter);
				mLv.stopRefresh();
			}else{
				mLv.stopLoadMore();
				mAdapter.notifyDataSetChanged();
			}
			
		}
	
	}

	@Override
	public void onTaskFail(Integer tag,Exception e) {
		
	}
}
