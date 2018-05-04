package com.apppubs.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.AppContext;
import com.apppubs.constant.APError;
import com.apppubs.d20.R;
import com.apppubs.asytask.AsyTaskCallback;
import com.apppubs.asytask.AsyTaskExecutor;
import com.apppubs.bean.UserInfo;
import com.apppubs.constant.URLs;
import com.apppubs.model.UserBiz;
import com.apppubs.model.message.UserBasicInfo;
import com.apppubs.model.IAPCallback;
import com.apppubs.util.BitmapUtils;
import com.apppubs.util.JSONResult;
import com.apppubs.util.Utils;
import com.apppubs.util.WebUtils;
import com.apppubs.ui.widget.CircleTextImageView;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.ui.widget.LoadingDialog;
import com.apppubs.ui.widget.ProgressHUD;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;


public class UserCencerActivity extends BaseActivity {

	private final int REQUEST_CODE_PICTURES = 3;
	private LoadingDialog dialog;
	private Button mMOdify;
	private boolean isHidden = true; // 密码的显示和隐藏
	private EditText mName, mEmail, mNicname, mPhone;
	private CircleTextImageView mAvatarIV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_peoplecencer);
		setTitle("账号信息");

		fetchView();
		showChangePwdIfNeed();
	}

	private void fetchView() {


		mAvatarIV = (CircleTextImageView) findViewById(R.id.usercenter_ctiv);
		mMOdify = (Button) findViewById(R.id.people_logout);
		mName = (EditText) findViewById(R.id.people_username);
		mEmail = (EditText) findViewById(R.id.people_email);
		mNicname = (EditText) findViewById(R.id.people_nicname);
		mPhone = (EditText) findViewById(R.id.people_tel);
	}

	private void showChangePwdIfNeed() {
		String flags = mAppContext.getAppConfig().getAdbookAccountPWDFlags();
		String[] params = TextUtils.isEmpty(flags)?null:flags.split(",");

		if(params!=null&&params.length>0){
			if(params[0] .equals("1")){
				mTitleBar.setRightText("修改密码");
				mTitleBar.setRightBtnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(UserCencerActivity.this,ChangePasswordActivity.class);
						startActivity(intent);
					}
				});
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		UserInfo user = AppContext.getInstance(mContext).getCurrentUser();
		mName.setText(user.getUsername());
		mEmail.setText(user.getEmail());
		mNicname.setText(user.getTrueName());
		mPhone.setText(user.getMobile());
		mImageLoader.displayImage(user.getAvatarUrl(),mAvatarIV);
		mAvatarIV.setText(user.getTrueName());
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {

		case R.id.people_logout:
			onLogout();
			break;
		case R.id.usercenter_avatar_rl:
			onChangeAvatar();
			break;
		}
	}

	Uri uritempFile;
	private void onChangeAvatar() {
		View layout = this.getLayoutInflater().inflate(R.layout.dialog_change_avatar,null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		dialog.setView(layout);
		TextView tv = (TextView) layout.findViewById(R.id.dialog_change_avatar_tv);
		tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();


				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				i.putExtra("crop", "true");
				i.putExtra("aspectX", 1);
				i.putExtra("aspectY", 1);
				startActivityForResult(i, REQUEST_CODE_PICTURES);
			}
		});
		dialog.show();
	}

	private void onLogout() {
		new ConfirmDialog(UserCencerActivity.this,
				new ConfirmDialog.ConfirmListener() {

					@Override
					public void onOkClick() {
						UserBiz.getInstance(mContext).logout(UserCencerActivity.this);
						UserCencerActivity.this.finish();
					}

					@Override
					public void onCancelClick() {

					}
				}, "确定注销登陆吗？", "取消", "注销").show();
	}

	@Override
	public void finish() {
		super.finish();
		// 关闭键盘
		Utils.colseInput(UserCencerActivity.this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(requestCode==REQUEST_CODE_PICTURES&&resultCode== RESULT_OK) {

			Bundle extras = data.getExtras();
			Bitmap photo = null;
			if(extras != null ) {
				photo = extras.getParcelable("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);
			}else{

				try {
					photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

			ProgressHUD.show(this);
			final Bitmap finalPhoto = photo;
			AsyTaskExecutor.getInstance().startTask(1, new AsyTaskCallback() {
				@Override
				public Object onExecute(Integer tag, String[] params) throws Exception {
					String url = String.format(URLs.URL_UPLOAD_AVATAR,URLs.baseURL,URLs.appCode, AppContext.getInstance(mContext).getCurrentUser().getUserId());
					Bitmap b = BitmapUtils.zoomImg(finalPhoto,400,400);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					b.compress(Bitmap.CompressFormat.PNG, 100, baos);
					Map<String,String> paramsMap = new HashMap<String,String>();
					paramsMap.put("userid",params[0]);
					paramsMap.put("appcode",params[1]);
					String response = WebUtils.uploadFile(baos.toByteArray(),"file.jpg",url,paramsMap);
					JSONResult jr = JSONResult.compile(response);
					String avatarUrl = (String) jr.getResultMap().get("photourl");
					AppContext.getInstance(mContext).getCurrentUser().setAvatarUrl(avatarUrl);

					return null;
				}

				@Override
				public void onTaskSuccess(Integer tag, Object obj) {
					ProgressHUD.dismissProgressHUDInThisContext(UserCencerActivity.this);
					Toast.makeText(UserCencerActivity.this,"头像修改成功",Toast.LENGTH_SHORT).show();
					mImageLoader.displayImage(AppContext.getInstance(mContext).getCurrentUser().getAvatarUrl(),mAvatarIV);
					List<String> ids = new ArrayList<String>();
					ids.add(mAppContext.getCurrentUser().getUserId());
					mUserBussiness.cacheUserBasicInfoList(ids, new IAPCallback<List<UserBasicInfo>>() {
						@Override
						public void onDone(List<UserBasicInfo> obj) {
							if (obj!=null&&obj.size()>0){
								UserBasicInfo ubi = obj.get(0);

								io.rong.imlib.model.UserInfo ui = new io.rong.imlib.model.UserInfo(ubi.getUserId(),ubi.getTrueName(),Uri.parse(ubi.getAtatarUrl()));
								RongIM.getInstance().refreshUserInfoCache(ui);
							}
						}

						@Override
						public void onException(APError excepCode) {

						}
					});

				}

				@Override
				public void onTaskFail(Integer tag, Exception e) {
					ProgressHUD.dismissProgressHUDInThisContext(UserCencerActivity.this);
					Toast.makeText(UserCencerActivity.this,"头像修改失败",Toast.LENGTH_SHORT).show();
				}
			},new String[]{AppContext.getInstance(mContext).getCurrentUser().getUserId(),mAppContext.getSettings().getAppCode()});

		}

		super.onActivityResult(requestCode, resultCode, data);

	}
}
