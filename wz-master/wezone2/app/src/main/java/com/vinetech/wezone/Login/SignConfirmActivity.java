package com.vinetech.wezone.Login;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;

import com.vinetech.ui.custompopup.CustomPopupManager;
import com.vinetech.ui.custompopup.Progress_FrameAnim;
import com.vinetech.util.DownloadFile;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Facebook;
import com.vinetech.wezone.Data.Data_Google;
import com.vinetech.wezone.R;

public class SignConfirmActivity extends BaseActivity {

    private static String KEY_PROVIDE_TYPE = "provide_type";
    private static String KEY_PROVIDE_DATA = "data";

    public String mMail = null;
    public String mPass = null;

//    public Data_langData mLearnLang;
//    public Data_langData mTeachLang;

    public String mIsSearch = null;
    public String mName = null;
    public String mGender = null;


    public LinearLayout linearlayout_btn_start;

//    private LibSystemManager mLibSystemManager;

    public String mProvideType;

    public Data_Facebook data_facebook;
    public Data_Google data_google;

    private CustomPopupManager mCustomPopupManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_confirm);



//        mMail = getIntent().getStringExtra(KEY_MAIL);
//        mPass = getIntent().getStringExtra(KEY_PASS);
//        mLearnLang = (Data_langData) getIntent().getSerializableExtra(LEARN_LANG_DATA);
//        mTeachLang = (Data_langData) getIntent().getSerializableExtra(TEACH_LANG_DATA);
//        mIsSearch = getIntent().getStringExtra(KEY_SEARCH);
//        mName = getIntent().getStringExtra(KEY_NAME);
//        mGender = getIntent().getStringExtra(KEY_GENDER);

        mProvideType = getIntent().getStringExtra(KEY_PROVIDE_TYPE);
        if ("G".equals(mProvideType)) {
            data_google = (Data_Google) getIntent().getSerializableExtra(KEY_PROVIDE_DATA);
        } else if ("F".equals(mProvideType)) {
            data_facebook = (Data_Facebook) getIntent().getSerializableExtra(KEY_PROVIDE_DATA);
        }

        mCustomPopupManager = new CustomPopupManager(SignConfirmActivity.this);
        mCustomPopupManager.onCreatePopup(new Progress_FrameAnim(SignConfirmActivity.this, R.layout.custom_popup_progress, R.drawable.loading_ani_coin));

        linearlayout_btn_start = (LinearLayout) findViewById(R.id.linearlayout_btn_start);
        linearlayout_btn_start.setOnClickListener(mClickListener);


        send_getLocate();
    }

    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId) {
                case R.id.linearlayout_btn_start:
                    if ("G".equals(mProvideType)) {
                        googleImageUpload();
                    } else if ("F".equals(mProvideType)) {
                        facebookImageUpload();
                    } else if("K".equals(mProvideType)){

                    }
                    else {
                        send_RegiUser();
                    }
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        fadeoutFinish();
    }

    public void send_getLocate() {

//        setLocation();
//
//        String lat = getShare().getLoginData().user_latitude;
//        String lng = getShare().getLoginData().user_longitude;
//        String username = "langtudy1";
//        String style = "full";
//        String lang = Locale.getDefault().getLanguage();
//
//        if (LangtudyUtil.isEmptyStr(lat) == false && LangtudyUtil.isEmptyStr(lng) == false) {
//            getShare().getNetworkManager().sendPacketWithEncodedType(SignConfirmActivity.this, new Send_getLocateInfo(lat, lng, username, style, lang), mNetListener);
//        } else {
//            getShare().getNetworkManager().sendPacket(SignConfirmActivity.this, new Send_getGeoName(lang), mNetListener);
//        }
    }

