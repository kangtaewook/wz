package com.vinetech.util.crypt;

import android.app.Activity;
import android.util.Log;

import com.vinetech.wezone.Define;

public final class AesCryptByUser 
{
	private static AesCrypt sCrypto;
	
	public static boolean isPrepared() { return sCrypto != null && sCrypto.isPrepared(); } 
	
	public static void prepare(Activity activity)
	{
		if( isPrepared() == true || Define.CRYPT_ENABLE == false ) return;
		
		String userCn = CryptPreferences.getCryptString(activity,Define.SHARE_KEY_UUID,null);

		if( userCn != null) 
			prepare(userCn);
	}
	
	public static void prepare(String userCn)
	{
		if( isPrepared() == true || Define.CRYPT_ENABLE == false ) return;
		
		try 
		{

			CryptHelper cryptHelper = CryptHelper.getInstance();
			
			final String chatKey = cryptHelper.toHashCryptKey(Math.abs(userCn.hashCode()), Math.abs(userCn.hashCode()));
		
//			final String chatKey2 = String.format("%09d%09d",Math.abs(userCn.hashCode()),Math.abs(deviceMacAddressHash)).substring(0,16);
//			final String imageKey2 = String.format("%09d%09d", Math.abs(deviceMacAddressHash),Math.abs(Build.MODEL.hashCode()+Build.PRODUCT.hashCode())).substring(0,16);
		
			
			sCrypto = new AesCrypt(( chatKey.length()>16 )?chatKey.substring(0,16):chatKey);
			//sCrypto = new AesCrypt(chatKey, imageKey); 
		} 
		catch (Exception e)
		{
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, e.getMessage());
			}
		}
	}
	
	public static void destroy()
	{
		if( isPrepared() == true )
		{
			sCrypto.destroy();
			sCrypto = null;
		}
	}
	
	public static String toEncryptStr(String plainText)
	{
		return ( isPrepared() == true )? sCrypto.toEncryptStr(plainText):plainText;
	}
	
	public static String toDecryptStr(String cipherText)
	{
		return ( isPrepared() == true )? sCrypto.toDecryptStr(cipherText):null;
	}

	public static byte[] toEncryptBytes(byte[] source) 
	{
		if( isPrepared() == true )
		{
			synchronized (sCrypto) 
			{
				return sCrypto.toEncryptBytes(source);	
			}
		}
		return null;
	}
	
	synchronized public static byte[] toDecryptBytes(byte[] cipherBytes) 
	{
		if( isPrepared() == true )
		{
			synchronized (sCrypto) 
			{
				return sCrypto.toDecryptBytes(cipherBytes);	
			}
		}
		return null;
	}	
}