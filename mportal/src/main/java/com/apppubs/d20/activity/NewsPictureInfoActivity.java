package com.apppubs.d20.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.d20.bean.Collection;
import com.apppubs.d20.bean.Comment;
import com.apppubs.d20.bean.NewsInfo;
import com.apppubs.d20.bean.NewsPictureInfo;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.ShareTools;
import com.apppubs.d20.util.Utils;
import com.apppubs.d20.widget.PictureInfoViewPager;
import com.apppubs.d20.widget.ZoomImageView;
import com.apppubs.d20.MportalApplication;
import com.apppubs.d20.R;
import com.apppubs.d20.business.BussinessCallbackCommon;
import com.apppubs.d20.widget.ImageButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.orm.SugarRecord;

/**
 * 图片详情页
 * 
 */
public class NewsPictureInfoActivity extends BaseActivity implements OnPageChangeListener  {
	
	public static final String EXTRA_STRING_NAME_ID = "id";
	
	private String mInfoId;
	private DisplayImageOptions mImageLoaderOptions;
	
	private PictureInfoViewPager mVp;
	private ImageView mBackIv;
	/**
	 * 单条图片信息属性
	 */
	private TextView mTitleTv, mDesTv, mNumTv;
	private PictureInfoPageAdapter mAdapter;
	private List<ZoomImageView> mPicList;
	private LinearLayout mBottomMenu;
	private List<NewsPictureInfo> mNewsPicInfoList;
	private NewsInfo mNewsInfo;

	private LinearLayout mBottomLay;// 底部显示
	private boolean animFlag = true;// 动画标志位
	private TextView mCommentTv;
	private ImageButton mSaveImagview;
	private Comment mCommment;
	private Future mFuture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setNeedTitleBar(false);
		setContentView(R.layout.act_picture_info);
		Intent i = getIntent();
		mInfoId = i.getStringExtra(NewsPictureInfoActivity.EXTRA_STRING_NAME_ID);
//		mPicInfo = SugarRecord.findById(NewsPictureInfo.class, mInfoId);
		init();
		initBset();// 底部图片的设置；
//		if (mPicInfo == null) {
//			mPicInfo = new NewsPictureInfo();
//			mPicInfo.setId(mInfoId);
//			// mPicInfo.setChannelCode(mChannelCode);
//		}
		
		Drawable drawable = Drawable.createFromPath(getFilesDir().getAbsolutePath()+"/stance_pic.png");
		mImageLoaderOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(drawable)
		.showImageForEmptyUri(drawable)
		.showImageOnFail(drawable)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.denyNetworkDownload(MportalApplication.systemState.getNetworkState()!=ConnectivityManager.TYPE_WIFI&&!mAppContext.getSettings().isAllowDowPicUse2G())
		.build();
		
		mFuture = mNewsBussiness.getPicInfoPage(mInfoId,
				1, new BussinessCallbackCommon<List<NewsPictureInfo>>() {

					@Override
					public void onException(int excepCode) {
						Log.v("newsInfoActivity", "getNewsInfo出现异常");
					}

					@Override
					public void onDone(List<NewsPictureInfo> obj) {
						Log.v("newsInfoActivity", "getNewsInfo完成" + obj);
						mNewsPicInfoList = obj;
						mPicList = new ArrayList<ZoomImageView>();
						int size = obj.size();
						for (int i = -1; ++i < size;) {
							final ZoomImageView pic = new ZoomImageView(NewsPictureInfoActivity.this);
							pic.setBackgroundColor(Color.BLACK);
							
							mPicList.add(pic);
						}

						mAdapter = new PictureInfoPageAdapter(NewsPictureInfoActivity.this, mPicList);
						mVp.setAdapter(mAdapter);
						refreshPageInfo(0);
					}
				});
	}

	private void initBset() {
		ImageButton bShare = (ImageButton) findViewById(R.id.bottom_info_bar_share);
		ImageButton bComment = (ImageButton) findViewById(R.id.bottom_info_bar_comment_i);

		mSaveImagview.setColorFilter(getResources().getColor(android.R.color.white), Mode.SRC_ATOP);
		bShare.setColorFilter(getResources().getColor(android.R.color.white), Mode.SRC_ATOP);
		bComment.setColorFilter(getResources().getColor(android.R.color.white), Mode.SRC_ATOP);
	}

	private void init() {
		mBackIv = (ImageView) findViewById(R.id.picinfo_back);
		mCommentTv = (TextView) findViewById(R.id.bottom_info_bar_comment);
		mSaveImagview = (ImageButton) findViewById(R.id.bottom_info_bar_save);
		mBottomMenu = (LinearLayout) findViewById(R.id.pic_bottom_anim);
		mVp = (PictureInfoViewPager) findViewById(R.id.picinfo_vp);
		mTitleTv = (TextView) findViewById(R.id.picinfo_title_tv);
		mDesTv = (TextView) findViewById(R.id.picinfo_des_tv);
		mNumTv = (TextView) findViewById(R.id.picinfo_num_tv);
		mBottomLay = (LinearLayout) findViewById(R.id.newsinfo_menubar);
		mBottomLay.setBackgroundColor(Color.TRANSPARENT);
		mVp.setOnPageChangeListener(this);
		mVp.setOnClickListener(this);
		mCommentTv.setOnClickListener(this);
		mNewsInfo = SugarRecord.findById(NewsInfo.class, mInfoId);
		if(mNewsInfo==null){
			mNewsInfo = new NewsInfo();
			mNewsInfo.setId(mInfoId);
		}
	}

	private void refreshPageInfo(int pos) {
		NewsPictureInfo npi = mNewsPicInfoList.get(pos);
		String des = npi.getTitle().length() > 15 ? npi.getTitle().substring(0, 15) + "..." : npi.getTitle();
		mTitleTv.setText(des);
		mDesTv.setText(npi.getDescription());
		mNumTv.setText((pos + 1) + "/" + mPicList.size());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.picinfo_vp:
			if (animFlag) {
				mBottomMenu.setVisibility(View.GONE);
				mBottomMenu.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_to_top));
				mBackIv.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_to_bottom));
				mBackIv.setVisibility(View.GONE);
				animFlag = false;
			} else {
				mBottomMenu.setVisibility(View.VISIBLE);
				mBackIv.setVisibility(View.VISIBLE);
				mBottomMenu.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom));
				mBackIv.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top));
				animFlag = true;
			}

			break;
		case R.id.picinfo_back:
			finish();
			break;
		case R.id.bottom_info_bar_back:
			finish();
			break;
		case R.id.bottom_info_bar_save:
			String title = mNewsPicInfoList.get(0).getTitle();
			String summy = mNewsPicInfoList.get(0).getDescription();
			boolean isCollected = mNewsInfo.getIsCollected() == NewsInfo.UNCOLLECTED;
			if (isCollected) {
				mNewsInfo.setIsCollected(1);
			} else {
				mNewsInfo.setIsCollected(0);
			}
			Utils.toggleCollect(Collection.TYPE_PIC, NewsPictureInfoActivity.this, isCollected, mInfoId, title, summy,
					mSaveImagview);
			break;
		case R.id.bottom_info_bar_share:
			new ShareTools(getApplication()).showShare(mNewsInfo.getTitle(),mNewsInfo.getPicURL(),mNewsInfo.getPicURL());
			break;
		case R.id.picture_info_download_ib:
