package com.apppubs.d20.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apppubs.d20.R;

import java.util.List;

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
	private int mRes;
	
	public interface OnOkClickListener{
		void onclick();
	}
	
	public AlertDialog(Context context, OnOkClickListener onOkClickListener,String title,String btnText){
		
		this(context,onOkClickListener,title,null,btnText);
		
	}
	
	public AlertDialog(Context context, OnOkClickListener onOkClickListener,String title,String msg,String btnText){
		super(context, R.style.dialog);
		this.mOnOkClickListener = onOkClickListener;

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(mRes);
	}

	public void setRes(int resId){
        this.mRes = resId;
    }

	@Override
	public void onClick(View v) {
		if(mOnOkClickListener!=null)
			mOnOkClickListener.onclick();
		this.dismiss();
	}

}
