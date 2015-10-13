package com.example.yevgen.architectmuseo.POIDetail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
        /*
        Poi_Image.setAdjustViewBounds(true);
        Poi_Image.setMaxHeight(Poi_Image.getHeight());
        Poi_Image.setMaxWidth(Poi_Image.getWidth());
        Poi_Image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        */
        int width = getActivity().getResources().getDisplayMetrics().widthPixels;
        int height= getActivity().getResources().getDisplayMetrics().heightPixels;
        Log.e("ImageView", width+" and "+height);
        Bundle arguments = getArguments();
        if (arguments != null){
            String imgbase64 = arguments.getString(PIC_URI);
            byte[] decodedString = Base64.decode(imgbase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Bitmap SizedBm = getSizedBm(decodedByte, width, height/2); // fit 5 inch 1920*1080 vertical screen most
            Poi_Image.setImageBitmap(SizedBm);
        }

        return rootView;
    }

    private Bitmap getSizedBm(Bitmap bm, int disWidth, int disHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float)disWidth)/width;
        float scaleHeight = ((float)disHeight)/height;
        Matrix matrix = new Matrix();

        //resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new bitmap
        Bitmap result = Bitmap.createBitmap(bm, 0,0, width, height, matrix, false);
        bm.recycle();
        return result;
    }
}
