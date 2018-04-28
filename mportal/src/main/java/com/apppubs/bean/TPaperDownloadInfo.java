package com.apppubs.bean;

import java.util.Date;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * 已下载的期信息
 * @author Administrator
 *
 */
@Table(name="paper_download_info")
public class TPaperDownloadInfo extends SugarRecord {
	
//	public static final int STATUS_REQUESTING = 0;
//	public static final int STATUS_DOWNLOADING = 1;
//	public static final int STATUS_PARSING = 2;
//	public static final int STATUS_DOWNLOADED = 3;
//	
	
	
	private String issueId;//期 id
	private String paperName;//报纸名称
	private Date downloadTime;//下载时间
	private String issueName;//期名称
	private int size;//缓存大小
	private int curSize;//当前下载大小
	private String issueFirstCataloglocalPic;//图片
	private int status ;//当前状态
	private String zipUrl;//下载地址
	private String desDir;// 目的地目录
	private int isPaused;//是否暂停0==false 1 = true; 
	public String getIssueId() {
		return issueId;
	}
	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}
	public String getPaperName() {
		return paperName;
	}
	public void setPaperName(String paperName) {
		this.paperName = paperName;
	}
	
	public String getIssueName() {
		return issueName;
	}
	public void setIssueName(String issueName) {
		this.issueName = issueName;
	}
	public Date getDownloadTime() {
		return downloadTime;
	}
	public void setDownloadTime(Date downloadTime) {
		this.downloadTime = downloadTime;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getIssueFirstCataloglocalPic() {
		return issueFirstCataloglocalPic;
	}
	public void setIssueFirstCataloglocalPic(String issueFirstCataloglocalPic) {
		this.issueFirstCataloglocalPic = issueFirstCataloglocalPic;
	}
	public int getCurSize() {
		return curSize;
	}
	public void setCurSize(int curSize) {
		this.curSize = curSize;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getZipUrl() {
		return zipUrl;
	}
	public void setZipUrl(String zipUrl) {
		this.zipUrl = zipUrl;
	}
	public String getDesDir() {
		return desDir;
	}
	public void setDesDir(String desDir) {
		this.desDir = desDir;
	}
	public int getIsPaused() {
		return isPaused;
	}
	public void setIsPaused(int isPaused) {
		this.isPaused = isPaused;
	}
	@Override
	public String getId() {
		
		return null;
	}
	@Override
	public void setId(String id) {
	}
	
	
	
	
}
