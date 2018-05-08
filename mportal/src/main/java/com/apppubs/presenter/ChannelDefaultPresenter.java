package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.bean.TNewsInfo;
import com.apppubs.bean.http.ArticlePageResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.NewsBiz;
import com.apppubs.ui.news.IChannelDefaultView;

import java.util.ArrayList;
import java.util.List;

public class ChannelDefaultPresenter extends AbsPresenter<IChannelDefaultView> {

    private int mPageNum = 1;
    private int mPageSize = 20;
    private int mTotalNum;
    private String mChannelCode;
    private NewsBiz mNewsBiz;

    private List<TNewsInfo> mNewsInfoList;

    public ChannelDefaultPresenter(Context context, IChannelDefaultView view, String channelCode) {
        super(context, view);
        mChannelCode = channelCode;
        mNewsBiz = NewsBiz.getInstance(mContext);
        mNewsInfoList = new ArrayList<>();
    }

    public void onRefreshClicked() {
        mPageNum = 1;
        loadMore();
    }

    public void onLoadMoreClicked() {
        loadMore();
    }

    private void loadMore() {
        if (mPageNum > 1 && mPageNum * mPageSize > mTotalNum) {
            mView.onError(new APError(APErrorCode.HAVE_NO_ERROR, "没有更多！"));
            return;
        }
        mNewsBiz.loadChannelArticlePage(mChannelCode, mPageNum, mPageSize, new
                IAPCallback<ArticlePageResult>() {

                    @Override
                    public void onDone(ArticlePageResult obj) {
                        mTotalNum = obj.getTotalNum();
                        List<TNewsInfo> infos = TNewsInfo.createFrom(obj);
                        if (mPageNum == 1) {
                            mNewsInfoList = infos;
                        } else {
                            mNewsInfoList.addAll(infos);
                        }
                        mView.setDatas(mNewsInfoList);
                        mView.stopLoadMore();
                        mView.stopRefresh();
                        mPageNum++;
                    }

                    @Override
                    public void onException(APError error) {
                        mView.onError(error);
                        mView.stopLoadMore();
                        mView.stopRefresh();
                    }
                });
    }
}
