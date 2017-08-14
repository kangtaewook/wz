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
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.Common.EditTextActivity;
import com.vinetech.wezone.Data.Data_Comment;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_CommetList;
import com.vinetech.wezone.RevPacket.Rev_Wezoninfo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WezoneHomeFragment extends WezoneBaseFragment<ObservableListView> {

    public static final String WEZONE_MAP_FRAGMENT_CLICK = "WEZONE_MAP_FRAGMENT_CLICK";
    public static final String WEZONE_MAP_UPDATE = "WEZONE_MAP_UPDATE";
    public static final String WEZONE_OTHER = "WEZONE_OTHER";

    private Data_WeZone mWezone;

    private WezoneMapFragment mWezoneMapFragment;

    private WezoneActivity mActivity;

    private ObservableListView mObservableListView;

    private TextView textview_review_cnt;
    private TextView textview_addr;
    private LinearLayout linearlayout_btn_write;

    public int mTotalCount = 0;

    public int mOffset = 0;
    public int mLimit = 10;

    public ArrayList<Data_Comment> mListItems;
    public ArrayList<Data_Zone_Member> mMemberlist;
    public WeZoneCommentListAdapter mWeZoneCommentListAdapter;

    boolean lastitemVisibleFlag = false;

    public enum Direction {UP, DOWN}

    public Direction direction;

    View view;

    public boolean mListViewLocked = true;

    public void setArguments(int scrollY, Data_WeZone wezone) {
        if (0 <= scrollY) {
            Bundle args = new Bundle();
            args.putInt(ARG_SCROLL_Y, scrollY);
            args.putSerializable(WEZONE_DATA,wezone);
            setArguments(args);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_flexiblespacewithimagelistview, container, false);

        mActivity = (WezoneActivity) getActivity();
        mMemberlist = new ArrayList<>();
        mObservableListView = (ObservableListView) view.findViewById(R.id.scroll);

        View header = mActivity.getLayoutInflater().inflate(R.layout.wezone_info_header, null, false);
        mObservableListView.addHeaderView(header,null,false);
        mObservableListView.setTouchInterceptionViewGroup((ViewGroup) view.findViewById(R.id.fragment_root));

        mObservableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
                //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag && mListViewLocked) {
                    //TODO 화면이 바닦에 닿을때 처리

                    if (mListItems.size() < mTotalCount) {
                        mOffset = mListItems.size();

                        direction = Direction.DOWN;

                        getCommentList(mOffset,mLimit);
                        mListViewLocked = false;
                    }
                }
            }

        });

        mListItems = new ArrayList<>();

        final int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_wezone_header) + getActionBarSize();

        // Scroll to the specified offset after layout
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SCROLL_Y)) {
            final int scrollY = args.getInt(ARG_SCROLL_Y, 0);
            ScrollUtils.addOnGlobalLayoutListener(mObservableListView, new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    int offset = scrollY % flexibleSpaceImageHeight;
                    mObservableListView.setSelectionFromTop(0, -offset);
                }
            });
            updateFlexibleSpace(scrollY, view);
        } else {
            updateFlexibleSpace(0, view);
        }
        mObservableListView.setScrollViewCallbacks(this);

        updateFlexibleSpace(0, view);

        mWezone = (Data_WeZone) args.getSerializable(WEZONE_DATA);
        mWeZoneCommentListAdapter = new WeZoneCommentListAdapter(mActivity,mListItems,mWezone,mMemberlist);
        mObservableListView.setAdapter(mWeZoneCommentListAdapter);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment_area, WezoneMapFragment.getInstance(mWezone.longitude,mWezone.latitude, mWezone.manage_type, mWezone.wezone_id,mWezone,""));
        fragmentTransaction.commit();

        textview_addr = (TextView)view.findViewById(R.id.textview_addr);
        textview_addr.setText(mWezone.address);

        textview_review_cnt = (TextView) view.findViewById(R.id.textview_review_cnt);
        linearlayout_btn_write = (LinearLayout) view.findViewById(R.id.linearlayout_btn_write);
        //가입자만 리뷰 작성되게 하기
        if(WezoneUtil.isMember(mWezone.manage_type)){
            linearlayout_btn_write.setVisibility(View.VISIBLE);
        }else{
            linearlayout_btn_write.setVisibility(View.GONE);
        }
        linearlayout_btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextActivity.startActivity(mActivity, "리뷰 작성","리뷰를 작성해 주세요.",null);
            }
        });

        getCommentList(mOffset, mLimit);

        return view;
    }

    public void address_reload(String mtype, Data_WeZone weZone){
        if(WEZONE_MAP_FRAGMENT_CLICK.equals(mtype)) {
            //Get 전문 변환된 latitude 등록 하고 나온 address 가져오기
            getWezoneaddress(weZone);

            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.fragment_area, WezoneMapFragment.getInstance(mWezone.longitude,mWezone.latitude, mWezone.manage_type, mWezone.wezone_id, mWezone, WEZONE_MAP_UPDATE));
            fragmentTransaction.commit();

        }
    }

    public void getWezoneaddress(Data_WeZone weZone) {

        Call<Rev_Wezoninfo> WezoneCall = mActivity.wezoneRestful.getWezone(weZone.wezone_id);
        WezoneCall.enqueue(new Callback<Rev_Wezoninfo>() {

            @Override
            public void onResponse(Call<Rev_Wezoninfo> call, Response<Rev_Wezoninfo> response) {
                Rev_Wezoninfo resultData = response.body();
                if (mActivity.isNetSuccess(resultData)) {
                    if (resultData.wezone_info != null) {
                        mWezone.address = resultData.wezone_info.address;
                        textview_addr.setText(mWezone.address);
                    }
                }
            }

            @Override
            public void onFailure(Call<Rev_Wezoninfo> call, Throwable t) {
            }
        });
    }


    public void Board_User_Info(ArrayList<Data_Zone_Member> memberlist){
        mWeZoneCommentListAdapter = new WeZoneCommentListAdapter(mActivity,mListItems,mWezone,memberlist);
        mObservableListView.setAdapter(mWeZoneCommentListAdapter);
    }

    public void addComment(Data_Comment comment){
        mListItems.add(0,comment);
        mWeZoneCommentListAdapter.notifyDataSetChanged();
        reloadCommentCnt();
    }

    public void reloadCommentCnt(){
        int cnt = mListItems.size();
        textview_review_cnt.setText(String.valueOf(cnt));
    }

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
        int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_wezone_header);

        View listBackgroundView = view.findViewById(R.id.list_background);

        // Translate list background
        ViewHelper.setTranslationY(listBackgroundView, Math.max(0, -scrollY + flexibleSpaceImageHeight));

        // Also pass this event to parent Activity
        WezoneActivity parentActivity = (WezoneActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.onScrollChanged(scrollY, (ObservableListView) view.findViewById(R.id.scroll));
        }
    }

    public void getCommentList(int mOffset, int mLimit){

        mActivity.showProgressPopup();
        Call<Rev_CommetList> revCommetListCall = mActivity.wezoneRestful.getCommentList(mWezone.wezone_id, Data_Comment.TYPE_WEZONE_COMMENT, String.valueOf(mOffset),String.valueOf(mLimit));
        revCommetListCall.enqueue(new Callback<Rev_CommetList>() {
            @Override
            public void onResponse(Call<Rev_CommetList> call, Response<Rev_CommetList> response) {
                Rev_CommetList commetList = response.body();
                if(mActivity.isNetSuccess(commetList)){
                    mTotalCount = Integer.valueOf(commetList.total_count);

                    if(direction == Direction.UP){
                        if(commetList.list.size() == 0){
                        }else{
                            mListItems.addAll(commetList.list);
                        }
                    }else{
                        mListViewLocked = true;
                        mListItems.addAll(commetList.list);
                    }
                    mWeZoneCommentListAdapter.notifyDataSetChanged();
                    reloadCommentCnt();
                }
                mActivity.hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_CommetList> call, Throwable t) {
                mActivity.hidePorgressPopup();
            }
        });
    }
}
