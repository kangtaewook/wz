package com.vinetech.wezone.Main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vinetech.wezone.Wezone.WezoneBoardFragment;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class ZonePagerAdapter extends FragmentStatePagerAdapter{

    public MainActivity mActivity;

    public ArrayList<Fragment> mFragmentList;

    public ZonePagerAdapter(FragmentManager fragmentManager, MainActivity activity){
        super(fragmentManager);
        mActivity = activity;

        mFragmentList = new ArrayList<>();
        mFragmentList.add(new ThemeZoneFragment());
        mFragmentList.add(new WezoneFragment());
        mFragmentList.add(new WezoneBoardFragment());
    }

    @Override
    public Fragment getItem(int position) {

        return mFragmentList.get(position);

//        if(position == 0){
//            return new ThemeZoneFragment();
//        }else{
//            return new WezoneFragment();
//        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
