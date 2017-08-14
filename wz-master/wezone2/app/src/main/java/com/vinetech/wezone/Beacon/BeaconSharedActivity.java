package com.vinetech.wezone.Beacon;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_Beacon_info;
import com.vinetech.wezone.Data.Data_UserInfo;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.Data.Send_PutBeacon;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_ZoneMemberList;
import com.vinetech.wezone.SendPacket.Send_PutShare;
import com.vinetech.wezone.Wezone.GpsInfo;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vinetech.wezone.Data.Data_ActionItem.getTitleText;

public class BeaconSharedActivity extends BaseActivity {

    public static final String BEACON_INFO = "beacon_info";
    public static final String BEACON_FLAG_PUSH = "push_flag";

    public static void startActivity(BaseActivity activity, Data_Beacon beaconInfo) {
        Intent intent = new Intent(activity, BeaconSharedActivity.class);
        intent.putExtra(BEACON_INFO,beaconInfo);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_BEACONE);
    }

    private Data_Beacon mBeaconInfo;
    private ArrayList<Data_Zone_Member> mMemberList;

    private RelativeLayout relativelayout_bg;
    private ImageView imageview_bg;
    private ImageView imageview_profile;
    private TextView textview_name;

    private LinearLayout linearlayout_btn_area;

    private LinearLayout linearlayout_btn_group_reject;
    private LinearLayout linearlayout_btn_add_beacon;


    private LinearLayout linearlayout_set_area;

    private LinearLayout linearlayout_btn_push;
    private TextView textview_desc_01;
    private TextView textview_desc_02;

    private LinearLayout linearlayout_member_title_area;
    private TextView textview_member_cnt;
    private ImageView delete_share_beacon;
    private LinearLayout linearlayout_btn_ok;
    private LinearLayout linearlayout_btn_cancal;


    private ListView listview;
    private BeaconZoneMemberListAdapter mBeaconZoneMemberListAdapter;

    private GpsInfo mGpsInfo;

    private double longitude;
    private double latitude;

    Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_shared);

        setHeaderView(R.drawable.btn_back_white, null, 0);

        mBeaconInfo = (Data_Beacon) getIntent().getSerializableExtra(BEACON_INFO);

        mMemberList = new ArrayList<>();

        View header = getLayoutInflater().inflate(R.layout.beacon_header, null, false);

        relativelayout_bg = (RelativeLayout) header.findViewById(R.id.relativelayout_bg);
        relativelayout_bg.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));

        imageview_bg = (ImageView)header.findViewById(R.id.imageview_bg);
        imageview_profile = (ImageView)header.findViewById(R.id.imageview_profile);
        textview_name = (TextView) header.findViewById(R.id.textview_name);

        showImageFromRemoteWithCircle(mBeaconInfo.img_url, R.drawable.btn_circle_white, imageview_profile);
        textview_name.setText(mBeaconInfo.name);

        linearlayout_btn_area = (LinearLayout) header.findViewById(R.id.linearlayout_btn_area);

        linearlayout_btn_group_reject = (LinearLayout) header.findViewById(R.id.linearlayout_btn_group_reject);
        linearlayout_btn_group_reject.setOnClickListener(mClickListener);

        linearlayout_btn_add_beacon = (LinearLayout) header.findViewById(R.id.linearlayout_btn_add_beacon);
        linearlayout_btn_add_beacon.setOnClickListener(mClickListener);

        linearlayout_set_area = (LinearLayout) header.findViewById(R.id.linearlayout_set_area);

        linearlayout_btn_push = (LinearLayout) header.findViewById(R.id.linearlayout_btn_push);

        linearlayout_btn_ok = (LinearLayout) header.findViewById(R.id.linearlayout_btn_ok);
        linearlayout_btn_cancal = (LinearLayout) header.findViewById(R.id.linearlayout_btn_cancal);

        delete_share_beacon = (ImageView) header.findViewById(R.id.delete_share_beacon);
        delete_share_beacon.setOnClickListener(mClickListener);

        if("T".equals(mBeaconInfo.push_flag)){
            linearlayout_btn_push.setSelected(true);
        }else{
            linearlayout_btn_push.setSelected(false);
        }

        linearlayout_btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linearlayout_btn_push.isSelected()){
                    linearlayout_btn_push.setSelected(false);
                    sendBeaconPut(BEACON_FLAG_PUSH, "F");
                }else{
                    linearlayout_btn_push.setSelected(true);
                    sendBeaconPut(BEACON_FLAG_PUSH, "T");
                }
            }
        });

        textview_desc_01 = (TextView) header.findViewById(R.id.textview_desc_01);
        textview_desc_02 = (TextView) header.findViewById(R.id.textview_desc_02);

        if (mBeaconInfo.beacon_info_vars != null) {
            if (mBeaconInfo.beacon_info_vars != null) {

                if (mBeaconInfo.beacon_info_vars.beacon != null) {

                    if (mBeaconInfo.beacon_info_vars.beacon.near_id != null) {
                        textview_desc_01.setText(getTitleText(mBeaconInfo.beacon_info_vars.beacon.near_id.id));
                    }

                    if (mBeaconInfo.beacon_info_vars.beacon.far_id != null) {
                        textview_desc_02.setText(getTitleText(mBeaconInfo.beacon_info_vars.beacon.far_id.id));
                    }
                }
            }
        }

        linearlayout_member_title_area = (LinearLayout) header.findViewById(R.id.linearlayout_member_title_area);
        textview_member_cnt = (TextView) header.findViewById(R.id.textview_member_cnt);


        listview = (ListView) findViewById(R.id.listview);
        listview.addHeaderView(header);

        mBeaconZoneMemberListAdapter = new BeaconZoneMemberListAdapter(BeaconSharedActivity.this, mMemberList,null);
        listview.setAdapter(mBeaconZoneMemberListAdapter);

        mGpsInfo = new GpsInfo(BeaconSharedActivity.this);
        // GPS 사용유무 가져오기
        if (mGpsInfo.isGetLocation()) {
            longitude = mGpsInfo.getLongitude();
            latitude = mGpsInfo.getLatitude();



        } else {
            // GPS 를 사용할수 없으므로
//            mGpsInfo.showSettingsAlert();
        }

        changeView();
    }

    public void changeView(){

        if("W".equals(mBeaconInfo.manage_type) || "R".equals(mBeaconInfo.manage_type) || WezoneUtil.isEmptyStr(mBeaconInfo.manage_type)){
            linearlayout_set_area.setVisibility(View.GONE);
            linearlayout_btn_area.setVisibility(View.VISIBLE);
        }else{
            linearlayout_set_area.setVisibility(View.VISIBLE);
            linearlayout_btn_area.setVisibility(View.GONE);

            getMemberList(0,100);
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

                    mMemberList.addAll(memberList.list);
                    for(int i = 0 ; i<mMemberList.size(); i++){
                        //자기 계정은 제외
                        if(mMemberList.get(i).uuid.equals(getShare().getMyInfo().uuid)){
                            mMemberList.remove(i);
                        }
                    }

                    linearlayout_member_title_area.setVisibility(View.VISIBLE);
                    textview_member_cnt.setText("" + mMemberList.size());
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

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            switch (viewId){
                case R.id.linearlayout_btn_group_reject:
                    sendBeaconShare(mBeaconInfo.beacon_id,Send_PutShare.SHARE_FLAG_REJECT);
                    finish();
                    break;

                case R.id.linearlayout_btn_add_beacon:
                    sendBeaconShare(mBeaconInfo.beacon_id,Send_PutShare.SHARE_FLAG_ACCEPT);
                    finish();
                    break;
                case R.id.delete_share_beacon:
                  //비콘 해제
                    setDelete_share_beacon();
                    break;
                case R.id.linearlayout_btn_ok:
                    //비콘 해제
                    ArrayList<Data_UserInfo> arrayList = new ArrayList<>();
                    arrayList.add(getShare().getMyInfo());
                    sendBeaconShare_delete(mBeaconInfo.beacon_id,arrayList);

                    mDialog.cancel();
                    finish();
                    break;

                case R.id.linearlayout_btn_cancal:
                    mDialog.cancel();
                    break;

            }
        }
    };

    public void sendBeaconShare(String beacon_id, final String flag){

        Send_PutShare putShare = new Send_PutShare();
        putShare.type = Send_PutShare.SHARE_TYPE_BEACON;
        putShare.zone_id = beacon_id;
        putShare.share_flag = flag;

        showProgressPopup();
        Call<Rev_Base> putBeaconShare = wezoneRestful.putBeaconShare(putShare);
        putBeaconShare.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revBase = response.body();
                if (isNetSuccess(revBase)) {

                    if(flag.equals(Send_PutShare.SHARE_FLAG_ACCEPT)){
                        changeView();
                    }else{
                        finish();
                    }

                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
                hidePorgressPopup();
            }
        });
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

    public void setDelete_share_beacon() {
        PopupMenu popup = new PopupMenu(this, delete_share_beacon);

        popup.getMenuInflater().inflate(R.menu.menu_delete_shared_beacon, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.delete_share_beacon:
                        delete_Dialog();
                        break;
                }

                return true;
            }
        });
        popup.show();
    }

    public void delete_Dialog() {
        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_delete_shared_beacon);
        linearlayout_btn_ok = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_ok);
        linearlayout_btn_ok.setOnClickListener(mClickListener);
        linearlayout_btn_cancal = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_cancal);
        linearlayout_btn_cancal.setOnClickListener(mClickListener);
        mDialog.show();
    }

    public void sendBeaconShare_delete(String beacon_id, ArrayList<Data_UserInfo> selectedUuid){

        Send_PutShare putShare = new Send_PutShare();
        putShare.type = Send_PutShare.SHARE_TYPE_BEACON;

        putShare.zone_id = beacon_id;

        putShare.other_uuids = new ArrayList<>();
        for(Data_UserInfo userInfo : selectedUuid){
            HashMap<String,String> other_uuids = new HashMap<>();
            other_uuids.put("uuid",userInfo.uuid);
            putShare.other_uuids.add(other_uuids);
        }
        putShare.share_flag = Send_PutShare.SHARE_FLAG_DELETE;

        showProgressPopup();
        Call<Rev_Base> putBeaconShare = wezoneRestful.putBeaconShare(putShare);
        putBeaconShare.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revBase = response.body();
                if (isNetSuccess(revBase)) {
                    Toast.makeText(BeaconSharedActivity.this, "공유된 WeCON을 해지했습니다.", Toast.LENGTH_SHORT).show();
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }

}
