package com.vinetech.wezone;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.kakao.auth.KakaoSDK;
import com.vinetech.beacon.BluetoothLeService;
import com.vinetech.util.LibSystemManager;
import com.vinetech.wezone.Common.KakaoSDKAdapter;
import com.vinetech.wezone.Data.Data_Chat_UserList;
import com.vinetech.wezone.Data.Data_Facebook;
import com.vinetech.wezone.Data.Data_Google;
import com.vinetech.wezone.Data.Data_KakaoTalk;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Data.Data_ServerInfo;
import com.vinetech.wezone.Data.Data_UserInfo;
import com.vinetech.wezone.SendPacket.Send_PostLogin;

import java.util.ArrayList;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 휘발성 메모리를 이 클래스에 담아 사용한다
 *
 * ex) 로그인 정보, 안읽음 데이터 등..
 */

public final class ShareApplication extends android.support.multidex.MultiDexApplication {

    private BluetoothLeService mBluetoothLeService;

    private LibSystemManager mLibSystemManager;

    private static ShareApplication instance;

    private Send_PostLogin loginParam;
    private Data_UserInfo myInfo;
    private Data_ServerInfo serverInfo;

    private ArrayList<Activity> mActivitys;

    public int getTotalUnReadCount(){

        if(this.mUnReadUserList == null)
            return 0;

        int totalCnt = 0;

        for(int i=0; i < this.mUnReadUserList.size(); i++){
            Data_Chat_UserList data = this.mUnReadUserList.get(i);
            if(data.unread != null && data.unread.msgkeys != null && data.unread.msgkeys.size() > 0){
                totalCnt += data.unread.msgkeys.size();
            }
        }
        return totalCnt;
    }

    private ArrayList<Data_Chat_UserList> mUnReadUserList;
    public void setUnReadList(ArrayList<Data_Chat_UserList> list){

        if(list == null){
            mUnReadUserList = new ArrayList<>();
            return;
        }


        if(this.mUnReadUserList == null){
            mUnReadUserList = new ArrayList<>();
        }

        this.mUnReadUserList.clear();

        this.mUnReadUserList.addAll(list);
    }

    public ArrayList<Data_Chat_UserList> getUnReadList(){
        if(this.mUnReadUserList == null){
            this.mUnReadUserList = new ArrayList<>();
        }

        return this.mUnReadUserList;
    }

    public void addUnRead(Data_Chat_UserList data){
        if(this.mUnReadUserList == null){
            mUnReadUserList = new ArrayList<>();
        }
        mUnReadUserList.add(data);
    }

    public void removeUnRead(Data_Chat_UserList data){
        mUnReadUserList.remove(data);
    }

    public Data_Chat_UserList getChatUserItem(String uuid){
        if(uuid == null || this.mUnReadUserList == null)
            return null;

        for(Data_Chat_UserList data : this.mUnReadUserList){
            return data;
        }

        return null;
    }

    public void removeUnReadWithUUID(String uuid){
        if(uuid == null || this.mUnReadUserList == null)
            return;

        int size = this.mUnReadUserList.size() - 1;

        for(int i = size; 0<=i; i--){
            Data_Chat_UserList data = this.mUnReadUserList.get(i);
            if(uuid.equals(data.other_uuid)){
                if(this.mUnReadUserList.get(i).unread.msgkeys != null){
                    this.mUnReadUserList.get(i).unread.msgkeys.clear();
                }
            }
        }
    }

    private int friendCnt = 0;
    public void addFriendCnt(){friendCnt++;}
    public void removeFriendCnt(){friendCnt--;};
    public void setFriendCnt(String cnt){
        if(cnt == null || "null".equals(cnt)){
            friendCnt = 0;
        }else{
            friendCnt = Integer.valueOf(cnt);
        }

    }
    public int getFriendCnt(){return friendCnt;}

