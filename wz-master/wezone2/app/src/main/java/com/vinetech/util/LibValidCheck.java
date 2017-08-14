package com.vinetech.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LibValidCheck {

	public static boolean isValidEmail(String email) {
		Pattern p = Pattern.compile("^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$");
		Matcher m = p.matcher(email);
		return m.matches();
	}
	
	public static boolean isValidCellPhone(String phoneNumber){
		Pattern p = Pattern.compile("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$");
		Matcher m = p.matcher(phoneNumber);
		return m.matches();
	}
	
	public static boolean isValidPhone(String phoneNumber){
		Pattern p = Pattern.compile("^\\d{2,3}-\\d{3,4}-\\d{4}$");
		Matcher m = p.matcher(phoneNumber);
		return m.matches();
	}
	
	public static boolean isValidInteger(String number){
		Pattern p = Pattern.compile("^[0-9]*$");
		Matcher m = p.matcher(number);
		return m.matches();
	}
	
	public static boolean isValidSocialNumber(String socialNumber){
		Pattern p = Pattern.compile("^\\d{7}-[1-4]\\d{6}");
		Matcher m = p.matcher(socialNumber);
		return m.matches();
	}
	
	public static boolean isValidIp(String ip){
		Pattern p = Pattern.compile("([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})");
		Matcher m = p.matcher(ip);
		return m.matches();
	}

}
