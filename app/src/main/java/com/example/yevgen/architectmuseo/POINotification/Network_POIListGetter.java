package com.example.yevgen.architectmuseo.POINotification;

import java.util.ArrayList;

/**
 * Created by wenhaowu on 14/09/15.
 */
public class Network_POIListGetter {

    private static final String TAG = "POIList";

    private ArrayList<Object_POI> POIList;

    public Network_POIListGetter() {
        POIList= new ArrayList<Object_POI>();
        POIList.add(new Object_POI(60.221345 , 24.804708, "School", 1, null, null, 0, 0.0, 0));
        POIList.add(new Object_POI(60.221345, 24.804, "Stadium", 2, null, null, 0, 0.0, 0));
    }


    public void printList(){
        for (Object_POI temp : this.POIList){
            temp.printPOI();
        }
    }

    public String getPOIName(int index){
        return POIList.get(index).getName();
    }

    public double getDisLat (int index){
        return POIList.get(index).getLatitude();
    }

    public double getDisLong (int index){
        return POIList.get(index).getLongitude();
    }

    public int getSize(){return POIList.size();}

}
