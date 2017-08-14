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
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Board;
import com.vinetech.wezone.Data.Data_BoardListItem;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_BoardList;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WezoneBoardFragment extends WezoneBaseFragment<ObservableListView> implements AbsListView.OnScrollListener {

    public int mOffset = 0;
    public int mLimit = 10;
    public int mTotalCnt;

    //cnt : notic title 사용유무
    private int cnt = 0;

    public ArrayList<Data_BoardListItem> mListItems;
    public ObservableListView listView;
    public WeZoneBoardListAdapter mWeZoneBoardListAdapter;
    public View mUnLockBottom;
    public View mNoResultBottom;
    public boolean mListViewLocked = true;

    public enum Direction {UP, DOWN}

    public Direction direction;

    private boolean mHasRequestedMore;
    private ArrayList<Data_Zone_Member> mMemberList;
    private int down_value = 0;
    private BoardCountListener mBoardCountListener;
    private Data_WeZone mWezone;
    private BaseActivity mActivity;

    int boardList_total_count;
    boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = (BaseActivity) getActivity();
        mMemberList = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_flexiblespacewithimagelistview, container, false);

        listView = (ObservableListView) view.findViewById(R.id.scroll);
        // Set padding view for ListView. This is the flexible space.
        View paddingView = new View(getActivity());
        final int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_wezone_header) + getActionBarSize();
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                flexibleSpaceImageHeight);
        paddingView.setLayoutParams(lp);

        // This is required to disable header's list selector effect
        paddingView.setClickable(true);

        listView.addHeaderView(paddingView);

