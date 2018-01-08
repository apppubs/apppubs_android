package com.apppubs.d20.fragment;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.apppubs.d20.activity.ContainerActivity;
import com.apppubs.d20.asytask.AsyTaskCallback;
import com.apppubs.d20.asytask.AsyTaskExecutor;
import com.apppubs.d20.bean.NewsSpecialsInfo;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.util.WebUtils;
import com.apppubs.d20.webapp.WebAppFragment;
import com.apppubs.d20.widget.commonlist.CommonListView;
import com.apppubs.d20.widget.commonlist.CommonListViewListener;
import com.apppubs.d20.R;
import com.apppubs.d20.adapter.CommonAdapter;

public class ChannelSpecialsFragment extends ChannelFragment implements AsyTaskCallback {
	
	private final int REQUEST_TAG_SPECIALS_LIST = 1;
	
	private CommonListView mLv;
	private SpecialsAdapter mAdapter;
	public static String VEDIOINTENTINFOS = "vedioinfo";
	private List<NewsSpecialsInfo> mSpecials;

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
//				Intent intent = new Intent(getActivity(), NewsVideoInfoActivity.class);
//				
//				intent.putExtra(NewsVideoInfoActivity.EXTRA_STRING_NAME_ID, video.getInfoId());
//				intent.putExtra(NewsVideoInfoActivity.EXTRA_STRING_NAME_CHANNELCODE, mChannelCode);
//				getActivity().startActivity(intent);
				NewsSpecialsInfo specials = mAdapter.getItem(position-1);
				Bundle bundle = new Bundle();
				bundle.putString(WebAppFragment.ARGUMENT_STRING_URL,specials.getUrl());
				ContainerActivity.startActivity(mContext, WebAppFragment.class,bundle,null);
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
		AsyTaskExecutor.getInstance().startTask(REQUEST_TAG_SPECIALS_LIST, this, new String[]{mCurPage+++""});
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	
	private class SpecialsAdapter extends CommonAdapter<NewsSpecialsInfo>{


		public SpecialsAdapter(Context context, List<NewsSpecialsInfo> datas, int resId) {
			super(context, datas, resId);
		}

		@Override
		protected void fillValues(com.apppubs.d20.adapter.ViewHolder holder, NewsSpecialsInfo bean, int position) {
			TextView titleTV = holder.getView(R.id.item_frg_specials_title);
			titleTV.setText(bean.getTitle());
//			
			ImageView iv = holder.getView(R.id.item_frg_specials_pic);
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
		if (tag==REQUEST_TAG_SPECIALS_LIST) {
			String url = String.format(URLs.URL_SPECIALS_LIST,URLs.baseURL,mChannel.getCode(),mCurPage);
				try {
					result = WebUtils.requestList(url, NewsSpecialsInfo.class,"resultinfo");
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
		if (tag==REQUEST_TAG_SPECIALS_LIST) {
			mSpecials = (List<NewsSpecialsInfo>) obj;
			if (mAdapter==null) {
				mAdapter = new SpecialsAdapter(mHostActivity, mSpecials, R.layout.item_frg_specials_xlv);
				mLv.setAdapter(mAdapter);
				
			}
			if (mCurPage==1) {
				mLv.setAdapter(mAdapter);
				mLv.stopRefresh();
			}else{
				mAdapter.notifyDataSetChanged();
				mLv.stopLoadMore();
			}
			
		}
	
	}

	@Override
	public void onTaskFail(Integer tag,Exception e) {
		
	}
}
