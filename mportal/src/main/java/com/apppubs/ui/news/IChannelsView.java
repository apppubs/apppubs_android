package com.apppubs.ui.news;

import com.apppubs.bean.TNewsChannel;
import com.apppubs.ui.ICommonView;

import java.util.List;

public interface IChannelsView extends ICommonView{

    void setSelectedChannels(List<TNewsChannel> channels);
}
