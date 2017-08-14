package com.vinetech.wezone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vinetech.ui.LoadingFooterView;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_UserInfo;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_FriendList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSelectActivity extends BaseActivity {

    public static final String USER_DATA = "user_data";
    public static final String MEMBER_DATA = "member_data";

    public static final String VIEW_MODE = "view_mode";
    public static final int MODE_NORMAL = 0;
    public static final int MODE_SHOW_SHARE = 1;

    public static void startActivity(BaseActivity activity, ArrayList<Data_UserInfo> users, ArrayList<Data_Zone_Member> mMemberList) {
        Intent intent = new Intent(activity, UserSelectActivity.class);
        intent.putExtra(VIEW_MODE,MODE_NORMAL);
        if(users != null){
            intent.putExtra(USER_DATA,users);
        }
        if(users != null){
            intent.putExtra(MEMBER_DATA,mMemberList);
        }
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_USER);
    }

    public static void startActivityAndShowShare(BaseActivity activity, ArrayList<Data_UserInfo> users) {
        Intent intent = new Intent(activity, UserSelectActivity.class);
        intent.putExtra(VIEW_MODE,MODE_SHOW_SHARE);
        if(users != null){
            intent.putExtra(USER_DATA,users);
        }
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_USER);
    }

    private int mViewMode;

    private LinearLayout linearlayout_invite_area;
    private LinearLayout linearlayout_btn_invite;

    private TextView textview_user_cnt;
    private ListView listview;

    private UserSelectListAdapter mUserSelectListAdapter;

    private ArrayList<Data_UserInfo> mUserList;

    private ArrayList<Data_UserInfo> mSelectedList;
    private ArrayList<Data_Zone_Member> mMeberList;


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
        setContentView(R.layout.activity_user_select);

        setHeaderView(R.drawable.btn_back_white,"친구",R.drawable.btn_check);

        mViewMode = getIntent().getIntExtra(VIEW_MODE,MODE_NORMAL);
        mSelectedList = (ArrayList<Data_UserInfo>)getIntent().getSerializableExtra(USER_DATA);
        mMeberList = (ArrayList<Data_Zone_Member>) getIntent().getSerializableExtra(MEMBER_DATA);

        listview = (ListView) findViewById(R.id.listview);

        View header = getLayoutInflater().inflate(R.layout.user_select_list_header, null, false);
        listview.addHeaderView(header,null,false);

        listFooterView = new LoadingFooterView(UserSelectActivity.this);

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

        linearlayout_invite_area = (LinearLayout) findViewById(R.id.linearlayout_invite_area);
        linearlayout_btn_invite = (LinearLayout) findViewById(R.id.linearlayout_btn_invite);
        if(mViewMode == MODE_NORMAL){
            linearlayout_invite_area.setVisibility(View.GONE);
        }else{
            linearlayout_invite_area.setVisibility(View.VISIBLE);
        }

        textview_user_cnt = (TextView) findViewById(R.id.textview_user_cnt);

        mUserList = new ArrayList<>();

        mUserSelectListAdapter = new UserSelectListAdapter(UserSelectActivity.this,mUserList);
        listview.setAdapter(mUserSelectListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0)
                    return;

//                if(getSelectedItemCount() > 4){
//                    Toast.makeText(UserSelectActivity.this,"최대 4개까지 선택가능합니다.",Toast.LENGTH_SHORT).show();
//                    return;
//                }

                int realPos = position - 1;
                mUserList.get(realPos).isSelected = !mUserList.get(realPos).isSelected;
                mUserSelectListAdapter.notifyDataSetChanged();

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

        mOffset = 0;
        mLimit = 10;
        direction = Direction.UP;
        getFriendList(true,mOffset,mLimit);

    }

    public void compare_memberlist_userlist(){
        if(mMeberList != null) {
            if (mMeberList.size() > 0 && mUserList.size() > 0) {
                for (Data_Zone_Member data_zone_member : mMeberList) {
                    for (Data_UserInfo data_userInfo : mUserList) {
                        if (data_zone_member.uuid.equals(data_userInfo.uuid)) {
                            data_userInfo.isSelected = true;
                            data_userInfo.isClicked = true;
                            mSelectedList.add(data_userInfo);
                        }
                    }
                }
                compareList();
            }
        }
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

                        mUserList.addAll(resultData.list);

                        compareList();

                        textview_user_cnt.setText(String.valueOf(mUserList.size()));

                    }else{
                        mListViewLocked = true;
                        listview.removeFooterView(listFooterView);

                        mUserList.addAll(resultData.list);

                        compareList();

                        textview_user_cnt.setText(String.valueOf(mUserList.size()));
                    }
                    compare_memberlist_userlist();
                    mUserSelectListAdapter.notifyDataSetChanged();
                }
                swipe.setRefreshing(false);
                hidePorgressPopup();

            }

            @Override
            public void onFailure(Call<Rev_FriendList> call, Throwable t) {
                hidePorgressPopup();
            }
        });
    }

    @Override
    public void onClickRightBtn(View v) {
        super.onClickRightBtn(v);
        Intent i = new Intent();
        i.putExtra(Define.INTENTKEY_USER_VALUE,getSelectedItems());
        setResult(RESULT_OK,i);
        finish();
    }

    public ArrayList<Data_UserInfo> getSelectedItems(){
        ArrayList<Data_UserInfo> selectedItemList = new ArrayList<>();
        for(int i=0; i<mUserList.size(); i++){
            if(mUserList.get(i).isSelected){
                if(mUserList.get(i).isClicked == false) {
                    selectedItemList.add(mUserList.get(i));
                }
            }
        }
        return selectedItemList;
    }

    public int getSelectedItemCount(){
        int cnt = 0;
        for(int i=0; i<mUserList.size(); i++){
            if(mUserList.get(i).isSelected){
                cnt ++;
            }
        }
        return cnt;
    }

    public void compareList(){
        if(mSelectedList != null && mSelectedList.size() > 0){
            for(Data_UserInfo selectedUser : mSelectedList){
                if(mUserList != null && mUserList.size() > 0){
                    for (int i=0; i<mUserList.size(); i++){
                        Data_UserInfo item = mUserList.get(i);
                        if(selectedUser.uuid.equals(item.uuid)){
                            mUserList.get(i).isSelected = true;
                        }
                    }
                }
            }
        }
    }



}
