package com.vinetech.wezone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vinetech.util.FileCache;
import com.vinetech.util.LibSystemManager;
import com.vinetech.util.WezoneUtil;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.Data.Data_PushData;
import com.vinetech.wezone.Gcm.GcmManager;
import com.vinetech.wezone.Login.LoginActivity;
import com.vinetech.wezone.RevPacket.Rev_PostLogin;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 최초 실행되는 화면
 *
 * 로그인을 위한 개인정보 셋팅
 * 로그인 화면으로 이동 및 자동로그인 수행
 *
 */

public class SplashActivity extends BaseActivity implements GcmManager.GcmManagerLinstener {

    private GcmManager mGcmManager;
    private Data_PushData mPushValue;

    private LibSystemManager mLibSystemManager;

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FileCache fileCache = FileCache.getInstance();
        fileCache.init(this);

        FirebaseMessaging.getInstance().subscribeToTopic("news");

        String token = FirebaseInstanceId.getInstance().getToken();
        if(token != null){
            CryptPreferences.putString(m_Context, Define.SHARE_KEY_PUSH_TOKEN,token);
        }


        mPushValue = (Data_PushData) getIntent().getSerializableExtra(Define.INTENTKEY_PUSH_VALUE);

        AfterCheckGoogle();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mPushValue = (Data_PushData) intent.getSerializableExtra(Define.INTENTKEY_PUSH_VALUE);

    }


    public void AfterCheckGoogle(){
        mLibSystemManager = getShare().getLibSystemManager();

        //로그인 전 가져올수 있는 데이터를 모두 가져온다.
        getShare().getLoginParam().device_id = mLibSystemManager.getDeviceId();
        getShare().getLoginParam().device_model = mLibSystemManager.getDeviceName();
        getShare().getLoginParam().device_os_ver = mLibSystemManager.getOSVersion();
        getShare().getLoginParam().device_os_type = "android";
        getShare().getLoginParam().app_ver = mLibSystemManager.getAppVersion(this);
        getShare().getLoginParam().sys_lang = "kor";
        getShare().getLoginParam().push_token = CryptPreferences.getCryptString(m_Context, Define.SHARE_KEY_PUSH_TOKEN, "");
//        getShare().getLoginParam().push_token = token;

        mLibSystemManager.setLocationManager();

        mHandler.sendEmptyMessageDelayed(0,500);
    }

    private final Handler mHandler = new Handler (new Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message message) {

            String providerType = CryptPreferences.getCryptString(m_Context,Define.SHARE_KEY_PROVIDER_TYPE,"");
            String uuid = CryptPreferences.getCryptString(m_Context,Define.SHARE_KEY_UUID,"");

            if(WezoneUtil.isEmptyStr(providerType) || WezoneUtil.isEmptyStr(uuid)){
                goToLogin();
            }else{
                sendLogin(providerType,uuid);
            }
            return false;
        }
    });

    public void sendLogin(String type, String uuid) {

        getShare().getLoginParam().provider_type = type;
        getShare().getLoginParam().uuid = uuid;

        Call<Rev_PostLogin> sendData = wezoneRestful.postLogin(getShare().getLoginParam());

        sendData.enqueue(new Callback<Rev_PostLogin>() {
            @Override
            public void onResponse(Call<Rev_PostLogin> call, Response<Rev_PostLogin> response) {
                Rev_PostLogin revData = response.body();

                if (isNetSuccess(revData)) {
                    moveMainActivity(revData,mPushValue);
                }else{
                    moveLoginActivity(mPushValue,false);
                }
            }

            @Override
            public void onFailure(Call<Rev_PostLogin> call, Throwable t) {

            }
        });

    }

    public void goToLogin(){
        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        moveActivity(i);
        finish();
    }



    @Override
    public void onFinish(String regiId) {
        if (!WezoneUtil.isEmptyStr(regiId)) {
//            CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_PUSH_TOKEN, regiId);
//            CryptPreferences.putCryptString(m_Context, token, regiId);
//            getShare().getLoginParam().push_token = regiId;
        }
    }

    @Override
    public void onError() {
        String str = getResources().getString(R.string.google_play_not_available);
        Toast.makeText(SplashActivity.this, str,Toast.LENGTH_LONG).show();
    }
}
