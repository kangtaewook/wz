package com.vinetech.wezone.Gcm;

import android.app.Activity;
import android.os.AsyncTask;

public class GcmManager {
	
	public interface GcmManagerLinstener{
		void onFinish(String regiId);
		void onError();
	}
	
//	public static final 	String SENDER_ID = "140598696939";
	
	public static final String SENDER_ID = "338438030889";
	
	public static final		int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	public GcmManagerLinstener mListener;
	
	private String regiId;
	public String getRegiId() {
		return regiId;
	}

	public void setRegiId(String regiId) {
		this.regiId = regiId;
	}

	public Activity mActivity;

	public int error_code;

	public GcmManager(Activity activity) {
		// TODO Auto-generated constructor stub
		this.mActivity = activity;
		mListener = (GcmManagerLinstener) this.mActivity;
	}
	
	public void requestRegistId() {
		new AsyncTask() {

			@Override
			protected Object doInBackground(Object... params) {
				// TODO Auto-generated method stub
				String msg = "";
				
//				if (checkPlayServices()) {
//					GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mActivity);

					// GCM에 Registration 등록. 등록되어 있다면 기등록된 아이디를 가져옴
//					try {
//						regiId = gcm.register(SENDER_ID);
//
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				return msg;
			}
			
			protected void onPostExecute(Object result) {
//				if (error_code != ConnectionResult.SUCCESS) {
//					mListener.onError();
//				}else{
//					mListener.onFinish(regiId);
//				}
				mListener.onFinish(regiId);
			}

		}.execute(null, null, null);

	}
	
//	public boolean checkPlayServices() {
//		final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.mActivity);
//		error_code =  resultCode;
//		if (resultCode != ConnectionResult.SUCCESS) {
//			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//
//				Handler mHandler = new Handler(Looper.getMainLooper());
//				mHandler.postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//				     // 내용
//					GooglePlayServicesUtil.getErrorDialog(resultCode,
//							mActivity,PLAY_SERVICES_RESOLUTION_REQUEST).show();
//				}
//				}, 0);
//
//
//			}
//			return false;
//		}
//		return true;
//	}

}
