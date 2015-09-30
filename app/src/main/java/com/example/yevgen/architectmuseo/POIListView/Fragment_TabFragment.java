package com.example.yevgen.architectmuseo.POIListView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.example.yevgen.architectmuseo.CamActivity;
import com.example.yevgen.architectmuseo.POINotification.Activity_POIActivity;
import com.example.yevgen.architectmuseo.POINotification.Object_POI;
import com.example.yevgen.architectmuseo.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;

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
                    url = "http://users.metropolia.fi/~dinhtr/arkkitehtuurimuseo_by_metropolia/includes/poi.json";
                    break;
                case 1:
                    //setListViewByMostviewed();
                    break;
                case 2:
                    //setListViewByRecomend();
                    break;
                default:
                    url = "http://users.metropolia.fi/~dinhtr/arkkitehtuurimuseo_by_metropolia/includes/poi.json";
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

                            String name = null;
                            double lat = 0;
                            double lng = 0;
                            try {
                                name = response.getJSONObject(i).getString("name");
                                lat = response.getJSONObject(i).getDouble("lat");
                                lng = response.getJSONObject(i).getDouble("lng");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Object_POI temp = new Object_POI(lat, lng, name, i);
                            result.add(temp);
                        }

                        adapter = new StableArrayAdapter(getContext(), result);
                        listView.setAdapter(adapter);

                        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent();
                                intent.putExtra(Activity_POIActivity.ARG_Name, result.get(position).getName());
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
            TextView Coordi = (TextView)rowView.findViewById(R.id.POIRowSecLine);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.POIRowImage);

            Title.setText(values.get(position).getName());
            Coordi.setText("lat: "+values.get(position).getLatitude()+" lng: "+values.get(position).getLongitude());
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
