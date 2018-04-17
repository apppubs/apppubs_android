package com.apppubs.net;

import android.support.annotation.Nullable;

import com.apppubs.util.JSONResult;

/**
 * Created by zhangwen on 2017/7/23.
 */

public interface WMHRequestListener {

	void onDone(JSONResult jsonResult,@Nullable WMHHttpErrorCode errorCode);
}
