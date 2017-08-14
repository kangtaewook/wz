package com.vinetech.wezone.Common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.util.LibCall;
import com.vinetech.util.LibValidCheck;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Beacon.Activity_SosEditor;
import com.vinetech.wezone.Data.Data_ActionItem;
import com.vinetech.wezone.Data.Data_ActionItemData;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_LocalUserInfo;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Upload;
import com.vinetech.wezone.Wezone.AppNotification;

import java.util.ArrayList;

public class SelectNotificationActivity extends BaseActivity {

    public static final String BEACON_SHORT_ID = "짧게 누르기";
    public static final String BEACON_LONG_ID = "길게 누르기";
    public static final String BEACON_NEAR_ID = "가까워짐";
    public static final String BEACON_FAR_ID = "멀어짐";


    public static final String NOTICE_PUSH_TITLE = "Push 메세지";
    public static final String NOTICE_MAIL_TITLE = "mail";
    public static final String NOTICE_TITLE = "title";
    public static final String NOTICE_ACTION_ITEM = "item";
    public static final String NOTICE_USER_INFO = "NOTICE_USER_INFO";
    public static final String INTENT_RESULT_NOTIFICATION_ACTIONITEM = "mActionItem";
    public static final String INTENT_RESULT_NOTIFICATION_USERLIST_SOS = "INTENT_RESULT_NOTIFICATION_USERLIST_SOS";
    public static final String NOTIFICATION_APP = "APP";
    public static final String NOTIFICATION_APP_NAME = "APP_NAME";

