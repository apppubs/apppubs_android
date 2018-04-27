package com.apppubs.bean;

import java.util.Date;

import com.orm.SugarRecord;

/**
 * 文章收藏
 * @author zhangwen 2014-12-28
 *
 */
public class TCollection extends SugarRecord{
	
	/**
	 * 普通收藏
	 */
	public static final int TYPE_NORMAL = 0;
	/**
	 * 报纸收藏
	 */
	public static final int TYPE_PAPER = 1;
	
	/**
	 * 图片收藏
	 */
	public static final int TYPE_PIC = 2;
	/**
	 * 视频收藏
	 */
	public static final int TYPE_VEDIO= 3;
	
	public static final int TYPE_URL = 4;//链接
	
	
	private String infoId;//文章id
	private String title;//标题
	private Date addTime;//收藏时间
	private int type;//收藏类型 0=普通信息1=报纸
	private String contentAbs;//内容摘要
	private String paperName;//报纸名称
	private String channelCode;//频道

	/**
	 * 新闻类别信息用的构造函数
	 * @param infoId
	 * @param title
	 * @param addTime
	 * @param type
	 * @param contentAbs
	 * @param channelCode
	 */
	public TCollection(String infoId, String title, Date addTime, int type,
					   String contentAbs, String channelCode) {
		super();
		this.infoId = infoId;
		this.title = title;
		this.addTime = addTime;
		this.type = type;
		this.contentAbs = contentAbs;
		this.channelCode = channelCode;
	}

	public TCollection(){
		super();
	}
	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContentAbs() {
		return contentAbs;
	}

	public void setContentAbs(String contentAbs) {
		this.contentAbs = contentAbs;
	}

	public String getPaperName() {
		return paperName;
	}

	public void setPaperName(String paperName) {
		this.paperName = paperName;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	@Override
	public String getId() {
		
		return null;
	}

	@Override
	public void setId(String id) {
	}
	
	
	
}
