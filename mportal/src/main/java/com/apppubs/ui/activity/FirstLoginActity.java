package com.apppubs.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.AppContext;
import com.apppubs.bean.App;
import com.apppubs.bean.UserInfo;
import com.apppubs.bean.http.LoginResult;
import com.apppubs.constant.APError;
import com.apppubs.d20.R;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.UserBiz;
import com.apppubs.ui.home.HomeBaseActivity;
import com.apppubs.ui.widget.ProgressHUD;
import com.apppubs.util.LogM;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstLoginActity extends BaseActivity {

    public static final int REQUEST_CODE_VERIFICATION = 100;
    public static final int LOGIN_WITH_USERNAME_AND_PASSWORD_TASK = 200;

    private LinearLayout mContainerLl;
    private TextView mTitleTv, mFristZhuce;
    private ImageView mBgIv;
    private WebView mWebview;
    // private LoadingDialog dialog;
    private EditText mUsernameTv, mPasswordTv, mPhoneEt, mUsernameEt, mOrgEt;
    private CheckBox mCheckBox;

    private ProgressHUD mProgressHUD;

    private boolean isUserSycnDone;// 用户同步是否完成
    private boolean isServiceNoSycnDone;// 服务号是否同步完成

    private int mLoginType;// 登陆类型

    private UserInfo mLoginingUser;//正在登录的用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNeedTitleBar(false);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.act_fristlogin);
        init();

        executeLoginContainerAnimation();
    }

    private void executeLoginContainerAnimation() {
        // 登录框动画
        AnimationSet animSet = new AnimationSet(true);
        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
        TranslateAnimation trans = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation
				.ABSOLUTE, 0,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.ABSOLUTE, 0);
        animSet.addAnimation(alpha);
        animSet.addAnimation(trans);
        animSet.setDuration(1100);
        mContainerLl.startAnimation(animSet);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {

        mLoginType = mAppContext.getApp().getLoginFlag();

        mContainerLl = (LinearLayout) findViewById(R.id.firstlogin_container_ll);
        mFristZhuce = (TextView) findViewById(R.id.frist_login_reg);
        mFristZhuce.setOnClickListener(this);
        mFristZhuce.setTextColor(mThemeColor);
        mFristZhuce.setVisibility(mAppContext.getApp().getAllowRegister() == 0 ? View.GONE : View
				.VISIBLE);
        mUsernameTv = (EditText) findViewById(R.id.fristregist_name);
        mPasswordTv = (EditText) findViewById(R.id.fristregist_password);
        mPhoneEt = (EditText) findViewById(R.id.firstlogin_phone_et);
        mOrgEt = (EditText) findViewById(R.id.firstlogin_orgcode_et);
        mUsernameEt = (EditText) findViewById(R.id.firstlogin_username_et);
        mTitleTv = (TextView) findViewById(R.id.firstlogin_title_tv);
        String title = mAppContext.getApp().getName();
        mTitleTv.setText(title);
        mBgIv = (ImageView) findViewById(R.id.firstlogin_bg_iv);
        LogM.log(this.getClass(), "mAppContext.getApp().getLoginPicUrl()" + mAppContext.getApp()
				.getLoginPicUrl());
        mImageLoader.displayImage(mAppContext.getApp().getLoginPicUrl(), mBgIv);
        mCheckBox = (CheckBox) findViewById(R.id.firstlogin_ckb);
        if (mLoginType == App.LOGIN_ONSTART_USE_USERNAME_PASSWORD) {

        } else if (mLoginType == App.LOGIN_ONSTART_USE_PHONE_NUMBER) {
            setVisibilityOfViewByResId(R.id.firstlogin_username_container_rl, View.GONE);
            setVisibilityOfViewByResId(R.id.firstlogin_password_container_rl, View.GONE);
            setVisibilityOfViewByResId(R.id.firstlogin_autologin_register_rl, View.GONE);
            setVisibilityOfViewByResId(R.id.firstlogin_phone_container_rl, View.VISIBLE);
        } else if (mLoginType == App.LOGIN_ONSTART_USE_USERNAME) {

            setVisibilityOfViewByResId(R.id.firstlogin_username_container_rl, View.GONE);
            setVisibilityOfViewByResId(R.id.firstlogin_password_container_rl, View.GONE);
            setVisibilityOfViewByResId(R.id.firstlogin_autologin_register_rl, View.GONE);
            setVisibilityOfViewByResId(R.id.firstlogin_phone_container_rl, View.GONE);
            setVisibilityOfViewByResId(R.id.firstlogin_username_login_container_rl, View.VISIBLE);

        } else if (mLoginType == App.LOGIN_ONSTART_USE_USERNAME_PASSWORD_ORGCODE) {
            setVisibilityOfViewByResId(R.id.firstlogin_orgcode_container_rl, View.VISIBLE);
        } else if (mLoginType == App.LOGIN_ONSTART_WEB) {
            setVisibilityOfViewByResId(R.id.firstlogin_container_ll, View.GONE);
            setVisibilityOfViewByResId(R.id.firstlogin_bg_iv, View.GONE);
            setVisibilityOfViewByResId(R.id.first_login_wv, View.VISIBLE);
            mWebview = (WebView) findViewById(R.id.first_login_wv);
            mWebview.getSettings().setDomStorageEnabled(true);
            mWebview.getSettings().setJavaScriptEnabled(true);
            mWebview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    System.out.println(url);
                    if (url.contains("app:login")) {
                        String params = url.substring(url.indexOf("{"));
                        System.out.println(params);
                        try {

                            JSONObject jo = new JSONObject(URLDecoder.decode(params, "utf-8"));
                            String username = jo.getString("username");
                            String password = jo.getString("password");
                            int autoLoginFlag = jo.getInt("autologinflag");
                            loginWithUsernameAndPassword(username, password, autoLoginFlag == 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        return true;
                    }
                    return false;
                }
            });

            mWebview.loadUrl(mAppContext.getApp().getWebLoginUrl());
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (AppContext.getInstance(mContext).getCurrentUser() != null) {
            UserInfo user = AppContext.getInstance(mContext).getCurrentUser();
            String usernameS = user.getUsername();
            if (!TextUtils.isEmpty(usernameS)) {
                if (!TextUtils.isEmpty(user.getOrgCode())) {
                    // 替换掉orgcode再进行显示，避免新的用户名让用户疑惑
                    mUsernameTv.setText(usernameS.replace(user.getOrgCode(), ""));
                } else {
                    mUsernameTv.setText(usernameS);
                }
                mOrgEt.setText(user.getOrgCode());
                mPasswordTv.requestFocus();
                mCheckBox.setChecked(mAppContext.getSettings().isAllowAutoLogin());

            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.frist_login_reg:
                BaseActivity.startActivity(this, RegisterActivity.class);
                break;
            case R.id.frist_login_login:
                login();
                break;
        }
    }

    private void login() {

        if (mLoginType == App.LOGIN_ONSTART_USE_PHONE_NUMBER) {
            String phone = mPhoneEt.getText().toString().trim();
            if (!verifyPhoneNum(phone)) {
                Toast.makeText(getApplication(), "请输入正确手机号!", Toast.LENGTH_LONG).show();
            } else {
                loginWithPhone(phone);
            }
        } else if (mLoginType == App.LOGIN_ONSTART_USE_USERNAME_PASSWORD) {

            String usetnameT = mUsernameTv.getText().toString().trim();
            String passwordT = mPasswordTv.getText().toString().trim();
            loginWithUsernameAndPassword(usetnameT, passwordT, mCheckBox.isChecked());
        } else if (mLoginType == App.LOGIN_ONSTART_USE_USERNAME) {
            String username = mUsernameEt.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(getApplication(), "请输入用户名!", Toast.LENGTH_LONG).show();
            } else {
                loginWithUsername(username);
            }
        } else if (mLoginType == App.LOGIN_ONSTART_USE_USERNAME_PASSWORD_ORGCODE) {
            String usetnameT = mUsernameTv.getText().toString().trim();
            String passwordT = mPasswordTv.getText().toString().trim();
            String orgCode = mOrgEt.getText().toString().trim();
            loginWithUsernamePasswordAndOrgId(usetnameT, passwordT, orgCode);
        }
    }

    private boolean verifyPhoneNum(String phone) {
        Pattern pattern = Pattern.compile("^1[0-9]{10}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private void loginWithUsernameAndPassword(String username, String password, boolean autoLogin) {
        if (username.isEmpty()) {
            Toast.makeText(getApplication(), "请输入用户名!", Toast.LENGTH_LONG).show();
        } else if (password.isEmpty()) {
            Toast.makeText(getApplication(), "请输入密码!", Toast.LENGTH_LONG).show();
        } else {
            mProgressHUD = ProgressHUD.show(this, "登录中...", true, false, null);
            UserBiz.getInstance(mContext).loginWithUsernameAndPwd(username, password, autoLogin,
					new IAPCallback<UserInfo>() {

                @Override
                public void onDone(UserInfo obj) {
                    enterHome();
                    ProgressHUD.dismissProgressHUDInThisContext(FirstLoginActity.this);
                }

                @Override
                public void onException(APError error) {
                    ProgressHUD.dismissProgressHUDInThisContext(FirstLoginActity.this);
                    mErrorHandler.onError(error);
                }
            });
        }
    }

    private void loginWithPhone(final String phone) {
        ProgressHUD.show(this);

        UserBiz.getInstance(this).loginWithPhone(phone, new IAPCallback<String>() {
            @Override
            public void onDone(String username) {
                ProgressHUD.dismissProgressHUDInThisContext(FirstLoginActity.this);

                Intent intent = new Intent(mContext, VerificationCodeActivity.class);
                Bundle extras = new Bundle();
                extras.putString(VerificationCodeActivity.EXTRA_STRING_PHONE, phone);
                extras.putString(VerificationCodeActivity.EXTRA_STRING_USERNAME, username);
                intent.putExtras(extras);
                startActivityForResult(intent, REQUEST_CODE_VERIFICATION);
            }

            @Override
            public void onException(APError error) {
                ProgressHUD.dismissProgressHUDInThisContext(FirstLoginActity.this);
                mErrorHandler.onError(error);
            }
        });
    }

    private void loginWithUsername(final String username) {
        ProgressHUD.show(this);

        UserBiz.getInstance(mContext).loginWithUsername(username, new IAPCallback<String>() {
            @Override
            public void onDone(String obj) {
                ProgressHUD.dismissProgressHUDInThisContext(FirstLoginActity.this);

                Intent intent = new Intent(mContext, VerificationCodeActivity.class);
                Bundle extras = new Bundle();
                extras.putString(VerificationCodeActivity.EXTRA_STRING_PHONE, obj);
                extras.putString(VerificationCodeActivity.EXTRA_STRING_USERNAME, username);
                intent.putExtras(extras);
                startActivityForResult(intent, REQUEST_CODE_VERIFICATION);
            }

            @Override
            public void onException(APError error) {
                ProgressHUD.dismissProgressHUDInThisContext(FirstLoginActity.this);
                mErrorHandler.onError(error);
            }
        });
    }

    private void enterHome() {
        if (mProgressHUD != null) {
            mProgressHUD.dismiss();
        }
        HomeBaseActivity.startHomeActivity(FirstLoginActity.this);
        finish();// 进入主界面
    }

    private void loginWithUsernamePasswordAndOrgId(String username, String password, String
			orgCode) {
        ProgressHUD.show(this);
        UserBiz.getInstance(mContext).loginWithUsernamePwdAndOrgCode(username, password, orgCode, new IAPCallback<LoginResult>() {

            @Override
            public void onDone(LoginResult obj) {
                ProgressHUD.dismissProgressHUDInThisContext(FirstLoginActity.this);
                enterHome();
            }

            @Override
            public void onException(APError error) {
                ProgressHUD.dismissProgressHUDInThisContext(FirstLoginActity.this);
                mErrorHandler.onError(error);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, R.anim.zoom_fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE_VERIFICATION && resultCode == RESULT_OK) {
            HomeBaseActivity.startHomeActivity(this);
        }
    }
}
