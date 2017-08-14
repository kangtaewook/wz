package com.vinetech.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

/**
 * @author 占쌘울옙占쏙옙
 * @brief  Bitmap 占쏙옙占쏙옙占�占쏙옙틸 클占쏙옙占쏙옙
 * @date  2013. 4. 26. 
 */
public class UIBitmap {
	
	//-----------------------------------------------------------
	// Text 
	//-----------------------------------------------------------
	/**
	 * @brief String 占쏙옙 Width 占싫쇽옙占쏙옙 占쏙옙占싼댐옙.
	 * @date  2013. 4. 26.
	 * @param fontSize  占쏙옙트 占쏙옙占쏙옙占쏙옙
	 * @param str       Width 占싫쇽옙占쏙옙 占쏙옙占쏙옙 String
	 * @return          Width 占싫쇽옙
	 */
	public static int getTextWidth(int fontSize, String str) {
		
		int nWidth		= 0;
		int nLength 	= str.length();
		float[] fWidth	= new float[nLength];
		
		Paint paint	= new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(fontSize);
		paint.getTextWidths(str, fWidth);
		for(int i = 0; i < nLength; i++) {
			nWidth += (int)fWidth[i];
		}
		
		return nWidth;
	}

	/**
	 * @brief 占쏙옙트 占쏙옙占쏙옙占쏙옙占쏙옙 Height 占싫쇽옙占쏙옙 占쏙옙占싼댐옙.
	 * @date 2013. 4. 26.
	 * @param fontSize 占쏙옙트 占쏙옙占쏙옙占쏙옙
	 * @return 占쏙옙트占쏙옙 height 占싫쇽옙         
	 */
	public static int getTextHeight(int fontSize) {
		
		Paint paint	= new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(fontSize);
		
		return (int)(paint.getTextSize() * 1);
	}
	
	//-----------------------------------------------------------
	// Bitmap 
	//-----------------------------------------------------------
	/**
	 * @brief			  Resource占싸븝옙占쏙옙 Bitmap占쏙옙 占쏙옙쨈占�
	 * @date 			  2013. 4. 26.		
	 * @param context     context
	 * @param resourceId  Resource Id
	 * @return		      Bitmap
	 */
	public static Bitmap getBitmapFromResource(Context context, int resourceId){
		
		if(context == null){
			return null;
		}
		
		BitmapDrawable Bitmapdrawable = (BitmapDrawable) context.getResources().getDrawable(resourceId);

		if(Bitmapdrawable != null){
			return Bitmapdrawable.getBitmap();			
		}

		return null;
	}
	
	/**
	 * @brief				Resource占싸븝옙占쏙옙 占쏙옙占쏙옙占쏙옙징 占쏙옙 drawable占쏙옙 占쏙옙쨈占�			
	 * @date 			    2013. 4. 26.		
	 * @param context  		Context
	 * @param resName		Resource Id
	 * @param nResizeWidth  占쏙옙占쏙옙占쏙옙징 占쏙옙 Width
	 * @param nResizeHeight 占쏙옙占쏙옙占쏙옙징 占쏙옙 Height
	 * @return				占쏙옙占쏙옙占쏙옙징 占쏙옙 Drawable / 占쏙옙占쏙옙 占쌍다몌옙 null
	 */
	public static Drawable getResizeDrawable(Context context, int resName, int nResizeWidth, int nResizeHeight) {
		
		if(context == null){
			return null;
		}

		BitmapDrawable Bitmapdrawable = (BitmapDrawable) context.getResources().getDrawable(resName);
		Bitmap bitmap 				  = Bitmapdrawable.getBitmap();
		bitmap 						  = getResizeBitmap(bitmap, nResizeWidth, nResizeHeight);
		
		if(bitmap == null){
			return null;
		}
		
		Drawable drawable 			  = new BitmapDrawable(bitmap);
		
		return drawable;			
	}
	
