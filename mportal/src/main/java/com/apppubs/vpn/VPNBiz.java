package com.apppubs.vpn;

import android.app.Activity;
import android.content.Context;

import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.constant.Constants;
import com.apppubs.model.BaseBiz;
import com.apppubs.model.IAPCallback;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.util.ACache;
import com.sangfor.bugreport.logger.Log;
import com.sangfor.ssl.BaseMessage;
import com.sangfor.ssl.LoginResultListener;
import com.sangfor.ssl.SFException;
import com.sangfor.ssl.SangforAuthManager;
import com.sangfor.ssl.common.ErrorCode;

import java.net.MalformedURLException;
import java.net.URL;

public class VPNBiz extends BaseBiz implements LoginResultListener {

    private final String CACHE_NAME_VPN = "vpn_pwd_cache";

    private static volatile VPNBiz sBiz;
    private SangforAuthManager mSFManager;
    private IAPCallback mLoginCallback;
    private boolean isVerify;
    private ACache mCache;
    private VPNPwdInfo mCurPwdInfo;

    private VPNBiz(Context context) {
        super(context);
        initLoginParms();
        mCache = ACache.get(context,CACHE_NAME_VPN);
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

    public void verifyVPN(Activity activity, String url, String username, String pwd, IAPCallback callback) {
        mLoginCallback = callback;
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

    public void loginVPN(Activity activity, String vpnId, IAPCallback callback){
        mLoginCallback = callback;
        VPNPwdInfo info = getPwdInfo(vpnId);
        try {
            mSFManager.startPasswordAuthLogin(activity.getApplication(), activity, VPNMode.L3VPN, new URL(info.getVpnURL()),
                    info.getUsername(), info.getPwd());
        } catch (SFException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void logoutVPN(){
        mSFManager.vpnLogout();
    }

    @Override
    public void onLoginFailed(ErrorCode errorCode, String s) {
        mLoginCallback.onException(new APError(APErrorCode.GENERAL_ERROR, s));
    }

    @Override
    public void onLoginProcess(int i, BaseMessage baseMessage) {
        mLoginCallback.onException(new APError(APErrorCode.GENERAL_ERROR, "请勿短时间内重复验证！"));
    }

    @Override
    public void onLoginSuccess() {
        mLoginCallback.onDone(null);
        if (isVerify){
            mSFManager.vpnLogout();
        }
    }

    public void savePwdInfo(VPNPwdInfo info){
        mCache.put(info.getVpnId(),info);
    }

    public VPNPwdInfo getPwdInfo(String vpnId){
        return (VPNPwdInfo) mCache.getAsObject(vpnId);
    }

    public void clearPwdInfo(String vpnId){
        mCache.remove(vpnId);
    }

}