//			ImageView iv = mPicList.get(mVp.getCurrentItem());
//			Bitmap bitmap = ((BitmapDrawable)iv.getDrawable()).getBitmap();
			File file = mImageLoader.getDiskCache().get(mNewsPicInfoList.get(mVp.getCurrentItem()).getUrl());
			Bitmap bit = BitmapFactory.decodeFile(file.getAbsolutePath());
			MediaStore.Images.Media.insertImage(getContentResolver(), bit, "标题" , "描述");
			Toast.makeText(this, "已保存至相册", Toast.LENGTH_SHORT).show();
			break;
		case R.id.bottom_info_bar_comment_i:
			Intent intent = new Intent(NewsPictureInfoActivity.this, CommentActivity.class);
			intent.putExtra(CommentActivity.EXTRA_STRING_NAME_ID, mInfoId);
			intent.putExtra(CommentActivity.NEWSTYPESTRING, NewsInfo.NEWS_TYPE_PICTURE);// 4图片
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			break;
		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int pos) {
		refreshPageInfo(pos);
//		mVp.setCurrentDragImageView(mPicList.get(pos));
	}

	@Override
	protected void onResume() {
		super.onResume();
		initState();
		// 刷新评论数量
		initCommentCount();
	}

	private void initState() {
		if (mNewsInfo.getIsCollected() == 1) {
			mSaveImagview.setImageResource(R.drawable.menubar_favorite_h);
		}
	}

	private class PictureInfoPageAdapter extends PagerAdapter {
		List<ZoomImageView> viewList;

		public PictureInfoPageAdapter(Context contex, List<ZoomImageView> list) {
			viewList = list;
		}

		@Override
		public int getCount() {
			return viewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		// 销毁Item
		@Override
		public void destroyItem(View view, int position, Object object) {
			((ViewPager) view).removeView(viewList.get(position));
			LogM.log(this.getClass(), "destroyItem" + position);
		}

		// 实例化Item
		@Override
		public Object instantiateItem(View view, int position) {
			LogM.log(this.getClass(), "instantiateItem" + position+"viewList.size():"+viewList.size());
			ZoomImageView gv = viewList.get(position);
			((ViewPager) view).addView(gv);
			mImageLoader.displayImage(mNewsPicInfoList.get(position).getUrl(), gv,mImageLoaderOptions);
			return gv;
		}
		public ImageView getItem(int pos){
			return viewList.get(pos);
		}
	}

	public Comment initCommentCount() {
		mSystemBussiness.getCommentSizeZanCai(mInfoId, new BussinessCallbackCommon<Comment>() {
			@Override
			public void onException(int excepCode) {
				// TODO Auto-generated method stub
				mCommment = null;
			}

			@Override
			public void onDone(Comment obj) {
				// TODO Auto-generated method stub
				mCommment = obj;
				mCommentTv.setText(mCommment.getCommentnum());
			}
		});
		return mCommment;
	}
}
