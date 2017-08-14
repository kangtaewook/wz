package com.vinetech.wezone.Theme;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vinetech.util.WezoneUtil;
import com.vinetech.wezone.Data.Data_Theme;
import com.vinetech.wezone.R;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class ThemeManagerListAdapter extends BaseAdapter {

    private ArrayList<Data_Theme> mListItem;
    private ThemeManageActivity mActivity;

    private LayoutInflater mInflater;

    public ThemeManagerListAdapter(ThemeManageActivity a, ArrayList<Data_Theme> themeInfos){
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
            convertView = mInflater.inflate(R.layout.theme_add_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.imageview_theme_icon = (ImageView) convertView.findViewById(R.id.imageview_theme_icon);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.textview_desc = (TextView) convertView.findViewById(R.id.textview_desc);
            holder.linearlayout_btn_del_theme = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_del_theme);
            holder.linearlayout_btn_del_theme.setOnClickListener(mClickListner);
            holder.linearlayout_btn_add_theme = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_add_theme);
            holder.linearlayout_btn_add_theme.setOnClickListener(mClickListner);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        int bgPos = (position / 5) + position;
        mListItem.get(position).resIdPos = bgPos;

        Data_Theme themeInfo = mListItem.get(position);

        holder.linearlayout_btn_del_theme.setTag(position);
        holder.linearlayout_btn_add_theme.setTag(position);


        holder.imageview_theme_icon.setVisibility(View.VISIBLE);

        if(WezoneUtil.isEmptyStr(themeInfo.bg_color) == false){
            holder.imageview_theme_icon.setBackgroundColor(Color.parseColor(themeInfo.bg_color));
        }

        if(WezoneUtil.isEmptyStr(themeInfo.img_url) == false){
            mActivity.showImageFromRemote(themeInfo.img_url, 0, holder.imageview_theme_icon);
        }

        holder.textview_name.setText(themeInfo.name);
        holder.textview_desc.setText(themeInfo.content);

        if(WezoneUtil.isEmptyStr(themeInfo.uuid)){
            holder.linearlayout_btn_del_theme.setVisibility(View.GONE);
            holder.linearlayout_btn_add_theme.setVisibility(View.VISIBLE);
        }else{
            holder.linearlayout_btn_del_theme.setVisibility(View.VISIBLE);
            holder.linearlayout_btn_add_theme.setVisibility(View.GONE);
        }

        return convertView;
    }

    View.OnClickListener mClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            int position = (int)v.getTag();

            switch (viewId){
                case R.id.linearlayout_btn_add_theme:
                    mActivity.putTheme(mListItem.get(position).theme_id);
                    break;

                case R.id.linearlayout_btn_del_theme:
                    mActivity.deleteTheme(mListItem.get(position).theme_id);
                    break;
            }
        }
    };

    class ViewHolder {
        public ImageView imageview_theme_icon;
        public TextView textview_name;
        public TextView textview_desc;

        public LinearLayout linearlayout_btn_del_theme;
        public LinearLayout linearlayout_btn_add_theme;
    }
}
