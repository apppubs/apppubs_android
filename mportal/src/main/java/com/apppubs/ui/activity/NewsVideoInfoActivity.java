package com.apppubs.ui.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.bean.TCollection;
import com.apppubs.constant.APError;
import com.apppubs.d20.R;
import com.apppubs.asytask.AsyTaskCallback;
import com.apppubs.asytask.AsyTaskExecutor;
import com.apppubs.bean.Comment;
import com.apppubs.bean.TNewsInfo;
import com.apppubs.bean.NewsVideoInfo;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.CollectionBiz;
import com.apppubs.constant.URLs;
import com.apppubs.util.ShareTools;
import com.apppubs.util.Utils;
import com.apppubs.util.WebUtils;
import com.apppubs.ui.videoView.VideoActivity;
import com.orm.SugarRecord;

public class NewsVideoInfoActivity extends BaseActivity implements AsyTaskCallback{
	
	public static final String EXTRA_STRING_NAME_ID = "id";
	public static final String EXTRA_STRING_NAME_CHANNELCODE = "channel_code";
	private static final int TASK_TAG_LOAD_DATA = 1;
	
	private String mInfoId;
	private String mChannelCode;
	private NewsVideoInfo mNewsVideo;
	private boolean isCollected;
	private Comment mCommment;
	
