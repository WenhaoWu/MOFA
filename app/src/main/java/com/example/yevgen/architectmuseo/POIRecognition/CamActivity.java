package com.example.yevgen.architectmuseo.POIRecognition;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.yevgen.architectmuseo.POIDetail.Activity_POIActivity;
import com.example.yevgen.architectmuseo.R;
import com.example.yevgen.architectmuseo.WikitudeSDKConstants;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.StartupConfiguration.CameraPosition;

public class CamActivity extends AbstractArchitectCamActivity {

	//private Receiver_DistanceResponseReceiver receiver;
	public static final String ARG_LOCATION = "Argument_location";
    private int mode;
	private String locationStr = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mode = getIntent().getIntExtra("mode", mode);
		/*
		IntentFilter filter = new IntentFilter(Receiver_DistanceResponseReceiver.PROCESS_RESPONSE);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new Receiver_DistanceResponseReceiver();
		registerReceiver(receiver, filter);
		*/

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

		if (getIntent().getStringExtra(ARG_LOCATION)!=null){
			locationStr = getIntent().getStringExtra(ARG_LOCATION);
			Log.e("locationStr", locationStr);
		}
	}

	@Override
	public String getARchitectWorldPath() {
        Log.e("Mode", mode+"");
        switch (mode){
            case 1: return "Cloud_Recognition/index.html";

            case 2: return "Cloud_Recognition/3dmodel.html";

            case 3: return "Cloud_Recognition/video.html";

            default:return "Cloud_Recognition/3dmodel.html";
        }

	}

	@Override
	public String getActivityTitle() {
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
            Uri invokedUri = Uri.parse(uriString);
            // pressed snapshot button
            if ("snapShotButton".equalsIgnoreCase(invokedUri.getHost())) {
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

	/*
    public class Receiver_DistanceResponseReceiver extends BroadcastReceiver {

		public static final String PROCESS_RESPONSE = "com.example.yevgen.architectmuseo.action.PROCESS_RESPONSE";

		@Override
		public void onReceive(Context context, Intent intent) {
			String responseDistance = intent.getStringExtra(Service_LocationTrackingService.RESPONSE_DISTANCE);
			TextView textView = (TextView)findViewById(R.id.Distance);
			textView.setText(responseDistance);
		}
	}
	*/
}