	/**
	 * @brief					占쏙옙 Bitmap占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙징 占쏙옙 Bitmap占쏙옙 占쏙옙쨈占�		
	 * @date 					2013. 4. 26.		
	 * @param bitmapImage		占쏙옙 Bitmap
	 * @param nResizedWidth     占쏙옙占쏙옙占쏙옙징 占쏙옙 Width
	 * @param nResizedHeight	占쏙옙占쏙옙占쏙옙징 占쏙옙 Height
	 * @return					占쏙옙占쏙옙占쏙옙징 占쏙옙 Bitmap / 占쏙옙占쏙옙 占쌍다몌옙 null
	 */
	public static Bitmap getResizeBitmap(Bitmap bitmapImage, int nResizedWidth , int nResizedHeight){
		
		int nWidth   = nResizedWidth;
		int nHeight  = nResizedHeight;

		if(bitmapImage == null){
			return null;
		}
		
		int bitmapWitdth  = bitmapImage.getWidth();
		int bitmapHeight  = bitmapImage.getHeight();
		
		if(nWidth  == bitmapWitdth &&
		   nHeight == bitmapHeight){
			return bitmapImage;
		}
		return Bitmap.createScaledBitmap(bitmapImage, nWidth, nHeight, true);
	}	


	
	/**
	 * @brief			占쏙옙占쏙옙 占승우가 占쌘바뀐옙 Bitmap 占쏙옙 占쏙옙占쏙옙占쏙옙징 占싹울옙 Return.				
	 * @date 			2013. 4. 26.		
	 * @param image     占쏙옙 Bitmap
	 * @param width     占쏙옙占쏙옙占쏙옙징 占쏙옙 Width
	 * @param height    占쏙옙占쏙옙占쏙옙징 占쏙옙 Height
	 * @return			占쏙옙占쏙옙 占승우가 占쌘바뀐옙占�占쏙옙占쏙옙占쏙옙징占쏙옙 Bitmap
	 */
	public static Bitmap getInvertImage(Bitmap image, int width, int height) {
		Matrix inversion = new Matrix();
		inversion.setScale(-1, 1);
		
		return Bitmap.createBitmap(image, 0, 0, width, height, inversion, false);
	}

	/**
	 * @brief			Bitmap占쏙옙 占쏙옙占싹울옙 占쏙옙占쏙옙占싼댐옙.							
	 * @date 			2013. 4. 26.		
	 * @param nWidth	占쏙옙占�Bitmap占쏙옙 Width
	 * @param nHeight	占쏙옙占�Bitmap占쏙옙 Height
	 * @return			占쏙옙占�Bitmap
	 */
	public static Bitmap createBitmap(int nWidth, int nHeight){
		Bitmap createBitmap = Bitmap.createBitmap(nWidth, nHeight, Config.ARGB_8888);
		return createBitmap;
	}

	public static int getBitmapWidth(Bitmap bitmap) {
		return bitmap.getWidth();
	}
	
	public static int getBitmapHeight(Bitmap bitmap) {
		return bitmap.getHeight();
	}

	/**
	 * @brief					Bitmap 占쏙옙 Center X 占쏙옙占쏙옙 占쏙옙占쏙옙					
	 * @date 					2013. 4. 26.		
	 * @param bitmapResource	占쏙옙 Bitmap
	 * @return				    Bitamp占쏙옙 Center X 占쏙옙 占쏙옙占쏙옙
	 */
	public static int getBitmapCenterPointX(Bitmap bitmapResource) {
		return getBitmapWidth(bitmapResource) / 2;
	}
	
	/**
	 * @brief					Bitmap 占쏙옙 Center Y 占쏙옙占쏙옙 占쏙옙占쏙옙					
	 * @date 					2013. 4. 26.		
	 * @param bitmapResource	占쏙옙 Bitmap
	 * @return				    Bitamp占쏙옙 Center Y 占쏙옙 占쏙옙占쏙옙
	 */
	public static int getBitmapCenterPointY(Bitmap bitmapResource) {
		return getBitmapHeight(bitmapResource) / 2;
	}
	
	/**
	 * @brief				  占쏙옙占쏙옙占쏙옙 占쏙옙恝占�占쌍댐옙 png 占쏙옙占쏙옙占쏙옙 Bitmap占쏙옙占쏙옙 占쌀뤄옙占승댐옙. 			
	 * @date 			      2013. 4. 26.		
	 * @param folderPath      占쏙옙占�占쏙옙占�
	 * @param fileName	      占쏙옙占쏙옙 占싱몌옙   				
	 * @param fileEndName     占쏙옙占쏙옙 占싱몌옙 (占쏙옙占쏙옙占쏙옙)
	 * @return				  占쏙옙환占쏙옙 Bitmap
	 */
	public static Bitmap createBitmapFromPngFile(String folderPath, String fileName, char fileEndName) {
		Bitmap createdBmp = null;
		
		StringBuilder sb  = new StringBuilder();
		
		if(folderPath != null && fileName != null){
			sb.append(folderPath).append(fileName).append(fileEndName).append(".png");			
		} else {
			return null;
		}

		if(sb != null){
			createdBmp = BitmapFactory.decodeFile(sb.toString());
		}

		if(createdBmp != null){
			return createdBmp;			
		} else {
			return null;
		}
	}
	
