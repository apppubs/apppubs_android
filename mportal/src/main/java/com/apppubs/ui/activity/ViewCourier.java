package com.apppubs.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.apppubs.AppContext;
import com.apppubs.bean.TMenuItem;
import com.apppubs.ui.fragment.CollectionFragment;
import com.apppubs.ui.fragment.HistoryFragment;
import com.apppubs.ui.fragment.ServiceNOsOfMineFragment;
import com.apppubs.ui.fragment.SettingFragment;
import com.apppubs.ui.fragment.TitleMenuFragment;
import com.apppubs.ui.message.fragment.AddressBookFragement;
import com.apppubs.ui.message.fragment.ConversationListFragment;
import com.apppubs.ui.myfile.MyFileFragment;
import com.apppubs.ui.news.ChannelFragment;
import com.apppubs.ui.news.ChannelFragmentFactory;
import com.apppubs.ui.news.ChannelsFragment;
import com.apppubs.ui.news.ChannelsSlideFragment;
import com.apppubs.ui.news.NewsInfoBaseActivity;
import com.apppubs.ui.page.PageFragment;
import com.apppubs.ui.webapp.WebAppFragment;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.util.StringUtils;

/**
 * 界面控制器，用户跳转页面
 *
 * @author Administrator
 */
public class ViewCourier {

    // 用户中心动作
    public static final int ACTION_USER_CENTER = 0;

    private Context mContext;


    /**
     * 当前的菜单
     */
    private static volatile ViewCourier sHomeviewController;

    private ViewCourier(Context context) {
        mContext = context;
    }

    public static ViewCourier getInstance(Context context) {

        if (sHomeviewController == null) {
            synchronized (ViewCourier.class) {
                if (sHomeviewController == null) {
                    sHomeviewController = new ViewCourier(context);
                }
            }
        }
        return sHomeviewController;
    }

    public void startSettingView(Context context, String menuId) {
        Bundle args = new Bundle();
        args.putString(TitleMenuFragment.ARGS_MENU_ID, menuId);
        ContainerActivity.startContainerActivity(context, SettingFragment.class, args, "设置");
    }

    /**
     * 通过URL打开一个界面，如果上下文为HomeBaseActivity则在homeBase中打开否则打开新界面
     *
     * @param context
     * @param url
     */
    public void execute(Context context, String url) {
        openWindow(url);
    }

