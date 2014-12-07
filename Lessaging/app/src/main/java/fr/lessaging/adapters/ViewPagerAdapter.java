package fr.lessaging.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import java.util.ArrayList;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> pages;

    public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> pages) {
        super(fm);
        this.pages = pages;
    }

    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence returnValue = null;
        switch (position) {
            case 0:
                returnValue = "SMS";
                break;
            case 1:
                returnValue = "MMS";
                break;
        }
        return returnValue;
    }
}
