package com.apppubs.d20.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.apppubs.d20.R;
import com.apppubs.d20.util.Utils;

public class AboutActivity extends BaseActivity {

	private View mWebsiteLL,mContactLL;
	private TextView mAppNameTV, mVersionTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_about);
		setTitle("关于");
		fetchView();
		fillText();
		addListener();
	}
	private void fetchView() {
		mAppNameTV = (TextView) findViewById(R.id.about_app_name_tv);
		mWebsiteLL = findViewById(R.id.about_website_ll);
		mContactLL = findViewById(R.id.about_contact_ll);
		mVersionTV = (TextView) findViewById(R.id.about_version_tv);
	}

	private void fillText() {
		mAppNameTV.setText(mAppContext.getApp().getName());
		mVersionTV.setText(mSystemBussiness.getVersionString(this));
	}

	private void addListener() {
		mWebsiteLL.setOnClickListener(this);
		mContactLL.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		int id = v.getId();
		switch (id){
			case R.id.about_website_ll:
				onWebSiteClicked();
				break;
			case R.id.about_contact_ll:
				onContactClicked();
				break;
			default:
				break;
		}
	}

	private void onContactClicked() {
		Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:010-62955760"));
		startActivity(intent);
	}

	private void onWebSiteClicked() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setData(Uri.parse("http://www.apppubs.com/"));
		startActivity(intent);
	}
}
