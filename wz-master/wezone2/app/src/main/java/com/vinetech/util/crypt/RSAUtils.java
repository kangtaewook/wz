package com.vinetech.util.crypt;

import android.util.Log;
import com.vinetech.wezone.Define;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public final class RSAUtils 
{
	private final static String RSA_Java = "RSA";
	private final static String RSA_Android = "RSA/ECB/PKCS1Padding";
	private final static int RSA_ENCODING_KEY_SIZE = 64;
	
	public KeyPairGenerator kpg;
	public KeyPair keyPair;
	public RSAPublicKey publicKey;
	public RSAPrivateKey privateKey;
	
	/**
	 * @title 	Base64String형태의 publicKey 를 이용하여 PublicKey 객체를 생성함 (클라이언트용)
	 * @param 	String base64String
	 * @return 	PublicKey 객체
	 */
	public static PublicKey generetBase64PublicKey(String publicKeyBase64String)
	{

		PublicKey publicKey = null;
		try 
		{
			publicKey = KeyFactory.getInstance(RSA_Java).generatePublic(new X509EncodedKeySpec(com.vinetech.util.crypt.Base64.decode(publicKeyBase64String,com.vinetech.util.crypt.Base64.NO_OPTIONS)));
			//publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(publicKeyBase64String, Base64.DEFAULT)));
			//publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(publicKeyBase64String)));
			//publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(com.androidfuse.Library.Encoding.Base64.decode(publicKeyBase64String)));

		} 
		catch (Exception e)
		{
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, e.getMessage());
			}
		} 
		return publicKey;
	}
		
	
	/**
	 * @title 	Public Key로 암호화한 후 결과로 출력된 byte 배열을 Base64로 인코딩하여 String으로 변환하여 리턴함 (클라이언트용)
	 * @param 	text 암호화할 텍스트
	 * @param 	publicKey RSA 공개키
	 * @return 	Base64로 인코딩된 암호화 문자열
	 */

	
	public static String encrypt(String text, PublicKey publicKey)
	{
		byte[] bytes = text.getBytes();
		String encryptedText = null;
		
		try 
		{
			Cipher cipher = Cipher.getInstance(RSA_Android);//RSA/NoPadding
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			
			//encryptedText = new String(Base64.encodeBase64(cipher.doFinal(bytes)));
			//encryptedText = new String(Base64.encode(cipher.doFinal(bytes), Base64.DEFAULT));
			
			byte[] encryptedData = cipher.doFinal(bytes);
			if( encryptedData != null )
				encryptedText = com.vinetech.util.crypt.Base64.encodeBytes(encryptedData);

			//encryptedText = com.androidfuse.Library.Encoding.Base64.encode(cipher.doFinal(bytes));
		} catch (NoSuchAlgorithmException e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, e.getMessage());
			}
		} catch (NoSuchPaddingException e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, e.getMessage());
			}
		} catch (InvalidKeyException e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, e.getMessage());
			}
		} catch (IllegalBlockSizeException e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, e.getMessage());
			}
		} catch (BadPaddingException e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, e.getMessage());
			}
		}
		
		return encryptedText;
	}	
	


	/** 2014.12.01 양민규 전달받음. 실패.ㅋ
	 * @title 	Public Key로 암호화한 후 결과로 출력된 byte 배열을 Base64로 인코딩하여 String으로 변환하여 리턴함 (클라이언트용)
	 * @param 	text 암호화할 텍스트
	 * @param 	publicKey RSA 공개키
	 * @return 	Base64로 인코딩된 암호화 문자열
	 */
	/*
	public static String encrypt(String text, PublicKey publicKey) {
		
		byte[] bytes = new BigInteger(text, 16).toByteArray();;
		String encryptedText = null;
		try {
			Cipher cipher = Cipher.getInstance(RSA_Android);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			int length = bytes.length;
			if(length > RSA_ENCODING_KEY_SIZE){
				ByteArrayInputStream bis = null;
				ByteArrayOutputStream bos = null;
				try{
					bis = new ByteArrayInputStream(bytes);
					bos = new ByteArrayOutputStream();
					int count = 0;
					byte[] buf = new byte[RSA_ENCODING_KEY_SIZE];
					while ((count = bis.read(buf))!= -1) {
						bos.write(cipher.doFinal(buf, 0, count));
					}
					bytes = bos.toByteArray();
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					if(bis!=null) try{bis.close();} catch(Exception e){};
					if(bos!=null) try{bos.close();} catch(Exception e){};
				}
			}
			
//			encryptedText = new String(Base64.encodeBase64(cipher.doFinal(bytes)));
			
			byte[] encryptedData = cipher.doFinal(bytes);
			if( encryptedData != null )
				encryptedText = com.androidfuse.Library.Crypt.Base64.encodeBytes(encryptedData);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();   
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
		return encryptedText;
	}
	*/
	
