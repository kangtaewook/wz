package com.vinetech.wezone.Wezone;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.vinetech.ui.SlidingTabLayout;
import com.vinetech.util.LibCall;
import com.vinetech.util.LibDateUtil;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Common.EditTextActivity;
import com.vinetech.wezone.Data.Data_Chat_UserList;
import com.vinetech.wezone.Data.Data_Comment;
import com.vinetech.wezone.Data.Data_MsgKey;
import com.vinetech.wezone.Data.Data_OtherUser;
import com.vinetech.wezone.Data.Data_UnRead;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Message.ChattingActivity;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_PostComment;
import com.vinetech.wezone.RevPacket.Rev_Wezoninfo;
import com.vinetech.wezone.SendPacket.Send_PostComment;
import com.vinetech.wezone.SendPacket.Send_PutDataWithValue;
import com.vinetech.wezone.SendPacket.Send_PutMyWezone;
import com.vinetech.wezone.SendPacket.Send_PutWezone;
import com.vinetech.wezone.SendPacket.Send_PutWezoneDelete;
import com.vinetech.xmpp.LibXmppManager;

import org.jivesoftware.smack.XMPPException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vinetech.wezone.R.color.notification_text_desc_selected_color;
import static com.vinetech.wezone.Wezone.WezoneManagerActivity.WEZONE_EDIT;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 위존 화면
 *
 * 홈(WezoneHomeFragment), 게시판(WezoneBoardFragment), 멤버(WezoneMemberFragment) 관리
 *
 */

public class WezoneActivity extends BaseActivity implements WezoneMapFragment.OnFragmentSetListener, WezoneBoardFragment.BoardCountListener {

    public static final String ENTRY_WAIT = "ENTRY_WAIT";
    public static final String ENTRY = "ENTRY";
    public static final String BOARD_TOTAL_COUNT = "board_total_count";
    public static final String MEMBER_TOTAL_COUNT = "member_total_count";
    public static final String WEZONE_WEZONE_PUT = "wezone_put";
    public static final String WEZONE_WEZONE = "wezone";
    public static final String WEZONE_WEZONE_id = "wezone_id";
    public static final String DELETE = "DELETE";
    public static final String REMOVE = "REMOVE";
    public static final String NOTICE = "NOTICE";
    public static final String WEZONE_WEZONE_PUT_TYPE = "WEZONE_WEZONE_PUT_TYPE";
    public static final String PUT = "PUT";
    public static final String PROFILE = "PROFILE";
    public static final String WEZONE_MAP_FRAGMENT_CLICK = "WEZONE_MAP_FRAGMENT_CLICK";
    public static final String FRIEND_PULS = "FRIEND_PULS";

    public static final int SCROLLY = 1806;
    public static final double ALPHA_TRANSPARENT = 1.0;
    public static final double ALPHA_MIDDLE = 0.6;

    public static final int WEZONE_ENTRY = 1;    //입장하기
    public static final int WEZONE_EDIT_DELETE = 2;  // 수정,삭제
    public static final int WEZONE_MAP_UPDATE = 3; // 지도 업데이트
    public static final int WEZONE_LEAVE = 4; // 탈퇴

