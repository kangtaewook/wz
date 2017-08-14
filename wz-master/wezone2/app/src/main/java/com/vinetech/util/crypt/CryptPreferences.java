package com.vinetech.util.crypt;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.vinetech.util.WezoneUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Set;

public final class CryptPreferences 
{
	private final static String KEY = "wezoneprefs";
	
	private static SharedPreferences sPrefs;
	
	private static SharedPreferences.Editor sPrefsEditor;
	
	private static AesCrypt sCrypto;

	public static SharedPreferences getPrefs(Context context)
	{
		if( sPrefs == null && context != null )
		{
			sPrefs = context.getSharedPreferences(KEY, Activity.MODE_PRIVATE);
			sPrefsEditor = sPrefs.edit();
		}
		
		
		return sPrefs;
	}
	
	public static AesCrypt getCrypto(Context context){
		
		if( sCrypto == null )
		{
			final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
			
			CryptHelper cryptHelper = CryptHelper.getInstance();

			final String resultKey = cryptHelper.toHashCryptKey(Math.abs(androidId.hashCode()), Math.abs(cryptHelper.getKey().hashCode()));
			sCrypto = new AesCrypt(( resultKey.length() > 16 )?resultKey.substring(0,16):resultKey);
		}
		
		return sCrypto;
	}
	
	public static SharedPreferences.Editor getPrefsEditor(Context context)
	{
		if( sPrefsEditor == null && context != null )
			getPrefs(context);
		
		return sPrefsEditor;
	}
	
	
	public static void putBoolean(Context context, String key, boolean value)
	{ 
		putBoolean(context, key, value, true); 
	}
	public static void putBoolean(Context context, String key, boolean value, boolean isCommit)
	{
		getPrefsEditor(context).putBoolean(key, value);
		if( isCommit == true )
			sPrefsEditor.commit();
	}
	
	public static void putInt(Context context, String key, int value)
	{ 
		putInt(context, key, value, true); 
	}
	public static void putInt(Context context, String key, int value, boolean isCommit)
	{
		getPrefsEditor(context).putInt(key, value);
		if( isCommit == true )
			sPrefsEditor.commit();
	}
	
	public static void putString(Context context, String key, String value)
	{ 
		putString(context, key, value, true);
	}
	public static void putString(Context context, String key, String value, boolean isCommit)
	{
		if( value != null )
			getPrefsEditor(context).putString(key, value);
		else
			getPrefsEditor(context).remove(key);
		
		if( isCommit == true )
			sPrefsEditor.commit();					
	}	
	
	//Array 저장
	public static void putArrayList(Context context, String Key, ArrayList<String> array){
		putArrayList(context, Key, array, true);
	}
	
	//Array 저장
	public static void putArrayList(Context context, String key, ArrayList<String> array, boolean isCommit)
	{
		JSONArray jArray = new JSONArray(array);
		
		if( array != null ){
			getPrefsEditor(context).putString(key, jArray.toString());
		}else{
			getPrefsEditor(context).remove(key);
		}
		
		if( isCommit == true ){
			sPrefsEditor.commit();
		}
	}
	
	//Array 저장
	public static void putStringSet(Context context, String Key, Set<String> values){
		putStringSet(context, Key, values, true);
	}
	
	//StringSet 저장
	public static void putStringSet(Context context, String key, Set<String> values, boolean isCommit)
	{
		if( values != null )
			getPrefsEditor(context).putStringSet(key, values);
		else
			getPrefsEditor(context).remove(key);
		
		if( isCommit == true )
			sPrefsEditor.commit();	
	}
	
	
	
	
	public static void putCryptBoolean(Context context, String key, boolean value)
	{ 
		putCryptBoolean(context, key, value, true, true); 
	}
	public static void putCryptBoolean(Context context, String key, boolean value, boolean isCommit)
	{
		putCryptString(context, key, String.valueOf(value), true, isCommit);
	}
	private static void putCryptBoolean(Context context, String key, boolean value, boolean isCheckRemoveKey, boolean isCommit)
	{
		putCryptString(context, key, String.valueOf(value), isCheckRemoveKey, isCommit);
	}
	
	public static void putCryptInt(Context context, String key, int value)
	{ 
		putCryptInt(context, key, value, true, true); 
	}
	public static void putCryptInt(Context context, String key, int value, boolean isCommit)
	{
		putCryptString(context, key, String.valueOf(value), true, isCommit);
	}
	public static void putCryptInt(Context context, String key, int value, boolean isCheckRemoveKey, boolean isCommit)
	{
		putCryptString(context, key, String.valueOf(value), isCheckRemoveKey, isCommit);
	}	
	
