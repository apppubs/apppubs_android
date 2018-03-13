package com.apppubs.d20.webapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.apppubs.d20.AppManager;
import com.apppubs.d20.model.SystemBussiness;
import com.apppubs.d20.model.VersionInfo;
import com.apppubs.d20.util.Utils;
import com.apppubs.d20.webapp.model.UserPickerVO;
import com.apppubs.d20.webapp.model.UserVO;
import com.apppubs.jsbridge.BridgeHandler;
import com.apppubs.jsbridge.CallBackFunction;
import com.jelly.mango.MultiplexImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwen on 2018/1/8.
 */

public class WebAppPresenter {

    private Context mContext;
    private IWebAppView mView;

    private CallBackFunction mPaddingCallbackFunction;

    public WebAppPresenter(Context context, IWebAppView view) {
        mContext = context;
        mView = view;
    }

    public void onCreateView() {
        resisterHandler();
    }

    private void resisterHandler() {

        //扫描二维码
        mView.getBridgeWebView().registerHandler("scanQRCode", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                mPaddingCallbackFunction = function;
                try {
                    JSONObject jo = new JSONObject(data);
                    boolean selfResovle = jo.getBoolean("selfResolve");
                    mView.showScanQRCode(selfResovle);
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showScanQRCode(true);
                }
            }
        });

        //选择图片
        mView.getBridgeWebView().registerHandler("userpicker", new BridgeHandler() {
            @Override
            public void handler(String data, final CallBackFunction function) {
                UserPickerVO vo = new UserPickerVO();
                try {
                    JSONObject jo = new JSONObject(data);
                    vo.setmSelectMode(jo.getInt("selectMode"));
                    vo.setmDeptsURL(jo.getString("deptsURL"));
                    vo.setmUsersURL(jo.getString("usersURL"));
                    vo.setmSearchURL(jo.getString("searchURL"));
                    vo.setRootDeptId(jo.getString("rootDeptId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(data);
                WebUserPickerActivity.startActivity(mContext, vo, new WebUserPickerActivity
                        .UserPickerListener() {

                    @Override
                    public void onPickDone(List<UserVO> users) {
                        String result = getJsonResultStr(users);
                        System.out.println("选择结果" + result);
                        function.onCallBack(result);
                    }

                    @NonNull
                    private String getJsonResultStr(List<UserVO> users) {
                        JSONObject jo = new JSONObject();
                        try {
                            jo.put("success", true);
                            JSONArray items = new JSONArray();
                            for (UserVO uv : users) {
                                JSONObject j = new JSONObject();
                                j.put("id", uv.getId());
                                j.put("name", uv.getName());
                                items.put(j);
                            }
                            jo.put("users", items);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return jo.toString();
                    }
                });
            }
        });

        mView.getBridgeWebView().registerHandler("handwriting", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                mPaddingCallbackFunction = function;
                try {
                    mView.showSignaturePanel(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        mView.getBridgeWebView().registerHandler("getaddress", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    String code = AppManager.getInstant(mContext).getCurrentAddressCode();
                    String name = AppManager.getInstant(mContext).getCurrentAddressName();
                    JSONObject result = new JSONObject();
                    result.put("name", name);
                    result.put("code", code);
                    JSONObject jo = new JSONObject();
                    jo.put("success", true);
                    jo.put("result", result);
                    function.onCallBack(jo.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mView.getBridgeWebView().registerHandler("displayImg", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    JSONObject jo = new JSONObject(data);
                    JSONArray ja = jo.getJSONArray("imgs");
                    String[] imgs = new String[ja.length()];
                    List<MultiplexImage> images = new ArrayList<>();
                    for (int i = -1; ++i < ja.length(); ) {
                        imgs[i] = ja.getString(i);
                        images.add(new MultiplexImage(ja.getString(i), ja
                                .getString(i),
                                MultiplexImage.ImageType.NORMAL));
                    }
                    mView.showImages(images);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        mView.getBridgeWebView().registerHandler("checkVersion", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                checkUpdate();
            }
        });
    }

    public void onSignatureDone(String result) {
        System.out.println(result);
        mPaddingCallbackFunction.onCallBack(result);
        mView.hideSignaturePanel();
    }

    public void onQRCodeDone(String result) {
        System.out.println("onQRCodeDone:" + result);
        String json = getQRCodeResultJsonString(result);
        mPaddingCallbackFunction.onCallBack(json);
    }

    public void startDownloadApp(String updateUrl) {
        AppManager.getInstant(mContext).downloadApp(updateUrl);
    }

    private String getQRCodeResultJsonString(String result) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("success", true);
            JSONObject resultJO = new JSONObject();
            resultJO.put("msg", result);
            jo.put("result", resultJO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo.toString();
    }

    private void checkUpdate() {
        SystemBussiness.getInstance(mContext).checkUpdate(mContext, new SystemBussiness.CheckUpdateListener() {

            @Override
            public void onDone(VersionInfo vi) {
                mView.showVersionInfo(vi);
            }
        });
    }

}
