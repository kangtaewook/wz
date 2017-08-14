package com.vinetech.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;

public class LibCall {

	public static void SendToSms(Context c, String msg, String... phoneNum){
		
		if(c == null && (msg == null || phoneNum ==null))
			return;
		
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("smsto:");
		
		for (String num : phoneNum) {
			strBuffer.append(num+";");
		}
		
		Intent i = new Intent(Intent.ACTION_SENDTO);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setData(Uri.parse(strBuffer.toString()));
		i.putExtra("sms_body",msg);
        c.startActivity(i);
	}

	public static void SendSmsToOthers(PendingIntent intent, String[] numbers, String message){

		SmsManager smsManager = SmsManager.getDefault();
		for(String number : numbers){
			smsManager.sendTextMessage(number,null,message,intent,null);
		}

	}

	public static void CallPhone(Context c, String phoneNum){
		
		if(c == null && phoneNum == null)
			return;
		
		Intent intCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
		c.startActivity(intCall);
	}
	 
	public static void SendToMail(Context c, String title, String msg, String... mailAddress){
		
		if(c == null && mailAddress == null)
			return;
		
		Intent intent = new Intent(Intent.ACTION_SEND);
	    intent.putExtra(Intent.EXTRA_EMAIL, mailAddress);
	    intent.putExtra(Intent.EXTRA_SUBJECT, title);
	    intent.putExtra(Intent.EXTRA_TEXT, msg);
	    intent.setType("plain/text");
	    c.startActivity(intent);
	}
    
	public static void shareDataActivity(Context c, String title, String text){
		Intent i = new Intent();
		i.setAction(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_TEXT, text );
		c.startActivity(Intent.createChooser(i, title));
	}

	/**
	 *  반듯이 activity를 넣으세요, Context 안됨
	 * @param activity
	 * @param title
	 * @param requestCode
     */
	public static void startAudioSelectActivity(Activity activity, String title, int requestCode){
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.setType("audio/*");
		activity.startActivityForResult(Intent.createChooser(i, "Select Audio Source..."),requestCode);

	}
}
