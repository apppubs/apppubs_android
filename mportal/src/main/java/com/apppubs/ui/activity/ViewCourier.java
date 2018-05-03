package com.apppubs.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.apppubs.AppContext;
import com.apppubs.bean.App;
import com.apppubs.bean.TMenuItem;
import com.apppubs.bean.TNewsChannel;
import com.apppubs.d20.R;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.ui.fragment.ChannelFragment;
import com.apppubs.ui.fragment.ChannelFragmentFactory;
import com.apppubs.ui.fragment.ChannelPictureFragment;
import com.apppubs.ui.fragment.ChannelVideoFragment;
import com.apppubs.ui.fragment.ChannelsFragment;
import com.apppubs.ui.fragment.ChannelsSlideFragment;
import com.apppubs.ui.fragment.ChannelsSquareFragment;
import com.apppubs.ui.fragment.CollectionFragment;
import com.apppubs.ui.fragment.CommonFragmentFactory;
import com.apppubs.ui.fragment.ServiceNOsOfMineFragment;
import com.apppubs.ui.fragment.ExceptionFragment;
import com.apppubs.ui.fragment.HistoryFragment;
import com.apppubs.ui.fragment.MenuGroupsFragment;
import com.apppubs.ui.fragment.MoreFragment;
import com.apppubs.ui.fragment.MsgRecordListFragment;
import com.apppubs.ui.fragment.PapersFragment;
import com.apppubs.ui.fragment.SettingFragment;
import com.apppubs.ui.fragment.TitleMenuFragment;
import com.apppubs.ui.fragment.WeiBoFragment;
import com.apppubs.ui.home.HomeBaseActivity;
import com.apppubs.ui.message.fragment.AddressBookFragement;
import com.apppubs.ui.message.fragment.ConversationListFragment;
import com.apppubs.ui.myfile.MyFileFragment;
import com.apppubs.ui.page.PageFragment;
import com.apppubs.ui.webapp.WebAppFragment;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.ui.widget.TitleBar;
import com.apppubs.util.FileUtils;
import com.apppubs.util.LogM;
import com.apppubs.util.StringUtils;
import com.orm.SugarRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 界面控制器，用户跳转页面
 *
 * @author Administrator
 */
public class ViewCourier {

    // 用户中心动作
    public static final int ACTION_USER_CENTER = 0;

    /**
     * 一个关于定位页面的URI app：xxx,为某种app内置功能
     */
//	private HomeBaseActivity mHomeActivity;
    private Context mContext;
    private TitleBar mHomeTitleBar;// 主界面titlebar
    private App mApp;