	public static void putCryptString(Context context, String key, String value)
	{ 
		putCryptString(context, key, value, true, true);
	}
	public static void putCryptString(Context context, String key, String value, boolean isCommit)
	{
		putCryptString(context, key, value, true, isCommit);
	}
	
	public static void putCryptString(Context context, String key, String value, boolean isCheckRemoveKey, boolean isCommit)
	{
		try 
		{
			if( getPrefs(context).contains(key) == true && isCheckRemoveKey == true )
			{
				sPrefsEditor.remove(key);
				if( isCommit == false )
					sPrefsEditor.commit();
			}		
			
			if( value != null )
				sPrefsEditor.putString(getCrypto(context).toEncryptStr(key), getCrypto(context).toEncryptStr(value));
			else
				sPrefsEditor.remove(getCrypto(context).toEncryptStr(key));
			
			if( isCommit == true )
				sPrefsEditor.commit();					
		} 
		catch (Exception e)
		{
		}
	}
	
	public static boolean getBoolean(Context context, String key) { return getBoolean(context, key, false); }
	
	public static boolean getBoolean(Context context, String key, boolean defaultValue)
	{
		if( getPrefs(context).contains(key) == true )
		{
			try {
				return sPrefs.getBoolean(key, defaultValue);	
			} catch (Exception e) { return defaultValue; }
		}
		else
		{
			SharedPreferences prefs = context.getSharedPreferences(key, Activity.MODE_PRIVATE);
			if( prefs.contains(key) == true )
			{
				defaultValue = prefs.getBoolean(key, defaultValue);
				removeKeyPrefs(prefs, key);
				
				putBoolean(context, key, defaultValue, true);
			}	
			return defaultValue;
		}
	}
	
	public static boolean getCryptBoolean(Context context, String key, boolean defaultValue)
	{
		boolean value = defaultValue;
		
		try 
		{
			String encryptedKey = getCrypto(context).toEncryptStr(key);
			if( getPrefs(context).contains(encryptedKey) == true )
			{
				String encryptedValue = sPrefs.getString(encryptedKey, null);
				if( encryptedValue != null && (encryptedValue = getCrypto(context).toDecryptStr(encryptedValue)) != null )
					value = WezoneUtil.parseBoolean(encryptedValue, defaultValue);
//				else
//					value = defaultValue;			
			}
			else
			{
				SharedPreferences prefs = context.getSharedPreferences(key, Activity.MODE_PRIVATE);
				if( prefs.contains(key) == true )
				{
					value = prefs.getBoolean(key, defaultValue);
					removeKeyPrefs(prefs, key);
					
					putCryptBoolean(context, key, value, false, true);
				}
				else if( sPrefs.contains(key) == true )
				{
					value = sPrefs.getBoolean(key, defaultValue);
					sPrefsEditor.remove(key);
					
					putCryptBoolean(context, key, value, false, true);
				}			
//				else 
//				{
//					value = defaultValue;
//				}
			}			
		} 
		catch (Exception e)
		{
		}
		return value;
	}
	
	public static int getInt(Context context, String key, int defaultValue)
	{
		if( getPrefs(context).contains(key) == true )
		{
			try {
				return sPrefs.getInt(key, defaultValue);	
			} catch (Exception e) { return defaultValue; }
		}
		else
		{
			SharedPreferences prefs = context.getSharedPreferences(key, Activity.MODE_PRIVATE);
			if( prefs.contains(key) == true )
			{
				defaultValue = prefs.getInt(key, defaultValue);
				removeKeyPrefs(prefs, key);
				
				putInt(context, key, defaultValue, true);
			}	
			return defaultValue;
		}
	}
	
