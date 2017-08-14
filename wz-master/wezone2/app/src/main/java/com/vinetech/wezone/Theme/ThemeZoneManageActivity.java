package com.vinetech.wezone.Theme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Beacon.BeaconSelectActivity;
import com.vinetech.wezone.Common.EditTextActivity;
import com.vinetech.wezone.Common.SelectNotificationActivity;
import com.vinetech.wezone.Data.Data_ActionItem;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Data.Data_Theme;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_Upload;
import com.vinetech.wezone.SendPacket.Send_PostThemeZone;
import com.vinetech.wezone.SendPacket.Send_PutBase;
import com.vinetech.wezone.SendPacket.Send_PutDataWithValue;
import com.vinetech.wezone.SendPacket.Send_PutThemeZone;
import com.vinetech.wezone.SendPacket.Send_PutThemeZoneArrayData;
import com.vinetech.wezone.SendPacket.Send_PutThemeZoneWithArray;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vinetech.wezone.SendPacket.Send_PutBase.FLAG_BEACON;
import static com.vinetech.wezone.Theme.ThemeDetailActivity.THEME;


public class ThemeZoneManageActivity extends BaseActivity {

    public static final String THEMEZONE_TYPE = "type";
    public static final String THEMEZONE = "bunnyzone";

    public static final int THEMEZONE_TYPE_REGIST = 0;
    public static final int THEMEZONE_TYPE_EDIT = 1;


