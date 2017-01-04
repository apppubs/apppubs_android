package com.mportal.client.bean;

import com.orm.SugarRecord;

/**
 * 城市属性实体类
 *
 */
public class City extends SugarRecord{
	private String name; //城市名字
	private String nameFirstInitial; //城市名第一个字拼音首字母
	private String nameInitial;//拼音首字母
	public String getName()
	{
		return name;
	}
    
	public City() {
		super();
	}

	public City(String name, String nameFirstInitial, String nameInitial) {
		super();
		this.name = name;
		this.nameFirstInitial = nameFirstInitial;
		this.nameInitial = nameInitial;
	}

	public void setName(String cityName)
	{
		name = cityName;
	}

	public String getNameFirstInitial()
	{
		return nameFirstInitial;
	}

	public void setNameFirstInitial(String nameSort)
	{
		this.nameFirstInitial = nameSort;
	}

	@Override
	public String getId() {
		
		return null;
	}

	@Override
	public void setId(String id) {
	}

	public String getNameInitial() {
		return nameInitial;
	}

	public void setNameInitial(String nameInitial) {
		this.nameInitial = nameInitial;
	}

	

}
