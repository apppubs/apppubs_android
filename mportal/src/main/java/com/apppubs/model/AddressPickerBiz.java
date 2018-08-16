package com.apppubs.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apppubs.bean.AddressModel;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.net.APHttpClient;
import com.apppubs.net.IHttpClient;
import com.apppubs.net.IRequestListener;
import com.apppubs.util.LogM;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by siger on 2018/1/31.
 */

public class AddressPickerBiz {

    public interface AddressPickerListener {
        void onDone(List<AddressModel> models);

        void onFailure(Exception e);
    }

    public void getAddressList(String rootId, final AddressPickerListener lisenter) {
        String url = "http://result.eolinker.com" +
                "/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=address";
        IHttpClient httpClient = new APHttpClient();
        Map<String, String> params = new HashMap<>();
        params.put("code", rootId);
        httpClient.asyncPOST(url, params, new IRequestListener() {
            @Override
            public void onResponse(String json, APError e) {
                if (e == null) {
                    JSONObject jsonObj = JSONObject.parseObject(json);
                    Integer code = jsonObj.getInteger("code");
                    if (code == APErrorCode.SUCCESS.getCode()) {
                        JSONObject result = jsonObj.getJSONObject("result");
                        JSONArray items = result.getJSONArray("items");
                        List<AddressModel> list = new ArrayList<AddressModel>();
                        for (int i = -1; ++i < items.size(); ) {
                            String itemStr = items.getString(i);
                            AddressModel model = null;
                            try {
                                model = new AddressModel(itemStr);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            list.add(model);
                        }
                        lisenter.onDone(list);
                    } else {
                        LogM.log(AddressPickerBiz.class, e.getMsg());
                    }

                } else {
                    LogM.log(AddressPickerBiz.class, e.getMsg());
                }
            }
        });
    }
}
