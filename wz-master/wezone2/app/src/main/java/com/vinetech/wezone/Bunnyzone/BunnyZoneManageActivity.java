package com.vinetech.wezone.Bunnyzone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Beacon.BeaconSelectActivity;
import com.vinetech.wezone.Common.Activity_PhotoPicker_Folder;
import com.vinetech.wezone.Common.EditTextActivity;
import com.vinetech.wezone.Common.SelectNotificationActivity;
import com.vinetech.wezone.Data.Data_ActionItem;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_BunnyZone;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Data.Data_Theme;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Profile.UserSelectActivity;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_PostBunnyZone;
import com.vinetech.wezone.RevPacket.Rev_Upload;
import com.vinetech.wezone.SendPacket.Send_PostBunnyZone;
import com.vinetech.wezone.Theme.ThemeSelectActivity;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BunnyZoneManageActivity extends BaseActivity {

    public static final String BUNNYZONE_TYPE = "type";
    public static final String BUNNYZONE_THEME = "theme";
    public static final String BUNNYZONE_BUNNYZONE = "bunnyzone";

    public static final int BUNNYZONE_TYPE_REGIST = 0;
    public static final int BUNNYZONE_TYPE_EDIT = 1;

    public static final String INTENT_RESULT_NOTIFICATION_MACTIONITEM = "mActionItem";

    public static void startActivityWithRegist(BaseActivity activity) {
        Intent intent = new Intent(activity, BunnyZoneManageActivity.class);
        intent.putExtra(BUNNYZONE_TYPE,BUNNYZONE_TYPE_REGIST);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_BUNNYZONE);
    }

    public static void startActivityWithRegist(BaseActivity activity, Data_Theme themeInfo) {
        Intent intent = new Intent(activity, BunnyZoneManageActivity.class);
        intent.putExtra(BUNNYZONE_TYPE,BUNNYZONE_TYPE_REGIST);
        intent.putExtra(BUNNYZONE_THEME,themeInfo);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_BUNNYZONE);
    }

    public static void startActivityWithEdit(BaseActivity activity, Data_BunnyZone bunnyZone) {
        Intent intent = new Intent(activity, BunnyZoneManageActivity.class);
        intent.putExtra(BUNNYZONE_TYPE,BUNNYZONE_TYPE_EDIT);
        intent.putExtra(BUNNYZONE_BUNNYZONE,bunnyZone);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_BUNNYZONE);
    }

    public static final int SELECTED_NEAR = 0;
    public static final int SELECTED_MID = 1;
    public static final int SELECTED_FAR = 2;

    private int mSelectedBtn = SELECTED_NEAR;

    private int mType = BUNNYZONE_TYPE_REGIST;

    private Data_BunnyZone mBunnyzone;

    private Data_Theme mSelectedTheme;

    private ArrayList<Data_Beacon> mBeacons;

    private LinearLayout linearlayout_header_bg;

    private RelativeLayout relativelayout_profile;
    private ImageView imageview_profile;


    private LinearLayout linearlayout_regist_title_area;
    private LinearLayout linearlayout_edit_title_area;

    private EditText edittext_title;
    private TextView textview_title;

    private LinearLayout linearlayout_btn_theme;
    private TextView textview_theme_name;
    private ImageView imageview_theme_icon;

    private LinearLayout linearlayout_btn_beacon;
    private LinearLayout linearlayout_beacon_area;
    private LinearLayout linearlayout_btn_push;

    private LinearLayout linearlayout_btn_set_01;
    private TextView textview_desc_01;
    private LinearLayout linearlayout_btn_set_02;
    private TextView textview_desc_02;
    private LinearLayout linearlayout_btn_set_03;
    private TextView textview_desc_03;

    private LinearLayout linearlayout_btn_share;

    private String mImagePath;

    private ArrayList<Data_ActionItem> mNearItem;
    private ArrayList<Data_ActionItem> mMidItem;
    private ArrayList<Data_ActionItem> mFarItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bunny_zone_manage);

        mType = getIntent().getIntExtra(BUNNYZONE_TYPE,BUNNYZONE_TYPE_REGIST);
        mSelectedTheme = (Data_Theme) getIntent().getSerializableExtra(BUNNYZONE_THEME);

        linearlayout_header_bg = (LinearLayout) findViewById(R.id.linearlayout_header_bg);
        linearlayout_header_bg.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));

        relativelayout_profile = (RelativeLayout) findViewById(R.id.relativelayout_profile);
        relativelayout_profile.setOnClickListener(mClickListener);

        imageview_profile = (ImageView) findViewById(R.id.imageview_profile);

        linearlayout_regist_title_area = (LinearLayout) findViewById(R.id.linearlayout_regist_title_area);
        linearlayout_edit_title_area = (LinearLayout) findViewById(R.id.linearlayout_edit_title_area);

        edittext_title = (EditText) findViewById(R.id.edittext_title);
        textview_title = (TextView) findViewById(R.id.textview_title);

        linearlayout_btn_theme = (LinearLayout) findViewById(R.id.linearlayout_btn_theme);
        linearlayout_btn_theme.setOnClickListener(mClickListener);

        textview_theme_name = (TextView) findViewById(R.id.textview_theme_name);

        imageview_theme_icon = (ImageView) findViewById(R.id.imageview_theme_icon);

        linearlayout_btn_beacon = (LinearLayout) findViewById(R.id.linearlayout_btn_beacon);
        linearlayout_btn_beacon.setOnClickListener(mClickListener);

        linearlayout_beacon_area = (LinearLayout) findViewById(R.id.linearlayout_beacon_area);

        linearlayout_btn_push = (LinearLayout) findViewById(R.id.linearlayout_btn_push);
        linearlayout_btn_push.setOnClickListener(mClickListener);

        linearlayout_btn_set_01 = (LinearLayout) findViewById(R.id.linearlayout_btn_set_01);
        linearlayout_btn_set_01.setOnClickListener(mClickListener);
        textview_desc_01 = (TextView) findViewById(R.id.textview_desc_01);
        linearlayout_btn_set_02 = (LinearLayout) findViewById(R.id.linearlayout_btn_set_02);
        linearlayout_btn_set_02.setOnClickListener(mClickListener);
        textview_desc_02 = (TextView) findViewById(R.id.textview_desc_02);
        linearlayout_btn_set_03 = (LinearLayout) findViewById(R.id.linearlayout_btn_set_03);
        linearlayout_btn_set_03.setOnClickListener(mClickListener);
        textview_desc_03 = (TextView) findViewById(R.id.textview_desc_03);

        linearlayout_btn_share = (LinearLayout) findViewById(R.id.linearlayout_btn_share);
        linearlayout_btn_share.setOnClickListener(mClickListener);

        mNearItem = new ArrayList<>();
        mMidItem = new ArrayList<>();
        mFarItem = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();

        reloadLayout();

    }

    public void reloadLayout(){
        String title = getResources().getString(R.string.bunnyzone_title);

        if(mSelectedTheme != null){

            textview_theme_name.setText(mSelectedTheme.name);

            imageview_theme_icon.setBackgroundResource(WezoneUtil.getThemeIconBgResId(mCurrentTheme,mSelectedTheme.resIdPos));
            if(WezoneUtil.isEmptyStr(mSelectedTheme.img_url) == false){
                showImageFromRemote(mSelectedTheme.img_url,R.drawable.ic_bunny_image,imageview_theme_icon);
            }

//            textview_desc_01.setText(Data_ActionItem.getTitleText(mSelectedTheme.near_id.id));
//            textview_desc_02.setText(Data_ActionItem.getTitleText(mSelectedTheme.mid_id.id));
//            textview_desc_03.setText(Data_ActionItem.getTitleText(mSelectedTheme.far_id.id));
        }

        if(mType == BUNNYZONE_TYPE_REGIST){
            setHeaderView(R.drawable.btn_back_white,title,R.drawable.btn_check);

            linearlayout_regist_title_area.setVisibility(View.VISIBLE);
            linearlayout_edit_title_area.setVisibility(View.GONE);

            linearlayout_beacon_area.removeAllViews();
            if(mBeacons != null){
                for(Data_Beacon beacon : mBeacons){
                    View beaconLayout = getLayoutInflater().inflate(R.layout.beacon_image, null, false);
                    ImageView imageview_beacon_profile = (ImageView)beaconLayout.findViewById(R.id.imageview_beacon_profile);
                    if(WezoneUtil.isEmptyStr(beacon.img_url) == false){
                        showImageFromRemote(beacon.img_url,R.drawable.im_beacon_add,imageview_beacon_profile);
                    }else{
                        imageview_beacon_profile.setImageResource(R.drawable.im_beacon_add);
                    }
                    linearlayout_beacon_area.addView(beaconLayout);
                }
            }
        }else{
            setHeaderView(R.drawable.btn_back_white,title + " 수정",0);

            mBunnyzone = (Data_BunnyZone) getIntent().getSerializableExtra(BUNNYZONE_BUNNYZONE);

            linearlayout_regist_title_area.setVisibility(View.GONE);
            linearlayout_edit_title_area.setVisibility(View.VISIBLE);
            linearlayout_edit_title_area.setOnClickListener(mClickListener);
            textview_title.setText(mBunnyzone.title);

            if("T".equals(mBunnyzone.push_flag)){
                linearlayout_btn_push.setSelected(true);
            }else{
                linearlayout_btn_push.setSelected(false);
            }

            if(WezoneUtil.isEmptyStr(mBunnyzone.theme_id)){
                textview_theme_name.setText("사용안함");
                imageview_theme_icon.setVisibility(View.GONE);
            }else{
                textview_theme_name.setText(mBunnyzone.theme_name);
            }

            linearlayout_beacon_area.removeAllViews();
            if(mBunnyzone.beacons != null){
                for(Data_Beacon beacon : mBunnyzone.beacons){
                    View beaconLayout = getLayoutInflater().inflate(R.layout.beacon_image, null, false);
                    ImageView imageview_beacon_profile = (ImageView)beaconLayout.findViewById(R.id.imageview_beacon_profile);
                    if(WezoneUtil.isEmptyStr(beacon.img_url) == false){
                        showImageFromRemote(beacon.img_url,R.drawable.im_beacon_add,imageview_beacon_profile);
                    }else{
                        imageview_beacon_profile.setImageResource(R.drawable.im_beacon_add);
                    }
                    linearlayout_beacon_area.addView(beaconLayout);
                }
            }
        }
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId){
                case R.id.relativelayout_profile:
                    Activity_PhotoPicker_Folder.startFolderActivityForResult(BunnyZoneManageActivity.this, 1);
                    break;

                case R.id.linearlayout_edit_title_area:
                    EditTextActivity.startActivity(BunnyZoneManageActivity.this,"타이틀 입력",mBunnyzone.title);
                    break;

                case R.id.linearlayout_btn_beacon:
                    moveActivityForResult(new Intent(BunnyZoneManageActivity.this, BeaconSelectActivity.class),Define.INTENT_RESULT_BEACONE);
                    break;

                case R.id.linearlayout_btn_theme:
                    ThemeSelectActivity.startActivity(BunnyZoneManageActivity.this,mSelectedTheme);
                    break;

                case R.id.linearlayout_btn_push:
                    if(linearlayout_btn_push.isSelected()){
                        linearlayout_btn_push.setSelected(false);
                    }else{
                        linearlayout_btn_push.setSelected(true);
                    }
                    break;

                case R.id.linearlayout_btn_set_01:
                    mSelectedBtn = SELECTED_NEAR;
                    if(mType == BUNNYZONE_TYPE_REGIST){
                        if(mSelectedTheme != null){
//                            SelectNotificationActivity.startActivity(BunnyZoneManageActivity.this,"1미터 이내", mSelectedTheme.near_id);
                        }else {
                            SelectNotificationActivity.startActivity(BunnyZoneManageActivity.this,"1미터 이내", null);
                        }
                    }else{
                        SelectNotificationActivity.startActivity(BunnyZoneManageActivity.this,"1미터 이내", mBunnyzone.near_id);
                    }

                    break;

                case R.id.linearlayout_btn_set_02:
                    mSelectedBtn = SELECTED_MID;
                    if(mType == BUNNYZONE_TYPE_REGIST){
                        if(mSelectedTheme != null){
//                            SelectNotificationActivity.startActivity(BunnyZoneManageActivity.this,"10미터 이내", mSelectedTheme.mid_id);
                        }else{
                            SelectNotificationActivity.startActivity(BunnyZoneManageActivity.this,"10미터 이내", null);
                        }
                    }else{
                        SelectNotificationActivity.startActivity(BunnyZoneManageActivity.this,"10미터 이내", mBunnyzone.mid_id);
                    }
                    break;

                case R.id.linearlayout_btn_set_03:
                    mSelectedBtn = SELECTED_FAR;
                    if(mType == BUNNYZONE_TYPE_REGIST){
                        if(mSelectedTheme != null){
//                            SelectNotificationActivity.startActivity(BunnyZoneManageActivity.this,"10미터 이상", mSelectedTheme.far_id);
                        }else{
                            SelectNotificationActivity.startActivity(BunnyZoneManageActivity.this,"10미터 이상", null);
                        }
                    }else{
                        SelectNotificationActivity.startActivity(BunnyZoneManageActivity.this,"10미터 이상", mBunnyzone.far_id);
                    }
                    break;

                case R.id.linearlayout_btn_share:
                    if(mType == BUNNYZONE_TYPE_REGIST){
                        UserSelectActivity.startActivity(BunnyZoneManageActivity.this,null,null);
                    }else{
                        UserSelectActivity.startActivityAndShowShare(BunnyZoneManageActivity.this,null);
                    }
                    break;
            }
        }
    };


    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);

        if(mImagePath != null){
            if(mType == BUNNYZONE_TYPE_REGIST){
                uploadImageFile(Define.IMAGE_TYPE_BUNNYZONE,Define.IMAGE_STATUS_NEW,null,mImagePath);
            }else{
                uploadImageFile(Define.IMAGE_TYPE_BUNNYZONE,Define.IMAGE_STATUS_UPDATE,mBunnyzone.zone_id,mImagePath);
            }
        }else{
            sendBunnyZonePost(null);
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
                    mImagePath = data_photo.path;
                    imageview_profile.setImageBitmap(data_photo.thumbBitmap);

                } else {
                    getShare().resetPhotoPickerImages();
                }
                break;

            case Define.INTENT_RESULT_BEACONE:
                if(resultCode == RESULT_OK){
                    ArrayList<Data_Beacon> selectedItemList = (ArrayList<Data_Beacon>)data.getSerializableExtra(Define.INTENTKEY_BEACONE_VALUE);
                    mBeacons = new ArrayList<>();
                    mBeacons.addAll(selectedItemList);
                    reloadLayout();
                }
                break;

            case Define.INTENT_RESULT_THEME:
                if(resultCode == RESULT_OK){
                    mSelectedTheme = (Data_Theme) data.getSerializableExtra(Define.INTENTKEY_THEME_VALUE);
                    reloadLayout();
                }
                break;

            case Define.INTENT_RESULT_EDIT_TEXT:
                if(resultCode == RESULT_OK){
                    String contents = data.getStringExtra(EditTextActivity.EDITTEXT_CONTENTS);
                    mBunnyzone.title = contents;
                    textview_title.setText(mBunnyzone.title);
                }
                break;

            case Define.INTENT_RESULT_NOTIFICATION:
                if(resultCode == RESULT_OK){

                    //Data_ActionItem item = (Data_ActionItem)data.getSerializableExtra(SelectNotificationActivity.NOTICE_ACTION_ITEM);
                    Data_ActionItem item = (Data_ActionItem)data.getSerializableExtra(INTENT_RESULT_NOTIFICATION_MACTIONITEM);
                    String mMessage = Data_ActionItem.getTitleText(item.id);
//
//                    mBunnyzone = (Data_BunnyZone) getIntent().getSerializableExtra(INTENT_RESULT_NOTIFICATION_MACTIONITEM);
//                    mSelectedTheme = (Data_Theme) getIntent().getSerializableExtra(INTENT_RESULT_NOTIFICATION_MACTIONITEM);
//                    Data_ActionItem near_id = new Data_ActionItem();
//

                    switch (mSelectedBtn){
                        case SELECTED_NEAR:
                            mNearItem.add(item);
//
//                            near_id.id = item.id;
//                            mBunnyzone.near_id.id=item.id;
                            textview_desc_01.setText(mMessage);
                            break;
                        case SELECTED_MID:
                            mMidItem.add(item);
//                            mSelectedTheme.near_id.id = item.id;
//                            mBunnyzone.near_id.id=item.id;
                            textview_desc_02.setText(mMessage);
                            break;

                        case SELECTED_FAR:
                            mFarItem.add(item);
//                            mSelectedTheme.near_id.id = item.id;
//                            mBunnyzone.near_id.id=item.id;
                            textview_desc_03.setText(mMessage);
                            break;
                    }

                }

                break;

        }
    }

    @Override
    public void uploadResult(String type, Rev_Upload resultData) {
        super.uploadResult(type,resultData);
        sendBunnyZonePost(resultData.url);
    }

    public void sendBunnyZonePost(String img_url){

        Send_PostBunnyZone bunnyzone = new Send_PostBunnyZone();
        bunnyzone.img_url = img_url;
        bunnyzone.title = edittext_title.getText().toString().trim();

        if(mSelectedTheme != null){
            bunnyzone.theme_id = mSelectedTheme.theme_id;
            bunnyzone.theme_name = mSelectedTheme.name;
        }

        if(linearlayout_btn_push.isSelected()){
            bunnyzone.push = "T";
        }else{
            bunnyzone.push = "F";
        }

        if(mNearItem == null && mNearItem.size() == 0){
            Data_ActionItem item = new Data_ActionItem();
            item.id = Data_ActionItem.ID_NOT_USE;
            mNearItem.add(item);
        }

        if(mMidItem == null && mMidItem.size() == 0){
            Data_ActionItem item = new Data_ActionItem();
            item.id = Data_ActionItem.ID_NOT_USE;
            mMidItem.add(item);
        }

        if(mFarItem == null && mFarItem.size() == 0){
            Data_ActionItem item = new Data_ActionItem();
            item.id = Data_ActionItem.ID_NOT_USE;
            mFarItem.add(item);
        }

        bunnyzone.near_id = mNearItem;
        bunnyzone.mid_id = mMidItem;
        bunnyzone.far_id = mFarItem;

        if(mType == BUNNYZONE_TYPE_REGIST){
            if(mBeacons != null && mBeacons.size() >0){
                ArrayList<HashMap<String,String>> beaconIds = new ArrayList<>();
                for(Data_Beacon beacon: mBeacons){
                    HashMap<String,String> beaconMap = new HashMap<>();
                    beaconMap.put("beacon_id",beacon.beacon_id);
                    beaconIds.add(beaconMap);
                }
                bunnyzone.beacon = beaconIds;
            }
        }else{
            if(mBunnyzone.beacons != null && mBunnyzone.beacons.size() > 0){
                ArrayList<HashMap<String,String>> beaconIds = new ArrayList<>();
                for(Data_Beacon beacon: mBunnyzone.beacons){
                    HashMap<String,String> beaconMap = new HashMap<>();
                    beaconMap.put("beacon_id",beacon.beacon_id);
                    beaconIds.add(beaconMap);
                }
                bunnyzone.beacon = beaconIds;
            }
        }

        showProgressPopup();
        Call<Rev_PostBunnyZone> postBunnyZoneCall = wezoneRestful.postBunnyZone(bunnyzone);
        postBunnyZoneCall.enqueue(new Callback<Rev_PostBunnyZone>() {
            @Override
            public void onResponse(Call<Rev_PostBunnyZone> call, Response<Rev_PostBunnyZone> response) {
                hidePorgressPopup();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(Call<Rev_PostBunnyZone> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }
}
