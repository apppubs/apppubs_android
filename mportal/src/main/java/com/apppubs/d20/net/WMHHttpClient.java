package com.apppubs.d20.net;

/**
 * Created by zhangwen on 2017/7/23.
 */

public interface WMHHttpClient {

	void GET(String url, Object[] params, WMHRequestListener listener);

	void POST(String url, String json, WMHRequestListener listener);
}
