package com.apppubs.d20.message.model;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.apppubs.d20.R;
import com.apppubs.d20.adapter.CommonAdapter;
import com.apppubs.d20.adapter.ViewHolder;
import com.apppubs.d20.message.activity.TranspondActivity;
import com.apppubs.d20.model.APResultCallback;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by zhangwen on 2017/8/11.
 */

public class TranspondDataHelper {

	private Context mContext;
	private static TranspondDataHelper sHelper;
	private TranspondDataHelper(Context context){
		mContext = context;
	}

	public static TranspondDataHelper getInstance(Context context){
		if (sHelper==null){
			synchronized (TranspondDataHelper.class){
				if (sHelper==null){
					sHelper = new TranspondDataHelper(context);
				}
			}
		}
		return sHelper;
	}

	public void fetchData( final APResultCallback<List<ConversationModel>> callback){

		RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback() {

			@Override
			public void onSuccess(Object o) {

				final List<Conversation> cons = (List<Conversation>) o;
				List<String> ids = getPrivateTargetIds(cons);

				UserBussiness.getInstance(mContext).cacheUserBasicInfoList(ids, new APResultCallback<List<UserBasicInfo>>() {
					@Override
					public void onDone(List<UserBasicInfo> obj) {
						List<ConversationModel> models = new ArrayList<ConversationModel>();
						for (Conversation conversation:cons){
							ConversationModel model = ConversationModel.modelFromConversation(mContext,conversation);
							models.add(model);
						}
						callback.onDone(models);
					}

					@Override
					public void onException(int excepCode) {

					}
				});

			}

			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {

			}
		});
	}

	private List<String> getPrivateTargetIds(List<Conversation> list) {
		List<String> ids = new ArrayList<String>();
		for (int i=-1;++i<list.size();){
			Conversation conversation = list.get(i);
			if (conversation.getConversationType() == Conversation.ConversationType.PRIVATE) {
				ids.add(conversation.getTargetId());
			}
		}
		return ids;
	}

}
