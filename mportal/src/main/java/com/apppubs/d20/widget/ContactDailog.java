package com.apppubs.d20.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.apppubs.d20.R;

public class ContactDailog extends Dialog implements android.view.View.OnClickListener{

	private ContactDailogListener listener;

	public interface ContactDailogListener {
		void onCallClick();
		void onSmsClick();
	}

	
	/**
	 * @param context 上下文
	 * @param theme 主题
	 * @param listener 监听器
	 * @param title 标题
	 * @param describe 描述
	 * @param cancleStr 取消按钮显示文字
	 * @param okStr 确定按钮显示文字
	 */
	public ContactDailog(Context context, int theme, ContactDailogListener listener){
		super(context, theme);
		this.listener = listener;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_userinfo_contact);
		initViews();
	}

	private void initViews() {
		LinearLayout call = (LinearLayout) findViewById(R.id.popu_userinfo_call);
		LinearLayout sms = (LinearLayout) findViewById(R.id.popu_userinfo_message);
		call.setOnClickListener(this);
		sms.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.popu_userinfo_call:
			dismiss();
			listener.onCallClick();
			break;
		case R.id.popu_userinfo_message:
			dismiss();
			listener.onSmsClick();
			break;

		default:
			break;
		}
	}


}
