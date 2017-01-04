package com.mportal.client.bean;

import java.io.Serializable;

public class Weather implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String data;
	private String weather;
	private String wind;
	private String temp;
	private String code;
	private String cityName;// 城市名字
	private String NameSort; // 城市首字母
	private String order;

	public String getNameSort() {
		return NameSort;
	}

	public void setNameSort(String nameSort) {
		NameSort = nameSort;
	}

	public Weather(String data, String weather, String wind, String temp, String cityName) {
		this.data = data;
		this.weather = weather;
		this.wind = wind;
		this.temp = temp;
		this.cityName = cityName;
	}

	public Weather(String code, String name, String order) {
		this.code = code;
		this.cityName = name;
		this.order = order;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String name) {
		this.cityName = name;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Weather() {
		super();
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cityName == null) ? 0 : cityName.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((temp == null) ? 0 : temp.hashCode());
		result = prime * result + ((weather == null) ? 0 : weather.hashCode());
		result = prime * result + ((wind == null) ? 0 : wind.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Weather other = (Weather) obj;
		if (cityName == null) {
			if (other.cityName != null)
				return false;
		} else if (!cityName.equals(other.cityName))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (temp == null) {
			if (other.temp != null)
				return false;
		} else if (!temp.equals(other.temp))
			return false;
		if (weather == null) {
			if (other.weather != null)
				return false;
		} else if (!weather.equals(other.weather))
			return false;
		if (wind == null) {
			if (other.wind != null)
				return false;
		} else if (!wind.equals(other.wind))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WeatherInfo [data=" + data + ", weather=" + weather + ", wind=" + wind + ", temp=" + temp + ", cityName=" + cityName
				+ "]";
	}

}
