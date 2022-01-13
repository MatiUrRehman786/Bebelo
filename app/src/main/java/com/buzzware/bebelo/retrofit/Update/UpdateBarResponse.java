package com.buzzware.bebelo.retrofit.Update;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateBarResponse {

	@SerializedName("result")
	private ResultItem result;

	@SerializedName("success")
	private String success;

	@SerializedName("message")
	private String message;

	public ResultItem getResult(){
		return result;
	}

	public String getSuccess(){
		return success;
	}

	public void setResult(ResultItem result) {
		this.result = result;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}