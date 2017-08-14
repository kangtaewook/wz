package com.vinetech.wezone.Login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.vinetech.ui.custompopup.CustomPopupManager;
import com.vinetech.util.LibSystemManager;
import com.vinetech.util.LibValidCheck;
import com.vinetech.util.UIViewAnimation;
import com.vinetech.util.WezoneUtil;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Common.Activity_WebView;
import com.vinetech.wezone.Data.Data_Facebook;
import com.vinetech.wezone.Data.Data_Google;
import com.vinetech.wezone.Data.Data_KakaoTalk;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Main.MainActivity;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_PostLogin;
import com.vinetech.wezone.Wezone.GpsInfo;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends BaseActivity implements OnClickListener ,LoginFragmentDialog.LoginFragmentDialogLinstener, GoogleApiClient.OnConnectionFailedListener {

//    public static final String SCOPE = "oauth2:" + Scopes.PLUS_ME;
    public static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    public static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    public static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

    public static final int RC_AUTHORIZE_CONTACTS = 1003;

    static final String SEND_MAIL = "send_mail";

    private AccountManager mAccountManager;

    private String mEmail;

    private LinearLayout mLinearlayout_btn_google;
    private LinearLayout mLinearlayout_btn_facebook;

    private LinearLayout linearlayout_btn_kakao;

    private LinearLayout mLinearlayout_btn_create_account;
    private LinearLayout mLinearlayout_btn_login;

    private LinearLayout linearlayout_btn_privacy;

    private String mCurrentProviederType;

    private String mTempEmail;
    private String mTempPasswd;
    private String mTempName;

    private int mLangStateType;
    private boolean mIsSearch = false;
    private String mGender;

    private boolean isFinished = false;

    private CallbackManager callbackManager;
    //	private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private CustomPopupManager mCustomPopupManager;

    private EditText edittext_email;
    private LinearLayout linearlayout_edit_area;


    private LoginFragmentDialog mCodeDialog;


    private int currentCount = 0;
    private int MODE_COUNT = 9;

    private Timer clickTimer;

    private GpsInfo mGpsInfo;

    private String token;
    private static final String TAG = "MyFirebaseIIDService";

    //kakao
    private SessionCallback mCallback;

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);


        String token = FirebaseInstanceId.getInstance().getToken();
        if(token != null){
            CryptPreferences.putString(m_Context, Define.SHARE_KEY_PUSH_TOKEN,token);
        }

        //kakao
        mCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(mCallback);
        Session.getCurrentSession().checkAndImplicitOpen();

        mAccountManager = AccountManager.get(this);

