package com.vinetech.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.util.Log;

import com.vinetech.wezone.Define;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author �ڿ���
 * @class LibSystemManager
 */
public class LibSystemManager {
	
	////////////////////////////////////////////////////////////
	// Program of Values
	private Context m_Context				= null;
	
	private AudioManager m_AudioManager			= null;
	private PowerManager m_PowerManager			= null;
	private ClipboardManager m_Clipboard				= null;
	private PowerManager.WakeLock	m_WakeLock				= null;
	private ConnectivityManager m_ConnectManager		= null;
	private IntentFilter m_BatteryFilter			= null;
	private BroadcastReceiver m_BatteryReceiver		= null;

	private int						m_nBatteryRatio			= 100;
	
	private IntentFilter m_ExtendStorageFilter	= null;
	private BroadcastReceiver m_ExtendStorageReceiver	= null;
	private boolean					m_bisExtendStorage		= false;

//	private TelephonyManager m_TelephoneManager		= null;

	private LocationManager m_LocationManager		= null;
	
	private Location m_Location = null;
	
	private String m_PhoneNumber			= null;

	private Vibrator m_Vibrate =  null;


	public LibSystemManager(Context context) {
		if(context != null) {
			m_Context = context;

			//////////////////////////////////
			// ClipBoard Manager
			m_Clipboard = (ClipboardManager) m_Context.getSystemService(Context.CLIPBOARD_SERVICE);

			//////////////////////////////////
			// Device PhoneNumber
//			m_TelephoneManager = (TelephonyManager) m_Context.getSystemService(Context.TELEPHONY_SERVICE);
//			m_PhoneNumber = m_TelephoneManager.getLine1Number();
			m_ConnectManager = (ConnectivityManager) m_Context.getSystemService(Context.CONNECTIVITY_SERVICE);

			m_Vibrate = (Vibrator) m_Context.getSystemService(Context.VIBRATOR_SERVICE);
		}
	}
	
	public void setLocationManager(){
		m_LocationManager = (LocationManager)m_Context.getSystemService(Context.LOCATION_SERVICE);

//		List<String> providers = m_LocationManager.getProviders(true);  // 0: network, 1: passive, 2: GPS

		m_Location = m_LocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		if (m_Location == null) {
//
//			if(providers.size() > 1)
//				m_Location = m_LocationManager.getLastKnownLocation(providers.get(1));
//
//			if (m_Location == null) {
//				if(providers.size() > 2)
//					m_Location = m_LocationManager.getLastKnownLocation(providers.get(2));
//			}
//
//			if(m_Location == null){
//				if(m_LocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER) && m_LocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//					m_LocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//				}
//			}
//		}
	}
	
	
	public void removeLocation(){
//		m_LocationManager.removeUpdates(locationListener);
	}
	
	// Define a listener that responds to location updates
	LocationListener locationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
	      // Called when a new location is found by the network location provider.
	    	m_Location = location;
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	  };
	  
	
	/**
	 * @brief			휴대폰의 전화번호를 가져온다.
	 * @date 			2013. 6. 21.		
	 * @return			전화번호	
	 */
	public String getPhoneNumber(){
		return m_PhoneNumber;
	}
	
	/**
	 * @brief			Device Id �� ��´�.
	 * @date 			2013. 5. 3.		
	 * @return			Device ID	
	 */
	public String getDeviceId() {
		
		String deviceId = null;
		
		TelephonyManager tm = (TelephonyManager) m_Context.getSystemService(android.content.Context.TELEPHONY_SERVICE);
		deviceId = tm.getDeviceId();

		if(deviceId == null)
			deviceId = Secure.getString(m_Context.getContentResolver(), Secure.ANDROID_ID);

		return deviceId;
	}

