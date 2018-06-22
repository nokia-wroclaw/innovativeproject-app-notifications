package com.example.mati.pojo;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

public interface MyWebService
{
    @Headers({"Accept: application/json"})
    @GET("/not/part/")
    void getData(@Query("offset") int offset,@Query("token") String token, Callback<Notifications> pResponse);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/accounts/")
    void getUserAccount(@Body ChangeFlagBody changeFlagBody, Callback<Services> response);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/register/")
    void registryNewUser(@Body User user, Callback<String> pResponse);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/login/")
    void loginToSystem(@Body User user, Callback<String> pResponse);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/user/password/")
    void changeUserPassword(@Body ChangePasswordBody changePasswordBody, Callback<JSONObject> response);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/setFlag/")
    void setFlag(@Body ChangeFlagBody changeFlagBody, Callback<String> response);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/notf/remove/")
    void removeNotification(@Body ChangeFlagBody changeFlagBody, Callback<JSONObject> response);


    
//    @Headers({"Content-Type: application/json","Accept: application/json"})
//    @POST("/accounts/")
//    void getUserAccount(@Body ChangeFlagBody changeFlagBody, Callback<JSONObject> response);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/new/twitter/confirm/")
    void addTwitterAccount(@Body AddServiceBody addServiceBody, Callback<JSONObject> response);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/new/website/")
    void addWebsiteToWatch(@Body AddWebsiteBody addWebsiteBody, Callback<JSONObject> response);
}
