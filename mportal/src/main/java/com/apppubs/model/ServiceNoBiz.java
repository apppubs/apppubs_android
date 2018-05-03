package com.apppubs.model;

import android.content.Context;

import com.apppubs.bean.http.MyServiceNOsResult;
import com.apppubs.bean.http.ServiceNOInfoPageResult;
import com.apppubs.bean.http.ServiceNOInfoResult;
import com.apppubs.constant.APError;
import com.apppubs.ui.activity.MainHandler;

import java.util.HashMap;
import java.util.Map;

public class ServiceNoBiz extends BaseBiz {
    public ServiceNoBiz(Context context) {
        super(context);
    }

    public void loadMyServiceNOs(final IAPCallback<MyServiceNOsResult> callback){
        String url = "http://result.eolinker.com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=my_servicenos";
        asyncPOST(url, null, MyServiceNOsResult.class, new IRQListener<MyServiceNOsResult>() {
            @Override
            public void onResponse(final MyServiceNOsResult result, final APError error) {
                if (error == null){
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(result);
                        }
                    });
                }else{
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(error);
                        }
                    });
                }
            }
        });
    }

    public void loadInfoPage(String servicenoId, int pageNum, int pageSize, final IAPCallback<ServiceNOInfoPageResult> callback){
        String url = "http://result.eolinker.com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=serviceno_article_page";
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", pageNum+"");
        params.put("pageSize", pageSize+"");
        params.put("servicenoId", servicenoId);
        asyncPOST(url, params, ServiceNOInfoPageResult.class, new IRQListener<ServiceNOInfoPageResult>(){

            @Override
            public void onResponse(final ServiceNOInfoPageResult jr, final APError error) {
                if (error == null){
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(jr);
                        }
                    });
                }else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(error);
                        }
                    });

                }
            }
        });
    }

    public void loadServiceNOInfo(String id, final IAPCallback<ServiceNOInfoResult> callback){
        String url = "http://result.eolinker.com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=serviceno";
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        asyncPOST(url, params, ServiceNOInfoResult.class, new IRQListener<ServiceNOInfoResult>(){

            @Override
            public void onResponse(final ServiceNOInfoResult jr, final APError error) {
                if (error == null){
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(jr);
                        }
                    });
                }else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(error);
                        }
                    });
                }
            }
        });
    }

}
