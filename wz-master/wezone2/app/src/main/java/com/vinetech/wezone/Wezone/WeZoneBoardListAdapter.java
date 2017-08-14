package com.vinetech.wezone.Wezone;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.vinetech.ui.CustomImageView;
import com.vinetech.util.LibDateUtil;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_BoardListItem;
import com.vinetech.wezone.Data.Data_Comment;
import com.vinetech.wezone.Data.Data_File;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class WeZoneBoardListAdapter extends BaseAdapter {

    public static final String WEZONE_BOARD_ID = "WEZONE_BOARD_ID";
    public static final String WEZONE_BOARD_RESULT_ACTION = "WEZONE_BOARD_RESULT_ACTION_DELETE";
    public static final String WEZONE_BOARD_RESULT_ACTION_DELETE = "WEZONE_BOARD_RESULT_ACTION_DELETE";

    private ArrayList<Data_BoardListItem> mListItem;
    private ArrayList<Data_Zone_Member> mMemberList;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;
    private Data_WeZone mWezone;

    public WeZoneBoardListAdapter(BaseActivity a, ArrayList<Data_BoardListItem> userInfos, Data_WeZone wezone, ArrayList<Data_Zone_Member> memberslist){
        mActivity = a;
        mListItem = userInfos;
        if(memberslist != null) {
            mMemberList = memberslist;
        }
        mInflater = LayoutInflater.from(mActivity);
        mWezone = wezone;
    }

    @Override
    public int getCount() {
        return mListItem.size();
    }

    @Override
    public Object getItem(int i) {
        return mListItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.wezone_board_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.linearlayout_title_area = (LinearLayout) convertView.findViewById(R.id.linearlayout_title_area);
            holder.textview_title = (TextView) convertView.findViewById(R.id.textview_title);
            holder.textview_title_cnt = (TextView) convertView.findViewById(R.id.textview_title_cnt);
            holder.linearlayout_notice_area = (LinearLayout) convertView.findViewById(R.id.linearlayout_notice_area);
            holder.textview_notice_contents = (TextView) convertView.findViewById(R.id.textview_notice_contents);
            holder.linearlayout_board_area = (LinearLayout) convertView.findViewById(R.id.linearlayout_board_area);
            holder.imageview_profile = (ImageView) convertView.findViewById(R.id.imageview_profile);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.textview_date = (TextView) convertView.findViewById(R.id.textview_date);
            holder.linearlayout_btn_edit = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_edit);
            holder.imageview_content = (CustomImageView) convertView.findViewById(R.id.imageview_content);
            holder.textview_contents = (TextView) convertView.findViewById(R.id.textview_contents);
            holder.textview_comment_more = (TextView) convertView.findViewById(R.id.textview_comment_more);
            holder.linearlayout_comment_area = (LinearLayout) convertView.findViewById(R.id.linearlayout_comment_area);
            holder.linearlayout_comment_01 = (LinearLayout) convertView.findViewById(R.id.linearlayout_comment_01);
            holder.imageview_profile_01 = (ImageView) convertView.findViewById(R.id.imageview_profile_01);
            holder.imageview_badge = (ImageView) convertView.findViewById(R.id.imageview_badge);
            holder.textview_comment_contents_01 = (TextView) convertView.findViewById(R.id.textview_comment_contents_01);
            holder.textview_comment_date_01 = (TextView) convertView.findViewById(R.id.textview_comment_date_01);
            holder.linearlayout_comment_02 = (LinearLayout) convertView.findViewById(R.id.linearlayout_comment_02);
            holder.imageview_profile_02 = (ImageView) convertView.findViewById(R.id.imageview_profile_02);
            holder.textview_comment_contents_02 = (TextView) convertView.findViewById(R.id.textview_comment_contents_02);
            holder.textview_comment_date_02 = (TextView) convertView.findViewById(R.id.textview_comment_date_02);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        final Data_BoardListItem boardItem = mListItem.get(position);

        holder.linearlayout_title_area.setVisibility(View.GONE);
        holder.linearlayout_notice_area.setVisibility(View.GONE);
        holder.linearlayout_notice_area.setOnClickListener(mClickListener);
        holder.linearlayout_notice_area.setTag(position);
        holder.linearlayout_board_area.setVisibility(View.GONE);
        holder.linearlayout_board_area.setOnClickListener(mClickListener);
        holder.linearlayout_board_area.setTag(position);

        if(boardItem.getType() == Data_BoardListItem.BOARD_TYPE_TITLE){
            holder.linearlayout_title_area.setVisibility(View.VISIBLE);

            holder.textview_title.setText("공지사항");
            holder.textview_title_cnt.setText(String.valueOf(boardItem.getCnt()));

        }else if(boardItem.getType() == Data_BoardListItem.BOARD_TYPE_NOTICE){
            holder.linearlayout_notice_area.setVisibility(View.VISIBLE);
            holder.textview_notice_contents.setText("· "+boardItem.getBoard().content);
        }else{
            holder.linearlayout_board_area.setVisibility(View.VISIBLE);

            if(WezoneUtil.isEmptyStr(boardItem.getBoard().img_url) == false){
                mActivity.showImageFromRemote(boardItem.getBoard().img_url,R.drawable.im_bunny_photo,holder.imageview_profile);
            }else{
                holder.imageview_profile.setImageResource(R.drawable.im_bunny_photo);
            }

            holder.textview_name.setText(boardItem.getBoard().user_name);

            if(mMemberList != null && WezoneUtil.isEmptyStr(boardItem.getBoard().uuid) == false) {
                if (mMemberList.size() > 0) {
                    if (mMemberList.get(0).uuid.equals(boardItem.getBoard().uuid)) {
                        holder.imageview_badge.setVisibility(View.VISIBLE);
                    } else {
                        holder.imageview_badge.setVisibility(View.GONE);
                    }
                }
            }
//            holder.imageview_badge.setVisibility(View.GONE);

            String dateWithFormat = LibDateUtil.getConvertDate(boardItem.getBoard().create_datetime,"yyyy-MM-dd HH:mm:ss","yyyy년 MM월 dd일");
            holder.textview_date.setText(dateWithFormat);

            if(boardItem.getBoard().uuid.equals(mActivity.getUuid())){
                holder.linearlayout_btn_edit.setVisibility(View.VISIBLE);
                holder.linearlayout_btn_edit.setTag(position);
                final ViewHolder finalHolder = holder;
                holder.linearlayout_btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(mActivity, finalHolder.linearlayout_btn_edit);

                        popup.getMenuInflater().inflate(R.menu.menu_board_edit_delete, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.edit_board:
                                        WriteBoardActivity.startActivityWithEdit(mActivity, boardItem.board , mWezone);
                                        break;
                                    case R.id.delete_board:
                                        deleteWezoneBoard(boardItem.board.board_id);
                                        break;
                                }
                                return true;
                            }
                        });
                        popup.show();
                    }
                });
            }else{
                holder.linearlayout_btn_edit.setVisibility(View.GONE);
            }


            if(boardItem.getBoard().board_file != null && boardItem.getBoard().board_file.size() > 0){
                Data_File file = boardItem.getBoard().board_file.get(0);
                holder.imageview_content.setVisibility(View.VISIBLE);
                if(WezoneUtil.isEmptyStr(file.url) == false){
                   mActivity.showImageFromRemote(file.url,0,holder.imageview_content);
                }else{
                    holder.imageview_content.setVisibility(View.GONE);
                }
            }else{
                holder.imageview_content.setVisibility(View.GONE);
            }

            holder.textview_contents.setText(boardItem.getBoard().content);

            if(boardItem.getBoard().comment != null && boardItem.getBoard().comment.size() > 0){
                holder.textview_comment_more.setVisibility(View.VISIBLE);
                holder.textview_comment_more.setText("댓글 "+ boardItem.getBoard().comment_total_count + "개 모두 보기");
                holder.linearlayout_comment_area.setVisibility(View.VISIBLE);

                if(boardItem.getBoard().comment.size() > 1){

                    Data_Comment comment01  = boardItem.getBoard().comment.get(0);
                    holder.linearlayout_comment_01.setVisibility(View.VISIBLE);

                    if(WezoneUtil.isEmptyStr(comment01.img_url) == false){
                        mActivity.showImageFromRemote(comment01.img_url,R.drawable.im_bunny_photo,holder.imageview_profile_01);
                    }else{
                        holder.imageview_profile_01.setImageResource(R.drawable.im_bunny_photo);
                    }
                    holder.textview_comment_contents_01.setText(comment01.content);
                    String realDate = LibDateUtil.getConvertDate(comment01.create_datetime,"yyyy-MM-dd HH:mm:ss");
                    holder.textview_comment_date_01.setText(realDate);


                    Data_Comment comment02  = boardItem.getBoard().comment.get(1);
                    holder.linearlayout_comment_02.setVisibility(View.VISIBLE);

                    if(WezoneUtil.isEmptyStr(comment02.img_url) == false){
                        mActivity.showImageFromRemote(comment02.img_url,R.drawable.im_bunny_photo,holder.imageview_profile_02);
                    }else{
                        holder.imageview_profile_02.setImageResource(R.drawable.im_bunny_photo);
                    }
                    holder.textview_comment_contents_02.setText(comment02.content);
                    String realDate02 = LibDateUtil.getConvertDate(comment02.create_datetime,"yyyy-MM-dd HH:mm:ss");
                    holder.textview_comment_date_02.setText(realDate02);

                }else{
                    holder.linearlayout_comment_02.setVisibility(View.GONE);
                    holder.linearlayout_comment_01.setVisibility(View.VISIBLE);

                    Data_Comment comment  = boardItem.getBoard().comment.get(0);

                    if(WezoneUtil.isEmptyStr(comment.img_url) == false){
                        mActivity.showImageFromRemote(comment.img_url,R.drawable.im_bunny_photo,holder.imageview_profile_01);
                    }else{
                        holder.imageview_profile_01.setImageResource(R.drawable.im_bunny_photo);
                    }
                    holder.textview_comment_contents_01.setText(comment.content);
                    String realDate = LibDateUtil.getConvertDate(comment.create_datetime,"yyyy-MM-dd HH:mm:ss");
                    holder.textview_comment_date_01.setText(realDate);
                }
            }else{
                holder.textview_comment_more.setVisibility(View.GONE);
                holder.linearlayout_comment_area.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            switch (viewId){
                case R.id.linearlayout_notice_area:{
                    int pos = (int)v.getTag();
                    WezoneBoardDetailActivity.startActivit(mActivity,"",mWezone,mListItem.get(pos).getBoard(),mMemberList);
                }
                    break;

                case R.id.linearlayout_board_area:{
                    int pos = (int)v.getTag();
                    WezoneBoardDetailActivity.startActivit(mActivity,mWezone.title,mWezone,mListItem.get(pos).getBoard(),mMemberList);
                }

                    break;
            }
        }
    };

    public void deleteWezoneBoard(final String board_id){
        Call<Rev_Base> boardCall = mActivity.wezoneRestful.deleteWezone(board_id);
        boardCall.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base rev_base = response.body();
                if(mActivity.isNetSuccess(rev_base)){
                    Intent intent = new Intent();
                    intent.putExtra(WEZONE_BOARD_RESULT_ACTION,WEZONE_BOARD_RESULT_ACTION_DELETE);
                    intent.putExtra(WEZONE_BOARD_ID, board_id);
                    mActivity.setResult(mActivity.RESULT_OK,intent);
                    mActivity.finish();
                    //화면 뒤로 넘어가지 말고 리로드
                }
            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {

            }
        });
    }

    class ViewHolder {

        public LinearLayout linearlayout_title_area;
        public TextView textview_title;
        public TextView textview_title_cnt;
        public LinearLayout linearlayout_notice_area;
        public TextView textview_notice_contents;
        public LinearLayout linearlayout_board_area;
        public ImageView imageview_profile;
        public TextView textview_name;
        public TextView textview_date;
        public LinearLayout linearlayout_btn_edit;
        public CustomImageView imageview_content;
        public TextView textview_contents;
        public TextView textview_comment_more;
        public LinearLayout linearlayout_comment_area;
        public LinearLayout linearlayout_comment_01;
        public ImageView imageview_profile_01;
        public ImageView imageview_badge;
        public TextView textview_comment_contents_01;
        public TextView textview_comment_date_01;
        public LinearLayout linearlayout_comment_02;
        public ImageView imageview_profile_02;
        public TextView textview_comment_contents_02;
        public TextView textview_comment_date_02;

    }
}
