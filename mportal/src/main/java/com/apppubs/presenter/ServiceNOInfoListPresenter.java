package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.bean.http.ServiceNOInfoPageResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.ServiceNoBiz;
import com.apppubs.ui.IServiceNoInfoPageView;

import java.util.List;

public class ServiceNOInfoListPresenter extends AbsPresenter<IServiceNoInfoPageView> {

    private int mPageNum = 1;
    private int mPageSize = 20;
    private int mTotalNum;

    private List<ServiceNOInfoPageResult.Items> mDatas;

    private ServiceNoBiz mBiz;
    private String mServicenoId;


    public ServiceNOInfoListPresenter(Context context, IServiceNoInfoPageView view,
                                      String servicenoId) {
        super(context, view);
        mServicenoId = servicenoId;
        mBiz = new ServiceNoBiz(mContext);
    }

    public void onRefresh(){
        mPageNum = 1;
        loadMore();
    }

    public void onLoadMore() {

        if (mPageNum > 1 && mPageNum * mPageSize > mTotalNum) {
            mView.haveLoadAll();
        }else {
            loadMore();
        }
    }

    private void loadMore() {
        mBiz.loadInfoPage(mServicenoId, mPageNum, mPageSize, new
                IAPCallback<ServiceNOInfoPageResult>() {
            @Override
            public void onDone(ServiceNOInfoPageResult obj) {
                mTotalNum = obj.getTotalNum();
                if (mTotalNum < 1){
                    mView.showEmptyView();
                }else{
                    mView.hideEmptyView();
                    if (mPageNum == 1){
                        mDatas = obj.getItems();
                    }else {
                        mDatas.addAll(obj.getItems());
                    }
                    mView.setDatas(mDatas);
                }
                mPageNum ++;
                mView.hideRefreshAndLoadMore();
            }

            @Override
            public void onException(APError error) {
                mView.onError(error);
                mView.hideRefreshAndLoadMore();
            }
        });
    }
}
