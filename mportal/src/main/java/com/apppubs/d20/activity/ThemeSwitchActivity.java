package com.apppubs.d20.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.apppubs.d20.constant.Actions;
import com.apppubs.d20.R;
import com.apppubs.d20.bean.Settings;

public class ThemeSwitchActivity extends BaseActivity {

	private int mCurSelectedTheme;
	private ImageView mIv;
	private Button mSwitchBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_theme_switch);
		mIv = (ImageView) findViewById(R.id.switch_iv);
		mSwitchBtn = (Button) findViewById(R.id.switch_btn);
		setTitle("主题切换");
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {

		super.onClick(v);
		int id = v.getId();
		switch (id) {
		case R.id.themeSwitch_blue_rl:
			mCurSelectedTheme = Settings.THEME_BLUE;
			int color = getResources().getColor(R.color.app_default);
			mTitleBar.setBackgroundColor(color);
			mIv.setImageResource(R.drawable.theme_img1);
			mSwitchBtn.setTextColor(color);
			break;
		case R.id.themeSwitch_indigo_rl:
			mCurSelectedTheme = Settings.THEME_INDIGO;
			int color1 = getResources().getColor(R.color.app_default_i);
			mTitleBar.setBackgroundColor(color1);
			mIv.setImageResource(R.drawable.theme_img2);
			mSwitchBtn.setTextColor(color1);
			break;
		case R.id.themeSwitch_red_rl:
			mCurSelectedTheme = Settings.THEME_RED;
			int color2 = getResources().getColor(R.color.app_default_r);
			mTitleBar.setBackgroundColor(color2);
			mIv.setImageResource(R.drawable.theme_img3);
			mSwitchBtn.setTextColor(color2);
			break;
		case R.id.themeSwitch_brown_rl:
			mCurSelectedTheme = Settings.THEME_BROWN;
			int color3 = getResources().getColor(R.color.app_default_brown);
			mTitleBar.setBackgroundColor(color3);
			mIv.setImageResource(R.drawable.theme_img4);
			mSwitchBtn.setTextColor(color3);
			break;
		case R.id.switch_btn:
			if(mCurSelectedTheme==Settings.THEME_BLUE){
				mAppContext.getSettings().setTheme(Settings.THEME_BLUE);
				mApp.setTheme(R.style.AppThemeBlue);
				
			}else if(mCurSelectedTheme==Settings.THEME_INDIGO){
				mAppContext.getSettings().setTheme(Settings.THEME_INDIGO);
				mApp.setTheme(R.style.AppThemeIndigo);
			}else if(mCurSelectedTheme==Settings.THEME_RED){
				mAppContext.getSettings().setTheme(Settings.THEME_RED);
				mApp.setTheme(R.style.AppThemeRed);
			}else{
				mAppContext.getSettings().setTheme(Settings.THEME_BROWN);
				mApp.setTheme(R.style.AppThemeBrown);
			}
			mAppContext.setSettings(mAppContext.getSettings());
			Intent i = new Intent(Actions.CLOSE_ALL_ACTIVITY);
			sendBroadcast(i);
			HomeBaseActivity.startHomeActivity(this);
			break;
		default:
			break;
		}
		
		
	}
}
