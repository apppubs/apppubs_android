package com.apppubs.vpn;

import android.app.Activity;
import android.content.Context;

import com.apppubs.AppContext;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.constant.Constants;
import com.apppubs.model.BaseBiz;
import com.apppubs.model.IAPCallback;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.util.ACache;
import com.apppubs.util.LogM;
import com.sangfor.bugreport.logger.Log;
import com.sangfor.ssl.BaseMessage;
import com.sangfor.ssl.LoginResultListener;
import com.sangfor.ssl.OnStatusChangedListener;
import com.sangfor.ssl.SFException;
import com.sangfor.ssl.SangforAuthManager;
import com.sangfor.ssl.StatusChangedReason;
import com.sangfor.ssl.common.ErrorCode;

import java.net.MalformedURLException;
import java.net.URL;

public class VPNBiz extends BaseBiz implements LoginResultListener, OnStatusChangedListener {

    private final String CACHE_NAME_VPN = "vpn_pwd_cache";

    private static volatile VPNBiz sBiz;
    private SangforAuthManager mSFManager;
    private ACache mCache;
    private int mCounter;
    private boolean isVerify;
    private LoginManager mLoginManager;
    private VerifyManager mVerifyManager;

    private CounterChangeListener mCounterChangeListener;

    @Override
    public void onStatusCallback(VPNStatus vpnStatus, StatusChangedReason statusChangedReason) {
        LogM.log(VPNBiz.class, "vpnStatus:" + vpnStatus + "reason:" + statusChangedReason.getReasonDes());
        if (mLoginManager != null) {
            mLoginManager.onStatusCallback(vpnStatus);
        }
    }

    public interface CounterChangeListener {
        void onCounterChanged(int preCounter, int curCounter);
    }

    private VPNBiz(Context context) {
        super(context);
        initLoginParms();
        mCache = ACache.get(context, CACHE_NAME_VPN);
    }

    /**
     * 初始化登录参数
     */
    private void initLoginParms() {
        // 1.构建SangforAuthManager对象
        mSFManager = SangforAuthManager.getInstance();
        // 2.设置VPN认证结果回调
        try {
            mSFManager.setLoginResultListener(this);
            mSFManager.addStatusChangedListener(this);
        } catch (SFException e) {
            Log.info("VPNBiz", "SFException:%s", e);
        }

        //3.设置登录超时时间，单位为秒
        mSFManager.setAuthConnectTimeOut(3);
    }

    public static VPNBiz getInstance(Context context) {
        if (sBiz == null) {
            synchronized (VPNBiz.class) {
                if (sBiz == null) {
                    sBiz = new VPNBiz(context);
                }
            }
        }
        return sBiz;
    }

    public void setCounterChangeListener(CounterChangeListener listener) {
        mCounterChangeListener = listener;
    }