	/**
	 * @brief				  占쏙옙占쏙옙占쏙옙 占쏙옙恝占�占쌍댐옙 png 占쏙옙占쏙옙占쏙옙 Bitmap占쏙옙占쏙옙 占쌀뤄옙占승댐옙. 			
	 * @date 			      2013. 4. 26.		
	 * @param folderPath      占쏙옙占�占쏙옙占�
	 * @param fileName	      占쏙옙占쏙옙 占싱몌옙   				
	 * @param fileEndName     占쏙옙占쏙옙 占싱몌옙 (占쏙옙占쏙옙占쏙옙)
	 * @return				  占쏙옙환占쏙옙 Bitmap
	 */
	public static Bitmap createBitmapFromPngFile(String folderPath, String fileName, int fileEndName) {
		Bitmap createdBmp = null;

		StringBuilder sb  = new StringBuilder();
		
		if(folderPath != null && fileName != null){
			sb.append(folderPath).append(fileName).append(fileEndName).append(".png");			
		} else {
			return null;
		}
		
		if(sb != null){
			createdBmp = BitmapFactory.decodeFile(sb.toString());
		}
		
		if(createdBmp != null){
			return createdBmp;			
		} else {
			return null;
		}
	}
	
	/**
     * @brief				  占쏙옙占쏙옙占쏙옙 占쏙옙恝占�占쌍댐옙 png 占쏙옙占쏙옙占쏙옙 Bitmap占쏙옙占쏙옙 占쌀뤄옙占승댐옙. 			
	 * @date 			      2013. 4. 26.		
	 * @param folderPath      占쏙옙占�占쏙옙占�
	 * @param fileName	      占쏙옙占쏙옙 占싱몌옙   				
	 * @param fileEndName     占쏙옙占쏙옙 占싱몌옙 (占쏙옙占쏙옙占쏙옙)
	 * @param bitmapSizeLevel 占쏙옙占싹듸옙 Bitmap占쏙옙 SizeLevel (占싫듸옙占쏙옙絹占쏙옙 Heap占쌨모리곤옙 占쏙옙占쏙옙 占쏙옙粹㏆옙占쏙옙占�占십뱄옙 큰 Bitmap占쏙옙 占쌀뤄옙占시띰옙 OutofMemory占쏙옙 占쌓뤄옙占쏙옙 levelSize 占쏙옙 占썅서 占쌜곤옙 占쌀뤄옙占쏙옙.) 
	 * @return				  占쏙옙환占쏙옙 Bitmap
	 */
	public static Bitmap createBitmapFromPngFile(String folderPath, String fileName, int fileEndName, int bitmapSizeLevel) {
		Bitmap createdBmp 			  = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize 		  = bitmapSizeLevel;
		
		StringBuilder sb  = new StringBuilder();
		
		if(folderPath != null && fileName != null){
			sb.append(folderPath).append(fileEndName).append(fileEndName).append(".png");			
		} else {
			return null;
		}
		
		if(sb != null){
			createdBmp = BitmapFactory.decodeFile(sb.toString(), options);
		} else {
			return null;
		}
		
		if(createdBmp != null){
			return createdBmp;			
		} else {
			return null;
		}
	}
	
