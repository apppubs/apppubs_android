package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.bean.http.MyServiceNOsResult;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.ServiceNoBiz;
import com.apppubs.ui.ICommonListView;
import com.apppubs.ui.ICommonView;
import com.apppubs.util.Utils;

public class ServiceNOPresenter extends AbsPresenter<ICommonListView<MyServiceNOsResult.MyServiceNOItem>> {

    private ServiceNoBiz mBiz;

    public ServiceNOPresenter(Context context, ICommonListView view) {
        super(context, view);
        mBiz = new ServiceNoBiz(context);
    }

    public void onVisiable(){
        loadData();
    }

    private void loadData(){
        mView.showLoading();
        mBiz.loadMyServiceNOs(new IAPCallback<MyServiceNOsResult>() {
            @Override
            public void onDone(MyServiceNOsResult obj) {
                if (Utils.isEmpty(obj.getItems())){
                    mView.showEmptyView();
                }else {
                    mView.hideEmptyView();
                    mView.setDatas(obj.getItems());
                }
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
