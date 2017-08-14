package com.vinetech.wezone;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.gson.Gson;
import com.kakao.auth.Session;
import com.vinetech.beacon.BluetoothLeDevice;
import com.vinetech.beacon.BluetoothLeService;
import com.vinetech.ui.custompopup.CustomPopupManager;
import com.vinetech.ui.custompopup.Progress_FrameAnim;
import com.vinetech.util.AddCookiesInterceptor;
import com.vinetech.util.LibDateUtil;
import com.vinetech.util.LibSystemManager;
import com.vinetech.util.ReceivedCookiesInterceptor;
import com.vinetech.util.UIControl;
import com.vinetech.util.WezoneUtil;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.Beacon.BeaconSharedActivity;
import com.vinetech.wezone.Data.Data_Chat_UserList;
import com.vinetech.wezone.Data.Data_MsgKey;
import com.vinetech.wezone.Data.Data_Notice;
import com.vinetech.wezone.Data.Data_OtherUser;
import com.vinetech.wezone.Data.Data_PushData;
import com.vinetech.wezone.Data.Data_ServerInfo;
import com.vinetech.wezone.Data.Data_UnRead;
import com.vinetech.wezone.Fcm.FirebaseMessagingService;
import com.vinetech.wezone.Login.LoginActivity;
import com.vinetech.wezone.Main.MainActivity;
import com.vinetech.wezone.Message.ChattingActivity;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_Beacon;
import com.vinetech.wezone.RevPacket.Rev_Board;
import com.vinetech.wezone.RevPacket.Rev_PostLogin;
import com.vinetech.wezone.RevPacket.Rev_Upload;
import com.vinetech.wezone.RevPacket.Rev_Wezoninfo;
import com.vinetech.wezone.SendPacket.Send_PutWezoneDelete;
import com.vinetech.wezone.Wezone.WezoneActivity;
import com.vinetech.wezone.Wezone.WezoneBoardDetailActivity;
import com.vinetech.xmpp.LibXmppManager;
import com.vinetech.xmpp.onXmppChatListener;

import org.jivesoftware.smack.XMPPException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 모든 화면(Activity)의 부모 클래스
 *
 * 1. 화면에서 공통으로 사용하는 업무 수행
 * 2. 비콘 brodcating, xmpp 업무 담당
 *
 */

public class BaseActivity extends AppCompatActivity implements onXmppChatListener
{

    public static final String IS_EMAIL_CONFIRM = "is_email_confirm";

    public static int LOADING_IMAGE_CHECK =  0; //비콘 알림 액션에서 용량이 큰 이미지 보이기를 설정할 때 오래 걸리는 화면에 로딩이미지를 넣기 위해 쓰임
    public static final int LOADING_IMAGE_SHOW =  0;
    public static final int LOADING_IMAGE_HIDE =  1;

    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private CustomPopupManager mCustomPopupManager;
    public WezoneRestful wezoneRestful;

    protected Context m_Context;

    public int mCurrentTheme = Define.THEME_BLUE;

    private Gson m_Gson = new Gson();
    public final Gson getGson() {
        return m_Gson;
    }

    protected boolean isStageMode;
    public boolean isStageMode(){
        return isStageMode;
    }

    private ShareApplication m_Share;

    private PopupWindow mPopupWindow;

    public final ShareApplication getShare() {

        if (m_Share == null) {
            m_Share = (ShareApplication) getApplication();
        }
        return m_Share;
    }

    protected Toolbar main_toolbar;
    protected LinearLayout leftBtn;
    protected LinearLayout rightBtn;
    protected TextView textview_navi_title;

    protected String user_uuid;
    protected String user_nmae;
    protected String user_img_url;

    public String getUuid() {
        if(user_uuid == null){
            user_uuid = CryptPreferences.getCryptString(m_Context, Define.SHARE_KEY_UUID, "");
        }
        return user_uuid;
    }

    public String getName() {

        if(user_nmae == null){
            user_nmae = CryptPreferences.getCryptString(m_Context, Define.SHARE_KEY_NAME, "");
        }
        return user_nmae;
    }

