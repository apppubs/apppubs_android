package com.mportal.client.message.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.activity.ChatNewGroupChatOrAddUserActivity;
import com.mportal.client.activity.ContainerActivity;
import com.mportal.client.bean.App;
import com.mportal.client.fragment.BaseFragment;
import com.mportal.client.fragment.ServiceNoSubscribeFragment;
import com.mportal.client.message.activity.UserPickerActivity;
import com.mportal.client.message.model.UserPickerHelper;
import com.mportal.client.widget.TitleBar;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by zhangwen on 2017/1/11.
 */

public class ConversationListFragment extends BaseFragment implements View.OnClickListener{

    private PopupWindow mMenuPW;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.frg_conversation_list, null);

        io.rong.imkit.fragment.ConversationListFragment fragment = new io.rong.imkit.fragment.ConversationListFragment();
        Uri uri = Uri.parse("rong://"+ mHostActivity.getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(),"false")
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(),"false")
                .build();
        fragment.setUri(uri);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
        if (fragments == null || !fragments.contains(fragment)) {
            transaction.remove(fragment);
            transaction.add(R.id.frag_container, fragment);
        }
        transaction.show(fragment);
        transaction.commit();
        return mRootView;
    }

    @Override
    public void changeActivityTitleView(TitleBar titleBar) {

        super.changeActivityTitleView(titleBar);
        if (titleBar == null) {
            return;
        }
        titleBar.addRightBtnWithImageResourceIdAndClickListener(R.drawable.plus, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View menuPop = LayoutInflater.from(mHostActivity).inflate(R.layout.pop_msg_record_menu, null);

                mMenuPW = new PopupWindow(menuPop, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                mMenuPW.setFocusable(true);
                mMenuPW.setOutsideTouchable(true);
                mMenuPW.setBackgroundDrawable(new BitmapDrawable());
                mMenuPW.showAsDropDown(mTitleBar.getRightView());
                if (MportalApplication.app.getAllowChat() == App.ALLOW_CHAT_FALSE) {
                    // 当没有聊天功能时隐藏新建聊天
                    setVisibilityOfViewByResId(menuPop, R.id.pop_msg_record_add_chat_ll, View.GONE);
                    setVisibilityOfViewByResId(menuPop, R.id.pop_msg_record_add_group_chat_ll, View.GONE);
                }

                View addChatV = menuPop.findViewById(R.id.pop_msg_record_add_chat_ll);
                View addServiceV = menuPop.findViewById(R.id.pop_msg_record_add_service_ll);
                View addGrougChatV = menuPop.findViewById(R.id.pop_msg_record_add_group_chat_ll);
                addChatV.setOnClickListener(ConversationListFragment.this);
                addServiceV.setOnClickListener(ConversationListFragment.this);
                addGrougChatV.setOnClickListener(ConversationListFragment.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_msg_record_add_service_ll:
                ContainerActivity.startActivity(mContext, ServiceNoSubscribeFragment.class, null, "添加服务号");
//			ContainerActivity.startActivity(mContext, ServiceNoListOfMineFragment.class, null, "我关注的服务号");
                mMenuPW.dismiss();
                break;
            case R.id.pop_msg_record_add_chat_ll:

                Log.e(this.getClass().getName(), "此处需要，增加AddressBookFragement的参数");
                String [] userIds = new String[]{MportalApplication.user.getUserId()};
                UserPickerHelper.startActivity(mContext, "选择人员", new ArrayList<String>(Arrays.asList(userIds)), new UserPickerHelper.UserPickerListener() {
                    @Override
                    public void onPickDone(List<String> userIds) {
                        if (userIds!=null&&userIds.size()>0){
                            if (userIds.size()>1){
                                RongIM.getInstance().createDiscussion("默认标题",userIds,null);
                            }else {
                                RongIM.getInstance().startConversation(mContext,Conversation.ConversationType.PRIVATE,userIds.get(0),"人名");
                            }
                        }
                    }
                });
                mMenuPW.dismiss();
                break;
            case R.id.pop_msg_record_add_group_chat_ll:
                Intent chatNewGroupIntent = new Intent(mHostActivity,ChatNewGroupChatOrAddUserActivity.class);
                chatNewGroupIntent.putExtra(ChatNewGroupChatOrAddUserActivity.EXTRA_PRESELECTED_USERNAME_LIST, MportalApplication.user.getUsername());
                startActivity(chatNewGroupIntent);
                mMenuPW.dismiss();
                break;
            default:
                break;
        }
    }

}
