package com.apppubs.d20.start;

/**
 * Created by zhangwen on 2017/9/5.
 */

public interface IStartUpView {

	void showWelcomeFragment();
	void hideWelcomeFragment();
	void showBgImage(String url);
	void showSkipBtn(long millis);
	void skip2Home();
	void showUpdateDialog(String title,String message,String updateUrl,boolean needForceUpdate);
	void showInitFailDialog();
	void showVersion(String version);
}
