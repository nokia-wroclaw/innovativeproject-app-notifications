package com.robinsonprogramming.www.loginrestapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mati.pojo.ChangePasswordBody;
import com.example.mati.pojo.MyWebService;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyAccount extends AppCompatActivity
{
    private EditText editText_password;
    private EditText editText_newPassword;
    private EditText editText_retype_newPassword;
    private Button button_change_password;
    RestAdapter retrofit;
    MyWebService myWebService;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final Bundle bundle;
        bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        editText_password = (EditText)findViewById(R.id.editText1myAccount);
        editText_newPassword = (EditText)findViewById(R.id.editText2myAccount);
        editText_retype_newPassword = (EditText)findViewById(R.id.editText3myAccount);
        button_change_password = (Button)findViewById(R.id.buttonMyAccount);
        retrofit = new RestAdapter.Builder()
                .setEndpoint("http://35.204.202.104:8080/api/v1.0/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        myWebService = retrofit.create(MyWebService.class);

        button_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                myWebService.changeUserPassword(new ChangePasswordBody("1", "pass1", "password"), new Callback<String>() {
                    @Override
                    public void success(String s, Response response)
                    {

                    }
                    @Override
                    public void failure(RetrofitError error)
                    {

                    }
                });
            }
        });

    }
}
