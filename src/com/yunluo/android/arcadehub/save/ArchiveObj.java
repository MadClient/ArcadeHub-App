package com.yunluo.android.arcadehub.save;

public class ArchiveObj {
	
	private String name = null;
	private String desc = null;
	private String time = null;
	private int count = 0;
	
	private long date = 0;
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDesc() {
		return desc;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	public String getTime() {
		return time;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public long getDate() {
		return date;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
