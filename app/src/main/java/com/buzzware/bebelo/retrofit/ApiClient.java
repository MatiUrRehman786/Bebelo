package com.buzzware.bebelo.retrofit;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiClient {

    // payment api

    @Headers("Accept: application/json")
    @POST("api/app_register")
    Call<String> addBar(@Body RequestBody body);

    @Headers("Accept: application/json")
    @GET("api/getallsstore")
    Call<String> getAllStore(@Query("a") int id);

    @Headers("Accept: application/json")
    @POST("api/app_login")
    Call<String> appLogin(@Body RequestBody body);

    @Headers("Accept: application/json")
    @POST("api/updatestore")
    Call<String> updateBar(@Body RequestBody body);

    @Headers("Accept: application/json")
    @POST("api/deleteaccount")
    Call<String> deleteBar(@Body RequestBody body);

}
