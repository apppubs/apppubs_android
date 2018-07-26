package com.apppubs.ui.message.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.apppubs.constant.APError;
import com.apppubs.d20.R;
import com.apppubs.model.BaseBiz;
import com.apppubs.model.cache.CacheListener;
import com.apppubs.model.cache.FileCacheErrorCode;
import com.apppubs.model.message.FilePickerModel;
import com.apppubs.model.message.MyFilePickerHelper;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.widget.ProgressHUD;
import com.apppubs.util.LogM;

import java.io.File;
import java.util.List;
import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

public class ConversationActivity extends BaseActivity {

	private final int REQUEST_CODE_DISCUSSION = 166;

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
		} else {
			mTitleBar.setRightBtnImageResourceId(R.drawable.chat_group);
		}
		mTitleBar.setRightBtnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				enterSettingActivity();
			}
		});

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LogM.log(this.getClass(), "onNewIntent");

		ProgressHUD.show(this);

		List<FilePickerModel> models = MyFilePickerHelper.getInstance(this).getSelectionModels();
		for (final FilePickerModel model : models) {
			if(TextUtils.isEmpty(model.getFilePath())){
				mMsgBiz.uploadFile(new File(model.getFileUrl()), true, new BaseBiz.IRQStringListener() {
					@Override
					public void onResponse(String result, APError error) {
						if (null == error){
							model.setFilePath(result);
							sendFileMessage(model);
						}
					}
				});
			}else{
				sendFileMessage(model);
			}

		}
	}

	private void sendFileMessage(FilePickerModel model){
		Uri uri = Uri.parse("file://" + model.getFilePath());;
		FileMessage fileMessage = FileMessage.obtain(uri);
		Message message = Message.obtain(mTargetId, mConversationType, fileMessage);
		RongIM.getInstance().sendMediaMessage(message, "", "", new IRongCallback.ISendMediaMessageCallbackWithUploader() {
			@Override
			public void onAttached(Message message, final IRongCallback.MediaMessageUploader mediaMessageUploader) {
				FileMessage me = (FileMessage) message.getContent();
				mMsgBiz.uploadFile(new File(me.getLocalPath().getPath()), true, new BaseBiz.IRQStringListener() {
					@Override
					public void onResponse(String result, APError error) {
						if (null==error){
							mediaMessageUploader.success(Uri.parse(result));
							Log.v("ConversationFragmentEx", "发送图片完成" + result);
						}else{
							Log.v("ConversationFragmentEx", "发送图片异常" + error.getMsg());
							mediaMessageUploader.error();
						}
					}
				});
			}

			@Override
			public void onProgress(Message message, int i) {

			}

			@Override
			public void onSuccess(Message message) {
				Toast.makeText(ConversationActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
				ProgressHUD.dismissProgressHUDInThisContext(ConversationActivity.this);
			}

			@Override
			public void onError(Message message, RongIMClient.ErrorCode errorCode) {
				ProgressHUD.dismissProgressHUDInThisContext(ConversationActivity.this);
				Toast.makeText(ConversationActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCanceled(Message message) {
				ProgressHUD.dismissProgressHUDInThisContext(ConversationActivity.this);
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
				startActivityForResult(intent, REQUEST_CODE_DISCUSSION);
				return;
			}
			intent.putExtra("TargetId", mTargetId);
			if (intent != null) {
				startActivityForResult(intent, 500);
			}

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == REQUEST_CODE_DISCUSSION && resultCode == RESULT_OK) {
			String title = intent.getStringExtra("title");
			setTitle(title);
		}
	}
}