    public String getImageUrl() {

        if(user_img_url == null){
            user_img_url = CryptPreferences.getCryptString(m_Context, Define.SHARE_KEY_IMAGE_URL, "");
        }
        return user_img_url;
    }

    protected ArrayList<String> scanableMac;

    private NotificationManager mNotificationManager;

    protected LibSystemManager mLibSystemManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_Context = getApplicationContext();

        mCustomPopupManager = new CustomPopupManager(BaseActivity.this);
        mCustomPopupManager.onCreatePopup(new Progress_FrameAnim(BaseActivity.this, R.layout.custom_popup_progress, R.drawable.loading_ani_coin));

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(Define.NET_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClient.readTimeout(Define.NET_READ_TIMEOUT,TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);
        httpClient.interceptors().add(new AddCookiesInterceptor(this));
        httpClient.interceptors().add(new ReceivedCookiesInterceptor(this));

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Define.BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        wezoneRestful = retrofit.create(WezoneRestful.class);

        isStageMode = CryptPreferences.getBoolean(m_Context,Define.SHARE_KEY_STAGE_MODE);
        if(isStageMode){
            Toast.makeText(BaseActivity.this,"STAGE MODE",Toast.LENGTH_SHORT).show();
        }

        mCurrentTheme = CryptPreferences.getInt(BaseActivity.this,Define.WEZONE_THEME,Define.THEME_BLUE);

        getShare().addActivity(this);

        if(getShare().isLogin()){

            connectXmpp();

//            int permissionCheck = ContextCompat.checkSelfPermission(BaseActivity.this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION);
//
//            if(permissionCheck == PackageManager.PERMISSION_DENIED){
//                ActivityCompat.requestPermissions(BaseActivity.this,
//                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//
//            }
//
//
//            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//                finish();
//            }
//
//            if(mBluetoothAdapter == null){
//                final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//                mBluetoothAdapter = bluetoothManager.getAdapter();
//
//                // Checks if Bluetooth is supported on the device.
//                if (mBluetoothAdapter == null) {
//                    Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
//                    finish();
//                    return;
//                }
//            }
        }

        mLibSystemManager = new LibSystemManager(getApplicationContext());
    }

    public void initXmpp(){
        Data_ServerInfo serverInfo = getShare().getServerInfo();

        //xmpp 로그인
        LibXmppManager.initXmpp(serverInfo, getShare().getMyInfo().uuid, getShare().getMyInfo().uuid, getShare().getLoginParam().device_id);
        LibXmppManager.getInstance().connect();
        LibXmppManager.getInstance().setXmppChatListener(this);
    }

