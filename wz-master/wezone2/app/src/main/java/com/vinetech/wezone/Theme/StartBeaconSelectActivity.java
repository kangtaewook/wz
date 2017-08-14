package com.vinetech.wezone.Theme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Beacon.BeaconSelectListAdapter;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_BeaconList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartBeaconSelectActivity extends BaseActivity {

    public static final String BEACON_DATA = "beacon_data";

    public static void startActivityWithData(BaseActivity activity, ArrayList<Data_Beacon> beacons) {
        Intent intent = new Intent(activity, StartBeaconSelectActivity.class);
        intent.putExtra(BEACON_DATA,beacons);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_START_BEACONE);
    }

    private TextView textview_beacon_cnt;

    private ListView listview;

    private BeaconSelectListAdapter mBeaconSelectListAdapter;

    private ArrayList<Data_Beacon> mBeaconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_select);

        String title = getResources().getString(R.string.select_beacon_title);
        setHeaderView(R.drawable.btn_back_white,title,R.drawable.btn_check);

        mBeaconList = (ArrayList<Data_Beacon>)getIntent().getSerializableExtra(BEACON_DATA);

        textview_beacon_cnt = (TextView) findViewById(R.id.textview_beacon_cnt);
        listview = (ListView) findViewById(R.id.listview);

        View header = getLayoutInflater().inflate(R.layout.beacon_select_list_header, null, false);
        listview.addHeaderView(header,null,false);

        mBeaconSelectListAdapter = new BeaconSelectListAdapter(StartBeaconSelectActivity.this,mBeaconList);
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

}
