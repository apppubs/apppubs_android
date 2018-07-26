package com.apppubs.vpn;

import android.app.Activity;
import android.os.Bundle;

import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.ui.webapp.WebAppFragment;

public class VPNWebAppFragment extends WebAppFragment {

    public static final String ARGS_STRING_VPN_ID = "vpn_id";

    private String mVPNId;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle args = getArguments();
        mVPNId = args.getString(ARGS_STRING_VPN_ID);

        VPNBiz.getInstance(mContext).loginVPN(getActivity(), mVPNId, new IAPCallback() {
            @Override
            public void onDone(Object obj) {
                refresh();
            }

            @Override
            public void onException(APError error) {
                onError(error);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VPNBiz.getInstance(mContext).logoutVPN();
    }
}
