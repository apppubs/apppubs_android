package com.mportal.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mportal.client.R;
import com.mportal.client.widget.LockPatternView;
import com.mportal.client.widget.LockPatternView.OnFinishListener;

public class LockActivity extends BaseActivity {
	
	private LinearLayout mContainerLl;
	private LockPatternView mLockPatternView;
	@Override
	protected void onCreate(Bundle arg0) {
		setNeedBack(false);
		super.onCreate(arg0);
		setNeedBack(false);
		setContentView(R.layout.act_lock);
		mTitleBar.setTitle("滑动解锁");
		mContainerLl = (LinearLayout) findViewById(R.id.lock_container_ll);
		mLockPatternView = (LockPatternView) findViewById(R.id.lock_lpv);
		mContainerLl.setBackgroundColor(mDefaultColor);
		
		mLockPatternView.setOnFinishListener(new OnFinishListener() {
			
			@Override
			public void onFinish(int result) {
				if(result==OnFinishListener.RESULT_SUCCESS){
					Intent i = new Intent(LockActivity.this, FirstLoginActity.class);
					startActivity(i);
				}else{
					Toast.makeText(LockActivity.this, "密码错误(2589)", Toast.LENGTH_LONG).show();
				}
			
			}
		});
		
	}
}