	private TextView mTitileTv, mTimeTv, mContentTv;
	private ImageView mVideoPic;
	private PopupWindow mMenuPW;
	private TextView mCommentTv;
	int tempTextSize = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_video_info);
		Intent intent = getIntent();
		mInfoId = intent.getStringExtra(EXTRA_STRING_NAME_ID);
		mChannelCode = intent.getStringExtra(EXTRA_STRING_NAME_CHANNELCODE);
		init();
		fetchData();
		initMenu();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshCommet();
	}
	private void init() {
		mTitileTv = (TextView) findViewById(R.id.video_title_tv);
		mTimeTv = (TextView) findViewById(R.id.video_time_tv);
		mContentTv = (TextView) findViewById(R.id.video_content);
		mVideoPic = (ImageView) findViewById(R.id.video_pic_iv);
	}
	private void initMenu() {

		this.mTitleBar.setRightBtnImageResourceId(R.drawable.title_more);
		this.mTitleBar.setRightBtnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View menuPop = LayoutInflater.from(NewsVideoInfoActivity.this).inflate(R.layout.pop_news_info_menu, null);
				mMenuPW = new PopupWindow(menuPop, ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				mMenuPW.setFocusable(true);
				mMenuPW.setOutsideTouchable(true);
				mMenuPW.setBackgroundDrawable(new BitmapDrawable());
				mMenuPW.showAsDropDown(mTitleBar.getRightView());

//				// 是否可收藏
//				if (mNewsInfo.getShareFlag() == 0) {
//					setVisibilityOfViewByResId(menuPop, R.id.pop_news_info_share, View.GONE);
//				}

				if (false) {
					setVisibilityOfViewByResId(menuPop, R.id.pop_news_info_collect, View.GONE);
				}else{
					isCollected = null!=SugarRecord.findByProperty(TCollection.class,"info_id", mInfoId+","+mChannelCode)?true:false;
					if (isCollected) {
						ImageView iv = (ImageView) menuPop.findViewById(R.id.pop_news_info_collect_ib);
						iv.setImageResource(R.drawable.menubar_favorite_h);
					}
					
				}

			}
		});

		// 评论数量按钮
		mCommentTv = new TextView(this);
		mCommentTv.setText(mCommment == null ? 0 + "" : mCommment.getCommentnum() + "");
		mCommentTv.setGravity(Gravity.CENTER);
		mCommentTv.setTextColor(Color.parseColor("#FFFFFF"));
		mCommentTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
		mCommentTv.setBackgroundResource(R.drawable.menubar_comment);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Utils.dip2px(this, 30),
				Utils.dip2px(this, 24));
		lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		mCommentTv.setLayoutParams(lp);
		RelativeLayout con = new RelativeLayout(this);
		con.addView(mCommentTv, lp);
		this.mTitleBar.setRight2ndView(con);
		
		this.mTitleBar.setRight2ndBtnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NewsVideoInfoActivity.this, CommentActivity.class);
				intent.putExtra(EXTRA_STRING_NAME_ID, mInfoId);
				startActivity(intent);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		});

	}
	private void fetchData(){
		AsyTaskExecutor.getInstance().startTask(TASK_TAG_LOAD_DATA, this, new String[]{mInfoId,mChannelCode});
	}
	
	public void refreshCommet() {
		mSystemBiz.getCommentSizeZanCai(mInfoId, new IAPCallback<Comment>() {
			@Override
			public void onException(APError excepCode) {
				mCommment = null;
			}

			@Override
			public void onDone(Comment obj) {
				mCommment = obj;
				if (mCommentTv != null) {
					mCommentTv.setText(mCommment.getCommentnum());
				}

				// 更新数据库中的评论数
				SugarRecord.updateById(TNewsInfo.class, mInfoId, "COMMENT_NUM", mCommment.getCommentnum());
			}
		});
	}
	
	@Override
	public Object onExecute(Integer tag, String[] params) throws IOException, InterruptedException, JSONException {
		Object result = null;
		if (tag==TASK_TAG_LOAD_DATA) {
			String url = String.format(URLs.URL_VIDEO,URLs.baseURL, params[0],params[1]);
				result = WebUtils.request(url, NewsVideoInfo.class, "resultinfo");
		}
		return result;
	}

	@Override
	public void onTaskSuccess(Integer tag, Object obj) {
		if(tag==TASK_TAG_LOAD_DATA){
			mNewsVideo = (NewsVideoInfo) obj;
			
			mTitileTv.setText(mNewsVideo.getTitle());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
			mTimeTv.setText(sdf.format(mNewsVideo.getPubTime()));
			mContentTv.setText(mNewsVideo.getDescription());
			mImageLoader.displayImage(mNewsVideo.getPicUrl(), mVideoPic);
			setVisibilityOfViewByResId(R.id.video_progressBar_ll, View.GONE);
		}
	}

	@Override
	public void onTaskFail(Integer tag,Exception e) {
		if (tag==TASK_TAG_LOAD_DATA) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.pop_news_info_collect:
			String title = mNewsVideo.getTitle();
			String summy = mNewsVideo.getDescription();
			ImageView iv = (ImageView) mMenuPW.getContentView().findViewById(R.id.pop_news_info_collect_ib);
			CollectionBiz.toggleCollect(TCollection.TYPE_VEDIO, this, isCollected, mInfoId+","+mChannelCode, title, summy);
			isCollected = !isCollected;
			Toast.makeText(this, isCollected?"已收藏":"取消收藏", Toast.LENGTH_SHORT).show();
			iv.setImageResource(isCollected?R.drawable.menubar_favorite_h:R.drawable.menubar_favorite);
			break;
		case R.id.pop_news_info_share:
			new ShareTools(getApplication()).showShare(mNewsVideo.getTitle(), mNewsVideo.getVideoUrl(), "");
			break;
		case R.id.pop_news_info_textsize:
			
			new AlertDialog.Builder(this)  
			.setTitle("字体大小")  
			.setSingleChoiceItems(new String[] {"大号","中号","小号"}, tempTextSize,   
			  new DialogInterface.OnClickListener() {  
			                              
			     public void onClick(DialogInterface dialog, int which) {  
			        dialog.dismiss();  
					if (which == 0) {
//						webSettings.setTextSize(TextSize.LARGER);
						mContentTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
						tempTextSize = 0;
					} else if (which == 1) {
//						webSettings.setTextSize(TextSize.NORMAL);
						tempTextSize = 1;
						mContentTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					} else {
//						webSettings.setTextSize(TextSize.SMALLER);
						tempTextSize = 2;
						mContentTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
					}
			     }  
			  }  
			)  
			.setNegativeButton("取消", null)  
			.show(); 
			
//			WebSettings webSettings = mWebView.getSettings();
//			if (tempTextSize == 0) {
//				webSettings.setTextSize(TextSize.LARGER);
//				tempTextSize = 1;
//			} else if (tempTextSize == 1) {
//				webSettings.setTextSize(TextSize.SMALLER);
//				tempTextSize = -1;
//			} else {
//				webSettings.setTextSize(TextSize.NORMAL);
//				tempTextSize = 0;
//			}

			break;
		case R.id.video_pic_iv:
			Intent videoActivityIntent = new Intent(this,VideoActivity.class);
			videoActivityIntent.putExtra(VideoActivity.EXTRA_STRING_NAME_VIDEO_URL, mNewsVideo.getVideoUrl());
			startActivity(videoActivityIntent);
			break;
		default:
			break;
		}
	}

}
