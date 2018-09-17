package com.apppubs.vpn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.apppubs.ui.activity.ContainerActivity;
import com.apppubs.ui.webapp.WebAppFragment;
import com.apppubs.util.StringUtils;
import com.apppubs.util.Utils;

public class VPNViewCourierHelper {
    private Context mContext;
    private volatile static VPNViewCourierHelper sHelper;
    private boolean needVPN;

    private VPNViewCourierHelper(Context context) {
        mContext = context;
    }

    public static VPNViewCourierHelper getInstance(Context context) {
        if (sHelper == null) {
            synchronized (VPNViewCourierHelper.class) {
                if (sHelper == null) {
                    sHelper = new VPNViewCourierHelper(context);
                }
            }
        }
        return sHelper;
    }

    public boolean needVPN() {
        return needVPN;
    }

    public void setNeedVPN(boolean need) {
        needVPN = need;
    }

    public boolean openWindow(String url) {
        String vpnId = StringUtils.getQueryParameter(url, "vpnId");
        if ((url.startsWith("http://") || url.startsWith("https://")) && !TextUtils.isEmpty(vpnId)) {
            VPNPwdInfo info = VPNBiz.getInstance(mContext).getPwdInfo(vpnId);
            if (null == info) {
                Intent intent = new Intent(mContext, VPNConfigActivity.class);
                intent.putExtra(VPNConfigActivity.EXTRA_BOOLEAN_SHOULD_CLOSE, true);
                mContext.startActivity(intent);
            } else {
                openWebApp(url, vpnId);
            }
            setNeedVPN(true);
            return true;
        } else if ("apppubs://VPNManager".equals(url)) {
            Intent intent = new Intent(mContext, VPNConfigActivity.class);
            mContext.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    private void openWebApp(String url, String vpnId) {
        Bundle args = new Bundle();
        args.putString(WebAppFragment.ARGUMENT_STRING_URL, url);
        args.putBoolean(ContainerActivity.EXTRA_BOOLEAN_IS_FULLSCREEN, true);
        String titlebarFlag = StringUtils.getQueryParameter(url, "titlebar");
        if (!Utils.isEmpty(titlebarFlag)) {
            args.putBoolean(WebAppFragment.ARGUMENT_STRING_NEED_TITLEBAR, Utils.compare(titlebarFlag, "1"));
            args.putBoolean(WebAppFragment.ARGUMENT_BOOLEAN_NEED_TITLE_BAR_ARROW, Utils.compare(titlebarFlag, "2"));
        }
        args.putString(VPNWebAppFragment.ARGS_STRING_VPN_ID, vpnId);
        ContainerActivity.startContainerActivity(mContext, VPNWebAppFragment.class, args);
    }
}
