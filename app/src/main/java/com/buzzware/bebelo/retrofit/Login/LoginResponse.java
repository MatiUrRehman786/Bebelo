package com.buzzware.bebelo.retrofit.Login;

import com.google.gson.annotations.SerializedName;

public class LoginResponse{

	@SerializedName("result")
	private Result result;

	@SerializedName("success")
	private int success;

	@SerializedName("message")
	private String message;

	public Result getResult(){
		return result;
	}

	public int getSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}