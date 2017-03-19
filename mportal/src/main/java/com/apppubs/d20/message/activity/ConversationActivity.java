package com.apppubs.d20.message.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.apppubs.d20.R;
import com.apppubs.d20.activity.BaseActivity;

import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imlib.model.Conversation;

public class ConversationActivity extends BaseActivity {

    private String TAG = ConversationActivity.class.getSimpleName();
    /**
     * 对方id
     */
    private String mTargetId;
    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;
    /**
     * title
     */
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_conversation);

        Intent intent = getIntent();

        if (intent == null || intent.getData() == null)
            return;

        mTargetId = intent.getData().getQueryParameter("targetId");
        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.getDefault()));

        title = intent.getData().getQueryParameter("title");
        setTitle(title);
        mTitleBar.setRightText("详情");
        if (mConversationType == Conversation.ConversationType.PRIVATE) {
            mTitleBar.setRightBtnImageResourceId(R.drawable.chat_private);
        }else{
            mTitleBar.setRightBtnImageResourceId(R.drawable.chat_group);
        }
        mTitleBar.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterSettingActivity();
            }
        });

    }


    /**
     * 根据 targetid 和 ConversationType 进入到设置页面
     */
    private void enterSettingActivity() {

        if (mConversationType == Conversation.ConversationType.PUBLIC_SERVICE
                || mConversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE) {

            RongIM.getInstance().startPublicServiceProfile(this, mConversationType, mTargetId);
        } else {
            UriFragment fragment = (UriFragment) getSupportFragmentManager().getFragments().get(0);
            //得到讨论组的 targetId
            mTargetId = fragment.getUri().getQueryParameter("targetId");

            if (TextUtils.isEmpty(mTargetId)) {
            }


            Intent intent = null;
            if (mConversationType == Conversation.ConversationType.GROUP) {
//                intent = new Intent(this, GroupDetailActivity.class);
//                intent.putExtra("conversationType", Conversation.ConversationType.GROUP);
            } else if (mConversationType == Conversation.ConversationType.PRIVATE) {
                intent = new Intent(this, PrivateChatDetailActivity.class);
                intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
            } else if (mConversationType == Conversation.ConversationType.DISCUSSION) {
                intent = new Intent(this, DiscussionDetailActivity.class);
                intent.putExtra("TargetId", mTargetId);
                startActivityForResult(intent, 166);
                return;
            }
            intent.putExtra("TargetId", mTargetId);
            if (intent != null) {
                startActivityForResult(intent, 500);
            }

        }
    }

}
