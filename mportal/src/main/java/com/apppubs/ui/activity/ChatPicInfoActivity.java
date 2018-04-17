package com.apppubs.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.apppubs.util.LogM;
import com.apppubs.ui.widget.ZoomImageView;
import com.apppubs.d20.R;
import com.apppubs.model.APResultCallback;

public class ChatPicInfoActivity extends BaseActivity implements OnClickListener {
	private ViewPager mViewPager;
	/**
	 * DragImageView支持手势缩放的ImageView
	 */
	private List<ZoomImageView> viewList = new ArrayList<ZoomImageView>();
	private String mPicPath;
	public static String EXTRA_PIC_URI = "intentpicinfosdpath";
	private String filepath = Environment.getExternalStorageDirectory() + "/downloadpic/";
	private ImageView save;
	private Bitmap bitmap;
	private Future f;

	/**
	 * 点击聊天内容中的图片放大
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/* set it to be full screen */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		mPicPath = getIntent().getStringExtra(EXTRA_PIC_URI);
		System.out.println("chatpicinfo的地址。。。。" + mPicPath);
		setContentView(R.layout.act_chatpicinfo);
		hideTitleBar();
		init();
		initImageView();
		mViewPager.setAdapter(new PictureInfoPageAdapter());
		mViewPager.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void init() {
		mViewPager = (ViewPager) findViewById(R.id.chat_picinfo_vp1);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		}
	}

	private void savePic() {

		final File file = new File(getFilesDir().getAbsolutePath() + File.pathSeparator + UUID.randomUUID() + ".jpg");

		if (mPicPath.startsWith("file:/")) {
			// TODO Auto-generated method stub
			Toast.makeText(ChatPicInfoActivity.this, "图片成功保存到相册", Toast.LENGTH_SHORT).show();
			// File file = new File(obj);
			Bitmap bit = BitmapFactory.decodeFile(mPicPath.replace("file:/", ""));
			MediaStore.Images.Media.insertImage(getContentResolver(), bit, "标题", "描述");
		} else {

			mMsgBussiness.writePicUrlSD(mPicPath, file, new APResultCallback<String>() {

				@Override
				public void onException(int excepCode) {

				}

				@Override
				public void onDone(String obj) {
					// TODO Auto-generated method stub
					Toast.makeText(ChatPicInfoActivity.this, "图片成功保存到相册", Toast.LENGTH_SHORT).show();
					// File file = new File(obj);
					Bitmap bit = BitmapFactory.decodeFile(file.getAbsolutePath());
					MediaStore.Images.Media.insertImage(getContentResolver(), bit, "标题", "描述");
				}
			});
		}
	}

	private void initImageView() {
		ZoomImageView dragImageView = new ZoomImageView(ChatPicInfoActivity.this);
		// 设置图片
		// dragImageView.setImageBitmap(bit);
		mImageLoader.displayImage(mPicPath, dragImageView);
		// dragImageView.setmActivity(this);// 注入Activity.
		// /** 测量状态栏高度 **/
		// ViewTreeObserver viewTreeObserver =
		// dragImageView.getViewTreeObserver();
		// viewTreeObserver.addOnGlobalLayoutListener(new
		// OnGlobalLayoutListener() {
		//
		// @Override
		// public void onGlobalLayout() {
		// int state_height = 0;// 状态栏的高度
		// if (state_height == 0) {
		// // 获取状况栏高度
		// Rect frame = new Rect();
		// getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// state_height = frame.top;
		// int window_width = MportalApplication.windowWidth;
		// int window_height = MportalApplication.windowHeight;
		// dragImageView.setScreen_H(window_height - state_height);
		// dragImageView.setScreen_W(window_width);
		// }
		//
		// }
		// });
		dragImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		dragImageView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ChatPicInfoActivity.this);
				builder.setTitle("操作");
				builder.setItems(new String[] { "保存到手机" }, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							savePic();
							break;

						default:
							break;
						}
					}
				});
				builder.show();
				return false;
			}
		});
		viewList.add(dragImageView);

	}

	private class PictureInfoPageAdapter extends PagerAdapter {

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
			LogM.log(this.getClass(), "instantiateItem" + position + "viewList.size():" + viewList.size());
			ZoomImageView iv = viewList.get(position);
			iv.setScaleType(ScaleType.CENTER_INSIDE);
			((ViewPager) view).addView(iv);
			return iv;
		}
	}

	@Override
	public void finish() {
		super.finish();
		// 实现淡入淡出的效果
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

	}
}
