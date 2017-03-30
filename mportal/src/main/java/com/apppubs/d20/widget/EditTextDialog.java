package com.apppubs.d20.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.apppubs.d20.R;

public class EditTextDialog extends Dialog implements View.OnClickListener {

	private ConfirmListener listener;
	private TextView title;
	private EditText mEditText;
	private Button cancle, ok;
	private String mTitle, mCancelStr, mOkStr,mDefaultText;

	public interface ConfirmListener {
		void onOkClick(String result);
		void onCancelClick();
	}


	/**
	 * @param context 上下文
	 * @param theme 主题
	 * @param listener 监听器
	 * @param title 标题
	 * @param cancleStr 取消按钮显示文字
	 * @param okStr 确定按钮显示文字
	 */
	public EditTextDialog(Context context, int theme, ConfirmListener listener, String title, String cancleStr,
                          String okStr) {
		super(context, theme);
		this.listener = listener;
		this.mTitle = title;
		this.mCancelStr = cancleStr;
		this.mOkStr = okStr;
	}

	public EditTextDialog(Context context, ConfirmListener listener, String title, String cancel, String ok) {
		this(context, R.style.dialog, listener, title, cancel, ok);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_edittext);
		initViews();
	}

	public void setDefaultText(String text){
		mDefaultText = text;
	}
	private void initViews() {
		title = (TextView) findViewById(R.id.dialog_title);
		mEditText = (EditText) findViewById(R.id.dialog_content_et);
		ok = (Button) findViewById(R.id.confirm_ok);
		cancle = (Button) findViewById(R.id.confirm_cancel);
		title.setText(mTitle);
		ok.setText(mOkStr);
		ok.setOnClickListener(this);
		cancle.setText(mCancelStr);
		cancle.setOnClickListener(this);

		mEditText.setText(mDefaultText);
		mEditText.setSelection(!TextUtils.isEmpty(mDefaultText)?mDefaultText.length():0);
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
			if(listener!=null&&mEditText!=null){
				listener.onOkClick(mEditText.getText().toString());
			}
			break;

		default:
			break;
		}
	}

}
