package com.vinetech.wezone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_OtherUser;
import com.vinetech.wezone.Data.Data_UserInfo;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Main.WezoneMainListAdapter;
import com.vinetech.wezone.Message.ChattingActivity;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_UserInfo;
import com.vinetech.wezone.RevPacket.Rev_WezoneList;
import com.vinetech.wezone.RevPacket.Rev_Wezoninfo;
import com.vinetech.wezone.SendPacket.Send_PutFriend;
import com.vinetech.wezone.Wezone.GpsInfo;
import com.vinetech.wezone.Wezone.WezoneActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity implements AbsListView.OnScrollListener {

    public static final String USER_INFO = "useinfo";
    public static final String USER_UUID = "user_uuid";
    public static final String USER_DISTANCE = "user_distance";
    public static final String PROFILE = "PROFILE";
    public static final String FRIEND_PULS = "FRIEND_PULS";
    public static final String ADD_FRIEND = "ADD_FRIEND";
    public static final String ZONE_MEMBER_INFO = "ADD_FRIEND";
    public static final String CHECK = "CHECK";
    public static final String MYWEZONE = "MYWEZONE";

    public static void startActivity(BaseActivity activity, Data_UserInfo userinfo) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra(USER_INFO, userinfo);
        activity.startActivityForResult(intent, Define.INTENT_RESULT_MEMBER_LIST_FRIEND_PULSE);
    }

    public static void startActivityWithuuid(BaseActivity activity, String uuid, String distance, Data_Zone_Member data_zone_member) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra(USER_UUID, uuid);
        intent.putExtra(USER_DISTANCE, distance);
        intent.putExtra(ZONE_MEMBER_INFO, data_zone_member);
        activity.startActivityForResult(intent, Define.INTENT_RESULT_MEMBER_LIST_FRIEND_PULSE);
    }

    GpsInfo mGpsInfo;
    public Data_UserInfo mUserInfo;
    public Data_Zone_Member mUserInfos;
    public Data_WeZone mWezone;


    public String mUserUuid;
    public String mUserDistance;

    public String add_friend_type;

    private RelativeLayout relativelayout_bg;
    private ImageView imageview_bg;
    private ImageView imageview_profile;

    private TextView textview_name;
    private TextView textview_loaction;
    private TextView textview_status_msg;

    private ImageView imageview_profile_gps_check;

    public TextView textview_profile_header_distance;
    public TextView textview_zone_count;

    public LinearLayout linearlayout_btn_group_chat;
    public LinearLayout linearlayout_btn_add_friend;
    public LinearLayout linearlayout_btn_group_chat_plused;
    public LinearLayout linearLayout1;
    public LinearLayout linearLayout2;

    public LinearLayout linearlayout_zone_title_area;

    public StaggeredGridView mGridView;

    public ArrayList<Data_WeZone> mWezoneInfos;
    public WezoneMainListAdapter mWezoneListAdapter;

    public double longitude;
    public double latitude;

    private String mcheck_gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setHeaderView(R.drawable.btn_back_white, null, 0);

        mGridView = (StaggeredGridView) findViewById(R.id.grid_view);
        View header = getLayoutInflater().inflate(R.layout.profile_header, null, false);
        mGridView.addHeaderView(header, null, false);

        mUserInfo = (Data_UserInfo) getIntent().getSerializableExtra(USER_INFO);
        mUserUuid = getIntent().getStringExtra(USER_UUID);
        mUserDistance = getIntent().getStringExtra(USER_DISTANCE);
        mUserInfos = (Data_Zone_Member) getIntent().getSerializableExtra(ZONE_MEMBER_INFO);

        relativelayout_bg = (RelativeLayout) header.findViewById(R.id.relativelayout_bg);
        relativelayout_bg.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));

        imageview_bg = (ImageView) header.findViewById(R.id.imageview_bg);
        imageview_profile = (ImageView) header.findViewById(R.id.imageview_profile);
        imageview_profile_gps_check = (ImageView) header.findViewById(R.id.imageview_profile_gps_check);

        textview_name = (TextView) header.findViewById(R.id.textview_name);
        textview_loaction = (TextView) header.findViewById(R.id.textview_loaction);
        textview_status_msg = (TextView) header.findViewById(R.id.textview_status_msg);

        textview_profile_header_distance = (TextView) header.findViewById(R.id.textview_profile_header_distance);
        textview_zone_count = (TextView) header.findViewById(R.id.textview_zone_count);
        linearlayout_btn_group_chat = (LinearLayout) header.findViewById(R.id.linearlayout_btn_group_chat);
        linearlayout_btn_group_chat.setOnClickListener(mClickListener);
        linearlayout_btn_add_friend = (LinearLayout) header.findViewById(R.id.linearlayout_btn_add_friend);
        linearlayout_btn_add_friend.setOnClickListener(mClickListener);
        linearlayout_btn_group_chat_plused = (LinearLayout) header.findViewById(R.id.linearlayout_btn_group_chat_plused);
        linearlayout_btn_group_chat_plused.setOnClickListener(mClickListener);
        linearLayout1 = (LinearLayout) header.findViewById(R.id.linearLayout1);
        linearLayout1.setOnClickListener(mClickListener);
        linearLayout2 = (LinearLayout) header.findViewById(R.id.linearLayout2);

        linearlayout_zone_title_area = (LinearLayout) header.findViewById(R.id.linearlayout_zone_title_area);

        mWezoneInfos = new ArrayList<>();
        mWezoneListAdapter = new WezoneMainListAdapter(ProfileActivity.this, mWezoneInfos, Define.WEZONE_LIST_PROFILE_TYPE);

        mGridView.setAdapter(mWezoneListAdapter);
        mGridView.setOnScrollListener(this);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int realPos = position - 1;
                mWezone= mWezoneInfos.get(realPos);
                //wezone/wezone/ 1건 조회
                mcheck_gps = MYWEZONE;
                gps();
            }
        });


        //내 친구 리스트에서 온 상황
        if(mUserInfo != null){

            if( mUserInfo.friend_uuid != null){
                linearLayout2.setVisibility(View.GONE);
                linearLayout1.setVisibility(View.VISIBLE);
            }else{
                mUserUuid = mUserInfo.uuid;
            }
        }


        //위존리스트에 멤버 리스트에서 온 상황 친구 추가버튼 클릭 후 mUserInfos.friend_uuid 에 uuid 가 들어있으면 친구 추가가 된 상황
        if(mUserInfos != null && mUserInfos.friend_uuid != null) {
            if (WezoneUtil.isEmptyStr(mUserInfos.friend_uuid) == false) {
                linearLayout2.setVisibility(View.GONE);
                linearLayout1.setVisibility(View.VISIBLE);
            }
        }

        if(mUserUuid != null){
            if(mUserUuid.equals(getShare().getMyInfo().uuid)){
                linearLayout2.setVisibility(View.GONE);
                linearLayout1.setVisibility(View.VISIBLE);
            }
        }

        gps();
