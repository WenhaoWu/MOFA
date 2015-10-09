package com.example.yevgen.architectmuseo.POIListView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.yevgen.architectmuseo.POINotification.Receiver_AlarmReceiver;
import com.example.yevgen.architectmuseo.R;

public class Activity_POIMainListView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poimain_list_view);

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        Adapter_MyViewPagerAdapter viewPagerAdapter = new Adapter_MyViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount()-1);
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.fixed_tabs);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = (Toolbar)findViewById(R.id.poi_list_toolbar);
        toolbar.setTitle("Demo");
        setSupportActionBar(toolbar);

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
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 5*1000, pIntent);

    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), Receiver_AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, Receiver_AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }
}