    public static void startActivityWithRegist(BaseActivity activity, Data_Theme themeInfo) {
        Intent intent = new Intent(activity, ThemeZoneManageActivity.class);
        intent.putExtra(THEMEZONE_TYPE,THEMEZONE_TYPE_REGIST);
        intent.putExtra(THEMEZONE,themeInfo);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_THEME_ZONE);
    }

    public static void startActivityWithEdit(BaseActivity activity, Data_Theme themeInfo) {
        Intent intent = new Intent(activity, ThemeZoneManageActivity.class);
        intent.putExtra(THEMEZONE_TYPE,THEMEZONE_TYPE_EDIT);
        intent.putExtra(THEMEZONE,themeInfo);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_THEME_ZONE);
    }


    public static final int SELECTED_IN = 0;
    public static final int SELECTED_OUT = 1;

    private int mSelectedBtn = SELECTED_IN;

    private int mType = THEMEZONE_TYPE_REGIST;

    private Data_Theme mSelectedTheme;

    private ListView listview;
    private BeaconListAdapter mBeaconListAdapter;

    private RelativeLayout relativelayout_header_bg;

    private LinearLayout linearlayout_btn_add_photo_bg;
    private ImageView imageview_bg;

    private ImageView imageview_theme_icon;

    private LinearLayout linearlayout_edit_title_area;
    private TextView textview_title;

    private LinearLayout linearlayout_btn_push;

    private LinearLayout linearlayout_theme_area_01;
    private TextView textview_theme_name_01;
    private TextView textview_desc_01;

    private LinearLayout linearlayout_theme_area_02;
    private TextView textview_theme_name_02;
    private TextView textview_desc_02;

    private LinearLayout linearlayout_beacon_area;
    private TextView textview_beacon_cnt;

    private ArrayList<Data_Beacon> mBeacons;
    private ArrayList<Data_ActionItem> mInItem;
    private ArrayList<Data_ActionItem> mOutItem;

    private String mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_zone_manage);

        mType = getIntent().getIntExtra(THEMEZONE_TYPE,THEMEZONE_TYPE_REGIST);
        mSelectedTheme = (Data_Theme) getIntent().getSerializableExtra(THEMEZONE);

        if(mType == THEMEZONE_TYPE_REGIST){
            setHeaderView(R.drawable.btn_back_white, null, R.drawable.btn_check_white);
        }else{
            setHeaderView(R.drawable.btn_back_white, null, 0);
        }

        listview = (ObservableListView) findViewById(R.id.listview);
        View paddingView = getLayoutInflater().inflate(R.layout.themezone_manager_header, null);
        paddingView.setClickable(true);
        listview.addHeaderView(paddingView);

        mBeacons = new ArrayList<>();

        if(mType == THEMEZONE_TYPE_EDIT){
            if(mSelectedTheme.beacons != null){
                mBeacons.addAll(mSelectedTheme.beacons);
            }
        }

        mBeaconListAdapter = new BeaconListAdapter(ThemeZoneManageActivity.this,mBeacons);
        listview.setAdapter(mBeaconListAdapter);

        relativelayout_header_bg = (RelativeLayout) findViewById(R.id.relativelayout_header_bg);
        relativelayout_header_bg.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));

        imageview_bg = (ImageView) findViewById(R.id.imageview_bg);

        if(WezoneUtil.isEmptyStr(mSelectedTheme.bg_img_url) == false){
            showImageFromRemote(mSelectedTheme.bg_img_url,0,imageview_bg);
        }

        linearlayout_btn_add_photo_bg = (LinearLayout) findViewById(R.id.linearlayout_btn_add_photo_bg);
        linearlayout_btn_add_photo_bg.setOnClickListener(mClickListener);

        imageview_theme_icon = (ImageView) findViewById(R.id.imageview_theme_icon);

        linearlayout_edit_title_area = (LinearLayout) findViewById(R.id.linearlayout_edit_title_area);
        linearlayout_edit_title_area.setOnClickListener(mClickListener);

        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_title.setText(mSelectedTheme.name);

        imageview_theme_icon.setBackgroundResource(WezoneUtil.getThemeIconBgResId(mCurrentTheme,mSelectedTheme.resIdPos));

        if(WezoneUtil.isEmptyStr(mSelectedTheme.img_url) == false){
            showImageFromRemote(mSelectedTheme.img_url,0,imageview_theme_icon);
        }

        linearlayout_btn_push = (LinearLayout) findViewById(R.id.linearlayout_btn_push);
        linearlayout_btn_push.setOnClickListener(mClickListener);

        if(mType  == THEMEZONE_TYPE_EDIT){

            if("T".equals(mSelectedTheme.push_flag)){
                linearlayout_btn_push.setSelected(true);
            }else{
                linearlayout_btn_push.setSelected(false);
            }
        }


        linearlayout_theme_area_01 = (LinearLayout) findViewById(R.id.linearlayout_theme_area_01);
        linearlayout_theme_area_01.setOnClickListener(mClickListener);

        textview_theme_name_01 = (TextView) findViewById(R.id.textview_theme_name_01);
        textview_desc_01 = (TextView) findViewById(R.id.textview_desc_01);
        if(mSelectedTheme.theme_in != null){
            textview_theme_name_01.setText(mSelectedTheme.theme_in.name);
            textview_desc_01.setText(Data_ActionItem.getTitleText(mSelectedTheme.theme_in.id));
        }else{
            linearlayout_theme_area_01.setVisibility(View.GONE);
        }

        linearlayout_theme_area_02 = (LinearLayout) findViewById(R.id.linearlayout_theme_area_02);
        linearlayout_theme_area_02.setOnClickListener(mClickListener);

        textview_theme_name_02 = (TextView) findViewById(R.id.textview_theme_name_02);
        textview_desc_02 = (TextView) findViewById(R.id.textview_desc_02);
        if(mSelectedTheme.theme_out != null) {
            textview_theme_name_02.setText(mSelectedTheme.theme_out.name);
            textview_desc_02.setText(Data_ActionItem.getTitleText(mSelectedTheme.theme_out.id));
        }else{
            linearlayout_theme_area_02.setVisibility(View.GONE);
        }

        linearlayout_beacon_area = (LinearLayout) findViewById(R.id.linearlayout_beacon_area);
        linearlayout_beacon_area.setOnClickListener(mClickListener);

        textview_beacon_cnt = (TextView) findViewById(R.id.textview_beacon_cnt);

        mInItem = new ArrayList<>();
        mOutItem = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTransparentHeaderView();

    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            switch (viewId){

                case R.id.linearlayout_btn_push:

                    if(linearlayout_btn_push.isSelected()){
                        linearlayout_btn_push.setSelected(false);
                    }else{
                        linearlayout_btn_push.setSelected(true);
                    }

                    if(mType == THEMEZONE_TYPE_EDIT){
                        String strYn = linearlayout_btn_push.isSelected() ? "Y" : "N";
                        sendThemeZonePutWithValue("push_flag",strYn);
                        mSelectedTheme.push_flag = strYn;
                    }

                    break;


                case R.id.linearlayout_edit_title_area:
                    EditTextActivity.startActivity(ThemeZoneManageActivity.this,"타이틀 입력",mSelectedTheme.name);
                    break;


                case R.id.linearlayout_beacon_area:
                    if(mType == THEMEZONE_TYPE_REGIST){
                        BeaconSelectActivity.startActivityWithData(ThemeZoneManageActivity.this, mBeacons);
                    }else{
                        BeaconSelectActivity.startActivityWithData(ThemeZoneManageActivity.this, mBeacons);
                    }

                    break;

            }
        }
    };

    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);

