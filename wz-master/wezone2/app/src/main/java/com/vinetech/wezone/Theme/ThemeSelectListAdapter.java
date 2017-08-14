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
import com.vinetech.wezone.BaseActivity;
import com.vinetech.wezone.Data.Data_Theme;
import com.vinetech.wezone.R;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class ThemeSelectListAdapter extends BaseAdapter {

    private ArrayList<Data_Theme> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;

    public ThemeSelectListAdapter(BaseActivity a, ArrayList<Data_Theme> themes){
        mActivity = a;
        mListItem = themes;
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
            convertView = mInflater.inflate(R.layout.theme_select_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.imageview_theme_icon = (ImageView) convertView.findViewById(R.id.imageview_theme_icon);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.textview_desc = (TextView) convertView.findViewById(R.id.textview_desc);
            holder.linearlayout_btn_check = (LinearLayout) convertView.findViewById(R.id.linearlayout_btn_check);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        Data_Theme theme = mListItem.get(position);

        int bgPos = (position / 5) + position;
        mListItem.get(position).resIdPos = bgPos;

        Data_Theme themeInfo = mListItem.get(position);

        holder.imageview_theme_icon.setVisibility(View.VISIBLE);

        if(WezoneUtil.isEmptyStr(themeInfo.bg_color) == false){
            holder.imageview_theme_icon.setBackgroundColor(Color.parseColor(themeInfo.bg_color));
        }

        if(WezoneUtil.isEmptyStr(themeInfo.img_url) == false){
            mActivity.showImageFromRemote(themeInfo.img_url, 0, holder.imageview_theme_icon);
        }

        holder.textview_name.setText(themeInfo.name);
        holder.textview_desc.setText(themeInfo.content);

        holder.linearlayout_btn_check.setSelected(theme.isSelected);

        return convertView;
    }

    class ViewHolder {
        public ImageView imageview_theme_icon;
        public TextView textview_name;
        public TextView textview_desc;

        public LinearLayout linearlayout_btn_check;
    }
}
