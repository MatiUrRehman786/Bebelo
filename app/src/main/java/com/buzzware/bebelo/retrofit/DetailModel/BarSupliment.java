package com.buzzware.bebelo.retrofit.DetailModel;

import com.google.gson.annotations.SerializedName;

public class BarSupliment{


	@SerializedName("isSupliment")
	private boolean isSupliment;



	public boolean isIsSupliment(){
		return isSupliment;
	}
	public void setSupliment(boolean supliment) {
		isSupliment = supliment;
	}
}