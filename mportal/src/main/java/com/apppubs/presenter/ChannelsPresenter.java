package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.bean.TNewsChannel;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.NewsBiz;
import com.apppubs.ui.news.IChannelsView;

import java.util.List;

public abstract class ChannelsPresenter<T extends IChannelsView> extends AbsPresenter<T>{

    protected NewsBiz mNewsBiz;
    protected String mChannelGroupId;

    public ChannelsPresenter(Context context, T view, String channelGroupId) {
        super(context, view);
        mNewsBiz = NewsBiz.getInstance(mContext);
        mChannelGroupId = channelGroupId;
    }

    public void onCreateView(){
        mView.showLoading();
        mNewsBiz.loadChannelGroup(mChannelGroupId, new IAPCallback<List<TNewsChannel>>() {
            @Override
            public void onDone(List<TNewsChannel> obj) {
                mView.hideLoading();
                mView.setSelectedChannels(obj);
            }

            @Override
            public void onException(APError error) {
                mView.hideLoading();
                mView.onError(error);
            }
        });
    }
}
