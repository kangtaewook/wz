package com.vinetech.wezone.Wezone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vinetech.ui.CustomImageView;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Common.Activity_PhotoPicker_Folder;
import com.vinetech.wezone.Data.Data_Board;
import com.vinetech.wezone.Data.Data_File;
import com.vinetech.wezone.Data.Data_PhotoPickerImage;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_PostBoard;
import com.vinetech.wezone.RevPacket.Rev_Upload;
import com.vinetech.wezone.SendPacket.Send_PostBoard;
import com.vinetech.wezone.SendPacket.Send_PutBoard;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 게시물 등록 화면
 *
 */

public class WriteBoardActivity extends BaseActivity {

    public static final String BOARD_TYPE = "type";
    public static final String WEZONE = "wezone_id";
    public static final String WEZONE_ID = "wezone_id";
    public static final String BOARD_DATA = "board_data";
    public static final String BOARD_DATA_ID = "board_data_id";
    public static final String BOARD_DATA_TOTAL_COUNT = "BOARD_DATA_TOTAL_COUNT";
    public static final String ADD = "ADD";
    public static final String DELETE = "DELETE";

    public static final int BOARD_TYPE_REGIST = 0;
    public static final int BOARD_TYPE_EDIT = 1;

    public static void startActivityWithRegist(BaseActivity activity, Data_WeZone weZone) {
        Intent intent = new Intent(activity, WriteBoardActivity.class);
        intent.putExtra(BOARD_TYPE, BOARD_TYPE_REGIST);
        intent.putExtra(WEZONE, weZone);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_BOARD);
    }

    public static void startActivityWithEdit(BaseActivity activity, Data_Board boardData,  Data_WeZone weZone) {
        Intent intent = new Intent(activity, WriteBoardActivity.class);
        intent.putExtra(BOARD_TYPE, BOARD_TYPE_EDIT);
        intent.putExtra(BOARD_DATA, boardData);
        intent.putExtra(WEZONE, weZone);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_BOARD);
    }

    public RelativeLayout relativelayout_image_area;
    public CustomImageView imageview_board;
    public LinearLayout linearlayout_btn_delete;


    public EditText edittext_contents;

    public LinearLayout linearlayout_btn_photo;
    public LinearLayout linearlayout_btn_notice;


    Data_WeZone mWezone;
    public Data_Board mDataBoard;
    public String mPutFlag;
    public String board_file_url;
    public String mstrTemp;
    public int mBoard_Type;

    private String mImagePath;
    private String image_delete_action = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_board);
        Send_PostBoard board = new Send_PostBoard();

        String title = getResources().getString(R.string.board_title);
        setHeaderView(R.drawable.btn_back_white, title, R.drawable.btn_check);

        mWezone = (Data_WeZone) getIntent().getSerializableExtra(WEZONE);
        mDataBoard = (Data_Board) getIntent().getSerializableExtra(BOARD_DATA);
        mBoard_Type = getIntent().getIntExtra(BOARD_TYPE, BOARD_TYPE_REGIST);

        relativelayout_image_area = (RelativeLayout) findViewById(R.id.relativelayout_image_area);
        relativelayout_image_area.setVisibility(View.GONE);
        imageview_board = (CustomImageView) findViewById(R.id.imageview_board);
        edittext_contents = (EditText) findViewById(R.id.edittext_contents);
        linearlayout_btn_delete = (LinearLayout) findViewById(R.id.linearlayout_btn_delete);
        linearlayout_btn_delete.setOnClickListener(mClickListener);
        linearlayout_btn_photo = (LinearLayout) findViewById(R.id.linearlayout_btn_photo);
        linearlayout_btn_photo.setOnClickListener(mClickListener);
        linearlayout_btn_notice = (LinearLayout) findViewById(R.id.linearlayout_btn_notice);
        linearlayout_btn_notice.setOnClickListener(mClickListener);


        if(mBoard_Type == BOARD_TYPE_EDIT){
            linearlayout_btn_notice.setVisibility(View.GONE);
        }

        if (WezoneUtil.isManager(mWezone.manage_type)) {
            linearlayout_btn_notice.setVisibility(View.VISIBLE);
        }

        reloadLayout();
    }


    public void reloadLayout() {
        if (null != mDataBoard) {

            if (!"0".equals(String.valueOf(mDataBoard.board_file.size()))) {
                board_file_url = mDataBoard.board_file.get(0).url;
            }
            if (mDataBoard.board_file != null) {
                if (WezoneUtil.isEmptyStr(board_file_url) == false) {
                    relativelayout_image_area.setVisibility(View.VISIBLE);
                    ImageView imageview_board = (ImageView) findViewById(R.id.imageview_board);
                    showImageFromRemote(board_file_url, R.drawable.ic_bunny_image, imageview_board);
                }
            }
            edittext_contents.setText(mDataBoard.content);

            if ("T".equals(mDataBoard.notice_flag)) {
                linearlayout_btn_notice.setSelected(true);
            } else {
                linearlayout_btn_notice.setSelected(false);
            }
        } else {

        }
    }
    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();
            switch (viewId) {
                case R.id.linearlayout_btn_photo:
                    Activity_PhotoPicker_Folder.startFolderActivityForResult(WriteBoardActivity.this, 1);
                    break;

                case R.id.linearlayout_btn_notice:
                    if (linearlayout_btn_notice.isSelected()) {
                        linearlayout_btn_notice.setSelected(false);
                    } else {
                        linearlayout_btn_notice.setSelected(true);
                    }
                    break;

                case R.id.linearlayout_btn_delete:
                    relativelayout_image_area.setVisibility(View.GONE);
                    mImagePath = null;
                    image_delete_action = DELETE;
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
                    mImagePath = data_photo.path;
                    imageview_board.setImageBitmap(data_photo.thumbBitmap);
                    relativelayout_image_area.setVisibility(View.VISIBLE);
                } else {
                    mImagePath = null;
                    getShare().resetPhotoPickerImages();
                }
                break;
        }

    }

    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);

        mstrTemp = edittext_contents.getText().toString().trim();

        if (WezoneUtil.isEmptyStr(mstrTemp)) {
            String str = getResources().getString(R.string.please_enter_your_board);
            Toast.makeText(WriteBoardActivity.this, str, Toast.LENGTH_SHORT).show();
            return;
        }
        editBoardActivitiy();
    }

    public void editBoardActivitiy() {
        //C 글만 변경, B 이미지만 변경, A 둘다 변경
        if (null != mDataBoard) {
            if (!mstrTemp.equals(mDataBoard.content)) {
                mPutFlag = "C";
            }
            if (mImagePath != null) {
                if (!mstrTemp.equals(mDataBoard.content) && !mImagePath.equals(board_file_url)) {
                    mPutFlag = "A";
                } else if(mstrTemp.equals(mDataBoard.content) && !mImagePath.equals(board_file_url)){
                    mPutFlag = "B";
                }
                uploadImageFile(Define.IMAGE_TYPE_BOARD, Define.IMAGE_STATUS_NEW, null, mImagePath);
            } else if (board_file_url != null) {
                if (!mstrTemp.equals(mDataBoard.content) && image_delete_action.equals(DELETE)) {
                    mPutFlag = "A";
                    Send_PutBoard(null, mstrTemp);
                }else if(!mstrTemp.equals(mDataBoard.content)){
                    mPutFlag = "C";
                    Send_PutBoard(null, mstrTemp);
                } else if(image_delete_action.equals(DELETE)){
                    //이미지만 변경됨 이미지 삭제한 상황
                    mPutFlag = "B";
                    Send_PutBoard(null, mstrTemp);
                } else{
                    // 아무것도 변경안됬을 때, 업로드된 이미지 삭제 안하고, 글도 안바뀌는 경우
                    finish();
                }

            } else {
                if (!mstrTemp.equals(mDataBoard.content) && image_delete_action.equals(DELETE)) {
                    mPutFlag = "A";
                    Send_PutBoard(null, mstrTemp);
                }else if(!mstrTemp.equals(mDataBoard.content)){
                    mPutFlag = "C";
                    Send_PutBoard(null, mstrTemp);
                } else{
                    //이미지만 변경됨 이미지 삭제한 상황
                    mPutFlag = "B";
                    Send_PutBoard(null, mstrTemp);
                }
            }
        } else {

            if (mImagePath != null) {
                uploadImageFile(Define.IMAGE_TYPE_BOARD, Define.IMAGE_STATUS_NEW, null, mImagePath);
            } else {
                Send_PostBoard(null, mstrTemp);
            }
        }
    }

    @Override
    public void uploadResult(String type, Rev_Upload resultData) { // 업로드된 이미지
        super.uploadResult(type, resultData);
        String strTemp = edittext_contents.getText().toString().trim();
        if (null != mDataBoard) {
            Send_PutBoard(resultData.url, strTemp);
            board_file_url = resultData.url;
        } else {
            Send_PostBoard(resultData.url, strTemp);
            board_file_url = resultData.url;
        }
    }


    public void Send_PostBoard(String imgUrl, String contents) {

        Send_PostBoard board = new Send_PostBoard();
        board.wezone_id = mWezone.wezone_id;

        if (linearlayout_btn_notice.isSelected()) {
            board.notice_flag = "T";
        } else {
            board.notice_flag = "F";
        }
        board.content = contents;

        if (imgUrl != null) {
            Data_File file = new Data_File();
            file.url = imgUrl;
            file.type = Data_File.FILE_TYPE_PHOTO;
            file.format = "png";
            ArrayList<Data_File> fileArray = new ArrayList<>();
            fileArray.add(file);
            board.board_file = fileArray;
        }

        showProgressPopup(); //로딩 이미지 등장
        Call<Rev_PostBoard> sendPostBoardCall = wezoneRestful.postWeZoneBoard(board);
        sendPostBoardCall.enqueue(new Callback<Rev_PostBoard>() {
            @Override
            public void onResponse(Call<Rev_PostBoard> call, Response<Rev_PostBoard> response) {
                Rev_PostBoard revPostBoard = response.body();
                if (isNetSuccess(revPostBoard)) {

                    hidePorgressPopup(); //로딩 이미지 사라짐
                    Intent i = new Intent();
                    i.putExtra(BOARD_DATA_ID,revPostBoard.board_id);
                    i.putExtra(BOARD_DATA_TOTAL_COUNT,ADD);
                    setResult(RESULT_OK, i);
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Rev_PostBoard> call, Throwable t) {

            }
        });

    }

    public void Send_PutBoard(String imgUrl, String contents) {

        Send_PutBoard putboard = new Send_PutBoard();
        putboard.board_id = mDataBoard.board_id;
        putboard.content = contents;
        putboard.put_flag = mPutFlag;

        ArrayList<Data_File> data_file_list = new ArrayList<>();
        if (imgUrl != null) {
            Data_File data_file = new Data_File();
            data_file.url = imgUrl;
            data_file.type = Data_File.FILE_TYPE_PHOTO;
            data_file.format = "png";
            data_file_list.add(data_file);
        }

        putboard.board_file = data_file_list;

        mDataBoard.content = putboard.content;
        mDataBoard.board_id = putboard.board_id;
        mDataBoard.board_file = putboard.board_file;

        showProgressPopup();
        Call<Rev_Base> sendPostBoardCall = wezoneRestful.putWeZoneBoard(putboard);
        sendPostBoardCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revPutBoard = response.body();
                if (isNetSuccess(revPutBoard)) {
                    Intent i = new Intent();
                    i.putExtra(BOARD_DATA,mDataBoard);
                    setResult(RESULT_OK, i);

                    hidePorgressPopup();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }
}
