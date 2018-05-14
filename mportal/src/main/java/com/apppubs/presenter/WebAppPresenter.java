package com.apppubs.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.apppubs.bean.UserInfo;
import com.apppubs.AppContext;
import com.apppubs.AppManager;
import com.apppubs.ui.webapp.IWebAppView;
import com.apppubs.ui.webapp.WebUserPickerActivity;
import com.apppubs.bean.webapp.UserPickerVO;
import com.apppubs.bean.webapp.UserVO;
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
                    String code = AppManager.getInstance(mContext).getCurrentAddressCode();
                    String name = AppManager.getInstance(mContext).getCurrentAddressName();
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

        //版本检查
        mView.getBridgeWebView().registerHandler("checkVersion", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                mView.checkUpdate();
            }
        });

        //用户信息获取
        mView.getBridgeWebView().registerHandler("getUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                JSONObject jo = getUserInfoJson();
                function.onCallBack(jo.toString());
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


    @NonNull
    private JSONObject getUserInfoJson() {
        UserInfo userInfo = AppContext.getInstance(mContext).getCurrentUser();
        JSONObject jo = new JSONObject();
        try {
            jo.put("success", true);
            JSONObject resultJo = new JSONObject();
            resultJo.put("userId", userInfo.getUserId());
            resultJo.put("username", userInfo.getUsername());
            resultJo.put("avatarUrl", userInfo.getAvatarUrl());
            resultJo.put("truename", userInfo.getTrueName());
            jo.put("result", resultJo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

}
