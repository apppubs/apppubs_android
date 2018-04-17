package com.apppubs.ui.adbook;

import android.widget.ImageView;

/**
 * Created by zhangwen on 2017/10/24.
 */

public interface IUserInfoView {

	void showStartChatLabel();

	void showInviteLabel();

	void showSendInviteMsgBtn();

	void disableInviteLabel();

	String getUserId();

	ImageView getIconImageView();

}

