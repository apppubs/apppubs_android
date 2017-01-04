package com.mportal.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mportal.client.R;
import com.mportal.client.constant.URLs;
import com.mportal.client.fragment.WebAppFragment;

/**
 * WebApp界面,打开此webapp需要传入url和title等信息
 * String
 */
public class WebAppActivity extends BaseActivity {

	public static final String EXTRA_NAME_MENUTYPE = "menubar_type";
	public static final String EXTRA_NAME_URL = "url";
	public static final String EXTRA_NAME_TITLE = "title";

	public static final String EXTRA_NAME_BOOL_NEED_CLEAR_SERVERCE_NO_UNREAD_NUM = "need_clear_service_no_unread_num";
	public static final String EXTRA_NAME_STRING_SERVICE_NO_INFO_ID = "service_no_info_id";
	private String mTitle;
	private WebAppFragment mFrg;
	private boolean isNeedClearServiceUnreadNum;
	private String mServiceNoInfoId;

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		setContentView(R.layout.act_webapp);

		init();
	}

	private void init() {

		Intent i = getIntent();
		mTitle = i.getStringExtra(EXTRA_NAME_TITLE);
		setTitle(mTitle);
		mFrg = new WebAppFragment();
		mFrg.setArguments(i.getExtras());
		isNeedClearServiceUnreadNum = i.getBooleanExtra(EXTRA_NAME_BOOL_NEED_CLEAR_SERVERCE_NO_UNREAD_NUM, false);
		mServiceNoInfoId = i.getStringExtra(EXTRA_NAME_STRING_SERVICE_NO_INFO_ID);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.webapp_frg, mFrg);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();

//		mTitleBar.setLeftBtnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if(!mFrg.goBack()){
//					finish();
//				}
//			}
//		});
		

		if(isNeedClearServiceUnreadNum){
			String url = String.format(URLs.URL_CLEAR_UNREAD_NUM_FOR_SINGLE_SERVICE_NO, mServiceNoInfoId);
			mRequestQueue.add(new StringRequest(url, new Listener<String>() {

				@Override
				public void onResponse(String arg0) {
					
				}
			},new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError arg0) {
					
				}
			}));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mFrg.changeActivityTitleView(mTitleBar);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(mFrg.onKeyDown(keyCode, event)){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void onClick(View v) {

		super.onClick(v);


	}
}
