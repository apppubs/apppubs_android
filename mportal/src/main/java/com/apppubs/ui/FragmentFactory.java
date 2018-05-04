package com.apppubs.ui;

import android.os.Bundle;

import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.ui.fragment.ChannelFragment;
import com.apppubs.ui.fragment.ChannelFragmentFactory;
import com.apppubs.ui.fragment.ChannelsFragment;
import com.apppubs.ui.fragment.ChannelsSlideFragment;
import com.apppubs.ui.fragment.ChannelsSquareFragment;
import com.apppubs.ui.fragment.ExceptionFragment;
import com.apppubs.ui.fragment.PapersFragment;
import com.apppubs.ui.fragment.ServiceNOsOfMineFragment;
import com.apppubs.ui.fragment.SettingFragment;
import com.apppubs.ui.message.fragment.AddressBookFragement;
import com.apppubs.ui.message.fragment.ConversationListFragment;
import com.apppubs.ui.myfile.MyFileFragment;
import com.apppubs.ui.page.PageFragment;
import com.apppubs.ui.webapp.WebAppFragment;
import com.apppubs.util.StringUtils;

public class FragmentFactory {

    public static BaseFragment getFragment(String uri) {
        BaseFragment frg;
        if ((uri.startsWith("http://") || uri.startsWith("https://"))) {
            frg = new WebAppFragment();
            Bundle args = new Bundle();
            args.putString(WebAppFragment.ARGUMENT_STRING_URL, uri);
            frg.setArguments(args);
        } else if (uri.matches("apppubs://channelgroup/[0-9?&=a-zA-Z]*")) {//频道组
            String[] arr = StringUtils.getPathParams(uri);
            String layout = StringUtils.getQueryParameter(uri, "layout");
            if (!"0".equals(layout)) {
                frg = new ChannelsSquareFragment();
            } else {
                frg = new ChannelsSlideFragment();
            }
            Bundle args = new Bundle();
            args.putString(ChannelsFragment.ARGUMENT_NAME_CHANNELTYPEID, arr[1]);
            frg.setArguments(args);
        } else if (uri.matches("apppubs:\\/\\/channel\\/[0-9]\\/[A-Za-z0-9]*")) {
            String[] arr = StringUtils.getPathParams(uri);
            ChannelFragment cf = ChannelFragmentFactory.getChannelFragment(Integer.parseInt
                    (arr[1]));
            Bundle args = new Bundle();
            args.putString(ChannelFragment.ARG_KEY, arr[2]);
            frg = cf;
        } else if (uri.startsWith("apppubs//newspaper")) {// 报纸
            frg = new PapersFragment();
            Bundle b = new Bundle();
            frg.setArguments(b);
        } else if (uri.startsWith("apppubs://message")) {
            frg = new ConversationListFragment();
        } else if (uri.startsWith("apppubs://addressbook")) {
            String rootSuperId = StringUtils.getQueryParameter(uri, "rootsuperid");
            Bundle args = new Bundle();
            args.putString(AddressBookFragement.ARGS_ROOT_DEPARTMENT_SUPER_ID, rootSuperId);
            frg = new AddressBookFragement();
            frg.setArguments(args);
        } else if (uri.startsWith("apppubs://myfile")) {
            frg = new MyFileFragment();
        } else if (uri.startsWith("apppubs://page")) {
            frg = new PageFragment();
            Bundle args = new Bundle();
            String[] params = StringUtils.getPathParams(uri);
            args.putString(PageFragment.EXTRA_STRING_NAME_PAGE_ID, params[1]);
            frg.setArguments(args);
        } else if (uri.startsWith("apppubs://service_no")) {
            String title = StringUtils.getQueryParameter(uri, "title");
            frg = new ServiceNOsOfMineFragment();
            Bundle args = new Bundle();
            frg.setArguments(args);
            frg.setTitle(title);
        } else if (uri.matches("apppubs:\\/\\/setting[\\S]*")) {
            String title = StringUtils.getQueryParameter(uri, "title");
            frg = new SettingFragment();
            Bundle args = new Bundle();
            frg.setArguments(args);
            frg.setTitle(title);
        } else {
            frg = new ExceptionFragment();
        }
        return frg;
    }
}
