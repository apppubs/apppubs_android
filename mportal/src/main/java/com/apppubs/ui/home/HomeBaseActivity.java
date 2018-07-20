package com.apppubs.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.apppubs.AppContext;
import com.apppubs.AppManager;
import com.apppubs.MportalApplication;
import com.apppubs.bean.App;
import com.apppubs.bean.TMenuItem;
import com.apppubs.bean.UserInfo;
import com.apppubs.bean.Weather;
import com.apppubs.constant.APError;
import com.apppubs.constant.Actions;
import com.apppubs.d20.BuildConfig;
import com.apppubs.d20.R;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.SystemBiz;
import com.apppubs.presenter.HomeBottomPresenter;
import com.apppubs.presenter.HomePresenter;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.activity.CompelMessageDialogActivity;
import com.apppubs.ui.activity.FirstLoginActity;
import com.apppubs.ui.activity.ViewCourier;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.util.FileUtils;
import com.apppubs.util.LogM;
import com.apppubs.util.SharedPreferenceUtils;
import com.apppubs.util.Utils;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;

public abstract class HomeBaseActivity extends BaseActivity implements IHomeView {

    public static String MPORTAL_PREFERENCE_NAME = "mportal_preference";
    public static String MPORTAL_PREFERENCE_APP_RUNNING_KEY = "is_app_running";

    /**
     * 应用主目录
     */
    protected List<TMenuItem> mPrimaryMenuList;
    /**
     * 应用次目录
     */
    protected static ArrayList<Weather> mWeathers;
    protected List<TMenuItem> mSecondaryMenuList;
    protected ViewCourier mViewCourier;// 视图控制器
    protected MportalApplication mApp;

    private BroadcastReceiver mLogoutBR;

    protected void onCreate(android.os.Bundle arg0) {
        super.onCreate(arg0);
        setNeedTitleBar(false);//主页面activity没有titlebar，titlebar由其包含的fragment负责渲染
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        //检测是否需要登录而且是否登录
        if (mAppContext.getApp().getLoginFlag() != App.LOGIN_INAPP && TextUtils.isEmpty(mAppContext.getCurrentUser()
                .getUserId())) {
            //跳转到登录界面
            BaseActivity.startActivity(mContext, FirstLoginActity.class);
            finish();
        }

        LogM.log(this.getClass(), " HomeActivity onCreate");
        if (AppContext.getInstance(mContext).getCurrentUser().getMenuPower() != null) {
            String menuPower = AppContext.getInstance(mContext).getCurrentUser().getMenuPower();
            String[] menus = menuPower.split(",");
            String sqlParam = "";
            for (int i = -1; ++i < menus.length; ) {
                if (i != 0) {
                    sqlParam += ",";
                }
                sqlParam += "'" + menus[i] + "'";
            }
            mPrimaryMenuList = SugarRecord.find(TMenuItem.class, "LOCATION=? and (PROTECTED_FLAG = 0 or ID in (" +
                            sqlParam + "))",
                    new String[]{TMenuItem.MENU_LOCATION_PRIMARY + ""}, null,
                    "SORT_ID", null);
        } else {

            mPrimaryMenuList = SugarRecord.find(TMenuItem.class, "LOCATION=? and PROTECTED_FLAG = 0",
                    new String[]{TMenuItem.MENU_LOCATION_PRIMARY + ""}, null, "SORT_ID", null);
        }
        mSecondaryMenuList = SugarRecord.find(TMenuItem.class, "LOCATION=?",
                new String[]{TMenuItem.MENU_LOCATION_SECONDARY + ""}, null, "SORT_ID", null);
        mApp = (MportalApplication) this.getApplication();
        mViewCourier = ViewCourier.getInstance(this);

        if (BuildConfig.ENABLE_CHAT) {
            mMsgBussiness.loginRC(new IAPCallback() {
                @Override
                public void onDone(Object obj) {
                    System.out.println("融云登录成功！");
                }

                @Override
                public void onException(APError error) {
                    onError(error);
                }
            });
        }

        initBroadcastReceiver();

        SharedPreferenceUtils.getInstance(this).putBoolean(MPORTAL_PREFERENCE_NAME,
                MPORTAL_PREFERENCE_APP_RUNNING_KEY, true);

        commitRegisterId();
    }

    private void commitRegisterId() {
        final String registerId = JPushInterface.getRegistrationID(mContext);
        if (Utils.isEmpty(registerId)) {
            LogM.log(HomeBottomPresenter.class, "fail 极光注册提交失败id：注册id为空! ");
            return;
        }
        SystemBiz biz = SystemBiz.getInstance(mContext);
        biz.commitPushRegisterId(registerId, new IAPCallback() {
            @Override
            public void onDone(Object obj) {
                LogM.log(HomeBottomPresenter.class, "success 成功提交极光注册register id： " + registerId);
            }

            @Override
            public void onException(APError error) {
                LogM.log(HomeBottomPresenter.class, "fail 极光注册提交失败id： " + registerId);
            }
        });
    }

