package com.apppubs.d20.adbook;

import android.content.Context;
import android.support.annotation.Nullable;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.bean.UserInfo;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.model.WMHErrorCode;
import com.apppubs.d20.myfile.FileCacheErrorCode;
import com.apppubs.d20.net.WMHHttpClient;
import com.apppubs.d20.net.WMHHttpErrorCode;
import com.apppubs.d20.net.WMHRequestListener;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.util.JSONUtils;

import org.json.JSONObject;

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
