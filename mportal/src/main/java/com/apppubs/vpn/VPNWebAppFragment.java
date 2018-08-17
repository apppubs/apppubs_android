package com.apppubs.vpn;

import android.app.Activity;
import android.os.Bundle;

import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.ui.webapp.WebAppFragment;
import com.apppubs.ui.widget.ProgressHUD;

public class VPNWebAppFragment extends WebAppFragment {

    public static final String ARGS_STRING_VPN_ID = "vpn_id";

    private String mVPNId;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle args = getArguments();
        mVPNId = args.getString(ARGS_STRING_VPN_ID);

        VPNBiz.getInstance(mContext).setCounterChangeListener(new VPNBiz.CounterChangeListener() {
            @Override
            public void onCounterChanged(int preCounter, int curCounter) {
                if (preCounter == 0 && curCounter == 1) {
                    ProgressHUD.dismissProgressHUDInThisContext(getContext());
                    ProgressHUD.show(getContext(), "VPN认证中..", true, false, null);
                    VPNBiz.getInstance(mContext).loginVPN(getActivity(), mVPNId, new IAPCallback() {
                        @Override
                        public void onDone(Object obj) {
                            refresh();
                            ProgressHUD.dismissProgressHUDInThisContext(getContext());
                        }

                        @Override
                        public void onException(APError error) {
                            onError(error);
                            ProgressHUD.dismissProgressHUDInThisContext(getContext());
                        }
                    });
                }

                if (preCounter > 0 && curCounter == 0) {
                    VPNBiz.getInstance(mContext).logoutVPN();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        VPNBiz.getInstance(mContext).addCounter();
    }

    @Override
    public void onStop() {
        super.onStop();
        VPNBiz.getInstance(mContext).reduceCounter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VPNBiz.getInstance(mContext).logoutVPN();
        VPNViewCourierHelper.getInstance(mContext).setNeedVPN(false);
    }
}
