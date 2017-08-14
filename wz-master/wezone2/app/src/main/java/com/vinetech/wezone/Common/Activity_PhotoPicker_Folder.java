package com.vinetech.wezone.Common;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.vinetech.util.FileCache;
import com.vinetech.util.LibCamera;
import com.vinetech.util.UIBitmap;
import com.vinetech.util.UIControl;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 
 * @author chokiyeon
 *
 
 	//폴더선택+카메라 모드인 경우 
	Activity_PhotoPicker_Folder.startFolderActivityForResult(this,5//최대선택갯수);
	
	//바로 카메라 실행하여 촬영 후 편집모드 진입인 경우 
	Activity_PhotoPicker_Folder.startCameraActivityForResult(this);
	
	--------------------------------------------------------------------------------
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		switch(requestCode)
		{
		case Define.INTENT_RESULT_PHOTO:
				//결과 데이타
				if(resultCode == RESULT_OK)
				{
					ArrayList<Data_PhotoPickerImage> 결과로사용할데이타 = m_Share.popPhotoPickerImages();
					
					//사용후에 각 Data_PhotoPickerImage안에 있는 thumnail과 bitmap을 해제하는 책임을 가지고 가야함
				}
				else
				{
					//데이타 해제할것 
					m_Share.resetPhotoPickerImages();
				}
				break;
				
				......
		}
	}

 */
