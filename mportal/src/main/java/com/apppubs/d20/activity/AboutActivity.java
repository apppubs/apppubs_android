package com.apppubs.d20.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.R;
import com.apppubs.d20.bean.AppConfig;

public class AboutActivity extends BaseActivity {

	private View mWebsiteLL,mContactLL;
	private TextView mAppNameTV, mVersionTV;
	private String mSupportCompany;
	private String mOfficialWebsite;
	private String mContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_about);
		setTitle("关于");
		initData();
		fetchView();
		fillText();
		addListener();
	}

	private void initData() {
		AppConfig config = AppContext.getInstance(mContext).getApp().getAppConfig();
		String properties = config.getAboutProperties();
		if (!TextUtils.isEmpty(properties)){
			String[] args = properties.split(";");
			if (args!=null&&args.length>2){
				mSupportCompany = args[0];
				mOfficialWebsite = args[1];
				mContact = args[2];
			}
		}
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
		setTextForTestView(mSupportCompany,R.id.about_supportcompany_tv);
		setTextForTestView(mOfficialWebsite,R.id.about_officialwebsite_tv);
		setTextForTestView(mContact,R.id.about_contact_tv);
	}

	private void setTextForTestView(String text,int resId) {
		if (!TextUtils.isEmpty(text)){
			TextView supportCompanyTv = (TextView) findViewById(resId);
			supportCompanyTv.setText(text);
		}
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
		Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse(TextUtils.isEmpty(mContact)?"tel:010-62955760":mContact));
		startActivity(intent);
	}

	private void onWebSiteClicked() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setData(Uri.parse(TextUtils.isEmpty(mOfficialWebsite)?"http://www.apppubs.com/":mOfficialWebsite));
		startActivity(intent);
	}
}
