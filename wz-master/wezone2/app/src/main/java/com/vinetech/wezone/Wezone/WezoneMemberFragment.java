/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vinetech.wezone.Wezone;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_ZoneMemberList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class WezoneMemberFragment extends WezoneBaseFragment<ObservableListView> implements AbsListView.OnScrollListener {

    ObservableListView listView;
    Dialog mDialog;

    public boolean mListViewLocked = true;

    public static final String WEZONE_WEZONE_PUT = "wezone_put";
    public static final String WEZONE_WEZONE_PUT_TYPE = "WEZONE_WEZONE_PUT_TYPE";
    public static final String REMOVE = "REMOVE";
    public static final String FRIEND_PULS = "FRIEND_PULS";
    public Direction direction;
    public View mNoResultBottom;
    public int mTotalCount = 0;

    public enum Direction {UP, DOWN}

    private boolean mHasRequestedMore;

    boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    private LinearLayout linearlayout_btn_ok;
    private Data_WeZone mWezone;
    private BaseActivity mActivity;

    private int down_value = 0;

    private ArrayList<Data_Zone_Member> mMemberList;
    private WeZoneMemberListAdapter mWeZoneMemberListAdapter;
    private GpsInfo mGpsInfo;

    private int mOffset = 0;
    private int mLimit = 10;
    private double longitude;
    private double latitude;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = (BaseActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_flexiblespacewithimagelistview, container, false);

        linearlayout_btn_ok = (LinearLayout) view.findViewById(R.id.linearlayout_btn_ok);

        listView = (ObservableListView) view.findViewById(R.id.scroll);
        // Set padding view for ListView. This is the flexible space.
        View paddingView = new View(getActivity());
        final int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_wezone_header) + getActionBarSize();
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                flexibleSpaceImageHeight);
        paddingView.setLayoutParams(lp);

        // This is required to disable header's list selector effect
        paddingView.setClickable(true);

        mNoResultBottom = inflater.inflate(R.layout.list_footer_noresult, null, false);

        listView.addHeaderView(paddingView);

        // TouchInterceptionViewGroup should be a parent view other than ViewPager.
        // This is a workaround for the issue #117:
        // https://github.com/ksoichiro/Android-ObservableScrollView/issues/117
        listView.setTouchInterceptionViewGroup((ViewGroup) view.findViewById(R.id.fragment_root));

        // Scroll to the specified offset after layout
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SCROLL_Y)) {
            final int scrollY = args.getInt(ARG_SCROLL_Y, 0);
            ScrollUtils.addOnGlobalLayoutListener(listView, new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    int offset = scrollY % flexibleSpaceImageHeight;
                    listView.setSelectionFromTop(0, -offset);
                }
            });
            updateFlexibleSpace(scrollY, view);
        } else {
            updateFlexibleSpace(0, view);
        }
        listView.setScrollViewCallbacks(this);

        updateFlexibleSpace(0, view);

        mWezone = (Data_WeZone) args.getSerializable(WEZONE_DATA);

        mMemberList = new ArrayList<>();
        mWeZoneMemberListAdapter = new WeZoneMemberListAdapter(mActivity, mWezone, mMemberList);
        listView.setAdapter(mWeZoneMemberListAdapter);
        listView.setOnScrollListener(this);
        mGpsInfo = new GpsInfo(getContext());
        // GPS 사용유무 가져오기
        if (mGpsInfo.isGetLocation()) {
            longitude = mGpsInfo.getLongitude();
            latitude = mGpsInfo.getLatitude();

            mWezone.latitude = String.valueOf(latitude);
            mWezone.longitude = String.valueOf(longitude);
        } else {
            // GPS 를 사용할수 없으므로
        }

        getMemberList(mOffset, mLimit);


        return view;
    }

    public void setFriendPuls(String uuid) {
        //친구추가시 리로드
        for (int i = 0; i < mMemberList.size(); i++) {
            if (uuid.equals(mMemberList.get(i).uuid)) {
                mMemberList.get(i).friend_uuid = mActivity.getUuid();
                mWeZoneMemberListAdapter.notifyDataSetChanged();
            }
        }
    }

    public void getMemberList(int offset, int limit) {
        mActivity.showProgressPopup();
        final Call<Rev_ZoneMemberList> wezoneMemberList = mActivity.wezoneRestful.getZoneMemeberList(mWezone.wezone_id, "W", String.valueOf(offset), String.valueOf(limit), mWezone.latitude, mWezone.longitude);
        wezoneMemberList.enqueue(new Callback<Rev_ZoneMemberList>() {
            @Override
            public void onResponse(Call<Rev_ZoneMemberList> call, Response<Rev_ZoneMemberList> response) {
                Rev_ZoneMemberList memberList = response.body();
                if (mActivity.isNetSuccess(memberList)) {
                    if (Integer.valueOf(memberList.total_count) == 0) {
                        //다이얼로그 띄우고 뒤로 가서 리로드
                        onDeletedWezoneDialog();
                    } else {
                        mTotalCount = Integer.valueOf(memberList.total_count);
                        mMemberList.addAll(memberList.list);

                        if (mActivity instanceof WezoneActivity) {
                            ((WezoneActivity) mActivity).onMemberList(mMemberList);
                        }

                        mListViewLocked = true;


                        if (mMemberList.size() > 0) {

                            mNoResultBottom.setVisibility(View.GONE);
                        } else {
                            mNoResultBottom.setVisibility(View.VISIBLE);
                        }

                        mWeZoneMemberListAdapter.notifyDataSetChanged();
                    }

                    mActivity.hidePorgressPopup();
                }
            }

            @Override
            public void onFailure(Call<Rev_ZoneMemberList> call, Throwable t) {
                mActivity.hidePorgressPopup();
            }
        });
    }


    //삭제된 위존 팝업
    public void onDeletedWezoneDialog() {
        mDialog = new Dialog(getContext());
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_deleted_wezone);
        linearlayout_btn_ok = (LinearLayout) mDialog.findViewById(R.id.linearlayout_btn_ok);
        linearlayout_btn_ok.setOnClickListener(mClickListener);
        mDialog.show();
    }


    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId) {
                case R.id.linearlayout_btn_ok:
                    mDialog.cancel();
                    Intent i = new Intent();
                    i.putExtra(WEZONE_WEZONE_PUT, mWezone);
                    i.putExtra(WEZONE_WEZONE_PUT_TYPE, REMOVE);
                    mActivity.setResult(RESULT_OK, i);
                    mActivity.finish();
                    break;
            }
        }
    };


    @SuppressWarnings("NewApi")
    @Override
    public void setScrollY(int scrollY, int threshold) {
        View view = getView();
        if (view == null) {
            return;
        }
        ObservableListView listView = (ObservableListView) view.findViewById(R.id.scroll);
        if (listView == null) {
            return;
        }
        View firstVisibleChild = listView.getChildAt(0);
        if (firstVisibleChild != null) {
            int offset = scrollY;
            int position = 0;
            if (threshold < scrollY) {
                int baseHeight = firstVisibleChild.getHeight();
                position = scrollY / baseHeight;
                offset = scrollY % baseHeight;
            }
            listView.setSelectionFromTop(position, -offset);
        }
    }

    @Override
    protected void updateFlexibleSpace(int scrollY, View view) {
        // Translate list background
        // Also pass this event to parent Activity
        WezoneActivity parentActivity = (WezoneActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.onScrollChanged(scrollY, (ObservableListView) view.findViewById(R.id.scroll));
        }
    }

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
            if (mMemberList.size() < mTotalCount) {
                mOffset = mMemberList.size();

                direction = Direction.DOWN;

                getMemberList(mOffset, mLimit);

                down_value = 1;

//                grid_view.addFooterView(listFooterView);
                mListViewLocked = false;
            }
        }
    }
}
