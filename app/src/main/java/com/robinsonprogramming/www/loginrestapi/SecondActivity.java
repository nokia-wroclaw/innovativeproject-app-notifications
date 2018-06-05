package com.robinsonprogramming.www.loginrestapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.mati.pojo.MyWebService;
import com.example.mati.pojo.Notification;
import com.example.mati.pojo.Notifications;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SecondActivity extends AppCompatActivity {

    private static final String CLASS_TAG = "SecondActivity";

    RestAdapter retrofit;
    MyWebService myWebService;
    List<Notification> list;
    //Bundle bundle = getIntent().getExtras();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle bundle;
        bundle = getIntent().getExtras();
        String token = bundle.getString("token");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        retrofit = new RestAdapter.Builder()
                .setEndpoint("http://35.204.202.104:8080/api/v1.0/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        myWebService = retrofit.create(MyWebService.class);
        try {
            /*to działa jak wpiszesz zapytanie, teraz tylko wydobyć bundle reszta twoja plus zapisywanie offsetu*/

            myWebService.getData(0,bundle.getString("token"),new Callback<Notifications>() {
                @Override
                public void success(Notifications myWebServiceResponse, Response response)
                {
                    Log.d(CLASS_TAG, myWebServiceResponse.getNotifications().get(1).getTopic());
                    list = myWebServiceResponse.getNotifications();
                }

                @Override
                public void failure(RetrofitError error)
                {
                    Log.d(CLASS_TAG, error.getLocalizedMessage());
                }
            });

        } catch (Exception e) {
            Log.d(CLASS_TAG, e.toString());
        }
    }
}