    public static final String ON = "ON";
    public static final String OFF = "OFF";

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    public static void startActivity(BaseActivity activity, String title, Data_ActionItem actionItem) {
        Intent intent = new Intent(activity, SelectNotificationActivity.class);
//        intent.putExtra(WHERE, where);
        intent.putExtra(NOTICE_TITLE, title);
        if (actionItem != null) {
            intent.putExtra(NOTICE_ACTION_ITEM, actionItem);
        }
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_NOTIFICATION);
    }

    private String[] noticeArrayBeacon_short_id = {Data_ActionItem.ID_CAMERA, Data_ActionItem.ID_SOUND, Data_ActionItem.ID_PUSH_MSG, Data_ActionItem.ID_IMAGE, Data_ActionItem.ID_APP, Data_ActionItem.ID_EMAIL};
    private String[] noticeArrayBeacon_long_id = {Data_ActionItem.ID_SOS, Data_ActionItem.ID_SOUND, Data_ActionItem.ID_PUSH_MSG, Data_ActionItem.ID_IMAGE, Data_ActionItem.ID_APP, Data_ActionItem.ID_EMAIL};
    private String[] noticeArrayBeacon_near_id = {Data_ActionItem.ID_SOUND, Data_ActionItem.ID_PUSH_MSG, Data_ActionItem.ID_IMAGE, Data_ActionItem.ID_APP, Data_ActionItem.ID_EMAIL};
    private String[] noticeArrayBeacon_far_id = {Data_ActionItem.ID_SOUND, Data_ActionItem.ID_PUSH_MSG, Data_ActionItem.ID_IMAGE, Data_ActionItem.ID_APP, Data_ActionItem.ID_EMAIL};

    private LinearLayout linearlayout_title_area;
    private LinearLayout linearLayout_select_notification;
    private TextView textview_title;
    private String sos_message;
    private ImageView ImageView_select_notification_list_item_check;

    private ListView listview;

    private ArrayList<Data_LocalUserInfo> mUserList;

    private String mTitle;
    private Data_ActionItem mActionItem;
    private Data_Beacon mBeacon;



    private SelectNotificationListAdapter mSelectNotificationListAdapter;
    private Dialog mDialog;
    private String mPushMsg;
    private String mEmailPushMsg;
    private LinearLayout linearlayout_btn_send;
    private EditText textview_email;
    private String mEmail_value;
    private String mImagePath;
    private String push_contents;
    private String email;
    private String phone_number;
    private String name;
    private String mMessage;

    private String mONOFF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_notification);

        setting = getSharedPreferences("setting",0);
        editor = setting.edit();




        mUserList = new ArrayList<>();

        mTitle = getIntent().getStringExtra(NOTICE_TITLE);
        mActionItem = (Data_ActionItem) getIntent().getSerializableExtra(NOTICE_ACTION_ITEM);

        setHeaderView(R.drawable.btn_back_white, "WeCON 버튼", 0);



        ImageView_select_notification_list_item_check = (ImageView) findViewById(R.id.ImageView_select_notification_list_item_check);
        linearlayout_title_area = (LinearLayout) findViewById(R.id.linearlayout_title_area);
        linearlayout_title_area.setSelected(true);
        linearlayout_title_area.setOnClickListener(mClickListener);
        textview_title = (TextView) findViewById(R.id.textview_title);
        listview = (ListView) findViewById(R.id.listview);
        textview_title.setText(mTitle);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mONOFF.equals(OFF)){
                    return;
                }

                if (BEACON_LONG_ID.equals(mTitle)) {

                    String selectedId = noticeArrayBeacon_long_id[position];
                    if (Data_ActionItem.ID_SOS.equals(selectedId)) {
//                        showProgressPopup();
                        Data_LocalUserInfo data_localUserInfo = new Data_LocalUserInfo();
                        if (mActionItem != null) {

                            //mUserList, mMessage 말고 mActionItem에 다 넣어서 바로 넣기 startActivity에


                            Activity_SosEditor.startActivity(SelectNotificationActivity.this, mActionItem);
                            showProgressPopup();
                        }
                    }


                    if (Data_ActionItem.ID_SOUND.equals(selectedId)) {
                        onSoundAlertDialog();

                    }
                    if (Data_ActionItem.ID_PUSH_MSG.equals(selectedId)) {

                        String mPush_content = null;
                        if(mActionItem != null) {
                            for (int i = 0; i < mActionItem.data.size(); i++) {
                                if ("push_msg".equals(mActionItem.data.get(i).key)) {
                                    mPush_content = mActionItem.data.get(i).value;
                                }
                            }
                        }

                        EditTextActivity.startActivity(SelectNotificationActivity.this, "Push 메세지",mPush_content);
                    }

                    if (Data_ActionItem.ID_IMAGE.equals(selectedId)) {
                        Activity_PhotoPicker_Folder.startFolderActivityForResult(SelectNotificationActivity.this, 1);
                    }

                    if (Data_ActionItem.ID_APP.equals(selectedId)) {
                        AppNotification.startActivity(SelectNotificationActivity.this, "test");
                    }

                    if (Data_ActionItem.ID_EMAIL.equals(selectedId)) {
                        String mEmail = null;
                        String mPush_content = null;
                        if(mActionItem != null) {
                            for (int i = 0; i < mActionItem.data.size(); i++) {
                                if ("email".equals(mActionItem.data.get(i).key)) {
                                    mEmail = mActionItem.data.get(i).value;
                                } else {
                                    mPush_content = mActionItem.data.get(i).value;
                                }
                            }
                        }

                        Activity_Email_Push.startActivity(SelectNotificationActivity.this, mEmail,mPush_content);
                    }

                } else if (BEACON_SHORT_ID.equals(mTitle)) {
                    String selectedId = noticeArrayBeacon_short_id[position];
                    if (Data_ActionItem.ID_CAMERA.equals(selectedId)) {
                        camera();
                        mSelectNotificationListAdapter.notifyDataSetChanged();
                    }

                    if (Data_ActionItem.ID_SOUND.equals(selectedId)) {
                        onSoundAlertDialog();

                    }
                    if (Data_ActionItem.ID_PUSH_MSG.equals(selectedId)) {

                        String mPush_content = null;
                        if(mActionItem != null) {
                            for (int i = 0; i < mActionItem.data.size(); i++) {
                                if ("push_msg".equals(mActionItem.data.get(i).key)) {
                                    mPush_content = mActionItem.data.get(i).value;
                                }
                            }
                        }

                        EditTextActivity.startActivity(SelectNotificationActivity.this, "Push 메세지",mPush_content);
                    }

                    if (Data_ActionItem.ID_IMAGE.equals(selectedId)) {
                        Activity_PhotoPicker_Folder.startFolderActivityForResult(SelectNotificationActivity.this, 1);
                    }

                    if (Data_ActionItem.ID_APP.equals(selectedId)) {
                        AppNotification.startActivity(SelectNotificationActivity.this, "test");
                    }

                    if (Data_ActionItem.ID_EMAIL.equals(selectedId)) {
                        String mEmail = null;
                        String mPush_content = null;
                        if(mActionItem != null) {
                            for (int i = 0; i < mActionItem.data.size(); i++) {
                                if ("email".equals(mActionItem.data.get(i).key)) {
                                    mEmail = mActionItem.data.get(i).value;
                                } else {
                                    mPush_content = mActionItem.data.get(i).value;
                                }
                            }
                        }
                        Activity_Email_Push.startActivity(SelectNotificationActivity.this, mEmail,mPush_content);
                    }
                } else if (BEACON_NEAR_ID.equals(mTitle)) {
                    String selectedId = noticeArrayBeacon_near_id[position];
                    if (Data_ActionItem.ID_SOUND.equals(selectedId)) {
                        onSoundAlertDialog();

                    }
                    if (Data_ActionItem.ID_PUSH_MSG.equals(selectedId)) {
                        String mPush_content = null;
                        if(mActionItem != null) {
                            for (int i = 0; i < mActionItem.data.size(); i++) {
                                if ("push_msg".equals(mActionItem.data.get(i).key)) {
                                    mPush_content = mActionItem.data.get(i).value;
                                }
                            }
                        }

                        EditTextActivity.startActivity(SelectNotificationActivity.this, "Push 메세지",mPush_content);
                    }

                    if (Data_ActionItem.ID_IMAGE.equals(selectedId)) {
                        Activity_PhotoPicker_Folder.startFolderActivityForResult(SelectNotificationActivity.this, 1);
                    }

                    if (Data_ActionItem.ID_APP.equals(selectedId)) {
                        AppNotification.startActivity(SelectNotificationActivity.this, "test");
                    }

                    if (Data_ActionItem.ID_EMAIL.equals(selectedId)) {
                        String mEmail = null;
                        String mPush_content = null;
                        if(mActionItem != null) {
                            for (int i = 0; i < mActionItem.data.size(); i++) {
                                if ("email".equals(mActionItem.data.get(i).key)) {
                                    mEmail = mActionItem.data.get(i).value;
                                } else {
                                    mPush_content = mActionItem.data.get(i).value;
                                }
                            }
                        }
                        Activity_Email_Push.startActivity(SelectNotificationActivity.this, mEmail,mPush_content);
                    }


                }else{
                    String selectedId = noticeArrayBeacon_far_id[position];
                    if (Data_ActionItem.ID_SOUND.equals(selectedId)) {
                        onSoundAlertDialog();

                    }
                    if (Data_ActionItem.ID_PUSH_MSG.equals(selectedId)) {
                        String mPush_content = null;
                        if(mActionItem != null) {
                            for (int i = 0; i < mActionItem.data.size(); i++) {
                                if ("push_msg".equals(mActionItem.data.get(i).key)) {
                                    mPush_content = mActionItem.data.get(i).value;
                                }
                            }
                        }

                        EditTextActivity.startActivity(SelectNotificationActivity.this, "Push 메세지",mPush_content);
                    }

                    if (Data_ActionItem.ID_IMAGE.equals(selectedId)) {
                        Activity_PhotoPicker_Folder.startFolderActivityForResult(SelectNotificationActivity.this, 1);
                    }

                    if (Data_ActionItem.ID_APP.equals(selectedId)) {
                        AppNotification.startActivity(SelectNotificationActivity.this, "test");
                    }

                    if (Data_ActionItem.ID_EMAIL.equals(selectedId)) {
                        String mEmail = null;
                        String mPush_content = null;
                        if(mActionItem != null) {
                            for (int i = 0; i < mActionItem.data.size(); i++) {
                                if ("email".equals(mActionItem.data.get(i).key)) {
                                    mEmail = mActionItem.data.get(i).value;
                                } else {
                                    mPush_content = mActionItem.data.get(i).value;
                                }
                            }
                        }
                        Activity_Email_Push.startActivity(SelectNotificationActivity.this, mEmail,mPush_content);
                    }
                }
            }
        });


