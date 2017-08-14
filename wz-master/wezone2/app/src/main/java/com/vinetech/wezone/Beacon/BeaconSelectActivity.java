package com.vinetech.wezone.Beacon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_BeaconList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 비콘 선택 화면
 *
 *
 */


public class BeaconSelectActivity extends BaseActivity {

    public static final String BEACON_DATA = "beacon_data";
    public static final String WHERE = "where";
    public static final String ZONE_ID = "zone_id";

    public static final int FROM_WEZONE = 0;
    public static final int FROM_THEME = 1;

    public static void startActivityWithData(BaseActivity activity, ArrayList<Data_Beacon> beacons) {
        Intent intent = new Intent(activity, BeaconSelectActivity.class);
        if(beacons != null){
            intent.putExtra(BEACON_DATA,beacons);
        }
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_BEACONE);
    }

    public static void startActivityWithData(BaseActivity activity, ArrayList<Data_Beacon> beacons, int where, String zone_id) {
        Intent intent = new Intent(activity, BeaconSelectActivity.class);
        if(beacons != null){
            intent.putExtra(BEACON_DATA,beacons);
            intent.putExtra(WHERE,where);
            intent.putExtra(ZONE_ID,zone_id);
        }
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_BEACONE);
    }

    private TextView textview_beacon_cnt;

    private ListView listview;

    private BeaconSelectListAdapter mBeaconSelectListAdapter;

    private ArrayList<Data_Beacon> mBeaconList;

    private ArrayList<Data_Beacon> mSelectedList;

    private int mWhere = FROM_WEZONE;
    private String mZoneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_select);

        String title = getResources().getString(R.string.select_beacon_title);
        setHeaderView(R.drawable.btn_back_white,title,R.drawable.btn_check);

        mSelectedList = (ArrayList<Data_Beacon>)getIntent().getSerializableExtra(BEACON_DATA);
        mWhere = getIntent().getIntExtra(WHERE,FROM_WEZONE);
        mZoneId = getIntent().getStringExtra(ZONE_ID);


        textview_beacon_cnt = (TextView) findViewById(R.id.textview_beacon_cnt);
        listview = (ListView) findViewById(R.id.listview);

        View header = getLayoutInflater().inflate(R.layout.beacon_select_list_header, null, false);
        listview.addHeaderView(header,null,false);

        mBeaconList = new ArrayList<>();

        mBeaconSelectListAdapter = new BeaconSelectListAdapter(BeaconSelectActivity.this,mBeaconList);
        listview.setAdapter(mBeaconSelectListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                if(position == 0)
//                    return;

//                if(getSelectedItemCount() >= 1){
//                    int realPos = position - 1;
//                    mBeaconList.get(realPos).isSelected = false;
//                    mBeaconSelectListAdapter.notifyDataSetChanged();
//                    return;
//                }

                int realPos = position - 1;
                mBeaconList.get(realPos).isSelected = !mBeaconList.get(realPos).isSelected;
                mBeaconSelectListAdapter.notifyDataSetChanged();
            }
        });

        showProgressPopup();
        Call<Rev_BeaconList> beaconListCall = wezoneRestful.getBeaconList();
        beaconListCall.enqueue(new Callback<Rev_BeaconList>() {
            @Override
            public void onResponse(Call<Rev_BeaconList> call, Response<Rev_BeaconList> response) {
                Rev_BeaconList resultData = response.body();
                if(isNetSuccess(resultData)){

                    if(mWhere == FROM_WEZONE){
                        mBeaconList.addAll(resultData.list);
                    }else{

                        for(Data_Beacon beacon : resultData.list) {

                            //공유 비콘 테마 비콘 제외
                            if (!"N".equals(beacon.manage_type) &&
                                    (WezoneUtil.isEmptyStr(beacon.theme_id) || WezoneUtil.isEmptyStr(mZoneId)) &&
                                    !mZoneId.equals(beacon.theme_id)) {
                                mBeaconList.add(beacon);
                            }

                        }
                    }

                    if(mBeaconList.size() > 0){
                        compareList();

                        mBeaconSelectListAdapter.notifyDataSetChanged();
                        textview_beacon_cnt.setText(String.valueOf(mBeaconList.size()));
                    }else{
                        Toast.makeText(BeaconSelectActivity.this,"등록된 WeCON이 없습니다",Toast.LENGTH_SHORT).show();
                        //finishAni();
                    }
                }else{

                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_BeaconList> call, Throwable t) {

                hidePorgressPopup();
            }
        });
    }

    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);
        Intent i = new Intent();
        i.putExtra(Define.INTENTKEY_BEACONE_VALUE,getSelectedItems());
        setResult(RESULT_OK,i);
        finish();
    }

    public ArrayList<Data_Beacon> getSelectedItems(){
        ArrayList<Data_Beacon> selectedItemList = new ArrayList<>();
        for(int i=0; i<mBeaconList.size(); i++){
            if(mBeaconList.get(i).isSelected){
                selectedItemList.add(mBeaconList.get(i));
            }
        }
        return selectedItemList;
    }

    public int getSelectedItemCount(){
        int cnt = 0;
        for(int i=0; i<mBeaconList.size(); i++){
            if(mBeaconList.get(i).isSelected){
                cnt ++;
            }
        }
        return cnt;
    }

    public void compareList(){
        if(mSelectedList != null && mSelectedList.size() > 0){
            for(Data_Beacon selectedBeacon : mSelectedList){
                if(mBeaconList != null && mBeaconList.size() > 0){
                    for (int i=0; i<mBeaconList.size(); i++){
                        Data_Beacon item = mBeaconList.get(i);
                        if(selectedBeacon.beacon_id.equals(item.beacon_id)){
                            mBeaconList.get(i).isSelected = true;
                        }
                    }
                }
            }
        }
    }



}
