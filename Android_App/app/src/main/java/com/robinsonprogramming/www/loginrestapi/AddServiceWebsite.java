package com.robinsonprogramming.www.loginrestapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mati.pojo.AddWebsiteBody;
import com.example.mati.pojo.ChangeFlagBody;
import com.example.mati.pojo.MyWebService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddServiceWebsite extends AppCompatActivity {

    private Button button;
    private EditText editText;
    private RestAdapter retrofit;
    private MyWebService myWebService;
    private StringBuffer response1 = new StringBuffer();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final Bundle bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_website);
        retrofit = new RestAdapter.Builder()
                .setEndpoint("http://35.204.202.104:8080/api/v1.0/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        myWebService = retrofit.create(MyWebService.class);

        button = (Button)findViewById(R.id.button2AddWebsite);
        editText = (EditText)findViewById(R.id.editTextAddWebsite);

       // button.setOnClickListener();
    }
}
