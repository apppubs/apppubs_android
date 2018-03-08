package com.apppubs.d20.webapp;

import com.apppubs.d20.model.WMHErrorCode;
import com.apppubs.jsbridge.BridgeWebView;
import com.jelly.mango.MultiplexImage;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhangwen on 2018/1/8.
 */

public interface IWebAppView {

    BridgeWebView getBridgeWebView();

    void showSignaturePanel(JSONObject jsonObject);

    void hideSignaturePanel();

    void showImages(List<MultiplexImage> images);

    void showScanQRCode(boolean needSelfResolve);

}
