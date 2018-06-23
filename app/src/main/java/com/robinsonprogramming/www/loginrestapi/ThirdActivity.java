package com.robinsonprogramming.www.loginrestapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mati.pojo.ChangeFlagBody;
import com.example.mati.pojo.MyWebService;
import com.example.mati.pojo.Notification;
import com.example.mati.pojo.Notifications;
import com.example.mati.pojo.Service;
import com.example.mati.pojo.Services;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

public class ThirdActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private static final String CLASS_TAG = "ThirdActivity";
    JSONObject myobj;
    ArrayList<Service> list = new ArrayList<Service>();
    List<String> servicename;
    private DrawerLayout mDrawerLayout;
    ArrayAdapter<String> adapter;
    ArrayList<String> h = new ArrayList<>();


    MyWebService myWebService;
    RestAdapter retrofit;
    FancyAdapter aa=null;
    private Bundle bundle1;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        retrofit = new RestAdapter.Builder()
                .setEndpoint("http://35.204.202.104:8080/api/v1.0/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        final Bundle bundle;
        bundle = getIntent().getExtras();
        bundle1 = bundle;
        final String token = bundle.getString("token");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        Spinner spin = findViewById(R.id.aggregation_type1);
        ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(this,R.array.Aggregation_Type,android.R.layout.simple_spinner_item);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapt);
        spin.setOnItemSelectedListener(this);
        spin.setVisibility(View.VISIBLE);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout1);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open,R.string.close);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setItemIconTintList(null);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.parseColor("#dcdcdc")));
        navigationView.setNavigationItemSelectedListener(this);
        getDataFromUrl(bundle);
        ListView myListView = (ListView)findViewById(R.id.myListView1);
        aa=new FancyAdapter();
        myListView.setAdapter(aa);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();


        myListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arq0, View arg1, final int position, long arg3) {

               // TextView AggregationType=(TextView)arg1.findViewById(R.id.aggregation_type);
                TextView AggregateBy=(TextView)arg1.findViewById(R.id.aggregation_by);
                TextView AggregateInterval=(TextView)arg1.findViewById(R.id.aggregation_interval);
                TextView AggregateSubstring=(TextView)arg1.findViewById(R.id.aggregation_substring);






//                if(AggregationType.getVisibility()==View.GONE)
//                    AggregationType.setVisibility(View.VISIBLE);
//                else
//                    AggregationType.setVisibility(View.GONE);

                if(AggregateBy.getVisibility()==View.GONE)
                    AggregateBy.setVisibility(View.VISIBLE);
                else
                    AggregateBy.setVisibility(View.GONE);

                if(AggregateInterval.getVisibility()==View.GONE)
                    AggregateInterval.setVisibility(View.VISIBLE);
                else
                    AggregateInterval.setVisibility(View.GONE);

                if(AggregateSubstring.getVisibility()==View.GONE)
                    AggregateSubstring.setVisibility(View.VISIBLE);
                else
                    AggregateSubstring.setVisibility(View.GONE);


            }

        });
        myListView.invalidateViews();

        aa.notifyDataSetChanged();

        // myListView.deferNotifyDataSetChanged();
        // aa.notifyDataSetChanged();
        // Adding button to listview at footer



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_notifications:
                goToMainActivity();
                break;
            case R.id.nav_services:
                finish();
                startActivity(getIntent());
                break;
            case R.id.nav_account:
                goToMyAccount();
                break;
