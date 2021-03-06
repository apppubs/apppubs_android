package com.apppubs.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.apppubs.asytask.AsyTaskCallback;
import com.apppubs.asytask.AsyTaskExecutor;
import com.apppubs.bean.App;
import com.apppubs.constant.Constants;
import com.apppubs.d20.R;
import com.apppubs.ui.webapp.WebAppFragment;
import com.apppubs.ui.widget.AlertDialog;
import com.apppubs.ui.widget.LoadingDialog;
import com.apppubs.util.FileUtils;
import com.apppubs.util.SystemUtils;
import com.apppubs.util.Utils;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 登录界面
 * 
 */
public class LoginActivity extends BaseActivity implements AsyTaskCallback {
	private EditText username, password;
	private String usernameT, passwordT;// 注册返回来的用户名，密码
	private ImageView mBgIV;
//	private Button zhuce;
	private String TAG = "LoginActivity";
	private LoadingDialog dialog;
	private Context context = LoginActivity.this;

	private String mRegurl;
	private String mForgetPwdUrl;
	
	private int LOGIN_TAG = 1;
	public static final int REQUEST_CODE = 3;
	public static final int SYSTEM_CONFIG_TAG = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);
		Intent intent = getIntent();
		usernameT = intent.getStringExtra(RegisterActivity.REGISTNAME);
		passwordT = intent.getStringExtra(RegisterActivity.REGISTPASSWORD);
		init();
	}

	private void init() {

		setTitle("用户登录");
		username = (EditText) findViewById(R.id.login_username);
		password = (EditText) findViewById(R.id.login_password);
		mBgIV = (ImageView) findViewById(R.id.login_bg_iv);
		// wangjimiam = (Button) findViewById(R.id.login_wangjipassword);
//		zhuce = (Button) findViewById(R.id.login_register);
//		if (mAppContext.getApp().getAllowRegister() == 1) {// 不允许注册
//			zhuce.setVisibility(View.GONE);
//		}
		username.setText(usernameT);
		password.setText(passwordT);
		
		AsyTaskExecutor.getInstance().startTask(SYSTEM_CONFIG_TAG, this, null);
		
		String picUrl = mAppContext.getApp().getLoginPicUrl();
		if(!TextUtils.isEmpty(picUrl)){
			mImageLoader.displayImage(picUrl, mBgIV);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		// case R.id.login_wangjipassword:
		// startContainerActivity(new Intent(LoginActivity.this,
		// ForgetPasswordActivity.class));
		// break;
//		case R.id.login_register:
//			startContainerActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//			break;
		case R.id.login_login:
			usernameT = username.getText().toString().trim();
			passwordT = password.getText().toString().trim();
			if (usernameT.isEmpty()) {
				Toast.makeText(getApplication(), "请输入用户名", Toast.LENGTH_SHORT).show();
			} else if (passwordT.isEmpty()) {
				Toast.makeText(getApplication(), "请输入密码", Toast.LENGTH_SHORT).show();
			} else {
				if (SystemUtils.canConnectNet(getApplication())) {
					dialog = new LoadingDialog(LoginActivity.this, "正在登陆，请稍候...");
					dialog.show();
					String deviceid = mAppContext.getApp().getPushVendorType()==App.PUSH_VENDOR_TYPE_BAIDU?mAppContext.getApp().getBaiduPushUserId():JPushInterface.getRegistrationID(this);// 百度硬件设备号
					String systemVresion = Utils.getAndroidSDKVersion();
					String currentVersionCode =  mAppContext.getVersionName();// app版本号
					String token = mAppContext.getApp().getPushVendorType() == App.PUSH_VENDOR_TYPE_BAIDU ? mAppContext.getApp()
							.getBaiduPushUserId() : JPushInterface.getRegistrationID(this);// 百度硬件设备号
					String[] params = new String[]{usernameT,passwordT,deviceid,token,Build.MODEL,systemVresion,currentVersionCode};
					AsyTaskExecutor.getInstance().startTask(LOGIN_TAG, LoginActivity.this, params);
				} else {
					Toast.makeText(getApplication(), getResources().getString(R.string.err_msg_network_faile), Toast.LENGTH_LONG).show();
				}

			}
			break;
		case R.id.login_reg_tv:
			Bundle bundle = new Bundle();
			bundle.putString(WebAppFragment.ARGUMENT_STRING_URL, mRegurl);
			ContainerActivity.startContainerActivity(this, WebAppFragment.class, bundle, "注册");
			break;
		case R.id.login_forget_pw_tv:
			Bundle forgetBundle = new Bundle();
			forgetBundle.putString(WebAppFragment.ARGUMENT_STRING_URL, mForgetPwdUrl);
			ContainerActivity.startContainerActivity(this, WebAppFragment.class, forgetBundle, "忘记密码");
			break;
		default:
			break;
		}
	}

	@Override
	public void finish() {
		super.finish();
		Utils.colseInput(LoginActivity.this);
	}

	@Override
	public Object onExecute(Integer tag, String[] params) throws Exception {
		Object obj = null;
		if(tag==LOGIN_TAG){
			obj = mUserBussiness.login(params[0], params[1], params[2], params[3], params[4], params[5],params[6], true);
		}else if(tag==SYSTEM_CONFIG_TAG){
			String appConfigStr = (String) FileUtils.readObj(this, Constants.FILE_NAME_APP_CONFIG);
			JSONObject jo = new JSONObject(appConfigStr);
			obj = new String[]{jo.getString(Constants.APP_CONFIG_PARAM_REG_URL),jo.getString(Constants.APP_CONFIG_PARAM_FORGET_PWD_URL)};
		}
		return obj;
	}

	@Override
	public void onTaskSuccess(Integer tag, Object obj) {
		if (tag==LOGIN_TAG&&(Integer)obj == 2) {
			dialog.dismiss();
			setResult(RESULT_OK);
			finish();
		}else if(tag==SYSTEM_CONFIG_TAG){
			String[] result = (String[]) obj;
			if(result!=null&&result.length>0&&!TextUtils.isEmpty(result[0])){
				setVisibilityOfViewByResId(R.id.login_reg_tv, View.VISIBLE);
				mRegurl = result[0];
			}
			if(result!=null&&result.length>1&&!TextUtils.isEmpty(result[1])){
				setVisibilityOfViewByResId(R.id.login_forget_pw_tv, View.VISIBLE);
				mForgetPwdUrl = result[1];
			}
			
		}else {
			dialog.dismiss();
			new AlertDialog(context, new AlertDialog.OnOkClickListener() {

				@Override
				public void onclick() {

				}
			}, "请检查您的用户名和密码是否正确", "确定").show();
			password.setText("");
		}
	}

	@Override
	public void onTaskFail(Integer tag, Exception e) {
		Toast.makeText(this, "服务器异常", Toast.LENGTH_SHORT).show();
		if(dialog!=null){
			dialog.dismiss();
		}
	}
}
