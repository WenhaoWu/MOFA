package com.example.yevgen.architectmuseo.POINotification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yevgen.architectmuseo.CamActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wenhaowu on 11/09/15.
 */
public class Service_LocationTrackingService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MyService";
    public static final String RESPONSE_DISTANCE = "ResponseDistance";
    public static final String RESPONSE_ARRAYLIST = "ResponseArraylist";


    //5s
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    //2.5s
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    protected Network_POIListGetter POIList;
    protected Location mCurrentLocation;


    public Service_LocationTrackingService() {
        super(Service_LocationTrackingService.class.getName());
    }

    //call this method when the service start. The service starts every 10s
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "Service is Running");
        buildGoogleApiClient();
        mGoogleApiClient.connect();

    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // 5s
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // 2.5s
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation != null){
                Log.e(TAG+" first",mCurrentLocation.toString());
            }
            else {
                Log.e(TAG+" error","No GoogleApi");
            }
        }

        //getting the distances between curlocation and targetlocations
        startLocationUpdates();

        //initialize everything in case after user clicks no service but GoogleApiClient still request for location
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        mGoogleApiClient=null;
        mCurrentLocation = null;

    }

    //Requests location updates from the FusedLocationApi.
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        addDisList();
    }

    //Removes location updates from the FusedLocationApi.
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    public void addDisList(){
        POIList = new Network_POIListGetter();
        POIList.printList();

        double mlat = mCurrentLocation.getLatitude();
        double mlong = mCurrentLocation.getLongitude();

        for (int i=0; i<POIList.getSize(); i++){
            final int POI_Index = i;
            get_Distance(new VolleyCallback() {
                @Override
                public void onSuccess(double result) {
                    Log.e(TAG + " distance", "" + result);
                    if (result < 50) {
                        pushNotification(POI_Index);
                    }
                    sendBackByBroadcast(result);
                }
            }, mlat, mlong, POIList.getDisLat(i), POIList.getDisLong(i));

        }

    }

    private void sendBackByBroadcast(double result) {
        Intent intent = new Intent();
        intent.setAction(CamActivity.Receiver_DistanceResponseReceiver.PROCESS_RESPONSE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(RESPONSE_DISTANCE, result + "");
        sendBroadcast(intent);
    }

    private void pushNotification(int POI_Index) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle(POIList.getPOIName(POI_Index))
                .setContentText("Find and Scan");
        mBuilder.setAutoCancel(true);

        //Set up the activity that it jumps to
        Intent resultIntent = new Intent(this, Activity_POIActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Activity_POIActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        //Activate the notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(POI_Index, mBuilder.build());

    }


    public interface VolleyCallback{
        void onSuccess(double result);
    }

    public double get_Distance(final VolleyCallback callback, double curLat, double curLong, double disLat, double disLong){
        String url = "http://maps.googleapis.com/maps/api/directions/json?origin="+curLat+"%2C"+curLong+
                "&destination="+disLat+"%2C"+disLong+"&sensor=false&mode=%22DRIVING%22";
        Log.e("URL", url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e(TAG + " response", response.toString());
                        try {
                            JSONArray routeArray = response.getJSONArray("routes");
                            JSONObject routes = routeArray.getJSONObject(0);

                            JSONArray newTempARr = routes.getJSONArray("legs");
                            JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

                            JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                            double result = distOb.getDouble("value");
                            //Log.e(TAG+" response", distOb.toString());
                            callback.onSuccess(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG+" error", error.toString());
                    }
                });

        queue.add(jsonObjReq);
        return 0;
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.e(TAG + " update", mCurrentLocation.toString());
    }

}