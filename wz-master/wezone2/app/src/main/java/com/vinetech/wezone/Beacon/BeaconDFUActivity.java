package com.vinetech.wezone.Beacon;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.beacon.BluetoothLeDevice;
import com.vinetech.beacon.DfuService;
import com.vinetech.util.DownloadFile;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import org.altbeacon.beacon.Beacon;

import java.io.File;

import no.nordicsemi.android.dfu.DfuBaseService;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 비콘 펌웨어 화면
 *
 * 펌웨어 등의 업무를 수행
 *
 */

public class BeaconDFUActivity extends BaseActivity {

    public static final String BEACON_INFO = "beacon_info";

    public static void startActivity(BaseActivity activity, Data_Beacon beaconInfo) {
        Intent intent = new Intent(activity, BeaconDFUActivity.class);
        intent.putExtra(BEACON_INFO, beaconInfo);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_DFU_BEACON);
    }

    private Data_Beacon mBeaconInfo;

    private RelativeLayout relativelayout_bg;

    public TextView textview_current_version;
    public TextView textview_new_version;

    public LinearLayout linearlayout_btn_update;
    public LinearLayout linearlayout_progress_area;

    public ImageView imageview_beacon_loading;
    public TextView textview_noti;

    public LinearLayout linearlayout_done_area;

    public int STATE_NONE = 0;
    public int STATE_UPDATE = 1;
    public int STATE_DONE = 2;

    public int mCurrentState = STATE_NONE;

    public boolean isUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_dfu);

        setHeaderView(R.drawable.btn_back_white, "비콘 펌웨어 업데이트", 0);

        mBeaconInfo = (Data_Beacon)getIntent().getSerializableExtra(BEACON_INFO);

        relativelayout_bg = (RelativeLayout) findViewById(R.id.relativelayout_bg);
        relativelayout_bg.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));

        textview_current_version = (TextView) findViewById(R.id.textview_current_version);
        textview_current_version.setText("version "+ mBeaconInfo.firmware_ver);

        textview_new_version = (TextView) findViewById(R.id.textview_new_version);
        textview_new_version.setText("version "+ mBeaconInfo.firmware_ver_new);

        linearlayout_btn_update = (LinearLayout) findViewById(R.id.linearlayout_btn_update);
        linearlayout_btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFileFromServer();
            }
        });

        linearlayout_progress_area = (LinearLayout) findViewById(R.id.linearlayout_progress_area);
        textview_noti = (TextView) findViewById(R.id.textview_noti);

        linearlayout_done_area = (LinearLayout) findViewById(R.id.linearlayout_done_area);

        imageview_beacon_loading = (ImageView) findViewById(R.id.imageview_beacon_loading);

        float curVer = Float.valueOf(mBeaconInfo.firmware_ver);
        float newVer = Float.valueOf(mBeaconInfo.firmware_ver_new);

        if(curVer < newVer){
            setBtnBottom(STATE_NONE);
        }else{
            setBtnBottom(STATE_DONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        DfuServiceListenerHelper.registerProgressListener(BeaconDFUActivity.this, mDfuProgressListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        DfuServiceListenerHelper.unregisterProgressListener(BeaconDFUActivity.this, mDfuProgressListener);
    }

    private DfuProgressListener mDfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
            textview_noti.setText("WeCON 연결 중..");
        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            textview_noti.setText("WeCON 연결");
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            textview_noti.setText("DFU 시작 중..");
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            textview_noti.setText("DFU 시작");
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            textview_noti.setText("Bootloader 시작..");
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            if (partsTotal > 1)
                textview_noti.setText(String.format("업그레이드 중(%d/%d)",currentPart, partsTotal));
            else
                textview_noti.setText("업그레이드 중..");
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            textview_noti.setText("유효성 검증..");
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            textview_noti.setText("연결 해제 중..");
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            textview_noti.setText("WeCON 연결 해제");
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            isUpdated = true;
            setBtnBottom(STATE_DONE);
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Toast.makeText(BeaconDFUActivity.this, "DFU 중단", Toast.LENGTH_SHORT).show();
            setBtnBottom(STATE_NONE);
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {

            if(Define.LOG_YN){
                Log.d(Define.LOG_TAG, "error : " + error);
                Log.d(Define.LOG_TAG, "errorType : " + errorType);
                Log.d(Define.LOG_TAG, "message : " + message);
            }

            Toast.makeText(BeaconDFUActivity.this, message, Toast.LENGTH_SHORT).show();
            setBtnBottom(STATE_NONE);
        }
    };

    public void getFileFromServer(){

        setBtnBottom(STATE_UPDATE);
        textview_noti.setText("파일 다운로드 중..");

        String downUrl = mBeaconInfo.firmware_url;
        final String path = Environment.getExternalStorageDirectory() + "/wezone/temp/";
        final String fileName = "dfu.zip";
        DownloadFile download = new DownloadFile(BeaconDFUActivity.this, new DownloadFile.DownloadListener() {
            @Override
            public void result(int nState) {
                switch (nState) {

                    case DownloadFile.DOWNLOAD_END: {


                        String nextMac = getNextMacAddr(mBeaconInfo.mac);
                        final DfuServiceInitiator starter = new DfuServiceInitiator(nextMac)
                                .setDisableNotification(true)
                                .setKeepBond(false);
                        String fullPath = path + fileName;

                        if(Define.LOG_YN){
                            Log.d(Define.LOG_TAG, "beacon_serial : " + mBeaconInfo.beacon_serial);
                            Log.d(Define.LOG_TAG, "mac : " + nextMac);
                            Log.d(Define.LOG_TAG, "fullPath : " + fullPath);
                        }

                        starter.setZip(fullPath);
                        starter.start(BeaconDFUActivity.this, DfuService.class);

                    }
                    break;
                    case DownloadFile.DOWNLOAD_CANCEL: {
                        Toast.makeText(BeaconDFUActivity.this, "파일 다운로드 취소", Toast.LENGTH_SHORT).show();
                        setBtnBottom(STATE_NONE);
                    }
                    break;
                    case DownloadFile.DOWNLOAD_ERRROR: {
                        Toast.makeText(BeaconDFUActivity.this, "파일 다운로드 실패", Toast.LENGTH_SHORT).show();
                        setBtnBottom(STATE_NONE);
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

    public void setBtnBottom(int state){

        linearlayout_btn_update.setVisibility(View.GONE);
        linearlayout_progress_area.setVisibility(View.GONE);
        linearlayout_done_area.setVisibility(View.GONE);
        if(imageview_beacon_loading.getAnimation() != null){
            imageview_beacon_loading.getAnimation().cancel();
        }

        if(state == STATE_UPDATE){
            linearlayout_progress_area.setVisibility(View.VISIBLE);

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            imageview_beacon_loading.setAnimation(animation);

        }else if(state == STATE_DONE){
            linearlayout_done_area.setVisibility(View.VISIBLE);
        }else{

            linearlayout_btn_update.setVisibility(View.VISIBLE);
        }
        mCurrentState = state;
    }

    public String getNextMacAddr(String mac){
        String tempStr[] = mac.split(":");

        //256 이상일때는..
        int lastValue = Integer.parseInt(tempStr[tempStr.length-1], 16);
        lastValue ++;

        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0; i<tempStr.length - 1; i++){
            stringBuffer.append(tempStr[i]);
            stringBuffer.append(":");
        }
        stringBuffer.append(String.format("%02X", lastValue));

        return stringBuffer.toString();
    }

    @Override
    public void onBackPressed() {
        setResultData();
    }

    @Override
    public void onClickLeftBtn(View v) {
        setResultData();
    }

    public void setResultData(){
        Intent i = new Intent();
        i.putExtra("isUpdated",isUpdated);
        setResult(RESULT_OK,i);
        finish();
    }

}
