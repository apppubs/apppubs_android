package com.apppubs.d20.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apppubs.d20.AppContext;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.R;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.util.SystemUtils;
import com.apppubs.d20.widget.ConfirmDialog;
import com.apppubs.d20.widget.ConfirmDialog.ConfirmListener;
import com.apppubs.d20.widget.ProgressHUD;

/**
 * 意见反馈
 * 
 */
public class FeedbackActivity extends BaseActivity {

	private EditText mContentTv, mPhoneEt;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.act_feedback);
		setTitle("意见反馈");
		mContentTv = (EditText) findViewById(R.id.feedback_editText1);
		mPhoneEt = (EditText) findViewById(R.id.feedback_editText2);
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);
		switch (v.getId()) {
		case R.id.suggestion_submit_btn:
			onSubmitBtnClicked();
			break;
		default:
			break;
		}

	}

	private void onSubmitBtnClicked() {
		
		if (mContentTv.getText().toString().equals("")) {
			SystemUtils.showToast(FeedbackActivity.this, "说点什么吧。");
		} else if (mPhoneEt.getText().toString().equals("")) {
			SystemUtils.showToast(FeedbackActivity.this, "请填写您的联系方式以便我们联系您");
		} else {
			new ConfirmDialog(this, new ConfirmListener() {

				@Override
				public void onOkClick() {
					ProgressHUD.show(FeedbackActivity.this,null, true, false, null);
					submit();
				}

				@Override
				public void onCancelClick() {

				}
			}, "确定提交？", "取消", "提交").show();
		}
	}

	//提交到服务器
	private void submit() {

		String phone = null;
		String content = null;
		try {
			phone = URLEncoder.encode(mPhoneEt.getText().toString(), "utf-8");
			content = URLEncoder.encode(mContentTv.getText().toString(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = String.format(URLs.URL_FEEDBACK,URLs.baseURL,URLs.appCode,
				AppContext.getInstance(mContext).getCurrentUser().getUserId(), "android", phone, content);
		LogM.log(this.getClass(), url);
		mRequestQueue.add(new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				ProgressHUD.dismissProgressHUDInThisContext(FeedbackActivity.this);
				try {
					JSONObject jo = new JSONObject(response);
					int result = jo.getInt("result");
					if (result == 1) {
						Toast.makeText(FeedbackActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(FeedbackActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				clearEditText();
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				ProgressHUD.dismissProgressHUDInThisContext(FeedbackActivity.this);
				Toast.makeText(FeedbackActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
			}

		}));
	}
	
	private void clearEditText(){
		 mContentTv.setText("");
		 mPhoneEt.setText("");
	}

}
