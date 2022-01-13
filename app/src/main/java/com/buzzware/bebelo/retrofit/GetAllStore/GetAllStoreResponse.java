package com.buzzware.bebelo.retrofit.GetAllStore;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GetAllStoreResponse{

	@SerializedName("result")
	private List<ResultItem> result;

	@SerializedName("success")
	private String success;

	public List<ResultItem> getResult(){
		return result;
	}

	public String getSuccess(){
		return success;
	}
}