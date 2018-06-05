package com.example.mati.pojo;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

public interface MyWebService
{
    @Headers({"Accept: application/json"})
    @GET("/{endpoint}/")
    void getData(@Path("endpoint")String pEndpoint, Callback<Notifications> pResponse);
}
