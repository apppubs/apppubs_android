package com.apppubs.model;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.widget.CheckBox;

import com.apppubs.bean.App;
import com.apppubs.bean.Settings;
import com.apppubs.bean.UserInfo;
import com.apppubs.AppContext;
import com.apppubs.bean.http.IJsonResult;
import com.apppubs.bean.http.LoginResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.URLs;
import com.apppubs.d20.R;
import com.apppubs.net.WMHHttpClient;
import com.apppubs.net.WMHHttpErrorCode;
import com.apppubs.net.WMHRequestListener;
import com.apppubs.ui.activity.FirstLoginActity;
import com.apppubs.util.JSONResult;
import com.apppubs.util.JSONUtils;
import com.apppubs.util.Utils;
import com.apppubs.util.WebUtils;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

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
                                        final APCallback<UserInfo> callback) {
        String url = "http://result.eolinker" +
                ".com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri" +
                "=login_with_username_and_pwd";
        asyncPOST(url, null, LoginResult.class, new IRQListener<LoginResult>() {
            @Override
            public void onResponse(LoginResult jr, APError error) {
                if (error == null) {
                    UserInfo user = new UserInfo(jr.getUserId(), jr.getUsername(), jr.getCNName(),
                            null, jr.getEmail(), jr.getMobile());
//                    user.setMenuPower(jo.getString("menupower"));
                    mAppContext.setCurrentUser(user);
                    // 保存user对象，并保存是否自动登录的配置
                    Settings settings = mAppContext.getSettings();
                    settings.setIsAllowAutoLogin(autoLogin);
                    mAppContext.setSettings(settings);
                    callback.onDone(user);
                } else {
                    callback.onException(error);
                }
            }
        });
    }

}