//    public NetworkManager.NetworkLisener mNetListener = new NetworkManager.NetworkLisener() {
//        @Override
//        public void networkResult(Object resultCode, Object type, Object message) {
//
//            if (resultCode.equals(NetworkResultData.RESULT_SUCCESS)) {
//                if (type.equals("Send_getLocateInfo") || type.equals("Send_getGeoName")) {
//
//                    Rev_getLocateInfo info = (Rev_getLocateInfo) parsingContents(message, Rev_getLocateInfo.class);
//
//                    if (info == null)
//                        return;
//
//
//                    if (info.geonames.get(0) != null) {
//                        if (info.geonames.get(0).timezone != null) {
//                            getShare().getLoginData().user_localtime_gmt = info.geonames.get(0).timezone.gmtOffset;
//                        }
//                    }
//
//                    if (info.geonames.get(0).countryId != null) {
//                        getShare().getLoginData().country_id = info.geonames.get(0).countryId;
//                    }
//
//                    if (info.geonames.get(0).countryName != null) {
//                        getShare().getLoginData().country_name = info.geonames.get(0).countryName;
//                    }
//
//                    if (info.geonames.get(0).adminId1 != null) {
//                        getShare().getLoginData().state_id = info.geonames.get(0).adminId1;
//                    }
//
//                    if (info.geonames.get(0).adminName1 != null) {
//                        getShare().getLoginData().state_name = info.geonames.get(0).adminName1;
//                    }
//
//                    if (info.geonames.get(0).lat != null) {
//                        getShare().getLoginData().user_latitude = info.geonames.get(0).lat;
//                    }
//
//                    if (info.geonames.get(0).lng != null) {
//                        getShare().getLoginData().user_longitude = info.geonames.get(0).lng;
//                    }
//
//                    if (info.geonames.get(0).continentCode != null) {
//                        getShare().getLoginData().continentCode = info.geonames.get(0).continentCode;
//                    }
//
//                    if (LangtudyUtil.isEmptyStr(getShare().getLoginData().user_localtime_gmt)) {
//                        getShare().getLoginData().user_localtime_gmt = LangtudyDateUtil.getCurrentGMTwithNumberOnly();
//                        if (getShare().getLoginData().user_localtime_gmt.contains("error")) {
//                            getShare().getLoginData().user_localtime_gmt = "0";
//                        }
//                    }
//                } else if (type.equals("Send_RegiUser")) {
//
//                    Rev_User rev_User = (Rev_User) parsingContents(message, Rev_User.class);
//
//                    if (rev_User == null)
//                        return;
//
//                    getShare().getLoginData().user_info_uuid = rev_User.user_uuid;
//
//                    if ("G".equals(mProvideType)) {
//                        send_loginWithOauth(data_google.id);
//                    } else if ("F".equals(mProvideType)) {
//                        send_loginWithOauth(data_facebook.id);
//                    } else {
//                        send_login();
//                    }
//                } else if (type.equals("Send_Login")) {
////                Toast.makeText(m_Context, "로그인 성공", Toast.LENGTH_LONG).show();
//
//                    if (mCustomPopupManager != null) {
//                        mCustomPopupManager.onDestroy();
//                        mCustomPopupManager = null;
//                    }
//
//                    Rev_getLogin rev_getLogin = (Rev_getLogin) parsingContents(message, Rev_getLogin.class);
//
//                    if (rev_getLogin == null)
//                        return;
//
//
//                    if ("G".equals(mProvideType)) {
//                        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_PROVIDER_TYPE, "G");
//                        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_PROVIDER_ID, data_google.id);
//                        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_PROVIDER_MAIL, mMail);
//                    } else if ("F".equals(mProvideType)) {
//                        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_PROVIDER_TYPE, "F");
//                        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_PROVIDER_ID, data_facebook.id);
//                        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_PROVIDER_NAME, data_facebook.name);
//                    } else {
//
//                        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_PROVIDER_TYPE, "L");
//                        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_LANGTUDY_ID, mMail);
//                        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_LANGTUDY_PW, mPass);
//                    }
//
//                    CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_UUID, rev_getLogin.user_info.user_uuid);
//                    CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_NAME, rev_getLogin.user_info.user_name);
//                    CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_IMAGE_URL, rev_getLogin.user_info.user_img_url);
//
//                    getShare().getLoginData().user_info = rev_getLogin.user_info;
//                    getShare().setServerInfo(rev_getLogin.server_info);
//
//                    initXmpp();
//
////                    CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_SYS_LANG, rev_getLogin.user_info.user_sys_lang);
//
//                    Data_langData mainLang = getMainLang(rev_getLogin.user_info.languages);
//
//                    Intent i = new Intent(SignConfirmActivity.this, MainActivity.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//                    if ("L".equals(mainLang.lang_mode)) {
//                        i.putExtra(Define.INTENTKEY_SEARCH_MODE, MainActivity.MODE_SEARCH_LEARNER);
//                    } else {
//                        i.putExtra(Define.INTENTKEY_SEARCH_MODE, MainActivity.MODE_SEARCH_TEACHER);
//                    }
//                    i.putExtra(Define.INTENTKEY_SEARCH_LANG_CODE, mainLang.lang_code);
//
//                    removeAllActivity();
//                    moveActivity(i);
//                    finish();
//
//                    CryptPreferences.putBoolean(m_Context, IS_EMAIL_CONFIRM, false);
//                }
//
//            } else if (resultCode.equals(NetworkResultData.RESULT_FAIL)) {
//
//                if (mCustomPopupManager != null) {
//                    mCustomPopupManager.onDestroy();
//                    mCustomPopupManager = null;
//                }
//
//                if (type.equals(NetworkResultData.RESULT_TYPE_HTTPCODE)) {
//                    Toast.makeText(SignConfirmActivity.this, "Http Error Code " + ((Integer) message), Toast.LENGTH_LONG).show();
//                } else {
//                    String strTemp = getResources().getString(R.string.network_error);
//                    Toast.makeText(SignConfirmActivity.this, strTemp, Toast.LENGTH_LONG).show();
//                }
//            } else if (resultCode.equals(NetworkResultData.RESULT_LOGIN_ERROR)) {
//
//                Header header = (Header) parsingContents(message, Header.class);
//
//                if (header == null)
//                    return;
//
//                String loginErrorCode = header.code;
//                if (Header.LOGIN_ERROR_ALREADY_EMAIl.equals(loginErrorCode)) {
//                    if ("G".equals(mProvideType)) {
//                        send_loginWithOauth( data_google.id);
//                    } else if ("F".equals(mProvideType)) {
//                        send_loginWithOauth(data_facebook.id);
//                    } else {
//                        send_login();
//                    }
//                }
//            } else {
//                if (mCustomPopupManager != null) {
//                    mCustomPopupManager.onDestroy();
//                    mCustomPopupManager = null;
//                }
//
//                String strTemp = getResources().getString(R.string.network_error);
//                Toast.makeText(SignConfirmActivity.this, strTemp, Toast.LENGTH_LONG).show();
//            }
//
//        }
//    };

    public void googleImageUpload() {

        if (mCustomPopupManager != null) {
            mCustomPopupManager.onShow();
        }

        String downUrl = data_google.picture.toString();
        data_google.picture = null;

        final String path = Environment.getExternalStorageDirectory() + "/langtudy/temp/";
        final String fileName = "photo.png";

        DownloadFile download = new DownloadFile(SignConfirmActivity.this, new DownloadFile.DownloadListener() {
            @Override
            public void result(int nState) {
                switch (nState) {

                    case DownloadFile.DOWNLOAD_END: {

//                        String uploadUrl = Define.NETWORK_URL + Define.IMAGEURL + "user_uuid=g" + data_google.id;
//
//                        MultipartFileUploader upload = new MultipartFileUploader(m_Context,uploadUrl);
//
//                        upload.uploadImage(upload, "g" + data_google.id, path + fileName, new MultipartFileUploader.OnUploadListener() {
//
//                            @Override
//                            public void onUploadSuccess(Rev_Multipart multipartResult) {
//                                // TODO Auto-generated method stub
//
//                                if ("TRUE".equals(multipartResult.result)) {
//                                    data_google.picture = multipartResult.data.imageUrl;
//                                    downLoadNextStep();
//                                } else {
//                                    //Toast.makeText(m_Context, "이미지 업로드 실패["+multipartResult.error+"]", //Toast.LENGTH_SHORT).show();
//                                    data_google.picture = null;
//                                    downLoadNextStep();
//                                }
//                            }
//
//                            @Override
//                            public void onUploadFail() {
//                                // TODO Auto-generated method stub
//                                data_google.picture = null;
//                                downLoadNextStep();
//                            }
//                        });

                    }
                    case DownloadFile.DOWNLOAD_CANCEL: {
//					downLoadNextStep();

                    }
                    break;
                    case DownloadFile.DOWNLOAD_ERRROR: {

                    }
                    break;
                    case DownloadFile.DOWNLOAD_PROGRESS:
                        break;
                    default: {

                    }
                    break;
                }
            }
        });
        download.execute(downUrl, path, fileName);

    }

    public void facebookImageUpload() {

        if (mCustomPopupManager != null) {
            mCustomPopupManager.onShow();
        }

        String downUrl = data_facebook.imgUri.toString();
        data_facebook.imgUri = null;
        final String path = Environment.getExternalStorageDirectory() + "/langtudy/temp/";
        final String fileName = "photo.png";

        DownloadFile download = new DownloadFile(SignConfirmActivity.this, new DownloadFile.DownloadListener() {
            @Override
            public void result(int nState) {
                switch (nState) {

                    case DownloadFile.DOWNLOAD_END: {

//                        String uploadUrl = Define.NETWORK_URL + Define.IMAGEURL + "user_uuid=f" + data_facebook.id;
//
//                        MultipartFileUploader upload = new MultipartFileUploader(m_Context,uploadUrl);
//
//                        upload.uploadImage(upload, "f" + data_facebook.id, path + fileName, new MultipartFileUploader.OnUploadListener() {
//
//                            @Override
//                            public void onUploadSuccess(Rev_Multipart multipartResult) {
//                                // TODO Auto-generated method stub
//
//                                if ("TRUE".equals(multipartResult.result)) {
//                                    data_facebook.imgUri = multipartResult.data.imageUrl;
//                                    downLoadNextStep();
//                                } else {
//                                    //Toast.makeText(m_Context, "이미지 업로드 실패["+multipartResult.error+"]", //Toast.LENGTH_SHORT).show();
//                                    data_facebook.imgUri = null;
//                                    downLoadNextStep();
//                                }
//                            }
//
//                            @Override
//                            public void onUploadFail() {
//                                // TODO Auto-generated method stub
//                                data_facebook.imgUri = null;
//                                downLoadNextStep();
//                            }
//                        });

                    }
                    case DownloadFile.DOWNLOAD_CANCEL: {
//					downLoadNextStep();

                    }
                    break;
                    case DownloadFile.DOWNLOAD_ERRROR: {

                    }
                    break;
                    case DownloadFile.DOWNLOAD_PROGRESS:
                        break;
                    default: {

                    }
                    break;
                }
            }
        });
        download.execute(downUrl, path, fileName);
    }

    public void downLoadNextStep() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                send_RegiUser();
            }
        });
    }

    public void send_loginWithOauth(String id) {

//        String provider_type = mProvideType;
//        String device_id = getShare().getLoginData().device_id;
//        String push_token = getShare().getLoginData().push_token;
//        if (LangtudyUtil.isEmptyStr(push_token)) {
//            push_token = getShare().getLoginData().push_token = CryptPreferences.getCryptString(m_Context, Define.SHARE_KEY_PUSH_TOKEN, "");
//        }
//
//        String device_model = getShare().getLoginData().device_model;
//        String device_os_ver = getShare().getLoginData().device_os_ver;
//        String device_os_type = getShare().getLoginData().device_os_type;
//        String device_app_ver = getShare().getLoginData().device_app_ver;
//        String langtudy_id = "";
//        String langtudy_password = "";
//        String provider_id = id;
//        String provider_email = mMail;
//        String user_uuid = "";
//        String sys_lang = getUser_sys_lang();
//
//        getShare().getNetworkManager().sendPacket(SignConfirmActivity.this,
//                new Send_Login(provider_type, device_id, push_token,
//                        device_model, device_os_ver, device_os_type, device_app_ver, langtudy_id, langtudy_password,
//                        provider_id, provider_email, user_uuid, sys_lang), mNetListener);
    }

    public void send_login() {

//        String provider_type = mProvideType;
//        String device_id = getShare().getLoginData().device_id;
//        String push_token = getShare().getLoginData().push_token;
//        if (LangtudyUtil.isEmptyStr(push_token)) {
//            push_token = getShare().getLoginData().push_token = CryptPreferences.getCryptString(m_Context, Define.SHARE_KEY_PUSH_TOKEN, "");
//        }
//
//        String device_model = getShare().getLoginData().device_model;
//        String device_os_ver = getShare().getLoginData().device_os_ver;
//        String device_os_type = getShare().getLoginData().device_os_type;
//        String device_app_ver = getShare().getLoginData().device_app_ver;
//        String langtudy_id = mMail;
//        String langtudy_password = mPass;
//        String provider_id = "";
//        String provider_email = "";
//        String user_uuid = "";
//        String sys_lang = getUser_sys_lang();
//
//        getShare().getNetworkManager().sendPacket(SignConfirmActivity.this,
//                new Send_Login(provider_type, device_id, push_token,
//                        device_model, device_os_ver, device_os_type, device_app_ver, langtudy_id, langtudy_password,
//                        provider_id, provider_email, user_uuid, sys_lang), mNetListener);
    }


    public void send_RegiUser() {

//        String provider_type = mProvideType;
//        String provider_id = "";
//        String provider_email = "";
//        String provider_img = "";
//
//        String user_login_id = "";
//        String user_login_passwd = "";
//        String user_name;
//        if ("F".equals(mProvideType)) {
//            provider_id = data_facebook.id;
//            provider_email = "";
//            provider_img = data_facebook.imgUri;
//            user_name = mName;
//
//        } else if ("G".equals(mProvideType)) {
//            provider_id = data_google.id;
//            provider_email = mMail;
//            provider_img = data_google.picture;
//            user_name = mName;
//        } else {
//            user_login_id = mMail;
//            user_login_passwd = mPass;
//            user_name = getShare().getLoginData().user_name = mName;
//        }
//
//        String user_sys_lang = getShare().getLoginData().user_sys_lang;
//        String user_gender = getShare().getLoginData().user_gender = mGender;
//        String user_longitude = getShare().getLoginData().user_longitude;
//        String user_latitude = getShare().getLoginData().user_latitude;
//        String continentCode = getShare().getLoginData().continentCode;
//        String user_localtime_gmt = getShare().getLoginData().user_localtime_gmt;
//        String country_id = getShare().getLoginData().country_id;
//        String country_name = getShare().getLoginData().country_name;
//        String state_id = getShare().getLoginData().state_id;
//        String state_name = getShare().getLoginData().state_name;
//
//        String is_search_teach = mIsSearch;
//
//        ArrayList<Data_langData> langList = new ArrayList<Data_langData>();
//        langList.add(new Data_langData(mLearnLang.lang_mode, mLearnLang.lang_code, mLearnLang.lang_level));
//        langList.add(new Data_langData(mTeachLang.lang_mode, mTeachLang.lang_code, mTeachLang.lang_level));
//
//        getShare().getNetworkManager().sendPacket(SignConfirmActivity.this,
//                new Send_RegiUser(provider_type, provider_id, provider_email, provider_img, user_login_id, user_login_passwd, user_name, user_sys_lang,
//                        user_gender, user_longitude, user_latitude, continentCode, user_localtime_gmt, country_id, country_name,
//                        state_id, state_name, is_search_teach, langList), mNetListener);

    }
}