// default 값 1. 짧게 누르기, 길게 누르기

            if (mActionItem == null) {
                mActionItem = new Data_ActionItem();
                mONOFF = ON;
                if (BEACON_LONG_ID.equals(mTitle)) {

                    ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
                    Data_ActionItemData itemData = new Data_ActionItemData();

                    mActionItem.id = Data_ActionItem.ID_SOS;
                    itemData.key = "sos";
                    itemData.value = "";
                    itemDataList.add(itemData);
                    mActionItem.data = itemDataList;

                } else if (BEACON_SHORT_ID.equals(mTitle)) {
                    mActionItem.id = Data_ActionItem.ID_CAMERA;

                    ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
                    Data_ActionItemData itemData = new Data_ActionItemData();

                    mActionItem.id = Data_ActionItem.ID_CAMERA;
                    itemData.key = "camera";
                    itemData.value = "";
                    itemDataList.add(itemData);
                    mActionItem.data = itemDataList;

                }else{
                    ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
                    Data_ActionItemData itemData = new Data_ActionItemData();

                    mActionItem.id = Data_ActionItem.ID_NOT_USE;
                    itemData.key = "not_use";
                    itemData.value = "";
                    itemDataList.add(itemData);
                    mActionItem.data = itemDataList;
                }

//                if (linearlayout_title_area.isSelected()) {
//                    linearlayout_title_area.setSelected(true);
//                    mONOFF = ON;
//                    listview.setVisibility(View.VISIBLE);
//                } else {
//                    linearlayout_title_area.setSelected(false);
//                    mONOFF = OFF;
//                    setmSelectNotificationListAdapter();
//                    mSelectNotificationListAdapter.notifyDataSetChanged();
//                }


            } else {
                if (mActionItem.id.equals(Data_ActionItem.ID_NOT_USE)) {
                    linearlayout_title_area.setSelected(false);
                    mONOFF = OFF;
                    setmSelectNotificationListAdapter();
                    mSelectNotificationListAdapter.notifyDataSetChanged();
                } else {
                    linearlayout_title_area.setSelected(true);
                    mONOFF = ON;
                    listview.setVisibility(View.VISIBLE);
                }
            }

