package com.buzzware.bebelo.retrofit.DetailModel;

import com.google.gson.annotations.SerializedName;

public class BarAnounce{

	@SerializedName("date")
	private long date;

	@SerializedName("name")
	private String name;

	public long getDate(){
		return date;
	}

	public String getName(){
		return name;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public void setName(String name) {
		this.name = name;
	}
}