package com.buzzware.bebelo.retrofit.DetailModel;

import com.google.gson.annotations.SerializedName;

public class BarWeekDayItem{

	@SerializedName("svalue")
	private long svalue;

	@SerializedName("weekDay")
	private String weekDay;

	@SerializedName("name")
	private String name;

	@SerializedName("evalue")
	private long evalue;

	public long getSvalue(){
		return svalue;
	}

	public String getWeekDay(){
		return weekDay;
	}

	public String getName(){
		return name;
	}

	public long getEvalue(){
		return evalue;
	}

	public void setSvalue(long svalue) {
		this.svalue = svalue;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEvalue(long evalue) {
		this.evalue = evalue;
	}
}