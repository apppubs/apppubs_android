package com.mportal.client.fragment;

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

import com.mportal.client.R;
import com.mportal.client.activity.NewsVideoInfoActivity;
import com.mportal.client.adapter.CommonAdapter;
import com.mportal.client.asytask.AsyTaskCallback;
import com.mportal.client.asytask.AsyTaskExecutor;
import com.mportal.client.bean.NewsVideoInfo;
import com.mportal.client.constant.URLs;
import com.mportal.client.util.WebUtils;
import com.mportal.client.widget.commonlist.CommonListView;
import com.mportal.client.widget.commonlist.CommonListViewListener;

public class ChannelVideoFragment extends ChannelFragment implements AsyTaskCallback {
	
	private final int REQUEST_TAG_VIDEO_LIST = 1;
	
	private CommonListView mLv;
	private VideoAdapter mAdapter;
	public static String VEDIOINTENTINFOS = "vedioinfo";
	private List<NewsVideoInfo> mVideos;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

	
	private class VideoAdapter extends CommonAdapter<NewsVideoInfo>{


		public VideoAdapter(Context context, List<NewsVideoInfo> datas, int resId) {
			super(context, datas, resId);
		}

		@Override
		protected void fillValues(com.mportal.client.adapter.ViewHolder holder, NewsVideoInfo bean, int position) {
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
			String url = String.format(URLs.URL_VIDEO_LIST,mChannel.getCode(),mCurPage);
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
