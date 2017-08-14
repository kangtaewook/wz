package com.vinetech.wezone.Beacon;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.beacon.BluetoothLeDevice;
import com.vinetech.beacon.BluetoothLeService;
import com.vinetech.beacon.BluetoothUpdateService;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Common.Activity_PhotoPicker_Folder;
import com.vinetech.wezone.Common.EditTextActivity;
import com.vinetech.wezone.Common.SelectNotificationActivity;
import com.vinetech.wezone.Data.Data_ActionItem;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_Beacon_info;
import com.vinetech.wezone.Data.Data_Beacon_info_Array;
import com.vinetech.wezone.Data.Data_LocalUserInfo;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Data.Data_UserInfo;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.Data.Send_PutBeacon;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Profile.UserSelectActivity;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_PostBeacon;
import com.vinetech.wezone.RevPacket.Rev_Upload;
import com.vinetech.wezone.RevPacket.Rev_ZoneMemberList;
import com.vinetech.wezone.SendPacket.Send_PostBeacon;
import com.vinetech.wezone.SendPacket.Send_PutBeaconArray;
import com.vinetech.wezone.SendPacket.Send_PutShare;
import com.vinetech.wezone.Wezone.GpsInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vinetech.wezone.Data.Data_ActionItem.getTitleText;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 비콘 관리 화면
 *
 * 비콘 등록 및 수정 업무
 *
 * 비콘 찾기, 인터벌, 액션 등의 업무를 수행
 *
 */

public class BeaconManageActivity extends BaseActivity {

    public static final int SELECTED_SHORT = 0;
    public static final int SELECTED_LONG = 1;
    public static final int SELECTED_NEAR = 2;
    public static final int SELECTED_FAR = 3;

    private int mSelectedBtn = SELECTED_NEAR;

    public static final String POPUP_MENU_TYPE_REGIST = "off";
    public static final String POPUP_MENU_TYPE_OFF = "off";
    public static final String POPUP_MENU_TYPE_DELETE = "delete";
    public static final String POPUP_MENU_TYPE_TIME_SECOND = "TIME_SECOND";

    public static final String BEACON_FLAG_SHORT_ID = "short_id";
    public static final String BEACON_FLAG_LONG_ID = "long_id";
    public static final String BEACON_FLAG_NEAR_ID = "near_id";
    public static final String BEACON_FLAG_FAR_ID = "far_id";
    public static final String BEACON_FLAG_NAME = "name";
    public static final String BEACON_FLAG_IMAGE_URL = "img_url";
    public static final String BEACON_FLAG_PUSH = "push_flag";
    public static final String BEACON_FLAG_UUID = "uuid";
    public static final String BEACON_FLAG_MODEL = "model";
    public static final String BEACON_FLAG_DEVICE_ID = "device_id";
    public static final String BEACON_FLAG_FIRMWARE = "firmware_ver";
    public static final String BEACON_FLAG_LAST_UPDATE = "last_update";


    public static final String BEACON_TYPE = "type";
    public static final String BEACON_INFO = "beacon_info";
    public static final String BEACON_ID = "beacon_id";
    public static final String BLE_INFO = "BLE_INFO";
    public static final String MAC_INFO = "mac_info";


    public static final int BEACON_TYPE_REGIST = 0;
    public static final int BEACON_TYPE_EDIT = 1;

    public static void startActivityWithRegist(BaseActivity activity, BluetoothLeDevice ble) {
        Intent intent = new Intent(activity, BeaconManageActivity.class);
        intent.putExtra(BEACON_TYPE, BEACON_TYPE_REGIST);
        intent.putExtra(BLE_INFO, ble);
        activity.moveActivity(intent);
    }

