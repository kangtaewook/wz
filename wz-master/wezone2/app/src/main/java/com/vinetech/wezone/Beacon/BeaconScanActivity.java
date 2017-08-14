package com.vinetech.wezone.Beacon;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vinetech.beacon.BluetoothLeDevice;
import com.vinetech.beacon.BluetoothLeDeviceStore;
import com.vinetech.beacon.BluetoothLeService;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_BeaconList;

import java.util.ArrayList;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 비콘 검색 화면
 *
 * 검색하고 등록화면으로 이동하는 역할
 *
 */

public class BeaconScanActivity extends BaseActivity {

    public LinearLayout linearlayout_beacon_controller;
    public ImageView imageview_beacon_loading;
    public TextView textview_beacon_cnt;

    private LinearLayout linearlayout_bottom_area;
    private  LinearLayout liniearlayout_btn_close;

//    private Timer mTimer;

    private BluetoothLeDeviceStore mBluetoothLeDeviceStore;

    private ListView listview;

    private ArrayList<BluetoothLeDevice> mListItem;


    private BeaconScanListAdapter mBeaconScanListAdapter;

    private ArrayList<Data_Beacon> mBeaconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_scan);

        setHeaderView(R.drawable.btn_back_white,"WeCON 등록",0);

        mBluetoothLeDeviceStore = new BluetoothLeDeviceStore(null);

        linearlayout_beacon_controller = (LinearLayout) findViewById(R.id.linearlayout_beacon_controller);
        imageview_beacon_loading = (ImageView) findViewById(R.id.imageview_beacon_loading);
        textview_beacon_cnt = (TextView) findViewById(R.id.textview_beacon_cnt);

        linearlayout_bottom_area = (LinearLayout) findViewById(R.id.linearlayout_bottom_area);
//
        liniearlayout_btn_close = (LinearLayout) findViewById(R.id.liniearlayout_btn_close);
        liniearlayout_btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearlayout_bottom_area.setVisibility(View.GONE);
            }
        });

        listview = (ListView) findViewById(R.id.listview);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        imageview_beacon_loading.setAnimation(animation);

        mListItem = new ArrayList<>();
        mBeaconScanListAdapter = new BeaconScanListAdapter(BeaconScanActivity.this,mListItem);
        listview.setAdapter(mBeaconScanListAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                BluetoothLeDevice device = mListItem.get(position);
//
//                int min = -60;
//                int max = -30;
//
//                if (min <= device.getLastRssi() && device.getLastRssi() <= max) {
//                    BeaconManageActivity.startActivityWithRegist(BeaconScanActivity.this, device.getMacAddr());
//                } else {
//                    Toast.makeText(BeaconScanActivity.this, "비콘이 등록가능 범위 밖에 있습니다", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        mBeaconList = new ArrayList<>();
    }

    public void getBeaconList(){
//        showProgressPopup();
        Call<Rev_BeaconList> beaconListCall = wezoneRestful.getBeaconList();
        beaconListCall.enqueue(new Callback<Rev_BeaconList>() {
            @Override
            public void onResponse(Call<Rev_BeaconList> call, Response<Rev_BeaconList> response) {
                Rev_BeaconList bl = response.body();
                if(isNetSuccess(bl)){
                    mBeaconList.clear();
                    mBeaconList.addAll(bl.list);
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_BeaconList> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBeaconList();
        mBluetoothLeDeviceStore.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mTimer != null) {
//            mTimer.cancel();
//            mTimer = null;
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBluetoothLeDeviceStore != null){
            mBluetoothLeDeviceStore.destroy();
        }
    }

    @Override
    protected void resultBeaconScan(String action, BluetoothLeDevice device) {
        super.resultBeaconScan(action, device);

        mBluetoothLeDeviceStore.addDevice(getUuid(), device);

        if (BluetoothLeService.ACTION_FOUND_VINE_BEACON.equals(action)) {
            mListItem.clear();

            ArrayList<BluetoothLeDevice> tempArray = new ArrayList<>();
            for(BluetoothLeDevice addedDevice : mBluetoothLeDeviceStore.getDeviceList()) {

                //이미 등록 된 비콘이 아닌것만 보이도록
                if(addedDevice.isOtherBeacon() == false){
                    if ((mBeaconList == null || mBeaconList.size() == 0) && addedDevice.getVineBeacon().getGATTRegiStatus() == 0) {
                        tempArray.add(addedDevice);
                    } else {
                        for(Data_Beacon beacon : mBeaconList){
                            if(!WezoneUtil.isSameBeacon(addedDevice,beacon) && addedDevice.getVineBeacon().getGATTRegiStatus() == 0){
                                tempArray.add(addedDevice);
                            }
                        }
                    }
                }
            }

            //중복 제거
            HashSet hs = new HashSet(tempArray);
            mListItem.addAll(hs);

            int size = mListItem.size();
            textview_beacon_cnt.setText("WeCON " + size + "개 발견");
            mBeaconScanListAdapter.notifyDataSetChanged();

        }else if(BluetoothLeService.ACTION_IN_VINE_BEACON.equals(action)){

        }else if(BluetoothLeService.ACTION_OUT_VINE_BEACON.equals(action)){

        }else if(BluetoothLeService.ACTION_CLICK_SHORT_PRESS.equals(action)){

            if(device.getVineBeacon().getGATTRegiStatus() == 0 && device.getLastRssi() >= -55){
                //리스트에 있는지 판단
                for(BluetoothLeDevice bleDevice : mListItem){
                    if(bleDevice.getMacAddr().equals(device.getMacAddr())){
                        BeaconManageActivity.startActivityWithRegist(BeaconScanActivity.this, device);
                    }
                }
            }

        }else if(BluetoothLeService.ACTION_CLICK_LONG_PRESS.equals(action)) {

//            if(device.getVineBeacon().getGATTRegiStatus() == 0 && device.getLastRssi() >= -80){
//                //리스트에 있는지 판단
//                for(BluetoothLeDevice bleDevice : mListItem){
//                    if(bleDevice.getMacAddr().equals(device.getMacAddr())){
//                        BeaconManageActivity.startActivityWithRegist(BeaconScanActivity.this, device);
//                    }
//                }
//            }
        }
    }

}
