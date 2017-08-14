package com.vinetech.wezone.Wezone;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Hash;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Main.WezoneFindListAdapter;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_WezoneHashtag;
import com.vinetech.wezone.RevPacket.Rev_WezoneList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vinetech.wezone.Wezone.WezoneActivity.WEZONE_WEZONE_PUT;
import static com.vinetech.wezone.Wezone.WezoneActivity.WEZONE_WEZONE_PUT_TYPE;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 위존 검색 화면
 *
 */

public class WezoneSearchActivity extends BaseActivity implements AbsListView.OnScrollListener, AbsListView.OnItemClickListener {


    ArrayList<Data_Hash> mWezoneHashList;
    Rev_WezoneHashtag wezonehashtag;
    Data_WeZone mWezon;
    InputMethodManager imm;

    private static final String SEARCH_MODE_NORMAL = "1";
    private static final String HASH_CODE = "3";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";
    public static final String ENTRY = "ENTRY";
    public static final String ENTRY_WAIT = "ENTRY_WAIT";
    public static final String REMOVE = "REMOVE";

    public static void startActivityWithhash(BaseActivity activity, String hashcode) {
        Intent intent = new Intent(activity, WezoneSearchActivity.class);
        intent.putExtra(HASH_CODE, hashcode);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_WEZONE_HASH);
    }

    public static void startActivityWith(BaseActivity activity) {
        Intent intent = new Intent(activity, WezoneSearchActivity.class);
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_WEZONE);
    }

    public int mTotalCount = 0;
    public boolean mListViewLocked = true;
    public Data_WeZone mWezone;
    public ArrayList<Data_WeZone> mWezoneInfos;
    public enum Direction {UP, DOWN}
    public Direction direction;

    public ListView grid_view;
    private LinearLayout linearlayout_btn_hash;
    private LinearLayout linearlayout_btn_search;
    private LinearLayout linearlayout_hash_btn_area;
    private LinearLayout linearlayout_bg_noresult;
    private ImageView ImageView_wezone_gps_check;
    private EditText edit_wezone_search;
    private int index;
    private int down_value = 0;

    public WezoneFindListAdapter mWezoneListAdapter;

    private GpsInfo mGpsInfo;

    private boolean mHasRequestedMore;
    boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    private String longitude;
    private String latitude;
    private String mPut_Type;
    private String keyword;

    int mOffset = 0;
    int mLimit = 10;

    private String hashcode;
    private String mType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wezone_search);

        setHeaderView(R.drawable.btn_back_white, "Wezone 검색", 0);
        hashcode = getIntent().getStringExtra(HASH_CODE);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        linearlayout_btn_hash = (LinearLayout) findViewById(R.id.linearlayout_btn_hash);
        linearlayout_btn_hash.setOnClickListener(mClickListenerButton);

        wezonehashtag = new Rev_WezoneHashtag();

        mWezoneHashList = new ArrayList<>();

        mWezoneInfos = new ArrayList<>();
        mWezoneListAdapter = new WezoneFindListAdapter(WezoneSearchActivity.this, mWezoneInfos, Define.WEZONE_LIST_NEAR_TYPE);
        edit_wezone_search = (EditText) findViewById(R.id.edit_wezone_search);
        linearlayout_btn_search = (LinearLayout) findViewById(R.id.linearlayout_btn_search);
        linearlayout_btn_search.setOnClickListener(mClickListenerButton);
        linearlayout_bg_noresult = (LinearLayout) findViewById(R.id.linearlayout_bg_noresult);
        ImageView_wezone_gps_check = (ImageView) findViewById(R.id.ImageView_wezone_gps_check);

        grid_view = (ListView) findViewById(R.id.grid_view);
        grid_view.setAdapter(mWezoneListAdapter);
        grid_view.setOnScrollListener(this);
        grid_view.setOnItemClickListener(this);

        Call<Rev_WezoneHashtag> wezoneHashtagListCall = wezoneRestful.getWezoneHashtag();
        wezoneHashtagListCall.enqueue(new Callback<Rev_WezoneHashtag>() {
            @Override
            public void onResponse(Call<Rev_WezoneHashtag> call, Response<Rev_WezoneHashtag> response) {
                Rev_WezoneHashtag resultData = response.body();
                if (isNetSuccess(resultData)) {
                    if (resultData.list != null) {
                        mWezoneHashList.addAll(resultData.list);
                        onHashtags();
                    }
                }
            }
            @Override
            public void onFailure(Call<Rev_WezoneHashtag> call, Throwable t) {

            }
        });

        edit_wezone_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Enter키눌렀을떄 처리
                    keyword = edit_wezone_search.getText().toString().trim();
                    if (WezoneUtil.isEmptyStr(keyword)) {
                        keyword = null;
                        hashcode = null;
                    }
                    imm.hideSoftInputFromWindow(linearlayout_btn_search.getWindowToken(), 0);
                    mOffset = 0;
                    mLimit = 10;
                    direction = Direction.UP;
                    if(keyword != null) {
                        if (keyword.contains("#")) {
                            hashcode = keyword;
                        }else{
                            hashcode = null;
                        }
                    }else{
                        hashcode = null;
                    }
                    gps(keyword, hashcode);
                    return true;
                }
                return false;
            }
        });
        mOffset = 0;
        mLimit = 10;
        direction = Direction.UP;
        gps(keyword, hashcode);
    }


    public void gps(String keyword, String hashcode) {
        mGpsInfo = new GpsInfo(WezoneSearchActivity.this);
        // GPS 사용유무 가져오기
        if (mGpsInfo.isGetLocation()) {
            longitude = String.valueOf(mGpsInfo.getLongitude());
            latitude = String.valueOf(mGpsInfo.getLatitude());

            if (hashcode != null) {

                getWezoneSearchList(true, longitude, latitude, mOffset, mLimit, SEARCH_MODE_NORMAL,hashcode);
                edit_wezone_search.setText(hashcode);
                edit_wezone_search.setSelection(edit_wezone_search.getText().length());
            } else {
                if (keyword != null) {
                    getWezoneSearchList(true, longitude, latitude, mOffset, mLimit, SEARCH_MODE_NORMAL, keyword);
                } else {
                    getWezoneSearchList(true, longitude, latitude, mOffset, mLimit, SEARCH_MODE_NORMAL, "");
                }
            }

        } else {
            // GPS 를 사용할수 없으므로
            if (hashcode != null) {
                getWezoneSearchListNOTGPS(true, mOffset, mLimit, SEARCH_MODE_NORMAL,hashcode);
                edit_wezone_search.setText(hashcode);
                edit_wezone_search.setSelection(edit_wezone_search.getText().length());
            } else {
                if (keyword != null) {
                    getWezoneSearchListNOTGPS(true, mOffset, mLimit, SEARCH_MODE_NORMAL, keyword);
                } else {
                    getWezoneSearchListNOTGPS(true, mOffset, mLimit, SEARCH_MODE_NORMAL, "");
                }
            }
        }
    }

    public void onHashtags() {
        linearlayout_hash_btn_area = (LinearLayout) findViewById(R.id.linearlayout_hash_btn_area);
        for (index = 0; index < mWezoneHashList.size(); index++) {
            LinearLayout linear = new LinearLayout(this);
            linear.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams parambtn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            Button button = new Button(this);
            button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            button.setId(index);
            button.setBackground(getResources().getDrawable(R.drawable.button_hashtag_blue));
            button.setMinWidth(1);
            button.setMinHeight(1);
            button.setMinimumWidth(1);
            button.setMinimumHeight(1);
            button.setPadding(60, 25, 60, 25);
            button.setText("# " + mWezoneHashList.get(index).hashtag);
            linear.setId(index);
            linear.addView(button, parambtn);
            parambtn.setMargins(20, 20, 20, 20);
            button.setOnClickListener(mClickListener);
            linearlayout_hash_btn_area.addView(linear, index);
        }
    }

    //GPS 정보가 없을 때 검색 전문
    public void getWezoneSearchListNOTGPS(boolean isProgress, final int offset, int limit, String search_type, final String keyword) {
        if (isProgress) {
            showProgressPopup();
        }
        Call<Rev_WezoneList> wezoneListCall = wezoneRestful.getWezoneSearchList(String.valueOf(offset), String.valueOf(limit), search_type, keyword);
        wezoneListCall.enqueue(new Callback<Rev_WezoneList>() {
            @Override
            public void onResponse(Call<Rev_WezoneList> call, Response<Rev_WezoneList> response) {
                Rev_WezoneList resultData = response.body();
                if (isNetSuccess(resultData)) {
                    if (1 != down_value) {
                        mWezoneInfos.clear();
                    } else {
                        down_value = 0;
                    }
                    mTotalCount = Integer.valueOf(resultData.total_count);
                    edit_wezone_search.setText(keyword);
                    edit_wezone_search.setSelection(edit_wezone_search.getText().length());

                    if (resultData.list != null) {
                        if (direction == Direction.UP) {
                            mWezoneInfos.clear();

                            if (resultData.list.size() == 0) {
                                linearlayout_bg_noresult.setVisibility(View.VISIBLE);
                            } else {
                                linearlayout_bg_noresult.setVisibility(View.GONE);
                                mWezoneInfos.addAll(resultData.list);
                            }
                        } else {
                            mListViewLocked = true;
                            if (resultData.list.size() == 0) {
                                linearlayout_bg_noresult.setVisibility(View.VISIBLE);
                            } else {
                                linearlayout_bg_noresult.setVisibility(View.GONE);
                                mWezoneInfos.addAll(resultData.list);
                            }
                        }
                        mWezoneListAdapter.notifyDataSetChanged();
                    } else {
                        linearlayout_bg_noresult.setVisibility(View.VISIBLE);
                    }
                    hidePorgressPopup();
                }
            }
            @Override
            public void onFailure(Call<Rev_WezoneList> call, Throwable t) {

            }
        });

    }

    //GPS 정보가 있을 때 검색 전문
    public void getWezoneSearchList(boolean isProgress, String longitude, String latitude, final int offset, int limit, String search_type, final String keyword) {
        if (isProgress) {
            showProgressPopup();
        }
        Call<Rev_WezoneList> wezoneListCall = wezoneRestful.getWezoneSearchList(longitude, latitude, String.valueOf(offset), String.valueOf(limit), search_type, keyword);
        wezoneListCall.enqueue(new Callback<Rev_WezoneList>() {
            @Override
            public void onResponse(Call<Rev_WezoneList> call, Response<Rev_WezoneList> response) {
                Rev_WezoneList resultData = response.body();
                if (isNetSuccess(resultData)) {
                    if (1 != down_value) {
                        mWezoneInfos.clear();
                    } else {
                        down_value = 0;
                    }
                    mTotalCount = Integer.valueOf(resultData.total_count);
                    edit_wezone_search.setText(keyword);
                    edit_wezone_search.setSelection(edit_wezone_search.getText().length());

                    if (resultData.list != null) {
                        if (direction == Direction.UP) {
                            mWezoneInfos.clear();
                            if (resultData.list.size() == 0) {
                                linearlayout_bg_noresult.setVisibility(View.VISIBLE);
                            } else {
                                linearlayout_bg_noresult.setVisibility(View.GONE);
                                mWezoneInfos.addAll(resultData.list);
                            }
                        } else {
                            mListViewLocked = true;
                            if (resultData.list.size() == 0) {
                                linearlayout_bg_noresult.setVisibility(View.VISIBLE);
                            } else {
                                linearlayout_bg_noresult.setVisibility(View.GONE);
                                mWezoneInfos.addAll(resultData.list);
                            }
                        }
                        mWezoneListAdapter.notifyDataSetChanged();
                    } else {
                        linearlayout_bg_noresult.setVisibility(View.VISIBLE);
                    }
                    hidePorgressPopup();
                }
            }
            @Override
            public void onFailure(Call<Rev_WezoneList> call, Throwable t) {

            }
        });

    }


    public View.OnClickListener mClickListenerButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.linearlayout_btn_search:
                    String keyword = edit_wezone_search.getText().toString().trim();
                    if (WezoneUtil.isEmptyStr(keyword)) {
                        keyword = null;
                        hashcode = null;
                    }
                    imm.hideSoftInputFromWindow(linearlayout_btn_search.getWindowToken(), 0);
                    mOffset = 0;
                    mLimit = 10;
                    if(keyword != null) {
                        if (keyword.contains("#")) {
                            hashcode = keyword;
                        }else{
                            hashcode = null;
                        }
                    }else{
                        hashcode = null;
                    }
                    gps(keyword, hashcode);
                    break;
                case R.id.linearlayout_btn_hash:
                    edit_wezone_search.setText(edit_wezone_search.getText().toString() + "#");
                    edit_wezone_search.setSelection(edit_wezone_search.getText().length());
                    break;
            }
        }
    };

    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            edit_wezone_search.setText("#" + mWezoneHashList.get(id).hashtag);
            edit_wezone_search.setSelection(edit_wezone_search.getText().length());
            String keyword = edit_wezone_search.getText().toString().trim();
            mOffset = 0;
            mLimit = 10;
            if (keyword.contains("#")) {
                hashcode = keyword;
            }
            gps(keyword, hashcode);
        }
    };

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (!mHasRequestedMore) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if (lastInScreen >= totalItemCount) {
                mHasRequestedMore = true;
            }
        }
        lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
        //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag && mListViewLocked) {
            //TODO 화면이 바닦에 닿을때 처리
            if (mWezoneInfos.size() < mTotalCount) {
                mOffset = mWezoneInfos.size();
                direction = Direction.DOWN;
                gps(keyword, hashcode);
                down_value = 1;
                mListViewLocked = false;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Data_WeZone info = mWezoneInfos.get(position);
        WezoneActivity.startActivit(WezoneSearchActivity.this, info, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Define.INTENT_RESULT_WEZONE:
                if (resultCode == RESULT_OK) {
                    mWezone = (Data_WeZone) data.getSerializableExtra(WEZONE_WEZONE_PUT);
                    mPut_Type = data.getStringExtra(WEZONE_WEZONE_PUT_TYPE);

                    if (DELETE.equals(mPut_Type)) {    //탈퇴
                        mWezone.manage_type = Define.TYPE_DELETE;
                        Intent i = new Intent();
                        i.putExtra(WEZONE_WEZONE_PUT, mWezone);
                        i.putExtra(WEZONE_WEZONE_PUT_TYPE, DELETE);
                        setResult(RESULT_OK, i);
                    } else if (PUT.equals(mPut_Type)) {  //수정
                        Intent i = new Intent();
                        i.putExtra(WEZONE_WEZONE_PUT, mWezone);
                        i.putExtra(WEZONE_WEZONE_PUT_TYPE, PUT);
                        setResult(RESULT_OK, i);
                    } else if (ENTRY.equals(mPut_Type)) { //입장
                        Intent i = new Intent();
                        i.putExtra(WEZONE_WEZONE_PUT, mWezone);
                        i.putExtra(WEZONE_WEZONE_PUT_TYPE, ENTRY);
                        setResult(RESULT_OK, i);
                    } else if (ENTRY_WAIT.equals(mPut_Type)) { //입장대기
                        Intent i = new Intent();
                        i.putExtra(WEZONE_WEZONE_PUT, mWezone);
                        i.putExtra(WEZONE_WEZONE_PUT_TYPE, ENTRY_WAIT);
                        setResult(RESULT_OK, i);
                    } else if (REMOVE.equals(mPut_Type)) { //삭제
                        mType = REMOVE;
                        Intent i = new Intent();
                        i.putExtra(WEZONE_WEZONE_PUT, mWezone);
                        i.putExtra(WEZONE_WEZONE_PUT_TYPE, REMOVE);
                        setResult(RESULT_OK, i);
                    }

                    if(mWezone != null){
                        if(mWezone.wezone_id != null){
                            setmWezoneList(mWezone);
                        }
                    }
                    break;
                }
        }

    }

    public void setmWezoneList(Data_WeZone weZone) {
        int idx = getWezoneIdx(weZone.wezone_id);
        if (idx != -1) {
            if (REMOVE.equals(mType)) {
                mWezoneInfos.remove(idx);
                mWezoneListAdapter.notifyDataSetChanged();
            } else {
                mWezoneInfos.remove(idx);
                mWezoneInfos.add(idx, weZone);
                mWezoneListAdapter.notifyDataSetChanged();
            }
        }
    }

    public int getWezoneIdx(String wezoneId) {
        int idx = -1;
        for (int i = 0; i < mWezoneInfos.size(); i++) {
            if (wezoneId.equals(mWezoneInfos.get(i).wezone_id)) {
                idx = i;
            }
        }
        return idx;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGpsInfo = new GpsInfo(WezoneSearchActivity.this);
        // GPS 사용유무 가져오기
        if (mGpsInfo.isGetLocation()) {
            ImageView_wezone_gps_check.setVisibility(View.GONE);

        } else {
            // GPS 를 사용할수 없으므로
//            mGpsInfo.showSettingsAlert();
            ImageView_wezone_gps_check.setVisibility(View.VISIBLE);
        }
    }
}


