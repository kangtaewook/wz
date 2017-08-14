package com.vinetech.wezone.Theme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Theme;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_ThemeList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemeSelectActivity extends BaseActivity {

    public static final String THEME_DATA = "theme_data";

    public static void startActivity(BaseActivity activity, Data_Theme theme) {
        Intent intent = new Intent(activity, ThemeSelectActivity.class);
        if(theme != null){
            intent.putExtra(THEME_DATA,theme);
        }
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_THEME);
    }

    private ListView listview;

    private ThemeSelectListAdapter mThemeSelectListAdapter;

    private Data_Theme mSelectedTheme;

    private ArrayList<Data_Theme> mThemeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_select);

        setHeaderView(R.drawable.btn_back_white,"테마 선택",0);

        mSelectedTheme = (Data_Theme) getIntent().getSerializableExtra(THEME_DATA);

        listview = (ListView) findViewById(R.id.listview);

        mThemeList = new ArrayList<>();

        mThemeSelectListAdapter = new ThemeSelectListAdapter(ThemeSelectActivity.this,mThemeList);
        listview.setAdapter(mThemeSelectListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                resetList();
                mThemeList.get(position).isSelected = true;
                mThemeSelectListAdapter.notifyDataSetChanged();
            }
        });

        Call<Rev_ThemeList> themeListCall = wezoneRestful.getMyThemeList();
        themeListCall.enqueue(new Callback<Rev_ThemeList>() {
            @Override
            public void onResponse(Call<Rev_ThemeList> call, Response<Rev_ThemeList> response) {
                Rev_ThemeList rev_themeList = response.body();
                if(isNetSuccess(rev_themeList)){
                    mThemeList.addAll(rev_themeList.list);

                    if(mThemeList.size() > 0){
                        compareList();
                        mThemeSelectListAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(ThemeSelectActivity.this,"등록된 테마가 없습니다",Toast.LENGTH_SHORT).show();
                        finishAni();
                    }

                }
            }

            @Override
            public void onFailure(Call<Rev_ThemeList> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);
        Intent i = new Intent();
        i.putExtra(Define.INTENTKEY_THEME_VALUE,getSelectedTheme());
        setResult(RESULT_OK,i);
        finish();
    }


    public void compareList(){
        if(mSelectedTheme != null){
            for(Data_Theme theme : mThemeList){
                if(mSelectedTheme.uuid.equals(theme.uuid)){
                    theme.isSelected = true;
                }
            }
        }
    }

    public void resetList(){
        for(Data_Theme theme : mThemeList){
            theme.isSelected = false;
        }
    }

    public Data_Theme getSelectedTheme(){
        for(Data_Theme theme : mThemeList){
            if(theme.isSelected){
                mSelectedTheme = theme;
            }
        }
        return mSelectedTheme;
    }
}
