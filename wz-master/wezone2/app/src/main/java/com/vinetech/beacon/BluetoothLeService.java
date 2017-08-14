/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vinetech.beacon;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.vinetech.beacon.advertising.ADPayloadParser;
import com.vinetech.beacon.advertising.ADStructure;
import com.vinetech.beacon.advertising.VineBeacon;
import com.vinetech.util.WezoneUtil;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.Common.CameraActivity;
import com.vinetech.wezone.Common.CountDownActivity;
import com.vinetech.wezone.Data.Data_ActionItem;
import com.vinetech.wezone.Data.Data_ActionItemData;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_BeaconInfo;
import com.vinetech.wezone.Data.Data_BeaconInfoList;
import com.vinetech.wezone.Data.Data_Notice;
import com.vinetech.wezone.Data.Data_PushData;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Gcm.PopupActivity;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.SendPacket.Send_PostEmailNoti;
import com.vinetech.wezone.WezoneRestful;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.vinetech.wezone.Gcm.GcmIntentService.NOTIFICATION_ID;

/**
 * 비콘 스캔, 액션 등 전반적인 비콘의 액션을 관리한다.
 *
 * 동작 절차
 * 비콘 스캔 -> 바인비콘 회사 코드로 비콘 필터링 -> 바인 비콘 형태로 객체화(패킷 분석) -> 서버 조회 -> beacon_info_vars 의 데이터로 액션 수행
 *
 */
public class BluetoothLeService extends Service implements BeaconTimeOut{
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private boolean mScanning;
    private int cnt = 0; //롱버튼 액션 중복 발생 방지
    public final static String ACTION_FOUND_VINE_BEACON = "com.vinetech.wezone.ACTION_FOUND_VINE_BEACON";
    public final static String ACTION_IN_VINE_BEACON = "com.vinetech.wezone.ACTION_IN";
    public final static String ACTION_OUT_VINE_BEACON = "com.vinetech.wezone.ACTION_OUT";
    public final static String ACTION_CLICK_SHORT_PRESS = "com.vinetech.wezone.ACTION_CLICK_SHORT_PRESS";
    public final static String ACTION_CLICK_LONG_PRESS = "com.vinetech.wezone.ACTION_CLICK_LONG_PRESS";
    public final static String ACTION_CLICK_SHUTTER = "com.vinetech.wezone.SHUTTER";
    public final static String VINEBEACON_DATA = "VINEBEACON_DATA";

    public ArrayList<Data_BeaconInfo> mBeaconInfos;

    public ArrayList<VineBeacon> mVineBeacons;

    private BluetoothLeDeviceStore mBluetoothLeDeviceStore;

    public WezoneRestful wezoneRestful;