//	public KeyPair generateKeyPair() {
//		
//		try {
//			kpg = KeyPairGenerator.getInstance(RSA);
//			kpg.initialize(2048);
//			keyPair = kpg.generateKeyPair();
//		} catch (NoSuchAlgorithmException e) {
//			SDSLog.e(e);
//		}
//		return keyPair;
//	}
//	
//	/**
//	 * @title 	keyPair 값을 이용하여 PublicKey 객체를 생성함
//	 * @param 	Keypair
//	 * @return 	PublicKey 객체
//	 */
//	public PublicKey getPublicKey(KeyPair keyPair){
//		return  (PublicKey) keyPair.getPublic();
//	}
//	
//	/**
//	 * @title 	keyPair 값을 이용하여 PrivateKey 객체를 생성함
//	 * @param 	Keypair
//	 * @return 	PrivateKey 객체
//	 */
//	public PrivateKey getPrivateKey(KeyPair keyPair){
//		return (PrivateKey) keyPair.getPrivate();
//	}
//	
//	/**
//	 * @title 	keyPair 값을 이용하여 PublicKey 객체를 생성함 (서버용)
//	 * @param 	Keypair
//	 * @return 	String PublicKey 객체
//	 */
//	public String getPublicKeyBase64(KeyPair keyPair){
//		return	Base64.encodeBase64String(keyPair.getPublic().getEncoded());
//	}
//	/**
//	 * @title 	keyPair 값을 이용하여 PrivateKey 객체를 생성함 (서버용)
//	 * @param 	Keypair
//	 * @return 	String privateKey 객체
//	 */
//	public String getPrivateKeyBase64(KeyPair keyPair){
//		return	Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
//	}
	

//	/**
//	 * @title 	Base64String형태의 privateKey 를 이용하여 PrivateKey 객체를 생성함 (서버용)
//	 * @param 	String base64String
//	 * @return 	PublicKey 객체
//	 */
//	public static PrivateKey generetBase64PrivateKey(String privateKeyBase64String){
//
//		PrivateKey privateKey = null;
//		try 
//		{
//			//privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyBase64String)));
//			privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(privateKeyBase64String, Base64.DEFAULT)));
//		} 
//		catch (Exception e) 
//		{
//			SDSLog.e(e);
//		} 
//		return privateKey;
//	}
		
	public static String encrypt(String text, String publicKeyBase64String)
	{
		return encrypt(text, generetBase64PublicKey(publicKeyBase64String));
	}
	

	
