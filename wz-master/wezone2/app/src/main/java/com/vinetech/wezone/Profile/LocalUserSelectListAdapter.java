package com.vinetech.wezone.Profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_LocalUserInfo;
import com.vinetech.wezone.R;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class LocalUserSelectListAdapter extends BaseAdapter {

    private ArrayList<Data_LocalUserInfo> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;

    public LocalUserSelectListAdapter(BaseActivity a, ArrayList<Data_LocalUserInfo> userInfos){
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
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.textview_phone_number = (TextView) convertView.findViewById(R.id.textview_phone_number);
            holder.textview_desc = (TextView) convertView.findViewById(R.id.textview_desc);
            holder.linearlayout_btn_check = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_check);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        Data_LocalUserInfo userInfo = mListItem.get(position);


        holder.textview_name.setText(userInfo.user_name);

        holder.textview_phone_number.setText(userInfo.phone_num);

        holder.linearlayout_btn_check.setSelected(userInfo.isSelected);

        return convertView;
    }

    class ViewHolder {
        public ImageView imageview_profile;
        public TextView textview_name;
        public TextView textview_desc;
        public LinearLayout linearlayout_btn_check;
        public TextView textview_phone_number;
    }
}
