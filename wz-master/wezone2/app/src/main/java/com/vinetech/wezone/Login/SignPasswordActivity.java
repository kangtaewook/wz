package com.vinetech.wezone.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.R;


public class SignPasswordActivity extends BaseActivity {

    private static String KEY_MAIL = "mail";

    public static void startActivity(BaseActivity activity, String mail)
    {
        Intent intent = new Intent(activity, SignPasswordActivity.class);
        intent.putExtra(KEY_MAIL, mail);
        activity.moveActivityWithFadeAni(intent);
    }

    public String mMail = null;
    public String mPass = null;

    public TextView textview_notice;

    public EditText edittext_userpass;

    public EditText write_setting_new_password_ok;

    public LinearLayout linearlayout_btn_next;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_password);

        String title = getResources().getString(R.string.sing_up_title);
        setHeaderView(R.drawable.btn_back_white,title,0);

        mMail = getIntent().getStringExtra(KEY_MAIL);

        textview_notice = (TextView) findViewById(R.id.textview_notice);

        String strTemp = getResources().getString(R.string.login_pass_notice);
        textview_notice.setText(String.format(strTemp,mMail));

        edittext_userpass = (EditText) findViewById(R.id.edittext_userpass);

        edittext_userpass.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[0-9a-zA-Z]+")){ // here no space character
                            return cs;
                        }
                        return "";
                    }
                }
        });

        write_setting_new_password_ok = (EditText) findViewById(R.id.write_setting_new_password_ok);
        write_setting_new_password_ok.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[0-9a-zA-Z]+")){ // here no space character
                            return cs;
                        }
                        return "";
                    }
                }
        });

        linearlayout_btn_next = (LinearLayout) findViewById(R.id.linearlayout_btn_next);
        linearlayout_btn_next.setOnClickListener(mLoginInputPassListener);

    }

    private View.OnClickListener mLoginInputPassListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.linearlayout_btn_next:

                    mPass = edittext_userpass.getText().toString().trim();

                    if (mPass.equals("") || mPass == null) {
                        String str = getResources().getString(R.string.please_enter_your_password);
                        Toast.makeText(SignPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (mPass.length() < 8) {
                        String str = getResources().getString(R.string.password_limit_error);
                        Toast.makeText(SignPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String confirmPw = write_setting_new_password_ok.getText().toString().trim();

                    if (confirmPw.equals("") || confirmPw == null) {
                        String str = getResources().getString(R.string.please_enter_your_password);
                        Toast.makeText(SignPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (confirmPw.length() < 8) {
                        String str = getResources().getString(R.string.password_limit_error);
                        Toast.makeText(SignPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!confirmPw.equals(mPass)){
                        Toast.makeText(SignPasswordActivity.this, "비밀번호가 상이합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SignNameActivity.startActivity(SignPasswordActivity.this,mMail,mPass);

                    break;
            }

        }
    };

    @Override
    public void onBackPressed() {
        fadeoutFinish();
    }

}
