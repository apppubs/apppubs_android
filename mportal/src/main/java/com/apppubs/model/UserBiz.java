package com.apppubs.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.apppubs.bean.UserInfo;
import com.apppubs.AppContext;
import com.apppubs.constant.URLs;
import com.apppubs.net.WMHHttpClient;
import com.apppubs.net.WMHHttpErrorCode;
import com.apppubs.net.WMHRequestListener;
import com.apppubs.util.JSONResult;
import com.apppubs.util.JSONUtils;

/**
 * Created by zhangwen on 2017/10/24.
 */

public class UserBiz {

	private static UserBiz sUserBiz;
	private Context mContext;

	private UserBiz(Context context){
		mContext = context;
	}

	public interface GetUserInfoCallback{
		void onException(WMHErrorCode code);
		void onDone(UserInfo user);
	}

	public static UserBiz getInstance(Context context){
		if (sUserBiz==null){
			synchronized (UserBiz.class){
				if (sUserBiz==null){
					sUserBiz = new UserBiz(context);
				}
			}
		}
		return sUserBiz;
	}

	public void getUserInfo(String userid, final GetUserInfoCallback callback){
		String userInfoUrl =  String.format(URLs.URL_USER_INFO,URLs.baseURL,URLs.appCode, userid);
		WMHHttpClient httpClient = AppContext.getInstance(mContext).getHttpClient();
		httpClient.GET(userInfoUrl, null, new WMHRequestListener() {
			@Override
			public void onDone(JSONResult jsonResult, @Nullable WMHHttpErrorCode errorCode) {
				if (errorCode==null){
					UserInfo userinfo = JSONUtils.parseObjectFromJson(jsonResult.result,UserInfo.class);
					callback.onDone(userinfo);
				}else{
					WMHErrorCode e = null;
					switch (errorCode){
						case JSON_PARSE_ERROR:
							e = WMHErrorCode.JSON_PARSE_ERROR;
						case IO_EXCEPTION:
							e = WMHErrorCode.IO_EXCEPTION;
						default:
							e = WMHErrorCode.UNKNOWN;
					}
					callback.onException(e);

				}
			}
		});
	}

}
