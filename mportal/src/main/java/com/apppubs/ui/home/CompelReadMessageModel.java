package com.apppubs.ui.home;

import com.apppubs.bean.http.CompelReadMessageResult;
import com.apppubs.util.Utils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwen on 2017/6/16.
 */

public class CompelReadMessageModel implements Serializable {

	@SerializedName("content")
	private String mContent;
	@SerializedName("serviceinfo_id")
	private String messageId;

	public String getmContent() {
		return mContent;
	}

	public void setmContent(String mContent) {
		this.mContent = mContent;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public static CompelReadMessageModel createFrom(CompelReadMessageResult.Item item){
		CompelReadMessageModel model = new CompelReadMessageModel();
		model.setmContent(item.getContent());
		model.setMessageId(item.getServiceArticleId());
		return model;
	}

	public static List<CompelReadMessageModel> createFrom(CompelReadMessageResult result){
		if (Utils.isEmpty(result.getItems())){
			return null;
		}
		List<CompelReadMessageModel> list = new ArrayList<>();
		for (CompelReadMessageResult.Item item : result.getItems()) {
			list.add(createFrom(item));
		}
		return list;
	}
}
