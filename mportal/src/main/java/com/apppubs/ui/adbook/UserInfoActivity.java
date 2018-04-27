package com.apppubs.ui.adbook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.bean.TUser;
import com.apppubs.presenter.UserInfoPresenter;
import com.apppubs.d20.R;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.widget.CircleTextImageView;

import java.util.List;

public class UserInfoActivity extends BaseActivity implements OnClickListener,IUserInfoView{

	public static String EXTRA_STRING_USER_ID = "user_id";

	private static int PERMISSION_CALL_REQUEST_CODE  = 1;
	private static int PERMISSION_TEL_CALL_REQUEST_CODE  = 2;

	
	private TUser mUser;
	private CircleTextImageView mIv;
	private TextView mNameTv, mDeptTv;
	private TextView mEmailTV, mTelTV, mMobileTV, mWorkAddressTV,mInviteTV;
	private LinearLayout mBeginChatBtn;
	private boolean mShouldShowDetail;
	private IUserInfoViewListener mListener;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.act_userinfo);
		setTitle("详细信息");
		init();
		optionView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mListener!=null){
			mListener.onResume();
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

		UserInfoPresenter presenter = new UserInfoPresenter(this,this);
		mListener = presenter;
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
				mListener.onButtonClicked(IUserInfoViewListener.EMAIL_BTN);
			}
			break;
		case R.id.userinfo_mobile_lay:
			if(mShouldShowDetail){
				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
					int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
					if (permission != PackageManager.PERMISSION_GRANTED) {
						final String[] PERMISSIONS = {Manifest.permission.CALL_PHONE};
						requestPermissions(PERMISSIONS,PERMISSION_CALL_REQUEST_CODE);
					}else{
						mListener.onButtonClicked(IUserInfoViewListener.MOBILE_PHONE_BTN);
					}
				}else{
					mListener.onButtonClicked(IUserInfoViewListener.MOBILE_PHONE_BTN);
				}
			}
			break;
		case R.id.userinfo_tel_lay:
			if (mShouldShowDetail){
				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
					int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
					if (permission != PackageManager.PERMISSION_GRANTED) {
						final String[] PERMISSIONS = {Manifest.permission.CALL_PHONE};
						requestPermissions(PERMISSIONS,PERMISSION_TEL_CALL_REQUEST_CODE);
					}else{
						mListener.onButtonClicked(IUserInfoViewListener.TEL_PHONE_BTN);
					}
				}else{
					mListener.onButtonClicked(IUserInfoViewListener.TEL_PHONE_BTN);
				}

			}
			break;
		case R.id.userinfo_add2contact:
			if (mShouldShowDetail){
				mListener.onButtonClicked(IUserInfoViewListener.ADD_CONTACT_BTN);
			}
			break;
		case R.id.userinfo_icon_iv:
			mListener.onIconClicked();
			break;
		case R.id.userinfo_welcome_tv:
			mListener.onButtonClicked(IUserInfoViewListener.RE_SEND_INVITE_BTN);
			break;
		case R.id.act_userinfo_sendinvitemsg_rl:
			mListener.onButtonClicked(IUserInfoViewListener.INVITE_BTN);
			break;
		default:
			break;
		}
	}

	private void onBeginTalkClicked() {
		mListener.onButtonClicked(IUserInfoViewListener.START_CHAT_BTN);
	}

	public static void startActivity(Context context, String userId) {
		Intent i = new Intent(context, UserInfoActivity.class);
		i.putExtra(EXTRA_STRING_USER_ID, userId);
		context.startActivity(i);
	}

	@Override
	public void showStartChatLabel() {
		setVisibilityOfViewByResId(R.id.userinfo_begin_talk, View.VISIBLE);
		setVisibilityOfViewByResId(R.id.userinfo_welcome_tv,View.GONE);
	}

	@Override
	public void showInviteLabel() {
		setVisibilityOfViewByResId(R.id.userinfo_begin_talk, View.GONE);
		setVisibilityOfViewByResId(R.id.userinfo_welcome_tv,View.VISIBLE);
	}

	@Override
	public void showSendInviteMsgBtn() {
		setVisibilityOfViewByResId(R.id.act_userinfo_sendinvitemsg_ll,View.VISIBLE);
	}

	@Override
	public void disableInviteLabel() {
		mInviteTV.setText("未激活，已邀请");
		mInviteTV.setBackgroundColor(Color.parseColor("#CCCCCC"));
		mInviteTV.setOnClickListener(null);
	}

	@Override
	public String getUserId() {
		return mUser.getUserId();
	}

	@Override
	public ImageView getIconImageView() {
		return mIv;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode==PERMISSION_CALL_REQUEST_CODE){
			if (isAllPermissionGranted(grantResults)){
				mListener.onButtonClicked(IUserInfoViewListener.MOBILE_PHONE_BTN);
			}else {
				Toast.makeText(this, "请在设置中允许拨打电话", Toast.LENGTH_LONG).show();
			}
		}else if(requestCode==PERMISSION_TEL_CALL_REQUEST_CODE){
			if (isAllPermissionGranted(grantResults)){
				mListener.onButtonClicked(IUserInfoViewListener.TEL_PHONE_BTN);
			}else {
				Toast.makeText(this, "请在设置中允许拨打电话", Toast.LENGTH_LONG).show();
			}
		}
	}

	private boolean isAllPermissionGranted(@NonNull int[] grantResults) {
		for (int permission : grantResults){
			if (permission!= PackageManager.PERMISSION_GRANTED){
				return false;
			}
		}
		return true;
	}
}
