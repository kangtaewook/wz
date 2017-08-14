package com.vinetech.ui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomPagerView extends ViewPager {
	
	private IPageControl iPageControl = null;
	
	float DIFF_VALUE = 150.0f;
	float mInterceptDownY = 0.0f;
	float mTouchDownY = 0.0f;
	
	public CustomPagerView(Context context) {
		super(context);
	}

	public CustomPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
	}
	
	 @Override
    public void setAdapter(PagerAdapter adapter) {
		super.setAdapter(adapter);
		
		if (iPageControl != null) {
			iPageControl.setPageSize(adapter.getCount());
		}
		
		if(adapter != null && adapter.getCount() != 0){
			setCurrentItem(0);
		}
    }
	 
	 @Override
	protected void onPageScrolled(int arg0, float arg1, int arg2) {
		super.onPageScrolled(arg0, arg1, arg2);
		
		if (iPageControl != null) {
			iPageControl.setPageIndex(arg0);
		}
	}
	 
	 @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean ret = super.onInterceptTouchEvent(ev);
        float mMoveY = 0.0f;
        float mDifY = 0.0f;
        
        if(MotionEvent.ACTION_DOWN ==  ev.getAction()){
        	mInterceptDownY = ev.getY();
        }
        
        if(MotionEvent.ACTION_MOVE == ev.getAction() || MotionEvent.ACTION_CANCEL == ev.getAction()){
        	mMoveY = ev.getY();
        }
        
        mDifY = mInterceptDownY - Math.abs(mMoveY);
        
        if(ret && (Math.abs(mDifY) < DIFF_VALUE)){
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        
//        else{
//        	getParent().requestDisallowInterceptTouchEvent(false);
//        }
        return ret;
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub

        boolean ret = super.onTouchEvent(ev);
        
        float mMoveY = 0.0f;
        float mDifY = 0.0f;
        
        if(MotionEvent.ACTION_DOWN ==  ev.getAction()){
        	mTouchDownY = ev.getY();
        }
        
        if(MotionEvent.ACTION_MOVE == ev.getAction() || MotionEvent.ACTION_CANCEL == ev.getAction()){
        	mMoveY = ev.getY();
        }
        
        mDifY = mTouchDownY - Math.abs(mMoveY);
        
        if(ret && (Math.abs(mDifY) < DIFF_VALUE)){
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        
//        else{
//        	getParent().requestDisallowInterceptTouchEvent(false);
//        }
        return ret;
    }
	    
    @Override
    public void setCurrentItem(int item) {
    	item = getOffsetAmount() + (item % getAdapter().getCount());
    	super.setCurrentItem(item);

    }

    private int getOffsetAmount() {
		if (getAdapter() instanceof InfinitePagerAdapter) {
		    InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
		    return infAdapter.getRealCount() * 100;
		} else {
		    return 0;
		}
    }
    
    public void setPageControl(IPageControl c) {
		iPageControl = c;
    }
}
