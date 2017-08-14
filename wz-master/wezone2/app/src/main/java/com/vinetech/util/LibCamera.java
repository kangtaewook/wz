package com.vinetech.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class LibCamera {
	
	private final String SHARE_KEY_QUERY_EXTERNAL_CONTENT_URI = "SHARE_KEY_CAMERA_EXTERNAL_CONTENT_URI";
	@SuppressWarnings("unused")
	private final int CHECK_EXTERNAL_CONTENT_URI_NONE = 0;
	private final int CHECK_EXTERNAL_CONTENT_URI_ENABLE = 1;
	private final int CHECK_EXTERNAL_CONTENT_URI_DISABLE = 2;
	
	private static final String TEMP_PHOTO_FILE = "tempFile.jpg";
	
	private Context m_Context;
	private Uri m_TempImageUri;
	
	public String filePath;
	
	private int checkExternalContentUriState;
	
	public LibCamera(Context context) {
		m_Context      = context;
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		checkExternalContentUriState = pref.getInt(SHARE_KEY_QUERY_EXTERNAL_CONTENT_URI, CHECK_EXTERNAL_CONTENT_URI_DISABLE);
		
		//TODO : 추후 단말 목록 추가 또는 공통된 체크방법을 추가해야함
//		if( checkExternalContentUriState == CHECK_EXTERNAL_CONTENT_URI_ENABLE )
//		{
//			m_TempImageUri = null;
//		
//		}
//		else
//		
		if( isQueryExternalContentUriDevice() == true )
		{
			SharedPreferences.Editor editPref = pref.edit();
			editPref.putInt(SHARE_KEY_QUERY_EXTERNAL_CONTENT_URI, CHECK_EXTERNAL_CONTENT_URI_ENABLE);
			editPref.commit();
		}
		else
		{
			m_TempImageUri = getTempUri();
		}
	}
	//TODO : 단말이 지속적으로 추가될 가능성이 있음 
	private boolean isQueryExternalContentUriDevice()
	{
		final String[] MODEL_PREFIXES = {"LG-F240"/*옵티머스G프로*/,};
		
		for( String MODEL_PREFIEX : MODEL_PREFIXES )
		{
			if( Build.MODEL.startsWith(MODEL_PREFIEX) == true )
				return true;
		}
		return false;
	}

	
	/**
	 * @brief					   카메라를 호출한다.					
	 * @date 					   2013. 9. 13.		
	 * @param activity_requestCode 카메라로 찍고 activityresult 로 돌아갈 requestCode	
	 */
	public void takeCamera(int activity_requestCode){
		// 카메라를 호출합니다.
		Intent camaraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
		
		if( m_TempImageUri != null )
			camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, m_TempImageUri);
		else
			camaraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		((Activity) m_Context).startActivityForResult(camaraIntent, activity_requestCode);
	}

	/**
	 * @brief					카메라로 찍은 데이터를 Bitmap으로 parsing 한다.
	 * @date 					2013. 9. 13.
	 * @return 					parsing된 bitmap
	 * @throws IOException
	 */
	public Bitmap parseTakeCameraData(Intent intent) throws IOException
	{
		String imagePath;

		if( m_TempImageUri == null )
		{
			m_TempImageUri = intent.getData();
			imagePath = getPath(m_TempImageUri);
		}
		else
		{
			imagePath = m_TempImageUri.getPath();

			File checkFile = new File(imagePath);
			if( checkFile.exists() == false || checkFile.length() == 0 )
			{
				SharedPreferences.Editor editPref = PreferenceManager.getDefaultSharedPreferences(m_Context).edit();
				editPref.putInt(SHARE_KEY_QUERY_EXTERNAL_CONTENT_URI, CHECK_EXTERNAL_CONTENT_URI_ENABLE);
				editPref.commit();
			}
		}
		filePath = imagePath;

		Bitmap bitmap = setImageResize(m_TempImageUri, imagePath);

		if( bitmap != null )
		{
			// 이미지를 상황에 맞게 회전시킨다
			ExifInterface exif  = new ExifInterface(imagePath);
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			int exifDegree 		= exifOrientationToDegrees(exifOrientation);

			Bitmap rotateBitmap = rotate(bitmap, exifDegree);
			if( rotateBitmap != null && rotateBitmap != bitmap )
			{
				UIBitmap.bitmapDestory(bitmap);
				bitmap = rotateBitmap;
			}
		}
		return bitmap;
	}

	/**
	 * @brief			갤러리를 호출한다.
	 * @date 			2013. 9. 13.
	 * @param activity_requestCode	카메라로 찍고 activityresult 로 돌아갈 requestCode
	 */
	public void picGallary(int activity_requestCode){
		// 카메라를 호출합니다.
		Intent selectPickIntent = new Intent(Intent.ACTION_PICK);
		selectPickIntent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		selectPickIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		((Activity) m_Context).startActivityForResult(selectPickIntent, activity_requestCode);
	}
	
	/**
	 * @brief			갤러리로 선택한 데이터를 Bitmap으로 parsing 한다.					
	 * @date 			2013. 9. 13.		
	 * @param uri		activityResult에서 받은 intent uri
	 * @return			parsing 된 Bitmap
	 * @throws IOException
	 */
	public Bitmap parsePicGallaryData(Uri uri) throws IOException {
		// 비트맵 이미지로 가져온다
		String imagePath 	= getPath(uri);
		Bitmap bitmap    	= setImageResize(uri, imagePath);
		
		// 이미지를 상황에 맞게 회전시킨다
		ExifInterface exif  = new ExifInterface(imagePath);
		int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		int exifDegree 		= exifOrientationToDegrees(exifOrientation);
		bitmap 				= rotate(bitmap, exifDegree);
		return bitmap;
	}
	
	private String getPath(Uri uri) {
		String path = "content://media"+uri.getPath();
		Cursor c	= m_Context.getContentResolver().query(Uri.parse(path), null, null, null, null);
		c.moveToNext();		
		return c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
	}
	
	/**
	 * EXIF정보를 회전각도로 변환하는 메서드
	 * 
	 * @param exifOrientation EXIF 회전각
	 * @return 실제 각도
	 */
	private int exifOrientationToDegrees(int exifOrientation)
	{
	  if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
	  {
	    return 90;
	  }
	  else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
	  {
	    return 180;
	  }
	  else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
	  {
	    return 270;
	  }
	  return 0;
	}

	
	private File getTempFile()
	{
        if (LibFileManager.isSDCardMount()) 
        {
        	File cacheRootDir;
    		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO )
    	    {
    			cacheRootDir = (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) == true ||
    							Environment.isExternalStorageRemovable()==false)?
    							m_Context.getExternalCacheDir() : m_Context.getCacheDir();
    	    }
    	    else
    	    	cacheRootDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + m_Context.getPackageName() + "/cache");
    		
    		final String cacheRootDirPath = cacheRootDir.getAbsolutePath() + File.separator;
        	
            File f = new File(cacheRootDirPath, TEMP_PHOTO_FILE); // 외장메모리 경로
            
            try {
            	if(f.exists()){
            		f.delete();
            	}
            	
                f.createNewFile();      // 외장메모리에 temp.jpg 파일 생성
            } catch (IOException e) {
            }
 
            return f;
        } else
            return null;
    }
	
	/** 임시 저장 파일의 경로를 반환 */
    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }
    
	public Bitmap setImageResize(Uri imageUri, String imagePath) throws FileNotFoundException {
		
		AssetFileDescriptor afd;
		
		afd = m_Context.getContentResolver().openAssetFileDescriptor(imageUri, "r");
		
		
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
		
		Bitmap bitmap = null;
		
		if(bResize){
			
			final int REQUIRED_SIZE = 512;
			int width_tmp = opt.outWidth, height_tmp = opt.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			
			BitmapFactory.Options opt2 = new BitmapFactory.Options();
			opt2.inSampleSize = scale;
			bitmap = BitmapFactory.decodeFile(imagePath, opt2);
			
		
		} else {
			bitmap = BitmapFactory.decodeFile(imagePath);
			
		}
		return bitmap;
	}
	
	private static Bitmap rotate(Bitmap bitmap, int degrees) {
		
		if (degrees != 0 && bitmap != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees);
			try {
				return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			} catch (OutOfMemoryError ex) {
				throw new OutOfMemoryError();
			}
		}
		return bitmap;
	}
}
