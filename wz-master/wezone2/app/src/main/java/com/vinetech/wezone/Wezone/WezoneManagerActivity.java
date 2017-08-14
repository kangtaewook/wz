package com.vinetech.wezone.Wezone;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Common.Activity_PhotoPicker_Folder;
import com.vinetech.wezone.Common.EditTextActivity;
import com.vinetech.wezone.Data.Data_ActionItem;
import com.vinetech.wezone.Data.Data_ActionItemData;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_BeaconArray_location_type;
import com.vinetech.wezone.Data.Data_Beacon_id;
import com.vinetech.wezone.Data.Data_Board;
import com.vinetech.wezone.Data.Data_Chat_UserList;
import com.vinetech.wezone.Data.Data_MsgKey;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Data.Data_UnRead;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_Upload;
import com.vinetech.wezone.RevPacket.Rev_WeZone;
import com.vinetech.wezone.SendPacket.Send_PostWezone;
import com.vinetech.wezone.SendPacket.Send_PutDataLocationTypeArray;
import com.vinetech.wezone.SendPacket.Send_PutDataWithArray;
import com.vinetech.wezone.SendPacket.Send_PutDataWithValue;
import com.vinetech.wezone.SendPacket.Send_PutWezone;
import com.vinetech.wezone.SendPacket.Send_PutWezoneWithArray;
import com.vinetech.wezone.SendPacket.Send_PutWezoneWithArrayBeacon;
import com.vinetech.xmpp.LibXmppManager;

import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sinsumin on 2017. 1. 18..
 * <p>
 * 위존 관리 화면
 * <p>
 * 위존 등록 및 수정 업무
 */

public class WezoneManagerActivity extends BaseActivity {

    public static final String WEZONE_TYPE = "type";
    public static final String WEZONE_WEZONE = "wezone";
    public static final String WEZONE_EDIT = "wezone_edit";
    public static final String LOACTIONSELECT = "locationselect";
    public static final String LOACTIONDATA = "locationdata";
    public static final String INTENT_RESULT_NOTIFICATION_MACTIONITEM = "mActionItem";
    public static final String WEZONE_WEZONE_PUT_TYPE = "WEZONE_WEZONE_PUT_TYPE";
    public static final String POST = "POST";
    public static final String WEZONE_WEZONE_PUT = "wezone_put";
    public static final int WEZONE_TYPE_REGIST = 0;
    public static final int WEZONE_TYPE_EDIT = 1;
    public static final int IMAGE_TYPE_PROFILE = 0;
    public static final int IMAGE_TYPE_BG = 1;
    public static final int TITLE_MODE = 3;
    public static final int INTRODUCTION_MODE = 4;
    public int LAST_TIME=0;
    public int CURRENT_TIME=0;

    public BaseActivity mActivity;

    private Data_ActionItem mActionItem_zone_in;
    private Data_BeaconArray_location_type mActionItem_beacon;
    private ArrayList<Data_Beacon> mBeacons;
    private Data_WeZone mWezone;

    private GpsInfo mGpsInfo;


    private LinearLayout linearlayout_btn_select_area;
    private LinearLayout linearlayout_btn_wezone_black;
    private ImageView activity_regist_wezone_people;
    private TextView activity_regist_wezone_people_textview;
    private TextView activity_regist_wezone_distance;
    private RelativeLayout relativelaoyt_wezone_profile;
    private ImageView imageView2;
    private RelativeLayout activity_regist_wezone_iamge;
    private EditText wezone_title_eidittext;
    private EditText wezone_intru_eidittext;
    private LinearLayout linearLayout_wezone_black;
    private LinearLayout linearLayout_wezone_people;
    private LinearLayout linearLayout_wezone_zonein;
    private TextView activity_zone_in_text;
    private ImageView wezone_profile;
    private ImageView image_back;
    private String mMemberLimit;
    private LinearLayout linearlayout_btn_wezone_alarm;
    private LinearLayout edit_wezone_title_linearlayout;
    private LinearLayout edit_wezone_introduction_linearlayout;
    private LinearLayout write_wezone_introduction_linearlayout;
    private LinearLayout write_wezone_title_linearlayout;

    private TextView activity_location_choice;
    private TextView edit_wezone_title_textview;
    private TextView edit_wezone_introduction_textview;
    private TextView wezone_block_text;
    private String mTB;
    private String alarm;
    private String mlocation_data;
    private String value;
    private String mImagePath;
    private String mBackImagePath;
    private String mImageUrl;
    private String mBackImageUrl;
    private String mlocationtype;
    private String mIN;
    private String mOUT;
    private String location_type_name;
    private String mstrTemp;

    private int mImageType = IMAGE_TYPE_PROFILE;
    private int mvalue;
    private int mBeaconsize;
    private int mType = WEZONE_TYPE_REGIST;

    private double latitude;
    private double longitude;

    private long mLastClickTime;

