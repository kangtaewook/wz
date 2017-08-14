package com.vinetech.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.vinetech.wezone.R;


//페이지 표시 컨트롤 
public class PageControl extends View implements IPageControl {
	
	private static final float RADIUS = 25;
	private static final int PADDING = 25;
	
	private int mWidth = 0;
	
	private int mPageSize;
	private int mCurrentPage;
	
	private float mRadius = RADIUS;
	private int mPadding = PADDING;
	
	private Paint mPaint;
	
	private Context mContext = null;
	
	
	public PageControl(Context context) {
		super(context);
		mContext = context;
		initialization(null);
	}

	public PageControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initialization(attrs);
	}
		
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {	
//		setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
//	}
//	
//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right,
//			int bottom) {
//		super.onLayout(changed, left, top, right, bottom);
//	}
	
	private Paint mPaint2;

	private Bitmap image;
	private Bitmap image2;
	
	
	private final void initialization(AttributeSet attrs) {
		
		//미선택
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.LTGRAY);
		
		//선택
		mPaint2 = new Paint();
		mPaint2.setAntiAlias(true);
		mPaint2.setColor(Color.WHITE);
		
		Resources r = mContext.getResources();
		
		image = BitmapFactory.decodeResource(r, R.drawable.guide_more);
		image2 = BitmapFactory.decodeResource(r, R.drawable.guide_more_np);
		
		
//		mRadius = r.getDimension(R.dimen.padding_size_32px);
//		mPadding = (int) r.getDimension(R.dimen.padding_size_10px);
		
		TypedArray a = mContext.obtainStyledAttributes(attrs,R.styleable.PageControl);
			
		if (a.hasValue(R.styleable.PageControl_controlColor)) {
			ColorStateList colors = a.getColorStateList(R.styleable.PageControl_controlColor);
			mPaint.setColor(colors.getDefaultColor());
		}

		if (a.hasValue(R.styleable.PageControl_controlOnColor)) {
			ColorStateList colors = a.getColorStateList(R.styleable.PageControl_controlOnColor);
			mPaint2.setColor(colors.getDefaultColor());
		}
		
		if (a.hasValue(R.styleable.PageControl_controlRadius)) {
			mRadius = a.getDimensionPixelOffset(R.styleable.PageControl_controlRadius, (int) RADIUS);
			mRadius /= 2;
		}

		if (a.hasValue(R.styleable.PageControl_controlPadding)) {
			mPadding = a.getDimensionPixelOffset(R.styleable.PageControl_controlPadding, PADDING);
		}
		
		
	}
			
	public int getCurrentPageIndex() {
		return mCurrentPage;
	}
	
	public void setPageSize (int size) {
		mPageSize = size;
		
		invalidate();
	}
	
	public int getPageSize() {
		return mPageSize;
	}
	
	public void setPageIndex(int index) {
		mCurrentPage = index;
		
		invalidate();
	}
		
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
			
		float x = (getWidth() / 2) - ( ((mPageSize * (mRadius * 2)) + (mPadding * mPageSize)) /2);
		float y = mRadius;
				
		for (int i = 0; i < mPageSize; i++) {
			if (mCurrentPage == i) {

				canvas.drawBitmap(image2, x, y, mPaint);
				
//				canvas.drawCircle(x, 
//						y, 
//						mRadius, 
//						mPaint2);
			} else {
				
				canvas.drawBitmap(image, x, y, mPaint);
				
//				canvas.drawCircle(x, 
//						y, 
//						mRadius, 
//						mPaint);
			}
			x += (mRadius * 2) + mPadding;
		}
	}

}
