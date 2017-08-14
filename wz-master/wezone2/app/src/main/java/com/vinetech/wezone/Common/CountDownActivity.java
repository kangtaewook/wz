package com.vinetech.wezone.Common;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.beacon.BluetoothLeDevice;
import com.vinetech.beacon.BluetoothLeService;
import com.vinetech.util.LibCall;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_OtherUser;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Message.ChattingActivity;
import com.vinetech.wezone.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CountDownActivity extends BaseActivity {

    public static final int COUNTDOWN_TIME = 1000 * 10;

    public static String PHONE_NUMBERS = "phone_numbers";
    public static String MESSAGE = "message";

    private TextView textview_count;

    private ImageView imageview_icon;

    private TextView textview_desc;

    private LinearLayout linearlayout_btn;
    private TextView textview_btn;

    private String[] mPhoneNumbers;
    private String mMessage;

    private int count = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        mPhoneNumbers = getIntent().getStringArrayExtra(CountDownActivity.PHONE_NUMBERS);
        mMessage = getIntent().getStringExtra(CountDownActivity.MESSAGE);

        textview_count = (TextView) findViewById(R.id.textview_count);
        textview_count.setVisibility(View.VISIBLE);

        imageview_icon = (ImageView) findViewById(R.id.imageview_icon);
        imageview_icon.setVisibility(View.GONE);

        textview_desc = (TextView) findViewById(R.id.textview_desc);

        linearlayout_btn = (LinearLayout) findViewById(R.id.linearlayout_btn);
        linearlayout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count != 0){
                    mTimer.cancel();
                    finish();
                }else{
                    finish();
                }
            }
        });


        textview_btn = (TextView) findViewById(R.id.textview_btn);
        textview_btn.setText("취소");

        mTimer.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mSmsReceiver,new IntentFilter("SMS_SENT_ACTION"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSmsReceiver);
    }

    ////비콘
    private final BroadcastReceiver mSmsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "전송 완료", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(context, "전송 실패", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(context, "서비스 지역이 아닙니다.", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(context, "무선(Radio)가 꺼져있습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(context, "PDU Null", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    CountDownTimer mTimer = new CountDownTimer(COUNTDOWN_TIME,1000){

        @Override
        public void onTick(long millisUntilFinished) {
            count--;
            textview_count.setText(String.valueOf(count));
            textview_desc.setText(count + " 초 후 친구들에게 SOS요청을 합니다");
        }

        @Override
        public void onFinish() {
            count = 0;

            textview_count.setVisibility(View.GONE);
            imageview_icon.setVisibility(View.VISIBLE);
            textview_desc.setText("SOS 요청이 발송되었습니다");
            textview_btn.setText("확인");
            textview_btn.setTextColor(ColorStateList.valueOf(Color.parseColor("#5AAEFF")));

            LibCall.SendSmsToOthers(PendingIntent.getBroadcast(CountDownActivity.this, 0, new Intent("SMS_SENT_ACTION"), 0),mPhoneNumbers,mMessage);

        }
    };

}