    public static void startActivityWithRegist(BaseActivity activity) { //등록
        Intent intent = new Intent(activity, WezoneManagerActivity.class);
        intent.putExtra(WEZONE_TYPE, WEZONE_TYPE_REGIST);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_WEZONE);
    }

    public static void startActivityWithEdit(BaseActivity activity, Data_WeZone wezone) { //수정
        Intent intent = new Intent(activity, WezoneManagerActivity.class);
        intent.putExtra(WEZONE_TYPE, WEZONE_TYPE_EDIT);
        intent.putExtra(WEZONE_WEZONE, wezone);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_WEZONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_wezone);

        mType = getIntent().getIntExtra(WEZONE_TYPE, WEZONE_TYPE_REGIST);

        relativelaoyt_wezone_profile = (RelativeLayout) findViewById(R.id.relativelaoyt_wezone_profile); // 프로필 사진
        relativelaoyt_wezone_profile.setOnClickListener(mClickListener);
        wezone_block_text = (TextView) findViewById(R.id.wezone_block_text);
        activity_regist_wezone_people = (ImageView) findViewById(R.id.activity_regist_wezone_people);
        activity_regist_wezone_people_textview = (TextView) findViewById(R.id.activity_regist_wezone_people_textview);
        linearlayout_btn_wezone_black = (LinearLayout) findViewById(R.id.linearlayout_btn_wezone_black);
        linearlayout_btn_wezone_black.setOnClickListener(mClickListener);
        linearlayout_btn_wezone_alarm = (LinearLayout) findViewById(R.id.linearlayout_btn_wezone_alarm);
        linearlayout_btn_wezone_alarm.setOnClickListener(mClickListener);
        activity_regist_wezone_distance = (TextView) findViewById(R.id.activity_regist_wezone_distance);
        imageView2 = (ImageView) findViewById(R.id.imageView2); // 배경 사진 등록 버튼
        imageView2.setOnClickListener(mClickListener);
        activity_regist_wezone_iamge = (RelativeLayout) findViewById(R.id.activity_regist_wezone_iamge); // 배경 사진
        activity_regist_wezone_iamge.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));
        activity_regist_wezone_iamge.setOnClickListener(mClickListener);
        wezone_title_eidittext = (EditText) findViewById(R.id.wezone_title_eidittext); // 타이틀 EditText
        wezone_title_eidittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        wezone_title_eidittext.setOnClickListener(mClickListener);
        wezone_intru_eidittext = (EditText) findViewById(R.id.wezone_intru_eidittext); // 소개 EditText
        wezone_intru_eidittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        wezone_intru_eidittext.setOnClickListener(mClickListener);
        linearLayout_wezone_black = (LinearLayout) findViewById(R.id.linearLayout_wezone_black); // 비공개 ZONE
        linearLayout_wezone_black.setSelected(true);
        linearLayout_wezone_people = (LinearLayout) findViewById(R.id.linearLayout_wezone_people); // 인원 제한
        linearLayout_wezone_people.setOnClickListener(mClickListener);
        linearLayout_wezone_zonein = (LinearLayout) findViewById(R.id.linearLayout_wezone_zonein); // 존in
        linearLayout_wezone_zonein.setOnClickListener(mClickListener);
        wezone_profile = (ImageView) findViewById(R.id.wezone_profile);
        image_back = (ImageView) findViewById(R.id.image_back);
        activity_location_choice = (TextView) findViewById(R.id.activity_location_choice);
        linearlayout_btn_select_area = (LinearLayout) findViewById(R.id.linearlayout_btn_select_area); // 거리 범위
        wezone_intru_eidittext.setOnClickListener(mClickListener);
        edit_wezone_title_linearlayout = (LinearLayout) findViewById(R.id.edit_wezone_title_linearlayout);
        edit_wezone_title_linearlayout.setOnClickListener(mClickListener);
        edit_wezone_introduction_linearlayout = (LinearLayout) findViewById(R.id.edit_wezone_introduction_linearlayout);
        edit_wezone_introduction_linearlayout.setOnClickListener(mClickListener);
        write_wezone_introduction_linearlayout = (LinearLayout) findViewById(R.id.write_wezone_introduction_linearlayout);
        write_wezone_title_linearlayout = (LinearLayout) findViewById(R.id.write_wezone_title_linearlayout);
        edit_wezone_title_textview = (TextView) findViewById(R.id.edit_wezone_title_textview);
        edit_wezone_introduction_textview = (TextView) findViewById(R.id.edit_wezone_introduction_textview);
        activity_zone_in_text = (TextView) findViewById(R.id.activity_zone_in_text);

        /** Default 값 **/
        if (mTB == null) {
            mTB = "T";
        }
        if (alarm == null) {
            alarm = "T";
        }
        if (mlocationtype == null) {
            mlocationtype = "G";
        }
        if (mIN == null) {
            mIN = Data_ActionItem.ID_NOT_USE;
        }
        if (mOUT == null) {
            mOUT = Data_ActionItem.ID_NOT_USE;
        }


        String titleW = getResources().getString(R.string.wezone_title);
        if (mType == WEZONE_TYPE_REGIST) {
            //등록시
            mWezone = new Data_WeZone();
            setHeaderView(R.drawable.btn_back_white, titleW, R.drawable.btn_check);// 상단바 타이틀

            edit_wezone_title_linearlayout.setVisibility(View.GONE);
            edit_wezone_introduction_linearlayout.setVisibility(View.GONE);

            /** Default 값 **/
            alarm = "T";
            linearlayout_btn_wezone_alarm.setSelected(true);

            mGpsInfo = new GpsInfo(WezoneManagerActivity.this);
            // GPS 사용유무 가져오기
            if (mGpsInfo.isGetLocation()) {
                longitude = mGpsInfo.getLongitude();
                latitude = mGpsInfo.getLatitude();

                mWezone.latitude = String.valueOf(latitude);
                mWezone.longitude = String.valueOf(longitude);
            } else {
                // GPS 를 사용할수 없으므로
                mGpsInfo.showSettingsAlert();
            }

        } else {
            //수정시
            setHeaderView(R.drawable.btn_back_white, titleW, 0);

            mWezone = (Data_WeZone) getIntent().getSerializableExtra(WEZONE_WEZONE);

            imageView2.setVisibility(View.VISIBLE);
            edit_wezone_title_linearlayout.setVisibility(View.VISIBLE);
            edit_wezone_introduction_linearlayout.setVisibility(View.VISIBLE);
            write_wezone_introduction_linearlayout.setVisibility(View.GONE);
            write_wezone_title_linearlayout.setVisibility(View.GONE);

            if (mWezone.location_type.equals("B")) {
                location_type_name = "WeCON";
            } else {
                if (mWezone.location_data == null || location_type_name == "0" || "".equals(location_type_name)) {
                    mlocation_data = "200";
                    mWezone.location_data = "200";
                } else {
                    location_type_name = "GPS";
                }
            }
            activity_location_choice.setText(location_type_name);//


            edit_wezone_introduction_textview.setText(mWezone.introduction);
            edit_wezone_title_textview.setText(mWezone.title);

            if ("10000".equals(mWezone.member_limit)) {
                activity_regist_wezone_people_textview.setText("무제한");
            } else {
                activity_regist_wezone_people_textview.setText(mWezone.member_limit);
            }

            if (mWezone.wezone_type.equals("T")) {
                linearlayout_btn_wezone_black.setSelected(true);
                wezone_block_text.setText("공개 ZONE");
            } else {
                linearlayout_btn_wezone_black.setSelected(false);
            }

            if (mWezone.push_flag.equals("T")) {
                linearlayout_btn_wezone_alarm.setSelected(true);
            } else {
                linearlayout_btn_wezone_alarm.setSelected(false);
            }

            if (WezoneUtil.isEmptyStr(mWezone.img_url) == false) {
                showImageFromRemote(mWezone.img_url, R.drawable.ic_bunny_image, wezone_profile);
            } else {
                wezone_profile.setImageResource(R.drawable.ic_bunny_image);
            }
            if (WezoneUtil.isEmptyStr(mWezone.bg_img_url) == false) {
                showImageFromRemote(mWezone.bg_img_url, 0, image_back);
            }
            if (mWezone.zone_in != null && mWezone.zone_in.id != null) {
                activity_zone_in_text.setText(Data_ActionItem.getTitleText(mWezone.zone_in.id));
            } else {
                activity_zone_in_text.setText(Data_ActionItem.getTitleText(Data_ActionItem.ID_NOT_USE));
            }
        }

        linearlayout_btn_select_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //위치인식 설정을 누를 시 G일 때
                if (mWezone.location_type == null || "G".equals(mWezone.location_type)) {
                    if (mWezone.location_data == null) {
                        mWezone.location_data = "200";
                    } else {
                        if (mWezone.location_data.equals("3.2")) {
                            mWezone.location_data = "3200";
                        } else if (mWezone.location_data.equals("1.6")) {
                            mWezone.location_data = "1600";
                        }

                        if (mlocation_data == null) {
                            if (mWezone.location_data.equals("1600")) {
                                mWezone.location_data = "1.6";
                            } else if (mWezone.location_data.equals("3200")) {
                                mWezone.location_data = "3.2";
                            } else if (mWezone.location_data.equals("400")) {
                                mWezone.location_data = "400";
                            } else {
                                mWezone.location_data = "200";
                            }
                        } else {
                            if (mlocation_data.equals("1.6")) {
                                mWezone.location_data = "1.6";
                            } else if (mlocation_data.equals("3.2")) {
                                mWezone.location_data = "3.2";
                            } else if (mWezone.location_data.equals("400")) {
                                mWezone.location_data = "400";
                            } else {
                                mWezone.location_data = "200";
                            }
                        }
                    }
                    if (mWezone.longitude == null || mWezone.latitude == null) {
                        mGpsInfo = new GpsInfo(WezoneManagerActivity.this);
                        // GPS 사용유무 가져오기
                        if (mGpsInfo.isGetLocation()) {
                            longitude = mGpsInfo.getLongitude();
                            latitude = mGpsInfo.getLatitude();
                        } else {
                            // GPS 를 사용할수 없으므로
//                            mGpsInfo.showSettingsAlert();
                        }
                    }
                } else if (mWezone.location_type.equals("B")) {
                    //위치인식 설정을 누를 시 WeCON 일 때
                    //location_type = "B";
                    mWezone.location_data = "";
                    mBeacons = (ArrayList<Data_Beacon>) mWezone.beacons;
                    if (mWezone.longitude == null || mWezone.latitude == null) {
                        mGpsInfo = new GpsInfo(WezoneManagerActivity.this);
                        // GPS 사용유무 가져오기
                        if (mGpsInfo.isGetLocation()) {
                            longitude = mGpsInfo.getLongitude();
                            latitude = mGpsInfo.getLatitude();
                        } else {
                            // GPS 를 사용할수 없으므로
                        }
                    }

                }
                if (mWezone.longitude == null || mWezone.latitude == null) {
                    Toast.makeText(m_Context, "GPS 기능을 키고 다시 위존 생성을 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //위치 인식 설정 화면으로
                WezoneLocationManagerActivity.setStartActivityLocation(WezoneManagerActivity.this, mBeaconsize, Double.valueOf(mWezone.longitude), Double.valueOf(mWezone.latitude), mWezone.location_data, mBeacons);
            }
        });


        linearLayout_wezone_people.setOnClickListener(new View.OnClickListener() {//인원 제한 클릭 시 설정
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(WezoneManagerActivity.this, activity_regist_wezone_people);
                //클릭 시 팝업창이 뜸.
                popup.getMenuInflater().inflate(R.menu.menu_wezone_people, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.design_menu_item_wezone_people_notlimit:
                                mMemberLimit = "무제한";
                                break;
                            case R.id.design_menu_item_wezone_people100:
                                mMemberLimit = "100";
                                break;
                            case R.id.design_menu_item_wezone_people50:
                                mMemberLimit = "50";
                                break;
                            case R.id.design_menu_item_wezone_people10:
                                mMemberLimit = "10";
                                break;
                        }

                        if (mMemberLimit.equals("무제한")) {
                            activity_regist_wezone_people_textview.setText(mMemberLimit);
                            mMemberLimit = "10000";

                            if (mType == WEZONE_TYPE_EDIT) { // 수정시
                                mWezone.member_limit = mMemberLimit;
                                sendWeZonePut(Send_PutDataWithValue.FLAG_MEMBER_LIMIT, mMemberLimit); //수정할 때 사용하는 전문
                            }
                        } else {
                            activity_regist_wezone_people_textview.setText(mMemberLimit + " 명");

                            if (mType == WEZONE_TYPE_EDIT) {
                                mWezone.member_limit = mMemberLimit;
                                sendWeZonePut(Send_PutDataWithValue.FLAG_MEMBER_LIMIT, mMemberLimit); //수정할 때 사용하는 전문
                            }
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });


    }


    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            switch (viewId) {
                case R.id.linearlayout_btn_wezone_black: // 공개 or 비공개 선택
                    if (linearlayout_btn_wezone_black.isSelected()) {
                        linearlayout_btn_wezone_black.setSelected(false);
                        mTB = "F"; //비공개
                    } else {
                        linearlayout_btn_wezone_black.setSelected(true);
                        wezone_block_text.setText("공개 ZONE");
                        mTB = "T"; //공개
                    }
                    if (mType == getIntent().getIntExtra(WEZONE_TYPE, WEZONE_TYPE_EDIT)) {
                        mWezone.wezone_type = mTB;
                        sendWeZonePut(Send_PutDataWithValue.FLAG_WEZONE_TYPE, mWezone.wezone_type);
                    }
                    break;
                case R.id.linearlayout_btn_wezone_alarm:
                    if (linearlayout_btn_wezone_alarm.isSelected()) {
                        linearlayout_btn_wezone_alarm.setSelected(false);
                        alarm = "F"; //알람OFF
                    } else {
                        linearlayout_btn_wezone_alarm.setSelected(true);
                        alarm = "T"; //알람ON
                    }
                    if (mType == getIntent().getIntExtra(WEZONE_TYPE, WEZONE_TYPE_EDIT)) {
                        mWezone.push_flag = alarm;
                        sendWeZonePut(Send_PutDataWithValue.FLAG_PUSH, mWezone.push_flag);
                    }
                    break;
                case R.id.relativelaoyt_wezone_profile:
                    mImageType = IMAGE_TYPE_PROFILE;
                    Activity_PhotoPicker_Folder.startFolderActivityForResult(WezoneManagerActivity.this, 1);
                    //프로필 사진 클릭시
                    break;
                case R.id.imageView2:
                    mImageType = IMAGE_TYPE_BG;
                    Activity_PhotoPicker_Folder.startFolderActivityForResult(WezoneManagerActivity.this, 1);
                    //배경 사진 클릭시
                    break;
                case R.id.edit_wezone_title_linearlayout:
                    EditTextActivity.startActivity(WezoneManagerActivity.this, "타이틀 입력", mWezone.title);
                    mvalue = TITLE_MODE;
                    break;
                case R.id.edit_wezone_introduction_linearlayout:
                    EditTextActivity.startActivity(WezoneManagerActivity.this, "소개 입력", mWezone.introduction);
                    mvalue = INTRODUCTION_MODE;
                    break;

                case R.id.linearLayout_wezone_zonein:
                    if (mType == WEZONE_TYPE_REGIST) {
                        if (mWezone != null) {
                            WezoneNoticeSelectActivity.startActivityWithRegist(WezoneManagerActivity.this, "존인", mWezone);
                        } else {
                            WezoneNoticeSelectActivity.startActivityWithRegist(WezoneManagerActivity.this, "존인", null);
                        }
                    } else {
                        WezoneNoticeSelectActivity.startActivityWithRegist(WezoneManagerActivity.this, "존인", mWezone);
                    }
                    break;
            }


        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Define.INTENT_RESULT_CAMERA:
            case Define.INTENT_RESULT_PHOTO:
                if (resultCode == RESULT_OK) {
                    ArrayList<Data_PhotoPickerImage> pickerImages = getShare().popPhotoPickerImages();

                    Data_PhotoPickerImage data_photo = pickerImages.get(0);

                    if (mImageType == IMAGE_TYPE_PROFILE) {
                        if (mType == WEZONE_TYPE_REGIST) {
                            mImagePath = data_photo.path;
                        } else {
                            mImagePath = data_photo.path;
                            uploadImageFile(Define.IMAGE_TYPE_WEZONE, Define.IMAGE_STATUS_UPDATE, mWezone.wezone_id, mImagePath); //사진을 업로드
                        }
                        wezone_profile.setImageBitmap(data_photo.thumbBitmap);
                    } else {
                        if (mType == WEZONE_TYPE_REGIST) {
                            mBackImagePath = data_photo.path;
                        } else {
                            mBackImagePath = data_photo.path;
                            uploadImageFile(Define.IMAGE_TYPE_WEZONE_BACKGROUND, Define.IMAGE_STATUS_UPDATE, mWezone.wezone_id, mBackImagePath);
                        }
                        image_back.setImageBitmap(data_photo.thumbBitmap);
                    }

                } else {
                    getShare().resetPhotoPickerImages();
                }
                break;
            case Define.INTENT_RESULT_EDIT_TEXT:
                if (resultCode == RESULT_OK) {
                    if (mvalue == TITLE_MODE) { // 타이틀이 설정값 받을 시
                        String contents = data.getStringExtra(EditTextActivity.EDITTEXT_CONTENTS);
                        if (mType == WEZONE_TYPE_REGIST) {
                            mWezone.title = contents;
                            edit_wezone_title_textview.setText(mWezone.title);
                        } else {
                            mWezone.title = contents;
                            edit_wezone_title_textview.setText(mWezone.title);
                            sendWeZonePut(Send_PutDataWithValue.FLAG_TITLE, mWezone.title);
                        }
                    }

                    if (mvalue == INTRODUCTION_MODE) {
                        String contents = data.getStringExtra(EditTextActivity.EDITTEXT_CONTENTS);
                        if (mType == WEZONE_TYPE_REGIST) {
                            mWezone.introduction = contents;
                            edit_wezone_introduction_textview.setText(mWezone.introduction);
                        } else {
                            mWezone.introduction = contents;
                            edit_wezone_introduction_textview.setText(mWezone.introduction);
                            sendWeZonePut(Send_PutDataWithValue.FLAG_INTRODUCTION, mWezone.introduction);
                        }
                    }
                }
                break;
            case Define.INTENT_RESULT_NOTIFICATION:
                if (resultCode == RESULT_OK) {

                    //아무것도 설정안하면 사용안함으로

                    if (mType == WEZONE_TYPE_REGIST) {
                        mActionItem_zone_in = (Data_ActionItem) data.getSerializableExtra(INTENT_RESULT_NOTIFICATION_MACTIONITEM);
                        mWezone.zone_in = new Data_ActionItem();

                        mWezone.zone_in.id = mActionItem_zone_in.id;
                        String mMessage = Data_ActionItem.getTitleText(mWezone.zone_in.id);
                        activity_zone_in_text.setText(mMessage);
                    } else {
                        mActionItem_zone_in = (Data_ActionItem) data.getSerializableExtra(INTENT_RESULT_NOTIFICATION_MACTIONITEM);
                        mWezone.zone_in = new Data_ActionItem();
                        mWezone.zone_in.id = mActionItem_zone_in.id;
                        String mMessage = Data_ActionItem.getTitleText(mWezone.zone_in.id);
                        activity_zone_in_text.setText(mMessage);

                        sendWeZonePutWithArray(Send_PutDataWithArray.FLAG_ZONE_IN, mActionItem_zone_in);
                    }
                }
                break;
            case Define.INTENT_RESULT_WEZONE_LOCATION: //위치 인식 방법 설정값 가져옴
                if (resultCode == RESULT_OK) {
                    //location_type = G, B
                    String mdistance = null;
                    Send_PostWezone wezone = new Send_PostWezone();
                    ArrayList<Data_Beacon> selectedItemList = (ArrayList<Data_Beacon>) data.getSerializableExtra(Define.INTENTKEY_BEACONE_VALUE);
                    mActionItem_beacon = new Data_BeaconArray_location_type();

                    value = data.getStringExtra(LOACTIONSELECT);
                    activity_location_choice.setText(value);

                    if (value.equals("WeCON")) {
                        mlocationtype = "B";

                        if (selectedItemList != null) {
                            mBeacons = new ArrayList<>();
                            mBeacons.addAll(selectedItemList);
                            mBeaconsize = mBeacons.size();
                        } else {
                            mBeaconsize = 0;
                        }


                        for (int i = 0; i < mBeacons.size(); i++) {
                            mActionItem_beacon.beacon_id = mBeacons.get(i).beacon_id;
                        }

                        mlocation_data = "";
                        mWezone.location_type = mlocationtype;
                        mWezone.location_data = mlocation_data;
                        mWezone.beacons = mBeacons;
                        //location_type "B", 선택된 비콘을 수정하거나, 등록하는 전문 날려야함.

                        sendWezonePutWithArrayBeacon(Send_PutDataLocationTypeArray.FLAG_BEACON, mActionItem_beacon);

                        sendWeZonePut(Send_PutDataWithArray.FLAG_LOCATION_TYPE, mWezone.location_type);
//                        sendWeZonePutWithArray(Send_PutDataWithValue.FLAG_BEACON, mActionItem_beacon);
                    } else {
                        value = "G";
                        mlocation_data = data.getStringExtra(LOACTIONDATA);
                        mWezone.location_type = mlocationtype;
                        if ("0".equals(mlocation_data) || WezoneUtil.isEmptyStr(mlocation_data)) {
                            mWezone.location_data = mdistance;
                            mdistance = "200";
                        } else {
                            if ("200".equals(mlocation_data)) {
                                mWezone.location_data = mlocation_data;
                                mdistance = "200";
                            } else if ("400".equals(mlocation_data)) {
                                mWezone.location_data = mlocation_data;
                                mdistance = "400";
                            } else if ("1.6".equals(mlocation_data)) {
                                mWezone.location_data = mlocation_data;
                                mdistance = "1600";
                            } else if ("3.2".equals(mlocation_data)) {
                                mWezone.location_data = mlocation_data;
                                mdistance = "3200";
                            }
                        }

                        if (mType == WEZONE_TYPE_EDIT) {
                            sendWeZonePut(Send_PutDataWithValue.FLAG_LOCATION_DATA, mdistance);
                            sendWeZonePut(Send_PutDataWithValue.FLAG_LOCATION_TYPE, mWezone.location_type);
                        }
                    }

                }
                break;
            case Define.INTENT_RESULT_WEZONE_IN:
                if (resultCode == RESULT_OK) {
                    if (mType == WEZONE_TYPE_REGIST) {

                        ArrayList<Data_Board> selectedItemList = (ArrayList<Data_Board>) data.getSerializableExtra(Define.INTENTKEY_NOTICE_VALUE);
                        String off = data.getStringExtra(Define.INTENTKEY_NOTICE_OFF);

                        ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
                        mActionItem_zone_in = new Data_ActionItem();

                        if (off.equals("OFF")) {
                            Data_ActionItemData itemPhone = new Data_ActionItemData();
                            //공지글이 없을 때
                            mActionItem_zone_in.id = Data_ActionItem.ID_NOT_USE;
                            itemDataList.add(itemPhone);
                            mActionItem_zone_in.data = itemDataList;
                            mWezone.zone_in = mActionItem_zone_in;
                            activity_zone_in_text.setText(Data_ActionItem.getTitleText(mActionItem_zone_in.id));
                        } else {
                            //스위치 ON 이면서 selectedItemList 선택된 공지글이 null 일 때
                            if (selectedItemList.size() == 0) {
                                Data_ActionItemData itemPhone = new Data_ActionItemData();
                                //공지글이 없을 때
                                mActionItem_zone_in.id = Data_ActionItem.ID_NOTIC;
//                            itemPhone.key = "board_id";
//                            itemPhone.value = selectedItemList.get(i).board_id;
                                itemDataList.add(itemPhone);
                                mActionItem_zone_in.data = itemDataList;
                                mWezone.zone_in = mActionItem_zone_in;
                                activity_zone_in_text.setText(Data_ActionItem.getTitleText(mActionItem_zone_in.id));
                            } else {
                                //스위치 ON 이면서 selectedItemList 선택된 공지글이 null 이 아닐 때
                                for (int i = 0; i < selectedItemList.size(); i++) {
                                    Data_ActionItemData itemPhone = new Data_ActionItemData();

                                    mActionItem_zone_in.id = Data_ActionItem.ID_NOTIC;
                                    itemPhone.key = "board_id";
                                    itemPhone.value = selectedItemList.get(i).board_id;
                                    itemDataList.add(itemPhone);
                                    mActionItem_zone_in.data = itemDataList;
                                    mWezone.zone_in = mActionItem_zone_in;
                                    activity_zone_in_text.setText(Data_ActionItem.getTitleText(mActionItem_zone_in.id));

                                }
                            }
                        }

                    } else {
                        ArrayList<Data_Board> selectedItemList = (ArrayList<Data_Board>) data.getSerializableExtra(Define.INTENTKEY_NOTICE_VALUE);
                        String off = data.getStringExtra(Define.INTENTKEY_NOTICE_OFF);

                        ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
                        mActionItem_zone_in = new Data_ActionItem();

                        if (off.equals("OFF")) {
                            Data_ActionItemData itemPhone = new Data_ActionItemData();
                            //공지글이 없을 때
                            mActionItem_zone_in.id = Data_ActionItem.ID_NOT_USE;
                            itemDataList.add(itemPhone);
                            mActionItem_zone_in.data = itemDataList;

                            mWezone.zone_in = mActionItem_zone_in;
                            mWezone.zone_in.id = mActionItem_zone_in.id;
                            mWezone.zone_in.data = mActionItem_zone_in.data;

                            sendWeZonePutWithArray(Send_PutDataWithValue.FLAG_ZONE_IN, mActionItem_zone_in);

                            //존in부분
                            activity_zone_in_text.setText(Data_ActionItem.getTitleText(mActionItem_zone_in.id));
                        } else {
                            //스위치 ON 이면서 selectedItemList 선택된 공지글이 null 이 아닐 때
                            if (selectedItemList.size() == 0) {
                                Data_ActionItemData itemPhone = new Data_ActionItemData();

                                mActionItem_zone_in.id = Data_ActionItem.ID_NOTIC;
                                itemPhone.key = "board_id";
                                itemPhone.value = null;
                                itemDataList.add(itemPhone);
                                mActionItem_zone_in.data = itemDataList;
                                mWezone.zone_in = mActionItem_zone_in;
                                activity_zone_in_text.setText(Data_ActionItem.getTitleText(mActionItem_zone_in.id));
                            } else {
                                for (int i = 0; i < selectedItemList.size(); i++) {
                                    Data_ActionItemData itemPhone = new Data_ActionItemData();

                                    mActionItem_zone_in.id = Data_ActionItem.ID_NOTIC;
                                    itemPhone.key = "board_id";
                                    itemPhone.value = selectedItemList.get(i).board_id;
                                    itemDataList.add(itemPhone);
                                    mActionItem_zone_in.data = itemDataList;

                                    mWezone.zone_in = mActionItem_zone_in;
                                    mWezone.zone_in.id = mActionItem_zone_in.id;
                                    mWezone.zone_in.data = mActionItem_zone_in.data;

                                    sendWeZonePutWithArray(Send_PutDataWithValue.FLAG_ZONE_IN, mActionItem_zone_in);

                                    //존in부분
                                    activity_zone_in_text.setText(Data_ActionItem.getTitleText(mActionItem_zone_in.id));
                                }
                            }
                        }
                    }
                }
                break;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        setTransparentHeaderView(); //상단바 설정
    }

    @Override
    public void onClickRightBtn(View v) {


        //GPS 정보를 갖고 등록 가능
        mstrTemp = wezone_title_eidittext.getText().toString().trim();
        if (WezoneUtil.isEmptyStr(mstrTemp)) {
            String str = getResources().getString(R.string.please_enter_your_wezone);
            Toast.makeText(WezoneManagerActivity.this, str, Toast.LENGTH_SHORT).show();
            return;
        }

        mGpsInfo = new GpsInfo(WezoneManagerActivity.this);
        // GPS 사용유무 가져오기
        if (!mGpsInfo.isGetLocation()) {
            // GPS 를 사용할수 없으므로
            Toast.makeText(WezoneManagerActivity.this, "GPS 상태를 체크해주시고 Wezone을 재생성 해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
        } else {
            if (latitude == 0.0 || longitude == 0.0) {

            } else if (latitude == 0 || latitude == 0) {
                Toast.makeText(WezoneManagerActivity.this, "GPS 상태를 체크해주시고 Wezone을 재생성 해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
            } else {
                if (mImagePath != null) {

                            if(CURRENT_TIME!=LAST_TIME){
                                return;
                                //중복 클릭인 경우
                            }
                            //중복 클릭이 아니라면 호출
                            uploadImageFile(Define.IMAGE_TYPE_WEZONE, Define.IMAGE_STATUS_NEW, null, mImagePath);

                } else {

                            if(CURRENT_TIME!=LAST_TIME){
                                return;
                                //중복 클릭인 경우
                            }
                            //중복 클릭이 아니라면 호출
                            uploadBackgroundImage();

                }
            }
        }
        CURRENT_TIME++;
    }

    @Override
    public void uploadResult(String type, Rev_Upload resultData) {
        super.uploadResult(type, resultData);
        // 업로드된 프로필 사진 설정
        if (mType == WEZONE_TYPE_REGIST) { //등록시
            if (Define.IMAGE_TYPE_WEZONE.equals(type)) {
                mImageUrl = resultData.url;
                uploadBackgroundImage();
            } else {
                mBackImageUrl = resultData.url;
                sendWeZonePost();
            }
        } else { //수정시
            if (Define.IMAGE_TYPE_WEZONE.equals(type)) {
                mWezone.img_url = resultData.url;
                sendWeZonePut(Send_PutDataWithValue.FLAG_IMG_URL, mWezone.img_url);
            } else {
                mWezone.bg_img_url = resultData.url;
                sendWeZonePut(Send_PutDataWithValue.FLAG_BG_IMG_URL, mWezone.bg_img_url);
            }

        }
    }

    public void uploadBackgroundImage() {
        if (mBackImagePath != null) { // 업로드된 배경사진 설정
            uploadImageFile(Define.IMAGE_TYPE_WEZONE_BACKGROUND, Define.IMAGE_STATUS_NEW, null, mBackImagePath);
        } else {
            sendWeZonePost();
        }
    }


    private void sendWezonePutWithArrayBeacon(String flag, Data_BeaconArray_location_type val) {
        //위치인식 비콘 전문
        Send_PutDataLocationTypeArray tempPutData = new Send_PutDataLocationTypeArray();
        tempPutData.flag = flag;
        tempPutData.val = new ArrayList<>();
        tempPutData.val.add(val);

        Send_PutWezoneWithArrayBeacon putWezoneWithArrayBeacon = new Send_PutWezoneWithArrayBeacon();

        putWezoneWithArrayBeacon.wezone_id = mWezone.wezone_id;
        putWezoneWithArrayBeacon.wezone_info = new ArrayList<>();
        putWezoneWithArrayBeacon.wezone_info.add(tempPutData);

        Call<Rev_Base> PutWezoneWithArrayCall = wezoneRestful.putWezoneWithArrayBeacon(putWezoneWithArrayBeacon);
        PutWezoneWithArrayCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });

    }


    private void sendWeZonePutWithArray(String flag, Data_ActionItem val) {
        //공지글 전문
        Send_PutDataWithArray tempPutData = new Send_PutDataWithArray();
        tempPutData.flag = flag;
        tempPutData.val = new ArrayList<>();
        tempPutData.val.add(val);

        Send_PutWezoneWithArray putWezoneWithArray = new Send_PutWezoneWithArray();


        putWezoneWithArray.wezone_id = mWezone.wezone_id;
        putWezoneWithArray.wezone_info = new ArrayList<>();
        putWezoneWithArray.wezone_info.add(tempPutData);

        Call<Rev_Base> PutWezoneWithArrayCall = wezoneRestful.putWezoneWithArray(putWezoneWithArray);
        PutWezoneWithArrayCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {

            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });

    }

    private void sendWeZonePut(String flag, String val) {
        //수정 전문
        Send_PutWezone putWezone = new Send_PutWezone();

        ArrayList<Send_PutDataWithValue> putDataList = new ArrayList<>();
        Send_PutDataWithValue putData = new Send_PutDataWithValue();

        putWezone.wezone_id = mWezone.wezone_id;

        putData.flag = flag;
        putData.val = val;
        putDataList.add(putData);
        putWezone.wezone_info = putDataList;

        Call<Rev_Base> PutWezoneCall = wezoneRestful.putWezone(putWezone);
        PutWezoneCall.enqueue(new Callback<Rev_Base>() {

            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {

            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });
    }

    private void sendWeZonePost() {
        //등록 전문
        String people = activity_regist_wezone_people_textview.getText().toString().trim();
        switch (people) {

            case "100명":
                people = "100";
                break;
            case "50명":
                people = "50";
                break;
            case "10명":
                people = "10";
                break;

        }

        Send_PostWezone wezone = new Send_PostWezone();
        String wezone_title;
        String wezone_introduction;
        String mdistance = null;

        wezone_title = wezone_title_eidittext.getText().toString().trim();
        wezone_introduction = wezone_intru_eidittext.getText().toString().trim();

        //챗방 갱신을 위한 추가
        mWezone.title = wezone_title;
        mWezone.img_url = mImageUrl;

        wezone.title = wezone_title;
        wezone.introduction = wezone_introduction;
        wezone.img_url = mImageUrl;
        wezone.bg_img_url = mBackImageUrl;
        wezone.longitude = Double.toString(longitude);
        wezone.latitude = Double.toString(latitude);
        wezone.wezone_type = mTB;
        if (mMemberLimit == null) {
            mMemberLimit = "100";
        }
        wezone.member_limit = mMemberLimit;
        wezone.location_type = mlocationtype;
        if (mlocationtype.equals("G")) {
            if ("0".equals(mlocation_data) || WezoneUtil.isEmptyStr(mlocation_data)) {
                wezone.location_data = "200";
            } else {
                if ("1.6".equals(mlocation_data)) {
                    mdistance = "1600";
                } else if ("3.2".equals(mlocation_data)) {
                    mdistance = "3200";
                }
                wezone.location_data = mdistance;
            }
        }
        wezone.push = alarm;
        if (mlocationtype.equals("B")) {
            if (mBeacons != null && mBeacons.size() > 0) {
                ArrayList<Data_Beacon_id> beaconIds = new ArrayList<>();
                Data_Beacon_id data_beacon = new Data_Beacon_id();
                for (Data_Beacon beacon : mBeacons) {
                    data_beacon.beacon_id = beacon.beacon_id;
                    beaconIds.add(data_beacon);
                }
                wezone.location_data = "";
                wezone.beacon = beaconIds;
            }
        }
        if (mActionItem_zone_in != null) {
            ArrayList<Data_ActionItem> zone_in_Item = new ArrayList<>();
            zone_in_Item.add(mActionItem_zone_in);
            wezone.zone_in = zone_in_Item;
        } else {
            ArrayList<Data_ActionItem> items = new ArrayList<>();
            mActionItem_zone_in = new Data_ActionItem();
            mActionItem_zone_in.id = Data_ActionItem.ID_NOT_USE;
            items.add(mActionItem_zone_in);
            wezone.zone_in = items;
        }


        showProgressPopup();
        Call<Rev_WeZone> PostWezoneCall = wezoneRestful.postWeZone(wezone);
        PostWezoneCall.enqueue(new Callback<Rev_WeZone>() {

            @Override
            public void onResponse(Call<Rev_WeZone> call, Response<Rev_WeZone> response) {
                Rev_WeZone rev_Data = response.body();

                if (isNetSuccess(rev_Data)) {
                    if (getShare().getServerInfo().groupchat_hostname != null) {
                        Data_Chat_UserList dataChatUserList = new Data_Chat_UserList();
                        dataChatUserList.other_uuid = rev_Data.wezone_id + "@" + getShare().getServerInfo().groupchat_hostname;
                        dataChatUserList.img_url = mWezone.img_url;
                        dataChatUserList.user_name = mWezone.title;
                        dataChatUserList.kind = "groupchat";
                        dataChatUserList.push_flag = "T";
                        dataChatUserList.unread = new Data_UnRead();
                        dataChatUserList.unread.msgkeys = new ArrayList<Data_MsgKey>();
                        dataChatUserList.member_count = "1";
                        getShare().addUnRead(dataChatUserList);

                        try {
                            LibXmppManager.getInstance().createGroupChat(dataChatUserList.other_uuid, getUuid());
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                        LibXmppManager.getInstance().joinRoom(dataChatUserList.other_uuid);

                        Intent i = new Intent();
                        i.putExtra(WEZONE_WEZONE_PUT, mWezone);
                        i.putExtra(WEZONE_WEZONE_PUT_TYPE, POST);
                        i.putExtra(Define.SHARE_KEY_WEZONE_ID, rev_Data.wezone_id);
                        setResult(RESULT_OK, i);
                        finish();
                    }

                    hidePorgressPopup();
                    CURRENT_TIME = 0;
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Rev_WeZone> call, Throwable t) {
                hidePorgressPopup();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //뒤로가기
        if (mType == WEZONE_TYPE_EDIT) {
            setEditResultData();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClickLeftBtn(View v) {
                if (mType == WEZONE_TYPE_EDIT) {
                    setEditResultData();
                } else {
                    if(mActivity != null) {
                        mActivity.onBackPressed();
                    }
                }
    }

    public void setEditResultData() {
        Intent i = new Intent();
        i.putExtra(WEZONE_WEZONE_PUT, "mWezone");
        i.putExtra(WEZONE_EDIT, mWezone); //수정된 설정값 전달
        setResult(RESULT_OK, i);
        super.onBackPressed();

    }

}


