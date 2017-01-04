package com.mportal.client.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.bean.App;
import com.mportal.client.bean.User;
import com.mportal.client.constant.Constants;
import com.mportal.client.constant.URLs;
import com.mportal.client.net.RequestListener;
import com.mportal.client.util.FileUtils;
import com.mportal.client.util.JSONResult;
import com.mportal.client.view.ContactDailog;
import com.mportal.client.view.ProgressHUD;

public class UserInfoActivity extends BaseActivity implements OnClickListener,RequestListener{

	public static String EXTRA_STRING_USER_ID = "user_id";
	
	private User mUser;
	private ImageView mIv;
	private TextView mNameTv, mDeptTv;
	private TextView mEmailTV, mTelTV, mMobileTV, mWorkAddressTV;
	
	private String mIconUrl;
	private String mIconCacheStr;

	private JSONObject mAppConfigJO;
	private String[] mIconConfigParams;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.act_userinfo);
		setTitle("详细信息");
		init();
		optionView();
		loadIcon();
	}

	private void loadIcon() {
		if(mIconConfigParams!=null&&mIconConfigParams.length>0&&mIconConfigParams[0].equals("1")){
			if(mIconConfigParams.length>2&&mIconConfigParams[2].equals("1")){
				mIv.setScaleType(ScaleType.CENTER_CROP);
			}
			loadIconCache();
			loadIconData();
		}
	}

	private void optionView() {
		if (mUser.getIcon() != null && !mUser.getIcon().equals("")) {
			mImageLoader.displayImage(mUser.getIcon(), mIv);
		}
		String emailS = mUser.getEmail();
		String workTELS = mUser.getWorkTEL();
		String mobS = mUser.getMobile();
		String offerS = mUser.getOfficeNO();
		if (emailS != null && !emailS.equals("")) {
			mEmailTV.setText(emailS);
		}
		if (workTELS != null && !workTELS.equals("")) {
			mTelTV.setText(workTELS);
		}
		if (mobS != null && !mobS.equals("")) {
			mMobileTV.setText(mobS);
		}
		if (offerS != null && !offerS.equals("")) {
			mWorkAddressTV.setText(offerS);
		}

		mNameTv.setText(mUser.getTrueName());
		fillDepartment();
	}

	private void fillDepartment() {
		try {
			String id = mAppConfigJO.getString(Constants.APP_CONFIG_PARAM_ADBOOK_ROOT_ID);
			if (!TextUtils.isEmpty(id)) {
				List<String> deptNameStringList;
				deptNameStringList = mUserBussiness.getDepartmentStringListByUserId(mUser.getUserId(), id);
				StringBuilder sb = new StringBuilder();
				int size = deptNameStringList.size();
				for (int i = -1; ++i < size;) {
					if (i > 0) {
						sb.append("\n" + deptNameStringList.get(i));
					} else {
						sb.append(deptNameStringList.get(i));
					}

				}
				mDeptTv.setText(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(UserInfoActivity.this, "通讯录根id配置错误", Toast.LENGTH_SHORT).show();
		}
	}

	private void loadIconCache() {
		if(mRequestQueue.getCache().get(mIconUrl)!=null){
			String cachedResponse = new String(mRequestQueue.getCache().get(mIconUrl).data);
			mIconCacheStr = cachedResponse;
			System.out.println("缓存中的："+cachedResponse);
			resolveIconResponse(cachedResponse);
		}
	}
	
	private void loadIconData(){
		StringRequest request = new StringRequest(mIconUrl, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				resolveIconResponse(response);
				mIconCacheStr = response;
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				
			}
		});
		request.setShouldCache(true);
		mRequestQueue.add(request);
	}
	
	private void resolveIconResponse(String responseStr){
		try {
			JSONResult jr = JSONResult.compile(responseStr);
			mImageLoader.displayImage(jr.getResultMap().get("icon"), mIv);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
	
	
	private void init() {
		String userId = getIntent().getStringExtra(EXTRA_STRING_USER_ID);
		mUser = mUserBussiness.getUserByUserId(userId);
		String appConfigStr = (String) FileUtils.readObj(this, Constants.FILE_NAME_APP_CONFIG);
		try {
			mAppConfigJO = new JSONObject(appConfigStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String flags = null;
		try {
			flags = mAppConfigJO.getString(Constants.APP_CONFIG_PARAM_USER_ICON_FLAGS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mIconConfigParams = TextUtils.isEmpty(flags)?null:flags.split(",");
		
		
		mIconUrl = String.format(URLs.URL_ADDRESS_USER_ICON, mUser.getUsername());
		
		mIv = (ImageView) findViewById(R.id.userinfo_icon_iv);
		mNameTv = (TextView) findViewById(R.id.userinfo_name_tv);
		mDeptTv = (TextView) findViewById(R.id.userinfo_dept_tv);
		mEmailTV = (TextView) findViewById(R.id.userinfo_email);
		mTelTV = (TextView) findViewById(R.id.userinfo_tel);
		mMobileTV = (TextView) findViewById(R.id.userinfo_mobile);
		mWorkAddressTV = (TextView) findViewById(R.id.userinfo_address);
		mMobileTV.setOnClickListener(this);
		if (!mSystemBussiness.containsMenuWithAppURL("app:{$message}")) {

		}

		if (MportalApplication.app.getAllowChat() == App.ALLOW_CHAT_FALSE) {
			// 当没有聊天功能时隐藏『开始沟通』
			setVisibilityOfViewByResId(R.id.userinfo_begin_talk, View.GONE);
		}

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.userinfo_begin_talk:
			onBeginTalkClicked();
			break;
		case R.id.userinfo_email_lay:
			// 系统邮件系统的动作为android.content.Intent.ACTION_SEND
			Intent email = new Intent(android.content.Intent.ACTION_SEND);
			email.setType("plain/text");
			// 设置邮件默认地址
			email.putExtra(android.content.Intent.EXTRA_EMAIL, mUser.getEmail());
			// // 设置邮件默认标题
			startActivity(Intent.createChooser(email, " 请选择邮件发送软件"));
			mUserBussiness.recordUser(mUser.getUserId());
			break;
		case R.id.userinfo_mobile_lay:
			String mpbile = mUser.getMobile();
			if (TextUtils.isEmpty(mpbile)) {
				Toast.makeText(this, "手机号不存在!", Toast.LENGTH_SHORT).show();
			} else {

				ContactDailog dialog = new ContactDailog(UserInfoActivity.this, R.style.dialog,
						new ContactDailog.ContactDailogListener() {
							String mpbile = mUser.getMobile();

							@Override
							public void onSmsClick() {
								Uri smsToUri = Uri.parse("smsto:" + mpbile);
								Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO);
								mIntent.setData(smsToUri);
								startActivity(mIntent);
								mUserBussiness.recordUser(mUser.getUserId());
							}

							@Override
							public void onCallClick() {
								Intent intentCall = new Intent(android.content.Intent.ACTION_CALL);
								intentCall.setData(Uri.parse("tel:" + mpbile));
								startActivity(intentCall);
								mUserBussiness.recordUser(mUser.getUserId());
							}
						});
				dialog.show();
			}
			break;
		case R.id.userinfo_tel_lay:
			final String tel = mUser.getWorkTEL();
			if (tel == null || tel.equals("")) {
				Toast.makeText(this, "电话号码不存在!", Toast.LENGTH_SHORT).show();
			} else {
				Intent intentCall = new Intent(android.content.Intent.ACTION_CALL);
				intentCall.setData(Uri.parse("tel:" + tel));
				startActivity(intentCall);
				mUserBussiness.recordUser(mUser.getUserId());
			}
			break;
		case R.id.userinfo_add2contact:
			addContact();
			break;
		case R.id.userinfo_icon_iv:
			//当头像地址不为空且允许显示头像时打开头像
			boolean isAllowOpen = mIconConfigParams!=null&&mIconConfigParams.length>3&&"1".equals(mIconConfigParams[3]);
			if(!TextUtils.isEmpty(mIconCacheStr)&&isAllowOpen){
				Intent ivIntent = new Intent(this, ImageViewActivity.class);
				JSONResult jr = JSONResult.compile(mIconCacheStr);
				try {
					ivIntent.putExtra(ImageViewActivity.EXTRA_STRING_IMG_URL, jr.getResultMap().get("icon"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				startActivity(ivIntent);
			}
			break;
		default:
			break;
		}
	}

	private void onBeginTalkClicked() {
		ProgressHUD.show(this);
		String url = String.format(URLs.URL_CHAT_GET_CHAT_GROUP_ID, MportalApplication.user.getUsername(),mUser.getUsername());
		mRequestQueue.add(new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				JSONResult jr = JSONResult.compile(response);
				if(jr.resultCode==JSONResult.RESULT_CODE_SUCCESS){
					String groupId = null;
					try {
						groupId = jr.getResultMap().get("groupid");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					ChatActivity.startActivity(UserInfoActivity.this, "",groupId,ChatActivity.CHAT_TYPE_SINGLE,mUser.getTrueName());
					ProgressHUD.dismissProgressHUDInThisContext(UserInfoActivity.this);
				}else{
					newChat();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(UserInfoActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
				ProgressHUD.dismissProgressHUDInThisContext(UserInfoActivity.this);
			}
		}));
		
		
	}
	
	private void newChat() {
		
		String url = String.format(URLs.URL_CHAT_CREATE_CHAT, MportalApplication.user.getUsername(),MportalApplication.user.getUsername()+","+mUser.getUsername(),"1");
		StringRequest request = new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				ProgressHUD.dismissProgressHUDInThisContext(UserInfoActivity.this);
				JSONResult jr = JSONResult.compile(response);
				if(jr.resultCode==JSONResult.RESULT_CODE_SUCCESS){
					try {
						String groupId = jr.getResultMap().get("groupid");
						ChatActivity.startActivity(UserInfoActivity.this, "",groupId,ChatActivity.CHAT_TYPE_SINGLE,mUser.getTrueName());
						finish();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressHUD.dismissProgressHUDInThisContext(UserInfoActivity.this);
			}
		});
		request.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, 1.0f));
		mRequestQueue.add(request);
	}

	public static void startActivity(Context context, String userId) {
		Intent i = new Intent(context, UserInfoActivity.class);
		i.putExtra(EXTRA_STRING_USER_ID, userId);
		context.startActivity(i);
	}

	// 添加联系人
	public void addContact() {
        
        Intent it = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(
        		Uri.parse("content://com.android.contacts"), "contacts"));
        		it.setType("vnd.android.cursor.dir/person");
        		// it.setType("vnd.android.cursor.dir/contact");
        		// it.setType("vnd.android.cursor.dir/raw_contact");
        		// 联系人姓名
        		it.putExtra(android.provider.ContactsContract.Intents.Insert.NAME, mUser.getTrueName());
        		// 公司
        		it.putExtra(android.provider.ContactsContract.Intents.Insert.COMPANY,
        		mUser.getOfficeNO());
        		// email
        		it.putExtra(android.provider.ContactsContract.Intents.Insert.EMAIL,
        		mUser.getEmail());
        		// 手机号码
        		it.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE,
        		mUser.getMobile());
        		// 单位电话
        		it.putExtra(
        		android.provider.ContactsContract.Intents.Insert.SECONDARY_PHONE,
        		mUser.getWorkTEL());
        		it.putExtra(android.provider.ContactsContract.Intents.Insert.JOB_TITLE, mDeptTv.getText());
        		// 备注信息
        		startActivity(it);
        
	}
	@Override
	public void onResponse(JSONResult jsonresult, int requestCode) {
		if(jsonresult.resultCode==JSONResult.RESULT_CODE_SUCCESS){
			List<String> deptNameStringList;
			try {
				deptNameStringList = mUserBussiness.getDepartmentStringListByUserId(mUser.getUserId(), jsonresult.getResultMap().get(Constants.APP_CONFIG_PARAM_ADBOOK_ROOT_ID));
				StringBuilder sb = new StringBuilder();
				int size = deptNameStringList.size();
				for (int i = -1; ++i < size;) {
					if (i > 0) {
						sb.append("\n" + deptNameStringList.get(i));
					} else {
						sb.append(deptNameStringList.get(i));
					}
					
				}
				mDeptTv.setText(sb.toString());
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(UserInfoActivity.this, "解析json对象失败", Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(UserInfoActivity.this, "获取APP_CONFIG_PARAM_ADBOOK_ROOT_ID失败", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onException(int resultCode, int requestCode) {
		Toast.makeText(UserInfoActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
	}

}
