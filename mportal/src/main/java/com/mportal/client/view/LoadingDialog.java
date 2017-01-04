package com.mportal.client.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.mportal.client.R;

public class LoadingDialog extends Dialog{
	private TextView mTitleTv;
	public LoadingDialog(Context context,String title){
		super(context, R.style.dialog);
		setContentView(R.layout.dialog_loading);
		mTitleTv = (TextView) findViewById(R.id.dialog_loading_text);
		mTitleTv.setText(title);
	}	
}
