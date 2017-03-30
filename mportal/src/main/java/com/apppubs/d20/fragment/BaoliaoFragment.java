package com.apppubs.d20.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.activity.BaseActivity;
import com.apppubs.d20.bean.User;
import com.apppubs.d20.bean.UserInfo;
import com.apppubs.d20.util.FileUtils;
import com.apppubs.d20.util.StringUtils;
import com.apppubs.d20.util.SystemUtils;
import com.apppubs.d20.util.Tools;
import com.apppubs.d20.util.Utils;
import com.apppubs.d20.widget.ConfirmDialog;
import com.apppubs.d20.widget.FlowLayout;
import com.apppubs.d20.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BaoliaoFragment extends BaseActivity {
	private LinearLayout pop_layout;// 图片来源显示
	private ImageView pic, nomaddpic;// 图片
	private LinearLayout progress,apppiclinerlayout;
	private FlowLayout chicepicss;// 选择加入的图片
	private ConfirmDialog dialog;// 是否提交的按钮
	private boolean isAddpic = true;
	private Bitmap bitmap;
	private String mPicPath;
	private Context context=BaoliaoFragment.this;
	private EditText baoliaotitle, baoliaocount, baoliaoname, baoliaophone, baoliaoemail;
	private static final int PHOTO_REQUEST_CUT = 1002;
	private File tempFile;
	private List<RelativeLayout> mRespic = new ArrayList<RelativeLayout>();// 加入的图片综合
	private Handler myhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			clearView();
			switch (msg.what) {
			case 0:
				SystemUtils.showToast(getApplication(), "提交失败");
				break;
			case 1:
				SystemUtils.showToast(getApplication(), "提交成功");
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_baoliao);
		init();
	}

	private void init() {
		mTitleBar.setRightText("提交");
//		mTitleBar.setRightTextColor(getResources().getColorStateList(R.drawable.sel_com_text));
		mTitleBar.setRightTextSize(getResources().getDimensionPixelSize(R.dimen.title_text_size));
		mTitleBar.setRightBtnClickListener(this);
		progress = (LinearLayout) findViewById(R.id.baoliao_progress_ll);
		pic = (ImageView) findViewById(R.id.baoliao_pic_iv);
		chicepicss = (FlowLayout) findViewById(R.id.chicepicss);
		pop_layout = (LinearLayout) findViewById(R.id.pop_layout);
		nomaddpic = (ImageView) findViewById(R.id.baoliao_addpic);
		apppiclinerlayout=(LinearLayout) findViewById(R.id.baoliao_apppic_lin);
		
		baoliaotitle = (EditText) findViewById(R.id.baoliao_title);
		baoliaocount = (EditText) findViewById(R.id.baoliao_count);
		baoliaoname = (EditText) findViewById(R.id.baoliao_name);
		baoliaophone = (EditText) findViewById(R.id.baoliao_phone);
		baoliaoemail = (EditText) findViewById(R.id.baoliao_email);

		baoliaotitle.setOnClickListener(this);
		baoliaocount.setOnClickListener(this);
		baoliaophone.setOnClickListener(this);
		baoliaoemail.setOnClickListener(this);

		setTitle("报料");
	}

	private void clearView() {
		// TODO Auto-generated method stub
		progress.setVisibility(View.GONE);
		baoliaotitle.setText("");
		baoliaocount.setText("");
		baoliaoname.setText("");
		baoliaophone.setText("");
		baoliaoemail.setText("");
		for (int i = 0; i < mRespic.size(); i++) {
			chicepicss.removeView(mRespic.get(i));
		}
		apppiclinerlayout.setVisibility(View.GONE);
		chicepicss.setVisibility(View.GONE);
		nomaddpic.setVisibility(View.GONE);
		isAddpic = true;
	};

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.titlebar_left_btn:
			finish();
			break;

		case R.id.titlebar_right_btn:
			final String title = baoliaotitle.getText().toString();
			final String content = baoliaocount.getText().toString();
			final String contract = baoliaophone.getText().toString();

			if (title.equals("")) {
				Toast.makeText(context, "请输入您要报料的标题", 1000).show();
			} else if (content.equals("")) {
				Toast.makeText(context, "请写入您要报料的内容", 1000).show();
			} else if (contract.equals("")) {
				Toast.makeText(context, "请填写您的联系方式以便我们联系您", 1000).show();
			} else {
				if (title.length() > 30) {
					Toast.makeText(context, "标题不可超过30个字，请重新填写", 1000).show();
					baoliaocount.setText("");
				} else if (content.length() > 400) {
					Toast.makeText(context, "内容不可超过400个字，请重新填写", 1000).show();
					baoliaocount.setText("");
				} else {
					dialog = new ConfirmDialog( this, new ConfirmDialog.ConfirmListener() {

						@Override
						public void onOkClick() {
							/**
							 * 报料
							 * http://www.sxxynews.com:8080/wmh360/epaper/json/
							 * readernews.jsp? &userid=123&title=123&name=1233&
							 * content=123&contract=123&picurl=123&appcode=D01
							 */
							// 联网请求数据
							String userid = "";
							String username = baoliaoname.getText().toString();
							UserInfo currentUser = AppContext.getInstance(mContext).getCurrentUser();
							if (currentUser.getId() == null
									|| currentUser.getId().equals("")) {
								userid = "";
							} else {
								userid = currentUser.getId();
							}
							if (username.equals("")) {
								if ((currentUser.getUsername() == null || currentUser
										.getUsername().equals(""))) {
									username = "";
								} else {
									username = currentUser.getUsername();
								}
							}
							String picPath=getImageStr(mPicPath);
							baoliaoCanNetwork(userid, title, username, content, contract, picPath,
									mAppContext.getApp().getCode());
							System.out.println("输入的名字，联系信息...." + username + "............" + contract);
						}

						@Override
						public void onCancelClick() {

						}
					}, "确定提交？", "取消", "确定");
					dialog.show();
				}
			}
			break;
		case R.id.baoliao_title:
			break;
		case R.id.baoliao_count:
			break;
		case R.id.baoliao_email:
			break;
		case R.id.baoliao_people:
			break;
		case R.id.baoliao_pic_iv:
			if (isAddpic) {
				apppiclinerlayout.setVisibility(View.VISIBLE);
				chicepicss.setVisibility(View.VISIBLE);
				nomaddpic.setVisibility(View.VISIBLE);
				
				isAddpic = false;
			} else {
				apppiclinerlayout.setVisibility(View.GONE);
				chicepicss.setVisibility(View.GONE);
				nomaddpic.setVisibility(View.GONE);
				isAddpic = true;
			}

			break;
		case R.id.baoliao_addpic:
			if (!isAddpic) {
				pop_layout.setVisibility(View.VISIBLE);
				Animation slideInAnimation = AnimationUtils.loadAnimation(context,
						R.anim.abc_slide_in_bottom);
				pop_layout.startAnimation(slideInAnimation);
			}

			break;
		case R.id.btn_take_photo:
			// 调用系统相机
			Intent camare = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			camare.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg")));
			startActivityForResult(camare, 2);
			pop_layout.setVisibility(View.GONE);
			break;
		case R.id.btn_pick_photo:
			// 调用本地相册
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);

			intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intent, 1); // RESULT_LOAD_IMAGE:Activity的标志。自已定义
			pop_layout.setVisibility(View.GONE);
			
			break;
		case R.id.btn_cancel:
			pop_layout.clearAnimation();
			if (pop_layout.getVisibility() == View.GONE)
				return;
			Animation slideOutAnimation = AnimationUtils.loadAnimation(context,
					R.anim.abc_slide_out_bottom);
			pop_layout.startAnimation(slideOutAnimation);
			pop_layout.setVisibility(View.GONE);

			break;
		default:

			break;
		}
	}

	// 判断联网提交
	private void baoliaoCanNetwork(final String userid, final String title, final String name, final String count,
			final String contract, final String picurl, final String appcode) {
		// TODO Auto-generated method stub
		if (SystemUtils.canConnectNet(getApplication())) {
			progress.setVisibility(View.VISIBLE);
			new Thread() {
				public void run() {

					Tools json = new Tools(context);
					int resurt = json.submmitBaoliao(userid, title, name, count, contract, picurl, appcode);
					if (resurt == 1) {
						myhandler.sendEmptyMessage(1);
					} else {
						myhandler.sendEmptyMessage(0);
					}
				};
			}.start();

		} else {
			SystemUtils.showToast(getApplication(), "联网失败，请检查您的网络");
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}
	private String getImageStr(String imgFilePath) {
		if(null == imgFilePath) {
			return "";
		}
		byte[] data = null;
		try {
			InputStream is = new FileInputStream(imgFilePath);
			data = new byte[is.available()];
			is.read(data);
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		BASE64Encoder encoder = new BASE64Encoder();
//		return Base64.encodeToString(data, Base64.DEFAULT);
		String temp  = Base64.encodeToString(data, Base64.DEFAULT);
		String str = null;
		try {
			str = URLEncoder.encode(temp,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return str;
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 相机拍摄
		if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				Log.i("TestFile", "SD card is not avaiable/writeable right now.");
				return;
			}
			String pictureName = StringUtils.getNowDateString("yyyyMMddHHmmss");
			String localPath = FileUtils.getInstance().getCameraTempFilePath();
			tempFile = new File(localPath, pictureName + ".jpg");
			startPhotoZoom(Uri.fromFile(tempFile),65);
			mPicPath = localPath + "/" + pictureName + ".jpg";

			String name = new DateFormat().format("yyyyMMddmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
			Bundle bundle = data.getExtras();
			bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

			FileOutputStream b = null;
			// ???????????????????????????????为什么不能直接保存在系统相册位置呢？？？？？？？？？？？？
			File file = new File("/sdcard/myImage/");
			file.mkdirs();// 创建文件夹
			String fileName = "/sdcard/myImage/" + name;
			try {
				b = new FileOutputStream(fileName);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
				putBackpic(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} // 本地相册
		else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			cursor.moveToFirst();
			mPicPath = cursor.getString(columnIndex);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			Log.d("picturePath", picturePath);
			bitmap = BitmapFactory.decodeFile(picturePath);
			startPhotoZoom(data.getData(), 65);
			// return
			putBackpic(bitmap);
		}
	}

	// 图的剪裁
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, PHOTO_REQUEST_CUT);

	}

	private void putBackpic(Bitmap bitmap) {
		// TODO Auto-generated method stub
		int winth = Utils.dip2px(context, 70);// 图片的大小
		int hight=Utils.dip2px(context, 55);// 图片的大小
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(winth, hight);
	
		RelativeLayout addpic = new RelativeLayout(context);

		addpic.setLayoutParams(params);
		ImageView iv = new ImageView(context);
		iv.setLayoutParams(params);
		iv.setScaleType(ScaleType.FIT_XY);
		iv.setImageBitmap(bitmap);// 将图片显示在ImageView里

		ImageView deliv = new ImageView(context);
		RelativeLayout.LayoutParams delivparams = new RelativeLayout.LayoutParams(
				android.support.v4.view.ViewPager.LayoutParams.WRAP_CONTENT,
				android.support.v4.view.ViewPager.LayoutParams.WRAP_CONTENT);
		delivparams.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.ALIGN_PARENT_RIGHT);
		int marginTop = Utils.dip2px(context, 2);
		int marginRight = Utils.dip2px(context, 2);// 图片的大小
		delivparams.setMargins(0, marginTop, marginRight, 0);
		deliv.setLayoutParams(delivparams);
		deliv.setImageResource(R.drawable.del_btn);

		addpic.addView(iv);
		addpic.addView(deliv);
		addpic.setOnClickListener(this);
		addpic.setId(mRespic.size());
		mRespic.add(addpic);
		System.out.println("mRespic.size().................." + mRespic.size());

		chicepicss.addView(addpic);
		addpic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				/**
				 * 取消已加入的图片
				 */
				Log.d("BaoliaoActivity.this", "mRespic.size()............two......" + mRespic.size());
				System.out.println();
				chicepicss.removeView(mRespic.get(view.getId()));
			}
		});
	}
}
