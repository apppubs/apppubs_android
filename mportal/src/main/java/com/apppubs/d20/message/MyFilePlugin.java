package com.apppubs.d20.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.apppubs.d20.R;
import com.apppubs.d20.message.activity.ConversationActivity;
import com.apppubs.d20.message.model.FilePickerModel;
import com.apppubs.d20.message.model.MyFilePickerHelper;

import java.util.List;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.location.AMapLocationActivity;
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

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}