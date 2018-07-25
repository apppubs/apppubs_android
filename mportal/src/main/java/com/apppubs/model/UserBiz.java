package com.apppubs.model;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apppubs.bean.Settings;
import com.apppubs.bean.UserInfo;
import com.apppubs.AppContext;
import com.apppubs.bean.http.DefaultResult;
import com.apppubs.bean.http.IJsonResult;
import com.apppubs.bean.http.LoginResult;
import com.apppubs.bean.http.UserBasicInfosResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.Actions;
import com.apppubs.constant.Constants;
import com.apppubs.constant.URLs;
import com.apppubs.model.message.UserBasicInfo;
import com.apppubs.net.WMHHttpClient;
import com.apppubs.net.WMHHttpErrorCode;
import com.apppubs.net.WMHRequestListener;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.util.ACache;
import com.apppubs.util.JSONResult;
import com.apppubs.util.JSONUtils;
import com.apppubs.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;

/**
 * Created by zhangwen on 2017/10/24.
 */

public class UserBiz extends BaseBiz {

    private static UserBiz sUserBiz;
    private Context mContext;
    private static final String CACHE_NAME = "com.client.message.model.UserBussiness";

    private UserBiz(Context context) {
        super(context);
        mContext = context;
    }

    public interface GetUserInfoCallback {
        void onException(APError error);

        void onDone(UserBasicInfosResult userInfos);
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

    public void cacheUserBasicInfo(String userid, final GetUserInfoCallback callback) {
        Map<String, String> map = new HashMap<>();
        map.put("userIds", userid);
        this.asyncPOST(Constants.API_NAME_USER_BASIC_INFO, map, true, UserBasicInfosResult.class, new
                IRQListener<UserBasicInfosResult>() {
                    @Override
                    public void onResponse(UserBasicInfosResult jr, APError error) {
                        if (error == null) {

                            ACache cache = ACache.get(mContext, CACHE_NAME);
                            for (UserBasicInfosResult.Item item : jr.getItems()) {
                                cache.put(item.getUserId(), item);
                            }
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (error != null) {
                                        callback.onException(error);
                                    } else {
                                        callback.onDone(jr);
                                    }
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

    public void cacheUserBasicInfo(List<String> userIds, IAPCallback<List<UserBasicInfosResult.Item>> callback) {
        Map<String, String> map = new HashMap<>();
        String userIdsStr = StringUtils.join(userIds);
        map.put("userIds", userIdsStr);
        this.asyncPOST(Constants.API_NAME_USER_BASIC_INFO, map, true, UserBasicInfosResult.class, new
                IRQListener<UserBasicInfosResult>() {
                    @Override
                    public void onResponse(UserBasicInfosResult jr, APError error) {
                        if (error == null) {
                            ACache cache = ACache.get(mContext, CACHE_NAME);
                            for (UserBasicInfosResult.Item item : jr.getItems()) {
                                cache.put(item.getUserId(), item);
                            }
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (error != null) {
                                        callback.onException(error);
                                    } else {
                                        callback.onDone(jr.getItems());
                                    }
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

    public UserBasicInfosResult.Item getCachedUserBasicInfo(String userId) {
        ACache cache = ACache.get(mContext, CACHE_NAME);
        UserBasicInfosResult.Item item = (UserBasicInfosResult.Item) cache.getAsObject(userId);
        return item;
    }

    public void loginWithUsernameAndPwd(String username, String pwd, final boolean autoLogin,
                                        final IAPCallback<UserInfo> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("pwd", pwd);
        asyncPOST(Constants.API_NAME_LOGIN_WITH_USERNAME_AND_PWD, params, LoginResult.class, new
                IRQListener<LoginResult>() {
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
        UserInfo user = new UserInfo(jr.getUserId(), jr.getUsername(), jr
                .getCNName(),
                null, jr.getEmail(), jr.getMobile());
        user.setToken(jr.getToken());
        user.setRongToken(jr.getRongToken());

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
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", phone);
        asyncPOST(Constants.API_NAME_LOGIN_WITH_PHONE, params, DefaultResult.class, new
                IRQListener<DefaultResult>() {
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

    public void loginWithUsername(String username, final IAPCallback<String> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        asyncPOST(Constants.API_NAME_LOGIN_WITH_USERNAME, params, DefaultResult.class, new
                IRQListener<DefaultResult>() {
                    @Override
                    public void onResponse(final DefaultResult jr, final APError error) {
                        if (error == null) {
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone((String) jr.get("phone"));
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

    public void loginWithUsernamePwdAndOrgCode(String username, String pwd, String orgCode, final
    IAPCallback<LoginResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("pwd", pwd);
        params.put("orgCode", orgCode);

        asyncPOST(Constants.API_NAME_LOGIN_WITH_ORG, params, LoginResult.class, new
                IRQListener<LoginResult>() {
                    @Override
                    public void onResponse(final LoginResult jr, final APError error) {
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

    public void requestVerifyCode(String username, final IAPCallback<DefaultResult> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        asyncPOST(Constants.API_NAME_REQUEST_SENT_VERIFY_CODE, params, DefaultResult.class, new
                IRQListener<DefaultResult>() {

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

    public void confirmVerifyCode(String username, String verifyCode, final
    IAPCallback<LoginResult> callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("verifyCode", verifyCode);
        asyncPOST(Constants.API_NAME_CONFIRM_VERIFY_CODE, params, LoginResult.class, new
                IRQListener<LoginResult>() {
                    @Override
                    public void onResponse(final LoginResult jr, final APError error) {
                        if (error == null) {
                            updateLocalUserInfo(jr, true);
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

    public void modifyPwd(final String oldPwd, String newPwd, final IAPCallback callback) {
        Map<String, String> params = new HashMap<>();
        asyncPOST(Constants.API_NAME_MODIFY_PWD, params, new IRQStringListener() {
            @Override
            public void onResponse(final String result, final APError error) {
                if (error == null) {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(result);
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

    public void inviteUsers(@NonNull final List<String> userIds, @NonNull final IAPCallback
            callback) {

        String idsStr = getUserIdsStr(userIds);
        Map<String, String> params = new HashMap<>();
        params.put("userIds", idsStr);
        asyncPOST(Constants.API_NAME_REQUEST_SENT_INVITE_SMS, params, true, new IRQStringListener() {
            @Override
            public void onResponse(final String result, final APError error) {
                if (error == null) {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(result);
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

    private String getUserIdsStr(@NonNull List<String> userIds) {
        StringBuilder sb = new StringBuilder();
        for (String userId : userIds) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(userId);
        }
        return sb.toString();
    }

    public void logout(final Context context, final IAPCallback<Object> callback) {
        asyncPOST(Constants.API_NAME_LOGOUT, null, true, new IRQStringListener() {

            @Override
            public void onResponse(final String result, final APError error) {
                if (error == null) {
                    AppContext.getInstance(mContext).clearCurrentUser();
                    RongIM.getInstance().logout();
                    context.sendBroadcast(new Intent(Actions.ACTION_LOGOUT));
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(result);
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

}