    public void reConnectXmpp() {
        LibXmppManager xmppManager = LibXmppManager.getInstance();
        if (xmppManager != null && !xmppManager.isConnected()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectXmpp();
                }
            }, 300);
        }
    }

    protected final void connectXmpp() {
        LibXmppManager xmppManager = LibXmppManager.getInstance();
        if (xmppManager != null) {
            if (getShare().isLogin() == true) {
                if (WezoneUtil.isNetworkStateConnected(this) == false) {
                    xmppManager.close();
                    return;
                }

                if (xmppManager.isConnected() == false)
                    xmppManager.connect();

//                if (xmppManager.isMessageFilterListener() == false)
//                    xmppManager.setChatLinsteners();
            } else {
                xmppManager.close();
            }
        }
    }

    public void resetData() {

        LibXmppManager xmppManager = LibXmppManager.getInstance();
        if (xmppManager != null){
            xmppManager.close();
        }

        //메모리의 유저정보 삭제
        getShare().setMyInfo(null);
        getShare().getLoginParam().uuid = null;

        CryptPreferences.putString(getApplicationContext(), Define.SHARE_KEY_BEACON_INFO, null);

        //badge 카운트 초기화
//        getShare().removePushDataAll();
//        LangtudyUtil.setBadge(getApplicationContext(),0);

        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_PROVIDER_TYPE, null);
        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_UUID, null);

    }

    public void moveLoginActivity(Data_PushData pushData, boolean isLogout){

        if(isLogout){
            Session.getCurrentSession().close();
            Call<Rev_Base> revLogout = wezoneRestful.postLogout(getShare().getLoginParam().device_id);
            revLogout.enqueue(new Callback<Rev_Base>() {
                @Override
                public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {

                }

                @Override
                public void onFailure(Call<Rev_Base> call, Throwable t) {

                }
            });

            CryptPreferences.putBoolean(m_Context, IS_EMAIL_CONFIRM, false);

            Intent serviceIntent = new Intent(BaseActivity.this, BluetoothLeService.class);
            serviceIntent.putExtra("stop",true);
            startService(serviceIntent);

        }

        resetData();
        removeAllActivity();

        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if(pushData !=null){
            intent.putExtra(Define.INTENTKEY_PUSH_VALUE,pushData);
        }
        moveActivity(intent);
        finish();
    }

    public void moveSplashActivity(){
        Intent i = new Intent(BaseActivity.this, SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        moveActivity(i);
        finish();
    }

    public void moveMainActivity(Rev_PostLogin revData, Data_PushData pushData){
        CryptPreferences.putCryptString(m_Context,Define.SHARE_KEY_PROVIDER_TYPE,getShare().getLoginParam().provider_type);
        CryptPreferences.putCryptString(m_Context,Define.SHARE_KEY_UUID,revData.user_info.uuid);
        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_NAME, revData.user_info.user_name);
        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_IMAGE_URL, revData.user_info.img_url);

        HashMap<String, Object> beaconHash = new HashMap<>();
        beaconHash.put("beaconInfo", revData.beacon_info);
        String strData = getGson().toJson(beaconHash);
        CryptPreferences.putString(getApplicationContext(), Define.SHARE_KEY_BEACON_INFO, strData);

        getShare().setMyInfo(revData.user_info);

        String strCnt = revData.userlist == null ? "0" : revData.userlist.total_unread;
        if(revData.userlist.list != null){
            getShare().setUnReadList(revData.userlist.list);
        }else{
            getShare().setUnReadList(null);
        }

        saveXmppServerInfo(revData.server_info);
        initXmpp();

        Intent i = new Intent(BaseActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if(pushData !=null){
            i.putExtra(Define.INTENTKEY_PUSH_VALUE,pushData);
        }
        moveActivity(i);
        removeAllActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadHeaderView();

        LibXmppManager xmppManager = LibXmppManager.getInstance();

        if (xmppManager != null && getShare().isLogin()) {

            if (xmppManager.isMessageFilterListener() == false) {
                xmppManager.setChatLinsteners();
            }

            xmppManager.setXmppChatListener(this);

            reConnectXmpp();

        }

        if(xmppManager == null && getShare().isLogin()){
            initXmpp();
        }

        registerReceiver(mBeaconScanReceiver, makeScanIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBeaconScanReceiver);

        TimerTask backgroundCheck = new TimerTask() {

            @Override
            public void run() {
                ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
                if (!tasks.isEmpty()) {
                    ComponentName topActivity = tasks.get(0).topActivity;
                    if (!topActivity.getPackageName().equals(getApplicationContext().getPackageName())) {
                        // APP in background, do something
                        LibXmppManager xmppManager = LibXmppManager.getInstance();

                        if (xmppManager != null){
                            xmppManager.close();
                        }

//                        savePushData();
                        return;
                    }
                }
                return;
                // APP in foreground, do something else
            }
        };
//
        Timer isBackgroundChecker = new Timer();
        isBackgroundChecker.schedule(backgroundCheck, 1000);
    }

