package com.vinetech.wezone.Main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.vinetech.beacon.BluetoothLeDevice;
import com.vinetech.beacon.BluetoothLeDeviceStore;
import com.vinetech.beacon.BluetoothLeService;
import com.vinetech.ui.MarqueeTextView;
import com.vinetech.ui.RandomImageView;
import com.vinetech.ui.RippleArcView;
import com.vinetech.util.LibDateUtil;
import com.vinetech.util.UIViewAnimation;
import com.vinetech.util.WezoneUtil;
import com.vinetech.util.crypt.CryptPreferences;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Beacon.BeaconManageActivity;
import com.vinetech.wezone.Beacon.BeaconScanActivity;
import com.vinetech.wezone.Beacon.BeaconSharedActivity;
import com.vinetech.wezone.Common.Activity_WebView;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_PushData;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Message.MessageListActivity;
import com.vinetech.wezone.Notice.NoticeActivity;
import com.vinetech.wezone.Profile.FriendListActivity;
import com.vinetech.wezone.Profile.MyProfileActivity;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Advert;
import com.vinetech.wezone.RevPacket.Rev_BeaconList;
import com.vinetech.wezone.RevPacket.Rev_Theme;
import com.vinetech.wezone.Setting.HelpActivity;
import com.vinetech.wezone.Setting.SettingActivity;
import com.vinetech.wezone.Theme.ThemeActivity;
import com.vinetech.wezone.Wezone.WezoneManagerActivity;
import com.vinetech.wezone.Wezone.WezoneSearchActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_COARSE_LOCATION = 1;
    public static final String WEZONE_WEZONE_PUT = "wezone_put";
    public static final String DELETE = "DELETE";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String REMOVE = "REMOVE";
    public static final String ENTRY_WAIT = "ENTRY_WAIT";
    public static final String ENTRY = "ENTRY";
    public static final String WEZONE_WEZONE_PUT_TYPE = "WEZONE_WEZONE_PUT_TYPE";

    public static final String KAKAOTALK = "K";
    public static final String FACEBOOK = "F";
    public static final String GOOGLE = "G";
    public static final String KAKAOTALK_EMAIL = "By KakaoTalk";
    public static final String FACEBOOK_EMAIL = "By Facebook";

    public static final String NOT_BACKPRESS = "NOT_BACKPRESS";
    public static final String THEMEZONE_CLICK = "THEMEZONE_CLICK";
    public static final String WEZONE_CLICK = "WEZONE_CLICK";

    public ArrayList<Data_WeZone> mWezoneInfo;
    public Data_WeZone mresultData;
    private SlidingUpPanelLayout mLayout;


    private static final float SLIDING_OFSSET_DIM = 0.7042553f;
    private RandomImageView randomImageView;
    private RippleArcView rippleArcView;
    private LinearLayout beacon_pager_area;
    private LinearLayout linearlayout_beacon_controller;
    private ImageView imageview_beacon_loading;
    private ImageView imageview_beacon_search_btn;
    private MainBeaconListAdapter mMainBeaconListAdapter;
    private MainBeaconListAdapter mSearchBeaconListAdapter;
    private ArrayList<Data_Beacon> mBeaconList;
    private ArrayList<Data_Beacon> mMyBeaconList;
    private ArrayList<Data_Beacon> mTempBeaconList;
    private boolean isScanning;
    private MarqueeTextView marqueeTextView;
    private ImageButton mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFlexibleSpaceListHeader;
    private int mFabMargin;
    private boolean mFabIsShown;

    private LinearLayout linearlayout_text_area;
    private LinearLayout linearlayout_btn_mode;
    private TextView textview_mode;
    private LinearLayout linearlayout_panel;
    private LinearLayout linearlayout_btn_theme_zone;
    private LinearLayout linearlayout_btn_wezone;
    private TextView textview_beacon_cnt;
    private EditText edittext_search;
    private TextView textview_bunnyzone;
    private ImageView imageview_bunnyzone_point;
    private TextView textview_wezone;
    private ImageView imageview_wezone_point;

    private ObservableListView mbeacon_listview;
    private FloatingActionButton fab_btn_add;
    private BluetoothLeDeviceStore mBluetoothLeDeviceStore;



    private RelativeLayout relativelayout_event_area;

    private ViewPager fragementPager;
    private ZonePagerAdapter mZonePagerAdapter;

    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawer;

    //왼쪽메뉴..
    private ImageView imageview_photo;
    private TextView textview_name;
    private TextView textview_email;
    private LinearLayout linearlayout_btn_beacon_market;
    private LinearLayout linearlayout_store;
    private LinearLayout linearlayout_btn_auction;
    private LinearLayout linearlayout_btn_gmarket;
    private LinearLayout linearlayout_btn_nstore;
    private LinearLayout linearlayout_btn_people;
    private TextView textview_people;
    private LinearLayout linearlayout_btn_setting;
    private LinearLayout linearlayout_btn_help;
    private LinearLayout linearlayout_btn_event;
    private ImageView imageview_event;
    private LinearLayout linearlayout_btn_title;
    private LinearLayout linearlayout_navi_btn_menu;
    private RelativeLayout relativelayout_navi_btn_noti;
    private TextView textview_noti_cnt;
    private RelativeLayout relativelayout_navi_btn_msg;
    private EditText edittext_in;
    private EditText edittext_out;
    private EditText edittext_queue;
    private EditText edittext_out_time;
    private EditText edittext_back_out_time;


    private boolean isFinished = false;

    private static final int SEARCH_MODE_ALL = 0;
    private static final int SEARCH_MODE_MINE = 1;
    private int mSearchMode = SEARCH_MODE_ALL;

    private LinearLayout linearlayout_btn_apply;

    //fab버튼 클릭시 전체화면 dim처리시 사용
    private LinearLayout LinearLayout_main_tab_bg;
    private RelativeLayout relativelayout_main_tab;
    private LinearLayout linearLayout_main_fab;

    private Data_PushData mPushData;

    private int currentCount = 0;
    private int MODE_COUNT = 9;
    private Timer clickTimer;

    private Rev_Advert mAdvert;

    private ArrayList<String> mBeaconNotiList;
    private ArrayList<String> mBeaconNotiImgList;

    private LinearLayout linearlayout_noti_area;
    private ImageView imageview_beacon_noti;
    private TextView textview_beacon_noti;
    private TextView textview_beacon_noti_time;
    private ImageView imageview_btn_close;

    private LinearLayout linearlayout_no_result_area;
    private ImageView imageview_btn_beacon_add;


    public FloatingActionMenu fab_menu;
    public com.github.clans.fab.FloatingActionButton fab_btn_search;
    public com.github.clans.fab.FloatingActionButton fab_btn_regist_wezone;
    private String PagePoint = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mresultData = new Data_WeZone();

        linearlayout_navi_btn_menu = (LinearLayout) findViewById(R.id.linearlayout_navi_btn_menu);
        linearlayout_navi_btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSideMenu();
            }
        });

        relativelayout_navi_btn_noti = (RelativeLayout) findViewById(R.id.relativelayout_navi_btn_noti);
        relativelayout_navi_btn_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(new Intent(MainActivity.this, NoticeActivity.class));
            }
        });

        textview_noti_cnt = (TextView) findViewById(R.id.textview_noti_cnt);

        relativelayout_main_tab = (RelativeLayout) findViewById(R.id.relativelayout_main_tab); // 추가됨
        LinearLayout_main_tab_bg = (LinearLayout) findViewById(R.id.LinearLayout_main_tab_bg); // 추가됨
        linearLayout_main_fab = (LinearLayout) findViewById(R.id.linearLayout_main_fab); // 추가됨

        relativelayout_navi_btn_msg = (RelativeLayout) findViewById(R.id.relativelayout_navi_btn_msg);
        relativelayout_navi_btn_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(new Intent(MainActivity.this, MessageListActivity.class));
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = (LinearLayout) findViewById(R.id.drawer);

        imageview_photo = (ImageView) findViewById(R.id.imageview_photo);
        imageview_photo.setOnClickListener(mLeftMenuListener);

        textview_name = (TextView) findViewById(R.id.textview_name);
        textview_email = (TextView) findViewById(R.id.textview_email);

        linearlayout_btn_beacon_market = (LinearLayout) findViewById(R.id.linearlayout_btn_beacon_market);
        linearlayout_btn_beacon_market.setOnClickListener(mLeftMenuListener);

        linearlayout_store = (LinearLayout) findViewById(R.id.linearlayout_store);
        linearlayout_btn_auction = (LinearLayout) findViewById(R.id.linearlayout_btn_auction);
        linearlayout_btn_auction.setOnClickListener(mLeftMenuListener);
        linearlayout_btn_gmarket = (LinearLayout) findViewById(R.id.linearlayout_btn_gmarket);
        linearlayout_btn_gmarket.setOnClickListener(mLeftMenuListener);
        linearlayout_btn_nstore = (LinearLayout) findViewById(R.id.linearlayout_btn_nstore);
        linearlayout_btn_nstore.setOnClickListener(mLeftMenuListener);


        linearlayout_btn_people = (LinearLayout) findViewById(R.id.linearlayout_btn_people);
        linearlayout_btn_people.setOnClickListener(mLeftMenuListener);

        textview_people = (TextView) findViewById(R.id.textview_people);

        getShare().setFriendCnt(getShare().getMyInfo().friend_count);

        linearlayout_btn_setting = (LinearLayout) findViewById(R.id.linearlayout_btn_setting);
        linearlayout_btn_setting.setOnClickListener(mLeftMenuListener);
        linearlayout_btn_help = (LinearLayout) findViewById(R.id.linearlayout_btn_help);
        linearlayout_btn_help.setOnClickListener(mLeftMenuListener);

        linearlayout_btn_event = (LinearLayout) findViewById(R.id.linearlayout_btn_event);
        linearlayout_btn_event.setOnClickListener(mLeftMenuListener);
        imageview_event = (ImageView) findViewById(R.id.imageview_event);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                dimbackgroundfab(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mLayout.setAnchorPoint(0.12f);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        rippleArcView = (RippleArcView) findViewById(R.id.rippleArcView);

        beacon_pager_area = (LinearLayout) findViewById(R.id.beacon_pager_area);
        linearlayout_beacon_controller = (LinearLayout) findViewById(R.id.linearlayout_beacon_controller);
        imageview_beacon_search_btn = (ImageView) findViewById(R.id.imageview_beacon_search_btn);

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceListHeader = getResources().getDimensionPixelSize(R.dimen.flexible_space_list_header);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);

        mActionBarSize = getActionBarSize();
        randomImageView = (RandomImageView) findViewById(R.id.randomImageView);
        randomImageView.setOnRippleViewClickListener(new RandomImageView.OnRippleViewClickListener() {
            @Override
            public void onRippleViewClicked(View view, Object obj) {
            }
        });


        mBluetoothLeDeviceStore = new BluetoothLeDeviceStore(null);

        textview_beacon_cnt = (TextView) findViewById(R.id.textview_beacon_cnt);
        edittext_search = (EditText) findViewById(R.id.edittext_search);
        edittext_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        edittext_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbeacon_listview.setSelection(1);
            }
        });

        edittext_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable edit) {
                String s = edit.toString();
                if (s.length() > 0){

                    mTempBeaconList.clear();

                    if(mSearchMode == SEARCH_MODE_ALL){
                        for(Data_Beacon beacon : mBeaconList){
                            if(beacon.name.contains(s)){
                                mTempBeaconList.add(beacon);
                            }
                        }
                    }else{
                        for(Data_Beacon beacon : mMyBeaconList){
                            if(beacon.name.contains(s)){
                                mTempBeaconList.add(beacon);
                            }
                        }
                    }
                    mSearchBeaconListAdapter = new MainBeaconListAdapter(MainActivity.this, mTempBeaconList);
                    textview_beacon_cnt.setText(" " + mTempBeaconList.size());
                    mbeacon_listview.setAdapter(mSearchBeaconListAdapter);
                }else{
                    mTempBeaconList.clear();
                    setBeaconListWithMode();
                }
            }
        });
        // 비콘
        mbeacon_listview = (ObservableListView) findViewById(R.id.beacon_listview);
        mbeacon_listview.setScrollViewCallbacks(this);
        mbeacon_listview.scrollVerticallyTo(-mFlexibleSpaceImageHeight);


        View paddingView = getLayoutInflater().inflate(R.layout.dummy_header, null);
        paddingView.setClickable(true);

        View paddingFotterView = getLayoutInflater().inflate(R.layout.dummy_footer, null);
        paddingFotterView.setClickable(true);

        mbeacon_listview.addHeaderView(paddingView);
        mbeacon_listview.addFooterView(paddingFotterView);

        linearlayout_no_result_area = (LinearLayout) paddingFotterView.findViewById(R.id.linearlayout_no_result_area);
        imageview_btn_beacon_add = (ImageView) paddingFotterView.findViewById(R.id.imageview_btn_beacon_add);
        imageview_btn_beacon_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(new Intent(MainActivity.this, BeaconScanActivity.class));
            }
        });

        mBeaconList = new ArrayList<>();
        mMyBeaconList = new ArrayList<>();
        mTempBeaconList = new ArrayList<>();
        setBeaconListWithMode();

        mbeacon_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int realPos = position - 1;

                //검색됬으면 그 데이터 사용
                    if(mTempBeaconList != null && mTempBeaconList.size() > 0){
                        if (mTempBeaconList.get(realPos) != null) {
                            Data_Beacon beacon = mTempBeaconList.get(realPos);
                            //비콘 공유 해제시 리스트에 바로 사라지지 않아 클릭 시 앱이 죽는 현상 방지
                            if ("N".equals(beacon.manage_type)) {
                                BeaconSharedActivity.startActivity(MainActivity.this, beacon);
                            } else {
                                BeaconManageActivity.startActivityWithEdit(MainActivity.this, beacon, beacon.mac);
                            }
                        }
                        return;
                    }

                    if (mSearchMode == SEARCH_MODE_ALL) {
                        if(mBeaconList != null) {
                            if (mBeaconList.size() != 0) {
                                if (mBeaconList.get(realPos) != null) {
                                    Data_Beacon beacon = mBeaconList.get(realPos);
                                    //비콘 공유 해제시 리스트에 바로 사라지지 않아 클릭 시 앱이 죽는 현상 방지
                                    if ("N".equals(beacon.manage_type)) {
                                        BeaconSharedActivity.startActivity(MainActivity.this, beacon);
                                    } else {
                                        BeaconManageActivity.startActivityWithEdit(MainActivity.this, beacon, beacon.mac);
                                    }
                                }
                            }
                        }
                    } else {

                        if(mMyBeaconList != null) {
                            Data_Beacon beacon = mMyBeaconList.get(realPos);

                            if ("N".equals(beacon.manage_type)) {
                                BeaconSharedActivity.startActivity(MainActivity.this, beacon);
                            } else {
                                BeaconManageActivity.startActivityWithEdit(MainActivity.this, beacon, beacon.mac);
                            }
                        }
                    }

            }
        });

        fab_btn_add = (FloatingActionButton) findViewById(R.id.fab_btn_add);
        fab_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(new Intent(MainActivity.this, BeaconScanActivity.class));
            }
        });

        setTitle(null);

        marqueeTextView = (MarqueeTextView) findViewById(R.id.marqueeTextView);

        mFab = (ImageButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleScanning();
            }
        });

        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        ViewHelper.setScaleX(mFab, 0);
        ViewHelper.setScaleY(mFab, 0);

        requestLocationPermission();

        relativelayout_event_area = (RelativeLayout) findViewById(R.id.relativelayout_event_area);

        textview_bunnyzone = (TextView) findViewById(R.id.textview_bunnyzone);
        imageview_bunnyzone_point = (ImageView) findViewById(R.id.imageview_bunnyzone_point);

        textview_wezone = (TextView) findViewById(R.id.textview_wezone);
        imageview_wezone_point = (ImageView) findViewById(R.id.imageview_wezone_point);

        fragementPager = (ViewPager) findViewById(R.id.fragementPager);
        mZonePagerAdapter = new ZonePagerAdapter(getSupportFragmentManager(), MainActivity.this);
        fragementPager.setAdapter(mZonePagerAdapter);

        fragementPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    PagePoint = THEMEZONE_CLICK;
                    dimbackgroundfab(0);
                } else {
                    PagePoint = WEZONE_CLICK;
                    dimbackgroundfab(0);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        linearlayout_text_area = (LinearLayout) findViewById(R.id.linearlayout_text_area);
        linearlayout_text_area.setVisibility(View.GONE);
        linearlayout_btn_mode = (LinearLayout) findViewById(R.id.linearlayout_btn_mode);
        linearlayout_btn_mode.setOnClickListener(mListener);
        textview_mode = (TextView) findViewById(R.id.textview_mode);
        textview_mode.setText("ALL");

        linearlayout_btn_title = (LinearLayout) findViewById(R.id.linearlayout_btn_title);
        linearlayout_btn_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCount >= MODE_COUNT) {
                    if (linearlayout_text_area.getVisibility() == View.VISIBLE) {
                        linearlayout_text_area.setVisibility(View.GONE);
                    } else {
                        linearlayout_text_area.setVisibility(View.VISIBLE);
                    }
                    currentCount = 0;
                } else {
                    currentCount++;

                    if (clickTimer == null) {
                        clickTimer = new Timer();
                        clickTimer.schedule(new TimerTask() {
                            public void run() {
                                currentCount = 0;
                                clickTimer = null;
                            }
                        }, 5000);
                    }
                }

            }
        });


        linearlayout_btn_theme_zone = (LinearLayout) findViewById(R.id.linearlayout_btn_theme_zone);
        linearlayout_btn_theme_zone.setOnClickListener(mListener);
        linearlayout_btn_wezone = (LinearLayout) findViewById(R.id.linearlayout_btn_wezone);
        linearlayout_btn_wezone.setOnClickListener(mListener);


        linearlayout_panel = (LinearLayout) findViewById(R.id.linearlayout_panel);

        toggleScanning();

        edittext_in = (EditText) findViewById(R.id.edittext_in);
        int intIn = CryptPreferences.getCryptInt(MainActivity.this, Define.BEACON_IN_RANGE, Define.BEACON_IN_RANGE_DEFAULT);
        edittext_in.setText(String.valueOf(intIn));
        edittext_out = (EditText) findViewById(R.id.edittext_out);
        int intOut = CryptPreferences.getCryptInt(MainActivity.this, Define.BEACON_OUT_RANGE, Define.BEACON_OUT_RANGE_DEFAULT);
        edittext_out.setText(String.valueOf(intOut));
        edittext_queue = (EditText) findViewById(R.id.edittext_queue);
        int queueCnt = CryptPreferences.getCryptInt(MainActivity.this, Define.BEACON_QUEUE_CNT, Define.BEACON_QUEUE_CNT_DEFAULT);
        edittext_queue.setText(String.valueOf(queueCnt));

        edittext_out_time = (EditText) findViewById(R.id.edittext_out_time);
        int outTime = CryptPreferences.getCryptInt(MainActivity.this, Define.BEACON_OUT_TIME, Define.BEACON_OUT_TIME_DEFAULT);
        edittext_out_time.setText(String.valueOf(outTime));

        edittext_back_out_time = (EditText) findViewById(R.id.edittext_back_out_time);
        int backOutTime = CryptPreferences.getCryptInt(MainActivity.this, Define.BEACON_BACKGROUND_OUT_TIME, Define.BEACON_BACKGROUND_OUT_TIME_DEFAULT);
        edittext_back_out_time.setText(String.valueOf(backOutTime));


        linearlayout_btn_apply = (LinearLayout) findViewById(R.id.linearlayout_btn_apply);
        linearlayout_btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strIn = edittext_in.getText().toString().trim();
                if (WezoneUtil.isEmptyStr(strIn)) {
                    edittext_in.setText("");
                    Toast.makeText(MainActivity.this, "IN 범위를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                String strOut = edittext_out.getText().toString().trim();
                if (WezoneUtil.isEmptyStr(strOut)) {
                    edittext_out.setText("");
                    Toast.makeText(MainActivity.this, "OUT 범위를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                String strQueueCnt = edittext_queue.getText().toString().trim();
                if (WezoneUtil.isEmptyStr(strQueueCnt)) {
                    edittext_queue.setText("");
                    Toast.makeText(MainActivity.this, "QUEUE 갯수를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                String strOutTime = edittext_out_time.getText().toString().trim();
                if (WezoneUtil.isEmptyStr(strOutTime)) {
                    edittext_out_time.setText("");
                    Toast.makeText(MainActivity.this, "Forground Out 시간을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                String strBackOutTime = edittext_back_out_time.getText().toString().trim();
                if (WezoneUtil.isEmptyStr(strBackOutTime)) {
                    edittext_back_out_time.setText("");
                    Toast.makeText(MainActivity.this, "Background Out 시간을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                CryptPreferences.putCryptInt(MainActivity.this, Define.BEACON_IN_RANGE, Integer.valueOf(strIn));
                CryptPreferences.putCryptInt(MainActivity.this, Define.BEACON_OUT_RANGE, Integer.valueOf(strOut));
                CryptPreferences.putCryptInt(MainActivity.this, Define.BEACON_QUEUE_CNT, Integer.valueOf(strQueueCnt));
                CryptPreferences.putCryptInt(MainActivity.this, Define.BEACON_OUT_TIME, Integer.valueOf(strOutTime));
                CryptPreferences.putCryptInt(MainActivity.this, Define.BEACON_BACKGROUND_OUT_TIME, Integer.valueOf(strBackOutTime));

                Toast.makeText(MainActivity.this, "적용 완료!!", Toast.LENGTH_SHORT).show();
            }
        });
        mWezoneInfo = new ArrayList<>();

        getAdvertData();

        mPushData = (Data_PushData) getIntent().getSerializableExtra(Define.INTENTKEY_PUSH_VALUE);
        if (mPushData != null) {
            moveActivityWithPushData(mPushData.type, mPushData.kind, mPushData.item_id);
        }

        mBeaconNotiList = new ArrayList<>();
        mBeaconNotiImgList = new ArrayList<>();

        linearlayout_noti_area = (LinearLayout) findViewById(R.id.linearlayout_noti_area);
        imageview_beacon_noti = (ImageView) findViewById(R.id.imageview_beacon_noti);
        textview_beacon_noti = (TextView) findViewById(R.id.textview_beacon_noti);
        textview_beacon_noti_time = (TextView) findViewById(R.id.textview_beacon_noti_time);
        imageview_btn_close = (ImageView) findViewById(R.id.imageview_btn_close);
        imageview_btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemCnt = mBeaconNotiList.size();
                if (itemCnt > 0) {
                    mBeaconNotiList.remove(itemCnt - 1);
                    mBeaconNotiImgList.remove(itemCnt - 1);
                    UIViewAnimation.GoneViewWithTransAnimationUToB(getApplicationContext(), linearlayout_noti_area, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            linearlayout_noti_area.setVisibility(View.GONE);
                            showBeaconNotice();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }
        });

        //fab 버튼 처리
        fab_menu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        fab_menu_event();
    }


    public void fab_menu_event() {
        fab_menu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        fab_menu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    LinearLayout_main_tab_bg.setVisibility(View.VISIBLE);
                    fab_menu.getMenuIconView().setImageResource(R.drawable.ic_add);
                    //새로운 액티비티로 dim 처리
//                    Intent intent = new Intent(getContext(), TransparentActivity.class);
//                    startActivity(intent);
                } else {
                    LinearLayout_main_tab_bg.setVisibility(View.GONE);
                    fab_menu.getMenuIconView().setImageResource(R.drawable.btn_more_black);
                }
            }
        });

        fab_btn_search = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_btn_search);
        fab_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mActivity.moveActivity(new Intent(mActivity,WezoneSearchActivity.class));
                WezoneSearchActivity.startActivityWith(MainActivity.this);
                fab_menu.toggle(true);
            }
        });

        fab_btn_regist_wezone = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_btn_regist_wezone);
        fab_btn_regist_wezone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WezoneManagerActivity.startActivityWithRegist(MainActivity.this);  //wezone 생성(등록)
                // BunnyZoneManageActivity.startActivityWithRegist(mActivity,null);
                fab_menu.toggle(true);

            }
        });
        LinearLayout_main_tab_bg.setVisibility(View.GONE);
        if(LinearLayout_main_tab_bg.VISIBLE == View.VISIBLE) {
            LinearLayout_main_tab_bg.setOnTouchListener(new View.OnTouchListener() {
                 @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    //wezonefragment 나오고 바탕화면 dim 처리, fab등장 메소드
    public void dimbackgroundfab(float slideOffset) {
        //슬라이딩레이아웃의 메뉴 wezone tab을 클릭하면서 슬라이딩 레이아웃이 위로 올라올 때
        //아니면 fab GONE
        if (mLayout != null) {
            if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
                if (PagePoint.equals(WEZONE_CLICK)) {
                    relativelayout_main_tab.setVisibility(View.VISIBLE);
                } else {
                    relativelayout_main_tab.setVisibility(View.GONE);
                }
            } else {
                if (slideOffset >= SLIDING_OFSSET_DIM) {
                    if (PagePoint.equals(WEZONE_CLICK)) {
                        relativelayout_main_tab.setVisibility(View.VISIBLE);
                    } else {
                        LinearLayout_main_tab_bg.setVisibility(View.GONE);
                        relativelayout_main_tab.setVisibility(View.GONE);
                    }
                } else {
                    LinearLayout_main_tab_bg.setVisibility(View.GONE);
                    relativelayout_main_tab.setVisibility(View.GONE);
                }
            }
        }
    }

    public void showBeaconNotice() {
        int itemCnt = mBeaconNotiList.size();
        if (itemCnt > 0) {

            String noticeMsg = mBeaconNotiList.get(itemCnt - 1);
            textview_beacon_noti.setText(noticeMsg);

            textview_beacon_noti_time.setText(LibDateUtil.getCurrentTime("yyyy/MM/dd HH:mm"));
            linearlayout_noti_area.setVisibility(View.VISIBLE);

            String notiImgUrl = mBeaconNotiImgList.get(itemCnt - 1);

            showImageFromRemoteWithCircle(notiImgUrl,R.drawable.im_bunny_photo,imageview_beacon_noti);

            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

            UIViewAnimation.ShakeAnimation(getApplicationContext(), linearlayout_noti_area);

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mPushData = (Data_PushData) intent.getSerializableExtra(Define.INTENTKEY_PUSH_VALUE);
        if (mPushData != null) {
            moveActivityWithPushData(mPushData.type, mPushData.kind, mPushData.item_id);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        reloadTheme();
        getBeaconList();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
            Intent serviceIntent = new Intent(this, BluetoothLeService.class);
            startService(serviceIntent);
        }else{
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Define.INTENT_RESULT_REQUEST_ENABLE_BLUETOOTH);
        }

        if (WezoneUtil.isEmptyStr(getShare().getMyInfo().img_url) == false) {
            showImageFromRemote(getShare().getMyInfo().img_url, R.drawable.im_bunny_photo, imageview_photo);
        } else {
            imageview_photo.setImageResource(R.drawable.im_bunny_photo);
        }
        textview_name.setText(getShare().getMyInfo().user_name);
            //provider_email == null or "" 일 때
        if(WezoneUtil.isEmptyStr(getShare().getMyInfo().provider_email)){
            if(!WezoneUtil.isEmptyStr(getShare().getLoginParam().provider_type)) {
                if (getShare().getLoginParam().provider_type.equals(FACEBOOK)) {
                    //페이스북 계정일 때.
                    textview_email.setText(FACEBOOK_EMAIL);
                } else if (getShare().getLoginParam().provider_type.equals(KAKAOTALK)) {
                    //카카오톡 계정일 때.
                    textview_email.setText(KAKAOTALK_EMAIL);
                } else {
                    //구글
                }
            }
        }else{
            //비어있지 않을 떄
            textview_email.setText(getShare().getMyInfo().provider_email);
        }

        textview_people.setText("내 친구 " + getShare().getFriendCnt());
        textview_noti_cnt.setText(String.valueOf(getShare().getTotalUnReadCount()));
//        getThemeList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Define.INTENT_RESULT_BUNNYZONE:
                if (resultCode == RESULT_OK) {
                    mZonePagerAdapter = new ZonePagerAdapter(getSupportFragmentManager(), MainActivity.this);
                    fragementPager.setAdapter(mZonePagerAdapter);

                    fragementPager.setCurrentItem(0);
                } else {

                }
                break;
            case Define.INTENT_RESULT_WEZONE:
                if (resultCode == RESULT_OK) {
//                    fragementPager.setAdapter(mZonePagerAdapter);
                    //리로드 시
                    String mput_Type = data.getStringExtra(WEZONE_WEZONE_PUT_TYPE);

                    Data_WeZone mWezone_edit = (Data_WeZone) data.getSerializableExtra(WEZONE_WEZONE_PUT);
                    if (mWezone_edit != null) {
                        if (DELETE.equals(mput_Type)) {
                            if(mWezone_edit.manage_type != Define.TYPE_DELETE){
                                mWezone_edit.manage_type = Define.TYPE_DELETE;
                            }
                            WezoneFragment mWezoneFragment = (WezoneFragment) mZonePagerAdapter.getItem(1);
                            mWezoneFragment.editWezoneData(mWezone_edit, DELETE);
                        } else if (PUT.equals(mput_Type)) {
                            WezoneFragment mWezoneFragment = (WezoneFragment) mZonePagerAdapter.getItem(1);
                            mWezoneFragment.editWezoneData(mWezone_edit, PUT);
                        } else if (POST.equals(mput_Type)) {
                            WezoneFragment mWezoneFragment = (WezoneFragment) mZonePagerAdapter.getItem(1);
                            mWezoneFragment.editWezoneData(mWezone_edit, POST);
                        } else if (ENTRY.equals(mput_Type)) {
                            WezoneFragment mWezoneFragment = (WezoneFragment) mZonePagerAdapter.getItem(1);
                            mWezoneFragment.editWezoneData(mWezone_edit, ENTRY);
                        } else if (ENTRY_WAIT.equals(mput_Type)) {
                            WezoneFragment mWezoneFragment = (WezoneFragment) mZonePagerAdapter.getItem(1);
                            mWezoneFragment.editWezoneData(mWezone_edit, ENTRY_WAIT);
                        } else if (REMOVE.equals(mput_Type)) {
                            WezoneFragment mWezoneFragment = (WezoneFragment) mZonePagerAdapter.getItem(1);
                            mWezoneFragment.editWezoneData(mWezone_edit, REMOVE);
                        } else if (REMOVE.equals(mput_Type)) {
                            WezoneFragment mWezoneFragment = (WezoneFragment) mZonePagerAdapter.getItem(1);
                            mWezoneFragment.editWezoneData(mWezone_edit, REMOVE);
                        }
                    }
                }
                break;

            case Define.INTENT_RESULT_THEME: {
                ThemeZoneFragment mThemeZoneFragment = (ThemeZoneFragment) mZonePagerAdapter.getItem(0);
                mThemeZoneFragment.getMyThemeList();
            }
            break;

            case Define.INTENT_RESULT_REQUEST_ENABLE_BLUETOOTH:{
                if (resultCode == RESULT_OK) {
                    // 확인 눌렀을 때
                    //Next Step
                    Intent serviceIntent = new Intent(this, BluetoothLeService.class);
                    startService(serviceIntent);
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, Define.INTENT_RESULT_REQUEST_ENABLE_BLUETOOTH);
                }
            }
                break;

        }
    }

    public void setBeaconListWithMode() {

        if (mSearchMode == SEARCH_MODE_ALL) {
            mMainBeaconListAdapter = new MainBeaconListAdapter(MainActivity.this, mBeaconList);
            textview_beacon_cnt.setText(" " + mBeaconList.size());
        } else {
            mMainBeaconListAdapter = new MainBeaconListAdapter(MainActivity.this, mMyBeaconList);
            textview_beacon_cnt.setText(" " + mMyBeaconList.size());
        }
        mbeacon_listview.setAdapter(mMainBeaconListAdapter);
    }

    public void reloadTheme() {
        mCurrentTheme = CryptPreferences.getInt(MainActivity.this, Define.WEZONE_THEME, Define.THEME_BLUE);
        if (mCurrentTheme == Define.THEME_BLUE) {

            rippleArcView.setBackgroundResource(R.drawable.im_scan_bg);

            fab_btn_add.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFCE00")));

            imageview_btn_beacon_add.setBackgroundResource(R.drawable.circle_for_blue);
        } else if (mCurrentTheme == Define.THEME_RED) {

            rippleArcView.setBackgroundResource(R.drawable.im_scan_bg_red);

            fab_btn_add.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F0F2F7")));
            imageview_btn_beacon_add.setBackgroundResource(R.drawable.circle_for_red);
        } else {

            rippleArcView.setBackgroundResource(R.drawable.im_scan_bg_yellow);

            fab_btn_add.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0C9BEA")));
            imageview_btn_beacon_add.setBackgroundResource(R.drawable.circle_for_yellow);
        }
    }


    public void getBeaconList() {
//        showProgressPopup();
        Call<Rev_BeaconList> beaconListCall = wezoneRestful.getBeaconList();
        beaconListCall.enqueue(new Callback<Rev_BeaconList>() {
            @Override
            public void onResponse(Call<Rev_BeaconList> call, Response<Rev_BeaconList> response) {
                Rev_BeaconList bl = response.body();
                if (isNetSuccess(bl)) {
                    mBeaconList.clear();
                    mMyBeaconList.clear();

                    for (Data_Beacon beacon : bl.list) {
                        if (WezoneUtil.isEmptyStr(beacon.theme_id)) {
                            mBeaconList.add(beacon);
                        }
                    }

                    randomImageView.removeKeyWordAll();

                    if(mBeaconList.size() > 0){
                        textview_beacon_cnt.setText(" " + mBeaconList.size());

                        for (Data_Beacon beacon : mBeaconList) {
                            if (getUuid().equals(beacon.uuid)) {
                                mMyBeaconList.add(beacon);
                            }
                        }

                        mMainBeaconListAdapter.notifyDataSetChanged();
                        fab_btn_add.setVisibility(View.VISIBLE);
                        linearlayout_no_result_area.setVisibility(View.GONE);
                    }else{
                        textview_beacon_cnt.setText(" " + mBeaconList.size());
                        fab_btn_add.setVisibility(View.GONE);
                        linearlayout_no_result_area.setVisibility(View.VISIBLE);
                    }
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_BeaconList> call, Throwable t) {
                hidePorgressPopup();
            }
        });

    }


    public void getAdvertData() {

        Call<Rev_Advert> advert = wezoneRestful.getAdvert();
        advert.enqueue(new Callback<Rev_Advert>() {
            @Override
            public void onResponse(Call<Rev_Advert> call, Response<Rev_Advert> response) {
                Rev_Advert advert = response.body();
                if (isNetSuccess(advert)) {
                    mAdvert = advert;

                    if (WezoneUtil.isEmptyStr(advert.main_adtext)) {
                        marqueeTextView.setText(getShare().getMyInfo().status_message);
                    } else {
                        marqueeTextView.setText(mAdvert.main_adtext);
                    }

                    if (!WezoneUtil.isEmptyStr(advert.advert_data)) {
                        showImageFromRemote(advert.advert_data, R.drawable.im_event, imageview_event);
                    }

                    if("F".equals(advert.market_flag)){
                        linearlayout_btn_beacon_market.setVisibility(View.GONE);
                        linearlayout_store.setVisibility(View.GONE);
                    }else{
                        linearlayout_btn_beacon_market.setVisibility(View.VISIBLE);
                        linearlayout_store.setVisibility(View.VISIBLE);
                    }

                    if (!WezoneUtil.isEmptyStr(mAdvert.theme_id)) {
                        Call<Rev_Theme> themeCall = wezoneRestful.getMyThemeZone(mAdvert.theme_id);
                        themeCall.enqueue(new Callback<Rev_Theme>() {
                            @Override
                            public void onResponse(Call<Rev_Theme> call, Response<Rev_Theme> response) {
                                Rev_Theme theme = response.body();
                                if (isNetSuccess(theme)) {
                                    if (theme.themezone_info != null) {
                                        Toast.makeText(MainActivity.this, "테마존이 실행중입니다\n 테마존으로 이동합니다", Toast.LENGTH_SHORT).show();
                                        ThemeActivity.startActivity(MainActivity.this, theme.themezone_info, ThemeActivity.THEME_MODE_RUNNING);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Rev_Theme> call, Throwable t) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Rev_Advert> call, Throwable t) {

            }
        });
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mFlexibleSpaceImageHeight;
//        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(randomImageView, ScrollUtils.getFloat(-scrollY, -mFlexibleSpaceImageHeight, 0));
        ViewHelper.setTranslationY(rippleArcView, ScrollUtils.getFloat(-scrollY, -mFlexibleSpaceImageHeight, 0));
        ViewHelper.setTranslationY(linearlayout_text_area, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));

        ViewHelper.setTranslationY(beacon_pager_area, ScrollUtils.getFloat(-scrollY + mFlexibleSpaceImageHeight, 0, mFlexibleSpaceImageHeight));

        // Translate FAB
        int fabHeight = mFlexibleSpaceImageHeight - (mFlexibleSpaceImageHeight / 5);
        int maxFabTranslationY = fabHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + fabHeight - mFab.getHeight() / 2,
                0,
                maxFabTranslationY);

        ViewHelper.setTranslationX(mFab, (rippleArcView.getWidth() / 2) - (mFab.getWidth() / 2));
        ViewHelper.setTranslationY(mFab, fabTranslationY);


        ViewHelper.setTranslationY(marqueeTextView, fabTranslationY + mFab.getHeight() + 50);

        // Show/hide FAB
        if (fabTranslationY <= 0) {
            hideFab();
        } else {
            showFab();
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }


    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();

            ViewPropertyAnimator.animate(marqueeTextView).cancel();
            ViewPropertyAnimator.animate(marqueeTextView).alpha(1).setDuration(200).start();

            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();

            ViewPropertyAnimator.animate(marqueeTextView).cancel();
            ViewPropertyAnimator.animate(marqueeTextView).alpha(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }


    private static final int NUM_OF_ITEMS = 30;
    private static final int NUM_OF_ITEMS_FEW = 3;

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }

    public static ArrayList<String> getDummyData() {
        return getDummyData(NUM_OF_ITEMS);
    }

    public static ArrayList<String> getDummyData(int num) {
        ArrayList<String> items = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            items.add("Item " + i);
        }
        return items;
    }

    protected void setDummyData(GridView gridView) {
        setDummyData(gridView, NUM_OF_ITEMS);
    }

    protected void setDummyData(GridView gridView, int num) {
        gridView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getDummyData(num)));
    }

    private void toggleScanning() {
        if (!isScanning) startScanning();
        else stopScanning();
    }

    //
    private void startScanning() {
        mFab.setBackgroundResource(R.drawable.ic_scan_pause);
        rippleArcView.startRippleAnimation();
        isScanning = true;
    }

    private void stopScanning(){
        mFab.setBackgroundResource(getCurrentThemeScanBtnId());
        rippleArcView.stopRippleAnimation();
        isScanning = false;
    }

    public int getCurrentThemeScanBtnId(){
        if(mCurrentTheme == Define.THEME_BLUE){
            return R.drawable.btn_scan_blue;
        }else if(mCurrentTheme == Define.THEME_RED){
            return R.drawable.btn_scan_red;
        }else{
            return R.drawable.btn_scan_yellow;
        }
    }

    private void requestBluetooth() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.bluetooth_not_enabled))
                .setMessage(getString(R.string.please_enable_bluetooth))
                .setPositiveButton("블루투스 설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Initializing intent to go to bluetooth settings.
                        Intent bltSettingsIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(bltSettingsIntent);
                    }
                })
                .show();
    }

    private void requestLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
