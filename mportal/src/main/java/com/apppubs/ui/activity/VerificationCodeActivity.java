package com.apppubs.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apppubs.bean.http.DefaultResult;
import com.apppubs.bean.http.LoginResult;
import com.apppubs.constant.APError;
import com.apppubs.d20.R;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.UserBiz;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.ui.widget.ProgressHUD;

public class VerificationCodeActivity extends BaseActivity {

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
        UserBiz.getInstance(mContext).requestVerifyCode(mUsername, new IAPCallback<DefaultResult>() {
            @Override
            public void onDone(DefaultResult obj) {
                Toast.makeText(mContext,"验证码已发送！",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(APError error) {
                mErrorHandler.onError(error);
                timer.cancel();
                mResendBtn.setEnabled(true);
                fillTextView(R.id.verification_resend_btn, "重新获取");
            }
        });
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
                setResult(RESULT_CANCELED);
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

        UserBiz.getInstance(mContext).confirmVerifyCode(mUsername, verificationCode, new IAPCallback<LoginResult>() {

            @Override
            public void onDone(LoginResult obj) {
                ProgressHUD.dismissProgressHUDInThisContext(VerificationCodeActivity.this);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onException(APError error) {
                ProgressHUD.dismissProgressHUDInThisContext(VerificationCodeActivity.this);
                mErrorHandler.onError(error);
            }
        });
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
