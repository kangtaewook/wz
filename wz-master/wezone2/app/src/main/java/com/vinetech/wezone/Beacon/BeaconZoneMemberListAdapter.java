package com.vinetech.wezone.Beacon;

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
import android.widget.Toast;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.Data.Data_Zone_Member;
import com.vinetech.wezone.Profile.MyProfileActivity;
import com.vinetech.wezone.Profile.ProfileActivity;
import com.vinetech.wezone.R;
import com.vinetech.wezone.RevPacket.Rev_Base;
import com.vinetech.wezone.SendPacket.Send_PutFriend;
import com.vinetech.wezone.SendPacket.Send_PutShare;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vinetech.wezone.R.id.imageview_profile;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class BeaconZoneMemberListAdapter extends BaseAdapter {

    private ArrayList<Data_Zone_Member> mListItem;
    private Data_Beacon mBeaconinfo;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;
    ViewHolder holder =null;

    public BeaconZoneMemberListAdapter(BaseActivity a, ArrayList<Data_Zone_Member> zoneMembers, Data_Beacon data_beacon){
        mActivity = a;
        mListItem = zoneMembers;
        mBeaconinfo = data_beacon;
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

        holder = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.member_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.linearlayout_btn_body = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_body);
            holder.imageview_profile = (ImageView) convertView.findViewById(imageview_profile);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.linearlayout_btn_add = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_add);
            holder.linearlayout_btn_delete_share = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_delete_share);

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
            holder.linearlayout_btn_delete_share.setVisibility(View.GONE);
        if(member.uuid.equals(mActivity.getUuid())){
            holder.linearlayout_btn_add.setVisibility(View.GONE);
        }else{
            if(WezoneUtil.isEmptyStr(member.friend_uuid)){
                holder.linearlayout_btn_add.setVisibility(View.VISIBLE);
            }else{
                holder.linearlayout_btn_add.setVisibility(View.GONE);
            }
        }

        if(mBeaconinfo != null) {
            if (mBeaconinfo.uuid.equals(mActivity.getShare().getMyInfo().uuid)) {
                holder.linearlayout_btn_delete_share.setVisibility(View.VISIBLE);
            }
        }

        holder.linearlayout_btn_add.setTag(position);
        holder.linearlayout_btn_add.setOnClickListener(mClickListener);
        holder.linearlayout_btn_delete_share.setTag(position);
        holder.linearlayout_btn_delete_share.setOnClickListener(mClickListener);

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
            }else if (viewId == R.id.linearlayout_btn_add) {
                final int pos = (int) v.getTag();
                final Data_Zone_Member memeber = mListItem.get(pos);

                Send_PutFriend param = new Send_PutFriend();
                param.other_uuid = memeber.uuid;
                Call<Rev_Base> putFriend = mActivity.wezoneRestful.putFriend(param);
                putFriend.enqueue(new Callback<Rev_Base>() {
                    @Override
                    public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                        Rev_Base revBase = response.body();
                        if (mActivity.isNetSuccess(revBase)) {
                            mListItem.get(pos).friend_uuid = memeber.uuid;
                            mActivity.getShare().addFriendCnt();
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<Rev_Base> call, Throwable t) {

                    }
                });

            }else if(viewId == R.id.linearlayout_btn_delete_share){
                final int pos = (int) v.getTag();
                final Data_Zone_Member memeber = mListItem.get(pos);

                //해지 버튼 눌렀을 때
                setDelete_share_beacon(mBeaconinfo,memeber.uuid,v);
            }
        }
    };

    class ViewHolder {
        public LinearLayout linearlayout_btn_body;
        public ImageView imageview_profile;
        public TextView textview_name;
        public LinearLayout linearlayout_btn_add;
        public LinearLayout linearlayout_btn_delete_share;
    }

    public void setDelete_share_beacon(final Data_Beacon data_beacon, final String uuid, final View v) {
        final PopupMenu popup = new PopupMenu(mActivity, holder.linearlayout_btn_delete_share);

        popup.getMenuInflater().inflate(R.menu.menu_delete_shared_beacon, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.delete_share_beacon:
                        if(data_beacon == null){
                            Toast.makeText(mActivity," data_beacon  = null ",Toast.LENGTH_SHORT).show();
                        }else {
                            sendBeaconShare_delete(data_beacon.beacon_id, uuid,v);
                        }
                        break;
                }

                return true;
            }
        });
        popup.show();
    }


    public void sendBeaconShare_delete(String beacon_id, String uuid, View v){

        int viewId = v.getId();
        final int pos = (int) v.getTag();

        Send_PutShare putShare = new Send_PutShare();
        putShare.type = Send_PutShare.SHARE_TYPE_BEACON;

        putShare.zone_id = beacon_id;

        putShare.other_uuids = new ArrayList<>();

            HashMap<String,String> other_uuids = new HashMap<>();
            other_uuids.put("uuid",uuid);
            putShare.other_uuids.add(other_uuids);

        putShare.share_flag = Send_PutShare.SHARE_FLAG_DELETE;


        Call<Rev_Base> putBeaconShare = mActivity.wezoneRestful.putBeaconShare(putShare);
        putBeaconShare.enqueue(new Callback<Rev_Base>() {
            @Override
            public void onResponse(Call<Rev_Base> call, Response<Rev_Base> response) {
                Rev_Base revBase = response.body();
                if (mActivity.isNetSuccess(revBase)) {
                    Toast.makeText(mActivity, "공유된 WeCON을 해지했습니다.", Toast.LENGTH_SHORT).show();
                    mListItem.remove(pos);
                    notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<Rev_Base> call, Throwable t) {
            }
        });
    }
}
