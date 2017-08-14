package com.vinetech.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by galuster3 on 2017-03-06.
 */

public class UninterceptableViewPager extends ViewPager {

    public UninterceptableViewPager(Context context){
        super(context);
    }

    public UninterceptableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = super.onInterceptTouchEvent(ev);
        if (ret)
            getParent().requestDisallowInterceptTouchEvent(true);
        return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean ret = super.onTouchEvent(ev);
        if (ret)
            getParent().requestDisallowInterceptTouchEvent(true);
        return ret;
    }
}