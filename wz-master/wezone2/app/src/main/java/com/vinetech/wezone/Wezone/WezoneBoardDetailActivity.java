package com.vinetech.wezone.Wezone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.vinetech.util.LibDateUtil;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Board;
import com.vinetech.wezone.Data.Data_Comment;
import com.vinetech.wezone.Data.Data_File;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.Define;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.RevPacket.Rev_CommetList;
import com.vinetech.wezone.RevPacket.Rev_PostComment;
import com.vinetech.wezone.SendPacket.Send_PostComment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WezoneBoardDetailActivity extends BaseActivity {

    public static final String WEZONE_WEZONE = "wezone";
    public static final String WEZONE_BOARD = "wezone_board";
    public static final String WEZONE_BOARD_TITLE = "wezone_board_title";
    public static final String WEZONE_MEMBER_LIST = "wezone_member_list";

    public static final String COMMENT = "comment";

    public static final String WEZONE_BOARD_ID = "WEZONE_BOARD_ID";
    public static final String WEZONE_BOARD_RESULT_ACTION = "WEZONE_BOARD_RESULT_ACTION_DELETE";
    public static final String WEZONE_BOARD_RESULT_ACTION_DELETE = "WEZONE_BOARD_RESULT_ACTION_DELETE";
    public static final String WEZONE_BOARD_RESULT_ACTION_LOOK = "WEZONE_BOARD_RESULT_ACTION_LOOK";

    public static void startActivit(BaseActivity activity, String title, Data_WeZone wezone, Data_Board board, ArrayList<Data_Zone_Member> memberlist) {
        Intent intent = new Intent(activity, WezoneBoardDetailActivity.class);
        intent.putExtra(WEZONE_BOARD, board);
        intent.putExtra(WEZONE_WEZONE, wezone);
        intent.putExtra(WEZONE_BOARD_TITLE, title);
        if (memberlist != null) {
            intent.putExtra(WEZONE_MEMBER_LIST, memberlist);
        }
        activity.moveActivityForResult(intent, Define.INTENT_RESULT_BOARD);
    }

    public boolean mListViewLocked = true;
    public int mTotalCount = 0;
    public String mTitle;
    public Data_Board mBoard;
    public Data_WeZone mWezone;
    public ArrayList<Data_Zone_Member> mMemberList;
    public ArrayList<Data_Comment> mListItems;
    public ListView listview;
    public WeZoneCommentListAdapter mWeZoneCommentListAdapter;

    public ImageView imageview_badge;
    public ImageView imageview_profile;
    public TextView textview_name;
    public TextView textview_date;

    public LinearLayout linearlayout_btn_edit;
    public ImageView imageview_content;
    public TextView textview_contents;

    public EditText edittext_comment;
    public LinearLayout linearlayout_btn_send;

    private String mContexts;

    public int mOffset = 0;
    public int mLimit = 10;

    private boolean mHasRequestedMore;
    boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wezone_board_detail);

        mMemberList = new ArrayList<>();

        mTitle = getIntent().getStringExtra(WEZONE_BOARD_TITLE);
        mBoard = (Data_Board) getIntent().getSerializableExtra(WEZONE_BOARD);
        mWezone = (Data_WeZone) getIntent().getSerializableExtra(WEZONE_WEZONE);
        mMemberList = (ArrayList<Data_Zone_Member>) getIntent().getSerializableExtra(WEZONE_MEMBER_LIST);

        if (WezoneUtil.isEmptyStr(mTitle)) {
            setHeaderView(R.drawable.btn_back_white, "공지사항", 0);
        } else {
            setHeaderView(R.drawable.btn_back_white, mTitle, 0);
        }

        listview = (ListView) findViewById(R.id.listview);

        mListItems = new ArrayList<>();
        mWeZoneCommentListAdapter = new WeZoneCommentListAdapter(WezoneBoardDetailActivity.this, mListItems, mWezone, mMemberList);

        View header = getLayoutInflater().inflate(R.layout.board_header, null, false);
        listview.addHeaderView(header, null, false);
        listview.setAdapter(mWeZoneCommentListAdapter);
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    if (mListItems.size() < mTotalCount) {
                        mOffset = mListItems.size();
//                direction = Direction.DOWN;
//                gps(keyword, hashcode);
//                down_value = 1;
                        getCommentList();
                        mListViewLocked = false;

                    }
                }
            }
        });


        imageview_profile = (ImageView) findViewById(R.id.imageview_profile);
        if (WezoneUtil.isEmptyStr(mBoard.img_url) == false) {
            showImageFromRemoteWithCircle(mBoard.img_url, R.drawable.btn_circle_white, imageview_profile);
        } else {
            imageview_profile.setImageResource(R.drawable.im_beacon_add);
        }

        imageview_badge = (ImageView) findViewById(R.id.imageview_badge);
        if (mBoard.uuid.equals(mWezone.uuid)) {
            imageview_badge.setVisibility(View.VISIBLE);
        } else {
            imageview_badge.setVisibility(View.GONE);
        }

        textview_name = (TextView) findViewById(R.id.textview_name);
        textview_name.setText(mBoard.user_name);

        textview_date = (TextView) findViewById(R.id.textview_date);
        String dateWithFormat = LibDateUtil.getConvertDate(mBoard.create_datetime, "yyyy-MM-dd HH:mm:ss", "yyyy년 MM월 dd일");
        textview_date.setText(dateWithFormat);

        linearlayout_btn_edit = (LinearLayout) findViewById(R.id.linearlayout_btn_edit);
        if (mBoard.uuid.equals(getUuid())) {
            linearlayout_btn_edit.setVisibility(View.VISIBLE);
            linearlayout_btn_edit.setOnClickListener(mClickListener);
        } else {
            linearlayout_btn_edit.setVisibility(View.GONE);
        }

        imageview_content = (ImageView) findViewById(R.id.imageview_content);
        if (mBoard.board_file != null && mBoard.board_file.size() > 0) {
            Data_File file = mBoard.board_file.get(0);
            imageview_content.setVisibility(View.VISIBLE);
            if (WezoneUtil.isEmptyStr(file.url) == false) {
                showImageFromRemote(file.url, 0, imageview_content);
            } else {
                imageview_content.setVisibility(View.GONE);
            }
        } else {
            imageview_content.setVisibility(View.GONE);
        }
        textview_contents = (TextView) findViewById(R.id.textview_contents);
        textview_contents.setText(mBoard.content);

        getCommentList();

        edittext_comment = (EditText) findViewById(R.id.edittext_comment);
        linearlayout_btn_send = (LinearLayout) findViewById(R.id.linearlayout_btn_send);
        linearlayout_btn_send.setOnClickListener(mClickListener);
    }


    public void getCommentList() {
        Call<Rev_CommetList> revCommetListCall = wezoneRestful.getCommentList(mBoard.wezone_id, Data_Comment.TYPE_WEZONE_BOARD_COMMENT, mBoard.board_id, String.valueOf(mOffset), String.valueOf(mLimit));
        revCommetListCall.enqueue(new Callback<Rev_CommetList>() {
            @Override
            public void onResponse(Call<Rev_CommetList> call, Response<Rev_CommetList> response) {
                Rev_CommetList commetList = response.body();
                if (isNetSuccess(commetList)) {
                    mTotalCount = Integer.valueOf(commetList.total_count);
                    mListItems.addAll(commetList.list);
                    mWeZoneCommentListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Rev_CommetList> call, Throwable t) {

            }
        });


    }


    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId) {
                case R.id.linearlayout_btn_edit:
                    PopupMenu popup = new PopupMenu(WezoneBoardDetailActivity.this, linearlayout_btn_edit);
                    popup.getMenuInflater().inflate(R.menu.menu_board_edit_delete, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit_board:
                                    WriteBoardActivity.startActivityWithEdit(WezoneBoardDetailActivity.this, mBoard, mWezone);
                                    break;
                                case R.id.delete_board:
                                    deleteWezoneBoard(mBoard.board_id);
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();
                    break;

                case R.id.linearlayout_btn_send:
                    String contents = edittext_comment.getText().toString().trim();
                    mContexts = contents;
                    postComment(contents);
                    break;
            }
        }
    };

    public void postComment(final String contents) {

        Send_PostComment comment = new Send_PostComment();

        comment.wezone_id = mWezone.wezone_id;
        comment.board_id = mBoard.board_id;
        comment.type = Data_Comment.TYPE_WEZONE_BOARD_COMMENT;
        comment.content = contents;

        Call<Rev_PostComment> postComment = wezoneRestful.postComment(comment);
        postComment.enqueue(new Callback<Rev_PostComment>() {
            @Override
            public void onResponse(Call<Rev_PostComment> call, Response<Rev_PostComment> response) {
                Rev_PostComment revPostComment = response.body();
                if (isNetSuccess(revPostComment)) {

                    edittext_comment.setText("");

                    Data_Comment comment = new Data_Comment();
                    comment.comment_id = revPostComment.comment_id;
                    comment.wezone_id = mWezone.wezone_id;
                    comment.uuid = getUuid();
                    comment.type = Data_Comment.TYPE_WEZONE_BOARD_COMMENT;
                    comment.board_id = mBoard.board_id;
                    comment.content = contents;
                    comment.user_name = getName();
                    comment.img_url = getImageUrl();
                    comment.create_datetime = LibDateUtil.getCurruntDayWithFormat("yyyy-MM-dd HH:mm:ss");
                    mListItems.add(0, comment);
                    mWeZoneCommentListAdapter.notifyDataSetChanged();

                    listview.smoothScrollToPosition(0);
                }
            }

            @Override
            public void onFailure(Call<Rev_PostComment> call, Throwable t) {

            }
        });
    }

    public void deleteWezoneBoard(final String board_id) {
        Call<Rev_Base> boardCall = wezoneRestful.deleteWezone(board_id);
        boardCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base rev_base = response.body();
                if (isNetSuccess(rev_base)) {
                    Intent intent = new Intent();
                    intent.putExtra(WEZONE_BOARD_RESULT_ACTION, WEZONE_BOARD_RESULT_ACTION_DELETE);
                    intent.putExtra(WEZONE_BOARD_ID, board_id);
                    setResult(RESULT_OK, intent);
                    finish();
                    //화면 뒤로 가지 않고 리로드 해야함
                }
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });
    }

    private void moveScrollToBottomWithAnimation(final boolean isAni) {
        listview.post(new Runnable() {
            @Override
            public void run() {

                if (isAni) {
                    listview.smoothScrollToPosition(mWeZoneCommentListAdapter.getCount());
                } else {
                    listview.setSelection(mWeZoneCommentListAdapter.getCount() - 1);
                }
            }
        });
    }

    @Override
    public void onClickLeftBtn(View v) {
        //추가할때만 호출
        sendcomment();
        finish();
    }
    @Override
    public void onBackPressed(){
        sendcomment();
        super.onBackPressed();
    }

    private void sendcomment() {
        if(mContexts != null) {
            Intent i = new Intent();
            i.putExtra(COMMENT, mContexts);
            i.putExtra(WEZONE_BOARD_RESULT_ACTION, WEZONE_BOARD_RESULT_ACTION_LOOK);
            setResult(RESULT_OK, i);
        }
    }

    public void reload() {

        textview_contents.setText(mBoard.content);

        imageview_content = (ImageView) findViewById(R.id.imageview_content);
        if (mBoard.board_file != null && mBoard.board_file.size() > 0) {
            Data_File file = mBoard.board_file.get(0);
            imageview_content.setVisibility(View.VISIBLE);
            if (WezoneUtil.isEmptyStr(file.url) == false) {
                showImageFromRemote(file.url, 0, imageview_content);
            } else {
                imageview_content.setVisibility(View.GONE);
            }
        } else {
            imageview_content.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Define.INTENT_RESULT_BOARD:
                if (resultCode == RESULT_OK) {
                    mBoard = (Data_Board) data.getSerializableExtra(WriteBoardActivity.BOARD_DATA);
                    reload();

                }
                break;
        }

    }


}
