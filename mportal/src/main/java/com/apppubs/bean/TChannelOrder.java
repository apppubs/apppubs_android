package com.apppubs.bean;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * 用户自定义频道顺序
 */
@Table(name="channel_order")
public class TChannelOrder extends SugarRecord{

    private String id;
    private String channelGroupId;
    private String channelId;
    private int orderNum;
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getChannelGroupId() {
        return channelGroupId;
    }

    public void setChannelGroupId(String channelGroupId) {
        this.channelGroupId = channelGroupId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }
}
