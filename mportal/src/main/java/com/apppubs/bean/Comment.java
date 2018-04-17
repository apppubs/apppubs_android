package com.apppubs.bean;

import java.util.Date;

public class Comment {
	/**
	 * "cid": "1422085030366", "cname": "", "cdate": "2015-01-20 13:47:10.0",
	 * "content": "123"
	 */
	private Date cdate;
	private String cid;
	private String cname;
	private String content;
	private String ipwhere;
	private String commentnum;// 评论数
	private String upnum;// 赞
	private String downnum;// 踩

	public Comment() {
		super();
	}

	public Comment(String cid, String cname, Date time, String content,
			String ipwhere) {
		super();
		this.cdate = time;
		this.cid = cid;
		this.cname = cname;
		this.content = content;
		this.ipwhere = ipwhere;
	}

	public Comment(String commentnum, String upnum, String downnum) {
		super();
		this.commentnum = commentnum;
		this.upnum = upnum;
		this.downnum = downnum;
	}


	public Date getCdate() {
		return cdate;
	}

	public void setCdate(Date cdate) {
		this.cdate = cdate;
	}

	public String getIpwhere() {
		return ipwhere;
	}

	public String getCommentnum() {
		return commentnum;
	}

	public void setCommentnum(String commentnum) {
		this.commentnum = commentnum;
	}

	public String getUpnum() {
		return upnum;
	}

	public void setUpnum(String upnum) {
		this.upnum = upnum;
	}

	public String getDownnum() {
		return downnum;
	}

	public void setDownnum(String downnum) {
		this.downnum = downnum;
	}

	public void setIpwhere(String ipwhere) {
		this.ipwhere = ipwhere;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cid == null) ? 0 : cid.hashCode());
		result = prime * result + ((cname == null) ? 0 : cname.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((cdate == null) ? 0 : cdate.hashCode());
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
		Comment other = (Comment) obj;
		if (cid == null) {
			if (other.cid != null)
				return false;
		} else if (!cid.equals(other.cid))
			return false;
		if (cname == null) {
			if (other.cname != null)
				return false;
		} else if (!cname.equals(other.cname))
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (cdate == null) {
			if (other.cdate != null)
				return false;
		} else if (!cdate.equals(other.cdate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Commment [data=" + cdate + ", cid=" + cid + ", cname=" + cname
				+ ", content=" + content + "]";
	}

}
