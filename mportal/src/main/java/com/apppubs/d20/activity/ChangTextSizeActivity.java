package com.apppubs.d20.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.apppubs.d20.bean.Settings;
import com.apppubs.d20.R;

public class ChangTextSizeActivity extends BaseActivity {
	
	private ImageView small, middle, big;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_change_text_size);
		init();
		initState();
	}

	private void init() {
		setTitle("字体设置");
		small = (ImageView) findViewById(R.id.changeword_smalliv);
		middle = (ImageView) findViewById(R.id.changeword_middleliv);
		big = (ImageView) findViewById(R.id.changeword_bigiv);
		
	}
	
	/**
	 * 恢复状态
	 */
	private void initState(){
		Settings settings = mAppContext.getSettings();
		if(settings.getTextSize()==Settings.TEXTSIZE_SMALL){
			small.setVisibility(View.VISIBLE);
			middle.setVisibility(View.GONE);
			big.setVisibility(View.GONE);
		}else if(settings.getTextSize()==Settings.TEXTSIZE_MEDIUM){
			middle.setVisibility(View.VISIBLE);
			small.setVisibility(View.GONE);
			big.setVisibility(View.GONE);
		}else{
			big.setVisibility(View.VISIBLE);
			middle.setVisibility(View.GONE);
			small.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.changeword_small:
			mAppContext.getSettings().setTextSize(Settings.TEXTSIZE_SMALL);
			mAppContext.setSettings(mAppContext.getSettings());
			small.setVisibility(View.VISIBLE);
			middle.setVisibility(View.GONE);
			big.setVisibility(View.GONE);
			break;
		case R.id.changeword_middlel:
			mAppContext.getSettings().setTextSize(Settings.TEXTSIZE_MEDIUM);
			mAppContext.setSettings(mAppContext.getSettings());
			middle.setVisibility(View.VISIBLE);
			small.setVisibility(View.GONE);
			big.setVisibility(View.GONE);
			break;
		case R.id.changeword_big:
			mAppContext.getSettings().setTextSize(Settings.TEXTSIZE_BIG);
			mAppContext.setSettings(mAppContext.getSettings());
			big.setVisibility(View.VISIBLE);
			middle.setVisibility(View.GONE);
			small.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}
	
}
