package com.vinetech.wezone.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

public class SettingActivity extends BaseActivity {

    private TextView textview_account_email;

    private LinearLayout linearlayout_btn_password;

    private LinearLayout linearlayout_btn_blue;
    private LinearLayout linearlayout_btn_red;
    private LinearLayout linearlayout_btn_yellow;

    private LinearLayout linearlayout_btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        String title = getResources().getString(R.string.setting_title);
        setHeaderView(R.drawable.btn_back_white,title,0);

        textview_account_email = (TextView) findViewById(R.id.textview_account_email);
        //login_id == null or "" 일 때
        textview_account_email.setText(getShare().getLoginParam().login_id);
        if(WezoneUtil.isEmptyStr(getShare().getLoginParam().login_id) || getShare().getLoginParam().login_id == null){
            if(WezoneUtil.isEmptyStr(getShare().getMyInfo().provider_email)) {
                textview_account_email.setText(getShare().getMyInfo().user_name);
            }
        }
        linearlayout_btn_password = (LinearLayout) findViewById(R.id.linearlayout_btn_password);
        linearlayout_btn_password.setVisibility(View.GONE);

        if(WezoneUtil.isEmptyStr(getShare().getLoginParam().provider_type) == false && getShare().getLoginParam().provider_type.equals("W")){
            linearlayout_btn_password.setVisibility(View.VISIBLE);
        }
        linearlayout_btn_password.setOnClickListener(mClickListener);
        linearlayout_btn_blue = (LinearLayout) findViewById(R.id.linearlayout_btn_blue);
        linearlayout_btn_blue.setOnClickListener(mClickListener);
        linearlayout_btn_red = (LinearLayout) findViewById(R.id.linearlayout_btn_red);
        linearlayout_btn_red.setOnClickListener(mClickListener);
        linearlayout_btn_yellow = (LinearLayout) findViewById(R.id.linearlayout_btn_yellow);
        linearlayout_btn_yellow.setOnClickListener(mClickListener);
        linearlayout_btn_logout = (LinearLayout) findViewById(R.id.linearlayout_btn_logout);
        linearlayout_btn_logout.setOnClickListener(mClickListener);

        if(mCurrentTheme == Define.THEME_BLUE){
            linearlayout_btn_blue.setSelected(true);
        }else if(mCurrentTheme == Define.THEME_RED){
            linearlayout_btn_red.setSelected(true);
        }else{
            linearlayout_btn_yellow.setSelected(true);
        }
    }

    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            switch (viewId){
                case R.id.linearlayout_btn_password:
                    moveActivity(new Intent(SettingActivity.this, SettingPasswordActivity.class));
                    break;
                case R.id.linearlayout_btn_push:
                    break;
                case R.id.linearlayout_btn_beacon:
                    break;
                case R.id.linearlayout_btn_blue:
                    linearlayout_btn_blue.setSelected(true);
                    linearlayout_btn_red.setSelected(false);
                    linearlayout_btn_yellow.setSelected(false);

                    CryptPreferences.putInt(SettingActivity.this,Define.WEZONE_THEME,Define.THEME_BLUE);
                    reloadHeaderView();
                    break;
                case R.id.linearlayout_btn_red:
                    linearlayout_btn_blue.setSelected(false);
                    linearlayout_btn_red.setSelected(true);
                    linearlayout_btn_yellow.setSelected(false);

                    CryptPreferences.putInt(SettingActivity.this,Define.WEZONE_THEME,Define.THEME_RED);
                    reloadHeaderView();
                    break;
                case R.id.linearlayout_btn_yellow:
                    linearlayout_btn_blue.setSelected(false);
                    linearlayout_btn_red.setSelected(false);
                    linearlayout_btn_yellow.setSelected(true);

                    CryptPreferences.putInt(SettingActivity.this,Define.WEZONE_THEME,Define.THEME_YELLOW);
                    reloadHeaderView();
                    break;
                case R.id.linearlayout_btn_logout:
                    moveLoginActivity(null,true);
                    break;

            }
        }
    };

}
