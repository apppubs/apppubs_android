package com.apppubs.d20.net;

import android.support.annotation.NonNull;

import com.apppubs.d20.util.JSONResult;

/**
 * Created by zhangwen on 2017/7/23.
 */

public interface WMHRequestListener {

	void onDone(JSONResult jsonResult,@NonNull WMHHttpErrorCode errorCode);
}
