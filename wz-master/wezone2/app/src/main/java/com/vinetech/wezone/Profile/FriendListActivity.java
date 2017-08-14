package com.vinetech.wezone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.vinetech.ui.LoadingFooterView;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_OtherUser;
import com.vinetech.wezone.Data.Data_UserInfo;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.Message.ChattingActivity;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_FriendList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendListActivity extends BaseActivity {

    private ListView listview;
    private LinearLayout linearlayout_bg_noresult;

    private FloatingActionButton fab_btn_search;

    private FriendListAdapter mFriendListAdapter;
    private ArrayList<Data_UserInfo> mUserList;

    boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    int mOffset = 0;
    int mLimit = 10;

    private SwipeRefreshLayout swipe;

    public enum Direction {UP, DOWN}

    public Direction direction;

    public int mTotalCount = 0;

    public boolean mListViewLocked = true;
    public LoadingFooterView listFooterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        setHeaderView(R.drawable.btn_back_white, "내 친구", R.drawable.btn_check);

        listview = (ListView) findViewById(R.id.listview);
        linearlayout_bg_noresult = (LinearLayout) findViewById(R.id.linearlayout_bg_noresult);
        linearlayout_bg_noresult.setVisibility(View.GONE);

        mUserList = new ArrayList<>();


        listFooterView = new LoadingFooterView(FriendListActivity.this);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mOffset = 0;
                mLimit = 10;
                direction = Direction.UP;
                getFriendList(true,mOffset,mLimit);

            }
        });

        mFriendListAdapter = new FriendListAdapter(FriendListActivity.this,mUserList,FriendListAdapter.FROM_NORMAL);
        listview.setAdapter(mFriendListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Data_UserInfo userinfo = mUserList.get(position);

                Data_OtherUser other = new Data_OtherUser();
                other.user_uuid = userinfo.uuid;
                other.user_name = userinfo.user_name;
                other.img_url = userinfo.img_url;
                ChattingActivity.startActivity(FriendListActivity.this,other);

            }
        });

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
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

                    if (mUserList.size() < mTotalCount) {
                        mOffset = mUserList.size();

                        direction = Direction.DOWN;

                        getFriendList(false,mOffset,mLimit);

                        listview.addFooterView(listFooterView);
                        mListViewLocked = false;
                    }
                }
            }

        });



        fab_btn_search = (FloatingActionButton) findViewById(R.id.fab_btn_search);
        fab_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchUserActivity.startActivity(FriendListActivity.this);
            }
        });


        mOffset = 0;
        mLimit = 10;
        direction = Direction.UP;
        getFriendList(true,mOffset,mLimit);
    }


    public void getFriendList(boolean isProgress, int offset, int limit) {

        if (isProgress) {
            showProgressPopup();
        }

        Call<Rev_FriendList> friendListCall = wezoneRestful.getFriendList(String.valueOf(offset), String.valueOf(limit));
        friendListCall.enqueue(new Callback<Rev_FriendList>() {
            @Override
            public void onResponse(Call<Rev_FriendList> call, Response<Rev_FriendList> response) {

                Rev_FriendList resultData = response.body();
                if(isNetSuccess(resultData)){

                    mTotalCount = Integer.valueOf(resultData.total_count);

                    if(direction == Direction.UP){
                        mUserList.clear();

                        if(resultData.list.size() == 0){
                            linearlayout_bg_noresult.setVisibility(View.VISIBLE);
                        }else{
                            linearlayout_bg_noresult.setVisibility(View.GONE);
                            mUserList.addAll(resultData.list);
                        }
                    }else{
                        mListViewLocked = true;
                        listview.removeFooterView(listFooterView);

                        mUserList.addAll(resultData.list);
                    }
                    mFriendListAdapter.notifyDataSetChanged();
                }
                swipe.setRefreshing(false);
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_FriendList> call, Throwable t) {
                listview.setVisibility(View.GONE);
                linearlayout_bg_noresult.setVisibility(View.VISIBLE);
                hidePorgressPopup();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Define.INTENT_RESULT_MEMBER_LIST_FRIEND_PULSE:
                if (resultCode == RESULT_OK) {
                    getFriendList(true,mOffset,mLimit);
                }
                break;
        }
    }
}
