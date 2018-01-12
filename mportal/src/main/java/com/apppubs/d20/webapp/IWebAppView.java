package com.apppubs.d20.webapp;

import com.apppubs.jsbridge.BridgeWebView;

import org.json.JSONObject;

/**
 * Created by zhangwen on 2018/1/8.
 */

public interface IWebAppView {

    BridgeWebView getBridgeWebView();

    void showSignaturePanel(JSONObject jsonObject);

    void hideSignaturePanel();
}
