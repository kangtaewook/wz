package com.vinetech.wezone.Common;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @author chokiyeon
 *
 */
public final class Activity_PhotoPicker_Photo extends BaseActivity
{
	public final static String INTENTKEY_FOLDER_NAME = "folder_name";
	public final static String INTENTKEY_FOLDER_ID = "folder_id";
	public final static String INTENTKEY_PICKER_MAX_COUNT = "picker_max_count";
	public final static String INTENTKEY_CROP_ASPECT_X = "crop_aspect_x";
	public final static String INTENTKEY_CROP_ASPECT_Y = "crop_aspect_y";
	
	private ImagePickerAdapter mAdapter;
	
	private ArrayList<ImagePickerItem> mImageItems;
	
	private ArrayList<Data_PhotoPickerImage> mPhotoPickerImages;
	
	private GridView mGridView;
	
	private TextView mCountTextView;
	
	private int mPickerMaxCount;
	
	private int mCropAspectX, mCropAspectY;
	
	
	public Activity_PhotoPicker_Photo() 
	{
		super();

		mImageItems = new ArrayList<ImagePickerItem>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_photo_picker_images);

		String strTitle = getResources().getString(R.string.choose_from_library);
		setHeaderView(R.drawable.btn_back_white,strTitle,R.drawable.btn_check);
		
		
		mCountTextView = (TextView)findViewById(R.id.photo_picker_images_count_textview);
	
		Intent intent = getIntent();
		mPickerMaxCount = intent.getIntExtra(Activity_PhotoPicker_Folder.INTENTKEY_PICKER_MAX_COUNT, 1);
		mCropAspectX = intent.getIntExtra(INTENTKEY_CROP_ASPECT_X, 0);
		mCropAspectY = intent.getIntExtra(INTENTKEY_CROP_ASPECT_Y, 0);
		
		String folderName = intent.getStringExtra(INTENTKEY_FOLDER_NAME);
		String folderId = intent.getStringExtra(INTENTKEY_FOLDER_ID);
		ImagePickerItemLoader.mActivity = this;
		ImagePickerItem.init(this);
		
		loadImageItems(folderId);
		mPhotoPickerImages = getShare().getPhotoPickerImages();
		
		mGridView = (GridView)findViewById(R.id.photo_picker_images_gridview);
		
		mAdapter = new ImagePickerAdapter(this, 
  				  mImageItems,
  				  mPhotoPickerImages,
  				  mPickerMaxCount);
		mGridView.setAdapter(mAdapter);

		updateSelectImageCount();
	}
	
	
	@Override
	protected void onDestroy() 
	{
		if( mAdapter != null )
			mAdapter.dispose();
		
		super.onDestroy();
	}


	@Override
	public void onClickRightBtn(View v)
	{
		if( mPhotoPickerImages.isEmpty() == false )
		{
			Activity_ImageEditor.startActivity(this, 0,mCropAspectX, mCropAspectY);
		}
	}
	
	//폴더별 이미지리스트 ID
	public int loadImageItems(String folderId)
	{
		mImageItems.clear();
		
		//MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, 
		final String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
		Cursor imageCusor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.ImageColumns.BUCKET_ID +"='"+folderId+"'", null, MediaStore.Images.Media.DATE_ADDED+" desc");
		  
		if(imageCusor != null)
		{
			if( imageCusor.moveToFirst() == true )
			{
	    		do
	    		{
	    			int id = imageCusor.getInt(imageCusor.getColumnIndex(Images.ImageColumns._ID));

	    			String data = imageCusor.getString(imageCusor.getColumnIndex(Images.ImageColumns.DATA));
	    			
	    			mImageItems.add(new ImagePickerItem(id, data));
	    		}
	    		while( imageCusor.moveToNext() == true );
	    	}
			if( imageCusor.isClosed() == false )
			{
				try{imageCusor.close();}
				catch (SQLException e){}
			}			
		}
		return mImageItems.size();
	}
	
	public void updateSelectImageCount()
	{
		mCountTextView.setText(mPhotoPickerImages.size() + " / "+mImageItems.size());
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch( requestCode )
		{
		case Define.INTENT_RESULT_IMAGE_EDIT:
			if( resultCode == RESULT_OK )
			{
				setResult(RESULT_OK);
				finish();
			}
			break;
		}
	}
	
}

final class ImagePickerAdapter extends BaseAdapter implements View.OnClickListener
{
	private Activity_PhotoPicker_Photo mActivity;
	
	private ArrayList<ImagePickerItem> mImageItems;
	
	private ArrayList<Data_PhotoPickerImage> mPhotoPickerImages;
	
	private int mPickerMaxCount;
	
	private BitmapFactory.Options mBitmapOptions;
	
	private ExecutorService mExecutorService;
	
