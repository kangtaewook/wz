package com.vinetech.wezone.Common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.ui.ToggleViewPager;
import com.vinetech.ui.TouchImageView;
import com.vinetech.util.FileCache;
import com.vinetech.util.UIBitmap;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 
 * @author chokiyeon
 *
 */
public final class Activity_ImageEditor extends BaseActivity implements View.OnClickListener
{
	public final static String INTENTKEY_POSITION = "init_position";
	public final static String INTENTKEY_IMAGE_PATHS = "image_paths";
	public final static String INTENTKEY_CROP_ASPECT_X = "crop_aspect_x";
	public final static String INTENTKEY_CROP_ASPECT_Y = "crop_aspect_y";
	
	private final int IMAGE_MAX_COUNT = 10;
	
	private ToggleViewPager mViewPager;
	
	private ArrayList<Data_PhotoPickerImage> mPhotoPickerImages;

	private SparseArray<Bitmap> mLoadImages;
	
	private SparseArray<TouchImageView> mLoadImageViews;

	private ImageViewPagerAdapter mViewPagerAdapter;
	
	private TouchImageView mCurrentImageView;
	
	private int mThumbSize;
	
	private int mTotalCount;
	
	private int mCurrentPosition, mLastSelectedPosition;
	
	private int mCropAspectX, mCropAspectY;
	
	private String mEditedFileDirPath;
	
	private boolean mIsPageScrolling, mIsViewPageEnable = true;
	
	private boolean mIsDone;
	
	public static void startActivityWithPaths(BaseActivity activity, ArrayList<String> imagePaths, int cropAspectX, int cropAspectY)
	{
		startActivityWithPaths(activity, imagePaths, -1, cropAspectX, cropAspectY);
	}
	
	public static void startActivityWithPaths(BaseActivity activity, ArrayList<String> imagePaths, int initPosition, int cropAspectX, int cropAspectY)
	{
		Intent intent = new Intent(activity, Activity_ImageEditor.class);
		intent.putExtra(INTENTKEY_IMAGE_PATHS, imagePaths);
		startActivity(activity, intent, initPosition, cropAspectX, cropAspectY);
	}
	
	public static void startActivity(BaseActivity activity, int initPosition, int cropAspectX, int cropAspectY)
	{
		Intent intent = new Intent(activity, Activity_ImageEditor.class);
		startActivity(activity, intent, initPosition, cropAspectX, cropAspectY);
	}	
	
	private static void startActivity(BaseActivity activity, Intent intent, int initPosition, int cropAspectX, int cropAspectY)
	{
		if( initPosition >= 0 )
			intent.putExtra(INTENTKEY_POSITION, initPosition);
		intent.putExtra(INTENTKEY_CROP_ASPECT_X, cropAspectX);
		intent.putExtra(INTENTKEY_CROP_ASPECT_Y, cropAspectY);
		
		activity.moveActivityForResult(intent, Define.INTENT_RESULT_IMAGE_EDIT);
		activity.overridePendingTransition(R.anim.cacaostory_start_enter, R.anim.cacaostory_start_exit);
	}
	
	public Activity_ImageEditor() 
	{
		super();
		
		mLoadImages = new SparseArray<Bitmap>(IMAGE_MAX_COUNT);
		mLoadImageViews = new SparseArray<TouchImageView>(IMAGE_MAX_COUNT);
		
		mEditedFileDirPath = FileCache.getInstance().getTempFilePath();
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_image_editor);
		
		Intent intent = getIntent();
		
		mCurrentPosition = intent.getIntExtra(INTENTKEY_POSITION, 0);
		mCropAspectX = intent.getIntExtra(INTENTKEY_CROP_ASPECT_X, 0);
		mCropAspectY = intent.getIntExtra(INTENTKEY_CROP_ASPECT_Y, 0);
		
		mPhotoPickerImages = getShare().getPhotoPickerImages();
		
		if( mPhotoPickerImages != null )
			mTotalCount = mPhotoPickerImages.size();
		
		if( mCurrentPosition >= mTotalCount )
			mCurrentPosition = 0;

		String strTemp = getResources().getString(R.string.preview);

		setHeaderView(
				R.drawable.btn_back_white, String.format(strTemp+"%d/%d", mCurrentPosition+1,mTotalCount),R.drawable.btn_check);

