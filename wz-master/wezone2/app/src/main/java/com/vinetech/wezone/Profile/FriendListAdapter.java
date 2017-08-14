package com.vinetech.wezone.Profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_UserInfo;
import com.vinetech.wezone.R;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class FriendListAdapter extends BaseAdapter {

    public static int FROM_NORMAL = 0;
    public static int FROM_SEARCH = 1;

    private ArrayList<Data_UserInfo> mListItem;
    private BaseActivity mActivity;
    private LayoutInflater mInflater;

    private int mWhere;

    public FriendListAdapter(BaseActivity a, ArrayList<Data_UserInfo> userInfos, int where){
        mActivity = a;
        mListItem = userInfos;
        mInflater = LayoutInflater.from(mActivity);

        mWhere = where;
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friend_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.imageview_profile = (ImageView) convertView.findViewById(R.id.imageview_profile);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.linearlayout_btn_chat = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_chat);
            holder.relativelayout_image_area = (RelativeLayout) convertView.findViewById(R.id.relativelayout_image_area);
            holder.relativelayout_image_area.setOnClickListener(mListener);
            holder.relativelayout_image_area.setTag(position);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder == null) return null;

        Data_UserInfo userInfo = mListItem.get(position);
        holder.relativelayout_image_area.setTag(position);

        if (WezoneUtil.isEmptyStr(userInfo.img_url) == false) {
            mActivity.showImageFromRemoteWithCircle(userInfo.img_url, R.drawable.im_bunny_photo, holder.imageview_profile);
        } else {
            holder.imageview_profile.setImageResource(R.drawable.im_bunny_photo);
        }

        holder.textview_name.setText(userInfo.user_name);

        if (mWhere == FROM_SEARCH) {
            holder.linearlayout_btn_chat.setVisibility(View.GONE);
        } else {
            holder.linearlayout_btn_chat.setVisibility(View.VISIBLE);
        }

        return convertView;

    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int)v.getTag();

            Data_UserInfo userInfo = mListItem.get(position);
            ProfileActivity.startActivity(mActivity,userInfo);
        }
    };

    class ViewHolder {
        public ImageView imageview_profile;
        public TextView textview_name;
        public LinearLayout linearlayout_btn_chat;
        public RelativeLayout relativelayout_image_area;
    }


}
