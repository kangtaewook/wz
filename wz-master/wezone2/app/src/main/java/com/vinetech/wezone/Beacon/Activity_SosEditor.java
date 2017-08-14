package com.vinetech.wezone.Beacon;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_ActionItem;
import com.vinetech.wezone.Data.Data_LocalUserInfo;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Profile.LocalUserSelectActivity;
import com.vinetech.wezone.Profile.LocalUserSelectListAdapter;
import com.vinetech.wezone.R;

import java.util.ArrayList;

public class Activity_SosEditor extends BaseActivity {

    public static final String VIEW_MODE = "view_mode";
    public static final int MODE_NORMAL = 0;
    public static final String USER_DATA = "user_data";
    public static final int MODE_SHOW_SHARE = 1;
    public static final String NO_FIND = "NO_FIND";

    public static final int WRITE_PHONE_NUMBER = 1;
    public static int PHONE_NUMBER_CHECK = 0;

//    public static final String PHONE_NUMBER = "PHONE_NUMBER";
//    public static final String SOS_MESSAGE = "SOS_MESSAGE";
      public static final String SOS_MESSAGE = "message";


    public static final int SOS_TYPE_REGIST = 0;
    private int mType = SOS_TYPE_REGIST;

//    public static void startActivity(BaseActivity activity, String users) {
//        Intent intent = new Intent(activity, Activity_SosEditor.class);
//        intent.putExtra(VIEW_MODE,MODE_NORMAL);
//        if(users != null){
//            intent.putExtra(USER_DATA,users);
//        }
//        activity.moveActivityForResult(intent, Define.INTENT_RESULT_USER);
//    }

    private int mViewMode;
    private String sos_message;
    private String message;

    private LinearLayout linearlayout_btn_invite;

    private TextView textview_user_cnt;
    private ListView listview;
    private EditText activity_sos_phone_edit_number_write;
    private Button button_sos_edit;

    private LocalUserSelectListAdapter mLocalUserSelectListAdapter;

    private ArrayList<Data_LocalUserInfo> mUserList;

    private ArrayList<Data_LocalUserInfo> mSelectedList;

    private Data_LocalUserInfo userInfo;

    private Data_ActionItem mActionItem;

    public static final String KEY_PHONE = "phone";
    public static final String KEY_USER = "phone";

    private static final String EM_PHONE_NUMBER = "em";

    private EditText activity_sos_phone_edit;
    private LinearLayout linearlayout_invite_area;

    public static void startActivity(BaseActivity activity, Data_ActionItem mActionItem) {
        Intent intent = new Intent(activity, Activity_SosEditor.class);
        if (mActionItem != null) {
            intent.putExtra(USER_DATA, mActionItem);
        }
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_USER_SOS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_editor);
        mUserList = new ArrayList<>();

        mType = getIntent().getIntExtra(Define.INTENTKEY_USER_SOS_TYPE, MODE_NORMAL);

        activity_sos_phone_edit_number_write = (EditText) findViewById(R.id.activity_sos_phone_edit_number_write);
        button_sos_edit = (Button) findViewById(R.id.button_sos_edit);
        button_sos_edit.setOnClickListener(mClickListener);
        activity_sos_phone_edit = (EditText) findViewById(R.id.activity_sos_phone_edit);

        linearlayout_invite_area = (LinearLayout) findViewById(R.id.linearlayout_invite_area);
        linearlayout_invite_area.setOnClickListener(mClickListener);

        mViewMode = getIntent().getIntExtra(VIEW_MODE, MODE_SHOW_SHARE);

        mActionItem = (Data_ActionItem)getIntent().getSerializableExtra(USER_DATA);

        if(mActionItem != null) {
            getContactsDataAll();

            for (int i = 0; i < mActionItem.data.size(); i++) {
                if (SOS_MESSAGE.equals(mActionItem.data.get(i).key)) {
                    message = mActionItem.data.get(i).value;
                    break;
                }
            }

            for (int j = 0; j < mUserList.size(); j++) {
                for (int i = 0; i < mActionItem.data.size(); i++) {
                    if (mUserList.get(j).phone_num.equals(mActionItem.data.get(i).value)) {
                        mUserList.get(j).isSelected = true;
                        mSelectedList = getSelectedItems();
                    }
                }
            }
            mUserList = new ArrayList<>();
        }

//        mSelectedList = (ArrayList<Data_LocalUserInfo>) getIntent().getSerializableExtra(USER_DATA);

