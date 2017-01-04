package com.mportal.client.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.mportal.client.R;
import com.mportal.client.view.ZoomImageView;

public class ImageViewActivity extends BaseActivity {

	public static final String EXTRA_STRING_IMG_URL = "img_url"; 
	
	private String mImgUrl;
	private ZoomImageView mIV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initProperty();
		initView();
		loadImage();
	}

	private void initProperty() {
		mImgUrl = getIntent().getStringExtra(EXTRA_STRING_IMG_URL);
		if (TextUtils.isEmpty(mImgUrl)) {
			Toast.makeText(this, "图片地址为空", Toast.LENGTH_SHORT).show();
			return;
		}
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_in);
	}
	
	private void initView() {
		setNeedTitleBar(false);
		setContentView(R.layout.act_image_view);
		mIV = (ZoomImageView) findViewById(R.id.img_view_zv);
		 mIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageViewActivity.this.finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
	}
	

	private void loadImage() {
		mImageLoader.displayImage(mImgUrl,  mIV);
	}

}
