package com.apppubs.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.apppubs.d20.R;

public class ChatVideoInfoActivity extends BaseActivity {

	public static final String EXTRA_STRING_VIDEO_URL = "video_http_url";
	
	
	private VideoView mVideoView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String videoUrl = getIntent().getExtras().getString(EXTRA_STRING_VIDEO_URL);
		
		setContentView(R.layout.act_chat_video_info);
		setTitle("视频预览");
		mVideoView = (VideoView) findViewById(R.id.chat_video_info_vv);
		//Use a media controller so that you can scroll the video contents
		//and also to pause, start the video.
		MediaController mediaController = new MediaController(this); 
		mediaController.setAnchorView(mVideoView);
		mVideoView.setMediaController(mediaController);
		mVideoView.setVideoURI(Uri.parse(videoUrl));
		mVideoView.start();
	}
}
