package com.example.yevgen.architectmuseo.POIDetail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.yevgen.architectmuseo.R;

/**
 * Created by wenhaowu on 07/10/15.
 */
public class Fragment_ImageFragment extends Fragment {

    private static final String PIC_URI = "PictureURI";
    private static final String PIC_Index = "PictureIndex";

    public static Fragment_ImageFragment newInstance(final String imgBase64){
        Bundle arguments = new Bundle();
        arguments.putString(PIC_URI, imgBase64);

        Fragment_ImageFragment fragment = new Fragment_ImageFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_poi_detail, container, false);
        ImageView Poi_Image = (ImageView)rootView.findViewById(R.id.fragment_image);

        Bundle arguments = getArguments();
        if (arguments != null){
            String imgbase64 = arguments.getString(PIC_URI);
            byte[] decodedString = Base64.decode(imgbase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            /**/
            //keep the ratio
            Bitmap idealBM = scaleBitmap(decodedByte, Poi_Image);
            Poi_Image.setImageBitmap(idealBM);


            /*
            //dont keep the ratio
            BitmapDrawable ob = new BitmapDrawable(getResources(), decodedByte);
            Poi_Image.setBackground(ob);
            */
        }

        Poi_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), Activity_ImageFullView.class);
                startActivity(intent);
            }
        });

        return rootView;
    }


    private Bitmap scaleBitmap(Bitmap bitMap, ImageView imageView){
        int bmWidth=bitMap.getWidth();
        int bmHeight=bitMap.getHeight();

        int ivWidth=getActivity().getResources().getDisplayMetrics().widthPixels;
        int ivHeight=getActivity().getResources().getDisplayMetrics().heightPixels;

        int new_width=ivWidth;
        int new_height = (int) Math.floor((double) bmHeight *( (double) new_width / (double) bmWidth));

        Log.e("ImageView", "wid="+new_width+" hei="+new_height);

        Bitmap newbitMap = Bitmap.createScaledBitmap(bitMap,new_width,new_height, true);

        return newbitMap;
    }
}