    public void openWindow(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            Bundle args = new Bundle();
            args.putString(WebAppFragment.ARGUMENT_STRING_URL, url);
            args.putBoolean(ContainerActivity.EXTRA_BOOLEAN_IS_FULLSCREEN, true);
            ContainerActivity.startContainerActivity(mContext, WebAppFragment.class, args);
        } else if (url.matches("apppubs:\\/\\/newsinfo\\/[A-Z0-9]*\\/[A-Za-z0-9]*\\/[A-Za-z0-9" +
                "]*")) {//新闻正文
            String[] arr = StringUtils.getPathParams(url);
            NewsInfoBaseActivity.startInfoActivity(mContext, arr[1], new String[]{arr[2], arr[3]})
            ;//频道
        } else if (url.matches("apppubs:\\/\\/channel/[^\\\\s]*")) {
            String[] arr = StringUtils.getPathParams(url);
            ChannelFragment cf = ChannelFragmentFactory.getChannelFragment(Integer.parseInt
                    (arr[1]));
            Bundle args = new Bundle();
            args.putString(ChannelFragment.ARG_KEY, arr[2]);
            String title = StringUtils.getQueryParameter(url, "title");
            args.putString(ContainerActivity.EXTRA_STRING_TITLE, title);
            ContainerActivity.startContainerActivity(mContext, cf.getClass(), args);
        } else if (url.matches("apppubs://channelgroup/[^\\s]*")) {//频道组
            String[] arr = StringUtils.getPathParams(url);
            ChannelsFragment frg = new ChannelsSlideFragment();
            String title = StringUtils.getQueryParameter(url, "title");
            Bundle args = new Bundle();
            args.putString(ContainerActivity.EXTRA_STRING_TITLE, title);
            args.putString(ChannelsFragment.ARGUMENT_NAME_CHANNELTYPEID, arr[1]);
            ContainerActivity.startContainerActivity(mContext, frg.getClass(), args);
        } else if (url.matches("apppubs:\\/\\/page\\/[\\S]*")) {
            PageFragment pageF = new PageFragment();
            String[] pathParams = StringUtils.getPathParams(url);
            String titlebarFlag = StringUtils.getQueryParameter(url, "titlebar");
            String title = StringUtils.getQueryParameter(url, "title");
            Bundle args = new Bundle();
            if (!TextUtils.isEmpty(titlebarFlag) && titlebarFlag.equals("0")) {
                args.putBoolean(ContainerActivity.EXTRA_BOOLEAN_IS_FULLSCREEN, true);
            }
            args.putString(PageFragment.EXTRA_STRING_NAME_PAGE_ID, pathParams[1]);
            args.putString(ContainerActivity.EXTRA_STRING_TITLE, title);
            ContainerActivity.startContainerActivity(mContext, pageF.getClass(), args);
        } else if (url.matches("apppubs:\\/\\/addressbook[\\S]*")) {
            String rootSuperId = StringUtils.getQueryParameter(url, "rootsuperid");
            Bundle args = new Bundle();
            args.putString(AddressBookFragement.ARGS_ROOT_DEPARTMENT_SUPER_ID, rootSuperId);
            args.putBoolean(ContainerActivity.EXTRA_BOOLEAN_IS_FULLSCREEN, true);
            ContainerActivity.startContainerActivity(mContext, AddressBookFragement.class, args);
        } else if (url.matches("apppubs:\\/\\/setting[\\S]*")) {
            String title = StringUtils.getQueryParameter(url, "title");
            ContainerActivity.startFullScreenContainerActivity(mContext, SettingFragment.class,
                    null, title);
        } else if (url.matches("apppubs:\\/\\/favorite[\\S]*")) {
            CollectionFragment frg = new CollectionFragment();
            ContainerActivity.startContainerActivity(mContext, frg.getClass());
        } else if (url.matches("apppubs:\\/\\/message[\\S]*")) {
            ContainerActivity.startContainerActivity(mContext, ConversationListFragment.class);
        } else if (url.matches("apppubs:\\/\\/history_message[\\S]*")) {
            ContainerActivity.startContainerActivity(mContext, HistoryFragment.class);
        } else if (url.matches("apppubs:\\/\\/baol[\\S]*")) {
            Intent intent = new Intent(mContext, BaoliaoActivity.class);
            mContext.startActivity(intent);
        } else if (url.matches("apppubs:\\/\\/user_account[\\S]*")) {
            String userId = AppContext.getInstance(mContext).getCurrentUser().getUserId();
            Intent intent = null;
            if (userId != null && !userId.equals("")) {// 已登录
                intent = new Intent(mContext, UserCenterActivity.class);
            } else {
                intent = new Intent(mContext, LoginActivity.class);
            }
            mContext.startActivity(intent);
        } else if (url.startsWith("apppubs://qrcode")) {
            Intent intent = new Intent(mContext, CaptureActivity.class);
            mContext.startActivity(intent);
        } else if (url.startsWith("apppubs://service_no")) {
            String title = StringUtils.getQueryParameter(url, "title");
            ContainerActivity.startFullScreenContainerActivity(mContext, ServiceNOsOfMineFragment
                    .class, null, title);
        } else if (url.startsWith("tel:")) {
            String str[] = url.split(":");
            final String uri = url;
            final Context con = mContext;
            new ConfirmDialog(mContext, new ConfirmDialog.ConfirmListener() {

                @Override
                public void onOkClick() {
                    Intent intentCall = new Intent(android.content.Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse(uri));
                    con.startActivity(intentCall);
                }

                @Override
                public void onCancelClick() {

                }
            }, "确定拨号?", "电话：" + str[1], "放弃", "拨号").show();
        } else if (url.startsWith("apppubs://hint")) {
            String[] params = StringUtils.getPathParams(url);
            if (params.length > 1) {
                Toast.makeText(mContext, params[1], Toast.LENGTH_LONG).show();
            }
        } else if (url.startsWith("apppubs://myfile")) {
            String title = StringUtils.getQueryParameter(url, "title");
            Bundle args = new Bundle();
            args.putString(ContainerActivity.EXTRA_STRING_TITLE, title);
            ContainerActivity.startContainerActivity(mContext, MyFileFragment.class, args);
        } else if (url.startsWith("hxLink://")) {
            String username = AppContext.getInstance(mContext).getCurrentUser().getUsername();
            String password = AppContext.getInstance(mContext).getCurrentUser().getPassword();
            Intent intent = new Intent();
            //启动IM
            ComponentName comp = new ComponentName("elink.mobile.im", "elink.mobile.im.splash" +
                    ".SplashFragment");
            intent.setComponent(comp);
            intent.setAction("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            //传入参数
            bundle.putString("userName", username);    //用户名
            bundle.putString("passWord", password);    //密码
//			bundle.putString("ip","192.168.0.2");	//服务器IP
//			bundle.putString("port","9000");		//即时通讯端口
//			bundle.putString("httpPort","9090");	//http端口
            intent.putExtras(bundle);
            try {
                mContext.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(mContext, "启动E-Link失败", Toast.LENGTH_LONG).show();
            }
        } else if (url.equals(TMenuItem.MENU_URL_EMAIL) || url.startsWith("apppubs://email")) {
            openEmailApp();
        } else {
            Toast.makeText(mContext, "请求地址(" + url + ")错误", Toast.LENGTH_SHORT).show();
        }

    }

    public static boolean openLoginViewIfNeeded(String url, BaseActivity context) {
        String apppubsloginFlag = StringUtils.getQueryParameter(url, "apppubslogin");
        String userId = AppContext.getInstance(context).getCurrentUser().getUserId();
        if (!TextUtils.isEmpty(apppubsloginFlag) && apppubsloginFlag.equals("1") && TextUtils
                .isEmpty(userId)) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivityForResult(intent, LoginActivity.REQUEST_CODE);
            return true;
        }
        return false;
    }


    private void openEmailApp() {
        Intent intent;

        intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, "没有邮件客户端", Toast.LENGTH_LONG).show();

        }
    }

    public void openRegView(Context context) {
        String regURL = AppContext.getInstance(mContext).getAppConfig().getRegURL();
        if (!TextUtils.isEmpty(regURL)) {
            ViewCourier.getInstance(mContext).openWindow(regURL);
        } else {
            Intent intent = new Intent(context, RegisterActivity.class);
            context.startActivity(intent);
        }
    }

}
