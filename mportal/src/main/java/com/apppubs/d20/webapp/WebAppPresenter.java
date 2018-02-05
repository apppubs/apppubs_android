package com.apppubs.d20.webapp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.apppubs.d20.webapp.model.UserPickerVO;
import com.apppubs.d20.webapp.model.UserVO;
import com.apppubs.jsbridge.BridgeHandler;
import com.apppubs.jsbridge.CallBackFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                WebUserPickerActivity.startActivity(mContext, vo, new WebUserPickerActivity.UserPickerListener() {

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
    }

    public void onSignatureDone(String result) {
        System.out.println(result);
        mPaddingCallbackFunction.onCallBack(result);
        mView.hideSignaturePanel();
    }


}
