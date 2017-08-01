package com.apppubs.d20.message.fragment;

import android.net.Uri;
import android.util.Log;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.myfile.CacheListener;
import com.apppubs.d20.myfile.FileCacheErrorCode;
import com.apppubs.d20.myfile.FileCacheManager;
import com.apppubs.d20.util.LogM;

import java.io.File;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

/**
 * Created by zhangwen on 2017/2/22.
 */

public class ConversationFragmentEx extends ConversationFragment {

    @Override
    public void onImageResult(List<Uri> selectedImages, boolean origin) {
//        super.onImageResult(selectedImages, origin);
        for (final Uri uri: selectedImages){
           Message message = new Message();
            ImageMessage imageMessage = new ImageMessage();
            imageMessage.setLocalUri(uri);
            message.setContent(imageMessage);
            LogM.log(this.getClass(),uri.toString());

			/**
			 * <p>发送图片消息，可以使用该方法将图片上传到自己的服务器发送，同时更新图片状态。</p>
			 * <p>使用该方法在上传图片时，会回调 {@link io.rong.imlib.RongIMClient.SendImageMessageWithUploadListenerCallback}
			 * 此回调中会携带 {@link RongIMClient.UploadImageStatusListener} 对象，使用者只需要调用其中的
			 * {@link RongIMClient.UploadImageStatusListener#update(int)} 更新进度
			 * {@link RongIMClient.UploadImageStatusListener#success(Uri)} 更新成功状态，并告知上传成功后的图片地址
			 * {@link RongIMClient.UploadImageStatusListener#error()} 更新失败状态 </p>
			 *
			 * @param message     发送消息的实体。
			 * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
			 *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
			 *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
			 * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
			 * @param callback    发送消息的回调，回调中携带 {@link RongIMClient.UploadImageStatusListener} 对象，用户调用该对象中的方法更新状态。
			 *                    {@link #sendImageMessage(Message, String, String, RongIMClient.SendImageMessageCallback)}
			 */
			RongIM.getInstance().sendImageMessage(message, "[图片]", "[图片]", new RongIMClient.SendImageMessageWithUploadListenerCallback() {

				@Override
				public void onAttached(Message message, final RongIMClient.UploadImageStatusListener uploadImageStatusListener) {
         		/*上传图片到自己的服务器*/

					Log.v("ConversationFragmentEx","发送图片");
					AppContext.getInstance(getContext()).getCacheManager().uploadFile(new File(uri.toString()), new CacheListener() {
						@Override
						public void onException(FileCacheErrorCode errorCode) {
							Log.v("ConversationFragmentEx","发送图片异常"+errorCode.getMessage());
						}

						@Override
						public void onDone(String fileUrl) {
							Log.v("ConversationFragmentEx","发送图片完成");
						}

						@Override
						public void onProgress(float progress, long totalBytesExpectedToRead) {

						}
					});
				}


				@Override
				public void onError(Message message, RongIMClient.ErrorCode errorCode) {
					//发送失败
				}


				@Override
				public void onSuccess(Message message) {
					//发送成功
				}


				@Override
				public void onProgress(Message message, int progress) {
					//发送进度
				}
			});

        }

    }

}
