package com.apppubs.ui.message.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.apppubs.bean.TUser;
import com.apppubs.AppContext;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.d20.R;
import com.apppubs.ui.activity.ChatNewGroupChatOrAddUserActivity;
import com.apppubs.ui.activity.ContainerActivity;
import com.apppubs.ui.fragment.ServiceNoSubscribeFragment;
import com.apppubs.model.message.UserPickerHelper;
import com.apppubs.ui.fragment.TitleBarFragment;
import com.apppubs.ui.widget.TitleBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by zhangwen on 2017/1/11.
 */

public class ConversationListFragment extends TitleBarFragment implements View.OnClickListener{

    private PopupWindow mMenuPW;

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mTitleBar == null) {
            return;
        }
        mTitleBar.addRightBtnWithImageResourceIdAndClickListener(R.drawable.plus, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View menuPop = LayoutInflater.from(mHostActivity).inflate(R.layout.pop_msg_record_menu, null);

                mMenuPW = new PopupWindow(menuPop, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                mMenuPW.setFocusable(true);
                mMenuPW.setOutsideTouchable(true);
                mMenuPW.setBackgroundDrawable(new BitmapDrawable());
                mMenuPW.showAsDropDown(mTitleBar.getRightView());
                if (!mAppContext.getApp().isAllowChat()) {
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
                ContainerActivity.startContainerActivity(mContext, ServiceNoSubscribeFragment.class, null, "添加服务号");
//			ContainerActivity.startContainerActivity(mContext, ServiceNoListOfMineFragment.class, null, "我关注的服务号");
                mMenuPW.dismiss();
                break;
            case R.id.pop_msg_record_add_chat_ll:

                Log.e(this.getClass().getName(), "此处需要，增加AddressBookFragement的参数");
                String [] userIds = new String[]{AppContext.getInstance(mContext).getCurrentUser().getUserId()};
                UserPickerHelper.startActivity(mContext, "选择人员", new ArrayList<String>(Arrays.asList(userIds)), new UserPickerHelper.UserPickerListener() {
                    @Override
                    public void onPickDone(List<String> userIds) {
                        if (userIds!=null&&userIds.size()>0){

                            if (userIds.size()>1){
                                String title = getTitleName(userIds);
                                RongIM.getInstance().createDiscussionChat(mContext,userIds,title);
                            }else {
                                TUser user = mUserBussiness.getUserByUserId(userIds.get(0));
                                RongIM.getInstance().startConversation(mContext,Conversation.ConversationType.PRIVATE,userIds.get(0),user.getTrueName());
                            }
                        }
                    }

                    @NonNull
                    private String getTitleName(List<String> userIds) {
                        StringBuilder titleSb = new StringBuilder();
                        for (int i=-1;++i<userIds.size();){
                            if (i>2){
                                titleSb.append("...");
                                break;
                            }
                            TUser user = mUserBussiness.getUserByUserId(userIds.get(i));
                            String trueName = user.getTrueName();
                            if (titleSb.length()>0){
                                titleSb.append("、");
                            }
                            titleSb.append(trueName);
                        }
                        return titleSb.toString();
                    }
                });
                mMenuPW.dismiss();
                break;
            case R.id.pop_msg_record_add_group_chat_ll:
                Intent chatNewGroupIntent = new Intent(mHostActivity,ChatNewGroupChatOrAddUserActivity.class);
                chatNewGroupIntent.putExtra(ChatNewGroupChatOrAddUserActivity.EXTRA_PRESELECTED_USERNAME_LIST, AppContext.getInstance(mContext).getCurrentUser().getUsername());
                startActivity(chatNewGroupIntent);
                mMenuPW.dismiss();
                break;
            default:
                break;
        }
    }

}
