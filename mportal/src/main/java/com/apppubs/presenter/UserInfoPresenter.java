package com.apppubs.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.apppubs.bean.User;
import com.apppubs.bean.UserInfo;
import com.apppubs.AppContext;
import com.apppubs.constant.APError;
import com.apppubs.model.SystemBiz;
import com.apppubs.ui.activity.ImageViewActivity;
import com.apppubs.ui.adbook.IUserInfoView;
import com.apppubs.ui.adbook.IUserInfoViewListener;
import com.apppubs.model.UserBiz;
import com.apppubs.model.message.UserBussiness;
import com.apppubs.model.APCallback;
import com.apppubs.model.WMHErrorCode;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.ui.widget.ProgressHUD;
import com.apppubs.ui.widget.menudialog.MenuDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Arrays;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by zhangwen on 2017/10/24.
 */

public class UserInfoPresenter implements IUserInfoViewListener {

    private Context mContext;
    private IUserInfoView mView;
    private Handler mHandler;
    private UserInfo mCurrentUser;

    public UserInfoPresenter(Context context, IUserInfoView view) {
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
                if (appContext.getAppConfig().getChatAuthFlag() == 0 || (appContext.getAppConfig
                        ().getChatAuthFlag() == 1 && ub.hasChatPermissionOfUser(mView.getUserId())
                )) {
                    if (appContext.getApp().isAllowChat() && !mView
                            .getUserId().equals(AppContext.getInstance(mContext).getCurrentUser()
                                    .getUserId())) {
                        //如果未激活显示未激活按钮，如果已激活显示开始聊天按钮,

                        if (TextUtils.isEmpty(user.getAppCodeVersion())) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mView.showInviteLabel();
                                }
                            });

                        } else {
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
                if (iconConfigParams != null && iconConfigParams.length > 0 &&
                        iconConfigParams[0].equals("1")) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mView.getIconImageView().setVisibility(View.VISIBLE);
                        }
                    });

                    if (iconConfigParams.length > 2 && iconConfigParams[2].equals("1")) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mView.getIconImageView().setScaleType(ImageView.ScaleType
                                        .CENTER_CROP);
                            }
                        });

                    }
                    if (!TextUtils.isEmpty(user.getAvatarUrl())) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ImageLoader.getInstance().displayImage(user.getAvatarUrl(), mView
                                        .getIconImageView());
                            }
                        });

                    }
                }

                //聊天头像更新
                if ("1".equals(appContext.getAppConfig().getChatFlag())) {
                    if (!TextUtils.isEmpty(user.getAvatarUrl())) {
                        RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model
                                .UserInfo(mView.getUserId(), user.getTrueName(), Uri.parse(user
                                .getAvatarUrl())));
                    }
                }
            }
        });
    }

    @Nullable
    private String[] getIconParams() {
        AppContext appContext = AppContext.getInstance(mContext);
        String flags = appContext.getAppConfig().getAdbookUserIconFlags();
        return TextUtils.isEmpty(flags) ? null : flags.split(",");
    }

    @Override
    public void onIconClicked() {
        String[] iconConfigParams = getIconParams();
        boolean isAllowOpen = iconConfigParams != null && iconConfigParams.length > 3 && "1"
                .equals(iconConfigParams[3]);
        if (!TextUtils.isEmpty(mCurrentUser.getAvatarUrl()) && isAllowOpen) {
            Intent ivIntent = new Intent(mContext, ImageViewActivity.class);
            ivIntent.putExtra(ImageViewActivity.EXTRA_STRING_IMG_URL, mCurrentUser.getAvatarUrl());
            mContext.startActivity(ivIntent);
        }
    }

    private void sendInviteSms() {
        ProgressHUD.show(mContext);
        String[] userIdArr = new String[]{mView.getUserId()};
        SystemBiz sysBiz = SystemBiz.getInstance(mContext);
        sysBiz.inviteUsers(Arrays.asList(userIdArr), new APCallback() {
            @Override
            public void onDone(Object obj) {
                ProgressHUD.dismissProgressHUDInThisContext(mContext);
                Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();
                mView.disableInviteLabel();
            }

            @Override
            public void onException(APError excepCode) {
                ProgressHUD.dismissProgressHUDInThisContext(mContext);
                Toast.makeText(mContext, "发送邀请短信失败!", Toast.LENGTH_SHORT).show();
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
    public void onButtonClicked(int index) {
        final User user = UserBussiness.getInstance(mContext).getUserByUserId(mView.getUserId());
        switch (index) {
            case START_CHAT_BTN:
                RongIM.getInstance().startConversation(mContext, Conversation.ConversationType
                        .PRIVATE, mCurrentUser.getUserId(), mCurrentUser.getTrueName());
                break;
            case MOBILE_PHONE_BTN:

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
                                UserBussiness.getInstance(mContext).recordUser(user.getUserId());
                            } else if (index == 1) {
                                Uri smsToUri = Uri.parse("smsto:" + user.getMobile());
                                Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO);
                                mIntent.setData(smsToUri);
                                mContext.startActivity(mIntent);
                                UserBussiness.getInstance(mContext).recordUser(user.getUserId());
                            } else {
                                Log.v("UserInfoActivity", "鬼才知道发生什么");
                            }
                        }
                    }).show();
                }
                break;
            case TEL_PHONE_BTN:
                final String tel = user.getWorkTEL();
                if (tel == null || tel.equals("")) {
                    Toast.makeText(mContext, "电话号码不存在!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentCall = new Intent(android.content.Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + tel));
                    mContext.startActivity(intentCall);
                    UserBussiness.getInstance(mContext).recordUser(user.getUserId());
                }
                break;
            case EMAIL_BTN:
                if (TextUtils.isEmpty(user.getEmail())) {
                    Toast.makeText(mContext, "邮箱不存在!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent email = new Intent(android.content.Intent.ACTION_SENDTO);
                    email.setData(Uri.parse("mailto:"+user.getEmail()));
                    // 设置邮件默认地址
                    email.putExtra(android.content.Intent.EXTRA_EMAIL, user.getEmail());
                    // // 设置邮件默认标题
                    mContext.startActivity(Intent.createChooser(email, " 请选择邮件发送软件"));
                    UserBussiness.getInstance(mContext).recordUser(user.getUserId());
                }
                break;
            case INVITE_BTN:
            case RE_SEND_INVITE_BTN:
                ConfirmDialog dialog = new ConfirmDialog(mContext, new ConfirmDialog
                        .ConfirmListener() {
                    @Override
                    public void onOkClick() {
                        sendInviteSms();
                    }

                    @Override
                    public void onCancelClick() {

                    }
                }, "确定发送？", "给 " + mCurrentUser.getTrueName() + " 发送客户端安装短信", "取消", "确定");
                dialog.show();
                break;
            case ADD_CONTACT_BTN:
                onAddContactClicked();
                break;
            default:

        }

    }

}
