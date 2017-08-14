package com.vinetech.wezone.Theme;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Theme;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_ThemeList;
import com.vinetech.wezone.SendPacket.Send_PutMyTheme;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemeManageActivity extends BaseActivity {

    public ListView listview;
    public ThemeManagerListAdapter mThemeManagerListAdapter;

    public ArrayList<Data_Theme> mThemeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_manage);

        setHeaderView(R.drawable.btn_back_white,getResources().getString(R.string.theme_manage_title),0);

        listview = (ListView) findViewById(R.id.listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Data_Theme themeInfo = mThemeList.get(position);
                ThemeDetailActivity.startActivity(ThemeManageActivity.this,themeInfo);
            }
        });

        mThemeList = new ArrayList<>();
        mThemeManagerListAdapter = new ThemeManagerListAdapter(ThemeManageActivity.this,mThemeList);
        listview.setAdapter(mThemeManagerListAdapter);

        Call<Rev_ThemeList> themeListCall = wezoneRestful.getThemeList();
        themeListCall.enqueue(new Callback<Rev_ThemeList>() {
            @Override
            public void onResponse(Call<Rev_ThemeList> call, Response<Rev_ThemeList> response) {
                Rev_ThemeList rev_themeList = response.body();
                if(isNetSuccess(rev_themeList)){
                    mThemeList.addAll(rev_themeList.list);
                    mThemeManagerListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Rev_ThemeList> call, Throwable t) {

            }
        });
    }

    public void putTheme(final String theme_id){
        Send_PutMyTheme param = new Send_PutMyTheme();
        param.theme_id = theme_id;

        Call<Rev_Base> themeCall = wezoneRestful.putTheme(param);
        themeCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base rev_base = response.body();
                if(isNetSuccess(rev_base)){
                    channgeThemeInfo(theme_id,true);
                    mThemeManagerListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });
    }

    public void deleteTheme(final String theme_id){
        Call<Rev_Base> themeCall = wezoneRestful.deleteTheme(theme_id);
        themeCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base rev_base = response.body();
                if(isNetSuccess(rev_base)){
                    channgeThemeInfo(theme_id,false);
                    mThemeManagerListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });
    }

    public void channgeThemeInfo(String theme_id, boolean isAdded){

        String myUUid = getUuid();
        for(Data_Theme theme : mThemeList){
            if(theme_id.equals(theme.theme_id)){
                if(isAdded){
                    theme.uuid = myUUid;
                }else{
                    theme.uuid = null;
                }
            }
        }
    }


}
