package com.apppubs.model.message;

import android.content.Context;

import com.apppubs.bean.http.UserBasicInfosResult;
import com.apppubs.model.UserBiz;

import io.rong.imlib.model.Conversation;

/**
 * Created by zhangwen on 2017/8/8.
 */

public class ConversationModel {

	private String targetId;
	private io.rong.imlib.model.Conversation.ConversationType conversationType;
	private String title;
	private String portraitUrl;

	public ConversationModel(String targetId, Conversation.ConversationType conversationType,String title,String portraitUrl){
		this.targetId = targetId;
		this.conversationType = conversationType;
		this.title = title;
		this.portraitUrl = portraitUrl;
	}

	public static ConversationModel modelFromConversation(Context context, Conversation conversation){
		String title = null;
		String portraitUrl = null;
		if (conversation.getConversationType() == Conversation.ConversationType.PRIVATE) {

			UserBasicInfosResult.Item ubi = UserBiz.getInstance(context).getCachedUserBasicInfo(conversation.getTargetId());
			title = ubi.getTruename();
			portraitUrl = ubi.getAvatarURL();
		} else {
			title = conversation.getConversationTitle();
		}
		return new ConversationModel(conversation.getTargetId(),conversation.getConversationType(),title,portraitUrl);

	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Conversation.ConversationType getConversationType() {
		return conversationType;
	}

	public void setConversationType(Conversation.ConversationType conversationType) {
		this.conversationType = conversationType;
	}

	public String getPortraitUrl() {
		return portraitUrl;
	}

	public void setPortraitUrl(String portraitUrl) {
		this.portraitUrl = portraitUrl;
	}
}
