package com.vinetech.wezone.Fcm; //작업중인 패키지 이름으로 수정

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.Define;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;



public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        if(Define.LOG_YN){
            Log.d(Define.LOG_TAG, "Refreshed token: " + token);
        }

        CryptPreferences.putString(getApplicationContext(), Define.SHARE_KEY_PUSH_TOKEN,token);
    }

}
