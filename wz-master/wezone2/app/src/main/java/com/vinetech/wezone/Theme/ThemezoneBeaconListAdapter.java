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
import com.vinetech.wezone.Data.Data_Beacon;
import com.vinetech.wezone.R;

import java.util.ArrayList;

/**
 * Created by galuster3 on 2017-01-23.
 */

public class ThemezoneBeaconListAdapter extends BaseAdapter {

    private ArrayList<Data_Beacon> mListItem;
    private BaseActivity mActivity;

    private LayoutInflater mInflater;

    private int mThemeMode;

    private int mCurrentState;

    public ThemezoneBeaconListAdapter(BaseActivity a, int state, ArrayList<Data_Beacon> beacons, int themeMode){
        mActivity = a;
        mListItem = beacons;
        mInflater = LayoutInflater.from(mActivity);
        mThemeMode = themeMode;
        mCurrentState = state;
    }

    public void setChangeState(int state){
        mCurrentState = state;
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
            convertView = mInflater.inflate(R.layout.theme_beacon_list_item, null);

            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.imageview_beacon = (ImageView) convertView.findViewById(R.id.imageview_beacon);
//            holder.imageview_theme_icon = (ImageView) convertView.findViewById(R.id.imageview_theme_icon);
            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.textview_desc = (TextView) convertView.findViewById(R.id.textview_desc);


            holder.linearlayout_distance_area = (LinearLayout) convertView.findViewById(R.id.linearlayout_distance_area);
            holder.imageview_distance_icon = (ImageView) convertView.findViewById(R.id.imageview_distance_icon);

            holder.linearlayout_zone_icon_area = (LinearLayout) convertView.findViewById(R.id.linearlayout_zone_icon_area);
            holder.imageview_zone_in = (LinearLayout) convertView.findViewById(R.id.imageview_zone_in);
            holder.imageview_zone_out = (LinearLayout) convertView.findViewById(R.id.imageview_zone_out);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if( holder == null ) return null;

        Data_Beacon beaconInfo = mListItem.get(position);

        if(WezoneUtil.isEmptyStr(beaconInfo.img_url) == false){
            mActivity.showImageFromRemote(beaconInfo.img_url,R.drawable.im_bunny_photo,holder.imageview_beacon);
        }else{
            holder.imageview_beacon.setImageResource(R.drawable.im_bunny_photo);
        }

        holder.textview_name.setText(beaconInfo.name);

        holder.linearlayout_distance_area.setVisibility(View.GONE);
        holder.linearlayout_zone_icon_area.setVisibility(View.GONE);

        if(mThemeMode == ThemeActivity.THEME_MODE_NORMAL){
            holder.linearlayout_distance_area.setVisibility(View.VISIBLE);


            int rssi = beaconInfo.rssi;
            if(rssi != 0 && rssi >  -50) {
                holder.imageview_distance_icon.setImageResource(R.drawable.ic_signal_level_max);
            }else if(rssi != 0 && rssi >  -70) {
                holder.imageview_distance_icon.setImageResource(R.drawable.ic_signal_level_medium);
            }else if(rssi != 0 && rssi >  -90){
                holder.imageview_distance_icon.setImageResource(R.drawable.ic_signal_level_minimum);
            }else{
                holder.imageview_distance_icon.setImageResource(R.drawable.ic_signal_level_none);
            }

        }else{
            holder.linearlayout_zone_icon_area.setVisibility(View.VISIBLE);

            holder.imageview_zone_in.setEnabled(false);
            holder.imageview_zone_out.setEnabled(false);

            if(mCurrentState == ThemeActivity.STATE_ZONE_IN){
                if(beaconInfo.beaconStatus == Data_Beacon.STATUE_IN){
                    holder.imageview_zone_in.setEnabled(true);
                    holder.imageview_zone_in.setSelected(true);
                }
            }else if(mCurrentState == ThemeActivity.STATE_ZONE_OUT){
                if(beaconInfo.beaconStatus == Data_Beacon.STATUE_OUT){
                    holder.imageview_zone_out.setEnabled(true);
                    holder.imageview_zone_out.setSelected(false);
                }
            }else{
                if(beaconInfo.beaconStatus == Data_Beacon.STATUE_IN){
                    holder.imageview_zone_in.setEnabled(true);
                    holder.imageview_zone_in.setSelected(true);
                }else{
                    holder.imageview_zone_out.setEnabled(true);
                    holder.imageview_zone_out.setSelected(false);
                }
            }
        }

        return convertView;
    }

    public void setThemeMode(int themeMode){
        this.mThemeMode = themeMode;
    }

    class ViewHolder {

        public ImageView imageview_beacon;
//        public ImageView imageview_theme_icon;
        public TextView textview_name;
        public TextView textview_desc;


        public LinearLayout linearlayout_distance_area;
        public ImageView imageview_distance_icon;

        public LinearLayout linearlayout_zone_icon_area;
        public LinearLayout imageview_zone_in;
        public LinearLayout imageview_zone_out;

    }
}
