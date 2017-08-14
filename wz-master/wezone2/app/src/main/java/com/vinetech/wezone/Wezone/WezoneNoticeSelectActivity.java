package com.vinetech.wezone.Wezone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_ActionItem;
import com.vinetech.wezone.Data.Data_Board;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_BoardList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WezoneNoticeSelectActivity extends BaseActivity {

    public static final String NOTICE_TITLE = "title";
    public static final String NOTICE_ACTION_ITEM = "item";
    public static final String ON = "ON";
    public static final String OFF = "OFF";
    public static final String NOTICE_FLAG = "T";

    public static void startActivityWithRegist(BaseActivity activity, String title, Data_WeZone weZone) {
        Intent intent = new Intent(activity, WezoneNoticeSelectActivity.class);
        intent.putExtra(NOTICE_TITLE, title);
        if (weZone != null) {
            intent.putExtra(NOTICE_ACTION_ITEM, weZone);
        }
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_WEZONE_IN);

    }

    Data_WeZone mWezone;
    Data_Board selectedBoard;

    private WezoneNoticeSelectListAdapter mLocalUserSelectListAdapter;

    private ArrayList<Data_Board> mNoticeList;

    private TextView textview_title;
    private LinearLayout relativelayout_wezone_notice;
    private LinearLayout linearlayout_title_area;

    private ListView listview;

    private String mOnOff;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wezone_notice_select);

        setHeaderView(R.drawable.btn_back_white, "알림", R.drawable.btn_check);

        mTitle = getIntent().getStringExtra(NOTICE_TITLE);
        mWezone = (Data_WeZone) getIntent().getSerializableExtra(NOTICE_ACTION_ITEM);

        relativelayout_wezone_notice = (LinearLayout) findViewById(R.id.relativelayout_wezone_notice);
        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_title.setText(mTitle);

        listview = (ListView) findViewById(R.id.listview_notice);
        mNoticeList = new ArrayList<>();

        if (mNoticeList == null) {
            relativelayout_wezone_notice.setVisibility(View.VISIBLE);
        }


        linearlayout_title_area = (LinearLayout) findViewById(R.id.linearlayout_title_area);
        linearlayout_title_area.setOnClickListener(mClickListener);

        mLocalUserSelectListAdapter = new WezoneNoticeSelectListAdapter(WezoneNoticeSelectActivity.this, mNoticeList, mOnOff);
        listview.setAdapter(mLocalUserSelectListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (getSelectedItemCount() >= 1) {
                    mNoticeList.get(position).isSelected = false;
                    mNoticeList.get(position).isSelectedname = false;
                    mLocalUserSelectListAdapter.notifyDataSetChanged();
                    return;
                }

                mNoticeList.get(position).isSelected = !mNoticeList.get(position).isSelected;
                mNoticeList.get(position).isSelectedname = !mNoticeList.get(position).isSelectedname;
                selectedBoard = mNoticeList.get(position);
                mLocalUserSelectListAdapter.notifyDataSetChanged();

            }
        });
        relativelayout_wezone_notice.setVisibility(View.VISIBLE);
        linearlayout_title_area.setSelected(true);
        mOnOff = ON;
        getBoardData();

        //선택한 공지글이 없어도 사용함으로 되어있을 때
        if (mWezone != null) {
            if (mWezone.zone_in != null) {
                if (mWezone.zone_in.id.equals(Data_ActionItem.ID_NOTIC)) {
                    linearlayout_title_area.setSelected(true);
                    mOnOff = ON;
                    if (mWezone.zone_in.data.size() == 0) {
                        relativelayout_wezone_notice.setVisibility(View.VISIBLE);
                    } else {
                        if (mWezone.zone_in.data.get(0).key == null) {
                            relativelayout_wezone_notice.setVisibility(View.VISIBLE);
                        } else {
                            relativelayout_wezone_notice.setVisibility(View.GONE);
                        }
                    }
                } else {
                    linearlayout_title_area.setSelected(false);
                    mOnOff = OFF;
                }
            }
        }
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId) {
                case R.id.linearlayout_title_area:
                    if (linearlayout_title_area.isSelected()) {
                        linearlayout_title_area.setSelected(false);
                        mOnOff = OFF;
                        mLocalUserSelectListAdapter = new WezoneNoticeSelectListAdapter(WezoneNoticeSelectActivity.this, mNoticeList, mOnOff);
                        listview.setAdapter(mLocalUserSelectListAdapter);
                    } else {
                        linearlayout_title_area.setSelected(true);
                        mOnOff = ON;
                        mLocalUserSelectListAdapter = new WezoneNoticeSelectListAdapter(WezoneNoticeSelectActivity.this, mNoticeList, mOnOff);
                        listview.setAdapter(mLocalUserSelectListAdapter);
                    }
                    break;
            }
        }
    };

    //wezone/board/ 전문

    public void getBoardData() {
        Call<Rev_BoardList> boardListCall = wezoneRestful.getBoardList(mWezone.wezone_id, NOTICE_FLAG);
        boardListCall.enqueue(new Callback<Rev_BoardList>() {
            @Override
            public void onResponse(Call<Rev_BoardList> call, Response<Rev_BoardList> response) {
                Rev_BoardList boardList = response.body();
                if (isNetSuccess(boardList)) {
                    mNoticeList.addAll(boardList.list);
                    if (mNoticeList.size() > 0) {
                        compareList();
                        mLocalUserSelectListAdapter = new WezoneNoticeSelectListAdapter(WezoneNoticeSelectActivity.this, mNoticeList, mOnOff);
                        listview.setAdapter(mLocalUserSelectListAdapter);
                        relativelayout_wezone_notice.setVisibility(View.GONE);
                    } else {
                        relativelayout_wezone_notice.setVisibility(View.VISIBLE);
                    }
                } else {
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_BoardList> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }

    //리스트 리로드
    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);
        Intent i = new Intent();

        //선택된 공지글 상관없이 스위치로 파악
        if (mOnOff.equals(OFF)) {
            //getSelectedItems = null 인 상태로 보낼 수 있음 WezoneManagerActivity에서 걸러 내야함.
            i.putExtra(Define.INTENTKEY_NOTICE_VALUE, getSelectedItems());
            i.putExtra(Define.INTENTKEY_NOTICE_OFF, mOnOff);
        } else {
            //조건이 mOnOff = ON; 일때
            //getSelectedItems = null 인 상태로 보낼 수 있음
            i.putExtra(Define.INTENTKEY_NOTICE_VALUE, getSelectedItems());
            i.putExtra(Define.INTENTKEY_NOTICE_OFF, mOnOff);
        }
        //getSelectedItemCount() =null 인 상태로 갈 수 있음. 선택된게 없이 스위치 ON이면
        i.putExtra(Define.INTENTKEY_NOTICE_COUNT_VALUE, getSelectedItemCount());
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onClickLeftBtn(View v) {
        super.onClickLeftBtn(v);
        finish();
    }

    public ArrayList<Data_Board> getSelectedItems() {
        ArrayList<Data_Board> selectedItemList = new ArrayList<>();
        for (int i = 0; i < mNoticeList.size(); i++) {
            if (mNoticeList.get(i).isSelected) {
                selectedItemList.add(mNoticeList.get(i));
            }
        }
        return selectedItemList;
    }

    public int getSelectedItemCount() {
        int cnt = 0;
        for (int i = 0; i < mNoticeList.size(); i++) {
            if (mNoticeList.get(i).isSelected) {
                cnt++;
            }
        }
        return cnt;
    }

    public void compareList() {
        if (mWezone != null) {
            for (int i = 0; i < mWezone.zone_in.data.size(); i++) {
                for (int j = 0; j < mNoticeList.size(); j++) {
                    if (mWezone.zone_in.data.get(i).value != null) {
                        if (mNoticeList.get(j).board_id.equals(mWezone.zone_in.data.get(i).value)) {
                            mNoticeList.get(j).isSelectedname = true;
                            mNoticeList.get(j).isSelected = true;
                            linearlayout_title_area.setSelected(true);
                            mOnOff = ON;
                        }
                    }
                }
            }
        }
    }
}


