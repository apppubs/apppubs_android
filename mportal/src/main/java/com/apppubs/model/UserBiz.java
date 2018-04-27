package com.apppubs.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apppubs.bean.Settings;
import com.apppubs.bean.UserInfo;
import com.apppubs.AppContext;
import com.apppubs.bean.http.DefaultResult;
import com.apppubs.bean.http.LoginResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.URLs;
import com.apppubs.net.WMHHttpClient;
import com.apppubs.net.WMHHttpErrorCode;
import com.apppubs.net.WMHRequestListener;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.util.JSONResult;
import com.apppubs.util.JSONUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangwen on 2017/10/24.
 */

public class UserBiz extends BaseBiz {

    private static UserBiz sUserBiz;
    private Context mContext;

    private UserBiz(Context context) {
        super(context);
        mContext = context;
    }

    public interface GetUserInfoCallback {
        void onException(WMHErrorCode code);

        void onDone(UserInfo user);
    }

    public static UserBiz getInstance(Context context) {
        if (sUserBiz == null) {
            synchronized (UserBiz.class) {
                if (sUserBiz == null) {
                    sUserBiz = new UserBiz(context);
                }
            }
        }
        return sUserBiz;
    }

    public void getUserInfo(String userid, final GetUserInfoCallback callback) {
        String userInfoUrl = String.format(URLs.URL_USER_INFO, URLs.baseURL, URLs.appCode, userid);
        WMHHttpClient httpClient = AppContext.getInstance(mContext).getHttpClient();
        httpClient.GET(userInfoUrl, null, new WMHRequestListener() {
            @Override
            public void onDone(JSONResult jsonResult, @Nullable WMHHttpErrorCode errorCode) {
                if (errorCode == null) {
                    UserInfo userinfo = JSONUtils.parseObjectFromJson(jsonResult.result, UserInfo
                            .class);
                    callback.onDone(userinfo);
                } else {
                    WMHErrorCode e = null;
                    switch (errorCode) {
                        case JSON_PARSE_ERROR:
                            e = WMHErrorCode.JSON_PARSE_ERROR;
                        case IO_EXCEPTION:
                            e = WMHErrorCode.IO_EXCEPTION;
                        default:
                            e = WMHErrorCode.UNKNOWN;
                    }
                    callback.onException(e);

                }
            }
        });
    }

    public void loginWithUsernameAndPwd(String username, String pwd, final boolean autoLogin,
                                        final IAPCallback<UserInfo> callback) {
        String url = "http://result.eolinker" +
                ".com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri" +
                "=login_with_username_and_pwd";
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("pwd", pwd);
        asyncPOST(url, params, LoginResult.class, new IRQListener<LoginResult>() {
            @Override
            public void onResponse(LoginResult jr, final APError error) {
                if (error == null) {
                    final UserInfo user = updateLocalUserInfo(jr, autoLogin);
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(user);
                        }
                    });

                } else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(error);
                        }
                    });

                }
            }
        });
    }

    @NonNull
    private UserInfo updateLocalUserInfo(LoginResult jr, boolean autoLogin) {
        final UserInfo user = new UserInfo(jr.getUserId(), jr.getUsername(), jr
                .getCNName(),
                null, jr.getEmail(), jr.getMobile());
//                    user.setMenuPower(jo.getString("menupower"));
        mAppContext.setCurrentUser(user);
        // 保存user对象，并保存是否自动登录的配置
        Settings settings = mAppContext.getSettings();
        settings.setIsAllowAutoLogin(autoLogin);
        mAppContext.setSettings(settings);
        return user;
    }

    /**
     * 通过用户名登录
     *
     * @param phone
     * @param callback
     */
    public void loginWithPhone(String phone, final IAPCallback<String> callback) {
        String url = "http://result.eolinker" +
                ".com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=login_with_phone";
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", phone);
        asyncPOST(url, params, DefaultResult.class, new IRQListener<DefaultResult>() {
            @Override
            public void onResponse(final DefaultResult jr, final APError error) {
                if (error == null) {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone((String) jr.get("username"));
                        }
                    });
                } else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(error);
                        }
                    });

                }
            }
        });
    }

    public void requestVerifyCode(String username, final IAPCallback<DefaultResult> callback) {
        String url = "http://result.eolinker" +
                ".com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=request_send_verify_code";
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        asyncPOST(url, params, DefaultResult.class, new IRQListener<DefaultResult>() {

            @Override
            public void onResponse(final DefaultResult jr, final APError error) {
                if (error == null) {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(jr);
                        }
                    });
                } else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(error);
                        }
                    });
                }
            }
        });
    }

    public void confirmVerifyCode(String username, String verifyCode, final IAPCallback<LoginResult> callback) {
        String url = "http://result.eolinker.com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=confirm_verify_code";
        Map<String,String> params = new HashMap<String,String>();
        params.put("username",username);
        params.put("verifyCode",verifyCode);
        asyncPOST(url, params, LoginResult.class, new IRQListener<LoginResult>() {
            @Override
            public void onResponse(final LoginResult jr, final APError error) {
                if (error==null){
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            updateLocalUserInfo(jr, true);
                            callback.onDone(jr);
                        }
                    });
                }else{
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(error);
                        }
                    });
                }
            }
        });
    }

}
