package com.example.yevgen.architectmuseo.POIRecognition;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.yevgen.architectmuseo.POIListView.Activity_POIMainListView;
import com.example.yevgen.architectmuseo.POINotification.Receiver_AlarmReceiver;
import com.example.yevgen.architectmuseo.R;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.StartupConfiguration;
import com.wikitude.architect.StartupConfiguration.CameraPosition;

import java.io.IOException;

public abstract class AbstractArchitectCamActivity extends AppCompatActivity implements ArchitectViewHolderInterface {

	protected ArchitectView architectView;
	protected ArchitectUrlListener urlListener;

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate( final Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		/* pressing volume up/down should cause music volume changes */
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.setContentView(this.getContentViewId());
		this.setTitle(this.getActivityTitle());
		/* set AR-view for life-cycle notifications etc. */
		this.architectView = (ArchitectView)this.findViewById( this.getArchitectViewId()  );
		final StartupConfiguration config = new StartupConfiguration( this.getWikitudeSDKLicenseKey());

		try {
			/* first mandatory life-cycle notification */
			this.architectView.onCreate( config );
		} catch (RuntimeException rex) {
			this.architectView = null;
			Toast.makeText(getApplicationContext(), "can't create Architect View", Toast.LENGTH_SHORT).show();
			Log.e(this.getClass().getName(), "Exception in ArchitectView.onCreate()", rex);
		}
        this.urlListener = this.getUrlListener();
		if (this.urlListener != null && this.architectView != null) {
			this.architectView.registerUrlListener( this.getUrlListener() );
		}
	}

	protected abstract CameraPosition getCameraPosition();

	@Override
	protected void onPostCreate( final Bundle savedInstanceState ) {
		super.onPostCreate(savedInstanceState);
		if ( this.architectView != null ) {
			this.architectView.onPostCreate();
			try {
				this.architectView.load( this.getARchitectWorldPath());
				if (this.getInitialCullingDistanceMeters() != ArchitectViewHolderInterface.CULLING_DISTANCE_DEFAULT_METERS) {
					// set the culling distance - meaning: the maximum distance to render geo-content
					this.architectView.setCullingDistance( this.getInitialCullingDistanceMeters() );
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if ( this.architectView != null ) {
			this.architectView.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if ( this.architectView != null ) {
			this.architectView.onPause();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if ( this.architectView != null ) {
			this.architectView.onDestroy();
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if ( this.architectView != null ) {
			this.architectView.onLowMemory();
		}
	}

	public abstract String getActivityTitle();

	@Override
	public abstract String getARchitectWorldPath();

	@Override
	public abstract ArchitectUrlListener getUrlListener();

	@Override
	public abstract int getContentViewId();

	@Override
	public abstract String getWikitudeSDKLicenseKey();

	@Override
	public abstract int getArchitectViewId();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
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
			intent.setClass(this, Activity_POIMainListView.class);
			startActivity(intent);
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
		//setup periodic alarm every 5 seconds
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