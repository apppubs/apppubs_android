package com.apppubs.d20.activity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apppubs.d20.AppContext;
import com.apppubs.d20.bean.App;
import com.apppubs.d20.bean.User;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.widget.ConfirmDialog;
import com.apppubs.d20.widget.ContactDailog;
import com.apppubs.d20.R;
import com.apppubs.d20.model.BussinessCallbackCommon;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.widget.CircleTextImageView;
import com.apppubs.d20.widget.ProgressHUD;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

public class UserInfoActivity extends BaseActivity implements OnClickListener{

	public static String EXTRA_STRING_USER_ID = "user_id";
	
	private User mUser;
	private CircleTextImageView mIv;
	private TextView mNameTv, mDeptTv;
	private TextView mEmailTV, mTelTV, mMobileTV, mWorkAddressTV,mInviteTV;
	private LinearLayout mBeginChatBtn;
	private String mUserInfoUrl;
	private String[] mIconConfigParams;
	private boolean mShouldShowDetail;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.act_userinfo);
		setTitle("详细信息");
		init();
		optionView();
		loadUserInfoFromNet();
	}

	private void loadUserInfoFromNet() {
		handleUserInfoResponse(getUrlCache(mUserInfoUrl));
		addStringRequest(mUserInfoUrl, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				handleUserInfoResponse(response);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
	}

	private void handleUserInfoResponse(String response){
		if (TextUtils.isEmpty(response)){
			return ;
		}
		JSONResult jr = JSONResult.compile(response);
		//底部按钮
		//不需要权限限制或需要限制而且有权限的情况下并且在移动门户下有用户时进行按钮的下一步逻辑判断
		if (mAppContext.getAppConfig().getChatAuthFlag()==0||(mAppContext.getAppConfig().getChatAuthFlag()==1&&mUserBussiness.hasChatPermissionOfUser(mUser.getUserId()))){
			if (mAppContext.getApp().getAllowChat() == App.ALLOW_CHAT_TRUE&&!mUser.getUserId().equals(AppContext.getInstance(mContext).getCurrentUser().getUserId())&&jr.resultCode==1) {
				//如果未激活显示未激活按钮，如果已激活显示开始聊天按钮,
				Map<String,String> resultMap = jr.getResultMap();
				if (jr.resultCode==1&&!TextUtils.isEmpty(resultMap.get("appcodeversion"))){
					setVisibilityOfViewByResId(R.id.userinfo_begin_talk, View.VISIBLE);
					setVisibilityOfViewByResId(R.id.userinfo_welcome_tv,View.GONE);
				}else{
					setVisibilityOfViewByResId(R.id.userinfo_welcome_tv,View.VISIBLE);
					setVisibilityOfViewByResId(R.id.userinfo_begin_talk,View.GONE);
				}

			}
		}
		//头像
		if(mIconConfigParams!=null&&mIconConfigParams.length>0&&mIconConfigParams[0].equals("1")){
			mIv.setVisibility(View.VISIBLE);
			if(mIconConfigParams.length>2&&mIconConfigParams[2].equals("1")){
				mIv.setScaleType(ScaleType.CENTER_CROP);
			}
			if (jr.resultCode==1){
				String photoUrl = (String)jr.getResultMap().get("photourl");
				if(!TextUtils.isEmpty(photoUrl)){
					mImageLoader.displayImage(photoUrl, mIv);
				}
			}
		}

		if(jr.resultCode==1&&"1".equals(mAppContext.getAppConfig().getChatFlag())){
			RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(mUser.getUserId(), mUser.getTrueName(), Uri.parse((String)jr.getResultMap().get("photourl"))));
		}

	}

	private void optionView() {
		if (mUser.getIcon() != null && !mUser.getIcon().equals("")) {
			mImageLoader.displayImage(mUser.getIcon(), mIv);
		}

		if (mShouldShowDetail){
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
		}else{
			setVisibilityOfViewByResId(R.id.act_userinfo_add2contact_ll,View.GONE);
		}


		mNameTv.setText(mUser.getTrueName());
		fillDepartment();
	}

	private void fillDepartment() {
		try {
			String id = mAppContext.getAppConfig().getAdbookRootId();
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

	private void init() {
		String userId = getIntent().getStringExtra(EXTRA_STRING_USER_ID);
		mUser = mUserBussiness.getUserByUserId(userId);

		String flags = AppContext.getInstance(this).getAppConfig().getAdbookUserIconFlags();
		mIconConfigParams = TextUtils.isEmpty(flags)?null:flags.split(",");


		mUserInfoUrl =  String.format(URLs.URL_USER_INFO, mUser.getUserId());

		mIv = (CircleTextImageView) findViewById(R.id.userinfo_icon_iv);
		mIv.setText(mUser.getTrueName());
		mNameTv = (TextView) findViewById(R.id.userinfo_name_tv);
		mDeptTv = (TextView) findViewById(R.id.userinfo_dept_tv);
		mEmailTV = (TextView) findViewById(R.id.userinfo_email);
		mTelTV = (TextView) findViewById(R.id.userinfo_tel);
		mMobileTV = (TextView) findViewById(R.id.userinfo_mobile);
		mWorkAddressTV = (TextView) findViewById(R.id.userinfo_address);
		mInviteTV = (TextView) findViewById(R.id.userinfo_welcome_tv);
		mInviteTV.setOnClickListener(this);

		//限制权限并且没有权限时不显示，其他情况均显示
		if (mAppContext.getAppConfig().getAdbookAuthFlag()==1&&!mUserBussiness.hasReadPermissionOfUser(userId)){
			mShouldShowDetail = false;
		}else {
			mShouldShowDetail = true;
		}
		mBeginChatBtn = (LinearLayout) findViewById(R.id.userinfo_begin_talk);
		mBeginChatBtn.setBackgroundColor(this.getThemeColor());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.userinfo_begin_talk:
			onBeginTalkClicked();
			break;
		case R.id.userinfo_email_lay:
			if (mShouldShowDetail){
				if (TextUtils.isEmpty(mUser.getEmail())) {
					Toast.makeText(this, "邮箱不存在!", Toast.LENGTH_SHORT).show();
				}else{
					// 系统邮件系统的动作为android.content.Intent.ACTION_SEND
					Intent email = new Intent(android.content.Intent.ACTION_SEND);
					email.setType("plain/text");
					// 设置邮件默认地址
					email.putExtra(android.content.Intent.EXTRA_EMAIL, mUser.getEmail());
					// // 设置邮件默认标题
					startActivity(Intent.createChooser(email, " 请选择邮件发送软件"));
					mUserBussiness.recordUser(mUser.getUserId());
				}
			}
			break;
		case R.id.userinfo_mobile_lay:
			if(mShouldShowDetail){
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
			}
			break;
		case R.id.userinfo_tel_lay:
			if (mShouldShowDetail){
				final String tel = mUser.getWorkTEL();
				if (tel == null || tel.equals("")) {
					Toast.makeText(this, "电话号码不存在!", Toast.LENGTH_SHORT).show();
				} else {
					Intent intentCall = new Intent(android.content.Intent.ACTION_CALL);
					intentCall.setData(Uri.parse("tel:" + tel));
					startActivity(intentCall);
					mUserBussiness.recordUser(mUser.getUserId());
				}
			}
			break;
		case R.id.userinfo_add2contact:
			if (mShouldShowDetail){
				addContact();
			}
			break;
		case R.id.userinfo_icon_iv:
			//当头像地址不为空且允许显示头像时打开头像
			boolean isAllowOpen = mIconConfigParams!=null&&mIconConfigParams.length>3&&"1".equals(mIconConfigParams[3]);
			String avatarUrl = (String) JSONResult.compile( getUrlCache(mUserInfoUrl)).getResultMap().get("photourl");
			if(!TextUtils.isEmpty(avatarUrl)&&isAllowOpen){
				Intent ivIntent = new Intent(this, ImageViewActivity.class);
				JSONResult jr = JSONResult.compile(getUrlCache(mUserInfoUrl));
				ivIntent.putExtra(ImageViewActivity.EXTRA_STRING_IMG_URL, (String)jr.getResultMap().get("photourl"));
				startActivity(ivIntent);
			}
			break;
			case R.id.userinfo_welcome_tv:
				ConfirmDialog dialog = new ConfirmDialog(this, new ConfirmDialog.ConfirmListener() {
					@Override
					public void onOkClick() {
						sendInviteSms();
					}

					@Override
					public void onCancelClick() {

					}
				}, "送邀请短信？", "取消", "确定");
				dialog.show();
				break;
		default:
			break;
		}
	}

	private void sendInviteSms() {
		ProgressHUD.show(this);
		String[] userIdArr = new String[]{mUser.getUserId()};
		mSystemBussiness.inviteUsers(Arrays.asList(userIdArr), new BussinessCallbackCommon() {
			@Override
			public void onDone(Object obj) {
				ProgressHUD.dismissProgressHUDInThisContext(UserInfoActivity.this);
				Toast.makeText(getApplicationContext(),"发送成功",Toast.LENGTH_SHORT).show();
				makeInviteGray();
			}

			@Override
			public void onException(int excepCode) {
				ProgressHUD.dismissProgressHUDInThisContext(UserInfoActivity.this);
				Toast.makeText(getApplicationContext(),"发送邀请短信失败!",Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void makeInviteGray() {
		mInviteTV.setText("未激活，已邀请");
		mInviteTV.setBackgroundColor(Color.parseColor("#CCCCCC"));
		mInviteTV.setOnClickListener(null);
	}

	private void onBeginTalkClicked() {
//		ProgressHUD.show(this);
//		String url = String.format(URLs.URL_CHAT_GET_CHAT_GROUP_ID, MportalApplication.user.getUsername(),mUser.getUsername());
//		mRequestQueue.add(new StringRequest(url, new Listener<String>() {
//
//			@Override
//			public void onResponse(String response) {
//				JSONResult jr = JSONResult.compile(response);
//				if(jr.resultCode==JSONResult.RESULT_CODE_SUCCESS){
//					String groupId = null;
//					groupId = (String)jr.getResultMap().get("groupid");
//					ChatActivity.startActivity(UserInfoActivity.this, "",groupId,ChatActivity.CHAT_TYPE_SINGLE,mUser.getTrueName());
//					ProgressHUD.dismissProgressHUDInThisContext(UserInfoActivity.this);
//				}else{
//					newChat();
//				}
//			}
//		}, new ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				Toast.makeText(UserInfoActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
//				ProgressHUD.dismissProgressHUDInThisContext(UserInfoActivity.this);
//			}
//		}));

		RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE,mUser.getUserId(),mUser.getTrueName());
	}
	
	private void newChat() {
		
		String url = String.format(URLs.URL_CHAT_CREATE_CHAT, AppContext.getInstance(mContext).getCurrentUser().getUsername(), AppContext.getInstance(mContext).getCurrentUser().getUsername()+","+mUser.getUsername(),"1");
		StringRequest request = new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				ProgressHUD.dismissProgressHUDInThisContext(UserInfoActivity.this);
				JSONResult jr = JSONResult.compile(response);
				if(jr.resultCode==JSONResult.RESULT_CODE_SUCCESS){
					String groupId = (String)jr.getResultMap().get("groupid");
					ChatActivity.startActivity(UserInfoActivity.this, "",groupId,ChatActivity.CHAT_TYPE_SINGLE,mUser.getTrueName());
					finish();
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

}
