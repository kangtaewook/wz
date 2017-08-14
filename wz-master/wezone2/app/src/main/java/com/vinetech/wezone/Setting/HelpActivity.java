package com.vinetech.wezone.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Common.Activity_PhotoPicker_Folder;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_EmailHelp;
import com.vinetech.wezone.RevPacket.Rev_Upload;
import com.vinetech.wezone.SendPacket.Send_PostEmailHelp;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpActivity extends BaseActivity {

    public static final int IMAGE_TYPE_EMAIL_HELP = 0;
    public static final int TYPE_UPLOAD_OK = 0;
    public static final int TYPE_UPLOAD_NOT = 1;
    private int mImageType = IMAGE_TYPE_EMAIL_HELP;

    private EditText activity_setting_help_content;
    private ImageView activity_setting_help_image;
    private ImageView activity_setting_help_upload_image;
    private LinearLayout activity_setting_help_delete;

    private int mType;
    private String help_content;
    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        //mType = TYPE_UPLOAD_OK;

        setHeaderView(R.drawable.btn_back_white, "고객센터", R.drawable.btn_check);

        activity_setting_help_content = (EditText) findViewById(R.id.activity_setting_help_content);
        activity_setting_help_image = (ImageView) findViewById(R.id.activity_setting_help_image);
        activity_setting_help_image.setOnClickListener(mClickListener);
        activity_setting_help_upload_image = (ImageView) findViewById(R.id.activity_setting_help_upload_image);
        activity_setting_help_upload_image.setOnClickListener(mClickListener);
        activity_setting_help_delete = (LinearLayout) findViewById(R.id.activity_setting_help_delete);
        activity_setting_help_delete.setOnClickListener(mClickListener);
    }

    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId) {
                case R.id.activity_setting_help_image:
                    mImageType = IMAGE_TYPE_EMAIL_HELP;
                    Activity_PhotoPicker_Folder.startFolderActivityForResult(HelpActivity.this, 1);
                    break;
                case R.id.activity_setting_help_delete:
                    activity_setting_help_upload_image.setVisibility(View.GONE);
                    activity_setting_help_delete.setVisibility(View.GONE);
                    mImageUrl = null;
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Define.INTENT_RESULT_CAMERA:
            case Define.INTENT_RESULT_PHOTO:
                if (resultCode == RESULT_OK) {
                    ArrayList<Data_PhotoPickerImage> pickerImages = getShare().popPhotoPickerImages();

                    Data_PhotoPickerImage data_photo = pickerImages.get(0);

                    if (mImageType == mImageType) {
                        String mImagePath = data_photo.path;
                        uploadImageFile(Define.IMAGE_TYPE_SETTING_HELP, Define.IMAGE_STATUS_UPDATE, getUuid(), mImagePath);

                        activity_setting_help_upload_image.setImageBitmap(data_photo.thumbBitmap);
                    } else {
                        getShare().resetPhotoPickerImages();
                    }
                }
                break;

        }
    }

    @Override
    public void uploadResult(String type, Rev_Upload resultData) {
        super.uploadResult(type, resultData);
        if (Define.IMAGE_TYPE_SETTING_HELP.equals(type)) {
            mImageUrl = resultData.url;
            activity_setting_help_upload_image.setVisibility(View.VISIBLE);
            activity_setting_help_delete.setVisibility(View.VISIBLE);
        }
    }


    private void ongetText() {
        help_content = activity_setting_help_content.getText().toString().trim();
    }


    private void postEmailHelp() {

        ongetText();

        Send_PostEmailHelp postEmailHelp = new Send_PostEmailHelp();

        postEmailHelp.img_url = mImageUrl;
        postEmailHelp.help_content = help_content;

        Call<Rev_EmailHelp> putEmailHelpCall = wezoneRestful.putEmailHelp(postEmailHelp);
        putEmailHelpCall.enqueue(new Callback<Rev_EmailHelp>() {
            @Override
            public void onResponse(Call<Rev_EmailHelp> call, Response<Rev_EmailHelp> response) {

            }

            @Override
            public void onFailure(Call<Rev_EmailHelp> call, Throwable t) {

            }
        });

    }

    @Override
    public void onClickRightBtn(View v) {
        postEmailHelp();
        super.onClickRightBtn(v);
        finish();
    }

    @Override
    public void onClickLeftBtn(View v) {
        super.onClickLeftBtn(v);
    }


}
