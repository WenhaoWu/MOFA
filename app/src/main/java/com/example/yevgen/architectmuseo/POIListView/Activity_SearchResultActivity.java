package com.example.yevgen.architectmuseo.POIListView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.yevgen.architectmuseo.Constains_BackendAPI_Url;
import com.example.yevgen.architectmuseo.POIDetail.Activity_POIActivity;
import com.example.yevgen.architectmuseo.Object_POI;
import com.example.yevgen.architectmuseo.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Activity_SearchResultActivity extends AppCompatActivity {

    public static final String Tag_SearchQuery = "SEARCH_QUERY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity__search_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String searchQuery = getIntent().getStringExtra(Tag_SearchQuery);
        String url = Constains_BackendAPI_Url.URL_POISearch+ searchQuery;

        final ListView searchList = (ListView)findViewById(R.id.POISearchListview);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("SearchResultSize", response.length() + "");
                        if (response.length() == 0){
                            Toast.makeText(getBaseContext(), "Nothing to show", Toast.LENGTH_LONG).show();
                        }
                        else {
                            final ArrayList<Object_POI> result = new ArrayList<>();
                            for (int i=0; i<response.length();i++){
                                String name = null, imgBase64 = null;
                                int id = 0;
                                try {
                                    name = response.getJSONObject(i).getString("poi_name");
                                    imgBase64 = response.getJSONObject(i).getString("compressed_image");
                                    id = response.getJSONObject(i).getInt("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Object_POI temp = new Object_POI(0, 0, name, id, imgBase64,null,0,0, 0, null, 0,null);
                                result.add(temp);
                            }

                            searchArrayAdapter adapter = new searchArrayAdapter(getBaseContext(),result);
                            searchList.setAdapter(adapter);

                            searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Activity_POIActivity.ARG_ID, result.get(position).getID());
                                    intent.setClass(getBaseContext(), Activity_POIActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SearchVolleyError", error.toString());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        queue.add(jsonArrayRequest);

    }

    private class searchArrayAdapter extends ArrayAdapter<Object_POI>{

        private Context mContext;
        private List<Object_POI> values;

        public searchArrayAdapter(Context context, List<Object_POI> objects) {
            super(context, -1, objects);
            this.mContext = context;
            this.values = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.layout_poi_list_row_layout, parent, false);

            TextView Title = (TextView)rowView.findViewById(R.id.POIRowFriLine);
            TextView Descrp = (TextView)rowView.findViewById(R.id.POIRowSecLine);
            ImageView imgView = (ImageView)rowView.findViewById(R.id.POIRowImage);

            byte[] decodedString = Base64.decode(values.get(position).getImgBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            BitmapDrawable ob = new BitmapDrawable(getResources(),decodedByte);
            imgView.setBackground(ob);

            Title.setText(values.get(position).getName());

            return rowView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity__poi, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager)getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent();
                intent.putExtra(Activity_SearchResultActivity.Tag_SearchQuery, query);
                intent.setClass(getBaseContext(), Activity_SearchResultActivity.class);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };

        searchView.setOnQueryTextListener(listener);

        return true;
    }

}






















