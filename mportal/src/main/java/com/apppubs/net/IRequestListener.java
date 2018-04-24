package com.apppubs.net;

import com.apppubs.bean.http.JsonResult;
import com.apppubs.constant.APError;

/**
 * Created by siger on 2018/4/19.
 */
public interface IRequestListener {
    void onResponse(String json,APError e);
}
