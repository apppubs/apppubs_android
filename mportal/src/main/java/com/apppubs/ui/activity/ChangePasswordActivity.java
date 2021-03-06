package com.apppubs.ui.activity;

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
import com.apppubs.AppContext;
import com.apppubs.bean.UserInfo;
import com.apppubs.constant.APError;
import com.apppubs.constant.URLs;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.UserBiz;
import com.apppubs.util.JSONResult;
import com.apppubs.ui.widget.ProgressHUD;
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
				UserBiz biz = UserBiz.getInstance(mContext);
				biz.modifyPwd(mOriginalEt
						.getText().toString().trim(), mNewEt.getText().toString().trim(), new IAPCallback() {


					@Override
					public void onDone(Object obj) {
						ProgressHUD.dismissProgressHUDInThisContext(ChangePasswordActivity.this);
						Toast.makeText(ChangePasswordActivity.this, R.string.modify_pwd_success, Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onException(APError error) {
						mErrorHandler.onError(error);
					}
				});
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
