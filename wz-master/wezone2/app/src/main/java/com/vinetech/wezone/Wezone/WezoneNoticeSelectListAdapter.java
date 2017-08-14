package com.vinetech.wezone.Wezone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Board;
import com.vinetech.wezone.R;

import java.util.ArrayList;

/**
 * Created by galuster on 2017-04-18.
 */

public class WezoneNoticeSelectListAdapter extends BaseAdapter {

    public static final String ON = "ON";
    public static final String OFF = "OFF";

    private ArrayList<Data_Board> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;

    private String mOnOff;

    public WezoneNoticeSelectListAdapter(BaseActivity a, ArrayList<Data_Board> userInfos, String onoff) {
        mActivity = a;
        mListItem = userInfos;
        mInflater = LayoutInflater.from(mActivity);
        mOnOff = onoff;
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
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        WezoneNoticeSelectListAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.notice_select_list_item, null);

            holder = new WezoneNoticeSelectListAdapter.ViewHolder();
            convertView.setTag(holder);

            holder.imageview_profile = (ImageView) convertView.findViewById(R.id.imageview_profile);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.textview_desc = (TextView) convertView.findViewById(R.id.textview_desc);
            holder.Imageview_btn_check = (ImageView) convertView.findViewById(R.id.Imageview_btn_check);

        } else {
            holder = (WezoneNoticeSelectListAdapter.ViewHolder) convertView.getTag();
        }

        if (holder == null) return null;

        Data_Board userInfo = mListItem.get(position);

        holder.textview_name.setText(userInfo.content);
        holder.Imageview_btn_check.setSelected(userInfo.isSelected);
//        holder.Imageview_btn_check.setSelected(userInfo.isSelected);
        if (mOnOff != null) {
            //처음 설정시
            if (mOnOff.equals(ON)) {
                //활성화
                holder.Imageview_btn_check.setClickable(true);
                if (userInfo.isSelectedname == true) {
                    holder.textview_name.setTextColor(mActivity.getResources().getColor(R.color.notification_text_title_selected_color));
                } else {
                    holder.textview_name.setTextColor(mActivity.getResources().getColor(R.color.notification_text_title_color));
                }
            } else if (mOnOff.equals(OFF)) {
                //비활성화
                holder.Imageview_btn_check.setSelected(false);
                holder.Imageview_btn_check.setClickable(false);
                userInfo.isSelected = false;
                holder.textview_name.setTextColor(mActivity.getResources().getColor(R.color.notification_text_not_use_color));
            }
        } else {
            //비활성화
            holder.Imageview_btn_check.setSelected(false);
            holder.Imageview_btn_check.setClickable(false);
            holder.textview_name.setTextColor(mActivity.getResources().getColor(R.color.notification_text_not_use_color));
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView imageview_profile;
        public TextView textview_name;
        public TextView textview_desc;
        public ImageView Imageview_btn_check;
    }
}
