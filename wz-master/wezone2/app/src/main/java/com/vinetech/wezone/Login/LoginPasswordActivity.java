package com.vinetech.wezone.Login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.AccountPicker;
import com.vinetech.util.WezoneUtil;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Google;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Login.google.GetNameInForeground;
import com.vinetech.wezone.Login.google.GoogleNetListner;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_PostLogin;
import com.vinetech.wezone.SendPacket.Send_CheckCode;
import com.vinetech.wezone.SendPacket.Send_PostEmail;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vinetech.wezone.Login.LoginActivity.IS_EMAIL_CONFIRM;
import static com.vinetech.wezone.Login.LoginActivity.SEND_MAIL;

public class LoginPasswordActivity extends BaseActivity  {

    private static String KEY_MAIL = "mail";

    public static void startActivity(BaseActivity activity, String mail) {
        Intent intent = new Intent(activity, LoginPasswordActivity.class);
        intent.putExtra(KEY_MAIL, mail);
        activity.moveActivityWithFadeAni(intent);
    }

    public String mMail = null;
    public String mPass = null;

    public EditText edittext_userpass;
    public LinearLayout linearlayout_btn_forgotten_pass;
    public LinearLayout linearlayout_btn_login;

    private String mGoogleMail;
    private String mCode;

    private boolean isNewPassword;

    private CallbackManager callbackManager;
    //	private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private AccountManager mAccountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        FacebookSdk.sdkInitialize(getApplicationContext());
//        callbackManager = CallbackManager.Factory.create();
//        LoginManager.getInstance().logOut();

        setContentView(R.layout.activity_login_password);

        mAccountManager = AccountManager.get(this);

        String title = getResources().getString(R.string.login);
        setHeaderView(R.drawable.btn_back_white,title,0);

        mMail = getIntent().getStringExtra(KEY_MAIL);

        edittext_userpass = (EditText) findViewById(R.id.edittext_userpass);
        edittext_userpass.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[0-9a-zA-Z]+")) { // here no space character
                            return cs;
                        }
                        return "";
                    }
                }
        });

        linearlayout_btn_forgotten_pass = (LinearLayout) findViewById(R.id.linearlayout_btn_forgotten_pass);
        linearlayout_btn_forgotten_pass.setOnClickListener(mLoginInputPassListener);

        linearlayout_btn_login = (LinearLayout) findViewById(R.id.linearlayout_btn_login);
        linearlayout_btn_login.setOnClickListener(mLoginInputPassListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        profileTracker.stopTracking();

    }

    private View.OnClickListener mLoginInputPassListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.linearlayout_btn_forgotten_pass:
                    LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_SEND_EMAIL, mDialogListener, mMail);
                    dialog.show(getFragmentManager(), "loginFragment");
                    break;

                case R.id.linearlayout_btn_login:

                    mPass = edittext_userpass.getText().toString().trim();

                    if (mPass.equals("") || mPass == null) {
                        String str = getResources().getString(R.string.please_enter_your_password);
                        Toast.makeText(LoginPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
                        return;
                    }

//                    if (mPass.length() < 8) {
//                        String str = getResources().getString(R.string.password_limit_error);
//                        Toast.makeText(LoginPasswordActivity.this, str, Toast.LENGTH_SHORT).show();
//                        return;
//                    }

                    sendLogin(mMail, mPass);
                    break;
            }

        }
    };

    @Override
    public void onBackPressed() {
        fadeoutFinish();
    }

    public void sendLogin(String id, String pw) {

        showProgressPopup();

        getShare().getLoginParam().provider_type = "W";
        getShare().getLoginParam().login_id = id;
        getShare().getLoginParam().login_passwd = pw;

        String push_token = getShare().getLoginParam().push_token;
        if (WezoneUtil.isEmptyStr(push_token)) {
            getShare().getLoginParam().push_token = CryptPreferences.getCryptString(m_Context, Define.SHARE_KEY_PUSH_TOKEN, "");
        }

        Call<Rev_PostLogin> sendData = wezoneRestful.postLogin(getShare().getLoginParam());

        sendData.enqueue(new Callback<Rev_PostLogin>() {
            @Override
            public void onResponse(Call<Rev_PostLogin> call, Response<Rev_PostLogin> response) {
                Rev_PostLogin revData = response.body();

                if (isNetSuccess(revData)) {
                    hidePorgressPopup();

                    moveMainActivity(revData,null);

                }else{
                    hidePorgressPopup();
                    checkLoginError(revData);
                }
            }

            @Override
            public void onFailure(Call<Rev_PostLogin> call, Throwable t) {
                hidePorgressPopup();
            }
        });

    }


    public void checkLoginError(Rev_Base revData) {
        String loginErrorCode = revData.code;
        if (Rev_Base.LOGIN_ERROR_ID.equals(loginErrorCode)) {
            //패스워드 틀림 팝업.
            LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_ERROR, mDialogListener);
            dialog.show(getFragmentManager(), "loginFragment");

        } else if (Rev_Base.LOGIN_ERROR_PW.equals(loginErrorCode)) {
            //패스워드 틀림 팝업
            LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_ERROR, mDialogListener);
            dialog.show(getFragmentManager(), "loginFragment");

        } else if (Rev_Base.LOGIN_ERROR_WEZONE.equals(loginErrorCode)) {
            LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_ERROR, mDialogListener);
            dialog.show(getFragmentManager(), "loginFragment");
        } else if (Rev_Base.LOGIN_ERROR_EMAIL.equals(loginErrorCode)) {
            String str = getResources().getString(R.string.the_unregistered_email);
            Toast.makeText(m_Context, str, Toast.LENGTH_LONG).show();
            return;
        } else if (Rev_Base.LOGIN_ERROR_CODE.equals(loginErrorCode)) {
            String str = getResources().getString(R.string.the_verification_number_is_incorrect);
            Toast.makeText(m_Context, str, Toast.LENGTH_LONG).show();

            LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_EMAIL_CONFIRM, mDialogListener, mMail);
            dialog.show(getFragmentManager(), "loginFragment");

            return;
        } else if (Rev_Base.LOGIN_ERROR_PW_FAIL.equals(loginErrorCode)) {
            String str = getResources().getString(R.string.the_password_change_failed);
            Toast.makeText(m_Context, str, Toast.LENGTH_LONG).show();
            return;
        }
    }

    LoginFragmentDialog.LoginFragmentDialogLinstener mDialogListener = new LoginFragmentDialog.LoginFragmentDialogLinstener() {
        @Override
        public void onClickGoogle() {

        }

        @Override
        public void onClickFaceBook() {
            AccessToken token = AccessToken.getCurrentAccessToken();
            LoginManager.getInstance().logInWithReadPermissions(LoginPasswordActivity.this, Arrays.asList("public_profile"));
        }

        @Override
        public void onClickLogin(String id, String pw) {
            sendLogin(id, pw);
        }

        @Override
        public void onClickLost(String email) {
            LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_SEND_EMAIL, this, mMail);
            dialog.show(getFragmentManager(), "loginFragment");
        }

        @Override
        public void onClickReTry() {
//            LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_LOGIN, this, mMail);
//            dialog.show(getFragmentManager(), "loginFragment");
        }

        @Override
        public void onClickSendMail(String email) {

            showProgressPopup();
            Send_PostEmail param = new Send_PostEmail();
            param.email = email;
            Call<Rev_Base> sendEmail = wezoneRestful.postEmail(param);
            sendEmail.enqueue(new Callback<Rev_Base>() {
                @Override
                public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                    Rev_Base revBase = response.body();

                    if(isNetSuccess(revBase)){
                        LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_EMAIL_CONFIRM, mDialogListener, mMail);
                        dialog.show(getFragmentManager(), "loginFragment");

                        CryptPreferences.putBoolean(m_Context, IS_EMAIL_CONFIRM, true);
                        CryptPreferences.putString(m_Context, SEND_MAIL, mMail);
                    }

                    hidePorgressPopup();
                }

                @Override
                public void onFailure(Call<Rev_Base> call, Throwable t) {
                    hidePorgressPopup();
                }
            });

