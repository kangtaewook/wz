package com.vinetech.wezone.Wezone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhn.android.maps.NMapView;
import com.vinetech.ui.RangeSliderView;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;


public class WezoneDistanceActivity extends BaseActivity {

    public static String INTENT_KEY_DISTANCE = "distance";
    //public static String INTENT_KEY_DISTANCE2 = "mdistance";

    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";

    private RangeSliderView smallSlider;
    public BaseActivity mActivity;
    private GpsInfo mGpsInfo;

    private ImageView Circle;
    private TextView distance;
    private TextView meter;
    private static final String LOG_TAG = "NMapViewer";
    private Button activity_wezone_distance_ok;
    private MapFragment mMapFragment;
    private String mDistance;
    private double latitude;
    private double longitude;
    private String mdistance;

    public static void setWezoneDistanceActivity(BaseActivity activity, String mDistacne, double longitudem, double latitude) {
        Intent intent = new Intent(activity, WezoneDistanceActivity.class);
        intent.putExtra(INTENT_KEY_DISTANCE, mDistacne);
        intent.putExtra(LONGITUDE,longitudem);
        intent.putExtra(LATITUDE,latitude);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_WEZONE_DISTANCE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wezone_distance);

        mGpsInfo = new GpsInfo(WezoneDistanceActivity.this);

        Intent i = getIntent();
        longitude = i.getExtras().getDouble(LONGITUDE);
        latitude = i.getExtras().getDouble(LATITUDE);

        mDistance = getIntent().getStringExtra(INTENT_KEY_DISTANCE);
        if (mDistance == null) {
            mDistance = "200";
        }

        mMapFragment = new MapFragment();
        mMapFragment.setArguments(new Bundle());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragmentHere, mMapFragment);
        fragmentTransaction.commit();

        smallSlider = (RangeSliderView) findViewById(R.id.rsv_small);
        smallSlider.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                mMapFragment.onChangeIndex(index);
            }

            @Override
            public void onChangeSlide(int value) {
                if(value <= 160){
                    mDistance = "200";
                }
                setLayoutValueWithLevel(getLevelFromDistance());
            }
        });

        mMapFragment.setOnViewer(new MapFragment.OnViewerListener() {
            @Override
            public void onZoomLevelChange(NMapView nMapView, int level) {
                setLayoutValueWithLevel(level);
            }
        });

        activity_wezone_distance_ok = (Button) findViewById(R.id.button123);

        Circle = (ImageView) findViewById(R.id.circle);
        distance = (TextView) findViewById(R.id.distance);
        meter = (TextView) findViewById(R.id.meter);

        activity_wezone_distance_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mDistance.equals("3200")){
                    mdistance = "3.2";
                }else if(mDistance.equals("1600")){
                    mdistance = "1.6";
                }else{
                    mdistance = mDistance;
                }
                Intent i = new Intent();
                i.putExtra(Define.INTENTKEY_DISTANCE_VALUE, mdistance);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        mHandler.sendEmptyMessageDelayed(0, 50);
    }

    public double getLongitude(){
        return longitude;
    }

    public double getLatitude(){
        return latitude;
    }



    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            setLayoutValueWithLevel(getLevelFromDistance());
            return false;
        }
    });

    public int getLevelFromDistance() {
        if ("3200".equals(mDistance)) {
            return 10;
        } else if ("1600".equals(mDistance)) {
            return 11;
        } else if ("400".equals(mDistance)) {
            return 13;
        } else {
            return 14;
        }
    }

    public void setLayoutValueWithLevel(int level) {

        switch (level) {
            case 14:
                smallSlider.setSelectedSlot(0);
                mMapFragment.setCurrentIndex(0);
                mDistance = "200";
                break;
            case 13:
                smallSlider.setSelectedSlot(1);
                mMapFragment.setCurrentIndex(1);
                mDistance = "400";
                break;
            case 11:
                smallSlider.setSelectedSlot(2);
                mMapFragment.setCurrentIndex(2);
                mDistance = "1600";
                break;
            case 10:
                smallSlider.setSelectedSlot(3);
                mMapFragment.setCurrentIndex(3);
                mDistance = "3200";
                break;
        }

        if (mDistance.equals("1600")) {
        String mDis = "1.6";
            distance.setText(mDis + "K");
        } else if (mDistance.equals("3200")) {
          String  mDis = "3.2";
            distance.setText(mDis + "K");
        } else {
            distance.setText(mDistance);
        }
    }

    @Override
    public void onClickLeftBtn(View v) {
        v.setOnClickListener(new OnSingleClickListener(){
            @Override
            public void onSingleClick(View v) {
                //중복 클릭으로 인한 중복 생성 방지
                mActivity.onBackPressed();
            }
        });

    }

    public abstract class OnSingleClickListener implements View.OnClickListener {
        // 중복 클릭 방지 시간 설정
        private static final long MIN_CLICK_INTERVAL = 600;

        private long mLastClickTime;

        public abstract void onSingleClick(View v);

        @Override
        public final void onClick(View v) {
            long currentClickTime = SystemClock.uptimeMillis();
            long elapsedTime = currentClickTime - mLastClickTime;
            mLastClickTime = currentClickTime;
            // 중복 클릭인 경우
            if (elapsedTime <= MIN_CLICK_INTERVAL) {
                return;
            }
            // 중복 클릭이 아니라면 추상함수 호출
            onSingleClick(v);
        }

    }

}