//        if(mImagePath != null){
//            if(mType == BUNNYZONE_TYPE_REGIST){
//                uploadImageFile(Define.IMAGE_TYPE_THEMEZONE,Define.IMAGE_STATUS_NEW,null,mImagePath);
//            }else{
//                uploadImageFile(Define.IMAGE_TYPE_THEMEZONE,Define.IMAGE_STATUS_UPDATE,mSelectedTheme.theme_id.zone_id,mImagePath);
//            }
//        }else{
//            sendBunnyZonePost(null);
//        }
        sendThemeZonePost(null);
    }

    @Override
    public void onClickLeftBtn(View v) {
        super.onClickLeftBtn(v);
        gotoBackWithData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotoBackWithData();
    }

    public void gotoBackWithData(){
        if(mType == THEMEZONE_TYPE_EDIT){
            Intent i = new Intent();
            i.putExtra(THEME,mSelectedTheme);
            setResult(RESULT_OK,i);
            finish();
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
                    imageview_bg.setImageBitmap(data_photo.thumbBitmap);

                } else {
                    getShare().resetPhotoPickerImages();
                }
                break;

            case Define.INTENT_RESULT_BEACONE:
                if(resultCode == RESULT_OK){
                    ArrayList<Data_Beacon> selectedItemList = (ArrayList<Data_Beacon>)data.getSerializableExtra(Define.INTENTKEY_BEACONE_VALUE);

                    mBeacons.addAll(selectedItemList);

                    if(mType == THEMEZONE_TYPE_EDIT){
                        if(mSelectedTheme.beacons != null){
                            mSelectedTheme.beacons.clear();
                            mSelectedTheme.beacons.addAll(mBeacons);
                        }
                        sendThemeZonePutWithBeacons();
                    }
                    textview_beacon_cnt.setText("WeCON "+ mBeacons.size());
                    mBeaconListAdapter.notifyDataSetChanged();
                }
                break;


            case Define.INTENT_RESULT_EDIT_TEXT:
                if(resultCode == RESULT_OK){
                    String contents = data.getStringExtra(EditTextActivity.EDITTEXT_CONTENTS);
                    mSelectedTheme.name = contents;
                    textview_title.setText(mSelectedTheme.name);

                    if(mType == THEMEZONE_TYPE_EDIT){
                        sendThemeZonePutWithValue(Send_PutBase.FLAG_TITLE,mSelectedTheme.name);
                    }
                }
                break;

            case Define.INTENT_RESULT_NOTIFICATION:
                if(resultCode == RESULT_OK){
                    Data_ActionItem item = (Data_ActionItem)data.getSerializableExtra(SelectNotificationActivity.INTENT_RESULT_NOTIFICATION_ACTIONITEM);
                    String mMessage = Data_ActionItem.getTitleText(item.id);

                    switch (mSelectedBtn){
                        case SELECTED_IN:
                            mInItem.add(item);
                            mSelectedTheme.theme_in.id = item.id;
                            textview_desc_01.setText(mMessage);
                            break;
                        case SELECTED_OUT:
                            mOutItem.add(item);
                            mSelectedTheme.theme_out.id = item.id;
                            textview_desc_02.setText(mMessage);
                            break;

                    }

                }

                break;

        }
    }


    @Override
    public void uploadResult(String type, Rev_Upload resultData) {
        super.uploadResult(type,resultData);
//        sendThemeZonePost(resultData.url);
    }

    public void sendThemeZonePutWithValue(String flag, String value){

        Send_PutThemeZone param = new Send_PutThemeZone();
        param.theme_id = mSelectedTheme.theme_id;

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

    public void sendThemeZonePutWithBeacons(){

        ArrayList<HashMap<String,String>> beaconIds = new ArrayList<>();
        for(Data_Beacon beacon: mBeacons){
            HashMap<String,String> beaconMap = new HashMap<>();
            beaconMap.put("beacon_id",beacon.beacon_id);
            beaconIds.add(beaconMap);
        }

        Send_PutThemeZoneWithArray param = new Send_PutThemeZoneWithArray();
        param.theme_id = mSelectedTheme.theme_id;

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

    public void sendThemeZonePost(String img_url){

        Send_PostThemeZone themeZone = new Send_PostThemeZone();
        themeZone.img_url = mSelectedTheme.img_url;
        themeZone.bg_img_url = mSelectedTheme.bg_img_url;
        themeZone.title = mSelectedTheme.name;
        themeZone.theme_id = mSelectedTheme.theme_id;

        if(linearlayout_btn_push.isSelected()){
            themeZone.push_flag = "Y";
        }else{
            themeZone.push_flag = "N";
        }

        themeZone.theme_in = new ArrayList<>();
        themeZone.theme_in.add(mSelectedTheme.theme_in);

        themeZone.theme_out = new ArrayList<>();
        themeZone.theme_out.add(mSelectedTheme.theme_out);

        if(mBeacons != null && mBeacons.size() >0){
            ArrayList<HashMap<String,String>> beaconIds = new ArrayList<>();
            for(Data_Beacon beacon: mBeacons){
                HashMap<String,String> beaconMap = new HashMap<>();
                beaconMap.put("beacon_id",beacon.beacon_id);
                beaconIds.add(beaconMap);
            }
            themeZone.beacon = beaconIds;
        }

        showProgressPopup();
        Call<Rev_Base> revBaseCall = wezoneRestful.postThemeZone(themeZone);
        revBaseCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                hidePorgressPopup();
                finish();
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });
    }
}
