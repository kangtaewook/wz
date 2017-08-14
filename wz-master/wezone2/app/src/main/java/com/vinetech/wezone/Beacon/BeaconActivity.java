package com.vinetech.wezone.Beacon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.beacon.BluetoothLeDevice;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BeaconActivity extends BaseActivity {

    public static final String BEACON_INFO = "beacon_info";

    public static void startActivity(BaseActivity activity, Data_Beacon beaconInfo) {
        Intent intent = new Intent(activity, BeaconActivity.class);
        intent.putExtra(BEACON_INFO,beaconInfo);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_BEACONE);
    }

    private Data_Beacon mBeaconInfo;

    private LinearLayout linearlayout_header_bg;

    private RelativeLayout relativelayout_profile;

    private ImageView imageview_profile;

    private TextView textview_distance;
    private TextView textview_name;
    private TextView textview_sn;

    private LinearLayout linearlayout_btn_short;
    private TextView textview_short_desc;

    private LinearLayout linearlayout_btn_long;
    private TextView textview_long_desc;

    private LinearLayout linearlayout_btn_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        linearlayout_header_bg = (LinearLayout) findViewById(R.id.linearlayout_header_bg);
        linearlayout_header_bg.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));

        relativelayout_profile = (RelativeLayout) findViewById(R.id.relativelayout_profile);
        imageview_profile = (ImageView) findViewById(R.id.imageview_profile);

        textview_distance = (TextView) findViewById(R.id.textview_distance);

        textview_name = (TextView) findViewById(R.id.textview_name);
        textview_sn = (TextView) findViewById(R.id.textview_sn);

        linearlayout_btn_short = (LinearLayout) findViewById(R.id.linearlayout_btn_short);
        textview_short_desc = (TextView) findViewById(R.id.textview_short_desc);

        linearlayout_btn_long = (LinearLayout) findViewById(R.id.linearlayout_btn_long);
        textview_long_desc = (TextView) findViewById(R.id.textview_long_desc);

        setHeaderView(R.drawable.btn_back_white,null,R.drawable.btn_more_white);

        mBeaconInfo = (Data_Beacon) getIntent().getSerializableExtra(BEACON_INFO);

        if(WezoneUtil.isEmptyStr(mBeaconInfo.img_url) == false){
            showImageFromRemote(mBeaconInfo.img_url,R.drawable.im_beacon_add,imageview_profile);
        }else{
            imageview_profile.setImageResource(R.drawable.im_beacon_add);
        }

        textview_name.setText(mBeaconInfo.name);
        textview_sn.setText(mBeaconInfo.beacon_id);
        textview_distance.setText("0 m");

        linearlayout_btn_reset = (LinearLayout) findViewById(R.id.linearlayout_btn_reset);
        linearlayout_btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressPopup();

                Call<Rev_Base> revBaseCall = wezoneRestful.deleteBeacon(mBeaconInfo.beacon_id);
                revBaseCall.enqueue(new Callback<Rev_Base>() {
                    @Override
                    public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                        Rev_Base revBase = response.body();
                        if(isNetSuccess(revBase)){
                            finish();
                        }
                        hidePorgressPopup();
                    }

                    @Override
                    public void onFailure(Call<Rev_Base> call, Throwable t) {
                        Toast.makeText(BeaconActivity.this,"WeCON 삭제 실패",Toast.LENGTH_SHORT).show();
                        hidePorgressPopup();
                    }
                });
            }
        });

    }


    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);
        BeaconManageActivity.startActivityWithEdit(BeaconActivity.this, mBeaconInfo, mBeaconInfo.mac);
    }

    @Override
    protected void resultBeaconScan(String action, BluetoothLeDevice device) {
        super.resultBeaconScan(action, device);
        if(WezoneUtil.isSameBeacon(device,mBeaconInfo)){
            double distance = BluetoothLeDevice.calculateDistance(device.getVineBeacon().getPower(),device.getRssi());
            double b = Math.round(distance * 100d) / 100d;
            textview_distance.setText(b + " m");
        }
    }
}
