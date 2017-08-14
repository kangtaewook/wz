package com.vinetech.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.vinetech.wezone.Define;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class LibFileManager {

	public static boolean isSDCardMount() {
		return Environment.getExternalStorageState().equals("mounted");
	}
	
	public static boolean makeDirectory(String strDirectoryPath) {
		
		if(isSDCardMount() == false)
			return false;		
		
		File fileHandle = new File(strDirectoryPath);
		return fileHandle.mkdirs();
	}
	
	public static boolean makeDirectory(java.io.File cacheDir, String strDirectoryPath) {

		File fileHandle = new File(cacheDir, strDirectoryPath);
		return fileHandle.mkdirs();
	}

	public static boolean isFile(String strFilePath) {

		if(isSDCardMount() == false)
			return false;

		File fileHandle = new File(strFilePath);
		return fileHandle.isFile();
	}

    public static boolean isFile (File file)
    {
    	return file.isFile();
    }

	public static boolean isFile(java.io.File cacheDir, String strFilePath) {

		File fileHandle = new File(cacheDir, strFilePath);
		return fileHandle.isFile();
	}

	public static long isFileSize(String strFilePath) {

		if(isSDCardMount() == false)
			return 0;

		File fileHandle = new File(strFilePath);
		return fileHandle.length();
	}

	public static long isFileSize(java.io.File cacheDir, String strFilePath) {

		File fileHandle = new File(cacheDir, strFilePath);
		return fileHandle.length();
	}

	public static boolean fileDelete(String strFilePath) {

		if(isSDCardMount() == false)
			return false;

		File fileHandle = new File(strFilePath);
		return fileHandle.delete();
	}

	public static boolean fileDelete(java.io.File cacheDir, String strFilePath) {

		File fileHandle = new File(cacheDir, strFilePath);
		return fileHandle.delete();
	}

	public static void fileWrite(String strFilePath, char[] buffer, int offset, int size, boolean type) {

		if(isSDCardMount() == false)
			return;

		Writer writer = null;
		FileOutputStream outputStream = null;
		try  {
			outputStream = new FileOutputStream(strFilePath, type);
			writer = new OutputStreamWriter(outputStream, "UTF-8");

			writer.write(buffer, offset, size);
			writer.close();

		} catch(Throwable e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FileWrite Fail..(" + e.toString() + ")");
			}
		} finally {
			writer = null;
			outputStream = null;
		}
	}

	public static void fileWrite(String strFilePath, byte[] buffer, int offset, int size, boolean type) {

		if(isSDCardMount() == false)
			return;

		FileOutputStream outputStream = null;
		BufferedOutputStream outputBuffer = null;
		try  {
			outputStream = new FileOutputStream(strFilePath, type);
			outputBuffer = new BufferedOutputStream(outputStream);

			outputBuffer.write(buffer, 0, size);
			outputBuffer.flush();
			outputBuffer.close();

		} catch(Throwable e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FileWrite Fail..(" + e.toString() + ")");
			}
		} finally {
			outputStream = null;
			outputBuffer = null;
		}
	}

	public static void fileWrite(java.io.File cacheDir, String strFilePath, byte[] buffer, int offset, int size, boolean type) {

		if(cacheDir == null)
			return;

		File fileHandler = null;
		FileOutputStream outputStream = null;
		BufferedOutputStream outputBuffer = null;
		try  {
			fileHandler  = new File(cacheDir, strFilePath);
			outputStream = new FileOutputStream(fileHandler, type);
			outputBuffer = new BufferedOutputStream(outputStream);

			outputBuffer.write(buffer, 0, size);
			outputBuffer.flush();
			outputBuffer.close();

		} catch(Throwable e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FileWrite Fail..(" + e.toString() + ")");
			}
		} finally {
			outputStream = null;
			outputBuffer = null;
		}
	}

	public static void fileWriteFromBitmap(Bitmap bitmap, java.io.File cacheDir, String strFilePath) {
		File fileHandle = null;
		FileOutputStream outputStream = null;

		try {
			fileHandle = new File(cacheDir, strFilePath);
			outputStream = new FileOutputStream(fileHandle, false);

			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		} catch(Exception e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FileWrite Fail..(" + e.toString() + ")");
			}
		} finally {
			outputStream = null;
		}
	}

	public static void fileWriteInFile(String strFilePath, byte[] buffer, int offset, int size) {

		if(isSDCardMount() == false)
			return;

		File fileHandler = null;
		RandomAccessFile randomAccess = null;
		try {
			fileHandler = new File(strFilePath);
			randomAccess = new RandomAccessFile(fileHandler, "rw");

			randomAccess.seek(offset);
			randomAccess.write(buffer, 0, size);
			randomAccess.close();

		} catch(Throwable e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FileWrite Fail..(" + e.toString() + ")");
			}
		} finally {
			fileHandler = null;
			randomAccess = null;
		}
	}

	public static char[] fileReadToChar(String strFilePath, int offset, int size) {

		if(!isFile(strFilePath))
			return null;

		char buffer[] = null;
		Reader reader = null;
		FileInputStream inputStream = null;
		try  {
			inputStream = new FileInputStream(strFilePath);
			reader = new InputStreamReader(inputStream, "UTF-8");

		    buffer = new char[size];
		    reader.read(buffer, offset, size);
		    reader.close();

		} catch(Throwable e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FileRead Fail..(" + e.toString() + ")");
			}
		} finally {
			reader = null;
			inputStream = null;
		}

		return buffer;
	}

	public static byte[] fileReadToByte(String strFilePath) {

		if(!isFile(strFilePath))
			return null;

		byte[] buffer = null;
		FileInputStream inputStream = null;
		BufferedInputStream inputBuffer = null;
		try  {
			int nLength = (int)isFileSize(strFilePath);
			buffer = new byte[nLength];

			inputStream = new FileInputStream(strFilePath);
			inputBuffer = new BufferedInputStream(inputStream);

			inputBuffer.read(buffer, 0, nLength);
			inputBuffer.close();

		} catch(Throwable e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FileRead Fail..(" + e.toString() + ")");
			}
		} finally {
			inputBuffer = null;
			inputStream = null;
		}

		return buffer;
	}

	public static byte[] fileReadToByte(File cacheDir, String strFilePath) {

		if(!isFile(cacheDir, strFilePath))
			return null;

		byte[] buffer = null;
		File fileHandle = null;
		FileInputStream inputStream = null;
		BufferedInputStream inputBuffer = null;
		try  {

			int nLength = (int)isFileSize(cacheDir, strFilePath);
			buffer = new byte[nLength];

			fileHandle	= new File(cacheDir, strFilePath);
			inputStream = new FileInputStream(fileHandle);
			inputBuffer = new BufferedInputStream(inputStream);

			inputBuffer.read(buffer, 0, nLength);
			inputBuffer.close();

		} catch(Throwable e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FileRead Fail..(" + e.toString() + ")");
			}
		} finally {
			fileHandle	= null;
			inputBuffer = null;
			inputStream = null;
		}

		return buffer;
	}

	public static byte[] fileReadToByte(String strFilePath, int offset, int size) {

		byte[] buffer = new byte[size];
		FileInputStream inputStream = null;
		BufferedInputStream inputBuffer = null;
		try  {

			inputStream = new FileInputStream(strFilePath);
			inputBuffer = new BufferedInputStream(inputStream);

			inputBuffer.skip(offset);
			inputBuffer.read(buffer, 0, size);
			inputBuffer.close();

		} catch(Throwable e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FileRead Fail..(" + e.toString() + ")");
			}
		} finally {
			inputBuffer = null;
			inputStream = null;
		}

		return buffer;
	}

	public static boolean fileReadToByte(String strFilePath, byte[] buffer, int offset, int size) {

		FileInputStream inputStream = null;
		BufferedInputStream inputBuffer = null;
		try  {
			inputStream = new FileInputStream(strFilePath);
			inputBuffer = new BufferedInputStream(inputStream);

			inputBuffer.skip(offset);
			inputBuffer.read(buffer, 0, size);
			inputBuffer.close();

		} catch(Throwable e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FileRead Fail..(" + e.toString() + ")");
			}
		} finally {
			inputBuffer = null;
			inputStream = null;
		}

		return true;
	}

	public static boolean fileCopy(String strOriPath, String strDestPath, boolean type) {

		int nLength		= 0;
		int nSize		= 0;
		byte[] buffer	= null;
		FileInputStream inputStream 	= null;
		FileOutputStream outputStream	= null;

		try {
			inputStream = new FileInputStream(strOriPath);
			nSize		= inputStream.available();
			buffer		= new byte[nSize];

			outputStream = new FileOutputStream(strDestPath, type);
			while((nLength = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, nLength);
			}

			inputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FILE COPY ERROR :" + e.getMessage());
			}
			return false;
		}

		return true;
	}

	public static boolean fileCopy(File cacheDir, String strOriPath, String strDestPath, boolean type) {

		int nLength		= 0;
		int nSize		= 0;
		byte[] buffer	= null;
		FileInputStream inputStream 	= null;
		FileOutputStream outputStream	= null;
		File fileOriHandler				= new File(cacheDir, strOriPath);
		File fileDesHandler				= new File(cacheDir, strDestPath);

		try {
			inputStream = new FileInputStream(fileOriHandler);
			nSize		= inputStream.available();
			buffer		= new byte[nSize];

			outputStream = new FileOutputStream(fileDesHandler, type);
			while((nLength = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, nLength);
			}

			inputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FILE COPY ERROR :" + e.getMessage());
			}
			return false;
		}

		return true;
	}

	public static boolean fileCopyToSdCard(File cacheDir, String strOriPath, String strDestPath, boolean type) {

		int nLength		= 0;
		int nSize		= 0;
		byte[] buffer	= null;
		FileInputStream inputStream 	= null;
		FileOutputStream outputStream	= null;
		File fileOriHandler				= new File(cacheDir, strOriPath);

		try {
			inputStream = new FileInputStream(fileOriHandler);
			nSize		= inputStream.available();
			buffer		= new byte[nSize];

			outputStream = new FileOutputStream(strDestPath, type);
			while((nLength = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, nLength);
			}

			inputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FILE COPY ERROR :" + e.getMessage());
			}
			return false;
		}

		return true;
	}

	public static List<String> isSearchDirectory(String strPath, final String strExt) {

		List<String> fileSearchList = new ArrayList<String>();

		File fileSearch = new File(strPath);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File file, String name) {
				return(name.endsWith(strExt));
			}
		};

		File fileList[] = fileSearch.listFiles(filter);
		if(fileList != null) {
			for(File file : fileList) {
				fileSearchList.add(file.getName());
			}
		}

		return fileSearchList;
	}

	public static boolean clearApplicationCache(java.io.File cacheDir) {
		if(cacheDir == null)
			return false;

		File[] subFileHandler = cacheDir.listFiles();
		try {
			for(int i = 0; i < subFileHandler.length; i++) {
				if(subFileHandler[i].isDirectory()) {
					clearApplicationCache(subFileHandler[i]);
				} else {
					subFileHandler[i].delete();
				}
			}
		} catch(Exception e) {
			return false;
		}

		return true;
	}

	public static boolean clearApplicationCache(java.io.File cacheDir, String strPath) {
		if(cacheDir == null)
			return false;

		File rootFileHandler = new File(cacheDir, strPath);
		File[] subFileHandler = rootFileHandler.listFiles();
		try {
			for(int i = 0; i < subFileHandler.length; i++) {
				if(subFileHandler[i].isDirectory()) {
					clearApplicationCache(subFileHandler[i]);
				} else {
					subFileHandler[i].delete();
				}
			}
		} catch(Exception e) {
			return false;
		}

		return true;
	}

	public static boolean getAssetsToCopy(AssetManager manager, String strAssetPath, String strPath, boolean type) {

		int nLength		= 0;
		int nSize		= 0;
		byte[] buffer	= null;
		InputStream inputStream = null;
		FileOutputStream outputStream	= null;

		try {
			inputStream = manager.open(strAssetPath);
			nSize		= inputStream.available();
			buffer		= new byte[nSize];

			outputStream = new FileOutputStream(strPath, type);
			while((nLength = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, nLength);
			}

			inputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FILE GET ASSETS ERROR :" + e.getMessage());
			}
			return false;
		}

		return true;
	}

	public static boolean getAssetsToCopy(AssetManager manager, String strAssetPath, File cacheDir, String strPath, boolean type) {

		int nLength		= 0;
		int nSize		= 0;
		byte[] buffer	= null;
		InputStream inputStream 		= null;
		FileOutputStream outputStream	= null;
		File fileHandler				= new File(cacheDir, strPath);

		try {
			inputStream = manager.open(strAssetPath);
			nSize		= inputStream.available();
			buffer		= new byte[nSize];

			outputStream = new FileOutputStream(fileHandler, type);
			while((nLength = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, nLength);
			}

			inputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FILE GET ASSETS ERROR :" + e.getMessage());
			}
			return false;
		}

		return true;
	}

	public static boolean getResourceToCopy(Resources resource, int resourceId, String strPath, boolean type) {

		int nLength		= 0;
		int nSize		= 0;
		byte[] buffer	= null;

		InputStream inputStream			= resource.openRawResource(resourceId);
		FileOutputStream outputStream	= null;

		try {
			nSize	= inputStream.available();
			buffer	= new byte[nSize];

			outputStream = new FileOutputStream(strPath, type);
			while((nLength = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, nLength);
			}

			inputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (Exception e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FILE GET RESOURCE ERROR :" + e.getMessage());
			}
			return false;
		}

		return true;
	}

	public static boolean getResourceToCopy(Resources resource, int resourceId, File cacheDir, String strPath, boolean type) {

		int nLength		= 0;
		int nSize		= 0;
		byte[] buffer	= null;

		InputStream inputStream			= resource.openRawResource(resourceId);
		FileOutputStream outputStream	= null;
		File fileHandler				= new File(cacheDir, strPath);

		try {
			nSize	= inputStream.available();
			buffer	= new byte[nSize];

			outputStream = new FileOutputStream(fileHandler, type);
			while((nLength = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, nLength);
			}

			inputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (Exception e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, "FILE GET RESOURCE ERROR :" + e.getMessage());
			}
			return false;
		}

		return true;
	}

	public static long FileLastModified(java.io.File cacheDir, String strFilePath) {
		
		File fileHandle = new File(cacheDir, strFilePath);
		return fileHandle.lastModified();
	}
	
	public static String getPath(Context context, Uri uri) throws URISyntaxException {
	    if ("content".equalsIgnoreCase(uri.getScheme())) {
	        String[] projection = { "_data" };
	        Cursor cursor = null;

	        try {
	            cursor = context.getContentResolver().query(uri, projection, null, null, null);
	            int column_index = cursor.getColumnIndexOrThrow("_data");
	            if (cursor.moveToFirst()) {
	                return cursor.getString(column_index);
	            }
	        } catch (Exception e) {
	            // Eat it
	        }
	    }
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	} 
	
	public static String getFileName(String path){
		
		String file = path.substring(path.lastIndexOf('/') + 1);
		
		return file.substring(0, file.indexOf('.'));
	}
	
	public static String getFileFullName(String path){
		
		String file = path.substring(path.lastIndexOf('/') + 1);
		
		return file;
	}

	public static String getFileExtension(String path){
		
		return path.substring(1, path.indexOf('.'));
	}
}
