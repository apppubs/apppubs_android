package com.apppubs.d20.net;

import android.util.Log;

import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.util.StringUtils;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhangwen on 2017/7/23.
 */

public class WMHHttpClientDefaultImpl implements WMHHttpClient {

    private OkHttpClient mOkHttpClient;

    public WMHHttpClientDefaultImpl() {
        mOkHttpClient = new OkHttpClient();
    }

    @Override
    public void GET(String pattern, Object[] params, final WMHRequestListener listener) {
        String url = String.format(pattern, params);
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onDone(null, WMHHttpErrorCode.IO_EXCEPTION);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                JSONResult jr = null;
                try {
                    jr = JSONResult.compile(responseStr);
                    listener.onDone(jr, null);
                } catch (JsonParseException e) {
                    Log.v("WMHHttpClient", responseStr);
                    e.printStackTrace();
                    listener.onDone(null, WMHHttpErrorCode.JSON_PARSE_ERROR);
                }
            }
        });
    }

    @Override
    public void POST(String url, String json, final WMHRequestListener listener) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
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
