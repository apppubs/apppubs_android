package com.apppubs.d20.net;

import com.apppubs.d20.util.JSONResult;

public interface RequestListener {

	void onResponse(JSONResult jsonresult, int requestCode);
	void onException(int resultCode,int requestCode);
}
