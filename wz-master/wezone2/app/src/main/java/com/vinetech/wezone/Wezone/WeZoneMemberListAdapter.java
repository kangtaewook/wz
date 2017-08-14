package com.vinetech.wezone.Wezone;

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
import com.vinetech.wezone.Data.Data_WeZone;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.Profile.MyProfileActivity;
import com.vinetech.wezone.Profile.ProfileActivity;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.SendPacket.Send_PutFriend;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vinetech.wezone.R.id.imageview_profile;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class WeZoneMemberListAdapter extends BaseAdapter {

    private ArrayList<Data_Zone_Member> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;

    private Data_WeZone mWezone;

    public WeZoneMemberListAdapter(BaseActivity a, Data_WeZone wezone, ArrayList<Data_Zone_Member> userInfos){
        mActivity = a;
        mListItem = userInfos;
        mWezone = wezone;
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
            convertView = mInflater.inflate(R.layout.wezone_member_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.linearlayout_btn_body = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_body);
            holder.imageview_profile = (ImageView) convertView.findViewById(imageview_profile);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.textview_desc = (TextView) convertView.findViewById(R.id.textview_desc);
            holder.linearlayout_btn_add = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_add);
            holder.imageview_badge = (ImageView) convertView.findViewById(R.id.imageview_badge);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        Data_Zone_Member userinfo = mListItem.get(position);

        holder.linearlayout_btn_body.setTag(position);
        holder.linearlayout_btn_body.setOnClickListener(mClickListener);

        if(WezoneUtil.isEmptyStr(userinfo.img_url) == false){
            mActivity.showImageFromRemote(userinfo.img_url,R.drawable.im_beacon_add,holder.imageview_profile);
        }else{
            holder.imageview_profile.setImageResource(R.drawable.im_beacon_add);
        }

        holder.textview_name.setText(userinfo.user_name);

        //나와의 거리 설정
        if (WezoneUtil.isEmptyStr(userinfo.distance) == false) {
            if (userinfo.distance.contains(".")) {
                int i = userinfo.distance.indexOf(".");
                String mDistance = userinfo.distance.substring(0, i);
                if (Integer.valueOf(mDistance) != 0) {
                    DecimalFormat form = new DecimalFormat("#.#");
                    double distance = Double.parseDouble(userinfo.distance);
                    holder.textview_desc.setText("나와의 거리  " + form.format(distance) + "km");
                    userinfo.distance = String.valueOf(form.format(distance));
                } else if (Double.valueOf(userinfo.distance) > 0.0) {
                    if (Integer.valueOf(mDistance) == 0) {
                        int meter = (int) (Double.valueOf(userinfo.distance) * 1000);
                        holder.textview_desc.setText("나와의 거리  " + meter + "m");
                        userinfo.distance = String.valueOf(meter);
                    }
                } else if (userinfo.distance.equals("0")) {
                    holder.textview_desc.setText("나와의 거리  " + 0 + "m");
                    userinfo.distance = String.valueOf(0);
                }
            }else if(Integer.valueOf(userinfo.distance) > 0){
                holder.textview_desc.setText("나와의 거리  " + userinfo.distance + "m");
                userinfo.distance = String.valueOf(userinfo.distance);
            } else {
                holder.textview_desc.setText("나와의 거리  " + 0 + "m");
                userinfo.distance = String.valueOf(0);
            }
        }




        if(userinfo.uuid.equals(mActivity.getUuid())){
            holder.linearlayout_btn_add.setVisibility(View.GONE);
        }else{
            if(WezoneUtil.isEmptyStr(userinfo.friend_uuid)){
                holder.linearlayout_btn_add.setVisibility(View.VISIBLE);
            } else{
                    holder.linearlayout_btn_add.setVisibility(View.GONE);
            }
        }
        holder.linearlayout_btn_add.setTag(position);
        holder.linearlayout_btn_add.setOnClickListener(mClickListener);

        for(Data_Zone_Member member : mListItem) {
            if ("M".equals(userinfo.manage_type)) {
                holder.imageview_badge.setVisibility(View.VISIBLE);
            } else {
                holder.imageview_badge.setVisibility(View.GONE);
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
                Data_Zone_Member memeber = mListItem.get(pos);

                if(memeber.uuid.equals(mActivity.getShare().getMyInfo().uuid)){
                    mActivity.moveActivity(new Intent(mActivity,MyProfileActivity.class));
                }else {
                    ProfileActivity.startActivityWithuuid(mActivity,memeber.uuid,memeber.distance, memeber);
                }
            }
            else if(viewId == R.id.linearlayout_btn_add){
                final int pos = (int)v.getTag();
                final Data_Zone_Member memeber = mListItem.get(pos);

                Send_PutFriend param = new Send_PutFriend();
                param.other_uuid = memeber.uuid;
                Call<Rev_Base> putFriend = mActivity.wezoneRestful.putFriend(param);
                putFriend.enqueue(new Callback<Rev_Base>() {
                    @Override
                    public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                        Rev_Base revBase = response.body();
                        if(mActivity.isNetSuccess(revBase)){
                            mListItem.get(pos).friend_uuid = memeber.uuid;
                            mActivity.getShare().addFriendCnt();
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<Rev_Base> call, Throwable t) {

                    }
                });

            }
        }
    };

    class ViewHolder {
        public LinearLayout linearlayout_btn_body;
        public ImageView imageview_profile;
        public TextView textview_name;
        public TextView textview_desc;
        public LinearLayout linearlayout_btn_add;
        public ImageView imageview_badge;
    }
}
