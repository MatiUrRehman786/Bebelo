package com.buzzware.bebelo.retrofit.DetailModel;

import com.google.gson.annotations.SerializedName;

public class BarBottleItem{

	@SerializedName("drinkImage")
	private String drinkImage;

	@SerializedName("drinkPrice")
	private String drinkPrice;

	@SerializedName("drinkName")
	private String drinkName;

	public String getDrinkImage(){
		return drinkImage;
	}

	public String getDrinkPrice(){
		return drinkPrice;
	}

	public String getDrinkName(){
		return drinkName;
	}

	public void setDrinkImage(String drinkImage) {
		this.drinkImage = drinkImage;
	}

	public void setDrinkPrice(String drinkPrice) {
		this.drinkPrice = drinkPrice;
	}

	public void setDrinkName(String drinkName) {
		this.drinkName = drinkName;
	}
}