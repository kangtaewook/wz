package com.vinetech.wezone.Profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.R;

public class MyProfileActivity extends BaseActivity {

    private RelativeLayout relativelayout_bg;
    private ImageView imageview_bg;
    private ImageView imageview_profile;

    private TextView textview_name;
    private TextView textview_loaction;
    private TextView textview_status_msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        setHeaderView(R.drawable.btn_back_white, null, R.drawable.btn_more_white);

        relativelayout_bg = (RelativeLayout) findViewById(R.id.relativelayout_bg);
        relativelayout_bg.setBackgroundColor(getResources().getColor(getCurrentThemeColorId()));

        imageview_bg = (ImageView)findViewById(R.id.imageview_bg);
        imageview_profile = (ImageView)findViewById(R.id.imageview_profile);

        textview_name = (TextView)findViewById(R.id.textview_name);
        textview_loaction = (TextView)findViewById(R.id.textview_loaction);
        textview_status_msg = (TextView)findViewById(R.id.textview_status_msg);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setTransparentHeaderView();

        if(WezoneUtil.isEmptyStr(getShare().getMyInfo().bg_img_url) == false){
            imageview_bg.setVisibility(View.VISIBLE);
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
        textview_loaction.setText(getShare().getMyInfo().address);

        String status_msg = getShare().getMyInfo().status_message;
        if(WezoneUtil.isEmptyStr(status_msg)){
            status_msg = "상태메세지를 입력해주세요.";
        }
        textview_status_msg.setText(status_msg);
    }

    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);
        EditProfileActivity.startActivity(MyProfileActivity.this);
    }

}