//        imageview_pager_background = (ImageView) findViewById(R.id.imageview_pager_background);
//        mViewPager = (ViewPager) findViewById(R.id.login_viewpager);
//        mPageControl = (PageControl) findViewById(R.id.login_pagecontrol);

        linearlayout_edit_area = (LinearLayout) findViewById(R.id.linearlayout_edit_area);
        edittext_email = (EditText) findViewById(R.id.edittext_email);

        boolean isNeedConfirm = CryptPreferences.getBoolean(m_Context, IS_EMAIL_CONFIRM);
        if (isNeedConfirm) {
            String email = CryptPreferences.getString(m_Context, SEND_MAIL, "");
            edittext_email.setText(email);

            mEmail = email;
            mCodeDialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_EMAIL_CONFIRM, this, email);
            mCodeDialog.show(getFragmentManager(), "loginFragment");
        }else{

            String provideType = CryptPreferences.getCryptString(m_Context, Define.SHARE_KEY_PROVIDER_TYPE,"");

            if(WezoneUtil.isEmptyStr(provideType) != false && "L".equals(provideType)){
                String mail = CryptPreferences.getCryptString(m_Context, Define.SHARE_KEY_LANGTUDY_ID, "");
                edittext_email.setText(mail);
            }else{

                String mail = getGoogleId();
                if (mail != null) {
                    edittext_email.setText(mail);
                }
            }
        }

        mLinearlayout_btn_login = (LinearLayout) findViewById(R.id.linearlayout_btn_login);
        mLinearlayout_btn_login.setOnClickListener(this);

        linearlayout_btn_kakao = (LinearLayout) findViewById(R.id.linearlayout_btn_kakao);
        linearlayout_btn_kakao.setOnClickListener(this);

        mLinearlayout_btn_google = (LinearLayout) findViewById(R.id.linearlayout_btn_google);
        mLinearlayout_btn_google.setOnClickListener(this);


        mLinearlayout_btn_facebook = (LinearLayout) findViewById(R.id.linearlayout_btn_facebook);
        mLinearlayout_btn_facebook.setOnClickListener(this);

        linearlayout_btn_privacy = (LinearLayout) findViewById(R.id.linearlayout_btn_privacy);
        linearlayout_btn_privacy.setOnClickListener(this);


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult result) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(FacebookException error) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        });

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    com.facebook.Profile oldProfile,
                    com.facebook.Profile currentProfile) {
                // TODO Auto-generated method stub

                if(currentProfile != null){
                    Data_Facebook df = new Data_Facebook();
                    df.id = currentProfile.getId();
                    df.firstName = currentProfile.getFirstName();
                    df.middleName = currentProfile.getMiddleName();
                    df.lastName = currentProfile.getLastName();
                    df.name = currentProfile.getName();
//                df.linkUri = currentProfile.getLinkUri();
                    df.imgUri = currentProfile.getProfilePictureUri(512, 512).toString();

                    getShare().setFacebookData(df);

                    mEmail = null;
                    sendLogin("F",getShare().getFacebookData().id);
                }
            }
        };


        //하단 가상 키보드 여부
        boolean hasMenuKey = ViewConfiguration.get(LoginActivity.this).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if(!hasMenuKey && !hasBackKey) {

        }else{

        }

        setDefaultLoginData();


        mGpsInfo = new GpsInfo(LoginActivity.this);
        // GPS 사용유무 가져오기
        if (mGpsInfo.isGetLocation()) {

            getShare().getLoginParam().longitude = String.valueOf(mGpsInfo.getLongitude());
            getShare().getLoginParam().latitude = String.valueOf(mGpsInfo.getLatitude());

        } else {
            // GPS 를 사용할수 없으므로
            mGpsInfo.showSettingsAlert();
        }

    }


    public void setDefaultLoginData(){

        //특정폰에서 Splash 에서 저장한 데이터가 사라진다.ㅡㅡ;;
        if(getShare().getLoginParam() != null || getShare().getLoginParam().device_os_type != null){
            LibSystemManager mLibSystemManager = getShare().getLibSystemManager();

            getShare().getLoginParam().device_id = mLibSystemManager.getDeviceId();
            getShare().getLoginParam().device_model = mLibSystemManager.getDeviceName();
            getShare().getLoginParam().device_os_ver = mLibSystemManager.getOSVersion();
            getShare().getLoginParam().device_os_type = "android";
            getShare().getLoginParam().app_ver = mLibSystemManager.getAppVersion(this);
            getShare().getLoginParam().sys_lang = LibSystemManager.getDefaultLanguage();

//            getShare().getLoginParam().user_localtime_gmt = LibDateUtil.getCurrentGMT();

            getShare().getLoginParam().push_token = CryptPreferences.getCryptString(m_Context, Define.SHARE_KEY_PUSH_TOKEN, "");
//            getShare().getLoginParam().push_token = token;
        }
    }

    public String getGoogleId() {

        String str = null;

        String[] accountTypes = new String[]{"com.google"};

        for (Account account : AccountManager.get(LoginActivity.this).getAccounts()) {
            if (accountTypes[0].equals(account.type)) {
                str = account.name;
            }
        }

        return str;
    }

    private void authorizeContactsAccess() {
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
            try {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_AUTHORIZE_CONTACTS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);

        LoginManager.getInstance().logOut();
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);

        if (mCustomPopupManager != null) {
            mCustomPopupManager.onDestroy();
            mCustomPopupManager = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();

        Session.getCurrentSession().removeCallback(mCallback);

        if (mCustomPopupManager != null) {
            mCustomPopupManager.onDestroy();
            mCustomPopupManager = null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // uiHelper.onActivityResult(requestCode, resultCode, data);

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
//            if (resultCode == RESULT_OK) {
//                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
////                getAccountUsername();
//
//            } else if (resultCode == RESULT_CANCELED) {
//                //Toast.makeText(this, "You must pick an account",
//                //Toast.LENGTH_SHORT).show();
//            }
//        }else
        if (requestCode == RC_AUTHORIZE_CONTACTS) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Data_Google data = new Data_Google();
            data.name = acct.getDisplayName();
            data.id = acct.getId();
            data.given_name = acct.getGivenName();
            data.family_name = acct.getFamilyName();
            data.picture = acct.getPhotoUrl();
            data.email = acct.getEmail();

            getShare().setGoogleData(data);
            getShare().setGoogleToken(acct.getIdToken());

            mTempEmail = mEmail = data.email;

            sendLogin("G",getShare().getGoogleData().id);

        } else {
            // Signed out, show unauthenticated UI.
            String errorMsg = getResources().getString(R.string.google_auth_error);
            Toast.makeText(LoginActivity.this,errorMsg, Toast.LENGTH_LONG).show();

            getShare().setGoogleToken(null);
        }
    }



    public void checkUserMail(String email) {
        showProgressPopup();
        Call<Rev_Base> callData = wezoneRestful.postUserMail(email);
        callData.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revData = response.body();
                if(isNetSuccess(revData)){
                    SignPasswordActivity.startActivity(LoginActivity.this,mTempEmail);
                }else{
                    checkLoginError(revData);
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
                hidePorgressPopup();
            }
        });