	public static int getCryptInt(Context context, String key, int defaultValue)
	{
		int value = defaultValue; 
		
		try 
		{
			String encryptedKey = getCrypto(context).toEncryptStr(key);
			if( getPrefs(context).contains(encryptedKey) == true )
			{
				String encryptedValue = sPrefs.getString(encryptedKey, null);
				if( encryptedValue != null && (encryptedValue = getCrypto(context).toDecryptStr(encryptedValue)) != null )
					value = WezoneUtil.parseInt(encryptedValue, defaultValue);
//				else
//					value = defaultValue; 
			}
			else
			{
				SharedPreferences prefs = context.getSharedPreferences(key, Activity.MODE_PRIVATE);
				if( prefs.contains(key) == true )
				{
					value = prefs.getInt(key, defaultValue);
					removeKeyPrefs(prefs, key);
					
					putCryptInt(context, key, value, false, true);
				}
				else if( sPrefs.contains(key) == true )
				{
					value = sPrefs.getInt(key, defaultValue);
					sPrefsEditor.remove(key);
					
					putCryptInt(context, key, value, false, true);
				}			
//				else
//				{
//					value = defaultValue;
//				}
			}			
		} 
		catch (Exception e)
		{
		}
		return value;
	}
	
	public static String getString(Context context, String key, String defaultValue)
	{
		if( getPrefs(context).contains(key) == true )
		{
			try {
				return sPrefs.getString(key, defaultValue);	
			} catch (Exception e) { return defaultValue; }
		}
		else
		{
			SharedPreferences prefs = context.getSharedPreferences(key, Activity.MODE_PRIVATE);
			if( prefs.contains(key) == true )
			{
				defaultValue = prefs.getString(key, defaultValue);
				removeKeyPrefs(prefs, key);
				
				putString(context, key, defaultValue, true);
			}	
			return defaultValue;
		}
	}
	
	//Array 가져오기
	public static ArrayList<String> getArrayList(Context context, String key)
	{
		ArrayList<String> array = new ArrayList<String>();
		SharedPreferences prefs = context.getSharedPreferences(key, Activity.MODE_PRIVATE);
		String jArrayString = prefs.getString(key, "dummy");
		
		if( jArrayString.matches("dummy")){
			return null;
		}else{
			try {
				JSONArray jArray = new JSONArray(jArrayString);
				for( int i = 0; i < jArray.length(); i++ ){
					array.add(jArray.getString(i));
				}
				return array;
			} catch (Exception e) {
				// TODO: handle exception
				return null;
			}
		}
	}
	
	//StringSet 가져오기
	public static Set<String> getStringSet(Context context, String key, Set<String> defaultValue){
		
		if( getPrefs(context).contains(key) == true )
		{
			try {
				return sPrefs.getStringSet(key, defaultValue);	
			} catch (Exception e) { return defaultValue; }
		}
		else
		{
			SharedPreferences prefs = context.getSharedPreferences(key, Activity.MODE_PRIVATE);
			if( prefs.contains(key) == true )
			{
				defaultValue = prefs.getStringSet(key, defaultValue);
				removeKeyPrefs(prefs, key);
				
				putStringSet(context, key, defaultValue, true);
			}	
			return defaultValue;
		}
		
	}
	
	public static String getCryptString(Context context, String key, String defaultValue)
	{
		String value = defaultValue;
		
		try 
		{
			String encryptedKey = getCrypto(context).toEncryptStr(key);
			if( getPrefs(context).contains(encryptedKey) == true )
			{
				String encryptedValue = sPrefs.getString(encryptedKey, null);
				if( encryptedValue != null && (encryptedValue = getCrypto(context).toDecryptStr(encryptedValue)) != null )
					value = encryptedValue;
				else
					value = defaultValue;
			}
			else
			{
				SharedPreferences prefs = context.getSharedPreferences(key, Activity.MODE_PRIVATE);
				if( prefs.contains(key) == true )
				{
					value = prefs.getString(key, defaultValue);
					removeKeyPrefs(prefs, key);
					
					putCryptString(context, key, value, false, true);
				}
				else if( sPrefs.contains(key) == true )
				{
					value = sPrefs.getString(key, defaultValue);
					sPrefsEditor.remove(key);
					
					putCryptString(context, key, value, false, true);
				}			
//				else 
//				{
//					value = defaultValue;
//				}
			}			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return value;		
	}
	
	public static void commit(Context context)
	{
		getPrefsEditor(context).commit();
	}
	
	private static void removeKeyPrefs(SharedPreferences prefs, String key)
	{
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(key);
		editor.commit();
	}
	
	public static void remove(Context context, String key)
	{
		try {
			if( getPrefs(context).contains(key) == true )
			{
				sPrefsEditor.remove(key);
				sPrefsEditor.commit();
			}			
		} 
		catch (Exception e)
		{
		}
	}
	
}
