package com.example.yevgen.architectmuseo.POIRecognition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yevgen.architectmuseo.POIDetail.Activity_POIActivity;
import com.example.yevgen.architectmuseo.POINotification.Service_LocationTrackingService;
import com.example.yevgen.architectmuseo.R;
import com.example.yevgen.architectmuseo.WikitudeSDKConstants;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.StartupConfiguration.CameraPosition;

public class CamActivity extends AbstractArchitectCamActivity {

	private Receiver_DistanceResponseReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		IntentFilter filter = new IntentFilter(Receiver_DistanceResponseReceiver.PROCESS_RESPONSE);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new Receiver_DistanceResponseReceiver();
		registerReceiver(receiver, filter);

		String imageFlag;
		if (savedInstanceState == null){
			Bundle extras = getIntent().getExtras();
			if (extras == null){
				imageFlag = null;
			}
			else {
				imageFlag = extras.getString("Image_Flag");
			}
		}
		else{
			imageFlag = (String)savedInstanceState.getSerializable("Image_Flag");
		}

		if (imageFlag == "True"){
			ImageView imageView = (ImageView)findViewById(R.id.ExampleImage);
			imageView.setImageResource(R.drawable.target_test);
		}


	}

	@Override
	public String getARchitectWorldPath() {
		//Log.e("CamAct_WP", getIntent().getExtras().getString("activityArchitectWorldUrl"));
		//return getIntent().getExtras().getString("activityArchitectWorldUrl");
		return "samples/1_Building$Recognition_1_Cloud$On-Click/index.html";
	}

	@Override
	public String getActivityTitle() {
		//Log.e("CamAct_AT", getIntent().getExtras().getString("activityTitle"));

		/*
        return (getIntent().getExtras() != null && getIntent().getExtras().get(
				"activityTitle") != null) ? getIntent()
				.getExtras().getString("activityTitle")
				: "Test-World";
		*/
        return "Demo";
	}

	@Override
	public int getContentViewId() {
		return R.layout.sample_cam;
	}

	@Override
	public int getArchitectViewId() {
		return R.id.architectView;
	}
	
	@Override
	public String getWikitudeSDKLicenseKey() {
		return WikitudeSDKConstants.WIKITUDE_SDK_KEY;
	}

	@Override
	public ArchitectUrlListener getUrlListener() {
		return new ArchitectUrlListener() {

			@Override
			public boolean urlWasInvoked(String uriString) {
				Log.e("CRAAAAAAAAAAAAAAAAAAAAP", uriString);
				Uri invokedUri = Uri.parse(uriString);

				// pressed snapshot button. check if host is button to fetch e.g. 'architectsdk://button?action=captureScreen', you may add more checks if more buttons are used inside AR scene
				if ("craphost1".equalsIgnoreCase(invokedUri.getHost())) {
					Intent intent = new Intent();
					intent.setClass(getBaseContext(), Activity_POIActivity.class);
                    intent.putExtra(Activity_POIActivity.ARG_Name, invokedUri.getQueryParameter("name"));
					intent.putExtra(Activity_POIActivity.ARG_Des, "Test");
                    Log.e("ResponseFromJS", invokedUri.getQueryParameter("name"));
                    startActivity(intent);
				}


				return true;
			}
		};
	}
	
	@Override
	public float getInitialCullingDistanceMeters() {
		// you need to adjust this in case your POIs are more than 50km away from user here while loading or in JS code (compare 'AR.context.scene.cullingDistance')
		return ArchitectViewHolderInterface.CULLING_DISTANCE_DEFAULT_METERS;
	}

	@Override
	protected CameraPosition getCameraPosition() {
		return CameraPosition.DEFAULT;
	}

    @Override
    protected void onStop() {
        super.onStop();
        //unregisterReceiver(receiver);
    }

    public class Receiver_DistanceResponseReceiver extends BroadcastReceiver {

		public static final String PROCESS_RESPONSE = "com.example.yevgen.architectmuseo.action.PROCESS_RESPONSE";

		@Override
		public void onReceive(Context context, Intent intent) {
			String responseDistance = intent.getStringExtra(Service_LocationTrackingService.RESPONSE_DISTANCE);
			TextView textView = (TextView)findViewById(R.id.Distance);
			textView.setText(responseDistance);
		}
	}
}
