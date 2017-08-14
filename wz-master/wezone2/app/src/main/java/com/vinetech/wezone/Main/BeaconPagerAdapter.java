package com.vinetech.wezone.Main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;

import com.vinetech.ui.CardAdapter;
import com.vinetech.wezone.Data.Data_Beacon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class BeaconPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {

    private MainActivity context;
    private FragmentManager fragmentManager;

    private ArrayList<Data_Beacon> beaconInfoList;

    private List<BeaconFragment> fragments;
    private float baseElevation;

    public BeaconPagerAdapter(MainActivity context, FragmentManager fm, ArrayList<Data_Beacon> beaconInfos, float baseElevation) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;

        this.beaconInfoList = beaconInfos;

        fragments = new ArrayList<>();
        this.baseElevation = baseElevation;

        for(int i = 0; i< this.beaconInfoList.size(); i++){
            addCardFragment(new BeaconFragment());
        }
    }

    @Override
    public float getBaseElevation() {
        return baseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return fragments.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return  BeaconFragment.newInstance(context, position, this.beaconInfoList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        fragments.set(position, (BeaconFragment) fragment);
        return fragment;
    }

    public void addCardFragment(BeaconFragment fragment) {
        fragments.add(fragment);
    }
}
