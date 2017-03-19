package com.apppubs.d20.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apppubs.d20.AppContext;
import com.apppubs.d20.bean.User;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.widget.ProgressHUD;
import com.apppubs.d20.R;

public class ChangePasswordActivity extends BaseActivity implements OnClickListener {

	private EditText mOriginalEt, mNewEt, mNewRepeatEt;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.act_change_password);
		setTitle("修改密码");

		mOriginalEt = (EditText) findViewById(R.id.change_password_original_tv);
		mNewEt = (EditText) findViewById(R.id.change_password_new_tv);
		mNewRepeatEt = (EditText) findViewById(R.id.change_password_new_repeat_tv);

		mTitleBar.setRightBtnWithText("保存");
		mTitleBar.setRightBtnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.titlebar_right_btn) {
			if (mOriginalEt.getText().toString().trim().isEmpty()) {
				Toast.makeText(this, "请输入原密码", Toast.LENGTH_SHORT).show();
			} else if (mNewEt.getText().toString().trim().isEmpty()) {
				Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
			} else if (mNewEt.getText().toString().trim().length() > 20) {
				Toast.makeText(this, "新密码过长", Toast.LENGTH_SHORT).show();
			} else if (mNewRepeatEt.getText().toString().trim().isEmpty()) {
				Toast.makeText(this, "请重复新密码", Toast.LENGTH_SHORT).show();
			} else if (!mNewEt.getText().toString().trim().equals(mNewRepeatEt.getText().toString().trim())) {
				Toast.makeText(this, "两次输入的新密码不相同", Toast.LENGTH_SHORT).show();
			} else if (mOriginalEt.getText().toString().trim().equals(mNewRepeatEt.getText().toString().trim())) {
				Toast.makeText(this, "新旧密码相同", Toast.LENGTH_SHORT).show();
			} else {
				ProgressHUD.show(this);
				User currentUser = AppContext.getInstance(mContext).getCurrentUser();
				String url = String.format(URLs.URL_MODIFY_PASSWORD, currentUser.getUserId(), mOriginalEt
						.getText().toString().trim(), mNewEt.getText().toString().trim());
				mRequestQueue.add(new StringRequest(url, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						ProgressHUD.dismissProgressHUDInThisContext(ChangePasswordActivity.this);
						JSONResult jr = JSONResult.compile(response);
						Toast.makeText(ChangePasswordActivity.this, jr.reason, Toast.LENGTH_SHORT).show();
						if (jr.resultCode == 1) {
							mOriginalEt.setText("");
							mNewEt.setText("");
							mNewRepeatEt.setText("");
						}
					}

				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						ProgressHUD.dismissProgressHUDInThisContext(ChangePasswordActivity.this);
						Toast.makeText(ChangePasswordActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
					}
				}));
			}
		}else if(v.getId()==R.id.titlebar_left_btn){
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
//				imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
				imm.hideSoftInputFromWindow(mOriginalEt.getWindowToken(), 0);
				
				
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		
	}

}
