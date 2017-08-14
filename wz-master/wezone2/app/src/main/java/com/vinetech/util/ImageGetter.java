package com.vinetech.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.vinetech.wezone.R;

public class ImageGetter implements android.text.Html.ImageGetter
{
	public static int EMOTICON_SIZE_SMALL = 0;
	public static int EMOTICON_SIZE_BIG = 1;
	public static int EMOTICON_SIZE_MID	= 2;
	
	
	private static boolean isInited;
	
	private static int iconImgWH = 32;
	
	private static int thumbEmoticonImgWidth = 64, thumbEmoticonImgHeight = 64;
	
	private Context mContext;
	
	private Resources res;
	
	public ImageGetter(Context c) 
	{
		mContext = c;
		res = mContext.getResources();
		
		if( isInited == false )
		{
			iconImgWH = res.getDimensionPixelOffset(R.dimen.emoticon_image_width);
			thumbEmoticonImgWidth = res.getDimensionPixelOffset(R.dimen.stickon_width);
			thumbEmoticonImgHeight = res.getDimensionPixelOffset(R.dimen.stickon_width);
		}
	}
	
	public void setIconSize(int size){

		if(size == EMOTICON_SIZE_SMALL){
			iconImgWH = res.getDimensionPixelOffset(R.dimen.emoticon_image_width);
			thumbEmoticonImgWidth = res.getDimensionPixelOffset(R.dimen.emoticon_image_width);
			thumbEmoticonImgHeight = res.getDimensionPixelOffset(R.dimen.emoticon_image_width);
		}else if(size == EMOTICON_SIZE_BIG){
			iconImgWH = res.getDimensionPixelOffset(R.dimen.stickon_width);
			thumbEmoticonImgWidth = res.getDimensionPixelOffset(R.dimen.stickon_width);
			thumbEmoticonImgHeight = res.getDimensionPixelOffset(R.dimen.stickon_width);
		}else{
			iconImgWH = res.getDimensionPixelOffset(R.dimen.emoticon_image_width_mid);
			thumbEmoticonImgWidth = res.getDimensionPixelOffset(R.dimen.stickon_width);
			thumbEmoticonImgHeight = res.getDimensionPixelOffset(R.dimen.stickon_width);
		}
	}
	
	@Override
	public Drawable getDrawable(String source) 
	{
		Drawable drawable = null;
		
		int value = WezoneUtil.parseInt(source, 0) - 1;
		
		if( value >= 0 && value < 20 )
		{
			drawable = res.getDrawable( R.drawable.e01 + value);
			drawable.setBounds( 0, 0, iconImgWH, iconImgWH);
		}
		else if( value >= 20 && value <= 101 )
		{
			drawable = res.getDrawable( R.drawable.sticker_01 + value - 20);
			drawable.setBounds( 0, 0, thumbEmoticonImgWidth, thumbEmoticonImgHeight);
		}
		return drawable;
	}

}