//            getShare().getNetworkManager().sendPacket(LoginPasswordActivity.this, new Send_Findpasswd(email), LoginPasswordActivity.this);
        }

        @Override
        public void onClickSendCode(String code) {

            showProgressPopup();
            mCode = code;

            Send_CheckCode checkCode = new Send_CheckCode();
            checkCode.email = mMail;
            checkCode.auth_code = code;
            Call<Rev_Base> sendEmail = wezoneRestful.checkCode(checkCode);
            sendEmail.enqueue(new Callback<Rev_Base>() {
                @Override
                public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {

                    Rev_Base revBase = response.body();
                    if(isNetSuccess(revBase)){
                        LoginFragmentDialog dialog = new LoginFragmentDialog().newInstance(LoginFragmentDialog.DAILOG_TYPE_FIND_PASSWD, mDialogListener, mMail);
                        dialog.show(getFragmentManager(), "loginFragment");
                        CryptPreferences.putBoolean(m_Context, IS_EMAIL_CONFIRM, false);
                    }
                    hidePorgressPopup();
                }

                @Override
                public void onFailure(Call<Rev_Base> call, Throwable t) {
                    hidePorgressPopup();
                }
            });


        }

        @Override
        public void onClickPassChange(String newpass) {

            showProgressPopup();
            Send_CheckCode checkCode = new Send_CheckCode();
            checkCode.email = mMail;
            checkCode.auth_code = mCode;
            checkCode.new_password = newpass;
            Call<Rev_Base> sendEmail = wezoneRestful.checkCode(checkCode);
            sendEmail.enqueue(new Callback<Rev_Base>() {
                @Override
                public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                    hidePorgressPopup();
                }

                @Override
                public void onFailure(Call<Rev_Base> call, Throwable t) {
                    hidePorgressPopup();
                }
            });
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // uiHelper.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);


        super.onActivityResult(requestCode, resultCode, data);
    }


}
