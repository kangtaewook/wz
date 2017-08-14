package com.vinetech.util.crypt;

import android.util.Log;
import com.vinetech.wezone.Define;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public final class AesCrypt 
{
	private static final String TRANSFORM = "AES/ECB/PKCS5Padding";
	
	private Cipher mEncryptStrCipher, mDecryptStrChipher;
	
	private Cipher mEncryptBytesCipher, mDecryptBytesChipher;
	
	private boolean sIsPrepared;
	
	
	private AesCrypt() {}

	public AesCrypt(String strKey)
	{
		prepare(strKey);
	}
	
	public boolean isPrepared() { return sIsPrepared; } 
	
	public void prepare(String strKey)
	{
		if( sIsPrepared == true ) return;
		
		try 
		{
			SecretKeySpec chatSecretKeySpec = new SecretKeySpec(strKey.getBytes(), "AES");
			
			mEncryptStrCipher = Cipher.getInstance(TRANSFORM);
			mEncryptStrCipher.init(Cipher.ENCRYPT_MODE, chatSecretKeySpec);

			mDecryptStrChipher = Cipher.getInstance(TRANSFORM);
			mDecryptStrChipher.init(Cipher.DECRYPT_MODE, chatSecretKeySpec);

			
			sIsPrepared = true;
		} 
		catch (Exception e)
		{
			if(Define.LOG_YN) {
				Log.e(Define.LOG_TAG, e.toString());
			}
		}
	}
	
	public void destroy()
	{
		if( sIsPrepared == true )
		{
			sIsPrepared = false;
			
			mEncryptStrCipher = mDecryptStrChipher = null;
			mEncryptBytesCipher = mDecryptBytesChipher = null;
		}
	}
	
	public String toEncryptStr(String plainText)
	{
		if( sIsPrepared == false ) return plainText;
		
		String result = null;
		if( plainText != null )
		{
			try 
			{
				byte[] encrypted = mEncryptStrCipher.doFinal(plainText.getBytes());
				if( encrypted != null && encrypted.length > 0 )
					result = asHex(encrypted);
			} 
			catch (Exception e)
			{
			}
		}
		
		
//		if( Define.DEBUGLOG_CRYPT_ENABLE == true )
//		{
//			SDSLog.d(SDSLog.TAGIDX_CRYPT,plainText);
//			SDSLog.d(SDSLog.TAGIDX_CRYPT,"ENC=>"+result);
//		}
		return result;
	}
	
	public String toDecryptStr(String cipherText)
	{
		if( sIsPrepared == false ) return cipherText;
		
		String result = null;
		if( cipherText != null )
		{
			try 
			{
				byte[] decrypted = mDecryptStrChipher.doFinal(fromString(cipherText));
				if( decrypted != null && decrypted.length > 0 )
					result = new String(decrypted);
			} 
			catch (Exception e)
			{
			}
		}
//		if( Define.DEBUGLOG_CRYPT_ENABLE == true )
//		{
//			SDSLog.d(SDSLog.TAGIDX_CRYPT,cipherText);
//			SDSLog.d(SDSLog.TAGIDX_CRYPT,"DEC=>"+result);
//		}
		return result;
	}

	public byte[] toEncryptBytes(byte[] source) 
	{
		if( sIsPrepared == true && source != null && source.length > 0 )
		{
			try 
			{
				byte[] encrypted = mEncryptBytesCipher.doFinal(source);
				if( encrypted != null && encrypted.length > 0 )
					return encrypted;
			} 
			catch (Exception e)
			{
			}
		}
		return null;
	}
	
	public byte[] toDecryptBytes(byte[] cipherBytes) 
	{
		if( sIsPrepared == true && cipherBytes != null && cipherBytes.length > 0 )
		{
			try 
			{
				byte[] decrypted = mDecryptBytesChipher.doFinal(cipherBytes);
				if( decrypted != null && decrypted.length > 0 )
					return decrypted;
			} 
			catch (Exception e)
			{
			}
		}
		return null;
	}	
	
	private String asHex(byte buf[])
	{
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;

		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}

	private byte[] fromString(String hex) {
		int len = hex.length();
		byte[] buf = new byte[((len + 1) / 2)];

		int i = 0, j = 0;
		if ((len % 2) == 1)
			buf[j++] = (byte) fromDigit(hex.charAt(i++));

		while (i < len) {
			buf[j++] = (byte) ((fromDigit(hex.charAt(i++)) << 4) | fromDigit(hex
					.charAt(i++)));
		}
		return buf;
	}

	private int fromDigit(char ch) {
		if (ch >= '0' && ch <= '9')
			return ch - '0';
		if (ch >= 'A' && ch <= 'F')
			return ch - 'A' + 10;
		if (ch >= 'a' && ch <= 'f')
			return ch - 'a' + 10;

		throw new IllegalArgumentException("invalid hex digit '" + ch + "'");
	}
	
//	public static void test(String source) 
//	{
//		Log.d("CKY", "source : ["+source+"]");
//		
//		String encStr = CryptUtil.toEncryptStr(source);
//		Log.d("CKY", "encStr : ["+encStr+"]");
//		
//		String decStr = CryptUtil.toDecryptStr(encStr);
//		Log.d("CKY", "decStr : ["+decStr+"]");
//	}	
}