    public static void startActivityWithEdit(BaseActivity activity, Data_Beacon beaconInfo, String macAddr) {
        Intent intent = new Intent(activity, BeaconManageActivity.class);
        intent.putExtra(BEACON_TYPE, BEACON_TYPE_EDIT);
        intent.putExtra(BEACON_INFO, beaconInfo);
        intent.putExtra(MAC_INFO, macAddr);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_BEACONE);
    }

    private int mType;

    private BluetoothLeDevice mBluetoothLeDevice;

    private Data_Beacon mBeaconInfo;

    private LinearLayout linearlayout_header_bg;

    private RelativeLayout relativelayout_profile;

    private ImageView imageview_profile;

    private TextView textview_distance;
    private TextView textview_name;
    private EditText edittext_sn;
    private TextView textview_sn;

    private LinearLayout linearlayout_btn_short;
    private TextView textview_short_desc;

    private LinearLayout linearlayout_btn_long;
    private TextView textview_long_desc;

    private LinearLayout linearlayout_btn_push;

    private LinearLayout linearlayout_btn_set_01;
    private TextView textview_desc_01;
    private LinearLayout linearlayout_btn_set_02;
    private TextView textview_desc_02;

    private LinearLayout linearlayout_btn_01;
    private LinearLayout linearlayout_btn_02;

    private LinearLayout linearLayout_time_edit;
    private LinearLayout linearlayout_beacon_plus;
    private TextView textview_beacon_plus;
    private LinearLayout linearLayout_friend_share;

    private LinearLayout linearlayout_update_area;
    private LinearLayout linearlayout_btn_update;
    private TextView textview_beacon_time_second;
    private TextView textview_beacon_time_second_2;
    private TextView textview_last_update;
    private TextView textview_version;
    private ImageView imageview_update;
    private EditText textview_time_edit;

    private LinearLayout linearlayout_noti_area;

    private String mImagePath;
    private String mMenu_type;
    private String time_second;


    private Data_ActionItem mShortActionItem;
    private Data_ActionItem mLongActionItem;
    private Data_ActionItem mNearActionItem;
    private Data_ActionItem mFarActionItem;

    private ArrayList<Data_LocalUserInfo> mSOS_Userinfo;

    private String mPush_content;

    Dialog mDialog;

    private ArrayList<Data_UserInfo> mUserList;

    private ListView listview;
    private ArrayList<Data_Zone_Member> mMemberList;
    private BeaconZoneMemberListAdapter mBeaconZoneMemberListAdapter;

    private GpsInfo mGpsInfo;

    private double longitude;
    private double latitude;


    private BluetoothDevice mSelectedDevice;

    private BluetoothUpdateService.ServiceBinder mBinder;
    private boolean mBinded;

    public int mRegistrationCode;
    public int mSelectedInterval;

    public String[] intervalName = {"매우 빠름","빠름","보통","약간 느림"};
    public int[] intervalArray = {152,211,318,417,546,760,852,1022,1285,1500,2000,3000};

    public int ACTION_NONE = 0;
    public int ACTION_FIND_BEACON = 1;
    public int ACTION_INTERVAL_BEACON = 2;
    public int ACTION_OFF_BEACON = 3;
    public int ACTION_REGIST_BEACON = 4;
    public int mSelectedBeaconAction = ACTION_NONE;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            final BluetoothUpdateService.ServiceBinder binder = mBinder = (BluetoothUpdateService.ServiceBinder) service;
            final int state = binder.getState();
            switch (state) {
                case BluetoothUpdateService.STATE_DISCONNECTED:
                    binder.connect();
                    break;
                case BluetoothUpdateService.STATE_CONNECTED:
                    break;
            }
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_manage);

        mType = getIntent().getIntExtra(BEACON_TYPE, BEACON_TYPE_REGIST);

        mMemberList = new ArrayList<>();
        View header = getLayoutInflater().inflate(R.layout.beacon_manage_header, null, false);
        listview = (ListView) findViewById(R.id.listview);
        listview.addHeaderView(header);
        mBeaconInfo = (Data_Beacon) getIntent().getSerializableExtra(BEACON_INFO);
        mBeaconZoneMemberListAdapter = new BeaconZoneMemberListAdapter(BeaconManageActivity.this, mMemberList, mBeaconInfo);
        listview.setAdapter(mBeaconZoneMemberListAdapter);

        linearlayout_header_bg = (LinearLayout) header.findViewById(R.id.linearlayout_header_bg);
        linearlayout_header_bg.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));

        relativelayout_profile = (RelativeLayout) header.findViewById(R.id.relativelayout_profile);
        relativelayout_profile.setOnClickListener(mClickListener);

        imageview_profile = (ImageView) header.findViewById(R.id.imageview_profile);

        textview_distance = (TextView) header.findViewById(R.id.textview_distance);

        textview_beacon_time_second = (TextView) header.findViewById(R.id.textview_beacon_time_second);
        textview_beacon_time_second_2 = (TextView) header.findViewById(R.id.textview_beacon_time_second_2);

        textview_name = (TextView) header.findViewById(R.id.textview_name);
        textview_name.setOnClickListener(mClickListener);
        edittext_sn = (EditText) header.findViewById(R.id.edittext_sn);
        textview_sn = (TextView) header.findViewById(R.id.textview_sn);

        linearlayout_beacon_plus = (LinearLayout) header.findViewById(R.id.linearlayout_beacon_plus);
        linearlayout_beacon_plus.setOnClickListener(mClickListener);

        textview_beacon_plus = (TextView) header.findViewById(R.id.textview_beacon_plus);

        linearLayout_friend_share = (LinearLayout) header.findViewById(R.id.linearLayout_friend_share);
        linearLayout_friend_share.setOnClickListener(mClickListener);

        mUserList = new ArrayList<>();

        linearLayout_time_edit = (LinearLayout) header.findViewById(R.id.linearLayout_time_edit);
        linearLayout_time_edit.setOnClickListener(mClickListener);

        linearlayout_btn_short = (LinearLayout) header.findViewById(R.id.linearlayout_btn_short);
        linearlayout_btn_short.setOnClickListener(mClickListener);
        textview_short_desc = (TextView) header.findViewById(R.id.textview_short_desc);

        linearlayout_btn_long = (LinearLayout) header.findViewById(R.id.linearlayout_btn_long);
        linearlayout_btn_long.setOnClickListener(mClickListener);
        textview_long_desc = (TextView) header.findViewById(R.id.textview_long_desc);

        linearlayout_btn_push = (LinearLayout) header.findViewById(R.id.linearlayout_btn_push);
        linearlayout_btn_push.setSelected(true);
        linearlayout_btn_push.setOnClickListener(mClickListener);

        linearlayout_btn_set_01 = (LinearLayout) header.findViewById(R.id.linearlayout_btn_set_01);
        linearlayout_btn_set_01.setOnClickListener(mClickListener);
        textview_desc_01 = (TextView) header.findViewById(R.id.textview_desc_01);
        linearlayout_btn_set_02 = (LinearLayout) header.findViewById(R.id.linearlayout_btn_set_02);
        linearlayout_btn_set_02.setOnClickListener(mClickListener);
        textview_desc_02 = (TextView) header.findViewById(R.id.textview_desc_02);

        linearlayout_update_area = (LinearLayout) header.findViewById(R.id.linearlayout_update_area);
        linearlayout_btn_update = (LinearLayout) header.findViewById(R.id.linearlayout_btn_update);

        textview_last_update = (TextView) header.findViewById(R.id.textview_last_update);
        textview_version = (TextView) header.findViewById(R.id.textview_version);
        imageview_update = (ImageView) header.findViewById(R.id.imageview_update);

        linearlayout_noti_area = (LinearLayout) header.findViewById(R.id.linearlayout_noti_area);

        if (mType == BEACON_TYPE_REGIST) {
            setHeaderView(R.drawable.btn_back_white, null, R.drawable.btn_check);
//
//            mBeaconInfo = (Data_Beacon) getIntent().getSerializableExtra(BEACON_INFO);

//            edittext_sn.setText(mBeaconInfo.bluetoothAddress);

//            if (mBeaconInfo.distance != 0) {
//                double b = Math.round(mBeaconInfo.distance * 100d) / 100d;
//                textview_distance.setText(b + " m");
//            }

            mBluetoothLeDevice = (BluetoothLeDevice)getIntent().getSerializableExtra(BLE_INFO);

            linearlayout_update_area.setVisibility(View.GONE);
            textview_beacon_time_second.setVisibility(View.GONE);

            edittext_sn.setVisibility(View.VISIBLE);
            textview_sn.setVisibility(View.GONE);

            // 03 ~ FE 까지
            mRegistrationCode = new Random().nextInt(252) + 3;

        } else {
            setHeaderView(R.drawable.btn_back_white, null, R.drawable.btn_more_white);

            mBeaconInfo = (Data_Beacon) getIntent().getSerializableExtra(BEACON_INFO);

            if(WezoneUtil.isEmptyStr(mBeaconInfo.registration_code)){
                mRegistrationCode = 0;
            }else{

                mBeaconInfo.registration_code = mBeaconInfo.registration_code.replaceAll("\n","");
                mRegistrationCode = Integer.parseInt(mBeaconInfo.registration_code,16);
            }

            edittext_sn.setVisibility(View.GONE);
            textview_sn.setVisibility(View.VISIBLE);

            textview_sn.setText(mBeaconInfo.beacon_serial);

            if("T".equals(mBeaconInfo.push_flag)){
                linearlayout_btn_push.setSelected(true);
            }else{
                linearlayout_btn_push.setSelected(false);
            }

            linearlayout_beacon_plus.setVisibility(View.VISIBLE);

            textview_distance.setText("0 m");

            linearlayout_update_area.setVisibility(View.VISIBLE);

            setVersionUI(mBeaconInfo.last_update);

            reload();

            mGpsInfo = new GpsInfo(BeaconManageActivity.this);
            // GPS 사용유무 가져오기
            if (mGpsInfo.isGetLocation()) {
                longitude = mGpsInfo.getLongitude();
                latitude = mGpsInfo.getLatitude();

            } else {
                // GPS 를 사용할수 없으므로
//            mGpsInfo.showSettingsAlert();
            }

            getMemberList(0, 100);

            String interval = String.valueOf(mBeaconInfo.interval[0]) + String.valueOf(mBeaconInfo.interval[1]);
            if("00".equals(interval)){
                textview_beacon_time_second.setText(intervalName[0]);
            }else if("01".equals(interval)){
                textview_beacon_time_second.setText(intervalName[1]);
            }else if("10".equals(interval)){
                textview_beacon_time_second.setText(intervalName[2]);
            }else{
                textview_beacon_time_second.setText(intervalName[3]);
            }
        }
    }


    public void reload() {

        textview_name.setText(mBeaconInfo.name);

        if (WezoneUtil.isEmptyStr(mBeaconInfo.img_url) == false) {
            showImageFromRemote(mBeaconInfo.img_url, R.drawable.im_beacon_add, imageview_profile);
        } else {
            imageview_profile.setImageResource(R.drawable.im_beacon_add);
        }

        if (mBeaconInfo.beacon_info_vars != null) {
            if (mBeaconInfo.beacon_info_vars != null) {

                if (mBeaconInfo.beacon_info_vars.beacon != null) {
                    if (mBeaconInfo.beacon_info_vars.beacon.short_id != null) {
                        textview_short_desc.setText(getTitleText(mBeaconInfo.beacon_info_vars.beacon.short_id.id));
                    }

                    if (mBeaconInfo.beacon_info_vars.beacon.long_id != null) {
                        textview_long_desc.setText(getTitleText(mBeaconInfo.beacon_info_vars.beacon.long_id.id));
                    }
                    if (mBeaconInfo.beacon_info_vars.beacon.near_id != null) {
                        textview_desc_01.setText(getTitleText(mBeaconInfo.beacon_info_vars.beacon.near_id.id));
                    }

                    if (mBeaconInfo.beacon_info_vars.beacon.far_id != null) {
                        textview_desc_02.setText(getTitleText(mBeaconInfo.beacon_info_vars.beacon.far_id.id));
                    }
                }
            }
        }

//        textview_beacon_time_second.setText(mBeaconInfo.);

    }

    public void setVersionUI(String lastUpdate){
        float curVer = Float.valueOf(mBeaconInfo.firmware_ver);
        float newVer = Float.valueOf(mBeaconInfo.firmware_ver_new);

        if(curVer < newVer){
            imageview_update.setImageResource(R.drawable.btn_update);
            linearlayout_btn_update.setOnClickListener(mClickListener);
        }else{
            imageview_update.setImageResource(R.drawable.btn_chevron_right_black);
        }
        textview_version.setText(mBeaconInfo.firmware_ver + " 버전");

        if(WezoneUtil.isEmptyStr(lastUpdate)){
            textview_last_update.setVisibility(View.GONE);
        }else{
            textview_last_update.setVisibility(View.VISIBLE);
            textview_last_update.setText("마지막 업데이트 "+lastUpdate);
        }
    }

    public void getMemberList(int offset, int limit) {
        showProgressPopup();
        Call<Rev_ZoneMemberList> wezoneMemberList = wezoneRestful.getZoneMemeberList(mBeaconInfo.beacon_id, "C", String.valueOf(offset), String.valueOf(limit),String.valueOf(latitude),String.valueOf(longitude));
        wezoneMemberList.enqueue(new Callback<Rev_ZoneMemberList>() {
            @Override
            public void onResponse(Call<Rev_ZoneMemberList> call, Response<Rev_ZoneMemberList> response) {
                Rev_ZoneMemberList memberList = response.body();
                if (isNetSuccess(memberList)) {

                    for (Data_Zone_Member member : memberList.list) {
                        if (!member.uuid.equals(getUuid())) {
                            mMemberList.add(member);
                        }
                    }

                    if (mMemberList.size() > 0) {
                        linearlayout_noti_area.setVisibility(View.GONE);
                    } else {
                        linearlayout_noti_area.setVisibility(View.VISIBLE);
                    }
                    mBeaconZoneMemberListAdapter.notifyDataSetChanged();

                    hidePorgressPopup();
                }
            }

            @Override
            public void onFailure(Call<Rev_ZoneMemberList> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }

    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId) {
                case R.id.linearlayout_beacon_plus:{

                    if(linearlayout_beacon_plus.isSelected()){
                        linearlayout_beacon_plus.setSelected(false);
                        textview_beacon_plus.setText("WeCON 찾기");
                        mBinder.setLedStatus(false);
                        mBinder.disconnectAndClose();
                    }else{
                        linearlayout_beacon_plus.setSelected(true);
                        mSelectedBeaconAction = ACTION_FIND_BEACON;
                        connectDevice(true);
                    }
                }
                    break;

                case R.id.relativelayout_profile:
                    Activity_PhotoPicker_Folder.startFolderActivityForResult(BeaconManageActivity.this, 1);
                    break;

                case R.id.linearlayout_btn_short:
                    mSelectedBtn = SELECTED_SHORT;
                    if (mType == BEACON_TYPE_REGIST) {
                        if (mBeaconInfo == null) {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "짧게 누르기", null);
                        }
//
                        else if (mBeaconInfo.beacon_info_vars != null && mBeaconInfo.beacon_info_vars.beacon != null && mBeaconInfo.beacon_info_vars.beacon.short_id != null) {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "짧게 누르기", mBeaconInfo.beacon_info_vars.beacon.short_id);
                        } else {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "짧게 누르기", null);
                        }
                    } else {
                        if (mBeaconInfo == null) {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "짧게 누르기", null);
                        } else if (mBeaconInfo.beacon_info_vars != null && mBeaconInfo.beacon_info_vars.beacon != null && mBeaconInfo.beacon_info_vars.beacon.short_id != null) {
                            if(mShortActionItem != null){
                                mBeaconInfo.beacon_info_vars.beacon.short_id = mShortActionItem;
                            } else if(mBeaconInfo.beacon_info_vars.beacon.short_id.id == null){
                                mBeaconInfo.beacon_info_vars.beacon.short_id.id = Data_ActionItem.ID_CAMERA;
                            }
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "짧게 누르기", mBeaconInfo.beacon_info_vars.beacon.short_id);
                        } else {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "짧게 누르기", null);
                        }
                    }
                    break;

                case R.id.linearlayout_btn_long:
                    mSelectedBtn = SELECTED_LONG;
                    if (mType == BEACON_TYPE_REGIST) {
                        if (mBeaconInfo == null) {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "길게 누르기", null);
                        }
