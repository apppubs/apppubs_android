package com.apppubs.d20.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.R;
import com.apppubs.d20.message.activity.ConversationActivity;
import com.apppubs.d20.message.model.FilePickerModel;
import com.apppubs.d20.myfile.CacheListener;
import com.apppubs.d20.myfile.FileCacheErrorCode;
import com.apppubs.d20.message.model.MyFilePickerHelper;
import com.apppubs.d20.widget.ProgressHUD;

import java.io.File;
import java.util.List;

import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

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

		MyFilePickerHelper helper = MyFilePickerHelper.getInstance(currentFragment.getContext());
		helper.clear();
		helper.startSelect(new MyFilePickerHelper.FilePickerListener(){

			@Override
			public void onSelectDone(List<FilePickerModel> model) {
				Intent intent = new Intent(currentFragment.getContext(), ConversationActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				currentFragment.startActivity(intent);

			}
		});
//		Bundle extra = new Bundle();
//		extra.putString(ContainerActivity.EXTRA_STRING_TITLE,"发送文件");
//
//		ContainerActivity.startActivity(currentFragment.getContext(), MyFileFragment.class,extra);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}