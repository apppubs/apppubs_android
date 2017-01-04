 
package com.mportal.client.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.asytask.AsyTaskCallback;
import com.mportal.client.asytask.AsyTaskExecutor;
import com.mportal.client.bean.HeadPic;
import com.mportal.client.bean.NewsChannel;
import com.mportal.client.bean.NewsInfo;
import com.mportal.client.business.NewsBussiness;
import com.mportal.client.constant.URLs;
import com.mportal.client.util.GsonUtils;
import com.mportal.client.util.JSONResult;
import com.mportal.client.util.LogM;
import com.mportal.client.util.WebUtils;
import com.mportal.client.view.SlidePicView;
import com.mportal.client.view.SlidePicView.SlidePicItem;
import com.mportal.client.view.commonlist.CommonListView;
import com.mportal.client.view.commonlist.CommonListViewListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.orm.SugarRecord;
/**
 * 
 * @author Administrator
 * 新闻Fragment
 *
 */
public class ChannelDefaultFragment extends ChannelFragment  implements OnClickListener,AsyTaskCallback{
	
	public static final String ARGUMENT_SERIALIZABLE_NAME_CHANNEL = "channel";
	private final int TAST_CODE_REQUEST_CHANNEL = 1;
	private final int TAST_CODE_REFRESH_NEWS = 2;
	private final int TAST_CODE_REFRESH__CHANNEL = 3;
	private final int TAST_CODE_REFRESH__CHANNEL_TIME = 4;
	private final int TAST_CODE_LOAD_DATA = 5;
	