//            startScanning();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    startScanning();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothLeDeviceStore != null) {
            mBluetoothLeDeviceStore.destroy();
        }
    }

    // 메뉴 열기
    public void openSideMenu() {
        if (mDrawerLayout == null || mDrawerLayout.isDrawerOpen(mDrawer) == false)//&& mDrawerLayout.isDrawerVisible(mSidemenuLayout) == false )
            mDrawerLayout.openDrawer(mDrawer);
    }

    // 메뉴 닫기
    public boolean closeSideMenu() {

        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawer)) {
            mDrawerLayout.closeDrawer(mDrawer);
            return true;
        }
        return false;
    }

    public View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            switch (viewId) {
                case R.id.linearlayout_btn_theme_zone: {
                    fragementPager.setCurrentItem(0, true);
                }
                break;

                case R.id.linearlayout_btn_wezone: {
                    fragementPager.setCurrentItem(1, true);
                }
                break;

                case R.id.linearlayout_btn_mode: {
                    if (mSearchMode == SEARCH_MODE_ALL) {
                        mSearchMode = SEARCH_MODE_MINE;
                        textview_mode.setText("MY ");
                        randomImageView.removeKeyWordAll();
                    } else {
                        mSearchMode = SEARCH_MODE_ALL;
                        textview_mode.setText("ALL");
                        randomImageView.removeKeyWordAll();
                    }
                    setBeaconListWithMode();
                }

            }
        }
    };

    public View.OnClickListener mLeftMenuListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();
            switch (viewId) {

                case R.id.imageview_photo:
                    moveActivity(new Intent(MainActivity.this, MyProfileActivity.class));
                    break;

                case R.id.linearlayout_btn_beacon_market: {

                    String strUrl = Define.NETURL_BEACON_MARKET;
                    if (mAdvert != null && !WezoneUtil.isEmptyStr(mAdvert.market_url)) {
                        strUrl = mAdvert.market_url;
                    }
                    Activity_WebView.startWebViewActivity(MainActivity.this, strUrl, "WeCON 마켓");
                }
                break;

                case R.id.linearlayout_btn_auction:{
                    if (mAdvert != null && !WezoneUtil.isEmptyStr(mAdvert.market_auction)) {
                        Activity_WebView.startWebViewActivity(MainActivity.this, mAdvert.market_auction, "Auction");
                    }
                }
                break;

                case R.id.linearlayout_btn_gmarket:{
                    if (mAdvert != null && !WezoneUtil.isEmptyStr(mAdvert.market_gmarket)) {
                        Activity_WebView.startWebViewActivity(MainActivity.this, mAdvert.market_gmarket, "G 마켓");
                    }
                }
                break;

                case R.id.linearlayout_btn_nstore:{
                    if (mAdvert != null && !WezoneUtil.isEmptyStr(mAdvert.market_naver)) {
                        Activity_WebView.startWebViewActivity(MainActivity.this, mAdvert.market_naver, "N 스토어");
                    }
                }
                break;

                case R.id.linearlayout_btn_people:
                    moveActivity(new Intent(MainActivity.this, FriendListActivity.class));
                    break;

                case R.id.linearlayout_btn_setting:
                    moveActivity(new Intent(MainActivity.this, SettingActivity.class));
                    break;

                case R.id.linearlayout_btn_help:
                    moveActivity(new Intent(MainActivity.this, HelpActivity.class));
                    break;

                case R.id.linearlayout_btn_logout:
                    moveLoginActivity(null, true);
                    break;

                case R.id.linearlayout_btn_event: {
                    String strUrl = Define.NETURL_EVENT;
                    if (mAdvert != null && WezoneUtil.isEmptyStr(mAdvert.advert_data)) {
                        strUrl = mAdvert.advert_data;
                    }
                    Activity_WebView.startWebViewActivity(MainActivity.this, strUrl, "이벤트");
                }
                break;

            }
        }
    };

    @Override
    public void onBackPressed() {
        if(relativelayout_main_tab.getVisibility() == View.GONE) {
            //dim처리 됬을 때(흑백모드) Sliding 내리는 효과 막기
            if (mLayout != null &&
                    (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                return;
            }
        }
        if (closeSideMenu() == false) {
            if (isFinished) {
                super.onBackPressed();
            } else {
                isFinished = true;
                String str = getResources().getString(R.string.press_again_to_exit);
                Toast.makeText(m_Context, str, Toast.LENGTH_SHORT).show();
            }

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    isFinished = false;
                }
            }, 3000);
        }
    }


    @Override
    protected void resultBeaconScan(String action, BluetoothLeDevice device) {
        super.resultBeaconScan(action, device);

        mBluetoothLeDeviceStore.addDevice(getUuid(), device);

        if (mSearchMode == SEARCH_MODE_ALL) {
            if (mBeaconList != null) {

                for (BluetoothLeDevice ble : mBluetoothLeDeviceStore.getDeviceList()) {
                    for (int i = 0; i < mBeaconList.size(); i++) {

                        Data_Beacon beacon = mBeaconList.get(i);
                        if (WezoneUtil.isSameBeacon(ble,beacon) && WezoneUtil.isEmptyStr(beacon.theme_id)) {
                            double distance = BluetoothLeDevice.calculateDistance(ble.getVineBeacon().getPower(), ble.getLastRssi());
                            double b = Math.round(distance * 100d) / 100d;
                            mBeaconList.get(i).rssi = ble.getLastRssi();
                            mBeaconList.get(i).distance = b;
                            mBeaconList.get(i).interval = ble.getVineBeacon().getInterval();
                            ble.setDataBeacon(mBeaconList.get(i));
                            randomImageView.addKeyWord(ble);
                        }
                    }
                }
            }
        } else {
            if (mMyBeaconList != null) {
                for (BluetoothLeDevice ble : mBluetoothLeDeviceStore.getDeviceList()) {
                    for (int i = 0; i < mMyBeaconList.size(); i++) {
                        Data_Beacon beacon = mMyBeaconList.get(i);
                        if (WezoneUtil.isSameBeacon(ble, beacon) && WezoneUtil.isEmptyStr(beacon.theme_id)) {
                            double distance = BluetoothLeDevice.calculateDistance(ble.getVineBeacon().getPower(), ble.getLastRssi());
                            double b = Math.round(distance * 100d) / 100d;
                            mMyBeaconList.get(i).rssi = ble.getLastRssi();
                            mMyBeaconList.get(i).distance = b;
                            mMyBeaconList.get(i).interval = ble.getVineBeacon().getInterval();
                            ble.setDataBeacon(mMyBeaconList.get(i));
                            randomImageView.addKeyWord(ble);
                        }
                    }
                }
            }
        }

        if (!randomImageView.isShow) {
            randomImageView.show();
        }

        mMainBeaconListAdapter.notifyDataSetChanged();

        if (device.getDataBeacon() != null) {
            String name = device.getDataBeacon().name;
            if (BluetoothLeService.ACTION_IN_VINE_BEACON.equals(action)) {
                String msg = name + "이 나의 존에 접근했습니다";
                mBeaconNotiList.add(msg);
                mBeaconNotiImgList.add(device.getDataBeacon().img_url);
                showBeaconNotice();
                mLibSystemManager.startVibrator(1000);
            } else if (BluetoothLeService.ACTION_OUT_VINE_BEACON.equals(action)) {
                String msg = name + "이 나의 존에서 벗어났습니다";
                mBeaconNotiList.add(msg);
                mBeaconNotiImgList.add(device.getDataBeacon().img_url);
                showBeaconNotice();
                mLibSystemManager.startVibrator(1000);
            } else if (BluetoothLeService.ACTION_CLICK_SHORT_PRESS.equals(action)) {
                String msg = name + "의 버튼이 눌러졌습니다";
                mBeaconNotiList.add(msg);
                mBeaconNotiImgList.add(device.getDataBeacon().img_url);
                showBeaconNotice();
                mLibSystemManager.startVibrator(1000);
            } else if (BluetoothLeService.ACTION_CLICK_LONG_PRESS.equals(action)) {
                String msg = name + "의 롱 버튼이 눌러졌습니다";
                mBeaconNotiList.add(msg);
                mBeaconNotiImgList.add(device.getDataBeacon().img_url);
                showBeaconNotice();
                mLibSystemManager.startVibrator(1000);
            }

        }
    }

    @Override
    public void onXmppReceiveMessage(MassegeType type, String fromID, String sFrom, String other_user_uuid, String other_user_name, String other_user_img_url, String message, String msgKey) {
        super.onXmppReceiveMessage(type, fromID, sFrom, other_user_uuid, other_user_name, other_user_img_url, message, msgKey);

        textview_noti_cnt.setText(String.valueOf(getShare().getTotalUnReadCount()));
    }
}
