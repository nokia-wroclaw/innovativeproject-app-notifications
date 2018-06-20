package com.example.mati.pojo;

import android.telecom.Call;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MyWebService
{
    @Headers({"Accept: application/json"})
    @GET("/not/part/")
    void getData(@Query("offset") int offset,@Query("token") String token, Callback<Notifications> pResponse);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/register/")
    void registryNewUser(@Body User user, Callback<String> pResponse);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/login/")
    void loginToSystem(@Body User user, Callback<String> pResponse);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/user/password/")
    void changeUserPassword(@Body ChangePasswordBody changePasswordBody, Callback<String> response);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/setFlag/")
    void setFlag(@Body ChangeFlagBody changeFlagBody, Callback<String> response);
}