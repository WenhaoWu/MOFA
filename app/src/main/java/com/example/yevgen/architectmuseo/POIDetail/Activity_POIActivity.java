package com.example.yevgen.architectmuseo.POIDetail;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yevgen.architectmuseo.Constains_BackendAPI_Url;
import com.example.yevgen.architectmuseo.POIListView.Activity_POIMainListView;
import com.example.yevgen.architectmuseo.POIListView.Fragment_TabFragment;
import com.example.yevgen.architectmuseo.POINotification.Receiver_AlarmReceiver;
import com.example.yevgen.architectmuseo.POIRecognition.CamActivity;
import com.example.yevgen.architectmuseo.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.liangfeizc.slidepageindicator.CirclePageIndicator;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Activity_POIActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener{

    public final static String ARG_Name = "PoiName";
    public final static String ARG_Des = "PoiDescript";
    public final static String ARG_ID = "PoiId";
    private CirclePageIndicator mPageIndicator;
    private MediaPlayer mediaPlayer = null;
    private FloatingActionButton fab_media, fab_cam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_poi_detail);

        final int POI_id = getIntent().getIntExtra(ARG_ID,42);
        final SharedPreferences sp = getSharedPreferences("my_prefs", MODE_PRIVATE);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        String url = "https://api.soundcloud.com/tracks/228179666/stream?client_id=76bf4a478f95a82ca090ecd8fa5b99db";
        try{
            mediaPlayer.setDataSource(url);
        } catch(Exception e){
            Log.e("Cannot open given URL",e.toString());
        }
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.prepareAsync();

        fab_cam = (FloatingActionButton)findViewById(R.id.poi_detail_fab_cam);
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

        final TextView titleTextView = (TextView)findViewById(R.id.POITitle);
        final TextView desTextView = (TextView)findViewById(R.id.POIDescription);

        final ViewPager pager = (ViewPager)findViewById(R.id.image_pager);
        final Adapter_ImageSlideAdapter slideAdapter = new Adapter_ImageSlideAdapter(getSupportFragmentManager());

        //set image slider picture, title and description. We get it from backend
        getDetail(new DetailCallback() {
            @Override
            public void onSuccess(List<String> pic_List, String title, String descrip) {

                //sending the picture list to full screen image view
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("Picture_size", pic_List.size());
                for (int i=0; i<pic_List.size(); i++){
                    editor.remove("Picture_"+i);
                    editor.putString("Picture_"+i, pic_List.get(i));
                }
                editor.commit();

                slideAdapter.setList(pic_List);
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

                mPageIndicator = (CirclePageIndicator) findViewById(R.id.imageIndicator);
                mPageIndicator.setViewPager(pager);

                titleTextView.setText(title);

                desTextView.setText(descrip);
                //desTextView.setMovementMethod(new ScrollingMovementMethod());
            }

        }, POI_id);

        RatingBar ratingBar = (RatingBar)findViewById(R.id.POIRatingBar);

        if (sp.contains("POI_Rate"+POI_id)){
            ratingBar.setRating(sp.getFloat("POI_Rate"+POI_id,0));
            //ratingBar.setEnabled(false);
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //ratingBar.setEnabled(false);
                float oldRate = 0;

                SharedPreferences.Editor ed = sp.edit();
                if (sp.getFloat("POI_Rate"+POI_id,0)!=0){
                    oldRate = sp.getFloat("POI_Rate"+POI_id,0);
                    ed.remove("POI_Rate" + POI_id);
                }
                ed.putFloat("POI_Rate" + POI_id, rating);
                ed.commit();

                //send a request to backend to update the rateSum and rateCount;
                /*********************/
                toRate(new RateCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(getBaseContext(),result,Toast.LENGTH_SHORT).show();
                    }
                }, POI_id, rating, oldRate);

            }
        });

    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        fab_media = (FloatingActionButton)findViewById(R.id.poi_detail_fab_media);

        fab_media.setOnClickListener(new View.OnClickListener() {
            boolean isPlaying = false;

            @Override
            public void onClick(View v) {

                if (isPlaying) {
                    mp.pause();
                } else {
                    mp.start();
                }
                isPlaying = !isPlaying;
            }
        });
    }

    private interface DetailCallback {
        void onSuccess(List<String> pic_List, String title, String descrip);
    }

    public double getDetail(final DetailCallback callback, int id){
        //String url = Constains_BackendAPI_Url.URL_POIDetail+getIntent().getIntExtra(ARG_ID, 0)+"'";
        String url = Constains_BackendAPI_Url.URL_POIDetail+ String.valueOf(id);
        Log.e("URL", url);
        final ProgressDialog PD = Fragment_TabFragment.createProgressDialog(Activity_POIActivity.this);

        final List<String> PicResult = new ArrayList<String>();
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Response Size", response.length()+"");
                        String title_temp = null, descrip_temp = null;
                        int pic_count=0;

                        try {
                            title_temp = response.getJSONObject(0).getString("poi_name");
                            descrip_temp = response.getJSONObject(0).getString("description");

                            pic_count = response.getJSONObject(1).getJSONArray("multiple_image").length();
                            for (int i=0; i<pic_count; i++){
                                PicResult.add(response.getJSONObject(1).getJSONArray("multiple_image").getString(i));
                            }

                        } catch (Exception e) {
                            Log.e("JsonPharseError", e.toString());
                        }

                        Log.e("Pic_count", "try "+pic_count);

                        PD.dismiss();
                        callback.onSuccess(PicResult, title_temp, descrip_temp);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        PD.dismiss();
                        Log.e("Volley error", error.toString());
                    }
                });

        queue.add(jsonArrayRequest);
        PD.show();
        return 0;
    }

    public interface RateCallback{
        void onSuccess(String result);
    }

    public void toRate(final RateCallback callback, int id, float newRate, float oldRate){
        String parms = "id="+id+"&rate="+newRate+"&oldRate="+oldRate;
        String url = Constains_BackendAPI_Url.URL_POIRate+parms;
        Log.e("RateURL", url);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rate error", error.toString());
                    }
                }
        );

        queue.add(stringRequest);
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
        else if (id== R.id.clearSP){
            int POI_id = getIntent().getIntExtra(ARG_ID,42);

            SharedPreferences sp = getSharedPreferences("my_prefs", MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();

            if (sp.getFloat("POI_Rate"+POI_id,0)!=0){
                ed.remove("POI_Rate" + POI_id);
                ed.commit();
                Toast.makeText(this,"Cleared SP",Toast.LENGTH_SHORT).show();
            }

            return true;
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
