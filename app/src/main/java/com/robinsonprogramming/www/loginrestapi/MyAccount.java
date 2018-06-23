package com.robinsonprogramming.www.loginrestapi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mati.pojo.ChangeFlagBody;
import com.example.mati.pojo.ChangePasswordBody;
import com.example.mati.pojo.MyWebService;
import com.example.mati.pojo.RemoveAccountBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.ResponseCallback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyAccount extends AppCompatActivity
{
    private EditText editText_password;
    private EditText editText_newPassword;
    private EditText editText_retype_newPassword;
    private Button button_change_password;
    private Button button_remove_account;

    RestAdapter retrofit;
    private Bundle bundle1;

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
        button_remove_account=(Button)findViewById(R.id.buttonRemoveAccount);
        retrofit = new RestAdapter.Builder()
                .setEndpoint("http://35.204.202.104:8080/api/v1.0/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        myWebService = retrofit.create(MyWebService.class);

        button_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!editText_newPassword.getText().toString().equals(editText_retype_newPassword.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(), "New Passwords is different", Toast.LENGTH_LONG).show();
                }
                else {
                    myWebService.changeUserPassword(new ChangePasswordBody(bundle.getString("token"), editText_password.getText().toString(), editText_newPassword.getText().toString()), new Callback<JSONObject>() {
                        @Override
                        public void success(JSONObject s, Response response)
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
            }
        });

        button_remove_account.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                AlertDialog.Builder theDialog = new AlertDialog.Builder(MyAccount.this);

                theDialog.setMessage("Are You sure")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myWebService.removeAccount(new RemoveAccountBody(bundle.getString("token"), editText_password.getText().toString()), new Callback<JSONObject>() {
                                    @Override
                                    public void success(JSONObject s, Response response)
                                    {
                                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                        SharedPreferences sp = getSharedPreferences("MYKEY",0);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("username" , "username");
                                        editor.putString("password" , "password");
                                        goToMainActivity();
                                    }
                                    @Override
                                    public void failure(RetrofitError error)
                                    {
                                        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        })
                        .setNegativeButton("Cancel",null);

                AlertDialog alert = theDialog.create();
                alert.show();

            }
        });

    }

    private void goToMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }
}
