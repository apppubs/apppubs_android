
package com.apppubs.ui.news;

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

import com.apppubs.MportalApplication;
import com.apppubs.bean.THeadPic;
import com.apppubs.bean.TNewsChannel;
import com.apppubs.bean.TNewsInfo;
import com.apppubs.d20.R;
import com.apppubs.presenter.ChannelDefaultPresenter;
import com.apppubs.ui.widget.SlidePicView;
import com.apppubs.ui.widget.commonlist.CommonListView;
import com.apppubs.ui.widget.commonlist.CommonListViewListener;
import com.apppubs.util.LogM;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Administrator
 * 新闻Fragment
 */
public class ChannelDefaultFragment extends ChannelFragment implements OnClickListener, IChannelDefaultView {

    public static final String ARGUMENT_SERIALIZABLE_NAME_CHANNEL = "channel";

    private View mRootView;
    private CommonListView mCommonLv;
    private SlidePicView mSlidePicView;
    private DisplayImageOptions mImageLoaderOptions;
    private SimpleDateFormat mDateFormat;
    private ChannelDefaultPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ChannelDefaultPresenter(mContext, this, mChannelCode);
    }

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState) {

        mRootView = inflater.inflate(R.layout.frg_channel, null);
        Log.d("newsfragment", "onCreateView");

        mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
//        if (mChannel == null) {
////            AsyTaskExecutor.getInstance().startTask(TAST_CODE_REQUEST_CHANNEL, this, new
////					String[]{mChannelCode});
//        } else {
//
//        }
        init();
        refresh();
    }

    private void init() {
        if (mTitleBar != null && TextUtils.isEmpty(mTitleBar.getTitle())) {
            mTitleBar.setTitle(mChannel.getName());
        }
        mCurPage = 1;
        Drawable drawable = Drawable.createFromPath(mHostActivity.getFilesDir().getAbsolutePath()
				+ "/stance.png");
        mImageLoaderOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(drawable)
                .showImageForEmptyUri(drawable)
                .showImageOnFail(drawable)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .denyNetworkDownload(MportalApplication.systemState.getNetworkState() !=
						ConnectivityManager.TYPE_WIFI && !mAppContext.getSettings()
						.isAllowDowPicUse2G())
                .build();

        mCommonLv = (CommonListView) mRootView.findViewById(R.id.channel_lv);
        mCommonLv.setCommonListViewListener(new CommonListViewListener() {

            @Override
            public void onRefresh() {
                Log.v("ChannelFragment", " 刷新");
                mPresenter.onRefreshClicked();

            }

            @Override
            public void onLoadMore() {
                Log.v("ChannelFragment", " 下一页");
                mPresenter.onLoadMoreClicked();

            }
        });
        mCommonLv.setPullLoadEnable(true);
        mCommonLv.setPullRefreshEnable(true);
        mCommonLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TNewsInfo ni = (TNewsInfo) parent.getAdapter().getItem(position);
                startInfoActivity(ni);

            }
        });
    }

    private void loadHeader() {
        LogM.log(this.getClass(), "loadHeader");
        final List<THeadPic> infoList = mNewsBiz.getNewsPopulation(mChannelCode);
        if (mChannel.getShowType() != TNewsChannel.SHOWTYPE_HEADLINE || mChannel.getFocusPicNum()
				== 0 || infoList.size() == 0) {
            LogM.log(this.getClass(), "没有推广图");
            if (mSlidePicView != null) {

                mCommonLv.removeHeaderView(mSlidePicView);
            }
            mSlidePicView = null;
            return;
        }

        LogM.log(this.getClass(), "onDone 加载推广完成:" + infoList.size());
        List<SlidePicView.SlidePicItem> list = new ArrayList<SlidePicView.SlidePicItem>();
        for (THeadPic hp : infoList) {
            SlidePicView.SlidePicItem sp = new SlidePicView.SlidePicItem();
            sp.picURL = hp.getPicURL();
            sp.title = hp.getTopic();
            sp.linkType = TNewsInfo.NEWS_TYPE_NORAML;
            list.add(sp);
        }
        if (mSlidePicView == null) {

            mSlidePicView = new SlidePicView(mHostActivity);
            mSlidePicView.setTitleTextSize(getResources().getDimension(R.dimen
					.slide_pic_title_text_size));
            mSlidePicView.setData(list);
            mSlidePicView.setOnItemClickListener(new SlidePicView.OnItemClickListener() {

                @Override
                public void onClick(int pos, SlidePicView.SlidePicItem item) {
                    startNewsInfoActivity(infoList.get(pos).getInfoid(), mChannelCode);
                }
            });
            mSlidePicView.setBackgroundColor(0xffffffff);
            mCommonLv.addHeaderView(mSlidePicView);
        } else {
            mSlidePicView.setData(list);
        }


    }

    public void refresh() {
        mCommonLv.refresh();
    }

    @Override
    public void stopRefresh() {
        mCommonLv.stopRefresh();
    }

    @Override
    public void stopLoadMore() {
        mCommonLv.stopLoadMore();
    }

    @Override
    public void haveLoadAll() {
        mCommonLv.haveLoadAll();
    }

    @Override
    public void setDatas(List<TNewsInfo> datas) {
        mNewsInfoList = datas;
        if (mAdapter == null) {
            mAdapter = new MyListAdapter();
            mCommonLv.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class MyListAdapter extends BaseAdapter {

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

            TNewsInfo info = mNewsInfoList.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mHostActivity).inflate(R.layout.item_news_list,
						null);
                holder.iv = (ImageView) convertView.findViewById(R.id.news_iv);
                holder.titleTv = (TextView) convertView.findViewById(R.id.news_title_tv);
                holder.tagTv = (TextView) convertView.findViewById(R.id.news_tag_tv);
                holder.commentTv = (TextView) convertView.findViewById(R.id.news_commentnum_tv);
                holder.pubtimeTv = (TextView) convertView.findViewById(R.id.news_pubtime_tv);

                GradientDrawable gd = new GradientDrawable();
                gd.setStroke(1, 0xFF000000);
                holder.tagTv.setBackgroundDrawable(gd);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.titleTv.setText(info.getTitle());
            String picURL = info.getPicURL();
            String tag = info.getTag();
            if (picURL != null && !picURL.equals("") && !picURL.equals("null")) {

                holder.iv.setVisibility(View.VISIBLE);
                mImageLoader.displayImage(picURL, holder.iv, mImageLoaderOptions);
            } else {
                holder.iv.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(info.getTag())) {
                holder.tagTv.setText(tag);
            } else {
                holder.tagTv.setVisibility(View.GONE);
            }

            int commentNum = info.getCommentNum();

            if (commentNum > 0) {
                holder.commentTv.setVisibility(View.VISIBLE);
                holder.commentTv.setText(commentNum + "评");

            } else {
                holder.commentTv.setVisibility(View.GONE);

            }

            holder.pubtimeTv.setText(mDateFormat.format(info.getPubTime()));
            return convertView;
        }

        private class ViewHolder {
            public ImageView iv;
            private TextView titleTv, tagTv, commentTv, pubtimeTv;
        }

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        mSlidePicView = null;
        mCommonLv = null;
    }

    @Override
    public void onClick(View v) {

    }
}

