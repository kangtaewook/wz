package com.vinetech.wezone.Main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;

import com.vinetech.ui.CardAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventFragmentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {

    private List<EventFragment> fragments;
    private float baseElevation;

    public EventFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();

        for(int i = 0; i< 8; i++){
            addCardFragment(new EventFragment());
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
        return EventFragment.getInstance(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        fragments.set(position, (EventFragment) fragment);
        return fragment;
    }

    public void addCardFragment(EventFragment fragment) {
        fragments.add(fragment);
    }

}
