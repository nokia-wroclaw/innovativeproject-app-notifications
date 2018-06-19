package com.robinsonprogramming.www.loginrestapi;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity
{
    private Button button_login_login;
    private Button button_login_register;
    private EditText editText_login_username;
    private EditText editText_login_password;
    private String username;
    private String password;
    private String baseUrl;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Replace this with your own IP address or URL.
        baseUrl = "http://35.204.202.104:8080/api/v1.0/login/";





        editText_login_username = (EditText) findViewById(R.id.editText_login_username);
        editText_login_password = (EditText) findViewById(R.id.editText_login_password);

        button_login_login = (Button) findViewById(R.id.button_login_login);
        button_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    username = editText_login_username.getText().toString();
                    password = editText_login_password.getText().toString();
                    ApiAuthenticationClient apiAuthenticationClient = new ApiAuthenticationClient(username, password);
                    AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(apiAuthenticationClient);
                    execute.execute();
                }
                catch (Exception ex)
                {}
            }
        });
        button_login_register = (Button) findViewById(R.id.button_login_register);
        button_login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                goToRegistryActivity();
            }
        });

    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(getApplicationContext(), "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(getApplicationContext(), "portrait", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * This subclass handles the network operations in a new thread.
     * It starts the progress bar, makes the API call, and ends the progress bar.
     */
    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String>
    {

        private ApiAuthenticationClient apiAuthenticationClient;
        private String isValidCredentials;

        public ExecuteNetworkOperation(ApiAuthenticationClient apiAuthenticationClient)
        {
            this.apiAuthenticationClient = apiAuthenticationClient;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Display the progress bar.
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params)
        {
            try
            {
                isValidCredentials = apiAuthenticationClient.execute();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            if (apiAuthenticationClient.getResponse().toString()!=null && apiAuthenticationClient.getStatus().equals("200"))
            {
                try {
                    token = apiAuthenticationClient.getResponseAsJsonObject().getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                goToSecondActivity();
            }
            else
                {
                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void goToSecondActivity()
    {
        editText_login_username.getText().clear();
        editText_login_password.getText().clear();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        bundle.putString("token", token);

        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void goToRegistryActivity()
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
