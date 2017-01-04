package com.mportal.client.net;

import com.mportal.client.util.JSONResult;

public interface RequestListener {

	void onResponse(JSONResult jsonresult,int requestCode);
	void onException(int resultCode,int requestCode);
}
