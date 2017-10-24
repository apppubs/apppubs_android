package com.apppubs.d20.adbook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.R;
import com.apppubs.d20.activity.ImageViewActivity;
import com.apppubs.d20.bean.App;
import com.apppubs.d20.bean.User;
import com.apppubs.d20.bean.UserInfo;
import com.apppubs.d20.message.model.UserBussiness;
import com.apppubs.d20.model.APResultCallback;
import com.apppubs.d20.model.SystemBussiness;
import com.apppubs.d20.model.WMHErrorCode;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.widget.ConfirmDialog;
import com.apppubs.d20.widget.ProgressHUD;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Arrays;
import java.util.Map;

import io.rong.imkit.RongIM;

/**
 * Created by zhangwen on 2017/10/24.
 */

public class UserInfoPresenter implements IUserInfoViewListener{

	private Context mContext;
	private IUserInfoView mView;
	private Handler mHandler;
	private UserInfo mCurrentUser;

	public UserInfoPresenter(Context context,IUserInfoView view){
		mContext = context;
		mView = view;
		mHandler = new Handler();
	}

	@Override
	public void onResume() {
		final UserBiz userBiz = UserBiz.getInstance(mContext);
		userBiz.getUserInfo(mView.getUserId(), new UserBiz.GetUserInfoCallback() {
			@Override
			public void onException(WMHErrorCode code) {

			}

			@Override
			public void onDone(final UserInfo user) {
				mCurrentUser = user;
				//底部聊天按钮的显示与隐藏
				AppContext appContext = AppContext.getInstance(mContext);
				UserBussiness ub = UserBussiness.getInstance(mContext);
				//底部按钮
				//不需要权限限制或需要限制而且有权限的情况下并且在移动门户下有用户时进行按钮的下一步逻辑判断
				if (appContext.getAppConfig().getChatAuthFlag()==0||(appContext.getAppConfig().getChatAuthFlag()==1&&ub.hasChatPermissionOfUser(mView.getUserId()))){
					if (appContext.getApp().getAllowChat() == App.ALLOW_CHAT_TRUE&&!mView.getUserId().equals(AppContext.getInstance(mContext).getCurrentUser().getUserId())) {
						//如果未激活显示未激活按钮，如果已激活显示开始聊天按钮,

						if (TextUtils.isEmpty(user.getAppCodeVersion())){
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									mView.showInviteLabel();
								}
							});

						}else{
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									mView.showStartChatLabel();
									mView.showSendInviteMsgBtn();
								}
							});

						}

					}
				}

				//头像显示
				String[] iconConfigParams = getIconParams();
				if(iconConfigParams!=null&&iconConfigParams.length>0&&iconConfigParams[0].equals("1")){
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mView.getIconImageView().setVisibility(View.VISIBLE);
						}
					});

					if(iconConfigParams.length>2&&iconConfigParams[2].equals("1")){
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mView.getIconImageView().setScaleType(ImageView.ScaleType.CENTER_CROP);
							}
						});

					}
					if(!TextUtils.isEmpty(user.getAvatarUrl())){
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								ImageLoader.getInstance().displayImage(user.getAvatarUrl(),mView.getIconImageView());
							}
						});

					}
				}

				//聊天头像更新
				if("1".equals(appContext.getAppConfig().getChatFlag())){
					if (!TextUtils.isEmpty(user.getAvatarUrl())){
						RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(mView.getUserId(), user.getTrueName(), Uri.parse(user.getAvatarUrl())));
					}
				}
			}
		});
	}

	@Nullable
	private String[] getIconParams() {
		AppContext appContext = AppContext.getInstance(mContext);
		String flags = appContext.getAppConfig().getAdbookUserIconFlags();
		return TextUtils.isEmpty(flags)?null:flags.split(",");
	}

	@Override
	public void onInviteButtonClicked() {
		ConfirmDialog dialog = new ConfirmDialog(mContext, new ConfirmDialog.ConfirmListener() {
			@Override
			public void onOkClick() {
				sendInviteSms();
			}

			@Override
			public void onCancelClick() {

			}
		}, "确定发送？","给 "+mCurrentUser.getTrueName()+" 发送客户端安装短信", "取消", "确定");
		dialog.show();
	}

	@Override
	public void onIconClicked() {
		String[] iconConfigParams = getIconParams();
		boolean isAllowOpen = iconConfigParams!=null&&iconConfigParams.length>3&&"1".equals(iconConfigParams[3]);
		if(!TextUtils.isEmpty(mCurrentUser.getAvatarUrl())&&isAllowOpen){
			Intent ivIntent = new Intent(mContext, ImageViewActivity.class);
			ivIntent.putExtra(ImageViewActivity.EXTRA_STRING_IMG_URL, mCurrentUser.getAvatarUrl());
			mContext.startActivity(ivIntent);
		}
	}

	private void sendInviteSms() {
		ProgressHUD.show(mContext);
		String[] userIdArr = new String[]{mView.getUserId()};
		SystemBussiness sysBiz = SystemBussiness.getInstance(mContext);
		sysBiz.inviteUsers(Arrays.asList(userIdArr), new APResultCallback() {
			@Override
			public void onDone(Object obj) {
				ProgressHUD.dismissProgressHUDInThisContext(mContext);
				Toast.makeText(mContext,"发送成功",Toast.LENGTH_SHORT).show();
				mView.disableInviteLabel();
			}

			@Override
			public void onException(int excepCode) {
				ProgressHUD.dismissProgressHUDInThisContext(mContext);
				Toast.makeText(mContext,"发送邀请短信失败!",Toast.LENGTH_SHORT).show();
			}
		});
	}


	@Override
	public void onAddContactClicked() {

		Intent it = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(
				Uri.parse("content://com.android.contacts"), "contacts"));
		it.setType("vnd.android.cursor.dir/person");
		// it.setType("vnd.android.cursor.dir/contact");
		// it.setType("vnd.android.cursor.dir/raw_contact");
		// 联系人姓名
		User user = UserBussiness.getInstance(mContext).getUserByUserId(mView.getUserId());
		it.putExtra(android.provider.ContactsContract.Intents.Insert.NAME, user.getTrueName());
		// 公司
		it.putExtra(android.provider.ContactsContract.Intents.Insert.COMPANY,
				user.getOfficeNO());
		// email
		it.putExtra(android.provider.ContactsContract.Intents.Insert.EMAIL,
				user.getEmail());
		// 手机号码
		it.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE,
				user.getMobile());
		// 单位电话
		it.putExtra(
				android.provider.ContactsContract.Intents.Insert.SECONDARY_PHONE,
				user.getWorkTEL());
//		it.putExtra(android.provider.ContactsContract.Intents.Insert.JOB_TITLE, mDeptTv.getText());
		// 备注信息
		mContext.startActivity(it);

	}

	@Override
	public void onSendInviteBtnCliked() {
		this.onInviteButtonClicked();
	}
}
