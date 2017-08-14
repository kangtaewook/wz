package com.vinetech.wezone.Common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.beacon.BluetoothLeDevice;
import com.vinetech.beacon.BluetoothLeService;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends BaseActivity {

    final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    CameraPreview preview;

    Camera camera;

    private TextView textview_hospital;
    private TextView textview_name;

    private LinearLayout linearlayout_btn_capture;
    private LinearLayout linearlayout_guide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        linearlayout_guide = (LinearLayout) findViewById(R.id.linearlayout_guide);
        linearlayout_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.autoFocus(mAutoFocus);
            }
        });

        linearlayout_btn_capture = (LinearLayout) findViewById(R.id.linearlayout_btn_capture);


        preview = new CameraPreview(this, (SurfaceView)findViewById(R.id.surfaceView));
        preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);

        linearlayout_btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            int APIVersion = Build.VERSION.SDK_INT;

            if(APIVersion >= Build.VERSION_CODES.M){
                if(checkCAMERAPermission()){
                    try{
                        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                        camera.startPreview();
                        preview.setCamera(camera);
                    } catch (RuntimeException ex){
                        Toast.makeText(CameraActivity.this, "카메라 기능을 사용할 수 없습니다 "+ex.getMessage() , Toast.LENGTH_LONG).show();
                    }
                }else{
                    ActivityCompat.requestPermissions(CameraActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
                }
            }else{
                try{
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    camera.startPreview();
                    preview.setCamera(camera);
                } catch (RuntimeException ex){
                    Toast.makeText(CameraActivity.this, "카메라 기능을 사용할 수 없습니다 "+ex.getMessage() , Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    @Override
    protected void onPause() {
        if(camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }


    Camera.AutoFocusCallback mAutoFocus = new Camera.AutoFocusCallback() {

        public void onAutoFocus(boolean success, Camera camera) {
            if(success){

            }
        }
    };

    private void refreshGallery(File file) {
//        Intent i = new Intent();
//        i.setData(Uri.fromFile(file));
//        setResult(RESULT_OK, i);
//        finish();

        Uri targetUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final String targetDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";   // 특정 경로!!
        targetUri = targetUri.buildUpon().appendQueryParameter("bucketId", String.valueOf(targetDir.toLowerCase().hashCode())).build();
        Intent intent = new Intent(Intent.ACTION_VIEW, targetUri);
//        intent.setDataAndType(targetUri, "vnd.android.cursor.dir/image");
        startActivity(intent);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraActivity.this, targetDir+" 위치에 저장되었습니다",Toast.LENGTH_SHORT).show();
            }
        });

    }

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            resetCam();
            Log.d("SUMIN", "onPictureTaken - jpeg");
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/DCIM/Camera/wezone");
                dir.mkdirs();

                Timestamp ts = new Timestamp(System.currentTimeMillis());
                String fileName = "wezone_" + ts.toString() + ".jpg";
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }

    private boolean checkCAMERAPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode){
            case CAMERA_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean cameraAccepted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                    if(cameraAccepted){
                        try{
                            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                            camera.startPreview();
                            preview.setCamera(camera);
                        } catch (RuntimeException ex){
                            Toast.makeText(CameraActivity.this, "카메라 기능을 이용할 수 없습니다 "+ex.getMessage() , Toast.LENGTH_LONG).show();
                        }
                    }else{

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){

                                showMessagePermission("카메라 권한을 허용하시겠습니까?", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                            ActivityCompat.requestPermissions(CameraActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
                                        }
                                    }
                                });
                                return;
                            }
                        }

                    }
                }
                break;
        }

    }

    private void showMessagePermission(String msg, DialogInterface.OnClickListener listener){
        new android.support.v7.app.AlertDialog.Builder(CameraActivity.this)
                .setMessage(msg)
                .setPositiveButton("허용",listener)
                .setNegativeButton("거부",null)
                .create()
                .show();
    }

    @Override
    protected void resultBeaconScan(String action, BluetoothLeDevice device) {
        super.resultBeaconScan(action, device);
        if(BluetoothLeService.ACTION_CLICK_SHUTTER.equals(action)){
            camera.takePicture(shutterCallback, rawCallback, jpegCallback);
        }
    }
}
