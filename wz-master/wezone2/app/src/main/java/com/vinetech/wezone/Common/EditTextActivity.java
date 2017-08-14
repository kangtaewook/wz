package com.vinetech.wezone.Common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

import static com.vinetech.wezone.Wezone.WriteReviewActivity.REVIEW_TYPE;
import static com.vinetech.wezone.Wezone.WriteReviewActivity.REVIEW_TYPE_REGIST;

public class EditTextActivity extends BaseActivity {

    public static final String EDITTEXT_TITLE = "title";
    public static final String EDITTEXT_HINT = "hint";
    public static final String EDITTEXT_CONTENTS = "contents";

    public static void startActivity(BaseActivity activity, String title, String contents) {
        Intent intent = new Intent(activity, EditTextActivity.class);
        intent.putExtra(REVIEW_TYPE, REVIEW_TYPE_REGIST);
        intent.putExtra(EDITTEXT_TITLE, title);
        if (contents != null) {
            intent.putExtra(EDITTEXT_CONTENTS, contents);
        }

        activity.moveActivityForResult(intent, Define.INTENT_RESULT_EDIT_TEXT);

    }

    public static void startActivity(BaseActivity activity, String title, String hint, String contents) {
        Intent intent = new Intent(activity, EditTextActivity.class);
        intent.putExtra(REVIEW_TYPE, REVIEW_TYPE_REGIST);
        intent.putExtra(EDITTEXT_TITLE, title);
        intent.putExtra(EDITTEXT_HINT, hint);
        if (contents != null) {
            intent.putExtra(EDITTEXT_CONTENTS, contents);
        }
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_EDIT_TEXT);
    }


    private String mTitle;
    private String mHint;
    private String mContents;

    public EditText edittext_contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        mTitle = getIntent().getStringExtra(EDITTEXT_TITLE);
        mHint = getIntent().getStringExtra(EDITTEXT_HINT);
        mContents = getIntent().getStringExtra(EDITTEXT_CONTENTS);

        setHeaderView(R.drawable.btn_back_white, mTitle, R.drawable.btn_check);

        edittext_contents = (EditText) findViewById(R.id.edittext_contents);

        if (!WezoneUtil.isEmptyStr(mHint)) {
            edittext_contents.setHint(mHint);
        }

        if (!WezoneUtil.isEmptyStr(mContents)) {
            edittext_contents.setText(mContents);
        }

    }

    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);

        Intent i = new Intent();
        i.putExtra(EDITTEXT_CONTENTS, edittext_contents.getText().toString().trim());
        setResult(RESULT_OK, i);
        finish();
    }
}
