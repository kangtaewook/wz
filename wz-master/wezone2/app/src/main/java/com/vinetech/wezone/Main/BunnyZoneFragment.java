package com.vinetech.wezone.Main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.vinetech.wezone.Bunnyzone.BunnyZoneActivity;
import com.vinetech.wezone.Bunnyzone.BunnyZoneManageActivity;
import com.vinetech.wezone.Data.Data_BunnyZone;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_BunnyzoneList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class BunnyZoneFragment  extends Fragment {

    public MainActivity mActivity;
    public ListView mListView;

    public ArrayList<Data_BunnyZone> mBunnyzoneInfo;

    public BunnyZoneListAdapter mBunnyZoneListAdapter;

    public FloatingActionButton fab_btn_add;

    public LinearLayout list_noresult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity)getActivity();

        View view = getView();
        mListView = (ListView) view.findViewById(R.id.listview);

        list_noresult = (LinearLayout) view.findViewById(R.id.list_noresult);

        mBunnyzoneInfo = new ArrayList<>();

        mBunnyZoneListAdapter = new BunnyZoneListAdapter(mActivity,mBunnyzoneInfo);
        mListView.setAdapter(mBunnyZoneListAdapter);

        fab_btn_add = (FloatingActionButton) view.findViewById(R.id.fab_btn_add);
        fab_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BunnyZoneManageActivity.startActivityWithRegist(mActivity,null);
            }
        });

        Call<Rev_BunnyzoneList> bunnyzoneListCall = mActivity.wezoneRestful.getBunnyzoneList();
        bunnyzoneListCall.enqueue(new Callback<Rev_BunnyzoneList>() {
            @Override
            public void onResponse(Call<Rev_BunnyzoneList> call, Response<Rev_BunnyzoneList> response) {
                Rev_BunnyzoneList resultData = response.body();
                if(mActivity.isNetSuccess(resultData)){
                    mBunnyzoneInfo.addAll(resultData.list);

                    if(mBunnyzoneInfo.size() > 0){
                        list_noresult.setVisibility(View.GONE);
                        mBunnyZoneListAdapter.notifyDataSetChanged();
                    }else{
                        list_noresult.setVisibility(View.VISIBLE);
                    }
                }else{
                    if(mBunnyzoneInfo.size() > 0){
                        list_noresult.setVisibility(View.GONE);
                        mBunnyZoneListAdapter.notifyDataSetChanged();
                    }else{
                        list_noresult.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Rev_BunnyzoneList> call, Throwable t) {
                if(mBunnyzoneInfo.size() > 0){
                    list_noresult.setVisibility(View.GONE);
                    mBunnyZoneListAdapter.notifyDataSetChanged();
                }else{
                    list_noresult.setVisibility(View.VISIBLE);
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Data_BunnyZone bunnyZone = mBunnyzoneInfo.get(position);

                BunnyZoneActivity.startActivity(mActivity,bunnyZone);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bunnyzone_fragment, null);
    }
}
