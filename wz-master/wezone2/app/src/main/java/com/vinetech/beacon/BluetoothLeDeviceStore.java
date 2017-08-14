package com.vinetech.beacon;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.vinetech.beacon.advertising.VineBeacon;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.RevPacket.Rev_Beacon;
import com.vinetech.wezone.WezoneRestful;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by galuster3 on 2017-03-14.
 *
 * 검색 된 비콘을 저장해 두는 클래스
 *
 * 모든 비콘은 이 객체에 저장이 되어 관리가 되어지며,
 * 비콘 아웃에 대한 타임아웃 관리도 이 객체에서 한다.
 */

public class BluetoothLeDeviceStore {
    private static final BluetoothLeDeviceComparator DEFAULT_COMPARATOR = new BluetoothLeDeviceComparator();
    private final Map<String, BluetoothLeDevice> mDeviceMap;

    private BeaconTimeOut mBeaconTimeOut;

    private int mTimeOutValue = 10;

    private WezoneRestful wezoneRestful;

    public BluetoothLeDeviceStore(BeaconTimeOut timeoutListner) {
        mDeviceMap = new HashMap<>();

        if(timeoutListner != null){
            mHandler.sendEmptyMessage(0);
            mBeaconTimeOut = timeoutListner;
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Define.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        wezoneRestful = retrofit.create(WezoneRestful.class);
    }

    public void setTimeoutValue(int value){
        mTimeOutValue = value;
    }

    public void addDevice(String user_uuid, @NonNull final BluetoothLeDevice device) {

        if (mDeviceMap.containsKey(device.getMacAddr())) {
            mDeviceMap.get(device.getMacAddr()).updateRssi(device.getLastRssi());
            mDeviceMap.get(device.getMacAddr()).updateVienBeacon(device.getVineBeacon());
        } else {
            mDeviceMap.put(device.getMacAddr(), device);

            if(user_uuid != null && mDeviceMap.get(device.getMacAddr()).alreadySearch() == false){

                VineBeacon vb = device.getVineBeacon();
                Call<Rev_Beacon> revBeaconCall = wezoneRestful.getBeacon(user_uuid,device.getMacAddr(),vb.getUUID().toString(),String.valueOf(vb.getmUsableMajor()),String.valueOf(vb.getMinor()));
                revBeaconCall.enqueue(new Callback<Rev_Beacon>() {
                    @Override
                    public void onResponse(Call<Rev_Beacon> call, Response<Rev_Beacon> response) {
                        Rev_Beacon revBeacon = response.body();
                        if("200".equals(revBeacon.code)){
                            if(revBeacon.beacon_info != null && mDeviceMap.get(device.getMacAddr()) != null){
                                mDeviceMap.get(device.getMacAddr()).setDataBeacon(revBeacon.beacon_info);
                                mDeviceMap.get(device.getMacAddr()).alreadySearch(true);
                            }
                        }else if("403001".equals(revBeacon)) {
                            //이미 등록된 비콘
                            if(mDeviceMap.get(device.getMacAddr()) != null){
                                mDeviceMap.get(device.getMacAddr()).setOtherBeacon(true);
                                mDeviceMap.get(device.getMacAddr()).alreadySearch(true);
                            }
                        }else{
                            if(mDeviceMap.get(device.getMacAddr()) != null) {
                                mDeviceMap.get(device.getMacAddr()).alreadySearch(true);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Rev_Beacon> call, Throwable t) {
                        mDeviceMap.get(device.getMacAddr()).alreadySearch(true);
                    }
                });
            }
        }
    }

    public void updateBeaconType(@NonNull final BluetoothLeDevice device, int beacon_type) {
        if (mDeviceMap.containsKey(device.getMacAddr())) {
            mDeviceMap.get(device.getMacAddr()).setBeaconType(beacon_type);
        }
    }

    public void resetSearchData(){
        Iterator<String> keySetIterator = mDeviceMap.keySet().iterator();
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            mDeviceMap.get(key).alreadySearch(false);
        }
    }

    public void clear() {
        mDeviceMap.clear();
    }

    public int getSize() {
        return mDeviceMap.size();
    }

    @NonNull
    public List<BluetoothLeDevice> getDeviceList() {
        return getDeviceList(DEFAULT_COMPARATOR);
    }

    @NonNull
    public List<BluetoothLeDevice> getDeviceList(@NonNull Comparator<BluetoothLeDevice> comparator) {
        final List<BluetoothLeDevice> methodResult = new ArrayList<>(mDeviceMap.values());

        Collections.sort(methodResult, comparator);

        return methodResult;
    }

    private static class BluetoothLeDeviceComparator implements Comparator<BluetoothLeDevice> {

        @Override
        public int compare(final BluetoothLeDevice arg0, final BluetoothLeDevice arg1) {
            return arg0.getMacAddr().compareTo(arg1.getMacAddr());
        }
    }

    public void destroy(){
        if(mHandler != null) {
            mHandler.removeMessages(0);
            mHandler = null;
        }
    }

    //1초에 한번씩 데이터 검사한다.
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            //전체 데이터 관리
            //1초에 한번씩 시간 오버된 데이터 찾아서 제거.
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            for(Iterator<BluetoothLeDevice> it = getDeviceList().iterator() ; it.hasNext() ; )
            {
                BluetoothLeDevice ble = it.next();
                long second = ((currentTimestamp.getTime()-ble.getTimestamp().getTime())/ 1000 );
                if(second > mTimeOutValue){
                    if(mBeaconTimeOut != null){
                        mBeaconTimeOut.onFinishTimeout(ble);
                        mDeviceMap.remove(ble.getMacAddr());
                    }
                }
            }

            mHandler.sendEmptyMessageDelayed(0,1000);
        }
    };

}