	/**
     * @brief				  占쏙옙占쏙옙占쏙옙 占쏙옙恝占�占쌍댐옙 png 占쏙옙占쏙옙占쏙옙 Bitmap占쏙옙占쏙옙 占쌀뤄옙占승댐옙. 			
	 * @date 			      2013. 4. 26.		
	 * @param folderPath      占쏙옙占�占쏙옙占�
	 * @param fileEndName     占쏙옙占쏙옙 占싱몌옙 (占쏙옙占쏙옙占쏙옙)
	 * @param width			  占쏙옙占쏙옙占쏙옙징 占쏙옙 Width
	 * @param height		  占쏙옙占쏙옙占쏙옙징 占쏙옙 Height
	 * @return				  占쏙옙환占쏙옙 Bitmap
	 */
	public static Bitmap createBitmapFromPngFile(String folderPath, String fileMidName, int fileEndName, int width, int height) {
		Bitmap createdBmp 			  = null;
		
		StringBuilder sb  = new StringBuilder();
		
		if(folderPath != null && fileMidName != null){
			sb.append(folderPath).append(fileMidName).append(fileEndName).append(".png");			
		} else {
			return null;
		}

		if(sb != null){
			createdBmp = BitmapFactory.decodeFile(sb.toString());
		}
		
		if(createdBmp != null){
			if(width > 0 && height > 0){
				return Bitmap.createScaledBitmap(createdBmp, width, height, true);
			} else {
				return null;
			}

		} else {
			return null;
		}
	}
	
	/**
	 * @brief			URI 占싸븝옙占쏙옙 Bitmap 占쏙옙 占쏙옙占싼댐옙.				
	 * @date 			2013. 5. 3.		
	 * @param context   Context
	 * @param BitmapUri Bitmap Data URI
	 * @return			Bitmap
	 */
	public static Bitmap createBitmapFromUri(Context context, Uri BitmapUri){
		AssetFileDescriptor afd;
		
		try {
			afd = context.getContentResolver().openAssetFileDescriptor(BitmapUri, "r");
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(afd.getFileDescriptor(), null, opt);
			
			boolean bResize = false;
			
			if(opt.outWidth > 512){
				bResize     = true;
			}
			
			if(opt.outHeight > 512){
				bResize     = true;
			}
			
			if(bResize){
//				afd = context.getContentResolver().openAssetFileDescriptor(BitmapUri, "r");
//				
//				int width = 0;
//				int height = 0;
//				
//		  		if(opt.outWidth != opt.outHeight){
//		  			int bitmapHeight = ((512 *  opt.outHeight) / opt.outWidth);
//	    			
//	    			if(512 > bitmapHeight) {
//	    				// Port
//	    				int rest = 512 - bitmapHeight;
//	    				width    = 512;
//	    				height   = 512 - rest;
//	        			
//	    			} else {
//	    				// Land
//	    				width  = 512;
//	    				height = bitmapHeight;
//	    			}
//	    			
//	    		} else {
//	    			width = 512;
//    				height = 512;
//	    		} 
				
				final int REQUIRED_SIZE = 512;
				int width_tmp = opt.outWidth, height_tmp = opt.outHeight;
				int scale = 1;
				while (true) {
					if (width_tmp / 2 < REQUIRED_SIZE
							|| height_tmp / 2 < REQUIRED_SIZE)
						break;
					width_tmp /= 2;
					height_tmp /= 2;
					scale *= 2;
				}

		  		
		  		BitmapFactory.Options opt2 = new BitmapFactory.Options();
				opt2.inSampleSize = scale;
				Bitmap bitmap = BitmapFactory.decodeFileDescriptor(afd.getFileDescriptor(), null, opt2);
//				Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFileDescriptor(afd.getFileDescriptor()), width, height, false);
//				System.out.println("~~~~ _Width : " + width);
//				System.out.println("~~~~ _Height : " + height);
//				System.out.println("~~~~ Width : " + bitmap.getWidth());
//				System.out.println("~~~~ Height : " + bitmap.getHeight());

				return bitmap;
			} else {
		  		BitmapFactory.Options opt2 = new BitmapFactory.Options();
				opt2.inSampleSize = 1;
				Bitmap bitmap = BitmapFactory.decodeFileDescriptor(afd.getFileDescriptor(), null, opt2);
				
				return bitmap;
			}
			
		} catch (FileNotFoundException e) {
			return null;
		}    
	}

	public static void bitmapDestory(Bitmap bitmap) {
		if(bitmap != null){
			if(!bitmap.isRecycled()){
				bitmap.recycle();
				bitmap = null;
			}
		}
	}
	
	public static void bitmapDestory(Bitmap[] bitmap) {
		if(bitmap != null){
			for(Bitmap bmp : bitmap){
				if(bmp != null){
					if(!bmp.isRecycled()){
						bmp.recycle();
						bmp = null;
					}
				}
			}			
		}
	}

