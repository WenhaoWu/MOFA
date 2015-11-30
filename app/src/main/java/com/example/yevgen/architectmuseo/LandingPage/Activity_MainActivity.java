package com.example.yevgen.architectmuseo.LandingPage;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.yevgen.architectmuseo.Constains_BackendAPI_Url;
import com.example.yevgen.architectmuseo.POIListView.Fragment_TabFragment;
import com.example.yevgen.architectmuseo.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Activity_MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.Landing_RV);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //make cardView responsive
        View view = View.inflate(this, R.layout.layout_rv_item_cardview, null);
        RelativeLayout rv = (RelativeLayout) view.findViewById(R.id.cardView_RV);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rv.getLayoutParams();
        int height = getResources().getDisplayMetrics().heightPixels;
        params.height = height * 3 / 10;// 30% of the screen
        Log.e("Landing_Height", params.height + "");
        rv.setLayoutParams(params);

        //getting the catagories data from back end and attach the adapter to recycle view
        getCataData(new cataCallBack() {
            @Override
            public void onSuccess(ArrayList<Object_RVItem> Items) {
                mAdapter = new Adapter_RVAdapter(Items, getBaseContext());
                mRecyclerView.setAdapter(mAdapter);
            }
        });


    }


    private interface cataCallBack {
        void onSuccess(ArrayList<Object_RVItem> Items);
    }

    private void getCataData(final cataCallBack callBack) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final ProgressDialog PD = Fragment_TabFragment.createProgressDialog(this);

        JsonArrayRequest cataReq = new JsonArrayRequest(Request.Method.GET, Constains_BackendAPI_Url.URL_GetCatagories, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("CataResponse", response.length() + "");
                        ArrayList<Object_RVItem> result = new ArrayList<>();
                        String cataTemp = null, imgTemp = null;
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                cataTemp = response.getJSONObject(i).getString("cata");
                                imgTemp = response.getJSONObject(i).getString("compressed_image");
                            } catch (JSONException e) {
                                Log.e("CataResponseErr", e.toString());
                            }
                            result.add(new Object_RVItem(cataTemp, imgTemp));
                        }
                        callBack.onSuccess(result);
                        PD.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CataReqErr", error.toString());
                        PD.dismiss();
                    }
                });

        queue.add(cataReq);
        PD.show();
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
        if (id == R.id.action_refresh) {
            getCataData(new cataCallBack() {
                @Override
                public void onSuccess(ArrayList<Object_RVItem> Items) {
                    mAdapter = new Adapter_RVAdapter(Items, getBaseContext());
                    mRecyclerView.setAdapter(mAdapter);
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

