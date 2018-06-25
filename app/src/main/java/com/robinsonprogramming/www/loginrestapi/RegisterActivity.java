package com.robinsonprogramming.www.loginrestapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mati.pojo.MyWebService;
import com.example.mati.pojo.User;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText editText_sing_in_name;
    private EditText editText_sing_in_surname;
    private EditText editText_sing_in_login;
    private EditText editText_sing_in_password;
    private EditText editText_sing_in_retype_password;
    private Button button_sing_up;
    private User user;
    RestAdapter retrofit;
    MyWebService myWebService;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editText_sing_in_name = (EditText)findViewById(R.id.editText);
        editText_sing_in_surname = (EditText) findViewById(R.id.editText2);
        editText_sing_in_login = (EditText)findViewById(R.id.editText3);
        editText_sing_in_password = (EditText)findViewById(R.id.editText4);
        editText_sing_in_retype_password = (EditText)findViewById(R.id.editText5);
        button_sing_up = (Button)findViewById(R.id.button);
        retrofit = new RestAdapter.Builder()
                .setEndpoint("http://35.204.202.104:8080/api/v1.0/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        myWebService = retrofit.create(MyWebService.class);
        button_sing_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(editText_sing_in_password.getText().toString().equals(editText_sing_in_retype_password.getText().toString()))
                {
                    user = new User(editText_sing_in_name.getText().toString(),editText_sing_in_surname.getText().toString(),editText_sing_in_login.getText().toString(),editText_sing_in_password.getText().toString());
                    myWebService.registryNewUser(user, new Callback<JSONObject>() {
                        @Override
                        public void success(JSONObject s, Response response)
                        {
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                            goToLogin();
                        }

                        @Override
                        public void failure(RetrofitError error)
                        {
                            Toast.makeText(getApplicationContext(), "Fail, User already exists", Toast.LENGTH_LONG).show();
                            goToLogin();
                        }
                    });
                    //System.out.println(user.getName() +" "+user.getSurname()+" "+user.getLogin()+" "+user.getPassword());

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Failed password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goToLogin()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