    private NotificationManager mNotificationManager;

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    scanResult(device,rssi,scanRecord);
                }
            };


    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            scanResult(result.getDevice(),result.getRssi(),result.getScanRecord().getBytes());
        }
    };

    public void scanResult(final BluetoothDevice device, int rssi, final byte[] scanRecord){
        if(mBeaconInfos !=null || mBeaconInfos.size() > 0){
            for(Data_BeaconInfo info : mBeaconInfos){
                if (scanRecord != null && scanRecord.length > 0) {
                    List<ADStructure> structures = ADPayloadParser.getInstance().parse(scanRecord);
                    for (ADStructure structure : structures) {

                        //바인테크 비콘 추출
                        if (structure instanceof VineBeacon) {
                            VineBeacon vineBeacon = (VineBeacon) structure;
//                                        vineBeacon.setDistance(calculateDistance(vineBeacon.getPower(),rssi));

                            if(info.uuid.equals(vineBeacon.getUUID().toString())){

                                if(Define.LOG_YN){
                                    Log.d(Define.LOG_TAG,vineBeacon.toString());
                                }

                                int queueCnt = CryptPreferences.getCryptInt(getApplicationContext(),Define.BEACON_QUEUE_CNT,Define.BEACON_QUEUE_CNT_DEFAULT);
                                String uuid = CryptPreferences.getCryptString(getApplicationContext(),Define.SHARE_KEY_UUID,null);

                                BluetoothLeDevice ble = new BluetoothLeDevice(device.getAddress(),rssi,scanRecord,queueCnt,vineBeacon);
                                broadcastUpdate(ACTION_FOUND_VINE_BEACON, ble);
//                                            ble.setBeaconTimeoutListener(BluetoothLeService.this);
                                mBluetoothLeDeviceStore.addDevice(uuid, ble);

                                //메인에서 바꿀수도 있으니,, 실시간으로 시간조정 해주자
                                int timeOutValue =  Define.BEACON_OUT_TIME_DEFAULT;
                                if(isForeground("com.vinetech.wezone")){
                                    timeOutValue = CryptPreferences.getCryptInt(getApplicationContext(), Define.BEACON_OUT_TIME, Define.BEACON_OUT_TIME_DEFAULT);
                                }else{
                                    timeOutValue = CryptPreferences.getCryptInt(getApplicationContext(), Define.BEACON_BACKGROUND_OUT_TIME, Define.BEACON_BACKGROUND_OUT_TIME_DEFAULT);
                                }
                                mBluetoothLeDeviceStore.setTimeoutValue(timeOutValue);

                                goAction();

                            }
                        }
                    }
                }
            }
        }
    }

    private void goAction(){
        for(BluetoothLeDevice device : mBluetoothLeDeviceStore.getDeviceList()){

            int intIn = CryptPreferences.getCryptInt(getApplicationContext(), Define.BEACON_IN_RANGE,Define.BEACON_IN_RANGE_DEFAULT) * -1;
            int intOut = CryptPreferences.getCryptInt(getApplicationContext(),Define.BEACON_OUT_RANGE,Define.BEACON_OUT_RANGE_DEFAULT) * -1;

            Data_Beacon beaconAction = device.getDataBeacon();

            //밑에서 검사 하지만.. 내비콘이 아니라도 비콘 정보가 내려오는 관계로.
            //액션 데이터가 없으면 무조건 액션을 안하도록 지정
            //beaconAction.beacon_info_vars != null
            if(beaconAction != null && "T".equals(beaconAction.push_flag) && beaconAction.beacon_info_vars != null) {

                if(WezoneUtil.isSameBeacon(device,beaconAction)){

                    if(!WezoneUtil.isEmptyStr(beaconAction.rssi_in)){
                        intIn = Integer.valueOf(beaconAction.rssi_in) * -1;
                    }

                    if(!WezoneUtil.isEmptyStr(beaconAction.rssi_out)){
                        intOut = Integer.valueOf(beaconAction.rssi_out) * -1;
                    }

                    int rssi = device.getRssi();

                    if(Define.LOG_YN) {
                        Log.d(Define.LOG_TAG, "rssi In [" + intIn + "]");
                        Log.d(Define.LOG_TAG, "rssi out[" + intOut + "]");
                        Log.d(Define.LOG_TAG, "평균 rssi[" + rssi + "]");
                        Log.d(Define.LOG_TAG, "마지막 rssi[" + device.getLastRssi() + "]");
                    }

                    //비콘 액션들
                    if(beaconAction.beacon_info_vars.beacon != null || beaconAction.beacon_info_vars.themezone != null){
                        //롱버튼
                        if(device.getVineBeacon().getButtonStatus1() == 1){
                            if(device.canAction()){
                                device.startAction();
                                if(beaconAction.beacon_info_vars.beacon.long_id != null) {
                                    if(isForeground("com.vinetech.wezone")){
                                        broadcastUpdate(ACTION_CLICK_LONG_PRESS, device);
                                    }else{
                                        actionWithItem(device, BluetoothLeDevice.BEACON_TYPE_LONG_PRESS, beaconAction.img_url, beaconAction.name, beaconAction.beacon_info_vars.beacon.long_id);
                                    }
                                }
                            }
                        }

                        //숏버튼
                        if(device.getVineBeacon().getButtonStatus2() == 1){
                            if(device.canAction()) {
                                device.startAction();
                                if (beaconAction.beacon_info_vars.beacon.short_id != null) {
                                    if (isForeground("com.vinetech.wezone")) {
                                        broadcastUpdate(ACTION_CLICK_SHORT_PRESS, device);
                                    } else {
                                        actionWithItem(device, BluetoothLeDevice.BEACON_TYPE_PRESS, beaconAction.img_url, beaconAction.name, beaconAction.beacon_info_vars.beacon.short_id);
                                    }
                                }
                            }
                        }

                        if(rssi > intIn){
//                        if(isForeground("com.vinetech.wezone")){
                            if(beaconAction.beacon_info_vars != null && "T".equals(beaconAction.push_flag)){
                                if(device.getBeaconType() == BluetoothLeDevice.BEACON_TYPE_OUT) {
                                    if(device.canAction()) {
                                        device.startAction();
                                        // 비콘
                                        if (beaconAction.beacon_info_vars.beacon != null && beaconAction.beacon_info_vars.beacon.near_id != null) {
                                            if (isForegroundWithClass("com.vinetech.wezone.Theme.ThemeActivity") || isForegroundWithClass("com.vinetech.wezone.Main.MainActivity")) {
                                                broadcastUpdate(ACTION_IN_VINE_BEACON, device);
                                            } else {
                                                actionWithItem(device, BluetoothLeDevice.BEACON_TYPE_IN, beaconAction.img_url, beaconAction.name, beaconAction.beacon_info_vars.beacon.near_id);
                                            }
                                        }

                                        if (beaconAction.beacon_info_vars.themezone != null && beaconAction.beacon_info_vars.themezone.theme_in != null) {
                                            if (isForegroundWithClass("com.vinetech.wezone.Theme.ThemeActivity") || isForegroundWithClass("com.vinetech.wezone.Main.MainActivity")) {
                                                broadcastUpdate(ACTION_IN_VINE_BEACON, device);
                                            } else {
                                                actionWithItem(device, BluetoothLeDevice.BEACON_TYPE_IN, beaconAction.img_url, beaconAction.name, beaconAction.beacon_info_vars.themezone.theme_in);
                                            }
                                        }
                                    }
                                    mBluetoothLeDeviceStore.updateBeaconType(device,BluetoothLeDevice.BEACON_TYPE_IN);
                                }
                            }

                            //비콘 나감
                        }else if(rssi < intOut){
//                        if(isForeground("com.vinetech.wezone")){
                            beaconOutAction(beaconAction,device);
                        }
                    }


                    //위존 액션들
                    if(beaconAction.beacon_info_vars.wezone != null){

                        String strSaveTime = CryptPreferences.getCryptString(getApplicationContext(),beaconAction.beacon_info_vars.wezone.zone_id,"0");
                        long saveTime = Long.valueOf(strSaveTime);
                        long currentTime = System.currentTimeMillis();

                        boolean overOneDay = (currentTime - saveTime) >= ((long) 1000 * 60 * 60 * 24 );
                        if(saveTime == 0 || overOneDay){  // 이 조건문 주석처리하면 가입대기 푸시메세지 계속 옵니다. 테스트하실 때 사용

                            actionWithItem(device, BluetoothLeDevice.BEACON_TYPE_IN, null, beaconAction.beacon_info_vars.wezone.zone_id, beaconAction.beacon_info_vars.wezone.zone_in);
                            CryptPreferences.putString(getApplicationContext(),beaconAction.beacon_info_vars.wezone.zone_id,String.valueOf(currentTime));
                        }
                    }

                }
            }else{

                //스캔화면
                //등록화면
                if (isForegroundWithClass("com.vinetech.wezone.Beacon.BeaconScanActivity") ||
                        isForegroundWithClass("com.vinetech.wezone.Beacon.BeaconManageActivity")) {
                    //롱버튼
//                    if (device.getVineBeacon().getButtonStatus1() == 1) {
                    if (device.getVineBeacon().getButtonStatus2() == 1) {
                        if (device.canAction()) {
                            device.startAction();
                            broadcastUpdate(ACTION_CLICK_SHORT_PRESS, device);
                        }
                    }
                }
            }
        }
    }

    public void beaconOutAction(Data_Beacon beaconAction, BluetoothLeDevice device){
        if(beaconAction.beacon_info_vars != null && "T".equals(beaconAction.push_flag)){
            if(device.getBeaconType() == BluetoothLeDevice.BEACON_TYPE_IN){

                if(device.canAction()) {
                    device.startAction();
                    if (beaconAction.beacon_info_vars.beacon != null && beaconAction.beacon_info_vars.beacon.far_id != null) {
                        if (isForegroundWithClass("com.vinetech.wezone.Theme.ThemeActivity") || isForegroundWithClass("com.vinetech.wezone.Main.MainActivity")) {
                            broadcastUpdate(ACTION_OUT_VINE_BEACON, device);
                        } else {
                            actionWithItem(device, BluetoothLeDevice.BEACON_TYPE_OUT, beaconAction.img_url, beaconAction.name, beaconAction.beacon_info_vars.beacon.far_id);
                        }
                    }

                    if (beaconAction.beacon_info_vars.themezone != null && beaconAction.beacon_info_vars.themezone.theme_out != null) {
                        if (isForegroundWithClass("com.vinetech.wezone.Theme.ThemeActivity") || isForegroundWithClass("com.vinetech.wezone.Main.MainActivity")) {
                            broadcastUpdate(ACTION_OUT_VINE_BEACON, device);
                        } else {
                            actionWithItem(device, BluetoothLeDevice.BEACON_TYPE_OUT, beaconAction.img_url, beaconAction.name, beaconAction.beacon_info_vars.themezone.theme_out);
                        }
                    }
                }
                mBluetoothLeDeviceStore.updateBeaconType(device,BluetoothLeDevice.BEACON_TYPE_OUT);
            }
        }
    }

    public void actionWithItem(BluetoothLeDevice ble, int actionType, String imgUrl,  String name, Data_ActionItem actionItem){

        if(actionItem != null){
            //사용안함

            String msg;
            if(actionType == BluetoothLeDevice.BEACON_TYPE_IN){
                msg = name + "이 나의 존에 접근했습니다";
            }else if(actionType == BluetoothLeDevice.BEACON_TYPE_OUT){
                msg = name + "이 나의 존에서 벗어났습니다";
            }else if(actionType == BluetoothLeDevice.BEACON_TYPE_PRESS){
                msg = name + "의 버튼이 눌러졌습니다";
            }else {
                msg = name + "의 롱 버튼이 눌러졌습니다";
            }


            if(Data_ActionItem.ID_NOT_USE.equals(actionItem.id)) {

            }else if(Data_ActionItem.ID_SOUND.equals(actionItem.id)){
                sendNotification(new Intent(),imgUrl, name, msg, null);

                Uri uri = Uri.parse(actionItem.data.get(0).value);
                RingtoneManager ringMan = new RingtoneManager(getApplicationContext());
                ringMan.stopPreviousRingtone();

                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
                AudioAttributes aa = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                ringtone.setAudioAttributes(aa);
                if(!ringtone.isPlaying()){
                    ringtone.setStreamType(AudioManager.STREAM_ALARM);
                    ringtone.play();
                }
            }else if(Data_ActionItem.ID_PUSH_MSG.equals(actionItem.id)){
                String strTemp = actionItem.data.get(0).value;
                strTemp = strTemp.replace(Define.BEACON_REPLACE_NAME,name);
                sendNotification(new Intent(), imgUrl,name, strTemp, null);
            }else if(Data_ActionItem.ID_IMAGE.equals(actionItem.id)){
                String strTemp = actionItem.data.get(0).value;
                sendNotification(new Intent(),imgUrl, name, msg, strTemp);
            }else if(Data_ActionItem.ID_APP.equals(actionItem.id)){
                String strTemp = actionItem.data.get(0).value;
                Intent intent = getPackageManager().getLaunchIntentForPackage(strTemp);
                startActivity(intent);
                sendNotification(new Intent(),imgUrl, name, msg, null);
            }else if(Data_ActionItem.ID_EMAIL.equals(actionItem.id)){
                sendNotification(new Intent(),imgUrl, name, msg, null);
                String strTemp = actionItem.data.get(0).value;
                String message = actionItem.data.get(1).value;
                sendEmail(ble.getDataBeacon().beacon_id,strTemp,message);

            }else if(Data_ActionItem.ID_CAMERA.equals(actionItem.id)){

                if (isForegroundWithClass("com.vinetech.wezone.Common.CameraActivity")) {
                    broadcastUpdate(ACTION_CLICK_SHUTTER,ble);
                }else{
                    sendNotification(new Intent(),imgUrl, name, msg, null);

                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                    try
                    {
                        pendingIntent.send();
                    }
                    catch(PendingIntent.CanceledException e)
                    {
                        e.printStackTrace();
                    }
                }
            }else if(Data_ActionItem.ID_SOS.equals(actionItem.id)) {

                sendNotification(new Intent(), imgUrl, name, msg, null);
                String message = actionItem.data.get(0).value;

                Intent intent = new Intent(getApplicationContext(), CountDownActivity.class);
                intent.putExtra(CountDownActivity.PHONE_NUMBERS,getNumberDatas(actionItem));
                intent.putExtra(CountDownActivity.MESSAGE,message);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                try
                {
                    pendingIntent.send();
                }
                catch(PendingIntent.CanceledException e)
                {
                    e.printStackTrace();
                }

            }

            //위존 공지
            else if(Data_ActionItem.ID_NOTIC.equals(actionItem.id)){

                String type = getItemWithKey(actionItem,"type");
                String icon = getItemWithKey(actionItem,"icon");
                String title = getItemWithKey(actionItem,"title");
                String content = getItemWithKey(actionItem,"content");
                String img_url = getItemWithKey(actionItem,"img_url");

                Data_PushData pushData = new Data_PushData();
                pushData.kind = "";
                //name에 아이디를 넘김
                pushData.item_id = name;
                //2는 가입
                if("2".equals(type)){
                    pushData.type = Data_Notice.MESSAGE_TYPE_WEZONE_REGIST;
                }else{
                    pushData.type = Data_Notice.MESSAGE_TYPE_WEZONE_NOTICE;
                }

                Intent mainIntent = new Intent(this, PopupActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.putExtra(Define.INTENTKEY_PUSH_MESSAGE, content);
                mainIntent.putExtra(Define.INTENTKEY_PUSH_VALUE, pushData);

                sendNotification(mainIntent, icon, title, content, null);
            }
        }
    }

    public String[] getNumberDatas(Data_ActionItem actionItem){

        ArrayList<String> tempArray = new ArrayList<>();

        int idxId = 0;
        for(int i=0; i< actionItem.data.size(); i++){
            String phoneNum = getItemWithKey(actionItem,"phone"+idxId);
            if(phoneNum != null){
                phoneNum = phoneNum.replaceAll("-", "");
                tempArray.add(phoneNum);
                idxId++;
            }
        }

        String[] simpleArray = new String[ tempArray.size() ];
        return tempArray.toArray(simpleArray);
    }

    public String getItemWithKey(Data_ActionItem actionItem, String key){
        String strTemp = null;

        for(Data_ActionItemData data : actionItem.data){
            if(key.equals(data.key)){
                strTemp = data.value;
            }
        }
        return strTemp;
    }


    private void sendNotification(Intent i, String iconUrl, String title, String message, final String bigImgeUrl) {

//        ShareApplication share = (ShareApplication) getApplication();

        mNotificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
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

        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,"MY TAG");
        wakeLock.acquire(); //스크린 키보드 켜기 ex) 알람,착신전화
        wakeLock.release(); // 배터리 소모 방지

        if(!WezoneUtil.isEmptyStr(iconUrl)){
            Glide.with(getApplicationContext()).
                    load(Define.BASE_URL + iconUrl).
                    asBitmap()
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mBuilder.setLargeIcon(resource);

                            if(!WezoneUtil.isEmptyStr(bigImgeUrl)){
                                Glide.with(getApplicationContext()).
                                        load(Define.BASE_URL + bigImgeUrl).
                                        asBitmap()
                                        .centerCrop()
                                        .into(new SimpleTarget<Bitmap>(){
                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                NotificationCompat.BigPictureStyle bps = new NotificationCompat.BigPictureStyle().bigPicture(resource);
                                                mBuilder.setStyle(bps);
                                                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                                            }
                                        });
                            }else{
                                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                            }
                        }
                    });
        }else{
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_wezone_app);
            mBuilder.setLargeIcon(largeIcon);

            if(!WezoneUtil.isEmptyStr(bigImgeUrl)){
                Glide.with(getApplicationContext()).
                        load(Define.BASE_URL + bigImgeUrl).
                        asBitmap()
                        .centerCrop()
                        .into(new SimpleTarget<Bitmap>(){
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                NotificationCompat.BigPictureStyle bps = new NotificationCompat.BigPictureStyle().bigPicture(resource);
                                mBuilder.setStyle(bps);
                                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                            }
                        });
            }else{
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }



    }

    public void sendEmail(String beaconId, String email, String content){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Define.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        WezoneRestful wezoneRestful = retrofit.create(WezoneRestful.class);

        Send_PostEmailNoti param = new Send_PostEmailNoti();
        param.beacon_id = beaconId;
        param.email = email;
        param.content = content;

        Call<Rev_Base> revBaseCall = wezoneRestful.postEmailNoti(param);
        revBaseCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {

            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });
    }


    private void broadcastUpdate(final String action, BluetoothLeDevice device) {
        final Intent intent = new Intent(action);
        if(device != null){
            intent.putExtra(VINEBEACON_DATA, device);
        }
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null && intent.getBooleanExtra("stop",false)){
            stopSelf();
        }else{
            mBeaconInfos = new ArrayList<>();
            mVineBeacons = new ArrayList<>();
            String strData = CryptPreferences.getCryptString(getApplicationContext(), Define.SHARE_KEY_BEACON_INFO, null);
            if(!WezoneUtil.isEmptyStr(strData)){
                Data_BeaconInfoList data = new Gson().fromJson(strData, Data_BeaconInfoList.class);
                mBeaconInfos.addAll(data.beaconInfo);
            }

            if(initialize()){

                if(mBluetoothLeDeviceStore == null){
                    mBluetoothLeDeviceStore = new BluetoothLeDeviceStore(new BeaconTimeOut() {
                        @Override
                        public void onFinishTimeout(BluetoothLeDevice ble) {
                            Data_Beacon beaconAction = ble.getDataBeacon();
                            if(beaconAction != null){
                                beaconOutAction(beaconAction,ble);
                            }
                        }
                    });
                }else{

                    //초기화 해줌으로써 갱신할수있도록 해준다.
                    mBluetoothLeDeviceStore.resetSearchData();
                }
                scanLeDevice(true);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mBluetoothAdapter != null) {
            scanLeDevice(false);
            mBluetoothAdapter = null;

            if(mBluetoothLeDeviceStore != null){
                mBluetoothLeDeviceStore.clear();
            }
        }
    }

    @Override
    public void onFinishTimeout(BluetoothLeDevice ble) {

    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
//        close();

        if(mBluetoothLeDeviceStore != null){
            mBluetoothLeDeviceStore.destroy();
        }
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }


        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }

        return true;
    }

    public void scanLeDevice(final boolean enable) {

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }

        if (enable) {
            mScanning = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
                List<ScanFilter> filters = new ArrayList<ScanFilter>();
                mBluetoothAdapter.getBluetoothLeScanner().startScan(filters,settings,scanCallback);
            }else{
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            mScanning = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
            }else {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }

    }

    public boolean isScanning(){
        return mScanning;
    }

    private boolean isThere(VineBeacon beacon){
        boolean isThere = false;
        if(mVineBeacons != null && mVineBeacons.size() > 0){
            for(VineBeacon b : mVineBeacons){
                if(b.getUUID().toString().equals(beacon.getUUID().toString())){
                    isThere = true;
                }
            }
        }else{
            isThere = false;
        }
        return isThere;
    }

    public boolean isForegroundWithClass(String classFullName){

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > runningTaskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getClassName().equals(classFullName);
    }


    public boolean isForeground(String myPackage){
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > runningTaskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

}
