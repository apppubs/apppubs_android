package com.apppubs.net;

import java.io.File;
import java.util.Map;

/**
 * Created by siger on 2018/4/18.
 */

public interface IHttpClient {
    void asyncPOST(String url, Map<String, String> params, IRequestListener listener);

    void asyncPOST(String url, Map<String, String> headers, Map<String, String> params,
                   IRequestListener listener);

    void asyncPOST(String url, Map<String, String> params, File file, IRequestListener listener);

    void asyncMultiPOST(String url, Map<String, String> headers, Map<String, Object> params,
                        IRequestListener listener);

    String syncPOST(String url, Map<String, String> params) throws APNetException;

    String syncPOST(String url, Map<String, String> headers, Map<String, String> params)
            throws APNetException;

    String syncPOST(String url, Map<String, String> params, File file) throws APNetException;


    String syncPOST(String url, Map<String, String> headers, File file, Map<String, String>
            params) throws APNetException;

}


