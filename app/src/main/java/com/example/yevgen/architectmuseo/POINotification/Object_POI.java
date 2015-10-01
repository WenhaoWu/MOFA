package com.example.yevgen.architectmuseo.POINotification;

import android.util.Log;

/**
 * Created by wenhaowu on 14/09/15.
 */
public class Object_POI {
    private double latitude;
    private double longitude;
    private String name;
    private String imgBase64;
    private int ID;


    public Object_POI(double latitude, double longitude, String name, int ID, String imgBase64) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.ID = ID;
        this.imgBase64 = imgBase64;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return  name;
    }

    public void printPOI (){
        Log.e("POI", "Name: "+name +" Lat: " + this.latitude + " Long: " + this.longitude);
    }

    public String getImgBase64() {
        return imgBase64;
    }

    @Override
    public String toString() {
        return "Name: "+name;
    }
}