    private Map<TMenuItem, Fragment> mFragmentsMap;
    private boolean isWeatherRcvRegistered;
    private BroadcastReceiver mWeatherRcv = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
//			refreshWeather();
        }
    };

    /**
     * 当前的菜单
     */
    private TMenuItem mCurMenuItem;
    private static ViewCourier mHomeviewController;

    private ViewCourier(Context context) {
        mContext = context;
        mApp = AppContext.getInstance(context).getApp();
        mFragmentsMap = new HashMap<TMenuItem, Fragment>();

    }

    public static ViewCourier getInstance(Context context) {

        if (mHomeviewController == null) {
            mHomeviewController = new ViewCourier(context);
        }
        return mHomeviewController;
    }

    public void startSettingView(Context context, String menuId) {
        Bundle args = new Bundle();
        args.putString(TitleMenuFragment.ARGS_MENU_ID, menuId);
        ContainerActivity.startContainerActivity(context, SettingFragment.class, args, "设置");
    }

    public void execute(int action) {
        switch (action) {
            case ACTION_USER_CENTER:
                String userId = AppContext.getInstance(mContext).getCurrentUser().getUserId();
                Intent intent = null;
                if (userId != null && !userId.equals("")) {// 已登录
                    intent = new Intent(mContext, UserCencerActivity.class);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                mContext.startActivity(intent);
                break;

            default:
                break;
        }
    }


    /**
     * 通过URL打开一个界面，如果上下文为HomeBaseActivity则在homeBase中打开否则打开新界面
     *
     * @param context
     * @param url
     */
    public void execute(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            WebAppFragment frg = new WebAppFragment();
            Bundle args = new Bundle();
            args.putString(WebAppFragment.ARGUMENT_STRING_URL, url);
            frg.setArguments(args);
            ContainerActivity.startContainerActivity(context, WebAppFragment.class, args);
        } else if (url.matches("apppubs:\\/\\/newsinfo\\/[A-Z0-9]*\\/[A-Za-z0-9]*\\/[A-Za-z0-9" +
                "]*")) {//新闻正文
            String[] arr = StringUtils.getPathParams(url);
            NewsInfoBaseActivity.startInfoActivity(context, arr[1], new String[]{arr[2], arr[3]})
            ;//频道
        } else if (url.matches("apppubs:\\/\\/channel\\/[0-9]\\/[A-Za-z0-9]*")) {
            String[] arr = StringUtils.getPathParams(url);
            ChannelFragment cf = ChannelFragmentFactory.getChannelFragment(Integer.parseInt
                    (arr[1]));
            Bundle args = new Bundle();
            args.putString(ChannelFragment.ARG_KEY, arr[2]);
            ContainerActivity.startContainerActivity(context, cf.getClass(), args);
        } else if (url.matches("apppubs://channelgroup/[0-9?&=a-zA-Z]*")) {//频道组
            String[] arr = StringUtils.getPathParams(url);
            String layout = StringUtils.getQueryParameter(url, "layout");
            ChannelsFragment frg = null;
            if (!"0".equals(layout)) {
                frg = new ChannelsSquareFragment();
            } else {
                frg = new ChannelsSlideFragment();
            }
            Bundle args = new Bundle();
            args.putString(ChannelsFragment.ARGUMENT_NAME_CHANNELTYPEID, arr[1]);
            ContainerActivity.startContainerActivity(context, frg.getClass(), args);
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
            ContainerActivity.startContainerActivity(context, pageF.getClass(), args);
        } else if (url.matches("apppubs:\\/\\/addressbook[\\S]*")) {
            String rootSuperId = StringUtils.getQueryParameter(url, "rootsuperid");
            Bundle args = new Bundle();
            args.putString(AddressBookFragement.ARGS_ROOT_DEPARTMENT_SUPER_ID, rootSuperId);
            args.putBoolean(ContainerActivity.EXTRA_BOOLEAN_IS_FULLSCREEN,true);
            ContainerActivity.startContainerActivity(context, AddressBookFragement.class, args);
        } else if (url.matches("apppubs:\\/\\/setting[\\S]*")) {
            String title = StringUtils.getQueryParameter(url, "title");
            ContainerActivity.startContainerActivity(context, SettingFragment.class, null, title);
        } else if (url.matches("apppubs:\\/\\/favorite[\\S]*")) {
            CollectionFragment frg = new CollectionFragment();
            ContainerActivity.startContainerActivity(context, frg.getClass());
        } else if (url.matches("apppubs:\\/\\/message[\\S]*")) {
            ContainerActivity.startContainerActivity(context, MsgRecordListFragment.class);
        } else if (url.matches("apppubs:\\/\\/history_message[\\S]*")) {
            ContainerActivity.startContainerActivity(context, HistoryFragment.class);
        } else if (url.matches("apppubs:\\/\\/baol[\\S]*")) {
            Intent intent = new Intent(context, BaoliaoActivity.class);
            context.startActivity(intent);
        } else if (url.matches("apppubs:\\/\\/user_account[\\S]*")) {
            String userId = AppContext.getInstance(context).getCurrentUser().getUserId();
            Intent intent = null;
            if (userId != null && !userId.equals("")) {// 已登录
                intent = new Intent(context, UserCencerActivity.class);
            } else {
                intent = new Intent(context, LoginActivity.class);
            }
            context.startActivity(intent);
        } else if (url.equals("apppubs://closewindow")) {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        } else if (url.startsWith("apppubs://qrcode")) {
            Intent intent = new Intent(context, CaptureActivity.class);
            context.startActivity(intent);
        } else if (url.startsWith("apppubs://service_no")) {
            String title = StringUtils.getQueryParameter(url, "title");
            Bundle args = new Bundle();
            args.putBoolean(ContainerActivity.EXTRA_BOOLEAN_IS_FULLSCREEN,true);
            ContainerActivity.startContainerActivity(context, ServiceNOsOfMineFragment.class, args, title);

        } else if (url.startsWith("tel:")) {
            String str[] = url.split(":");
            final String uri = url;
            final Context con = context;
            new ConfirmDialog(context, new ConfirmDialog.ConfirmListener() {

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
                Toast.makeText(context, params[1], Toast.LENGTH_LONG).show();
            }
        } else if (url.startsWith("apppubs://myfile")) {
            String title = StringUtils.getQueryParameter(url, "title");
            Bundle args = new Bundle();
            args.putString(ContainerActivity.EXTRA_STRING_TITLE, title);
            ContainerActivity.startContainerActivity(context, MyFileFragment.class, args);
        } else if (url.startsWith("hxLink://")) {
            String username = AppContext.getInstance(context).getCurrentUser().getUsername();
            String password = AppContext.getInstance(context).getCurrentUser().getPassword();
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
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "启动E-Link失败", Toast.LENGTH_LONG).show();
            }
        } else if (url.equals(TMenuItem.MENU_URL_EMAIL) || url.startsWith("apppubs://email")) {
            openEmailApp();
        } else {
            Toast.makeText(context, "请求地址(" + url + ")错误", Toast.LENGTH_SHORT).show();
        }

    }

    public void executeInHomeActivity(String menuId, HomeBaseActivity homeBaseActivity) {
        executeInHomeActivity(SugarRecord.findById(TMenuItem.class, menuId), homeBaseActivity);
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

    public void executeInHomeActivity(TMenuItem item, final HomeBaseActivity mHomeActivity) {

        LogM.log(this.getClass(), "item.getOpenType()" + item.getOpenType());

        mHomeTitleBar = mHomeActivity.getTitleBar();

        int type = item.getLocation();
        // 重复点击直接返回
        if (mCurMenuItem != null && mCurMenuItem.getId() == item.getId() && type == TMenuItem
                .MENU_LOCATION_PRIMARY) {
            return;
        }

        final String uri = item.getUrl();
        Intent intent = null;
        BaseFragment frg = null;

        if (mFragmentsMap.containsKey(item)) {
            frg = (BaseFragment) mFragmentsMap.get(item);
            LogM.log(this.getClass(), "此fragment已经存在了");
            mHomeActivity.changeContent(frg);
        } else if ((uri.startsWith("http://") || uri.startsWith("https://")) && type == TMenuItem
                .MENU_LOCATION_PRIMARY) {// webview方式打开

            String tempUri = convertUri(item, uri);

            frg = new WebAppFragment();
            Bundle args = new Bundle();
            args.putInt(WebAppFragment.ARGUMENT_INT_MENUBARTYPE, item.getMenuBarType());
            args.putString(WebAppFragment.ARGUMENT_STRING_URL, tempUri);
            args.putString(ContainerActivity.EXTRA_STRING_TITLE, item.getName());
            args.putString(WebAppFragment.ARGUMENT_STRING_MORE_MENUS, item.getWebAppMenus());
            args.putString(ChannelsFragment.ARGS_MENU_ID, item.getId());
            frg.setArguments(args);
            mFragmentsMap.put(item, frg);
            LogM.log(this.getClass(), "此fragment尚未存在");

            mHomeActivity.changeContent(frg);

        } else if (uri.startsWith("http://") || uri.startsWith("https://")) {
            String tempUri = convertUri(item, uri);
            Bundle args = new Bundle();
            args.putInt(WebAppFragment.ARGUMENT_INT_MENUBARTYPE, item.getMenuBarType());
            args.putString(WebAppFragment.ARGUMENT_STRING_URL, tempUri);
            args.putString(ContainerActivity.EXTRA_STRING_TITLE, item.getName());
            args.putString(WebAppFragment.ARGUMENT_STRING_MORE_MENUS, item.getWebAppMenus());
            args.putString(ChannelsFragment.ARGS_MENU_ID, item.getId());
            ContainerActivity.startContainerActivity(mHomeActivity, WebAppFragment.class, args);
        } else if (uri.equals(TMenuItem.MENU_URL_WEIBO) && type == TMenuItem
                .MENU_LOCATION_PRIMARY) {// 微博界面
            frg = CommonFragmentFactory.getFragment(CommonFragmentFactory.TYPE_WEIBO);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(TMenuItem.MENU_URL_WEIBO)) {
            ContainerActivity.startContainerActivity(mHomeActivity, WeiBoFragment.class, null, item
                    .getName());
        } else if (uri.contains("$qrcode") || uri.startsWith("apppubs://qrcode")) {// 二维码
            intent = new Intent(mHomeActivity, CaptureActivity.class);
            mHomeActivity.startActivity(intent);
        } else if (uri.equals(TMenuItem.MENU_URL_NEWS)) {// 新闻
            String channelTypeId = item.getChannelTypeId();
            //如果此资讯类型菜单只有一个频道则直接显示这个频道列表不显示多频道标签
            List<TNewsChannel> channelList = SugarRecord.find(TNewsChannel.class, "TYPE_ID=?",
                    new String[]{channelTypeId + ""}, null, "DISPLAY_ORDER", null);

            Bundle args = new Bundle();
            args.putString(ChannelsFragment.ARGS_MENU_ID, item.getId());
            if (channelList != null && channelList.size() == 1) {
                TNewsChannel nc = channelList.get(0);
                frg = ChannelFragmentFactory.getChannelFragment(nc.getShowType());
                args.putString(ChannelFragment.ARG_KEY, nc.getCode());
                ;
                frg.setArguments(args);
            } else {
                if (item.getChannelLayout() == TMenuItem.CHANNEL_LAYOUT_SLIDE) {
                    frg = new ChannelsSlideFragment();
                } else if (item.getChannelLayout() == TMenuItem.CHANNEL_LAYOUT_ZAKER) {
                    frg = new ChannelsSquareFragment();
                }

                args.putString(ChannelsFragment.ARGUMENT_NAME_CHANNELTYPEID, channelTypeId);
                frg.setArguments(args);
            }
            if (type == TMenuItem.MENU_LOCATION_PRIMARY) {
                mFragmentsMap.put(item, frg);
                mHomeActivity.changeContent(frg);
            } else {
                ContainerActivity.startContainerActivity(mHomeActivity, frg.getClass(), args, item.getName
                        ());
            }

        } else if (uri.equals(TMenuItem.MENU_URL_NEWSPAPER)) {// 报纸
            frg = new PapersFragment();
            Bundle b = new Bundle();
            b.putString(PapersFragment.ARGS_MENU_ID, item.getId());
            frg.setArguments(b);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
            // mActivity.changeContent(new PaperFragment());
            // intent = new Intent(mActivity,PapperActivity.class);
            // mActivity.startContainerActivity(intent);
        } else if (uri.equals(TMenuItem.MENU_URL_HISTORY_MESSAGE) && type == TMenuItem
                .MENU_LOCATION_PRIMARY) {// 推送消息
            frg = new HistoryFragment();
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(TMenuItem.MENU_URL_HISTORY_MESSAGE)) {
            ContainerActivity.startContainerActivity(mHomeActivity, HistoryFragment.class, null, "历史消息");
        } else if ((uri.equals(TMenuItem.MENU_URL_MESSAGE) || uri.startsWith("apppubs://message")
        ) && type == TMenuItem.MENU_LOCATION_PRIMARY) {
//            frg = new ServiceNOsOfMineFragment();
            frg = new ConversationListFragment();
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);

        } else if (uri.equals(TMenuItem.MENU_URL_MESSAGE) || uri.startsWith("apppubs://message")) {
            ContainerActivity.startContainerActivity(mHomeActivity, ServiceNOsOfMineFragment.class, null, item
                    .getName());
        } else if (uri.equals(TMenuItem.MENU_URL_PIC)) {// 图片
            frg = new ChannelPictureFragment();
            Bundle b = new Bundle();
            b.putString(ChannelPictureFragment.ARG_KEY, mApp.getWebAppCode());
            frg.setArguments(b);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(TMenuItem.MENU_URL_VIDEO)) {// 视频
            frg = new ChannelVideoFragment();
            Bundle b = new Bundle();
            b.putString(ChannelFragment.ARG_KEY, mApp.getWebAppCode());
            frg.setArguments(b);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals("app:{$jfshop}")) {// 商城

        } else if (uri.equals(TMenuItem.MENU_URL_BAOLIAO)) {// 爆料
            intent = new Intent(mHomeActivity, BaoliaoActivity.class);
            mHomeActivity.startActivity(intent);
        } else if (uri.equals("app:{$search}")) {// 搜索
            intent = new Intent(mHomeActivity, SearchActivity.class);
            mHomeActivity.startActivity(intent);
        } else if (uri.equals("app:{$weather}")) {// 天气
            intent = new Intent(mHomeActivity, WeatherActivity.class);
            mHomeActivity.startActivity(intent);
        } else if (uri.equals(TMenuItem.MENU_URL_FAVORITE) && type == TMenuItem
                .MENU_LOCATION_PRIMARY) {// 收藏
            frg = new CollectionFragment();
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(TMenuItem.MENU_URL_FAVORITE)) {// 收藏
            frg = new CollectionFragment();
            ContainerActivity.startContainerActivity(mHomeActivity, frg.getClass(), null, item.getName());
        } else if (uri.equals("app:{$vote}")) {// 投票
            intent = new Intent(mHomeActivity, VoteActivity.class);
            mHomeActivity.startActivity(intent);
        } else if (uri.equals("app:{$more}")) {// 更多
            Bundle args = new Bundle();
            args.putString(MenuGroupsFragment.ARGS_SUPER_ID, item.getId());
            args.putString(ChannelsFragment.ARGS_MENU_ID, item.getId());
            frg = new MoreFragment();
            frg.setArguments(args);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(TMenuItem.MENU_URL_MENU) || uri.startsWith("apppubs://menugroups"))
        {// 逻辑菜单
            if (ViewCourier.openLoginViewIfNeeded(uri, mHomeActivity)) {
                return;
            }
            frg = new MenuGroupsFragment();
            Bundle args = new Bundle();
            args.putString(MenuGroupsFragment.ARGS_SUPER_ID, item.getId());
            args.putString(ChannelsFragment.ARGS_MENU_ID, item.getId());
            frg.setArguments(args);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(TMenuItem.MENU_URL_ADDRESSBOOK) || uri.startsWith("apppubs://addressbook")) {
            Bundle args = new Bundle();
            args.putString(AddressBookFragement.ARGS_ROOT_DEPARTMENT_SUPER_ID, item
                    .getChannelTypeId());
            frg = new AddressBookFragement();
            frg.setArguments(args);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(TMenuItem.MENU_URL_SETTING) && type == TMenuItem
                .MENU_LOCATION_PRIMARY) {
            frg = new SettingFragment();
            Bundle args = new Bundle();
            args.putString(TitleMenuFragment.ARGS_MENU_ID, item.getId());
            frg.setArguments(args);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(TMenuItem.MENU_URL_SETTING)) {
            startSettingView(mContext, item.getId());
        } else if (uri.equals(TMenuItem.MENU_URL_EMAIL)) {

            openEmailApp();
        } else if (uri.equals("app:{$menu_extra}")) {
            frg = new MenuGroupsFragment();
            Bundle args = new Bundle();
            args.putString(MenuGroupsFragment.ARGS_SUPER_ID, item.getId());
            args.putString(TitleMenuFragment.ARGS_MENU_ID, item.getId());
            frg.setArguments(args);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(TMenuItem.MENU_URL_MY_FILE)) {
            intent = new Intent(mHomeActivity, MyFileFragment.class);
            intent.putExtra(BaseActivity.EXTRA_STRING_TITLE, item.getName());
            mHomeActivity.startActivity(intent);

        } else if (uri.startsWith("tel:")) {
            String str[] = uri.split(":");

            new ConfirmDialog(mHomeActivity, new ConfirmDialog.ConfirmListener() {

                @Override
                public void onOkClick() {
                    Intent intentCall = new Intent(android.content.Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse(uri));
                    mHomeActivity.startActivity(intentCall);
                }

                @Override
                public void onCancelClick() {

                }
            }, "确定拨号?", "电话：" + str[1], "放弃", "拨号").show();

        } else if (uri.equals(TMenuItem.MENU_URL_USER_ACCOUNT)) {
            execute(ACTION_USER_CENTER);
        } else if (uri.startsWith("apppubs://page") && type == TMenuItem.MENU_LOCATION_PRIMARY) {
            frg = new PageFragment();
            Bundle args = new Bundle();
            String[] params = StringUtils.getPathParams(uri);
            args.putString(PageFragment.EXTRA_STRING_NAME_PAGE_ID, params[1]);
            frg.setArguments(args);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);

        } else if (uri.startsWith("apppubs://page")) {
            execute(mHomeActivity, uri);
        } else if (uri.startsWith("apppubs://service_no")) {
            String title = StringUtils.getQueryParameter(uri, "title");

            frg = new ServiceNOsOfMineFragment();
            Bundle args = new Bundle();
            String[] params = StringUtils.getPathParams(uri);
            frg.setArguments(args);
            frg.setTitle(title);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);

        } else if (uri.matches("apppubs:\\/\\/setting[\\S]*")) {
            String title = StringUtils.getQueryParameter(uri, "title");
            frg = new SettingFragment();
            Bundle args = new Bundle();
            String[] params = StringUtils.getPathParams(uri);
            frg.setArguments(args);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.matches("apppubs:\\/\\/channel\\/[0-9]\\/[A-Za-z0-9]*")) {
            String[] arr = StringUtils.getPathParams(uri);
            frg = ChannelFragmentFactory.getChannelFragment(Integer.parseInt(arr[1]));
            Bundle args = new Bundle();
            args.putString(ChannelFragment.ARG_KEY, arr[2]);
            frg.setArguments(args);
            mHomeActivity.changeContent(frg);
        } else {
            if (type == TMenuItem.MENU_LOCATION_PRIMARY) {
                frg = new ExceptionFragment();
                mHomeActivity.changeContent(frg);
            } else {
                execute(mHomeActivity, uri);
            }


        }


        //注意！每次请求菜单时均要通过这里，但上面一大段的可能被跳过,
        // 如果请求的是主菜单则需要更改标题
        if (type == TMenuItem.MENU_LOCATION_PRIMARY) {

            if (mApp.getLayoutScheme() == App.LAYOUT_BOTTOM_MENU) {

                if (isWeatherRcvRegistered) {
//					mHomeActivity.unregisterReceiver(mWeatherRcv);
                    isWeatherRcvRegistered = false;
                }
            }
            mCurMenuItem = item;
            LogM.log(this.getClass(), "切换主导航菜单：item" + item.getName() + "item.getSortid:" + item
                    .getSortId());
            if (uri.contains("$menu_extra")) {

                mHomeTitleBar.addRightBtnWithImageResourceIdAndClickListener(R.drawable.user, new
                        OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                execute(ACTION_USER_CENTER);
                            }
                        });

                mHomeTitleBar.setTitle(item.getName());
            } else if (item.getSortId() != 1 && !item.getUrl().contains("$addressbook") && !item
                    .getUrl().contains("$newspaper")) {
                mHomeTitleBar.setTitle(item.getName());
            }

            if (item.getSortId() == 1) {
                if (mApp.getLayoutLocalScheme() == App.LAYOUT_BOTTOM_MENU) {
//					refreshWeather();
//                    mHomeActivity.registerReceiver(mWeatherRcv, new IntentFilter(Actions
// .REFRESH_WEATHER));
                    isWeatherRcvRegistered = true;
                }

//                mHomeTitleBar.setTitle(mApp.getName());
            }
        }

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

    private String convertUri(TMenuItem item, final String uri) {
        String tempUri = uri;
        Map<String, String> customIpMap = (Map<String, String>) FileUtils.readObj(mContext,
                CustomWebAppUrlProtocolAndIpActivity.CUSTOM_WEB_APP_URL_SERIALIZED_FILE_NAME);
        if (customIpMap != null) {
            String customIp = customIpMap.get(item.getId());
            if (!TextUtils.isEmpty(customIp)) {
                Pattern pattern = Pattern.compile("(http|https)://[^/]+");
                Matcher matcher = pattern.matcher(uri);
                matcher.find();
                tempUri = matcher.replaceFirst(customIp);
            }
        }
        return tempUri;
    }

    private View weather;

