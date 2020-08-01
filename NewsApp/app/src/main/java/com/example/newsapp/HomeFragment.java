package com.example.newsapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    ArrayList<String> img_arr = new ArrayList<String>();
    ArrayList<String> title_arr = new ArrayList<String>();
    ArrayList<String> section_arr = new ArrayList<String>();
    ArrayList<String> time_arr = new ArrayList<String>();
    ArrayList<String> news_id_arr = new ArrayList<String>();
    ArrayList<String> web_url_arr = new ArrayList<String>();
    ProgressBar progressBar;
    TextView progresstext;
    RecyclerView recyclerView;
    RecycleAdapter recycleAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @RequiresApi(api = Build.VERSION_CODES.O)
    static long dateTimeDifference(Temporal d1, Temporal d2, ChronoUnit unit){
        return unit.between(d1, d2);
    }
    @Override
    public void onResume() {
        super.onResume();
        if(recycleAdapter!=null){
            recycleAdapter.notifyDataSetChanged();
        }

    }
    public void get_news_data(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String urlnew = "https://android-backend-275002.wl.r.appspot.com/api";
        final StringRequest jsonObjectRequestnew = new StringRequest(urlnew, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject response_obj = object.getJSONObject("response");
                    JSONArray results = response_obj.getJSONArray("results");
                    for(int i=0;i<=Math.min(10,results.length()-1);i++){
                        JSONObject cur_news = results.getJSONObject(i);
                        String title = cur_news.getString("webTitle");
                        String time = cur_news.getString("webPublicationDate");
                        String section = cur_news.getString("sectionName");
                        String newsid = cur_news.getString("id");
                        String weburl = cur_news.getString("webUrl");
                        JSONObject fields = cur_news.getJSONObject("fields");
                        String thumbnail ="";
                        try {
                            thumbnail = fields.getString("thumbnail");
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
                    recycleAdapter = new RecycleAdapter(getActivity(),img_arr,title_arr,section_arr,time_arr,news_id_arr,web_url_arr);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_fragment,container,false);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                img_arr.removeAll(img_arr);
                title_arr.removeAll(time_arr);
                section_arr.removeAll(section_arr);
                time_arr.removeAll(time_arr);
                news_id_arr.removeAll(news_id_arr);
                web_url_arr.removeAll(web_url_arr);
                get_news_data();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        progressBar = view.findViewById(R.id.progress);
        progresstext = view.findViewById(R.id.progress_text_view);
        Bundle arguments = getArguments();
        Log.d("arguments", String.valueOf(arguments));
        TextView city_id = view.findViewById(R.id.city);
        city_id.setText(arguments.getString("city"));

        TextView state_id =view.findViewById(R.id.state);
        state_id.setText(arguments.getString("state"));

        TextView temp_id = view.findViewById(R.id.temperature);
        temp_id.setText(arguments.getString("temperature")+ "°C");

        TextView weather_id = view.findViewById(R.id.summary);
        weather_id.setText(arguments.getString("summary"));


        Resources res = getActivity().getResources();
        String summary = arguments.getString("summary");
        Drawable background_img=null;
        if(summary.toLowerCase().equals("clouds")){
            background_img = ResourcesCompat.getDrawable(res, R.drawable.clouds, null); ;
        }
        else if(summary.toLowerCase().equals("clear")){
            background_img = ResourcesCompat.getDrawable(res, R.drawable.clear, null);
        }
        else if(summary.toLowerCase().equals("snow")){
            background_img = ResourcesCompat.getDrawable(res, R.drawable.snow, null);
        }
        else if(summary.toLowerCase().equals("rain") || summary.toLowerCase().equals("drizzle")){
            background_img = ResourcesCompat.getDrawable(res, R.drawable.rain, null);
        }
        else if(summary.toLowerCase().equals("thunderstorm")){
            background_img = ResourcesCompat.getDrawable(res, R.drawable.thunder, null);
        }
        else{
            background_img = ResourcesCompat.getDrawable(res, R.drawable.sunny, null);
        }


        ImageView img_id = view.findViewById(R.id.image_view);
        img_id.setBackground(background_img);

        recyclerView = view.findViewById(R.id.recyclerViewList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        get_news_data();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

}
