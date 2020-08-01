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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.RecycleViewHolder> {


    ArrayList<String> img,title,section,id,time,web_url;
    String titleD;
    RecyclerView recyclerView;
    TextView emptyview;

    Context context;
    public  BookmarkAdapter(Context ct, ArrayList<String> img_arr, ArrayList<String> title_arr, ArrayList<String> section_arr,ArrayList<String> time_arr,ArrayList<String> id_arr,TextView empty,RecyclerView recycle,ArrayList<String> weburl){
        context = ct;
        img = img_arr;
        title = title_arr;
        section = section_arr;
        id = id_arr;
        time = time_arr;
        titleD = "Shamanth";
        emptyview = empty;
        recyclerView = recycle;
        web_url = weburl;
    }
    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.bookmarklayout,parent,false);
        return new RecycleViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final RecycleViewHolder holder, final int position) {

        holder.title_view.setText(title.get(position));
        holder.section_view.setText(section.get(position));
        String timestr = time.get(position);
        ZonedDateTime pubtime = ZonedDateTime.parse(timestr);
        ZoneId pubtime1 = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zoneLA = pubtime.withZoneSameInstant(pubtime1);
        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd MMM");
        String outdate = date.format(zoneLA);
        holder.time_view.setText(outdate);
        String url = img.get(position);
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
                    if(curobj.getString("id").equals(id.get(position))){
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
                    obj.put("id", id.get(position));
                    obj.put("title", title.get(position));
                    obj.put("section", section.get(position));
                    obj.put("time",time.get(position));
                    obj.put("img", img.get(position));
                    obj.put("weburl", web_url.get(position));
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
                            if(curobj.getString("id").equals(id.get(position))){
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
                    String msg= title.get(position) + " was removed from the Bookmarks";
                    Toast.makeText(context, msg,
                            Toast.LENGTH_LONG).show();
                    img.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, img.size());

                    title.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, title.size());

                    section.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, section.size());

                    id.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, id.size());

                    time.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, time.size());


                    web_url.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, web_url.size());

                    if (img.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        emptyview.setVisibility(View.VISIBLE);
                    }


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
                    String msg= title.get(position) + " was added to the Bookmarks";
                    Toast.makeText(context, msg,
                            Toast.LENGTH_LONG).show();
                }



            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DetailActivity.class);
                intent.putExtra("articleid", id.get(position));
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
                text.setText(title.get(position));
                ImageView image = (ImageView) dialog.findViewById(R.id.dialogimage);
                ImageView twitter = (ImageView) dialog.findViewById(R.id.twitter_dialog);
                twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tweet = "https://twitter.com/intent/tweet?text="+ Uri.encode("Check out this link:\n"+ web_url.get(position) +"\n"+"#CSCI571NewsSearch");
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
                            if(curobj.getString("id").equals(id.get(position))){
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
                            obj.put("id", id.get(position));
                            obj.put("title", title.get(position));
                            obj.put("section", section.get(position));
                            obj.put("time", time.get(position));
                            obj.put("img", img.get(position));
                            obj.put("weburl", web_url.get(position));
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
                                    if(curobj.getString("id").equals(id.get(position))){
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
                            String msg= title.get(position) + " was removed from the Bookmarks";
                            Toast.makeText(context, msg,
                                    Toast.LENGTH_LONG).show();
                            img.remove(position);
                            section.remove(position);
                            title.remove(position);
                            id.remove(position);
                            time.remove(position);
                            web_url.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,result.size());
                            if (img.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                emptyview.setVisibility(View.VISIBLE);
                            }
                            dialog.dismiss();
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
                            String msg= title.get(position) + " was added to the Bookmarks";
                            Toast.makeText(context, msg,
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });
                Glide.with(context)
                        .load(img.get(position))
                        .into(image);
                dialog.show();
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {

        return img.size();
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {
        TextView title_view, time_view, section_view,dummy_text;
        ImageView image_view,bookmark_view;
        CardView cardView;
        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);

            title_view = itemView.findViewById(R.id.bookmarkcardtitle);
            time_view = itemView.findViewById(R.id.bookmarkcardtime);
            section_view = itemView.findViewById(R.id.bookmarkcardsection);
            image_view = itemView.findViewById(R.id.bookmarkcardimage);
            bookmark_view = itemView.findViewById(R.id.bookmarkicon);
            cardView = itemView.findViewById(R.id.bookmarkcardView);

        }
    }
}
