package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity<FusedLocationProviderClient> extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static double latitude = 34.0322;
    public static double longitude = -118.2836;
    public String  city = "Los Angeles";
    public String state = "California";
    public int temperature;
    public String summary;
    public int selected_item;
    Fragment selectedFragment =null;
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(selected_item==R.id.nav_bookmark){
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new BookmarkFragment()).commit();
//
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListner);
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#FFFFFF"));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);
        setTitleColor(R.color.black);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);


        } else {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            LocationProvider provider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(String.valueOf(provider));
            Log.d("location", String.valueOf(lastKnownLocationGPS));
            Location finallocation = null;
            if (lastKnownLocationGPS != null) {
                finallocation = lastKnownLocationGPS;
            } else {
                Location loc =  locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                finallocation = loc;
            }

            if(finallocation!=null){
                latitude = finallocation.getLatitude();
                longitude = finallocation.getLongitude();
            }


        }
        make_weather_api();

    }
    public void make_headlines(){
        Fragment headline  = new HeadlineFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,headline).commit();
    }



    public void make_weather_api(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&appid=8f61620e8ff78d94f95e1ab132549350";
        final StringRequest jsonObjectRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("jsonresponse",response);
                    JSONObject object = new JSONObject(response);
                    JSONObject mainobj = object.getJSONObject("main");
                    Double temp = (Double) mainobj.get("temp");
                    temperature = temp.intValue();
                    JSONArray ja = (JSONArray) object.get("weather");
                    JSONObject jobj = ja.getJSONObject(0);
                    summary = jobj.getString("main");


                } catch (Exception e) {
                    e.printStackTrace();
                }
                Bundle bundle = new Bundle();
                bundle.putString("city",city);
                bundle.putString("state",state);
                bundle.putString("temperature", String.valueOf(temperature));
                bundle.putString("summary",summary);
                Fragment home  = new HomeFragment();
                home.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,home).commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("api error","Error in weather api");
            }
        });
        queue.add(jsonObjectRequest);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListner = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    make_weather_api();
                    selected_item = R.id.nav_home;
                    break;
                case R.id.nav_headlines:
                    make_headlines();
                    selected_item = R.id.nav_headlines;
                    break;
                case R.id.nav_trending:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TrendingFragment()).commit();
                    selected_item = R.id.nav_trending;
                    break;
                case R.id.nav_bookmark:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new BookmarkFragment()).commit();
                    selected_item = R.id.nav_bookmark;
                    break;
            }


            return true;
        }
    };

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem searchMenu = menu.findItem(R.id.app_bar_menu_search);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("result",query);
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                intent.putExtra("searchid",query);
                MainActivity.this.startActivity(intent);
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                final ArrayList<String> dataArr = new ArrayList<>();
                if(newText.length()>=2){
                    Log.d("shamanth","called");
                    String url = "https://shamanth.cognitiveservices.azure.com/bing/v7.0/suggestions?" +"q="+newText;
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    final StringRequest jsonObjectRequestnew = new StringRequest(url, new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(String response) {
                            Log.d("shamanth",response);
                            try {
                                JSONObject object = new JSONObject(response);
                                JSONArray suggestionGroups = object.getJSONArray("suggestionGroups");
                                JSONObject obj = suggestionGroups.getJSONObject(0);
                                JSONArray searchSuggestions =obj.getJSONArray("searchSuggestions");
                                for(int i = 0;i< Math.min(searchSuggestions.length(),5);i++){
                                    JSONObject cur = searchSuggestions.getJSONObject(i);
                                    dataArr.add(cur.getString("displayText"));
                                }

                                Log.d("search", String.valueOf(dataArr));
                                ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, dataArr);
                                searchAutoComplete.setAdapter(newsAdapter);

                                searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                                        String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                                        searchAutoComplete.setText("" + queryString);




                                    }
                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }




                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){    //this is the part, that adds the header to the request
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Ocp-Apim-Subscription-Key", "5a79dd7ec8614291a348c7247dc13543");
                            return params;
                        }
                    };
                    queue.add(jsonObjectRequestnew);

                }


                return false;
            }
        });




        return super.onCreateOptionsMenu(menu);
    }
}
