package com.vinetech.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.vinetech.wezone.Define;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

// 앱내에서 각각 생성해서 사용할 필요가 없으므로 SingleTone 방식으로 변경

/**
 * 01-24
 * -미사용코드 주석처리
 * -로직은 있으나 동작이 되지 않고있는 코드 주석 처리(캐시이미지 초기로드)
 * -널 체크 추가
 */
public final class FileCache {
    
	public static final String SAVED_FOLDER_NAME = "vinetech";
	
	public static FileCache instance;
	
    private File cacheRootDir;
    
    private File cacheContentImgDir, cacheUserImgDir;
    
    private File cacheTempDir, cacheChatDir;
	   
	public static FileCache getInstance()
	{ 
		return (instance==null)?(instance = new FileCache()):instance;
	}
	
	private FileCache() {}

    public void init(Context context)
    {
    	initCacheDir(context);
    }
    
    public MemoryCache initMemoryCache(Context context, MemoryCache memoryCache)
    {
    	init(context);
    	
    	/*
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(), SAVED_FOLDER_NAME);
        } else {
            cacheDir=context.getCacheDir();
        }

        if( cacheDir.exists() == false )
            cacheDir.mkdirs();        	

        m_bInitialize  = true;
        */
        
        /*
        //if( cacheDir.isFile()==true ) //기존코드수정전 
        if( cacheDir.isDirectory()==true ) //기존코드수정후 : 조기연
        {
        	File[] files = cacheDir.listFiles();
        	
        	for(File file : files)
        	{
        		String name   = file.getName();
        		
        		Bitmap bitmap = decodeFile(file);
        		
        		memoryCache.put(name, bitmap);
        	}
        }
        m_bInitialize  = true;
        */
        
        return memoryCache;
    }
    
    public File getFile(String url)
    {
    	if( url != null )
    	{
	        //String filename = String.valueOf(url.hashCode());
	        //String filename = URLEncoder.encode(url);
//	        File f = new File(cacheDir, filename);
//	        SDSLog.d(SDSLog.TAGIDX_CACHE,"파일캐시히트 = "+f.exists() + " "+filename);
//	        return f;
	        
//    		return new File(cacheRootDir, String.valueOf(url.hashCode()));
    		return new File(cacheContentImgDir, String.valueOf(url.hashCode()));
    	}
    	return null;
    }
    
    public File getUserImgFile(String url)
    {
    	return (url!=null)?new File(cacheUserImgDir, String.valueOf(url.hashCode())):null;
    }
    
    public File getContentImgFile(String url)
    {
    	return (url!=null)?new File(cacheContentImgDir, String.valueOf(url.hashCode())):null;
    }
    
    public String getTempFilePath()
    {     	
    	return cacheTempDir.getAbsolutePath()+"/";
    }
    
    public File getChatImageFile(String path)
    { 
    	return (path!=null)?new File(cacheChatDir, String.valueOf(path.hashCode())+".jpg"):null;
    }    
    
    public String getChatFilePath()
    { 
    	return cacheChatDir.getAbsolutePath()+"/"; 
    }
    

    /*
     * 미사용코드로 판단되어 주석처리함 : 조기연
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
//			BitmapFactory.Options o = new BitmapFactory.Options();
//			o.inJustDecodeBounds = true;
//			FileInputStream stream1 = new FileInputStream(f);
//			BitmapFactory.decodeStream(stream1, null, o);
//			stream1.close();

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = 1;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			SDSLog.e(e);
		}
		return null;
	}
	*/
    
    public void clear()
    {
    	clearImageCacheFiles();

//        File[] files=cacheRootDir.listFiles();
//        
//        if( files==null || files.length == 0 )
//            return;
//        
//        for(File f : files)
//            f.delete();
    }
    
	private void initCacheDir(Context context)
	{
//		getCacheDir()= /data/data/com.shinhan_stalk/cache
//		getExternalCacheDir()= /storage/sdcard0/Android/data/com.shinhan_stalk/cache
		if( cacheRootDir != null ) 
			return;
		
		if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO )
	    {
			cacheRootDir = (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) == true ||
							Environment.isExternalStorageRemovable()==false)?
							context.getExternalCacheDir() : context.getCacheDir();
	    }
	    else
	    	cacheRootDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/cache");
		
		final String cacheRootDirPath = cacheRootDir.getAbsolutePath() + File.separator;
	      
		
		cacheUserImgDir = createCacheDir(cacheRootDirPath + "user");
	    
