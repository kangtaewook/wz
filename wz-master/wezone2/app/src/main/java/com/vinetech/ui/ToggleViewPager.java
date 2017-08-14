package com.vinetech.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

//CKY : View대상 이미지뷰의 zoom변경시 뷰페이저 이벤트처리 안하기위함 
public final class ToggleViewPager extends ViewPager
{
    private boolean isPagingEnabled;
    
	public ToggleViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        
        this.isPagingEnabled = true;
    }
    
    public void setPagingEnabled(boolean isPagingEnabled) { this.isPagingEnabled = isPagingEnabled; }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return (isPagingEnabled) && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
    	return (isPagingEnabled) && super.onInterceptTouchEvent(event);
    }


}