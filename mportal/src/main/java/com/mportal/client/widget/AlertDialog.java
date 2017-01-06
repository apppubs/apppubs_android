package com.mportal.client.widget;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mportal.client.R;

/**
 * 
 * @author zhangwen 2014-12-22
 * 只带有一个确认按钮的dialog
 *
 */
public class AlertDialog extends Dialog implements android.view.View.OnClickListener{
	
	private TextView mTitleTv;
	private Button mOkBtn;
	private TextView mMsgTv;
	private OnOkClickListener mOnOkClickListener;
	
	public interface OnOkClickListener{
		void onclick();
	}
	
	public AlertDialog(Context context, OnOkClickListener onOkClickListener,String title,String btnText){
		
		this(context,onOkClickListener,title,null,btnText);
		
	}
	
	public AlertDialog(Context context, OnOkClickListener onOkClickListener,String title,String msg,String btnText){
		super(context, R.style.dialog);
		this.mOnOkClickListener = onOkClickListener;
		setContentView(R.layout.dialog_alert);
		mTitleTv = (TextView) findViewById(R.id.alert_title);
		mTitleTv.setText(title);
		if(msg!=null){
			mMsgTv = (TextView) findViewById(R.id.alert_msg_tv);
			mMsgTv.setText(msg);
			mMsgTv.setVisibility(View.VISIBLE);
		}
		mOkBtn = (Button) findViewById(R.id.alert_btn);
		mOkBtn.setText(btnText);
		mOkBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(mOnOkClickListener!=null)
			mOnOkClickListener.onclick();
		this.dismiss();
	}
	
}
