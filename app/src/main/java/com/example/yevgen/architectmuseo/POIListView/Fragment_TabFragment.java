package com.example.yevgen.architectmuseo.POIListView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.yevgen.architectmuseo.Constains_BackendAPI_Url;
import com.example.yevgen.architectmuseo.POIDetail.Activity_POIActivity;
import com.example.yevgen.architectmuseo.POINotification.Object_POI;
import com.example.yevgen.architectmuseo.POIRecognition.CamActivity;
import com.example.yevgen.architectmuseo.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenhaowu on 23/09/15.
 */
public class Fragment_TabFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String ARG_PARM1 = "SortingMethodID";
    public static final String ARG_PARM2 = "POIName";
    private static final String TAG = "Fragment_Tab";


    protected GoogleApiClient mGoogleApiClient;
    protected Location mCurrentLocation;
    protected StableArrayAdapter adapter;

    private ProgressDialog progress;

    public static Fragment_TabFragment newInstance(int ID) {
        Fragment_TabFragment fragment = new Fragment_TabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARM1, ID);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_TabFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation != null) {
                Log.e(TAG + " Location", mCurrentLocation.toString());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        /********For showing the loading message for 3 seconds before it receive data. ********/
        /*
        final ProgressDialog dialog=new ProgressDialog(getContext());
        dialog.setMessage("Getting data from back end");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(3*1000);//Delay of 3 seconds
                    dialog.hide();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        welcomeThread.start();

        /********For showing the loading message for 3 seconds before it receive data. ********/
        progress = new ProgressDialog(getContext());
        progress.setTitle("Loading");
        progress.setMessage("Retrieving data from backend...");
        progress.show();

        CoordinatorLayout myView = (CoordinatorLayout) inflater.inflate(R.layout.fragment_poi_list_tab, container, false);

        FloatingActionButton fab_cam = (FloatingActionButton)myView.findViewById(R.id.poi_list_fab_cam);
        fab_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), CamActivity.class);
                startActivity(intent);
            }
        });

        final ListView listView = (ListView) myView.findViewById(R.id.POIlistview);

        String url = null;

        if (getArguments() != null) {
            int sortingMethodID = getArguments().getInt(ARG_PARM1);
            switch (sortingMethodID) {
                case 0:
                    mGoogleApiClient.connect();
                    url = "http://dev.mw.metropolia.fi/mofa/Wikitude_1/geoLocator/distance_matrix.php?lat=60.221354&lng=24.804587";
                    //url = Constains_BackendAPI_Url.URL_POIList;
                    //url = url + 60.221354+ "&lng="+24.804587;
                    Log.e("POIList URL", url);
                    break;
                case 1:
                    //setListViewByMostviewed();
                    break;
                case 2:
                    //setListViewByRecomend();
                    break;
                default:
                    //url = "http://dev.mw.metropolia.fi/mofa/Wikitude_1/geoLocator/poi.json";
                    url = Constains_BackendAPI_Url.URL_POIList;
                    break;
            }
        }


        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.e("Response size", response.length() + "");

                        final ArrayList<Object_POI> result = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {

                            String name = null, imgBase64 = null;
                            int id = 0, disTo=0;
                            try {
                                name = response.getJSONObject(i).getString("poi_name");
                                disTo = response.getJSONObject(i).getInt("distance");
                                imgBase64 = response.getJSONObject(i).getString("compressed_image");
                                id = response.getJSONObject(i).getInt("id");

                            } catch (Exception e) {
                                Log.e("Response Error", e.toString());
                            }
                            Object_POI temp = new Object_POI(0, 0, name, id, imgBase64,null,disTo);
                            result.add(temp);
                        }

                        adapter = new StableArrayAdapter(getContext(), result);
                        listView.setAdapter(adapter);

                        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent();
                                intent.putExtra(Activity_POIActivity.ARG_Name, result.get(position).getName());
                                intent.putExtra(Activity_POIActivity.ARG_Des, result.get(position).getDescrip());
                                intent.putExtra(Activity_POIActivity.ARG_ID, result.get(position).getID());
                                intent.setClass(getContext(), Activity_POIActivity.class);
                                startActivity(intent);
                            }
                        };
                        listView.setOnItemClickListener(onItemClickListener);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG + " error", error.toString());
                    }
                }
        );

        if (url != null) {
            queue.add(jsonArrayRequest);
        }

        /**/
        progress.dismiss();
        return myView;
    }


    private class StableArrayAdapter extends ArrayAdapter<Object_POI> {
        private final Context context;

        //list of the name of the poi
        private final List<Object_POI> values;


        public StableArrayAdapter(Context context, List<Object_POI> objects) {
            super(context, -1, objects);
            this.context = context;
            this.values = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.layout_poi_list_row_layout, parent, false);

            TextView Title = (TextView) rowView.findViewById(R.id.POIRowFriLine);
            TextView Disto = (TextView)rowView.findViewById(R.id.POIRowSecLine);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.POIRowImage);

            /**/
            byte[] decodedString = Base64.decode(values.get(position).getImgBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);


            Title.setText(values.get(position).getName());
            Disto.setText(values.get(position).getDisTo()+" m");
            return rowView;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
}
