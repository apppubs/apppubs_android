package com.apppubs.ui.activity;

import com.apppubs.util.JSONResult;

public interface RequestListener {

	void onResponse(JSONResult jsonresult, int requestCode);
	void onException(int resultCode,int requestCode);
}
