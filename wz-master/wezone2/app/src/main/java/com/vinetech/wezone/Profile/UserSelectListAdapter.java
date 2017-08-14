package com.vinetech.wezone.Profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_UserInfo;
import com.vinetech.wezone.R;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class UserSelectListAdapter extends BaseAdapter {

    private ArrayList<Data_UserInfo> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;
    private int mCount = -1;

    public UserSelectListAdapter(BaseActivity a, ArrayList<Data_UserInfo> userInfos){
        mActivity = a;
        mListItem = userInfos;
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
            convertView = mInflater.inflate(R.layout.user_select_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.imageview_profile = (ImageView) convertView.findViewById(R.id.imageview_profile);
            holder.TextView_already_shared_beacon = (TextView) convertView.findViewById(R.id.TextView_already_shared_beacon);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.textview_desc = (TextView) convertView.findViewById(R.id.textview_desc);
            holder.linearlayout_btn_check = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_check);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

            Data_UserInfo userInfo = mListItem.get(position);

            if (WezoneUtil.isEmptyStr(userInfo.img_url) == false) {
                mActivity.showImageFromRemote(userInfo.img_url, R.drawable.im_bunny_photo, holder.imageview_profile);
            } else {
                holder.imageview_profile.setImageResource(R.drawable.im_bunny_photo);
            }

            holder.textview_name.setText(userInfo.user_name);

            holder.linearlayout_btn_check.setSelected(userInfo.isSelected);

            holder.linearlayout_btn_check.setVisibility(View.VISIBLE);
            holder.TextView_already_shared_beacon.setVisibility(View.GONE);


            if (userInfo.isClicked == true) {
//                holder.linearlayout_btn_check.setEnabled(false);
                holder.linearlayout_btn_check.setVisibility(View.GONE);
                holder.TextView_already_shared_beacon.setVisibility(View.VISIBLE);
            }


        return convertView;
    }


    class ViewHolder {
        public ImageView imageview_profile;
        public TextView TextView_already_shared_beacon;
        public TextView textview_name;
        public TextView textview_desc;
        public LinearLayout linearlayout_btn_check;
    }
}
