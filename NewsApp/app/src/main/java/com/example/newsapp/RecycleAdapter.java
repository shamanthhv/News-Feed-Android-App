package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder> {


    String[] img,title,section,id,time,web_url;
    String titleD;


    Context context;
    public  RecycleAdapter(Context ct, ArrayList<String> img_arr, ArrayList<String> title_arr, ArrayList<String> section_arr,ArrayList<String> time_arr,ArrayList<String> id_arr,ArrayList<String> url_arr){
        context = ct;
        img = img_arr.toArray(new String[img_arr.size()]);
        Log.d("length", String.valueOf(img.length));
        title = title_arr.toArray(new String[title_arr.size()]);
        section =  section_arr.toArray(new String[section_arr.size()]);
        id = id_arr.toArray(new String[id_arr.size()]);
        time = time_arr.toArray(new String[time_arr.size()]);
        web_url = url_arr.toArray(new String[url_arr.size()]);
        titleD = "Shamanth";
    }
    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cardlayout,parent,false);
        return new RecycleViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final RecycleViewHolder holder, final int position) {

        holder.title_view.setText(title[position]);
        holder.section_view.setText(section[position]);
        String time_str = time[position];
        ZonedDateTime pubtime = ZonedDateTime.parse(time_str);
        ZonedDateTime curtime = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        long date_time = HomeFragment.dateTimeDifference(pubtime,curtime, ChronoUnit.SECONDS);
        String duration = "";
        if(date_time>=86400){
            Integer val = (int)date_time/86400;
            duration = val.toString()+"d ago ";
        }
        else if(date_time>=60 && date_time<3600){
            Integer val = (int)date_time/60;
            duration = val.toString()+"m ago ";
        }
        else if(date_time>=3600){
            Integer val = (int)date_time/3600;
            duration = val.toString()+"h ago ";
        }
        else{
            Integer val =(int)date_time;
            duration =val.toString()+"s ago ";
        }

        holder.time_view.setText(duration);
        String url = img[position];
        Glide.with(context)
                .load(url)
                .into(holder.image_view);
        SharedPreferences pref = context.getSharedPreferences("Bookmarks", 0);
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
                    if(curobj.getString("id").equals(id[position])){
                        checked = true;
                        index =i;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if(checked){
            holder.bookmark_view.setImageResource(R.drawable.ic_bookmark_black_18dp);
        }
        else{
            holder.bookmark_view.setImageResource(R.drawable.ic_bookmark_card);
        }

        holder.bookmark_view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("id",id[position]);
                    obj.put("title",title[position]);
                    obj.put("section",section[position]);
                    obj.put("time",time[position]);
                    obj.put("img",img[position]);
                    obj.put("weburl",web_url[position]);

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
                            if(curobj.getString("id").equals(id[position])){
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
                   holder.bookmark_view.setImageResource(R.drawable.ic_bookmark_card);
                   String msg= title[position] + " was removed from the Bookmarks";
                   Toast.makeText(context, msg,
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
                   holder.bookmark_view.setImageResource(R.drawable.ic_bookmark_black_18dp);
                   String msg= title[position] + " was added to the Bookmarks";
                   Toast.makeText(context, msg,
                           Toast.LENGTH_LONG).show();
               }



            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(context,DetailActivity.class);
            intent.putExtra("articleid",id[position]);
            context.startActivity(intent);
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog);
                dialog.setTitle("Title");
                TextView text = (TextView) dialog.findViewById(R.id.dialogtext);
                text.setText(title[position]);
                ImageView image = (ImageView) dialog.findViewById(R.id.dialogimage);
                Glide.with(context)
                        .load(img[position])
                        .into(image);
                ImageView twitter = (ImageView) dialog.findViewById(R.id.twitter_dialog);
                twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tweet = "https://twitter.com/intent/tweet?text="+ Uri.encode("Check out this link:\n"+web_url[position]+"\n"+"#CSCI571NewsSearch");
                        Uri uri=Uri.parse(tweet);
                        context.startActivity(new Intent(Intent.ACTION_VIEW,uri));
                    }
                });
                final ImageView bookmark = (ImageView) dialog.findViewById(R.id.bookmark_dialog);

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
                            if(curobj.getString("id").equals(id[position])){
                                checked = true;
                                index =i;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(checked){
                    bookmark.setImageResource(R.drawable.ic_bookmark_black_18dp);
                }
                else{
                    bookmark.setImageResource(R.drawable.ic_bookmark_card);
                }
                bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("id",id[position]);
                            obj.put("title",title[position]);
                            obj.put("section",section[position]);
                            obj.put("time",time[position]);
                            obj.put("img",img[position]);
                            obj.put("weburl",web_url[position]);
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
                                    if(curobj.getString("id").equals(id[position])){
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
                            bookmark.setImageResource(R.drawable.ic_bookmark_card);
                            notifyDataSetChanged();
                            String msg= title[position] + " was removed from the Bookmarks";
                            Toast.makeText(context, msg,
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
                            bookmark.setImageResource(R.drawable.ic_bookmark_black_18dp);
                            notifyDataSetChanged();
                            String msg= title[position] + " was added to the Bookmarks";
                            Toast.makeText(context, msg,
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });

                dialog.show();
                return false;
            }
        });


    }


    @Override
    public int getItemCount() {

        return img.length;
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {
        TextView title_view, time_view, section_view,dummy_text;
        ImageView image_view,bookmark_view;
        CardView cardView;
        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);

            title_view = itemView.findViewById(R.id.cardtitle);
            time_view = itemView.findViewById(R.id.cardtime);
            section_view = itemView.findViewById(R.id.cardsection);
            image_view = itemView.findViewById(R.id.cardimage);
            bookmark_view = itemView.findViewById(R.id.cardbookmark);
            cardView  = itemView.findViewById(R.id.cardView);
        }
    }
}
