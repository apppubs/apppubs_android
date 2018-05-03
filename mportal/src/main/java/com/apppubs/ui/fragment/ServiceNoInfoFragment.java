package com.apppubs.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.apppubs.AppContext;
import com.apppubs.bean.http.ServiceNOInfoResult;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.presenter.ServiceNOInfoPresenter;
import com.apppubs.ui.ICommonDataView;
import com.apppubs.ui.activity.ServiceNOArticlesActivity;
import com.apppubs.util.DateUtils;
import com.apppubs.d20.R;
import com.apppubs.ui.activity.ContainerActivity;

/**
 * 服务号详情
 * <p>
 * Copyright (c) heaven Inc.
 * <p>
 * Original Author: zhangwen
 * <p>
 * ChangeLog: 2015年4月1日 by zhangwen create
 */
public class ServiceNoInfoFragment extends BaseFragment implements
		ICommonDataView<ServiceNOInfoResult> {

    public static final String ARGS_STRING_SERVICE_NO_ID = "service_no_id";

    private String mServiceNoId;
    private ServiceNOInfoResult mData;
    private Button attentionBt;
    private LinearLayout progressBar;
    private boolean isSubscribed;

    private ServiceNOInfoPresenter mPresenter;

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState) {
        mRootView = mInflater.inflate(R.layout.frg_serviceno_info, null);
        Bundle args = getArguments();
        mServiceNoId = args.getString(ARGS_STRING_SERVICE_NO_ID);
        progressBar = (LinearLayout) mRootView.findViewById(R.id.service_no_progress_ll);
        attentionBt = (Button) mRootView.findViewById(R.id.service_no_attention);
//		mServiceNo = mMsgBussiness.getServiceNoById(mServiceNoId);
        registerClickListener();

        mPresenter = new ServiceNOInfoPresenter(mContext, this, mServiceNoId);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void registerClickListener() {
        RelativeLayout seehistory = (RelativeLayout) mRootView.findViewById(R.id
				.service_no_history_rl);
        seehistory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Bundle b = new Bundle();
                b.putString(ServiceNoInfoFragment.ARGS_STRING_SERVICE_NO_ID, mServiceNoId);
                ServiceNOArticlesActivity.startActivity(mHostActivity, ServiceNOArticlesActivity.class, b);
            }
        });

        attentionBt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                progressBar.setVisibility(View.VISIBLE);
                if (attentionBt.getText().equals("关注")) {
                    mMsgBussiness.getServiceAttention(mServiceNoId, AppContext.getInstance
									(mContext).getCurrentUser().getUsername(),
                            new IAPCallback<String>() {

                                @Override
                                public void onException(APError excepCode) {
                                }

                                @Override
                                public void onDone(String obj) {
                                    progressBar.setVisibility(View.GONE);
                                    /**
                                     * "result": "1"} rest:0失败，1成功，2已经关注
                                     */
                                    System.out.println("关注过此公众。。。" + obj);
                                    if (obj.equals("0")) {
                                        Toast.makeText(getActivity(), "关注失败！", Toast
												.LENGTH_SHORT).show();
                                    } else if (obj.equals("1")) {
                                        attentionBt.setText("取消关注");
                                    } else if (obj.equals("2")) {
                                        attentionBt.setText("取消关注");
                                        Toast.makeText(getActivity(), "您已经关注过此公众号！", Toast
												.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {// 取消关注
                    mMsgBussiness.getServiceUnAttention(mServiceNoId, AppContext.getInstance
									(mContext).getCurrentUser().getUsername(),
                            new IAPCallback<String>() {

                                @Override
                                public void onException(APError excepCode) {
                                }

                                @Override
                                public void onDone(String obj) {
                                    progressBar.setVisibility(View.GONE);
                                    /**
                                     * {"result": "1"} result:0失败，1成功
                                     */
                                    if (obj.equals("0")) {
                                        Toast.makeText(getActivity(), "取消关注失败！", Toast.LENGTH_SHORT).show();
                                    } else if (obj.equals("1")) {
                                        attentionBt.setText("关注");
                                        Toast.makeText(getActivity(), "取消关注成功！", Toast.LENGTH_SHORT).show();
                                        mHostActivity.setResult(Activity.RESULT_OK);
                                        mHostActivity.finish();
                                    }
                                }
                            });

                }

            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.onCreate();
    }

    @Override
    public void setData(ServiceNOInfoResult data) {
        mData = data;

        fillTextView(mRootView, R.id.service_no_name_tv, data.getName());
        fillTextView(mRootView, R.id.service_no_des_tv, data.getDesc());
        String time = DateUtils.dateToStrLong(data.getCreateTime());
        fillTextView(mRootView, R.id.service_no_createdate_tv, "创建时间：" + time);
        fillImageView(mRootView, R.id.service_no_icon_iv, data.getPicURL());
    }
}
