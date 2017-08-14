package com.vinetech.wezone.Fcm; //작업중인 패키지 이름

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.Data.Data_Notice;
import com.vinetech.wezone.Data.Data_PushData;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Gcm.PopupActivity;
import com.vinetech.wezone.R;
import com.vinetech.wezone.ShareApplication;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    public static final String TAG = "FirebaseMsgService";
    public static final String COMMANT = "42";
    Bitmap bitmap;

    private static PowerManager.WakeLock sCpuWakeLock;
    protected ShareApplication m_Share;

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getData() != null){

            if (sCpuWakeLock != null) {
                return;
            }
            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            sCpuWakeLock = pm.newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.ON_AFTER_RELEASE, "wezone");

            sCpuWakeLock.acquire();


            if (sCpuWakeLock != null) {
                sCpuWakeLock.release();
                sCpuWakeLock = null;
            }


            Data_PushData pushData = new Data_PushData();
            pushData.sender_url = remoteMessage.getData().get("sender_url");
            pushData.id = remoteMessage.getData().get("id");
            pushData.zone_id = remoteMessage.getData().get("zone_id");
            pushData.kind = remoteMessage.getData().get("kind");
            pushData.sender_id = remoteMessage.getData().get("sender_id");
            pushData.sender_name = remoteMessage.getData().get("sender_name");
            pushData.receiver_id = remoteMessage.getData().get("receiver_id");
            pushData.content = remoteMessage.getData().get("content");
            pushData.item_id = remoteMessage.getData().get("item_id");
            pushData.type = remoteMessage.getData().get("type");

            Log.d(TAG, ": " + pushData.type);
            Log.d(TAG, ": " + pushData.sender_id);
            Log.d(TAG, ": " + pushData.sender_url);
            Log.d(TAG, ": " + pushData.sender_name);

            String title = remoteMessage.getData().get("title");
            String msg = remoteMessage.getData().get("body");

            if(Data_Notice.MESSAGE_TYPE_LOGOUT.equals(pushData.type)){
                m_Share = (ShareApplication) getApplication();
                if(isForegroundWithClass("com.vinetech.wezone.Login.LoginActivity")){
                    //자동로그인 제거
                    CryptPreferences.putCryptString(getApplicationContext(), Define.SHARE_KEY_PROVIDER_TYPE, null);
                    CryptPreferences.putCryptString(getApplicationContext(), Define.SHARE_KEY_UUID, null);

                }else{
                    if(isForeground("com.vinetech.wezone") || m_Share.isLogin()){
                        Intent mainIntent = new Intent(this, PopupActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mainIntent.putExtra(Define.INTENTKEY_PUSH_MESSAGE, msg);
                        mainIntent.putExtra(Define.INTENTKEY_PUSH_VALUE, pushData);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, mainIntent, 0);
                        try
                        {
                            pendingIntent.send();
                        }
                        catch(PendingIntent.CanceledException e)
                        {
                            e.printStackTrace();
                        }
                    }else{
                        //자동로그인 제거
                        CryptPreferences.putCryptString(getApplicationContext(), Define.SHARE_KEY_PROVIDER_TYPE, null);
                        CryptPreferences.putCryptString(getApplicationContext(), Define.SHARE_KEY_UUID, null);
                    }
                }

                sendNotificationWithOutMove(title, msg);
            }else{
                if(pushData.type.equals(COMMANT)){
                    if(pushData.sender_id.equals(pushData.receiver_id)){
                        Log.d("태욱","자기가 쓴 댓글에 대한 푸시오지 않게");
                    }else {
                        sendNotification(pushData, title, msg);
                    }
                }else{
                    sendNotification(pushData, title, msg);
                }
            }
        }
    }

    private void sendNotification(Data_PushData pushData, String title, String msg) {

        Intent mainIntent = new Intent(this, PopupActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.putExtra(Define.INTENTKEY_PUSH_MESSAGE, msg);
        mainIntent.putExtra(Define.INTENTKEY_PUSH_VALUE, pushData);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(contentIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    private void sendNotificationWithOutMove(String title, String msg) {


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(contentIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl){
        try{
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
       return bitmap;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public boolean isForeground(String myPackage){
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > runningTaskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

    public boolean isForegroundWithClass(String classFullName){

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > runningTaskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getClassName().equals(classFullName);
    }

}