		mThumbSize = 379;
		
		final TextView mainTitle = (TextView)findViewById(R.id.textview_navi_title);
		
		mainTitle.setText(Html.fromHtml(strTemp+"&nbsp;&nbsp;&nbsp;<small><font color=#999999>"+(mCurrentPosition+1)+"/"+mTotalCount+"</font></small>"));
		
		findViewById(R.id.header_layout).setBackgroundColor(Color.BLACK);
		
		//----------------------------------------------------------------------
		mViewPager = (ToggleViewPager)findViewById(R.id.image_editor_viewpager);
		
		mViewPagerAdapter = new ImageViewPagerAdapter();
		mViewPager.setOffscreenPageLimit(1);
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position) 
			{
				if( mCurrentPosition != position && mCurrentPosition >= 0 )
				{
					TouchImageView imageView = mLoadImageViews.get(mCurrentPosition);
					if( imageView != null )
						imageView.setDefaultState();
				}

				//SDSLog.d(SDSLog.TAGIDX_CKY,"페이지선택 mCurrentPosition="+ mCurrentPosition + " , position="+position);
				
				mLastSelectedPosition = mCurrentPosition;
				mCurrentPosition = position;
				mCurrentImageView = mLoadImageViews.get(position);
				String strTemp = getResources().getString(R.string.preview);
				mainTitle.setText(Html.fromHtml(strTemp+"&nbsp;&nbsp;&nbsp;<small><font color=#999999>"+(mCurrentPosition+1)+"/"+mTotalCount+"</font></small>"));
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) 
			{
				mIsPageScrolling = true;
				mViewPager.setPagingEnabled(true);
			}
			@Override
			public void onPageScrollStateChanged(int arg0) 
			{
				switch( arg0 )
				{
				case 0:
					mIsPageScrolling = false;
					if( mCurrentPosition != mLastSelectedPosition )
						mViewPager.setPagingEnabled(true);
					else
						mViewPager.setPagingEnabled(mIsViewPageEnable);
					mIsViewPageEnable = true;
					break;
					
				case 1:
					mCurrentPosition = mViewPager.getCurrentItem();
					mIsPageScrolling = true;
					break;
					
				default:
					mIsPageScrolling = true;
					break;
				}
			}
		});
		mViewPager.setCurrentItem(mCurrentPosition, true);
		
		findViewById(R.id.image_editor_rotate_imgbutton).setOnClickListener(this);
		findViewById(R.id.image_editor_crop_imgbutton).setOnClickListener(this);
		
		
		(new Timer()).schedule(new TimerTask() {
			@Override
			public void run() 
			{ 
				moveCrop();
			}
		}, 200);

	}
	
	public final class ImageViewPagerAdapter extends PagerAdapter
	{
		@Override
	    public Object instantiateItem(ViewGroup container, int position)
		{
			LinearLayout instantiateView = new LinearLayout(m_Context);
			
	        TouchImageView imageView = new TouchImageView(m_Context);
	        imageView.setTag(position);
	        imageView.setOnScaleChangedListener(new TouchImageView.OnScaleChangedListener()
	        {
	        	public void onScaleChanged(float scale)
	        	{
	        		if( mViewPager != null )
	        		{
	        			mIsViewPageEnable = scale==1.0f;
	        			if( mIsPageScrolling == true )
	        				mViewPager.setPagingEnabled(true);
	        			else
	        				mViewPager.setPagingEnabled(mIsViewPageEnable);
	        		}
	        	}

				@Override
				public void onScaleonDoubleTap() {
					// TODO Auto-generated method stub
					mViewPager.setPagingEnabled(false);
				}
	        });	        
	        mViewPager.setPagingEnabled(true);
	        
	        
	        if( mPhotoPickerImages != null && mPhotoPickerImages.isEmpty() == false )
	        {
	        	Data_PhotoPickerImage photoPickerImage = mPhotoPickerImages.get(position);
	        	
	        	Bitmap bitmap = WezoneUtil.loadBitmapOptimizeMemory(Activity_ImageEditor.this, photoPickerImage.path);
	        	if( bitmap == null )
	        		bitmap = BitmapFactory.decodeResource(Activity_ImageEditor.this.getResources(), R.drawable.img_file_error);

	        	imageView.setImageBitmap(bitmap);
	        	imageView.invalidate();
        		mLoadImages.put(position, bitmap);
	        }
	        mLoadImageViews.put(position, imageView);
	        
	        instantiateView.addView(imageView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
	        container.addView(instantiateView);
        	
        	if( mCurrentImageView == null )
        		mCurrentImageView = imageView;

        	return instantiateView;
	    }

	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object)
	    {
	    	LinearLayout layout = (LinearLayout)object;
	    	TouchImageView imageView = (TouchImageView)layout.getChildAt(0);
	    	
	    	if( mLoadImages.size() > 0 )
	    		mLoadImages.remove(position);
	    	
	    	if( mLoadImageViews.size() > 0 )
	    		mLoadImageViews.remove(position);
	    	
	    	layout.removeView(imageView);
	    	container.removeView(layout);
	    }

	    @Override
	    public int getCount() { return mTotalCount; }

	    @Override
	    public boolean isViewFromObject(View view, Object object) {
	        return (view == object);
	    }
	}

	@Override
	protected void onDestroy() 
	{
		if( mLoadImages != null )
		{
			int count;
			if( (count=mLoadImages.size()) > 0 )
			{
				for( int i = 0 ; i < count ; i++ )
					UIBitmap.bitmapDestory(mLoadImages.get(i));
				mLoadImages.clear();
				mLoadImages = null;
				
				if( mLoadImageViews != null )
				{
					mLoadImageViews.clear();
					mLoadImageViews = null;
				}
			}
		}
		super.onDestroy();
	}
	
	@Override
	public void onClickRightBtn(View v)
	{
//		String newFilePath = GoriUtil.createFitContentMaxSizeImageFile(boardImage.path);
		
		if( mIsDone == false )
		{
			mIsDone = true;
			
			if( mPhotoPickerImages != null && mPhotoPickerImages.size() >= 3 )
			{
				String str = getResources().getString(R.string.plz_wait);
				Toast.makeText(Activity_ImageEditor.this, str, Toast.LENGTH_SHORT).show();
				(new Timer()).schedule(new TimerTask() {
					@Override
					public void run()
					{ 
						setResult(RESULT_OK);
						finish();
					}
				}, 200);
			}
			else
			{
				setResult(RESULT_OK);
				finish();				
			}
		}
	}
	
	@Override
	public void onClick(View v)
	{
		final int clickedViewId = v.getId();
		
		Data_PhotoPickerImage photoPickerImage = mPhotoPickerImages.get(mCurrentPosition);
		
		if( clickedViewId == R.id.image_editor_rotate_imgbutton )
		{
			Bitmap bitmap = mLoadImages.get(mCurrentPosition);
			final Bitmap rotateBitmap = UIBitmap.rotate(bitmap, 90);
			if( rotateBitmap != null )
			{
				if( rotateBitmap != bitmap )
				{
					LinearLayout layout = (LinearLayout)mCurrentImageView.getParent();
					if( layout == null ) return;
					
					UIBitmap.bitmapDestory(bitmap);
					//UIBitmap.recycleBitmap(mCurrentImageView);
					
					
					TouchImageView imageView = new TouchImageView(m_Context);
					imageView.setTag(mCurrentPosition);
					layout.addView(imageView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
					
					layout.removeView(mCurrentImageView);
					
					mCurrentImageView = imageView;
					
					mCurrentImageView.setImageBitmap(rotateBitmap);
					mCurrentImageView.invalidate();

					mLoadImageViews.put(mCurrentPosition, mCurrentImageView);
					mLoadImages.put(mCurrentPosition, rotateBitmap);
					
					photoPickerImage.setRecycleThumbBitmap(ThumbnailUtils.extractThumbnail(rotateBitmap, mThumbSize, mThumbSize));
					
					try 
					{

						String imagePath = photoPickerImage.path;

						boolean isPng = imagePath.toLowerCase().endsWith(".png");

						String newFilePath;
						int offset = imagePath.lastIndexOf('/');
						if( offset > 0 )
							newFilePath = FileCache.getInstance().getTempFilePath()+imagePath.substring(offset+1);
						else
							newFilePath = FileCache.getInstance().getTempFilePath()+ String.valueOf(System.currentTimeMillis())+((isPng==true)?".png":".jpg");

						File file = new File(newFilePath);
						if( file.exists() == true ) file.delete();

						boolean isSucc = (isPng==true)?
								WezoneUtil.saveBitmapToPngFile(rotateBitmap, file, false):
								WezoneUtil.saveBitmapToJpgFile(rotateBitmap, file, false);

						if( isSucc )
						{
							photoPickerImage.path = newFilePath;
						}
					} 
					catch (Exception e) {}
				}
			}
		}
		else if( clickedViewId == R.id.image_editor_crop_imgbutton )
		{
			Intent intent = new Intent(this, Activity_ImageCrop.class);
			
			intent.putExtra(Activity_ImageCrop.INTENTKEY_PICKERIMAGE_POSITION, mCurrentPosition);
			
	        //intent.putExtra(Activity_ImageCrop.IMAGE_PATH, photoPickerImage.path);
	        intent.putExtra(Activity_ImageCrop.SCALE, true);

//	        intent.putExtra(Activity_ImageCrop.ASPECT_X, 3);
//	        intent.putExtra(Activity_ImageCrop.ASPECT_Y, 2);

	        intent.putExtra(Activity_ImageCrop.ASPECT_X, mCropAspectX);
	        intent.putExtra(Activity_ImageCrop.ASPECT_Y, mCropAspectY);
			
	        moveActivityForResult(intent,Define.INTENT_RESULT_IMAGE_CROP);
		}
	}
	
	public void moveCrop(){
		Intent intent = new Intent(this, Activity_ImageCrop.class);
		intent.putExtra(Activity_ImageCrop.INTENTKEY_PICKERIMAGE_POSITION, mCurrentPosition);
        intent.putExtra(Activity_ImageCrop.SCALE, true);
        intent.putExtra(Activity_ImageCrop.ASPECT_X, mCropAspectX);
        intent.putExtra(Activity_ImageCrop.ASPECT_Y, mCropAspectY);
        moveActivityForResult(intent,Define.INTENT_RESULT_IMAGE_CROP);
	}
	
	public void onClickCloseBtn(View v)
	{
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data); 

		switch(requestCode)
		{
		case Define.INTENT_RESULT_IMAGE_CROP:
			if( resultCode == RESULT_OK )
			{
				Data_PhotoPickerImage photoPickerImage = mPhotoPickerImages.get(mCurrentPosition);
				
				Bitmap bitmap = WezoneUtil.loadBitmapOptimizeMemory(Activity_ImageEditor.this, photoPickerImage.path);
				if( bitmap == null )
					return;
				
				LinearLayout layout = (LinearLayout)mCurrentImageView.getParent();
				
				layout.removeView(mCurrentImageView);
				
//				UIBitmap.bitmapDestory(mLoadImages.get(mCurrentPosition));
//				UIBitmap.recycleBitmap(mCurrentImageView);
				
				TouchImageView imageView = new TouchImageView(m_Context);
				imageView.setTag(mCurrentPosition);
				layout.addView(imageView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
				
				mCurrentImageView = imageView;
				mCurrentImageView.setImageBitmap(bitmap);
				mCurrentImageView.invalidate();

				mLoadImages.put(mCurrentPosition, bitmap);
				
				photoPickerImage.setRecycleThumbBitmap(ThumbnailUtils.extractThumbnail(bitmap, mThumbSize, mThumbSize));
			}
			break;
		}
	}
}

/*
					UIBitmap.bitmapDestory(bitmap);
					//UIBitmap.recycleBitmap(mCurrentImageView);
					
					LinearLayout layout = (LinearLayout)mCurrentImageView.getParent();
					//layout.removeViewAt(0);
					layout.removeView(mCurrentImageView);
					
					TouchImageView imageView = new TouchImageView(m_Context);
					imageView.setTag(mCurrentPosition);
					layout.addView(imageView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
					
					mCurrentImageView = imageView;
					
					mCurrentImageView.setImageBitmap(rotateBitmap);
					mCurrentImageView.invalidate();

					mLoadImages.put(mCurrentPosition, rotateBitmap);
					
					photoPickerImage.setRecycleThumbBitmap(ThumbnailUtils.extractThumbnail(rotateBitmap, mThumbSize, mThumbSize)); 
 */ 
