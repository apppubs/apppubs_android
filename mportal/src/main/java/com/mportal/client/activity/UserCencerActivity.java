package com.mportal.client.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.bean.App;
import com.mportal.client.bean.User;
import com.mportal.client.constant.Constants;
import com.mportal.client.util.FileUtils;
import com.mportal.client.util.Utils;
import com.mportal.client.view.ConfirmDialog;
import com.mportal.client.view.LoadingDialog;

public class UserCencerActivity extends BaseActivity {
	/**
	 * 个人中心
	 * 
	 */
	private LoadingDialog dialog;
	private Button mMOdify;
	private boolean isHidden = true; // 密码的显示和隐藏
	private EditText mName, mEmail, mNicname, mPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_peoplecencer);
		init();
	}

	private void init() {

		setTitle("账号信息");

		mMOdify = (Button) findViewById(R.id.people_logout);
		mName = (EditText) findViewById(R.id.people_username);
		mEmail = (EditText) findViewById(R.id.people_email);
		mNicname = (EditText) findViewById(R.id.people_nicname);
		mPhone = (EditText) findViewById(R.id.people_tel);
		
		String appConfigStr = (String) FileUtils.readObj(this,Constants.FILE_NAME_APP_CONFIG);
		String flags = null;
		try {
			JSONObject jo = new JSONObject(appConfigStr);
			flags = jo.getString(Constants.APP_CONFIG_PARAM_USER_CENTER_PWD_FLAGS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String[] params = TextUtils.isEmpty(flags)?null:flags.split(",");
		
		if(params!=null&&params.length>0){
			if(params[0] .equals("1")){
				mTitleBar.setRightText("修改密码");
				mTitleBar.setRightBtnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(UserCencerActivity.this,ChangePasswordActivity.class);
						startActivity(intent);
					}
				});
			}
		}
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {

		case R.id.people_logout:
			
			new ConfirmDialog(UserCencerActivity.this,
					new ConfirmDialog.ConfirmListener() {

						@Override
						public void onOkClick() {
							
//							logout();
							mUserBussiness.logout(UserCencerActivity.this);
							UserCencerActivity.this.finish();
						}

						@Override
						public void onCancelClick() {

						}
					}, "确定注销登陆吗？", "取消", "注销").show();

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		User user = MportalApplication.user;
		mName.setText(user.getUsername());
		mEmail.setText(user.getEmail());
		mNicname.setText(user.getTrueName());
		mPhone.setText(user.getMobile());
	}

	@Override
	public void finish() {
		super.finish();
		// 关闭键盘
		Utils.colseInput(UserCencerActivity.this);
	}

	
}
