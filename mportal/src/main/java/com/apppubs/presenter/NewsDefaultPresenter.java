package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.bean.http.ArticleResult;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.NewsBiz;
import com.apppubs.ui.news.INewsDefaultInfoView;

public class NewsDefaultPresenter extends AbsPresenter<INewsDefaultInfoView> {

    private String mInfoId;
    private String mChannelCode;

    private NewsBiz mNewsBiz;

    public NewsDefaultPresenter(Context context, INewsDefaultInfoView view, String infoId, String channelCode) {
        super(context, view);
        mInfoId = infoId;
        mChannelCode = channelCode;
        mNewsBiz = NewsBiz.getInstance(mContext);
    }

    public void onCreateView(){
        loadData();
    }

    private void loadData() {
        mView.showLoading();
        mNewsBiz.loadArticle(mInfoId, mChannelCode, new IAPCallback<ArticleResult>() {
            @Override
            public void onDone(ArticleResult obj) {
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
