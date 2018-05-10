package com.apppubs.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.apppubs.AppContext;
import com.apppubs.bean.http.MyServiceNOsResult;
import com.apppubs.constant.Actions;
import com.apppubs.d20.R;
import com.apppubs.model.MsgController;
import com.apppubs.presenter.ServiceNOPresenter;
import com.apppubs.ui.ICommonListView;
import com.apppubs.ui.activity.ChatNewGroupChatOrAddUserActivity;
import com.apppubs.ui.activity.ContainerActivity;
import com.apppubs.ui.activity.ServiceNOArticlesActivity;
import com.apppubs.ui.widget.TitleBar;
import com.apppubs.util.StringUtils;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 服务号界面
 * <p>
 * Copyright (c) heaven Inc.
 * <p>
 * Original Author: zhangwen
 * <p>
 * ChangeLog: 2015年3月18日 by zhangwen create
 */
public class ServiceNOsOfMineFragment extends TitleBarFragment implements OnClickListener,
        ICommonListView<MyServiceNOsResult.MyServiceNOItem> {

    private ListView mLv;
    private MyAdapter mAdapter;
    private List<MyServiceNOsResult.MyServiceNOItem> mDatas;
    private PopupWindow mMenuPW;
    private DisplayImageOptions mDisplayImageOptions;
    private SimpleDateFormat mSimpleDateFormat;
    private BroadcastReceiver mRefreshBR;
    private Date mCurResponseTime;

    private ServiceNOPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        mPresenter = new ServiceNOPresenter(mContext, this);
    }

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mDisplayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.user)
                .showImageForEmptyUri(R.drawable.user).showImageOnFail(R.drawable.user)
                .cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        initRootView();
        mRefreshBR = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
            }
        };
        return mRootView;
    }

    private void initRootView() {
        FrameLayout rootView = new FrameLayout(mContext);
        mLv = new ListView(mContext);
        mLv.setDivider(null);
        mLv.setSelector(R.drawable.sel_common_item);
        rootView.addView(mLv);
        mRootView = rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.onVisiable();
        } else {
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        MsgController.getInstance(mContext).setMsgListVisiable(true);
        mHostActivity.registerReceiver(mRefreshBR, new IntentFilter(Actions
                .ACTION_REFRESH_CHAT_RECORD_LIST));

        mPresenter.onVisiable();
    }

    @Override
    public void onStop() {
        super.onStop();
        mHostActivity.unregisterReceiver(mRefreshBR);
    }


    @Override
    public void onPause() {

        super.onPause();
    }


    @Override
    public void setDatas(List<MyServiceNOsResult.MyServiceNOItem> datas) {
        mDatas = datas;
        if (mAdapter == null) {

            mAdapter = new MyAdapter();
            mLv.setAdapter(mAdapter);
            mLv.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mHostActivity,ServiceNOArticlesActivity.class);
                    MyServiceNOsResult.MyServiceNOItem item = mDatas.get(position);
                    Bundle b = new Bundle();
                    b.putString(ServiceNOArticlesFragment.ARGS_STRING_SERVICE_NO_ID, item.getId());
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
            mLv.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> adapter, View view, int pos, long
                        id) {
                    System.out.println("长按");
                    return true;
                }
            });
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    private class MyAdapter extends BaseSwipeAdapter {

        SimpleDateFormat sdf;

        public MyAdapter() {
            sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        }

        @Override
        public int getCount() {

            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {

            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public void fillValues(final int pos, View convertView) {
            MyServiceNOsResult.MyServiceNOItem item = mDatas.get(pos);
            TextView titleTv = titleTv = (TextView) convertView.findViewById(R.id
                    .message_item_title_tv1);
            TextView subTitleTv = (TextView) convertView.findViewById(R.id.message_item_des_tv1);
            ImageView iconIv = (ImageView) convertView.findViewById(R.id.message_item_iv1);
            TextView updateTimeTv = (TextView) convertView.findViewById(R.id.message_item_time_tv1);
            TextView unreadTv = (TextView) convertView.findViewById(R.id
                    .msg_record_item_unread_tv1);

            convertView.findViewById(R.id.swipe).setVisibility(View.GONE);
            convertView.findViewById(R.id.swipe1).setVisibility(View.VISIBLE);

            titleTv.setText(item.getName());
            subTitleTv.setText(item.getDesc());
            mImageLoader.displayImage(item.getPicURL(), iconIv, mDisplayImageOptions);
            updateTimeTv.setText(StringUtils.getFormattedTime(item.getCreateTime(),
                    mCurResponseTime == null ? new Date() : mCurResponseTime));
            if (item.getUnreadCount() > 0) {
                unreadTv.setVisibility(View.VISIBLE);
                unreadTv.setText(item.getUnreadCount() + "");
            } else {
                unreadTv.setVisibility(View.GONE);
            }
        }

        @Override
        public View generateView(final int pos, ViewGroup parent) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_msg_record_lv, null);
            return v;
        }

        @Override
        public int getSwipeLayoutResourceId(int arg0) {
            return R.id.swipe;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_msg_record_add_service_ll:
                ContainerActivity.startContainerActivity(mContext, ServiceNoSubscribeFragment.class, null,
                        "添加服务号");
//			ContainerActivity.startContainerActivity(mContext, ServiceNoListOfMineFragment.class, null,
// "我关注的服务号");
                mMenuPW.dismiss();
                break;
            case R.id.pop_msg_record_add_chat_ll:

                Log.e(this.getClass().getName(), "此处需要，增加AddressBookFragement的参数");
                String[] userIds = new String[]{AppContext.getInstance(mContext).getCurrentUser()
                        .getUserId()};
//			UserPickerHelper.startContainerActivity(mContext,"选择人员",new ArrayList<String>(Arrays.asList
// (userIds)));
                mMenuPW.dismiss();
                break;
            case R.id.pop_msg_record_add_group_chat_ll:
                Intent chatNewGroupIntent = new Intent(mHostActivity,
                        ChatNewGroupChatOrAddUserActivity.class);
                chatNewGroupIntent.putExtra(ChatNewGroupChatOrAddUserActivity
                        .EXTRA_PRESELECTED_USERNAME_LIST, AppContext.getInstance(mContext)
                        .getCurrentUser().getUsername());
                startActivity(chatNewGroupIntent);
                mMenuPW.dismiss();
                break;
            default:
                super.onClick(v);
        }
    }

}