//	/**
//	 * @title 	Base64로 인코딩된 문자열을 받아 decode 시킨 후 RSA 비밀키(Private Key)를 이용하여 암호화된 텍스트를 원문으로 복호화
//	 * @param 	encryptedBase64Text Base64로 인코딩된 암호화 문자열
//	 * @param 	privateKey RSA 비밀 키
//	 * @return 	복호화된 텍스트
//	 */
//	public static String decrypt(String encryptedBase64Text, PrivateKey privateKey) 
//	{
//		//byte[] bytes = Base64.decodeBase64(encryptedBase64Text.getBytes());
//		
//		byte[] bytes = Base64.decode(encryptedBase64Text.getBytes(), Base64.DEFAULT);
//		//byte[] bytes = com.androidfuse.Library.Crypt.Base64.encodeBytes(cipher.doFinal(bytes));
//		
//		String decryptedText = null;
//		try {
//			Cipher cipher = Cipher.getInstance(RSA_Android);
//			cipher.init(Cipher.DECRYPT_MODE, privateKey);
//			decryptedText = new String(cipher.doFinal(bytes));
//		} catch (NoSuchAlgorithmException e) {
//			SDSLog.e(e);
//		} catch (NoSuchPaddingException e) {
//			SDSLog.e(e);
//		} catch (InvalidKeyException e) {
//			SDSLog.e(e);
//		} catch (IllegalBlockSizeException e) {
//			SDSLog.e(e);
//		} catch (BadPaddingException e) {
//			SDSLog.e(e);
//		}
//		
//		return decryptedText;
//	}
//	
//	/**
//	 * @title 	RSA 공개키로부터 RSAPublicKeySpec 객체를 생성함
//	 * @param 	publicKey 공개키
//	 * @return 	RSAPublicKeySpec
//	 */
//	public RSAPublicKeySpec getRSAPublicKeySpec(PublicKey publicKey) {
//		RSAPublicKeySpec spec = null;
//		generateKeyPair();
//		try {
//			spec = KeyFactory.getInstance(RSA).getKeySpec(publicKey, RSAPublicKeySpec.class);
//		} catch (InvalidKeySpecException e) {
//			SDSLog.e(e);
//		} catch (NoSuchAlgorithmException e) {
//			SDSLog.e(e);
//		}
//		
//		return spec;
//	}
//	
//	/**
//	 * @title 	RSA 비밀키로부터 RSAPrivateKeySpec 객체를 생성함
//	 * @param 	privateKey 비밀키
//	 * @return 	RSAPrivateKeySpec
//	 */
//	public RSAPrivateKeySpec getRSAPrivateKeySpec(PrivateKey privateKey) {
//		RSAPrivateKeySpec spec = null;
//		
//		try {
//			spec = KeyFactory.getInstance(RSA).getKeySpec(privateKey, RSAPrivateKeySpec.class);
//		} catch (InvalidKeySpecException e) {
//			SDSLog.e(e);
//		} catch (NoSuchAlgorithmException e) {
//			SDSLog.e(e);
//		}
//		
//		return spec;
//	}
//	
//	/**
//	 * @title 	Moduls, Exponent 값을 이용하여 PublicKey 객체를 생성함
//	 * @param 	modulus RSA Public Key Modulus
//	 * @param 	exponent RSA Public Key exponent
//	 * @return 	PublicKey 객체
//	 */
//	public PublicKey getPublicKey(String modulus, String exponent) {
//		BigInteger modulus_ = new BigInteger(modulus);
//		BigInteger exponent_ = new BigInteger(exponent);
//		PublicKey publicKey = null;
//		
//		try {
//			publicKey = KeyFactory.getInstance(RSA).generatePublic(new RSAPublicKeySpec(modulus_, exponent_));
//		} catch (InvalidKeySpecException e) {
//			SDSLog.e(e);
//		} catch (NoSuchAlgorithmException e) {
//			SDSLog.e(e);
//		}
//		
//		return publicKey;
//	}
//	
//	/**
//	 * @title 	Modulus, Exponent 값을 이용하여 PrivateKey 객체를 생성함
//	 * @param 	modulus RSA private key Modulus
//	 * @param 	privateExponent RSA private key exponent
//	 * @return 	PrivateKey 객체
//	 */
//	public PrivateKey getPrivateKey(String modulus, String privateExponent) {
//		BigInteger modulus_ = new BigInteger(modulus);
//		BigInteger privateExponent_ = new BigInteger(privateExponent);
//		PrivateKey privateKey = null;
//		
//		try {
//			privateKey = KeyFactory.getInstance(RSA).generatePrivate(new RSAPrivateKeySpec(modulus_, privateExponent_));
//		} catch (InvalidKeySpecException e) {
//			SDSLog.e(e);
//		} catch (NoSuchAlgorithmException e) {
//			SDSLog.e(e);
//		}
//		
//		return privateKey;
//	}
//	
//	/**
//	 * @title 	JSON 데이터의 String publicKey를 PublicKey 객체로 치환
//	 * @param 	String publicKey
//	 * @return 	PrivateKey 객체
//	 */
//	public PublicKey getPublicKeyToJson(String publicKey){
//		Gson gson = new Gson();
//		PublicKey pubServerKey = gson.fromJson(publicKey, PublicKey.class);
//		return pubServerKey;
//	}
}
