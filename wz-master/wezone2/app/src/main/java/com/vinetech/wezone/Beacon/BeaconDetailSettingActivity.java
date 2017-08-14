package com.vinetech.wezone.Beacon;

import android.os.Bundle;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.R;

public class BeaconDetailSettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_detail_setting);

        String title = getResources().getString(R.string.setting_beacon_detail);
        setHeaderView(R.drawable.btn_back_white,title,0);
    }
}
