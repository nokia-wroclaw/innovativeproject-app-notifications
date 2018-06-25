package com.robinsonprogramming.www.loginrestapi;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mati.pojo.AddServiceBody;
import com.example.mati.pojo.AddWebsiteBody;
import com.example.mati.pojo.MyWebService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddServiceSecond extends AppCompatActivity
{
    private Button button1;
    private Button button2;
    private EditText editText;
    private RestAdapter retrofit;
    private MyWebService myWebService;
    private StringBuffer response1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final Bundle bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_second);
        button1 = (Button)findViewById(R.id.button1Add);
        editText = (EditText)findViewById(R.id.editTextAddServices);
        button2 = (Button)findViewById(R.id.button2Add);

        retrofit = new RestAdapter.Builder()
                .setEndpoint("http://35.204.202.104:8080/api/v1.0/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        myWebService = retrofit.create(MyWebService.class);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                myWebService.getLink(new AddWebsiteBody("1", " "), new Callback<JSONObject>() {
                    @Override
                    public void success(JSONObject jsonObject, Response response) {
                        BufferedReader in = null;
                        response1 = new StringBuffer();
                        try {
                            in = new BufferedReader(new InputStreamReader(response.getBody().in()));


                        String inputLine;
                            while ((inputLine = in.readLine()) != null)
                            {
                                response1.append(inputLine);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String url = response1.substring(11,response1.length()-2);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("Fail");
                    }
                });

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                myWebService.addTwitterAccount(new AddServiceBody(bundle.getString("token"), editText.getText().toString()), new Callback<JSONObject>() {
                    @Override
                    public void success(JSONObject jsonObject, Response response)
                    {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(RetrofitError error)
                    {
                        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