	private View mRootView;
	private CommonListView mCommonLv;
	private SlidePicView mSlidePicView;
	private DisplayImageOptions mImageLoaderOptions;
	private Future<?> mRefreshFuture;
	private Future<?> mLoadFuture;
	private Future<?> mCheckOutofDateFuture;
	private SimpleDateFormat mDateFormat;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mRootView = inflater.inflate(R.layout.frg_channel, null);
		Log.d("newsfragment","onCreateView");
		
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
		return mRootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		if(mChannel==null){
			AsyTaskExecutor.getInstance().startTask(TAST_CODE_REQUEST_CHANNEL, this, new String[]{mChannelCode});
		}else{
			init();
		}
		
	}
	
	private void init(){
		if(mTitleBar!=null&&TextUtils.isEmpty(mTitleBar.getTitle())){
			mTitleBar.setTitle(mChannel.getName());
		}
		mCurPage = 1;
		Drawable drawable = Drawable.createFromPath(mHostActivity.getFilesDir().getAbsolutePath()+"/stance.png");
		mImageLoaderOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(drawable)
		.showImageForEmptyUri(drawable)
		.showImageOnFail(drawable)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.denyNetworkDownload(MportalApplication.systemState.getNetworkState()!=ConnectivityManager.TYPE_WIFI&&!MportalApplication.systemSettings.isAllowDowPicUse2G())
		.build();
		
		mCommonLv = (CommonListView) mRootView.findViewById(R.id.channel_lv);
		
		mCommonLv.setPullLoadEnable(true);
		mCommonLv.setCommonListViewListener(new CommonListViewListener() {
			
			@Override
			public void onRefresh() {
				Log.v("ChannelFragment"," 刷新");
				mCurPage = 1;
				ChannelDefaultFragment.this.onRefresh();
			}
			
			@Override
			public void onLoadMore() {
				Log.v("ChannelFragment"," 下一页");
				load();
				
			}
		});
		
		mCommonLv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				NewsInfo ni  = (NewsInfo) parent.getAdapter().getItem(position);
				startInfoActivity(ni);

			}
		});
		
		Date lastUpdateTime = mChannel.getLastUpdateTime();
		final Date lastLocalUpdateTime = mChannel.getLocalLastupdateTime();
		if(lastLocalUpdateTime==null||lastLocalUpdateTime.before(lastUpdateTime)){
			mNewsInfoList = new ArrayList<NewsInfo>();
			mAdapter = new MyListAdapter();
			mCommonLv.setAdapter(mAdapter);
			refresh();
		}else{
			loadHeader();
			load();
		}
		
	}

	private void loadHeader() {
		LogM.log(this.getClass(), "loadHeader");
		final List<HeadPic> infoList = mNewsBussiness.getNewsPopulation(mChannelCode);
		if(mChannel.getShowType()!=NewsChannel.SHOWTYPE_HEADLINE||mChannel.getFocusPicNum()==0||infoList.size()==0){
			LogM.log(this.getClass(), "没有推广图");
			if(mSlidePicView!=null){
				
				mCommonLv.removeHeaderView(mSlidePicView);
			}
			mSlidePicView = null;
			return;
		}
		
		LogM.log(this.getClass(), "onDone 加载推广完成:"+infoList.size());
		List<SlidePicItem> list = new ArrayList<SlidePicItem>();
		for(HeadPic hp:infoList){
			SlidePicItem sp = new SlidePicItem();
			sp.picURL = hp.getPicURL();
			sp.title = hp.getTopic();
			sp.linkType = NewsInfo.NEWS_TYPE_NORAML;
			list.add(sp);
		}
		if(mSlidePicView==null){
			
			mSlidePicView = new SlidePicView(mHostActivity);
			mSlidePicView.setTitleTextSize(getResources().getDimension(R.dimen.slide_pic_title_text_size));
			mSlidePicView.setData(list);
			mSlidePicView.setOnItemClickListener(new SlidePicView.OnItemClickListener() {
				
				@Override
				public void onClick(int pos,SlidePicItem item) {
					startNewsInfoActivity(infoList.get(pos).getInfoid(), mChannelCode);
				}
			});
			mSlidePicView.setBackgroundColor(0xffffffff);
			mCommonLv.addHeaderView(mSlidePicView);
		}else{
			mSlidePicView.setData(list);
		}
		
		
	}
	
	public void refresh(){
		mCommonLv.refresh();
	}
	private void onRefresh(){
		
		AsyTaskExecutor.getInstance().startTask(TAST_CODE_REFRESH__CHANNEL, this, null);
	}
	
	
	private void load() {
		AsyTaskExecutor.getInstance().startTask(TAST_CODE_LOAD_DATA, this, null);
		// 首先检测服务端和客户端的更新时间，如果客户端更新时间为空或者客户端更新时间在服务器更新之前则刷新
	}
	
	private class MyListAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return mNewsInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return mNewsInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			NewsInfo info = mNewsInfoList.get(position);
			ViewHolder holder = null; 
			if(convertView==null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mHostActivity).inflate(R.layout.item_news_list, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.news_iv);
				holder.titleTv = (TextView) convertView.findViewById(R.id.news_title_tv);
				holder.tagTv = (TextView) convertView.findViewById(R.id.news_tag_tv);
				holder.commentTv = (TextView) convertView.findViewById(R.id.news_commentnum_tv);
				holder.pubtimeTv = (TextView) convertView.findViewById(R.id.news_pubtime_tv);

				GradientDrawable gd = new GradientDrawable();
		        gd.setStroke(1, 0xFF000000);
				holder.tagTv.setBackgroundDrawable(gd);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.titleTv.setText(info.getTitle());
			String picURL =  info.getPicURL();
			String tag =  info.getTag();
			if(picURL!=null&&!picURL.equals("")&&!picURL.equals("null")){
				
				holder.iv.setVisibility(View.VISIBLE);
				mImageLoader.displayImage(picURL, holder.iv,mImageLoaderOptions);
			}else{
				holder.iv.setVisibility(View.GONE);
			}
			if(!TextUtils.isEmpty(info.getTag())){
				holder.tagTv.setText(tag);
			}else{
				holder.tagTv.setVisibility(View.GONE);
			}
			
			int commentNum = info.getCommentNum();
			
			if(commentNum>0){
				holder.commentTv.setVisibility(View.VISIBLE);
				holder.commentTv.setText(commentNum+"评");
				
			}else{
				holder.commentTv.setVisibility(View.GONE);
				
			}
			
			holder.pubtimeTv.setText(mDateFormat.format(info.getPubTime()));
			Log.v("ChannelFragment","position"+position+"title:"+info.getTitle());
			return convertView;
		}
		

	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		mSlidePicView = null;
		mCommonLv = null;
	}
	private class ViewHolder{
		public ImageView iv;
		private TextView titleTv,tagTv,commentTv,pubtimeTv;
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if(mRefreshFuture!=null)
			mRefreshFuture.cancel(true);
		if(mLoadFuture!=null)
			mLoadFuture.cancel(true);
		if(mCheckOutofDateFuture!=null){
			mCheckOutofDateFuture.cancel(true);
		}
	}
	
	@Override
	public void onClick(View v) {
		
	}

	@Override
	public Object onExecute(Integer tag, String[] params) throws Exception {
		Object obj = null;
		if(tag==TAST_CODE_REQUEST_CHANNEL){
			String url = String.format(URLs.URL_CHANNEL, params[0]);
			String result = WebUtils.requestWithGet(url);
			JSONResult jr = JSONResult.compile(result);
			obj = GsonUtils.getGson().fromJson(jr.result, NewsChannel.class);
		}else if(tag==TAST_CODE_REFRESH_NEWS){
			// 如果page==0，刷新数据库信息
			String url = URLs.URL_NEWS_LIST_OF_CHANNEL + "&channelcode=" + mChannelCode
					+ "&pno=1&pernum=" + 10;

			List<NewsInfo> infoList = WebUtils.requestList(url, NewsInfo.class, "info");

			SugarRecord.deleteAll(NewsInfo.class, "CHANNEL_CODE=?", mChannelCode);
			Date curDate = new Date();
			SugarRecord.update(NewsChannel.class, "LOCAL_LAST_UPDATE_TIME", curDate.getTime() + "",
					"CODE = ?", new String[] { mChannelCode });
			mNewsBussiness.saveList(infoList, mChannelCode);
			
		}else if(tag==TAST_CODE_REFRESH__CHANNEL){
			SugarRecord.deleteAll(NewsInfo.class, "CHANNEL_CODE=?", mChannelCode);
			SugarRecord.deleteAll(HeadPic.class,"CHANNEL_CODE=?",mChannelCode);
			// 刷新推广信息
			NewsChannel newsChannel = SugarRecord.findByProperty(NewsChannel.class, "CODE", mChannelCode);
			int showType = newsChannel.getShowType();
			if (showType == NewsChannel.SHOWTYPE_HEADLINE) {

				String url = URLs.URL_HEAD_PIC + "&channelcode=" + mChannelCode + "&webappcode="
						+ MportalApplication.app.getWebAppCode();
				List<HeadPic> hList = WebUtils.requestList(url, HeadPic.class, "tgpic");

				for (HeadPic hp : hList) {
					hp.setChannelCode(mChannelCode);
					hp.save();
				}

			}
			String url = URLs.URL_NEWS_LIST_OF_CHANNEL + "&channelcode=" + mChannelCode + "&pno=1&pernum="
					+ URLs.PAGE_SIZE;

			List<NewsInfo> infoList = WebUtils.requestList(url, NewsInfo.class, "info");

			mNewsBussiness.saveList(infoList, mChannelCode);
			if (newsChannel.getLastUpdateTime() != null) {

				SugarRecord.update(NewsChannel.class, "LOCAL_LAST_UPDATE_TIME", newsChannel.getLastUpdateTime()
						.getTime() + "", "CODE = ?", new String[] { mChannelCode });
			}
		}else if(tag==TAST_CODE_REFRESH__CHANNEL_TIME){
			String url = URLs.URL_CHANNEL_UPDATE_TIME + "&channelcode=" + mChannelCode;
			JSONObject jo = new JSONObject(WebUtils.requestWithGet(url));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
			Date date = sdf.parse(jo.getString("lastupdatetime"));
			SugarRecord.update(NewsChannel.class, "LAST_UPDATE_TIME", date.getTime() + "", "CODE = ?",
					new String[] { mChannelCode });
			obj = date;
		}else if(tag==TAST_CODE_LOAD_DATA){
			// 先从数据库获取数据
			List<NewsInfo> infoList = null;
			if (mCurPage == 0) {

					// 如果page==0，刷新数据库信息
					String url = URLs.URL_NEWS_LIST_OF_CHANNEL + "&channelcode=" + mChannelCode
							+ "&pno=1&pernum=" + URLs.PAGE_SIZE;

					infoList = WebUtils.requestList(url, NewsInfo.class, "info");
					Date curDate = new Date();
					SugarRecord.update(NewsChannel.class, "LOCAL_LAST_UPDATE_TIME", curDate.getTime() + "",
							"CODE = ?", new String[] { mChannelCode });
					if(infoList!=null&&infoList.size()>0){
						
						SugarRecord.deleteAll(NewsInfo.class, "CHANNEL_CODE=?", mChannelCode);
						mNewsBussiness.saveList(infoList, mChannelCode);
					}
				
			} else if (mCurPage == 1) {
				infoList = SugarRecord.find(NewsInfo.class, "CHANNEL_CODE = ?", new String[] { mChannelCode },
						null, "PUB_TIME desc", (mCurPage - 1) * URLs.PAGE_SIZE + "," + URLs.PAGE_SIZE);
				LogM.log(NewsBussiness.class, "从数据库中查询出的数据条数："+infoList.size());
			} else {

				infoList = SugarRecord.find(NewsInfo.class, "CHANNEL_CODE = ? ", new String[] { mChannelCode },
						null, "PUB_TIME desc", (mCurPage - 1) * URLs.PAGE_SIZE + "," + URLs.PAGE_SIZE);
				if (infoList.size() != URLs.PAGE_SIZE) {

					String url = URLs.URL_NEWS_LIST_OF_CHANNEL + "&channelcode=" + mChannelCode + "&pno=" + mCurPage
							+ "&pernum=" + URLs.PAGE_SIZE;
					infoList = WebUtils.requestList(url, NewsInfo.class, "info");
					mNewsBussiness.saveList(infoList, mChannelCode);
				} 
			}
			LogM.log(NewsBussiness.class, "准备返回到界面："+infoList.size());
			obj = infoList;
		}
		return obj;
	}

	@Override
	public void onTaskSuccess(Integer tag, Object obj) {
		if(tag==TAST_CODE_REQUEST_CHANNEL){
			mChannel = (NewsChannel) obj;
			SugarRecord.deleteAll(NewsInfo.class, "CHANNEL_CODE=?", mChannel.getCode());
			mChannel.save();
			init();
		}else if(tag==TAST_CODE_REFRESH_NEWS){
			
		}else if(tag==TAST_CODE_REFRESH__CHANNEL){
			if(!isAdded())
				return;
			LogM.log(this.getClass(), "刷新完成返回界面 ");
			loadHeader();
			load();
			if(mCommonLv!=null){
				mCommonLv.stopRefresh();
			}
		}else if(tag==TAST_CODE_REFRESH__CHANNEL_TIME){
			Date date = (Date) obj;
			LogM.log(this.getClass(), "查询更新时间完毕："+date.toString());
			if(mChannel.getLastUpdateTime()==null||mChannel.getLastUpdateTime().before(date)){
				LogM.log(this.getClass(), "本地信息已过期：最新更新时间："+date.toString()+"本地时间：");
				if(!mCommonLv.isRefreshing()){
					refresh();
				}
			}
		}else if(tag==TAST_CODE_LOAD_DATA){
			LogM.log(this.getClass(), "获得到数据：");
			List<NewsInfo> list = (List<NewsInfo>) obj;
//			if (!ChannelDefaultFragment.this.isVisible())
//				return;
			LogM.log(this.getClass(), "返回成功 size:" + list.size() + "mCurPage:" + mCurPage);
			if (mCurPage == 1) {
				mNewsInfoList = list;
				mAdapter = new MyListAdapter();
				mCommonLv.setAdapter(mAdapter);
				mCurPage++;
				
			} else {
				mNewsInfoList.addAll(list);
				mAdapter.notifyDataSetChanged();
				mCommonLv.stopLoadMore();
				mCurPage++;
			}
			if (list.size() < URLs.PAGE_SIZE) {
				mCommonLv.haveLoadAll();
			}
		}

	}

	@Override
	public void onTaskFail(Integer tag, Exception e) {
		Toast.makeText(mContext, "服务器异常", Toast.LENGTH_SHORT).show();
		if(mCommonLv!=null){
			mCommonLv.stopRefresh();
			mCommonLv.stopLoadMore();
		}
	}

}