    public ImagePickerAdapter(Activity_PhotoPicker_Photo activity, 
    				  ArrayList<ImagePickerItem> imageItems,
    				  ArrayList<Data_PhotoPickerImage> photoPickerImages,
    				  int maxCount) 
    {
    	this.mActivity = activity;
    	this.mImageItems = imageItems;
    	this.mPhotoPickerImages = photoPickerImages;
    	this.mPickerMaxCount = maxCount;
    	
    	mBitmapOptions = new BitmapFactory.Options();
    	mBitmapOptions.inJustDecodeBounds = false;
    	
		mExecutorService = Executors.newFixedThreadPool(5);
    }
    
    public void dispose()
    {
		if( mExecutorService != null )
		{
			mExecutorService.shutdownNow();
			mExecutorService = null;
		}
    }
    
    
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
		if( convertView == null )
		{
			convertView = View.inflate(mActivity, R.layout.photopicker_grid_cell, null);
			if(convertView != null)
			{
				viewHolder = new ViewHolder();
				convertView.setTag(viewHolder);
				
				viewHolder.mImageView = (ImageView)convertView.findViewById(R.id.photo_picker_grid_imgview);
				viewHolder.mCheckImageView = (ImageView)convertView.findViewById(R.id.photo_picker_grid_check_imgview);

				viewHolder.mImageView.setOnClickListener(this);
				viewHolder.mCheckImageView.setOnClickListener(this);
				
				viewHolder.mImageView.setTag(viewHolder.mCheckImageView);
			}
		} 
		else 
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		if( viewHolder != null )
		{
			ImagePickerItem imageItem = mImageItems.get(position);

			viewHolder.mCheckImageView.setTag(position);
			viewHolder.mCheckImageView.setSelected(imageItem.mIsSelected);

			Integer settingId = (Integer)viewHolder.mImageView.getTag(-1);
//			SDSLog.d(SDSLog.TAGIDX_CKY,"settingId="+settingId);
//			SDSLog.d(SDSLog.TAGIDX_CKY,"imageItem.id="+imageItem.id);
			
			if( settingId == null || settingId != imageItem.mId )
			{
				mExecutorService.submit(new ImagePickerItemLoader(imageItem,viewHolder.mImageView));
			}
		}
		return convertView;	
    }
    
	public final class ViewHolder
	{
		public ImageView mImageView;
		public ImageView mCheckImageView;
	}

    public final int getCount() 
    {
        return mImageItems.size();
    }

    public final Object getItem(int position)
    {
        return (position < getCount())?mImageItems.get(position):null;
    }

    public final long getItemId(int position) 
    {
        return position;
    }
    
   
	@Override
	public void onClick(View v)
	{
		final int clickedViewId = v.getId();
		
		ImageView photoCheckImageView;
		
		if( clickedViewId == R.id.photo_picker_grid_imgview )
			photoCheckImageView = (ImageView)v.getTag();
		else
			photoCheckImageView = (ImageView)v;
			
		 
		Integer pos = (Integer)photoCheckImageView.getTag();
		
		if( photoCheckImageView.isSelected() == false )
		{
			if(mPhotoPickerImages.size() >= mPickerMaxCount)
			{
				if( mPickerMaxCount > 1 ){
					String str = mActivity.getResources().getString(R.string.you_can_not_select_more_than_image);
					Toast.makeText(mActivity, String.format(str, mPickerMaxCount), Toast.LENGTH_SHORT).show();
				}
				else{
					String str = mActivity.getResources().getString(R.string.only_1_image_can_be_selected);
					Toast.makeText(mActivity,str, Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				setSelecteItem(photoCheckImageView, photoCheckImageView, pos, true);
			}
		}
		else
		{
			setSelecteItem(photoCheckImageView, photoCheckImageView, pos, false);
		}
		
		mActivity.updateSelectImageCount();			
	}
	
	
	private void setSelecteItem(View view, ImageView checkImageView, int pos, boolean isSelect)
	{
		ImagePickerItem imageItem = mImageItems.get(pos);
		
		if( imageItem.mIsSelected == isSelect )
			return;
		
		imageItem.mIsSelected = isSelect;
		view.setSelected(isSelect);
		checkImageView.setSelected(isSelect);
		
		if( isSelect == true )
		{
			Bitmap thumbBitmap = MediaStore.Images.Thumbnails.getThumbnail(mActivity.getContentResolver(), Integer.valueOf(imageItem.mId), Thumbnails.MICRO_KIND, mBitmapOptions);
			if( thumbBitmap != null )
			{
				Data_PhotoPickerImage photoPickerImage = new Data_PhotoPickerImage();
				photoPickerImage.thumbBitmap = thumbBitmap; 
				photoPickerImage.columnId = String.valueOf(imageItem.mId);
				photoPickerImage.path = imageItem.mData;
				mPhotoPickerImages.add(photoPickerImage);
			}
		}
		else
		{
			final String imageItemId = String.valueOf(imageItem.mId);
			int i = 0;
			
			for( Data_PhotoPickerImage photoPickerImage : mPhotoPickerImages )
			{
				if( photoPickerImage.columnId != null && imageItemId.equals(photoPickerImage.columnId) == true )
				{
					photoPickerImage.release();
					mPhotoPickerImages.remove(i);
					return;
				}
				i++;
			}
		}
	}
	
//	private void removeSelectItem(int pos)
//	{
//		ImagePickerItem imageItem = imageItems.get(pos);
//		
//		String imageId = String.valueOf(imageItem.id);
//		
//		int i = 0;
//		for( Data_AttachImage dataAttach : selectedImageItems )
//		{
//			String id = dataAttach.getImageColumnId();
//			if( id != null && imageId.equals(id) == true )
//			{
//				UIBitmap.bitmapDestory(dataAttach.getThumbnail());
//				selectedImageItems.remove(i);
//				return;
//			}
//			i++;
//		}
//	}
}

final class ImagePickerItemLoader implements Runnable
{
	public static Activity_PhotoPicker_Photo mActivity;
	
	public ImagePickerItem mItem;
	
	public ImageView mImageView;
	
	public ImagePickerItemLoader(ImagePickerItem item, ImageView imageView)
	{
		mItem = item;
		mImageView = imageView;
		mImageView.setTag(-1, mItem.mId);
		imageView.setImageResource(R.drawable.img_loading);
	}
	
	@Override
	public void run() 
	{
		Integer settingId = (Integer)mImageView.getTag(-1);
		if( mItem.mId != settingId )
		{
			//Log.d("TEST","이미비동기로드run 리턴 " + mItem.id+" != "+settingId);
			return;
		}
		
		final Bitmap thumbBitmap = mItem.loadThumbBitmap();
		if( thumbBitmap != null )
		{
			mActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run() 
				{
					Integer settingId = (Integer)mImageView.getTag(-1);
					if( mItem.mId != settingId )
					{
						if( thumbBitmap.isRecycled() == false )
							thumbBitmap.recycle();
						//Log.d("TEST","이미비동기로드run 리턴 " + mItem.id+" != "+settingId);
						return;
					}
					mImageView.setImageBitmap(thumbBitmap); 
				}
			});
			//Log.d("TEST","이미비동기로드run OK = " + mItem.id);
		}
		else
		{
			mImageView.setTag(-1, 0);
			//Log.d("TEST","이미비동기로드run FAIL#### = " + mItem.id);
		}		
	}
}

