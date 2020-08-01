package com.example.newsapp;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    String image,title,section,date_time,description,weburl,article_id,time_cookie;
    Context context = this;
    ProgressBar progressBar;
    TextView progresstext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detaillayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progress);
        progresstext = findViewById(R.id.progress_text_view);
        getArticleid();
    }
    private void getArticleid(){
        if(getIntent().hasExtra("articleid")){
            String articleid = getIntent().getStringExtra("articleid");
            article_id = articleid;
            makeapicall(articleid);

        }
    }
    private void makeapicall(String articleid){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://android-backend-275002.wl.r.appspot.com/detail?id="+articleid;
        Log.d("url",url);
        final StringRequest jsonObjectRequest = new StringRequest(url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject responseobj = object.getJSONObject("response");
                    JSONObject cur_news = responseobj.getJSONObject("content");
                    Log.d("resp",response);
                    title = cur_news.getString("webTitle");
                    getSupportActionBar().setTitle(title);
                    String time = cur_news.getString("webPublicationDate");
                    time_cookie =time;
                    ZonedDateTime pubtime = ZonedDateTime.parse(time);
                    ZoneId pubtime1 = ZoneId.of("America/Los_Angeles");
                    ZonedDateTime zoneLA = pubtime.withZoneSameInstant(pubtime1);
                    DateTimeFormatter date = DateTimeFormatter.ofPattern("dd MMM YYYY");
                    String outdate = date.format(zoneLA);
                    date_time =outdate;
                    section = cur_news.getString("sectionName");
                    weburl = cur_news.getString("webUrl");
                    String thumbnail = "";
                    try {
                        JSONObject blocks = cur_news.getJSONObject("blocks");
                        description = "";
                        JSONArray body = blocks.getJSONArray("body");
                        for(int i=0;i<body.length();i++){
                            JSONObject cur = body.getJSONObject(i);
                            description=description+cur.getString("bodyHtml");
                        }
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
                    progressBar.setVisibility(View.GONE);
                    progresstext.setVisibility(View.GONE);
                    image = thumbnail;
                    String anchor = "<a href=\""+weburl+"\">View Full Article</a>";

                    ImageView img_view = findViewById(R.id.detailimage);
                    TextView title_view = findViewById(R.id.detailtitle);
                    TextView section_view = findViewById(R.id.detailsection);
                    TextView date_view = findViewById(R.id.detaildate);
                    TextView description_view = findViewById(R.id.detaildescription);
                    TextView link_view = findViewById(R.id.detaillink);
                    Glide.with(context)
                            .load(image)
                            .into(img_view);

                    title_view.setText(title);
                    section_view.setText(section);
                    date_view.setText(date_time);
                    description_view.setText(Html.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY));
                    link_view.setText(Html.fromHtml(anchor, HtmlCompat.FROM_HTML_MODE_LEGACY));
                    link_view.setMovementMethod(LinkMovementMethod.getInstance());


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("api error","Error in weather api");
            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu){
        SharedPreferences pref = context.getSharedPreferences("Bookmarks", 0);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = pref.getString("bookmark",null);
        Type type = new TypeToken<ArrayList<JSONObject>>() {}.getType();
        ArrayList<JSONObject> result = gson.fromJson(json,type);
        Boolean checked = false;
        int index = -1;
        if(result!=null){
            for(int i=0;i<result.size();i++){
                JSONObject curobj = result.get(i);
                try {
                    if(curobj.getString("id").equals(article_id)){
                        checked = true;
                        index =i;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        getMenuInflater().inflate(R.menu.top_navigation,menu);
        MenuItem item = menu.findItem(R.id.navbookmark);
        if(checked){
            item.setIcon(R.drawable.ic_bookmark_black_18dp);
        }else{
            item.setIcon(R.drawable.ic_bookmark_card);
        }

        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navtwitter:
                String tweet = "https://twitter.com/intent/tweet?text="+ Uri.encode("Check out this link:\n"+weburl+"\n"+"#CSCI571NewsSearch");
                Uri uri=Uri.parse(tweet);
                this.startActivity(new Intent(Intent.ACTION_VIEW,uri));
                return super.onOptionsItemSelected(item);
            case R.id.navbookmark:
                JSONObject obj = new JSONObject();
                try {
                    obj.put("id", article_id);
                    obj.put("title", title);
                    obj.put("section", section);
                    obj.put("time",time_cookie);
                    obj.put("img", image);
                    obj.put("weburl", weburl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferences pref = context.getSharedPreferences("Bookmarks", 0);
                SharedPreferences.Editor editor = pref.edit();
                Gson gson = new Gson();
                String json = pref.getString("bookmark",null);
                Type type = new TypeToken<ArrayList<JSONObject>>() {}.getType();
                ArrayList<JSONObject> result = gson.fromJson(json,type);
                Boolean checked = false;
                int index = -1;
                if(result!=null){
                    for(int i=0;i<result.size();i++){
                        JSONObject curobj = result.get(i);
                        try {
                            if(curobj.getString("id").equals(article_id)){
                                checked = true;
                                index =i;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(checked){
                    result.remove(index);
                    String json_obj = gson.toJson(result);
                    editor.putString("bookmark",json_obj);
                    editor.apply();
                    item.setIcon(R.drawable.ic_bookmark_card);
                    String msg= title + " was removed from the Bookmarks";
                    Toast.makeText(this, msg,
                            Toast.LENGTH_LONG).show();



                }
                else{
                    if(result==null){
                        result = new ArrayList<>();
                    }
                    result.add(obj);
                    String json_obj = gson.toJson(result);
                    editor.putString("bookmark",json_obj);
                    editor.apply();
                    item.setIcon(R.drawable.ic_bookmark_black_18dp);
                    String msg= title + " was added to the Bookmarks";
                    Toast.makeText(this, msg,
                            Toast.LENGTH_LONG).show();

                }
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