	/**
	 * @brief			�ȵ���̵� OS ������ ��´�.
	 * @date 			2013. 5. 3.
	 * @return			OS ����
	 */
	public String getOSVersion(){
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * @brief			Mac Address �� ��´�.
	 * @date 			2013. 5. 3.
	 * @param context   Context
	 * @return			Mac Address
	 */
	public static String getWifiMacAddress(Context context)
	{
		WifiManager wifiman = (WifiManager)context.getSystemService(android.content.Context.WIFI_SERVICE);
		String strwifiMacAddress = wifiman.getConnectionInfo().getMacAddress();
		
		if(strwifiMacAddress == null && !wifiman.isWifiEnabled()){
			
			wifiman.setWifiEnabled(true);
			
			
			//최대 10초 대기 
			// 1초에 한번씩 10번 호출
			int count = 10;
			do{
				if(strwifiMacAddress == null){
					strwifiMacAddress = wifiman.getConnectionInfo().getMacAddress();					
				} else {
					break;					
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					if(Define.LOG_YN) {
						Log.d(Define.LOG_TAG, e.getMessage());
					}
				}
				count--;
			}while(count > 0);
			
			wifiman.setWifiEnabled(false);
		}
		
		return strwifiMacAddress;
	}
	
	/**
	 * @brief			App�� �����Ѵ�.				
	 * @date 			2013. 5. 3.						
	 */
	public void onDestroyProgram() {
		if(m_Context == null)
			return;

		ActivityManager actManager = (ActivityManager)m_Context.getSystemService(Context.ACTIVITY_SERVICE);
		actManager.restartPackage(m_Context.getPackageName());
		
		android.os.Process.killProcess(android.os.Process.myPid());
		actManager.killBackgroundProcesses(m_Context.getPackageName());
	}
	
	/**
	 * @brief			App�� �����Ѵ�.				
	 * @date 			2013. 5. 3.		
	 * @param activity	������ Activity
	 */
	public void onDestroyProgram(Activity activity) {
		if(m_Context == null)
			return;

		ActivityManager actManager = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
		actManager.restartPackage(activity.getPackageName());
		
		android.os.Process.killProcess(android.os.Process.myPid());
		actManager.killBackgroundProcesses(m_Context.getPackageName());
	}
	
	/**
	 * @brief			Destroy Program In Anroid				
	 * @date 			2013. 5. 3.		
	 * @param context	Context
	 * @param strPackageName Package Name
	 * @return			 �� ���� ����
	 */
	public boolean onTerminalInAnroid(Context context, String strPackageName) {
		ActivityManager actManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runAppList = actManager.getRunningAppProcesses();
	    for(int i = 0 ; i <runAppList.size() ; i ++){
			if(runAppList.get(i).processName.compareToIgnoreCase(strPackageName) == 0) {
				actManager.killBackgroundProcesses(runAppList.get(i).processName);
			}
	    }

	    return false;
	}
	
	/**
	 * @brief			���� ��ġ �Ǿ��ִ��� Ȯ���Ѵ�.				
	 * @date 			2013. 5. 3.		
	 * @param context   Context
	 * @param strPackageName Package Name
	 * @return				 �ۼ�ġ�Ǿ����� ���  True, ���� ��ġ �Ǿ� ���� ���� ��� False
	 */
	public boolean onCheckInstallInAnroid(Context context, String strPackageName) {
		List<PackageInfo> appinfo = context.getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
	    for(int i = 0 ; i <appinfo.size() ; i ++){
			if(appinfo.get(i).packageName.compareToIgnoreCase(strPackageName) == 0) {
				return true;
			}
	    }

	    return false;
	}
	
	/**
	 * @brief			����� ��带 äũ�Ѵ�.				
	 * @date 			2013. 5. 3.		
	 * @return			����� ��� ���� 	
	 */
	public static boolean onCheckAirPlaneMode(Context context) {
		return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	}
	
	/**
	 * @brief			����� ���� �����Ѵ�.	
	 * @date 			2013. 5. 3.		
	 * @param context   Context
	 * @param status    ����� ���� ������ ���°�	
	 */
	public static void setAirplaneMode(Context context, boolean status) {
		boolean isAirplaneModeOn = onCheckAirPlaneMode(context);
		
		if(isAirplaneModeOn && status)
			return;
		
		if(!isAirplaneModeOn && !status)
			return;
		
		if(isAirplaneModeOn && !status) {
			Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
			
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", 0);
            context.sendBroadcast(intent);
            return;
		}
        
		if(!isAirplaneModeOn && status) {
			Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 1);
            
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", 1);
            context.sendBroadcast(intent);
            return;
		}
    }
	
//	/**
//	 * @brief			���͸� ��뷮 üũ. (���ù��� ���)
//	 * @date 			2013. 5. 3.
//	 * @param context	Context
//	 */
//	public void onStartWatchingBattery(Context context) {
//		if(m_BatteryReceiver != null) {
//			onStopWatchingBattery(context);
//		}
//
//		m_BatteryFilter		 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//		m_BatteryReceiver	 = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
//					if(intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false) == false)
//						return;
//
//					int iPlugged	= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
//					if (iPlugged == BatteryManager.BATTERY_PLUGGED_AC || iPlugged == BatteryManager.BATTERY_PLUGGED_USB)
//						return;
//
//					int iScale		= intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
//					int iLevel		= intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//					m_nBatteryRatio	= iLevel * 100 / iScale;
//
//					if(m_nBatteryRatio < 10) {
//						onStopWatchingBattery(context);
//					}
//				}
//			}
//		};
//
//		context.registerReceiver(m_BatteryReceiver, m_BatteryFilter);
//	}
	
