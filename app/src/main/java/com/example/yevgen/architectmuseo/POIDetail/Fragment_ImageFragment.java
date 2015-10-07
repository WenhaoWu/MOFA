package com.example.yevgen.architectmuseo.POIDetail;

import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
            Poi_Image.setImageBitmap(decodedByte);
        }

        return rootView;
    }
}