	public static void recycleBitmap(ImageView iv){

		if(iv == null)
			return;
		
		Drawable d = iv.getDrawable();
		
		if(d != null && d instanceof BitmapDrawable)
		{
			Bitmap bm = ((BitmapDrawable)d).getBitmap();
			if( bm != null && bm.isRecycled()==false)
				bm.recycle();
			d.setCallback(null);
		}
	}
	
	public static void recycleBitmap(ViewGroup v){
		
		if(v == null)
			return;

		for (int i = 0, count = v.getChildCount(); i < count; i++) {
		        View child = v.getChildAt(i);
		        if (child instanceof ImageView)
		        	recycleBitmap(((ImageView)child));
		        else if (child instanceof ViewGroup)
		        	recycleBitmap((ViewGroup)child);
		}
	}
	
	
	public static Bitmap cropCircleBitmap(Bitmap bmp) {

		Bitmap circleBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(bmp,  TileMode.CLAMP, TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);

        Canvas c = new Canvas(circleBitmap);

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        //약간 벗어나서...  -3 정도
        int r = (w/2) - 3;

        c.drawCircle(w/2, h/2, r, paint);


	    return circleBitmap;
	}

	public static Bitmap getTopRoundedRectBitmap(Bitmap bmp, int pixels)
	{
		Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		int color = 0xff424242;
		float roundPx = pixels;

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		rectF.bottom += pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bmp, rect, rect, paint);

