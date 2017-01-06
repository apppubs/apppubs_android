package com.mportal.client.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.mportal.client.R;

public class BaoliaoFromDialog extends Dialog implements android.view.View.OnClickListener{
	private TextView mFrom1;
	private TextView mFrom2;
	private OnOkClickListener mOnOkClickListener;
	public interface OnOkClickListener{
		void onclick(View v);
	}
	public BaoliaoFromDialog (Context context, OnOkClickListener onOkClickListener,String title,String msg){
		super(context, R.style.dialog);
		this.mOnOkClickListener = onOkClickListener;
		setContentView(R.layout.dialog_baoliao);
		mFrom1 = (TextView) findViewById(R.id.from1);
		mFrom2 = (TextView) findViewById(R.id.from2);
		mFrom1.setText(title);
		mFrom2.setText(msg);
		mFrom1.setOnClickListener(this);
		mFrom2.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(mOnOkClickListener!=null)
			mOnOkClickListener.onclick(v);
		this.dismiss();
	}
}
