package com.apppubs.model.myfile;

import android.content.Context;

import com.apppubs.bean.http.IJsonResult;
import com.apppubs.bean.http.MyFilePageResult;
import com.apppubs.constant.APError;
import com.apppubs.model.BaseBiz;
import com.apppubs.model.IAPCallback;
import com.apppubs.ui.activity.MainHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.rong.imlib.IStringCallback;

public class MyFileBiz extends BaseBiz{


    public MyFileBiz(Context context) {
        super(context);
    }

    public void loadMyFilePage(int pageNum, int pageSize, final IAPCallback<MyFilePageResult> callback){
        String url = "http://result.eolinker.com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=my_file_page";
        Map<String,String> params = new HashMap<>();
        params.put("pageNum", pageNum+"");
        params.put("pageSize", pageSize+"");
        asyncPOST(url, params, MyFilePageResult.class, new IRQListener<MyFilePageResult>() {
            @Override
            public void onResponse(final MyFilePageResult jr, final APError error) {
                if (error == null){
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(jr);
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

    public void searchMyFilePage(String searchText, int pageNum, int pageSize, final IAPCallback<MyFilePageResult> callback){
        String url = "http://result.eolinker.com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=search_my_file";
        Map<String,String> params = new HashMap<>();
        params.put("pageNum", pageNum+"");
        params.put("pageSize", pageSize+"");
        params.put("fileName",searchText);
        asyncPOST(url, params, MyFilePageResult.class, new IRQListener<MyFilePageResult>() {
            @Override
            public void onResponse(final MyFilePageResult jr, final APError error) {
                if (error == null){
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(jr);
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

    public void deleMyFile(String fileId, final IAPCallback<String> callback){
        String url = "http://result.eolinker.com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=del_my_file";
        Map<String,String> params = new HashMap<>();
        params.put("fileId", fileId);

        asyncPOST(url,params, new IRQStringListener(){

            @Override
            public void onResponse(String jr, final APError error) {
                if (error == null){
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(null);
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
}
