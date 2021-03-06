package com.apppubs.ui.news;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apppubs.model.NewsBiz;
import com.apppubs.presenter.ChannelsSlidePresenter;
import com.apppubs.ui.home.HomeSlideMenuActivity;
import com.apppubs.ui.adapter.NewsFragmentPagerAdapter;
import com.apppubs.bean.TNewsChannel;
import com.apppubs.util.LogM;
import com.apppubs.ui.widget.AlternativeChannelLayout;
import com.apppubs.ui.widget.NewsViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.apppubs.d20.R;
import com.apppubs.ui.widget.DraggableGridView;
import com.apppubs.ui.widget.DraggableGridView.OnRearrangeListener;
import com.apppubs.ui.widget.ScrollTabs;
import com.apppubs.ui.widget.ScrollTabs.OnColunmBtnClickListener;
import com.apppubs.ui.widget.ScrollTabs.OnItemClickListener;

/**
 * 资讯列表容器,需要传入频道所属的类别
 *
 * @author Administrator
 */
public class ChannelsSlideFragment extends ChannelsFragment implements OnPageChangeListener,
		IChannelsSlideView {


    private ScrollTabs mChannelTabs;
    private NewsViewPager mViewPager;
    private int mCurPos;
    private List<ChannelFragment> mFragmentList;
    private LinearLayout mColumnLl;//栏目选择
    private boolean channelChanged;//已选择频道结构是否改变
    private DraggableGridView mChannelSelDg;
    private AlternativeChannelLayout mAlterChannelL;
    private ChannelsSlidePresenter mPresenter;

    private SlidingMenu mSm;
    private NewsBiz mNb;
    private NewsFragmentPagerAdapter mFragmentAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ChannelsSlidePresenter(mContext, this, mChannelTypeId);
    }

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState) {

        mRootView = inflater.inflate(R.layout.frg_channels_slide, null);

        initComponent();

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        if (mHostActivity instanceof HomeSlideMenuActivity) {
            mSm = ((HomeSlideMenuActivity) mHostActivity).getSlidingMenu();
        }
//		init();
        mPresenter.onCreateView();

    }

    private void initComponent() {
        mChannelTabs = (ScrollTabs) mRootView.findViewById(R.id.channels_ct);
        mViewPager = (NewsViewPager) mRootView.findViewById(R.id.channels_vp);
        mViewPager.setOnPageChangeListener(this);
        mChannelTabs.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onclick(int pos) {
                mViewPager.setCurrentItem(pos, false);
            }
        });

        mChannelTabs.setOnColumnBtnClickListener(new OnColunmBtnClickListener() {

            @Override
            public void onClick(boolean isOpen) {
                openOrClose(isOpen);

            }
        });

        mChannelSelDg = (DraggableGridView) mRootView.findViewById(R.id.channels_dgv);
        mAlterChannelL = (AlternativeChannelLayout) mRootView.findViewById(R.id.channels_alter_acl);
        mNb = NewsBiz.getInstance(mContext);
        mColumnLl = (LinearLayout) mRootView.findViewById(R.id.channels_column_fl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mChannelTabs.isSelectMode()) {
            openOrClose(false);
            mChannelTabs.openOrCloseSelectMode(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void openOrClose(boolean isOpen) {
        Animation showAnim = AnimationUtils.loadAnimation(mHostActivity, R.anim.slide_in_from_top);
        Animation hideAnim = AnimationUtils.loadAnimation(mHostActivity, R.anim.slide_out_to_top);
        if (isOpen) {
            if (mSm != null)
                mSm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            mColumnLl.setVisibility(ViewGroup.VISIBLE);
            mColumnLl.startAnimation(showAnim);
        } else {
            if (mSm != null)
                mSm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
            mColumnLl.startAnimation(hideAnim);
            mColumnLl.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mColumnLl.setVisibility(ViewGroup.GONE);
                    //重新加载
                    if (channelChanged) {

                        refresh();
                    }
                    channelChanged = false;
                }
            }, hideAnim.getDuration());
        }
    }

    private void init() {
        mChannelTabs.setIsAllowConfiguration(mAllowConfig);
        //全部栏目
        mFragmentList = new ArrayList<ChannelFragment>();
        for (TNewsChannel nc : mChannelList) {

            //初始化栏目
            LayoutInflater li = LayoutInflater.from(mHostActivity);
            TextView tv = (TextView) li.inflate(R.layout.item_channel, null);
            tv.setTag(nc.getCode());
            tv.setText(nc.getName());
            if (nc.getDisplayOrder() != 0) {
                Log.v("ChannelsFrag", "增加到已选");
                mChannelTabs.addTab(nc.getName());
                mChannelSelDg.addView(tv);
                LogM.log(this.getClass(), "nc.getShowType()" + nc.getShowType());
                ChannelFragment f = ChannelFragmentFactory.getChannelFragment(nc.getShowType());
                Bundle b = new Bundle();
                b.putSerializable(ChannelFragment.ARG_KEY, nc.getCode());
                f.setArguments(b);
                mFragmentList.add(f);
            } else {
                Log.v("ChannelsFrag", "增加到为选");
                mAlterChannelL.addView(tv);
            }

            mChannelSelDg.setOnRearrangeListener(new OnRearrangeListener() {

                @Override
                public void onRearrange(int oldIndex, int newIndex) {
                    if (oldIndex == newIndex) return;
                    Log.v("ChannelsF", "onRearrange oldIndex:" + oldIndex + "new Index:" +
							newIndex);
                    mNb.rerangeChannelIndex(mChannelTypeId, oldIndex, newIndex);
                    channelChanged = true;
                }

                @Override
                public void onRemove(View view, int index) {
                    mNb.removeChannel(mChannelTypeId, (String) view.getTag());
                    channelChanged = true;
                }

                @Override
                public void onAdd() {
                }
            });

        }

        mAlterChannelL.setOnRemoveListener(new AlternativeChannelLayout.OnRemoveListener() {

            @Override
            public void onRemove(View view, int index) {
                //此处移除，已选栏目增加
                mNb.addChannel(mChannelTypeId + "", view.getTag().toString());
                channelChanged = true;
            }
        });
        mChannelSelDg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View view, int pos, long id) {
                mChannelSelDg.removeViewAt(pos);
                TextView tv = (TextView) view;
                tv.setGravity(Gravity.CENTER);
                mAlterChannelL.addView(view);
            }
        });
        mAlterChannelL.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mAlterChannelL.removeViewAt(position);
                mChannelSelDg.addView(view);
            }
        });

