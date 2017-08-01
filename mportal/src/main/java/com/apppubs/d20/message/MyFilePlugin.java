package com.apppubs.d20.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.apppubs.d20.R;
import com.apppubs.d20.activity.ContainerActivity;
import com.apppubs.d20.myfile.MyFileFragment;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

public class MyFilePlugin implements IPluginModule {
	Conversation.ConversationType conversationType;
    String targetId;
    @Override
    public Drawable obtainDrawable(Context context) {
        //设置插件 Plugin 图标
        return ContextCompat.getDrawable(context, R.drawable.rc_ic_files_normal);
    }
    @Override
    public String obtainTitle(Context context) {
        //设置插件 Plugin 展示文字
        return "文件";
    }
    @Override
    public void onClick(final Fragment currentFragment, RongExtension extension) {
        //示例获取 会话类型、targetId、Context,此处可根据产品需求自定义逻辑，如:开启新的 Activity 等。
        conversationType = extension.getConversationType();
        targetId = extension.getTargetId();
//        Message message = Message.obtain(targetId, conversationType, TextMessage.obtain("示例插件功能"));
//        RongIM.getInstance().sendMessage(message, null, null, new IRongCallback.ISendMessageCallback() {
//            @Override
//            public void onAttached(Message message) {
//            }
//            @Override
//            public void onSuccess(Message message) {
//                Toast.makeText(currentFragment.getActivity(), "消息发送成功, 示例获取 Context", Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//            }
//        });

		ContainerActivity.startActivity(currentFragment.getContext(), MyFileFragment.class);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}