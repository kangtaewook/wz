package com.vinetech.wezone.Theme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_ActionItem;
import com.vinetech.wezone.Data.Data_Theme;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.SendPacket.Send_PutMyTheme;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemeDetailActivity extends BaseActivity {

    public static final String THEME = "theme";

    public static void startActivity(BaseActivity activity, Data_Theme themeInfo) {
        Intent intent = new Intent(activity, ThemeDetailActivity.class);
        intent.putExtra(THEME,themeInfo);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_THEME);
    }

    private Data_Theme mTheme;

    private LinearLayout linearlayout_header_bg;

    private ImageView imageview_bg;

    private ImageView imageview_theme_icon;
    private TextView textview_name;
    private TextView textview_desc;

    private LinearLayout linearlayout_btn_add_theme;
    private LinearLayout linearlayout_btn_del_theme;
//    private LinearLayout linearlayout_btn_create_theme;
    private LinearLayout linearlayout_btn_area;


    private LinearLayout linearlayout_theme_area_01;
    private TextView textview_theme_name_01;
    private TextView textview_desc_01;

    private LinearLayout linearlayout_theme_area_02;
    private TextView textview_theme_name_02;
    private TextView textview_desc_02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_detail);

        setHeaderView(R.drawable.btn_back_white,null,0);

        mTheme = (Data_Theme)getIntent().getSerializableExtra(THEME);

        linearlayout_header_bg = (LinearLayout) findViewById(R.id.linearlayout_header_bg);
        linearlayout_header_bg.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));

        imageview_bg = (ImageView) findViewById(R.id.imageview_bg);

        if(WezoneUtil.isEmptyStr(mTheme.bg_img_url) == false){
            showImageFromRemote(mTheme.bg_img_url,0,imageview_bg);
        }

        imageview_theme_icon = (ImageView) findViewById(R.id.imageview_theme_icon);
        textview_name = (TextView) findViewById(R.id.textview_name);
        textview_desc = (TextView) findViewById(R.id.textview_desc);

        imageview_theme_icon.setBackgroundResource(WezoneUtil.getThemeIconBgResId(mCurrentTheme,mTheme.resIdPos));

        if(WezoneUtil.isEmptyStr(mTheme.img_url) == false){
            showImageFromRemote(mTheme.img_url,0,imageview_theme_icon);
        }

        textview_name.setText(mTheme.name);
        textview_desc.setText(mTheme.content);

        linearlayout_btn_add_theme = (LinearLayout) findViewById(R.id.linearlayout_btn_add_theme);
        linearlayout_btn_add_theme.setOnClickListener(mClickListener);
        linearlayout_btn_del_theme = (LinearLayout) findViewById(R.id.linearlayout_btn_del_theme);
        linearlayout_btn_del_theme.setOnClickListener(mClickListener);

        linearlayout_btn_area = (LinearLayout) findViewById(R.id.linearlayout_btn_area);

//        linearlayout_btn_create_theme = (LinearLayout) findViewById(R.id.linearlayout_btn_create_theme);
//        linearlayout_btn_create_theme.setOnClickListener(mClickListener);


        reloadButton();

        linearlayout_theme_area_01 = (LinearLayout) findViewById(R.id.linearlayout_theme_area_01);
        textview_theme_name_01 = (TextView) findViewById(R.id.textview_theme_name_01);
        textview_desc_01 = (TextView) findViewById(R.id.textview_desc_01);
        if(mTheme.theme_in != null){
            textview_theme_name_01.setText(mTheme.theme_in.name);
            textview_desc_01.setText(Data_ActionItem.getTitleText(mTheme.theme_in.id));
        }else{
            linearlayout_theme_area_01.setVisibility(View.GONE);
        }

        linearlayout_theme_area_02 = (LinearLayout) findViewById(R.id.linearlayout_theme_area_02);
        textview_theme_name_02 = (TextView) findViewById(R.id.textview_theme_name_02);
        textview_desc_02 = (TextView) findViewById(R.id.textview_desc_02);
        if(mTheme.theme_out != null) {
            textview_theme_name_02.setText(mTheme.theme_out.name);
            textview_desc_02.setText(Data_ActionItem.getTitleText(mTheme.theme_out.id));
        }else{
            linearlayout_theme_area_02.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTransparentHeaderView();
    }

    public void reloadButton(){
        if(WezoneUtil.isEmptyStr(mTheme.uuid)){
            linearlayout_btn_add_theme.setVisibility(View.VISIBLE);
            linearlayout_btn_area.setVisibility(View.GONE);
        }else{
            linearlayout_btn_add_theme.setVisibility(View.GONE);
            linearlayout_btn_area.setVisibility(View.VISIBLE);
        }
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            switch (viewId){
                case R.id.linearlayout_btn_add_theme:
                    putTheme(mTheme.theme_id);
                    break;

                case R.id.linearlayout_btn_del_theme:
                    deleteTheme(mTheme.theme_id);
                    break;

//                case R.id.linearlayout_btn_create_theme:
//                    ThemeZoneManageActivity.startActivityWithRegist(ThemeDetailActivity.this,mTheme);
//                    break;
            }
        }
    };

    public void putTheme(final String theme_id){
        Send_PutMyTheme param = new Send_PutMyTheme();
        param.theme_id = theme_id;

        Call<Rev_Base> themeCall = wezoneRestful.putTheme(param);
        themeCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base rev_base = response.body();
                if(isNetSuccess(rev_base)){
                    mTheme.uuid = getUuid();
                    reloadButton();
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
                    mTheme.uuid = null;
                    reloadButton();
                }
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });
    }
}
