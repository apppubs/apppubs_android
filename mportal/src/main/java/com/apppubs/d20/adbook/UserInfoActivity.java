package com.apppubs.d20.adbook;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.apppubs.d20.AppContext;
import com.apppubs.d20.activity.BaseActivity;
import com.apppubs.d20.activity.ImageViewActivity;
import com.apppubs.d20.bean.App;
import com.apppubs.d20.bean.User;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.widget.ConfirmDialog;
import com.apppubs.d20.R;
import com.apppubs.d20.model.APResultCallback;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.widget.CircleTextImageView;
import com.apppubs.d20.widget.ProgressHUD;
import com.apppubs.d20.widget.menudialog.MenuDialog;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

public class UserInfoActivity extends BaseActivity implements OnClickListener,IUserInfoView{

	public static String EXTRA_STRING_USER_ID = "user_id";
	
	private User mUser;
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
					String[] menus = {"打电话","发信息"};
					new MenuDialog(mContext, menus, new MenuDialog.MenuDialogListener() {
						@Override
						public void onItemClicked(int index) {
							String mpbile = mUser.getMobile();
							if (index==0){
								Intent intentCall = new Intent(android.content.Intent.ACTION_CALL);
								intentCall.setData(Uri.parse("tel:" + mpbile));
								startActivity(intentCall);
								mUserBussiness.recordUser(mUser.getUserId());
							}else if (index==1){
								Uri smsToUri = Uri.parse("smsto:" + mpbile);
								Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO);
								mIntent.setData(smsToUri);
								startActivity(mIntent);
								mUserBussiness.recordUser(mUser.getUserId());
							}else{
								Log.v("UserInfoActivity","鬼才知道发生什么");
							}
						}
					}).show();
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
				mListener.onAddContactClicked();
			}
			break;
		case R.id.userinfo_icon_iv:
			mListener.onIconClicked();
			break;
		case R.id.userinfo_welcome_tv:
			mListener.onInviteButtonClicked();
			break;
		case R.id.act_userinfo_sendinvitemsg_rl:
			mListener.onSendInviteBtnCliked();
			break;
		default:
			break;
		}
	}

	private void onBeginTalkClicked() {
		RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE,mUser.getUserId(),mUser.getTrueName());
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
}