public final class Activity_PhotoPicker_Folder extends BaseActivity implements
AdapterView.OnItemClickListener
{
	public final static String INTENTKEY_TYPE = "picker_type";
	public final static String INTENTKEY_PICKER_MAX_COUNT = "picker_max_count";
	public final static String INTENTKEY_CROP_ASPECT_X = "crop_aspect_x";
	public final static String INTENTKEY_CROP_ASPECT_Y = "crop_aspect_y";
	
	
	public final static int TYPE_FOLDER = 0;
	public final static int TYPE_CAMERA = 1;
	
	
	private ListView mListView;
	
	private Adapter_PhotoPicker_Folder mAdapter;
	
	private ArrayList<PhotoDir> mPhotoDirs;
	
	private ArrayList<Data_PhotoPickerImage> mPhotoPickerImages;
	
	private LibCamera mCamera;
	
	private int mType;
	
	private int mPickerMaxCount;
	
	private int mCropAspectX, mCropAspectY; 
	
	private boolean mIsCancelledCameraFinish;
	
	
	public static void startFolderActivityForResult(BaseActivity activity, int pickerMaxCount)
	{
		startFolderActivityForResult(activity, TYPE_FOLDER, pickerMaxCount);
	}
	public static void startFolderActivityForResult(BaseActivity activity, int pickerMaxCount, int cropAspectX, int cropAspectY)
	{
		startFolderActivityForResult(activity, TYPE_FOLDER, pickerMaxCount, cropAspectX, cropAspectY);
	}
	
	public static void startCameraActivityForResult(BaseActivity activity)
	{
		startFolderActivityForResult(activity, TYPE_CAMERA, 1);
	}
	
	public static void startCameraActivityForResult(BaseActivity activity, int cropAspectX, int cropAspectY)
	{
		startFolderActivityForResult(activity, TYPE_CAMERA, 1, cropAspectX, cropAspectY);
	}

	public static void startFolderActivityForResult(BaseActivity activity, int type, int pickerMaxCount)
	{
		startFolderActivityForResult(activity, type, pickerMaxCount, 0, 0);
	}
	private static void startFolderActivityForResult(BaseActivity activity, int type, int pickerMaxCount, int cropAspectX, int cropAspectY)
	{
		Intent intent = new Intent(activity, Activity_PhotoPicker_Folder.class);
		intent.putExtra(INTENTKEY_TYPE, type);
		intent.putExtra(INTENTKEY_PICKER_MAX_COUNT, pickerMaxCount);
		intent.putExtra(INTENTKEY_CROP_ASPECT_X, cropAspectX);
		intent.putExtra(INTENTKEY_CROP_ASPECT_Y, cropAspectY);
		activity.moveActivityForResult(intent, Define.INTENT_RESULT_PHOTO);
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_photo_picker_folders);

		String strTitle = getResources().getString(R.string.choose_from_library);
		setHeaderView(R.drawable.btn_back_white,strTitle,R.drawable.icon_picture);

		Intent intent = getIntent();
		
		mType = intent.getIntExtra(INTENTKEY_TYPE, TYPE_FOLDER);
		mPickerMaxCount = intent.getIntExtra(INTENTKEY_PICKER_MAX_COUNT, 1);
		mCropAspectX = intent.getIntExtra(INTENTKEY_CROP_ASPECT_X, 0);
		mCropAspectY = intent.getIntExtra(INTENTKEY_CROP_ASPECT_Y, 0);
		
		mCamera = new LibCamera(this);
		
//		if( getShare().getPhotoPickerImages() == null)
////	 	||	m_Share.isContainActivityList(new Class<?>[]{Activity_PhotoPicker_Photo.class,Activity_ImageEditor.class}) == false )
//		{
//
//		}

		mPhotoPickerImages = getShare().preparePhotoPickerImages();

		mPhotoDirs = new ArrayList<PhotoDir>();
		
		if( mType == TYPE_FOLDER )
		{
			ArrayList<PhotoDir> photoDirs = getImageFolderList();
			
			HashSet<PhotoDir> hs = new HashSet<PhotoDir>(photoDirs);
			Iterator<PhotoDir> it = hs.iterator();
			while(it.hasNext()) 
				mPhotoDirs.add(it.next());
	
			mListView = (ListView)findViewById(R.id.ListView_imagedir);
	
			mAdapter = new Adapter_PhotoPicker_Folder(m_Context, mPhotoDirs);
			mListView.setAdapter(mAdapter);
			mListView.setOnItemClickListener(this);
		}
		else if( mType == TYPE_CAMERA )
		{
			mCamera.takeCamera(Define.INTENT_RESULT_CAMERA);
			mIsCancelledCameraFinish = true;
		}
	}
	
	@Override
	protected void onDestroy() 
	{
		if(mListView !=  null)
		{
			for(int i=0, count=mListView.getChildCount(); i<count; i++)
			{
				if(mListView.getChildAt(i) instanceof ImageView)
				{
					ImageView iv = (ImageView) mListView.getChildAt(i);
					UIBitmap.recycleBitmap(iv);
				}
			}
		}
		//m_Share.resetPhotoPickerImages();
		super.onDestroy();
	}

	/**
	 * 카메라 촬영
	 */

	@Override
	public void onClickRightBtn(View v) {
		super.onClickRightBtn(v);
		mCamera.takeCamera(Define.INTENT_RESULT_CAMERA);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		PhotoDir photoData = mPhotoDirs.get(arg2);
		
		Intent intent = new Intent(this,Activity_PhotoPicker_Photo.class);
		intent.putExtra(Activity_PhotoPicker_Photo.INTENTKEY_FOLDER_NAME, photoData.mFolderName);
		intent.putExtra(Activity_PhotoPicker_Photo.INTENTKEY_FOLDER_ID, photoData.mFolderId);
		intent.putExtra(Activity_PhotoPicker_Photo.INTENTKEY_PICKER_MAX_COUNT, mPickerMaxCount);
		intent.putExtra(INTENTKEY_CROP_ASPECT_X, mCropAspectX);
		intent.putExtra(INTENTKEY_CROP_ASPECT_Y, mCropAspectY);
		moveActivityForResult(intent,Define.INTENT_RESULT_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		
		switch(requestCode)
		{
		case Define.INTENT_RESULT_PHOTO:
		case Define.INTENT_RESULT_IMAGE_EDIT:
			if(resultCode == RESULT_OK)
			{
				setResult(resultCode, data);
				finish();
			}
			else
			{
				getShare().resetPhotoPickerImages();
				if( mType == TYPE_CAMERA )
					finish();
			}
			break;
			
		case Define.INTENT_RESULT_CAMERA:
			if(resultCode == RESULT_OK)
			{ 
				try 
				{
					Bitmap bitmap = mCamera.parseTakeCameraData(data);
					if(bitmap != null)
					{
						int imageWidthHeight = 299;
						
						//String fileDirPath = FileCache.getInstance().getTempFilePath()+"takePicture-"+String.valueOf(System.currentTimeMillis())+".jpg";
						String fileDirPath = FileCache.getInstance().getTempFilePath()+"takePicture.jpg";
						File file = new File(fileDirPath);
						file.delete();
						
						if( WezoneUtil.saveBitmapToPngFile(bitmap, file, false) == true )
						{
							Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(bitmap, imageWidthHeight, imageWidthHeight);
							
							Data_PhotoPickerImage photoPickerImage = new Data_PhotoPickerImage();
							photoPickerImage.thumbBitmap = thumbBitmap; 
							photoPickerImage.path = fileDirPath;
							mPhotoPickerImages.add(photoPickerImage);
							
							Activity_ImageEditor.startActivity(this, 0, mCropAspectX, mCropAspectY);							
						}
						
						UIBitmap.bitmapDestory(bitmap);
					}
				} 
				catch (Exception e){
					if(Define.LOG_YN) {
						Log.d(Define.LOG_TAG, e.toString());
					}
				} 
			}
			else if( mIsCancelledCameraFinish == true )
			{
				mIsCancelledCameraFinish = false;
				finish();
			}
			break;
		}
	}
	
	
	public ArrayList<PhotoDir> getImageFolderList()
	{
		ArrayList<PhotoDir> folderList = new ArrayList<PhotoDir>();
		
		final String[] projection = {"distinct "+ MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME , MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.BUCKET_ID};
		Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, "", null, MediaStore.Images.Media.DATE_MODIFIED+" desc");

		if(imageCursor != null && imageCursor.moveToFirst() == true )
		{
			String bucket_display_name, id, bucket_display_id;
			do
			{
				bucket_display_name = imageCursor.getString(imageCursor.getColumnIndex(Images.ImageColumns.BUCKET_DISPLAY_NAME));
				bucket_display_id = imageCursor.getString(imageCursor.getColumnIndex(Images.ImageColumns.BUCKET_ID));
				id = imageCursor.getString(imageCursor.getColumnIndex(Images.ImageColumns._ID));
	    			
	    		folderList.add(new PhotoDir(id, bucket_display_name, bucket_display_id));
	    	}
			while( imageCursor.isLast() == false && imageCursor.moveToNext() == true );

			imageCursor.close();
		}
		return folderList;
	}
	