	/**
	 * @brief			���͸� ��뷮 üũ (�����۾����� onStartWatchingBattery�� ȣ���ؾ���.)				
	 * @date 			2013. 5. 3.		
	 * @return			���͸� ��뷮	
	 */
	public int getBatteryValue() {
		return m_nBatteryRatio;
	}
	
	/**
	 * @brief			���͸� ��뷮 üũ ���� (onStartWatchingBattery���� ����ߴ� ���ù� ����)
	 * @date 			2013. 5. 3.		
	 * @param context	Context			
	 */
//	public void onStopWatchingBattery(Context context) {
//		if(m_BatteryReceiver != null)
//			context.unregisterReceiver(m_BatteryReceiver);
//
//		m_BatteryFilter			= null;
//		m_BatteryReceiver		= null;
//	}
	
	/**
	 * @brief			�ܺ� Sdī�� mount ���� Ȯ�� (Receiver ���)
	 * @date 			2013. 5. 3.		
	 * @param context	Context			
	 */
//	public void onStartWatchingExtendStorage(Context context) {
//		if (m_ExtendStorageReceiver != null)
//			onStopWatchingExtendStorage(context);
//
//		String state = Environment.getExternalStorageState();
//		if (Environment.MEDIA_SHARED.equals(state)) {
//			m_bisExtendStorage = true;
//			return;
//		}
//
//		m_ExtendStorageFilter	 = new IntentFilter(Intent.ACTION_MEDIA_UNMOUNTED);
//		m_ExtendStorageFilter.addDataScheme("file");
//		m_ExtendStorageReceiver	 = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				m_bisExtendStorage = true;
//			}
//		};
//
//		context.registerReceiver(m_ExtendStorageReceiver, m_ExtendStorageFilter);
//	}
	
	/**
	 * @brief			�ܺ� Sdī�� mount ���� Ȯ�� (�����۾����� onStartWatchingExtendStorage�� ȣ���ؾ���.)				
	 * @date 			2013. 5. 3.		
	 * @return			�ܺ� Sd ī�� Mount ����	
	 */
	public boolean getExtenralStorageState() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_SHARED.equals(state);

	}
	
	public boolean isExtendStrorage() {
		return m_bisExtendStorage;
	}
	
	/**
	 * @brief			�ܺ� Sdī�� mount ���� Ȯ�� ���� (onStartWatchingExtendStorage ����ߴ� ���ù� ����)
	 * @date 			2013. 5. 3.		
	 * @param context	Context			
	 */
