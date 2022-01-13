package com.buzzware.bebelo.retrofit.DetailModel;

import com.google.gson.annotations.SerializedName;

public class BarHasItem{

	@SerializedName("isSelected")
	private boolean isSelected;

	@SerializedName("title")
	private String title;

	public boolean isIsSelected(){
		return isSelected;
	}

	public String getTitle(){
		return title;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}