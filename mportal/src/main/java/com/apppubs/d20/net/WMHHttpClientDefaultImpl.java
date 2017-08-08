package com.apppubs.d20.net;

import android.util.Log;

import com.apppubs.d20.util.JSONResult;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhangwen on 2017/7/23.
 */

public class WMHHttpClientDefaultImpl implements WMHHttpClient {

	private OkHttpClient mOkHttpClient;
	public WMHHttpClientDefaultImpl(){
		mOkHttpClient = new OkHttpClient();
	}
	@Override
	public void GET(String pattern, Object[] params, final WMHRequestListener listener) {
		String url  = String.format(pattern,params);
		OkHttpClient okHttpClient = new OkHttpClient();
		final Request request = new Request.Builder()
				.url(url)
				.build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				listener.onDone(null,WMHHttpErrorCode.IO_EXCEPTION);

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String responseStr = response.body().string();
				JSONResult jr = null;
				try{
					 jr = JSONResult.compile(responseStr);
					listener.onDone(jr, null);
				}catch (JsonParseException e){
					Log.v("WMHHttpClient",responseStr);
					e.printStackTrace();
					listener.onDone(null,WMHHttpErrorCode.JSON_PARSE_ERROR);
				}
			}
		});
	}
}
