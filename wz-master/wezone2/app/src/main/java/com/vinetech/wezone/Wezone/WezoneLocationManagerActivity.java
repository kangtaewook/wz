package com.vinetech.wezone.Wezone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Beacon.BeaconSelectActivity;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import java.util.ArrayList;

public class WezoneLocationManagerActivity extends BaseActivity {

    public static final int WEZONE_TYPE_LOCATION = 0;
    public static final int DEFAULT_BEACON_SIZE = 0;
    public static final int WEZONE_MANAGER_TYPE = 1;

    public static final String TYPE = "type";
    public static final String BEACONSIZE = "Beaconsize";
    public static final String LOACTIONSELECT = "locationselect";
    public static final String LOACTIONDATA = "locationdata";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String MAPDISTANCE = "MapDistance";
    public static final String BEACONS = "BEACONS";


    private TextView activity_regist_wezone_distance;
    private LinearLayout wezone_linear1;
    private LinearLayout wezone_linear2;
    private int mType = WEZONE_TYPE_LOCATION;
    private TextView activity_location_choice_one;
    private String value;
    private String mMapDistance;
    private ArrayList<Data_Beacon> mBeacons;
    private ArrayList<Data_Beacon> selectedItemList;
    private int mvalue;
    private String distance;
    private double longitude;
    private double latitude;