    private void initBroadcastReceiver() {
        mLogoutBR = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                skip2Login();
            }
        };
        registerReceiver(mLogoutBR, new IntentFilter(Actions.ACTION_LOGOUT));
    }

    protected abstract HomePresenter getPresenter();

    /**
     * 天气信息(系统静态变量)
     */
    public static void refreshWether(Context context, ArrayList<Weather> weathers) {
        mWeathers = weathers;
        FileUtils.writeObj(context, weathers, "weathers.cfg");
    }

    protected abstract void setUnreadNumForMenu(String menuId, int num);

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        //检测是否需要登录而且是否登录
        if (mAppContext.getApp().getLoginFlag() != App.LOGIN_INAPP && TextUtils.isEmpty
                (mAppContext.getCurrentUser().getUserId())) {
            //跳转到登录界面
            Intent intent = new Intent(this, FirstLoginActity.class);
            startActivity(intent);
            finish();
        }
        String paddingUrl = mAppContext.getApp().getPaddingUrlOnHomeActivityStartUp();
        if (!TextUtils.isEmpty(paddingUrl)) {
            mAppContext.getApp().setPaddingUrlOnHomeActivityStartUp(null);
            if (paddingUrl.startsWith("apppubs://message")) {
                for (TMenuItem mi : mPrimaryMenuList) {
                    if (mi.getUrl().startsWith("apppubs://message")) {
                        getPresenter().onMenuSelected(mi.getUrl());
                        break;
                    }
                }
            } else {
                executeURL(paddingUrl);
            }
        }

        showCompelMessageIfHave();
    }

    public void showCompelMessageIfHave() {

        mSystemBiz.loadCompelReadMessage(new IAPCallback<List<CompelReadMessageModel>>() {
            @Override
            public void onDone(List<CompelReadMessageModel> models) {
                if (Utils.isEmpty(models)) {
                    return;
                }
                ArrayList<CompelReadMessageModel> serializableList = new ArrayList<CompelReadMessageModel>(models);
                Intent i = new Intent(mContext, CompelMessageDialogActivity.class);
                i.putExtra(CompelMessageDialogActivity.EXTRA_DATAS, serializableList);
                mContext.startActivity(i);
            }

            @Override
            public void onException(APError error) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    public ViewCourier getViewController() {
        return mViewCourier;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void finish() {
        LogM.log(this.getClass(), "关闭应用");
        super.finish();
        // 发广播关闭下载服务
        Intent closeService = new Intent("com.apppubs.d20.stopdownload");
        sendBroadcast(closeService);
    }

    public static void startHomeActivity(Context fromActivy) {

        LogM.log(Class.class, "startHomeActivity-->启动主界面");

        Intent intent = null;
        AppContext appContext = AppContext.getInstance(fromActivy);
        if (appContext.getApp().getLoginFlag() != App.LOGIN_INAPP) {
            UserInfo currentUser = AppContext.getInstance(fromActivy).getCurrentUser();

            int layout = AppContext.getInstance(fromActivy).getApp().getLayoutLocalScheme();

            if (currentUser == null || TextUtils.isEmpty(currentUser.getUserId())) {
                intent = new Intent(fromActivy, FirstLoginActity.class);
            } else {
                if (layout == App.STYLE_SLIDE_MENU) {
                    intent = new Intent(fromActivy, HomeSlideMenuActivity.class);
                } else {
                    intent = new Intent(fromActivy, HomeBottomMenuActivity.class);
                }
            }
        } else {
            int layout = AppContext.getInstance(fromActivy).getApp().getLayoutLocalScheme();
            if (layout == App.STYLE_SLIDE_MENU) {
                intent = new Intent(fromActivy, HomeSlideMenuActivity.class);
            } else {
                intent = new Intent(fromActivy, HomeBottomMenuActivity.class);
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        fromActivy.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferenceUtils.getInstance(this).putBoolean(MPORTAL_PREFERENCE_NAME,
                MPORTAL_PREFERENCE_APP_RUNNING_KEY, false);
        unregisterReceiver(mLogoutBR);
        AppManager.getInstance(this).destroy();

        //如果是用户名密码登录，没有自动登录时候清空用户信息
        if (mAppContext.getApp().getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME_PASSWORD && !mAppContext
                .getSettings().isAllowAutoLogin()) {
            mAppContext.clearCurrentUser();
        }
    }

    private long lastClickTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            long curClickTime = System.currentTimeMillis();
            if (curClickTime - lastClickTime < 1000) {
                HomeBaseActivity.super.finish();
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
                sendBroadcast(closeI);
                RongIM.getInstance().disconnect();
            } else {
                lastClickTime = curClickTime;
                Toast.makeText(HomeBaseActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            }
            // exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public List<Weather> getWeatherList() {
        return mWeathers;
    }

    public void skip2Login() {
        AppContext.getInstance(mContext).setCurrentUser(new UserInfo());
        if (mAppContext.getApp().getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME_PASSWORD || mAppContext.getApp().getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME_PASSWORD_ORGCODE) {
            Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
            sendBroadcast(closeI);
            Intent intent = new Intent(this, FirstLoginActity.class);
            startActivity(intent);

        } else if (mAppContext.getApp().getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME) {
            Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
            sendBroadcast(closeI);
            Intent intent = new Intent(this, FirstLoginActity.class);
            startActivity(intent);

        } else if (mAppContext.getApp().getLoginFlag() == App.LOGIN_ONSTART_USE_PHONE_NUMBER) {
            Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
            sendBroadcast(closeI);
            Intent intent = new Intent(this, FirstLoginActity.class);
            startActivity(intent);
        } else {
        }
    }

    public void selectMessageFragment() {
        for (int i = -1; ++i < mPrimaryMenuList.size(); ) {
            TMenuItem mi = mPrimaryMenuList.get(i);
            if (mi.getUrl().contains("apppubs://message")) {
                selectTab(i);
            }
        }
    }

    protected abstract void selectTab(int index);

    @Override
    public void changeContent(BaseFragment frg) {

    }

    @Override
    public abstract void setMenus(List<TMenuItem> menus);

    @Override
    public void onError(APError error) {
        mErrorHandler.onError(error);
    }

    @Override
    public void showEmptyView() {

    }

    @Override
    public void hideEmptyView() {

    }
}
