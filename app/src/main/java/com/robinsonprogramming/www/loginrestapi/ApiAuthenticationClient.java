package com.robinsonprogramming.www.loginrestapi;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiAuthenticationClient
{
    private String username;
    private String password;
    private String httpMethod;
    private String status;
    private StringBuffer response;

    public ApiAuthenticationClient(String username, String password)
    {
        this.username = username;
        this.password = password;
        this.httpMethod = "POST";
        this.response =  new StringBuffer();
        this.status = "";
        System.setProperty("jsse.enableSNIExtension", "false");
    }

    public ApiAuthenticationClient setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public ApiAuthenticationClient clearAll()
    {
        this.username = "";
        this.password = "";
        this.httpMethod = "";
        this.status = "";
        return this;
    }

    public StringBuffer getResponse()
    {
        return response;
    }

    public String getStatus()
    {
        return status;
    }

    public JSONObject getResponseAsJsonObject()
    {
        try
        {
            return new JSONObject(String.valueOf(response));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String execute()
    {
        try
        {
            URL url = new URL("http://35.204.202.104:8080/api/v1.0/login/");
            String b = "{\"login\":"+"\""+username+"\""+",\"password\":"+"\""+password+"\""+"}";
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept","application/json");
            connection.setRequestMethod(httpMethod);
            connection.setDoOutput(true);
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(b);
            os.flush();
            os.close();
            Log.i("STATUS", String.valueOf(connection.getResponseCode()));
            Log.i("response",connection.getResponseMessage());
            Log.i("MSG", connection.getInputStream().toString());
            status = String.valueOf(connection.getResponseCode());
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());

            connection.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return response.toString();
    }
}