//        getWezoneList();
//        refreshLayout();
    }

    public void gps() {


        mGpsInfo = new GpsInfo(ProfileActivity.this);
        // GPS 사용유무 가져오기
        if (mGpsInfo.isGetLocation()) {
            longitude = mGpsInfo.getLongitude();
            latitude = mGpsInfo.getLatitude();
            imageview_profile_gps_check.setVisibility(View.GONE);

            if(mcheck_gps != null){
                if(mcheck_gps.equals(MYWEZONE)){
                    getWezone(mWezone);
                }else {
                    getWezoneList();
                }
            } else{
                getUserInfo();
                getWezoneList();
            }

        } else {
            // GPS 를 사용할수 없으므로
//            mGpsInfo.showSettingsAlert();
            imageview_profile_gps_check.setVisibility(View.VISIBLE);
            if(mcheck_gps != null){
                if(mcheck_gps.equals(MYWEZONE)) {
                    getWezone_notgps(mWezone);
                }else{
                    getWezoneListNOTGPS();
                }
            }else{
                getUserInfo_notgps();
                getWezoneListNOTGPS();
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume(); //조건 넣기 처음빼고
        setTransparentHeaderView();
        if(mcheck_gps != null) {
            if(mcheck_gps.equals(CHECK))
            gps();
        }
        mcheck_gps = CHECK;
    }

    public void refreshLayout() {

        if (WezoneUtil.isEmptyStr(mUserInfo.bg_img_url) == false) {
            imageview_bg.setVisibility(View.VISIBLE);
            showImageFromRemote(mUserInfo.bg_img_url, 0, imageview_bg);
        } else {
            imageview_bg.setVisibility(View.GONE);
        }

        if (WezoneUtil.isEmptyStr(mUserInfo.img_url) == false) {
            showImageFromRemoteWithCircle(mUserInfo.img_url,R.drawable.btn_circle_white,imageview_profile);
        } else {
            imageview_profile.setImageResource(R.drawable.btn_circle_white);

        }

        textview_name.setText(mUserInfo.user_name);
        textview_loaction.setText(mUserInfo.address);

        //Distance
        if (WezoneUtil.isEmptyStr(mUserDistance) == false) {
            DecimalFormat form = new DecimalFormat("#.#");
            double distance = Double.parseDouble(mUserDistance);
            textview_profile_header_distance.setText(form.format(distance) + "km");
        } else if (WezoneUtil.isEmptyStr(String.valueOf(mUserInfo.distance)) == false) {
            DecimalFormat form = new DecimalFormat("#.#");
            double distance = Double.parseDouble(String.valueOf(mUserInfo.distance));
            textview_profile_header_distance.setText(form.format(distance) + "km");
        } else {
            textview_profile_header_distance.setText("0 km");
        }




        if (WezoneUtil.isEmptyStr(mUserDistance) == false) {
            if (mUserDistance.contains(".")) {
                int i = mUserDistance.indexOf(".");
                String mDistance = mUserDistance.substring(0, i);
                if (Integer.valueOf(mDistance) != 0) {
                    DecimalFormat form = new DecimalFormat("#.#");
                    double distance = Double.parseDouble(mUserDistance);
                    textview_profile_header_distance.setText("나와의 거리  " + form.format(distance) + "km");
                    mUserDistance = String.valueOf(form.format(distance));
                } else if (Double.valueOf(mUserDistance) > 0.0) {
                    if (Integer.valueOf(mDistance) == 0) {
                        int meter = (int) (Double.valueOf(mUserDistance) * 1000);
                        textview_profile_header_distance.setText("나와의 거리  " + meter + "m");
                        mUserDistance = String.valueOf(meter);
                    }
                } else if (mUserDistance.equals("0")) {
                    textview_profile_header_distance.setText("나와의 거리  " + 0 + "m");
                    mUserDistance = String.valueOf(0);
                }
            }else if(Integer.valueOf(mUserDistance) > 0){
                textview_profile_header_distance.setText("나와의 거리  " + mUserDistance + "m");
                mUserDistance = String.valueOf(mUserDistance);
            } else {
                textview_profile_header_distance.setText("나와의 거리  " + 0 + "m");
                mUserDistance = String.valueOf(0);
            }
        }


    }

    public void getUserInfo(){
        //GPS가 될 때
        showProgressPopup();
        Call<Rev_UserInfo> userInfoCall;
        if (mUserInfo == null) {
            userInfoCall = wezoneRestful.getUserInfo(mUserUuid, String.valueOf(longitude), String.valueOf(latitude));
        } else {
            userInfoCall = wezoneRestful.getUserInfo(mUserInfo.uuid);
        }
        userInfoCall.enqueue(new Callback<Rev_UserInfo>() {
            @Override
            public void onResponse(Call<Rev_UserInfo> call, Response<Rev_UserInfo> response) {
                Rev_UserInfo revUserInfo = response.body();
                if (isNetSuccess(revUserInfo)) {
                    mUserInfo = revUserInfo.user_info;
                    refreshLayout();
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_UserInfo> call, Throwable t) {
                hidePorgressPopup();
            }
        });


    }

    public void getUserInfo_notgps() {
        //GPS가 안될 때
        showProgressPopup();
        Call<Rev_UserInfo> userInfoCall;
        if (mUserInfo == null) {
            userInfoCall = wezoneRestful.getUserInfo(mUserUuid);
        } else {
            userInfoCall = wezoneRestful.getUserInfo(mUserInfo.uuid);
        }
        userInfoCall.enqueue(new Callback<Rev_UserInfo>() {
            @Override
            public void onResponse(Call<Rev_UserInfo> call, Response<Rev_UserInfo> response) {
                Rev_UserInfo revUserInfo = response.body();
                if (isNetSuccess(revUserInfo)) {
                    mUserInfo = revUserInfo.user_info;
                    refreshLayout();
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_UserInfo> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }


    public void getWezoneListNOTGPS() {

        showProgressPopup();
        Call<Rev_WezoneList> wezoneListCall;
        if (mUserInfo == null) {
            wezoneListCall = wezoneRestful.getMyWezoneList(mUserUuid);
        } else {
            wezoneListCall = wezoneRestful.getMyWezoneList(mUserInfo.uuid);
        }

//        showProgressPopup();
//        Call<Rev_WezoneList> wezoneListCall = wezoneRestful.getMyWezoneList(mUserUuid);

        wezoneListCall.enqueue(new Callback<Rev_WezoneList>() {
            @Override
            public void onResponse(Call<Rev_WezoneList> call, Response<Rev_WezoneList> response) {
                Rev_WezoneList resultData = response.body();
                if (isNetSuccess(resultData)) {
                    if (resultData.list != null) {
                        mWezoneInfos.addAll(resultData.list);
                        for(Data_WeZone weZone : mWezoneInfos){
                            if("F".equals(weZone.wezone_type)){
                                //비공개는 안보이게
                                mWezoneInfos.remove(weZone);
                            }
                        }
                        mWezoneListAdapter.notifyDataSetChanged();

                        int cnt = mWezoneInfos.size();
                        textview_zone_count.setText(String.valueOf(cnt));
                    }
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_WezoneList> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }


    public void getWezoneList() {

        showProgressPopup();
        Call<Rev_WezoneList> wezoneListCall;
        if (mUserUuid == null) {
            wezoneListCall = wezoneRestful.getMyWezoneList(String.valueOf(longitude), String.valueOf(latitude), mUserInfo.uuid);
        } else {
            wezoneListCall = wezoneRestful.getMyWezoneList(String.valueOf(longitude), String.valueOf(latitude), mUserUuid);
        }
        //전문 3번날림
        wezoneListCall.enqueue(new Callback<Rev_WezoneList>() {
            @Override
            public void onResponse(Call<Rev_WezoneList> call, Response<Rev_WezoneList> response) {
                Rev_WezoneList resultData = response.body();
                if (isNetSuccess(resultData)) {
                    if (resultData.list != null) {
                        mWezoneInfos.clear();
                        mWezoneInfos.addAll(resultData.list);
                        mWezoneListAdapter.notifyDataSetChanged();

                        int cnt = mWezoneInfos.size();
                        textview_zone_count.setText(String.valueOf(cnt));
                    }
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_WezoneList> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }


        public void getWezone_notgps(Data_WeZone weZone) {
            Call<Rev_Wezoninfo> WezoneCall = wezoneRestful.getWezone(weZone.wezone_id);
            WezoneCall.enqueue(new Callback<Rev_Wezoninfo>() {

                @Override
                public void onResponse(Call<Rev_Wezoninfo> call, Response<Rev_Wezoninfo> response) {
                    Rev_Wezoninfo resultData = response.body();
                    if (isNetSuccess(resultData)) {
                        if (resultData.wezone_info != null) {
                            mWezone = resultData.wezone_info;
                            WezoneActivity.startActivit(ProfileActivity.this, mWezone, PROFILE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Rev_Wezoninfo> call, Throwable t) {


                }
            });
        }


    public void getWezone(Data_WeZone weZone) {
        Call<Rev_Wezoninfo> WezoneCall = wezoneRestful.getWezone(weZone.wezone_id, String.valueOf(longitude), String.valueOf(latitude));
        WezoneCall.enqueue(new Callback<Rev_Wezoninfo>() {

            @Override
            public void onResponse(Call<Rev_Wezoninfo> call, Response<Rev_Wezoninfo> response) {
                Rev_Wezoninfo resultData = response.body();
                if (isNetSuccess(resultData)) {
                    if (resultData.wezone_info != null) {
                        mWezone = resultData.wezone_info;
                        WezoneActivity.startActivit(ProfileActivity.this, mWezone, PROFILE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Rev_Wezoninfo> call, Throwable t) {


            }
        });
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId) {
                case R.id.linearlayout_btn_group_chat_plused: {
                    Data_OtherUser other = new Data_OtherUser();
                    other.user_uuid = mUserInfo.uuid;
                    other.user_name = mUserInfo.user_name;
                    other.img_url = mUserInfo.img_url;
                    ChattingActivity.startActivity(ProfileActivity.this, other);
                }
                break;
                case R.id.linearlayout_btn_group_chat: {
                    Data_OtherUser other = new Data_OtherUser();
                    other.user_uuid = mUserInfo.uuid;
                    other.user_name = mUserInfo.user_name;
                    other.img_url = mUserInfo.img_url;
                    ChattingActivity.startActivity(ProfileActivity.this, other);
                }
                break;

                case R.id.linearlayout_btn_add_friend: {
                    showProgressPopup();
                    Send_PutFriend param = new Send_PutFriend();
                    param.other_uuid = mUserUuid;
                    Call<Rev_Base> putFriend = wezoneRestful.putFriend(param);
                    putFriend.enqueue(new Callback<Rev_Base>() {
                        @Override
                        public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                            hidePorgressPopup();
                            linearLayout2.setVisibility(View.GONE);
                            linearLayout1.setVisibility(View.VISIBLE);
                            //버튼이 없어지거나 해야함. 중복 클릭으로 인한 에러 발생

                            linearLayout1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Data_OtherUser other = new Data_OtherUser();
                                    other.user_uuid = mUserInfo.uuid;
                                    other.user_name = mUserInfo.user_name;
                                    other.img_url = mUserInfo.img_url;
                                    ChattingActivity.startActivity(ProfileActivity.this, other);
                                }
                            });

                            getShare().addFriendCnt();
                            add_friend_type = ADD_FRIEND;

                            //추가 됨을 알림 전 화면에
                            Intent intent = new Intent();
                            if (mUserUuid == null) {
                                intent.putExtra(FRIEND_PULS, mUserInfo.uuid);
                            } else {
                                intent.putExtra(FRIEND_PULS, mUserUuid);
                            }
                            setResult(RESULT_OK, intent);

                        }

                        @Override
                        public void onFailure(Call<Rev_Base> call, Throwable t) {
                            hidePorgressPopup();
                        }
                    });
                }
                break;
            }
        }
    };


}
