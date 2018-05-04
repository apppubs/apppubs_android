package com.apppubs.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.apppubs.bean.http.ChannelsResult;
import com.apppubs.util.Utils;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * 频道(栏目)
 */
@Table(name = "news_channel")
public class TNewsChannel extends SugarRecord implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final int SHOWTYPE_HEADLINE = 1;
    public static final int SHOWTYPE_NORMAL = 2;

    private String appId;
    private String id;

    private String code;//频道编码
    private String name;//名称
    private int displayOrder;//显示顺序 0 标识不显示，列于待选
    private int showType;//显示类别  1==头条 2==文字+图片 3==图片为主  4==纯文本 5==视频 6==weburl
    private int focusPicNum;//头图数量
    private int paperSize;//分页大小
    private String pic;//频道图片
    private String typeId;//频道类别
    private Date lastUpdateTime;//服务器上次更新时间
    private Date localLastUpdateTime;// 客户端上次更新时间
    private String linkURL;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public int getFocusPicNum() {
        return focusPicNum;
    }

    public void setFocusPicNum(int focusPicNum) {
        this.focusPicNum = focusPicNum;
    }

    public int getPaperSize() {
        return paperSize;
    }

    public void setPaperSize(int paperSize) {
        this.paperSize = paperSize;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    @Override
    public String getId() {

        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Date getLocalLastupdateTime() {
        return localLastUpdateTime;
    }

    public void setLocalLastupdateTime(Date localLastupdateTime) {
        this.localLastUpdateTime = localLastupdateTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public static TNewsChannel createFrom(ChannelsResult.Item item) {
        TNewsChannel channel = new TNewsChannel();
        channel.setCode(item.getChannelCode());
        channel.setName(item.getChannelName());
        channel.setDisplayOrder(item.getDisplayOrder());
        channel.setShowType(item.getShowType());
        channel.setPic(item.getPicURL());
        channel.setFocusPicNum(item.getPicNum());
        channel.setLastUpdateTime(item.getLastUpdateTime());
        channel.setLinkURL(item.getLinkURL());
        return channel;
    }

    public static List<TNewsChannel> createFrom(ChannelsResult result) {
        List<TNewsChannel> channels = new ArrayList<>();
        if (!Utils.isEmpty(result.getItems())) {
            for (ChannelsResult.Item item : result.getItems()) {
                TNewsChannel channel = createFrom(item);
                channels.add(channel);
            }
        }
        return channels;
    }
}
