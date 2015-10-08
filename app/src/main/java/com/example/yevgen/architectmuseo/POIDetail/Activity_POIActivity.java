package com.example.yevgen.architectmuseo.POIDetail;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.yevgen.architectmuseo.POIListView.Activity_POIMainListView;
import com.example.yevgen.architectmuseo.POINotification.Receiver_AlarmReceiver;
import com.example.yevgen.architectmuseo.POIRecognition.CamActivity;
import com.example.yevgen.architectmuseo.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Activity_POIActivity extends AppCompatActivity {

    public final static String ARG_Name = "PoiName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);

        FloatingActionButton fab_cam = (FloatingActionButton)findViewById(R.id.poi_detail_fab_cam);
        fab_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getBaseContext(), CamActivity.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar)findViewById(R.id.poi_detail_toolbar);
        toolbar.setTitle("Point Of Interest");
        setSupportActionBar(toolbar);

        final ViewPager pager = (ViewPager)findViewById(R.id.image_pager);
        final Adapter_ImageSlideAdapter slideAdapter = new Adapter_ImageSlideAdapter(getSupportFragmentManager());
        getPicList(new VolleyCallback() {
            @Override
            public void onSuccess(List<String> result) {
                slideAdapter.setList(result);
                pager.setAdapter(slideAdapter);
                pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        });

        TextView titleTextView = (TextView)findViewById(R.id.POITitle);
        Intent intent = getIntent();
        titleTextView.setText(intent.getStringExtra(ARG_Name));
    }

    private interface VolleyCallback{
        void onSuccess(List<String> result);
    }

    public double getPicList(final VolleyCallback callback){
        String url = "http://dev.mw.metropolia.fi/mofa/Wikitude_1/geoLocator/poi.json";
        Log.e("URL", url);

        final List<String> result = new ArrayList<String>();
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Response Size", response.length()+"");
                        for (int i=0; i<response.length(); i++){
                            String imgBase64_temp = "iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAMAAAAp4XiDAAABgFBMVEX////+/v77+/z8/Pz6+/vb3OC+wMeytLzFx8zw8PLCxcq0t77P0dXu7vC5u8Kztr7c3uH39/i9vsWztb3x8fPS09iytbzMzdL19fa3uMCxs7vz8/Te4OOws7qytLvk5eevsbnk5ej19vf5+vru7/Hn6Or9/f309PXn5+r9/f6tsLerrrausbi7vsTV1trx8vP6+vvh4uXb3N/T1dmqrbW7vMPs7e7Excupq7Tm5+nHyc+nqrOvsrr9/v6mqbGwsrrl5un///+srrakp7Ceoqujpq+go6yipK3AwsilqLChpK2boKj4+Pn5+fqYnKadoKqWmaScnqmVmKOanaiytL2Vl6KZnKamqbKTl6KXm6WZnKeipa+YmqXJy9CPkp6TlqGkpq/j4+aMj5ySlaDp6uyQk56FiJX7+/vc3eGOkZ2NkJz8/P2lp7CNkJuIi5fr7O7Y2d2Mj5uprLSKjpmLj5rg4eSKjZnLzNKIjJj29veHipaAhJCGipXX2NzR0tff3+LqOSweAAAAAXRSTlMAQObYZgAAAYRJREFUeNrt1Nk3QlEUx/FfInKTpCRjkbEroWsmZJ5F5jLP8zzzr7sWy0t7n+UsD1583s7Dd+21zln74N/fM6QYISU1zZSeARnmTEVRLJCQZc3W2SSKHHuuTnFIJM68Dy6JIt9doHMXSiRFxbqSUkiweDwetxcyXB6rsww/Ue6rqHBUAqjypQBl1Tab11EjfDyLu9bvV+sC9QCCDRl5fp1qbwyCUdmkhr6omQGXEvqmNYNkbGllqW2gtHcIdHYRRWO3QLinF0mCfRFebRYI3v4BVqQBFNPgEGuYvuCRQdZoD72zY/2s0DgoE5NTLAV0Mj3DsjJJdJY1xySxedbCBCjBxSXWZBsoxuVpngkUw0o0xlqlx8QTa7x1Bwgbm1sCkW3q39raEYk6DUgSTgjt7iHJ/oHIYTqSHR2fCJyegXB+IXDcC4Lh8op1fQPSduKWo4Fxd8+YeQAnfEq62AArZ+iRYoFA4crTh+eO+P5NwLr2eXBByPhi1+yv5q+9e2vRtLgPv/UOcaQrXbMkXkMAAAAASUVORK5CYII=";
                            try {
                                imgBase64_temp = response.getJSONObject(i).getString("image");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            result.add(imgBase64_temp);
                        }
                        callback.onSuccess(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley error", error.toString());
                    }
                });

        queue.add(jsonArrayRequest);
        return 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity__poi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.location_start) {
            scheduleAlarm();
            return true;
        }
        else if(id == R.id.location_stop){
            cancelAlarm();
            return true;
        }
        else if(id == R.id.show_list){
            Intent intent = new Intent();
            intent.setClass(getBaseContext(), Activity_POIMainListView.class);
            startActivity(intent);

        }


        return super.onOptionsItemSelected(item);
    }

    public void scheduleAlarm(){
        Log.e("MySer", "startAlarm");
        Intent intent = new Intent(getApplicationContext(), Receiver_AlarmReceiver.class);

        //create a pendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, Receiver_AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //alarm is set from now
        long firstMillis = System.currentTimeMillis();
        //setup periodic alarm every 10 seconds
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 10000, pIntent);

    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), Receiver_AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, Receiver_AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }
}
