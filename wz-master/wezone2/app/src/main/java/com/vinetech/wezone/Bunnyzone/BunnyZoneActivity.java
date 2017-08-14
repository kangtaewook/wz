package com.vinetech.wezone.Bunnyzone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_BunnyZone;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_ZoneMemberList;
import com.vinetech.wezone.Wezone.GpsInfo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BunnyZoneActivity extends BaseActivity {

    public static final String BUNNYZONE_ITEM = "item";

    public static void startActivity(BaseActivity activity, Data_BunnyZone bunnyzone) {
        Intent intent = new Intent(activity, BunnyZoneActivity.class);
        intent.putExtra(BUNNYZONE_ITEM,bunnyzone);
        activity.moveActivity(intent);
    }

    private ListView listview;
    private BunnyZoneMemberListAdapter mBunnyZoneMemberListAdapter;

    //header
    private LinearLayout linearlayout_header_bg;
    private ImageView imageview_profile;
    private TextView textview_name;

    private TextView textview_theme_name;
    private ImageView imageview_theme_icon;

    private LinearLayout linearlayout_beacon_area;

    private LinearLayout linearlayout_btn_area;
    private LinearLayout linearlayout_btn_refuse;
    private LinearLayout linearlayout_btn_accept;

    private LinearLayout linearlayout_btn_push;

    private LinearLayout linearlayout_member_title_area;
    private TextView textview_member_cnt;

    private Data_BunnyZone mBunnyzone;

    private ArrayList<Data_Zone_Member> mMemberList;
    private GpsInfo mGpsInfo;

    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bunny_zone);

        mBunnyzone = (Data_BunnyZone) getIntent().getSerializableExtra(BUNNYZONE_ITEM);

        listview = (ListView) findViewById(R.id.listview);
        View header = getLayoutInflater().inflate(R.layout.bunnyzone_header, null, false);
        listview.addHeaderView(header,null,false);

        linearlayout_header_bg = (LinearLayout) findViewById(R.id.linearlayout_header_bg);
        linearlayout_header_bg.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));

        imageview_profile = (ImageView) findViewById(R.id.imageview_profile);
        textview_name = (TextView) findViewById(R.id.textview_name);

        textview_theme_name = (TextView) findViewById(R.id.textview_theme_name);
        imageview_theme_icon = (ImageView) findViewById(R.id.imageview_theme_icon);

        linearlayout_beacon_area = (LinearLayout) findViewById(R.id.linearlayout_beacon_area);
        linearlayout_btn_area = (LinearLayout) findViewById(R.id.linearlayout_btn_area);
        linearlayout_btn_refuse = (LinearLayout) findViewById(R.id.linearlayout_btn_area);
        linearlayout_btn_refuse.setOnClickListener(mClickListner);
        linearlayout_btn_accept = (LinearLayout) findViewById(R.id.linearlayout_btn_accept);
        linearlayout_btn_accept.setOnClickListener(mClickListner);

        linearlayout_btn_push = (LinearLayout) findViewById(R.id.linearlayout_btn_push);

        linearlayout_member_title_area = (LinearLayout) findViewById(R.id.linearlayout_member_title_area);
        textview_member_cnt = (TextView) findViewById(R.id.textview_member_cnt);

        String title = getResources().getString(R.string.bunnyzone_title);
        if(WezoneUtil.isManager(mBunnyzone.manage_type)){
            setHeaderView(R.drawable.btn_back_white,title,R.drawable.btn_more_white);
        }else{
            setHeaderView(R.drawable.btn_back_white,title,0);
        }

        if(WezoneUtil.isMember(mBunnyzone.manage_type)){
            linearlayout_beacon_area.setVisibility(View.GONE);
            linearlayout_btn_push.setVisibility(View.VISIBLE);
        }else{
            linearlayout_beacon_area.setVisibility(View.VISIBLE);
            linearlayout_btn_push.setVisibility(View.GONE);
        }

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


        mMemberList = new ArrayList<>();

        mGpsInfo = new GpsInfo(BunnyZoneActivity.this);
        // GPS 사용유무 가져오기
        if (mGpsInfo.isGetLocation()) {
            longitude = mGpsInfo.getLongitude();
            latitude = mGpsInfo.getLatitude();



        } else {
            // GPS 를 사용할수 없으므로
            mGpsInfo.showSettingsAlert();
        }

        showProgressPopup();
        Call<Rev_ZoneMemberList> revZoneMemberListCall = wezoneRestful.getZoneMemeberList(mBunnyzone.zone_id,"B","0","10",String.valueOf(latitude),String.valueOf(longitude));
        revZoneMemberListCall.enqueue(new Callback<Rev_ZoneMemberList>() {
            @Override
            public void onResponse(Call<Rev_ZoneMemberList> call, Response<Rev_ZoneMemberList> response) {
                Rev_ZoneMemberList revZoneMemberList = response.body();
                if(isNetSuccess(revZoneMemberList)){
                    mMemberList.addAll(revZoneMemberList.list);
                    mBunnyZoneMemberListAdapter.notifyDataSetChanged();
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_ZoneMemberList> call, Throwable t) {
                hidePorgressPopup();
            }
        });


//        if(mBunnyzone.members != null && mBunnyzone.members.size() > 0){
//            linearlayout_member_title_area.setVisibility(View.VISIBLE);
//            textview_member_cnt.setText(String.valueOf(mBunnyzone.members.size()));
//            mMemberList.addAll(mBunnyzone.members);
//        }else{
//            linearlayout_member_title_area.setVisibility(View.GONE);
//        }

        reloadLayout();
    }

    public void reloadLayout(){
        if(WezoneUtil.isEmptyStr(mBunnyzone.img_url) == false){
            showImageFromRemote(mBunnyzone.img_url,R.drawable.ic_bunny_image,imageview_profile);
        }else{
            imageview_profile.setImageResource(R.drawable.ic_bunny_image);
        }

        textview_name.setText(mBunnyzone.title);

        mBunnyZoneMemberListAdapter = new BunnyZoneMemberListAdapter(BunnyZoneActivity.this,mMemberList);
        listview.setAdapter(mBunnyZoneMemberListAdapter);

    }

    View.OnClickListener mClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId){
                case R.id.linearlayout_btn_push:
                    break;

                case R.id.linearlayout_btn_refuse:
                    finish();
                    break;

                case R.id.linearlayout_btn_accept:

                    break;
            }
        }
    };


    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);
        BunnyZoneManageActivity.startActivityWithEdit(BunnyZoneActivity.this,mBunnyzone);
    }
}