//	private String splitLastDirName(String path)
//	{
//		if(path == null)
//			return "";
//		
//		String[] dirArray = path.split("/");
//		
//		int idx = dirArray.length - 2;
//		return dirArray[idx];
//	}
}


final class Adapter_PhotoPicker_Folder extends BaseAdapter
{
	private Context mContext;
	
	private ArrayList<PhotoDir> mPhotoDirs;
	
	private BitmapFactory.Options mBitmapOption;
	
	public Adapter_PhotoPicker_Folder(Context context, ArrayList<PhotoDir> listData)
	{
		mContext = context;
		mPhotoDirs = listData;
		
		mBitmapOption = new BitmapFactory.Options();
		mBitmapOption.inJustDecodeBounds = false;
	}
	
	@Override
	public int getCount() { return (mPhotoDirs!=null)?mPhotoDirs.size():0; }

	@Override
	public Object getItem(int position) { return (mPhotoDirs!=null && position<mPhotoDirs.size())?mPhotoDirs.get(position) : null; }

	@Override
	public long getItemId(int position) { return position; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		
		if (convertView == null) 
		{
			convertView = View.inflate(mContext,R.layout.photopicker_folder_cell, null);
			
			holder = new ViewHolder();
			holder.imageView  = (ImageView)convertView.findViewById(R.id.photopicker_imgview);
			holder.textView = (TextView)convertView.findViewById(R.id.photopicker_textview);
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		
		if(holder != null)
		{
			PhotoDir photoDir = mPhotoDirs.get(position);
			
			UIBitmap.recycleBitmap(holder.imageView);
			
			int id = Integer.parseInt(photoDir.mImageId);

			Bitmap thumbnailBitmap = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), id, Thumbnails.MINI_KIND, mBitmapOption);
			
			if(thumbnailBitmap != null)
			{
//				Bitmap resizeBitmap = UIBitmap.getResizeBitmap(thumbnailBitmap, mImageWidthHeight, mImageWidthHeight);
//				
//				if( resizeBitmap != null )
//				{
//					if( thumbnailBitmap.isRecycled() == false && resizeBitmap != thumbnailBitmap )
//						thumbnailBitmap.recycle();		
//				}
//				else
//					resizeBitmap = thumbnailBitmap;
					
				/**
				 * TODO : MASK, CLIPPING 처리로 변경 예정
				 */
//				thumbnailBitmap = UIBitmap.getRoundedRectBitmap(resizeBitmap, 5);
//				if( resizeBitmap != null )
//					resizeBitmap.recycle();
				
				UIControl.setBackgroundBitmap(mContext, holder.imageView, thumbnailBitmap);
			}
			else
			{
				
			}

			holder.textView.setText(photoDir.mFolderName);
		}
		
		return convertView;
	}
	
	public final class ViewHolder
	{
		public ImageView imageView;
		public TextView textView;
	}
}

final class PhotoDir implements Comparable<PhotoDir>
{
	public String mImageId;
	public String mFolderName;
	public String mFolderId;
	
	public PhotoDir(String imageId, String folderName, String folderId)
	{
		this.mImageId = imageId;
		this.mFolderName = folderName;
		this.mFolderId = folderId;
	}
	@Override
	public int compareTo(PhotoDir dir) 
	{
		return mFolderName.compareTo(dir.mFolderName);
	}
	public boolean equals(Object dir)
	{   
		return mFolderName.equals(((PhotoDir)dir).mFolderName); 
	}
	public int hashCode() 
	{
		return mFolderName.hashCode(); 
	}
}
