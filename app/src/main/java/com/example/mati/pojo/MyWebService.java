package com.example.mati.pojo;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MyWebService
{
    @Headers({"Accept: application/json"})
    @GET("/not/part/")
    void getData(@Query("offset") int offset,@Query("token") String token, Callback<Notifications> pResponse);
}
