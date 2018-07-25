package com.apppubs.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.apppubs.bean.TUser;
import com.apppubs.bean.UserInfo;
import com.apppubs.AppContext;
import com.apppubs.bean.http.UserBasicInfosResult;
import com.apppubs.constant.APError;
import com.apppubs.d20.BuildConfig;
import com.apppubs.d20.R;
import com.apppubs.model.AdbookBiz;
import com.apppubs.ui.activity.ImageViewActivity;
import com.apppubs.ui.adbook.IUserInfoView;
import com.apppubs.model.UserBiz;
import com.apppubs.model.IAPCallback;
import com.apppubs.ui.widget.menudialog.MenuDialog;

import java.util.Arrays;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by zhangwen on 2017/10/24.
 */

public class UserInfoPresenter {

    private Context mContext;
    private IUserInfoView mView;
    private UserBiz mUserBiz;
    private AdbookBiz mAdbookBiz;
    private UserBasicInfosResult.Item mCurrentUserInfo;

    public UserInfoPresenter(Context context, IUserInfoView view) {
        mContext = context;
        mView = view;
        mUserBiz = UserBiz.getInstance(context);
        mAdbookBiz = AdbookBiz.getInstance(context);
    }

    public void onCreate() {
        TUser user = mAdbookBiz.getUserByUserId(mView.getUserId());
        mView.setUser(user);
        mView.setDepartmentStr(mAdbookBiz.getDepartmentStringByUserId(mView.getUserId()));
    }

    public void onResume() {
        mUserBiz.cacheUserBasicInfo(mView.getUserId(), new UserBiz.GetUserInfoCallback() {
            @Override
            public void onException(APError error) {
                mView.onError(error);
            }

            @Override
            public void onDone(UserBasicInfosResult userInfos) {
                if (userInfos.getItems().size() > 0) {
                    UserBasicInfosResult.Item item = userInfos.getItems().get(0);
                    mCurrentUserInfo = item;
                    if (!TextUtils.isEmpty(item.getAvatarURL())) {
                        mView.showIcon(item.getAvatarURL(), item.getTruename(), true);
                    } else {
                        mView.showIcon(item.getAvatarURL(), item.getTruename(), false);
                    }
                    UserInfo curUser = AppContext.getInstance(mContext).getCurrentUser();
                    if (BuildConfig.ENABLE_CHAT && !mView.getUserId().equals(curUser.getUserId())) {
                        if (AdbookBiz.getInstance(mContext).hasChatPermissionOfUser(mView.getUserId())) {
                            if (item.getAppVersionCode() > 0) {
                                mView.setBottomBtnType(IUserInfoView.BOTTOM_BTN_TYPE_NORMAL);
                            } else {
                                mView.setBottomBtnType(IUserInfoView.BOTTOM_BTN_TYPE_ACTIVE);
                            }
                        } else {
                            mView.setBottomBtnType(IUserInfoView.BOTTOM_BTN_TYPE_NONE);
                        }
                    } else {
                        mView.setBottomBtnType(IUserInfoView.BOTTOM_BTN_TYPE_NONE);
                    }
                } else {
                    Toast.makeText(mContext, "未找到用户信息", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onIconClicked() {
        if (!TextUtils.isEmpty(mCurrentUserInfo.getAvatarURL())) {
            Intent ivIntent = new Intent(mContext, ImageViewActivity.class);
            ivIntent.putExtra(ImageViewActivity.EXTRA_STRING_IMG_URL, mCurrentUserInfo.getAvatarURL());
            mContext.startActivity(ivIntent);
        }
    }

    private void sendInviteSms() {
        mView.showLoading();
        String[] userIdArr = new String[]{mView.getUserId()};
        UserBiz sysBiz = UserBiz.getInstance(mContext);
        sysBiz.inviteUsers(Arrays.asList(userIdArr), new IAPCallback() {
            @Override
            public void onDone(Object obj) {
                mView.hideLoading();
                mView.showMessage(R.string.send_success);
                mView.setBottomBtnType(IUserInfoView.BOTTOM_BTN_TYPE_ACTIVED);
            }

            @Override
            public void onException(APError error) {
                mView.hideLoading();
                mView.onError(error);
            }
        });
    }

    public void onAddContactClicked() {

        Intent it = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(
                Uri.parse("content://com.android.contacts"), "contacts"));
        it.setType("vnd.android.cursor.dir/person");
        // it.setType("vnd.android.cursor.dir/contact");
        // it.setType("vnd.android.cursor.dir/raw_contact");
        // 联系人姓名
        TUser user = AdbookBiz.getInstance(mContext).getUserByUserId(mView.getUserId());
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

    public void onButtonClicked(int index) {
        final TUser user = AdbookBiz.getInstance(mContext).getUserByUserId(mView.getUserId());
        switch (index) {
            case IUserInfoView.START_CHAT_BTN:
                RongIM.getInstance().startConversation(mContext, Conversation.ConversationType
                        .PRIVATE, mCurrentUserInfo.getUserId(), mCurrentUserInfo.getTruename());
                break;
            case IUserInfoView.MOBILE_PHONE_BTN:

                if (TextUtils.isEmpty(user.getMobile())) {
                    Toast.makeText(mContext, "手机号不存在!", Toast.LENGTH_SHORT).show();
                } else {
                    String[] menus = {"打电话", "发信息"};
                    new MenuDialog(mContext, menus, new MenuDialog.MenuDialogListener() {
                        @Override
                        public void onItemClicked(int index) {
                            if (index == 0) {
                                Intent intentCall = new Intent(android.content.Intent.ACTION_CALL);
                                intentCall.setData(Uri.parse("tel:" + user.getMobile()));
                                mContext.startActivity(intentCall);
                                AdbookBiz.getInstance(mContext).recordUser(user.getUserId());
                            } else if (index == 1) {
                                Uri smsToUri = Uri.parse("smsto:" + user.getMobile());
                                Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO);
                                mIntent.setData(smsToUri);
                                mContext.startActivity(mIntent);
                                AdbookBiz.getInstance(mContext).recordUser(user.getUserId());
                            } else {
                                Log.v("UserInfoActivity", "鬼才知道发生什么");
                            }
                        }
                    }).show();
                }
                break;
            case IUserInfoView.TEL_PHONE_BTN:
                final String tel = user.getWorkTEL();
                if (tel == null || tel.equals("")) {
                    Toast.makeText(mContext, "电话号码不存在!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentCall = new Intent(android.content.Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + tel));
                    mContext.startActivity(intentCall);
                    AdbookBiz.getInstance(mContext).recordUser(user.getUserId());
                }
                break;
            case IUserInfoView.EMAIL_BTN:
                if (TextUtils.isEmpty(user.getEmail())) {
                    Toast.makeText(mContext, "邮箱不存在!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent email = new Intent(android.content.Intent.ACTION_SENDTO);
                    email.setData(Uri.parse("mailto:" + user.getEmail()));
                    // 设置邮件默认地址
                    email.putExtra(android.content.Intent.EXTRA_EMAIL, user.getEmail());
                    // // 设置邮件默认标题
                    mContext.startActivity(Intent.createChooser(email, " 请选择邮件发送软件"));
                    AdbookBiz.getInstance(mContext).recordUser(user.getUserId());
                }
                break;
            case IUserInfoView.ADD_CONTACT_BTN:
                onAddContactClicked();
                break;
            default:

        }

    }

    public void onConfirmSendSMS() {
        sendInviteSms();
    }
}
