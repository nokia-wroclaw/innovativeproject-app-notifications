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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mati.pojo.MyWebService;
import com.example.mati.pojo.Notification;
import com.example.mati.pojo.Notifications;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

public class SecondActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CLASS_TAG = "SecondActivity";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    RestAdapter retrofit;
    MyWebService myWebService;
    ArrayList<Notification> list = new ArrayList<Notification>();
    List<String> topic;
    private ListView myListView;
    //Bundle bundle = getIntent().getExtras();
    int currentFirstVisibleItem = 0;
    int currentVisibleItemCount = 0;
    int totalItemCount = 0;
    int currentScrollState = 0;
    boolean loadingMore = false;
    Long startIndex = 0L;
    //int offset = 0;
    View footerView;
    private int pageCount = 0;

    FancyAdapter aa=null;
   // static ArrayList<String> resultRow;
    static List<String> resultRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        final Bundle bundle;
        bundle = getIntent().getExtras();
        String token = bundle.getString("token");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open,R.string.close);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.parseColor("#dcdcdc")));
        navigationView.setNavigationItemSelectedListener(this);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
//  z      Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        // setting Toolbar as Action Bar for the App
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setIcon(getDrawable(R.drawable.logo));

      //  toolbar.setNavigationIcon(R.drawable.good_day);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        retrofit = new RestAdapter.Builder()
                .setEndpoint("http://35.204.202.104:8080/api/v1.0/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        getDataFromUrl(bundle,list.size());
        ListView myListView = (ListView)findViewById(R.id.myListView);

        aa=new FancyAdapter();
        myListView.setAdapter(aa);
        // Creating a button - Load More
        Button btnLoadMore = new Button(this);
        btnLoadMore.setText("Load More");
        btnLoadMore.setBackgroundColor(Color.parseColor("#32e196"));

        btnLoadMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Starting a new async task
                getDataFromUrl(bundle,list.size());
              //  aa.notifyDataSetChanged();

            }

        });
        myListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arq0, View arg1, int position, long arg3) {


                TextView timestamp = (TextView) arg1.findViewById(R.id.timestamp);
                if(timestamp.getVisibility()==View.GONE)
                    timestamp.setVisibility(View.VISIBLE);
                else
                    timestamp.setVisibility(View.GONE);
                TextView message = (TextView) arg1.findViewById(R.id.message);
                if(message.getVisibility()==View.GONE)
                    message.setVisibility(View.VISIBLE);
                else
                    message.setVisibility(View.GONE);


            }

        });
      //  aa.notifyDataSetChanged();

       // myListView.deferNotifyDataSetChanged();
       // aa.notifyDataSetChanged();
        // Adding button to listview at footer
        myListView.addFooterView(btnLoadMore);
       // myListView.setOnScrollListener(onScrollListener(bundle));

        //     myListView.setOnScrollListener(onScrollListener());



    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_notifications:
                finish();
                startActivity(getIntent());
                break;
//            case R.id.nav_services:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ChatFragment()).commit();
//                break;
//            case R.id.nav_account:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ProfileFragment()).commit();
//                break;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mToggle.onOptionsItemSelected(item))
            return true;

    return super.onOptionsItemSelected(item);
    }
   /* private AbsListView.OnScrollListener onScrollListener(final Bundle bundle) {
        return new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int threshold = 1;
                int count = myListView.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (myListView.getLastVisiblePosition() >= count - threshold && pageCount < 2) {
                        Log.i("TAG", "loading more data");
                        // Execute LoadMoreDataTask AsyncTask
                        getDataFromUrl(bundle);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
            }

        };
    }
*/

    private void getDataFromUrl(Bundle bundle,int offset){
        myWebService = retrofit.create(MyWebService.class);
        try {

            myWebService.getData(offset,bundle.getString("token"),new Callback<Notifications>() {
                @Override
                public void success(Notifications myWebServiceResponse, Response response)
                {
                    //Log.d(CLASS_TAG, myWebServiceResponse.getNotifications().get(1).getTopic());
                    list.addAll(myWebServiceResponse.getNotifications());
                    //ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, list);
                    aa.notifyDataSetChanged();

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
 /*   private void getDataFromUrl(MyWebService myWebServiceyWebService) {
        new LoadCountriesFromUrlTask(this, url).execute();
    }*/
    class FancyAdapter extends ArrayAdapter<Notification>{
        FancyAdapter(){
            super(SecondActivity.this,R.layout.row,list);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView==null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView=inflater.inflate(R.layout.row,null);
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
        public TextView topic=null;
        public TextView message=null;
        public TextView timestamp=null;
        ViewHolder(View row)
        {
            topic=(TextView)row.findViewById(R.id.topic);
            message=(TextView)row.findViewById(R.id.message);
            timestamp=(TextView)row.findViewById(R.id.timestamp);
        }
        void populateFrom(Notification n)
        {
            topic.setText(n.getTopic());
            message.setText(n.getMessage());
            message.setVisibility(View.GONE);
            timestamp.setText(n.getTimestamp());
            timestamp.setVisibility(View.GONE);
        }
    }





}