//        if (BEACON_LONG_ID.equals(mTitle)) {
//            mSelectNotificationListAdapter = new SelectNotificationListAdapter(SelectNotificationActivity.this, noticeArrayBeacon_long_id, mActionItem,mONOFF);
//            listview.setAdapter(mSelectNotificationListAdapter);
//        } else if (BEACON_SHORT_ID.equals(mTitle)) {
//            mSelectNotificationListAdapter = new SelectNotificationListAdapter(SelectNotificationActivity.this, noticeArrayBeacon_short_id, mActionItem,mONOFF);
//            listview.setAdapter(mSelectNotificationListAdapter);
//        }  else if (BEACON_NEAR_ID.equals(mTitle)) {
//            mSelectNotificationListAdapter = new SelectNotificationListAdapter(SelectNotificationActivity.this, noticeArrayBeacon_near_id, mActionItem,mONOFF);
//            listview.setAdapter(mSelectNotificationListAdapter);
//        } else{
//            mSelectNotificationListAdapter = new SelectNotificationListAdapter(SelectNotificationActivity.this, noticeArrayBeacon_far_id, mActionItem,mONOFF);
//            listview.setAdapter(mSelectNotificationListAdapter);
//        }

        setmSelectNotificationListAdapter();

//
//        else {
//            mSelectNotificationListAdapter = new SelectNotificationListAdapter(SelectNotificationActivity.this, noticeArrayZone, mActionItem);
//            listview.setAdapter(mSelectNotificationListAdapter);
//        }
//

    }

    public void camera() {
        ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
        Data_ActionItemData itemData = new Data_ActionItemData();

        mActionItem.id = Data_ActionItem.ID_CAMERA;
        itemData.key = "camera";
        itemData.value = "";
        itemDataList.add(itemData);
        mActionItem.data = itemDataList;
    }

    public void onSoundAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

