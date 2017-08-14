package com.vinetech.wezone.Login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.util.LibValidCheck;
import com.vinetech.util.UIViewAnimation;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.R;

import static com.vinetech.wezone.Login.LoginActivity.IS_EMAIL_CONFIRM;


public class LoginFragmentDialog extends DialogFragment implements OnClickListener {

    public static String DIALOG_TYPE = "dialog_type";
    public static String DIALOG_EMAIL = "dialog_email";

    public interface LoginFragmentDialogLinstener {

        void onClickGoogle();

        void onClickFaceBook();

        void onClickLogin(String id, String pw);

        void onClickLost(String email);

        void onClickReTry();

        void onClickSendMail(String mail);

        void onClickSendCode(String code);

        void onClickPassChange(String pw);
    }

    public static int DAILOG_TYPE_LOGIN = 0;
    public static int DAILOG_TYPE_ERROR = 1;
    public static int DAILOG_TYPE_SEND_EMAIL = 2;
    public static int DAILOG_TYPE_EMAIL_CONFIRM = 3;
    public static int DAILOG_TYPE_FIND_PASSWD = 4;

    private LoginFragmentDialogLinstener mListener;

    public LoginFragmentDialogLinstener getListener() {
        return mListener;
    }

    public void setListener(LoginFragmentDialogLinstener mListener) {
        this.mListener = mListener;
    }

    public static LoginFragmentDialog newInstance(int type, LoginFragmentDialogLinstener listener) {
        LoginFragmentDialog f = new LoginFragmentDialog();
        f.setListener(listener);
        Bundle args = new Bundle();
        args.putInt(DIALOG_TYPE, type);
        f.setArguments(args);
        return f;
    }

    public static LoginFragmentDialog newInstance(int type, LoginFragmentDialogLinstener listener, String email) {
        LoginFragmentDialog f = new LoginFragmentDialog();
        f.setListener(listener);
        Bundle args = new Bundle();
        args.putInt(LoginFragmentDialog.DIALOG_TYPE, type);
        args.putSerializable(DIALOG_EMAIL, email);
        f.setArguments(args);
        return f;
    }

    private int mDialogType;

    private String mEmail;

    private Dialog mDialog;

    private EditText mEdittext_userid;
    private EditText mEdittext_userpass;

    private TextView mTextview_noti;

    private LinearLayout linearLayout_btn_login;

    private LinearLayout mLinearlayout_btn_google;
    private LinearLayout mLinearlayout_btn_facebook;

    //
    private TextView mTextview_desc;
    private LinearLayout mLinearlayout_btn_01;
    private LinearLayout mLinearlayout_btn_02;


    private EditText textview_email;
    private LinearLayout linearlayout_btn_send;
    private LinearLayout linearlayout_btn_cancel;

    private LinearLayout linearlayout_btn_edit;

    private EditText mEdittext_code;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        mDialogType = getArguments().getInt(LoginFragmentDialog.DIALOG_TYPE);

        mDialog = new Dialog(getActivity());
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        if (mDialogType == DAILOG_TYPE_LOGIN) {

            mDialog.setContentView(R.layout.dialog_login);

            mEmail = (String) getArguments().getSerializable(DIALOG_EMAIL);

            mEdittext_userid = (EditText) mDialog.findViewById(R.id.edittext_userid);
            mEdittext_userpass = (EditText) mDialog.findViewById(R.id.edittext_userpass);

//			mEdittext_userid.setText("user200@user.com");
//			mEdittext_userpass.setText("123qwe");

            if (mEmail == null) {
//                String mail = getGoogleId();
//                if (mail != null) {
//                    mEmail = mail;
//                    mEdittext_userid.setText(mail);
//                }
            } else {
                mEdittext_userid.setText(mEmail);
            }

            mTextview_noti = (TextView) mDialog.findViewById(R.id.textview_noti);
            mTextview_noti.setOnClickListener(this);

            linearLayout_btn_login = (LinearLayout) mDialog.findViewById(R.id.linearLayout_btn_login);
            linearLayout_btn_login.setOnClickListener(this);

            mLinearlayout_btn_google = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_google);
            mLinearlayout_btn_google.setOnClickListener(this);
            mLinearlayout_btn_facebook = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_facebook);
            mLinearlayout_btn_facebook.setOnClickListener(this);

        } else if (mDialogType == DAILOG_TYPE_ERROR) {

            mDialog.setContentView(R.layout.dialog_error);

            mTextview_desc = (TextView) mDialog.findViewById(R.id.textview_desc);
            mLinearlayout_btn_01 = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_01);
            mLinearlayout_btn_01.setOnClickListener(this);
            mLinearlayout_btn_02 = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_02);
            mLinearlayout_btn_02.setOnClickListener(this);

        } else if (mDialogType == DAILOG_TYPE_SEND_EMAIL) {

            mDialog.setContentView(R.layout.dialog_send_email);
            textview_email = (EditText) mDialog.findViewById(R.id.textview_email);
            textview_email.setClickable(false);
            textview_email.setEnabled(false);
            textview_email.setFocusable(false);
            textview_email.setFocusableInTouchMode(false);

            mEmail = (String) getArguments().getSerializable(DIALOG_EMAIL);

            textview_email.setText(mEmail);

            linearlayout_btn_send = (LinearLayout)  mDialog.findViewById(R.id.linearlayout_btn_send);
            linearlayout_btn_send.setOnClickListener(this);

        } else if (mDialogType == DAILOG_TYPE_EMAIL_CONFIRM) {

            mDialog.setContentView(R.layout.dialog_email_confirm);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);

            setCancelable(false);

            mEmail = (String) getArguments().getSerializable(DIALOG_EMAIL);

            mTextview_desc = (TextView) mDialog.findViewById(R.id.textview_desc);

            mEdittext_code = (EditText) mDialog.findViewById(R.id.edittext_code);

            String tempStr = String.format(getResources().getString(R.string.we_have_sent_a_push_message), mEmail);
            mTextview_desc.setText(tempStr);

            mTextview_noti = (TextView) mDialog.findViewById(R.id.textview_noti);
            mTextview_noti.setOnClickListener(this);

            linearlayout_btn_send = (LinearLayout)  mDialog.findViewById(R.id.linearlayout_btn_send);
            linearlayout_btn_send.setOnClickListener(this);

            linearlayout_btn_cancel = (LinearLayout)  mDialog.findViewById(R.id.linearlayout_btn_cancel);
            linearlayout_btn_cancel.setOnClickListener(this);