//        getShare().getNetworkManager().sendPacket(LoginActivity.this, new Send_CheckMail(email), this);
    }


    public void sendLogin(String type, String id) {
        showProgressPopup();
        getShare().getLoginParam().provider_type = type;
        getShare().getLoginParam().provider_email = mEmail;
        getShare().getLoginParam().provider_id = id;

        Call<Rev_PostLogin> sendData = wezoneRestful.postLogin(getShare().getLoginParam());

        sendData.enqueue(new Callback<Rev_PostLogin>() {
            @Override
            public void onResponse(Call<Rev_PostLogin> call, Response<Rev_PostLogin> response) {
                Rev_PostLogin revData = response.body();

                if (isNetSuccess(revData)) {
                    CryptPreferences.putCryptString(m_Context,Define.SHARE_KEY_PROVIDER_TYPE,getShare().getLoginParam().provider_type);
                    CryptPreferences.putCryptString(m_Context,Define.SHARE_KEY_UUID,revData.user_info.uuid);
                    CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_NAME, revData.user_info.user_name);
                    CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_IMAGE_URL, revData.user_info.img_url);

                    HashMap<String, Object> beaconHash = new HashMap<>();
                    beaconHash.put("beaconInfo", revData.beacon_info);
                    String strData = getGson().toJson(beaconHash);
                    CryptPreferences.putString(getApplicationContext(), Define.SHARE_KEY_BEACON_INFO, strData);

                    getShare().setMyInfo(revData.user_info);

                    saveXmppServerInfo(revData.server_info);
                    initXmpp();

                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    moveActivity(i);
                    removeAllActivity();

                }else{
                    checkLoginError(revData);

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
    public void onClick(View v) {
        // TODO Auto-generated method stub

        int viewId = v.getId();

        switch (viewId) {
            case R.id.linearlayout_btn_google:{
                mCurrentProviederType = "G";
//                getAccountUsername();
                onClickGoogle();

            }
            break;
            case R.id.linearlayout_btn_facebook: {
                mCurrentProviederType = "F";
                onClickFaceBook();
            }
                break;

            case R.id.linearlayout_btn_kakao:{
                mCurrentProviederType = "K";
                try {
                    Method methodGetAuthTypes = LoginButton.class.getDeclaredMethod("getAuthTypes");
                    methodGetAuthTypes.setAccessible(true);
                    final List<AuthType> authTypes = (List<AuthType>) methodGetAuthTypes.invoke(this);
                    if(authTypes.size() == 1){
                        Session.getCurrentSession().open(authTypes.get(0), LoginActivity.this);
                    } else {
                        Method methodOnClickLoginButton = LoginButton.class.getDeclaredMethod("onClickLoginButton",List.class);
                        methodOnClickLoginButton.setAccessible(true);
                        methodOnClickLoginButton.invoke(this,authTypes);
                    }
                } catch (Exception e) {
                    Session.getCurrentSession().open(AuthType.KAKAO_ACCOUNT,LoginActivity.this);
                }
            }
            break;

            case R.id.linearlayout_btn_login: {

                mGpsInfo = new GpsInfo(LoginActivity.this);
                // GPS 사용유무 가져오기
                if (mGpsInfo.isGetLocation()) {

                    getShare().getLoginParam().longitude = String.valueOf(mGpsInfo.getLongitude());
                    getShare().getLoginParam().latitude = String.valueOf(mGpsInfo.getLatitude());

//                    int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//                    if (errorCode != ConnectionResult.SUCCESS) {
//                        String str = getResources().getString(R.string.google_play_not_available);
//                        Toast.makeText(LoginActivity.this, str, Toast.LENGTH_LONG).show();
//                    }

                    String id = edittext_email.getText().toString().trim();

                    if (id.equals("") || id == null) {
                        String str = getResources().getString(R.string.please_enter_id);
                        Toast.makeText(LoginActivity.this, str, Toast.LENGTH_SHORT).show();

                        UIViewAnimation.ShakeAnimation(LoginActivity.this, linearlayout_edit_area);
                        return;
                    }

                    if (!LibValidCheck.isValidEmail(id)) {
                        String str = getResources().getString(R.string.not_the_email_type);
                        Toast.makeText(LoginActivity.this, str, Toast.LENGTH_SHORT).show();

                        UIViewAnimation.ShakeAnimation(LoginActivity.this, linearlayout_edit_area);
                        return;
                    }

                    mCurrentProviederType = "W";

                    getShare().getLoginParam().provider_type = "W";
                    getShare().getLoginParam().login_id = id;
                    getShare().getLoginParam().login_passwd = "1111";

                    mTempEmail = id;
                    checkUserMail(id);

                } else {
                    // GPS 를 사용할수 없으므로
                    mGpsInfo.showSettingsAlert();
                }
            }
            break;

            case R.id.linearlayout_btn_privacy:{
                String title = getResources().getString(R.string.terms_of_service);
                Activity_WebView.startWebViewActivity(LoginActivity.this,Define.NETURL_TERNS_OF_SERVICE,title);
            }
                break;
        }
    }

    public void checkLoginError(Rev_Base revData){
        String loginErrorCode = revData.code;
        if (Rev_Base.LOGIN_ERROR_ID.equals(loginErrorCode)) {
                //패스워드 틀림 팝업.
                LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_ERROR, this);
                dialog.show(getFragmentManager(), "loginFragment");

            } else if (Rev_Base.LOGIN_ERROR_PW.equals(loginErrorCode)) {
                //패스워드 틀림 팝업
                LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_ERROR, this);
                dialog.show(getFragmentManager(), "loginFragment");

            } else if (Rev_Base.LOGIN_ERROR_WEZONE.equals(loginErrorCode)) {

                String email = edittext_email.getText().toString().trim();
                SignPasswordActivity.startActivity(this,email);

            } else if (Rev_Base.LOGIN_ERROR_TYPE.equals(loginErrorCode)) {
                //나올일이 없는데?

            } else if (Rev_Base.LOGIN_ERROR_UUID.equals(loginErrorCode)) {
                //UUID 내가 넣을껀데?

            } else if (Rev_Base.LOGIN_ERROR_PROVIDER.equals(loginErrorCode)) {
                StringBuffer str = new StringBuffer();

                if(!"W".equals(mCurrentProviederType)){
                    getShare().getLoginParam().provider_type = mCurrentProviederType;
                    SignNameActivity.startActivity(LoginActivity.this,"","");
                }

            } else if (Rev_Base.LOGIN_ERROR_EMAIL.equals(loginErrorCode)) {
//                String str = getResources().getString(R.string.the_unregistered_email);
//                Toast.makeText(m_Context, str, Toast.LENGTH_LONG).show();
                return;
            }

            else if (Rev_Base.LOGIN_ERROR_CODE.equals(loginErrorCode)) {
//                String str = getResources().getString(R.string.the_verification_number_is_incorrect);
//                Toast.makeText(m_Context, str, Toast.LENGTH_LONG).show();

                mCodeDialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_EMAIL_CONFIRM, this, mEmail);
                mCodeDialog.show(getFragmentManager(), "loginFragment");

                return;
            } else if (Rev_Base.LOGIN_ERROR_PW_FAIL.equals(loginErrorCode)) {
//                String str = getResources().getString(R.string.the_password_change_failed);
//                Toast.makeText(m_Context, str, Toast.LENGTH_LONG).show();
                return;
            } else if (Rev_Base.LOGIN_ERROR_ALREADY_EMAIl.equals(loginErrorCode)) {

                StringBuffer str = new StringBuffer();
                if ("G".equals(revData.data.provider_email)) {
                    Toast.makeText(m_Context,revData.msg, Toast.LENGTH_LONG).show();

//                    LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_LOGIN, this);
//                    dialog.show(getFragmentManager(), "loginFragment");

                } else if ("F".equals(revData.data.provider_email)) {
                    Toast.makeText(m_Context, revData.msg, Toast.LENGTH_LONG).show();

//                    LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_LOGIN, this);
//                    dialog.show(getFragmentManager(), "loginFragment");
                } else if("K".equals(revData.data.provider_email)){
                    Toast.makeText(m_Context, revData.msg, Toast.LENGTH_LONG).show();
                }
                else {

                    if("W".equals(mCurrentProviederType)){

                        String email = edittext_email.getText().toString().trim();
                        LoginPasswordActivity.startActivity(this,email);

                    }else{
//                        Toast.makeText(m_Context, revData.msg, Toast.LENGTH_LONG).show();

//                        LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_LOGIN, this,header.email);
//                        dialog.show(getFragmentManager(), "loginFragment");
                    }
                }
                return;
            } else {
                Toast.makeText(m_Context, revData.msg + "[" + loginErrorCode + "]", Toast.LENGTH_LONG).show();
            }
    }

    //팝업 리스너;;;;;
    @Override
    public void onClickGoogle() {
        // TODO Auto-generated method stub
        authorizeContactsAccess();
    }

    @Override
    public void onClickFaceBook() {
        // TODO Auto-generated method stub

        AccessToken token = AccessToken.getCurrentAccessToken();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    @Override
    public void onClickLogin(String id, String pw) {
        // TODO Auto-generated method stub

//        mTempEmail = id;
//        mTempPasswd = pw;
//        send_login(id, pw);
    }

    @Override
    public void onClickLost(String email) {
        // TODO Auto-generated method stub

        LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_SEND_EMAIL, this, email);
        dialog.show(getFragmentManager(), "loginFragment");

    }

    @Override
    public void onClickReTry() {

    }


    @Override
    public void onClickSendMail(String email) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClickSendCode(String code) {

    }

    @Override
    public void onClickPassChange(String newpass) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBackPressed() {

        // TODO Auto-generated method stub

        if (isFinished) {
            super.onBackPressed();
        } else {
            isFinished = true;
            String str = getResources().getString(R.string.press_again_to_exit);
            Toast.makeText(m_Context, str, Toast.LENGTH_SHORT).show();
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                isFinished = false;
            }
        }, 5000);
    }


    //javascript 메소드와 조금 다르다.
    //
    public String getStringWithEncode(String src) {
        String result = null;

        try {
            result = URLEncoder.encode(src, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e) {
            result = src;
        }
        return result;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            requestMeFromKakao();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }

    private void requestMeFromKakao() {

        List<String> propertyKeys = new ArrayList<String>();
        propertyKeys.add("kaccount_email");
        propertyKeys.add("nickname");
        propertyKeys.add("profile_image");
        propertyKeys.add("thumbnail_image");

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile);
                Data_KakaoTalk data = new Data_KakaoTalk();
                data.id = String.valueOf(userProfile.getId());
                data.uuid = userProfile.getUUID();
                data.nickname = userProfile.getNickname();
                data.thumbnailImagePath = userProfile.getThumbnailImagePath();
                data.profileImagePath = userProfile.getProfileImagePath();

                getShare().setKakaoData(data);

                mEmail = null;
                sendLogin("K",getShare().getKakaoData().id);

            }

            @Override
            public void onNotSignedUp() {

            }
        }, propertyKeys, false);
    }


}
