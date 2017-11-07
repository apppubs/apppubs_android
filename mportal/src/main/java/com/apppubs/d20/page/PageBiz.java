package com.apppubs.d20.page;

import android.content.Context;
import android.support.annotation.Nullable;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.bean.UserInfo;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.model.APResultCallback;
import com.apppubs.d20.net.WMHHttpClient;
import com.apppubs.d20.net.WMHHttpErrorCode;
import com.apppubs.d20.net.WMHRequestListener;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.util.LogM;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by zhangwen on 2017/9/27.
 */

public class PageBiz implements IPageBiz {

	private Context mContext;
	private WMHHttpClient mHttpClient;

	public PageBiz(Context context){
		mContext = context;
		mHttpClient = AppContext.getInstance(mContext).getHttpClient();
	}

	@Override
	public void loadPage(String pageId, final APResultCallback<PageModel> callback) {
		String url = getUrl(pageId);
		mHttpClient.GET(url, null, new WMHRequestListener() {
			@Override
			public void onDone(JSONResult jsonResult, @Nullable WMHHttpErrorCode errorCode) {
				if (errorCode==null){
					if (jsonResult.resultCode==1){
						PageModel model = new PageModel(mContext,jsonResult.result);
						callback.onDone(model);
					}else{
						callback.onException(jsonResult.resultCode);
					}
				}else{
					callback.onException(errorCode.getValue());
				}
			}
		});

		LogM.log(this.getClass(), "请求page json："+url);
	}

	private String getUrl(String pageId) {
		UserInfo ui = AppContext.getInstance(mContext).getCurrentUser();
		String url = null;
		if (ui!=null){
			url = String.format(URLs.URL_PAGE,URLs.baseURL,URLs.appCode, pageId,ui.getUserId());
		}else{
			url = String.format(URLs.URL_PAGE,URLs.baseURL,URLs.appCode, pageId,"");
		}
		return url;
	}
}
