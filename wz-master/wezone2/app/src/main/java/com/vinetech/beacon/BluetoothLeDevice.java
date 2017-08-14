package com.vinetech.beacon;

import android.os.CountDownTimer;

import com.vinetech.beacon.advertising.VineBeacon;
import com.vinetech.wezone.Data.Data_Beacon;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-03-14.
 *
 * 비콘 구조체
 *
 * 비콘의 상태 정보와 서버에서 조회한 비콘 정보를 관리함
 */

public class BluetoothLeDevice implements Serializable {

    public static final int BEACON_TYPE_IN = 0;
    public static final int BEACON_TYPE_OUT = 1;
    public static final int BEACON_TYPE_PRESS = 2;
    public static final int BEACON_TYPE_LONG_PRESS = 3;

    public static final int REACTION_TIMER = 1000 * 10;

    private String macAddr;
    private int lastRssi;
    private ArrayList<Integer> rssiList;

    private byte[] scanRecord;

    private boolean canAction = true;

    private boolean alreadySearch;

    private boolean isOtherBeacon = false;

    private Data_Beacon mBeaconInfo;

    private int mBeaconType = BEACON_TYPE_OUT;

    //회사명으로 걸러냄
    //다음에 바인 비콘으로 변경해야함
    private VineBeacon mVineBeacon;
    public VineBeacon getVineBeacon(){
        return mVineBeacon;
    }

    private int mQueueCnt;

    private Timestamp timestamp;

//    private CountDownTimer mCountDownTimer;

//    private BeaconTimeOut mBeaconTimeoutListner;
//    public void setBeaconTimeoutListener(BeaconTimeOut listener){
//        this.mBeaconTimeoutListner = listener;
//    }

    public BluetoothLeDevice(String macAddr, int rssi, byte[] scanRecord, int queueCnt, VineBeacon vineBeacon){
        this.macAddr = macAddr;

        if(this.rssiList == null)
        {
            this.rssiList = new ArrayList<>();
            this.rssiList.add(rssi);
            lastRssi = rssi;
        }

        this.scanRecord = scanRecord;

        this.mQueueCnt = queueCnt;

        mVineBeacon = vineBeacon;
        timestamp = new Timestamp(System.currentTimeMillis());

    }

    public void updateRssi(int rssi){
        if(this.rssiList.size() >= this.mQueueCnt){
            this.rssiList.remove(this.rssiList.size() - 1);
        }
        this.rssiList.add(0,rssi);
        lastRssi = rssi;

        timestamp = new Timestamp(System.currentTimeMillis());
    }

    public void updateVienBeacon(VineBeacon vineBeacon){
        mVineBeacon = vineBeacon;
    }

    public void setDataBeacon(Data_Beacon beacon){
        this.mBeaconInfo = beacon;
    }
    public Data_Beacon getDataBeacon(){
        return this.mBeaconInfo;
    }

    public String getMacAddr(){
        return this.macAddr;
    }

    public int getLastRssi(){
        return this.lastRssi;
    }
    public int getRssi(){
        int value = 0;
        int listSize = rssiList.size();
        for(int i=0; i<listSize; i++) {
            if(i < this.mQueueCnt){
                value += rssiList.get(i);
            }
        }
        return listSize < this.mQueueCnt ? value / listSize : value / this.mQueueCnt;
    }

    public byte[] getScanRecord(){
        return this.scanRecord;
    }

    public void startAction(){
        this.canAction = false;
        new CountDownTimer(REACTION_TIMER,1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                canAction = true;
            }
        }.start();
    }
    public boolean canAction(){
        return this.canAction;
    }

    public void alreadySearch(boolean isAlready){
        this.alreadySearch = isAlready;
    }

    public boolean alreadySearch(){
        return this.alreadySearch;
    }

    public void setOtherBeacon(boolean isOtherBeacon){
        this.isOtherBeacon = isOtherBeacon;
    }
    public boolean isOtherBeacon(){
        return this.isOtherBeacon;
    }

    public int getBeaconType(){
        return this.mBeaconType;
    }
    public void setBeaconType(int type){
        this.mBeaconType = type;
    }

    public Timestamp getTimestamp(){
        return this.timestamp;
    }

    public static double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }
}