//            case R.id.settings:
//                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.nav_logout:
                SharedPreferences sp = getSharedPreferences("MYKEY",0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username" , "username");
                editor.putString("password" , "password");

                finish();

                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void goToMyAccount()
    {
        Intent intent = new Intent(this, MyAccount.class);
        intent.putExtras(bundle1);
        startActivity(intent);
    }

    private void goToMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(bundle1);
        startActivity(intent);
    }

    private void getDataFromUrl(Bundle bundle){
        myWebService = retrofit.create(MyWebService.class);

        try {

            myWebService.getUserAccount(new ChangeFlagBody(bundle.getString("token"), ""),new Callback<Services>() {
                @Override
                public void success(Services myWebServiceResponse, Response response)
                {

                    BufferedReader in = null;
                    StringBuffer response1 = new StringBuffer();
                    try {
                        in = new BufferedReader(new InputStreamReader(response.getBody().in()));

                        String inputLine;
                        while ((inputLine = in.readLine()) != null)
                        {
                            response1.append(inputLine);
                        }
                        myobj= new JSONObject(String.valueOf(response1));
                        JSONArray jsonArray = myobj.getJSONArray("accounts");
                        // ObjectMapper mapper = new ObjectMapper(jsonArray,list);
                        Gson gson = new Gson();
                        for(int i=0; i<jsonArray.length();i++) {
                            Service s = gson.fromJson(jsonArray.get(i).toString(),Service.class);
                            list.add(s);
                        }
                        aa.notifyDataSetChanged();


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //response1.append(response.getBody().in());
                    System.out.println(myobj.toString());

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String txt = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(),txt,Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    class FancyAdapter extends ArrayAdapter<Service>{
        FancyAdapter(){
            super(ThirdActivity.this,R.layout.row2,list);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView==null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView=inflater.inflate(R.layout.row2,null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
                //  aa.notifyDataSetChanged();

            }
            else
            {
                holder =(ViewHolder)convertView.getTag();
                //  aa.notifyDataSetChanged();

            }
            holder.populateFrom(list.get(position));
            return (convertView);
        }
    }
    class ViewHolder{
        public TextView servicename=null;
        public TextView AggregationType=null;
        public TextView AggregateBy=null;
        public TextView AggregateInterval=null;
        public TextView AggregateSubstring=null;
        String Interval = "";
        String Substring = "";

        //  ImageButton del_image;
        Button del_image;

        ViewHolder(View row)
        {

            // del_image=(ImageButton)row.findViewById(R.id.removeButton);
            servicename=(TextView)row.findViewById(R.id.service_name);
          //  AggregationType=(TextView)row.findViewById(R.id.aggregation_type);
            AggregateBy=(TextView)row.findViewById(R.id.aggregation_by);
            AggregateInterval=(TextView)row.findViewById(R.id.aggregation_interval);
            AggregateSubstring=(TextView)row.findViewById(R.id.aggregation_substring);



        }
        void populateFrom(Service s)
        {
            // del_image.setVisibility(View.VISIBLE);

            if(s.getSourceID().equals(10))
                servicename.setText("Custom Website");
            if(s.getSourceID().equals(15))
                servicename.setText("Twitter");
            if(!s.getSourceID().equals(10)&&!s.getSourceID().equals(15))
                servicename.setText("Custom Service");
          //  if(s.getAggregationtype().equals(0)) //none first last count
//            AggregationType.setText("none");
//            if(s.getAggregationtype().equals(1)) //none first last count
//                AggregationType.setText("first");
//            if(s.getAggregationtype().equals(2)) //none first last count
//                AggregationType.setText("last");
//            if(s.getAggregationtype().equals(3)) //none first last count
//                AggregationType.setText("count");
//            AggregationType.setVisibility(View.GONE);
            if(s.getAggregation().equals(1))
                AggregateBy.setText("Topic");
            if(s.getAggregation().equals(2))
                AggregateBy.setText("Message");
            if(!s.getAggregation().equals(1)&&!s.getAggregation().equals(2))
                AggregateBy.setText("Aggregate By");
            AggregateBy.setVisibility(View.GONE);
            Interval = s.getAggregationdate().toString();
            Substring = s.getAggregationkey();
            AggregateInterval.setText(Interval);
            AggregateInterval.setVisibility(View.GONE);
            AggregateSubstring.setText(Substring);
            AggregateSubstring.setVisibility(View.GONE);
        }
    }

}