//		cacheContentImgDir = createCacheDir(cacheRootDirPath + "content");
//
		cacheTempDir = createCacheDir(cacheRootDirPath + "temp");
//
//		cacheChatDir = createCacheDir(cacheRootDirPath + "chat");
	    
//	    SDSLog.d(SDSLog.TAGIDX_CACHE,"userImgDir="+cacheUserImgDir.getAbsolutePath());
//	    SDSLog.d(SDSLog.TAGIDX_CACHE,"userImgDir="+cacheUserImgDir.exists());
//	    SDSLog.d(SDSLog.TAGIDX_CACHE,"contentImgDir="+cacheContentImgDir.getAbsolutePath());
//	    SDSLog.d(SDSLog.TAGIDX_CACHE,"contentImgDir="+cacheContentImgDir.exists());
//	    SDSLog.d(SDSLog.TAGIDX_CACHE,"getTempFilePath()="+getTempFilePath());
	}
	
	private File createCacheDir(String path)
	{
		File dir = new File(path);
	    if( dir.exists() == false )
	    	dir.mkdir();
	    return dir;
	}

	/*
	@SuppressWarnings("unused")
	public void clearCacheFiles() 
	{
		deleteFiles(cacheRootDir, 0);
		clearUserImageCacheFiles();
		clearContentImageCacheFiles();
		deleteFiles(cacheTempDir, 0 );
		deleteFiles(cacheChatDir, 0 );
	}
	*/
	
	public void clearImageCacheFiles()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, Define.CACHE_USER_IMAGE_DELETE_TIME);
//		clearCacheFiles(cacheContentImgDir, calendar.getTimeInMillis() );
		clearCacheFiles(cacheUserImgDir, calendar.getTimeInMillis() );
		clearCacheFiles(cacheTempDir, calendar.getTimeInMillis() );
//		clearCacheFiles(cacheChatDir, calendar.getTimeInMillis() );
	}
    
	public void clearImageCacheFiles(long time)
	{
		clearCacheFiles(cacheContentImgDir, time);
		clearCacheFiles(cacheUserImgDir, time);
		clearCacheFiles(cacheTempDir, time);
		clearCacheFiles(cacheChatDir, time);
	}	
	
/*	
	public void clearContentImageCacheFiles()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, Define.CACHE_BOARD_IMAGE_DELETE_TIME);

		clearCacheFiles(cacheContentImgDir, calendar.getTimeInMillis());
	}
	public void clearContentImageCacheFiles(long time)
	{
		clearCacheFiles(cacheContentImgDir, time);
	}
	
	
	public void clearChatImageCacheFiles()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, Define.CACHE_BOARD_IMAGE_DELETE_TIME);

		clearCacheFiles(cacheChatDir, calendar.getTimeInMillis());
	}
	
	public void clearChatImageCacheFiles(long time)
	{
		clearCacheFiles(cacheChatDir, time);
	}
*/	
	
	public void clearCacheFiles(File cacheDir, long time)
	{
		deleteFiles(cacheDir, time );
	}
	
	private void deleteFiles(File rootDir, long deleteTime)
	{
		if( rootDir == null || rootDir.exists() == false )
			return;
		
	    for (File cacheFile : rootDir.listFiles())
	    {
	        if (cacheFile.isFile()) 
	        {
	            if( cacheFile.exists() == true ) 
	            {
	            	if( deleteTime == 0 || cacheFile.lastModified() < deleteTime )
	            	{
						if(Define.LOG_YN) {
							Log.d(Define.LOG_TAG, "캐시파일삭제 deleteTime= " + (new Date(deleteTime)).toLocaleString());
							Log.d(Define.LOG_TAG, "캐시파일삭제 cacheFile.lastModified()= " + (new Date(cacheFile.lastModified())).toLocaleString());
							Log.d(Define.LOG_TAG, "캐시파일삭제 = " + cacheFile.getAbsolutePath());
						}
	            		cacheFile.delete();
	            	}
	            }
	        else
	        	deleteFiles(cacheFile, deleteTime);
	        }
	    }
	}
}