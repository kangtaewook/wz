package com.vinetech.wezone.Common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.vinetech.imagecrop.HighlightView;
import com.vinetech.imagecrop.ImageViewTouchBase;

import java.util.ArrayList;

public final class CropImageView extends ImageViewTouchBase
{
	private Activity_ImageCrop mActivity;
	
	private ArrayList<HighlightView> mHighlightViews = new ArrayList<HighlightView>();
	
	private HighlightView mMotionHighlightView;
	
	private float mLastX, mLastY;
    
	private int mMotionEdge;
    
    private boolean isRotationHighlightView;
    
    private boolean mIsHiddenHighlightView;

    public CropImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        
        mActivity = (Activity_ImageCrop)context;
    }
    

    public HighlightView getHighlightView()
    {
		return (mHighlightViews.isEmpty()==false)?mHighlightViews.get(0):null;
	}
    
    public ArrayList<HighlightView> getHighlightViews()
    {
		return mHighlightViews;
	}

	public void setHighlightViews(ArrayList<HighlightView> mHighlightViews)
	{
		this.mHighlightViews = mHighlightViews;
	}

	public HighlightView getMotionHighlightView() {
		return mMotionHighlightView;
	}

	public void setMotionHighlightView(HighlightView mMotionHighlightView) {
		this.mMotionHighlightView = mMotionHighlightView;
	}

	public void setHiddenHighlightViews(boolean isHidden)
    {
		mIsHiddenHighlightView = isHidden;
    	for (HighlightView hv : mHighlightViews)
    		hv.setHidden(isHidden);
    }
    
    public void clearHighlightViews()
    {
    	mHighlightViews.clear();	
    }

    @Override
    protected void onLayout(boolean changed, int left, int top,
                            int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);
        
        if( mIsHiddenHighlightView == false && mBitmapDisplayed.getBitmap() != null) 
        {
            for (HighlightView hv : mHighlightViews) 
            {
                hv.mMatrix.set(getImageMatrix());
                hv.invalidate();
                if (hv.mIsFocused) 
                {
                    centerBasedOnHighlightView(hv);
                }
            }
        }
    }
    
    @Override
    protected void zoomTo(float scale, float centerX, float centerY) {

        super.zoomTo(scale, centerX, centerY);
        for (HighlightView hv : mHighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomIn() {

        super.zoomIn();
        for (HighlightView hv : mHighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomOut() {

        super.zoomOut();
        for (HighlightView hv : mHighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void postTranslate(float deltaX, float deltaY) {

        super.postTranslate(deltaX, deltaY);

        for (int i = 0, count = mHighlightViews.size(); i < count; i++) 
        {
            HighlightView hv = mHighlightViews.get(i);
            hv.mMatrix.postTranslate(deltaX, deltaY);
            hv.invalidate();
        }
    }

    // According to the event's position, change the focus to the first
    // hitting cropping rectangle.
    private void recomputeFocus(MotionEvent event) {

    	for (int i = 0, count = mHighlightViews.size(); i < count; i++) 
        {
            HighlightView hv = mHighlightViews.get(i);
            hv.setFocus(false);
            hv.invalidate();
        }

    	for (int i = 0, count = mHighlightViews.size(); i < count; i++) 
        {
            HighlightView hv = mHighlightViews.get(i);
            int edge = hv.getHit(event.getX(), event.getY());
            if (edge != HighlightView.GROW_NONE) 
            {
                if (!hv.hasFocus()) 
                {
                    hv.setFocus(true);
                    hv.invalidate();
                }
                break;
            }
        }
        invalidate();
    }

    

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if( mActivity.mSaving == true ) 
        {
            return false;
        }

        switch (event.getAction()) 
        {
            case MotionEvent.ACTION_DOWN:
            	isRotationHighlightView = false;
            	
                if (mActivity.mWaitingToPick) 
                {
                    recomputeFocus(event);
                } 
                else 
                {
                	HighlightView highlightView = getHighlightView();
                    
                	int edge = highlightView.getHit(event.getX(), event.getY());
                    
                	if( edge == HighlightView.ROTATE )
                	{
                		isRotationHighlightView = true;
                		mActivity.setExchangeAspect();
                	}
                	else if( edge != HighlightView.GROW_NONE ) 
                    {
                        mMotionEdge = edge;
                        mMotionHighlightView = highlightView;
                        mLastX = event.getX();
                        mLastY = event.getY();
                        mMotionHighlightView.setMode(
                                (edge == HighlightView.MOVE)
                                        ? HighlightView.ModifyMode.Move
                                        : HighlightView.ModifyMode.Grow);
                    }                	
                	/*
                    for (int i = 0, count = mHighlightViews.size(); i < count; i++) 
                    {
                        HighlightView hv = mHighlightViews.get(i);
                        int edge = hv.getHit(event.getX(), event.getY());
                        if (edge != HighlightView.GROW_NONE) 
                        {
                            mMotionEdge = edge;
                            mMotionHighlightView = hv;
                            mLastX = event.getX();
                            mLastY = event.getY();
                            mMotionHighlightView.setMode(
                                    (edge == HighlightView.MOVE)
                                            ? HighlightView.ModifyMode.Move
                                            : HighlightView.ModifyMode.Grow);
                            break;
                        }
                    }
                    */
                }
                break;
            case MotionEvent.ACTION_UP:
            	if( isRotationHighlightView == true )
            	{
            		isRotationHighlightView = false;
            		return true;
            	}
            	
                if (mActivity.mWaitingToPick) 
                {
                	HighlightView highlightView = getHighlightView();
                    if (highlightView.hasFocus()) 
                    {
                    	mActivity.mCropHighlightView = highlightView;
                        centerBasedOnHighlightView(highlightView);
                        mActivity.mWaitingToPick = false;
                        return true;
                    }                	
                	/*
                	for (int i = 0, count = mHighlightViews.size(); i < count; i++)  
                    {
                        HighlightView hv = mHighlightViews.get(i);
                        if (hv.hasFocus()) 
                        {
                            cropImage.mCropHighlightView = hv;
                            for (int j = 0; j < count; j++) 
                            {
                                if (j == i) {
                                    continue;
                                }
                                mHighlightViews.get(j).setHidden(true);
                            }
                            centerBasedOnHighlightView(hv);
                            ((Activity_ImageCrop) mContext).mWaitingToPick = false;
                            return true;
                        }
                    }
                    */
                } 
                else if (mMotionHighlightView != null) 
                {
                    centerBasedOnHighlightView(mMotionHighlightView);
                    mMotionHighlightView.setMode(HighlightView.ModifyMode.None);
                }
                mMotionHighlightView = null;
                break;
                
            case MotionEvent.ACTION_MOVE:
            	if( isRotationHighlightView == true )
            		return true;
            	
                if (mActivity.mWaitingToPick) 
                {
                    recomputeFocus(event);
                } 
                else if (mMotionHighlightView != null) 
                {
                    mMotionHighlightView.handleMotion(mMotionEdge,
                            event.getX() - mLastX,
                            event.getY() - mLastY);
                    mLastX = event.getX();
                    mLastY = event.getY();

                    ensureVisible(mMotionHighlightView);
                }
                break;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                center(true, true);
                break;
            case MotionEvent.ACTION_MOVE:
                // if we're not zoomed then there's no point in even allowing
                // the user to move the image around.  This call to center puts
                // it back to the normalized location (with false meaning don't
                // animate).
                if (getScale() == 1F) {
                    center(true, true);
                }
                break;
        }

        return true;
    }

    // Pan the displayed image to make sure the cropping rectangle is visible.
    private void ensureVisible(HighlightView hv) {

        Rect r = hv.mDrawRect;

        int panDeltaX1 = Math.max(0, mLeft - r.left);
        int panDeltaX2 = Math.min(0, mRight - r.right);

        int panDeltaY1 = Math.max(0, mTop - r.top);
        int panDeltaY2 = Math.min(0, mBottom - r.bottom);

        int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
        int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;

        if (panDeltaX != 0 || panDeltaY != 0) {
            panBy(panDeltaX, panDeltaY);
        }
    }

    // If the cropping rectangle's size changed significantly, change the
    // view's center and scale according to the cropping rectangle.
    private void centerBasedOnHighlightView(HighlightView hv) {

        Rect drawRect = hv.mDrawRect;

        float width = drawRect.width();
        float height = drawRect.height();

        float thisWidth = getWidth();
        float thisHeight = getHeight();

        float z1 = thisWidth / width * .6F;
        float z2 = thisHeight / height * .6F;

        float zoom = Math.min(z1, z2);
        zoom = zoom * this.getScale();
        zoom = Math.max(1F, zoom);
        if ((Math.abs(zoom - getScale()) / zoom) > .1) {
            float[] coordinates = new float[]{hv.mCropRect.centerX(),
                    hv.mCropRect.centerY()};
            getImageMatrix().mapPoints(coordinates);
            zoomTo(zoom, coordinates[0], coordinates[1], 300F);
        }

        ensureVisible(hv);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        for (int i = 0; i < mHighlightViews.size(); i++) {
            mHighlightViews.get(i).draw(canvas);
        }
    }

    public void add(HighlightView hv) {

        mHighlightViews.add(hv);
        invalidate();
    }
}