//        setDummyData(listView);
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

        mUnLockBottom = inflater.inflate(R.layout.wezone_board_noresult, null, false);
        mNoResultBottom = inflater.inflate(R.layout.list_footer_noresult, null, false);

        mListItems = new ArrayList<>();
        mWeZoneBoardListAdapter = new WeZoneBoardListAdapter(mActivity, mListItems, mWezone, mMemberList);
        listView.setAdapter(mWeZoneBoardListAdapter);
        listView.setOnScrollListener(this);

        if (WezoneUtil.isManager(mWezone.manage_type)) {
            getBoardData(true, mOffset, mLimit);
            if (WezoneUtil.isNotEmptyStr(String.valueOf(mTotalCnt))) {
                listView.addFooterView(mNoResultBottom);
            }
        } else if (WezoneUtil.isMember(mWezone.manage_type) || Define.TYPE_NORMAL.equals(mWezone.manage_type)) {
            getBoardData(true, mOffset, mLimit);
            if (WezoneUtil.isNotEmptyStr(String.valueOf(mTotalCnt))) {
                listView.addFooterView(mNoResultBottom);
            }
        } else {
            listView.addFooterView(mUnLockBottom);
        }

        return view;
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

    public int getNoticeCount(ArrayList<Data_Board> boards) {
        int noticeCnt = 0;
        for (Data_Board board : boards) {
            if ("T".equals(board.notice_flag)) {
                noticeCnt++;
            }
        }
        return noticeCnt;
    }

    public void addNoticeData(ArrayList<Data_Board> boards) {
        for (Data_Board board : boards) {
            if ("T".equals(board.notice_flag)) {
                Data_BoardListItem item = new Data_BoardListItem(Data_BoardListItem.BOARD_TYPE_NOTICE, board);
                mListItems.add(item);
            }
        }
    }

    public void addBoardData(ArrayList<Data_Board> boards) {

        for (Data_Board board : boards) {
            if (!"T".equals(board.notice_flag)) {
                Data_BoardListItem item = new Data_BoardListItem(Data_BoardListItem.BOARD_TYPE_BOARD, board);
                mListItems.add(item);
            }
        }
    }

    public void refreshBoardData() {
//        mListItems.clear();
        getBoardData(false, mOffset, mLimit);
        mWeZoneBoardListAdapter = new WeZoneBoardListAdapter(mActivity, mListItems, mWezone, mMemberList);
        listView.setAdapter(mWeZoneBoardListAdapter);

    }

    public int BoardCount() {
        return boardList_total_count;
    }

    public void Board_User_Info(ArrayList<Data_Zone_Member> memberlist) {
        mWeZoneBoardListAdapter = new WeZoneBoardListAdapter(mActivity, mListItems, mWezone, memberlist);
        listView.setAdapter(mWeZoneBoardListAdapter);
    }

    public void deleteBoardData(String board_id) {

        for (int i = 0; i < mListItems.size(); i++) {
            if (WezoneUtil.isEmptyStr(String.valueOf(mListItems.get(i).board)) == false) {
                for (Iterator<Data_BoardListItem> it = mListItems.iterator(); it.hasNext(); ) {
                    Data_BoardListItem aDrugStrength = it.next();
                    if (aDrugStrength.board != null) {
                        if (aDrugStrength.getBoard().board_id.equals(board_id)) {
                            it.remove();
                            refreshBoardData();
                        }
                    }
                }
            }
        }
    }

    public void getBoardData(boolean isProgress, int offset, int limit) {
        if (isProgress) {
            mActivity.showProgressPopup();
        }

        Call<Rev_BoardList> boardListCall = mActivity.wezoneRestful.getBoardList(mWezone.wezone_id, String.valueOf(offset), String.valueOf(limit));
        boardListCall.enqueue(new Callback<Rev_BoardList>() {
            @Override
            public void onResponse(Call<Rev_BoardList> call, Response<Rev_BoardList> response) {
                Rev_BoardList boardList = response.body();
                if (mActivity.isNetSuccess(boardList)) {

                    if (boardList.list != null) {
                        if (1 != down_value) {
                            mListItems.clear();
                        } else {
                            down_value = 0;
                        }

                        mTotalCnt += boardList.list.size();

                        boardList_total_count = Integer.valueOf(boardList.total_count);
                        mBoardCountListener.OnBoardCountEvent(boardList_total_count);

                        int noticeCont = Integer.valueOf(boardList.notice_count);
                        if (noticeCont != 0) {
                            if (cnt == 0) {
                                Data_BoardListItem boardItem = new Data_BoardListItem(Data_BoardListItem.BOARD_TYPE_TITLE, noticeCont);
                                mListItems.add(boardItem);
                            }
                            cnt += 1;
                        }
                        addNoticeData((ArrayList<Data_Board>) boardList.list);
                        addBoardData((ArrayList<Data_Board>) boardList.list);

                        mListViewLocked = true;
                        if (WezoneUtil.isMember(mWezone.manage_type)) {
                            if (mListItems.size() > 0) {
                                if (mNoResultBottom != null) {
                                    listView.removeFooterView(mNoResultBottom);
                                    mNoResultBottom = null;
                                }
                            } else {
                                if (mNoResultBottom != null) {
                                    mNoResultBottom.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            mUnLockBottom.setVisibility(View.VISIBLE);
                        }
                    }

                    mWeZoneBoardListAdapter.notifyDataSetChanged();
                }
                mActivity.hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_BoardList> call, Throwable t) {
                mActivity.hidePorgressPopup();
            }
        });
    }


    private void moveScrollToBottomWithAnimation(final boolean isAni) {
        listView.post(new Runnable() {
            @Override
            public void run() {

                if (isAni) {
                    listView.smoothScrollToPosition(mWeZoneBoardListAdapter.getCount());
                } else {
                    listView.setSelection(mWeZoneBoardListAdapter.getCount() - 1);
                }
            }
        });
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

            if (mListItems.size() < boardList_total_count) {
                mOffset = mListItems.size();

                direction = Direction.DOWN;

                getBoardData(false, mOffset, mLimit);
                down_value = 1;
                mListViewLocked = false;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BoardCountListener) {
            mBoardCountListener = (BoardCountListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "dddd");

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface BoardCountListener {
        void OnBoardCountEvent(int board_count);
    }
}
