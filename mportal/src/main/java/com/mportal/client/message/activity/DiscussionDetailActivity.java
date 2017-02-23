package com.mportal.client.message.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mportal.client.R;
import com.mportal.client.activity.BaseActivity;
import com.mportal.client.AppContext;
import com.mportal.client.business.BussinessCallbackCommon;
import com.mportal.client.message.model.OperationRong;
import com.mportal.client.message.model.UserBasicInfo;
import com.mportal.client.message.model.UserPickerHelper;
import com.mportal.client.util.Utils;
import com.mportal.client.widget.CircleTextImageView;
import com.mportal.client.widget.DemoGridView;
import com.mportal.client.widget.DialogWithYesOrNoUtils;
import com.mportal.client.widget.LoadDialog;
import com.mportal.client.widget.NToast;
import com.mportal.client.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.utilities.PromptPopupDialog;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.UserInfo;


/**
 * Created by AMing on 16/5/5.
 * Company RongCloud
 */
public class DiscussionDetailActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final int FIND_USER_INFO = 10;
    private String targetId;
    private String createId;
    private Discussion mDiscussion;
    private TextView memberSize;
    private List<UserInfo> memberList = new ArrayList<>();
    private DemoGridView mGridView;
    private GridAdapter adapter;
    private boolean isCreated;
    private SwitchButton discussionTop, discussionNof;
    private List<String> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_discussion);
        setTitle("讨论组详情");
        targetId = getIntent().getStringExtra("TargetId");
        if (TextUtils.isEmpty(targetId)) {
            return;
        }
        LoadDialog.show(this);
        initView();
        RongIM.getInstance().getDiscussion(targetId, new RongIMClient.ResultCallback<Discussion>() {
            @Override
            public void onSuccess(Discussion discussion) {
                mDiscussion = discussion;
                if (mDiscussion != null) {
                    initData(mDiscussion);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        });

    }


    private void initView() {
        memberSize = (TextView) findViewById(R.id.discu_member_size);
        mGridView = (DemoGridView) findViewById(R.id.discu_gridview);
        discussionTop = (SwitchButton) findViewById(R.id.sw_discu_top);
        discussionNof = (SwitchButton) findViewById(R.id.sw_discu_notfaction);
        LinearLayout discussionClean = (LinearLayout) findViewById(R.id.discu_clean);
        Button deleteDiscussion = (Button) findViewById(R.id.discu_quit);
        discussionTop.setOnCheckedChangeListener(this);
        discussionNof.setOnCheckedChangeListener(this);
        discussionClean.setOnClickListener(this);
        deleteDiscussion.setOnClickListener(this);
        RongIM.getInstance().getConversation(Conversation.ConversationType.DISCUSSION, targetId, new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                if (conversation == null) {
                    return;
                }
                if (conversation.isTop()) {
                    discussionTop.setChecked(true);
                } else {
                    discussionTop.setChecked(false);
                }

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });

        RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.DISCUSSION, targetId, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {

                if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                    discussionNof.setChecked(true);
                } else {
                    discussionNof.setChecked(false);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }


    private void initData(Discussion mDiscussion) {
        memberSize.setText("讨论组成员(" + mDiscussion.getMemberIdList().size() + ")");
        createId = mDiscussion.getCreatorId();
        ids = mDiscussion.getMemberIdList();
        if (ids != null) {
//            request(FIND_USER_INFO);
            mUserBussiness.cacheUserBasicInfoList(ids, new BussinessCallbackCommon<List<UserBasicInfo>>() {
                @Override
                public void onDone(List<UserBasicInfo> infos) {

                    memberList.clear();
                    for (UserBasicInfo userBasicInfo : infos) {
                        memberList.add(new UserInfo(userBasicInfo.getUserId(), userBasicInfo.getTrueName(), Uri.parse(userBasicInfo.getAtatarUrl())));
                    }
                    String currentUserId = AppContext.getInstance(mContext).getCurrentUser().getUserId();
                    if (currentUserId.equals(createId)) {
                        isCreated = true;
                    }
                    if (memberList != null && memberList.size() > 1) {
                        if (adapter == null) {
                            adapter = new GridAdapter(mContext, memberList);
                            mGridView.setAdapter(adapter);
                        } else {
                            adapter.updateListView(memberList);
                        }
                    }
                    LoadDialog.dismiss(mContext);
                }

                @Override
                public void onException(int excepCode) {

                }
            });
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_discu_top:
                if (isChecked) {
                    OperationRong.setConversationTop(mContext, Conversation.ConversationType.DISCUSSION, targetId, true);
                } else {
                    OperationRong.setConversationTop(mContext, Conversation.ConversationType.DISCUSSION, targetId, false);
                }
                break;
            case R.id.sw_discu_notfaction:
                if (isChecked) {
                    OperationRong.setConverstionNotif(mContext, Conversation.ConversationType.DISCUSSION, targetId, true);
                } else {
                    OperationRong.setConverstionNotif(mContext, Conversation.ConversationType.DISCUSSION, targetId, false);
                }
                break;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.discu_clean:
                PromptPopupDialog.newInstance(mContext,
                        "确定删除讨论组聊天记录吗").setLayoutRes(io.rong.imkit.R.layout.rc_dialog_popup_prompt_warning)
                .setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        if (RongIM.getInstance() != null) {
                            RongIM.getInstance().clearMessages(Conversation.ConversationType.DISCUSSION, targetId, new RongIMClient.ResultCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    NToast.shortToast(mContext, "清除成功");
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    NToast.shortToast(mContext, "清除失败");
                                }
                            });
                        }
                    }
                }).show();
                break;
            case R.id.discu_quit:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "是否退出并删除当前讨论组?", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void executeEvent() {
                        RongIM.getInstance().quitDiscussion(targetId, new RongIMClient.OperationCallback() {
                            @Override
                            public void onSuccess() {
                                RongIM.getInstance().removeConversation(Conversation.ConversationType.DISCUSSION, targetId);
                                Intent i = new Intent();
                                i.putExtra("disFinish", "disFinish");
                                setResult(112, i);
                                finish();
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
                    }

                    @Override
                    public void executeEditEvent(String editText) {

                    }

                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {

                    }
                });

                break;
        }
    }


    private class GridAdapter extends BaseAdapter {

        private List<UserInfo> list;
        Context context;


        public GridAdapter(Context context, List<UserInfo> list) {
            this.list = list;
            this.context = context;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.social_chatsetting_gridview_item, parent, false);
            }
            CircleTextImageView iv_avatar = (CircleTextImageView) convertView.findViewById(R.id.iv_avatar);
            TextView tv_username = (TextView) convertView.findViewById(R.id.tv_username);
            ImageView badge_delete = (ImageView) convertView.findViewById(R.id.badge_delete);
            iv_avatar.setFillColor(Color.TRANSPARENT);
            // 最后一个item，减人按钮
            if (position == getCount() - 1 && isCreated) {
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.drawable.delete_members);

                iv_avatar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(DiscussionDetailActivity.this, SelectFriendsActivity.class);
