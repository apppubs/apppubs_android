package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.ui.news.IChannelsSquareView;

public class ChannelsSquarePresenter extends ChannelsPresenter<IChannelsSquareView> {

    public ChannelsSquarePresenter(Context context, IChannelsSquareView view, String channelGroupId) {
        super(context, view, channelGroupId);
    }
}
