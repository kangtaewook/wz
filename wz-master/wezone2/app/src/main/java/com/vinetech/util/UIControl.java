package com.vinetech.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputFilter;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class UIControl {

	public static ImageButton setButtonImage(Activity activity, int buttonId, final int defaultImage, final int selectImage) {
		
		ImageButton button = (ImageButton)activity.findViewById(buttonId);
		button.setOnTouchListener(new ImageButton.OnTouchListener() {
			
			public boolean onTouch(View view, MotionEvent event) {
				
				int nAction = event.getAction();
				ImageButton touchButton = (ImageButton)view;
				
				if(nAction == MotionEvent.ACTION_DOWN){
					touchButton.setBackgroundResource(selectImage);
				} else {
					touchButton.setBackgroundResource(defaultImage);
				}
				
				return false;
			}
		});
		
		return button;
	}
	

	
	public static View setButtonColor(View view, final int defaultColor, final int selectColor) {
		
		if(view != null){
			view.setOnTouchListener(new ImageButton.OnTouchListener() {
				
				public boolean onTouch(View view, MotionEvent event) {
					
					int nAction = event.getAction();
					View touchButton = view;
					
					if(nAction == MotionEvent.ACTION_DOWN){
						touchButton.setBackgroundColor(selectColor);
					} else {
						touchButton.setBackgroundColor(defaultColor);
					}
					
					return false;
				}
			});	
		}
		
		return view;
	}
	
	public static View setButtonImage(View view, final int defaultImage, final int selectImage) {
		
		if(view != null){
			view.setOnTouchListener(new ImageButton.OnTouchListener() {
				
				public boolean onTouch(View view, MotionEvent event) {
					
					int nAction = event.getAction();
					View touchButton = view;
					
					if(nAction == MotionEvent.ACTION_DOWN){
						touchButton.setBackgroundResource(selectImage);
					} else {
						touchButton.setBackgroundResource(defaultImage);
					}
					
					return false;
				}
			});	
		}
		
		return view;
	}
	
	public static View setButtonImage(View view, final View changedView, final int defaultImage, final int selectImage) {
		
		if(view != null){
			view.setOnTouchListener(new ImageButton.OnTouchListener() {
				
				public boolean onTouch(View view, MotionEvent event) {
					
					int nAction = event.getAction();
					
					if(nAction == MotionEvent.ACTION_DOWN){
						changedView.setBackgroundResource(selectImage);
					} else {
						changedView.setBackgroundResource(defaultImage);
					}
					
					return false;
				}
			});	
		}
		
		return view;
	}
	
	public static TextView setTextView(View view, int textViewId, int fontSize, final String strText) {
		
		TextView textView = (TextView)view.findViewById(textViewId);
		textView.setTextSize(fontSize);
		textView.setText(strText);
		
		return textView;
	}

	public static void setTextViewSizePxWithFont(TextView tv, int dimenId){

		if(tv == null)
			return;
		
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv.getResources().getDimension(dimenId));
			tv.setTypeface(Typeface.SANS_SERIF);

	}
	
	public static void setTextViewSizePxWithFont(TextView tv, float textSize){

		if(tv == null)
			return;
		
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			tv.setTypeface(Typeface.SANS_SERIF);

	}
	
	public static void setTextViewSizePxWithFont(Button bt, int dimenId){

		if(bt == null)
			return;
			
		bt.setTextSize(TypedValue.COMPLEX_UNIT_PX, bt.getResources().getDimension(dimenId));
		bt.setTypeface(Typeface.SANS_SERIF);

	}
	
	public static void toggleSoftKeyBoard(Context context)
	{
		InputMethodManager inputMgr = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}
	
	public static void hideSoftKeyBoard(Context context, EditText editText) {
		
		if(editText != null){
			InputMethodManager inputMgr = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			
		}
	}
	
	public static void showSoftKeyBoard(Context context, EditText editText) {
		InputMethodManager inputMgr = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public static void setChangeLinearLayoutMargin(ImageView imageView, int nLeft, int nTop, int nRight, int nBottom){
		FrameLayout.LayoutParams param = (FrameLayout.LayoutParams)imageView.getLayoutParams();
		 param.leftMargin   = nLeft;
		 param.topMargin    = nTop;
		 param.rightMargin  = nRight;
		 param.bottomMargin = nBottom;
		 imageView.setLayoutParams(param);
	}

	public static void setChangeFrameLayoutMargin(FrameLayout layoutFrame, int nLeft, int nTop, int nRight, int nBottom){
		 FrameLayout.LayoutParams param = (FrameLayout.LayoutParams)layoutFrame.getLayoutParams();
		 param.leftMargin   = nLeft;
		 param.topMargin    = nTop;
		 param.rightMargin  = nRight;
		 param.bottomMargin = nBottom;
		 layoutFrame.setLayoutParams(param);
	}

	public static void setChangeFrameLayoutMargin(FrameLayout layoutFrame, int nLeft, int nTop){
		 FrameLayout.LayoutParams param = new LayoutParams(layoutFrame.getLayoutParams());
		 param.leftMargin   = nLeft;
		 param.topMargin    = nTop;
		 layoutFrame.setLayoutParams(param);
	}

	public static void setChangeFrameLayoutSize(FrameLayout FrameLayout, int nWidth, int nHeight){
		android.widget.FrameLayout.LayoutParams param = (android.widget.FrameLayout.LayoutParams)FrameLayout.getLayoutParams();
		param.width  = nWidth;
		param.height = nHeight;
		FrameLayout.setLayoutParams(param);
	}

	public static void setChangeFrameLayoutMargin(SurfaceView surfaceSubView, int nLeft, int nTop, int nRight, int nBottom){
		 FrameLayout.LayoutParams param = (FrameLayout.LayoutParams)surfaceSubView.getLayoutParams();
		 param.leftMargin   = nLeft;
		 param.topMargin    = nTop;
		 param.rightMargin  = nRight;
		 param.bottomMargin = nBottom;
		 surfaceSubView.setLayoutParams(param);
	}

	public static void setChangeFrameLayoutSize(SurfaceView surfaceSubView, int nWidth, int nHeight){
		FrameLayout.LayoutParams param = (FrameLayout.LayoutParams)surfaceSubView.getLayoutParams();
		param.width  = nWidth;
		param.height = nHeight;
		surfaceSubView.setLayoutParams(param);
	}
	
	public static ImageButton setButtonImageToCheckClick(View view, int buttonId, final int defaultImage, final int selectImage) {
		
		final ImageButton button = (ImageButton)view.findViewById(buttonId);
		button.setOnTouchListener(new ImageButton.OnTouchListener() {
			
			public boolean onTouch(View view, MotionEvent event) {
				
				if(button.isClickable() == true){
					int nAction = event.getAction();
					ImageButton touchButton = (ImageButton)view;
					
					if(nAction == MotionEvent.ACTION_DOWN){
						touchButton.setBackgroundResource(selectImage); 
					} else {
						touchButton.setBackgroundResource(defaultImage);			
					}
					return false;
				}
				return false; 
			}
		});
		
		return button;
	}
	
	public static ImageView setImageResToStringName(Context context, View view, ImageView imageView, String ResourceName) {
		if(view != null)
		{
			int id = view.getResources().getIdentifier(ResourceName, "drawable", context.getPackageName());
			imageView.setImageResource(id);	
		} 
		
		return imageView;	
	}
	
	public static int getImageResourceFromStringName(Context context, View view, String ResourceName){
		int id = view.getResources().getIdentifier(ResourceName, "drawable", context.getPackageName());
		return id;
	}
	
	public static int getImageResourceFromStringName(Context context, String ResourceName){
		int id = context.getResources().getIdentifier(ResourceName, "drawable", context.getPackageName());
		return id;
	}
	
	public static ColorMatrixColorFilter setColorMatrix(float R, float G, float B, float Alpha){
		ColorMatrix cm = new ColorMatrix(new float[] {
				0, 0, 0, 0, R, 
		 		0, 0, 0, 0, G,
		 		0, 0, 0, 0, B,
		 		0, 0, 0, 1, Alpha});
		
		return new ColorMatrixColorFilter(cm);
	}
	
	public static Bitmap setDarknessBitmap(Bitmap bitmap){
		
		Bitmap bmpOverlay = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
		Canvas canvas 	  = new Canvas(bmpOverlay);
		Paint pnt		  = new Paint();
		
		pnt.setColorFilter(setColorMatrix(0,0,0,0));
		pnt.setAlpha(140);
		
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.drawBitmap(bitmap, 0, 0, pnt);
		
		return bmpOverlay;
	}
	
	public static void setGrayScale(ImageView v){
	    ColorMatrix matrix 		  = new ColorMatrix();
	    matrix.setSaturation(0);
	    ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
	    v.setColorFilter(cf);
	}
	
	public static void setClearColorMatrix(ImageView v){
		v.clearColorFilter();                        //?��?�?��??컬�??��? ??��
		v.invalidate();  
	}
	
	@SuppressLint("NewApi")
	public static byte[] decodeBase64(String strBase64){
    	if(strBase64 != null){
    		return Base64.decode(strBase64, Base64.DEFAULT);
    	} else {
    		return null;
    	}
    }

	public static String decodeBase64WithString(String strTemp){
		if(strTemp != null){

			String data = null;
			try {
				data = new String(Base64.decode(strTemp.getBytes(), Base64.DEFAULT));
			}catch (Exception e){
				e.printStackTrace();
				data = strTemp;
			}

			String decodeStr = null;
			try {
				decodeStr = URLDecoder.decode(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				decodeStr = data;
			} catch (Exception e){
				e.printStackTrace();
				decodeStr = data;
			}

			return decodeStr;
		} else {
			return "";
		}
	}

	public static String encodeBase64WithString(String strTemp){
		if(strTemp != null){
			String result = null;

			try {
				result = URLEncoder.encode(strTemp, "UTF-8")
						.replaceAll("\\+", "%20")
						.replaceAll("\\%21", "!")
						.replaceAll("\\%27", "'")
						.replaceAll("\\%28", "(")
						.replaceAll("\\%29", ")")
						.replaceAll("\\%7E", "~");
			}

			// This exception should never occur.
			catch (UnsupportedEncodingException e) {
				result = strTemp;
			}

			String baseStr = Base64.encodeToString(result.getBytes(), Base64.DEFAULT).toString();
			return baseStr;

		} else {
			return "";
		}
	}

    public static Bitmap base64ByteStringToBitmap(byte[] byteArray){
    	
    	if(byteArray != null){
    		
        	ByteArrayInputStream inStream = new ByteArrayInputStream(byteArray);
        	Bitmap bm = BitmapFactory.decodeStream(inStream) ;
        	
        	return bm;
        	
    	} else {
    		
    		return null;
    		
    	}
    }
	
	public static void setVisibility(ImageButton imageButton, int nVisibleState){
		if(imageButton != null){
			if(imageButton.getVisibility() != nVisibleState){
				imageButton.setVisibility(nVisibleState);
			}
		}
	}
	
	public static void setVisibility(View view, int nVisibility){
		if(view != null){
			switch (nVisibility) {
			case View.VISIBLE:
				
				if(view.getVisibility() != View.VISIBLE) {
					view.setVisibility(View.VISIBLE);
				}
				
			break;

			case View.INVISIBLE:
				
				if(view.getVisibility() != View.INVISIBLE) {
					view.setVisibility(View.INVISIBLE);
				}
				
			break;
			
			case View.GONE:
				
				if(view.getVisibility() != View.GONE) {
					view.setVisibility(View.GONE);
				}
				
			break;
			
			default:
				
				if(view.getVisibility() != View.VISIBLE) {
					view.setVisibility(View.VISIBLE);
				}
				
			break;
			}
		}
	}
	
	/**
	 * Get the screen height.
	 * 
	 * @param context
	 * @return the screen height
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getScreenHeight(Activity context) {

		Display display = context.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.y;
		}
		return display.getHeight();
	}

	/**
	 * Get the screen width.
	 * 
	 * @param context
	 * @return the screen width
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getScreenWidth(Activity context) {

		Display display = context.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.x;
		}
		return display.getWidth();
	}
	
	public static int getDeviceWidth(Context context){
		DisplayMetrics displaymetrics = new DisplayMetrics();
//        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);  
		displaymetrics = context.getResources().getDisplayMetrics();
        return displaymetrics.widthPixels;
	}
	
	public static void setHidden_FrameLayout(FrameLayout frameLayoutRoot, FrameLayout hiddenFrameLayout) {
		frameLayoutRoot.removeView(hiddenFrameLayout);
		hiddenFrameLayout = null;
	}
	
	public static float getDpSize(Context context){
		WindowManager wm = ((Activity) context).getWindowManager(); // getWindowManager는 Activity 의 메쏘드
		Display dp = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		dp.getMetrics(metrics);
		return metrics.density; 			// <- density/160 으로 계산된 값 출력 (ex, 160 dpi 해상도의 경우 1.0 출력)
//		return metrics.densityDpi; 	// <- density 출력 (160 dpi 해상도의 경우 160 출력)
	}
	

	public static float getDp2Px(Context context, float dp){
			
		if(context == null){
			return 0L;
		}
	
		float density = context.getResources().getDisplayMetrics().density;
		return density * dp;
		
	}
	
	public static float getPx2Dp(Context context, float px){

		if(context == null){
			return 0L;
		}
		
		float density = context.getResources().getDisplayMetrics().density;
		return px / density;
	}


	
	public static boolean setMaxText(EditText editText, int maxTextCount){
		
		if(editText == null){
			return false;
		}
		
		if(maxTextCount <= 0){
			return false;
		}
		
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(maxTextCount);
		editText.setFilters(FilterArray);
		
		return true;
	}

	public static void setBackgroundDrawable(Context c, View v, int drawableId){
		
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			v.setBackground(c.getResources().getDrawable(drawableId));
		}else{
			v.setBackgroundResource(drawableId);
		}
	}
	
	public static void setBackgroundBitmap(Context c, View v, Bitmap bm){
		
		 Drawable img = new BitmapDrawable(c.getResources(),bm);
		 
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			v.setBackground(img);
		}else{
			v.setBackgroundDrawable(img);
		}
	}

}