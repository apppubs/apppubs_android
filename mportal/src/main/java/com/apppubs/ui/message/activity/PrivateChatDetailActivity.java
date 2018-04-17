package com.apppubs.ui.message.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.d20.R;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.adbook.UserInfoActivity;
import com.apppubs.model.message.OperationRong;
import com.apppubs.util.Utils;
import com.apppubs.ui.widget.CircleTextImageView;
import com.apppubs.ui.widget.SwitchButton;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.PromptPopupDialog;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.SearchConversationResult;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/3/9.
 * Company RongCloud
 */
public class PrivateChatDetailActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final int SEARCH_TYPE_FLAG = 1;

    private UserInfo mUserInfo;
    private SwitchButton messageTop, messageNotification;
    private CircleTextImageView mImageView;
    private TextView friendName;
//    private LinearLayout mSearchChattingRecordsLinearLayout;

    private Conversation.ConversationType mConversationType;
    private String fromConversationId;
    private SearchConversationResult mResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fr_friend_detail);
        setTitle("用户详情");
        initView();
        fromConversationId = getIntent().getStringExtra("TargetId");
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("conversationType");

        if (!TextUtils.isEmpty(fromConversationId)) {
            mUserInfo = RongUserInfoManager.getInstance().getUserInfo(fromConversationId);
            updateUI();
        }
        EventBus.getDefault().register(this);
    }

    private void updateUI() {
        if (mUserInfo != null) {
            initData();
            getState(mUserInfo.getUserId());
        }
    }

    private void initData() {
        if (mUserInfo != null) {
            mImageView.setText(mUserInfo.getName());
            mImageView.setTextColor(Color.WHITE);
            mImageView.setFillColor(getResources().getColor(R.color.common_btn_bg_gray));
            mImageView.setTextSize(Utils.dip2px(mContext,12));
            mImageLoader.displayImage(mUserInfo.getPortraitUri().toString(),mImageView);
            friendName.setText(mUserInfo.getName());
        }

    }

    private void initView() {
        LinearLayout cleanMessage = (LinearLayout) findViewById(R.id.clean_friend);
        mImageView = (CircleTextImageView) findViewById(R.id.friend_header);
        mImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivateChatDetailActivity.this, UserInfoActivity.class);
				intent.putExtra(UserInfoActivity.EXTRA_STRING_USER_ID, mUserInfo.getUserId());
                PrivateChatDetailActivity.this.startActivity(intent);
            }
        });
        messageTop = (SwitchButton) findViewById(R.id.sw_freind_top);
        messageNotification = (SwitchButton) findViewById(R.id.sw_friend_notfaction);
        friendName = (TextView) findViewById(R.id.friend_name);
        cleanMessage.setOnClickListener(this);
        messageNotification.setOnCheckedChangeListener(this);
        messageTop.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.clean_friend:
                PromptPopupDialog.newInstance(this,
                                              "确定删除聊天记录吗").setLayoutRes(io.rong.imkit.R.layout.rc_dialog_popup_prompt_warning)
                .setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        if (RongIM.getInstance() != null) {
                            if (mUserInfo != null) {
                                RongIM.getInstance().clearMessages(Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        Toast.makeText(PrivateChatDetailActivity.this,"清除成功",Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        Toast.makeText(PrivateChatDetailActivity.this,"清除失败",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                }).show();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_friend_notfaction:
                if (isChecked) {
                    if (mUserInfo != null) {
                        OperationRong.setConverstionNotif(PrivateChatDetailActivity.this, Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), true);
                    }
                } else {
                    if (mUserInfo != null) {
                        OperationRong.setConverstionNotif(PrivateChatDetailActivity.this, Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), false);
                    }
                }
                break;
            case R.id.sw_freind_top:
                if (isChecked) {
                    if (mUserInfo != null) {
                        OperationRong.setConversationTop(PrivateChatDetailActivity.this, Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), true);
                    }
                } else {
                    if (mUserInfo != null) {
                        OperationRong.setConversationTop(PrivateChatDetailActivity.this, Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), false);
                    }
                }
                break;
        }
    }

    private void getState(String targetId) {
        if (targetId != null) {//群组列表 page 进入
            if (RongIM.getInstance() != null) {
                RongIM.getInstance().getConversation(Conversation.ConversationType.PRIVATE, targetId, new RongIMClient.ResultCallback<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {
                        if (conversation == null) {
                            return;
                        }

                        if (conversation.isTop()) {
                            messageTop.setChecked(true);
                        } else {
                            messageTop.setChecked(false);
                        }

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });

                RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.PRIVATE, targetId, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                    @Override
                    public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {

                        if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                            messageNotification.setChecked(true);
                        } else {
                            messageNotification.setChecked(false);
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }
        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        if (userInfo != null && userInfo.getUserId().equals(fromConversationId)) {
            mUserInfo = userInfo;
            updateUI();
        }
    }
}