// 여기서 부터는 알림창의 속성 설정
        builder.setTitle("사운드 선택")        // 제목 설정
                .setMessage("Please select sound.")        // 메세지 설정
                .setCancelable(true)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("벨소리", new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent i = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                        startActivityForResult(i, Define.INTENT_RESULT_GET_SOUND_RING);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("일반 사운드", new DialogInterface.OnClickListener() {
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        LibCall.startAudioSelectActivity(SelectNotificationActivity.this, "사운드 선택", Define.INTENT_RESULT_GET_SOUND);
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }


    public void onEmailDialog() {


        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.FILL_PARENT;


//        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
//
//        params.width = 550;
//        params.height = 450;


        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
//        mDialog.getWindow().setAttributes(params);
        mDialog.setContentView(R.layout.dialog_send_email);
        linearlayout_btn_send = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_send);
        linearlayout_btn_send.setOnClickListener(mClickListener);
        textview_email = (EditText) mDialog.findViewById(R.id.textview_email);

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
        mDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        mDialog.getWindow().setGravity(Gravity.CENTER);

    }

    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            switch (viewId) {

                case R.id.linearlayout_title_area:
                    if (linearlayout_title_area.isSelected()) {
                        linearlayout_title_area.setSelected(false);

                        mONOFF = OFF;
                        setmSelectNotificationListAdapter();
                        mSelectNotificationListAdapter.notifyDataSetChanged();


                    } else {
                        linearlayout_title_area.setSelected(true);
                        listview.setVisibility(View.VISIBLE);

                        mONOFF = ON;
                        setmSelectNotificationListAdapter();
                        mSelectNotificationListAdapter.notifyDataSetChanged();

                    }
                    break;

                case R.id.linearlayout_btn_send:
                    mEmail_value = textview_email.getText().toString().trim();

                    if (!LibValidCheck.isValidEmail(mEmail_value)) {
                        Toast.makeText(m_Context, "이메일 형식이 이상합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
                    Data_ActionItemData itemData = new Data_ActionItemData();

                    itemData.key = "email";
                    itemData.value = mEmail_value;
                    mActionItem.id = Data_ActionItem.ID_EMAIL;
                    itemDataList.add(itemData);
                    mActionItem.data = itemDataList;

                    // EditTextActivity.startActivity(SelectNotificationActivity.this, "PUSH", mEmailPushMsg);

                    mSelectNotificationListAdapter.notifyDataSetChanged();

                    mDialog.cancel();
                    break;
            }


        }
    };

    @Override
    public void onClickLeftBtn(View v) {

        backitem(mActionItem);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        backitem(mActionItem);
    }


    public void backitem(Data_ActionItem mActionItem) {
        if(mONOFF.equals(OFF)){
            ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
            Data_ActionItemData itemData = new Data_ActionItemData();

            itemData.key = "not_use";
            itemData.value = "";
            mActionItem.id = Data_ActionItem.ID_NOT_USE;
            itemDataList.add(itemData);
            mActionItem.data = itemDataList;
            mSelectNotificationListAdapter.notifyDataSetChanged();
        }
        Intent intent = new Intent();
        intent.putExtra(INTENT_RESULT_NOTIFICATION_ACTIONITEM, mActionItem);
        setResult(RESULT_OK, intent);
        finish();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Define.INTENT_RESULT_USER_SOS:
                if (resultCode == RESULT_OK) {

                    mUserList = (ArrayList<Data_LocalUserInfo>) data.getSerializableExtra(Define.INTENTKEY_USER_VALUE);
                    ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
                    sos_message = data.getStringExtra(Define.SOS_MESSAGE);

                    mMessage = sos_message;

                    mActionItem.data = new ArrayList<>();
                    mActionItem.id = Data_ActionItem.ID_SOS;

                    Data_ActionItemData itemMessage = new Data_ActionItemData();

                    itemMessage.key = "message";
                    itemMessage.value = sos_message;

                    itemDataList.add(itemMessage);

//                    mActionItem.data = itemDataList;

                    for (int i = 0; i < mUserList.size(); i++) {
                        Data_ActionItemData itemPhone = new Data_ActionItemData();

                        itemPhone.key = "phone" + i;
                        itemPhone.value = mUserList.get(i).phone_num;
                        if(!WezoneUtil.isEmptyStr(mUserList.get(i).phone_num)){
                            itemDataList.add(itemPhone);

                            Data_ActionItemData itemName = new Data_ActionItemData();
                            itemName.key = "name" + i;
                            itemName.value = mUserList.get(i).user_name;
                            itemDataList.add(itemName);
                        }
                    }

                    mActionItem.data = itemDataList;

                    mSelectNotificationListAdapter.notifyDataSetChanged();

                }
                break;


            case Define.INTENT_RESULT_EDIT_TEXT:
                if (resultCode == RESULT_OK) {
                    mPushMsg = data.getStringExtra(EditTextActivity.EDITTEXT_CONTENTS);
                    ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
                    Data_ActionItemData itemData = new Data_ActionItemData();

                    itemData.key = "push_msg";
                    itemData.value = mPushMsg;
                    mActionItem.id = Data_ActionItem.ID_PUSH_MSG;
                    itemDataList.add(itemData);
                    mActionItem.data = itemDataList;

//                    editor.putString("PUSH_MESSAGE",mPushMsg);
//                    editor.commit();

                    Log.d("태욱", " mPushMsg  :" + mPushMsg);
                    mSelectNotificationListAdapter.notifyDataSetChanged();


                }
                break;
            case Define.INTENT_RESULT_GET_SOUND_RING:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    Ringtone ringtone = RingtoneManager.getRingtone(m_Context, uri);
                    String sound_name = ringtone.getTitle(m_Context);

                if(uri == null){
                    uri = Uri.parse("무음");
                }

                    RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM, uri);

                    ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
                    Data_ActionItemData itemData = new Data_ActionItemData();
                    Data_ActionItemData itemData_sound_name = new Data_ActionItemData();
                    itemData.key = "sound";
                    itemData.value = uri.toString().trim();
                    itemData_sound_name.key = "sound";
                    itemData_sound_name.value = sound_name;
                    mActionItem.id = Data_ActionItem.ID_SOUND;
                    itemDataList.add(itemData);
                    itemDataList.add(itemData_sound_name);
                    mActionItem.data = itemDataList;



                    mSelectNotificationListAdapter.notifyDataSetChanged();
                }
                break;
            case Define.INTENT_RESULT_GET_SOUND:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();

                    ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
                    Data_ActionItemData itemData = new Data_ActionItemData();
                    itemData.key = "sound";
                    itemData.value = uri.toString().trim();
                    mActionItem.id = Data_ActionItem.ID_SOUND;
                    itemDataList.add(itemData);
                    mActionItem.data = itemDataList;


                    mSelectNotificationListAdapter.notifyDataSetChanged();
                }
                break;
            case Define.INTENT_RESULT_APP:
                if (resultCode == RESULT_OK) {
                    String app_package = data.getStringExtra(NOTIFICATION_APP);
                    String appname = data.getStringExtra(NOTIFICATION_APP_NAME);

                    ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
                    Data_ActionItemData itemData = new Data_ActionItemData();
                    Data_ActionItemData itemData_appname = new Data_ActionItemData();
                    itemData.key = "app";
                    itemData.value = app_package;
                    itemData_appname.key = "app";
                    itemData_appname.value = appname;
                    Log.d("태욱","package name     :"+app_package);
                    mActionItem.id = Data_ActionItem.ID_APP;
                    itemDataList.add(itemData);
                    itemDataList.add(itemData_appname);
                    mActionItem.data = itemDataList;


                    mSelectNotificationListAdapter.notifyDataSetChanged();

                }
                break;
            case Define.INTENT_RESULT_CAMERA:
            case Define.INTENT_RESULT_PHOTO:
                if (resultCode == RESULT_OK) {
                    ArrayList<Data_PhotoPickerImage> pickerImages = getShare().popPhotoPickerImages();

                    Data_PhotoPickerImage data_photo = pickerImages.get(0);
                    mImagePath = data_photo.path;
                    uploadImageFile(Define.IMAGE_TYPE_BUNNYZONE, Define.IMAGE_STATUS_UPDATE, getUuid(), mImagePath);
                }
                break;
            case Define.INTENT_RESULT_EMAIL_PUSH:
                if (resultCode == RESULT_OK) {
                    push_contents = data.getStringExtra(Activity_Email_Push.EMAIL_PUSH_CONTENTS);
                    email = data.getStringExtra(Activity_Email_Push.EMAIL_DATA);
                    Log.d("태욱", "push_contents" + push_contents);
                    Log.d("태욱", "email" + email);

                    editor.putString("PUSH_CONTENTS",push_contents);
                    editor.putString("EMAIL",email);
                    editor.commit();

                    ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();

                    Data_ActionItemData email_List = new Data_ActionItemData();
                    email_List.key = "email";
                    email_List.value = email;


                    Data_ActionItemData push_data_list = new Data_ActionItemData();
                    push_data_list.key = "push_content";
                    push_data_list.value = push_contents;


                    mActionItem.id = Data_ActionItem.ID_EMAIL;
                    itemDataList.add(email_List);
                    itemDataList.add(push_data_list);
                    mActionItem.data = itemDataList;

                    mSelectNotificationListAdapter.notifyDataSetChanged();

                }
                break;




        }
    }

    @Override
    public void uploadResult(String type, Rev_Upload resultData) {
        super.uploadResult(type, resultData);
        //  getShare().getMyInfo().img_url = resultData.url;

        ArrayList<Data_ActionItemData> itemDataList = new ArrayList<>();
        Data_ActionItemData itemData = new Data_ActionItemData();
        itemData.key = "image";
        itemData.value = resultData.url;
        mActionItem.id = Data_ActionItem.ID_IMAGE;
        itemDataList.add(itemData);
        mActionItem.data = itemDataList;

//        mBeaconAction = new Data_BeaconAction();
//        mBeaconAction.short_id = mActionItem;
//        mBeaconAction.short_id.id = mActionItem.id;
//        mBeacon_Info_Vas = new Data_Beacon_Info_Vas();
//        mBeacon_Info_Vas.beacon = mBeaconAction;
//        mBeacon = new Data_Beacon();
//        mBeacon.beacon_info_vars = mBeacon_Info_Vas;

//        mActionItemData.key = "image";
//        mActionItemData.value = resultData.url;

        mSelectNotificationListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onRestart(){
        super.onRestart();
        hidePorgressPopup();
    }

    public void setmSelectNotificationListAdapter(){
        if (BEACON_LONG_ID.equals(mTitle)) {
            mSelectNotificationListAdapter = new SelectNotificationListAdapter(SelectNotificationActivity.this, noticeArrayBeacon_long_id, mActionItem,mONOFF);
            listview.setAdapter(mSelectNotificationListAdapter);
        } else if (BEACON_SHORT_ID.equals(mTitle)) {
            mSelectNotificationListAdapter = new SelectNotificationListAdapter(SelectNotificationActivity.this, noticeArrayBeacon_short_id, mActionItem,mONOFF);
            listview.setAdapter(mSelectNotificationListAdapter);
        }  else if (BEACON_NEAR_ID.equals(mTitle)) {
            mSelectNotificationListAdapter = new SelectNotificationListAdapter(SelectNotificationActivity.this, noticeArrayBeacon_near_id, mActionItem,mONOFF);
            listview.setAdapter(mSelectNotificationListAdapter);
        } else{
            mSelectNotificationListAdapter = new SelectNotificationListAdapter(SelectNotificationActivity.this, noticeArrayBeacon_far_id, mActionItem,mONOFF);
            listview.setAdapter(mSelectNotificationListAdapter);
        }

    }

}