    public static ShareApplication getInstance() {
        if (instance == null)
            throw new IllegalStateException();
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(getBaseContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        KakaoSDK.init(new KakaoSDKAdapter());

//		if(DexDex.dexOptRequired){
//			DexDex.showUiBlocker(this, "Preparing", "Please wait");
//		}
        mLibSystemManager = new LibSystemManager(getApplicationContext());

        mActivitys = new ArrayList<>();

    }

    public BluetoothLeService getBluetoothLeService(){
        return mBluetoothLeService;
    }
    public void setBluetoothLeService(BluetoothLeService service){
        mBluetoothLeService = service;
    }


    public void addActivity(Activity a) {
        if (mActivitys == null) {
            mActivitys = new ArrayList<>();
        }
        mActivitys.add(a);
    }

    public void removeActivity(Activity a){
        if(mActivitys != null && mActivitys.size() > 0) {
            mActivitys.remove(a);
        }
    }

    public void removeAllActivity(){
        if(mActivitys != null){

            while(mActivitys.size() != 0){
                int idx = mActivitys.size() - 1;
                Activity a = mActivitys.get(idx);
                if(a == null){
                    continue;
                }

                try{
                    a.finish();
                    mActivitys.remove(idx);
                } catch (Exception e){

                }
            }
            mActivitys.clear();
        }
    }

    public LibSystemManager getLibSystemManager() {
        return mLibSystemManager;
    }

    // ---------------------------------------------------------------------------------------------------
    // LoginData
    // ---------------------------------------------------------------------------------------------------
    public Data_UserInfo getMyInfo() {
        if (myInfo == null) {
            myInfo = new Data_UserInfo();
        }
        return myInfo;
    }

    public Data_ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(Data_ServerInfo info) {
        this.serverInfo = info;
    }

    public boolean isLogin() {

        if (myInfo == null)
            return false;

        return !("".equals(myInfo.uuid) || myInfo.uuid == null);
    }

    public void setMyInfo(Data_UserInfo userinfo) {
        this.myInfo = userinfo;
    }

    public Send_PostLogin getLoginParam(){
        if(loginParam == null){
            loginParam = new Send_PostLogin();
        }
        return loginParam;
    }


    private Data_Google mGoogleData;
    private String mGoogleToken;

    public Data_Google getGoogleData() {
        return mGoogleData;
    }

    public void setGoogleData(Data_Google mGoogleData) {
        this.mGoogleData = mGoogleData;
    }

    public String getGoogleToken() {
        return mGoogleToken;
    }

    public void setGoogleToken(String mGoogleToken) {
        this.mGoogleToken = mGoogleToken;
    }


    private Data_Facebook mFaceboockData;

    public Data_Facebook getFacebookData() {
        return mFaceboockData;
    }

    public void setFacebookData(Data_Facebook facebookdata) {
        this.mFaceboockData = facebookdata;
    }

    public Data_KakaoTalk mKakaoData;
    public Data_KakaoTalk getKakaoData(){ return mKakaoData;}
    public void setKakaoData(Data_KakaoTalk data){
        this.mKakaoData = data;
    }


    // ---------------------------------------------------------------------------------------------------
    // 사진 선택
    // ---------------------------------------------------------------------------------------------------

    /**
     * 포토피커에서 사진가져올때 결과값 용도로만 사용
     */
    private ArrayList<Data_PhotoPickerImage> mPhotoPickerImages;

    public ArrayList<Data_PhotoPickerImage> getPhotoPickerImages() {
        return mPhotoPickerImages;
    }

    public ArrayList<Data_PhotoPickerImage> popPhotoPickerImages() {
        ArrayList<Data_PhotoPickerImage> photoPickerImages = mPhotoPickerImages;
        mPhotoPickerImages = null;
        return photoPickerImages;
    }

    public ArrayList<Data_PhotoPickerImage> preparePhotoPickerImages() {
        if (mPhotoPickerImages == null)
            mPhotoPickerImages = new ArrayList<Data_PhotoPickerImage>();
        else
            resetPhotoPickerImages();
        return mPhotoPickerImages;
    }

    public boolean hasPhotoPickerImages() {
        return mPhotoPickerImages != null
                && mPhotoPickerImages.isEmpty() == false;
    }

    public void resetPhotoPickerImages() {
        if (mPhotoPickerImages != null && mPhotoPickerImages.isEmpty() == false) {
            for (Data_PhotoPickerImage photoPickerImage : mPhotoPickerImages)
                photoPickerImage.release();
            mPhotoPickerImages.clear();
        }
    }


}
