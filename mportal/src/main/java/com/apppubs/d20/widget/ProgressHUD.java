package com.apppubs.d20.widget;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.apppubs.d20.R;

public class ProgressHUD extends Dialog {
	
	private static Map<Context,ProgressHUD> progressHUDMap = new HashMap<Context, ProgressHUD>();
	public ProgressHUD(Context context) {
		super(context);
	}

	public ProgressHUD(Context context, int theme) {
		super(context, theme);
	}


	public void onWindowFocusChanged(boolean hasFocus){
		ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();
    }
	
	public void setMessage(CharSequence message) {
		if(message != null && message.length() > 0) {
			findViewById(R.id.message).setVisibility(View.VISIBLE);			
			TextView txt = (TextView)findViewById(R.id.message);
			txt.setText(message);
			txt.invalidate();
		}
	}
	
	public static ProgressHUD show(Context context, CharSequence message, boolean indeterminate, boolean cancelable,
			OnCancelListener cancelListener) {
		ProgressHUD dialog = new ProgressHUD(context,R.style.ProgressHUD);
		dialog.setTitle("");
		dialog.setContentView(R.layout.progress_hud);
		if(message == null || message.length() == 0) {
			dialog.findViewById(R.id.message).setVisibility(View.GONE);			
		} else {
			TextView txt = (TextView)dialog.findViewById(R.id.message);
			txt.setText(message);
		}
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.getWindow().getAttributes().gravity=Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
		lp.dimAmount=0.2f;
		dialog.getWindow().setAttributes(lp); 
		//dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();
		progressHUDMap.put(context, dialog);
		return dialog;
	}
	
	public static ProgressHUD show(Context context){
		return show(context, null, true, false, null);
	}
	
	public static void dismissProgressHUDInThisContext(Context context){
		if(progressHUDMap!=null&&progressHUDMap.size()>0&&progressHUDMap.get(context)!=null){
			progressHUDMap.get(context).dismiss();
			progressHUDMap.remove(context);
		}
	}
}
