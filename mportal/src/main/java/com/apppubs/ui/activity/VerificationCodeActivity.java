package com.apppubs.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.d20.R;
import com.apppubs.bean.App;
import com.apppubs.constant.URLs;
import com.apppubs.util.JSONResult;
import com.apppubs.util.Utils;
import com.apppubs.ui.widget.ProgressHUD;

import cn.jpush.android.api.JPushInterface;

public class VerificationCodeActivity extends BaseActivity implements ErrorListener {

    public static final String EXTRA_STRING_PHONE = "phone";
    public static final String EXTRA_STRING_USERNAME = "username";

    private String mPhone;
    private String mUsername;
    private EditText mVerficationCodeEt;
    private Button mResendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_verification_code);
        setTitle("获取验证码");

        mVerficationCodeEt = (EditText) findViewById(R.id.verification_et);
        mResendBtn = (Button) findViewById(R.id.verification_resend_btn);
        mPhone = getIntent().getStringExtra(EXTRA_STRING_PHONE);
        mUsername = getIntent().getStringExtra(EXTRA_STRING_USERNAME);
        String disposePhone = null;
        if (!TextUtils.isEmpty(mPhone)) {
            disposePhone = mPhone.substring(0, 3) + "xxxxxx" + mPhone.substring(9);
        }
        fillTextView(R.id.verification_notification_tv, "已将短信发到手机：" + disposePhone);

        sendSMS();

    }

    private void sendSMS() {

        mResendBtn.setEnabled(false);
        //倒计时
        final CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {

            private int countdown = 59;

            @Override
            public void onTick(long millisUntilFinished) {
                fillTextView(R.id.verification_resend_btn, countdown-- + "秒后重新获取");
            }

            @Override
            public void onFinish() {
                fillTextView(R.id.verification_resend_btn, "重新获取");
                mResendBtn.setEnabled(true);
            }


        };
        timer.start();

        String url = String.format(URLs.URL_SEND_SMS, URLs.baseURL, URLs.appCode, mUsername, mPhone, mSystemBiz.getMachineId());
        StringRequest request = new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONResult jr = JSONResult.compile(response);
                if (jr.code == URLs.RESULT_CODE_SEND_SMS_ERROR) {
                    Toast.makeText(VerificationCodeActivity.this, jr.msg, Toast.LENGTH_SHORT).show();
                    timer.cancel();
                    mResendBtn.setEnabled(true);
                    fillTextView(R.id.verification_resend_btn, "重新获取");
                }
            }
        }, this);
        request.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, 1, 1.0f));
        mRequestQueue.add(request);


    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                onGoBack();
                break;
            case R.id.verification_done_btn:
                confirmVerificationCode(v);
                break;
            case R.id.verification_resend_btn:
                sendSMS();
                break;
        }
    }

    private void onGoBack() {
        new ConfirmDialog(this, new ConfirmDialog.ConfirmListener() {

            @Override
            public void onOkClick() {
                finish();
            }

            @Override
            public void onCancelClick() {

            }
        }, "验证码就要到了 放弃等待？", "等待", "放弃").show();
    }

    @Override
    public boolean shouldInterceptBackClick() {
        return true;
    }

    private void confirmVerificationCode(final View view) {

        String verificationCode = mVerficationCodeEt.getText().toString();
        if (TextUtils.isEmpty(verificationCode)) {
            Toast.makeText(this, "请填写验证码", Toast.LENGTH_SHORT).show();
            return;//如果格式不正确直接返回
        }
        ProgressHUD.show(this, "请稍候", true, false, null);

        String osVersion = Utils.getAndroidSDKVersion();// 操作系统号
        String currentVersionName = mAppContext.getVersionName();// app版本号
        int buildId = Utils.getVersionCode(VerificationCodeActivity.this);
        String url = null;
        try {

            String token = mAppContext.getApp().getPushVendorType() == App.PUSH_VENDOR_TYPE_BAIDU ? mAppContext.getApp().getBaiduPushUserId() : JPushInterface.getRegistrationID(this);
            url = String.format(URLs.URL_CONFIRM_VERIFICATION_CODE, URLs.baseURL,mPhone,
                    mSystemBiz.getMachineId(), verificationCode,
                    URLEncoder.encode(mUsername, "utf-8"), token, osVersion, URLEncoder.encode(Build.MODEL, "utf-8"), currentVersionName, buildId,URLs.appCode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest request = new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {

                ProgressHUD.dismissProgressHUDInThisContext(VerificationCodeActivity.this);

                JSONResult jr = JSONResult.compile(response);
                if (jr.code == URLs.RESULT_CODE_CONFIRM_VERIFICATION_CODE_ERROR) {
                    Toast.makeText(VerificationCodeActivity.this, jr.msg, Toast.LENGTH_SHORT).show();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                Toast.makeText(VerificationCodeActivity.this, "网络故障", Toast.LENGTH_SHORT).show();
                ProgressHUD.dismissProgressHUDInThisContext(VerificationCodeActivity.this);
            }

        });
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        mRequestQueue.add(request);
    }

    @Override
    public void onErrorResponse(VolleyError err) {
        Toast.makeText(this, "网络故障", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            onGoBack();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


}
