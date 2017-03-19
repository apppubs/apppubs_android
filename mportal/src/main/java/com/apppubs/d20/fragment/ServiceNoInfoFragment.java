package com.apppubs.d20.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

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

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apppubs.d20.AppContext;
import com.apppubs.d20.util.DateUtils;
import com.apppubs.d20.R;
import com.apppubs.d20.activity.ContainerActivity;
import com.apppubs.d20.bean.ServiceNo;
import com.apppubs.d20.business.BussinessCallbackCommon;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.util.JSONResult;

/**
 * 服务号详情
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年4月1日 by zhangwen create
 * 
 */
public class ServiceNoInfoFragment extends BaseFragment {

	public static final String ARGS_STRING_SERVICE_NO_ID = "service_no_id";

	private String mServiceNoId;
	private ServiceNo mServiceNo;
	private Button attentionBt;
	private LinearLayout progressBar;
	private boolean isSubscribed;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mRootView = mInflater.inflate(R.layout.frg_serviceno_info, null);
		Bundle args = getArguments();
		mServiceNoId = args.getString(ARGS_STRING_SERVICE_NO_ID);
		progressBar = (LinearLayout) mRootView.findViewById(R.id.service_no_progress_ll);
		attentionBt = (Button) mRootView.findViewById(R.id.service_no_attention);
//		mServiceNo = mMsgBussiness.getServiceNoById(mServiceNoId);
		registerClickListener();
		return mRootView;
	}


	@Override
	public void onResume() {
		super.onResume();
		String url = String.format(URLs.URL_SERVICE_NO, mServiceNoId, AppContext.getInstance(mContext).getCurrentUser().getUsername());
		mRequestQueue.add(new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				JSONResult jr = JSONResult.compile(response);
				mServiceNo = new ServiceNo();
				Map<String, String> resultMap = null;
				try {
					resultMap = jr.getResultMap();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
					mServiceNo.setName(resultMap.get("service_name"));
					mServiceNo.setPicURL(resultMap.get("service_picurl"));
					mServiceNo.setDesc(resultMap.get("service_desc"));
					mServiceNo.setCreateDate(sdf.parse(resultMap.get("service_thedate")));
					mServiceNo.setType(Integer.parseInt(resultMap.get("service_flag")));
					mServiceNo.setReceiverType(Integer.parseInt(resultMap.get("receive_flag")));
					fillTextView();
					if (mServiceNo.isAllowSubscribe()) {
						setVisibilityOfViewByResId(mRootView, R.id.service_no_attention, View.VISIBLE);
					}
					
					isSubscribed = resultMap.get("service_iforder").equals("1");
					if (isSubscribed) {
						attentionBt.setText("取消关注");
					} else {
						attentionBt.setText("关注");
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				
			}
		}));
	}


	private void registerClickListener() {
		RelativeLayout seehistory = (RelativeLayout) mRootView.findViewById(R.id.service_no_history_rl);
		seehistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Bundle b = new Bundle();
				b.putString(ServiceNoInfoFragment.ARGS_STRING_SERVICE_NO_ID, mServiceNoId);
				ContainerActivity.startActivity(mHostActivity, ServiceNoInfoListFragement.class, b, "服务号历史消息");
			}
		});

		attentionBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				progressBar.setVisibility(View.VISIBLE);
				if (attentionBt.getText().equals("关注")) {
					mMsgBussiness.getServiceAttention(mServiceNoId, AppContext.getInstance(mContext).getCurrentUser().getUsername(),
							new BussinessCallbackCommon<String>() {

								@Override
								public void onException(int excepCode) {
								}

								@Override
								public void onDone(String obj) {
									progressBar.setVisibility(View.GONE);
									/**
									 * "result": "1"} rest:0失败，1成功，2已经关注
									 */
									System.out.println("关注过此公众。。。" + obj);
									if (obj.equals("0")) {
										Toast.makeText(getActivity(), "关注失败！", Toast.LENGTH_SHORT).show();
									} else if (obj.equals("1")) {
										attentionBt.setText("取消关注");
									} else if (obj.equals("2")) {
										attentionBt.setText("取消关注");
										Toast.makeText(getActivity(), "您已经关注过此公众号！", Toast.LENGTH_SHORT).show();
									}
								}
							});
				} else {// 取消关注
					mMsgBussiness.getServiceUnAttention(mServiceNoId, AppContext.getInstance(mContext).getCurrentUser().getUsername(),
							new BussinessCallbackCommon<String>() {

								@Override
								public void onException(int excepCode) {
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

	}

	private void fillTextView() {
		fillTextView(mRootView, R.id.service_no_name_tv, mServiceNo.getName());
		fillTextView(mRootView, R.id.service_no_des_tv, mServiceNo.getDesc());
		String time = DateUtils.dateToStrLong(mServiceNo.getCreateDate());
		fillTextView(mRootView, R.id.service_no_createdate_tv, "创建时间："+time);
		fillImageView(mRootView, R.id.service_no_icon_iv, mServiceNo.getPicURL());
	}
}
