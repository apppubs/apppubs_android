package com.apppubs.ui.adbook;

/**
 * Created by zhangwen on 2017/10/24.
 */

public interface IUserInfoViewListener {

	public static final int START_CHAT_BTN = 1;
	public static final int MOBILE_PHONE_BTN = 2;
	public static final int TEL_PHONE_BTN = 3;
	public static final int EMAIL_BTN = 4;
	public static final int INVITE_BTN = 5;
	public static final int RE_SEND_INVITE_BTN = 6;
	public static final int ADD_CONTACT_BTN = 7;

	void onResume();

	void onIconClicked();

	void onButtonClicked(int index);
}
