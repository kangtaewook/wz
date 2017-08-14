package com.vinetech.wezone.Common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.util.LibValidCheck;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;

public class Activity_Email_Push extends BaseActivity {


    public static final String EMAIL_DATA = "EMAIL_DATA";
    public static final String EMAIL_PUSH_CONTENTS = "EMAIL_PUSH_CONTENTS";
    public static final String EDITTEXT_CONTENTS = "contents";

    EditText edittext_email_push_content;

    private EditText edittext_email_push_mail;

    private TextView textview_email_push_data;

    private String push_contents;
    private String email;
    private String contents;


    public static void startActivity(BaseActivity activity, String email_data, String contents) {

        Intent intent = new Intent(activity, Activity_Email_Push.class);

        if (contents != null && email_data != null) {
            intent.putExtra(EMAIL_PUSH_CONTENTS, contents);
            intent.putExtra(EMAIL_DATA, email_data);
        }

        activity.moveActivityForResult(intent, Define.INTENT_RESULT_EMAIL_PUSH);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_push);

        setHeaderView(R.drawable.btn_back_white, "이메일", R.drawable.btn_check);

        push_contents = getIntent().getStringExtra(EMAIL_PUSH_CONTENTS);
        email = getIntent().getStringExtra(EMAIL_DATA);


        edittext_email_push_mail = (EditText) findViewById(R.id.edittext_email_push_mail);
        edittext_email_push_content = (EditText) findViewById(R.id.edittext_email_push_content);


        if(WezoneUtil.isEmptyStr(push_contents) == false && WezoneUtil.isEmptyStr(email) == false){
            edittext_email_push_mail.setText(email);
            edittext_email_push_content.setText(push_contents);
        }


    }




    @Override
    public void onClickRightBtn(View v) {
        String email = edittext_email_push_mail.getText().toString().trim();
        String contents = edittext_email_push_content.getText().toString().trim();
        if (WezoneUtil.isEmptyStr(email)) {
            Toast.makeText(m_Context, "정확한 메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!LibValidCheck.isValidEmail(email)) {
            Toast.makeText(m_Context, "이메일 형식이 이상합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(contents == null || contents.length() == 0 ){
            Toast.makeText(m_Context, "메세지를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }else {
            Intent i = new Intent();
            i.putExtra(EMAIL_DATA, email);
            i.putExtra(EMAIL_PUSH_CONTENTS, contents);
            setResult(RESULT_OK, i);
            super.onClickRightBtn(v);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

        }
    }
}