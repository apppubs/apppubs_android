package com.mportal.client.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mportal.client.R;
import com.mportal.client.business.BussinessCallbackCommon;
import com.mportal.client.constant.URLs;
import com.mportal.client.util.SystemUtils;
import com.mportal.client.util.Utils;
import com.mportal.client.widget.LoadingDialog;

/**
 * 注册界面
 * 
 */
public class RegisterActivity extends BaseActivity {
	private ImageView seePassword;
	private CheckBox mMianzeCb;
	private TextView seeMianze;
	private Button zhuce;
	public static String REGISTNAME="REGISTNAME";
	public static String REGISTPASSWORD="REGISTPASSWORD";
	private EditText mName, mEmail, mPassword, mRepassword, mNicname, mPhone;
	private boolean ismianzebn = true;
	private Context context = RegisterActivity.this;
	private LoadingDialog dialog;
	private int resultzhuce;
    private boolean isHidden=true;//密码的显示和隐藏
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_regsisger);
		init();
	}

	private void init() {
		
		setTitle("注册");
		seePassword = (ImageView) findViewById(R.id.zhuce_seeword);
		mMianzeCb = (CheckBox) findViewById(R.id.zhuce_mianzebn);
		seeMianze = (TextView) findViewById(R.id.zhuce_mianzetv);
		zhuce = (Button) findViewById(R.id.zhuce_zhuce);
		
		mName = (EditText) findViewById(R.id.zhuce_username);
		mEmail = (EditText) findViewById(R.id.zhuce_email);
		mRepassword = (EditText) findViewById(R.id.zhuce_re_password);
		mNicname = (EditText) findViewById(R.id.zhuce_nicname);
		mPhone = (EditText) findViewById(R.id.zhuce_tel);
		mPassword = (EditText) findViewById(R.id.zhuce_password);

		seePassword.setOnClickListener(this);
		mMianzeCb.setOnClickListener(this);
		seeMianze.setOnClickListener(this);
		zhuce.setOnClickListener(this);
		seeMianze.setText("同意《"+mApp.app.getName()+"》的免责声明");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {

		case R.id.zhuce_seeword:
			mPassword.setPressed(false);
			  if (isHidden) {                 
				  //设置EditText文本为可见的             
				  mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());       
				  isHidden=false; 
			  } else {                     //设置EditText文本为隐藏的          
			     mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); 
			     isHidden=true; 
			  } 
			break;
		case R.id.zhuce_mianzebn:
			if (ismianzebn) {
				ismianzebn = false;
			} else {
				ismianzebn = true;
			}

			break;
		case R.id.zhuce_mianzetv:
			Intent intent=new Intent(RegisterActivity.this,WebAppActivity.class);
			intent.putExtra(WebAppActivity.EXTRA_NAME_TITLE, "免责声明");
			intent.putExtra(WebAppActivity.EXTRA_NAME_URL,URLs.URL_REGMIANZE);
			startActivity(intent);
			break;
		case R.id.zhuce_zhuce:
          
			if (mName.getText().toString().equals("")) {
				Toast.makeText(context, "请选择一个用户名", Toast.LENGTH_SHORT).show();
			} else if (mPassword.getText().toString().equals("")) {
				Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
			} else if (mRepassword.getText().toString().equals("")) {
				Toast.makeText(context, "请再次输入密码", Toast.LENGTH_SHORT).show();
			} else if (!mPassword.getText().toString()
					.equals(mRepassword.getText().toString())) {
				Toast.makeText(context, "两次密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
				mRepassword.setText("");
			} else if (mEmail.getText().toString().equals("")) {
				Toast.makeText(context, "请输入邮箱", Toast.LENGTH_SHORT).show();
			}else if(!mMianzeCb.isChecked()){
				Toast.makeText(context, "请同意免责声明", Toast.LENGTH_SHORT).show();
			}else {
				
				if (SystemUtils.canConnectNet(context)) {
				dialog = new LoadingDialog(context, "正在提交信息，请稍后...");
				dialog.show();
				/**
				 * usernamestr emailstr passwordstr clientidstr //D01,,D58
				 * mobilestr nicknamestr
				 * mName.getText().toString().trim(), mEmail.getText().toString().trim(),
								mPassword.getText().toString().trim(), mPhone
										.getText().toString().trim(), mNicname
										.getText().toString().trim());
				 */
				mSystemBussiness.postZhuce(mName.getText().toString().trim(), mEmail.getText().toString().trim(),
						mPassword.getText().toString().trim(), mPhone
						.getText().toString().trim(), mNicname
						.getText().toString().trim(),new BussinessCallbackCommon<String>() {
							
							@Override
							public void onException(int excepCode) {
								
							}
							
							@Override
							public void onDone(String obj) {
								dialog.dismiss();
								System.out.println("dialog.dismiss();+dialog.dismiss();999999999999999999999999 ");
								boolean bo=Boolean.parseBoolean(obj);
								if (bo) {
									Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
									Intent intent=new Intent(context,LoginActivity.class);
									intent.putExtra(REGISTNAME, mName.getText().toString().trim());
									intent.putExtra(REGISTPASSWORD, mPassword.getText().toString().trim());
									startActivity(intent);
									finish();
								}
								else{
									Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
								}
								//resultzhuce=Integer.parseInt(obj);
//								 switch (resultzhuce) {
//								 case -1:
//									 Toast.makeText(context, "两次密码不一致，请重新输入", 2000).show();
//								 break;
//								 case -2:
//									 Toast.makeText(context, "两次密码不一致，请重新输入", 2000).show();
//								 break;
//								 case -3:
//									 Toast.makeText(context, "两次密码不一致，请重新输入", 2000).show();
//								 break;
//								 case -4:
//									 Toast.makeText(context, "两次密码不一致，请重新输入", 2000).show();
//								 break;
//								 case -5:
//									 Toast.makeText(context, "两次密码不一致，请重新输入", 2000).show();
//								 break;
//								 case -6:
//									 Toast.makeText(context, "两次密码不一致，请重新输入", 2000).show();
//								 break;
//								 case 0:
//									 Toast.makeText(context, "两次密码不一致，请重新输入", 2000).show();
//								 break;
//								
//								 default:
//									 Toast.makeText(context, "当官的风格", 2000).show();
//								 break;
//								 }
							}
						});
					
				}
				else{
					Toast.makeText(context, getResources().getString(R.string.network_faile), Toast.LENGTH_SHORT).show();
				}
			}
		}
   }		
	
	@Override
	public void finish() {
		super.finish();
		Utils.colseInput(RegisterActivity.this);
	}
}