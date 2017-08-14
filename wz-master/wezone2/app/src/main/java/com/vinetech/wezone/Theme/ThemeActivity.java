package com.vinetech.wezone.Theme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.vinetech.beacon.BeaconTimeOut;
import com.vinetech.beacon.BluetoothLeDevice;
import com.vinetech.beacon.BluetoothLeDeviceStore;
import com.vinetech.beacon.BluetoothLeService;
import com.vinetech.ui.RandomImageView;
import com.vinetech.ui.RippleArcView;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Beacon.BeaconManageActivity;
import com.vinetech.wezone.Beacon.BeaconSelectActivity;
import com.vinetech.wezone.Beacon.BeaconSharedActivity;
import com.vinetech.wezone.Common.EditTextActivity;
import com.vinetech.wezone.Common.SelectNotificationActivity;
import com.vinetech.wezone.Data.Data_ActionItem;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Data.Data_Theme;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Main.MainBeaconListAdapter;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.SendPacket.Send_PutBase;
import com.vinetech.wezone.SendPacket.Send_PutDataWithValue;
import com.vinetech.wezone.SendPacket.Send_PutThemeZone;
import com.vinetech.wezone.SendPacket.Send_PutThemeZoneArrayData;
import com.vinetech.wezone.SendPacket.Send_PutThemeZoneMix;
import com.vinetech.wezone.SendPacket.Send_PutThemeZoneWithArray;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vinetech.wezone.SendPacket.Send_PutBase.FLAG_BEACON;
import static com.vinetech.wezone.Theme.ThemeDetailActivity.THEME;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 테마존
 *
 * 테마존 비콘 IN, OUT 관리
 *
 */

public class ThemeActivity extends BaseActivity {

    public static final String THEME_MODE = "theme_mode";
    public static final int THEME_MODE_NORMAL = 0;
    public static final int THEME_MODE_RUNNING = 1;


    public static final int STATE_ZONE_IN = 0;
    public static final int STATE_ZONE_OUT = 1;
    public static final int STATE_ZONE_NONE = 2;

