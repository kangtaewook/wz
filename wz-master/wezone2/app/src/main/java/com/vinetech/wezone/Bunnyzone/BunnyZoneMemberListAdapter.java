package com.vinetech.wezone.Bunnyzone;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.Profile.MyProfileActivity;
import com.vinetech.wezone.Profile.ProfileActivity;
import com.vinetech.wezone.R;

import java.util.ArrayList;

import static com.vinetech.wezone.R.id.imageview_profile;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class BunnyZoneMemberListAdapter extends BaseAdapter {

    private ArrayList<Data_Zone_Member> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;

    public BunnyZoneMemberListAdapter(BaseActivity a, ArrayList<Data_Zone_Member> zoneMembers){
        mActivity = a;
        mListItem = zoneMembers;
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
            convertView = mInflater.inflate(R.layout.member_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.linearlayout_btn_body = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_body);
            holder.imageview_profile = (ImageView) convertView.findViewById(imageview_profile);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.linearlayout_btn_add = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_add);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        Data_Zone_Member member = mListItem.get(position);

        holder.linearlayout_btn_body.setTag(position);
        holder.linearlayout_btn_body.setOnClickListener(mClickListener);

        if(WezoneUtil.isEmptyStr(member.img_url) == false){
            mActivity.showImageFromRemote(member.img_url,R.drawable.im_beacon_add,holder.imageview_profile);
        }else{
            holder.imageview_profile.setImageResource(R.drawable.im_beacon_add);
        }

        holder.textview_name.setText(member.user_name);

        if(member.uuid.equals(mActivity.getUuid())){
            holder.linearlayout_btn_add.setVisibility(View.GONE);
        }else{
            if(WezoneUtil.isEmptyStr(member.friend_uuid)){
                holder.linearlayout_btn_add.setVisibility(View.VISIBLE);
            }else{
                holder.linearlayout_btn_add.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if(viewId == R.id.linearlayout_btn_body){
                int pos = (int)v.getTag();

                Data_Zone_Member member = mListItem.get(pos);
                if(member.uuid.equals(mActivity.getUuid())){
                    mActivity.moveActivity(new Intent(mActivity,MyProfileActivity.class));
                }else {
                    ProfileActivity.startActivityWithuuid(mActivity,member.uuid,member.distance,member);
                }
            }
        }
    };

    class ViewHolder {
        public LinearLayout linearlayout_btn_body;
        public ImageView imageview_profile;
        public TextView textview_name;
        public LinearLayout linearlayout_btn_add;
    }
}
