package com.vinetech.wezone.Profile;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_LocalUserInfo;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import java.util.ArrayList;

public class LocalUserSelectActivity extends BaseActivity {

    public static final String USER_DATA = "user_data";

    public static final int MODE_NORMAL = 0;
    public static final int MODE_SHOW_SHARE = 1;
    public static final String EDIT = "EDIT";

    public static void startActivity(BaseActivity activity, ArrayList<Data_LocalUserInfo> users) {
        Intent intent = new Intent(activity, LocalUserSelectActivity.class);
        if (users != null) {
            intent.putExtra(USER_DATA, users);
        }
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_USER);
    }

    private int mViewMode;

    private LinearLayout linearlayout_invite_area;
    private LinearLayout linearlayout_btn_invite;

    private TextView textview_user_cnt;
    private ListView listview;

    private LocalUserSelectListAdapter mLocalUserSelectListAdapter;

    private ArrayList<Data_LocalUserInfo> mUserList;

    private ArrayList<Data_LocalUserInfo> mSelectedList;

    private Data_LocalUserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);


        //입력한 전화번호를 받고 인텐트
        setHeaderView(R.drawable.btn_back_white, "주소록", R.drawable.btn_check);

        mSelectedList = (ArrayList<Data_LocalUserInfo>) getIntent().getSerializableExtra(USER_DATA);

        listview = (ListView) findViewById(R.id.listview);

        View header = getLayoutInflater().inflate(R.layout.user_select_list_header, null, false);
        listview.addHeaderView(header, null, false);

        linearlayout_invite_area = (LinearLayout) findViewById(R.id.linearlayout_invite_area);
        linearlayout_btn_invite = (LinearLayout) findViewById(R.id.linearlayout_btn_invite);
        if (mViewMode == MODE_NORMAL) {
            linearlayout_invite_area.setVisibility(View.GONE);
        } else {
            linearlayout_invite_area.setVisibility(View.VISIBLE);
        }

        textview_user_cnt = (TextView) findViewById(R.id.textview_user_cnt);

        mUserList = new ArrayList<>();

        getContactsDataAll();

        compareList();

        mLocalUserSelectListAdapter = new LocalUserSelectListAdapter(LocalUserSelectActivity.this, mUserList);
        listview.setAdapter(mLocalUserSelectListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                if(position == 0)
//                    return;
                int realPos = position - 1;

                mUserList.get(realPos).isSelected = !mUserList.get(realPos).isSelected;
                mLocalUserSelectListAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);
        Intent i = new Intent();
        i.putExtra(Define.INTENTKEY_USER_VALUE, getSelectedItems());
        i.putExtra(Define.INTENTKEY_USER_SOS_TYPE, MODE_SHOW_SHARE);
        setResult(RESULT_OK, i);
        finish();
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

    public void compareList() {
        if (mSelectedList != null && mSelectedList.size() > 0) {
            for (Data_LocalUserInfo selectedUser : mSelectedList) {
                if(selectedUser.isSelected == true){
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

        mLocalUserSelectListAdapter = new LocalUserSelectListAdapter(LocalUserSelectActivity.this, mSelectedList);
        listview.setAdapter(mLocalUserSelectListAdapter);

        mLocalUserSelectListAdapter.notifyDataSetChanged();
    }

    public void getContactsDataAll() {

        // 주소록 URI
        Uri uri_contacts = ContactsContract.Contacts.CONTENT_URI;

        // 주소록 데이터를 가져오기 위한 Projection
        String[] str_contactProjection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,};

        String str_phoneName = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " asc";
        // 커서 세팅
        Cursor cursor_contacts = getContentResolver().query(uri_contacts, str_contactProjection, null, null, str_phoneName);

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


                    Cursor cursor_phoneNumber = getContentResolver().query(uri_phoneNumber, str_phoneNumberProjection, str_selectionPhoneNumber, null, str_phoneName);

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

    }


}
