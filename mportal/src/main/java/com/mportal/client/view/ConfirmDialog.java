package com.mportal.client.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mportal.client.R;

public class ConfirmDialog extends Dialog implements android.view.View.OnClickListener {

	private ConfirmListener listener;
	private TextView title, contentTv;
	private Button cancle, ok;
	private String mTitle, mCancelStr, mOkStr, mDescribeStr;

	public interface ConfirmListener {
		void onOkClick();
		void onCancelClick();
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
	public ConfirmDialog(Context context, int theme, ConfirmListener listener, String title, String describe, String cancleStr,
			String okStr) {
		super(context, theme);
		this.listener = listener;
		this.mTitle = title;
		this.mDescribeStr = describe;
		this.mCancelStr = cancleStr;
		this.mOkStr = okStr;
	}

	public ConfirmDialog(Context context, ConfirmListener listener, String title, String describe, String cancel, String ok) {
		this(context, R.style.dialog, listener, title, describe, cancel, ok);
	}
	public ConfirmDialog(Context context ,ConfirmListener listener,String title,String cancel,String ok){
		this(context, listener, title, null, cancel, ok);
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_confirm);
		initViews();
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.dialog_title);
		contentTv = (TextView) findViewById(R.id.dialog_content);
		ok = (Button) findViewById(R.id.confirm_ok);
		cancle = (Button) findViewById(R.id.confirm_cancel);
		if (mDescribeStr!=null&&!mDescribeStr.equals("")) {
			contentTv.setVisibility(View.VISIBLE);
			contentTv.setText(mDescribeStr);
			contentTv.setGravity(Gravity.CENTER_HORIZONTAL);
		} else {
			contentTv.setVisibility(View.GONE);
		}
		title.setText(mTitle);
		ok.setText(mOkStr);
		ok.setOnClickListener(this);
		cancle.setText(mCancelStr);
		cancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.confirm_cancel:
			dismiss();
			listener.onCancelClick();
			break;
		case R.id.confirm_ok:
			dismiss();
			listener.onOkClick();
			break;

		default:
			break;
		}
	}

}
