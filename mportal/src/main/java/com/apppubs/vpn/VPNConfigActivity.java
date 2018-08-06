package com.apppubs.vpn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.constant.APError;
import com.apppubs.d20.R;
import com.apppubs.model.IAPCallback;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.activity.ViewCourier;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.util.Utils;
import com.sangfor.ssl.IVpnDelegate;

import java.util.List;

public class VPNConfigActivity extends BaseActivity implements IVPNConfigView {

    private VPNConfigPresenter mPresenter;

    private LinearLayout mLl;

    private String mReadmeURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_vpnconfig);
        setTitle("VPN配置");
        mTitleBar.addRightBtnWithTextAndClickListener("说明", new View.OnClickListener
                () {
            @Override
            public void onClick(View v) {
                ViewCourier.getInstance(mContext).openWindow(mReadmeURL);
            }
        });
        mLl = (LinearLayout) findViewById(R.id.act_vpnconfig_ll);

        mPresenter = new VPNConfigPresenter(this, this);
        mPresenter.onCreate();
    }


    @Override
    public void showItems(List<VPNInfoWithPwd> items) {
        mLl.removeAllViews();
        for (int i = -1; ++i < items.size(); ) {
            VPNInfoWithPwd item = items.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.item_vpn_info, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = Utils.dip2px(mContext, 20);
            lp.rightMargin = Utils.dip2px(mContext, 20);
            lp.topMargin = Utils.dip2px(mContext, 20);
            if (i == items.size() - 1) {
                lp.bottomMargin = Utils.dip2px(mContext, 20);
            }
            view.setLayoutParams(lp);
            mLl.addView(view, lp);

            TextView nameTv = (TextView) view.findViewById(R.id.item_vpn_name_tv);
            nameTv.setText(item.getVpnName());

            EditText nameEt = (EditText) view.findViewById(R.id.item_vpn_user_et);
            nameEt.setText(item.getUsername());
            EditText pwdEt = (EditText) view.findViewById(R.id.item_vpn_pwd_et);
            pwdEt.setText(item.getPwd());
            Button confirmBtn = (Button) view.findViewById(R.id.item_vpn_confirm_btn);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onConfirmClicked(VPNConfigActivity.this, item.getVpnId(), nameEt.getText().toString(),
                            pwdEt.getText().toString(), new IAPCallback() {


                                @Override
                                public void onDone(Object obj) {
                                    Toast.makeText(mContext, "验证成功！", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onException(APError error) {
                                    Toast.makeText(mContext, error.getMsg(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            });

            Button clearBtn = (Button) view.findViewById(R.id.item_vpn_clear_btn);
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ConfirmDialog(VPNConfigActivity.this,
                            new ConfirmDialog.ConfirmListener() {

                                @Override
                                public void onOkClick() {
                                    mPresenter.onRestClicked(item.getVpnId());
                                }

                                @Override
                                public void onCancelClick() {

                                }
                            }, "确定清空此VPN配置？", "取消", "注销").show();
                }
            });

            if (!TextUtils.isEmpty(item.getUsername())) {
                ImageView configStatusIv = (ImageView) view.findViewById(R.id.item_vpn_config_iv);
                configStatusIv.setImageResource(R.drawable.vpn_config_done);
            }
        }
    }

    @Override
    public void setReadmeURL(String readmeURL) {
        mReadmeURL = readmeURL;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {

            case IVpnDelegate.REQUEST_L3VPNSERVICE:
                /* L3VPN模式下下必须回调此方法
                 * 注意：当前Activity的launchMode不能设置为 singleInstance，否则L3VPN服务启动会失败。
                 */
                mPresenter.onActivityResult(requestCode, resultCode);
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
}