//                        intent.putExtra("DeleteDiscuMember", (Serializable) memberList);
//                        intent.putExtra("DeleteDiscuId", targetId);
//                        startActivityForResult(intent, SealConst.DISCUSSION_REMOVE_MEMBER_REQUEST_CODE);
                    }

                });
            } else if ((isCreated && position == getCount() - 2) || (!isCreated && position == getCount() - 1)) {
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.drawable.add_members);

                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        List<String> preSelectedUserIds = new ArrayList<String>();
                        for (UserInfo userinfo:memberList){
                            preSelectedUserIds.add(userinfo.getUserId());
                        }
                        UserPickerHelper.startActivity(mContext, "添加成员", preSelectedUserIds, new UserPickerHelper.UserPickerListener() {
                            @Override
                            public void onPickDone(final List<String> userIds) {
                                onAddMemberPickerDone(userIds);
                            }
                        });
                    }
                });
            } else { // 普通成员
                UserInfo bean = list.get(position);
                if (!TextUtils.isEmpty(bean.getName())) {
                    tv_username.setText(bean.getName());
                }
                iv_avatar.setText(bean.getName());
                iv_avatar.setFillColor(getResources().getColor(R.color.common_btn_bg_gray));
                iv_avatar.setTextColor(Color.WHITE);
                iv_avatar.setTextSize(Utils.dip2px(mContext,12));
                mImageLoader.displayImage(bean.getPortraitUri().toString(),iv_avatar);
                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }

                });

            }

            return convertView;
        }

        private void onAddMemberPickerDone(final List<String> userIds) {

            LoadDialog.show(mContext);
            final List<String> tempUserIds = new ArrayList<String>();
            tempUserIds.addAll(userIds);
            RongIMClient.getInstance().addMemberToDiscussion(targetId, userIds, new RongIMClient.OperationCallback() {
                @Override
                public void onSuccess() {
                    onAddMemberFinish(tempUserIds);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LoadDialog.dismiss(mContext);
                }
            });
        }

        private void onAddMemberFinish(List<String> userIds) {
            mUserBussiness.cacheUserBasicInfoList(userIds, new BussinessCallbackCommon<List<UserBasicInfo>>() {
                @Override
                public void onDone(List<UserBasicInfo> basicInfos) {
                    if (basicInfos != null && basicInfos.size() > 0) {
                        for (UserBasicInfo basicInfo : basicInfos) {
                            memberList.add(new UserInfo(basicInfo.getUserId(), basicInfo.getTrueName(), Uri.parse(basicInfo.getAtatarUrl())));
                        }
                        adapter.updateListView(memberList);
                        memberSize.setText("讨论组成员(" + memberList.size() + ")");
                        LoadDialog.dismiss(mContext);
                    }
                }

                @Override
                public void onException(int excepCode) {
                    LoadDialog.dismiss(mContext);
                }
            });
        }

        @Override
        public int getCount() {
            if (isCreated) {
                return list.size() + 2;
            } else {
                return list.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 传入新的数据 刷新UI的方法
         */
        public void updateListView(List<UserInfo> list) {
            this.list = list;
            notifyDataSetChanged();
        }

    }


    // 拿到新增的成员刷新adapter
    @Override
    @SuppressWarnings("unchecked")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
//                case SealConst.DISCUSSION_ADD_MEMBER_REQUEST_CODE:
//                    final List<String> addMember = (List<String>) data.getSerializableExtra("addDiscuMember");
//                    RongIMClient.getInstance().addMemberToDiscussion(targetId, addMember, new RongIMClient.OperationCallback() {
//                        @Override
//                        public void onSuccess() {
//                            SealUserInfoManager.getInstance().getFriends(new SealUserInfoManager.ResultCallback<List<Friend>>() {
//                                @Override
//                                public void onSuccess(List<Friend> friendList) {
//                                    if (friendList != null && friendList.size() > 0) {
//                                        for (Friend friend : friendList) {
//                                            for (String userId : addMember) {
//                                                if (userId.equals(friend.getUserId()))
//                                                    memberList.add(new UserInfo(userId, friend.getName(), Uri.parse(friend.getPortraitUri())));
//                                            }
//                                        }
//                                        adapter.updateListView(memberList);
//                                    }
//                                }
//
//                                @Override
//                                public void onError(String errString) {
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onError(RongIMClient.ErrorCode errorCode) {
//
//                        }
//                    });
//                    break;
//                case SealConst.DISCUSSION_REMOVE_MEMBER_REQUEST_CODE:
//                    List<String> deleteMember = (List<String>) data.getSerializableExtra("deleteDiscuMember");
//                    List<UserInfo> filtered = new ArrayList<>();
//                    for (String id : deleteMember) {
//                        int count = memberList.size();
//                        for (int i = 0; i < count; i++) {
//                            if (memberList.get(i).getUserId().equals(id))
//                                filtered.add(memberList.get(i));
//                        }
//                    }
//                    for (UserInfo userInfo : filtered) {
//                        RongIMClient.getInstance().removeMemberFromDiscussion(targetId, userInfo.getUserId(), null);
//                        memberList.remove(userInfo);
//                    }
//                    adapter.updateListView(memberList);
//                    break;
            }
        }
    }