final class ImagePickerItem
{
	private static Activity mActivity;
	private static ContentResolver mContentResolver;
	private static BitmapFactory.Options mBitmapOption8;
	private static int mThumbWidthHeight;
	
	public int mId;
	
	public String mData;
	
	public String mThumbData;
	
	public boolean mIsSelected;

	
	public static void init(Activity activity)
	{
		if( ImagePickerItem.mActivity != null ) return;
		
		ImagePickerItem.mActivity = activity;
		
		mContentResolver = activity.getContentResolver();
		
		mThumbWidthHeight = 379;
		
		mBitmapOption8 = new BitmapFactory.Options();
		mBitmapOption8.inSampleSize = 8;
	}
	
	
	public ImagePickerItem( int id, String data )
	{
		this.mId = id;
		this.mData = data;	
	}
	
	public Bitmap loadThumbBitmap()
	{
		Bitmap bitmap = null;
		if( mThumbData == null )
			mThumbData = getThumbData(mId);
		
		if( mThumbData != null )
			bitmap = WezoneUtil.loadBitmapOptimizeMemory(mActivity, mThumbData);
		
		if( bitmap == null && mData != null )
			bitmap = WezoneUtil.loadBitmapOptimizeMemory(mActivity, mData);
		
		Bitmap thumbBitmap = bitmap;
		if( bitmap != null )
		{
			thumbBitmap = ThumbnailUtils.extractThumbnail(bitmap, mThumbWidthHeight, mThumbWidthHeight);
			if( thumbBitmap != null && thumbBitmap != bitmap && bitmap.isRecycled() == false )
				bitmap.recycle();
		}
		
		/*
		if( mThumbData != null )
			thumbBitmap = BitmapFactory.decodeFile(mThumbData, mBitmapOption8);
		
		if( thumbBitmap == null && mData != null )
			thumbBitmap = BitmapFactory.decodeFile(mData, mBitmapOption8);
		*/
		return thumbBitmap;
	}	
	
	private String getThumbData(int imageId)
	{
		final String[] thumbPrj =
		{
			MediaStore.Images.Thumbnails.DATA,
		};

		Cursor cursor = mContentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
				thumbPrj,
				MediaStore.Images.Thumbnails.IMAGE_ID + " = " + imageId,
				null, null);

		String thumbData = null;
		if( cursor != null )
		{
			if( cursor.moveToFirst() == true )
			{
				thumbData = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
			}
			
			if( cursor.isClosed() == false )
			{
				try
				{
					cursor.close();
				}
				catch (SQLException e){}
			}
		}
		return thumbData;
	}
}