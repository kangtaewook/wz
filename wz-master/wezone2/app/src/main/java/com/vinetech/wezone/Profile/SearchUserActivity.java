package com.vinetech.wezone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_UserInfo;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_FriendList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUserActivity extends BaseActivity {

    public static String FRIENDUUID = "FRIENDUUID";

    private EditText edittext_search;
    private ListView listview;
    private TextView textview_noresult;

    private FriendListAdapter mFriendListAdapter;
    private ArrayList<Data_UserInfo> mUserList;



    private Data_UserInfo userInfo;
    private String friend_uuid;
    private String strTemp;

    boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    private int mOffset = 0;
    private int mLimit = 10;

    public enum Direction {UP, DOWN}

    public Direction direction;

    public int mTotalCount = 0;

    public boolean mListViewLocked = true;

    public static void startActivity(BaseActivity activity) {
        Intent intent = new Intent(activity, SearchUserActivity.class);
        activity.startActivityForResult(intent, Define.INTENT_RESULT_MEMBER_LIST_FRIEND_PULSE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        setHeaderView(R.drawable.btn_back_white, "친구 찾기", R.drawable.btn_check);

        edittext_search = (EditText) findViewById(R.id.edittext_search);
        edittext_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:{
                        // 검색 동작
                        strTemp = edittext_search.getText().toString().trim();
                        if(WezoneUtil.isEmptyStr(strTemp)){
                            Toast.makeText(SearchUserActivity.this,"검색어를 입력해주세요",Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        direction = Direction.UP;
                        mOffset = 0;
                        getSearchFriend(strTemp);
                        edittext_search.setText(strTemp);
                        edittext_search.setSelection(edittext_search.length());
                    }
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });


        listview = (ListView) findViewById(R.id.listview);
        textview_noresult = (TextView) findViewById(R.id.textview_noresult);

        mUserList = new ArrayList<>();
        mFriendListAdapter = new FriendListAdapter(SearchUserActivity.this, mUserList,FriendListAdapter.FROM_SEARCH);

        listview.setAdapter(mFriendListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userInfo = mUserList.get(position);
                ProfileActivity.startActivity(SearchUserActivity.this,userInfo);
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

                        getSearchFriend(strTemp);

                        mListViewLocked = false;
                    }
                }
            }

        });


    }

    public void getSearchFriend(String keyword){

        showProgressPopup();

        edittext_search.setText("");

        Call<Rev_FriendList> friendListCall = wezoneRestful.getFriendList(keyword,String.valueOf(mOffset),String.valueOf(mLimit));
        friendListCall.enqueue(new Callback<Rev_FriendList>() {
            @Override
            public void onResponse(Call<Rev_FriendList> call, Response<Rev_FriendList> response) {
                Rev_FriendList resultData = response.body();
                if (isNetSuccess(resultData)) {

                    mTotalCount = Integer.valueOf(resultData.total_count);

                    if(direction == Direction.DOWN) {

                        mUserList.addAll(resultData.list);

                        if (mUserList.size() == 0) {
                            listview.setVisibility(View.GONE);
                            textview_noresult.setVisibility(View.VISIBLE);
                        } else {
                            listview.setVisibility(View.VISIBLE);
                            textview_noresult.setVisibility(View.GONE);
                        }

                        mFriendListAdapter.notifyDataSetChanged();
                    }else{
                        mUserList.clear();
                        mUserList.addAll(resultData.list);

                        if (mUserList.size() == 0) {
                            listview.setVisibility(View.GONE);
                            textview_noresult.setVisibility(View.VISIBLE);
                        } else {
                            listview.setVisibility(View.VISIBLE);
                            textview_noresult.setVisibility(View.GONE);
                        }

                        mFriendListAdapter.notifyDataSetChanged();

                    }
                } else {
                    listview.setVisibility(View.GONE);
                    textview_noresult.setVisibility(View.VISIBLE);
                }
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_FriendList> call, Throwable t) {
                hidePorgressPopup();
                listview.setVisibility(View.GONE);
                textview_noresult.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Define.INTENT_RESULT_MEMBER_LIST_FRIEND_PULSE:
                if (resultCode == RESULT_OK) {
                    friend_uuid = data.getStringExtra(ProfileActivity.FRIEND_PULS);
                    for(int i = 0; i<mUserList.size(); i++){
                        if(mUserList.get(i).uuid.equals(friend_uuid)){
                            //친구 추가한 사용자의 friend_uuid에 자기 자신의 uuid를 넣는다?
                            mUserList.get(i).friend_uuid = getShare().getMyInfo().uuid;
                            mFriendListAdapter.notifyDataSetChanged();
                        }
                    }
                  Intent i = new Intent();
                    i.putExtra(FRIENDUUID,friend_uuid);
                    setResult(RESULT_OK, i);
                }
                break;
        }
    }


}
