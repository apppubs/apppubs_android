package com.mportal.client.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.mportal.client.MportalApplication;
import com.mportal.client.MyFileActivity;
import com.mportal.client.R;
import com.mportal.client.bean.App;
import com.mportal.client.bean.MenuItem;
import com.mportal.client.bean.NewsChannel;
import com.mportal.client.bean.User;
import com.mportal.client.constant.Actions;
import com.mportal.client.message.fragment.AddressBookFragement;
import com.mportal.client.fragment.BaseFragment;
import com.mportal.client.fragment.ChannelFragment;
import com.mportal.client.fragment.ChannelFragmentFactory;
import com.mportal.client.fragment.ChannelPictureFragment;
import com.mportal.client.fragment.ChannelVideoFragment;
import com.mportal.client.fragment.ChannelsFragment;
import com.mportal.client.fragment.ChannelsSlideFragment;
import com.mportal.client.fragment.ChannelsSquareFragment;
import com.mportal.client.fragment.CollectionFragment;
import com.mportal.client.fragment.CommonFragmentFactory;
import com.mportal.client.fragment.ConversationFragment;
import com.mportal.client.fragment.ExceptionFragment;
import com.mportal.client.fragment.HistoryFragment;
import com.mportal.client.fragment.MenuGroupsFragment;
import com.mportal.client.fragment.MoreFragment;
import com.mportal.client.fragment.MsgRecordListFragment;
import com.mportal.client.fragment.PageFragment;
import com.mportal.client.fragment.PapersFragment;
import com.mportal.client.fragment.SettingFragment;
import com.mportal.client.fragment.TitleMenuFragment;
import com.mportal.client.fragment.WebAppFragment;
import com.mportal.client.fragment.WeiBoFragment;
import com.mportal.client.util.FileUtils;
import com.mportal.client.util.LogM;
import com.mportal.client.util.StringUtils;
import com.mportal.client.widget.ConfirmDialog;
import com.mportal.client.widget.ConfirmDialog.ConfirmListener;
import com.mportal.client.widget.TitleBar;
import com.orm.SugarRecord;

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

    private Map<MenuItem, Fragment> mFragmentsMap;
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
    private MenuItem mCurMenuItem;
    private static ViewCourier mHomeviewController;

    private ViewCourier(Context context) {
        mContext = context;
        mApp = MportalApplication.app;
        mFragmentsMap = new HashMap<MenuItem, Fragment>();

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
        ContainerActivity.startActivity(context, SettingFragment.class, args, "设置");
    }

    public void execute(int action) {
        switch (action) {
            case ACTION_USER_CENTER:
                String userId = MportalApplication.user.getUserId();
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
    public static void execute(BaseActivity context, String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            WebAppFragment frg = new WebAppFragment();
            Bundle args = new Bundle();
            args.putString(WebAppFragment.ARGUMENT_STRING_URL, url);
            frg.setArguments(args);
            ContainerActivity.startActivity(context, WebAppFragment.class, args);
        } else if (url.matches("apppubs:\\/\\/newsinfo\\/[A-Z0-9]*\\/[A-Za-z0-9]*\\/[A-Za-z0-9]*")) {//新闻正文
            String[] arr = StringUtils.getPathParams(url);
            NewsInfoBaseActivity.startInfoActivity(context, arr[1], new String[]{arr[2], arr[3]});//频道
        } else if (url.matches("apppubs:\\/\\/channel\\/[0-9]\\/[A-Za-z0-9]*")) {
            String[] arr = StringUtils.getPathParams(url);
            ChannelFragment cf = ChannelFragmentFactory.getChannelFragment(Integer.parseInt(arr[1]));
            Bundle args = new Bundle();
            args.putString(ChannelFragment.ARG_KEY, arr[2]);
            ContainerActivity.startActivity(context, cf.getClass(), args);
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
            ContainerActivity.startActivity(context, frg.getClass(), args);
        } else if (url.matches("apppubs:\\/\\/page\\/[\\S]*")) {
            PageFragment pageF = new PageFragment();
            String[] pathParams = StringUtils.getPathParams(url);
            String titlebarFlag = StringUtils.getQueryParameter(url, "titlebar");
            Bundle args = new Bundle();
            if (!TextUtils.isEmpty(titlebarFlag) && titlebarFlag.equals("0")) {
                args.putBoolean(ContainerActivity.EXTRA_BOOLEAN_IS_FULLSCREEN, true);
            }
            args.putString(PageFragment.EXTRA_STRING_NAME_PAGE_ID, pathParams[1]);
            ContainerActivity.startActivity(context, pageF.getClass(), args);
        } else if (url.matches("apppubs:\\/\\/addressbook[\\S]*")) {
            String rootSuperId = StringUtils.getQueryParameter(url, "rootsuperid");
            Bundle args = new Bundle();
            args.putString(AddressBookFragement.ARGS_ROOT_DEPARTMENT_SUPER_ID, rootSuperId);
            ContainerActivity.startActivity(context, AddressBookFragement.class, args);
        } else if (url.matches("apppubs:\\/\\/setting[\\S]*")) {
            ContainerActivity.startActivity(context, SettingFragment.class);
        } else if (url.matches("apppubs:\\/\\/favorite[\\S]*")) {
            CollectionFragment frg = new CollectionFragment();
            ContainerActivity.startActivity(context, frg.getClass());
        } else if (url.matches("apppubs:\\/\\/message[\\S]*")) {
            ContainerActivity.startActivity(context, MsgRecordListFragment.class);
        } else if (url.matches("apppubs:\\/\\/history_message[\\S]*")) {
            ContainerActivity.startActivity(context, HistoryFragment.class);
        } else if (url.matches("apppubs:\\/\\/baol[\\S]*")) {
            Intent intent = new Intent(context, BaoliaoActivity.class);
            context.startActivity(intent);
        } else if (url.matches("apppubs:\\/\\/user_account[\\S]*")) {
            String userId = MportalApplication.user.getUserId();
            Intent intent = null;
            if (userId != null && !userId.equals("")) {// 已登录
                intent = new Intent(context, UserCencerActivity.class);
            } else {
                intent = new Intent(context, LoginActivity.class);
            }
            context.startActivity(intent);
        } else if (url.equals("apppubs://closewindow")) {
            context.finish();
        } else if (url.equals("apppubs://qrcode")) {
            Intent intent = new Intent(context, CaptureActivity.class);
            context.startActivity(intent);
        } else if (url.startsWith("tel:")) {
            String str[] = url.split(":");
            final String uri = url;
            final Context con = context;
            new ConfirmDialog(context, new ConfirmListener() {

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
        } else {
            Toast.makeText(context, "请求地址(" + url + ")错误", Toast.LENGTH_SHORT).show();
        }

    }

    public void executeInHomeActivity(String menuId, HomeBaseActivity homeBaseActivity) {
        executeInHomeActivity(SugarRecord.findById(MenuItem.class, menuId), homeBaseActivity);
    }

    public static void openMenu(String menuId, final BaseActivity context) {

        MenuItem menuItem = SugarRecord.findById(MenuItem.class, menuId);
        String uri = menuItem.getUrl();
        if (uri.equals(MenuItem.MENU_URL_BAOLIAO)) {
            Intent intent = new Intent(context, BaoliaoActivity.class);
            context.startActivity(intent);
        } else if (uri.equals(MenuItem.MENU_URL_USER_ACCOUNT)) {
            String userId = MportalApplication.user.getUserId();
            Intent intent = null;
            if (userId != null && !userId.equals("")) {// 已登录
                intent = new Intent(context, UserCencerActivity.class);
            } else {
                intent = new Intent(context, LoginActivity.class);
            }
            context.startActivity(intent);
        } else if (uri.equals(MenuItem.MENU_URL_LOGOUT)) {
            new ConfirmDialog(context,
                    new ConfirmDialog.ConfirmListener() {

                        @Override
                        public void onOkClick() {

                            if (MportalApplication.app.getLoginFlag() == App.LOGIN_INAPP) {

                                User user = new User();
                                MportalApplication.saveAndRefreshUser(context, user);
                                context.finish();
                            } else {
                                context.sendBroadcast(new Intent(Actions.ACTION_LOGOUT));
                            }
                        }

                        @Override
                        public void onCancelClick() {

                        }
                    }, "确定注销登陆吗？", "取消", "注销").show();
        }
    }

    public static boolean openLoginViewIfNeeded(String url, BaseActivity context) {
        String apppubsloginFlag = StringUtils.getQueryParameter(url, "apppubslogin");
        String userId = MportalApplication.user.getUserId();
        if (!TextUtils.isEmpty(apppubsloginFlag) && apppubsloginFlag.equals("1") && TextUtils.isEmpty(userId)) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivityForResult(intent, LoginActivity.REQUEST_CODE);
            return true;
        }
        return false;
    }

    public void executeInHomeActivity(MenuItem item, final HomeBaseActivity mHomeActivity) {

        LogM.log(this.getClass(), "item.getOpenType()" + item.getOpenType());

        mHomeTitleBar = mHomeActivity.getTitleBar();

        int type = item.getLocation();
        // 重复点击直接返回
        if (mCurMenuItem != null && mCurMenuItem.getId() == item.getId() && type == MenuItem.MENU_LOCATION_PRIMARY) {
            return;
        }

        final String uri = item.getUrl();
        Intent intent = null;
        BaseFragment frg = null;
        //配置顶部菜单条是否显示
        //当url的titlebar参数和menu的参数都为true时才可以显示标题
        String needTitleBarFlag = StringUtils.getQueryParameter(uri, "titlebar");
        if (!(!TextUtils.isEmpty(needTitleBarFlag) && needTitleBarFlag.equals("0")) && item.getTitleBarShowFlag() == MenuItem.TITLEBAR_SHOW_FLAG_TRUE) {
            mHomeActivity.setNeedTitleBar(true);
        } else {
            mHomeActivity.setNeedTitleBar(false);
        }


        if (mFragmentsMap.containsKey(item)) {
            frg = (BaseFragment) mFragmentsMap.get(item);
            LogM.log(this.getClass(), "此fragment已经存在了");
            mHomeActivity.changeContent(frg);
        } else if ((uri.startsWith("http://") || uri.startsWith("https://")) && type == MenuItem.MENU_LOCATION_PRIMARY) {// webview方式打开

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
            ContainerActivity.startActivity(mHomeActivity, WebAppFragment.class, args);
        } else if (uri.equals(MenuItem.MENU_URL_WEIBO) && type == MenuItem.MENU_LOCATION_PRIMARY) {// 微博界面
            frg = CommonFragmentFactory.getFragment(CommonFragmentFactory.TYPE_WEIBO);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(MenuItem.MENU_URL_WEIBO)) {
            ContainerActivity.startActivity(mHomeActivity, WeiBoFragment.class, null, item.getName());
        } else if (uri.contains("$qrcode")) {// 二维码
            intent = new Intent(mHomeActivity, CaptureActivity.class);
            mHomeActivity.startActivity(intent);
        } else if (uri.equals(MenuItem.MENU_URL_NEWS)) {// 新闻
            String channelTypeId = item.getChannelTypeId();
            //如果此资讯类型菜单只有一个频道则直接显示这个频道列表不显示多频道标签
            List<NewsChannel> channelList = SugarRecord.find(NewsChannel.class, "TYPE_ID=?", new String[]{channelTypeId + ""}, null, "DISPLAY_ORDER", null);

            Bundle args = new Bundle();
            args.putString(ChannelsFragment.ARGS_MENU_ID, item.getId());
            if (channelList != null && channelList.size() == 1) {
                NewsChannel nc = channelList.get(0);
                frg = ChannelFragmentFactory.getChannelFragment(nc.getShowType());
                args.putString(ChannelFragment.ARG_KEY, nc.getCode());
                ;
                frg.setArguments(args);
            } else {
                if (item.getChannelLayout() == MenuItem.CHANNEL_LAYOUT_SLIDE) {
                    frg = new ChannelsSlideFragment();
                } else if (item.getChannelLayout() == MenuItem.CHANNEL_LAYOUT_ZAKER) {
                    frg = new ChannelsSquareFragment();
                }

                args.putString(ChannelsFragment.ARGUMENT_NAME_CHANNELTYPEID, channelTypeId);
                frg.setArguments(args);
            }
            if (type == MenuItem.MENU_LOCATION_PRIMARY) {
                mFragmentsMap.put(item, frg);
                mHomeActivity.changeContent(frg);
            } else {
                ContainerActivity.startActivity(mHomeActivity, frg.getClass(), args, item.getName());
            }

        } else if (uri.equals(MenuItem.MENU_URL_NEWSPAPER)) {// 报纸
            frg = new PapersFragment();
            Bundle b = new Bundle();
            b.putString(PapersFragment.ARGS_MENU_ID, item.getId());
            frg.setArguments(b);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
            // mActivity.changeContent(new PaperFragment());
            // intent = new Intent(mActivity,PapperActivity.class);
            // mActivity.startActivity(intent);
        } else if (uri.equals(MenuItem.MENU_URL_HISTORY_MESSAGE) && type == MenuItem.MENU_LOCATION_PRIMARY) {// 推送消息
            frg = new HistoryFragment();
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(MenuItem.MENU_URL_HISTORY_MESSAGE)) {
            ContainerActivity.startActivity(mHomeActivity, HistoryFragment.class, null, "历史消息");
        } else if ((uri.equals(MenuItem.MENU_URL_MESSAGE)|| uri.startsWith("apppubs://message")) && type == MenuItem.MENU_LOCATION_PRIMARY) {
            frg = new ConversationFragment();
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);

        } else if (uri.equals(MenuItem.MENU_URL_MESSAGE)|| uri.startsWith("apppubs://message")) {
            ContainerActivity.startActivity(mHomeActivity, ConversationFragment.class, null, item.getName());
        } else if (uri.equals(MenuItem.MENU_URL_PIC)) {// 图片
            frg = new ChannelPictureFragment();
            Bundle b = new Bundle();
            b.putString(ChannelPictureFragment.ARG_KEY, MportalApplication.app.getWebAppCode());
            frg.setArguments(b);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(MenuItem.MENU_URL_VIDEO)) {// 视频
            frg = new ChannelVideoFragment();
            Bundle b = new Bundle();
            b.putString(ChannelFragment.ARG_KEY, mApp.getWebAppCode());
            frg.setArguments(b);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals("app:{$jfshop}")) {// 商城

        } else if (uri.equals(MenuItem.MENU_URL_BAOLIAO)) {// 爆料
            intent = new Intent(mHomeActivity, BaoliaoActivity.class);
            mHomeActivity.startActivity(intent);
        } else if (uri.equals("app:{$search}")) {// 搜索
            intent = new Intent(mHomeActivity, SearchActivity.class);
            mHomeActivity.startActivity(intent);
        } else if (uri.equals("app:{$weather}")) {// 天气
            intent = new Intent(mHomeActivity, WeatherActivity.class);
            mHomeActivity.startActivity(intent);
        } else if (uri.equals(MenuItem.MENU_URL_FAVORITE) && type == MenuItem.MENU_LOCATION_PRIMARY) {// 收藏
            frg = new CollectionFragment();
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(MenuItem.MENU_URL_FAVORITE)) {// 收藏
            frg = new CollectionFragment();
            ContainerActivity.startActivity(mHomeActivity, frg.getClass(), null, item.getName());
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
        } else if (uri.equals(MenuItem.MENU_URL_MENU) || uri.startsWith("apppubs://menugroups")) {// 逻辑菜单
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
        } else if (uri.equals(MenuItem.MENU_URL_ADDRESSBOOK) && type == MenuItem.MENU_LOCATION_PRIMARY) {
            Bundle args = new Bundle();
            args.putString(AddressBookFragement.ARGS_ROOT_DEPARTMENT_SUPER_ID, item.getChannelTypeId());
            frg = new AddressBookFragement();
            frg.setArguments(args);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(MenuItem.MENU_URL_ADDRESSBOOK)) {
            Bundle args = new Bundle();
            args.putString(AddressBookFragement.ARGS_ROOT_DEPARTMENT_SUPER_ID, item.getChannelTypeId());
            ContainerActivity.startActivity(mHomeActivity, AddressBookFragement.class, args);

        } else if (uri.equals(MenuItem.MENU_URL_SETTING) && type == MenuItem.MENU_LOCATION_PRIMARY) {
            frg = new SettingFragment();
            Bundle args = new Bundle();
            args.putString(TitleMenuFragment.ARGS_MENU_ID, item.getId());
            frg.setArguments(args);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(MenuItem.MENU_URL_SETTING)) {
            startSettingView(mContext, item.getId());
        } else if (uri.equals(MenuItem.MENU_URL_EMAIL)) {

            try {
                //获得邮箱包名信息
                PackageInfo pi = mHomeActivity.getPackageManager().getPackageInfo("com.android.email", 0);
                //获得当前应用程序的包管理器
                PackageManager pm = mHomeActivity.getPackageManager();

                Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);

                resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                resolveIntent.setPackage(pi.packageName);

                List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

                ResolveInfo ri = apps.iterator().next();

                if (ri != null) {

                    String packageName = ri.activityInfo.packageName;

                    String className = ri.activityInfo.name;


                    intent = new Intent(Intent.ACTION_MAIN);

                    intent.addCategory(Intent.CATEGORY_LAUNCHER);

                    ComponentName cn = new ComponentName(packageName, className);

                    intent.setComponent(cn);

                    mHomeActivity.startActivity(intent);

                }

            } catch (Exception e) {

                e.printStackTrace();
                Toast.makeText(mHomeActivity, "没有安装邮件客户端!", Toast.LENGTH_SHORT).show();

            }


//			intent = new Intent(Intent.ACTION_SENDTO);
//		    intent.setData(Uri.parse("mailto:"));
////		    intent.putExtra(Intent.EXTRA_EMAIL, addresses);
////		    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
////		    intent.putExtra(Intent.EXTRA_STREAM, attachment);
//		    if (intent.resolveActivity(mHomeActivity.getPackageManager()) != null) {
//		        mHomeActivity.startActivity(intent);
//		    }else{
//		    	Toast.makeText(mHomeActivity, "没有邮件客户端", Toast.LENGTH_LONG).show();
//		    	
//		    }


        } else if (uri.equals("app:{$menu_extra}")) {
            frg = new MenuGroupsFragment();
            Bundle args = new Bundle();
            args.putString(MenuGroupsFragment.ARGS_SUPER_ID, item.getId());
            args.putString(TitleMenuFragment.ARGS_MENU_ID, item.getId());
            frg.setArguments(args);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);
        } else if (uri.equals(MenuItem.MENU_URL_MY_FILE)) {
            intent = new Intent(mHomeActivity, MyFileActivity.class);
            intent.putExtra(BaseActivity.EXTRA_STRING_TITLE, item.getName());
            mHomeActivity.startActivity(intent);

        } else if (uri.startsWith("tel:")) {
            String str[] = uri.split(":");

            new ConfirmDialog(mHomeActivity, new ConfirmListener() {

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

        } else if (uri.equals(MenuItem.MENU_URL_USER_ACCOUNT)) {
            execute(ACTION_USER_CENTER);
        } else if (uri.startsWith("apppubs://page") && type == MenuItem.MENU_LOCATION_PRIMARY) {
            frg = new PageFragment();
            Bundle args = new Bundle();
            String[] params = StringUtils.getPathParams(uri);
            args.putString(PageFragment.EXTRA_STRING_NAME_PAGE_ID, params[1]);
            frg.setArguments(args);
            mFragmentsMap.put(item, frg);
            mHomeActivity.changeContent(frg);

        } else if (uri.startsWith("apppubs://page")) {
            execute(mHomeActivity, uri);
        } else {
            if (type == MenuItem.MENU_LOCATION_PRIMARY) {
                frg = new ExceptionFragment();
                mHomeActivity.changeContent(frg);
            } else {
                Toast.makeText(mHomeActivity, "应用类型不支持或者配置错误！", Toast.LENGTH_LONG).show();
            }


        }


        //注意！每次请求菜单时均要通过这里，但上面一大段的可能被跳过,
        // 如果请求的是主菜单则需要更改标题
        if (type == MenuItem.MENU_LOCATION_PRIMARY) {

            if (mApp.getLayoutScheme() == App.LAYOUT_BOTTOM_MENU) {

                if (isWeatherRcvRegistered) {
//					mHomeActivity.unregisterReceiver(mWeatherRcv);
                    isWeatherRcvRegistered = false;
                }
            }
            mCurMenuItem = item;
            LogM.log(this.getClass(), "切换主导航菜单：item" + item.getName() + "item.getSortid:" + item.getSortId());
            if (uri.contains("$menu_extra")) {

                mHomeTitleBar.addRightBtnWithImageResourceIdAndClickListener(R.drawable.user, new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        execute(ACTION_USER_CENTER);
                    }
                });

                mHomeTitleBar.setTitle(item.getName());
            } else if (item.getSortId() != 1 && !item.getUrl().contains("$addressbook") && !item.getUrl().contains("$newspaper")) {
                mHomeTitleBar.setTitle(item.getName());
            }

            if (item.getSortId() == 1) {
                if (mApp.getLayoutLocalScheme() == App.LAYOUT_BOTTOM_MENU) {
//					refreshWeather();
                    mHomeActivity.registerReceiver(mWeatherRcv, new IntentFilter(Actions.REFRESH_WEATHER));
                    isWeatherRcvRegistered = true;
                }

                mHomeTitleBar.setTitle(MportalApplication.app.getName());
            }
        }

    }

    private String convertUri(MenuItem item, final String uri) {
        String tempUri = uri;
        Map<String, String> customIpMap = (Map<String, String>) FileUtils.readObj(mContext, CustomWebAppUrlProtocolAndIpActivity.CUSTOM_WEB_APP_URL_SERIALIZED_FILE_NAME);
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
//				mContext.startActivity(intent);
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