//            mBtn_send = (Button) mDialog.findViewById(R.id.btn_send);
//            mBtn_send.setOnClickListener(this);

//			mLinearlayout_btn_01 = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_01);
//			mLinearlayout_btn_01.setOnClickListener(this);
//			mLinearlayout_btn_02 = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_02);
//			mLinearlayout_btn_02.setOnClickListener(this);


        } else if (mDialogType == DAILOG_TYPE_FIND_PASSWD) {

            mDialog.setContentView(R.layout.dialog_find_passwd);

            mEmail = (String) getArguments().getSerializable(DIALOG_EMAIL);

            mTextview_desc = (TextView) mDialog.findViewById(R.id.textview_desc);
            String tempStr = String.format(getResources().getString(R.string.set_new_password_of), mEmail);
            mTextview_desc.setText(tempStr);

            mEdittext_userpass = (EditText) mDialog.findViewById(R.id.edittext_userpass);

            linearlayout_btn_edit = (LinearLayout)  mDialog.findViewById(R.id.linearlayout_btn_edit);
            linearlayout_btn_edit.setOnClickListener(this);

        }

        LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        mDialog.getWindow().setAttributes(params);

        return mDialog;
    }

    public String getGoogleId() {

        String str = null;

        String[] accountTypes = new String[]{"com.google"};

        for (Account account : AccountManager.get(getActivity()).getAccounts()) {
            if (accountTypes[0].equals(account.type)) {
                str = account.name;
            }
        }

        return str;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        int viewId = v.getId();

        switch (viewId) {

            case R.id.textview_noti: {
                if(mDialogType == DAILOG_TYPE_EMAIL_CONFIRM){
                    CryptPreferences.putBoolean(getActivity(), IS_EMAIL_CONFIRM, false);
                }else{
                    mListener.onClickLost(mEmail);
                }
                mDialog.dismiss();
            }
            break;

            case R.id.linearLayout_btn_login: {

                String id = mEdittext_userid.getText().toString().trim();

                String pw = mEdittext_userpass.getText().toString().trim();

                if (id.equals("") || id == null) {
                    String str = getResources().getString(R.string.please_enter_id);
//                    Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                    showToastWithShake(str);
                    return;
                }

                if (!LibValidCheck.isValidEmail(id)) {
                    String str = getResources().getString(R.string.not_the_email_type);
                    showToastWithShake(str);
//                    Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pw.equals("") || pw == null) {
                    String str = getResources().getString(R.string.please_enter_your_password);
                    showToastWithShake(str);
//                    Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                    return;
                }

                mListener.onClickLogin(id, pw);

                mDialog.dismiss();
            }

            break;

            case R.id.linearlayout_btn_google: {
                mListener.onClickGoogle();
                mDialog.dismiss();
            }
            break;

            case R.id.linearlayout_btn_facebook: {
                mListener.onClickFaceBook();
                mDialog.dismiss();
            }
            break;

            case R.id.linearlayout_btn_01: {
                if (mDialogType == DAILOG_TYPE_ERROR) {
                    mListener.onClickLost(mEmail);
                    mDialog.dismiss();
                } else {
                    mListener.onClickReTry();
                    mDialog.dismiss();
                }
            }
            break;

            case R.id.linearlayout_btn_02:{
                mListener.onClickReTry();
                mDialog.dismiss();
            }
            break;

            case R.id.linearlayout_btn_send: {

                if (mDialogType == DAILOG_TYPE_SEND_EMAIL) {
                    String email = textview_email.getText().toString().trim();

                    if (!LibValidCheck.isValidEmail(email)) {
                        String str = getResources().getString(R.string.not_the_email_type);
//                        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                        showToastWithShake(str);
                        return;
                    }

                    mListener.onClickSendMail(email);
                } else {

                    String code = mEdittext_code.getText().toString().trim();

                    if (code.equals("") || code == null) {
                        String str = getResources().getString(R.string.please_enter_your_code);
//                        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                        showToastWithShake(str);
                        return;
                    }

                    mListener.onClickSendCode(code);
                }

                mDialog.dismiss();
            }
            break;

            case R.id.linearlayout_btn_edit: {

                String pw = mEdittext_userpass.getText().toString().trim();

                if (pw.equals("") || pw == null) {
                    String str = getResources().getString(R.string.please_enter_your_password);
//                    Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                    showToastWithShake(str);
                    return;
                }

                mListener.onClickPassChange(pw);
                mDialog.dismiss();

            }
            break;

            case R.id.linearlayout_btn_cancel: {

//                mListener.onClickPassChange(pw);

                mDialog.dismiss();

            }
            break;

        }
    }

    public void showToastWithShake(String str){
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
        UIViewAnimation.ShakeAnimation(getActivity(), mDialog.findViewById(android.R.id.content));
    }
}
