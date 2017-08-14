package com.vinetech.wezone.Wezone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 위존 리뷰 등록 화면
 *
 */

public class WriteReviewActivity extends BaseActivity {

    public static final String REVIEW_TYPE = "type";
    public static final String WEZONE_ID = "wezone_id";
    public static final String REVIEW_ID = "review_id";
    public static final String REVIEW_CONTENTS = "contents";

    public static final int REVIEW_TYPE_REGIST = 0;
    public static final int REVIEW_TYPE_EDIT = 1;

    public static void startActivityWithRegist(BaseActivity activity,String wezone_id) {
        Intent intent = new Intent(activity, WriteReviewActivity.class);
        intent.putExtra(REVIEW_TYPE,REVIEW_TYPE_REGIST);
        intent.putExtra(WEZONE_ID,wezone_id);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_REVIEW);
    }

    public static void startActivityWithEdit(BaseActivity activity, String review_id, String contents) {
        Intent intent = new Intent(activity, WriteReviewActivity.class);
        intent.putExtra(REVIEW_TYPE,REVIEW_TYPE_EDIT);
        intent.putExtra(REVIEW_ID,review_id);
        intent.putExtra(REVIEW_CONTENTS,contents);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_REVIEW);
    }

    public EditText edittext_contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        String title = getResources().getString(R.string.review_title);
        setHeaderView(R.drawable.btn_back_white,title,R.drawable.btn_check);

        edittext_contents = (EditText) findViewById(R.id.edittext_contents);
    }

    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);

        String strTemp = edittext_contents.getText().toString().trim();

        if (WezoneUtil.isEmptyStr(strTemp)) {
            String str = getResources().getString(R.string.please_enter_your_review);
            Toast.makeText(WriteReviewActivity.this, str, Toast.LENGTH_SHORT).show();
            return;
        }
        //리뷰 등록 전문..
    }
}
