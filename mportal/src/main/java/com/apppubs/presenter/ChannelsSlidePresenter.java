package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.ui.news.IChannelsSlideView;

public class ChannelsSlidePresenter extends ChannelsPresenter<IChannelsSlideView> {
    public ChannelsSlidePresenter(Context context, IChannelsSlideView view, String channelGroupId) {
        super(context, view, channelGroupId);
    }
}
