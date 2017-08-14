package com.vinetech.wezone.Setting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.SendPacket.Send_PutPw;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingPasswordActivity extends BaseActivity {


    private EditText write_setting_now_password;
    private EditText write_setting_new_password;
    private EditText write_setting_new_password_ok;

    private String now_password;
    private String new_password;
    private String new_password_ok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);

        String title = getResources().getString(R.string.setting_my_info);
        setHeaderView(R.drawable.btn_back_white, title, R.drawable.btn_check);

        write_setting_now_password = (EditText) findViewById(R.id.write_setting_now_password);
        write_setting_new_password = (EditText) findViewById(R.id.write_setting_new_password);
        write_setting_new_password_ok = (EditText) findViewById(R.id.write_setting_new_password_ok);
    }


    private void putPw() {

        now_password = write_setting_now_password.getText().toString();
        new_password = write_setting_new_password.getEditableText().toString().trim();
        new_password_ok = write_setting_new_password_ok.getText().toString().trim();


        if (now_password.equals("") || now_password == null) {
            String str = "현재 비밀번호를 입력해주세요";
            Toast.makeText(SettingPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
            return;
        }

        if (now_password.length() < 8) {
            String str = "현재 비밀번호를 8자리 이상 입력해주세요";
            Toast.makeText(SettingPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
            return;
        }

        if (new_password.equals("") || new_password == null) {
            String str = "새 비밀번호를 입력해주세요";
            Toast.makeText(SettingPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
            return;
        }

        if (new_password.length() < 8) {
            String str = "새 비밀번호를 8자리 이상 입력해주세요";
            Toast.makeText(SettingPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
            return;
        }

        if (new_password_ok.equals("") || new_password_ok == null) {
            String str = "확인 비밀번호를 입력해주세요";
            Toast.makeText(SettingPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
            return;
        }

        if (new_password_ok.length() < 8) {
            String str = "확인 비밀번호를 8자리 이상 입력해주세요";
            Toast.makeText(SettingPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!new_password.equals(new_password_ok)){
            String str = "비밀번호가 상이합니다. 다시 확인해주세요.";
            Toast.makeText(SettingPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
            return;
        }

        Send_PutPw putPw = new Send_PutPw();

        putPw.passwd = now_password;
        putPw.new_passwd = new_password;

        Call<Rev_Base> PutPwCall = wezoneRestful.putPw(putPw);
        PutPwCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {

                Rev_Base revBase = response.body();
                if(isNetSuccess(revBase)){
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });

    }

    @Override
    public void onClickRightBtn(View v) {
        putPw();
    }

    @Override
    public void onClickLeftBtn(View v) {
        super.onClickLeftBtn(v);
    }

}
