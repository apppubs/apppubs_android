package com.apppubs.bean;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * 城市属性实体类
 *
 */
@Table(name="city")
public class TCity extends SugarRecord{
	private String name; //城市名字
	private String nameFirstInitial; //城市名第一个字拼音首字母
	private String nameInitial;//拼音首字母
	public String getName()
	{
		return name;
	}
    
	public TCity() {
		super();
	}

	public TCity(String name, String nameFirstInitial, String nameInitial) {
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
