package com.vinetech.wezone.Gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.Data.Data_PushData;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.ShareApplication;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private String message;

    private String pushValue;

    private Gson m_Gson = new Gson();

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (!extras.isEmpty()) {
            message = extras.getString("message");
            pushValue = extras.getString("value");

            if (Define.LOG_YN) {
                Log.d("SUMIN", "push=================================================");
                Log.d("SUMIN", message);
                Log.d("SUMIN", pushValue);
            }

//            if (pushValue != null && message != null) {
//
//                ShareApplication share = (ShareApplication) getApplication();
//
//                Gson gSon = new Gson();
//
//                Data_PushData data = gSon.fromJson(pushValue.trim(), Data_PushData.class);
//
//                //내부에선 인코딩해서 움직인다.
////                data.msg = UIControl.encodeBase64WithString(message);
//                //                data.gmt_date = LangtudyDateUtil.getCurrentGMTTime("yyyyMMddHHmm", 0);
//
//                Bitmap Img = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//                sendNoti(new Intent(), Img, data.sender_name, data.msg);
//            }
        }
    }

    public void sendNoti(Intent i, Bitmap bitmap, String title, String message) {
        if (WezoneUtil.hasJellyBean()) {
            sendNotification(i, bitmap, title, message);
        } else {
            sendNotificationWithJellyBean(i, bitmap, title, message);
        }
    }

    private void sendPopup(Intent i, String message, Data_PushData value) {
        // Release the wake lock provided by the WakefulBroadcastReceiver.

        GcmBroadcastReceiver.completeWakefulIntent(i);
        Intent mainIntent = new Intent(this, PopupActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.putExtra(Define.INTENTKEY_PUSH_MESSAGE, message);
        mainIntent.putExtra(Define.INTENTKEY_PUSH_VALUE, value);
        startActivity(mainIntent);
    }

    private void sendPopup(Intent i, Data_PushData pushData) {
        // Release the wake lock provided by the WakefulBroadcastReceiver.

        GcmBroadcastReceiver.completeWakefulIntent(i);
        Intent mainIntent = new Intent(this, PopupActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.putExtra(Define.INTENTKEY_PUSH_VALUE, pushData);
        startActivity(mainIntent);
    }

    private void sendNotification(Intent i, Bitmap bitmap, String title, String message) {

        mNotificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setLargeIcon(bitmap);
        mBuilder.setContentTitle(title);

//        String strTemp = UIControl.decodeBase64WithString(message);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message).setSummaryText("Wezone"));
        mBuilder.setContentText(message);

//        mBuilder.setNumber(share.getPushDataCount());
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setVibrate(new long[]{1000, 1000});
        mBuilder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNotificationWithJellyBean(Intent i, Bitmap bitmap, String title, String message) {

        mNotificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);


//        String strTemp = UIControl.decodeBase64WithString(message);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(bitmap)
                // 작은아이콘 세팅
                .setContentTitle(title)
                // 노티바에 표시될 타이틀
                .setContentText(message)
                // 노티바에 표시된 Description
                .setAutoCancel(true).setPriority(Notification.PRIORITY_MAX)
                // 클릭하게 되면 사라지도록...
                .setVibrate(new long[]{1000, 1000})
                // 노티가 등록될 때 진동 패턴 1초씩 두번.
                .setDefaults(
                        Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());

    }

}