//	private void refreshWeather() {
//
//		mHomeTitleBar.setLeftBtnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(mContext, WeatherActivity.class);
//				mContext.startContainerActivity(intent);
//			}
//		});
//
//		List<Weather> list = mHomeActivity.getWeatherList();
//		if (list==null||list.size()==0) {
//			TextView tv = new TextView(mHomeActivity);
//			tv.setText("获取天气");
//			tv.setGravity(Gravity.CENTER);
//			tv.setTextColor(Color.WHITE);
//			mHomeTitleBar.setLeftView(tv);
//			return;
//		}
//
//		if (weather == null)
//			weather = mHomeActivity.getLayoutInflater().inflate(R.layout.weather_small, null);
//
//		mHomeTitleBar.setLeftView(weather);
//		TextView mWeatherTempTv = (TextView) weather.findViewById(R.id.weather_temp_tv);
//		// 天气信息
//		String temp = list.get(0).getTemp();
//		mWeatherTempTv.setText(temp.replaceAll(" ", ""));
//		TextView cityTv = (TextView) weather.findViewById(R.id.weather_city_tv);
//		cityTv.setText(list.get(0).getCityName());
//		ImageView weatherIv = (ImageView) weather.findViewById(R.id.weather_im);
//		weatherIv.setImageResource(WeatherUtils.solvedWeather(list.get(0).getWeather()));
//	}

    public void destory() {
        mHomeviewController = null;
        /*if (mWeatherRcv!=null&&isWeatherRcvRegistered) {
            mHomeActivity.unregisterReceiver(mWeatherRcv);
		}*/
    }

}
