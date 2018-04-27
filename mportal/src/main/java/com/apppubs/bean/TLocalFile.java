package com.apppubs.bean;

import java.util.Date;

import com.orm.SugarRecord;
/**
 * 本地文件
 * @author zhangwen
 * @since 2015-05-28
 *
 */
public class TLocalFile extends SugarRecord{

	public static int TYPE_UNKNOW = 0;
	public static int TYPE_PIC = 1;
	public static int TYPE_VIDEO = 2;
	public static int TYPE_SOUND = 3;
	public static int TYPE_DOCUMENT = 4;
	
	private String id;
	private String name;
	private String path;
	private String sourcePath;//来源地址
	private Date saveTime;//保存时间
	private int type;//类别
	private long size;
	
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public Date getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(Date saveTime) {
		this.saveTime = saveTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	

}
