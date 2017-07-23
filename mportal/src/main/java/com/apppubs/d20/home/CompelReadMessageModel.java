package com.apppubs.d20.home;

import java.io.Serializable;

/**
 * Created by zhangwen on 2017/6/16.
 */

public class CompelReadMessageModel implements Serializable {

	private String mContent;
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
}
