package com.vinetech.wezone.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.vinetech.wezone.Data.Data_Theme;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_ThemeList;
import com.vinetech.wezone.Theme.ThemeActivity;
import com.vinetech.wezone.Theme.ThemeManageActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class ThemeZoneFragment extends Fragment {

    public MainActivity mActivity;
    public GridView gridview;
    public ArrayList<Data_Theme> mThemeInfos;
    public ThemeListAdapter mThemeListAdapter;
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
        gridview = (GridView) view.findViewById(R.id.gridview);

        list_noresult = (LinearLayout) view.findViewById(R.id.list_noresult);

        fab_btn_add = (FloatingActionButton) view.findViewById(R.id.fab_btn_add);
        fab_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mActivity, ThemeManageActivity.class);
                mActivity.moveActivityForResult(i, Define.INTENT_RESULT_THEME);
            }
        });

        mThemeInfos = new ArrayList<>();

        mThemeListAdapter = new ThemeListAdapter(mActivity,mThemeInfos);
        gridview.setAdapter(mThemeListAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Data_Theme theme = mThemeInfos.get(position);
                ThemeActivity.startActivity(mActivity, theme,ThemeActivity.THEME_MODE_NORMAL);
            }
        });

        getMyThemeList();
    }

    public void getMyThemeList(){

        if(mThemeInfos != null) {
            mThemeInfos.clear();

            Call<Rev_ThemeList> themeListCall = mActivity.wezoneRestful.getMyThemeZoneList();
            themeListCall.enqueue(new Callback<Rev_ThemeList>() {
                @Override
                public void onResponse(Call<Rev_ThemeList> call, Response<Rev_ThemeList> response) {
                    Rev_ThemeList resultData = response.body();
                    if (mActivity.isNetSuccess(resultData)) {

                        for (Data_Theme theme : resultData.list) {
                            theme.themeType = Data_Theme.TYPE_THEMEZONE;
                            mThemeInfos.add(theme);
                        }
                    } else {

                    }
                    mThemeListAdapter.notifyDataSetChanged();
                    getThemeList();
                }

                @Override
                public void onFailure(Call<Rev_ThemeList> call, Throwable t) {
                    getThemeList();
                }
            });
        }
    }

    public void getThemeList(){
        Call<Rev_ThemeList> themeListCall = mActivity.wezoneRestful.getMyThemeList();
        themeListCall.enqueue(new Callback<Rev_ThemeList>() {
            @Override
            public void onResponse(Call<Rev_ThemeList> call, Response<Rev_ThemeList> response) {
                Rev_ThemeList tl = response.body();
                if (mActivity.isNetSuccess(tl)) {
                    for (Data_Theme theme : tl.list) {
                        theme.themeType = Data_Theme.TYPE_THEME;
                        mThemeInfos.add(theme);
                    }
                } else {

                }

                mThemeListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Rev_ThemeList> call, Throwable t) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.themezone_fragment, null);
    }
}
