package com.buzzware.bebelo.retrofit.Login;

import com.google.gson.annotations.SerializedName;

public class Result{

	@SerializedName("cphone")
	private String cphone;

	@SerializedName("imge_url")
	private String imgeUrl;

	@SerializedName("blat")
	private String blat;

	@SerializedName("bname")
	private String bname;

	@SerializedName("baddress")
	private String baddress;

	@SerializedName("cname")
	private String cname;

	@SerializedName("id")
	private String id;

	@SerializedName("cemail")
	private String cemail;

	@SerializedName("blng")
	private String blng;

	@SerializedName("cpassword")
	private String cpassword;

	@SerializedName("bdetail")
	private String bdetail;

	public String getCphone(){
		return cphone;
	}

	public String getImgeUrl(){
		return imgeUrl;
	}

	public String getBlat(){
		return blat;
	}

	public String getBname(){
		return bname;
	}

	public String getBaddress(){
		return baddress;
	}

	public String getCname(){
		return cname;
	}

	public String getId(){
		return id;
	}

	public String getCemail(){
		return cemail;
	}

	public String getBlng(){
		return blng;
	}

	public String getCpassword(){
		return cpassword;
	}

	public String getBdetail(){
		return bdetail;
	}

	public void setBdetail(String bdetail) {
		this.bdetail = bdetail;
	}


}