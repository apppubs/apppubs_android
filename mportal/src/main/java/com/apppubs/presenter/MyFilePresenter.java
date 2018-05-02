package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.bean.http.MyFilePageResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.MyFileModel;
import com.apppubs.model.myfile.MyFileBiz;
import com.apppubs.ui.myfile.IMyFileView;

import java.util.ArrayList;
import java.util.List;

public class MyFilePresenter extends AbsPresenter<IMyFileView> {

    private int mPageNum = 1;
    private int mPageSize = 2;
    private int mTotalNum;

    private List<MyFileModel> mDatas;
    private List<MyFileModel> mSearchDatas;

    private int mSearchPageNum = 1;
    private int mSearchPageSize = 2;
    private int mSearchTotalNum;

    private MyFileBiz mMyFileBiz;

    public MyFilePresenter(Context context, IMyFileView view) {
        super(context, view);
        mMyFileBiz = new MyFileBiz(context);
        mDatas = new ArrayList<>();
        mSearchDatas = new ArrayList<>();
    }

    public void onLoadMoreClicked() {
        if (mPageNum > 1 && mPageSize * mPageNum > mTotalNum) {
            mView.onError(new APError(APErrorCode.HAVE_NO_ERROR,"没有更多！"));
            mView.stopLoadMore();
        } else {
            loadMore();
        }
    }

    public void onRefreshClicked() {
        mPageNum = 1;
        loadMore();
    }

    private void loadMore() {

        mMyFileBiz.loadMyFilePage(mPageNum, mPageSize, new IAPCallback<MyFilePageResult>() {
            @Override
            public void onDone(MyFilePageResult obj) {
                mTotalNum = obj.getTotalNum();
                List<MyFileModel> models = MyFileModel.createFrom(obj.getItems());
                if (mPageNum == 1) {
                    mDatas = models;
                    mView.stopRefresh();
                } else {
                    mDatas.addAll(models);
                    mView.stopLoadMore();
                }
                mView.setFileModels(models);

                mPageNum++;
            }

            @Override
            public void onException(APError error) {
                mView.onError(error);
                if (mPageNum == 1) {
                    mView.stopRefresh();
                } else {
                    mView.stopLoadMore();
                }
            }
        });
    }

    public void onSerchMoreClicked(String searchText) {
        if (mSearchPageNum> 1 && mSearchPageSize * mSearchPageNum > mSearchTotalNum) {
            mView.onError(new APError(APErrorCode.HAVE_NO_ERROR,"没有更多！"));
            mView.stopLoadMore();
        } else {
            searchMore(searchText);
        }
    }
    public void searchMore(String text){
        mMyFileBiz.searchMyFilePage(text,mSearchPageNum, mSearchPageSize, new IAPCallback<MyFilePageResult>() {
            @Override
            public void onDone(MyFilePageResult obj) {
                mTotalNum = obj.getTotalNum();
                List<MyFileModel> models = MyFileModel.createFrom(obj.getItems());
                if (mSearchPageNum == 1) {
                    mSearchDatas = models;
                    mView.stopRefresh();
                } else {
                    mSearchDatas.addAll(models);
                    mView.stopLoadMore();
                }
                mView.setFileModels(mSearchDatas);

                mSearchPageNum++;
            }

            @Override
            public void onException(APError error) {
                mView.onError(error);
                if (mSearchPageNum == 1) {
                    mView.stopRefresh();
                } else {
                    mView.stopLoadMore();
                }
            }
        });
    }

    public void onStopSearch(){
        mView.setFileModels(mDatas);
        mSearchPageNum = 1;
    }

    public void onDeleteRemoteClicked(String fileId, IAPCallback<String> callback){
       mMyFileBiz.deleMyFile(fileId,callback);
    }
}
