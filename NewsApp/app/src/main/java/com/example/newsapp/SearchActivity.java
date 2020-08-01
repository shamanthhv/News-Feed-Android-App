package com.example.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    String searchterm;
    RecyclerView recyclerView;
    ArrayList<String> img_arr = new ArrayList<String>();
    ArrayList<String> title_arr = new ArrayList<String>();
    ArrayList<String> section_arr = new ArrayList<String>();
    ArrayList<String> time_arr = new ArrayList<String>();
    ArrayList<String> news_id_arr = new ArrayList<String>();
    ArrayList<String> web_url_arr = new ArrayList<String>();
    Context context = this;
    ProgressBar progressBar;
    TextView progresstext;
    RecycleAdapter recycleAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onResume() {
        super.onResume();
        if(recycleAdapter!=null){
            recycleAdapter.notifyDataSetChanged();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_world);
        recyclerView = findViewById(R.id.headlines_recyclerViewList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progress);
        progresstext = findViewById(R.id.progress_text_view);
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                img_arr.removeAll(img_arr);
                title_arr.removeAll(time_arr);
                section_arr.removeAll(section_arr);
                time_arr.removeAll(time_arr);
                news_id_arr.removeAll(news_id_arr);
                web_url_arr.removeAll(web_url_arr);
                makeapicall(searchterm);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSearchterm();
    }
    private void getSearchterm(){
        if(getIntent().hasExtra("searchid")){
            String searchid = getIntent().getStringExtra("searchid");
            searchterm = searchid;
            makeapicall(searchid);

        }
    }
    private void makeapicall(String searchid){
        RequestQueue queue = Volley.newRequestQueue(this);
        String action_title = "Search Results for "+ searchterm;
        getSupportActionBar().setTitle(action_title);
        String url = "https://android-backend-275002.wl.r.appspot.com/search?searchkey="+searchid;
        Log.d("url",url);
        final StringRequest jsonObjectRequestnew = new StringRequest(url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                try {
                    img_arr.removeAll(img_arr);
                    title_arr.removeAll(title_arr);
                    section_arr.removeAll(section_arr);
                    time_arr.removeAll(time_arr);
                    news_id_arr.removeAll(news_id_arr);
                    JSONObject object = new JSONObject(response);
                    JSONObject response_obj = object.getJSONObject("response");
                    Log.d("resp",response);
                    JSONArray results = response_obj.getJSONArray("results");
                    for(int i=0;i<=Math.min(10,results.length()-1);i++){
                        JSONObject cur_news = results.getJSONObject(i);
                        String title = cur_news.getString("webTitle");
                        String time = cur_news.getString("webPublicationDate");
                        String weburl = cur_news.getString("webUrl");
                        String section = cur_news.getString("sectionName");
                        String newsid = cur_news.getString("id");
                        String thumbnail = "";
                        try {
                            JSONObject blocks = cur_news.getJSONObject("blocks");
                            JSONObject main = blocks.getJSONObject("main");
                            JSONArray elements = main.getJSONArray("elements");
                            JSONObject cur_obj = elements.getJSONObject(0);
                            JSONArray assets = cur_obj.getJSONArray("assets");
                            JSONObject lastobj = assets.getJSONObject(assets.length() - 1);
                            thumbnail = lastobj.getString("file");
                            if(thumbnail==null || thumbnail.equals("")){
                                thumbnail = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                            }
                        }catch (JSONException e){
                            thumbnail = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                        }

                        img_arr.add(thumbnail);
                        title_arr.add(title);
                        section_arr.add(section);

                        time_arr.add(time);
                        news_id_arr.add(newsid);
                        web_url_arr.add(weburl);

                    }
                    progressBar.setVisibility(View.GONE);
                    progresstext.setVisibility(View.GONE);
                    recycleAdapter = new RecycleAdapter(context,img_arr,title_arr,section_arr,time_arr,news_id_arr,web_url_arr);
                    recyclerView.setAdapter(recycleAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequestnew);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
