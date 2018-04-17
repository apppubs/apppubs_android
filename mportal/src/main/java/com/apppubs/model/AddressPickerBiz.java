package com.apppubs.model;

import android.support.annotation.Nullable;

import com.apppubs.net.WMHHttpClient;
import com.apppubs.net.WMHHttpClientDefaultImpl;
import com.apppubs.net.WMHHttpErrorCode;
import com.apppubs.net.WMHRequestListener;
import com.apppubs.bean.AddressModel;
import com.apppubs.util.JSONResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by siger on 2018/1/31.
 */

public class AddressPickerBiz {

    public interface AddressPickerListener{
        void onDone(List<AddressModel> models);
        void onFailure(Exception e);
    }

    public void getAddressList(String rootId, final AddressPickerListener lisenter) {
        String url = "http://result.eolinker.com" +
                "/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=address";
        WMHHttpClient httpClient = new WMHHttpClientDefaultImpl();
        JSONObject params = new JSONObject();
        try {
            params.put("code",rootId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpClient.POST(url, params.toString(), new WMHRequestListener() {
            @Override
            public void onDone(JSONResult jsonResult, @Nullable WMHHttpErrorCode errorCode) {
                if(errorCode==null&&jsonResult.resultCode == 0){
                    try {
                        JSONArray ja = new JSONArray(jsonResult.getResultJSONObject().getString("items"));
                        List<AddressModel> list = new ArrayList<AddressModel>();
                        for (int i=-1;++i<ja.length();){
                            String json = ja.getString(i);
                            AddressModel model = new AddressModel(json);
                            list.add(model);
                        }
                        lisenter.onDone(list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        lisenter.onFailure(e);
                    }

                }else{
                    lisenter.onFailure(new IOException("获取地址信息失败！"));
                }
            }
        });
    }
}
