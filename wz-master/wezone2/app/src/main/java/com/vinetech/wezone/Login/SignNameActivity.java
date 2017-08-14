package com.vinetech.wezone.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vinetech.util.ByteLengthFilter;
import com.vinetech.util.WezoneUtil;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Facebook;
import com.vinetech.wezone.Data.Data_Google;
import com.vinetech.wezone.Data.Data_KakaoTalk;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_PostLogin;
import com.vinetech.wezone.SendPacket.Send_PostRegiste;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignNameActivity extends BaseActivity {

    private static String KEY_PROVIDE_TYPE = "provide_type";
    private static String KEY_PROVIDE_DATA = "data";
    private static String KEY_MAIL = "mail";
    private static String KEY_PASS = "pass";

    public static void startActivity(BaseActivity activity, String mail, String pw)
    {
        Intent intent = new Intent(activity, SignNameActivity.class);
        intent.putExtra(KEY_MAIL, mail);
        intent.putExtra(KEY_PASS, pw);
        activity.moveActivityWithFadeAni(intent);
    }

    public String mPorvideType = null;
    public String mMail = null;
    public String mPass = null;

    public String mName = null;

    public EditText edittext_username;
    public LinearLayout linearlayout_btn_next;


    public String mProvideType;

    public Data_Facebook data_facebook;
    public Data_Google data_google;
    public Data_KakaoTalk data_kakaoTalk;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_name);

        String title = getResources().getString(R.string.sing_up);

        mMail = getIntent().getStringExtra(KEY_MAIL);
        mPass = getIntent().getStringExtra(KEY_PASS);

        mProvideType = getShare().getLoginParam().provider_type;

        if("G".equals(mProvideType)){
            data_google = getShare().getGoogleData();
        }else if("F".equals(mProvideType)){
            data_facebook = getShare().getFacebookData();
        }else if("K".equals(mProvideType)){
            data_kakaoTalk = getShare().getKakaoData();
        }

        edittext_username = (EditText) findViewById(R.id.edittext_username);
        InputFilter[] byteFilter = new InputFilter[]{new ByteLengthFilter(26, "KSC5601")};
        edittext_username.setFilters(byteFilter);

        linearlayout_btn_next = (LinearLayout) findViewById(R.id.linearlayout_btn_next);
        linearlayout_btn_next.setOnClickListener(mListener);
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.linearlayout_btn_next:


                    mName = edittext_username.getText().toString().trim();

                    if (mName.equals("") || mName == null) {
                        String str = getResources().getString(R.string.please_enter_your_name);
                        Toast.makeText(SignNameActivity.this, str, Toast.LENGTH_SHORT).show();
                        return;
                    }


                    showProgressPopup();
                    Send_PostRegiste param = new Send_PostRegiste();
                    param.provider_type = getShare().getLoginParam().provider_type;

                    if("G".equals(mProvideType)){
                        param.provider_id = data_google.id;
                        param.provider_email = mMail;

                        getShare().getLoginParam().provider_id = data_google.id;
                        getShare().getLoginParam().provider_email = mMail;

                    }else if("F".equals(mProvideType)){
                        param.provider_id = data_facebook.id;
                        param.provider_email = mMail;

                        getShare().getLoginParam().provider_id = data_facebook.id;
                        getShare().getLoginParam().provider_email = mMail;
                    }else if("K".equals(mProvideType)){
                        param.provider_id = data_kakaoTalk.id;
                        param.provider_email = mMail;

                        getShare().getLoginParam().provider_id = data_kakaoTalk.id;
                        getShare().getLoginParam().provider_email = mMail;

                    }else{
                        param.login_id = mMail;
                        param.login_passwd = mPass;

                        getShare().getLoginParam().login_id = mMail;
                        getShare().getLoginParam().login_passwd = mPass;
                    }
                    param.user_name = mName;

                    param.longitude = getShare().getLoginParam().longitude;
                    param.latitude = getShare().getLoginParam().latitude;



                    Call<Rev_Base> sendData = wezoneRestful.postRegister(param);

                    sendData.enqueue(new Callback<Rev_Base>() {
                        @Override
                        public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                            Rev_Base revData = response.body();

                            if(isNetSuccess(revData)){

                                getShare().getLoginParam().uuid = revData.uuid;
                                sendLogin();
                            }

                            hidePorgressPopup();
                        }

                        @Override
                        public void onFailure(Call<Rev_Base> call, Throwable t) {
                            hidePorgressPopup();
                        }
                    });
                    break;
            }

        }
    };

    public void sendLogin() {

        showProgressPopup();

        String push_token = getShare().getLoginParam().push_token;
        if (WezoneUtil.isEmptyStr(push_token)) {
            getShare().getLoginParam().push_token = CryptPreferences.getCryptString(m_Context, Define.SHARE_KEY_PUSH_TOKEN, "");
        }

        Call<Rev_PostLogin> sendData = wezoneRestful.postLogin(getShare().getLoginParam());

        sendData.enqueue(new Callback<Rev_PostLogin>() {
            @Override
            public void onResponse(Call<Rev_PostLogin> call, Response<Rev_PostLogin> response) {
                Rev_PostLogin revData = response.body();

                if (isNetSuccess(revData)) {
                    moveMainActivity(revData,null);
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_PostLogin> call, Throwable t) {
                hidePorgressPopup();
            }
        });

    }

    @Override
    public void onBackPressed() {
        fadeoutFinish();
    }

}
