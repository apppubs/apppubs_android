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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.bean.TUser;
import com.apppubs.d20.BuildConfig;
import com.apppubs.d20.R;
import com.apppubs.presenter.UserInfoPresenter;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.widget.CircleTextImageView;
import com.apppubs.ui.widget.ConfirmDialog;

public class UserInfoActivity extends BaseActivity implements OnClickListener, IUserInfoView {

    public static String EXTRA_STRING_USER_ID = "user_id";

    private static int PERMISSION_CALL_REQUEST_CODE = 1;
    private static int PERMISSION_TEL_CALL_REQUEST_CODE = 2;

    private TUser mUser;
    private CircleTextImageView mIv;
    private TextView mNameTv, mDeptTv;
    private TextView mEmailTV, mTelTV, mMobileTV, mWorkAddressTV, mInviteTV;
    private View mResendSMSBtn;
    private LinearLayout mBeginChatBtn;
    private UserInfoPresenter mPresenter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setTitle("详细信息");
        initView();
        initPresenter();
        mPresenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    private void initView() {
        setContentView(R.layout.act_userinfo);

        mIv = (CircleTextImageView) findViewById(R.id.userinfo_icon_iv);
        mNameTv = (TextView) findViewById(R.id.userinfo_name_tv);
        mDeptTv = (TextView) findViewById(R.id.userinfo_dept_tv);
        mEmailTV = (TextView) findViewById(R.id.userinfo_email);
        mTelTV = (TextView) findViewById(R.id.userinfo_tel);
        mMobileTV = (TextView) findViewById(R.id.userinfo_mobile);
        mWorkAddressTV = (TextView) findViewById(R.id.userinfo_address);
        mInviteTV = (TextView) findViewById(R.id.userinfo_welcome_tv);
        mBeginChatBtn = (LinearLayout) findViewById(R.id.userinfo_begin_talk);
        mBeginChatBtn.setOnClickListener(this);
        mBeginChatBtn.setBackgroundColor(this.getThemeColor());

        mResendSMSBtn = findViewById(R.id.act_userinfo_sendinvitemsg_ll);
        if (BuildConfig.ADBOOK_ENABLE_RESEND_INVITE_SMS) {
            mResendSMSBtn.setVisibility(View.VISIBLE);
            mResendSMSBtn.setOnClickListener(this);
        } else {
            mResendSMSBtn.setVisibility(View.GONE);
        }

        TextView tv = (TextView) findViewById(R.id.act_user_info_startchat_tv);
        tv.setText(BuildConfig.ADBOOK_START_CHAT_LABEL);
    }

