package com.example.newsapp;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScienceFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<String> img_arr = new ArrayList<String>();
    ArrayList<String> title_arr = new ArrayList<String>();
    ArrayList<String> section_arr = new ArrayList<String>();
    ArrayList<String> time_arr = new ArrayList<String>();
    ArrayList<String> news_id_arr = new ArrayList<String>();
    ArrayList<String> web_url_arr = new ArrayList<String>();
    ProgressBar progressBar;
    RecycleAdapter recycleAdapter;
    TextView progresstext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public ScienceFragment() {
        // Required empty public constructor
    }
    @Override
    public void onResume() {
        super.onResume();
        if(recycleAdapter!=null){
            recycleAdapter.notifyDataSetChanged();
        }

    }
    public void  get_world_news(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String urlnew = "https://android-backend-275002.wl.r.appspot.com/science";
        final StringRequest jsonObjectRequestnew = new StringRequest(urlnew, new Response.Listener<String>() {
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
                        String section = cur_news.getString("sectionName");
                        String weburl = cur_news.getString("webUrl");
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_world, container, false);

        recyclerView = view.findViewById(R.id.headlines_recyclerViewList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = view.findViewById(R.id.progress);
        progresstext = view.findViewById(R.id.progress_text_view);
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
                get_world_news();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        get_world_news();
        return view;
    }
}