		return bitmap;
	}

	public static Bitmap getRoundedRectBitmap(Bitmap sourceBitmap, int pixels)
	{
		if(sourceBitmap == null)
			return null;

		Bitmap bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		int color = 0xff424242;

		float roundPx = pixels;

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		paint.setColor(color);

		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(sourceBitmap, rect, rect, paint);

		return bitmap;
	}



	public static Bitmap getCaptureView(Activity c, int w, int h){
		View window = c.getWindow().getDecorView().getRootView();
		Rect rect = new Rect();
		c.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

		window.setDrawingCacheEnabled(true);
		window.buildDrawingCache(true);

		Bitmap captureView = Bitmap.createBitmap(w,h, Bitmap.Config.RGB_565);
		Canvas screenShotCanvas = new Canvas(captureView);
		window.draw(screenShotCanvas);

		//스테터스바 자르고
		Bitmap bm = Bitmap.createBitmap(captureView,0, rect.top, w, h - rect.top);

		window.setDrawingCacheEnabled(false);

		return bm;
	}

    /**
     * Bitmap 占싱뱄옙占쏙옙占쏙옙 占쏙옙占쏘데占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙 w, h 크占쏙옙 占쏙옙큼 crop占싼댐옙.
     *
     * @param src 占쏙옙
     * @param w 占쏙옙占쏙옙
     * @param h 占쏙옙占쏙옙
     * @return
     */
    public static Bitmap cropCenterBitmap(Bitmap src, int w, int h) {
        if(src == null)
            return null;

        int width  = src.getWidth();
        int height = src.getHeight();

        if(width < w && height < h)
            return src;

        int x = 0;
        int y = 0;

        if(width > w)
            x = (width - w)/2;

        if(height > h)
            y = (height - h)/2;

        int cw = w; // crop width
        int ch = h; // crop height

        if(w > width)
            cw = width;

        if(h > height)
            ch = height;

        if(cw <= 0){
        	return src;
        }

        if(ch <= 0){
        	return src;
        }

        return Bitmap.createBitmap(src, x, y, cw, ch);
    }


    public static Bitmap resizeBitmap(Bitmap src, int w){
    	if(src == null)
    		return null;

    	 float width = (float)src.getWidth();
    	 float height = (float)src.getHeight();

         float ratio = w / width;

         height = height * ratio;

         return Bitmap.createScaledBitmap(src,w, (int)height, false);
    }

    //작은 사이즈로 리사이징 (업로드 사이즈)
    public static Bitmap resizeSmallBitmap(Bitmap src, int wh){
    	if(src == null)
    		return null;

    	 float width = (float)src.getWidth();
    	 float height = (float)src.getHeight();
    	 float ratio = 1.0f;

    	 if(width < wh && height < wh){
    		 return src;
    	 }

    	 if(width < height){
    		 ratio = wh / width;
    		 height = height * ratio;
    		 width = wh;
    	 }else{
    		 ratio = wh / height;
    		 width = width * ratio;
    		 height = wh;
    	 }

         return Bitmap.createScaledBitmap(src,(int)width, (int)height, false);
    }


    //큰 사이즈로 리사이징 (업로드 사이즈)
    public static Bitmap resizeRatioBitmap(Bitmap src, int wh){
    	if(src == null)
    		return null;

    	 float width = (float)src.getWidth();
    	 float height = (float)src.getHeight();
    	 float ratio = 1.0f;

    	 if(width < wh && height < wh){
    		 return src;
    	 }

    	 if(width > height){
    		 ratio = wh / width;
    		 height = height * ratio;
    		 width = wh;
    	 }else{
    		 ratio = wh / height;
    		 width = width * ratio;
    		 height = wh;
    	 }
         return Bitmap.createScaledBitmap(src,(int)width, (int)height, true);
    }

    //작은 사이즈로 리사이징 (무조껀)
    public static Bitmap resizeOnlyBitmap(Bitmap src, int wh){
    	if(src == null)
    		return null;

    	 float width = (float)src.getWidth();
    	 float height = (float)src.getHeight();
    	 float ratio = 1.0f;

    	 if(width < height){
    		 ratio = wh / width;
    		 height = height * ratio;
    		 width = wh;
    	 }else{
    		 ratio = wh / height;
    		 width = width * ratio;
    		 height = wh;
    	 }

         return Bitmap.createScaledBitmap(src,(int)width, (int)height, false);
    }

    public static Bitmap resizeBitmap(Bitmap src, int w, int h) {
        if(src == null)
            return null;

        int width = src.getWidth();
        int height = src.getHeight();

        if(width < w && height < h)
            return src;

        return Bitmap.createBitmap(src, 0, 0, w, h);
    }

    public static Bitmap resizeBitmap(Bitmap src, int w, int h, boolean isAdjustSmallBitmap) {
        if(src == null)
            return null;

        int width = src.getWidth();
        int height = src.getHeight();

        return Bitmap.createBitmap(src, 0, 0, w, h);
    }

	//-----------------------------------------------------------
	// Paint
	//-----------------------------------------------------------
	/**
	 * @brief			占쏙옙占�占쏙옙 占쏙옙占쏙옙占쏙옙 Paint 占쏙옙占쏙옙
	 * @date 			2013. 4. 26.
	 * @return			paint
	 */
	public static Paint getPaint_Transparent_PorterDuffXfermode() {
		Paint pnt = new Paint();
		pnt.setColor(Color.BLACK);
		pnt.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
		pnt.setAntiAlias(true);
		return pnt;
	}

	//-----------------------------------------------------------
	// Canvas
	//-----------------------------------------------------------
	public static void setCanvasFillColor(Canvas canvas, int color) {
		canvas.drawColor(color);
	}

	public static Canvas setCanvasToBitmap(Bitmap createdBitmap){
		Canvas canvas = new Canvas(createdBitmap);
		return canvas;
	}

	public static int getCanvasWidth(Canvas canvas) {
		return canvas.getWidth();
	}

	public static int getCanvasHeight(Canvas canvas) {
		return canvas.getHeight();
	}

	/**
	 * @brief			Canvas占쏙옙 Center X 占쏙옙占쏙옙 占쏙옙占쏙옙
	 * @date 			2013. 4. 26.
	 * @param canvas    canvas
	 * @return			Canvas Center X 占쏙옙 占싫쇽옙 占쏙옙占쏙옙
	 */
	public static int getCanvasCenterPointX(Canvas canvas) {
		return getCanvasWidth(canvas) / 2;
	}

	/**
	 * @brief			Canvas占쏙옙 Center Y 占쏙옙占쏙옙 占쏙옙占쏙옙
	 * @date 			2013. 4. 26.
	 * @param canvas    canvas
	 * @return			Canvas Center Y 占쏙옙 占싫쇽옙 占쏙옙占쏙옙
	 */
	public static int getCanvasCenterPointY(Canvas canvas) {
		return getCanvasHeight(canvas) / 2;
	}

	//-----------------------------------------------------------
	// Draw
	//-----------------------------------------------------------

	/**
	 * @brief					Canvas占쏙옙 占쌩앙울옙 Bitmap Draw
	 * @date 					2013. 4. 26.
	 * @param canvas			canvas
	 * @param bitmapResource	Bitmap
	 */
	public static void drawImageToOrigin(Canvas canvas, Bitmap bitmapResource) {

		int bmpImageCenterPointX = getBitmapCenterPointX(bitmapResource);
		int bmpImageCenterPointY = getBitmapCenterPointY(bitmapResource);
		int subCanvasPointX 	 = getCanvasCenterPointX(canvas);
		int subCanvasPointY 	 = getCanvasCenterPointY(canvas);

		canvas.save();
		canvas.drawBitmap(bitmapResource, subCanvasPointX - bmpImageCenterPointX,
								          subCanvasPointY - bmpImageCenterPointY, null);
		canvas.restore();
	}

	public static Rect getRect(int leftPosition, int topPosition, int width, int height) {

		Rect rect  = new Rect(leftPosition, topPosition ,
							  leftPosition + width, topPosition + height);
		return rect;

	}

	public static Rect getBitmapeRect(int leftPosition, int topPosition, Bitmap bitmapResource) {

		int width  = getBitmapWidth(bitmapResource);
		int height = getBitmapHeight(bitmapResource);
		Rect rect  = new Rect(leftPosition, topPosition ,
							  leftPosition + width, topPosition + height);
		return rect;

	}

	/**
	 * @brief			Canvas占쏙옙 Center Line Draw
	 * @date 			2013. 4. 26.
	 * @param canvas    canvas
	 * @param color		color
	 */
	public static void drawCenterPoint(Canvas canvas, int color){
		Paint pnt = new Paint();
		pnt.setColor(color);

		int canvasWidth  = getCanvasWidth(canvas);
		int canvasHeight = getCanvasHeight(canvas);
		int canvasPointX = getCanvasCenterPointX(canvas);
		int canvasPointY = getCanvasCenterPointY(canvas);

		canvas.drawLine(0, canvasPointY, canvasWidth, canvasPointY, pnt);
		canvas.drawLine(canvasPointX, 0, canvasPointX, canvasHeight, pnt);
	}

	public static void drawRectBlack(Canvas canvas, int width, int height){
		Paint pnt = new Paint();
		pnt.setColor(Color.BLACK);

		canvas.drawRect(0, 0, width, height, pnt);
	}

	//-----------------------------------------------
	// 	ColorMatrix
	//-----------------------------------------------
	/**
	 * @brief			占쏙옙占쏙옙微占�占쏙옙占쏙옙占쏙옙 R,G,B, Alpha 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 Paint 占쏙옙占쏙옙
	 * @date 			2013. 4. 26.
	 * @param pnt		占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占�Paint
	 * @param R			Red
	 * @param G  		Green
	 * @param B			Blue
	 * @param Alpha		Alpha
	 */
	public static void setColorMatrix(Paint pnt, float R, float G, float B, float Alpha){
		ColorMatrix cm = new ColorMatrix(new float[] {
				0, 0, 0, 0, R,
		 		0, 0, 0, 0, G,
		 		0, 0, 0, 0, B,
		 		0, 0, 0, 1, Alpha});

		pnt.setColorFilter(new ColorMatrixColorFilter(cm));
	}

	/**
	 * @param bitmap
	 *
	 * @return converting bitmap and return a string
	 */
	public static String BitmapToString(Bitmap bitmap) {
		return BitmapToString(bitmap, Bitmap.CompressFormat.PNG);
	}

	//Bitmap.CompressFormat.PNG
	public static String BitmapToString(Bitmap bitmap, CompressFormat format)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(format, 100, baos);

		return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
	}

	/**
	 * @param bitmap
	 *
	 * @return converting bitmap and return a string
	 */
	public static byte[] BitmapToByte(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap byteArrayToBitmap(byte[] $byteArray) {
		Bitmap bitmap = BitmapFactory.decodeByteArray($byteArray, 0, $byteArray.length);
		return bitmap;
	}
	
	/**
	 * @param encodedString
	 * @return bitmap (from given string)
	 */
	public static Bitmap StringToBitmap(String encodedString) {
		try {
			byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length);
			return bitmap;
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        } else {
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        }
	    }
	    return inSampleSize;
	}
	
	public static Bitmap rotate(Bitmap bitmap, int degrees) {
		
		if (degrees != 0 && bitmap != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees);
			try {
				Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
				if (bitmap != converted) {
					bitmap = null;
					bitmap = converted;
					converted = null;
				}
			} catch (OutOfMemoryError ex) {
				throw new OutOfMemoryError();
			}
		}
		return bitmap;
	}
}
