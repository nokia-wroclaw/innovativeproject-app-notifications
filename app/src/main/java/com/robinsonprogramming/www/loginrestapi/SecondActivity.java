package com.robinsonprogramming.www.loginrestapi;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

public class SecondActivity extends AppCompatActivity {

    private static final String CLASS_TAG = "SecondActivity";

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
    int offset = 0;
    View footerView;
    private int pageCount = 0;

    FancyAdapter aa=null;
   // static ArrayList<String> resultRow;
    static List<String> resultRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle bundle;
        bundle = getIntent().getExtras();
        String token = bundle.getString("token");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        retrofit = new RestAdapter.Builder()
                .setEndpoint("http://35.204.202.104:8080/api/v1.0/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        getDataFromUrl(bundle);
        ListView myListView = (ListView)findViewById(R.id.myListView);

        aa=new FancyAdapter();
        myListView.setAdapter(aa);
       // myListView.setOnScrollListener(onScrollListener(bundle));

        //     myListView.setOnScrollListener(onScrollListener());


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
    private void getDataFromUrl(Bundle bundle){
        myWebService = retrofit.create(MyWebService.class);
        try {

            myWebService.getData(offset,bundle.getString("token"),new Callback<Notifications>() {
                @Override
                public void success(Notifications myWebServiceResponse, Response response)
                {
                    Log.d(CLASS_TAG, myWebServiceResponse.getNotifications().get(1).getTopic());
                    list.addAll(myWebServiceResponse.getNotifications());
                    offset+=10;
                    //ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, list);
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
            super(SecondActivity.this,android.R.layout.simple_list_item_1,list);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView==null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView=inflater.inflate(R.layout.row,null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            else
            {
                holder =(ViewHolder)convertView.getTag();
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
            timestamp.setText(n.getTimestamp());
        }
    }





}
