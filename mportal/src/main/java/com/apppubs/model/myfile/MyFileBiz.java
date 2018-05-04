package com.apppubs.model.myfile;

import android.content.Context;

import com.apppubs.bean.http.IJsonResult;
import com.apppubs.bean.http.MyFilePageResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.Constants;
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
        Map<String,String> params = new HashMap<>();
        params.put("pageNum", pageNum+"");
        params.put("pageSize", pageSize+"");
        asyncPOST(Constants.API_NAME_MYFILE_PAGE, params, MyFilePageResult.class, new IRQListener<MyFilePageResult>() {
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
        Map<String,String> params = new HashMap<>();
        params.put("pageNum", pageNum+"");
        params.put("pageSize", pageSize+"");
        params.put("fileName",searchText);
        asyncPOST(Constants.API_NAME_SEARCH_MY_FILE, params, MyFilePageResult.class, new IRQListener<MyFilePageResult>() {
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
        Map<String,String> params = new HashMap<>();
        params.put("fileId", fileId);
        asyncPOST(Constants.API_NAME_DEL_MY_FILE,params, new IRQStringListener(){

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
