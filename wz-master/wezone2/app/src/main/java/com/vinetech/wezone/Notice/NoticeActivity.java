package com.vinetech.wezone.Notice;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.vinetech.ui.LoadingFooterView;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Notice;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Notice;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends BaseActivity {

    private LinearLayout linearlayout_bg_noresult;

    private ListView listview;

    private NoticeListAdapter mNoticeListAdapter;

    private ArrayList<Data_Notice> mNoticeItemList;

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
        setContentView(R.layout.activity_notice);

        setHeaderView(R.drawable.btn_back_white, "알림", 0);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mOffset = 0;
                mLimit = 10;
                direction = Direction.UP;
                getNoticeList(true,mOffset,mLimit);

            }
        });

        listFooterView = new LoadingFooterView(NoticeActivity.this);

        linearlayout_bg_noresult = (LinearLayout) findViewById(R.id.linearlayout_bg_noresult);
        listview = (ListView) findViewById(R.id.listview);
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

                    if (mNoticeItemList.size() < mTotalCount) {
                        mOffset = mNoticeItemList.size();

                        direction = Direction.DOWN;

                        getNoticeList(false,mOffset,mLimit);

                        listview.addFooterView(listFooterView);
                        mListViewLocked = false;
                    }
                }
            }

        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Data_Notice notice = mNoticeItemList.get(position);
                moveActivityWithPushData(notice.message_type,notice.kind,notice.item_id);
            }
        });


        mNoticeItemList = new ArrayList<>();

        mNoticeListAdapter = new NoticeListAdapter(NoticeActivity.this,mNoticeItemList);
        listview.setAdapter(mNoticeListAdapter);

        direction = Direction.UP;
        getNoticeList(true,mOffset,mLimit);
    }

    public void getNoticeList(boolean isProgress, int offset, int limit){

        if(isProgress){
            showProgressPopup();
        }
        Call<Rev_Notice> revNoticeCall = wezoneRestful.getNotice(String.valueOf(offset),String.valueOf(limit));
        revNoticeCall.enqueue(new Callback<Rev_Notice>() {
            @Override
            public void onResponse(Call<Rev_Notice> call, Response<Rev_Notice> response) {
                Rev_Notice revNotice = response.body();
                if(isNetSuccess(revNotice)){

                    mTotalCount = Integer.valueOf(revNotice.total_count);

                    if(direction == Direction.UP){
                        mNoticeItemList.clear();

                        if(revNotice.list.size() == 0){
                            linearlayout_bg_noresult.setVisibility(View.VISIBLE);
                        }else{
                            linearlayout_bg_noresult.setVisibility(View.GONE);
                            mNoticeItemList.addAll(revNotice.list);
                        }
                    }else{
                        mListViewLocked = true;
                        listview.removeFooterView(listFooterView);

                        mNoticeItemList.addAll(revNotice.list);
                    }
                    mNoticeListAdapter.notifyDataSetChanged();
                }
                swipe.setRefreshing(false);
                hidePorgressPopup();
            }

            @Override
            public void onFailure(Call<Rev_Notice> call, Throwable t) {
                hidePorgressPopup();
            }
        });

    }
}