//
                        else if (mBeaconInfo.beacon_info_vars != null && mBeaconInfo.beacon_info_vars.beacon != null && mBeaconInfo.beacon_info_vars.beacon.long_id != null) {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "길게 누르기", mBeaconInfo.beacon_info_vars.beacon.long_id);
                        } else {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "길게 누르기", null);
                        }
                    } else {
                        if (mBeaconInfo == null) {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "길게 누르기", null);
                        } else if (mBeaconInfo.beacon_info_vars != null && mBeaconInfo.beacon_info_vars.beacon != null && mBeaconInfo.beacon_info_vars.beacon.long_id != null) {
                           if(mLongActionItem != null){
                               mBeaconInfo.beacon_info_vars.beacon.long_id = mLongActionItem;
                           } else if(mBeaconInfo.beacon_info_vars.beacon.long_id.id == null){
                                mBeaconInfo.beacon_info_vars.beacon.long_id.id = Data_ActionItem.ID_SOS;
                            }
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "길게 누르기", mBeaconInfo.beacon_info_vars.beacon.long_id);
                        } else {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "길게 누르기", null);
                        }
                    }
                    break;

                case R.id.linearlayout_btn_push: {
                    if (linearlayout_btn_push.isSelected()) {
                        linearlayout_btn_push.setSelected(false);

                        if (mType == BEACON_TYPE_EDIT) {
                            sendBeaconPut(BEACON_FLAG_PUSH,"F");
                        }

                    } else {
                        linearlayout_btn_push.setSelected(true);

                        if (mType == BEACON_TYPE_EDIT) {
                            sendBeaconPut(BEACON_FLAG_PUSH, "T");
                        }
                    }
                }
                break;

                case R.id.linearlayout_btn_set_01:
                    mSelectedBtn = SELECTED_NEAR;
                    if (mType == BEACON_TYPE_REGIST) {
                        SelectNotificationActivity.startActivity(BeaconManageActivity.this, "가까워짐", null);
                    } else {
                        if(mNearActionItem != null) {
                            mBeaconInfo.beacon_info_vars.beacon.near_id = mNearActionItem;
                        }
                        if (mBeaconInfo.beacon_info_vars != null && mBeaconInfo.beacon_info_vars.beacon != null && mBeaconInfo.beacon_info_vars.beacon.near_id != null) {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "가까워짐", mBeaconInfo.beacon_info_vars.beacon.near_id);
                        } else {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "가까워짐", null);
                        }
                    }

                    break;

                case R.id.linearlayout_btn_set_02:
                    mSelectedBtn = SELECTED_FAR;
                    if (mType == BEACON_TYPE_REGIST) {
                        SelectNotificationActivity.startActivity(BeaconManageActivity.this, "멀어짐", null);
                    } else {
                        if(mFarActionItem != null) {
                            mBeaconInfo.beacon_info_vars.beacon.far_id = mFarActionItem;
                        }
                        if (mBeaconInfo.beacon_info_vars != null && mBeaconInfo.beacon_info_vars.beacon != null && mBeaconInfo.beacon_info_vars.beacon.far_id != null) {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "멀어짐", mBeaconInfo.beacon_info_vars.beacon.far_id);
                        } else {
                            SelectNotificationActivity.startActivity(BeaconManageActivity.this, "멀어짐", null);
                        }
                    }
                    break;

                case R.id.linearlayout_btn_update:
                    BeaconDFUActivity.startActivity(BeaconManageActivity.this,mBeaconInfo);
                    break;

                case R.id.linearlayout_btn_01:
                    //캔슬
                    mDialog.cancel();
                  break;
                case R.id.linearlayout_btn_02:
                    if(mMenu_type.equals(POPUP_MENU_TYPE_OFF)){
                        // 비콘끔
                        mSelectedBeaconAction = ACTION_OFF_BEACON;
                        connectDevice(true);
                        mDialog.cancel();

                    }else if(mMenu_type.equals(POPUP_MENU_TYPE_TIME_SECOND)){
                        //확인
                        time_second_edit();
                    }else if(mMenu_type.equals(POPUP_MENU_TYPE_DELETE)){
                        // 비콘초기화
                        Beacon_delete();
                    }else{

                    }
                    break;
                case R.id.linearLayout_time_edit:
                    showIntervalPopup();
                    break;
                case R.id.linearLayout_friend_share:{
         //           UserSelectActivity.startActivity(BeaconManageActivity.this, mUserList);
                    UserSelectActivity.startActivity(BeaconManageActivity.this, mUserList, mMemberList);
                }
                    break;
                case R.id.textview_name:
                    mPush_content = textview_name.getText().toString();
                    EditTextActivity.startActivity(BeaconManageActivity.this, "이름",mPush_content);
                    break;
            }
        }
    };

    public void showIntervalPopup(){
        CharSequence info[] = new CharSequence[12];
        for(int i=0; i<intervalArray.length; i++){
            info[i] = intervalArray[i] + "ms";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(info, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                mSelectedInterval = intervalArray[which];
                mSelectedBeaconAction = ACTION_INTERVAL_BEACON;
                switch(which)

                {
                    //매우빠름
                    case 0:
                    case 1:
                    case 2:
                    {
                        textview_beacon_time_second.setText("매우빠름");
                        connectDevice(true);
                    }
                        break;
                    //빠름
                    case 3:
                    case 4:
                    case 5:
                    {
                        textview_beacon_time_second.setText("빠름");
                        connectDevice(true);
                        break;
                    }
                    //보통
                    case 6:
                    case 7:
                    case 8: {
                        textview_beacon_time_second.setText("보통");
                        connectDevice(true);
                        break;
                    }
                    //약간느림
                    case 9:
                    case 10:
                    case 11: {
                        textview_beacon_time_second.setText("약간느림");
                        connectDevice(true);
                    }
                        break;
                }
                dialog.dismiss();
            }
        });

        builder.show();

    }

    public void time_second_edit(){
        time_second = textview_time_edit.getText().toString().trim();

        if(WezoneUtil.isEmptyStr(time_second) || Integer.valueOf(time_second) == 0){
            Toast.makeText(m_Context, "1초 이상 가능합니다.", Toast.LENGTH_SHORT).show();
            return;
        }else {
            textview_beacon_time_second.setText(time_second);
            textview_beacon_time_second.setVisibility(View.VISIBLE);
            mDialog.cancel();
        }
    }


    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);

        if (mImagePath != null) {
            if (mType == BEACON_TYPE_REGIST) {

                String sn = edittext_sn.getText().toString().trim();
                if (sn.equals("") || sn == null) {
                    Toast.makeText(BeaconManageActivity.this,"시리얼 넘버를 입력해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                showRegistDialog();

//                uploadImageFile(Define.IMAGE_TYPE_BEACON, Define.IMAGE_STATUS_NEW, null, mImagePath);
            } else if (mType == BEACON_TYPE_EDIT) {
                PopupMenu popup = new PopupMenu(BeaconManageActivity.this, v);

                popup.getMenuInflater().inflate(R.menu.menu_beacon_edit, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.design_menu_item_beacon_off:
                                beacon_off_dialog();
                                break;
                            case R.id.design_menu_item_beacon_delete:
                                beacon_delete_dialog();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            } else {
                uploadImageFile(Define.IMAGE_TYPE_BEACON, Define.IMAGE_STATUS_UPDATE, mBeaconInfo.beacon_id, mImagePath);
            }
        } else {

            if (mType == BEACON_TYPE_EDIT) {
                PopupMenu popup = new PopupMenu(BeaconManageActivity.this, v);

                popup.getMenuInflater().inflate(R.menu.menu_beacon_edit, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.design_menu_item_beacon_off:
                                beacon_off_dialog();
                                break;
                            case R.id.design_menu_item_beacon_delete:
                                beacon_delete_dialog();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            } else {

                String sn = edittext_sn.getText().toString().trim();
                if (sn.equals("") || sn == null) {
                    Toast.makeText(BeaconManageActivity.this,"시리얼 넘버를 입력해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                showRegistDialog();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothUpdateService.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothUpdateService.ACTION_DONE);
        filter.addAction(BluetoothUpdateService.ACTION_UUID_READY);
        filter.addAction(BluetoothUpdateService.ACTION_MAJOR_MINOR_READY);
        filter.addAction(BluetoothUpdateService.ACTION_RSSI_READY);
        filter.addAction(BluetoothUpdateService.ACTION_MANUFACTURER_ID_READY);
        filter.addAction(BluetoothUpdateService.ACTION_ADV_INTERVAL_READY);
        filter.addAction(BluetoothUpdateService.ACTION_LED_STATUS_READY);
        filter.addAction(BluetoothUpdateService.ACTION_GATT_ERROR);
        LocalBroadcastManager.getInstance(BeaconManageActivity.this).registerReceiver(mServiceBroadcastReceiver, filter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(BeaconManageActivity.this).unregisterReceiver(mServiceBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            Intent service = new Intent(BeaconManageActivity.this, BluetoothUpdateService.class);
            stopService(service);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Define.INTENT_RESULT_CAMERA:
            case Define.INTENT_RESULT_PHOTO:
                if (resultCode == RESULT_OK) {
                    ArrayList<Data_PhotoPickerImage> pickerImages = getShare().popPhotoPickerImages();

                    Data_PhotoPickerImage data_photo = pickerImages.get(0);

                    if(mType == BEACON_TYPE_REGIST) {
                        mImagePath = data_photo.path;
                        imageview_profile.setImageBitmap(data_photo.thumbBitmap);
                    }else{
                        mImagePath = data_photo.path;
                        uploadImageFile(Define.IMAGE_TYPE_BEACON, Define.IMAGE_STATUS_UPDATE, mBeaconInfo.beacon_id, mImagePath);
                        imageview_profile.setImageBitmap(data_photo.thumbBitmap);
                    }
                } else {
                    mImagePath = null;
                    getShare().resetPhotoPickerImages();
                }
                break;
            case Define.INTENT_RESULT_NOTIFICATION:
                if (resultCode == RESULT_OK) {
                    //아무것도 설정안하면 사용안함으로
                    switch (mSelectedBtn) {
                        case SELECTED_SHORT: {
                            mShortActionItem = (Data_ActionItem) data.getSerializableExtra(SelectNotificationActivity.INTENT_RESULT_NOTIFICATION_ACTIONITEM);
                            String mMessage = getTitleText(mShortActionItem.id);
                            textview_short_desc.setText(mMessage);

                            if(mType == BEACON_TYPE_EDIT) {
                                sendBeaconPutArray(BEACON_FLAG_SHORT_ID, mShortActionItem);
                            }
                        }
                        break;

                        case SELECTED_LONG: {

                            mSOS_Userinfo = (ArrayList<Data_LocalUserInfo>) data.getSerializableExtra(SelectNotificationActivity.INTENT_RESULT_NOTIFICATION_USERLIST_SOS);
                            mLongActionItem = (Data_ActionItem) data.getSerializableExtra(SelectNotificationActivity.INTENT_RESULT_NOTIFICATION_ACTIONITEM);


                            String mMessage = getTitleText(mLongActionItem.id);
                            textview_long_desc.setText(mMessage);

                            if(mType == BEACON_TYPE_EDIT) {
                                sendBeaconPutArray(BEACON_FLAG_LONG_ID, mLongActionItem);
                            }

                        }
                        break;

                        case SELECTED_NEAR: {
                            mNearActionItem = (Data_ActionItem) data.getSerializableExtra(SelectNotificationActivity.INTENT_RESULT_NOTIFICATION_ACTIONITEM);
                            String mMessage = getTitleText(mNearActionItem.id);
                            textview_desc_01.setText(mMessage);

                            if(mType == BEACON_TYPE_EDIT) {
                                sendBeaconPutArray(BEACON_FLAG_NEAR_ID, mNearActionItem);
                            }

                        }
                        break;

                        case SELECTED_FAR: {
                            mFarActionItem = (Data_ActionItem) data.getSerializableExtra(SelectNotificationActivity.INTENT_RESULT_NOTIFICATION_ACTIONITEM);
                            String mMessage = getTitleText(mFarActionItem.id);
                            textview_desc_02.setText(mMessage);

                            if(mType == BEACON_TYPE_EDIT) {
                                sendBeaconPutArray(BEACON_FLAG_FAR_ID, mFarActionItem);
                            }
                        }
                        break;
                    }
                }
                break;
            case  Define.INTENT_RESULT_EDIT_TEXT:
                if (resultCode == RESULT_OK) {
                    mPush_content = data.getStringExtra(EditTextActivity.EDITTEXT_CONTENTS);
                    if(mType == BEACON_TYPE_EDIT) {
                        //string 으로
                        sendBeaconPut(BEACON_FLAG_NAME, mPush_content);
                    }
                    textview_name.setText(mPush_content);
                }
                break;

            case Define.INTENT_RESULT_USER:
                if (resultCode == RESULT_OK) {

                    mUserList = (ArrayList<Data_UserInfo>)data.getSerializableExtra(Define.INTENTKEY_USER_VALUE);

                    if(mUserList != null && mUserList.size() > 0){
                        sendBeaconShare(mBeaconInfo.beacon_id,mUserList);
                    }

                }
                break;

            case Define.INTENT_RESULT_DFU_BEACON:

                if(resultCode == RESULT_OK){
                    boolean isUpdated = data.getBooleanExtra("isUpdated",false);
                    if(isUpdated){
                        sendBeaconPut(BEACON_FLAG_FIRMWARE,mBeaconInfo.firmware_ver_new);
                        mBeaconInfo.firmware_ver = mBeaconInfo.firmware_ver_new;
                        setVersionUI("방금");
                    }
                }
                break;
        }
    }

    public void connectDevice(boolean isProgress){
        if(mType == BEACON_TYPE_REGIST){
            mSelectedDevice = getDevice(mBluetoothLeDevice.getMacAddr());
        }else{
            mSelectedDevice = getDevice(mBeaconInfo.mac);
        }

        if(!mBinded) {
            if(isProgress) {
                showProgressPopup();
            }

            Intent service = new Intent(BeaconManageActivity.this, BluetoothUpdateService.class);
            service.putExtra(BluetoothUpdateService.EXTRA_DATA, mSelectedDevice);
            startService(service);
            mBinded = bindService(service, mServiceConnection, 0);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    //비콘수정 String형식
    private void sendBeaconPut(String flag, String val) {

        Send_PutBeacon putbeacon = new Send_PutBeacon();
        Data_Beacon_info data_beacon_info = new Data_Beacon_info();
        ArrayList<Data_Beacon_info> dataBeaconArrayList = new ArrayList<>();

//        putbeacon.beacon_id = mBeaconInfo.beacon_id;
        putbeacon.beacon_id = mBeaconInfo.beacon_id;

        data_beacon_info.flag = flag;
        data_beacon_info.val = val;
        dataBeaconArrayList.add(data_beacon_info);

        putbeacon.beacon_info = dataBeaconArrayList;


        Call<Rev_Base> PutWezoneWithCall = wezoneRestful.puBeacon(putbeacon);
        PutWezoneWithCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {

            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });

    }


    //비콘수정 Array형식
    private void sendBeaconPutArray(String flag, Data_ActionItem val) {

        Send_PutBeaconArray putbeacon = new Send_PutBeaconArray();
        Data_Beacon_info_Array data_beacon_info_array = new Data_Beacon_info_Array();
        ArrayList<Data_Beacon_info_Array> dataBeaconInfoArrayArrayList = new ArrayList<>();

        putbeacon.beacon_id = mBeaconInfo.beacon_id;

        data_beacon_info_array.flag = flag;
        data_beacon_info_array.val = new ArrayList<>();
        data_beacon_info_array.val.add(val);
        dataBeaconInfoArrayArrayList.add(data_beacon_info_array);

        putbeacon.beacon_info = dataBeaconInfoArrayArrayList;

        Call<Rev_Base> PutWezoneWithArrayCall = wezoneRestful.putBeaconArray(putbeacon);
        PutWezoneWithArrayCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {

            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });

    }


    @Override
    public void uploadResult(String type, Rev_Upload resultData) {
        super.uploadResult(type, resultData);
        if(mType == BEACON_TYPE_EDIT) {
            sendBeaconPut(BEACON_FLAG_IMAGE_URL,resultData.url);
        }else {
            sendBeaconPost(resultData.url);
        }
    }

    public void sendBeaconPost(String img_url) {

        Send_PostBeacon beacon = new Send_PostBeacon();
        beacon.device_id = getShare().getLoginParam().device_id;
        beacon.model = "vinetech beacon";
        beacon.firmware_ver = "1.0";
        beacon.mac = mBluetoothLeDevice.getMacAddr();
        beacon.beacon_major = String.valueOf(mBluetoothLeDevice.getVineBeacon().getmUsableMajor());
        beacon.beacon_minor = String.valueOf(mBluetoothLeDevice.getVineBeacon().getMinor());
        beacon.beacon_uuid = mBluetoothLeDevice.getVineBeacon().getUUID().toString();
        beacon.beacon_serial = edittext_sn.getText().toString().trim();
        beacon.name = textview_name.getText().toString().trim();
        beacon.img_url = img_url;
        beacon.registration_code = String.format("%02X", mRegistrationCode);

//        if(mBinded){
//            mBinder.setLedTempStatus(mRegistrationCode);
//        }else{
//            Toast.makeText(BeaconManageActivity.this,"WeCON 연결이 불안정합니다. 다시 시도해주세요",Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (linearlayout_btn_push.isSelected()) {
            beacon.push_flag = "T";
        } else {
            beacon.push_flag = "F";
        }

        if (mShortActionItem != null) {
            ArrayList<Data_ActionItem> items = new ArrayList<>();
            items.add(mShortActionItem);
            beacon.short_id = items;
        } else {
            ArrayList<Data_ActionItem> items = new ArrayList<>();
            mShortActionItem = new Data_ActionItem();
            mShortActionItem.id = Data_ActionItem.ID_NOT_USE;
            items.add(mShortActionItem);
            beacon.short_id = items;
        }

        if (mLongActionItem != null) {
            ArrayList<Data_ActionItem> items = new ArrayList<>();
            items.add(mLongActionItem);
            beacon.long_id = items;
        } else {
            ArrayList<Data_ActionItem> items = new ArrayList<>();
            mLongActionItem = new Data_ActionItem();
            mLongActionItem.id = Data_ActionItem.ID_NOT_USE;
            items.add(mLongActionItem);
            beacon.long_id = items;
        }

        if (mNearActionItem != null) {
            ArrayList<Data_ActionItem> items = new ArrayList<>();
            items.add(mNearActionItem);
            beacon.near_id = items;
        } else {
            ArrayList<Data_ActionItem> items = new ArrayList<>();
            mNearActionItem = new Data_ActionItem();
            mNearActionItem.id = Data_ActionItem.ID_NOT_USE;
            items.add(mNearActionItem);
            beacon.near_id = items;
        }

        if (mFarActionItem != null) {
            ArrayList<Data_ActionItem> items = new ArrayList<>();
            items.add(mFarActionItem);
            beacon.far_id = items;
        } else {
            ArrayList<Data_ActionItem> items = new ArrayList<>();
            mFarActionItem = new Data_ActionItem();
            mFarActionItem.id = Data_ActionItem.ID_NOT_USE;
            items.add(mFarActionItem);
            beacon.far_id = items;
        }

        if(mDialog != null){
            mDialog.cancel();
        }
        showProgressPopup();
        Call<Rev_PostBeacon> postBeaconCall = wezoneRestful.postBeacon(beacon);
        postBeaconCall.enqueue(new Callback<Rev_PostBeacon>() {
            @Override
            public void onResponse(Call<Rev_PostBeacon> call, Response<Rev_PostBeacon> response) {
                Rev_PostBeacon revPostBeacon = response.body();
                if (isNetSuccess(revPostBeacon)) {
                    finish();
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_PostBeacon> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }

    public void sendBeaconShare(String beacon_id, ArrayList<Data_UserInfo> selectedUuid){

        Send_PutShare putShare = new Send_PutShare();
        putShare.type = Send_PutShare.SHARE_TYPE_BEACON;

        putShare.zone_id = beacon_id;

        putShare.other_uuids = new ArrayList<>();
        for(Data_UserInfo userInfo : selectedUuid){
            HashMap<String,String> other_uuids = new HashMap<>();
            other_uuids.put("uuid",userInfo.uuid);
            putShare.other_uuids.add(other_uuids);
        }
        putShare.share_flag = Send_PutShare.SHARE_FLAG_INVITE;

        showProgressPopup();
        Call<Rev_Base> putBeaconShare = wezoneRestful.putBeaconShare(putShare);
        putBeaconShare.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revBase = response.body();
                if (isNetSuccess(revBase)) {
                    Toast.makeText(BeaconManageActivity.this, "WeCON을 공유하였습니다", Toast.LENGTH_SHORT).show();
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

        if (mType == BEACON_TYPE_REGIST) {
            if(mBluetoothLeDevice.getMacAddr().equals(device.getMacAddr())){
                double distance = BluetoothLeDevice.calculateDistance(device.getVineBeacon().getPower(), device.getRssi());
                double b = Math.round(distance * 100d) / 100d;
                textview_distance.setText(b + " m");
            }

//            if(BluetoothLeService.ACTION_CLICK_LONG_PRESS.equals(action)) {
            if(BluetoothLeService.ACTION_CLICK_SHORT_PRESS.equals(action)) {
                if(mBluetoothLeDevice.getMacAddr().equals(device.getMacAddr()) && mDialog != null && mDialog.isShowing()){
                    mSelectedBeaconAction = ACTION_REGIST_BEACON;
                    connectDevice(true);
                }
            }

        }else{
            if (WezoneUtil.isSameBeacon(device,mBeaconInfo)) {
                double distance = BluetoothLeDevice.calculateDistance(device.getVineBeacon().getPower(), device.getRssi());
                double b = Math.round(distance * 100d) / 100d;
                textview_distance.setText(b + " m");

                //수정 화면 들어오면 등록 시도함
                if(device.getVineBeacon().getGATTRegiStatus() == 0){

                    mSelectedBeaconAction = ACTION_REGIST_BEACON;
                    connectDevice(false);
                }
            }
        }
    }

    public void showRegistDialog() {
        mMenu_type = POPUP_MENU_TYPE_REGIST;
        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_beacon_regist);

        linearlayout_btn_01 = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_01);
        linearlayout_btn_01.setOnClickListener(mClickListener);

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
        mDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        mDialog.getWindow().setGravity(Gravity.CENTER);
    }

    public void beacon_off_dialog() {
        mMenu_type = POPUP_MENU_TYPE_OFF;
        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_beacon_off);

        linearlayout_btn_01 = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_01);
        linearlayout_btn_01.setOnClickListener(mClickListener);

        linearlayout_btn_02 = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_02);
        linearlayout_btn_02.setOnClickListener(mClickListener);

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
        mDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        mDialog.getWindow().setGravity(Gravity.CENTER);
    }

    public void beacon_delete_dialog() {
        mMenu_type = POPUP_MENU_TYPE_DELETE;
        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_beacon_delete);

        linearlayout_btn_01 = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_01);
        linearlayout_btn_01.setOnClickListener(mClickListener);

        linearlayout_btn_02 = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_02);
        linearlayout_btn_02.setOnClickListener(mClickListener);

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
        mDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        mDialog.getWindow().setGravity(Gravity.CENTER);
    }


    public void Beacon_delete() {
        showProgressPopup();

        Call<Rev_Base> revBaseCall = wezoneRestful.deleteBeacon(mBeaconInfo.beacon_id);
        revBaseCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revBase = response.body();
                if (isNetSuccess(revBase)) {
                    new AlertDialog.Builder(BeaconManageActivity.this)
                            .setTitle("이용 안내")
                            .setMessage("초기화가 완료되었습니다\n 배터리를 탈착/장착 후 사용하세요")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                }
                            })
                            .show();
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
                Toast.makeText(BeaconManageActivity.this, "WeCON 삭제 실패", Toast.LENGTH_SHORT).show();
                hidePorgressPopup();
            }
        });
    }

    public void onTimeEditDialog() {

        mMenu_type = POPUP_MENU_TYPE_TIME_SECOND;

        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.FILL_PARENT;

        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
//        mDialog.getWindow().setAttributes(params);
        mDialog.setContentView(R.layout.dialog_time_select);

        linearlayout_btn_01 = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_01);
        linearlayout_btn_01.setOnClickListener(mClickListener);

        linearlayout_btn_02 = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_02);
        linearlayout_btn_02.setOnClickListener(mClickListener);

        textview_time_edit = (EditText) mDialog.findViewById(R.id.textview_time_edit);

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
        mDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        mDialog.getWindow().setGravity(Gravity.CENTER);

    }


    public BluetoothDevice getDevice(String macAddr){
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if(mBluetoothManager != null){
            BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter != null) {
                return mBluetoothAdapter.getRemoteDevice(macAddr);
            }
        }
        return null;
    }

    private BroadcastReceiver mServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
