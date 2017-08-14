package com.vinetech.wezone.Gcm;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.util.LibSystemManager;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Notice;
import com.vinetech.wezone.Data.Data_PushData;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.ShareApplication;
import com.vinetech.wezone.SplashActivity;

public class PopupActivity extends BaseActivity implements OnClickListener {

	protected ShareApplication m_Share;
	
	private String mPushMessage;
	private Data_PushData mPushValue;

	private String mType;


	private TextView textview_name;
	private TextView mTextview;

	private LinearLayout linearlayout_btn_cancel;
	private LinearLayout linearlayout_btn_move;

	private String mCurrentProviederType;

	private LibSystemManager mLibSystemManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_popup);
		
		m_Share 	 = (ShareApplication)getApplication();

		textview_name = (TextView) findViewById(R.id.textview_name);
		mTextview = (TextView)findViewById(R.id.textview);

		mPushMessage = getIntent().getStringExtra(Define.INTENTKEY_PUSH_MESSAGE);
		mPushValue = (Data_PushData) getIntent().getSerializableExtra(Define.INTENTKEY_PUSH_VALUE);
		
		String title = mPushMessage;

		textview_name.setText(mPushValue.sender_name);
		mTextview.setText(mPushMessage);
		
		linearlayout_btn_cancel = (LinearLayout)findViewById(R.id.linearlayout_btn_cancel);
		linearlayout_btn_cancel.setOnClickListener(this);
		linearlayout_btn_move = (LinearLayout)findViewById(R.id.linearlayout_btn_move);
		linearlayout_btn_move.setOnClickListener(this);

		if(m_Share.isLogin()){
			moveActivityWithPushData(mPushValue.type,mPushValue.kind,mPushValue.item_id);
			finish();
		}else{

			mLibSystemManager = getShare().getLibSystemManager();

			//로그인 전 가져올수 있는 데이터를 모두 가져온다.
			getShare().getLoginParam().device_id = mLibSystemManager.getDeviceId();
			getShare().getLoginParam().device_model = mLibSystemManager.getDeviceName();
			getShare().getLoginParam().device_os_ver = mLibSystemManager.getOSVersion();
			getShare().getLoginParam().device_os_type = "android";

			getShare().getLoginParam().push_token = CryptPreferences.getCryptString(m_Context, Define.SHARE_KEY_PUSH_TOKEN, "");


			if(Data_Notice.MESSAGE_TYPE_LOGOUT.equals(mPushValue.type)){
				resetData();
			}

			goToSplash();

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		int viewId = v.getId();
		
		switch(viewId){
		

		case R.id.linearlayout_btn_cancel:
			finish();
			break;
			
		case R.id.linearlayout_btn_move:
//			moveAcitivtyWithPushData(mPushValue);
			finish();
			break;
			
		}
	}

//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		
//		int viewId = v.getId();
//		
//		switch(viewId){
//		case R.id.linearlayout_btn_01:
//			if(!Rev_PushData.TYPE_P05.equals(mType)){
//				Intent i = new Intent(PopupActivity.this,MainActivity.class);
//				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				i.putExtra(Define.INTENTKEY_PUSH_DATA, mPushData);
//				startActivity(i);
//				finish();
//			}
//			
//			break;
//			
//		case R.id.linearlayout_btn_02:{
//			if(Rev_PushData.TYPE_P01.equals(mType) || Rev_PushData.TYPE_P03.equals(mType)){
//				startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+mPushData.tel)));
//				finish();
//			}else{
//				finish();
//			}
//		}
//			break;
//			
//		case R.id.linearlayout_type_noti_popup:{
//			Intent i = new Intent(PopupActivity.this,MainActivity.class);
//			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			i.putExtra(Define.INTENTKEY_PUSH_DATA, mPushData);
//			startActivity(i);
//			finish();
//		}
//			break;
//			
//		}
//	}		

	public void goToSplash(){
		Intent i = new Intent(PopupActivity.this, SplashActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra(Define.INTENTKEY_PUSH_VALUE, mPushValue);
		moveActivity(i);
		finish();
	}
}
