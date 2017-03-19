package com.apppubs.d20.bean;

import java.util.Date;

public class History {
	
	private Long id;
	private String msgid;// 历史浏览ID
	private String url;
	private String topic;// 标题
	private Date dotime;// 浏览的时间
	private String type;//1 资讯 
	private String summary;// 摘要

	public History() {
		super();
	}
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dotime == null) ? 0 : dotime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((msgid == null) ? 0 : msgid.hashCode());
		result = prime * result + ((summary == null) ? 0 : summary.hashCode());
		result = prime * result + ((topic == null) ? 0 : topic.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		History other = (History) obj;
		if (dotime == null) {
			if (other.dotime != null)
				return false;
		} else if (!dotime.equals(other.dotime))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (msgid == null) {
			if (other.msgid != null)
				return false;
		} else if (!msgid.equals(other.msgid))
			return false;
		if (summary == null) {
			if (other.summary != null)
				return false;
		} else if (!summary.equals(other.summary))
			return false;
		if (topic == null) {
			if (other.topic != null)
				return false;
		} else if (!topic.equals(other.topic))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}



	public History(String msgid, String url, String topic, Date dotime,
			String type, String summary) {
		super();
		this.msgid = msgid;
		this.url = url;
		this.topic = topic;
		this.dotime = dotime;
		this.type = type;
		this.summary = summary;
	}

	public History(Long id, String msgid, String url, String topic,
			Date dotime, String type,  String summary) {
		super();
		this.id = id;
		this.msgid = msgid;
		this.url = url;
		this.topic = topic;
		this.dotime = dotime;
		this.type = type;
		this.summary = summary;
	}

	public History(String msgid, String url, String topic, Date dotime,
			String summary) {
		super();
		this.msgid = msgid;
		this.url = url;
		this.topic = topic;
		this.dotime = dotime;
		this.summary = summary;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
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

	public Date getDotime() {
		return dotime;
	}

	public void setDotime(Date dotime) {
		this.dotime = dotime;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}


}
