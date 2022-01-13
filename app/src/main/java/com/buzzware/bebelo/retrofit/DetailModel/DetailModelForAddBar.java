package com.buzzware.bebelo.retrofit.DetailModel;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DetailModelForAddBar {

	@SerializedName("barSupliment")
	private BarSupliment barSupliment;

	@SerializedName("barHas")
	private List<BarHasItem> barHas;

	@SerializedName("barBottle")
	private List<BarBottleItem> barBottle;

	@SerializedName("barAnounce")
	private BarAnounce barAnounce;

	@SerializedName("freeTable")
	private FreeTable freeTable;

	@SerializedName("barWeekDay")
	private List<BarWeekDayItem> barWeekDay;

	public BarSupliment getBarSupliment(){
		return barSupliment;
	}

	public List<BarHasItem> getBarHas(){
		return barHas;
	}

	public List<BarBottleItem> getBarBottle(){
		return barBottle;
	}

	public BarAnounce getBarAnounce(){
		return barAnounce;
	}

	public FreeTable getFreeTable(){
		return freeTable;
	}

	public List<BarWeekDayItem> getBarWeekDay(){
		return barWeekDay;
	}

	public void setBarSupliment(BarSupliment barSupliment) {
		this.barSupliment = barSupliment;
	}

	public void setBarHas(List<BarHasItem> barHas) {
		this.barHas = barHas;
	}

	public void setBarBottle(List<BarBottleItem> barBottle) {
		this.barBottle = barBottle;
	}

	public void setBarAnounce(BarAnounce barAnounce) {
		this.barAnounce = barAnounce;
	}

	public void setFreeTable(FreeTable freeTable) {
		this.freeTable = freeTable;
	}

	public void setBarWeekDay(List<BarWeekDayItem> barWeekDay) {
		this.barWeekDay = barWeekDay;
	}
}