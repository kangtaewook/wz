package com.vinetech.wezone.Main;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.Data.Data_Theme;
import com.vinetech.wezone.R;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class ThemeListAdapter extends BaseAdapter {

    private ArrayList<Data_Theme> mListItem;
    private MainActivity mActivity;

    private LayoutInflater mInflater;

    public ThemeListAdapter(MainActivity a, ArrayList<Data_Theme> themeInfos){
        mActivity = a;
        mListItem = themeInfos;
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
            convertView = mInflater.inflate(R.layout.theme_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.imageview_bg = (ImageView) convertView.findViewById(R.id.imageview_bg);
            holder.textview_theme_name = (TextView) convertView.findViewById(R.id.textview_theme_name);

            holder.textview_beacon_cnt = (TextView) convertView.findViewById(R.id.textview_beacon_cnt);
            holder.textview_desc = (TextView) convertView.findViewById(R.id.textview_desc);

            holder.imageview_theme_icon = (ImageView) convertView.findViewById(R.id.imageview_theme_icon);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        int bgPos = (position / 5) + position;
        mListItem.get(position).resIdPos = bgPos;

        Data_Theme themeInfo = mListItem.get(position);

        if(WezoneUtil.isEmptyStr(themeInfo.bg_img_url) == false){
            mActivity.showImageFromRemote(themeInfo.bg_img_url, 0, holder.imageview_bg);
        }


        holder.imageview_theme_icon.setVisibility(View.VISIBLE);

        if(WezoneUtil.isEmptyStr(themeInfo.bg_color) == false){
            holder.imageview_theme_icon.setBackgroundColor(Color.parseColor(themeInfo.bg_color));
        }

        if(WezoneUtil.isEmptyStr(themeInfo.img_url) == false){
            mActivity.showImageFromRemote(themeInfo.img_url, 0, holder.imageview_theme_icon);
        }

        holder.textview_theme_name.setText(themeInfo.name);

        int beaconSize = themeInfo.beacons == null ? 0 : themeInfo.beacons.size();
        holder.textview_beacon_cnt.setText(beaconSize +" 개");
        holder.textview_desc.setText(themeInfo.content);

        return convertView;
    }

    class ViewHolder {
        public ImageView imageview_bg;
        public TextView textview_theme_name;
        public TextView textview_beacon_cnt;
        public TextView textview_desc;
        public ImageView imageview_theme_icon;

    }
}
