package com.vinetech.wezone.Data;

import android.graphics.Bitmap;

public final class Data_PhotoPickerImage 
{
	public Bitmap thumbBitmap;
	public Bitmap bitmap;
	public String columnId;
	public String path;
	
	public Data_PhotoPickerImage(){}
	public Data_PhotoPickerImage(Bitmap bitmap){ this.bitmap = bitmap; }
	
	public void release()
	{
		if( thumbBitmap != null )
		{
			if( thumbBitmap.isRecycled() == false )
				thumbBitmap.recycle();
			thumbBitmap = null;
		}
		if( bitmap != null )
		{
			if( bitmap.isRecycled() == false )
				bitmap.recycle();
			bitmap = null;
		}
	}
	
	public void setRecycleThumbBitmap(Bitmap thumbBitmap)
	{
		if( this.thumbBitmap != null && this.thumbBitmap.isRecycled() == false )
			this.thumbBitmap.recycle();
		this.thumbBitmap = thumbBitmap;
	}
}
