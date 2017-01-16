package com.mportal.client.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.asytask.AsyTaskCallback;
import com.mportal.client.asytask.AsyTaskExecutor;
import com.mportal.client.bean.User;
import com.mportal.client.constant.Constants;
import com.mportal.client.constant.URLs;
import com.mportal.client.util.Base64;
import com.mportal.client.util.BitmapUtils;
import com.mportal.client.util.FileUtils;
import com.mportal.client.util.JSONResult;
import com.mportal.client.util.Utils;
import com.mportal.client.util.WebUtils;
import com.mportal.client.widget.CircleTextImageView;
import com.mportal.client.widget.ConfirmDialog;
import com.mportal.client.widget.LoadingDialog;
import com.mportal.client.widget.ProgressHUD;
import com.mportal.multi_image_selector.MultiImageSelectorActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		init();
	}

	private void init() {

		setTitle("账号信息");
		mAvatarIV = (CircleTextImageView) findViewById(R.id.usercenter_ctiv);
		mMOdify = (Button) findViewById(R.id.people_logout);
		mName = (EditText) findViewById(R.id.people_username);
		mEmail = (EditText) findViewById(R.id.people_email);
		mNicname = (EditText) findViewById(R.id.people_nicname);
		mPhone = (EditText) findViewById(R.id.people_tel);
		
		String appConfigStr = (String) FileUtils.readObj(this,Constants.FILE_NAME_APP_CONFIG);
		String flags = null;
		try {
			JSONObject jo = new JSONObject(appConfigStr);
			flags = jo.getString(Constants.APP_CONFIG_PARAM_USER_CENTER_PWD_FLAGS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {

		case R.id.people_logout:
			
			new ConfirmDialog(UserCencerActivity.this,
					new ConfirmDialog.ConfirmListener() {

						@Override
						public void onOkClick() {
							
//							logout();
							mUserBussiness.logout(UserCencerActivity.this);
							UserCencerActivity.this.finish();
						}

						@Override
						public void onCancelClick() {

						}
					}, "确定注销登陆吗？", "取消", "注销").show();

			break;
			case R.id.usercenter_avatar_rl:
				View layout = this.getLayoutInflater().inflate(R.layout.dialog_change_avatar,null);
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				final AlertDialog dialog = builder.create();
				dialog.setView(layout);
				TextView tv = (TextView) layout.findViewById(R.id.dialog_change_avatar_tv);
				tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.cancel();
						Intent intent = new Intent(UserCencerActivity.this, MultiImageSelectorActivity.class);
						intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE,MultiImageSelectorActivity.MODE_SINGLE);
						startActivityForResult(intent, REQUEST_CODE_PICTURES);
					}
				});
				dialog.show();

				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		User user = MportalApplication.user;
		mName.setText(user.getUsername());
		mEmail.setText(user.getEmail());
		mNicname.setText(user.getTrueName());
		mPhone.setText(user.getMobile());
		mImageLoader.displayImage(user.getAvatarUrl(),mAvatarIV);
		mAvatarIV.setText(user.getTrueName());
	}

	@Override
	public void finish() {
		super.finish();
		// 关闭键盘
		Utils.colseInput(UserCencerActivity.this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE_PICTURES&&resultCode== Activity.RESULT_OK) {

			final List<String> selectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
			Log.v(UserCencerActivity.class.getName(),selectPath.get(0));
			ProgressHUD.show(this);
			AsyTaskExecutor.getInstance().startTask(1, new AsyTaskCallback() {
				@Override
				public Object onExecute(Integer tag, String[] params) throws Exception {
					String url = String.format(URLs.URL_UPLOAD_AVATAR,MportalApplication.user.getUserId());
					Bitmap bitmap = BitmapFactory.decodeFile(params[0]);
					Bitmap b = BitmapUtils.zoomImg(bitmap,400,400);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					b.compress(Bitmap.CompressFormat.PNG, 100, baos);
					Map<String,String> paramsMap = new HashMap<String,String>();
					paramsMap.put("userid",params[1]);
					paramsMap.put("appcode",params[2]);
					String response = WebUtils.uploadFile(baos.toByteArray(),"file.jpg",url,paramsMap);
					JSONResult jr = JSONResult.compile(response);
					String avatarUrl = (String) jr.getResultMap().get("photourl");
					MportalApplication.user.setAvatarUrl(avatarUrl);

					return null;
				}

				@Override
				public void onTaskSuccess(Integer tag, Object obj) {
					ProgressHUD.dismissProgressHUDInThisContext(UserCencerActivity.this);
					Toast.makeText(UserCencerActivity.this,"头像修改成功",Toast.LENGTH_SHORT).show();
					mImageLoader.displayImage(MportalApplication.user.getAvatarUrl(),mAvatarIV);
				}

				@Override
				public void onTaskFail(Integer tag, Exception e) {
					ProgressHUD.dismissProgressHUDInThisContext(UserCencerActivity.this);
					Toast.makeText(UserCencerActivity.this,"头像修改失败",Toast.LENGTH_SHORT).show();
				}
			},new String[]{selectPath.get(0),MportalApplication.user.getUserId(),MportalApplication.systemSettings.getAppCode()});

		}

	}
}
