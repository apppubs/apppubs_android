package com.apppubs.ui;

import android.os.Bundle;

import com.apppubs.bean.ApppubsProtocol;
import com.apppubs.constant.Constants;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.ui.fragment.ExceptionFragment;
import com.apppubs.ui.fragment.PapersFragment;
import com.apppubs.ui.fragment.ServiceNOsOfMineFragment;
import com.apppubs.ui.fragment.SettingFragment;
import com.apppubs.ui.message.fragment.AdbookFragement;
import com.apppubs.ui.message.fragment.ConversationListFragment;
import com.apppubs.ui.myfile.MyFileFragment;
import com.apppubs.ui.news.ChannelFragment;
import com.apppubs.ui.news.ChannelFragmentFactory;
import com.apppubs.ui.news.ChannelsFragment;
import com.apppubs.ui.news.ChannelsSlideFragment;
import com.apppubs.ui.page.PageFragment;
import com.apppubs.ui.webapp.WebAppFragment;
import com.apppubs.util.StringUtils;
import com.apppubs.util.Utils;

public class FragmentFactory {

    public static BaseFragment getFragment(String uri) {
        BaseFragment frg;
        if ((uri.startsWith("http://") || uri.startsWith("https://"))) {
            frg = new WebAppFragment();
            Bundle args = new Bundle();
            args.putString(WebAppFragment.ARGUMENT_STRING_URL, uri);
            String titlebarFlag = StringUtils.getQueryParameter(uri, "titlebar");
            if (!Utils.isEmpty(titlebarFlag)){
                args.putBoolean(WebAppFragment.ARGUMENT_STRING_NEED_TITLEBAR,Utils.compare(titlebarFlag,"1"));
            }
            frg.setArguments(args);
        } else if(ApppubsProtocol.isApppubsProtocol(uri)){
            ApppubsProtocol pro = new ApppubsProtocol(uri);
            if (Constants.APPPUBS_PROTOCOL_TYPE_CHANNEL_GROUP.equals(pro.getType())){
                String[] arr = StringUtils.getPathParams(uri);
                frg = new ChannelsSlideFragment();
                Bundle args = new Bundle();
                args.putString(ChannelsFragment.ARGUMENT_NAME_CHANNELTYPEID, arr[1]);
                frg.setArguments(args);
            }else if(Constants.APPPUBS_PROTOCOL_TYPE_CHANNEL.equals(pro.getType())){
                String[] arr = StringUtils.getPathParams(uri);
                ChannelFragment cf = ChannelFragmentFactory.getChannelFragment(Integer.parseInt
                        (arr[1]));
                Bundle args = new Bundle();
                args.putString(ChannelFragment.ARG_KEY, arr[2]);
                frg = cf;
            }else if(Constants.APPPUBS_PROTOCOL_TYPE_NEWSPAPER.equals(pro.getType())){
                frg = new PapersFragment();
            }else if(Constants.APPPUBS_PROTOCOL_TYPE_MESSAGE.equals(pro.getType())){
                frg = new ConversationListFragment();
            }else if(Constants.APPPUBS_PROTOCOL_TYPE_MY_FILE.equals(pro.getType())){
                frg = new MyFileFragment();
            }else if(Constants.APPPUBS_PROTOCOL_TYPE_ADDRESS_BOOK.equals(pro.getType())){
                String rootSuperId = StringUtils.getQueryParameter(uri, "rootsuperid");
                Bundle args = new Bundle();
                args.putString(AdbookFragement.ARGS_ROOT_DEPARTMENT_SUPER_ID, rootSuperId);
                frg = new AdbookFragement();
                frg.setArguments(args);
            }else if(Constants.APPPUBS_PROTOCOL_TYPE_PAGE.equals(pro.getType())){
                frg = new PageFragment();
                Bundle args = new Bundle();
                String[] params = StringUtils.getPathParams(uri);
                args.putString(PageFragment.EXTRA_STRING_NAME_PAGE_ID, params[1]);
                frg.setArguments(args);
            }else if(Constants.APPPUBS_PROTOCOL_TYPE_SERVICENO.equals(pro.getType())){
                frg = new ServiceNOsOfMineFragment();
            }else if(Constants.APPPUBS_PROTOCOL_TYPE_SETTING.equals(pro.getType())){
                frg = new SettingFragment();
            }else {
                frg = new ExceptionFragment();
            }
        } else {
            frg = new ExceptionFragment();
        }
        String title = StringUtils.getQueryParameter(uri, "title");
        frg.setTitle(title);
        return frg;
    }
}