//    public void savePushData(){
//        HashMap<String, Object> pushHash = new HashMap<>();
//        pushHash.put("push_list",getShare().getPushDataList());
//
//        String data = getGson().toJson(pushHash);
//        CryptPreferences.putString(BaseActivity.this,Define.SHARE_KEY_PUSH_LIST_DATA,data);
//    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        getShare().removeActivity(this);
        destroyProgressPopup();
    }

    protected void removeAllActivity(){
        getShare().removeAllActivity();
    }



    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    public void setHeaderView(
            int leftBtnDrawableId,
            String title,
            int rightBtn1DrawableId){

        main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        leftBtn = (LinearLayout) findViewById(R.id.linearlayout_navi_btn_left);
        rightBtn =(LinearLayout) findViewById(R.id.linearlayout_navi_btn_right);
        textview_navi_title = (TextView) findViewById(R.id.textview_navi_title);


        if (leftBtnDrawableId != 0) {
            leftBtn.setVisibility(View.VISIBLE);
            UIControl.setBackgroundDrawable(m_Context, findViewById(R.id.imageview_navi_left), leftBtnDrawableId);
        } else {
            leftBtn.setVisibility(View.GONE);
        }

        leftBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onClickLeftBtn(v);
            }
        });

        if (rightBtn1DrawableId != 0) {
            rightBtn.setVisibility(View.VISIBLE);
            UIControl.setBackgroundDrawable(m_Context, findViewById(R.id.imageview_navi_right), rightBtn1DrawableId);
        } else {
            rightBtn.setVisibility(View.GONE);
        }

        rightBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onClickRightBtn(v);
            }
        });

        if (title == null || title.equals("")) {
            textview_navi_title.setVisibility(View.INVISIBLE);
        } else {
            textview_navi_title.setVisibility(View.VISIBLE);
            textview_navi_title.setText(title);
        }
    }

    public void reloadHeaderView(){

        main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if(main_toolbar != null){
            main_toolbar.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));
        }
    }

    public void setTransparentHeaderView(){
        main_toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(getCurrentThemeColorId())));
    }

    public int getCurrentThemeColorId(){
        mCurrentTheme = CryptPreferences.getInt(BaseActivity.this,Define.WEZONE_THEME,Define.THEME_BLUE);
        if(mCurrentTheme == Define.THEME_BLUE){
            return R.color.navi_blue;
        }else if(mCurrentTheme == Define.THEME_RED){
            return R.color.navi_red;
        }else{
            return R.color.navi_yellow;
        }
    }

    public void onClickLeftBtn(View v){
        finishAni();
    }
    public void onClickRightBtn(View v){}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAni();
    }

    public final void moveActivity(Context context, Class<?> classOft) {
        Intent intent = new Intent(context, classOft);
        startActivity(intent);
        overridePendingTransition(R.anim.cacaostory_start_enter, R.anim.cacaostory_start_exit);
    }

    public final void moveActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.cacaostory_start_enter, R.anim.cacaostory_start_exit);
    }

    public final void moveActivityForResult(Intent intent, int resultFlag) {
        startActivityForResult(intent, resultFlag);
        overridePendingTransition(R.anim.cacaostory_start_enter, R.anim.cacaostory_start_exit);
    }

    public final void moveActivityWithFadeAni(Intent i) {
        startActivity(i);
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    public final void moveActivityWithFadeAni(Context context, Class<?> classOft) {
        Intent intent = new Intent(context, classOft);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    public final void moveActivityWithFadeAniForResult(Intent i, int resultFlag) {
        startActivityForResult(i, resultFlag);
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    public final void finishActivity(int requestCode) {
        super.finishActivity(requestCode);
        this.overridePendingTransition(R.anim.cacaostory_end_enter, R.anim.cacaostory_end_exit);
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void finishAni() {
        super.finish();
        this.overridePendingTransition(R.anim.cacaostory_end_enter, R.anim.cacaostory_end_exit);
    }

    public final void fadeoutFinish() {
        super.finish();
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    public final void modalFinish() {
        super.finish();
        overridePendingTransition(R.anim.activity_stop, R.anim.activity_down_up_close);
    }

    public boolean isNetSuccess(Rev_Base revData){

        if(revData == null){
            return false;
        }else{
            if("200".equals(revData.code)){
                return true;
            }else{
                if(!WezoneUtil.isEmptyStr(revData.msg)){
                    String codeText = "code : " + revData.code + "\n";
                    Toast.makeText(BaseActivity.this,codeText + revData.msg,Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }
    }

    public Object parsingContents(Object json, Class<?> classOft) {
        try {
            return getGson().fromJson(((String) json).trim(), classOft);
        } catch (Exception e) {

            Toast.makeText(BaseActivity.this,"parsing error",Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //로그인.. 차후 교체하자.
    public void handleException(final Exception e) {

    }

    public void uploadImageFile(final String type, String status, String id, String filePath){
        LOADING_IMAGE_CHECK = LOADING_IMAGE_HIDE;
        showProgressPopup();
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("userfile",file.getName(), requestFile);

        Call<Rev_Upload> callUpload = wezoneRestful.upload(type,status,id, body);
        callUpload.enqueue(new Callback<Rev_Upload>() {
            @Override
            public void onResponse(Call<Rev_Upload> call, Response<Rev_Upload> response) {
                Rev_Upload resultData = response.body();
                if(isNetSuccess(resultData)){
                    uploadResult(type,resultData);
                }
                LOADING_IMAGE_CHECK = LOADING_IMAGE_SHOW;
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_Upload> call, Throwable t) {
                LOADING_IMAGE_CHECK = LOADING_IMAGE_SHOW;
                hidePorgressPopup();
            }
        });
    }

    /**
     * 하위에서 재정의해서 사용.
     * @param resultData
     */
    public void uploadResult(String type, Rev_Upload resultData){

    }


    public void showImageFromRemote(String url, int defalutResId,ImageView view){
        String fullUrl = Define.BASE_URL + url;
        if(defalutResId == 0){
            defalutResId = R.drawable.img_loading_black;
        }

        Activity activity = BaseActivity.this;
        if (activity.isFinishing())
            return;

        Glide.with(BaseActivity.this)
                .load(fullUrl)
                .crossFade()
                .placeholder(defalutResId)
                .crossFade().into(view);
    }

    public void showImageFromRemoteWithCircle(String url, int defalutResId,ImageView view){
        String fullUrl = Define.BASE_URL + url;
        if(defalutResId == 0){
            defalutResId = R.drawable.img_loading_black;
        }

        Activity activity = BaseActivity.this;
        if (activity.isFinishing())
            return;


        Glide.with(BaseActivity.this)
                .load(fullUrl)
                .crossFade()
                .placeholder(defalutResId)
                .crossFade().bitmapTransform(new CropCircleTransformation(BaseActivity.this)).into(view);
    }

    public void saveXmppServerInfo(ArrayList<Data_ServerInfo> infos){
        for(Data_ServerInfo info : infos){
            if("xmpp".equals(info.server_kind)){
                getShare().setServerInfo(info);
            }
        }
    }

    @Override
    public void onXmppConnected() {

    }

    @Override
    public void onXmppLoginOK() {
        for(Data_Chat_UserList chat : getShare().getUnReadList()){
            if("groupchat".equals(chat.kind)){
                //채팅방 입장.
                String roomId = chat.other_uuid;
                try {
                    LibXmppManager.getInstance().createGroupChat(roomId,getUuid());
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        }
        LibXmppManager.getInstance().joinRooms();
    }

    @Override
    public void onXmppClosedOnError(Exception e) {

    }

    @Override
    public void onXmppClosed() {

    }

    @Override
    public void onXmppReceiveCommand(MassegeType type, String fromID, ArrayList<Data_MsgKey> msgkeys) {

    }

    @Override
    public void onXmppReceiveMessege(MassegeType type, String roomJID, String fromID, String messege) {
//        Toast.makeText(BaseActivity.this,messege,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onXmppReceiveMessage(MassegeType type, String fromID, String sFrom, String other_user_uuid, String other_user_name, String other_user_img_url, String message, String msgKey) {

        Data_Chat_UserList item = new Data_Chat_UserList();

        if(type == MassegeType.groupchat){
            item.kind = "groupchat";
            item.other_uuid = fromID;
        }else{
            item.kind = "chat";
            item.other_uuid = other_user_uuid;
        }
        item.img_url = other_user_img_url;
        item.user_name = other_user_name;
        item.txt = message;
        item.push_flag = "T";

        int idx = getUserDataIndex(item.other_uuid);
        if(idx == -1){
            getShare().getUnReadList().add(item);

            if(isScreenOn()) {
                Toast.makeText(BaseActivity.this,"["+other_user_name+"] " + message,Toast.LENGTH_SHORT).show();
            } else {
               sendNotification(new Intent(),other_user_name,message);
            }

        }else{
            Data_MsgKey keyData = new Data_MsgKey();
            keyData.msgkey = msgKey;
//            if(getShare().getUnReadList().get(idx).unread == null && getShare().getUnReadList().get(idx).unread.msgkeys == null){
//                getShare().getUnReadList().get(idx).unread.msgkeys = new ArrayList<>();
//            }
            if(getShare().getUnReadList().get(idx).unread == null){
                getShare().getUnReadList().get(idx).unread = new Data_UnRead();
                getShare().getUnReadList().get(idx).unread.msgkeys = new ArrayList<>();
            }
            getShare().getUnReadList().get(idx).update_at = LibDateUtil.getCurruntDayWithFormat(Define.DEFUALT_DATE_FORMAT);
            getShare().getUnReadList().get(idx).txt = message;
            getShare().getUnReadList().get(idx).unread.msgkeys.add(keyData);

            if("T".equals(getShare().getUnReadList().get(idx).push_flag)){

                if(isScreenOn()) {
                    Toast.makeText(BaseActivity.this,"["+other_user_name+"] " + message,Toast.LENGTH_SHORT).show();
                } else {
                    sendNotification(new Intent(),other_user_name,message);
                }
            }
        }
    }

    public int getUserDataIndex(String uuid){
        int idx = -1;
        if(getShare().getUnReadList() != null && getShare().getUnReadList().size() > 0){
            for(int i=0; i<getShare().getUnReadList().size(); i++){
                Data_Chat_UserList item =  getShare().getUnReadList().get(i);
                if(uuid.equals(item.other_uuid)){
                    idx = i;
                }
            }
        }
        return idx;
    }

    @Override
    public void onXmppReceiveError(String body) {

    }

    public void showProgressPopup(){
        if (mCustomPopupManager != null && !mCustomPopupManager.isShow()) {
            mCustomPopupManager.onShow();
        }
    }

    public void hidePorgressPopup(){
        if(mCustomPopupManager != null){
            if(LOADING_IMAGE_CHECK == LOADING_IMAGE_HIDE) { // 이미지를 업로딩하는데 오래 걸리면 로딩이미지(거북이이미지)를 띄우게 하기 위해서
            }else
            mCustomPopupManager.onDismiss();
        }
    }

    public void destroyProgressPopup(){
        if (mCustomPopupManager != null) {
            mCustomPopupManager.onDestroy();
            mCustomPopupManager = null;
        }
    }

    ////비콘
    private final BroadcastReceiver mBeaconScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            BluetoothLeDevice deviceData = (BluetoothLeDevice)intent.getSerializableExtra(BluetoothLeService.VINEBEACON_DATA);
            resultBeaconScan(action,deviceData);
        }
    };

    private static IntentFilter makeScanIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_FOUND_VINE_BEACON);
        intentFilter.addAction(BluetoothLeService.ACTION_IN_VINE_BEACON);
        intentFilter.addAction(BluetoothLeService.ACTION_OUT_VINE_BEACON);
        intentFilter.addAction(BluetoothLeService.ACTION_CLICK_SHORT_PRESS);
        intentFilter.addAction(BluetoothLeService.ACTION_CLICK_LONG_PRESS);
        intentFilter.addAction(BluetoothLeService.ACTION_CLICK_SHUTTER);
        return intentFilter;
    }

    protected void resultBeaconScan(String action, BluetoothLeDevice device){
        if (BluetoothLeService.ACTION_FOUND_VINE_BEACON.equals(action)) {

        }else if(BluetoothLeService.ACTION_IN_VINE_BEACON.equals(action)){

        }else if(BluetoothLeService.ACTION_OUT_VINE_BEACON.equals(action)){

        }
    }


    public void moveActivityWithPushData(String message_type, String kind, final String item_id){

        if(Data_Notice.MESSAGE_TYPE_CHAT.equals(message_type)){
            if("chat".equals(kind)){
                Data_OtherUser other = new Data_OtherUser();
                other.user_uuid = item_id;
                ChattingActivity.startActivity(BaseActivity.this, other);
            }else{

                String uuid = item_id.replaceAll("@" + getShare().getServerInfo().groupchat_hostname,"");
                Data_OtherUser other = new Data_OtherUser();
                other.user_uuid = uuid;
                ChattingActivity.startActivityWithGroup(BaseActivity.this, other);
            }
        }else if(Data_Notice.MESSAGE_TYPE_STICKERCON.equals(message_type)){

        }else if(Data_Notice.MESSAGE_TYPE_INVITE_BEACON.equals(message_type)){
            Call<Rev_Beacon> revBeaconCall = wezoneRestful.getBeacon(item_id);
            revBeaconCall.enqueue(new Callback<Rev_Beacon>() {
                @Override
                public void onResponse(Call<Rev_Beacon> call, Response<Rev_Beacon> response) {
                    Rev_Beacon revBeacon = response.body();

                    //에러코드 별도 처리
                    //정상처리가 아니면 삭제된걸로 간주
                    if("200".equals(revBeacon.code)){
                        BeaconSharedActivity.startActivity(BaseActivity.this,revBeacon.beacon_info);
                    }else{
                        Toast.makeText(BaseActivity.this,"삭제된 WeCON입니다",Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<Rev_Beacon> call, Throwable t) {

                }
            });
        }else if(Data_Notice.MESSAGE_TYPE_JOIN_BEACONZONE.equals(message_type)){

        }else if(Data_Notice.MESSAGE_TYPE_INVITE_WEZONE.equals(message_type)){
            Call<Rev_Wezoninfo> revWeZoneCall =  wezoneRestful.getWezone(item_id);
            revWeZoneCall.enqueue(new Callback<Rev_Wezoninfo>() {
                @Override
                public void onResponse(Call<Rev_Wezoninfo> call, Response<Rev_Wezoninfo> response) {
                    Rev_Wezoninfo wezoneinfo = response.body();
                    if(isNetSuccess(wezoneinfo)){
                        WezoneActivity.startActivit(BaseActivity.this,wezoneinfo.wezone_info,null);
                    }
                }

                @Override
                public void onFailure(Call<Rev_Wezoninfo> call, Throwable t) {

                }
            });
        }else if(Data_Notice.MESSAGE_TYPE_JOIN_WEZONE.equals(message_type)){
            Call<Rev_Wezoninfo> revWeZoneCall =  wezoneRestful.getWezone(item_id);
            revWeZoneCall.enqueue(new Callback<Rev_Wezoninfo>() {
                @Override
                public void onResponse(Call<Rev_Wezoninfo> call, Response<Rev_Wezoninfo> response) {
                    Rev_Wezoninfo wezoneinfo = response.body();
                    if(isNetSuccess(wezoneinfo)){
                        WezoneActivity.startActivit(BaseActivity.this,wezoneinfo.wezone_info,null);
                    }
                }

                @Override
                public void onFailure(Call<Rev_Wezoninfo> call, Throwable t) {

                }
            });
        }else if(Data_Notice.MESSAGE_TYPE_BOARD.equals(message_type)){
            getWezoneBoardAndGotoBoard(item_id);
        }else if(Data_Notice.MESSAGE_TYPE_COMMANT.equals(message_type)){
            getWezoneBoardAndGotoBoard(item_id);
        }else if(Data_Notice.MESSAGE_TYPE_LOGOUT.equals(message_type)){
            moveLoginActivity(null,true);
        }

        //위존 비콘
        else if(Data_Notice.MESSAGE_TYPE_WEZONE_REGIST.equals(message_type)){
            Send_PutWezoneDelete deleteWezone = new Send_PutWezoneDelete();
            deleteWezone.wezone_id = item_id;
            deleteWezone.flag = "N";

            Call<Rev_Base> DeleteWezoneCall = wezoneRestful.putWezoneDelete(deleteWezone);
            DeleteWezoneCall.enqueue(new Callback<Rev_Base>() {

                @Override
                public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                    Rev_Base revBase = response.body();
                    if (isNetSuccess(revBase)) {
                        Call<Rev_Wezoninfo> revWeZoneCall =  wezoneRestful.getWezone(item_id);
                        revWeZoneCall.enqueue(new Callback<Rev_Wezoninfo>() {
                            @Override
                            public void onResponse(Call<Rev_Wezoninfo> call, Response<Rev_Wezoninfo> response) {
                                Rev_Wezoninfo wezoneinfo = response.body();
                                if(isNetSuccess(wezoneinfo)){
                                    WezoneActivity.startActivit(BaseActivity.this,wezoneinfo.wezone_info,null);
                                }
                            }

                            @Override
                            public void onFailure(Call<Rev_Wezoninfo> call, Throwable t) {

                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<Rev_Base> call, Throwable t) {

                }
            });
        }else if(Data_Notice.MESSAGE_TYPE_WEZONE_NOTICE.equals(message_type)){
            Call<Rev_Wezoninfo> revWeZoneCall =  wezoneRestful.getWezone(item_id);
            revWeZoneCall.enqueue(new Callback<Rev_Wezoninfo>() {
                @Override
                public void onResponse(Call<Rev_Wezoninfo> call, Response<Rev_Wezoninfo> response) {
                    Rev_Wezoninfo wezoneinfo = response.body();
                    if(isNetSuccess(wezoneinfo)){
                        WezoneActivity.startActivit(BaseActivity.this,wezoneinfo.wezone_info,null);
                    }
                }

                @Override
                public void onFailure(Call<Rev_Wezoninfo> call, Throwable t) {

                }
            });
        }
    }

    public void getWezoneBoardAndGotoBoard(String item_id){
        Call<Rev_Board> revBoardCall = wezoneRestful.getWezoneBoard(item_id);
        revBoardCall.enqueue(new Callback<Rev_Board>() {
            @Override
            public void onResponse(Call<Rev_Board> call, Response<Rev_Board> response) {
                final Rev_Board board = response.body();
                if(isNetSuccess(board)){

                    if(board.board_info != null){
                        Call<Rev_Wezoninfo> revWeZoneCall =  wezoneRestful.getWezone(board.board_info.wezone_id);
                        revWeZoneCall.enqueue(new Callback<Rev_Wezoninfo>() {
                            @Override
                            public void onResponse(Call<Rev_Wezoninfo> call, Response<Rev_Wezoninfo> response) {
                                Rev_Wezoninfo wezoneinfo = response.body();
                                if(isNetSuccess(wezoneinfo)){
                                    WezoneBoardDetailActivity.startActivit(BaseActivity.this,wezoneinfo.wezone_info.title,wezoneinfo.wezone_info,board.board_info,null);
                                }
                            }

                            @Override
                            public void onFailure(Call<Rev_Wezoninfo> call, Throwable t) {

                            }
                        });
                    }else{
                        Toast.makeText(BaseActivity.this,"삭제된 게시물입니다",Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<Rev_Board> call, Throwable t) {

            }
        });
    }

    protected void sendNotification(Intent i, String title, String message) {

        ShareApplication share = (ShareApplication) getApplication();

        mNotificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title);

//        String strTemp = UIControl.decodeBase64WithString(message);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message).setSummaryText("Wezone"));
        mBuilder.setContentText(message);

//        mBuilder.setNumber(share.getPushDataCount());
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setVibrate(new long[]{1000, 1000});
        mBuilder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(0, mBuilder.build());
    }

    public boolean isScreenOn(){
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            return pm.isInteractive();
        } else {
            return pm.isScreenOn();
        }
    }
}