//    @Override
//    public Object doInBackground(int requestCode, String id) throws HttpException {
//        switch (requestCode) {
//            case FIND_USER_INFO:
//                return action.getUserInfos(ids);
//        }
//        return super.doInBackground(requestCode, id);
//    }

//    @Override
//    public void onSuccess(int requestCode, Object result) {
//        switch (requestCode) {
//            case FIND_USER_INFO:
//                GetUserInfosResponse response = (GetUserInfosResponse) result;
//                if (response.getCode() == 200) {
//                    List<GetUserInfosResponse.ResultEntity> infos = response.getResult();
//                    memberList.clear();
//                    for (GetUserInfosResponse.ResultEntity g : infos) {
//                        memberList.add(new UserInfo(g.getId(), g.getNickname(), Uri.parse(g.getPortraitUri())));
//                    }
//                    String loginId = getSharedPreferences("config", MODE_PRIVATE).getString(SealConst.SEALTALK_LOGIN_ID, "");
//                    if (loginId.equals(createId)) {
//                        isCreated = true;
//                    }
//                    if (memberList != null && memberList.size() > 1) {
//                        if (adapter == null) {
//                            adapter = new GridAdapter(mContext, memberList);
//                            mGridView.setAdapter(adapter);
//                        } else {
//                            adapter.updateListView(memberList);
//                        }
//                    }
//                    LoadDialog.dismiss(mContext);
//                }
//                break;
//        }
//    }

//    @Override
//    public void onFailure(int requestCode, int state, Object result) {
//        LoadDialog.dismiss(mContext);
//    }

}