        listview = (ListView) findViewById(R.id.listview);

        compareList();

        if(mSelectedList != null) {
            activity_sos_phone_edit.setText(message);

                        mUserList.addAll(mSelectedList);

            mLocalUserSelectListAdapter = new LocalUserSelectListAdapter(Activity_SosEditor.this, getSelectedItems());
            listview.setAdapter(mLocalUserSelectListAdapter);
        }

        String titleW = getResources().getString(R.string.sos_title);
        setHeaderView(R.drawable.btn_back_white, titleW, 0);

        if (mViewMode == MODE_NORMAL) {
            linearlayout_invite_area.setVisibility(View.GONE);
        } else {
            linearlayout_invite_area.setVisibility(View.VISIBLE);
        }

        textview_user_cnt = (TextView) findViewById(R.id.textview_user_cnt);

        mLocalUserSelectListAdapter = new LocalUserSelectListAdapter(Activity_SosEditor.this, getSelectedItems());
        listview.setAdapter(mLocalUserSelectListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                mUserList.get(position).isSelected = !mUserList.get(position).isSelected;
                mLocalUserSelectListAdapter.notifyDataSetChanged();
            }
        });
    }

    public ArrayList<Data_LocalUserInfo> getSelectedItems() {
        ArrayList<Data_LocalUserInfo> selectedItemList = new ArrayList<>();
        for (int i = 0; i < mUserList.size(); i++) {
            if (mUserList.get(i).isSelected) {
                selectedItemList.add(mUserList.get(i));
            }
        }
        return selectedItemList;
    }

    public int getSelectedItemCount() {
        int cnt = 0;
        for (int i = 0; i < mUserList.size(); i++) {
            if (mUserList.get(i).isSelected) {
                cnt++;
            }
        }
        return cnt;
    }


    @Override
    protected void onResume() {
        super.onResume();
        sos_message = activity_sos_phone_edit.getText().toString();

    }


    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            switch (viewId) {
                case R.id.linearlayout_invite_area:
                    showProgressPopup();
                    LocalUserSelectActivity.startActivity(Activity_SosEditor.this, mUserList);

                    //입력한 전화번호를 전달한다.
                    break;
                case R.id.button_sos_edit:
                    showProgressPopup();
                    String number = activity_sos_phone_edit_number_write.getText().toString().replaceAll("-","");
                    getContactsDataAll();
                    if(!WezoneUtil.isEmptyStr(number) && number != null){
                        if(mUserList != null){

                            for(int i = 0; i<mUserList.size(); i++) {
                                String num_phone = mUserList.get(i).phone_num.toString().replaceAll("-", "");
                                if (number.equals(num_phone)) {
                                    mUserList.get(i).isSelected = true;
                                    getSelectedItems();
                                }
                            }
                            mLocalUserSelectListAdapter = new LocalUserSelectListAdapter(Activity_SosEditor.this, getSelectedItems());
                            listview.setAdapter(mLocalUserSelectListAdapter);
                        }
                    }

                    //입력한 전화번호를 내부의 번호와 비교한다.
                    hidePorgressPopup();
                    break;
            }

        }

    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Define.INTENT_RESULT_USER:
                if (resultCode == RESULT_OK) {

                    mUserList = (ArrayList<Data_LocalUserInfo>) data.getSerializableExtra(Define.INTENTKEY_USER_VALUE);
                    mLocalUserSelectListAdapter = new LocalUserSelectListAdapter(Activity_SosEditor.this, getSelectedItems());
                    listview.setAdapter(mLocalUserSelectListAdapter);


                }
                break;
        }

    }

    public void compareList() {
        if (mSelectedList != null && mSelectedList.size() > 0) {
            for (Data_LocalUserInfo selectedUser : mSelectedList) {
                if (mUserList != null && mUserList.size() > 0) {
                    for (int i = 0; i < mUserList.size(); i++) {
                        Data_LocalUserInfo item = mUserList.get(i);
                        if (selectedUser.user_name.equals(item.user_name) && selectedUser.phone_num.equals(item.phone_num)) {
                            mUserList.get(i).isSelected = true;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClickLeftBtn(View v) {
        sos_message = activity_sos_phone_edit.getText().toString();

//        if (WezoneUtil.isEmptyStr(sos_message)) {
//            Toast.makeText(Activity_SosEditor.this, "메세지를 입력해주세요", Toast.LENGTH_SHORT).show();
//            return;
//        }

        Intent i = new Intent();
        i.putExtra(Define.SOS_MESSAGE, sos_message);
        i.putExtra(Define.INTENTKEY_USER_VALUE, getSelectedItems());
        setResult(RESULT_OK, i);
        super.onClickLeftBtn(v);


    }


    public void getContactsDataAll() {
        //수동입력 했을 때 이미 리스트에 있는 것들은 놔두기.
        //대신 중복되는게 있으면 안됨.
        String number = activity_sos_phone_edit_number_write.getText().toString().replaceAll("-","");

        String check = null;
        if(getSelectedItems() != null) {
            for (int i = 0; i < mUserList.size(); i++) {
                if(mUserList.get(i).isSelected == true) {
                    for (Data_LocalUserInfo data_localUserInfo : getSelectedItems()) {
                        String selected_num = data_localUserInfo.phone_num.toString().replaceAll("-", "");
                        if (selected_num.equals(number)) {
                            Toast.makeText(m_Context, " 이미 리스트에 있습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
//                            check = NO_FIND;
                        }
                    }
                }else{
                    mUserList.remove(i);
                    i--;
                }
            }
//        if(check != null){
//            Toast.makeText(m_Context," 주소록에 해당 번호가 없습니다. ",Toast.LENGTH_SHORT).show();
//          }
        }

        showProgressPopup();
        // 주소록 URI
        Uri uri_contacts = ContactsContract.Contacts.CONTENT_URI;

        // 주소록 데이터를 가져오기 위한 Projection
        String[] str_contactProjection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,};

        // 커서 세팅
        Cursor cursor_contacts = getContentResolver().query(uri_contacts, str_contactProjection, null, null, null);

        // 데이터 없으면 return
        if (cursor_contacts.getCount() == 0) {
            cursor_contacts.close();
            return;
        }
        // 주소록 데이터가 존재한다면
        else {
            cursor_contacts.moveToFirst();

            do {

                String hasPhoneNum = cursor_contacts.getString(cursor_contacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                //전화 번호 있음
                if ("1".equals(hasPhoneNum)) {

                    userInfo = new Data_LocalUserInfo();
                    userInfo.id = cursor_contacts.getString(cursor_contacts.getColumnIndex(ContactsContract.Contacts._ID));
                    userInfo.user_name = cursor_contacts.getString(cursor_contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                    Uri uri_phoneNumber = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                    // 해당 ID 내에 존재하는 전화번호를 불러온다.
                    String str_selectionPhoneNumber = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= " + userInfo.id;

                    String[] str_phoneNumberProjection = new String[]{
                            ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    };


                    Cursor cursor_phoneNumber = getContentResolver().query(uri_phoneNumber, str_phoneNumberProjection, str_selectionPhoneNumber, null, null);

                    if (cursor_phoneNumber.getCount() == 0) {
                        break;
                    } else {
                        cursor_phoneNumber.moveToFirst();
                        do {
                            userInfo.phone_num = cursor_phoneNumber.getString(cursor_phoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        } while (cursor_phoneNumber.moveToNext());
                    }

                    cursor_phoneNumber.close();
                        mUserList.add(userInfo);

                } else {

                }

            } while (cursor_contacts.moveToNext());
        }

        cursor_contacts.close();
        hidePorgressPopup();
    }


    @Override
    public void onRestart(){
        super.onRestart();
        hidePorgressPopup();
    }




}