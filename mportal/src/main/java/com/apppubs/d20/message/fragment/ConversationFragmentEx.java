package com.apppubs.d20.message.fragment;

import android.net.Uri;

import com.apppubs.d20.activity.ContainerActivity;
import com.apppubs.d20.myfile.MyFileFragment;
import com.apppubs.d20.util.LogM;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

/**
 * Created by zhangwen on 2017/2/22.
 */

public class ConversationFragmentEx extends ConversationFragment {

    @Override
    public void onImageResult(List<Uri> selectedImages, boolean origin) {
        super.onImageResult(selectedImages, origin);
        for (Uri uri: selectedImages){

           Message message = new Message();
            ImageMessage imageMessage = new ImageMessage();
            imageMessage.setLocalUri(uri);
            message.setContent(imageMessage);
            RongIM.getInstance().sendImageMessage(message, "[图片]", "[图片]", new RongIMClient.SendImageMessageWithUploadListenerCallback() {

                @Override
                public void onAttached(Message message, final RongIMClient.UploadImageStatusListener uploadImageStatusListener) {
         /*上传图片到自己的服务器*/
//                    uploadImg(imgMsg.getPicFilePath(), new UploadListener() {
//                        @Override
//                        public void onSuccess(String url) {
//                            // 上传成功，回调 SDK 的 success 方法，传递回图片的远端地址
//                            uploadImageStatusListener.success(Uri.parse(url));
//                        }
//
//
//                        @Override
//                        public void onProgress(float progress) {
//                            //刷新上传进度
//                            uploadImageStatusListener.update((int) progress);
//                        }
//
//
//                        @Override
//                        public void onFail() {
//                            // 上传图片失败，回调 error 方法。
//                            uploadImageStatusListener.error();
//                        }
//                    });
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

            LogM.log(this.getClass(),uri.toString());
        }

    }

	@Override
	public void onPluginClicked(IPluginModule pluginModule, int position) {
		if (position==1){
			ContainerActivity.startActivity(getContext(), MyFileFragment.class);
		}else{
//			super.onPluginClicked(pluginModule, position);
		}
	}
}
