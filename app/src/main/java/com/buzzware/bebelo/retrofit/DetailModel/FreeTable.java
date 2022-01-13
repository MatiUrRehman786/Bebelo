package com.buzzware.bebelo.retrofit.DetailModel;

import com.google.gson.annotations.SerializedName;

public class FreeTable{

	@SerializedName("date")
	private long date;

	@SerializedName("freeTable")
	private boolean freeTable;

	public long getDate(){
		return date;
	}

	public boolean isFreeTable(){
		return freeTable;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public void setFreeTable(boolean freeTable) {
		this.freeTable = freeTable;
	}
}