package com.vinetech.wezone.Login.google;


public interface GoogleNetListner {
	void onFinish(String token, String result);
	void onError(int errorCode, String message);
}
