package com.vinetech.util;

import android.graphics.Bitmap;
import android.os.Build;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 폰의 메모리에서 25%를 캐시 용량으로 사용한다.
 * 
 */
public class MemoryCache {

    private static final String TAG = "MemoryCache";
    
  //Last argument true for LRU ordering
    private final Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10,1.5f,true));
    
    private long size;//current allocated size
    
    private long limit = 1000000;//max memory in bytes

    private final static MemoryCache m_Instance = new MemoryCache();
    
    public static MemoryCache getInstance()
    {
    	return m_Instance;
    }
    
    private MemoryCache()
    {
        setLimit(Math.max(4*1024*1024, Runtime.getRuntime().maxMemory()/10));
    }
    
    public void setLimit(long new_limit){
        limit=new_limit;
        //Log.i(TAG, "MemoryCache will use up to "+limit/1024./1024.+"MB");
    }

    //수정 : 조기연
    public Bitmap get(String id)
    {
    	//해당 메소드가 예외를 유발하진 않음 / 널체크를 하고 try/catch를 제거하여 속도개선처리
        return ( id != null && cache.containsKey(id) == true )?cache.get(id) : null;

//		if( id != null && cache.containsKey(id) == true )
//    	{
//    		return cache.get(id);
//    	}
//    	return null;
    	
//        try
//        {
//            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
//            return cache.get(id);
//        }
//        catch(NullPointerException ex)
//        {
//        }
//        return null;
    }

    public void put(String id, Bitmap bitmap)
    {
        try
        {
            if( cache.containsKey(id) )
                size -= getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        }catch(Throwable th){
//            th.printStackTrace();
        }
    }
    
    /**
     * TODO: 캐시메모리가 꽉 차면 오래된 캐시데이터를 삭제해가면서 적정사이즈를 유지한다
     * 이 때 캐시에서 삭제되는 이미지 데이터를 언제 리사이클링 할 것인지 확인이 필요하다.
     * 조기연
     */
    private void checkSize() {
        //Log.i(TAG, "cache size="+size+" length="+cache.size());
        if(size>limit){
            Iterator<Entry<String, Bitmap>> iter=cache.entrySet().iterator();//least recently accessed item will be the first one iterated
            while(iter.hasNext()){
                Entry<String, Bitmap> entry=iter.next();
                size-=getSizeInBytes(entry.getValue());
                iter.remove();
                if(size<=limit)
                    break;
            }
            //Log.i(TAG, "Clean cache. New size "+cache.size());
        }
    }

    /**
     * TODO: 모든 캐시데이터 삭제시 이미지 데이터를 리사이클링 해야하는지 확인이 필요하다.
     * 조기연
     */
    public void clear() {
    	
    	//해당 메소드가 예외를 유발하진 않음 / 널체크를 하고 try/catch를 제거하여 속도개선처리
    	if( cache != null && cache.isEmpty() == false )
    	{
    		Collection<Bitmap> bitmaps = cache.values();
    		for( Bitmap bitmap : bitmaps )
    		{
    			if( bitmap != null && bitmap.isRecycled() == false )
    				bitmap.recycle();
    		}
    		cache.clear();
    	}
        size=0;
    	
//        try{
//            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78 
//            cache.clear();
//            size=0;
//        }catch(NullPointerException ex){
//            ex.printStackTrace();
//        }
    }

    private long getSizeInBytes(Bitmap bitmap) {

    	if( bitmap != null )
    	{
    		return ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1 )?
    				bitmap.getByteCount():
    				bitmap.getRowBytes() * bitmap.getHeight();
    	}
    	return 0;
    	//기존코드 
    	//return ( bitmap == null )?0 : bitmap.getRowBytes() * bitmap.getHeight();
    	
//    	
//    	if(bitmap==null)
//            return 0;
//        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}