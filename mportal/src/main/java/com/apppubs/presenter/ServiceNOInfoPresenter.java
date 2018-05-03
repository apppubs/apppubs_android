package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.bean.http.ServiceNOInfoResult;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.ServiceNoBiz;
import com.apppubs.ui.ICommonDataView;

public class ServiceNOInfoPresenter extends AbsPresenter<ICommonDataView<ServiceNOInfoResult>> {

    private ServiceNoBiz mBiz;
    private String mServiceNOId;

    public ServiceNOInfoPresenter(Context context, ICommonDataView<ServiceNOInfoResult> view, String serviceNoId) {
        super(context, view);
        mBiz = new ServiceNoBiz(mContext);

        mServiceNOId = serviceNoId;
    }

    public void onCreate(){
        loadData();
    }

    public void loadData(){
        mView.showLoading();
        mBiz.loadServiceNOInfo(mServiceNOId, new IAPCallback<ServiceNOInfoResult>() {
            @Override
            public void onDone(ServiceNOInfoResult obj) {
                mView.setData(obj);
                mView.hideLoading();
            }

            @Override
            public void onException(APError error) {
                mView.onError(error);
                mView.hideLoading();
            }
        });
    }
}