//		mChannelTabs.setCurrentTab(mCurPos);
//		mRootView.removeCallbacks(mRefreshTask);
//		mRootView.postDelayed(mRefreshTask, 1000);


        mFragmentAdapter = new NewsFragmentPagerAdapter(getChildFragmentManager());
        mFragmentAdapter.setData(mFragmentList);
        mViewPager.setAdapter(mFragmentAdapter);
        mColumnLl = (LinearLayout) mRootView.findViewById(R.id.channels_column_fl);

//		mSm.addIgnoredView(mColumnLl);
    }

    private void refresh() {
        LogM.log(this.getClass(), "刷新已选频道");
        mChannelTabs.removeAll();
        mFragmentList.clear();
        refreshChannelList();
        refreshSelectedList();
        for (TNewsChannel nc : mChannelSelectedList) {
            mChannelTabs.addTab(nc.getName());

            ChannelFragment f = ChannelFragmentFactory.getChannelFragment(nc.getShowType());
            Bundle b = new Bundle();
            b.putString(ChannelFragment.ARG_KEY, nc.getCode());
            f.setArguments(b);
            mFragmentList.add(f);
        }
        mFragmentAdapter.setData(mFragmentList);
        mFragmentAdapter.notifyDataSetChanged();
        LogM.log(this.getClass(), "刷新已选频道 完毕 当前fragment数量:" + mFragmentList.size());
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 0) {
            mChannelTabs.setCurrentTab(mCurPos);
            LogM.log(this.getClass(), "onPageScrollStateChanged 0 mCurPos" + mCurPos);
        }
    }


    @Override
    public void onPageScrolled(int position, float offset, int offsetPixel) {
        mChannelTabs.onPageScrolled(position, offset);

    }


    @Override
    public void onPageSelected(int position) {
        mCurPos = position;
    }

    @Override
    public void setSelectedChannels(List<TNewsChannel> channels) {

        mFragmentList = new ArrayList<ChannelFragment>();
        for (TNewsChannel channel : channels) {
			mChannelTabs.addTab(channel.getName());
            LogM.log(this.getClass(), "nc.getShowType()" + channel.getShowType());
            ChannelFragment f = ChannelFragmentFactory.getChannelFragment(channel.getShowType());
            Bundle b = new Bundle();
            b.putSerializable(ChannelFragment.ARG_KEY, channel.getCode());
            f.setArguments(b);
            mFragmentList.add(f);
        }

        mFragmentAdapter = new NewsFragmentPagerAdapter(getChildFragmentManager());
        mFragmentAdapter.setData(mFragmentList);
        mViewPager.setAdapter(mFragmentAdapter);
    }


}
