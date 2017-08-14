package com.vinetech.wezone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Common.Activity_PhotoPicker_Folder;
import com.vinetech.wezone.Common.EditTextActivity;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Upload;
import com.vinetech.wezone.RevPacket.Rev_user;
import com.vinetech.wezone.SendPacket.Send_PutUser;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity {

    public static final int IMAGE_TYPE_PROFILE = 0;
    public static final int IMAGE_TYPE_BG = 1;
    private int mImageType = IMAGE_TYPE_PROFILE;

    public static final int EDIT_TEXT_NAME = 0;
    public static final int EDIT_TEXT_STATUS_MSG = 1;
    private int mEditTextType = EDIT_TEXT_NAME;

    public static void startActivity(BaseActivity activity) {
        Intent intent = new Intent(activity, EditProfileActivity.class);
        activity.moveActivity(intent);
    }

    private RelativeLayout relativelayout_bg;
    private ImageView imageview_bg;
    private LinearLayout linearlayout_btn_add_photo_bg;

    private ImageView imageview_profile;
    private LinearLayout linearlayout_btn_add_photo;


    private LinearLayout linearlayout_btn_name;
    private TextView textview_name;

    private LinearLayout linearlayout_btn_status_msg;
    private TextView textview_status_msg;

    private String mImagePath;
    private String mBackImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setHeaderView(R.drawable.btn_back_white, null, 0);

        relativelayout_bg = (RelativeLayout) findViewById(R.id.relativelayout_bg);
        relativelayout_bg.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));

        imageview_bg = (ImageView)findViewById(R.id.imageview_bg);
        linearlayout_btn_add_photo_bg = (LinearLayout) findViewById(R.id.linearlayout_btn_add_photo_bg);
        linearlayout_btn_add_photo_bg.setOnClickListener(mClickListener);

        imageview_profile = (ImageView)findViewById(R.id.imageview_profile);
        linearlayout_btn_add_photo = (LinearLayout) findViewById(R.id.linearlayout_btn_add_photo);
        linearlayout_btn_add_photo.setOnClickListener(mClickListener);


        textview_name = (TextView)findViewById(R.id.textview_name);
        linearlayout_btn_name = (LinearLayout) findViewById(R.id.linearlayout_btn_name);
        linearlayout_btn_name.setOnClickListener(mClickListener);

        textview_status_msg = (TextView)findViewById(R.id.textview_status_msg);
        linearlayout_btn_status_msg = (LinearLayout) findViewById(R.id.linearlayout_btn_status_msg);
        linearlayout_btn_status_msg.setOnClickListener(mClickListener);


        if(WezoneUtil.isEmptyStr(getShare().getMyInfo().bg_img_url) == false){
            showImageFromRemote(getShare().getMyInfo().bg_img_url,0,imageview_bg);
        }else{
            imageview_bg.setVisibility(View.GONE);
        }

        if(WezoneUtil.isEmptyStr(getShare().getMyInfo().img_url) == false){
            showImageFromRemoteWithCircle(getShare().getMyInfo().img_url,R.drawable.btn_circle_white,imageview_profile);
        }else{
            imageview_profile.setImageResource(R.drawable.im_bunny_photo);
        }

        textview_name.setText(getShare().getMyInfo().user_name);

        String status_msg = getShare().getMyInfo().status_message;
        if(WezoneUtil.isEmptyStr(status_msg)){
            status_msg = "상태메세지를 입력해주세요.";
        }
        textview_status_msg.setText(status_msg);

    }

    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            switch (viewId) {
                case R.id.linearlayout_btn_name:
                    mEditTextType = EDIT_TEXT_NAME;
                    EditTextActivity.startActivity(EditProfileActivity.this, "프로파일", getShare().getMyInfo().user_name);
                    break;
                case R.id.linearlayout_btn_status_msg:
                    mEditTextType = EDIT_TEXT_STATUS_MSG;
                    EditTextActivity.startActivity(EditProfileActivity.this, "프로파일", getShare().getMyInfo().status_message);
                    break;
                case R.id.linearlayout_btn_add_photo:
                    mImageType = IMAGE_TYPE_PROFILE;
                    Activity_PhotoPicker_Folder.startFolderActivityForResult(EditProfileActivity.this, 1);
                    break;
                case R.id.linearlayout_btn_add_photo_bg:
                    mImageType = IMAGE_TYPE_BG;
                    Activity_PhotoPicker_Folder.startFolderActivityForResult(EditProfileActivity.this, 1);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        setTransparentHeaderView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Define.INTENT_RESULT_EDIT_TEXT:
                if (resultCode == RESULT_OK) {

                    String contents = data.getStringExtra(EditTextActivity.EDITTEXT_CONTENTS);

                    if(mEditTextType == EDIT_TEXT_NAME){
                        textview_name.setText(contents);
                        getShare().getMyInfo().user_name = contents;
                        CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_NAME, contents);
                        sendUserPut(Send_PutUser.FLAG_USER_NAME, contents);
                    }else{
                        textview_status_msg.setText(contents);
                        getShare().getMyInfo().status_message = contents;
                        sendUserPut(Send_PutUser.FLAG_STATUS_MESSAGE, contents);
                    }
                }
                break;
            case Define.INTENT_RESULT_CAMERA:
                case Define.INTENT_RESULT_PHOTO:
                    if (resultCode == RESULT_OK) {
                        ArrayList<Data_PhotoPickerImage> pickerImages = getShare().popPhotoPickerImages();

                        Data_PhotoPickerImage data_photo = pickerImages.get(0);

                        if (mImageType == IMAGE_TYPE_PROFILE) {
                            mImagePath = data_photo.path;
                            uploadImageFile(Define.IMAGE_TYPE_PROFILE, Define.IMAGE_STATUS_UPDATE, getShare().getMyInfo().uuid, mImagePath);
                        } else if (mImageType == IMAGE_TYPE_BG) {

                            mBackImagePath = data_photo.path;
                            uploadImageFile(Define.IMAGE_TYPE_BACKGROUND, Define.IMAGE_STATUS_UPDATE, getShare().getMyInfo().uuid, mBackImagePath);

                            imageview_bg.setVisibility(View.VISIBLE);
                            imageview_bg.setImageBitmap(data_photo.thumbBitmap);
                        } else {
                            getShare().resetPhotoPickerImages();
                        }
                    }
                    break;

        }

    }

//
    @Override
    public void uploadResult(String type, Rev_Upload resultData) {
        super.uploadResult(type, resultData);
        if (mImageType == IMAGE_TYPE_PROFILE) {
            sendUserPut(Send_PutUser.FLAG_IMG_URL,resultData.url);
            getShare().getMyInfo().img_url = resultData.url;
            CryptPreferences.putCryptString(m_Context, Define.SHARE_KEY_IMAGE_URL, resultData.url);
            showImageFromRemoteWithCircle(getShare().getMyInfo().img_url,R.drawable.btn_circle_white,imageview_profile);
        }else{
            sendUserPut(Send_PutUser.FLAG_BG_IMG_URL,resultData.url);
            getShare().getMyInfo().bg_img_url = resultData.url;
        }
    }

    private void sendUserPut(String flag, String val) {

        Send_PutUser putUser = new Send_PutUser();

        putUser.flag = flag;
        putUser.val = val;

        showProgressPopup();
        Call<Rev_user> PutWezoneCall = wezoneRestful.putUser(putUser);
        PutWezoneCall.enqueue(new Callback<Rev_user>() {

            @Override
            public void onResponse(Call<Rev_user> call, Response<Rev_user> response) {
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_user> call, Throwable t) {
                hidePorgressPopup();
            }
        });

    }
}