//	public void onStopWatchingExtendStorage(Context context) {
//		if (m_ExtendStorageReceiver != null)
//			context.unregisterReceiver(m_ExtendStorageReceiver);
//
//		m_ExtendStorageFilter		= null;
//		m_ExtendStorageReceiver		= null;
//	}

	/**
	 * @brief			�Ŀ� �޴��� �ʱ�ȭ				
	 * @date 			2013. 5. 3.		
	 * @return			�Ŀ� �Ŵ��� �ʱ�ȭ ����	
	 */
	public boolean onInitPowerManager() {
		if(m_Context == null)
			return false;
		
		m_PowerManager = (PowerManager)m_Context.getSystemService(Context.POWER_SERVICE);
		m_WakeLock = m_PowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "SYSTEM_TAG");
		return true;
	}
	
	/**
	 * @brief			�Ŀ� �޴��� ��ŸƮ				
	 * @date 			2013. 5. 3.		
	 * @return			�Ŀ��޴��� ��ŸƮ ����	
	 */
	public boolean onStartPowerManager() {
		if(m_WakeLock == null)
			return false;
		
		m_WakeLock.acquire();
		return true;
	}
	
	/**
	 * @brief			�Ŀ� �޴��� �޸� ����				
	 * @date 			2013. 5. 3.		
	 * @return			�Ŀ� �޴��� �޸� ���� ����	
	 */
	public boolean onDestroyPowerManager() {
		if(m_WakeLock == null)
			return false;
		
		if(m_WakeLock.isHeld())
			m_WakeLock.release();
		m_WakeLock 		= null;
		m_PowerManager	= null;
		return true;
	}
	
	/**
	 * @brief			����� �޴��� �ʱ�ȭ				
	 * @date 			2013. 5. 3.		
	 * @return			����� �޴��� �ʱ�ȭ ����
	 */
	public boolean onInitAudioManager() {
		m_AudioManager = (AudioManager)m_Context.getSystemService(Context.AUDIO_SERVICE);
		((Activity)m_Context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		return true;
	}
	
	/**
	 * @brief			����� �޴��� �ʱ�ȭ 				
	 * @date 			2013. 5. 3.		
	 * @param activity  activity
	 * @return			����� �޴��� �ʱ�ȭ ����	
	 */
	public boolean onInitAudioManager(Activity activity) {
		m_AudioManager = (AudioManager)activity.getSystemService(Context.AUDIO_SERVICE);
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		return true;
	}
	
	/**
	 * @brief			����� ���� ��				
	 * @date 			2013. 5. 3.		
	 * @return			����� �޴��� ����� ����	
	 */
	public boolean setAudioVolumeUp() {
		if(m_AudioManager == null)
			return false;
		
		m_AudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 1);
		return true;
	}
	
	/**
	 * @brief			����� �޴��� ���� �ٿ�				
	 * @date 			2013. 5. 3.		
	 * @return			����� �޴��� ���� �ٿ� ����	
	 */
	public boolean setAudioVolumeDown() {
		if(m_AudioManager == null)
			return false;
		
		m_AudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 1);
		return true;
	}
	
	/**
	 * @brief			����� �޴��� �޸� ����				
	 * @date 			2013. 5. 3.		
	 * @return			�޸� ���� ����	
	 */
	public boolean onDestroyAudioManager() {
		m_AudioManager = null;
		return true;
	}
	
	/**
	 * @brief			��Ʈ�� �޴��� �ʱ�ȭ				
	 * @date 			2013. 5. 3.		
	 * @return			��Ʈ�� �޴��� �ʱ�ȭ ����	
	 */
	public boolean onInitNetworkManager() {
		if(m_Context == null)
			return false;
		
		m_ConnectManager = (ConnectivityManager)m_Context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return true;
	}
	
	/**
	 * @brief			��Ʈ�� 3G ���� üũ				
	 * @date 			2013. 5. 3.		
	 * @return			3g ���� ����	
	 */
	public boolean getNetworkStatusOn3G() {
		if(m_ConnectManager == null)
			return false;
		
		NetworkInfo networkInfo	= m_ConnectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return networkInfo.isConnected();
	}
	
	/**
	 * @brief			��Ʈ�� Wifi ���� üũ				
	 * @date 			2013. 5. 3.		
	 * @return			Wifi ���� ����	
	 */
	public boolean getNetworkStatusOnWIFI() {
		if(m_ConnectManager == null)
			return false;
		
		NetworkInfo networkInfo	= m_ConnectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return networkInfo.isConnected();
	}
	
	/**
	 * @brief			��Ʈ�� Wifi ���� on				
	 * @date 			2013. 5. 3.		
	 * @return			Wifi ���� On ����	
	 */
	public boolean setNetworkStatusOnWIFI() {
		if(m_ConnectManager == null)
			return false;
		
		WifiManager wifiManager = (WifiManager)m_Context.getSystemService(Context.WIFI_SERVICE);
		if(!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		
		return true;
	}
	
	
	/**
	 * @brief			��Ʈ�� Wifi ���� Off				
	 * @date 			2013. 5. 3.		
	 * @return		 	Wifi Off ���� ����	
	 */
	public boolean setNetworkStatusOffWIFI() {
		if(m_ConnectManager == null)
			return false;
		
		WifiManager wifiManager = (WifiManager)m_Context.getSystemService(Context.WIFI_SERVICE);
		if(!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		}
		
		return true;
	}
	
	/**
	 * @brief			SD ī�峻�� ���� �뷮 üũ				
	 * @date 			2013. 5. 3.		
	 * @return			Sd ī�� ���� ���� �뷮	
	 */
	public long getFreeSpaceInSystem() {
		File path	= Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();

		return availableBlocks * blockSize;
	}
	
    /**
     * @brief			�� Install
     * @date 			2013. 5. 3.		
     * @param context   Context
     * @param apkFile   File ���� apk ���� ���
     */
    public static boolean InstallApp(Context context, File apkFile){
        Intent intent  = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        context.startActivity(intent);
        
        return true;
    }
    
    /**
     * @brief			��ġ �Ǿ� �ִ� ���� �����Ѵ�.				
     * @date 			2013. 5. 3.		
     * @param context   Contexrt
     * @param strPackageName Package Name
     */
    public static boolean DeleteApp(Context context, String strPackageName) {
    	Intent i = new Intent(Intent.ACTION_DELETE);
    	i.setData(Uri.parse("package:" + strPackageName));
    	context.startActivity(i);
    	
    	return true;
    }
    
    /**
     * @brief			Install �� �� ���� ����Ʈ�� ��´�.				
     * @date 			2013. 5. 3.		
     * @param context	Context
     * @return			Install �� �� ���� ����Ʈ	
     */
    public static List<PackageInfo> getAppInstalledInfo(Context context){
    	List<PackageInfo> appinfo = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
    	return appinfo;
    }
    
    /**
     * @brief			�ȵ���̵� ����̽� �̸��� ��´�.				
     * @date 			2013. 5. 3.		
     * @return			�ȵ���̵� ����̽� ��	
     */
    public String getDeviceName(){
        String model_name = null;
        model_name = Build.MODEL;
        return model_name;
    }
    
    @SuppressWarnings("deprecation")
	public boolean setClipboard(String string){
    	if(m_Clipboard != null){
    		if(string != null){
    			m_Clipboard.setText(string);
    		}
    	}
    	
    	return false;
    }
    
    /**
     * @brief			Ŭ�����忡 ����Ǿ� �ִ� String �� ��´�.				
     * @date 			2013. 5. 3.		
     * @return			Ŭ�����忡 ����Ǿ� �ִ� String	
     */
    public String getClipboard(){
    	if(m_Clipboard != null)
    	{
        	if(m_Clipboard.hasText()){
        		return m_Clipboard.getText().toString();
        	} 
        	else
        	{
        		return null;
        	}
    	} else {
    		return null;
    	}
    }
    
	/**
	 * @brief			�Է¹��� �Ķ���ͷ� ��ȭ�� �Ǵ�.
	 * @date 			2013. 5. 8.		
	 * @param activity  activity or Context
	 * @param num		��ȭ�� ��ȭ��ȣ				
	 */
	public static void call(Activity activity, String num) {
		try {
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + num));
			activity.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			
		}
	}
	
	/**
	 * @brief			�Է¹��� �Ķ���ͷ� ��ȭ�� �Ǵ�.
	 * @date 			2013. 5. 8.		
	 * @param context  activity or Context
	 * @param num		��ȭ�� ��ȭ��ȣ				
	 */
	public static void call(Context context, String num) {
		try {
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + num));
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			
		}
	}
	
	public String getAppVersion(Context context){
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
			
		} catch (NameNotFoundException e) {
			if(Define.LOG_YN) {
				Log.d(Define.LOG_TAG, e.getMessage());
			}
			return null;
		}
	}
	
	/**
	 * @Method Name : isRunningApplication
	 * @Autor : Park YK
	 * @Method 설명 : 현재 App 이 실행되고 있는 상태인지 확인한다.
	 * @param context
	 * @return  String[0] : Package Name   ex) com.app.demo
	 * 			String[1] : Class   Name   ex) com.app.demo.DemoActivity
	 * 			null : 앱 실행중 아님
	 */
	public static String[] isRunningApplication(Context context) {
		ActivityManager activityManager = null;
		List<RunningTaskInfo> runningTaskInfo = null;

		// ///////////////////////////////////////////////////
		// 현재 실행 되고 있는 Task 를 가져온다.
		activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		runningTaskInfo = activityManager.getRunningTasks(10);

		// ///////////////////////////////////////////////////
		// Context에 담겨져 있는 Package 이름을 가져온다.
		String packageName     = context.getPackageName();
		String topActivityInfo = null;

		for (Iterator<RunningTaskInfo> iterator = runningTaskInfo.iterator(); iterator.hasNext();) {
			RunningTaskInfo runningTask = iterator.next();
			topActivityInfo = runningTask.topActivity.getPackageName();
			if (topActivityInfo != null) {
				// ----------------------------------------------------------
				//
				// 현재 Task에 상주하고 있는 Activity의
				// Package명과 Context내에 있는 Package명을 비교한다.
				// 만약 비교해서 같다면 APP 실행중 아니라면 APP 실행중 아님
				//
				// 예제)
				// Task 상주 Package명 : com.app.demo.DemoActivity
				// context내의 Package명 : com.app.demo
				//
				// ----------------------------------------------------------
				if (topActivityInfo.equals(packageName)) {
					String[] arrPackageValue = new String[2];
					arrPackageValue[0]		 = packageName;
					arrPackageValue[1]       = runningTask.topActivity.getClassName();
					return arrPackageValue;
				}
			}
		}
		return null;
	}
	
	public static String getDefaultLanguage(){
		return Locale.getDefault().getISO3Language();
		
	}
	
	public double getLastLatitude(){
		if(m_Location == null){
			return 0;
		}
		return m_Location.getLatitude();
	}
	
	public double getLastLongitude(){
		if(m_Location == null){
			return 0;
		}
		return m_Location.getLongitude();
		
	}

	public void startVibrator(long milliseconds){
		if(m_Vibrate != null){
			if(m_Vibrate.hasVibrator()){
//				long[] pattern = {milliseconds,milliseconds};
				m_Vibrate.vibrate(milliseconds);
			}
		}
	}
	public void stopVibrator(){
		if(m_Vibrate != null){
			if(m_Vibrate.hasVibrator()){
				m_Vibrate.cancel();
				m_Vibrate = null;
			}
		}
	}

}