    private void initPresenter() {
        UserInfoPresenter presenter = new UserInfoPresenter(this, this);
        mPresenter = presenter;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.userinfo_begin_talk:
                onBeginTalkClicked();
                break;
            case R.id.userinfo_email_lay:
                mPresenter.onButtonClicked(EMAIL_BTN);
                break;
            case R.id.userinfo_mobile_lay:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        final String[] PERMISSIONS = {Manifest.permission.CALL_PHONE};
                        requestPermissions(PERMISSIONS, PERMISSION_CALL_REQUEST_CODE);
                    } else {
                        mPresenter.onButtonClicked(MOBILE_PHONE_BTN);
                    }
                } else {
                    mPresenter.onButtonClicked(MOBILE_PHONE_BTN);
                }
                break;
            case R.id.userinfo_tel_lay:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        final String[] PERMISSIONS = {Manifest.permission.CALL_PHONE};
                        requestPermissions(PERMISSIONS, PERMISSION_TEL_CALL_REQUEST_CODE);
                    } else {
                        mPresenter.onButtonClicked(TEL_PHONE_BTN);
                    }
                } else {
                    mPresenter.onButtonClicked(TEL_PHONE_BTN);
                }
                break;
            case R.id.userinfo_add2contact:
                mPresenter.onButtonClicked(ADD_CONTACT_BTN);
                break;
            case R.id.act_userinfo_sendinvitemsg_ll:
                showConfirmSendDialog();
                break;
            case R.id.userinfo_icon_iv:
                mPresenter.onIconClicked();
                break;
            case R.id.userinfo_welcome_tv:
                showConfirmSendDialog();
                break;
            default:
                break;
        }
    }

    private void showConfirmSendDialog() {
        ConfirmDialog dialog = new ConfirmDialog(mContext, new ConfirmDialog
                .ConfirmListener() {
            @Override
            public void onOkClick() {
                mPresenter.onConfirmSendSMS();
            }

            @Override
            public void onCancelClick() {

            }
        }, "确定发送？", "给 " + mUser.getTrueName() + " 发送客户端安装短信", "取消", "确定");
        dialog.show();
    }

    private void onBeginTalkClicked() {
        mPresenter.onButtonClicked(START_CHAT_BTN);
    }

    public static void startActivity(Context context, String userId) {
        Intent i = new Intent(context, UserInfoActivity.class);
        i.putExtra(EXTRA_STRING_USER_ID, userId);
        context.startActivity(i);
    }

    @Override
    public void setBottomBtnType(int type) {
        if (type == BOTTOM_BTN_TYPE_NONE) {
            mBeginChatBtn.setVisibility(View.GONE);
            setVisibilityOfViewByResId(R.id.userinfo_welcome_tv, View.GONE);
        } else {
            if (type == BOTTOM_BTN_TYPE_ACTIVED) {
                setVisibilityOfViewByResId(R.id.userinfo_begin_talk, View.GONE);
                setVisibilityOfViewByResId(R.id.userinfo_welcome_tv, View.VISIBLE);
                mInviteTV.setText("未激活，已邀请");
                mInviteTV.setBackgroundColor(Color.parseColor("#CCCCCC"));
                mInviteTV.setOnClickListener(null);
            } else if (type == BOTTOM_BTN_TYPE_ACTIVE) {
                setVisibilityOfViewByResId(R.id.userinfo_begin_talk, View.GONE);
                setVisibilityOfViewByResId(R.id.userinfo_welcome_tv, View.VISIBLE);
                mInviteTV.setText("未激活，点击邀请");
                mInviteTV.setBackgroundColor(mContext.getResources().getColor(R.color.common_notify_red));
                mInviteTV.setOnClickListener(this);
            } else {
                setVisibilityOfViewByResId(R.id.userinfo_begin_talk, View.VISIBLE);
                setVisibilityOfViewByResId(R.id.userinfo_welcome_tv, View.GONE);
            }
        }
    }

    @Override
    public String getUserId() {
        String userId = getIntent().getStringExtra(EXTRA_STRING_USER_ID);
        return userId;
    }

    @Override
    public void setUser(TUser user) {
        mUser = user;
        mNameTv.setText(mUser.getTrueName());
        if (!TextUtils.isEmpty(mUser.getEmail())) {
            mEmailTV.setText(mUser.getEmail());
        }
        if (!TextUtils.isEmpty(mUser.getWorkTEL())) {
            mTelTV.setText(mUser.getWorkTEL());
        }
        if (!TextUtils.isEmpty(mUser.getMobile())) {
            mMobileTV.setText(mUser.getMobile());
        }
        if (!TextUtils.isEmpty(mUser.getOfficeNO())) {
            mWorkAddressTV.setText(mUser.getOfficeNO());
        }
    }

    @Override
    public void setDepartmentStr(String departmentStr) {
        mDeptTv.setText(departmentStr);
    }

    @Override
    public void showIcon(String iconURL, String name , boolean needZoom) {
        mIv.setVisibility(View.VISIBLE);
        if (needZoom){
            mIv.setOnClickListener(this);
        }
        mIv.setText(name);
        mImageLoader.displayImage(iconURL, mIv);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALL_REQUEST_CODE) {
            if (isAllPermissionGranted(grantResults)) {
                mPresenter.onButtonClicked(MOBILE_PHONE_BTN);
            } else {
                Toast.makeText(this, "请在设置中允许拨打电话", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PERMISSION_TEL_CALL_REQUEST_CODE) {
            if (isAllPermissionGranted(grantResults)) {
                mPresenter.onButtonClicked(TEL_PHONE_BTN);
            } else {
                Toast.makeText(this, "请在设置中允许拨打电话", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isAllPermissionGranted(@NonNull int[] grantResults) {
        for (int permission : grantResults) {
            if (permission != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