    void fetchVPNInfos(IAPCallback<VPNInfosResult> callback) {
        this.asyncPOST(Constants.API_NAME_VPN_OF_USER, null, true, VPNInfosResult.class, new
                IRQListener<VPNInfosResult>() {
                    @Override
                    public void onResponse(VPNInfosResult jr, APError error) {
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                if (null == error) {
                                    callback.onDone(jr);
                                } else {
                                    callback.onException(error);
                                }
                            }
                        });
                    }
                });
    }

    /**
     * 增加VPN计数器，计数器从0变为1时登录从>1变为0时注销
     * addCounter放到onStart中，reduceCounter放到onStop中，
     * 当界面A关闭界面B打开会执行A.onPause,B.onCreate,B.onStart,B.onResume,A.onStop,所以这个过程vpn状态依然不会注销
     *
     * @return
     */
    public int addCounter() {
        onCounterChange(mCounter, ++mCounter);
        return mCounter;
    }

    public int reduceCounter() {
        onCounterChange(mCounter, --mCounter);
        return mCounter;
    }

    private void onCounterChange(int lastCounter, int curCounter) {
        if (mCounterChangeListener != null) {
            mCounterChangeListener.onCounterChanged(lastCounter, curCounter);
        }
    }

    public void verifyVPN(Activity activity, String url, String username, String pwd, IAPCallback callback) {
        VerifyManager manager = new VerifyManager();
        manager.setActivity(activity);
        manager.setUrl(url);
        manager.setUsername(username);
        manager.setPwd(pwd);
        manager.setCallback(callback);
        mVerifyManager = manager;
        manager.verify();
    }

    public void loginVPN(Activity activity, String vpnId, IAPCallback callback) {
        mLoginManager = new LoginManager();
        mLoginManager.setActivity(activity);
        mLoginManager.setVpnId(vpnId);
        mLoginManager.setCallback(callback);
        mLoginManager.login();
    }

    private class VerifyManager {
        private Activity activity;
        private String url;
        private String username;
        private String pwd;
        private IAPCallback callback;

        public Activity getActivity() {
            return activity;
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public IAPCallback getCallback() {
            return callback;
        }

        public void setCallback(IAPCallback callback) {
            this.callback = callback;
        }

        public void verify() {
            isVerify = true;
            try {
                mSFManager.startPasswordAuthLogin(activity.getApplication(), activity, VPNMode.L3VPN, new URL(url),
                        username, pwd);
            } catch (SFException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private class LoginManager {
        private Activity activity;
        private String vpnId;
        private IAPCallback callback;
        private boolean isPaddingLogin;
        private VPNStatus mStatus;

        public Activity getActivity() {
            return activity;
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }

        public String getVpnId() {
            return vpnId;
        }

        public void setVpnId(String vpnId) {
            this.vpnId = vpnId;
        }

        public IAPCallback getCallback() {
            return callback;
        }

        public void setCallback(IAPCallback callback) {
            this.callback = callback;
        }

        public boolean isPaddingLogin() {
            return isPaddingLogin;
        }

        public void setPaddingLogin(boolean paddingLogin) {
            isPaddingLogin = paddingLogin;
        }

        public void login() {
            if (null == mStatus || mStatus == VPNStatus.VPNOFFLINE) {
                isPaddingLogin = false;
                VPNPwdInfo info = getPwdInfo(vpnId);
                try {
                    mSFManager.startPasswordAuthLogin(activity.getApplication(), activity, VPNMode.L3VPN, new URL(info
                                    .getVpnURL()),
                            info.getUsername(), info.getPwd());
                } catch (SFException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                isPaddingLogin = true;
            }
        }

        public void doProgressAuth() {
            VPNPwdInfo info = getPwdInfo(vpnId);
            try {
                mSFManager.doPasswordAuth(info.getUsername(), info.getPwd());
            } catch (SFException e) {
                e.printStackTrace();
            }
        }

        public void onStatusCallback(VPNStatus vpnStatus) {
            mStatus = vpnStatus;
            if (vpnStatus == VPNStatus.VPNOFFLINE && isPaddingLogin) {
                login();
            }
        }
    }

    public void logoutVPN() {
        mSFManager.vpnLogout();
        if (!isVerify) {
            mLoginManager.setPaddingLogin(false);
        }
    }

    @Override
    public void onLoginFailed(ErrorCode errorCode, String s) {
        if (isVerify) {
            mVerifyManager.getCallback().onException(new APError(APErrorCode.GENERAL_ERROR, s));
            isVerify = false;
        } else {
            mLoginManager.getCallback().onException(new APError(APErrorCode.GENERAL_ERROR, s));
        }
    }

    @Override
    public void onLoginProcess(int i, BaseMessage baseMessage) {
        if (isVerify) {
            mVerifyManager.getCallback().onException(new APError(APErrorCode.GENERAL_ERROR, "code:" + i + "err:" +
                    baseMessage));
            isVerify = false;
        } else {
            mLoginManager.doProgressAuth();
        }
    }

    @Override
    public void onLoginSuccess() {
        if (isVerify) {
            mVerifyManager.getCallback().onDone(null);
            mSFManager.vpnLogout();
            isVerify = false;
        } else {
            mLoginManager.getCallback().onDone(null);
        }
    }

    public void savePwdInfo(VPNPwdInfo info) {
        mCache.put(getCacheKey(info.getVpnId()), info);
    }

    public VPNPwdInfo getPwdInfo(String vpnId) {
        return (VPNPwdInfo) mCache.getAsObject(getCacheKey(vpnId));
    }

    public void clearPwdInfo(String vpnId) {
        mCache.remove(getCacheKey(vpnId));
    }

    private String getCacheKey(String vpnId) {
        String username = AppContext.getInstance(mContext).getCurrentUser().getUsername();
        String cacheKey = "vpnKey:" + vpnId + "username:" + username;
        return cacheKey;
    }

    public void onActivityResult(int requestCode, int resultCode) {
        mSFManager.onActivityResult(requestCode, resultCode);
    }
}
