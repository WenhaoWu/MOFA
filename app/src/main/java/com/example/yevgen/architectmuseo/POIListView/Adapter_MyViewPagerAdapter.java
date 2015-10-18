package com.example.yevgen.architectmuseo.POIListView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by wenhaowu on 23/09/15.
 */
public class Adapter_MyViewPagerAdapter extends FragmentPagerAdapter{

    private final int PAGE_COUNT = 3;
    private String locatStr;

    private String tabTitles[] = new String[]{"Near", "Popular", "Suggest"};


    public Adapter_MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return Fragment_TabFragment.newInstance(0, getLocationStr());
            case 1:
                return Fragment_TabFragment.newInstance(1, getLocationStr());
            case 2:
                return Fragment_TabFragment.newInstance(2,getLocationStr());
            default:
                return Fragment_TabFragment.newInstance(0,getLocationStr());
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public CharSequence getPageTitle(int position){
        return tabTitles[position];
    }

    public String getLocationStr (){
        return locatStr;
    }

    public void setLocatStr(String locatStr) {
        this.locatStr = locatStr;
    }
}