    public static void startActivit(BaseActivity activity, Data_WeZone wezon, String profile) {
        Intent intent = new Intent(activity, WezoneActivity.class);
        intent.putExtra(WEZONE_WEZONE, wezon);
        intent.putExtra(PROFILE, profile);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_WEZONE);
    }

    Dialog mDialog;
    private GpsInfo mGpsInfo;
    private Data_WeZone mWezone;
    private Data_WeZone mWezoneadress;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;

    private ImageView imageview_profile;
    private ImageView imageview_bg;
    private TextView textview_title;
    private TextView textview_desc;
    private TextView textview_distance;
    private ImageView editimageview_wezone;
    private TextView textview_wezone_comment;
    private ImageView imageview_wezone_share;
    private LinearLayout linearlayout_manager_btn_area;
    private LinearLayout linearlayout_btn_chat;
    private LinearLayout linearlayout_btn_group_write;
    private LinearLayout linearlayout_btn_cancal;
    private LinearLayout linearlayout_btn_ok;
    private LinearLayout linearLayout_wezone_desc;
    private LinearLayout linearlayout_btn_entry;
    private ImageView ImageView_wezone_gps_check;
    private TextView textview_wezone_activity;

    private String mType;
    private String wezoneManageType;
    private String mWezone_id;
    private String mProfile;
    private String latitude;
    private String longitude;

    private int mFlexibleSpaceHeight;
    private int mTabHeight;
    private int mMember_type = 0;
    private int entry_ask = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wezone);

        mWezone = (Data_WeZone) getIntent().getSerializableExtra(WEZONE_WEZONE);
        mProfile = (String) getIntent().getSerializableExtra(PROFILE);
        mWezone_id = (String) getIntent().getSerializableExtra(WEZONE_WEZONE_id);

        if (WezoneUtil.isManager(mWezone.manage_type)) {
            if (mWezone == null) {
                setHeaderView(R.drawable.btn_back_white, null, 0);
            } else {
                setHeaderView(R.drawable.btn_back_white, mWezone.title, 0);
            }
        } else {
            if (mWezone == null) {
                setHeaderView(R.drawable.btn_back_white, null, 0);
            } else {
                setHeaderView(R.drawable.btn_back_white, mWezone.title, 0);
            }
        }

        main_toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(android.R.color.black)));
        editimageview_wezone = (ImageView) findViewById(R.id.editimageview_wezone);
        mPager = (ViewPager) findViewById(R.id.pager);

        String[] titles = new String[]{"상세정보", "게시글" + mWezone.board_count, "멤버" + mWezone.member_count};
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager(), titles);
        mPagerAdapter.setWezoneId(mWezone);
        mPager.setOffscreenPageLimit(3);

        setPagerView();

        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_wezone_header);
        mTabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);

        TextView titleView = (TextView) findViewById(R.id.title);

        textview_wezone_activity = (TextView) findViewById(R.id.textview_wezone_activity);
        textview_wezone_comment = (TextView) findViewById(R.id.textview_wezone_comment);
        linearLayout_wezone_desc = (LinearLayout) findViewById(R.id.linearLayout_wezone_desc);
        ImageView_wezone_gps_check = (ImageView) findViewById(R.id.ImageView_wezone_gps_check);
        imageview_wezone_share = (ImageView) findViewById(R.id.imageview_wezone_share);
        imageview_wezone_share.setOnClickListener(mClickListener);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        mSlidingTabLayout.setSelectedIndicatorColors(Color.parseColor("#689df9"));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mPager);

        // Initialize the first Fragment's state when layout is completed.
        ScrollUtils.addOnGlobalLayoutListener(mSlidingTabLayout, new Runnable() {
            @Override
            public void run() {
                translateTab(0, false);
            }
        });

        imageview_profile = (ImageView) findViewById(R.id.imageview_profile);
        if (WezoneUtil.isEmptyStr(mWezone.img_url) == false) {
            showImageFromRemote(mWezone.img_url, R.drawable.ic_bunny_image, imageview_profile);
        } else {
            imageview_profile.setImageResource(R.drawable.ic_bunny_image);
        }

        imageview_bg = (ImageView) findViewById(R.id.imageview_bg);
        if (WezoneUtil.isEmptyStr(mWezone.bg_img_url) == false) {
            showImageFromRemote(mWezone.bg_img_url, R.drawable.ic_bunny_image, imageview_bg);
        } else {
            imageview_bg.setVisibility(View.GONE);
        }

        imageview_bg = (ImageView) findViewById(R.id.imageview_bg);
        if (WezoneUtil.isEmptyStr(mWezone.bg_img_url) == false) {
            showImageFromRemote(mWezone.bg_img_url, 0, imageview_bg);
        } else {
            imageview_bg.setVisibility(View.GONE);
        }

        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_title.setText(mWezone.title);
        hashtag_desc();
        textview_distance = (TextView) findViewById(R.id.textview_distance);

        //나와의 거리 표시
        if (WezoneUtil.isEmptyStr(mWezone.distance) == false) {
            if (mWezone.distance.contains(".")) {
                int i = mWezone.distance.indexOf(".");
                String mDistance = mWezone.distance.substring(0, i);
                if (Integer.valueOf(mDistance) != 0) {
                    DecimalFormat form = new DecimalFormat("#.#");
                    double distance = Double.parseDouble(mWezone.distance);
                    textview_distance.setText(form.format(distance) + "km");
                    mWezone.distance = String.valueOf(form.format(distance));
                } else if (Double.valueOf(mWezone.distance) > 0.0) {
                    if (Integer.valueOf(mDistance) == 0) {
                        int meter = (int) (Double.valueOf(mWezone.distance) * 1000);
                        textview_distance.setText(+meter + "m");
                        mWezone.distance = String.valueOf(meter);
                    }
                } else if (mWezone.distance.equals("0")) {
                    textview_distance.setText(+0 + "m");
                    mWezone.distance = String.valueOf(0);
                }
            } else if (Integer.valueOf(mWezone.distance) > 0) {
                textview_distance.setText(mWezone.distance + "m");
                mWezone.distance = String.valueOf(mWezone.distance);
            } else {
                if (Integer.valueOf(mWezone.distance) > 0) {
                    //ex) 37km 가 들어오면 소수가 아닐 때
                    textview_distance.setText(mWezone.distance + "km");
                } else {
                    textview_distance.setText(0 + "m");
                    mWezone.distance = String.valueOf(0);
                }
            }
        }

        linearlayout_manager_btn_area = (LinearLayout) findViewById(R.id.linearlayout_manager_btn_area);
        linearlayout_btn_chat = (LinearLayout) findViewById(R.id.linearlayout_btn_chat);
        linearlayout_btn_chat.setOnClickListener(mClickListener);
        linearlayout_btn_group_write = (LinearLayout) findViewById(R.id.linearlayout_btn_group_write);
        linearlayout_btn_group_write.setOnClickListener(mClickListener);
        linearlayout_btn_entry = (LinearLayout) findViewById(R.id.linearlayout_btn_entry);
        linearlayout_btn_entry.setOnClickListener(mClickListener);

        refreshLayout();
    }

    public void hashtag_desc() {
        String str1 = mWezone.introduction;

        textview_desc = (TextView) findViewById(R.id.textview_desc);

        SpannableString hashText = new SpannableString(str1);
        Matcher matcher = Pattern.compile("\\#([0-9a-zA-Z가-힣]*)").matcher(hashText);

        while (matcher.find()) {
            hashText.setSpan(new ForegroundColorSpan(Color.parseColor("#000763")), matcher.start(), matcher.end(), 0);
            final String tag = matcher.group(0);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    WezoneSearchActivity.startActivityWithhash(WezoneActivity.this, tag);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);

                }
            };
            hashText.setSpan(clickableSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textview_desc.setMovementMethod(LinkMovementMethod.getInstance());
        }
        textview_desc.setText(hashText);
    }

    public void setPagerView() {
        int currentItem = mPager.getCurrentItem();
        String[] titles = new String[]{"상세정보", "게시글" + mWezone.board_count, "멤버" + mWezone.member_count};
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager(), titles);
        mPagerAdapter.setWezoneId(mWezone);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(currentItem);
    }

    public void setLinearlayout_btn_entry() {
        mGpsInfo = new GpsInfo(WezoneActivity.this);
        if (Define.LOACTION_TYPE_B.equals(mWezone.location_type)) {
            mMember_type = WEZONE_LEAVE;
            textview_wezone_activity.setText("입장대기");
            textview_wezone_comment.setText("'" + mWezone.title + "'의 WeCON근처에서 입장이 가능합니다.");
            textview_wezone_comment.setTextColor(getResources().getColor(notification_text_desc_selected_color));

            if (Define.TYPE_INVITED.equals(mWezone.manage_type)) {
                //입장대기 중복 클릭 방지
                linearlayout_btn_entry.setOnClickListener(mNotClickListener);
                editimageview_wezone.setOnClickListener(mClickListener);
            } else {
                wezoneManageType = "type_w";
                editimageview_wezone.setOnClickListener(mClickListener);
                entry_ask = WEZONE_ENTRY;
            }
        } else if (Define.LOACTION_TYPE_G.equals(mWezone.location_type)) {
            if (Define.ZONE_POSSIBLE_T.equals(mWezone.zone_possible)) {
                //mWezone.manage_type = Define.TYPE_NORMAL;//입장하기 일반멤버
                textview_wezone_activity.setText("입장하기");

                editimageview_wezone.setOnClickListener(mClickListener);
            } else {
                if (mWezone.zone_possible == null) {
                    //입장대기 했던 위치인식방법 B 비콘을 등록한 위존이 G GPS로 변경한 위존일 때 입장불가 일 때. 앱을 재 접속하면 된다.
                    Toast.makeText(m_Context, " 위존 앱을 재 접속 주시길 바랍니다. ", Toast.LENGTH_SHORT).show();
                }
                textview_wezone_activity.setText("입장불가");
                textview_wezone_activity.setTextColor(getResources().getColor(R.color.wezone_enry_btn));
                textview_wezone_comment.setText("'" + mWezone.title + "'의 " + (int) (Double.valueOf(mWezone.location_data) / 2) + " m 이내에서만 입장이 가능합니다.");
                textview_wezone_comment.setTextColor(getResources().getColor(notification_text_desc_selected_color));
                //상세정보, 게시글, 멤버 부분 높이 조절
                if (mGpsInfo.isGetLocation() == false) {
                    View headerView = findViewById(R.id.linearlayout_header);
                    mTabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
                    mPager = (ViewPager) findViewById(R.id.pager);
                    String[] titles = new String[]{"상세정보", "게시글" + mWezone.board_count, "멤버" + mWezone.member_count};
                    mPagerAdapter = new NavigationAdapter(getSupportFragmentManager(), titles);
                    mPagerAdapter.setWezoneId(mWezone);
                    mPager.setOffscreenPageLimit(3);
                    setPagerView();
                }
            }

        } else {
            if(Define.LOG_YN){
                Log.d(Define.LOG_TAG, " 입장한 Wezone 정보가 제대로 없습니다...");
            }
        }
    }

    public void refreshLayout() {
        if (WezoneUtil.isManager(mWezone.manage_type)) {
            linearlayout_manager_btn_area.setVisibility(View.VISIBLE);
//            linearlayout_btn_chat_member.setVisibility(View.GONE);
            linearlayout_btn_entry.setVisibility(View.GONE);
            imageview_wezone_share.setVisibility(View.VISIBLE);
            mMember_type = WEZONE_EDIT_DELETE;
            if (PROFILE.equals(mProfile)) {
                editimageview_wezone.setVisibility(View.GONE);
            } else {
                editimageview_wezone.setVisibility(View.VISIBLE);
                editimageview_wezone.setOnClickListener(mClickListener);
            }
        } else {
            if (WezoneUtil.isMember(mWezone.manage_type)) {
                linearlayout_manager_btn_area.setVisibility(View.VISIBLE);
                linearlayout_btn_entry.setVisibility(View.GONE);
                imageview_wezone_share.setVisibility(View.VISIBLE);
                mMember_type = WEZONE_LEAVE;
                if (PROFILE.equals(mProfile)) {
                    editimageview_wezone.setVisibility(View.GONE);
                } else {
                    editimageview_wezone.setVisibility(View.VISIBLE);
                    editimageview_wezone.setOnClickListener(mClickListener);
                }
            } else {
                linearlayout_manager_btn_area.setVisibility(View.GONE);
//                linearlayout_btn_chat_member.setVisibility(View.GONE);
                linearlayout_btn_entry.setVisibility(View.VISIBLE);

//location_type = G 이면 manage_type을 W를 없애고 다른걸로
                // location_type이 B -> G 변경될 때 경우고 반대로 G -> B 이면
                if (Define.TYPE_INVITED.equals(mWezone.manage_type)) {
                    //초대 맴버 일때
                    if (PROFILE.equals(mProfile)) {
                        //프로필에서 올 때
                        editimageview_wezone.setVisibility(View.GONE);
                    } else if ("G".equals(mWezone.location_type)) {
                        //입장대기했던 위치인식방법 비콘인 위존이 GPS로 변경했을 때
                        //대기멤버W -> 가입안한 위존으로 변경 NULL
                        mWezone.manage_type = null;
                        editimageview_wezone.setVisibility(View.GONE);
                    } else {
                        editimageview_wezone.setVisibility(View.VISIBLE);
                    }
                } else {
                    editimageview_wezone.setVisibility(View.GONE);
                }
                setLinearlayout_btn_entry();

            }
        }
    }

    public void reload() {
        if (WezoneUtil.isManager(mWezone.manage_type)) {

            if (mWezone == null) {
                setHeaderView(R.drawable.btn_back_white, null, R.drawable.btn_more_white);
            } else {
                setHeaderView(R.drawable.btn_back_white, mWezone.title, 0);
            }
        } else {
            setHeaderView(R.drawable.btn_back_white, mWezone.title, 0);
        }

        if (WezoneUtil.isEmptyStr(mWezone.img_url) == false) {
            showImageFromRemote(mWezone.img_url, R.drawable.ic_bunny_image, imageview_profile);
        } else {
            imageview_profile.setImageResource(R.drawable.ic_bunny_image);
        }

        if (WezoneUtil.isEmptyStr(mWezone.bg_img_url) == false) {
            showImageFromRemote(mWezone.bg_img_url, R.drawable.ic_bunny_image, imageview_bg);
            imageview_bg.setVisibility(View.VISIBLE);
        } else {
            imageview_bg.setVisibility(View.GONE);
        }

        if (WezoneUtil.isEmptyStr(mWezone.bg_img_url) == false) {
            showImageFromRemote(mWezone.bg_img_url, 0, imageview_bg);
            imageview_bg.setVisibility(View.VISIBLE);
        } else {
            imageview_bg.setVisibility(View.GONE);
        }

        textview_title.setText(mWezone.title);
        hashtag_desc();
        if (!WezoneUtil.isEmptyStr(mWezone.distance)) {
            double b = Math.round(Double.valueOf(mWezone.distance) * 100d) / 100d;
            textview_distance.setText(b + " m");
            mWezone.distance = String.valueOf(b);
        }
    }

    View.OnClickListener mNotClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //입장대기 중복 클릭 방지
            return;
        }
    };

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId) {
                case R.id.linearlayout_btn_chat: {
                    Data_OtherUser other = new Data_OtherUser();
                    other.img_url = mWezone.img_url;
                    other.user_uuid = mWezone.wezone_id;
                    other.user_name = mWezone.title;

                    ChattingActivity.startActivityWithGroup(WezoneActivity.this, other);
                }
                break;

                case R.id.linearlayout_btn_group_write:
                    WriteBoardActivity.startActivityWithRegist(WezoneActivity.this, mWezone);
                    break;

                case R.id.linearlayout_btn_entry: //입장버튼
                    if (WezoneUtil.isManager(mWezone.manage_type)) {
                        onEntryAskDialog();
                        return;
                    } else if (Define.LOACTION_TYPE_B.equals(mWezone.location_type)) {
                        //다이얼로그 "입장대기 하시겠습니까?"
                        onWaitAskDialog();
                        return;
                    } else if (Define.LOACTION_TYPE_G.equals(mWezone.location_type) && (Define.ZONE_POSSIBLE_T.equals(mWezone.zone_possible))) {
                        onEntryAskDialog();
                        return;
                    }
                    break;
                case R.id.editimageview_wezone:
                    if (WEZONE_EDIT_DELETE == mMember_type) {
                        setEditDELETEWezone();
                        //수정,삭제
                    } else if (WEZONE_LEAVE == mMember_type) {
                        setLeaveWezone();
                        //입장 대기한 위존 + 입장한 위존
                        //탈퇴
                    }
                    break;
                case R.id.linearlayout_btn_cancal:
                    mDialog.cancel();
                    return;
                case R.id.linearlayout_btn_ok:
                    if (WEZONE_ENTRY == entry_ask) {
                        //입장하기
                        putMyWezone();
                        mDialog.cancel();
                    } else if (WEZONE_MAP_UPDATE == entry_ask) {
                        //위치 업데이트
                        sendWeZonePut(Send_PutDataWithValue.FLAG_LATITUDE, Send_PutDataWithValue.FLAG_LONGITUDE, latitude, longitude);
                        mDialog.cancel();
                    } else if (WEZONE_LEAVE == entry_ask) {
                        //탈퇴
                        putWezoneDelete(Define.TYPE_DELETE);
                        String roomId = mWezone.wezone_id + "@" + getShare().getServerInfo().groupchat_hostname;
                        LibXmppManager.getInstance().leaveMUC(roomId);
                        Data_Chat_UserList data = getShare().getChatUserItem(roomId);
                        getShare().removeUnRead(data);
                        mDialog.cancel();
                    } else {
                        //삭제
                        deleteWezoneDelete(mWezone);
                        mDialog.cancel();
                        Intent i = new Intent();
                        i.putExtra(WEZONE_WEZONE_PUT, mWezone);
                        i.putExtra(WEZONE_WEZONE_PUT_TYPE, REMOVE);
                        setResult(RESULT_OK, i);
                        finish();
                    }
                    break;
                case R.id.imageview_wezone_share:
                    //공유 버튼 클릭시
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("[");
                    sb.append(mWezone.title);
                    sb.append("]위존으로 초대합니다\r\n");
                    sb.append(mWezone.short_url);
                    sb.append("\r\n링크를 통해 가입하세요\r\n");
                    sb.append("From ");
                    sb.append(getShare().getMyInfo().user_name);
                    LibCall.shareDataActivity(WezoneActivity.this, "위존 공유", sb.toString());
                }
                break;
            }
        }
    };

    public void setLeaveWezone() {
        PopupMenu popup = new PopupMenu(WezoneActivity.this, editimageview_wezone);
        popup.getMenuInflater().inflate(R.menu.menu_wezone_leave, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.leave_wezone:
                        onLeaveDialog();
                        break;
                }

                return true;
            }
        });
        popup.show();
    }


    public void setEditDELETEWezone() {
        PopupMenu popup = new PopupMenu(WezoneActivity.this, editimageview_wezone);

        popup.getMenuInflater().inflate(R.menu.menu_wezone_edit_delete, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_wezone:
                        WezoneManagerActivity.startActivityWithEdit(WezoneActivity.this, mWezone);
                        break;
                    case R.id.delete_wezone:
                        onEditDELETEDialog();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }


    private void deleteWezoneDelete(Data_WeZone weZone) {

        Call<Rev_Base> DeleteWezoneCall = wezoneRestful.deleteWezoneDelete(weZone.wezone_id);
        DeleteWezoneCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revBase = response.body();
                if (isNetSuccess(revBase)) {
                }
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
            }
        });
    }

    public void onEntryAskDialog() {
        entry_ask = WEZONE_ENTRY;
        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_entry_ask);
        linearlayout_btn_ok = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_ok);
        linearlayout_btn_ok.setOnClickListener(mClickListener);
        linearlayout_btn_cancal = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_cancal);
        linearlayout_btn_cancal.setOnClickListener(mClickListener);
        mDialog.show();
    }

    public void onWaitAskDialog() {

        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_wait_ask);
        linearlayout_btn_ok = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_ok);
        linearlayout_btn_ok.setOnClickListener(mClickListener);
        linearlayout_btn_cancal = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_cancal);
        linearlayout_btn_cancal.setOnClickListener(mClickListener);
        mDialog.show();

    }

    public void onLeaveDialog() {
        entry_ask = WEZONE_LEAVE;
        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_leave_ask);
        linearlayout_btn_ok = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_ok);
        linearlayout_btn_ok.setOnClickListener(mClickListener);
        linearlayout_btn_cancal = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_cancal);
        linearlayout_btn_cancal.setOnClickListener(mClickListener);
        mDialog.show();
    }

    public void onEditDELETEDialog() {
        entry_ask = WEZONE_EDIT_DELETE;
        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_delete_ask);
        linearlayout_btn_ok = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_ok);
        linearlayout_btn_ok.setOnClickListener(mClickListener);
        linearlayout_btn_cancal = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_cancal);
        linearlayout_btn_cancal.setOnClickListener(mClickListener);
        mDialog.show();
    }

    public void onWezoneMapUpdate() {
        entry_ask = WEZONE_MAP_UPDATE;
        mDialog = new Dialog(this);
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_mapupadate);
        linearlayout_btn_ok = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_ok);
        linearlayout_btn_ok.setOnClickListener(mClickListener);
        linearlayout_btn_cancal = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_cancal);
        linearlayout_btn_cancal.setOnClickListener(mClickListener);
        mDialog.show();
    }


    private void putWezoneDelete(String flag) {

        Send_PutWezoneDelete deleteWezone = new Send_PutWezoneDelete();
        deleteWezone.wezone_id = mWezone.wezone_id;
        deleteWezone.flag = flag;

        Call<Rev_Base> DeleteWezoneCall = wezoneRestful.putWezoneDelete(deleteWezone);
        DeleteWezoneCall.enqueue(new Callback<Rev_Base>() {

            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revBase = response.body();
                if (isNetSuccess(revBase)) {
                    Intent i = new Intent();
                    i.putExtra(WEZONE_WEZONE_PUT, mWezone);
                    i.putExtra(WEZONE_WEZONE_PUT_TYPE, DELETE);
                    setResult(RESULT_OK, i);

                    if (Integer.valueOf(mWezone.member_count) > 0) {
                        int member_count = Integer.valueOf(mWezone.member_count);
                        member_count -= 1;
                        mWezone.member_count = String.valueOf(member_count);

                        mSlidingTabLayout.setViewPager(mPager);

                        mPagerAdapter.notifyDataSetChanged();
                    }
                    setPagerView();
                    mDialog.cancel();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
            }
        });
    }

    public void putMyWezone() {

        Send_PutMyWezone param = new Send_PutMyWezone();
        param.wezone_id = mWezone.wezone_id;
        if ("type_w".equals(wezoneManageType)) {
            param.flag = Define.TYPE_INVITED;
            mType = Define.TYPE_INVITED;

        } else {
            param.flag = Define.TYPE_NORMAL;
            mType = Define.TYPE_NORMAL;
        }
        final Call<Rev_Base> putMywezone = wezoneRestful.putMyWezone(param);
        putMywezone.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revBase = response.body();
                if (isNetSuccess(revBase)) {

                    if (mType.equals(Define.TYPE_NORMAL)) {
                        mWezone.manage_type = Define.TYPE_NORMAL;//입장하기 일반멤버
                        refreshLayout();

                        int member_count = Integer.valueOf(mWezone.member_count);
                        member_count += 1;
                        mWezone.member_count = String.valueOf(member_count);

                        Intent i = new Intent();
                        i.putExtra(WEZONE_WEZONE_PUT, mWezone);
                        i.putExtra(WEZONE_WEZONE_PUT_TYPE, ENTRY);
                        setResult(RESULT_OK, i);

                    } else if (mType.equals(Define.TYPE_INVITED)) {
                        //입장대기 재 입력 방지
                        linearlayout_btn_entry.setOnClickListener(mNotClickListener);
                        mWezone.manage_type = Define.TYPE_INVITED;//입장대기
                        Intent i = new Intent();
                        i.putExtra(WEZONE_WEZONE_PUT, mWezone);
                        i.putExtra(WEZONE_WEZONE_PUT_TYPE, ENTRY_WAIT);
                        setResult(RESULT_OK, i);
                    }

                    editimageview_wezone.setVisibility(View.VISIBLE);
                    setPagerView();
                    mSlidingTabLayout.setViewPager(mPager);
                    mPagerAdapter.notifyDataSetChanged();




                    Data_Chat_UserList dataChatUserList = new Data_Chat_UserList();
                    dataChatUserList.other_uuid = mWezone.wezone_id + "@" + getShare().getServerInfo().groupchat_hostname;
                    dataChatUserList.img_url = mWezone.img_url;
                    dataChatUserList.user_name = mWezone.title;
                    dataChatUserList.kind = "groupchat";
                    dataChatUserList.push_flag = "T";
                    dataChatUserList.unread = new Data_UnRead();
                    dataChatUserList.unread.msgkeys = new ArrayList<Data_MsgKey>();
                    dataChatUserList.member_count = mWezone.member_count;
                    getShare().addUnRead(dataChatUserList);

                    try {
                        LibXmppManager.getInstance().createGroupChat(dataChatUserList.other_uuid, getUuid());
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    LibXmppManager.getInstance().joinRoom(dataChatUserList.other_uuid);
                }
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
            }
        });

    }

    @Override
    public void OnBoardCountEvent(int board_count) {
        if (mWezone.board_count.equals(String.valueOf(board_count)) == false) {
            mWezone.board_count = String.valueOf(board_count);
            setPagerView();
            mSlidingTabLayout.setViewPager(mPager);
            mPagerAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onFragmentPickerSet(String lat, String lon, String mtype) {
        if (WEZONE_MAP_FRAGMENT_CLICK.equals(mtype)) {
            //다이얼로그
            this.latitude = lat;
            this.longitude = lon;
            onWezoneMapUpdate();
            //
        } else {
            if (!"0".equals(String.valueOf(lat)) || !"0".equals(String.valueOf(lon))) {
            } else {
                mGpsInfo = new GpsInfo(WezoneActivity.this);
            }
        }
    }

    private void sendWeZonePut(String flag1, String flag2, String lat, String lon) {

        Send_PutWezone putWezone = new Send_PutWezone();

        ArrayList<Send_PutDataWithValue> putDataList = new ArrayList<>();
        ArrayList<Send_PutDataWithValue> putDataList2 = new ArrayList<>();
        ArrayList<Send_PutDataWithValue> putDataList3 = new ArrayList<>();
        Send_PutDataWithValue putData = new Send_PutDataWithValue();
        Send_PutDataWithValue putData2 = new Send_PutDataWithValue();

        putWezone.wezone_id = mWezone.wezone_id;

        putData.flag = flag1;
        putData.val = lat;
        mWezone.latitude = lat;
        putDataList.add(putData);
        putWezone.wezone_info = putDataList;
        putDataList3.addAll(putDataList);

        putData2.flag = flag2;
        putData2.val = lon;
        mWezone.longitude = lon;
        putDataList2.add(putData2);
        putDataList3.addAll(putDataList2);

        putWezone.wezone_info = putDataList3;

        Call<Rev_Base> PutWezoneCall = wezoneRestful.putWezone(putWezone);
        PutWezoneCall.enqueue(new Callback<Rev_Base>() {

            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                WezoneHomeFragment fragment = (WezoneHomeFragment) mPagerAdapter.getItemAt(0);
                fragment.address_reload(WEZONE_MAP_FRAGMENT_CLICK, mWezone);
                Intent i = new Intent();
                i.putExtra(WEZONE_WEZONE_PUT, mWezone);
                i.putExtra(WEZONE_WEZONE_PUT_TYPE, PUT);
                setResult(RESULT_OK, i);
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
            }
        });
    }

    private class NavigationAdapter extends CacheFragmentStatePagerAdapter {

        private int mScrollY;
        private Data_WeZone mWezone;
        private String[] mTitles;

        public NavigationAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            mTitles = titles;
        }

        public void setScrollY(int scrollY) {
            mScrollY = scrollY;
        }

        public void setWezoneId(Data_WeZone wezone) {
            mWezone = wezone;
        }

        @Override
        protected Fragment createItem(int position) {
            WezoneBaseFragment f;
            final int pattern = position % 4;
            switch (pattern) {
                case 0: {
                    f = new WezoneHomeFragment();
                    break;
                }
                case 1: {
                    f = new WezoneBoardFragment();
                    break;
                }
                case 2: {
                    f = new WezoneMemberFragment();
                    break;
                }
                case 3:
                default: {
                    f = new WezoneHomeFragment();
                    break;
                }
            }
            f.setArguments(mScrollY, mWezone);
            return f;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        public void setPageTitle(String[] titles) {
            mTitles = titles;
        }
    }

    //WezoneHomeFragment, WezoneBoardFragment, WezoneMemberFragment
    public void onScrollChanged(int scrollY, Scrollable s) {
        WezoneBaseFragment fragment = (WezoneBaseFragment) mPagerAdapter.getItemAt(mPager.getCurrentItem());
        if (fragment == null) {
            return;
        }
        View view = fragment.getView();
        if (view == null) {
            return;
        }
        Scrollable scrollable = (Scrollable) view.findViewById(R.id.scroll);
        if (scrollable == null) {
            return;
        }
        if (scrollable == s) {
            // This method is called by not only the current fragment but also other fragments
            // when their scrollY is changed.
            // So we need to check the caller(S) is the current fragment.
            int adjustedScrollY = Math.min(scrollY, mFlexibleSpaceHeight - mTabHeight + getActionBarSize());
            translateTab(adjustedScrollY, false);
            propagateScroll(adjustedScrollY);
        }

        //toolbar
        int baseColor = getResources().getColor(getCurrentThemeColorId());
        float alpha = Math.min(1, (float) scrollY / (mFlexibleSpaceHeight / 2));
        if (alpha == ALPHA_TRANSPARENT && alpha > ALPHA_MIDDLE) {
            //scrollY가 제대로 읽어지지 않을 때
            scrollY = SCROLLY; // maintoolbar의 투명도에서 일부 버그가 있음. scrollY가 제대로 읽어지지 않음. 평균수치에 맞춰 scrollY에 직접 값을 입력함.
            alpha = Math.min(1, (float) scrollY / (mFlexibleSpaceHeight / 2));
            main_toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        } else if (alpha < ALPHA_MIDDLE) {
            main_toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        }
    }

    private void translateTab(int scrollY, boolean animated) {
        int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_wezone_header) + getActionBarSize();
        View headerView = findViewById(R.id.linearlayout_header);
        TextView titleView = (TextView) findViewById(R.id.title);
        ViewHelper.setTranslationY(headerView, ScrollUtils.getFloat(-scrollY, -flexibleSpaceImageHeight, 0));

        // If tabs are moving, cancel it to start a new animation.
        ViewPropertyAnimator.animate(mSlidingTabLayout).cancel();
        // Tabs will move between the top of the screen to the bottom of the image.
        float translationY = ScrollUtils.getFloat(-scrollY + mFlexibleSpaceHeight - mTabHeight + getActionBarSize(), getActionBarSize(), mFlexibleSpaceHeight - mTabHeight + getActionBarSize());

        if (animated) {
            // Animation will be invoked only when the current tab is changed.
            ViewPropertyAnimator.animate(mSlidingTabLayout)
                    .translationY(translationY)
                    .setDuration(200)
                    .start();
        } else {
            // When Fragments' scroll, translate tabs immediately (without animation).
            ViewHelper.setTranslationY(mSlidingTabLayout, translationY);
        }
    }

    private void propagateScroll(int scrollY) {
        // Set scrollY for the fragments that are not created yet
        mPagerAdapter.setScrollY(scrollY);

        // Set scrollY for the active fragments
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            // Skip current item
            if (i == mPager.getCurrentItem()) {
                continue;
            }
            // Skip destroyed or not created item
            WezoneBaseFragment f =
                    (WezoneBaseFragment) mPagerAdapter.getItemAt(i);
            if (f == null) {
                continue;
            }

            View view = f.getView();
            if (view == null) {
                continue;
            }
            f.setScrollY(scrollY, 0);
            f.updateFlexibleSpace(scrollY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Define.INTENT_RESULT_EDIT_TEXT:
                if (resultCode == RESULT_OK) {
                    String contents = data.getStringExtra(EditTextActivity.EDITTEXT_CONTENTS);
                    postComment(contents);
                }
                break;

            case Define.INTENT_RESULT_BOARD: {

                if (resultCode == RESULT_OK) {
                    String action = data.getStringExtra(WezoneBoardDetailActivity.WEZONE_BOARD_RESULT_ACTION);
                    String write_board = data.getStringExtra(WriteBoardActivity.BOARD_DATA_TOTAL_COUNT);

                    WezoneBoardFragment boardFragment = (WezoneBoardFragment) mPagerAdapter.getItemAt(1);

                    if (action != null) {
                        if (WezoneBoardDetailActivity.WEZONE_BOARD_RESULT_ACTION_DELETE.equals(action)) {
                            String board_id = data.getStringExtra(WezoneBoardDetailActivity.WEZONE_BOARD_ID);
                            boardFragment.deleteBoardData(board_id); // d
                            if (Integer.valueOf(mWezone.board_count) > 0) {
                                int board_count = Integer.valueOf(mWezone.board_count);
                                board_count -= 1;
                                mWezone.board_count = String.valueOf(board_count);
                            }
                        }
                    }

                    if (write_board != null) {
                        if (write_board.equals(WriteBoardActivity.ADD)) {
                            boardFragment.refreshBoardData();
                            int board_count = Integer.valueOf(mWezone.board_count);
                            board_count += 1;
                            mWezone.board_count = String.valueOf(board_count);
                        }
                    }
                    setPagerView();
                    mSlidingTabLayout.setViewPager(mPager);

                    mPagerAdapter.notifyDataSetChanged();
                }
            }
            break;

            case Define.INTENT_RESULT_WEZONE:
                if (resultCode == RESULT_OK) {
                    mWezone = (Data_WeZone) data.getSerializableExtra(WEZONE_EDIT);
                    reload();
                    getWezoneaddress(mWezone);
                }
                break;
            case Define.INTENT_RESULT_WEZONE_HASH:
                if (resultCode == RESULT_OK) {

                }
                break;
            case Define.INTENT_RESULT_MEMBER_LIST_FRIEND_PULSE:
                if (resultCode == RESULT_OK) {
                    String mfriend_uuid = data.getStringExtra(FRIEND_PULS);
                    WezoneMemberFragment wezoneMemberFragment = (WezoneMemberFragment) mPagerAdapter.getItemAt(2);
                    wezoneMemberFragment.setFriendPuls(mfriend_uuid);
                }
                break;
        }
    }

    public void postComment(final String contents) {

        Send_PostComment comment = new Send_PostComment();

        comment.wezone_id = mWezone.wezone_id;
        comment.type = Data_Comment.TYPE_WEZONE_COMMENT;
        comment.content = contents;

        Call<Rev_PostComment> postComment = wezoneRestful.postComment(comment);
        postComment.enqueue(new Callback<Rev_PostComment>() {
            @Override
            public void onResponse(Call<Rev_PostComment> call, Response<Rev_PostComment> response) {
                Rev_PostComment revPostComment = response.body();
                if (isNetSuccess(revPostComment)) {

                    Data_Comment comment = new Data_Comment();
                    comment.comment_id = revPostComment.comment_id;
                    comment.wezone_id = mWezone.wezone_id;
                    comment.uuid = getUuid();
                    comment.type = Data_Comment.TYPE_WEZONE_COMMENT;
                    comment.content = contents;
                    comment.user_name = getName();
                    comment.img_url = getImageUrl();
                    comment.create_datetime = LibDateUtil.getCurruntDayWithFormat("yyyy-MM-dd HH:mm:ss");

                    WezoneHomeFragment fragment = (WezoneHomeFragment) mPagerAdapter.getItemAt(0);
                    fragment.addComment(comment);
                }
            }

            @Override
            public void onFailure(Call<Rev_PostComment> call, Throwable t) {
            }
        });
    }

    public void getWezoneaddress(final Data_WeZone weZone) {
        Call<Rev_Wezoninfo> WezoneCall = wezoneRestful.getWezone(weZone.wezone_id);
        WezoneCall.enqueue(new Callback<Rev_Wezoninfo>() {

            @Override
            public void onResponse(Call<Rev_Wezoninfo> call, Response<Rev_Wezoninfo> response) {
                Rev_Wezoninfo resultData = response.body();
                if (isNetSuccess(resultData)) {
                    if (resultData.wezone_info != null) {
                        mWezoneadress = resultData.wezone_info;
                        if (mWezone.zone_in != null) {
//                            공지글 선택한 데이터 저장.
                            mWezoneadress.location_data = mWezone.location_data;
                        }
                        if (weZone.zone_in != null) {
                            //선택한 공지글이 있으면 저장
                            mWezoneadress.zone_in = weZone.zone_in;
                        }
                        Intent i = new Intent();
                        i.putExtra(WEZONE_WEZONE_PUT, mWezoneadress);
                        i.putExtra(WEZONE_WEZONE_PUT_TYPE, PUT);
                        setResult(RESULT_OK, i);

                    }
                }
            }

            @Override
            public void onFailure(Call<Rev_Wezoninfo> call, Throwable t) {
            }
        });
    }

    public void onMemberList(ArrayList<Data_Zone_Member> memberlist) {
        WezoneBoardFragment fragment1 = (WezoneBoardFragment) mPagerAdapter.getItemAt(1);
        fragment1.Board_User_Info(memberlist);

        WezoneHomeFragment fragment2 = (WezoneHomeFragment) mPagerAdapter.getItemAt(0);
        fragment2.Board_User_Info(memberlist);
    }

    @Override
    public void onClickLeftBtn(View v) {
        sendlocationdata();
    }

    public void sendlocationdata() {
        if (Define.TYPE_INVITED.equals(mType)) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGpsInfo = new GpsInfo(WezoneActivity.this);
        // GPS 사용유무 가져오기
        if (mGpsInfo.isGetLocation()) {
            ImageView_wezone_gps_check.setVisibility(View.GONE);
        } else {
            // GPS 를 사용할수 없으므로
            ImageView_wezone_gps_check.setVisibility(View.VISIBLE);
        }
    }
}
