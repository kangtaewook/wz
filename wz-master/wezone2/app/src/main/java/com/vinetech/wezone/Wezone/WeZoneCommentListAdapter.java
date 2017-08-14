package com.vinetech.wezone.Wezone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vinetech.util.LibDateUtil;
import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Comment;
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.R;

import java.util.ArrayList;

import static com.vinetech.wezone.R.id.imageview_profile;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class WeZoneCommentListAdapter extends BaseAdapter {

    private ArrayList<Data_Comment> mListItem;
    private ArrayList<Data_Zone_Member> mMemberlist;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;
    private Data_WeZone mWezone;

    public WeZoneCommentListAdapter(BaseActivity a, ArrayList<Data_Comment> comments, Data_WeZone weZone, ArrayList<Data_Zone_Member> memberlist){
        mActivity = a;
        mListItem = comments;
        if(memberlist != null) {
            mMemberlist = memberlist;
        }
        mWezone = weZone;
        mInflater = LayoutInflater.from(mActivity);
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
            convertView = mInflater.inflate(R.layout.comment_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.imageview_profile = (ImageView) convertView.findViewById(imageview_profile);
            holder.textview_comment_name = (TextView) convertView.findViewById(R.id.textview_comment_name);
            holder.textview_comment_contents = (TextView) convertView.findViewById(R.id.textview_comment_contents);
            holder.textview_comment_date = (TextView) convertView.findViewById(R.id.textview_comment_date);
            holder.imageview_badge = (ImageView) convertView.findViewById(R.id.imageview_badge);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        Data_Comment comment = mListItem.get(position);

        if(WezoneUtil.isEmptyStr(comment.img_url) == false){
            mActivity.showImageFromRemote(comment.img_url,R.drawable.im_bunny_photo,holder.imageview_profile);
        }else{
            holder.imageview_profile.setImageResource(R.drawable.im_bunny_photo);
        }

        holder.textview_comment_name.setText(comment.user_name);
        holder.textview_comment_contents.setText(comment.content);

            if(mMemberlist != null){
                if(mMemberlist.get(0).uuid.equals(comment.uuid)){
                    holder.imageview_badge.setVisibility(View.VISIBLE);
                }else{
                    holder.imageview_badge.setVisibility(View.GONE);
                }
            }else{
                holder.imageview_badge.setVisibility(View.GONE);
            }

        String realDate = LibDateUtil.getConvertDate(comment.create_datetime,"yyyy-MM-dd HH:mm:ss");
        holder.textview_comment_date.setText(realDate);

        return convertView;
    }

    class ViewHolder {
        public ImageView imageview_profile;
        public TextView textview_comment_name;
        public TextView textview_comment_contents;
        public TextView textview_comment_date;
        public ImageView imageview_badge;
    }
}
