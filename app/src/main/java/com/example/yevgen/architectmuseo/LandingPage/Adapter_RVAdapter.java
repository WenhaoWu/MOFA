package com.example.yevgen.architectmuseo.LandingPage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yevgen.architectmuseo.POIListView.Activity_POIMainListView;
import com.example.yevgen.architectmuseo.R;

import java.util.ArrayList;

/**
 * Created by wenhaowu on 19/11/15.
 */
public class Adapter_RVAdapter extends android.support.v7.widget.RecyclerView.Adapter<Adapter_RVAdapter.mViewHolder> {

    private ArrayList<Object_RVItem> dataList;
    private Context mContext;

    public Adapter_RVAdapter(ArrayList<Object_RVItem> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CardView cv ;
        public ImageView imgV;
        public TextView txtV;
        public RelativeLayout rv;

        public mViewHolder(View itemView) {
            super(itemView);
            this.cv = (CardView)itemView.findViewById(R.id.Landing_RV_CV);
            this.imgV = (ImageView)itemView.findViewById(R.id.Landing_RV_CV_img);
            this.txtV = (TextView)itemView.findViewById(R.id.Landing_RV_CV_txt);
            this.rv = (RelativeLayout)itemView.findViewById(R.id.cardView_RV);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_item_cardview, parent, false);
        mViewHolder vh = new mViewHolder(v);

        return vh;
    }



    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        byte[] decodedString = Base64.decode(dataList.get(position).getImg_base64(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        holder.imgV.setImageBitmap(decodedByte);
        holder.imgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, Activity_POIMainListView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        holder.txtV.setText(dataList.get(position).getCataName());

        //set height in proportion to screen size
        int proportionalHeight = containerHeight();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, proportionalHeight); // (width, height)
        holder.rv.setLayoutParams(params);

    }

    public int containerHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);
        //get predefined value
        double ratio = 3.0;

        return (int) (dm.heightPixels / ratio);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
