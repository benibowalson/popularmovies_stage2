package com.example.android.popularmoviesstage2.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.android.popularmoviesstage2.fragments.overviewFragment;
import com.example.android.popularmoviesstage2.fragments.reviewsFragment;
import com.example.android.popularmoviesstage2.fragments.trailersFragment;


/**
 * Created by 48101040 on 5/11/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    int tabCount;
    //Constructor
    public PagerAdapter(FragmentManager fragmentManager, int tabCount){
        super(fragmentManager);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new overviewFragment();
            case 1:
                return new reviewsFragment();
            case 2:
                return new trailersFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
