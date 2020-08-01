package com.example.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BookmarkFragment extends Fragment {
    ArrayList<String> img_arr = new ArrayList<String>();
    ArrayList<String> title_arr = new ArrayList<String>();
    ArrayList<String> section_arr = new ArrayList<String>();
    ArrayList<String> time_arr = new ArrayList<String>();
    ArrayList<String> news_id_arr = new ArrayList<String>();
    ArrayList<String> web_url_arr = new ArrayList<String>();
    RecyclerView recyclerView;
    TextView emptyview;
    BookmarkAdapter recycleAdapter;

    @Override
    public void onResume() {
        super.onResume();
        if(recycleAdapter!=null){
            img_arr.removeAll(img_arr);
            title_arr.removeAll(title_arr);
            section_arr.removeAll(section_arr);
            time_arr.removeAll(time_arr);
            news_id_arr.removeAll(news_id_arr);
            web_url_arr.removeAll(web_url_arr);
            getdata();
            if (img_arr.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyview.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyview.setVisibility(View.GONE);
            }
            recyclerView.setAdapter(recycleAdapter);
            recycleAdapter.notifyDataSetChanged();
        }

    }
    public void getdata(){
        SharedPreferences pref = getActivity().getSharedPreferences("Bookmarks", 0);
        Gson gson = new Gson();
        String json = pref.getString("bookmark",null);
        Type type = new TypeToken<ArrayList<JSONObject>>() {}.getType();
        ArrayList<JSONObject> result = gson.fromJson(json,type);
        if(result!=null){
            for(int i=0;i<result.size();i++){
                JSONObject curobj = result.get(i);
                try {
                    title_arr.add(curobj.getString("title"));
                    img_arr.add(curobj.getString("img"));
                    section_arr.add(curobj.getString("section"));
                    time_arr.add(curobj.getString("time"));
                    news_id_arr.add(curobj.getString("id"));
                    web_url_arr.add(curobj.getString("weburl"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view =  inflater.inflate(R.layout.bookmark_fragment,container,false);
            getdata();
            recyclerView = view.findViewById(R.id.bookmarkrecyclerViewList);
            emptyview = view.findViewById(R.id.empty);
            if (img_arr.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyview.setVisibility(View.VISIBLE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyview.setVisibility(View.GONE);
            }
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
            recycleAdapter = new BookmarkAdapter(getActivity(),img_arr,title_arr,section_arr,time_arr,news_id_arr,emptyview,recyclerView,web_url_arr);
            recyclerView.setAdapter(recycleAdapter);

            return view;

    }
}



