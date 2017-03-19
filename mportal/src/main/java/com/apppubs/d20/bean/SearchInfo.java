package com.apppubs.d20.bean;

public class SearchInfo {
	private int _id;
	private String pagernum;// 第几页 -页数
	private String infoid;
	private String url;
	private String topic;
	private String pubtime;
	private String coumment;
	private String sunnary;
	private String chanlcode;
/**
 *           infoid ":"100",
			" topic ":"标题",
			" pubtime ":"2012-5-25 11:11:11"
 */
	public SearchInfo(int _id, String pagernum, String infoid, String url,
			String topic, String pubtime, String coumment, String sunnary) {
		super();
		this._id = _id;
		this.pagernum = pagernum;
		this.infoid = infoid;
		this.url = url;
		this.topic = topic;
		this.pubtime = pubtime;
		this.coumment = coumment;
		this.sunnary = sunnary;
	}
    
	public SearchInfo( String infoid, String url, String topic,
		String pubtime, String coumment, String sunnary, String chanlcode) {
	super();
	this.infoid = infoid;
	this.url = url;
	this.topic = topic;
	this.pubtime = pubtime;
	this.coumment = coumment;
	this.sunnary = sunnary;
	this.chanlcode = chanlcode;
}

	public SearchInfo(String infoid, String url, String topic, String pubtime,
		String coumment, String sunnary) {
	super();
	this.infoid = infoid;
	this.url = url;
	this.topic = topic;
	this.pubtime = pubtime;
	this.coumment = coumment;
	this.sunnary = sunnary;
}	

	public String getChanlcode() {
		return chanlcode;
	}

	public void setChanlcode(String chanlcode) {
		this.chanlcode = chanlcode;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getPagernum() {
		return pagernum;
	}

	public void setPagernum(String pagernum) {
		this.pagernum = pagernum;
	}

	public String getInfoid() {
		return infoid;
	}

	public void setInfoid(String infoid) {
		this.infoid = infoid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getPubtime() {
		return pubtime;
	}

	public void setPubtime(String pubtime) {
		this.pubtime = pubtime;
	}

	public String getCoumment() {
		return coumment;
	}

	public void setCoumment(String coumment) {
		this.coumment = coumment;
	}

	public String getSunnary() {
		return sunnary;
	}

	public void setSunnary(String sunnary) {
		this.sunnary = sunnary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _id;
		result = prime * result
				+ ((coumment == null) ? 0 : coumment.hashCode());
		result = prime * result + ((infoid == null) ? 0 : infoid.hashCode());
		result = prime * result
				+ ((pagernum == null) ? 0 : pagernum.hashCode());
		result = prime * result + ((pubtime == null) ? 0 : pubtime.hashCode());
		result = prime * result + ((sunnary == null) ? 0 : sunnary.hashCode());
		result = prime * result + ((topic == null) ? 0 : topic.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		SearchInfo other = (SearchInfo) obj;
		if (_id != other._id)
			return false;
		if (coumment == null) {
			if (other.coumment != null)
				return false;
		} else if (!coumment.equals(other.coumment))
			return false;
		if (infoid == null) {
			if (other.infoid != null)
				return false;
		} else if (!infoid.equals(other.infoid))
			return false;
		if (pagernum == null) {
			if (other.pagernum != null)
				return false;
		} else if (!pagernum.equals(other.pagernum))
			return false;
		if (pubtime == null) {
			if (other.pubtime != null)
				return false;
		} else if (!pubtime.equals(other.pubtime))
			return false;
		if (sunnary == null) {
			if (other.sunnary != null)
				return false;
		} else if (!sunnary.equals(other.sunnary))
			return false;
		if (topic == null) {
			if (other.topic != null)
				return false;
		} else if (!topic.equals(other.topic))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SearchInfo [_id=" + _id + ", pagernum=" + pagernum
				+ ", infoid=" + infoid + ", url=" + url + ", topic=" + topic
				+ ", pubtime=" + pubtime + ", coumment=" + coumment
				+ ", sunnary=" + sunnary + "]";
	}

}