    public static void startActivity(BaseActivity activity, Data_Theme themeInfo,int themeMode) {
        Intent intent = new Intent(activity, ThemeActivity.class);
        intent.putExtra(THEME,themeInfo);
        intent.putExtra(THEME_MODE,themeMode);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_THEME);
    }

    private Data_Theme mThemeInfo;

    private int themeMode;

    private ListView listview;
    private ThemezoneBeaconListAdapter mThemezoneBeaconListAdapter;

    private RippleArcView rippleArcView;


    private RandomImageView randomImageView;

    private TextView textview_theme_desc;

    private LinearLayout linearlayout_beacon_area;

    private TextView textview_title;

    private LinearLayout linearlayout_btn_add_beacon;

    private LinearLayout linearlayout_theme_type_1;
    private TextView textview_state_zone_in;
    private TextView textview_state_zone_out;

    private LinearLayout linearlayout_theme_type_2;

    private LinearLayout linearlayout_noresult_area;

    private boolean isScanning = false;
    private ImageButton mFab;


    private int mActionBarSize;
    private int mFlexibleSpaceImageHeight;

    private boolean mFabIsShown;

    private BluetoothLeDeviceStore mBluetoothLeDeviceStore;

    private ArrayList<Data_Beacon> mBeacons;
    private ArrayList<Data_Beacon> mRunningBeacons;


    private LinearLayout linearlayout_btn_start_theme;
    private LinearLayout linearlayout_btn_end_theme;

    public int mCurrentState = STATE_ZONE_IN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        mThemeInfo = (Data_Theme) getIntent().getSerializableExtra(THEME);
        themeMode = getIntent().getIntExtra(THEME_MODE,THEME_MODE_NORMAL);

        mBluetoothLeDeviceStore = new BluetoothLeDeviceStore(null);

        listview = (ListView) findViewById(R.id.listview);

        View paddingView = getLayoutInflater().inflate(R.layout.theme_header, null);
        paddingView.setClickable(true);
        listview.addHeaderView(paddingView);

        rippleArcView = (RippleArcView) findViewById(R.id.rippleArcView);

        textview_theme_desc = (TextView) findViewById(R.id.textview_theme_desc);
        textview_theme_desc.setText(mThemeInfo.content);

        if(WezoneUtil.isEmptyStr(mThemeInfo.bg_img_url) == false){

            String fullUrl = Define.BASE_URL + mThemeInfo.bg_img_url;

            Glide.with(ThemeActivity.this)
                    .load(fullUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new ViewTarget<View,GlideDrawable>(rippleArcView) {

                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            View mView = this.view;
                            // Set your resource on myView and/or start your animation here.
                            mView.setBackground(resource);
                        }
                    });

        }

        randomImageView = (RandomImageView) findViewById(R.id.randomImageView);

        linearlayout_beacon_area = (LinearLayout) findViewById(R.id.linearlayout_beacon_area);

        textview_title = (TextView) findViewById(R.id.textview_title);
        linearlayout_btn_add_beacon = (LinearLayout) findViewById(R.id.linearlayout_btn_add_beacon);
        linearlayout_btn_add_beacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeaconSelectActivity.startActivityWithData(ThemeActivity.this, mBeacons,BeaconSelectActivity.FROM_THEME,mThemeInfo.theme_id);
            }
        });

        linearlayout_theme_type_1 = (LinearLayout) findViewById(R.id.linearlayout_theme_type_1);
        textview_state_zone_in = (TextView) findViewById(R.id.textview_state_zone_in);
        textview_state_zone_out = (TextView) findViewById(R.id.textview_state_zone_out);

        linearlayout_theme_type_2 = (LinearLayout) findViewById(R.id.linearlayout_theme_type_2);

        linearlayout_noresult_area = (LinearLayout) findViewById(R.id.linearlayout_noresult_area);

        if("1".equals(mThemeInfo.theme_type)){
            linearlayout_theme_type_1.setVisibility(View.VISIBLE);

            if(mCurrentState == STATE_ZONE_IN){
                textview_state_zone_in.setTextColor(Color.parseColor("#80bc75"));
                textview_state_zone_out.setTextColor(Color.parseColor("#9e9e9e"));
                linearlayout_theme_type_1.setBackgroundResource(R.drawable.btn_zone_in);
            }else{
                textview_state_zone_out.setTextColor(Color.parseColor("#f76976"));
                textview_state_zone_in.setTextColor(Color.parseColor("#9e9e9e"));
                linearlayout_theme_type_1.setBackgroundResource(R.drawable.btn_zone_out);
            }

            linearlayout_theme_type_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mCurrentState == STATE_ZONE_IN){
                        mCurrentState = STATE_ZONE_OUT;
                    }else{
                        mCurrentState = STATE_ZONE_IN;
                    }

                    if(mCurrentState == STATE_ZONE_IN){
                        textview_state_zone_out.setTextColor(Color.parseColor("#9e9e9e"));
                        textview_state_zone_in.setTextColor(Color.parseColor("#80bc75"));
                        linearlayout_theme_type_1.setBackgroundResource(R.drawable.btn_zone_in);
                    }else{
                        textview_state_zone_in.setTextColor(Color.parseColor("#9e9e9e"));
                        textview_state_zone_out.setTextColor(Color.parseColor("#f76976"));
                        linearlayout_theme_type_1.setBackgroundResource(R.drawable.btn_zone_out);
                    }

                    if(mThemezoneBeaconListAdapter != null){
                        mThemezoneBeaconListAdapter.setChangeState(mCurrentState);
                        mThemezoneBeaconListAdapter.notifyDataSetChanged();
                    }
                }
            });

        }else{
            linearlayout_theme_type_2.setVisibility(View.VISIBLE);

            mCurrentState = STATE_ZONE_NONE;
        }

        mFab = (ImageButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleScanning();
            }
        });

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getActionBarSize();

        linearlayout_btn_start_theme = (LinearLayout) findViewById(R.id.linearlayout_btn_start_theme);
        linearlayout_btn_start_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mBeacons.size() > 0){
                    StartBeaconSelectActivity.startActivityWithData(ThemeActivity.this,mBeacons);
                }else{
                    Toast.makeText(ThemeActivity.this,"등록된 WeCON이 없습니다",Toast.LENGTH_SHORT).show();
                }

            }
        });

        linearlayout_btn_end_theme = (LinearLayout) findViewById(R.id.linearlayout_btn_end_theme);
        linearlayout_btn_end_theme.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                sendThemeZonePutWithValue("themezone_flag","F");
            }
        });

        mBeacons = new ArrayList<>();
        mRunningBeacons = new ArrayList<>();
        if(themeMode == THEME_MODE_RUNNING){
            if(mThemeInfo.start_beacon != null){
                mRunningBeacons.addAll(mThemeInfo.start_beacon);
            }
        }else{
            if(mThemeInfo.beacons != null){
                mBeacons.addAll(mThemeInfo.beacons);
            }
        }
        changeMode();
    }


    public void changeMode(){

        if(themeMode == THEME_MODE_RUNNING){
            linearlayout_btn_start_theme.setVisibility(View.GONE);
            linearlayout_btn_end_theme.setVisibility(View.VISIBLE);
            mThemezoneBeaconListAdapter = new ThemezoneBeaconListAdapter(ThemeActivity.this,mCurrentState,mRunningBeacons,themeMode);
//            linearlayout_btn_add_beacon();

            linearlayout_btn_add_beacon.setVisibility(View.INVISIBLE);
            textview_title.setText("테마가 실행중입니다");
            setHeaderView(0, mThemeInfo.name, 0);

            Intent serviceIntent = new Intent(this, BluetoothLeService.class);
            startService(serviceIntent);

        }else{
            linearlayout_btn_start_theme.setVisibility(View.VISIBLE);
            linearlayout_btn_end_theme.setVisibility(View.GONE);
            mThemezoneBeaconListAdapter = new ThemezoneBeaconListAdapter(ThemeActivity.this,mCurrentState,mBeacons,themeMode);
            mThemezoneBeaconListAdapter.setThemeMode(themeMode);
//            stopScanning();
            textview_title.setText("WeCON을 추가하고 시작해보세요!");
            linearlayout_btn_add_beacon.setVisibility(View.VISIBLE);

            setHeaderView(R.drawable.btn_back_white, mThemeInfo.name, 0);
        }

        if(mBeacons.size() > 0 || mRunningBeacons.size() > 0){
            linearlayout_noresult_area.setVisibility(View.GONE);
        }else{
            linearlayout_noresult_area.setVisibility(View.VISIBLE);
        }

        listview.setAdapter(mThemezoneBeaconListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int realPos = position - 1;

                if(themeMode == THEME_MODE_NORMAL){
                    Data_Beacon beacon = mBeacons.get(realPos);
                    if("N".equals(beacon.manage_type)){
                        BeaconSharedActivity.startActivity(ThemeActivity.this, beacon);
                    }else{
                        BeaconManageActivity.startActivityWithEdit(ThemeActivity.this, beacon, beacon.mac);
                    }
                }
            }
        });

        mThemezoneBeaconListAdapter.notifyDataSetChanged();

        mBluetoothLeDeviceStore.clear();

        toggleScanning();

    }

    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);
        ThemeZoneManageActivity.startActivityWithEdit(ThemeActivity.this, mThemeInfo);
    }

    @Override
    public void onBackPressed() {
        if(themeMode != THEME_MODE_RUNNING){
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTransparentHeaderView();
    }

    private void toggleScanning() {
        if (!isScanning) startScanning();
        else stopScanning();
    }
    //
    private void startScanning() {
        mFab.setBackgroundResource(R.drawable.ic_scan_pause);
        rippleArcView.startRippleAnimation();
        isScanning = true;
    }

    private void stopScanning(){
        mFab.setBackgroundResource(getCurrentThemeScanBtnId());
        rippleArcView.stopRippleAnimation();
        isScanning = false;
    }

    public int getCurrentThemeScanBtnId(){
        if(mCurrentTheme == Define.THEME_BLUE){
            return R.drawable.btn_scan_blue;
        }else if(mCurrentTheme == Define.THEME_RED){
            return R.drawable.btn_scan_red;
        }else{
            return R.drawable.btn_scan_yellow;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case Define.INTENT_RESULT_BEACONE:
                if(resultCode == RESULT_OK){
                    ArrayList<Data_Beacon> selectedItemList = (ArrayList<Data_Beacon>)data.getSerializableExtra(Define.INTENTKEY_BEACONE_VALUE);

                    mBeacons.clear();

                    mBeacons.addAll(selectedItemList);
                    sendThemeZonePutWithBeacons();

                    if(mBeacons.size() > 0){
                        linearlayout_noresult_area.setVisibility(View.GONE);
                    }else{
                        linearlayout_noresult_area.setVisibility(View.VISIBLE);
                    }

                    mThemezoneBeaconListAdapter.notifyDataSetChanged();
//                    if(mType == THEMEZONE_TYPE_EDIT){
//                        if(mSelectedTheme.beacons != null){
//                            mSelectedTheme.beacons.clear();
//                            mSelectedTheme.beacons.addAll(mBeacons);
//                        }
//                        sendThemeZonePutWithBeacons();
//                    }
//                    textview_beacon_cnt.setText("비콘 "+ mBeacons.size());
//                    mBeaconListAdapter.notifyDataSetChanged();
                }
                break;

            case Define.INTENT_RESULT_START_BEACONE:
                if(resultCode == RESULT_OK){
                    mRunningBeacons = (ArrayList<Data_Beacon>)data.getSerializableExtra(Define.INTENTKEY_BEACONE_VALUE);
                    sendStartThemeZone();
                }
                break;
        }
    }

    public void sendThemeZonePutWithValue(String flag, String value){

        Send_PutThemeZone param = new Send_PutThemeZone();
        param.theme_id = mThemeInfo.theme_id;

        Send_PutDataWithValue valueData = new Send_PutDataWithValue();
        valueData.flag = flag;
        valueData.val = value;

        param.themezone_info = new ArrayList<>();
        param.themezone_info.add(valueData);

        showProgressPopup();
        Call<Rev_Base> revBaseCall = wezoneRestful.putThemeZoneWithValue(param);
        revBaseCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revData = response.body();
                if (isNetSuccess(revData)) {
                    finish();
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
                hidePorgressPopup();
            }
        });

    }

    public void sendThemeZonePutWithBeacons(){

        ArrayList<HashMap<String,String>> beaconIds = new ArrayList<>();
        for(Data_Beacon beacon: mBeacons){
            HashMap<String,String> beaconMap = new HashMap<>();
            beaconMap.put("beacon_id",beacon.beacon_id);
            beaconIds.add(beaconMap);
        }

        Send_PutThemeZoneWithArray param = new Send_PutThemeZoneWithArray();
        param.theme_id = mThemeInfo.theme_id;

        Send_PutThemeZoneArrayData data = new Send_PutThemeZoneArrayData();
        data.flag = FLAG_BEACON;
        data.val = new ArrayList<>();
        data.val.addAll(beaconIds);

        param.themezone_info = new ArrayList<>();
        param.themezone_info.add(data);

        showProgressPopup();
        Call<Rev_Base> revBaseCall = wezoneRestful.putThemeZoneWithArray(param);
        revBaseCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revData = response.body();
                if(isNetSuccess(revData)){

                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }

    public void sendStartThemeZone(){
        ArrayList<HashMap<String,String>> beaconIds = new ArrayList<>();
        for(Data_Beacon beacon: mRunningBeacons){
            HashMap<String,String> beaconMap = new HashMap<>();
            beaconMap.put("beacon_id",beacon.beacon_id);
            beaconIds.add(beaconMap);
        }

        Send_PutThemeZoneMix param = new Send_PutThemeZoneMix();
        param.theme_id = mThemeInfo.theme_id;

        Send_PutDataWithValue prevData = new Send_PutDataWithValue();
        prevData.flag = "themezone_flag";
        prevData.val = "T";

        Send_PutThemeZoneArrayData data = new Send_PutThemeZoneArrayData();
        data.flag = "start_beacon";
        data.val = new ArrayList<>();
        data.val.addAll(beaconIds);

        param.themezone_info = new ArrayList<>();
        param.themezone_info.add(prevData);
        param.themezone_info.add(data);

        showProgressPopup();
        Call<Rev_Base> revBaseCall = wezoneRestful.putThemeZoneWithMix(param);
        revBaseCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revData = response.body();
                if(isNetSuccess(revData)){
                    themeMode = THEME_MODE_RUNNING;
                    changeMode();
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }


    @Override
    protected void resultBeaconScan(String action, BluetoothLeDevice device) {
        super.resultBeaconScan(action, device);

        if(themeMode == THEME_MODE_NORMAL){
            mBluetoothLeDeviceStore.addDevice(getUuid(),device);
            randomImageView.removeKeyWordAll();
            if(mBeacons != null) {
                for(BluetoothLeDevice ble : mBluetoothLeDeviceStore.getDeviceList()){
                    for (int i = 0; i < mBeacons.size(); i++) {
                        if (mBeacons.get(i).mac.equals(ble.getMacAddr())) {
                            double distance = BluetoothLeDevice.calculateDistance(ble.getVineBeacon().getPower(), ble.getLastRssi());
                            double b = Math.round(distance * 100d) / 100d;
                            mBeacons.get(i).rssi = ble.getLastRssi();
                            mBeacons.get(i).distance = b;
                            mBeacons.get(i).interval = ble.getVineBeacon().getInterval();
                            ble.setDataBeacon(mBeacons.get(i));
                            randomImageView.addKeyWord(ble);
                            mThemezoneBeaconListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }else{

            mBluetoothLeDeviceStore.addDevice(getUuid(),device);
            randomImageView.removeKeyWordAll();

            if(BluetoothLeService.ACTION_FOUND_VINE_BEACON.equals(action)){
                for(int i=0; i<mRunningBeacons.size(); i++) {
                    if (WezoneUtil.isSameBeacon(device, mRunningBeacons.get(i))) {
                        mRunningBeacons.get(i).beaconStatus = Data_Beacon.STATUE_IN;

                        randomImageView.addKeyWord(device);
                    }
                }
                mThemezoneBeaconListAdapter.notifyDataSetChanged();
            }else if(BluetoothLeService.ACTION_IN_VINE_BEACON.equals(action)){
                for(int i=0; i<mRunningBeacons.size(); i++){
                    if(WezoneUtil.isSameBeacon(device,mRunningBeacons.get(i))){
                        mRunningBeacons.get(i).beaconStatus = Data_Beacon.STATUE_IN;
                        String msg;
                        if("1".equals(mThemeInfo.theme_type)){
                            msg = mRunningBeacons.get(i).name + " 님이 승차하셨습니다";
                        }else{
                            msg = mRunningBeacons.get(i).name + " 님이 입장하셨습니다";
                        }
//                        sendNotification(new Intent(),mRunningBeacons.get(i).name,msg);
                        textview_title.setText(msg);
                    }
                }
                mThemezoneBeaconListAdapter.notifyDataSetChanged();
            }else if(BluetoothLeService.ACTION_OUT_VINE_BEACON.equals(action)){
                for(int i=0; i<mRunningBeacons.size(); i++){
                   if(WezoneUtil.isSameBeacon(device,mRunningBeacons.get(i))){
                       mRunningBeacons.get(i).beaconStatus = Data_Beacon.STATUE_OUT;

                       String msg;
                       if("1".equals(mThemeInfo.theme_type)){
                           msg = mRunningBeacons.get(i).name + " 님이 하차하셨습니다";
                       }else{
                           msg = mRunningBeacons.get(i).name + " 님이 퇴장하셨습니다";
                       }
//                       sendNotification(new Intent(),mRunningBeacons.get(i).name,msg);

                       textview_title.setText(msg);

                   }
               }
               mThemezoneBeaconListAdapter.notifyDataSetChanged();
            }
            if (!randomImageView.isShow) {
                randomImageView.show();
            }
        }

    }

}