    public static void setStartActivityLocation(BaseActivity activity, int mBeaconsize, double longitudem, double latitude, String mMapDistance, ArrayList<Data_Beacon> mBeacons) {
        Intent intent = new Intent(activity, WezoneLocationManagerActivity.class);
        intent.putExtra(BEACONSIZE, mBeaconsize);
        intent.putExtra(LONGITUDE, longitudem);
        intent.putExtra(LATITUDE, latitude);
        intent.putExtra(MAPDISTANCE, mMapDistance);
        intent.putExtra(TYPE, WEZONE_MANAGER_TYPE);
        intent.putExtra(BEACONS, mBeacons);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_WEZONE_LOCATION);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wezone_location_manager);
        activity_regist_wezone_distance = (TextView) findViewById(R.id.activity_regist_wezone_distance);

        mBeacons = (ArrayList<Data_Beacon>) getIntent().getSerializableExtra(BEACONS);

        wezone_linear1 = (LinearLayout) findViewById(R.id.wezone_linear1);
        wezone_linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMapDistance.equals("3.2")){
                    distance = "3200";
                    WezoneDistanceActivity.setWezoneDistanceActivity(WezoneLocationManagerActivity.this, distance, longitude, latitude);
                }else if(mMapDistance.equals("1.6")){
                    distance = "1600";
                    WezoneDistanceActivity.setWezoneDistanceActivity(WezoneLocationManagerActivity.this, distance, longitude, latitude);
                }else{
                    WezoneDistanceActivity.setWezoneDistanceActivity(WezoneLocationManagerActivity.this, mMapDistance, longitude, latitude);
                }

            }
        });

        wezone_linear2 = (LinearLayout) findViewById(R.id.wezone_linear2);
        wezone_linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeaconSelectActivity.startActivityWithData(WezoneLocationManagerActivity.this, mBeacons);
            }
        });

        //뒤로가기 버튼
        String titleW = getResources().getString(R.string.Location);
        if (mType == WEZONE_TYPE_LOCATION) {
            setHeaderView(R.drawable.btn_back_white, titleW, 0);
        } else {
            setHeaderView(R.drawable.btn_back_white, titleW, 0);
        }

        reLoadActivity();
    }

    public void reLoadActivity() {
        onActivityxml();

        mMapDistance = getIntent().getStringExtra(MAPDISTANCE);
        if ("1.6".equals(mMapDistance) || "3.2".equals(mMapDistance)) {
            activity_regist_wezone_distance.setText(mMapDistance + "km");
            activity_location_choice_one.setText("사용안함");
        } else if("200".equals(mMapDistance) || "400".equals(mMapDistance)){
            activity_regist_wezone_distance.setText(mMapDistance + "m");
            activity_location_choice_one.setText("사용안함");
        } else{
            activity_regist_wezone_distance.setText("사용안함");
        }

        if (mvalue == 0) {
            String distance = activity_regist_wezone_distance.getText().toString();
            onActivityxml();

            longitude = getIntent().getExtras().getDouble(LONGITUDE);
            latitude = getIntent().getExtras().getDouble(LATITUDE);

            if (mBeacons == null || mBeacons.size() == 0) {
                activity_location_choice_one.setText("사용안함");
            }else if(mBeacons.size() > 0){
                activity_location_choice_one.setText("WeCON 개수 : " + mBeacons.size());
            }else if(distance.equals("사용안함")){
                activity_location_choice_one.setText("WeCON 개수 : " + mBeacons.size());
            }
        }
    }

    @Override
    public void onClickRightBtn(View v) {

        backData();
    }

    // 헤더 뒤로가기 버튼
    @Override
    public void onClickLeftBtn(View v) {
        backData();
    }

    //하단 뒤로가기 버튼
    @Override
    public void onBackPressed() {
        finish();
    }

    public void backData() {
        onLocationSelect();
        String distance = activity_regist_wezone_distance.getText().toString().trim();
        String beacon = activity_location_choice_one.getText().toString().trim();

        if("사용안함".equals(distance) && "사용안함".equals(beacon)){
            Toast.makeText(m_Context,"GPS 또는 WeCON을 설정해주시기 바랍니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Define.INTENT_RESULT_BEACONE:
                if (resultCode == RESULT_OK) {
                    if (resultCode == RESULT_OK) {
                        selectedItemList = (ArrayList<Data_Beacon>) data.getSerializableExtra(Define.INTENTKEY_BEACONE_VALUE);

                        mBeacons = new ArrayList<>();
                        mBeacons.addAll(selectedItemList);

                        if (mBeacons != null && mBeacons.size() > 0) {
                            ArrayList<String> beaconIds = new ArrayList<>();
                            for (Data_Beacon beacon : mBeacons) {
                                beaconIds.add(beacon.beacon_id);
                            }
                            mBeacons.size();
                            activity_location_choice_one.setText("WeCON 개수 : " + mBeacons.size());
                            mvalue = mBeacons.size();
                        } else {
//                            activity_location_choice_one.setText("비콘 개수 : " + 0);
                            activity_location_choice_one.setText("사용안함");
                            mvalue = 1;
                        }
                    }
                    activity_regist_wezone_distance.setText("사용안함");
                }
                break;
            case Define.INTENT_RESULT_WEZONE_DISTANCE:
                if (resultCode == RESULT_OK) {
                    mMapDistance = data.getStringExtra(Define.INTENTKEY_DISTANCE_VALUE);

                    if(mMapDistance.equals("1.6") || mMapDistance.equals("3.2")){
                        activity_regist_wezone_distance.setText(mMapDistance + "km");
                    }else{
                        activity_regist_wezone_distance.setText(mMapDistance + "m");
                    }
                    activity_location_choice_one.setText("사용안함");
                    mvalue = 1;

                }
                break;
        }
    }

    public void onActivityxml() {
        activity_location_choice_one = (TextView) findViewById(R.id.activity_location_choice_one);
        activity_regist_wezone_distance = (TextView) findViewById(R.id.activity_regist_wezone_distance);
    }

    public void onLocationSelect() {
        String mDistance = null;
        onActivityxml();
        value = activity_location_choice_one.getText().toString().trim();
        activity_regist_wezone_distance = (TextView) findViewById(R.id.activity_regist_wezone_distance);
        Intent intent = new Intent();
        if (value.equals("사용안함")) {
            value = "GPS";
            mMapDistance = activity_regist_wezone_distance.getText().toString().trim();
            if (mMapDistance == null) {
                mMapDistance = "200";
            }else{
                if(mMapDistance.equals("1.6km") || mMapDistance.equals("3.2km")){
                    mDistance = mMapDistance.replaceAll("km","");
                }else{
                    mDistance = mMapDistance.replaceAll("m","");
                }
            }
            intent.putExtra(LOACTIONDATA, mDistance);
        }else {
            value = "WeCON";
            intent.putExtra(Define.INTENTKEY_BEACONE_VALUE, mBeacons);
        }
        intent.putExtra(LOACTIONSELECT, value);
        setResult(RESULT_OK, intent);
    }
}