//            final Activity activity = getActivity();
//            if (activity == null || !isResumed())
//                return;

            final String action = intent.getAction();
            if (BluetoothUpdateService.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothUpdateService.EXTRA_DATA, BluetoothUpdateService.STATE_DISCONNECTED);
                switch (state) {
                    case BluetoothUpdateService.STATE_DISCONNECTED:

                        if(mBinded && mBinder != null){
                            final Intent service = new Intent(BeaconManageActivity.this, BluetoothUpdateService.class);
                            unbindService(mServiceConnection);
                            stopService(service);
                            mBinder = null;
                            mBinded = false;
                            hidePorgressPopup();
                        }
                        break;
                    case BluetoothUpdateService.STATE_CONNECTED:
                        mBinded = true;
                        mBinder.setLedTempStatus(mRegistrationCode);
                        hidePorgressPopup();
                        break;
                    case BluetoothUpdateService.STATE_DISCONNECTING:
                    case BluetoothUpdateService.STATE_CONNECTING:
                        break;
                }
            } else if (BluetoothUpdateService.ACTION_UUID_READY.equals(action)) {
//                final UUID uuid = ((ParcelUuid) intent.getParcelableExtra(BluetoothUpdateService.EXTRA_DATA)).getUuid();
//                mUuidView.setText(uuid.toString());
//                setUuidControlsEnabled(true);
            } else if (BluetoothUpdateService.ACTION_MAJOR_MINOR_READY.equals(action)) {
//                final int major = intent.getIntExtra(BluetoothUpdateService.EXTRA_MAJOR, 0);
//                final int minor = intent.getIntExtra(BluetoothUpdateService.EXTRA_MINOR, 0);
//                mMajorView.setText(String.valueOf(major));
//                mMinorView.setText(String.valueOf(minor));
//                setMajorMinorControlsEnabled(true);
            } else if (BluetoothUpdateService.ACTION_RSSI_READY.equals(action)) {
//                final int rssi = intent.getIntExtra(BluetoothUpdateService.EXTRA_DATA, 0);
//                mCalibratedRssiView.setTag(rssi);
//                mCalibratedRssiView.setText(String.valueOf(rssi));
//                setRssiControlsEnabled(true);
            } else if (BluetoothUpdateService.ACTION_MANUFACTURER_ID_READY.equals(action)) {
//                final int manufacturerId = intent.getIntExtra(BluetoothUpdateService.EXTRA_DATA, 0);
//                mManufacturerIdView.setText(String.valueOf(manufacturerId));
//                setManufacturerIdControlsEnabled(true);
            } else if (BluetoothUpdateService.ACTION_ADV_INTERVAL_READY.equals(action)) {
                final int interval = intent.getIntExtra(BluetoothUpdateService.EXTRA_DATA, 0);

            } else if (BluetoothUpdateService.ACTION_LED_STATUS_READY.equals(action)) {
                final boolean on = intent.getBooleanExtra(BluetoothUpdateService.EXTRA_DATA, false);

                if(mSelectedBeaconAction == ACTION_FIND_BEACON){
                    if(linearlayout_beacon_plus.isSelected()){
                        textview_beacon_plus.setText("WeCON 소리끄기");
                        mBinder.setLedStatus(true);
                        //비콘 찾기 할떄는 disconnect 하지말자.

                    }
                }else if(mSelectedBeaconAction == ACTION_INTERVAL_BEACON){
                    mBinder.setAdvInterval(mSelectedInterval);
                    mBinder.disconnectAndClose();
                }else if(mSelectedBeaconAction == ACTION_OFF_BEACON){
                    mBinder.setLedTempStatus(2);
                    mBinder.disconnectAndClose();
                }else if(mSelectedBeaconAction == ACTION_REGIST_BEACON){
                    mBinder.disconnectAndClose();

                    //등록 시 비콘 입력이 끝나야 등록 함
                    if(mType == BEACON_TYPE_REGIST){
                        if (mImagePath != null) {
                            uploadImageFile(Define.IMAGE_TYPE_BEACON, Define.IMAGE_STATUS_NEW, null, mImagePath);
                        }else{
                            sendBeaconPost(null);
                        }
                    }
                }

                mSelectedBeaconAction = ACTION_NONE;


            } else if (BluetoothUpdateService.ACTION_DONE.equals(action)) {
//                final boolean advanced = intent.getBooleanExtra(BluetoothUpdateService.EXTRA_DATA, false);
//                mAdvancedTitleView.setEnabled(advanced);
//                mBinder.read();
            } else if (BluetoothUpdateService.ACTION_GATT_ERROR.equals(action)) {
                final int error = intent.getIntExtra(BluetoothUpdateService.EXTRA_DATA, 0);

                switch (error) {
                    case BluetoothUpdateService.ERROR_UNSUPPORTED_DEVICE:
                        Toast.makeText(BeaconManageActivity.this, "미지원 단말입니다", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(BeaconManageActivity.this, "WeCON과의 연결이 불안정합니다["+error+"]", Toast.LENGTH_SHORT).show();
                        break;
                }
                mBinder.disconnectAndClose();
                mBinder = null;
                mBinded = false;
                hidePorgressPopup();

                //등록 시 등록 오류나면
                if(mType == BEACON_TYPE_REGIST){
                       if(mDialog != null && mDialog.isShowing()){
                           mDialog.dismiss();
                       }
                }
            }
        }
    };
}
