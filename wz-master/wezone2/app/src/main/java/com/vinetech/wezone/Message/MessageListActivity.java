package com.vinetech.wezone.Message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.vinetech.ui.LoadingFooterView;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Chat_UserList;
import com.vinetech.wezone.Data.Data_Message;
import com.vinetech.wezone.Data.Data_OtherUser;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_ChatUserList;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sinsumin on 2017. 1. 18..
 *
 * 대화 리스트
 *
 * 대화 리스트 관리
 *
 * 실시간 안읽음 메세지 표시
 *
 */

public class MessageListActivity extends BaseActivity {

    private LinearLayout linearlayout_bg_noresult;

    private ListView listview;

    private MessageListAdapter mMessageListAdapter;

//    private ArrayList<Data_Chat_UserList> mData_MessageList;

    boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    int mOffset = 0;
    int mLimit = 100;

    private SwipeRefreshLayout swipe;

    public enum Direction {UP, DOWN}

    public Direction direction;

    public int mTotalCount = 0;

    public ArrayList<HashMap> other_user_uuids;

    public boolean mListViewLocked = true;
    public LoadingFooterView listFooterView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        setHeaderView(R.drawable.btn_back_white, "대화리스트", 0);

        linearlayout_bg_noresult = (LinearLayout) findViewById(R.id.linearlayout_bg_noresult);
        linearlayout_bg_noresult.setVisibility(View.GONE);

        listview = (ListView) findViewById(R.id.listview);



        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                direction = Direction.UP;
                mOffset = 0;
                getMessageUserList(true, mOffset, mLimit);
            }
        });

        listFooterView = new LoadingFooterView(MessageListActivity.this);

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

                    if (getShare().getUnReadList().size() < mTotalCount) {
                        mOffset = getShare().getUnReadList().size();

                        direction = Direction.DOWN;
                        getMessageUserList(false, mOffset, mLimit);

                        listview.addFooterView(listFooterView);
                        mListViewLocked = false;
                    }
                }
            }

        });

        direction = Direction.UP;
        mOffset = 0;
        getMessageUserList(true, mOffset, mLimit);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Define.INTENT_RESULT_CHATTING:
            {
                if(resultCode == RESULT_OK){
                    Data_OtherUser otherUser = (Data_OtherUser)data.getSerializableExtra(ChattingActivity.OTHER_DATA);
                    Data_Message message = (Data_Message)data.getSerializableExtra(ChattingActivity.LAST_CHAT_ITEM);
                    String pushFlag = (String)data.getStringExtra(ChattingActivity.PUSH_FLAG);
                    if(message != null && otherUser != null){
                        for(int i=0; i<getShare().getUnReadList().size(); i++){
                            Data_Chat_UserList listitem = getShare().getUnReadList().get(i);
                            String user_uuid = otherUser.user_uuid;
                            if("groupchat".equals(listitem.kind)){
                                user_uuid = user_uuid + "@" + getShare().getServerInfo().groupchat_hostname;
                            }
                            if(listitem.other_uuid.equals(user_uuid)){
                                getShare().getUnReadList().get(i).txt = message.txt;
                                getShare().getUnReadList().get(i).update_at = message.created_at;
                                getShare().getUnReadList().get(i).push_flag = pushFlag;

                            }
                        }
                        mMessageListAdapter.notifyDataSetChanged();
                    }
                }
            }
                break;

        }
    }

    public void getMessageUserList(boolean isProgress, int offset, int limit) {

        if(isProgress){
            showProgressPopup();
        }

        retrofit2.Call<Rev_ChatUserList> revChatUserListCall = wezoneRestful.getChatUserList(String.valueOf(offset),String.valueOf(limit));
        revChatUserListCall.enqueue(new Callback<Rev_ChatUserList>() {
            @Override
            public void onResponse(Call<Rev_ChatUserList> call, Response<Rev_ChatUserList> response) {
                Rev_ChatUserList revChatUserList = response.body();
                if(isNetSuccess(revChatUserList)){

                    mTotalCount = Integer.valueOf(revChatUserList.total_count);

                    if(revChatUserList.list != null){
                        if(direction == Direction.UP){
                            if(revChatUserList.list.size() == 0){
                                linearlayout_bg_noresult.setVisibility(View.VISIBLE);
                            }else{
                                linearlayout_bg_noresult.setVisibility(View.GONE);
                                getShare().setUnReadList(revChatUserList.list);

                                mMessageListAdapter = new MessageListAdapter(MessageListActivity.this, MessageListAdapter.TYPE_NORMAL, getShare().getUnReadList());
                                listview.setAdapter(mMessageListAdapter);
                            }


                        }else{
                            mListViewLocked = true;
                            listview.removeFooterView(listFooterView);

                            for(Data_Chat_UserList data : revChatUserList.list){
                                getShare().addUnRead(data);
                            }
                            mMessageListAdapter.notifyDataSetChanged();
                        }

                    }else{
                        if(direction == Direction.UP) {
                            linearlayout_bg_noresult.setVisibility(View.VISIBLE);
                        }
                    }


                }
                swipe.setRefreshing(false);
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_ChatUserList> call, Throwable t) {
                swipe.setRefreshing(false);
                hidePorgressPopup();
            }
        });
    }

    @Override
    public void onXmppReceiveMessage(MassegeType type, String fromID, String sFrom, String other_user_uuid, String other_user_name, String other_user_img_url, String message, String msgKey) {
        super.onXmppReceiveMessage(type, fromID, sFrom, other_user_uuid, other_user_name, other_user_img_url,  message, msgKey);

        if(mMessageListAdapter != null) {
            mMessageListAdapter.notifyDataSetChanged();
        }
    }

}
