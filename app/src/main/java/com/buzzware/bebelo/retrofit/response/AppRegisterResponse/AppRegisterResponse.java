package com.buzzware.bebelo.retrofit.response.AppRegisterResponse;

import com.google.gson.annotations.SerializedName;

public class AppRegisterResponse{

	@SerializedName("result")
	private Result result;

	@SerializedName("success")
	private String success;

	@SerializedName("message")
	private String message;

	public Result getResult(){
		return result;
	}

	public String getSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}
}