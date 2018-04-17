package com.apppubs.bean;

import java.io.Serializable;

import com.orm.SugarRecord;

/**
 * 日报订单
 * 
 */
public class PaperOrder extends SugarRecord implements Serializable{
	
	private String orderNum;//订单号
	private String goodsName;
	private String goodsDesc;
	private double price;
	private String ligintime;
	private String endtime;
	private String state;
	public PaperOrder(){}
	public PaperOrder(String orderNum, String goodsName, String goodsDesc,
			double price) {
		this.orderNum = orderNum;
		this.goodsName = goodsName;
		this.goodsDesc = goodsDesc;
		this.price = price;
	}

	public PaperOrder(String orderNum, String goodsName, double price,
			String ligintime, String endtime, String state) {
		super();
		this.orderNum = orderNum;
		this.goodsName = goodsName;
		this.price = price;
		this.ligintime = ligintime;
		this.endtime = endtime;
		this.state = state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getState() {
		return state;
	}
	public PaperOrder(String orderNum, String goodsName, String goodsDesc,
			double price, String ligintime, String endtime) {
		super();
		this.orderNum = orderNum;
		this.goodsName = goodsName;
		this.goodsDesc = goodsDesc;
		this.price = price;
		this.ligintime = ligintime;
		this.endtime = endtime;
	}
	
	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsDesc() {
		return goodsDesc;
	}

	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getLigintime() {
		return ligintime;
	}
	public void setLigintime(String ligintime) {
		this.ligintime = ligintime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	@Override
	public String getId() {
		
		return null;
	}
	@Override
	public void setId(String id) {
	}


}
