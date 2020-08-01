package com.example.newsapp;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;

public class TrendingFragment extends Fragment  {
    LineChart mpLineChart;

    ArrayList<Integer> res = new ArrayList<>();
    private ArrayList<Entry> getdata(){
        ArrayList<Entry> datavalues = new ArrayList<Entry>();
        for(int i=0;i<res.size();i++){
            datavalues.add(new Entry(i, res.get(i)));
        }
        return datavalues;
    }
    private void makeApicall(final String searchterm){
        res.removeAll(res);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String urlnew = "https://android-backend-275002.wl.r.appspot.com/trending?searchword="+searchterm;
        Log.d("myurl",urlnew);
        final StringRequest jsonObjectRequestnew = new StringRequest(urlnew, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray results = object.getJSONArray("vals");
                    for(int i=0;i< results.length();i++){
                        int y = Integer.parseInt(results.getString(i));
                        int x = i;
                        res.add(y);
                    }
                    String data_str = "Trending chart for "+ searchterm;
                    LineDataSet lineDataSet1 = new LineDataSet(getdata(),data_str);
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(lineDataSet1);
                    lineDataSet1.setColor(getResources().getColor(R.color.colorPrimary));
                    lineDataSet1.setCircleColor(getResources().getColor(R.color.colorPrimary));
                    lineDataSet1.setCircleHoleColor(getResources().getColor(R.color.colorPrimary));
                    LineData data = new LineData(dataSets);
                    Legend legend = mpLineChart.getLegend();
                    legend.setTextColor(getResources().getColor(R.color.colorPrimary));
                    mpLineChart.getXAxis().setDrawGridLines(false);
                    mpLineChart.getAxisRight().setDrawGridLines(false);
                    mpLineChart.getAxisLeft().setDrawGridLines(false);
                    mpLineChart.getAxisLeft().setDrawAxisLine(false);
                    mpLineChart.setData(data);
                    mpLineChart.invalidate();


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
        View view=  inflater.inflate(R.layout.trending_fragment,container,false);
        mpLineChart = (LineChart) view.findViewById(R.id.line_chart);
        makeApicall("Coronavirus");

        final EditText editText = view.findViewById(R.id.editText);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==event.KEYCODE_ENTER && event.getAction()==event.ACTION_DOWN){
                    makeApicall(editText.getText().toString());
                }
                return false;
            }
        });
        return view;
    }
}
