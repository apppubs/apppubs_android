package com.apppubs.ui.message.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.bean.TUser;
import com.apppubs.constant.APError;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.AppContext;
import com.apppubs.d20.R;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.model.message.ConversationModel;
import com.apppubs.model.message.TranspondDataHelper;
import com.apppubs.model.message.UserBussiness;
import com.apppubs.model.message.UserPickerHelper;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.myfile.CacheListener;
import com.apppubs.model.myfile.FileCacheErrorCode;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.ui.widget.ProgressHUD;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

/**
 * Created by zhangwen on 2017/8/8.
 */

public class TranspondActivity extends BaseActivity {
	public static final String EXTRA_NAME_FILE_LOCATION = "file_name";

	private String mFilePath;
	private ListView mListView;
	private CommonAdapter<ConversationModel> mAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
		initData();
		initViews();
		loadData();
	}

	private void initData() {
		mFilePath = getIntent().getStringExtra(EXTRA_NAME_FILE_LOCATION);
	}

	private void loadData() {

		TranspondDataHelper.getInstance(this).fetchData(new IAPCallback<List<ConversationModel>>() {
			@Override
			public void onDone(List<ConversationModel> modelList) {

				mAdapter = new CommonAdapter<ConversationModel>(TranspondActivity.this, modelList, R.layout.item_transpond) {
					@Override
					protected void fillValues(final ViewHolder holder, ConversationModel bean, int position) {
						ImageView iv = holder.getView(R.id.transpond_image_iv);
						TextView tv = holder.getView(R.id.transpond_title_tv);
						tv.setText(bean.getTitle());

						if (bean.getConversationType() == Conversation.ConversationType.PRIVATE) {
							mImageLoader.displayImage(bean.getPortraitUrl(), iv);
						} else {
							iv.setImageResource(R.drawable.default_discussion_portrait);
						}
					}
				};
				mListView.setAdapter(mAdapter);
			}

			@Override
			public void onException(APError excepCode) {

			}
		});
	}

	private void initViews() {
		setContentView(R.layout.act_transpond);
		setTitle("转发给");
		mListView = (ListView) findViewById(R.id.act_transpond_lv);
		View header = LayoutInflater.from(this).inflate(R.layout.header_transpond_message_lv, mListView, false);
		mListView.addHeaderView(header);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final ConversationModel c = mAdapter.getItem((int) parent.getItemIdAtPosition(position));
				showSendAlert(c);
			}
		});
		View selectUserLl = findViewById(R.id.header_transpond_conversation_ll);
		selectUserLl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] userIds = new String[]{AppContext.getInstance(mContext).getCurrentUser().getUserId()};
				UserPickerHelper.startActivity(mContext, "选择人员", new ArrayList<String>(Arrays.asList(userIds)), UserPickerHelper.UserPickerMode.USER_PICKER_MODE_SINGLE, new UserPickerHelper.UserPickerListener() {
					@Override
					public void onPickDone(List<String> userIds) {
						if (userIds != null && userIds.size() > 0) {
							TUser user = UserBussiness.getInstance(mContext).getUserByUserId(userIds.get(0));
							ConversationModel model = new ConversationModel(userIds.get(0), Conversation.ConversationType.PRIVATE, user.getTrueName(), "");
							showSendAlert(model);
						}
					}
				});

			}
		});
	}
	private void showSendAlert(final ConversationModel c) {

		new ConfirmDialog(TranspondActivity.this, new ConfirmDialog.ConfirmListener() {
			@Override
			public void onOkClick() {
				sendMessage(c);
			}

			@Override
			public void onCancelClick() {

			}
		},"是否发送到："+c.getTitle(),"取消","确定").show();
	}

	private void sendMessage(ConversationModel conversation){
		ProgressHUD.show(this);
		FileMessage fileMessage = FileMessage.obtain(Uri.parse("file://"+mFilePath));
		Message message = Message.obtain(conversation.getTargetId(),conversation.getConversationType(),fileMessage);
		RongIM.getInstance().sendMediaMessage(message, "", "", new IRongCallback.ISendMediaMessageCallbackWithUploader() {
			@Override
			public void onAttached(Message message, final IRongCallback.MediaMessageUploader mediaMessageUploader) {
				AppContext.getInstance(TranspondActivity.this).getCacheManager().uploadFile(new File(mFilePath), new CacheListener() {
					@Override
					public void onException(FileCacheErrorCode errorCode) {
						Log.v("ConversationFragmentEx","发送图片异常"+errorCode.getMessage());
						mediaMessageUploader.error();
					}

					@Override
					public void onDone(String fileUrl) {
						mediaMessageUploader.success(Uri.parse(fileUrl));
						Log.v("ConversationFragmentEx","发送图片完成"+fileUrl);
					}

					@Override
					public void onProgress(float progress, long totalBytesExpectedToRead) {
						mediaMessageUploader.update((int)(totalBytesExpectedToRead*progress));
					}
				});
			}

			@Override
			public void onProgress(Message message, int i) {

			}

			@Override
			public void onSuccess(Message message) {
				Toast.makeText(TranspondActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
				finish();
			}

			@Override
			public void onError(Message message, RongIMClient.ErrorCode errorCode) {
				ProgressHUD.dismissProgressHUDInThisContext(TranspondActivity.this);
				Toast.makeText(TranspondActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCanceled(Message message) {

			}
		});
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_to_bottom);
	}
}