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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myWebService.getUserAccount(new ChangeFlagBody("1", " "), new Callback<JSONObject>() {
                    @Override
                    public void success(JSONObject jsonObject, Response response) {
                        BufferedReader in = null;
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
                            //response1.append(response.getBody().in());
                        System.out.println(response1);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
                /*myWebService.addWebsiteToWatch(new AddWebsiteBody(bundle.getString("token"), editText.getText().toString()), new Callback<JSONObject>() {
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
                });*/
            }
        });
    }
}
